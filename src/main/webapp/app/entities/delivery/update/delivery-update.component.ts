import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IPurchaseOrder } from 'app/entities/purchase-order/purchase-order.model';
import { PurchaseOrderService } from 'app/entities/purchase-order/service/purchase-order.service';
import { OrderStatus } from 'app/entities/enumerations/order-status.model';
import { DeliveryService } from '../service/delivery.service';
import { IDelivery } from '../delivery.model';
import { DeliveryFormGroup, DeliveryFormService } from './delivery-form.service';

@Component({
  selector: 'jhi-delivery-update',
  templateUrl: './delivery-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class DeliveryUpdateComponent implements OnInit {
  isSaving = false;
  delivery: IDelivery | null = null;
  orderStatusValues = Object.keys(OrderStatus);

  usersSharedCollection: IUser[] = [];
  purchaseOrdersSharedCollection: IPurchaseOrder[] = [];

  protected deliveryService = inject(DeliveryService);
  protected deliveryFormService = inject(DeliveryFormService);
  protected userService = inject(UserService);
  protected purchaseOrderService = inject(PurchaseOrderService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: DeliveryFormGroup = this.deliveryFormService.createDeliveryFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  comparePurchaseOrder = (o1: IPurchaseOrder | null, o2: IPurchaseOrder | null): boolean =>
    this.purchaseOrderService.comparePurchaseOrder(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ delivery }) => {
      this.delivery = delivery;
      if (delivery) {
        this.updateForm(delivery);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const delivery = this.deliveryFormService.getDelivery(this.editForm);
    if (delivery.id !== null) {
      this.subscribeToSaveResponse(this.deliveryService.update(delivery));
    } else {
      this.subscribeToSaveResponse(this.deliveryService.create(delivery));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDelivery>>): void {
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

  protected updateForm(delivery: IDelivery): void {
    this.delivery = delivery;
    this.deliveryFormService.resetForm(this.editForm, delivery);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, delivery.driver);
    this.purchaseOrdersSharedCollection = this.purchaseOrderService.addPurchaseOrderToCollectionIfMissing<IPurchaseOrder>(
      this.purchaseOrdersSharedCollection,
      delivery.order,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.delivery?.driver)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.purchaseOrderService
      .query()
      .pipe(map((res: HttpResponse<IPurchaseOrder[]>) => res.body ?? []))
      .pipe(
        map((purchaseOrders: IPurchaseOrder[]) =>
          this.purchaseOrderService.addPurchaseOrderToCollectionIfMissing<IPurchaseOrder>(purchaseOrders, this.delivery?.order),
        ),
      )
      .subscribe((purchaseOrders: IPurchaseOrder[]) => (this.purchaseOrdersSharedCollection = purchaseOrders));
  }
}
