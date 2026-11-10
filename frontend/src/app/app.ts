import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { Imovel, ImoveisService } from './imoveis.service';

@Component({
  imports: [CommonModule],
  selector: 'app-root',
  styleUrl: './app.scss',
  templateUrl: './app.html',
})
export class App implements OnInit {
  private readonly imoveisService = inject(ImoveisService);

  protected readonly imoveis = signal<Imovel[]>([]);
  protected readonly carregando = signal(true);
  protected readonly erro = signal<string | null>(null);

  ngOnInit(): void {
    this.carregarImoveis();
  }

  protected carregarImoveis(): void {
    this.carregando.set(true);
    this.erro.set(null);

    this.imoveisService.listar().subscribe({
      next: (imoveis) => this.imoveis.set(imoveis),
      error: (error: HttpErrorResponse) => {
        this.erro.set(
          error.status === 0
            ? 'Não foi possível conectar ao servidor. Verifique se o backend está em execução.'
            : 'Não foi possível carregar os imóveis. Tente novamente.',
        );
        this.carregando.set(false);
      },
      complete: () => this.carregando.set(false),
    });
  }

  protected formatarPreco(valor: number): string {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
      maximumFractionDigits: 0,
    }).format(valor);
  }
}
