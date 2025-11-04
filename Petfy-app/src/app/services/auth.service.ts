import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, catchError, map, of } from 'rxjs';

export interface User {
  username: string;
  email: string;
  password: string;
  role?: 'customer' | 'walker';
}

export interface LoginCredentials {
  email: string;
  password: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    // Verificar si hay un usuario logueado en localStorage
    const savedUser = localStorage.getItem('currentUser');
    if (savedUser) {
      this.currentUserSubject.next(JSON.parse(savedUser));
    }
  }

  // Validar formato de email
  private isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  // Validar que el email tenga más de 2 caracteres en el dominio y usuario
  private validateEmailFormat(email: string): boolean {
    if (!this.isValidEmail(email)) {
      return false;
    }
    
    const [username, domain] = email.split('@');
    const [domainName, extension] = domain.split('.');
    
    return username.length > 2 && domainName.length > 2 && extension.length > 0;
  }

  // Validar username
  private validateUsername(username: string): boolean {
    const trimmedUsername = username.trim();
    return trimmedUsername.length >= 3 && trimmedUsername.length <= 20;
  }

  // Validar contraseñas
  private validatePasswords(password: string, confirmPassword: string): boolean {
    return password === confirmPassword && password.length > 0;
  }

  // Registrar nuevo usuario
  register(userData: User & { confirmPassword: string }): Observable<{ success: boolean; message: string; user?: User }> {
    // Validar username
    if (!this.validateUsername(userData.username)) {
      return of({
        success: false,
        message: 'El nombre de usuario debe tener entre 3 y 20 caracteres'
      });
    }

    // Validar email
    if (!this.validateEmailFormat(userData.email)) {
      return of({
        success: false,
        message: 'El email debe tener un formato válido con más de 2 caracteres en usuario y dominio'
      });
    }

    // Validar contraseñas
    if (!this.validatePasswords(userData.password, userData.confirmPassword)) {
      return of({
        success: false,
        message: 'Las contraseñas no coinciden'
      });
    }

    // Preparar datos para enviar al backend
    const registerData = {
      username: userData.username.trim(),
      email: userData.email.trim(),
      password: userData.password,
      confirmPassword: userData.confirmPassword
    };

    // Llamar al backend
    return this.http.post<{ success: boolean; message: string; data?: any }>(`${this.apiUrl}/register`, registerData)
      .pipe(
        map(response => {
          if (response.success && response.data) {
            // Convertir la respuesta del backend al formato del frontend
            const newUser: User = {
              username: response.data.username,
              email: response.data.email,
              password: userData.password, // Guardar contraseña en texto plano para el frontend (no se envía al servidor después)
              role: response.data.role || 'customer'
            };

            // Guardar usuario en localStorage
            localStorage.setItem('currentUser', JSON.stringify(newUser));
            this.currentUserSubject.next(newUser);

            return {
              success: true,
              message: response.message || 'Usuario registrado exitosamente',
              user: newUser
            };
          } else {
            return {
              success: false,
              message: response.message || 'Error al registrar usuario'
            };
          }
        }),
        catchError(error => {
          console.error('Error en registro:', error);
          const errorMessage = error.error?.message || error.message || 'Error al conectar con el servidor';
          return of({
            success: false,
            message: errorMessage
          });
        })
      );
  }

  // Iniciar sesión
  login(credentials: LoginCredentials): Observable<{ success: boolean; message: string; user?: User }> {
    // Llamar al backend
    return this.http.post<{ success: boolean; message: string; data?: any }>(`${this.apiUrl}/login`, credentials)
      .pipe(
        map(response => {
          if (response.success && response.data) {
            // Convertir la respuesta del backend al formato del frontend
            const user: User = {
              username: response.data.username,
              email: response.data.email,
              password: credentials.password, // Guardar contraseña en texto plano para el frontend (no se envía al servidor después)
              role: response.data.role || 'customer'
            };

            // Guardar usuario en localStorage
            localStorage.setItem('currentUser', JSON.stringify(user));
            this.currentUserSubject.next(user);

            return {
              success: true,
              message: response.message || 'Inicio de sesión exitoso',
              user: user
            };
          } else {
            return {
              success: false,
              message: response.message || 'Error al iniciar sesión'
            };
          }
        }),
        catchError(error => {
          console.error('Error en login:', error);
          const errorMessage = error.error?.message || error.message || 'Error al conectar con el servidor';
          return of({
            success: false,
            message: errorMessage
          });
        })
      );
  }

  // Cerrar sesión
  logout(): void {
    // Limpiar usuario actual
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
    
    // Limpiar todos los datos relacionados con la sesión
    localStorage.removeItem('walkRequests');
    localStorage.removeItem('walkRatings');
    localStorage.removeItem('appRating');
    
    // Limpiar todos los chats (buscamos todas las claves que empiezan con 'chat_')
    const keysToRemove: string[] = [];
    for (let i = 0; i < localStorage.length; i++) {
      const key = localStorage.key(i);
      if (key && key.startsWith('chat_')) {
        keysToRemove.push(key);
      }
    }
    keysToRemove.forEach(key => localStorage.removeItem(key));
    
    // Nota: 'users' se mantiene por si se necesita para desarrollo, pero ya no se usa
    // Si quieres limpiarlo también, descomenta la siguiente línea:
    // localStorage.removeItem('users');
  }

  // Obtener usuarios almacenados
  private getStoredUsers(): User[] {
    const users = localStorage.getItem('users');
    return users ? JSON.parse(users) : [];
  }

  // Obtener usuario actual
  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  // Verificar si hay usuario logueado
  isLoggedIn(): boolean {
    return this.currentUserSubject.value !== null;
  }

  // Cambiar rol del usuario a walker
  setWalkerRole(): boolean {
    const currentUser = this.getCurrentUser();
    if (!currentUser) return false;

    // Actualizar rol en el array de usuarios
    const users = this.getStoredUsers();
    const userIndex = users.findIndex(u => u.email === currentUser.email);
    if (userIndex !== -1) {
      users[userIndex].role = 'walker';
      localStorage.setItem('users', JSON.stringify(users));
    }

    // Actualizar usuario actual
    currentUser.role = 'walker';
    localStorage.setItem('currentUser', JSON.stringify(currentUser));
    this.currentUserSubject.next(currentUser);

    return true;
  }

  // Verificar si el usuario es paseador
  isWalker(): boolean {
    return this.getCurrentUser()?.role === 'walker';
  }
}

