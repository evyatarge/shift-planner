package com.example.shiftplanner.domain;

import java.util.Objects;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

/** One staffing slot for a Task that requires N employees => N Assignments. */
@PlanningEntity
public class Assignment {

    @PlanningId
    private Long id;
    private Task task;

    @PlanningVariable(valueRangeProviderRefs = "employeeRange")
    private Employee employee; // nullable means unassigned

    public Assignment(){}
    public Assignment(Long id, Task task){ this.id = id; this.task = task; }

    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public Task getTask(){return task;} public void setTask(Task task){this.task=task;}
    public Employee getEmployee(){return employee;} public void setEmployee(Employee employee){this.employee=employee;}

    @Override public boolean equals(Object o){ return o instanceof Assignment a && Objects.equals(id,a.id); }
    @Override public int hashCode(){ return Objects.hash(id); }
    @Override public String toString(){ return "Assign(" + id + "->" + (employee!=null?employee.getName():"null") + ")"; }
}
