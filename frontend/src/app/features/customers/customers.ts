import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Customer } from '../../shared/models/customer.model';
import { ApiService } from '../../shared/services/api';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-customers',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './customers.html',
  styleUrls: ['./customers.css'],
})
export class Customers {

  customers = signal<Customer[]>([]);
  loading = signal<boolean>(false);
  editingCustomer = signal<Customer | null>(null);

  formCustomer = signal<Customer>({
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    address: '',
  });

  constructor(private api: ApiService) {
    this.loadCustomers();
  }

  async loadCustomers() {
    this.loading.set(true);
    this.api.getCustomers().subscribe({
      next: (data) => this.customers.set(data),
      error: () => {
        Swal.fire('Error', 'No se pudieron cargar los clientes.', 'error');
      },
      complete: () => this.loading.set(false),
    });
  }

  updateFormField<K extends keyof Customer>(field: K, value: Customer[K]) {
    this.formCustomer.set({
      ...this.formCustomer(),
      [field]: value,
    });
  }

  async saveCustomer() {
    const customer = this.formCustomer();
    const isEditing = this.editingCustomer() != null;

    this.loading.set(true);

    if (isEditing) {
      this.api.updateCustomer(this.editingCustomer()!.id!, customer).subscribe({
        next: (updated) => {
          const list = [...this.customers()];
          const idx = list.findIndex(c => c.id === updated.id);
          if (idx >= 0) list[idx] = updated;
          this.customers.set(list);

          Swal.fire({
            title: 'Actualizado',
            text: 'El cliente ha sido actualizado correctamente.',
            icon: 'success',
            timer: 1300,
            showConfirmButton: false,
          });

          this.cancelEdit();
        },
        error: () => {
          Swal.fire('Error', 'No se pudo actualizar el cliente.', 'error');
        },
        complete: () => this.loading.set(false),
      });
    } else {
      this.api.createCustomer(customer).subscribe({
        next: (created) => {
          this.customers.set([...this.customers(), created]);

          Swal.fire({
            title: 'Creado',
            text: 'El cliente se ha registrado correctamente.',
            icon: 'success',
            timer: 1300,
            showConfirmButton: false,
          });

          this.resetForm();
        },
        error: () => {
          Swal.fire('Error', 'No se pudo crear el cliente.', 'error');
        },
        complete: () => this.loading.set(false),
      });
    }
  }

  editCustomer(customer: Customer) {
    this.editingCustomer.set(customer);
    this.formCustomer.set({ ...customer });
  }

  async deleteCustomer(id: string) {
    const result = await Swal.fire({
      title: '¿Eliminar cliente?',
      text: 'Esta acción no se puede deshacer.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar',
      confirmButtonColor: '#d33',
    });

    if (!result.isConfirmed) return;

    this.api.deleteCustomer(id).subscribe({
      next: () => {
        this.customers.set(this.customers().filter(c => c.id !== id));

        Swal.fire({
          title: 'Eliminado',
          text: 'El cliente ha sido eliminado.',
          icon: 'success',
          timer: 1200,
          showConfirmButton: false,
        });
      },
      error: () => Swal.fire('Error', 'No se pudo eliminar.', 'error'),
    });
  }

  cancelEdit() {
    this.editingCustomer.set(null);
    this.resetForm();
  }

  resetForm() {
    this.formCustomer.set({
      firstName: '',
      lastName: '',
      email: '',
      phone: '',
      address: '',
    });
  }
}
