import dayjs from 'dayjs/esm';

import { IFarmerProduce, NewFarmerProduce } from './farmer-produce.model';

export const sampleWithRequiredData: IFarmerProduce = {
  id: 17524,
  quantity: 30552.12,
  unit: 'GRAM',
  pricePerUnit: 21119.49,
  availableFrom: dayjs('2025-08-16T19:47'),
};

export const sampleWithPartialData: IFarmerProduce = {
  id: 9820,
  quantity: 14314.21,
  unit: 'GRAM',
  pricePerUnit: 23577.91,
  availableFrom: dayjs('2025-08-17T10:01'),
  availableUntil: dayjs('2025-08-17T04:07'),
  grade: 'A_GRADE',
};

export const sampleWithFullData: IFarmerProduce = {
  id: 575,
  quantity: 10964.12,
  unit: 'KG',
  pricePerUnit: 30888.24,
  availableFrom: dayjs('2025-08-17T12:28'),
  availableUntil: dayjs('2025-08-17T09:01'),
  grade: 'C_GRADE',
};

export const sampleWithNewData: NewFarmerProduce = {
  quantity: 7491.01,
  unit: 'GRAM',
  pricePerUnit: 5672,
  availableFrom: dayjs('2025-08-17T08:21'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
