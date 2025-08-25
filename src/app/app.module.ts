import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatTableModule } from '@angular/material/table';
import { MatSelectModule } from '@angular/material/select';
import { MatCardModule } from '@angular/material/card';

import { AppComponent } from './app.component';
import { EmployeesEditorComponent } from './employees-editor.component';
import { TasksEditorComponent } from './tasks-editor.component';
import { ResultsViewComponent } from './results-view.component';

@NgModule({
  declarations: [],
  imports: [
    AppComponent, BrowserModule, BrowserAnimationsModule, FormsModule, ReactiveFormsModule, HttpClientModule,
    MatFormFieldModule, MatInputModule, MatButtonModule, MatIconModule, MatChipsModule, MatTableModule, MatSelectModule, MatCardModule,
    EmployeesEditorComponent, TasksEditorComponent, ResultsViewComponent
  ],
})
export class AppModule {}
