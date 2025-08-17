import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { Unit } from 'app/entities/enumerations/unit.model';
import { QualityGrade } from 'app/entities/enumerations/quality-grade.model';

export interface IFarmerProduce {
  id: number;
  quantity?: number | null;
  unit?: keyof typeof Unit | null;
  pricePerUnit?: number | null;
  availableFrom?: dayjs.Dayjs | null;
  availableUntil?: dayjs.Dayjs | null;
  grade?: keyof typeof QualityGrade | null;
  farmer?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewFarmerProduce = Omit<IFarmerProduce, 'id'> & { id: null };
