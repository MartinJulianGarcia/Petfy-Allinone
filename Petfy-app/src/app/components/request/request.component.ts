import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';

interface RequestData {
  id?: number;
  date: string;
  startTime: string;
  endTime: string;
  address: string;
  walker: string;
  status: 'pending' | 'confirmed';
}

@Component({
  selector: 'app-request',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './request.component.html',
  styleUrls: ['./request.component.css']
})
export class RequestComponent implements OnInit {
  requestData: RequestData = {
    date: '',
    startTime: '',
    endTime: '',
    address: '',
    walker: '',
    status: 'pending'
  };

  walkers = [
    { name: 'Aleatorio', selected: false, isRandom: true },
    { name: 'Martin', selected: false, isRandom: false },
    { name: 'Azul', selected: false, isRandom: false },
    { name: 'Tomas', selected: false, isRandom: false },
    { name: 'Sofia', selected: false, isRandom: false }
  ];

  timeSlots: string[] = [];
  showWalkersDropdown = false;
  selectedWalkerName = '';
  isEditing = false;
  editingRequestId: number | null = null;

  constructor(private router: Router, private route: ActivatedRoute) {
    this.generateTimeSlots();
  }

  ngOnInit() {
    // Verificar si estamos editando una solicitud existente
    this.route.queryParams.subscribe(params => {
      if (params['edit'] && params['id']) {
        this.loadRequestForEdit(parseInt(params['id']));
      }
    });
  }

  loadRequestForEdit(requestId: number) {
    const allRequests = JSON.parse(localStorage.getItem('walkRequests') || '[]');
    const requestToEdit = allRequests.find((req: any) => req.id === requestId);
    
    if (requestToEdit) {
      this.isEditing = true;
      this.editingRequestId = requestId;
      this.requestData = { ...requestToEdit };
      this.selectedWalkerName = requestToEdit.walker;
      
      // Seleccionar el paseador en la lista
      this.walkers.forEach(walker => {
        walker.selected = walker.name === requestToEdit.walker;
      });
    }
  }

  generateTimeSlots() {
    // Generar rangos de media hora desde las 7:00 AM hasta las 10:00 PM
    const slots = [];
    for (let hour = 7; hour < 22; hour++) {
      slots.push(`${hour.toString().padStart(2, '0')}:00`);
      slots.push(`${hour.toString().padStart(2, '0')}:30`);
    }
    // Agregar las 22:00
    slots.push('22:00');
    this.timeSlots = slots;
  }

  goBack() {
    if (this.isEditing) {
      this.router.navigate(['/requests']);
    } else {
      this.router.navigate(['/home']);
    }
  }

  getTodayDate(): string {
    const today = new Date();
    return today.toISOString().split('T')[0];
  }

  onTimeChange() {
    if (this.requestData.startTime) {
      // Calcular hora de fin (1 hora después)
      const startTime = this.requestData.startTime;
      const [hours, minutes] = startTime.split(':').map(Number);
      const endHours = (hours + 1) % 24;
      const endMinutes = minutes;
      this.requestData.endTime = `${endHours.toString().padStart(2, '0')}:${endMinutes.toString().padStart(2, '0')}`;
    }
  }

  toggleWalkersDropdown() {
    this.showWalkersDropdown = !this.showWalkersDropdown;
  }

  selectWalker(walkerName: string) {
    this.walkers.forEach(walker => {
      walker.selected = walker.name === walkerName;
    });
    this.requestData.walker = walkerName;
    this.selectedWalkerName = walkerName;
    this.showWalkersDropdown = false;
  }

  closeDropdown() {
    this.showWalkersDropdown = false;
  }

  confirmRequest() {
    if (this.requestData.date && this.requestData.startTime && this.requestData.address && this.requestData.walker) {
      // Si es aleatorio, seleccionar un paseador al azar
      let selectedWalker = this.requestData.walker;
      if (selectedWalker === 'Aleatorio') {
        const randomWalkers = ['Martin', 'Azul', 'Tomas', 'Sofia'];
        selectedWalker = randomWalkers[Math.floor(Math.random() * randomWalkers.length)];
      }

      const request = {
        ...this.requestData,
        walker: selectedWalker
      };

      // Guardar en localStorage
      const existingRequests = JSON.parse(localStorage.getItem('walkRequests') || '[]');
      
      if (this.isEditing && this.editingRequestId) {
        // Actualizar solicitud existente
        const requestIndex = existingRequests.findIndex((req: any) => req.id === this.editingRequestId);
        if (requestIndex !== -1) {
          // Si se está modificando una solicitud confirmada, volverla a pending
          const updatedRequest = { 
            ...request, 
            id: this.editingRequestId,
            status: 'pending' // Siempre volver a pending cuando se modifica
          };
          existingRequests[requestIndex] = updatedRequest;
          localStorage.setItem('walkRequests', JSON.stringify(existingRequests));
          alert('¡Paseo modificado exitosamente! Volverá a confirmarse automáticamente.');
        }
      } else {
        // Crear nueva solicitud
        request.id = Date.now(); // ID único para la solicitud
        existingRequests.push(request);
        localStorage.setItem('walkRequests', JSON.stringify(existingRequests));
        alert('¡Solicitud de paseo enviada exitosamente!');
      }

      this.router.navigate(['/requests']);
    } else {
      alert('Por favor completa todos los campos requeridos');
    }
  }
}
