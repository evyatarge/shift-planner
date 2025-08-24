package com.example.shiftplanner.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import org.optaplanner.core.api.domain.lookup.PlanningId;

/** Task/shift that needs staffing, with time window and required skills. */
public class Task {

    @PlanningId
    private Long id;
    private String name;
    private LocalDateTime start;
    private LocalDateTime end;
    private Set<String> requiredSkills;
    private int requiredEmployees;

    public Task() {}
    public Task(Long id, String name, LocalDateTime start, LocalDateTime end, Set<String> requiredSkills, int requiredEmployees) {
        this.id = id; this.name = name; this.start = start; this.end = end; this.requiredSkills = requiredSkills; this.requiredEmployees = requiredEmployees;
    }

    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getName(){return name;} public void setName(String name){this.name=name;}
    public LocalDateTime getStart(){return start;} public void setStart(LocalDateTime start){this.start=start;}
    public LocalDateTime getEnd(){return end;} public void setEnd(LocalDateTime end){this.end=end;}
    public Set<String> getRequiredSkills(){return requiredSkills;} public void setRequiredSkills(Set<String> s){this.requiredSkills=s;}
    public int getRequiredEmployees(){return requiredEmployees;} public void setRequiredEmployees(int n){this.requiredEmployees=n;}

    @Override public boolean equals(Object o){ return o instanceof Task t && Objects.equals(id,t.id); }
    @Override public int hashCode(){ return Objects.hash(id); }
    @Override public String toString(){ return "Task(" + id + "," + name + ")"; }
}
