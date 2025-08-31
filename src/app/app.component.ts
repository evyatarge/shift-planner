import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

import { EmployeesEditorComponent } from './components/employees/employees-editor.component';
import { TasksEditorComponent } from './components/tasks/tasks-editor.component';
import { ResultsViewComponent } from './components/results/results-view.component';
// import { ExplanationPanelComponent } from './components/explanation-panel/explanation-panel.component';

import { ApiService, ScheduleRequest } from './services/api.service';
import { Employee, Task, SolveResponse, Explanation, RestMode } from './models';
import { ShiftTemplatesComponent } from './components/shifts/shift-templates.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule, MatButtonModule, MatSnackBarModule, MatFormFieldModule, MatInputModule, MatSelectModule, FormsModule, ReactiveFormsModule,
    EmployeesEditorComponent, TasksEditorComponent, ResultsViewComponent, ShiftTemplatesComponent // ExplanationPanelComponent
  ],
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {
  title = 'שבצק-אותי משמרות';

  employees: Employee[] = [];
  tasks: Task[] = [];
  result: SolveResponse | null = null;
  explanation: Explanation | null = null; // optional use of explanation for constraint details
  solving = false;
  // settings for constraints
  minRestHours = 0;
  restMode: RestMode = 'SOFT';

  private api = inject(ApiService);
  private snack = inject(MatSnackBar);

  ngOnInit(): void {
    // load saved employees (if any) from backend
    this.api.getEmployees().subscribe({
      next: employeesList => { if (employeesList?.length) this.employees = employeesList.map(employee => ({...employee, active: employee.active ?? true})); },
      error: err => { console.error('Failed to load saved employees (using defaults).', err); }
    });
    this.api.getTasks().subscribe({
      next: tasksList => {
        this.convertTasksTimesTo000Z(tasksList); // TODO - should fix and change

        if (tasksList?.length) this.tasks = tasksList;
      },
      error: err => { console.error('Failed to load saved tasks (using defaults).', err); }
    });
    // load last result (if any) from backend
    this.api.getLastResults().subscribe({
      next: lastResult => { this.result = lastResult; },
      error: err => { console.info('no last results to load.', err); }
    });
  }

  private convertTasksTimesTo000Z(tasksList: Task[]) {
    tasksList.forEach(task => { task.start += '.000Z'; task.end += '.000Z'; });
  }

  onEmployeesChange(list: Employee[]) { this.employees = list; }
  onTasksChange(list: Task[]) { this.tasks = list; console.log('tasks change: ', list, list[list.length-1])}

  saveEmployees(): void {
    const payload = this.employees;
    this.api.saveEmployees(payload).subscribe({
      next: savedEmps => {
        this.snack.open('העובדים נשמרו בהצלחה', 'x', { duration: 1800 });
        this.employees = savedEmps;
      },
      error: err => {
        console.error('Failed to save employees.', err);
        this.snack.open('שמירת עובדים נכשלה', 'x', { duration: 2500 });
      }
    });
  }

  saveTasks(): void {
    this.api.saveTasks(this.tasks).subscribe({
      next: savedTasks => {
        this.snack.open('המשימות נשמרו', 'x', { duration: 1800 });
        this.convertTasksTimesTo000Z(savedTasks);
        this.tasks = savedTasks;
      },
      error: err => {
        console.error('Failed to save tasks.', err);
        this.snack.open('שמירת משימות נכשלה', 'x', { duration: 2500 });
      }
    });
  }

  onTasksGenerated(list: Task[]) {
    // change/add tasks for generated day - here I chose to add
    this.tasks = [...this.tasks, ...list];
    this.snack.open(`נוצרו ${list.length} משימות ליממה`, 'x', { duration: 1800 });
  }
  
  solve() {
    const activeEmployees = this.employees.filter(e => e.active !== false);
    const req: ScheduleRequest = {
      employees: activeEmployees,
      tasks: this.tasks,
      minRestHours: this.minRestHours,
      restMode: this.restMode
    };
    
    this.solving = true;
    this.result = null;
    this.explanation = null;
    this.checkActiveEmployees(activeEmployees);
    if (activeEmployees.length !== 0) {
      this.api.solve(req).subscribe({
        next: res => { this.result = res; this.solving = false; },
        error: err => { console.error(err); this.solving = false; }
      });
    }
  }

  checkActiveEmployees(activeEmployees: Employee[]) {
    if (activeEmployees.length === 0) {
      this.snack.open('אין עובדים פעילים', 'x', { duration: 2500 });
    }

  }

}
