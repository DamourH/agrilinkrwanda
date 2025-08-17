import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IProduce } from '../produce.model';
import { ProduceService } from '../service/produce.service';

@Component({
  templateUrl: './produce-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ProduceDeleteDialogComponent {
  produce?: IProduce;

  protected produceService = inject(ProduceService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.produceService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
