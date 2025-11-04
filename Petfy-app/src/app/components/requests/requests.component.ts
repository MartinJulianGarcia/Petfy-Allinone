import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

interface WalkRequest {
  id: number;
  date: string;
  startTime: string;
  endTime: string;
  address: string;
  walker: string;
  status: 'pending' | 'confirmed';
}

@Component({
  selector: 'app-requests',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './requests.component.html',
  styleUrls: ['./requests.component.css']
})
export class RequestsComponent implements OnInit, OnDestroy {
  pendingRequests: WalkRequest[] = [];
  confirmedRequests: WalkRequest[] = [];
  selectedRequest: WalkRequest | null = null;
  private timers: Map<number, any> = new Map();

  constructor(private router: Router) {}

  ngOnInit() {
    this.loadRequests();
    this.startAutoConfirmationTimers();
  }

  loadRequests() {
    const allRequests = JSON.parse(localStorage.getItem('walkRequests') || '[]');
    this.pendingRequests = allRequests.filter((req: WalkRequest) => req.status === 'pending');
    this.confirmedRequests = allRequests.filter((req: WalkRequest) => req.status === 'confirmed');
    
    // Reiniciar timers para las nuevas solicitudes pendientes
    this.startAutoConfirmationTimers();
  }

  startAutoConfirmationTimers() {
    // Limpiar timers existentes
    this.clearAllTimers();
    
    // Crear timers para cada solicitud pendiente
    this.pendingRequests.forEach(request => {
      this.startTimerForRequest(request);
    });
  }

  startTimerForRequest(request: WalkRequest) {
    const timer = setTimeout(() => {
      this.autoConfirmRequest(request.id);
    }, 5000); // 5 segundos

    this.timers.set(request.id, timer);
  }

  autoConfirmRequest(requestId: number) {
    const allRequests = JSON.parse(localStorage.getItem('walkRequests') || '[]');
    const requestIndex = allRequests.findIndex((req: WalkRequest) => req.id === requestId);
    
    if (requestIndex !== -1 && allRequests[requestIndex].status === 'pending') {
      // Cambiar estado a confirmado
      allRequests[requestIndex].status = 'confirmed';
      localStorage.setItem('walkRequests', JSON.stringify(allRequests));
      
      // Actualizar la vista
      this.loadRequests();
      
      // Limpiar el timer
      this.timers.delete(requestId);
      
      // Mostrar notificación
      alert(`¡La solicitud del ${this.formatDate(allRequests[requestIndex].date)} ha sido confirmada!`);
    }
  }

  clearAllTimers() {
    this.timers.forEach(timer => clearTimeout(timer));
    this.timers.clear();
  }

  ngOnDestroy() {
    this.clearAllTimers();
  }

  goBack() {
    this.router.navigate(['/home']);
  }

  selectRequest(request: WalkRequest) {
    this.selectedRequest = request;
  }

  modifyRequest() {
    if (this.selectedRequest) {
      // Navegar a la página de solicitud con parámetros de edición
      this.router.navigate(['/request'], {
        queryParams: { edit: 'true', id: this.selectedRequest.id }
      });
    } else {
      alert('Por favor selecciona una solicitud');
    }
  }

  cancelRequest() {
    if (this.selectedRequest) {
      if (confirm(`¿Estás seguro de que quieres cancelar la solicitud del ${this.selectedRequest.date}?`)) {
        const allRequests = JSON.parse(localStorage.getItem('walkRequests') || '[]');
        const updatedRequests = allRequests.filter((req: WalkRequest) => req.id !== this.selectedRequest!.id);
        localStorage.setItem('walkRequests', JSON.stringify(updatedRequests));
        
        // Limpiar el timer si existe
        if (this.timers.has(this.selectedRequest.id)) {
          clearTimeout(this.timers.get(this.selectedRequest.id));
          this.timers.delete(this.selectedRequest.id);
        }
        
        this.loadRequests();
        this.selectedRequest = null;
        alert('Solicitud cancelada exitosamente');
      }
    } else {
      alert('Por favor selecciona una solicitud');
    }
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const hours = date.getHours().toString().padStart(2, '0');
    return `${day}/${month}, ${hours}hs`;
  }

  openChat(request: WalkRequest) {
    // Navegar al chat con el paseador y el ID de la solicitud
    this.router.navigate(['/chat'], {
      queryParams: { 
        walker: request.walker,
        requestId: request.id
      }
    });
  }
}
