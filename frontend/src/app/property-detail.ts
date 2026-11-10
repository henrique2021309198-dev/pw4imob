import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { ContactRequest, FotoImovel, Imovel, ImoveisService } from './imoveis.service';

@Component({
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './property-detail.html',
})
export class PropertyDetail implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly imoveisService = inject(ImoveisService);

  protected readonly imovel = signal<Imovel | null>(null);
  protected readonly fotos = signal<FotoImovel[]>([]);
  protected readonly carregando = signal(true);
  protected readonly erro = signal<string | null>(null);
  protected readonly enviando = signal(false);
  protected readonly mensagem = signal<string | null>(null);
  protected readonly erroContato = signal<string | null>(null);
  protected contato: ContactRequest = { nome: '', telefone: '', email: '', mensagem: '' };
  private imovelId = 0;

  ngOnInit(): void {
    this.imovelId = Number(this.route.snapshot.paramMap.get('id'));
    if (!Number.isInteger(this.imovelId) || this.imovelId < 1) {
      this.erro.set('O identificador do imóvel é inválido.');
      this.carregando.set(false);
      return;
    }

    forkJoin({
      imovel: this.imoveisService.buscarPorId(this.imovelId),
      fotos: this.imoveisService.fotos(this.imovelId),
    }).subscribe({
      next: (resultado) => {
        this.imovel.set(resultado.imovel);
        this.fotos.set(resultado.fotos.sort((a, b) => Number(b.capa) - Number(a.capa) || (a.ordem ?? 0) - (b.ordem ?? 0)));
      },
      error: (error: HttpErrorResponse) => {
        this.erro.set(
          error.status === 404
            ? 'Este imóvel não foi encontrado.'
            : error.status === 0
              ? 'Não foi possível conectar à API. Confira se o Spring Boot está em execução.'
              : 'Não foi possível carregar os detalhes do imóvel.',
        );
        this.carregando.set(false);
      },
      complete: () => this.carregando.set(false),
    });
  }

  protected enviarContato(): void {
    this.erroContato.set(null);
    this.mensagem.set(null);
    this.enviando.set(true);
    this.imoveisService.enviarContato(this.imovelId, this.contato).subscribe({
      next: () => this.mensagem.set('Solicitação enviada. Verifique seu e-mail para receber as informações.'),
      error: (error: HttpErrorResponse) => {
        const apiMessage = this.mensagemDaApi(error);
        this.erroContato.set(
          error.status === 0
            ? 'Não foi possível conectar ao servidor.'
            : apiMessage ?? 'Não foi possível enviar sua solicitação. Tente novamente.',
        );
        this.enviando.set(false);
      },
      complete: () => this.enviando.set(false),
    });
  }

  protected urlDaFoto(url: string | null): string | null {
    return this.imoveisService.urlDaFoto(url);
  }

  protected formatarPreco(valor: number): string {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL', maximumFractionDigits: 0 }).format(valor);
  }

  private mensagemDaApi(error: HttpErrorResponse): string | null {
    if (typeof error.error === 'string') {
      return error.error;
    }
    return typeof error.error?.message === 'string' ? error.error.message : null;
  }
}
