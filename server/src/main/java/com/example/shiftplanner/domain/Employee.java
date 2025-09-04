package com.example.shiftplanner.domain;

import java.util.Objects;
import java.util.Set;
import org.optaplanner.core.api.domain.lookup.PlanningId;

/** Employee fact with a set of skills. */
public class Employee {

    @PlanningId
    private Long id;
    private String name;
    private Set<String> skills;
    private Boolean active;

    public Employee() {}
    public Employee(Long id, String name, Set<String> skills) {
        this.id = id; this.name = name; this.skills = skills;
    }
    public Employee(Long id, String name, Set<String> skills, Boolean active) {
        this.id = id; this.name = name; this.skills = skills; this.active = active;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Set<String> getSkills() { return skills; }
    public void setSkills(Set<String> skills) { this.skills = skills; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    @Override public boolean equals(Object o){ return o instanceof Employee e && Objects.equals(id,e.id); }
    @Override public int hashCode(){ return Objects.hash(id); }
    @Override public String toString(){ return "Emp(" + id + "," + name + ")"; }
}
