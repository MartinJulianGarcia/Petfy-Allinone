import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

interface User {
  username: string;
  email: string;
  role?: 'customer' | 'walker';
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
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

  // Ir al perfil del usuario
  goToProfile(): void {
    this.router.navigate(['/profile']);
  }

  // Ir al historial de paseos
  goToHistory(): void {
    this.router.navigate(['/history']);
  }

  // Solicitar paseo
  goToVeterinaries(): void {
    this.router.navigate(['/request']);
  }

  // Ver mis solicitudes (como cliente)
  createEmergencyRequest(): void {
    this.router.navigate(['/requests']);
  }

  // Ver peticiones como paseador
  goToWalkerRequests(): void {
    this.router.navigate(['/walker-requests']);
  }

  // Cerrar sesión
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}

