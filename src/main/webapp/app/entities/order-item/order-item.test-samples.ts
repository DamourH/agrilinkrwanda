import { IOrderItem, NewOrderItem } from './order-item.model';

export const sampleWithRequiredData: IOrderItem = {
  id: 16549,
  quantity: 23987.13,
  priceAtOrder: 22506.51,
};

export const sampleWithPartialData: IOrderItem = {
  id: 28487,
  quantity: 27298.36,
  priceAtOrder: 22462.83,
};

export const sampleWithFullData: IOrderItem = {
  id: 6728,
  quantity: 17448.88,
  priceAtOrder: 20263.67,
};

export const sampleWithNewData: NewOrderItem = {
  quantity: 12164.23,
  priceAtOrder: 1027.11,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
