package com.example.shiftplanner.api;

import com.example.shiftplanner.api.Dtos.AssignmentDTO;
import com.example.shiftplanner.api.Dtos.AvailabilityDTO;
import com.example.shiftplanner.api.Dtos.EmployeeDTO;
import com.example.shiftplanner.api.Dtos.ScheduleRequest;
import com.example.shiftplanner.api.Dtos.SolveResponse;
import com.example.shiftplanner.api.Dtos.TaskDTO;
import com.example.shiftplanner.domain.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class SolveController {

    private final SolverConfig solverConfig;
    private final AtomicLong idGen = new AtomicLong(1L);

    @Value("${feature.availability.enabled:false}")
    private boolean availabilityEnabled;

    public SolveController(SolverConfig solverConfig) {
        this.solverConfig = solverConfig;
    }

    @PostMapping("/solve")
    public SolveResponse solve(@RequestBody ScheduleRequest req) {
        List<Employee> employees = new ArrayList<>();
        if (req.employees() != null) {
            for (EmployeeDTO e : req.employees()) {
                // Skip inactive employees if DTO exposes 'active' (Boolean)
                try {
                    var m = e.getClass().getMethod("getActive");
                    Object val = m.invoke(e);
                    if (val instanceof Boolean b && !b) continue;
                } catch (Exception ignore) {}
                employees.add(new Employee(e.id()!=null?e.id():idGen.getAndIncrement(), e.name(), e.skills()));
            }
        }

        List<Task> tasks = new ArrayList<>();
        if (req.tasks() != null) {
            for (TaskDTO t : req.tasks()) {
                tasks.add(new Task(t.id()!=null?t.id():idGen.getAndIncrement(), t.name(), t.start(), t.end(), t.requiredSkills(),
                        Math.max(1, t.requiredEmployees()), 
                        t.allowEmptySlots() != null ? t.allowEmptySlots() : false,
                        t.is24HourTask() != null ? t.is24HourTask() : false));
            }
        }

        List<Availability> avails = new ArrayList<>();
        if (availabilityEnabled && req.availabilities() != null) {
            for (AvailabilityDTO a : req.availabilities()) {
                avails.add(new Availability(a.employeeId(), a.start(), a.end()));
            }
        }

        List<Assignment> slots = new ArrayList<>();
        for (Task t : tasks) {
            var reqSkills = t.getRequiredSkills();
            int headcount = Math.max(1, t.getRequiredEmployees());
            int roleCount = (reqSkills == null) ? 0 : reqSkills.size();

            // Create one slot per required skill (role)
            if (reqSkills != null) {
                for (String skill : reqSkills) {
                    slots.add(new Assignment(idGen.getAndIncrement(), t, skill));
                }
            }
            // Remaining generic slots if headcount exceeds number of distinct role skills
            for (int i = roleCount; i < headcount; i++) {
                slots.add(new Assignment(idGen.getAndIncrement(), t));
            }
        }

        int minRestHours = (req.minRestHours() != null || req.minRestHours() != 0) ? req.minRestHours() : 6;
        String restMode = (req.restMode() != null) ? req.restMode() : "HARD";
        var settingsList = List.of(new SchedulingSettings(minRestHours, restMode));

        Schedule problem = new Schedule(employees, tasks, avails, slots, settingsList);

        // Build a Solver and solve synchronously (simple and robust)
        SolverFactory<Schedule> factory = SolverFactory.create(solverConfig);
        Solver<Schedule> solver = factory.buildSolver();
        Schedule best = solver.solve(problem);

        List<AssignmentDTO> out = new ArrayList<>();
        long unassigned = 0L;
        for (Assignment a : best.getAssignmentList()) {
            Task t = a.getTask();
            if (a.getEmployee() == null) {
                unassigned++;
                out.add(new AssignmentDTO(t.getId(), t.getName(), t.getStart(), t.getEnd(), t.getRequiredSkills(), null, null));
            } else {
                out.add(new AssignmentDTO(t.getId(), t.getName(), t.getStart(), t.getEnd(), t.getRequiredSkills(),
                        a.getEmployee().getId(), a.getEmployee().getName()));
            }
        }
        String score = best.getScore()!=null ? best.getScore().toString() : "0hard/0soft";
        // Save result to data-storage/result.json, to have last result available to load faster
        String timestamp = Instant.now().toString();
        try {
            Path storageDir = Paths.get("data-storage");
            Files.createDirectories(storageDir);
            Path resultFile = storageDir.resolve("result.json");
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.writeValue(resultFile.toFile(), new SolveResponse(out, score, unassigned, timestamp));
        } catch (Exception e) {
            // Log error or handle as needed
            e.printStackTrace();
        }
        return new SolveResponse(out, score, unassigned, timestamp);
    }
    // getLastResult
    @GetMapping("/lastResult")
    public SolveResponse getLastResult() {
        try {
            Path resultFile = Paths.get("data-storage/result.json");
            if (!Files.exists(resultFile)) {
                return null;
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper.readValue(resultFile.toFile(), SolveResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
