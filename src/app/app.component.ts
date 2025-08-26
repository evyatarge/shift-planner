import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

import { EmployeesEditorComponent } from './components/employees/employees-editor.component';
import { TasksEditorComponent } from './components/tasks/tasks-editor.component';
import { ResultsViewComponent } from './components/results/results-view.component';
// import { ExplanationPanelComponent } from './components/explanation-panel/explanation-panel.component';

import { ApiService, ScheduleRequest } from './services/api.service';
import { Employee, Task, SolveResponse, Explanation } from './models';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule, MatButtonModule, MatSnackBarModule,
    EmployeesEditorComponent, TasksEditorComponent, ResultsViewComponent, // ExplanationPanelComponent
  ],
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {
  title = 'שבצק-אותי משמרות';

  employees: Employee[] = [
    // { id: 1, name: 'אליס', skills: ['מפקד'] },
    // { id: 2, name: 'בוב', skills: ['נהג', 'בנאי'] }
  ];

  tasks: Task[] = [
    // { id: 10, name: 'סיור בוקר', start: '2025-08-24T08:00', end: '2025-08-24T12:00', requiredSkills: ['נהג'], requiredEmployees: 1 }
  ];

  result: SolveResponse | null = null;
  explanation: Explanation | null = null; // optional use of explanation for constraint details
  solving = false;

  private api = inject(ApiService);
  private snack = inject(MatSnackBar);

  ngOnInit(): void {
    // load saved employees (if any) from backend
    this.api.getEmployees().subscribe({
      next: list => { if (list && list.length) this.employees = list; },
      error: err => console.warn('Failed to load saved employees (using defaults).', err)
    });
  }

  onEmployeesChange(list: Employee[]) { this.employees = list; }
  onTasksChange(list: Task[]) { this.tasks = list; }

  saveEmployees(): void {
    this.api.saveEmployees(this.employees).subscribe({
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      next: _ => this.snack.open('העובדים נשמרו בהצלחה', 'close', { duration: 1800 }),
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      error: err => this.snack.open('שמירה נכשלה', 'close', { duration: 2500 })
    });
  }

  solve() {
    const req: ScheduleRequest = { employees: this.employees, tasks: this.tasks };
    this.solving = true;
    this.result = null;
    // this.explanation = null;

    // this.api.solveWithExplain(req).subscribe({
    this.api.solve(req).subscribe({
      next: res => { 
        this.result = res; //.result;
        // this.explanation = res.explanation;
        this.solving = false;
      },
      error: err => {
        console.error(err); this.solving = false;
      }
    });
  }
}
