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

  onCsv(ev: Event) {
    const input = ev.target as HTMLInputElement;
    const file = input.files && input.files[0]; if (!file) return;
    const reader = new FileReader();
    reader.onload = () => {
      const text = String(reader.result || '');
      const lines = text.split(/\r?\n/).map(l => l.trim()).filter(Boolean);
      if (!lines.length) return;

      // Detect header (hebrew/english)
      const headerTokens = lines[0].split(/[\,\t;|]/).map(t => t.trim().toLowerCase());
      const isHeader = headerTokens.some(t => ['name','שם'].includes(t)) ||
                       headerTokens.some(t => ['start','התחלה','שעה התחלה'].includes(t)) ||
                       headerTokens.some(t => ['end','סיום','שעה סיום'].includes(t));
      const startIdx = isHeader ? 1 : 0;

      let maxId = this.tasks.length ? Math.max(...this.tasks.map(t => t.id)) : 0;
      for (let i = startIdx; i < lines.length; i++) {
        const raw = lines[i];
        const cols = raw.split(',');
        // Expected order: name,start,end,requiredSkills,requiredEmployees
        const name = (cols[0] || '').trim();
        const start = (cols[1] || '').trim();
        const end = (cols[2] || '').trim();
        const skillsPart = (cols[3] || '').trim();
        const reqEmpStr = (cols[4] || '').trim();
        if (!name || !start || !end) continue;
        const requiredSkills = skillsPart ? skillsPart.split(/[;|]/).map(s => s.trim()).filter(Boolean) : [];
        const requiredEmployees = Math.max(1, Number.parseInt(reqEmpStr || '1', 10) || 1);
        maxId += 1;
        this.tasks.push({ id: maxId, name, start, end, requiredSkills, requiredEmployees });
      }
      this.tasks = [...this.tasks];
      this.tasksChange.emit(this.tasks);
      input.value = '';
    };
    reader.readAsText(file);
  }

  downloadCsvTemplate() {
    const content = "שם,התחלה,סיום,כישורים נדרשים,כמות לוחמים\n" +
                    "שמירה,2025-08-24T08:00,2025-08-24T16:00,מפקד;נהג,2\n" +
                    "חמ" + "ל,2025-08-24T16:00,2025-08-24T23:00,חמליסט,1\n";
    const blob = new Blob([content], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a'); a.href = url; a.download = 'tasks_template.csv'; a.click();
    URL.revokeObjectURL(url);
  }

}
