import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-contact',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './contact.component.html',
  styleUrls: ['./contact.component.css']
})
export class ContactComponent {
  contactForm = {
    nombre: '',
    email: '',
    telefono: '',
    consulta: ''
  };

  errors = {
    nombre: '',
    email: '',
    telefono: '',
    consulta: ''
  };

  constructor(private router: Router) {}

  goBack() {
    this.router.navigate(['/login']);
  }

  onSubmit() {
    if (this.validateForm()) {
      // Aquí se enviaría el formulario
      console.log('Formulario enviado:', this.contactForm);
      alert('¡Consulta enviada exitosamente!');
      this.resetForm();
    }
  }

  validateForm(): boolean {
    this.errors = { nombre: '', email: '', telefono: '', consulta: '' };
    let isValid = true;

    if (!this.contactForm.nombre.trim()) {
      this.errors.nombre = 'El nombre es requerido';
      isValid = false;
    }

    if (!this.contactForm.email.trim()) {
      this.errors.email = 'El email es requerido';
      isValid = false;
    } else if (!this.isValidEmail(this.contactForm.email)) {
      this.errors.email = 'Ingresa un email válido';
      isValid = false;
    }

    if (this.contactForm.telefono && !this.isValidPhone(this.contactForm.telefono)) {
      this.errors.telefono = 'Ingresa un teléfono válido';
      isValid = false;
    }

    if (!this.contactForm.consulta.trim()) {
      this.errors.consulta = 'La consulta es requerida';
      isValid = false;
    }

    return isValid;
  }

  isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  isValidPhone(phone: string): boolean {
    const phoneRegex = /^[\+]?[0-9\s\-\(\)]{10,}$/;
    return phoneRegex.test(phone);
  }

  resetForm() {
    this.contactForm = {
      nombre: '',
      email: '',
      telefono: '',
      consulta: ''
    };
    this.errors = {
      nombre: '',
      email: '',
      telefono: '',
      consulta: ''
    };
  }
}
