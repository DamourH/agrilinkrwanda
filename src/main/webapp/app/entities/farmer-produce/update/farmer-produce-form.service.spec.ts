import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../farmer-produce.test-samples';

import { FarmerProduceFormService } from './farmer-produce-form.service';

describe('FarmerProduce Form Service', () => {
  let service: FarmerProduceFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FarmerProduceFormService);
  });

  describe('Service methods', () => {
    describe('createFarmerProduceFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createFarmerProduceFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quantity: expect.any(Object),
            unit: expect.any(Object),
            pricePerUnit: expect.any(Object),
            availableFrom: expect.any(Object),
            availableUntil: expect.any(Object),
            grade: expect.any(Object),
            farmer: expect.any(Object),
          }),
        );
      });

      it('passing IFarmerProduce should create a new form with FormGroup', () => {
        const formGroup = service.createFarmerProduceFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quantity: expect.any(Object),
            unit: expect.any(Object),
            pricePerUnit: expect.any(Object),
            availableFrom: expect.any(Object),
            availableUntil: expect.any(Object),
            grade: expect.any(Object),
            farmer: expect.any(Object),
          }),
        );
      });
    });

    describe('getFarmerProduce', () => {
      it('should return NewFarmerProduce for default FarmerProduce initial value', () => {
        const formGroup = service.createFarmerProduceFormGroup(sampleWithNewData);

        const farmerProduce = service.getFarmerProduce(formGroup) as any;

        expect(farmerProduce).toMatchObject(sampleWithNewData);
      });

      it('should return NewFarmerProduce for empty FarmerProduce initial value', () => {
        const formGroup = service.createFarmerProduceFormGroup();

        const farmerProduce = service.getFarmerProduce(formGroup) as any;

        expect(farmerProduce).toMatchObject({});
      });

      it('should return IFarmerProduce', () => {
        const formGroup = service.createFarmerProduceFormGroup(sampleWithRequiredData);

        const farmerProduce = service.getFarmerProduce(formGroup) as any;

        expect(farmerProduce).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IFarmerProduce should not enable id FormControl', () => {
        const formGroup = service.createFarmerProduceFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewFarmerProduce should disable id FormControl', () => {
        const formGroup = service.createFarmerProduceFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
