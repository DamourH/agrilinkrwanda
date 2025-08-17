import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IDelivery, NewDelivery } from '../delivery.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDelivery for edit and NewDeliveryFormGroupInput for create.
 */
type DeliveryFormGroupInput = IDelivery | PartialWithRequiredKeyOf<NewDelivery>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IDelivery | NewDelivery> = Omit<T, 'pickupDate' | 'deliveryDate'> & {
  pickupDate?: string | null;
  deliveryDate?: string | null;
};

type DeliveryFormRawValue = FormValueOf<IDelivery>;

type NewDeliveryFormRawValue = FormValueOf<NewDelivery>;

type DeliveryFormDefaults = Pick<NewDelivery, 'id' | 'pickupDate' | 'deliveryDate'>;

type DeliveryFormGroupContent = {
  id: FormControl<DeliveryFormRawValue['id'] | NewDelivery['id']>;
  pickupDate: FormControl<DeliveryFormRawValue['pickupDate']>;
  deliveryDate: FormControl<DeliveryFormRawValue['deliveryDate']>;
  status: FormControl<DeliveryFormRawValue['status']>;
  driver: FormControl<DeliveryFormRawValue['driver']>;
  order: FormControl<DeliveryFormRawValue['order']>;
};

export type DeliveryFormGroup = FormGroup<DeliveryFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DeliveryFormService {
  createDeliveryFormGroup(delivery: DeliveryFormGroupInput = { id: null }): DeliveryFormGroup {
    const deliveryRawValue = this.convertDeliveryToDeliveryRawValue({
      ...this.getFormDefaults(),
      ...delivery,
    });
    return new FormGroup<DeliveryFormGroupContent>({
      id: new FormControl(
        { value: deliveryRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      pickupDate: new FormControl(deliveryRawValue.pickupDate),
      deliveryDate: new FormControl(deliveryRawValue.deliveryDate),
      status: new FormControl(deliveryRawValue.status),
      driver: new FormControl(deliveryRawValue.driver),
      order: new FormControl(deliveryRawValue.order),
    });
  }

  getDelivery(form: DeliveryFormGroup): IDelivery | NewDelivery {
    return this.convertDeliveryRawValueToDelivery(form.getRawValue() as DeliveryFormRawValue | NewDeliveryFormRawValue);
  }

  resetForm(form: DeliveryFormGroup, delivery: DeliveryFormGroupInput): void {
    const deliveryRawValue = this.convertDeliveryToDeliveryRawValue({ ...this.getFormDefaults(), ...delivery });
    form.reset(
      {
        ...deliveryRawValue,
        id: { value: deliveryRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): DeliveryFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      pickupDate: currentTime,
      deliveryDate: currentTime,
    };
  }

  private convertDeliveryRawValueToDelivery(rawDelivery: DeliveryFormRawValue | NewDeliveryFormRawValue): IDelivery | NewDelivery {
    return {
      ...rawDelivery,
      pickupDate: dayjs(rawDelivery.pickupDate, DATE_TIME_FORMAT),
      deliveryDate: dayjs(rawDelivery.deliveryDate, DATE_TIME_FORMAT),
    };
  }

  private convertDeliveryToDeliveryRawValue(
    delivery: IDelivery | (Partial<NewDelivery> & DeliveryFormDefaults),
  ): DeliveryFormRawValue | PartialWithRequiredKeyOf<NewDeliveryFormRawValue> {
    return {
      ...delivery,
      pickupDate: delivery.pickupDate ? delivery.pickupDate.format(DATE_TIME_FORMAT) : undefined,
      deliveryDate: delivery.deliveryDate ? delivery.deliveryDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
