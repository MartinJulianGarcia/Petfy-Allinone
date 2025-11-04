import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  currentUser: any = null;
  isWalker = false;

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
    }

    // Verificar si es paseador
    this.isWalker = this.authService.isWalker();
  }

  // Volver a la página anterior
  goBack(): void {
    this.router.navigate(['/home']);
  }

  // Editar perfil (función placeholder)
  editProfile(): void {
    alert('Función "Editar Perfil" - Próximamente disponible');
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

