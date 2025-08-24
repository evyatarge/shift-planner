package com.example.shiftplanner.domain;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

/** Planning solution containing facts and planning entities. */
@PlanningSolution
public class Schedule {

    @ValueRangeProvider(id = "employeeRange")
    @ProblemFactCollectionProperty
    private List<Employee> employeeList;

    @ProblemFactCollectionProperty
    private List<Task> taskList;

    // Optional availability facts. If empty or null, availability constraint has no effect.
    @ProblemFactCollectionProperty
    private List<Availability> availabilityList;

    @PlanningEntityCollectionProperty
    private List<Assignment> assignmentList;

    @PlanningScore
    private HardSoftScore score;

    public Schedule(){}

    public Schedule(List<Employee> employeeList, List<Task> taskList, List<Availability> availabilityList, List<Assignment> assignmentList) {
        this.employeeList = employeeList;
        this.taskList = taskList;
        this.availabilityList = availabilityList;
        this.assignmentList = assignmentList;
    }

    public List<Employee> getEmployeeList(){ return employeeList; }
    public void setEmployeeList(List<Employee> employeeList){ this.employeeList = employeeList; }

    public List<Task> getTaskList(){ return taskList; }
    public void setTaskList(List<Task> taskList){ this.taskList = taskList; }

    public List<Availability> getAvailabilityList(){ return availabilityList; }
    public void setAvailabilityList(List<Availability> availabilityList){ this.availabilityList = availabilityList; }

    public List<Assignment> getAssignmentList(){ return assignmentList; }
    public void setAssignmentList(List<Assignment> assignmentList){ this.assignmentList = assignmentList; }

    public HardSoftScore getScore(){ return score; }
    public void setScore(HardSoftScore score){ this.score = score; }
}
