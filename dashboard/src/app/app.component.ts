import { Component } from '@angular/core';
import { AggregateOverviewComponent } from './features/aggregates/aggregate-overview.component';

@Component({ selector: 'app-root', standalone: true, imports: [AggregateOverviewComponent], template: '<app-aggregate-overview />' })
export class AppComponent {}
