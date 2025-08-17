import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IPurchaseOrder } from 'app/entities/purchase-order/purchase-order.model';
import { PurchaseOrderService } from 'app/entities/purchase-order/service/purchase-order.service';
import { IFarmerProduce } from 'app/entities/farmer-produce/farmer-produce.model';
import { FarmerProduceService } from 'app/entities/farmer-produce/service/farmer-produce.service';
import { OrderItemService } from '../service/order-item.service';
import { IOrderItem } from '../order-item.model';
import { OrderItemFormGroup, OrderItemFormService } from './order-item-form.service';

@Component({
  selector: 'jhi-order-item-update',
  templateUrl: './order-item-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class OrderItemUpdateComponent implements OnInit {
  isSaving = false;
  orderItem: IOrderItem | null = null;

  purchaseOrdersSharedCollection: IPurchaseOrder[] = [];
  farmerProducesSharedCollection: IFarmerProduce[] = [];

  protected orderItemService = inject(OrderItemService);
  protected orderItemFormService = inject(OrderItemFormService);
  protected purchaseOrderService = inject(PurchaseOrderService);
  protected farmerProduceService = inject(FarmerProduceService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: OrderItemFormGroup = this.orderItemFormService.createOrderItemFormGroup();

  comparePurchaseOrder = (o1: IPurchaseOrder | null, o2: IPurchaseOrder | null): boolean =>
    this.purchaseOrderService.comparePurchaseOrder(o1, o2);

  compareFarmerProduce = (o1: IFarmerProduce | null, o2: IFarmerProduce | null): boolean =>
    this.farmerProduceService.compareFarmerProduce(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ orderItem }) => {
      this.orderItem = orderItem;
      if (orderItem) {
        this.updateForm(orderItem);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const orderItem = this.orderItemFormService.getOrderItem(this.editForm);
    if (orderItem.id !== null) {
      this.subscribeToSaveResponse(this.orderItemService.update(orderItem));
    } else {
      this.subscribeToSaveResponse(this.orderItemService.create(orderItem));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOrderItem>>): void {
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

  protected updateForm(orderItem: IOrderItem): void {
    this.orderItem = orderItem;
    this.orderItemFormService.resetForm(this.editForm, orderItem);

    this.purchaseOrdersSharedCollection = this.purchaseOrderService.addPurchaseOrderToCollectionIfMissing<IPurchaseOrder>(
      this.purchaseOrdersSharedCollection,
      orderItem.order,
    );
    this.farmerProducesSharedCollection = this.farmerProduceService.addFarmerProduceToCollectionIfMissing<IFarmerProduce>(
      this.farmerProducesSharedCollection,
      orderItem.farmerProduce,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.purchaseOrderService
      .query()
      .pipe(map((res: HttpResponse<IPurchaseOrder[]>) => res.body ?? []))
      .pipe(
        map((purchaseOrders: IPurchaseOrder[]) =>
          this.purchaseOrderService.addPurchaseOrderToCollectionIfMissing<IPurchaseOrder>(purchaseOrders, this.orderItem?.order),
        ),
      )
      .subscribe((purchaseOrders: IPurchaseOrder[]) => (this.purchaseOrdersSharedCollection = purchaseOrders));

    this.farmerProduceService
      .query()
      .pipe(map((res: HttpResponse<IFarmerProduce[]>) => res.body ?? []))
      .pipe(
        map((farmerProduces: IFarmerProduce[]) =>
          this.farmerProduceService.addFarmerProduceToCollectionIfMissing<IFarmerProduce>(farmerProduces, this.orderItem?.farmerProduce),
        ),
      )
      .subscribe((farmerProduces: IFarmerProduce[]) => (this.farmerProducesSharedCollection = farmerProduces));
  }
}
