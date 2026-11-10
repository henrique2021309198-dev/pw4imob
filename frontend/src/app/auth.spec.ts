import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { authInterceptor } from './auth.interceptor';
import { AuthService } from './auth.service';
import { API_URL } from './imoveis.service';

describe('authentication', () => {
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    localStorage.removeItem('pw4imob.token');
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
        provideRouter([{ path: 'login', children: [] }]),
      ],
    });
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
    localStorage.removeItem('pw4imob.token');
  });

  it('attaches the JWT and clears an expired session after a 401', () => {
    const token = criarToken('ROLE_USER');
    localStorage.setItem('pw4imob.token', token);
    const auth = TestBed.inject(AuthService);

    expect(auth.role()).toBe('USER');
    expect(auth.isAuthenticated()).toBe(true);

    TestBed.inject(HttpClient).get(`${API_URL}/users`).subscribe({
      error: () => undefined,
    });
    const request = httpTesting.expectOne(`${API_URL}/users`);
    expect(request.request.headers.get('Authorization')).toBe(`Bearer ${token}`);
    request.flush({}, { status: 401, statusText: 'Unauthorized' });

    expect(auth.isAuthenticated()).toBe(false);
    expect(localStorage.getItem('pw4imob.token')).toBeNull();
  });
});

function criarToken(role: string): string {
  const payload = btoa(
    JSON.stringify({
      sub: 'cliente@example.com',
      role,
      exp: Math.floor(Date.now() / 1000) + 3600,
    }),
  )
    .replace(/=/g, '')
    .replace(/\+/g, '-')
    .replace(/\//g, '_');
  return `header.${payload}.signature`;
}
