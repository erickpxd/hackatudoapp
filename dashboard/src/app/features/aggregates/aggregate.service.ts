import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../core/api.config';

export interface ClassroomAggregate {
  classroomId: string;
  sessionCount: number;
  averageDurationMinutes: number;
  completionRate: number;
  interventionCount: number;
  trend: string;
}

@Injectable({ providedIn: 'root' })
export class AggregateService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = inject(API_BASE_URL);
  getClassroom(classroomId: string): Observable<ClassroomAggregate> {
    return this.http.get<ClassroomAggregate>(`${this.baseUrl}/aggregates/classrooms/${encodeURIComponent(classroomId)}`);
  }
}
