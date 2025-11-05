import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  currentUser: any = null;
  isWalker = false;
  showEditModal = false;
  editUsername = '';
  isUpdating = false;

  constructor(
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // Verificar si el usuario está logueado
    this.currentUser = this.authService.getCurrentUser();
    
    if (!this.currentUser) {
      // Si no hay usuario logueado, redirigir al login
      this.router.navigate(['/login']);
      return;
    }

    // Obtener datos actualizados del backend para asegurar que el rol esté sincronizado
    this.authService.refreshCurrentUser().subscribe({
      next: (user) => {
        if (user) {
          this.currentUser = user;
          this.isWalker = this.authService.isWalker();
        }
      },
      error: (error) => {
        console.error('Error al obtener usuario actual:', error);
        // Si falla, usar el usuario del localStorage
        this.isWalker = this.authService.isWalker();
      }
    });
  }

  // Volver a la página anterior
  goBack(): void {
    this.router.navigate(['/home']);
  }

  // Abrir modal de edición
  editProfile(): void {
    this.editUsername = this.currentUser?.username || '';
    this.showEditModal = true;
  }

  // Cerrar modal de edición
  closeEditModal(): void {
    this.showEditModal = false;
    this.editUsername = '';
  }

  // Guardar cambios del perfil
  saveProfile(): void {
    if (!this.editUsername.trim()) {
      alert('El nombre de usuario no puede estar vacío');
      return;
    }

    if (this.editUsername.trim().length < 3 || this.editUsername.trim().length > 20) {
      alert('El nombre de usuario debe tener entre 3 y 20 caracteres');
      return;
    }

    if (this.editUsername.trim() === this.currentUser?.username) {
      this.closeEditModal();
      return;
    }

    if (this.isUpdating) {
      return;
    }

    this.isUpdating = true;

    this.authService.updateProfile(this.editUsername.trim()).subscribe({
      next: (response) => {
        this.isUpdating = false;
        if (response.success && response.user) {
          this.currentUser = response.user;
          this.closeEditModal();
          alert('Nombre de usuario actualizado exitosamente');
        } else {
          alert('Error: ' + response.message);
        }
      },
      error: (error) => {
        this.isUpdating = false;
        console.error('Error al actualizar perfil:', error);
        const errorMessage = error.error?.message || error.message || 'Error al actualizar el perfil';
        alert('Error: ' + errorMessage);
      }
    });
  }

  // Cerrar sesión
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  // Ir al formulario para ser paseador
  goToWalkerApplication(): void {
    this.router.navigate(['/walker-application']);
  }

  // Ir al historial de paseos
  goToHistory(): void {
    this.router.navigate(['/history']);
  }
}

