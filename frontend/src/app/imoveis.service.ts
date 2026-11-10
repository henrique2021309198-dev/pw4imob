import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

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
  endereco?: string | null;
  bairro?: { nome: string } | null;
  tipoImovel?: { nome: string } | null;
  destaque?: boolean | null;
}

@Injectable({ providedIn: 'root' })
export class ImoveisService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/imoveis';

  listar(): Observable<Imovel[]> {
    return this.http.get<Imovel[]>(this.apiUrl);
  }
}
