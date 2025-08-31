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

@Component({
  selector: 'app-results-view',
  standalone: true,
  imports: [CommonModule, MatTableModule],
  templateUrl: './results-view.component.html'
})
export class ResultsViewComponent {
  @Input() result!: SolveResponse;

  get groupedAssignments(): Array<WorkScheduleEntry> {
    if (!this.result || !this.result.assignments) return [];
    
    const map = new Map<string, WorkScheduleEntry>();
    
    this.result.assignments.forEach(task => {
      const key = `${task.taskName}|${task.start}|${task.end}`;
      if (!map.has(key)) {
        map.set(key, { taskName: task.taskName, start: task.start, end: task.end, employeeNames: [] });
      }
      if (task.employeeName) {
        map.get(key)!.employeeNames.push(task.employeeName);
      }
    });

    return Array.from(map.values());
  }

  getEmployeeNames(a: Partial<WorkScheduleEntry>): string {
    return a.employeeNames.length ? a.employeeNames.join(', ') : '—';
  }
}
