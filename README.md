# Aula 2 — Vitrine e área do cliente

Aplicação imobiliária com API Spring Boot, autenticação JWT, vitrine pública e interface Angular.

## Requisitos

- Java 25 (LTS)
- Node.js LTS e npm
- Maven (ou o Maven Wrapper incluído)

## Executar localmente

1. Configure a conexão com o MySQL usado pelo phpMyAdmin. Copie `.env.example` para `.env` e ajuste as credenciais se necessário. O `.env` é local e não deve ser enviado ao Git.

   ```powershell
   Copy-Item .env.example .env
   ```

2. Inicie o backend pela configuração `Spring Boot-DemoApplication<demo>` no VS Code. Ela lê as variáveis do arquivo `.env` por meio de `envFile`.

   A API ficará disponível em `http://localhost:8080`. Se iniciar pelo terminal com `.\mvnw.cmd spring-boot:run`, configure antes as mesmas variáveis no ambiente desse terminal. Para desenvolvimento sem MySQL, o perfil opcional `dev` usa H2 em memória.

3. Em outro terminal, instale as dependências e inicie o Angular:

   ```powershell
   Set-Location .\frontend
   npm install
   npm start
   ```

   Abra `http://localhost:4200`. A tela carrega os imóveis diretamente da API.

## Testes

Na raiz, execute `.\mvnw.cmd test`. No diretório `frontend`, execute `npm test -- --watch=false` e `npm run build`.

Os testes do backend usam H2 e cobrem a busca pública paginada, os filtros da vitrine, as permissões por perfil, o contato de imóveis, o cadastro de contas sem elevação de privilégio e o CORS local.

## Segurança e configuração

- `GET /imoveis` preserva a resposta em lista. A vitrine usa `GET /imoveis/pagina` com `page`, `size`, `finalidade`, `tipoImovelId`, `bairroId`, `precoMin`, `precoMax`, `sort` e `direction`.
- Detalhes usam `GET /imoveis/{id}`; opções de filtro usam `GET /imoveis/finalidades`, `/bairros` e `/tipos-imoveis`.
- Contato público usa `POST /imoveis/sabermais/{id}`. Fotos de um imóvel usam `GET /fotos-imoveis/imovel/{id}` e são servidas em `/uploads/...`.
- Operações de cadastro exigem perfil ADMIN e JWT; a consulta da vitrine e o contato são públicos.
- O cadastro público sempre cria usuários com o papel `USER`; o papel enviado pelo cliente é ignorado.
- O envio do formulário de contato depende de SMTP configurado em `MAIL_HOST`, `MAIL_USERNAME` e `MAIL_PASSWORD`.
- CORS permite apenas `http://localhost:4200` e `http://127.0.0.1:4200` para desenvolvimento local.
- Configure `JWT_SECRET` com pelo menos 32 caracteres e `DATABASE_URL`, `DATABASE_USERNAME` e `DATABASE_PASSWORD` no `.env`.
- Credenciais de banco e e-mail, assim como a chave Mailgun, são lidas de variáveis de ambiente e não devem ser incluídas no repositório.

O segredo de exemplo é exclusivo para desenvolvimento local; troque-o por um segredo forte antes de qualquer implantação.
