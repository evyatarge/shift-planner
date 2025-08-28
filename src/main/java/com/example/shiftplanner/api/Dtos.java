package com.example.shiftplanner.api;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

public class Dtos {

    public record EmployeeDTO(Long id, String name, Set<String> skills) {}
    public record TaskDTO(Long id, String name, ZonedDateTime start, ZonedDateTime end, Set<String> requiredSkills, int requiredEmployees) {}
    public record AvailabilityDTO(Long employeeId, ZonedDateTime start, ZonedDateTime end) {}

    /** 'availabilities' is optional; omit or send empty list when feature is off. */
    public record ScheduleRequest(
        List<EmployeeDTO> employees,
        List<TaskDTO> tasks,
        List<AvailabilityDTO> availabilities,
        Integer minRestHours,
        String restMode // "HARD"/"SOFT" - consider to change to enum
    ) {}

    public record AssignmentDTO(Long taskId, String taskName, ZonedDateTime start, ZonedDateTime end, Set<String> requiredSkills,
                                Long employeeId, String employeeName) {}
    public record SolveResponse(List<AssignmentDTO> assignments, String score, long unassignedCount) {}
}
