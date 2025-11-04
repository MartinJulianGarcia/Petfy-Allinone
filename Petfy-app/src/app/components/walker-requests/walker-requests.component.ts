import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

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
  selector: 'app-walker-requests',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './walker-requests.component.html',
  styleUrls: ['./walker-requests.component.css']
})
export class WalkerRequestsComponent implements OnInit {
  pendingRequests: WalkRequest[] = [];
  confirmedRequests: WalkRequest[] = [];
  selectedRequest: WalkRequest | null = null;
  walkStatus: Map<number, 'not_started' | 'in_progress' | 'finished'> = new Map();
  isButtonDisabled: Map<number, boolean> = new Map();

  constructor(private router: Router, private authService: AuthService) {}

  ngOnInit(): void {
    // Verificar que el usuario sea un paseador
    if (!this.authService.isWalker()) {
      alert('No tienes permisos para acceder a esta página');
      this.router.navigate(['/home']);
      return;
    }

    this.loadRequests();
  }

  goBack(): void {
    this.router.navigate(['/home']);
  }

  loadRequests(): void {
    const currentUser = this.authService.getCurrentUser();
    const currentUsername = currentUser?.username || '';
    
    // Cargar todas las solicitudes desde localStorage
    const allRequests = JSON.parse(localStorage.getItem('walkRequests') || '[]');
    
    // Filtrar solicitudes pendientes
    const realPendingRequests = allRequests.filter((req: WalkRequest) => req.status === 'pending');
    
    // Si no hay solicitudes pendientes, mostrar solicitudes de ejemplo
    this.pendingRequests = realPendingRequests.length === 0 ? this.getExampleRequests() : realPendingRequests;
    
    // Filtrar solicitudes confirmadas por este paseador
    this.confirmedRequests = allRequests.filter((req: WalkRequest) => 
      req.status === 'confirmed' && req.walker === currentUsername
    );
  }

  getExampleRequests(): WalkRequest[] {
    return [
      {
        id: 999,
        date: '2025-10-25',
        startTime: '14:00',
        endTime: '15:00',
        address: 'Av. Libertador 4567, CABA',
        walker: 'Aleatorio',
        status: 'pending'
      },
      {
        id: 998,
        date: '2025-10-26',
        startTime: '10:30',
        endTime: '11:30',
        address: 'Av. Cabildo 1234, CABA',
        walker: 'Aleatorio',
        status: 'pending'
      }
    ];
  }

  selectRequest(request: WalkRequest): void {
    this.selectedRequest = request;
    // Inicializar estados si no existen
    if (!this.walkStatus.has(request.id)) {
      this.walkStatus.set(request.id, 'not_started');
    }
    if (!this.isButtonDisabled.has(request.id)) {
      this.isButtonDisabled.set(request.id, false);
    }
  }

  acceptRequest(): void {
    if (!this.selectedRequest) {
      alert('Por favor selecciona una solicitud');
      return;
    }

    if (this.selectedRequest.status !== 'pending') {
      alert('Esta solicitud ya fue aceptada');
      return;
    }

    // Si es una solicitud de ejemplo, agregarla a localStorage
    if (this.selectedRequest.id === 999 || this.selectedRequest.id === 998) {
      const allRequests = JSON.parse(localStorage.getItem('walkRequests') || '[]');
      
      // Buscar si ya existe
      const existingIndex = allRequests.findIndex((req: WalkRequest) => req.id === this.selectedRequest!.id);
      
      if (existingIndex !== -1) {
        allRequests[existingIndex].status = 'confirmed';
        allRequests[existingIndex].walker = this.authService.getCurrentUser()?.username || 'Paseador';
      } else {
        const newRequest = {
          ...this.selectedRequest,
          status: 'confirmed' as const,
          walker: this.authService.getCurrentUser()?.username || 'Paseador'
        };
        allRequests.push(newRequest);
      }
      
      localStorage.setItem('walkRequests', JSON.stringify(allRequests));
    } else {
      // Si es una solicitud real, actualizarla
      const allRequests = JSON.parse(localStorage.getItem('walkRequests') || '[]');
      const requestIndex = allRequests.findIndex((req: WalkRequest) => req.id === this.selectedRequest!.id);
      
      if (requestIndex !== -1) {
        allRequests[requestIndex].status = 'confirmed';
        allRequests[requestIndex].walker = this.authService.getCurrentUser()?.username || 'Paseador';
        localStorage.setItem('walkRequests', JSON.stringify(allRequests));
      }
    }
    
    alert('¡Solicitud aceptada exitosamente!');
    this.loadRequests();
    this.selectedRequest = null;
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const year = date.getFullYear();
    return `${day}/${month}/${year}`;
  }

  startWalk(): void {
    if (!this.selectedRequest) return;
    
    this.isButtonDisabled.set(this.selectedRequest.id, true);
    this.walkStatus.set(this.selectedRequest.id, 'in_progress');
    
    alert('¡Paseo iniciado!');
    
    // Habilitar el botón después de 5 segundos
    setTimeout(() => {
      this.isButtonDisabled.set(this.selectedRequest!.id, false);
    }, 5000);
  }

  finishWalk(): void {
    if (!this.selectedRequest) return;
    
    this.isButtonDisabled.set(this.selectedRequest.id, true);
    this.walkStatus.set(this.selectedRequest.id, 'finished');
    
    alert('¡Paseo finalizado!');
    
    // Habilitar el botón después de 5 segundos
    setTimeout(() => {
      this.isButtonDisabled.set(this.selectedRequest!.id, false);
    }, 5000);
  }

  openChat(request: WalkRequest): void {
    // Obtener el nombre del cliente (asumimos que el cliente es el dueño de la solicitud)
    const clientName = 'Cliente';
    
    // Navegar al chat con el paseador como sender y el cliente como recipient
    this.router.navigate(['/chat'], {
      queryParams: {
        walker: request.walker,
        requestId: request.id,
        clientName: clientName
      }
    });
  }
}
