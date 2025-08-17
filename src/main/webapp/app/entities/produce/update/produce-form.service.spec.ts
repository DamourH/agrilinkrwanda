import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../produce.test-samples';

import { ProduceFormService } from './produce-form.service';

describe('Produce Form Service', () => {
  let service: ProduceFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProduceFormService);
  });

  describe('Service methods', () => {
    describe('createProduceFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProduceFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            category: expect.any(Object),
          }),
        );
      });

      it('passing IProduce should create a new form with FormGroup', () => {
        const formGroup = service.createProduceFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            category: expect.any(Object),
          }),
        );
      });
    });

    describe('getProduce', () => {
      it('should return NewProduce for default Produce initial value', () => {
        const formGroup = service.createProduceFormGroup(sampleWithNewData);

        const produce = service.getProduce(formGroup) as any;

        expect(produce).toMatchObject(sampleWithNewData);
      });

      it('should return NewProduce for empty Produce initial value', () => {
        const formGroup = service.createProduceFormGroup();

        const produce = service.getProduce(formGroup) as any;

        expect(produce).toMatchObject({});
      });

      it('should return IProduce', () => {
        const formGroup = service.createProduceFormGroup(sampleWithRequiredData);

        const produce = service.getProduce(formGroup) as any;

        expect(produce).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProduce should not enable id FormControl', () => {
        const formGroup = service.createProduceFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProduce should disable id FormControl', () => {
        const formGroup = service.createProduceFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
