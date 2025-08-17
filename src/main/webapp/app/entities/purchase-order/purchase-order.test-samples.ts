import dayjs from 'dayjs/esm';

import { IPurchaseOrder, NewPurchaseOrder } from './purchase-order.model';

export const sampleWithRequiredData: IPurchaseOrder = {
  id: 2720,
  orderDate: dayjs('2025-08-16T18:29'),
  status: 'REJECTED',
  totalAmount: 4698.54,
  deliveryAddress: 'even only',
};

export const sampleWithPartialData: IPurchaseOrder = {
  id: 2550,
  orderDate: dayjs('2025-08-16T20:17'),
  status: 'CONFIRMED',
  totalAmount: 30248.08,
  deliveryAddress: 'pointless',
};

export const sampleWithFullData: IPurchaseOrder = {
  id: 24613,
  orderDate: dayjs('2025-08-17T00:05'),
  status: 'REJECTED',
  totalAmount: 5631.4,
  deliveryAddress: 'calculus now pension',
  notes: 'er bookcase phooey',
};

export const sampleWithNewData: NewPurchaseOrder = {
  orderDate: dayjs('2025-08-17T12:00'),
  status: 'DELIVERED',
  totalAmount: 25582.13,
  deliveryAddress: 'yippee thongs fatally',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
