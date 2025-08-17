import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPurchaseOrder, NewPurchaseOrder } from '../purchase-order.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPurchaseOrder for edit and NewPurchaseOrderFormGroupInput for create.
 */
type PurchaseOrderFormGroupInput = IPurchaseOrder | PartialWithRequiredKeyOf<NewPurchaseOrder>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IPurchaseOrder | NewPurchaseOrder> = Omit<T, 'orderDate'> & {
  orderDate?: string | null;
};

type PurchaseOrderFormRawValue = FormValueOf<IPurchaseOrder>;

type NewPurchaseOrderFormRawValue = FormValueOf<NewPurchaseOrder>;

type PurchaseOrderFormDefaults = Pick<NewPurchaseOrder, 'id' | 'orderDate'>;

type PurchaseOrderFormGroupContent = {
  id: FormControl<PurchaseOrderFormRawValue['id'] | NewPurchaseOrder['id']>;
  orderDate: FormControl<PurchaseOrderFormRawValue['orderDate']>;
  status: FormControl<PurchaseOrderFormRawValue['status']>;
  totalAmount: FormControl<PurchaseOrderFormRawValue['totalAmount']>;
  deliveryAddress: FormControl<PurchaseOrderFormRawValue['deliveryAddress']>;
  notes: FormControl<PurchaseOrderFormRawValue['notes']>;
  buyer: FormControl<PurchaseOrderFormRawValue['buyer']>;
};

export type PurchaseOrderFormGroup = FormGroup<PurchaseOrderFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PurchaseOrderFormService {
  createPurchaseOrderFormGroup(purchaseOrder: PurchaseOrderFormGroupInput = { id: null }): PurchaseOrderFormGroup {
    const purchaseOrderRawValue = this.convertPurchaseOrderToPurchaseOrderRawValue({
      ...this.getFormDefaults(),
      ...purchaseOrder,
    });
    return new FormGroup<PurchaseOrderFormGroupContent>({
      id: new FormControl(
        { value: purchaseOrderRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      orderDate: new FormControl(purchaseOrderRawValue.orderDate, {
        validators: [Validators.required],
      }),
      status: new FormControl(purchaseOrderRawValue.status, {
        validators: [Validators.required],
      }),
      totalAmount: new FormControl(purchaseOrderRawValue.totalAmount, {
        validators: [Validators.required],
      }),
      deliveryAddress: new FormControl(purchaseOrderRawValue.deliveryAddress, {
        validators: [Validators.required],
      }),
      notes: new FormControl(purchaseOrderRawValue.notes),
      buyer: new FormControl(purchaseOrderRawValue.buyer),
    });
  }

  getPurchaseOrder(form: PurchaseOrderFormGroup): IPurchaseOrder | NewPurchaseOrder {
    return this.convertPurchaseOrderRawValueToPurchaseOrder(form.getRawValue() as PurchaseOrderFormRawValue | NewPurchaseOrderFormRawValue);
  }

  resetForm(form: PurchaseOrderFormGroup, purchaseOrder: PurchaseOrderFormGroupInput): void {
    const purchaseOrderRawValue = this.convertPurchaseOrderToPurchaseOrderRawValue({ ...this.getFormDefaults(), ...purchaseOrder });
    form.reset(
      {
        ...purchaseOrderRawValue,
        id: { value: purchaseOrderRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PurchaseOrderFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      orderDate: currentTime,
    };
  }

  private convertPurchaseOrderRawValueToPurchaseOrder(
    rawPurchaseOrder: PurchaseOrderFormRawValue | NewPurchaseOrderFormRawValue,
  ): IPurchaseOrder | NewPurchaseOrder {
    return {
      ...rawPurchaseOrder,
      orderDate: dayjs(rawPurchaseOrder.orderDate, DATE_TIME_FORMAT),
    };
  }

  private convertPurchaseOrderToPurchaseOrderRawValue(
    purchaseOrder: IPurchaseOrder | (Partial<NewPurchaseOrder> & PurchaseOrderFormDefaults),
  ): PurchaseOrderFormRawValue | PartialWithRequiredKeyOf<NewPurchaseOrderFormRawValue> {
    return {
      ...purchaseOrder,
      orderDate: purchaseOrder.orderDate ? purchaseOrder.orderDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
