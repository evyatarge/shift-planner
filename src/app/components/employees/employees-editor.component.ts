import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatCardModule } from '@angular/material/card';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { Employee } from '../../models';

@Component({
  selector: 'app-employees-editor',
  standalone: true,
  templateUrl: './employees-editor.component.html',
  styleUrls: ['./employees-editor.component.scss'],
  imports: [
    CommonModule, FormsModule, MatFormFieldModule, MatInputModule, MatButtonModule,
    MatIconModule, MatChipsModule, MatCardModule, MatSlideToggleModule
  ]
})
export class EmployeesEditorComponent {
  @Input() employees: Employee[] = [];
  @Output() employeesChange = new EventEmitter<Employee[]>();

  newName = '';
  newNamesList = '';
  newSkill = '';
  tempSkills: string[] = [];

  addSkill() {
    const s = this.newSkill.trim();
    if (!s) return;
    this.tempSkills = [...this.tempSkills, s];
    this.newSkill = '';
  }

  addEmployee() {
    const name = this.newName.trim();
    if (!name) return;
    const nextId = this.employees.length ? Math.max(...this.employees.map(e => e.id)) + 1 : 1;
    const emp: Employee = { id: nextId, name, skills: [...this.tempSkills], active: true };
    this.employees = [...this.employees, emp];
    this.tempSkills = [];
    this.newName = '';
    this.emit();
  }

  addEmployeesList() {
    const raw = this.newNamesList || '';
    const items = raw.split(/[\n,;|]/).map(s => s.trim()).filter(Boolean);
    if (!items.length) return;
    let maxId = this.employees.length ? Math.max(...this.employees.map(e => e.id)) : 0;
    const added: Employee[] = [];
    for (const name of items) {
      maxId += 1;
      added.push({ id: maxId, name, skills: [], active: true });
    }
    this.employees = [...this.employees, ...added];
    this.newNamesList = '';
    this.emit();
  }

  removeEmployee(e: Employee) {
    this.employees = this.employees.filter(x => x !== e);
    this.emit();
  }

  editAddSkill(e: Employee) {
    const s = prompt(`הוסף כישור עבור ${e.name}:`)?.trim();
    if (!s) return;
    if (!e.skills.includes(s)) {
      e.skills = [...e.skills, s];
      this.emit();
    }
  }

  editRemoveSkill(e: Employee) {
    const s = prompt(`הסר כישור עבור ${e.name}:\n(כתוב בדיוק כפי שמופיע)` )?.trim();
    if (!s) return;
    e.skills = e.skills.filter(x => x !== s);
    this.emit();
  }

  onCsv(ev: Event) {
    const input = ev.target as HTMLInputElement;
    const file = input.files && input.files[0]; if (!file) return;
    const reader = new FileReader();
    reader.onload = () => {
      const text = String(reader.result || '');
      const lines = text.split(/\r?\n/).map(l => l.trim()).filter(Boolean);
      if (!lines.length) return;

      // Header detection (hebrew/english)
      const headerTokens = lines[0].split(/[,\t;]/).map(t => t.trim().toLowerCase());
      const isHeader = headerTokens.some(t => ['name','שם'].includes(t)) ||
                       headerTokens.some(t => ['skills','כישורים','מיומנויות'].includes(t));
      const startIdx = isHeader ? 1 : 0;

      let maxId = this.employees.length ? Math.max(...this.employees.map(e => e.id)) : 0;
      for (let i = startIdx; i < lines.length; i++) {
        const raw = lines[i];
        const firstComma = raw.indexOf(',');
        let name = raw;
        let skillsPart = '';
        if (firstComma >= 0) {
          name = raw.slice(0, firstComma).trim();
          skillsPart = raw.slice(firstComma + 1).trim();
        }
        if (!name) continue;
        const skills = (skillsPart ? skillsPart.split(/[;,|]/) : []).map(s => s.trim()).filter(Boolean);
        maxId += 1;
        this.employees.push({ id: maxId, name, skills, active: true });
      }
      this.employees = [...this.employees];
      this.emit();
      input.value = '';
    };
    reader.readAsText(file);
  }

  downloadCsvTemplate() {
    const content = "שם,כישורים\nהמפקד,מפקד;נהג\nהחייל,חמליסט\n";
    const blob = new Blob([content], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a'); a.href = url; a.download = 'employees_template.csv'; a.click();
    URL.revokeObjectURL(url);
  }

  private emit(){ this.employeesChange.emit(this.employees); }
}
