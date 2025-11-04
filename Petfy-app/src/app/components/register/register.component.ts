import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

interface User {
  username: string;
  email: string;
  password: string;
  confirmPassword: string;
}

interface ValidationErrors {
  username?: string;
  email?: string;
  password?: string;
  confirmPassword?: string;
}

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  user: User = {
    username: '',
    email: '',
    password: '',
    confirmPassword: ''
  };

  errors: ValidationErrors = {};
  isFormValid = false;

  constructor(private router: Router, private authService: AuthService) {}

  onSubmit(): void {
    this.authService.register(this.user).subscribe({
      next: (result) => {
        if (result.success) {
          alert(`¡Registro exitoso! Bienvenido a Petfy, ${result.user?.username}.`);
          this.resetForm();
          // Redirigir al home después del registro exitoso
          this.router.navigate(['/home']);
        } else {
          alert(result.message);
        }
      },
      error: (error) => {
        console.error('Error en registro:', error);
        alert('Error al registrar usuario. Por favor intenta nuevamente.');
      }
    });
  }

  // Validaciones en tiempo real
  validateUsername(): void {
    const username = this.user.username.trim();
    if (username.length === 0) {
      this.errors.username = 'El nombre de usuario es requerido';
    } else if (username.length < 3) {
      this.errors.username = 'El nombre de usuario debe tener al menos 3 caracteres';
    } else if (username.length > 20) {
      this.errors.username = 'El nombre de usuario no puede tener más de 20 caracteres';
    } else {
      this.errors.username = '';
    }
    this.checkFormValidity();
  }

  validateEmail(): void {
    const email = this.user.email.trim();
    if (email.length === 0) {
      this.errors.email = 'El email es requerido';
    } else if (!this.isValidEmailFormat(email)) {
      this.errors.email = 'El email debe tener un formato válido con más de 2 caracteres en usuario y dominio';
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
    this.validateConfirmPassword();
    this.checkFormValidity();
  }

  validateConfirmPassword(): void {
    if (this.user.confirmPassword.length === 0) {
      this.errors.confirmPassword = 'Debes confirmar tu contraseña';
    } else if (this.user.password !== this.user.confirmPassword) {
      this.errors.confirmPassword = 'Las contraseñas no coinciden';
    } else {
      this.errors.confirmPassword = '';
    }
    this.checkFormValidity();
  }

  private isValidEmailFormat(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      return false;
    }
    
    const [username, domain] = email.split('@');
    const [domainName, extension] = domain.split('.');
    
    return username.length > 2 && domainName.length > 2 && extension.length > 0;
  }

  private checkFormValidity(): void {
    this.isFormValid = 
      !this.errors.username && 
      !this.errors.email && 
      !this.errors.password && 
      !this.errors.confirmPassword &&
      this.user.username.trim().length > 0 &&
      this.user.email.trim().length > 0 &&
      this.user.password.length > 0 &&
      this.user.confirmPassword.length > 0;
  }

  private resetForm(): void {
    this.user = {
      username: '',
      email: '',
      password: '',
      confirmPassword: ''
    };
    this.errors = {};
    this.isFormValid = false;
  }

  // Método para navegar al login
  goToLogin(): void {
    this.router.navigate(['/login']).then(() => {
      console.log('Navegando al login...');
    }).catch(err => {
      console.error('Error navegando al login:', err);
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