package com.example.shiftplanner.api;

import com.example.shiftplanner.domain.Employee;
import com.example.shiftplanner.store.EmployeeStore;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import static com.example.shiftplanner.api.Dtos.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class EmployeesController {

    private final EmployeeStore store;

    public EmployeesController(EmployeeStore store) {
        this.store = store;
    }

    @GetMapping("/employees")
    public List<EmployeeDTO> getAll() {
        List<Employee> list = store.load();
        List<EmployeeDTO> out = new ArrayList<>();
        for (Employee e : list) {
            out.add(new EmployeeDTO(e.getId(), e.getName(), e.getSkills()));
        }
        return out;
    }

    /** keeps the existing employees and adds new ones */
    @PostMapping("/employees")
    public List<EmployeeDTO> saveAll(@RequestBody List<EmployeeDTO> dtos) {
        // simple validation + DTO to domain conversion
        Set<Long> ids = new HashSet<>();
        List<Employee> list = new ArrayList<>();
        for (EmployeeDTO d : dtos) {
            if (d.id() == null) throw new IllegalArgumentException("Employee id is required");
            if (!ids.add(d.id())) throw new IllegalArgumentException("Duplicate employee id: " + d.id());
            list.add(new Employee(d.id(), d.name(), d.skills()));
        }
        List<Employee> saved = store.save(list);
        List<EmployeeDTO> out = new ArrayList<>();
        for (Employee e : saved) {
            out.add(new EmployeeDTO(e.getId(), e.getName(), e.getSkills()));
        }
        return out;
    }
}