import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SolveResponse, Employee, Task, ExplainSolveResponse } from '../models';

export interface ScheduleRequest { employees: Employee[]; tasks: Task[]; availabilities?: any[]; }

@Injectable({ providedIn: 'root' })
export class ApiService {

  private base = '/api';
  
  private http = inject(HttpClient);


  solve(req: ScheduleRequest): Observable<SolveResponse> {
    return this.http.post<SolveResponse>(`${this.base}/solve`, req);
  }

  solveWithExplain(req: ScheduleRequest): Observable<ExplainSolveResponse> {
    return this.http.post<ExplainSolveResponse>(`${this.base}/solve/explain`, req);
  }

  // persistence - save data on the BE (employees list)
  getEmployees(): Observable<Employee[]> {
    return this.http.get<Employee[]>(`${this.base}/employees`);
  }

  saveEmployees(employees: Employee[]): Observable<Employee[]> {
    return this.http.post<Employee[]>(`${this.base}/employees`, employees);
  }
}