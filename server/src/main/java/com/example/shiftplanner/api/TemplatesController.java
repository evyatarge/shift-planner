package com.example.shiftplanner.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.*;

import static com.example.shiftplanner.api.Dtos.*;
import static com.example.shiftplanner.api.TemplateDtos.*;

@RestController
@RequestMapping("/api/templates")
@CrossOrigin
public class TemplatesController {

    @PostMapping("/generateDaily")
    public List<TaskDTO> generateDaily(@RequestBody GenerateDailyRequest req) {
        List<TaskDTO> out = new ArrayList<>();
        if (req == null || req.templates() == null || req.date() == null) return out;
        long id = System.currentTimeMillis();
        for (ShiftTemplateDTO t : req.templates()) {
            if (t.roles() == null) continue;
            for (RoleReqDTO r : t.roles()) {
                if (r.count() <= 0) continue;
                String name = t.name() + " – " + r.skill();
                var start = req.date().atTime(t.start());
                var end   = req.date().atTime(t.end());
                out.add(new TaskDTO(id++, name, start, end, Set.of(r.skill()), r.count(), false, false));
            }
        }
        return out;
    }
}
