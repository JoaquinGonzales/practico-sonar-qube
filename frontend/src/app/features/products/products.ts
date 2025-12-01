import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Product } from '../../shared/models/product.model';
import { ApiService } from '../../shared/services/api';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './products.html',
  styleUrl: './products.css',
})
export class Products {

  products = signal<Product[]>([]);
  loading = signal<boolean>(false);
  editingProduct = signal<Product | null>(null);

  formProduct = signal<Product>({
    name: '',
    description: '',
    price: 0,
    stock: 0,
    category: '',
  });

  constructor(private api: ApiService) {
    this.loadProducts();
  }

  loadProducts() {
    this.loading.set(true);
    this.api.getProducts().subscribe({
      next: (data) => this.products.set(data),
      error: () => Swal.fire('Error', 'No se pudieron cargar los productos.', 'error'),
      complete: () => this.loading.set(false),
    });
  }

  updateFormField<K extends keyof Product>(field: K, value: Product[K]) {
    this.formProduct.set({
      ...this.formProduct(),
      [field]: value,
    });
  }

  saveProduct() {
    const product = this.formProduct();
    const isEditing = this.editingProduct() != null;
    this.loading.set(true);

    if (isEditing) {
      this.api.updateProduct(this.editingProduct()!.id!, product).subscribe({
        next: (updated) => {
          const list = [...this.products()];
          const index = list.findIndex(p => p.id === updated.id);
          if (index >= 0) list[index] = updated;
          this.products.set(list);

          Swal.fire({
            title: 'Actualizado',
            text: 'El producto ha sido actualizado.',
            icon: 'success',
            timer: 1300,
            showConfirmButton: false,
          });

          this.cancelEdit();
        },
        error: () => Swal.fire('Error', 'No se pudo actualizar el producto.', 'error'),
        complete: () => this.loading.set(false),
      });
    } else {
      this.api.createProduct(product).subscribe({
        next: (created) => {
          this.products.set([...this.products(), created]);

          Swal.fire({
            title: 'Creado',
            text: 'El producto ha sido registrado.',
            icon: 'success',
            timer: 1300,
            showConfirmButton: false,
          });

          this.resetForm();
        },
        error: () => Swal.fire('Error', 'No se pudo crear el producto.', 'error'),
        complete: () => this.loading.set(false),
      });
    }
  }

  editProduct(product: Product) {
    this.editingProduct.set(product);
    this.formProduct.set({ ...product });
  }

  async deleteProduct(id: string) {
    const result = await Swal.fire({
      title: '¿Eliminar producto?',
      text: 'Esta acción es irreversible.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar',
      confirmButtonColor: '#d33',
    });

    if (!result.isConfirmed) return;

    this.api.deleteProduct(id).subscribe({
      next: () => {
        this.products.set(this.products().filter(p => p.id !== id));

        Swal.fire({
          title: 'Eliminado',
          text: 'El producto ha sido eliminado.',
          icon: 'success',
          timer: 1200,
          showConfirmButton: false,
        });
      },
      error: () => Swal.fire('Error', 'No se pudo eliminar el producto.', 'error'),
    });
  }

  cancelEdit() {
    this.editingProduct.set(null);
    this.resetForm();
  }

  resetForm() {
    this.formProduct.set({
      name: '',
      description: '',
      price: 0,
      stock: 0,
      category: '',
    });
  }
}
