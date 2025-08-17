import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IFarmerProduce } from '../farmer-produce.model';

@Component({
  selector: 'jhi-farmer-produce-detail',
  templateUrl: './farmer-produce-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class FarmerProduceDetailComponent {
  farmerProduce = input<IFarmerProduce | null>(null);

  previousState(): void {
    window.history.back();
  }
}
