package com.example.shiftplanner.solver;

import com.example.shiftplanner.domain.*;

import org.optaplanner.core.api.score.stream.*;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;

public class ShiftConstraintProvider implements ConstraintProvider {

    // Fixed weights:
    private static final HardSoftLongScore HARD = HardSoftLongScore.ONE_HARD;
    private static final HardSoftLongScore SOFT = HardSoftLongScore.ONE_SOFT;

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {
            requiredSkills(factory),
            requiredRoleOnSlot(factory),
            noOverlappingForSameEmployee(factory),
            noDuplicateAssignmentToSameTask(factory),
            preferAssignEverySlotSoft(factory),
            respectAvailability(factory),
            spreadWorkAcrossEmployeesSoft(factory),
            noConsecutiveShiftsHard(factory),
            minRestBetweenShiftsSoft(factory),
            minRestBetweenShiftsHard(factory),
            block24HourTaskEmployees(factory),
            prioritizeSkillAssignments(factory)
        };
    }

    private Constraint requiredSkills(ConstraintFactory factory) {
        return factory.from(Assignment.class)
            .filter(a -> {
                if (a.getEmployee() == null) return false;
                var required = a.getTask().getRequiredSkills();
                if (required == null || required.isEmpty()) return false; // no required skills
                var empSkills = a.getEmployee().getSkills();
                if (empSkills == null || empSkills.isEmpty()) return true; // has requirements but employee has none
                // Violate if there is no intersection (require at least one of the required skills)
                return required.stream().noneMatch(empSkills::contains);
            })
            .penalize("Missing any-of required skills", HARD);
    }

    // If a slot has a specific role (requiredSkill set), enforce that the assigned employee has that skill.
    private Constraint requiredRoleOnSlot(ConstraintFactory factory) {
        return factory.from(Assignment.class)
            .filter(a -> a.getEmployee() != null && a.getRequiredSkill() != null
                && (a.getEmployee().getSkills() == null || !a.getEmployee().getSkills().contains(a.getRequiredSkill())))
            .penalize("Missing required role on slot", HARD);
    }

    private Constraint noOverlappingForSameEmployee(ConstraintFactory factory) {
        return factory.fromUniquePair(Assignment.class,
                Joiners.equal(Assignment::getEmployee),
                Joiners.filtering((a, b) -> a.getEmployee()!=null && b.getEmployee()!=null && overlaps(a.getTask(), b.getTask())))
            .penalize("Overlapping tasks for same employee", HARD);
    }

    private Constraint noDuplicateAssignmentToSameTask(ConstraintFactory factory) {
        return factory.forEachUniquePair(Assignment.class,
                Joiners.equal(Assignment::getTask),
                Joiners.filtering((a, b) -> a.getTask() != null && b.getTask() != null && a.getTask().equals(b.getTask())))
            .penalize("Duplicate assignment to same task", HARD);
    }

    private Constraint preferAssignEverySlotSoft(ConstraintFactory factory) {
        return factory.from(Assignment.class)
            .filter(a -> a.getEmployee() == null)
            .penalize("Unassigned slot", SOFT);
    }

    // Encourage using more/different employees by discouraging many assignments for the same person
    private Constraint spreadWorkAcrossEmployeesSoft(ConstraintFactory factory) {
        return factory.from(Assignment.class)
            .filter(a -> a.getEmployee() != null)
            .groupBy(Assignment::getEmployee, ConstraintCollectors.count())
            .penalizeLong("Spread work across employees", SOFT, (emp, count) -> Math.max(0L, (long)count - 1L));
    }

    private Constraint respectAvailability(ConstraintFactory factory) {
        // If no Availability facts exist, join won't match and no penalties will be applied.
        return factory.from(Assignment.class)
            .join(Availability.class,
                Joiners.equal(a -> a.getEmployee() != null ? a.getEmployee().getId() : null, Availability::getEmployeeId))
            .filter((a, av) -> {
                if (a.getEmployee() == null) return false;
                var t = a.getTask();
                return t.getStart().isBefore(av.getStart()) || t.getEnd().isAfter(av.getEnd());
            })
            .penalize("Outside availability", HARD);
    }

    // Hard constraint: no two consecutive shifts for the same employee
    private Constraint noConsecutiveShiftsHard(ConstraintFactory f) {
        return f.fromUniquePair(Assignment.class,
                Joiners.equal(a -> a.getEmployee() != null ? a.getEmployee().getId() : null),
                Joiners.filtering((a, b) -> {
                    if (a.getEmployee() == null || b.getEmployee() == null)
                        return false;
                    Task t1 = a.getTask(), t2 = b.getTask();
                    var aFirst = t1.getStart().isBefore(t2.getStart());
                    var earlier = aFirst ? t1 : t2;
                    var later = aFirst ? t2 : t1;
                    return earlier.getEnd().equals(later.getStart()); // רצופות בדיוק
                }))
                .penalize("Back-to-back shifts for same employee", HARD);
    }

    // 2) Soft: minimum rest hours (when restMode=SOFT)
    private Constraint minRestBetweenShiftsSoft(ConstraintFactory f) {
        return f.forEachUniquePair(Assignment.class,
                        Joiners.equal(a -> a.getEmployee() != null ? a.getEmployee().getId() : null))
                .join(SchedulingSettings.class)
                .filter((a, b, s) -> s.isSoftMode())
                .penalizeLong("Min rest between shifts (soft)", SOFT, (a, b, s) -> {
                    Task t1 = a.getTask(), t2 = b.getTask();
                    var aFirst = t1.getStart().isBefore(t2.getStart());
                    var earlier = aFirst ? t1 : t2;
                    var later = aFirst ? t2 : t1;
                    long gapMinutes = java.time.Duration.between(earlier.getEnd(), later.getStart()).toMinutes();
                    int requiredMinutes = s.getMinRestHours() * 60;
                    int deficit = (int) Math.max(0, requiredMinutes - gapMinutes);
                    return deficit; // קנס פר דקת חסר
                });
    }

    // 3) Hard: minimum rest hours (when restMode=HARD)
    private Constraint minRestBetweenShiftsHard(ConstraintFactory f) {
        return f.fromUniquePair(Assignment.class,
                Joiners.equal(a -> a.getEmployee() != null ? a.getEmployee().getId() : null))
                .join(SchedulingSettings.class)
                .filter((a, b, s) -> s.isHardMode())
                .filter((a, b, s) -> {
                    Task t1 = a.getTask(), t2 = b.getTask();
                    var aFirst = t1.getStart().isBefore(t2.getStart());
                    var earlier = aFirst ? t1 : t2;
                    var later = aFirst ? t2 : t1;
                    long gapMinutes = java.time.Duration.between(earlier.getEnd(), later.getStart()).toMinutes();
                    long requiredMinutes = (long) s.getMinRestHours() * 60L;
                    return gapMinutes < requiredMinutes; // הפרה קשיחה
                })
                .penalize("Min rest between shifts (hard)", HARD);
    }

    private Constraint block24HourTaskEmployees(ConstraintFactory factory) {
        return factory.forEachUniquePair(Assignment.class,
                Joiners.equal(Assignment::getEmployee),
                Joiners.filtering((a, b) -> {
                    if (a.getEmployee() == null || b.getEmployee() == null) return false;
                    // If one assignment is to a 24-hour task, block the other assignment
                    return (a.getTask().isIs24HourTask() && !b.getTask().isIs24HourTask()) ||
                           (b.getTask().isIs24HourTask() && !a.getTask().isIs24HourTask());
                }))
            .penalize("Employee assigned to both 24-hour task and other task", HARD);
    }

    private Constraint prioritizeSkillAssignments(ConstraintFactory factory) {
        return factory.from(Assignment.class)
            .filter(a -> a.getEmployee() != null && a.getTask() != null && !a.getTask().getRequiredSkills().isEmpty())
            .filter(a -> a.getEmployee().getSkills().stream().noneMatch(skill -> a.getTask().getRequiredSkills().contains(skill)))
            .penalize("Employee without required skills assigned to skill-required task", SOFT);
    }

    private static boolean overlaps(Task t1, Task t2) {
        return !(t1.getEnd().isEqual(t2.getStart()) || t1.getEnd().isBefore(t2.getStart())
              || t2.getEnd().isEqual(t1.getStart()) || t2.getEnd().isBefore(t1.getStart()));
    }

    private static long durationMinutes(Task task) {
        return java.time.Duration.between(task.getStart(), task.getEnd()).toMinutes();
    }
}
