import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { App } from './app';
import { API_URL } from './imoveis.service';
import { PropertyList } from './property-list';

describe('application pages', () => {
  let httpTesting: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App, PropertyList],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('renders the public navigation', () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();

    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Vitrine');
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Entrar');
  });

  it('loads the paginated public listing and its filter options', () => {
    const fixture = TestBed.createComponent(PropertyList);
    fixture.detectChanges();

    httpTesting.expectOne(`${API_URL}/imoveis/finalidades`).flush([]);
    httpTesting.expectOne(`${API_URL}/bairros`).flush([]);
    httpTesting.expectOne(`${API_URL}/tipos-imoveis`).flush([]);
    const pageRequest = httpTesting.expectOne(
      (request) => request.url === `${API_URL}/imoveis/pagina`,
    );
    expect(pageRequest.request.method).toBe('GET');
    expect(pageRequest.request.params.get('page')).toBe('0');
    expect(pageRequest.request.params.get('size')).toBe('9');
    pageRequest.flush({
      content: [],
      totalElements: 0,
      totalPages: 0,
      number: 0,
      size: 9,
      first: true,
      last: true,
    });
    fixture.detectChanges();

    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Nenhum imóvel encontrado');
  });
});
