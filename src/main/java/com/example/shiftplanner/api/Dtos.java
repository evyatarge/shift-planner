package com.example.shiftplanner.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class Dtos {

    public record EmployeeDTO(Long id, String name, Set<String> skills, Boolean active) {}
    public record TaskDTO(Long id, String name, LocalDateTime start, LocalDateTime end, Set<String> requiredSkills, int requiredEmployees) {}
    public record AvailabilityDTO(Long employeeId, LocalDateTime start, LocalDateTime end) {}

    /** 'availabilities' is optional; omit or send empty list when feature is off. */
    public record ScheduleRequest(
        List<EmployeeDTO> employees,
        List<TaskDTO> tasks,
        List<AvailabilityDTO> availabilities,
        Integer minRestHours,
        String restMode // "HARD"/"SOFT" - consider to change to enum
    ) {}

    public record AssignmentDTO(Long taskId, String taskName, LocalDateTime start, LocalDateTime end, Set<String> requiredSkills,
                                Long employeeId, String employeeName) {}
    public record SolveResponse(List<AssignmentDTO> assignments, String score, long unassignedCount) {}
}
