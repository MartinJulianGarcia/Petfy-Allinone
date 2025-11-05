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
    
    // Filtrar solicitudes confirmadas por este paseador PRIMERO
    this.confirmedRequests = allRequests.filter((req: WalkRequest) => 
      req.status === 'confirmed' && req.walker === currentUsername
    );
    
    // Obtener IDs de solicitudes confirmadas por este paseador (para excluirlas de pendientes)
    const confirmedIds = this.confirmedRequests.map(req => req.id);
    
    // Filtrar solicitudes pendientes (excluyendo las que ya fueron confirmadas por este paseador)
    const realPendingRequests = allRequests.filter((req: WalkRequest) => 
      req.status === 'pending' && !confirmedIds.includes(req.id)
    );
    
    // Si no hay solicitudes pendientes reales, mostrar solicitudes de ejemplo (solo si no fueron aceptadas)
    const exampleRequests = this.getExampleRequests();
    const exampleNotAccepted = exampleRequests.filter(example => 
      !confirmedIds.includes(example.id)
    );
    
    this.pendingRequests = realPendingRequests.length > 0 
      ? realPendingRequests 
      : (exampleNotAccepted.length > 0 ? exampleNotAccepted : []);
  }

  getExampleRequests(): WalkRequest[] {
    // Obtener fecha y hora actuales
    const now = new Date();
    const currentHour = now.getHours();
    
    // Formatear fecha actual (YYYY-MM-DD)
    const currentDate = now.toISOString().split('T')[0];
    
    // Calcular horas para las solicitudes (asegurar que sean iguales o mayores a la hora actual)
    // Primera solicitud: hora actual + 1 hora (mínimo 1 hora desde ahora)
    let hour1 = currentHour + 1;
    let endHour1 = hour1 + 1;
    if (hour1 >= 24) {
      hour1 = 23;
      endHour1 = 23; // Si es muy tarde, usar 23:00-23:59
    } else if (endHour1 >= 24) {
      endHour1 = 23;
    }
    const startTime1 = `${hour1.toString().padStart(2, '0')}:00`;
    const endTime1 = `${endHour1.toString().padStart(2, '0')}:00`;
    
    // Segunda solicitud: hora actual + 2 horas (mínimo 2 horas desde ahora)
    let hour2 = currentHour + 2;
    let endHour2 = hour2 + 1;
    if (hour2 >= 24) {
      hour2 = 23;
      endHour2 = 23; // Si es muy tarde, usar 23:00-23:59
    } else if (endHour2 >= 24) {
      endHour2 = 23;
    }
    const startTime2 = `${hour2.toString().padStart(2, '0')}:30`;
    const endTime2 = `${endHour2.toString().padStart(2, '0')}:30`;
    
    return [
      {
        id: 999,
        date: currentDate,
        startTime: startTime1,
        endTime: endTime1,
        address: 'Av. Libertador 4567, CABA',
        walker: 'Aleatorio',
        status: 'pending'
      },
      {
        id: 998,
        date: currentDate,
        startTime: startTime2,
        endTime: endTime2,
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

    const currentUsername = this.authService.getCurrentUser()?.username || 'Paseador';
    const allRequests = JSON.parse(localStorage.getItem('walkRequests') || '[]');
    
    // Buscar si ya existe en localStorage
    const existingIndex = allRequests.findIndex((req: WalkRequest) => req.id === this.selectedRequest!.id);
    
    if (existingIndex !== -1) {
      // Actualizar solicitud existente
      allRequests[existingIndex].status = 'confirmed';
      allRequests[existingIndex].walker = currentUsername;
    } else {
      // Si es una solicitud de ejemplo que no existe en localStorage, agregarla
      const newRequest = {
        ...this.selectedRequest,
        status: 'confirmed' as const,
        walker: currentUsername
      };
      allRequests.push(newRequest);
    }
    
    // Guardar en localStorage
    localStorage.setItem('walkRequests', JSON.stringify(allRequests));
    
    alert('¡Solicitud aceptada exitosamente!');
    
    // Recargar las solicitudes para actualizar las listas
    this.loadRequests();
    
    // Limpiar selección
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
