import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatCardModule } from '@angular/material/card';
import { Employee } from './models';

@Component({
  selector: 'app-employees-editor',
  standalone: true,
  imports: [CommonModule, FormsModule, MatFormFieldModule, MatInputModule, MatButtonModule, MatIconModule, MatChipsModule, MatCardModule],
  templateUrl: './employees-editor.component.html'
})
export class EmployeesEditorComponent {
  @Input() employees: Employee[] = [];
  @Output() employeesChange = new EventEmitter<Employee[]>();

  newName = '';
  newSkill = '';
  tempSkills: string[] = [];

  addSkill() {
    if (this.newSkill.trim()) {
      this.tempSkills.push(this.newSkill.trim());
      this.newSkill = '';
    }
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

  remove(emp: Employee) {
    this.employees = this.employees.filter(e => e !== emp);
    this.employeesChange.emit(this.employees);
  }
}
