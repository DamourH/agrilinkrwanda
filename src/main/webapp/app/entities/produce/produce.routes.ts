import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import ProduceResolve from './route/produce-routing-resolve.service';

const produceRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/produce.component').then(m => m.ProduceComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/produce-detail.component').then(m => m.ProduceDetailComponent),
    resolve: {
      produce: ProduceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/produce-update.component').then(m => m.ProduceUpdateComponent),
    resolve: {
      produce: ProduceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/produce-update.component').then(m => m.ProduceUpdateComponent),
    resolve: {
      produce: ProduceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default produceRoute;
