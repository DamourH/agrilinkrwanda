import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { OrderStatus } from 'app/entities/enumerations/order-status.model';

export interface IPurchaseOrder {
  id: number;
  orderDate?: dayjs.Dayjs | null;
  status?: keyof typeof OrderStatus | null;
  totalAmount?: number | null;
  deliveryAddress?: string | null;
  notes?: string | null;
  buyer?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewPurchaseOrder = Omit<IPurchaseOrder, 'id'> & { id: null };
