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
            noOverlappingForSameEmployee(factory),
            assignEverySlot(factory),
            respectAvailability(factory), // has effect only if availability facts are present
            softPreferShorterTotalWork(factory)
        };
    }

    private Constraint requiredSkills(ConstraintFactory factory) {
        return factory.from(Assignment.class)
            .filter(a -> a.getEmployee() != null
                && !a.getEmployee().getSkills().containsAll(a.getTask().getRequiredSkills()))
            .penalize("Missing required skills", HARD);
    }

    private Constraint noOverlappingForSameEmployee(ConstraintFactory factory) {
        return factory.fromUniquePair(Assignment.class,
                Joiners.equal(Assignment::getEmployee),
                Joiners.filtering((a, b) -> a.getEmployee()!=null && b.getEmployee()!=null && overlaps(a.getTask(), b.getTask())))
            .penalize("Overlapping tasks for same employee", HARD);
    }

    private Constraint assignEverySlot(ConstraintFactory factory) {
        return factory.from(Assignment.class)
            .filter(a -> a.getEmployee() == null)
            .penalize("Unassigned slot", HARD);
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

    private Constraint softPreferShorterTotalWork(ConstraintFactory factory) {
        return factory.from(Assignment.class)
            .filter(a -> a.getEmployee() != null)
            .groupBy(Assignment::getEmployee, ConstraintCollectors.sumLong(a -> durationMinutes(a.getTask())))
            .penalizeLong("Total work minutes", SOFT, (emp, minutes) -> minutes);
    }

    private static boolean overlaps(Task t1, Task t2) {
        return !(t1.getEnd().isEqual(t2.getStart()) || t1.getEnd().isBefore(t2.getStart())
              || t2.getEnd().isEqual(t1.getStart()) || t2.getEnd().isBefore(t1.getStart()));
    }

    private static long durationMinutes(Task task) {
        return java.time.Duration.between(task.getStart(), task.getEnd()).toMinutes();
    }
}
