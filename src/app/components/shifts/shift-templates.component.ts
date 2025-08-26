import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { Task } from '../../models';
import {MatTimepickerModule} from "@angular/material/timepicker";
import {provideNativeDateAdapter} from "@angular/material/core";
import {MatDatepickerModule} from "@angular/material/datepicker";

interface ShiftTemplate { id: number; name: string; start: string; end: string; requiredEmployees: number; } // HH:mm

@Component({
  selector: 'app-shift-templates',
  standalone: true,
  providers: [provideNativeDateAdapter()],
  imports: [CommonModule, FormsModule, MatFormFieldModule, MatInputModule, MatButtonModule, MatCardModule, MatTimepickerModule, MatDatepickerModule],
  templateUrl: './shift-templates.component.html',
  styles: [`.flex{display:flex;gap:12px;flex-wrap:wrap;align-items:center}.list{display:grid;gap:8px;margin-top:8px}`]
})
export class ShiftTemplatesComponent {
  @Output() tasksGenerated = new EventEmitter<Task[]>();

  templates: ShiftTemplate[] = [];
  form: ShiftTemplate = { id: 1, name: '', start: '', end: '', requiredEmployees: 1 };
  targetDate = '';

  add() {
    if (!this.form.name || !this.form.start || !this.form.end) return;
    const id = this.templates.length ? Math.max(...this.templates.map(t => t.id)) + 1 : 1;
    this.templates = [...this.templates, { ...this.form, id }];
    this.form = { id: id+1, name:'', start:'', end:'', requiredEmployees: 1 };
  }
  remove(s: ShiftTemplate) { this.templates = this.templates.filter(t => t !== s); }

  generate() {
    if (!this.targetDate) return;
    const tasks: Task[] = this.templates.map((t, idx) => ({
      id: Date.now() + idx, // temp id
      name: t.name,
      start: `${this.targetDate}T${t.start}`,
      end: `${this.targetDate}T${t.end}`,
      requiredSkills: [],
      requiredEmployees: t.requiredEmployees
    }));
    this.tasksGenerated.emit(tasks);
  }
}