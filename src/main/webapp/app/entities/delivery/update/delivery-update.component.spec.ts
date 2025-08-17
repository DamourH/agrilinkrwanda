import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IPurchaseOrder } from 'app/entities/purchase-order/purchase-order.model';
import { PurchaseOrderService } from 'app/entities/purchase-order/service/purchase-order.service';
import { IDelivery } from '../delivery.model';
import { DeliveryService } from '../service/delivery.service';
import { DeliveryFormService } from './delivery-form.service';

import { DeliveryUpdateComponent } from './delivery-update.component';

describe('Delivery Management Update Component', () => {
  let comp: DeliveryUpdateComponent;
  let fixture: ComponentFixture<DeliveryUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let deliveryFormService: DeliveryFormService;
  let deliveryService: DeliveryService;
  let userService: UserService;
  let purchaseOrderService: PurchaseOrderService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [DeliveryUpdateComponent],
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
      .overrideTemplate(DeliveryUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DeliveryUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    deliveryFormService = TestBed.inject(DeliveryFormService);
    deliveryService = TestBed.inject(DeliveryService);
    userService = TestBed.inject(UserService);
    purchaseOrderService = TestBed.inject(PurchaseOrderService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call User query and add missing value', () => {
      const delivery: IDelivery = { id: 9797 };
      const driver: IUser = { id: 3944 };
      delivery.driver = driver;

      const userCollection: IUser[] = [{ id: 3944 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [driver];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ delivery });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('should call PurchaseOrder query and add missing value', () => {
      const delivery: IDelivery = { id: 9797 };
      const order: IPurchaseOrder = { id: 29828 };
      delivery.order = order;

      const purchaseOrderCollection: IPurchaseOrder[] = [{ id: 29828 }];
      jest.spyOn(purchaseOrderService, 'query').mockReturnValue(of(new HttpResponse({ body: purchaseOrderCollection })));
      const additionalPurchaseOrders = [order];
      const expectedCollection: IPurchaseOrder[] = [...additionalPurchaseOrders, ...purchaseOrderCollection];
      jest.spyOn(purchaseOrderService, 'addPurchaseOrderToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ delivery });
      comp.ngOnInit();

      expect(purchaseOrderService.query).toHaveBeenCalled();
      expect(purchaseOrderService.addPurchaseOrderToCollectionIfMissing).toHaveBeenCalledWith(
        purchaseOrderCollection,
        ...additionalPurchaseOrders.map(expect.objectContaining),
      );
      expect(comp.purchaseOrdersSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const delivery: IDelivery = { id: 9797 };
      const driver: IUser = { id: 3944 };
      delivery.driver = driver;
      const order: IPurchaseOrder = { id: 29828 };
      delivery.order = order;

      activatedRoute.data = of({ delivery });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContainEqual(driver);
      expect(comp.purchaseOrdersSharedCollection).toContainEqual(order);
      expect(comp.delivery).toEqual(delivery);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDelivery>>();
      const delivery = { id: 16325 };
      jest.spyOn(deliveryFormService, 'getDelivery').mockReturnValue(delivery);
      jest.spyOn(deliveryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ delivery });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: delivery }));
      saveSubject.complete();

      // THEN
      expect(deliveryFormService.getDelivery).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(deliveryService.update).toHaveBeenCalledWith(expect.objectContaining(delivery));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDelivery>>();
      const delivery = { id: 16325 };
      jest.spyOn(deliveryFormService, 'getDelivery').mockReturnValue({ id: null });
      jest.spyOn(deliveryService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ delivery: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: delivery }));
      saveSubject.complete();

      // THEN
      expect(deliveryFormService.getDelivery).toHaveBeenCalled();
      expect(deliveryService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDelivery>>();
      const delivery = { id: 16325 };
      jest.spyOn(deliveryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ delivery });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(deliveryService.update).toHaveBeenCalled();
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

    describe('comparePurchaseOrder', () => {
      it('should forward to purchaseOrderService', () => {
        const entity = { id: 29828 };
        const entity2 = { id: 21921 };
        jest.spyOn(purchaseOrderService, 'comparePurchaseOrder');
        comp.comparePurchaseOrder(entity, entity2);
        expect(purchaseOrderService.comparePurchaseOrder).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
