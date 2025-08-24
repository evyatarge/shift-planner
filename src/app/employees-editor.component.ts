import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Employee } from './models';

@Component({
  selector: 'app-employees-editor',
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
