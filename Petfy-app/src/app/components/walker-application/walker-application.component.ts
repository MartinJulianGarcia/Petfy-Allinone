import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-walker-application',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './walker-application.component.html',
  styleUrls: ['./walker-application.component.css']
})
export class WalkerApplicationComponent implements OnInit {
  applicationForm = {
    documentImage: null as File | null,
    phone: '',
    description: ''
  };

  selectedFileName = '';
  showValidationModal = false;
  validationCode = '';
  isSubmitting = false;

  constructor(private router: Router, private authService: AuthService) {}

  ngOnInit(): void {}

  goBack(): void {
    this.router.navigate(['/profile']);
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.applicationForm.documentImage = file;
      this.selectedFileName = file.name;
    }
  }

  submitApplication(): void {
    // Validar que todos los campos estén completos
    if (!this.applicationForm.documentImage) {
      alert('Por favor sube una imagen de tu documento');
      return;
    }

    if (!this.applicationForm.phone.trim()) {
      alert('Por favor ingresa tu número de teléfono');
      return;
    }

    if (!this.applicationForm.description.trim()) {
      alert('Por favor ingresa una descripción sobre ti');
      return;
    }

    // Mostrar modal de código de validación
    this.showValidationModal = true;
    this.validationCode = '';
  }

  closeValidationModal(): void {
    this.showValidationModal = false;
    this.validationCode = '';
  }

  submitWithValidationCode(): void {
    // Si no hay código, no enviar al backend
    if (!this.validationCode.trim()) {
      this.closeValidationModal();
      alert('Tu solicitud quedó en revisión y será evaluada por nuestro equipo.');
      this.router.navigate(['/profile']);
      return;
    }

    if (this.isSubmitting) {
      return;
    }

    this.isSubmitting = true;

    // Solo llamar al backend si hay código de validación
    this.authService.solicitarSerPaseador(
      this.applicationForm.phone,
      this.applicationForm.description,
      this.applicationForm.documentImage!,
      this.validationCode.trim()
    ).subscribe({
      next: (response) => {
        this.isSubmitting = false;
        this.showValidationModal = false;
        
        console.log('Respuesta completa:', response);
        
        if (response.success) {
          const mensaje = (response.message || '').toLowerCase();
          console.log('Mensaje recibido:', mensaje);
          console.log('¿Contiene "aprobada"?', mensaje.includes('aprobada'));
          console.log('¿Contiene "aprobado"?', mensaje.includes('aprobado'));
          
          // Verificar si fue aprobado (buscar "aprobada" o "aprobado" en el mensaje)
          if (mensaje.includes('aprobada') || mensaje.includes('aprobado')) {
            alert('¡Solicitud aprobada con éxito! Rol cambiado a paseador. Ahora puedes ver las solicitudes de los clientes.');
            // Redirigir a la página de paseador
            this.router.navigate(['/walker-requests']);
          } else {
            // Si el código es incorrecto, mostrar mensaje de error
            alert('Código de validación incorrecto. Tu solicitud quedó en revisión y será evaluada por nuestro equipo.');
            // Redirigir al perfil
            this.router.navigate(['/profile']);
          }
        } else {
          alert('Error: ' + response.message);
        }
      },
      error: (error) => {
        this.isSubmitting = false;
        console.error('Error al enviar solicitud:', error);
        alert('Error al enviar la solicitud. Por favor intenta de nuevo.');
      }
    });
  }

  cancelValidation(): void {
    this.closeValidationModal();
    // Si cancela, no enviar al backend, solo mostrar mensaje
    alert('Tu solicitud quedó en revisión y será evaluada por nuestro equipo.');
    this.router.navigate(['/profile']);
  }
}
