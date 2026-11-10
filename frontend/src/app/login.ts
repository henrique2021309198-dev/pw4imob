import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from './auth.service';

@Component({
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
})
export class Login implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  protected email = '';
  protected senha = '';
  protected readonly carregando = signal(false);
  protected readonly erro = signal<string | null>(null);
  protected readonly aviso = signal<string | null>(null);

  ngOnInit(): void {
    if (this.route.snapshot.queryParamMap.has('expired')) {
      this.aviso.set('Sua sessão expirou. Entre novamente para continuar.');
    }
  }

  protected entrar(): void {
    this.erro.set(null);
    if (!this.email.trim()) {
      this.erro.set('Informe seu e-mail.');
      return;
    }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.email.trim())) {
      this.erro.set('Informe um endereço de e-mail válido.');
      return;
    }
    if (!this.senha) {
      this.erro.set('Informe sua senha.');
      return;
    }

    this.carregando.set(true);
    this.auth.login(this.email.trim(), this.senha).subscribe({
      next: () => {
        const destination = this.auth.role() === 'ADMIN' ? '/admin' : '/cliente';
        void this.router.navigateByUrl(destination);
      },
      error: (error: HttpErrorResponse) => {
        this.erro.set(
          error.status === 401
            ? 'E-mail ou senha incorretos.'
            : error.status === 0
              ? 'Não foi possível conectar ao servidor. Verifique se a API está em execução.'
              : error.status >= 500
                ? 'O servidor encontrou um problema. Tente novamente mais tarde.'
                : this.mensagemDaApi(error) ?? 'Não foi possível entrar. Confira os dados e tente novamente.',
        );
        this.carregando.set(false);
      },
      complete: () => this.carregando.set(false),
    });
  }

  private mensagemDaApi(error: HttpErrorResponse): string | null {
    if (typeof error.error === 'string') {
      return error.error;
    }
    return typeof error.error?.message === 'string' ? error.error.message : null;
  }
}
