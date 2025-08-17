import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import FarmerProduceResolve from './route/farmer-produce-routing-resolve.service';

const farmerProduceRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/farmer-produce.component').then(m => m.FarmerProduceComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/farmer-produce-detail.component').then(m => m.FarmerProduceDetailComponent),
    resolve: {
      farmerProduce: FarmerProduceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/farmer-produce-update.component').then(m => m.FarmerProduceUpdateComponent),
    resolve: {
      farmerProduce: FarmerProduceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/farmer-produce-update.component').then(m => m.FarmerProduceUpdateComponent),
    resolve: {
      farmerProduce: FarmerProduceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default farmerProduceRoute;
