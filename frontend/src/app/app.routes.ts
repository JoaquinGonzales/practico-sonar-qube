import { Routes } from '@angular/router';

export const routes: Routes = [
    {
        path: 'products',
        loadComponent: () =>
            import('./features/products/products')
                .then(m => m.Products)
    },
    {
        path: 'employees',
        loadComponent: () =>
            import('./features/employees/employees')
                .then(m => m.Employees)
    },
    {
        path: 'customers',
        loadComponent: () =>
            import('./features/customers/customers')
                .then(m => m.Customers)
    },
    { path: '', redirectTo: 'products', pathMatch: 'full' }
];
