import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatCardModule } from '@angular/material/card';
import { Employee } from '../../models';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';

@Component({
  selector: 'app-employees-editor',
  templateUrl: './employees-editor.component.html',
  styleUrls: ['employees-editor.component.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule, MatFormFieldModule, MatInputModule, MatButtonModule, MatSlideToggleModule, MatIconModule, MatChipsModule, MatCardModule],
})
export class EmployeesEditorComponent {
  @Input() employees: Employee[] = [];
  @Output() employeesChange = new EventEmitter<Employee[]>();

  
  newNamesList = '';


  newName = '';
  newSkill = '';
  tempSkills: string[] = [];

  addSkill() {
    if (this.newSkill.trim()) {
      this.tempSkills.push(this.newSkill.trim());
      this.newSkill = '';
    }
  }

  
  
  addEmployeesList() {
    if (!this.newNamesList.trim()) return;

    const parsedEmpList = this.newNamesList.split(',');
    parsedEmpList.forEach(emp => {
      this.newName = emp;
      this.addEmployee();
    });
    
    this.newNamesList = '';
    this.tempSkills = [];
  }



  addEmployee() {
    if (!this.newName.trim()) return;
    const id = this.employees.length ? Math.max(...this.employees.map(e => e.id)) + 1 : 1;
    const emp: Employee = { id, name: this.newName.trim(), skills: [...this.tempSkills] };
    this.employees = [...this.employees, emp];
    this.employeesChange.emit(this.employees);
    this.newName = '';
    this.tempSkills = [];
  }

  editAddSkill(emp: Employee) {
    const employee = this.employees.find(e => e.id === emp.id);
    const newSkill = prompt('כישור להוספה:');
    employee.skills.push(newSkill);
    this.employeesChange.emit(this.employees);
  }
  
  editremoveSkill(emp: Employee, firstTime = true) {
    const employee = this.employees.find(e => e.id === emp.id);
    let title = 'כישור להסרה:';
    if (!firstTime) {
      const titlePrefix = 'אנא הזן כישור קיים - ';
      title = titlePrefix + title;
    }
    const skillToRemove = prompt(title);

    const found = employee.skills.some(skill => skill === skillToRemove);
    if (found || !skillToRemove) {
      employee.skills = employee.skills.filter(skill => skill !== skillToRemove);
      this.employeesChange.emit(this.employees);
    } else {
      this.editremoveSkill(emp, false);
    }
  }

  remove(emp: Employee) {
    // TODO - add a confirmation to remove employee

    // const confirmed = confirm('בטוח שברצונך למחוק את '+emp.name);
    // if (confirmed) {
      this.employees = this.employees.filter(e => e !== emp);
      this.employeesChange.emit(this.employees);
    // }
  }

  onCsv(ev: Event) {
    const input = ev.target as HTMLInputElement;
    const file = input.files && input.files[0]; if (!file) return;
    const reader = new FileReader();
    reader.onload = () => {
      const text = String(reader.result || '');
      const lines = text.split(/\r?\n/).map(l => l.trim()).filter(Boolean);
      // תומך גם בכותרת אופציונלית name,skills
      const startIdx = lines[0]?.toLowerCase().startsWith('name') ? 1 : 0;
      const nextId = () => (this.employees.length ? Math.max(...this.employees.map(e => e.id)) + 1 : 1);
      for (let i=startIdx;i<lines.length;i++){
        const raw = lines[i];
        // פיצול בסיסי: עמודה ראשונה שם, השאר כישורים (מופרדים ב-, ; |)
        const parts = raw.split(',').map(p => p.trim()).filter(Boolean);
        if (!parts.length) continue;
        const name = parts[0];
        const rest = raw.slice(raw.indexOf(',')+1).trim();
        const skills = (rest ? rest.split(/[;,|]/) : []).map(s => s.trim()).filter(Boolean);
        const emp: Employee = { id: nextId(), name, skills, active: true };
        this.employees.push(emp);
      }
      this.employees = [...this.employees];
      this.employeesChange.emit(this.employees);
      input.value = '';
    };
    reader.readAsText(file);
  }

  downloadCsvTemplate() {
    const content = "שם,כישורים\nהמפקד,מפקד;נהג\nהחייל,חמליסט\n";
    const blob = new Blob([content], {type:'text/csv;charset=utf-8;'});
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a'); a.href = url; a.download = 'template.csv'; a.click();
    URL.revokeObjectURL(url);
  }

}
