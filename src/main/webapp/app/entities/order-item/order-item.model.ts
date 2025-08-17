import { IPurchaseOrder } from 'app/entities/purchase-order/purchase-order.model';
import { IFarmerProduce } from 'app/entities/farmer-produce/farmer-produce.model';

export interface IOrderItem {
  id: number;
  quantity?: number | null;
  priceAtOrder?: number | null;
  order?: Pick<IPurchaseOrder, 'id'> | null;
  farmerProduce?: Pick<IFarmerProduce, 'id'> | null;
}

export type NewOrderItem = Omit<IOrderItem, 'id'> & { id: null };
