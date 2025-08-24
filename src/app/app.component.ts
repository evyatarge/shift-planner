import { Component } from '@angular/core';
import { ApiService } from './services/api.service';
import { Employee, Task, ScheduleRequest, SolveResponse } from './models';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent {
  title = 'תכנון משמרות';
  employees: Employee[] = [
    { id: 1, name: 'אליס', skills: ['cashier', 'stock'] },
    { id: 2, name: 'בוב', skills: ['cashier'] }
  ];
  tasks: Task[] = [
    { id: 10, name: 'משמרת בוקר', start: '2025-08-24T08:00', end: '2025-08-24T12:00', requiredSkills: ['cashier'], requiredEmployees: 1 }
  ];
  result: SolveResponse | null = null;
  solving = false;

  constructor(private api: ApiService) {}

  onEmployeesChange(list: Employee[]) { this.employees = list; }
  onTasksChange(list: Task[]) { this.tasks = list; }

  solve() {
    const req: ScheduleRequest = { employees: this.employees, tasks: this.tasks };
    this.solving = true;
    this.api.solve(req).subscribe({
      next: res => { this.result = res; this.solving = false; },
      error: err => { console.error(err); this.solving = false; }
    });
  }
}
