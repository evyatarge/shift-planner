import { Component, Input } from '@angular/core';
import { SolveResponse } from './models';

@Component({
  selector: 'app-results-view',
  templateUrl: './results-view.component.html'
})
export class ResultsViewComponent {
  @Input() result!: SolveResponse;
}
