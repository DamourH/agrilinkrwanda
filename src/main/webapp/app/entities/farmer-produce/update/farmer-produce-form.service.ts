import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IFarmerProduce, NewFarmerProduce } from '../farmer-produce.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IFarmerProduce for edit and NewFarmerProduceFormGroupInput for create.
 */
type FarmerProduceFormGroupInput = IFarmerProduce | PartialWithRequiredKeyOf<NewFarmerProduce>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IFarmerProduce | NewFarmerProduce> = Omit<T, 'availableFrom' | 'availableUntil'> & {
  availableFrom?: string | null;
  availableUntil?: string | null;
};

type FarmerProduceFormRawValue = FormValueOf<IFarmerProduce>;

type NewFarmerProduceFormRawValue = FormValueOf<NewFarmerProduce>;

type FarmerProduceFormDefaults = Pick<NewFarmerProduce, 'id' | 'availableFrom' | 'availableUntil'>;

type FarmerProduceFormGroupContent = {
  id: FormControl<FarmerProduceFormRawValue['id'] | NewFarmerProduce['id']>;
  quantity: FormControl<FarmerProduceFormRawValue['quantity']>;
  unit: FormControl<FarmerProduceFormRawValue['unit']>;
  pricePerUnit: FormControl<FarmerProduceFormRawValue['pricePerUnit']>;
  availableFrom: FormControl<FarmerProduceFormRawValue['availableFrom']>;
  availableUntil: FormControl<FarmerProduceFormRawValue['availableUntil']>;
  grade: FormControl<FarmerProduceFormRawValue['grade']>;
  farmer: FormControl<FarmerProduceFormRawValue['farmer']>;
};

export type FarmerProduceFormGroup = FormGroup<FarmerProduceFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class FarmerProduceFormService {
  createFarmerProduceFormGroup(farmerProduce: FarmerProduceFormGroupInput = { id: null }): FarmerProduceFormGroup {
    const farmerProduceRawValue = this.convertFarmerProduceToFarmerProduceRawValue({
      ...this.getFormDefaults(),
      ...farmerProduce,
    });
    return new FormGroup<FarmerProduceFormGroupContent>({
      id: new FormControl(
        { value: farmerProduceRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      quantity: new FormControl(farmerProduceRawValue.quantity, {
        validators: [Validators.required],
      }),
      unit: new FormControl(farmerProduceRawValue.unit, {
        validators: [Validators.required],
      }),
      pricePerUnit: new FormControl(farmerProduceRawValue.pricePerUnit, {
        validators: [Validators.required],
      }),
      availableFrom: new FormControl(farmerProduceRawValue.availableFrom, {
        validators: [Validators.required],
      }),
      availableUntil: new FormControl(farmerProduceRawValue.availableUntil),
      grade: new FormControl(farmerProduceRawValue.grade),
      farmer: new FormControl(farmerProduceRawValue.farmer),
    });
  }

  getFarmerProduce(form: FarmerProduceFormGroup): IFarmerProduce | NewFarmerProduce {
    return this.convertFarmerProduceRawValueToFarmerProduce(form.getRawValue() as FarmerProduceFormRawValue | NewFarmerProduceFormRawValue);
  }

  resetForm(form: FarmerProduceFormGroup, farmerProduce: FarmerProduceFormGroupInput): void {
    const farmerProduceRawValue = this.convertFarmerProduceToFarmerProduceRawValue({ ...this.getFormDefaults(), ...farmerProduce });
    form.reset(
      {
        ...farmerProduceRawValue,
        id: { value: farmerProduceRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): FarmerProduceFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      availableFrom: currentTime,
      availableUntil: currentTime,
    };
  }

  private convertFarmerProduceRawValueToFarmerProduce(
    rawFarmerProduce: FarmerProduceFormRawValue | NewFarmerProduceFormRawValue,
  ): IFarmerProduce | NewFarmerProduce {
    return {
      ...rawFarmerProduce,
      availableFrom: dayjs(rawFarmerProduce.availableFrom, DATE_TIME_FORMAT),
      availableUntil: dayjs(rawFarmerProduce.availableUntil, DATE_TIME_FORMAT),
    };
  }

  private convertFarmerProduceToFarmerProduceRawValue(
    farmerProduce: IFarmerProduce | (Partial<NewFarmerProduce> & FarmerProduceFormDefaults),
  ): FarmerProduceFormRawValue | PartialWithRequiredKeyOf<NewFarmerProduceFormRawValue> {
    return {
      ...farmerProduce,
      availableFrom: farmerProduce.availableFrom ? farmerProduce.availableFrom.format(DATE_TIME_FORMAT) : undefined,
      availableUntil: farmerProduce.availableUntil ? farmerProduce.availableUntil.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
