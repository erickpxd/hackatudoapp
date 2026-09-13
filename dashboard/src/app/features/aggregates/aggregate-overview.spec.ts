import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AggregateOverviewComponent } from './aggregate-overview.component';
import { API_BASE_URL } from '../../core/api.config';

describe('AggregateOverviewComponent', () => {
  let fixture: ComponentFixture<AggregateOverviewComponent>;
  let http: HttpTestingController;
  beforeEach(async () => {
    await TestBed.configureTestingModule({ imports: [AggregateOverviewComponent, HttpClientTestingModule], providers: [{ provide: API_BASE_URL, useValue: 'https://api.test/v1' }] }).compileComponents();
    fixture = TestBed.createComponent(AggregateOverviewComponent); http = TestBed.inject(HttpTestingController); fixture.detectChanges();
  });
  afterEach(() => http.verify());
  it('renders aggregate and has no individual drill-down', () => {
    fixture.componentInstance.load();
    http.expectOne(/\/aggregates\/classrooms\//).flush({ classroomId: 'x', sessionCount: 42, averageDurationMinutes: 31, completionRate: .86, interventionCount: 9, trend: 'melhorando' });
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('42');
    expect(fixture.nativeElement.textContent).not.toContain('estudante individual');
    expect(fixture.nativeElement.querySelector('a')).toBeNull();
  });
  it('renders a recoverable error', () => {
    fixture.componentInstance.load(); http.expectOne(/\/aggregates\/classrooms\//).flush({}, { status: 500, statusText: 'Error' }); fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('[role=alert]').textContent).toContain('Não foi possível');
  });
});
