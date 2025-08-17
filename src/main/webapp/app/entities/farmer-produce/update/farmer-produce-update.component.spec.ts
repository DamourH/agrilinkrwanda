import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { FarmerProduceService } from '../service/farmer-produce.service';
import { IFarmerProduce } from '../farmer-produce.model';
import { FarmerProduceFormService } from './farmer-produce-form.service';

import { FarmerProduceUpdateComponent } from './farmer-produce-update.component';

describe('FarmerProduce Management Update Component', () => {
  let comp: FarmerProduceUpdateComponent;
  let fixture: ComponentFixture<FarmerProduceUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let farmerProduceFormService: FarmerProduceFormService;
  let farmerProduceService: FarmerProduceService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [FarmerProduceUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(FarmerProduceUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FarmerProduceUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    farmerProduceFormService = TestBed.inject(FarmerProduceFormService);
    farmerProduceService = TestBed.inject(FarmerProduceService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call User query and add missing value', () => {
      const farmerProduce: IFarmerProduce = { id: 1333 };
      const farmer: IUser = { id: 3944 };
      farmerProduce.farmer = farmer;

      const userCollection: IUser[] = [{ id: 3944 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [farmer];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ farmerProduce });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const farmerProduce: IFarmerProduce = { id: 1333 };
      const farmer: IUser = { id: 3944 };
      farmerProduce.farmer = farmer;

      activatedRoute.data = of({ farmerProduce });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContainEqual(farmer);
      expect(comp.farmerProduce).toEqual(farmerProduce);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFarmerProduce>>();
      const farmerProduce = { id: 14315 };
      jest.spyOn(farmerProduceFormService, 'getFarmerProduce').mockReturnValue(farmerProduce);
      jest.spyOn(farmerProduceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ farmerProduce });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: farmerProduce }));
      saveSubject.complete();

      // THEN
      expect(farmerProduceFormService.getFarmerProduce).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(farmerProduceService.update).toHaveBeenCalledWith(expect.objectContaining(farmerProduce));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFarmerProduce>>();
      const farmerProduce = { id: 14315 };
      jest.spyOn(farmerProduceFormService, 'getFarmerProduce').mockReturnValue({ id: null });
      jest.spyOn(farmerProduceService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ farmerProduce: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: farmerProduce }));
      saveSubject.complete();

      // THEN
      expect(farmerProduceFormService.getFarmerProduce).toHaveBeenCalled();
      expect(farmerProduceService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFarmerProduce>>();
      const farmerProduce = { id: 14315 };
      jest.spyOn(farmerProduceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ farmerProduce });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(farmerProduceService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('should forward to userService', () => {
        const entity = { id: 3944 };
        const entity2 = { id: 6275 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
