import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IPurchaseOrder } from 'app/entities/purchase-order/purchase-order.model';
import { OrderStatus } from 'app/entities/enumerations/order-status.model';

export interface IDelivery {
  id: number;
  pickupDate?: dayjs.Dayjs | null;
  deliveryDate?: dayjs.Dayjs | null;
  status?: keyof typeof OrderStatus | null;
  driver?: Pick<IUser, 'id' | 'login'> | null;
  order?: Pick<IPurchaseOrder, 'id'> | null;
}

export type NewDelivery = Omit<IDelivery, 'id'> & { id: null };
