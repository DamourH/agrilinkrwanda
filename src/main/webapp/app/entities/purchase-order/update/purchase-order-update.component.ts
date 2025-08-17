import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { OrderStatus } from 'app/entities/enumerations/order-status.model';
import { PurchaseOrderService } from '../service/purchase-order.service';
import { IPurchaseOrder } from '../purchase-order.model';
import { PurchaseOrderFormGroup, PurchaseOrderFormService } from './purchase-order-form.service';

@Component({
  selector: 'jhi-purchase-order-update',
  templateUrl: './purchase-order-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PurchaseOrderUpdateComponent implements OnInit {
  isSaving = false;
  purchaseOrder: IPurchaseOrder | null = null;
  orderStatusValues = Object.keys(OrderStatus);

  usersSharedCollection: IUser[] = [];

  protected purchaseOrderService = inject(PurchaseOrderService);
  protected purchaseOrderFormService = inject(PurchaseOrderFormService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PurchaseOrderFormGroup = this.purchaseOrderFormService.createPurchaseOrderFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ purchaseOrder }) => {
      this.purchaseOrder = purchaseOrder;
      if (purchaseOrder) {
        this.updateForm(purchaseOrder);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const purchaseOrder = this.purchaseOrderFormService.getPurchaseOrder(this.editForm);
    if (purchaseOrder.id !== null) {
      this.subscribeToSaveResponse(this.purchaseOrderService.update(purchaseOrder));
    } else {
      this.subscribeToSaveResponse(this.purchaseOrderService.create(purchaseOrder));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPurchaseOrder>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(purchaseOrder: IPurchaseOrder): void {
    this.purchaseOrder = purchaseOrder;
    this.purchaseOrderFormService.resetForm(this.editForm, purchaseOrder);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, purchaseOrder.buyer);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.purchaseOrder?.buyer)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
