import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { SolveResponse } from '../../models';

@Component({
  selector: 'app-results-view',
  standalone: true,
  imports: [CommonModule, MatTableModule],
  templateUrl: './results-view.component.html'
})
export class ResultsViewComponent {
  @Input() result!: SolveResponse;

  parseUtcDate(dateString: string): Date {
    // Parse the date string as UTC, then convert to local time for display
    return new Date(dateString + 'Z');
  }
}
