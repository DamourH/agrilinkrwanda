import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IProduce } from '../produce.model';

@Component({
  selector: 'jhi-produce-detail',
  templateUrl: './produce-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class ProduceDetailComponent {
  produce = input<IProduce | null>(null);

  previousState(): void {
    window.history.back();
  }
}
