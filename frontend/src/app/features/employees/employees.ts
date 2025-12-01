import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import Swal from 'sweetalert2';
import { ApiService } from '../../shared/services/api';
import { Employee } from '../../shared/models/employee.model';

@Component({
  selector: 'app-employees',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './employees.html',
  styleUrl: './employees.css',
})
export class Employees {

  employees = signal<Employee[]>([]);
  loading = signal<boolean>(false);
  editingEmployee = signal<Employee | null>(null);

  formEmployee = signal<Employee>({
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    position: '',
  });

  constructor(private api: ApiService) {
    this.loadEmployees();
  }

  loadEmployees() {
    this.loading.set(true);

    this.api.getEmployees().subscribe({
      next: data => this.employees.set(data),
      error: () => Swal.fire('Error', 'No se pudieron cargar los empleados.', 'error'),
      complete: () => this.loading.set(false)
    });
  }

  updateFormField<K extends keyof Employee>(field: K, value: Employee[K]) {
    this.formEmployee.set({
      ...this.formEmployee(),
      [field]: value,
    });
  }

  saveEmployee() {
    const employee = this.formEmployee();
    const isEditing = this.editingEmployee() != null;

    this.loading.set(true);

    if (isEditing) {

      this.api.updateEmployee(this.editingEmployee()!.id!, employee).subscribe({
        next: updated => {
          const list = [...this.employees()];
          const idx = list.findIndex(e => e.id === updated.id);
          if (idx >= 0) list[idx] = updated;
          this.employees.set(list);

          Swal.fire({
            title: 'Actualizado',
            text: 'El empleado ha sido actualizado correctamente.',
            icon: 'success',
            timer: 1300,
            showConfirmButton: false,
          });

          this.cancelEdit();
        },
        error: () => Swal.fire('Error', 'No se pudo actualizar el empleado.', 'error'),
        complete: () => this.loading.set(false)
      });

    } else {

      this.api.createEmployee(employee).subscribe({
        next: created => {
          this.employees.set([...this.employees(), created]);

          Swal.fire({
            title: 'Creado',
            text: 'El empleado ha sido registrado correctamente.',
            icon: 'success',
            timer: 1300,
            showConfirmButton: false,
          });

          this.resetForm();
        },
        error: () => Swal.fire('Error', 'No se pudo crear el empleado.', 'error'),
        complete: () => this.loading.set(false)
      });

    }
  }

  editEmployee(employee: Employee) {
    this.editingEmployee.set(employee);
    this.formEmployee.set({ ...employee });
  }

  async deleteEmployee(id: string) {
    const result = await Swal.fire({
      title: '¿Eliminar empleado?',
      text: 'Esta acción no se puede deshacer.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar',
      confirmButtonColor: '#d33',
    });

    if (!result.isConfirmed) return;

    this.api.deleteEmployee(id).subscribe({
      next: () => {
        this.employees.set(this.employees().filter(e => e.id !== id));

        Swal.fire({
          title: 'Eliminado',
          text: 'El empleado ha sido eliminado.',
          icon: 'success',
          timer: 1200,
          showConfirmButton: false,
        });
      },
      error: () => Swal.fire('Error', 'No se pudo eliminar el empleado.', 'error'),
    });
  }

  cancelEdit() {
    this.editingEmployee.set(null);
    this.resetForm();
  }

  resetForm() {
    this.formEmployee.set({
      firstName: '',
      lastName: '',
      email: '',
      phone: '',
      position: '',
    });
  }
}
