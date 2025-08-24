import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ScheduleRequest, SolveResponse } from '../models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  constructor(private http: HttpClient) {}
  solve(req: ScheduleRequest): Observable<SolveResponse> {
    return this.http.post<SolveResponse>('/api/solve', req);
  }
}
