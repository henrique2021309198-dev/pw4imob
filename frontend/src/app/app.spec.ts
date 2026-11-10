import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { App } from './app';

describe('App', () => {
  let httpTesting: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    })
      .compileComponents();
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('loads property listings from the backend API', () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
    const request = httpTesting.expectOne('http://localhost:8080/imoveis');
    expect(request.request.method).toBe('GET');
    request.flush([]);
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h1')?.textContent).toContain('Imóveis para você');
    expect(compiled.textContent).toContain('Nenhum imóvel cadastrado ainda');
  });
});
