import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

interface LoginUser {
  email: string;
  password: string;
}

interface LoginValidationErrors {
  email?: string;
  password?: string;
}

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  user: LoginUser = {
    email: '',
    password: ''
  };

  errors: LoginValidationErrors = {};
  isFormValid = false;

  constructor(private router: Router, private authService: AuthService) {}

  onSubmit(): void {
    this.authService.login(this.user).subscribe({
      next: (result) => {
        if (result.success) {
          alert(`¡Bienvenido ${result.user?.username}! Has iniciado sesión correctamente en Petfy.`);
          this.resetForm();
          // Redirigir a la página home después del login exitoso
          this.router.navigate(['/home']);
        } else {
          alert(result.message);
        }
      },
      error: (error) => {
        console.error('Error en login:', error);
        alert('Error al iniciar sesión. Por favor intenta nuevamente.');
      }
    });
  }

  // Validaciones en tiempo real
  validateEmail(): void {
    const email = this.user.email.trim();
    if (email.length === 0) {
      this.errors.email = 'El email es requerido';
    } else if (!this.isValidEmailFormat(email)) {
      this.errors.email = 'El email debe tener un formato válido';
    } else {
      this.errors.email = '';
    }
    this.checkFormValidity();
  }

  validatePassword(): void {
    const password = this.user.password;
    if (password.length === 0) {
      this.errors.password = 'La contraseña es requerida';
    } else {
      this.errors.password = '';
    }
    this.checkFormValidity();
  }

  private isValidEmailFormat(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  private checkFormValidity(): void {
    this.isFormValid = 
      !this.errors.email && 
      !this.errors.password &&
      this.user.email.trim().length > 0 &&
      this.user.password.length > 0;
  }

  private resetForm(): void {
    this.user = {
      email: '',
      password: ''
    };
    this.errors = {};
    this.isFormValid = false;
  }

  // Método para navegar al registro
  goToRegister(): void {
    this.router.navigate(['/register']).then(() => {
      console.log('Navegando al registro...');
    }).catch(err => {
      console.error('Error navegando al registro:', err);
    });
  }

  // Método para navegar a contacto
  goToContact(): void {
    this.router.navigate(['/contact']);
  }

  // Método para navegar a sobre nosotros
  goToAbout(): void {
    this.router.navigate(['/about']);
  }
}
