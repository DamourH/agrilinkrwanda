import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IProduce, NewProduce } from '../produce.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProduce for edit and NewProduceFormGroupInput for create.
 */
type ProduceFormGroupInput = IProduce | PartialWithRequiredKeyOf<NewProduce>;

type ProduceFormDefaults = Pick<NewProduce, 'id'>;

type ProduceFormGroupContent = {
  id: FormControl<IProduce['id'] | NewProduce['id']>;
  name: FormControl<IProduce['name']>;
  description: FormControl<IProduce['description']>;
  category: FormControl<IProduce['category']>;
};

export type ProduceFormGroup = FormGroup<ProduceFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProduceFormService {
  createProduceFormGroup(produce: ProduceFormGroupInput = { id: null }): ProduceFormGroup {
    const produceRawValue = {
      ...this.getFormDefaults(),
      ...produce,
    };
    return new FormGroup<ProduceFormGroupContent>({
      id: new FormControl(
        { value: produceRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(produceRawValue.name, {
        validators: [Validators.required],
      }),
      description: new FormControl(produceRawValue.description),
      category: new FormControl(produceRawValue.category),
    });
  }

  getProduce(form: ProduceFormGroup): IProduce | NewProduce {
    return form.getRawValue() as IProduce | NewProduce;
  }

  resetForm(form: ProduceFormGroup, produce: ProduceFormGroupInput): void {
    const produceRawValue = { ...this.getFormDefaults(), ...produce };
    form.reset(
      {
        ...produceRawValue,
        id: { value: produceRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ProduceFormDefaults {
    return {
      id: null,
    };
  }
}
