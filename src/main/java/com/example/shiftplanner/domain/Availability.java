package com.example.shiftplanner.domain;

import java.time.ZonedDateTime;
import java.util.Objects;

/** Employee availability window. */
public class Availability {
    private Long employeeId;
    private ZonedDateTime start;
    private ZonedDateTime end;

    public Availability() {}
    public Availability(Long employeeId, ZonedDateTime start, ZonedDateTime end) {
        this.employeeId = employeeId; this.start = start; this.end = end;
    }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long id) { this.employeeId = id; }

    public ZonedDateTime getStart() { return start; }
    public void setStart(ZonedDateTime start) { this.start = start; }

    public ZonedDateTime getEnd() { return end; }
    public void setEnd(ZonedDateTime end) { this.end = end; }

    @Override public boolean equals(Object o) { return o instanceof Availability a && Objects.equals(employeeId, a.employeeId); }
    @Override public int hashCode() { return Objects.hash(employeeId); }
    @Override public String toString() { return "Avail(" + employeeId + ")"; }
}
