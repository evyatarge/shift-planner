package com.example.shiftplanner.api;

import com.example.shiftplanner.domain.Task;
import com.example.shiftplanner.store.TaskStore;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import static com.example.shiftplanner.api.Dtos.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class TasksController {

    private final TaskStore store;

    public TasksController(TaskStore store) { this.store = store; }

    @GetMapping("/tasks")
    public List<TaskDTO> getAll() {
        List<Task> list = store.load();
        List<TaskDTO> out = new ArrayList<>();
        for (Task t : list) {
            out.add(new TaskDTO(t.getId(), t.getName(), t.getStart(), t.getEnd(), t.getRequiredSkills(), t.getRequiredEmployees()));
        }
        return out;
    }

    @PostMapping("/tasks")
    public List<TaskDTO> saveAll(@RequestBody List<TaskDTO> dtos) {
        Set<Long> ids = new HashSet<>();
        List<Task> tasks = new ArrayList<>();
        for (TaskDTO d : dtos) {
            if (d.id() == null) throw new IllegalArgumentException("Task id is required");
            if (!ids.add(d.id())) throw new IllegalArgumentException("Duplicate task id: " + d.id());
            tasks.add(new Task(d.id(), d.name(), d.start(), d.end(), d.requiredSkills(), d.requiredEmployees()));
        }
        List<Task> saved = store.save(tasks);
        List<TaskDTO> out = new ArrayList<>();
        for (Task t : saved) {
            out.add(new TaskDTO(t.getId(), t.getName(), t.getStart(), t.getEnd(), t.getRequiredSkills(), t.getRequiredEmployees()));
        }
        return out;
    }
}