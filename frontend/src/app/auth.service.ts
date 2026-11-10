import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { tap } from 'rxjs';
import { API_URL } from './imoveis.service';

const TOKEN_KEY = 'pw4imob.token';

interface LoginResponse {
  token: string;
}

interface TokenClaims {
  role: string;
  subject: string | null;
}

export type UserRole = 'ADMIN' | 'USER';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);

  readonly token = signal(this.loadToken());
  private readonly claims = computed(() => this.decodeToken(this.token()));
  readonly role = computed<UserRole | null>(() => {
    const role = this.claims()?.role.replace(/^ROLE_/, '').toUpperCase();
    return role === 'ADMIN' || role === 'USER' ? role : null;
  });
  readonly email = computed(() => this.claims()?.subject ?? null);
  readonly isAuthenticated = computed(() => this.token() !== null && this.role() !== null);

  login(email: string, senha: string) {
    return this.http
      .post<LoginResponse>(`${API_URL}/auth/login`, { email, senha })
      .pipe(tap(({ token }) => this.saveToken(token)));
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    this.token.set(null);
  }

  private saveToken(token: string): void {
    localStorage.setItem(TOKEN_KEY, token);
    this.token.set(token);
  }

  private loadToken(): string | null {
    const token = localStorage.getItem(TOKEN_KEY);
    if (!token) {
      return null;
    }

    const claims = this.decodeToken(token);
    if (!claims) {
      localStorage.removeItem(TOKEN_KEY);
      return null;
    }
    return token;
  }

  private decodeToken(token: string | null): TokenClaims | null {
    if (!token) {
      return null;
    }

    const encodedPayload = token.split('.')[1];
    if (!encodedPayload) {
      return null;
    }

    let payload: string;
    try {
      const base64 = encodedPayload.replace(/-/g, '+').replace(/_/g, '/');
      payload = atob(base64.padEnd(Math.ceil(base64.length / 4) * 4, '='));
    } catch (error) {
      if (error instanceof DOMException) {
        return null;
      }
      throw error;
    }

    let parsed: unknown;
    try {
      parsed = JSON.parse(payload);
    } catch (error) {
      if (error instanceof SyntaxError) {
        return null;
      }
      throw error;
    }

    if (typeof parsed !== 'object' || parsed === null) {
      return null;
    }
    const claims = parsed as Record<string, unknown>;
    if (
      typeof claims['role'] !== 'string' ||
      typeof claims['exp'] !== 'number' ||
      claims['exp'] * 1000 <= Date.now()
    ) {
      return null;
    }

    return {
      role: claims['role'],
      subject: typeof claims['sub'] === 'string' ? claims['sub'] : null,
    };
  }
}
