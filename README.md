# SugestFilmes-Front-JSP

Frontend web do SugestFilmes reestruturado em **Java + Jakarta Servlets + JSP**, seguindo MVC e consumindo a aplicação `SugestFilmes-API` por HTTP/JSON. Este projeto não possui DAO SQL, driver MySQL, banco de dados ou regras de persistência.

## Arquitetura MVC

```text
src/main/java/br/com/sugestfilmes/
├── api/             # Cliente HTTP reutilizável e tratamento de erros da API
├── config/          # Configuração da URL externa da API
├── controller/      # Servlets: controllers/rotas web
├── filter/          # Autenticação por sessão e proteção CSRF
├── model/           # Modelos que representam os dados recebidos da API
└── service/         # Casos de uso da interface; encaminham operações à API

src/main/webapp/
├── WEB-INF/
│   ├── jsp/         # Views JSP, organizadas por funcionalidade
│   └── web.xml      # Configuração web e sessão
└── css/             # Estilos reaproveitados do projeto original
```

**Fluxo:** Navegador → Servlet Controller → Service → `ApiClient` → `SugestFilmes-API` → Controller/Service/DAO da API. O Servlet prepara atributos da requisição e encaminha para a JSP correspondente.

## Páginas e funcionalidades

- Login e logout.
- Home com categorias e filmes em destaque.
- Listagem, pesquisa, detalhes, cadastro, edição e exclusão de filmes (cadastro/edição/exclusão somente para Administrador).
- Consulta e gerenciamento de categorias (alterações somente para Administrador).
- Recomendações por uma ou mais categorias.
- Consulta das próprias avaliações, consulta das avaliações de um filme e registro/remoção de avaliação.
- Administração de usuários e perfis (somente Administrador).

## Comunicação e autenticação

O frontend usa `java.net.http.HttpClient` no servidor para chamar os endpoints JSON da API. O JWT recebido em `POST /api/auth/login` é guardado na `HttpSession` do Tomcat e enviado nas chamadas seguintes pelo cabeçalho `Authorization: Bearer ...`.

O token **não é exposto ao JavaScript nem armazenado no navegador**. O navegador mantém apenas o cookie de sessão HTTP-only do frontend. Como as chamadas à API são servidor-servidor, não existe chamada AJAX cross-origin e CORS não é necessário para esta arquitetura.

O nome do perfil do usuário no JWT/API é comparado com `Administrador` para liberar as telas administrativas. A API continua sendo a autoridade final das permissões.

## Executar com Docker

Pré-requisitos: Docker Engine e Docker Compose; a API deve estar ativa e publicada no host na porta `8081`.

Na pasta deste projeto:

```bash
docker compose up --build -d
```

Acesse: <http://localhost:8080>

Este `docker-compose.yml` sobe **somente o Tomcat do frontend**. Não inicia MySQL nem a API.

### URL da API

Dentro do container frontend, `localhost:8081` apontaria para o próprio container. Por isso, o Compose define:

```text
API_BASE_URL=http://host.docker.internal:8081/api
```

O mapeamento `host.docker.internal:host-gateway` permite que o Tomcat do frontend acesse a porta 8081 publicada no host. Se estiver usando uma rede Docker compartilhada entre frontend e API, configure `API_BASE_URL` com o nome DNS do serviço da API, por exemplo `http://sugestfilmes-api:8080/api`.

Ao executar pelo IDE/Maven diretamente no computador (fora do container), configure `API_BASE_URL=http://localhost:8081/api`.

A URL é lida pela classe `ApiConfig`; não é preciso editar páginas JSP ou arquivos Java para mudar o endereço.

## Endpoints consumidos

| Método | Endpoint | Uso no frontend |
|---|---|---|
| POST | `/api/auth/login` | Autenticação e obtenção do JWT |
| GET | `/api/filmes?q=termo` | Listar/pesquisar filmes |
| GET | `/api/filmes/{id}` | Detalhes e formulário de edição |
| POST/PUT/DELETE | `/api/filmes[/{id}]` | CRUD de filmes (admin) |
| GET | `/api/categorias` | Listar categorias |
| GET | `/api/categorias/{id}` | Consultar categoria |
| POST/PUT/DELETE | `/api/categorias[/{id}]` | CRUD de categorias (admin) |
| GET | `/api/recomendacoes?categorias=1,2` | Recomendações |
| GET | `/api/avaliacoes` | Avaliações do usuário autenticado |
| GET | `/api/avaliacoes?filmeId={id}` | Avaliações de um filme |
| POST | `/api/avaliacoes` | Criar/atualizar avaliação própria via upsert |
| PUT/DELETE | `/api/avaliacoes/{id}` | Atualizar/excluir avaliação |
| GET/POST | `/api/usuarios` | Listar/cadastrar usuários (admin) |
| GET/PUT/DELETE | `/api/usuarios/{id}` | Consultar/alterar/excluir usuário (admin) |
| GET | `/api/perfis` | Listar perfis |
| POST/PUT/DELETE | `/api/perfis[/{id}]` | CRUD de perfis (admin) |

## Segurança aplicada no frontend

- JWT guardado na sessão do servidor, nunca em localStorage/sessionStorage.
- Cookie de sessão configurado como HTTP-only.
- Sessão com tempo limite de 30 minutos e regeneração do identificador após login.
- Filtro de autenticação para proteger rotas internas.
- Token CSRF para formulários POST.
- O frontend não conecta diretamente ao MySQL nem possui credenciais de banco.

## Observação sobre usuários

A API atual exige uma senha (mínimo de 6 caracteres) também na operação de atualização de usuários. Por isso, no formulário de edição é necessário informar uma nova senha. A API não possui, no momento, operação separada de alteração de dados sem troca de senha.

## Build sem Docker

Com JDK 17 e Maven 3.9+:

```bash
mvn clean package
```

O WAR será gerado em `target/SugestFilmes-Front-JSP.war`. Configure a variável de ambiente `API_BASE_URL` para `http://localhost:8081/api` e publique o WAR em Tomcat 10.1.

## Verificação de disponibilidade da API

O `SecurityFilter` verifica `GET /api/health` antes de liberar cada rota autenticada. Se a API não responder com HTTP 200, a sessão local do frontend é invalidada, uma mensagem é exibida na tela de login e o usuário precisa autenticar novamente quando o backend voltar. As páginas autenticadas também recebem cabeçalhos `Cache-Control: no-store` para evitar a reutilização de conteúdo protegido pelo cache do navegador.

O endpoint `/api/health` já existe na API e é público no `JwtFilter`; não é necessário alterar o backend para esse comportamento.

### Codificação de caracteres (UTF-8)

O `SecurityFilter` define a codificação UTF-8 da requisição e da resposta antes do encaminhamento para as JSPs. O fragmento `header.jspf` também declara `pageEncoding="UTF-8"`. Após atualizar o projeto, reconstrua a imagem do frontend sem cache para garantir que o Tomcat publique o WAR atualizado.

```bash
docker compose build --no-cache front
docker compose up -d --force-recreate front
```
