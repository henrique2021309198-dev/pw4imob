import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export const API_URL = 'http://localhost:8080';

export interface Imovel {
  id: number;
  titulo: string;
  descricao?: string | null;
  precoVenda?: number | null;
  precoAluguel?: number | null;
  finalidade?: string | null;
  dormitorios?: number | null;
  banheiros?: number | null;
  garagem?: number | null;
  areaTotal?: number | null;
  areaConstruida?: number | null;
  endereco?: string | null;
  numero?: string | null;
  complemento?: string | null;
  cep?: string | null;
  caracteristicas?: string | null;
  status?: string | null;
  bairro?: { nome: string } | null;
  tipoImovel?: { nome: string } | null;
  destaque?: boolean | null;
}

export interface ImovelPage {
  content: Imovel[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

export interface BairroOption {
  id: number;
  nome: string;
}

export interface TipoImovelOption {
  id: number;
  nome: string;
}

export interface FotoImovel {
  id: number;
  url: string | null;
  capa: boolean | null;
  ordem: number | null;
}

export interface ContactRequest {
  nome: string;
  telefone: string;
  email: string;
  mensagem: string;
}

export interface ImovelSearch {
  page: number;
  size: number;
  sort: string;
  direction: string;
  finalidade?: string;
  bairroId?: number;
  tipoImovelId?: number;
  precoMin?: number;
  precoMax?: number;
}

@Injectable({ providedIn: 'root' })
export class ImoveisService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${API_URL}/imoveis`;

  listar(): Observable<Imovel[]> {
    return this.http.get<Imovel[]>(this.apiUrl);
  }

  buscar(filtros: ImovelSearch): Observable<ImovelPage> {
    let params = new HttpParams()
      .set('page', filtros.page)
      .set('size', filtros.size)
      .set('sort', filtros.sort)
      .set('direction', filtros.direction);
    if (filtros.finalidade) params = params.set('finalidade', filtros.finalidade);
    if (filtros.bairroId) params = params.set('bairroId', filtros.bairroId);
    if (filtros.tipoImovelId) params = params.set('tipoImovelId', filtros.tipoImovelId);
    if (filtros.precoMin !== undefined) params = params.set('precoMin', filtros.precoMin);
    if (filtros.precoMax !== undefined) params = params.set('precoMax', filtros.precoMax);
    return this.http.get<ImovelPage>(`${this.apiUrl}/pagina`, { params });
  }

  finalidades(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/finalidades`);
  }

  bairros(): Observable<BairroOption[]> {
    return this.http.get<BairroOption[]>(`${API_URL}/bairros`);
  }

  tipos(): Observable<TipoImovelOption[]> {
    return this.http.get<TipoImovelOption[]>(`${API_URL}/tipos-imoveis`);
  }

  buscarPorId(id: number): Observable<Imovel> {
    return this.http.get<Imovel>(`${this.apiUrl}/${id}`);
  }

  fotos(id: number): Observable<FotoImovel[]> {
    return this.http.get<FotoImovel[]>(`${API_URL}/fotos-imoveis/imovel/${id}`);
  }

  enviarContato(id: number, contato: ContactRequest): Observable<string> {
    return this.http.post(`${this.apiUrl}/sabermais/${id}`, contato, { responseType: 'text' });
  }

  urlDaFoto(url: string | null): string | null {
    return url ? `${API_URL}${url}` : null;
  }
}
