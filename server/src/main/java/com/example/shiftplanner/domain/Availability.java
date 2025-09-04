package com.example.shiftplanner.domain;

import java.time.LocalDateTime;
import java.util.Objects;

/** Employee availability window [start, end). Optional; when feature is disabled, it's ignored. */
public class Availability {
    private Long employeeId;
    private LocalDateTime start;
    private LocalDateTime end;

    public Availability(){}
    public Availability(Long employeeId, LocalDateTime start, LocalDateTime end){
        this.employeeId = employeeId; this.start = start; this.end = end;
    }

    public Long getEmployeeId(){return employeeId;} public void setEmployeeId(Long id){this.employeeId=id;}
    public LocalDateTime getStart(){return start;} public void setStart(LocalDateTime start){this.start=start;}
    public LocalDateTime getEnd(){return end;} public void setEnd(LocalDateTime end){this.end=end;}

    @Override public boolean equals(Object o){
        return o instanceof Availability a && Objects.equals(employeeId,a.employeeId)
               && Objects.equals(start,a.start) && Objects.equals(end,a.end);
    }
    @Override public int hashCode(){ return Objects.hash(employeeId,start,end); }
}
