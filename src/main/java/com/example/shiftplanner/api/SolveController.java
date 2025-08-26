
package com.example.shiftplanner.api;

import com.example.shiftplanner.api.Dtos.AssignmentDTO;
import com.example.shiftplanner.api.Dtos.AvailabilityDTO;
import com.example.shiftplanner.api.Dtos.EmployeeDTO;
import com.example.shiftplanner.api.Dtos.ScheduleRequest;
import com.example.shiftplanner.api.Dtos.SolveResponse;
import com.example.shiftplanner.api.Dtos.TaskDTO;
import com.example.shiftplanner.domain.*;

import static com.example.shiftplanner.api.Dtos.*;

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
                    var m = e.getClass().getMethod("active");
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
                        Math.max(1, t.requiredEmployees())));
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
            for (int i=0; i<t.getRequiredEmployees(); i++) {
                slots.add(new Assignment(idGen.getAndIncrement(), t));
            }
        }

        int minRestHours = (req.minRestHours() != null) ? req.minRestHours() : 0; // ברירת מחדל: 0ברירת מחדל: 0
        String restMode = (req.restMode() != null) ? req.restMode() : "SOFT";
        var settingsList = java.util.List.of(new com.example.shiftplanner.domain.SchedulingSettings(minRestHours, restMode));

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
        return new SolveResponse(out, score, unassigned);
    }
}
