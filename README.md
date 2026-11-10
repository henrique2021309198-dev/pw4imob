# Aula 1 — Backend Java e front-end Angular

Aplicação de imóveis com API Spring Boot, autenticação JWT e uma tela Angular que consulta `GET /imoveis`.

## Requisitos

- Java 25 (LTS)
- Node.js LTS e npm
- Maven (ou o Maven Wrapper incluído)

## Executar localmente

1. Inicie o backend com o perfil de desenvolvimento, que usa H2 em memória:

   ```powershell
   .\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
   ```

   A API ficará disponível em `http://localhost:8080`. A listagem pública pode ser conferida em `http://localhost:8080/imoveis`.

2. Em outro terminal, instale as dependências e inicie o Angular:

   ```powershell
   Set-Location .\frontend
   npm install
   npm start
   ```

   Abra `http://localhost:4200`. A tela carrega os imóveis diretamente da API.

## Testes

Na raiz, execute `.\mvnw.cmd test`. No diretório `frontend`, execute `npm test -- --watch=false` e `npm run build`.

Os testes do backend usam H2 e cobrem a listagem pública, a proteção de operações de escrita, a proteção dos dados de usuários, a criação de contas sem elevação de privilégio e o CORS do Angular local.

## Segurança e configuração

- A consulta de imóveis é pública; criação, alteração e remoção exigem autenticação JWT.
- O cadastro público sempre cria usuários com o papel `USER`; o papel enviado pelo cliente é ignorado.
- CORS permite apenas `http://localhost:4200` e `http://127.0.0.1:4200` para desenvolvimento local.
- Para executar sem o perfil `dev`, configure `JWT_SECRET` com pelo menos 32 caracteres e as variáveis de banco de dados.
- Credenciais de banco e e-mail, assim como a chave Mailgun, são lidas de variáveis de ambiente e não devem ser incluídas no repositório.

O perfil `dev` e o segredo nele definido são exclusivos para desenvolvimento local; não os use em produção.
