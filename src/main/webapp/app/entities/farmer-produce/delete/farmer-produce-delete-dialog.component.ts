import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IFarmerProduce } from '../farmer-produce.model';
import { FarmerProduceService } from '../service/farmer-produce.service';

@Component({
  templateUrl: './farmer-produce-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class FarmerProduceDeleteDialogComponent {
  farmerProduce?: IFarmerProduce;

  protected farmerProduceService = inject(FarmerProduceService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.farmerProduceService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
