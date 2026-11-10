import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { BairroOption, Imovel, ImovelPage, ImoveisService, TipoImovelOption } from './imoveis.service';

@Component({
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './property-list.html',
})
export class PropertyList implements OnInit {
  private readonly imoveisService = inject(ImoveisService);

  protected readonly resultado = signal<ImovelPage | null>(null);
  protected readonly carregando = signal(true);
  protected readonly erro = signal<string | null>(null);
  protected readonly erroFiltros = signal<string | null>(null);
  protected readonly finalidades = signal<string[]>([]);
  protected readonly bairros = signal<BairroOption[]>([]);
  protected readonly tipos = signal<TipoImovelOption[]>([]);

  protected finalidade = '';
  protected bairroId = '';
  protected tipoImovelId = '';
  protected precoMin = '';
  protected precoMax = '';
  protected sort = 'recentes';
  protected direction = 'desc';
  protected readonly pageSize = 9;

  ngOnInit(): void {
    this.carregarOpcoes();
    this.carregarImoveis(0);
  }

  protected carregarOpcoes(): void {
    forkJoin({
      finalidades: this.imoveisService.finalidades(),
      bairros: this.imoveisService.bairros(),
      tipos: this.imoveisService.tipos(),
    }).subscribe({
      next: (opcoes) => {
        this.finalidades.set(opcoes.finalidades);
        this.bairros.set(opcoes.bairros);
        this.tipos.set(opcoes.tipos);
      },
      error: () => this.erroFiltros.set('Não foi possível carregar as opções de filtro.'),
    });
  }

  protected aplicarFiltros(): void {
    this.carregarImoveis(0);
  }

  protected limparFiltros(): void {
    this.finalidade = '';
    this.bairroId = '';
    this.tipoImovelId = '';
    this.precoMin = '';
    this.precoMax = '';
    this.sort = 'recentes';
    this.direction = 'desc';
    this.carregarImoveis(0);
  }

  protected carregarImoveis(page: number): void {
    this.carregando.set(true);
    this.erro.set(null);
    this.imoveisService.buscar({
      page,
      size: this.pageSize,
      sort: this.sort,
      direction: this.direction,
      finalidade: this.finalidade || undefined,
      bairroId: this.bairroId ? Number(this.bairroId) : undefined,
      tipoImovelId: this.tipoImovelId ? Number(this.tipoImovelId) : undefined,
      precoMin: this.precoMin ? Number(this.precoMin) : undefined,
      precoMax: this.precoMax ? Number(this.precoMax) : undefined,
    }).subscribe({
      next: (resultado) => this.resultado.set(resultado),
      error: (error: HttpErrorResponse) => {
        this.erro.set(
          error.status === 0
            ? 'Não foi possível conectar à API. Confira se o Spring Boot está em execução.'
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
