import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AggregateService, ClassroomAggregate } from './aggregate.service';

@Component({
  selector: 'app-aggregate-overview',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <main>
      <h1>Indicadores da turma</h1>
      <p>Somente totais coletivos — sem filtros, exportação ou detalhes individuais.</p>
      <label>Turma <input [(ngModel)]="classroomId" aria-label="Identificador da turma"></label>
      <button (click)="load()">Carregar totais</button>
      <button (click)="createLocalClassroom()">Criar turma demonstrativa</button>
      <p *ngIf="loading">Carregando…</p><p role="alert" *ngIf="error">{{ error }}</p>
      <section class="grid" *ngIf="aggregate as data">
        <article class="card">Sessões <strong>{{ data.sessionCount }}</strong></article>
        <article class="card">Duração média <strong>{{ data.averageDurationMinutes }} min</strong></article>
        <article class="card">Conclusão <strong>{{ data.completionRate | percent }}</strong></article>
        <article class="card">Intervenções <strong>{{ data.interventionCount }}</strong></article>
        <article class="card">Tendência <strong>{{ data.trend }}</strong></article>
      </section>
    </main>`,
  styles: [`main{max-width:900px;margin:auto;padding:2rem}.grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(180px,1fr));gap:1rem;margin-top:1rem}label{display:block;margin:1rem 0}`],
})
export class AggregateOverviewComponent {
  private readonly service = inject(AggregateService);
  classroomId = '20000000-0000-0000-0000-000000000001';
  aggregate: ClassroomAggregate | null = null;
  loading = false;
  error = '';
  load(): void {
    this.loading = true; this.error = '';
    this.service.getClassroom(this.classroomId).subscribe({ next: value => { this.aggregate = value; this.loading = false; }, error: () => { this.error = 'Não foi possível carregar os totais.'; this.loading = false; } });
  }
  createLocalClassroom(): void { this.classroomId = crypto.randomUUID(); this.aggregate = null; }
}
