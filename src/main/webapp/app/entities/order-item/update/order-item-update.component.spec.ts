import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IPurchaseOrder } from 'app/entities/purchase-order/purchase-order.model';
import { PurchaseOrderService } from 'app/entities/purchase-order/service/purchase-order.service';
import { IFarmerProduce } from 'app/entities/farmer-produce/farmer-produce.model';
import { FarmerProduceService } from 'app/entities/farmer-produce/service/farmer-produce.service';
import { IOrderItem } from '../order-item.model';
import { OrderItemService } from '../service/order-item.service';
import { OrderItemFormService } from './order-item-form.service';

import { OrderItemUpdateComponent } from './order-item-update.component';

describe('OrderItem Management Update Component', () => {
  let comp: OrderItemUpdateComponent;
  let fixture: ComponentFixture<OrderItemUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let orderItemFormService: OrderItemFormService;
  let orderItemService: OrderItemService;
  let purchaseOrderService: PurchaseOrderService;
  let farmerProduceService: FarmerProduceService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [OrderItemUpdateComponent],
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
      .overrideTemplate(OrderItemUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OrderItemUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    orderItemFormService = TestBed.inject(OrderItemFormService);
    orderItemService = TestBed.inject(OrderItemService);
    purchaseOrderService = TestBed.inject(PurchaseOrderService);
    farmerProduceService = TestBed.inject(FarmerProduceService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call PurchaseOrder query and add missing value', () => {
      const orderItem: IOrderItem = { id: 123 };
      const order: IPurchaseOrder = { id: 29828 };
      orderItem.order = order;

      const purchaseOrderCollection: IPurchaseOrder[] = [{ id: 29828 }];
      jest.spyOn(purchaseOrderService, 'query').mockReturnValue(of(new HttpResponse({ body: purchaseOrderCollection })));
      const additionalPurchaseOrders = [order];
      const expectedCollection: IPurchaseOrder[] = [...additionalPurchaseOrders, ...purchaseOrderCollection];
      jest.spyOn(purchaseOrderService, 'addPurchaseOrderToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ orderItem });
      comp.ngOnInit();

      expect(purchaseOrderService.query).toHaveBeenCalled();
      expect(purchaseOrderService.addPurchaseOrderToCollectionIfMissing).toHaveBeenCalledWith(
        purchaseOrderCollection,
        ...additionalPurchaseOrders.map(expect.objectContaining),
      );
      expect(comp.purchaseOrdersSharedCollection).toEqual(expectedCollection);
    });

    it('should call FarmerProduce query and add missing value', () => {
      const orderItem: IOrderItem = { id: 123 };
      const farmerProduce: IFarmerProduce = { id: 14315 };
      orderItem.farmerProduce = farmerProduce;

      const farmerProduceCollection: IFarmerProduce[] = [{ id: 14315 }];
      jest.spyOn(farmerProduceService, 'query').mockReturnValue(of(new HttpResponse({ body: farmerProduceCollection })));
      const additionalFarmerProduces = [farmerProduce];
      const expectedCollection: IFarmerProduce[] = [...additionalFarmerProduces, ...farmerProduceCollection];
      jest.spyOn(farmerProduceService, 'addFarmerProduceToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ orderItem });
      comp.ngOnInit();

      expect(farmerProduceService.query).toHaveBeenCalled();
      expect(farmerProduceService.addFarmerProduceToCollectionIfMissing).toHaveBeenCalledWith(
        farmerProduceCollection,
        ...additionalFarmerProduces.map(expect.objectContaining),
      );
      expect(comp.farmerProducesSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const orderItem: IOrderItem = { id: 123 };
      const order: IPurchaseOrder = { id: 29828 };
      orderItem.order = order;
      const farmerProduce: IFarmerProduce = { id: 14315 };
      orderItem.farmerProduce = farmerProduce;

      activatedRoute.data = of({ orderItem });
      comp.ngOnInit();

      expect(comp.purchaseOrdersSharedCollection).toContainEqual(order);
      expect(comp.farmerProducesSharedCollection).toContainEqual(farmerProduce);
      expect(comp.orderItem).toEqual(orderItem);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrderItem>>();
      const orderItem = { id: 25971 };
      jest.spyOn(orderItemFormService, 'getOrderItem').mockReturnValue(orderItem);
      jest.spyOn(orderItemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orderItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: orderItem }));
      saveSubject.complete();

      // THEN
      expect(orderItemFormService.getOrderItem).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(orderItemService.update).toHaveBeenCalledWith(expect.objectContaining(orderItem));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrderItem>>();
      const orderItem = { id: 25971 };
      jest.spyOn(orderItemFormService, 'getOrderItem').mockReturnValue({ id: null });
      jest.spyOn(orderItemService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orderItem: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: orderItem }));
      saveSubject.complete();

      // THEN
      expect(orderItemFormService.getOrderItem).toHaveBeenCalled();
      expect(orderItemService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrderItem>>();
      const orderItem = { id: 25971 };
      jest.spyOn(orderItemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orderItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(orderItemService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('comparePurchaseOrder', () => {
      it('should forward to purchaseOrderService', () => {
        const entity = { id: 29828 };
        const entity2 = { id: 21921 };
        jest.spyOn(purchaseOrderService, 'comparePurchaseOrder');
        comp.comparePurchaseOrder(entity, entity2);
        expect(purchaseOrderService.comparePurchaseOrder).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareFarmerProduce', () => {
      it('should forward to farmerProduceService', () => {
        const entity = { id: 14315 };
        const entity2 = { id: 1333 };
        jest.spyOn(farmerProduceService, 'compareFarmerProduce');
        comp.compareFarmerProduce(entity, entity2);
        expect(farmerProduceService.compareFarmerProduce).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
