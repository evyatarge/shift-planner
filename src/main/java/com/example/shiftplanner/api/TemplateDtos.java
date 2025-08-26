package com.example.shiftplanner.api;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public final class TemplateDtos {
    public record RoleReqDTO(String skill, int count) {}
    public record ShiftTemplateDTO(String name, LocalTime start, LocalTime end, List<RoleReqDTO> roles) {}
    public record GenerateDailyRequest(LocalDate date, List<ShiftTemplateDTO> templates) {}
}
