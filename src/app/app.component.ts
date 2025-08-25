import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { EmployeesEditorComponent } from './components/employees/employees-editor.component';
import { TasksEditorComponent } from './components/tasks/tasks-editor.component';
import { ResultsViewComponent } from './components/results/results-view.component';
import { ApiService } from './services/api.service';
import { Employee, Task, SolveResponse, ScheduleRequest } from './models';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, MatButtonModule, EmployeesEditorComponent, TasksEditorComponent, ResultsViewComponent],
  templateUrl: './app.component.html'
})
export class AppComponent {
  title = 'שבצק-אותי משמרות';
  employees: Employee[] = [
    { id: 1, name: 'אליס', skills: ['מפקד'] },
    { id: 2, name: 'בוב', skills: ['נהג', 'בנאי'] }
  ];
  tasks: Task[] = [
    { id: 10, name: 'סיור בוקר', start: '2025-08-24T08:00', end: '2025-08-24T12:00', requiredSkills: ['נהג'], requiredEmployees: 1 }
  ];
  result: SolveResponse | null = null;
  solving = false;

  private api = inject(ApiService);

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
