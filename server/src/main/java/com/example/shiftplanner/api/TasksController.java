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
    public List<TaskDTO> getTasks() {
        List<Task> tasks = store.load();
        return tasks.stream()
                .map(t -> new TaskDTO(t.getId(), t.getName(), t.getStart(), t.getEnd(), t.getRequiredSkills(), t.getRequiredEmployees(), t.isAllowEmptySlots(), t.isIs24HourTask()))
                .toList();
    }

    @PostMapping("/tasks")
    public List<TaskDTO> saveTasks(@RequestBody List<TaskDTO> tasks) {
        List<Task> taskEntities = tasks.stream()
                .map(dto -> {
                    Long id = dto.id() != null ? dto.id() : System.currentTimeMillis() + System.identityHashCode(dto);
                    return new Task(id, dto.name(), dto.start(), dto.end(), dto.requiredSkills(), dto.requiredEmployees(), 
                                   dto.allowEmptySlots() != null ? dto.allowEmptySlots() : false,
                                   dto.is24HourTask() != null ? dto.is24HourTask() : false);
                })
                .toList();
        
        store.save(taskEntities);
        
        return taskEntities.stream()
                .map(t -> new TaskDTO(t.getId(), t.getName(), t.getStart(), t.getEnd(), t.getRequiredSkills(), t.getRequiredEmployees(), t.isAllowEmptySlots(), t.isIs24HourTask()))
                .toList();
    }
}