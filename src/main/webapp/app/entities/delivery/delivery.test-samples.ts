import dayjs from 'dayjs/esm';

import { IDelivery, NewDelivery } from './delivery.model';

export const sampleWithRequiredData: IDelivery = {
  id: 30085,
};

export const sampleWithPartialData: IDelivery = {
  id: 14818,
  pickupDate: dayjs('2025-08-17T14:59'),
};

export const sampleWithFullData: IDelivery = {
  id: 6503,
  pickupDate: dayjs('2025-08-17T14:22'),
  deliveryDate: dayjs('2025-08-17T00:06'),
  status: 'PENDING',
};

export const sampleWithNewData: NewDelivery = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
