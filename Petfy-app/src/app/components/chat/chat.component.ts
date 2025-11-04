import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';

interface Message {
  id: number;
  text: string;
  sender: 'user' | 'walker';
  timestamp: Date;
}

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent implements OnInit {
  walkerName: string = '';
  clientName: string = '';
  messages: Message[] = [];
  newMessage: string = '';
  requestId: number | null = null;
  currentUser: any = null;
  isWalker: boolean = false;

  constructor(private router: Router, private route: ActivatedRoute) {}

  ngOnInit() {
    // Obtener parámetros de la URL
    this.route.queryParams.subscribe(params => {
      this.walkerName = params['walker'] || 'Paseador';
      this.clientName = params['clientName'] || 'Cliente';
      this.requestId = parseInt(params['requestId']) || null;
    });

    // Obtener usuario actual desde localStorage
    const userData = localStorage.getItem('currentUser');
    if (userData) {
      this.currentUser = JSON.parse(userData);
      // Verificar si es paseador (el usuario tiene nombre igual al walkerName)
      this.isWalker = this.currentUser?.username === this.walkerName;
    }

    this.loadMessages();
  }

  loadMessages() {
    // Cargar mensajes existentes desde localStorage
    const chatKey = `chat_${this.requestId}_${this.walkerName}`;
    const savedMessages = localStorage.getItem(chatKey);
    if (savedMessages) {
      this.messages = JSON.parse(savedMessages).map((msg: any) => ({
        ...msg,
        timestamp: new Date(msg.timestamp)
      }));
    } else {
      // Mensaje inicial dependiendo de quién lo ve
      if (this.isWalker) {
        // Si es el paseador, mensaje de bienvenida del paseador
        this.messages = [{
          id: 1,
          text: `¡Hola! Soy ${this.walkerName}, voy a acompañar a tu mascota en el paseo.`,
          sender: 'walker',
          timestamp: new Date()
        }];
      } else {
        // Si es el cliente, mensaje inicial del paseador
        this.messages = [{
          id: 1,
          text: `¡Hola! Soy ${this.walkerName}, tu paseador asignado. ¿Cómo está tu mascota?`,
          sender: 'walker',
          timestamp: new Date()
        }];
      }
    }
  }

  sendMessage() {
    if (this.newMessage.trim()) {
      const message: Message = {
        id: Date.now(),
        text: this.newMessage.trim(),
        sender: this.isWalker ? 'walker' : 'user',
        timestamp: new Date()
      };

      this.messages.push(message);
      this.newMessage = '';
      this.saveMessages();

      // Si es el cliente, simular respuesta del paseador
      // Si es el paseador, simular respuesta del cliente
      if (this.isWalker) {
        setTimeout(() => {
          this.simulateClientResponse();
        }, 2500);
      } else {
        setTimeout(() => {
          this.simulateWalkerResponse();
        }, 2500);
      }
    }
  }

  simulateClientResponse() {
    const responses = [
      'Perfecto, muchas gracias!',
      'De acuerdo, nos vemos entonces.',
      'Excelente, mi mascota está muy contenta.',
      'Entendido, no hay problema.',
      'Está bien, gracias por la información.',
      'Perfecto, nos comunicamos más tarde.',
      'De acuerdo, muy amable.'
    ];
    const randomResponse = responses[Math.floor(Math.random() * responses.length)];
    
    const message: Message = {
      id: Date.now(),
      text: randomResponse,
      sender: 'user',
      timestamp: new Date()
    };

    this.messages.push(message);
    this.saveMessages();
  }

  simulateWalkerResponse() {
    const responses = [
      'No hay inconveniente alguno.',
      'Perfecto, no hay problema.',
      'Entendido, sin problemas.',
      'No te preocupes, todo está bien.',
      'Perfecto, me parece bien.',
      'Entendido, todo bajo control.',
      '¡Perfecto! Todo listo.',
      'No hay inconveniente con eso.',
      'Perfecto, sin problemas.',
      'Entendido, no hay inconveniente.',
      '¡Genial! Todo está claro.',
      'Perfecto, todo bien.',
      'No hay problema alguno.',
      'Entendido, sin inconveniente.',
      '¡Perfecto! No hay inconveniente.'
    ];

    const randomResponse = responses[Math.floor(Math.random() * responses.length)];
    
    const walkerMessage: Message = {
      id: Date.now() + 1,
      text: randomResponse,
      sender: 'walker',
      timestamp: new Date()
    };

    this.messages.push(walkerMessage);
    this.saveMessages();
  }


  saveMessages() {
    const chatKey = `chat_${this.requestId}_${this.walkerName}`;
    localStorage.setItem(chatKey, JSON.stringify(this.messages));
  }

  goBack() {
    // Si es paseador, volver a sus peticiones; si es cliente, volver a sus solicitudes
    if (this.isWalker) {
      this.router.navigate(['/walker-requests']);
    } else {
      this.router.navigate(['/requests']);
    }
  }

  formatTime(timestamp: Date): string {
    return timestamp.toLocaleTimeString('es-ES', { 
      hour: '2-digit', 
      minute: '2-digit' 
    });
  }
}
