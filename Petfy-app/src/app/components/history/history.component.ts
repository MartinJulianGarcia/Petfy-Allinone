import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

interface WalkHistory {
  id: number;
  date: string;
  time: string;
  walker: string;
  status: 'finalized';
  address: string;
  rating?: number; // Calificación del paseo (1-5)
}

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.css']
})
export class HistoryComponent implements OnInit {
  finalizedWalks: WalkHistory[] = [];
  filteredWalks: WalkHistory[] = [];
  selectedWalk: WalkHistory | null = null;
  
  // Filtros
  startDate = '';
  
  // Estado de calificación
  showRatingModal = false;
  ratingType: 'app' | 'walk' = 'app';
  currentRating = 0;
  ratingHover = 0;
  appRating: number | null = null; // Calificación de la app guardada

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.loadFinalizedWalks();
    this.loadAppRating();
  }

  goBack(): void {
    this.router.navigate(['/home']);
  }

  loadFinalizedWalks(): void {
    // Cargar paseos finalizados desde localStorage
    const allRequests = JSON.parse(localStorage.getItem('walkRequests') || '[]');
    // Filtrar paseos con isCompleted como true
    const realWalks = allRequests.filter((request: any) => request.isCompleted === true);
    
    // Cargar calificaciones guardadas
    const savedRatings = JSON.parse(localStorage.getItem('walkRatings') || '{}');
    
    // Si no hay paseos reales, usar paseos de ejemplo por defecto
    if (realWalks.length === 0) {
      this.finalizedWalks = this.getDefaultWalks();
    } else {
      this.finalizedWalks = realWalks.map((walk: any) => ({
        ...walk,
        rating: savedRatings[walk.id] || undefined
      }));
    }
    
    // Aplicar calificaciones a los paseos de ejemplo también
    this.finalizedWalks = this.finalizedWalks.map(walk => {
      const savedRating = savedRatings[walk.id];
      return savedRating ? { ...walk, rating: savedRating } : walk;
    });
    
    this.filteredWalks = [...this.finalizedWalks];
  }

  loadAppRating(): void {
    const savedAppRating = localStorage.getItem('appRating');
    if (savedAppRating) {
      this.appRating = parseInt(savedAppRating, 10);
    }
  }

  getDefaultWalks(): WalkHistory[] {
    return [
      {
        id: 1,
        date: '2025-10-20',
        time: '10:00',
        walker: 'Martin',
        status: 'finalized',
        address: 'Av. Corrientes 1234, CABA'
      },
      {
        id: 2,
        date: '2025-10-18',
        time: '16:30',
        walker: 'Azul',
        status: 'finalized',
        address: 'Av. Santa Fe 5678, CABA'
      }
    ];
  }

  filterWalks(): void {
    if (!this.startDate) {
      this.filteredWalks = [...this.finalizedWalks];
      return;
    }

    this.filteredWalks = this.finalizedWalks.filter(walk => {
      const walkDate = new Date(walk.date);
      const start = new Date(this.startDate);
      return walkDate >= start;
    });
  }

  selectWalk(walk: WalkHistory): void {
    this.selectedWalk = walk;
  }

  selectRating(walk: WalkHistory): void {
    this.selectedWalk = walk;
    this.ratingType = 'walk';
    this.showRatingModal = true;
    this.currentRating = 0;
  }

  rateApp(): void {
    this.ratingType = 'app';
    this.showRatingModal = true;
    this.currentRating = 0;
  }

  setRating(rating: number): void {
    this.currentRating = rating;
  }

  hoverRating(rating: number): void {
    this.ratingHover = rating;
  }

  clearHover(): void {
    this.ratingHover = 0;
  }

  submitRating(): void {
    if (this.currentRating === 0) {
      alert('Por favor selecciona una calificación');
      return;
    }

    if (this.ratingType === 'app') {
      // Guardar calificación de la app
      localStorage.setItem('appRating', this.currentRating.toString());
      this.appRating = this.currentRating;
      alert(`¡Gracias por calificar nuestra app con ${this.currentRating} estrella${this.currentRating > 1 ? 's' : ''}!`);
    } else {
      // Guardar calificación del paseo
      if (this.selectedWalk) {
        const savedRatings = JSON.parse(localStorage.getItem('walkRatings') || '{}');
        savedRatings[this.selectedWalk.id] = this.currentRating;
        localStorage.setItem('walkRatings', JSON.stringify(savedRatings));
        
        // Actualizar la calificación en el paseo actual
        this.selectedWalk.rating = this.currentRating;
        
        // Actualizar en la lista
        const walkIndex = this.finalizedWalks.findIndex(w => w.id === this.selectedWalk!.id);
        if (walkIndex !== -1) {
          this.finalizedWalks[walkIndex].rating = this.currentRating;
        }
        
        // Actualizar lista filtrada
        const filteredIndex = this.filteredWalks.findIndex(w => w.id === this.selectedWalk!.id);
        if (filteredIndex !== -1) {
          this.filteredWalks[filteredIndex].rating = this.currentRating;
        }
        
        alert(`¡Gracias por calificar el paseo con ${this.currentRating} estrella${this.currentRating > 1 ? 's' : ''}!`);
      }
    }

    this.showRatingModal = false;
    this.currentRating = 0;
    this.ratingHover = 0;
  }

  closeRatingModal(): void {
    this.showRatingModal = false;
    this.currentRating = 0;
    this.ratingHover = 0;
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    return `${day}/${month}`;
  }
}
