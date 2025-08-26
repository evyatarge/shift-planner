import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatTimepickerModule } from '@angular/material/timepicker';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import {provideNativeDateAdapter} from '@angular/material/core';
import { Task } from '../../models';

@Component({
  selector: 'app-tasks-editor',
  templateUrl: './tasks-editor.component.html',
  standalone: true,
  providers: [provideNativeDateAdapter()],
  imports: [CommonModule, FormsModule, MatFormFieldModule, MatInputModule, MatButtonModule, MatTimepickerModule],
})
export class TasksEditorComponent {
  @Input() tasks: Task[] = [];
  @Output() tasksChange = new EventEmitter<Task[]>();

  form: Task = { id: 1, name: '', start: '', end: '', requiredSkills: [], requiredEmployees: 1 };
  newSkill = '';

  addSkill() {
    if (this.newSkill.trim()) {
      this.form.requiredSkills = [...this.form.requiredSkills, this.newSkill.trim()];
      this.newSkill = '';
    }
  }

  addTask() {
    if (!this.form.name || !this.form.start || !this.form.end) return;
    const id = this.tasks.length ? Math.max(...this.tasks.map(t => t.id)) + 1 : 1;
    
    console.log(this.form);
    
    const taskToAdd: Task = { ...this.form, id };
    this.tasks = [...this.tasks, taskToAdd];
    this.tasksChange.emit(this.tasks);
    this.form = { id: id + 1, name: '', start: '', end: '', requiredSkills: [], requiredEmployees: 1 };
  }

  remove(task: Task) {
    this.tasks = this.tasks.filter(t => t !== task);
    this.tasksChange.emit(this.tasks);
  }

}
