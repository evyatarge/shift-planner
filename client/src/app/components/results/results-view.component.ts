import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { SolveResponse, AssignmentDTO } from '../../models';

interface WorkScheduleEntry {
  taskName: string;
  start: string;
  end: string;
  employeeNames: string[];
}

interface TaskGroup {
  taskName: string;
  assignments: WorkScheduleEntry[];
}

@Component({
  selector: 'app-results-view',
  standalone: true,
  imports: [CommonModule, MatTableModule],
  templateUrl: './results-view.component.html'
})
export class ResultsViewComponent {
  @Input() result!: SolveResponse;

  get groupedByTask(): Array<TaskGroup> {
    if (!this.result || !this.result.assignments) return [];
    
    const taskMap = new Map<string, Map<string, WorkScheduleEntry>>();
    
    this.result.assignments.forEach(task => {
      if (!taskMap.has(task.taskName)) {
        taskMap.set(task.taskName, new Map<string, WorkScheduleEntry>());
      }
      
      const timeKey = `${task.start}|${task.end}`;
      const taskGroup = taskMap.get(task.taskName)!;
      
      if (!taskGroup.has(timeKey)) {
        taskGroup.set(timeKey, { 
          taskName: task.taskName, 
          start: task.start, 
          end: task.end, 
          employeeNames: [] 
        });
      }
      
      if (task.employeeName) {
        taskGroup.get(timeKey)!.employeeNames.push(task.employeeName);
      }
    });

    return Array.from(taskMap.entries()).map(([taskName, assignments]) => ({
      taskName,
      assignments: Array.from(assignments.values())
    }));
  }

  getEmployeeNames(a: Partial<WorkScheduleEntry>): string {
    return a.employeeNames?.length ? a.employeeNames.join(', ') : '—';
  }
}
