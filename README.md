# Geo-Explorer

Projeto de aprendizado que gera **planos de estudo (trilhas), desafios de código e certificados fictícios** para **Java**, **Python** e **JavaScript**. A mesma base de dados é exposta por quatro interfaces:

| Interface | Tecnologia | Como acessar |
|-----------|------------|--------------|
| **API REST** | Spring Boot Web | `http://localhost:8080` |
| **Frontend web** | React + Vite + Tailwind CSS | `http://localhost:5173` |
| **CLI interativo** | Spring Shell | terminal com prompt `geo-explorer:>` |
| **Servidor MCP** | Spring AI (transport STDIO) | Claude Desktop, IBM Bob ou qualquer cliente MCP |

Os três recursos estão disponíveis em todas as interfaces:

- **Trilha** — plano de estudos completo de uma tecnologia, com módulos ordenados.
- **Desafio** — desafio de código sorteado conforme a tecnologia e o nível (`BEGINNER`, `INTERMEDIATE`, `ADVANCED`).
- **Certificado** — certificado fictício de conclusão de trilha para um usuário informado.

---

## Índice

1. [Visão Geral da Arquitetura](#visão-geral-da-arquitetura)
2. [Tecnologias](#tecnologias)
3. [Configuração do Ambiente](#configuração-do-ambiente)
   - [Pré-requisitos](#pré-requisitos)
   - [Opção A — Docker (recomendado)](#opção-a--docker-recomendado)
   - [Opção B — Execução local](#opção-b--execução-local)
4. [API REST](#api-rest)
5. [CLI Interativo](#cli-interativo)
6. [Integração MCP](#integração-mcp)
7. [Banco de Dados](#banco-de-dados)
8. [Testes e Qualidade](#testes-e-qualidade)
9. [Estrutura do Projeto](#estrutura-do-projeto)
10. [Segurança](#segurança)

---

## Visão Geral da Arquitetura

O backend é um único JAR Spring Boot que opera em **três modos**, selecionados por Spring Profiles. Isso resolve um conflito arquitetural: o Spring Shell e o servidor MCP STDIO disputam `System.in`/`System.out`, então cada modo roda isolado no seu próprio profile.

| Profile | Modo | O que sobe | O que fica desligado |
|---------|------|------------|----------------------|
| *(nenhum)* | API REST | Tomcat na porta 8080, pacote `controller` | Shell e MCP |
| `cli` | Shell interativo | Spring Shell, pacote `command`, console H2 | Pacote `mcp`, autoconfiguração MCP |
| `mcp` | Servidor MCP STDIO | Servidor MCP, pacote `mcp` | Tomcat, banner, Spring Shell; logs vão para `stderr` e o `stdout` fica exclusivo do JSON-RPC |

O frontend React consome a API REST pelo prefixo `/api`. Em desenvolvimento, o proxy do Vite encaminha as chamadas ao backend; em produção, o nginx faz o mesmo papel.

```
┌──────────────┐  /api/*   ┌─────────────────────────────────────┐
│   Frontend   │ ────────► │        Backend Spring Boot          │
│ React + Vite │           │                                     │
└──────────────┘           │  REST (default)  · porta 8080       │
┌──────────────┐           │  CLI  (profile cli)                 │
│ Cliente MCP  │ ◄─stdio─► │  MCP  (profile mcp)                 │
└──────────────┘           │                                     │
                           │  Services → Repositories JPA        │
                           │  H2 em memória + Flyway             │
                           └─────────────────────────────────────┘
```

---

## Tecnologias

**Backend**

| Tecnologia | Versão |
|------------|--------|
| Java | 21 |
| Spring Boot | 3.3.13 |
| Spring AI (BOM) — servidor MCP | 1.0.0 |
| Spring Shell | 3.4.0 |
| Spring Data JPA / Hibernate | gerenciado pelo Boot |
| H2 Database (em memória) | gerenciado pelo Boot |
| Flyway (migrações e seed) | gerenciado pelo Boot |
| JaCoCo (cobertura) | 0.8.12 |
| Maven | 3.9+ |

**Frontend**

| Tecnologia | Versão |
|------------|--------|
| Node.js | 22+ |
| React | 19 |
| Vite | 8 |
| TypeScript | ~6.0 |
| Tailwind CSS | 4 |
| TanStack React Query | 5 |
| React Router | 7 |
| Axios | 1.x |
| Oxlint (lint) | 1.x |

---

## Configuração do Ambiente

Esta seção descreve tudo que é necessário para rodar o projeto após clonar o repositório.

### Pré-requisitos

| Ferramenta | Versão mínima | Verificação |
|------------|---------------|-------------|
| JDK | 21 | `java -version` |
| Maven | 3.9 | `mvn -version` |
| Node.js + npm | 22 | `node -v` |
| Docker + Docker Compose | qualquer versão recente | `docker compose version` |

> O Docker substitui os quatro primeiros itens se você preferir não instalar nada no host.

Instalação das ferramentas locais no Debian/Ubuntu/Pop!\_OS:

```bash
sudo apt-get update
sudo apt-get install -y openjdk-21-jdk maven
# Node.js 22 (via NodeSource):
curl -fsSL https://deb.nodesource.com/setup_22.x | sudo -E bash -
sudo apt-get install -y nodejs
```

### Opção A — Docker (recomendado)

O `compose.yaml` orquestra todo o ecossistema: backend, frontend de desenvolvimento, frontend de produção, Maven utilitário, CLI e servidor MCP.

**1. Clone o repositório e prepare as variáveis de ambiente:**

```bash
git clone https://github.com/artifonni/projeto-bob-ibm-geo-explorer.git
cd projeto-bob-ibm-geo-explorer

cp .env.example .env
```

O arquivo `.env` (não versionado) aceita as seguintes variáveis:

| Variável | Padrão | Descrição |
|----------|--------|-----------|
| `FRONTEND_PORT` | `5173` | Porta do host para o Vite dev server |
| `BACKEND_PORT` | `8080` | Porta do host para a API REST |
| `FRONTEND_PROD_PORT` | `8088` | Porta do host para o nginx de produção |
| `UID` / `GID` | `1000` | Usuário do host — evita artefatos do Maven como root em `./target` (descubra os seus com `id -u` e `id -g`) |
| `TZ` | `America/Sao_Paulo` | Fuso horário dos containers |

**2. Construa as imagens e suba o ambiente de desenvolvimento:**

```bash
docker compose build
docker compose up -d
```

Isso inicia dois serviços com healthcheck:

- **Backend (API REST):** `http://localhost:8080`
- **Frontend (Vite com hot reload):** `http://localhost:5173` — o código em `./frontend` é montado como volume, então edições aparecem sem reiniciar

**3. Serviços opcionais (perfis do Compose):**

```bash
# Frontend de produção (build otimizado servido pelo nginx na :8088)
docker compose --profile prod up -d frontend-prod

# Build e testes do backend sem instalar Maven no host
docker compose --profile tools build backend-tools
docker compose --profile tools run --rm backend-tools mvn test

# CLI interativo
docker compose --profile cli run --rm backend-cli

# Servidor MCP STDIO
docker compose --profile mcp run --rm backend-mcp
```

Para parar tudo:

```bash
docker compose down
```

### Opção B — Execução local

**Backend:**

```bash
# Compilar e gerar o JAR (pula os testes)
mvn clean package -DskipTests

# Compilar e rodar toda a suíte de testes
mvn clean install
```

O JAR é gerado em `target/geo-explorer-1.0.0.jar`. Escolha o modo de execução:

```bash
# API REST (porta 8080)
java -jar target/geo-explorer-1.0.0.jar

# CLI interativo (Spring Shell)
java -jar target/geo-explorer-1.0.0.jar --spring.profiles.active=cli

# Servidor MCP STDIO (sem prompt; aguarda JSON-RPC no stdin)
java -jar target/geo-explorer-1.0.0.jar --spring.profiles.active=mcp
```

**Frontend (em outro terminal, com o backend de pé):**

```bash
cd frontend
npm install
npm run dev        # http://localhost:5173 — proxy /api → localhost:8080
```

Outros scripts disponíveis:

```bash
npm run build      # checagem de tipos (tsc -b) + build de produção em dist/
npm run preview    # serve o build de produção localmente
npm run lint       # oxlint
```

---

## API REST

No modo padrão (sem profile), o backend expõe três endpoints GET:

| Endpoint | Parâmetros | Retorno |
|----------|------------|---------|
| `GET /trail` | `technology` | Trilha completa com módulos ordenados |
| `GET /challenge` | `technology`, `level` | Desafio sorteado para tecnologia e nível |
| `GET /certificate` | `technology`, `user` | Certificado fictício (texto formatado) |

Valores válidos: `technology` = `java`, `python` ou `javascript`; `level` = `BEGINNER`, `INTERMEDIATE` ou `ADVANCED`.

Exemplos:

```bash
curl "http://localhost:8080/trail?technology=java"
curl "http://localhost:8080/challenge?technology=python&level=INTERMEDIATE"
curl "http://localhost:8080/certificate?technology=javascript&user=Ana+Lima"
```

Códigos de resposta: `200` em caso de sucesso, `404` quando a tecnologia não existe no seed e `400` para entrada inválida (por exemplo, nível desconhecido).

Em desenvolvimento, o CORS está liberado para a origem `http://localhost:5173` (ver `CorsConfig.java`). Tanto o proxy do Vite quanto o nginx de produção repassam as requisições `/api/*` ao backend removendo o prefixo `/api`.

---

## CLI Interativo

Inicie com o profile `cli` (localmente ou via `docker compose --profile cli run --rm backend-cli`):

```
geo-explorer:>
```

Use `help` para listar os comandos. Exemplos:

```
geo-explorer:> trail --technology java
geo-explorer:> challenge --technology python --level INTERMEDIATE
geo-explorer:> certificate --technology javascript --user "Ana Lima"
```

Saída esperada do comando `certificate`:

```
╔══════════════════════════════════════════════════════════════╗
║                  GEO-EXPLORER — CERTIFICADO                  ║
╠══════════════════════════════════════════════════════════════╣
║                                                              ║
║  Certificamos que                                            ║
║                                                              ║
║    Ana Lima                                                  ║
║                                                              ║
║  concluiu com êxito a trilha de estudos:                     ║
║                                                              ║
║    Javascript — Nível: BEGINNER                              ║
║                                                              ║
║  ...                                                         ║
╚══════════════════════════════════════════════════════════════╝
```

No profile `cli`, o console administrativo do H2 também fica disponível em `http://localhost:8080/h2-console` (ver [Banco de Dados](#banco-de-dados)).

---

## Integração MCP

Com o profile `mcp`, o projeto funciona como servidor MCP com transporte STDIO, expondo três tools:

| Tool | Descrição |
|------|-----------|
| `geo_trail` | Retorna o plano de estudos completo de uma tecnologia |
| `geo_challenge` | Gera um desafio de código para tecnologia e nível informados |
| `geo_certificate` | Emite um certificado fictício para o usuário informado |

Nesse modo, todo log vai para `stderr` e o `stdout` é reservado ao canal JSON-RPC — requisito dos clientes MCP.

### Configurar no Claude Desktop

Edite o arquivo de configuração (`~/Library/Application Support/Claude/claude_desktop_config.json` no macOS ou `%APPDATA%\Claude\claude_desktop_config.json` no Windows):

```json
{
  "mcpServers": {
    "geo-explorer": {
      "command": "java",
      "args": [
        "-jar",
        "/caminho/absoluto/para/geo-explorer-1.0.0.jar",
        "--spring.profiles.active=mcp"
      ]
    }
  }
}
```

### Configurar no IBM Bob

```json
{
  "mcpServers": {
    "geo-explorer": {
      "command": "java",
      "args": [
        "-jar",
        "/caminho/absoluto/para/geo-explorer-1.0.0.jar",
        "--spring.profiles.active=mcp"
      ],
      "transport": "stdio"
    }
  }
}
```

> Substitua `/caminho/absoluto/para/` pelo caminho real do JAR na sua máquina. Alternativamente, use o container Docker: `docker compose --profile mcp run --rm backend-mcp`.

---

## Banco de Dados

- **H2 em memória**, banco `geoexplorer` — os dados existem apenas enquanto o processo estiver de pé e são recriados a cada inicialização.
- O schema e o seed são criados pelas migrações Flyway:
  - `V1__init_schema_and_data.sql` — tabelas `TRAILS`, `MODULES` e `CHALLENGES` + carga inicial (3 tecnologias × 3 módulos × 3 desafios);
  - `V2__more_challenges.sql` — desafios adicionais por trilha e nível.

### Console H2 (apenas no profile `cli`)

1. Com o profile `cli` rodando, abra `http://localhost:8080/h2-console`.
2. No campo **JDBC URL**, substitua o valor padrão por: `jdbc:h2:mem:geoexplorer`
3. **User Name:** `sa` — **Password:** deixe em branco.
4. Clique em **Connect**.

---

## Testes e Qualidade

A suíte possui **77 testes** entre unitários (Mockito), de persistência (`@DataJpaTest` + Flyway) e de integração dos perfis `cli`, `mcp` e REST.

```bash
# Todos os testes (ao final, imprime resumo de cobertura por área)
mvn test

# Apenas testes unitários de services e tools MCP
mvn test -Dtest="TrailServiceTest,ChallengeServiceTest,CertificateServiceTest,GeoExplorerToolsTest"

# Testes de persistência e de entidades
mvn test -Dtest="TrailRepositoryTest,EntityModelTest"

# Testes de integração da API REST e do profile cli
mvn test -Dtest="GeoExplorerControllerTest,TrailCommandTest,ChallengeCommandTest,CertificateCommandTest"

# Teste de fumaça do profile mcp
mvn test -Dtest="McpToolsConfigTest"

# Build completo com gate de cobertura do JaCoCo
mvn clean verify
```

O `mvn verify` falha se a cobertura ficar abaixo dos limites configurados no `pom.xml`:

| Métrica | Mínimo exigido |
|---------|----------------|
| Linhas cobertas | 90% |
| Branches cobertas | 80% |
| Métodos cobertos | 85% |

No frontend, o lint roda com `npm run lint` (oxlint) e o build já inclui a checagem de tipos do TypeScript.

---

## Estrutura do Projeto

```
geo-explorer/
├── pom.xml                          # Maven — Spring Boot 3.3.13 + Spring AI BOM 1.0.0
├── compose.yaml                     # Orquestração Docker (perfis dev, prod, tools, cli, mcp)
├── Dockerfile                       # Backend multi-stage (build Maven → runtime JRE, não-root)
├── Dockerfile.tools                 # Maven utilitário para build/testes via container
├── .env.example                     # Modelo de variáveis de ambiente (copiar para .env)
├── geo-explorer-backend-plan.md     # Plano original de desenvolvimento do backend
├── src/
│   ├── main/
│   │   ├── java/com/geoexplorer/
│   │   │   ├── GeoExplorerApplication.java   # Classe main
│   │   │   ├── command/                      # @Profile("cli") — comandos Spring Shell
│   │   │   │   ├── TrailCommand.java
│   │   │   │   ├── ChallengeCommand.java
│   │   │   │   └── CertificateCommand.java
│   │   │   ├── common/AppConstants.java      # Constantes compartilhadas
│   │   │   ├── config/CorsConfig.java        # CORS para o frontend em desenvolvimento
│   │   │   ├── controller/
│   │   │   │   └── GeoExplorerController.java # Endpoints REST /trail, /challenge, /certificate
│   │   │   ├── domain/
│   │   │   │   ├── model/                    # Trail, Module, Challenge, Level (enum)
│   │   │   │   ├── repository/               # TrailRepository, ChallengeRepository
│   │   │   │   └── dto/                      # Records TrailDTO, ModuleDTO, ChallengeDTO
│   │   │   ├── exception/                    # Hierarquia a partir de GeoExplorerException
│   │   │   ├── mcp/                          # @Profile("mcp") — tools MCP (@Tool)
│   │   │   │   ├── GeoExplorerTools.java
│   │   │   │   └── McpToolsConfig.java
│   │   │   └── service/                      # Regras de negócio compartilhadas
│   │   │       ├── TrailService.java
│   │   │       ├── ChallengeService.java
│   │   │       └── CertificateService.java
│   │   └── resources/
│   │       ├── application.yml               # Base compartilhada (H2, JPA, Flyway)
│   │       ├── application-cli.yml           # Sobrescritas do profile cli
│   │       ├── application-mcp.yml           # Sobrescritas do profile mcp
│   │       ├── logback-spring.xml            # Logs → stderr no profile mcp
│   │       └── db/migration/                 # Migrações Flyway (schema + seed)
│   └── test/java/com/geoexplorer/            # Unitários, persistência e integração
└── frontend/
    ├── package.json                 # Scripts: dev, build, lint, preview
    ├── vite.config.ts               # Proxy /api → backend (VITE_PROXY_TARGET)
    ├── Dockerfile                   # Multi-stage: deps → dev (hot reload) | build → prod (nginx)
    ├── nginx.conf                   # Produção: serve dist/ e repassa /api ao backend
    ├── public/                      # favicon.svg, icons.svg
    └── src/
        ├── main.tsx                 # Entry React + React Query
        ├── App.tsx                  # Rotas: /, /challenge, /certificate
        ├── index.css                # Tema Tailwind
        ├── components/
        │   ├── ErrorBoundary.tsx
        │   ├── layout/AppLayout.tsx # Sidebar, navegação e rodapé
        │   └── ui/                  # Button, Card, Skeleton
        ├── pages/
        │   ├── Dashboard.tsx        # Trilhas + modal de detalhes
        │   ├── Challenge.tsx        # Gerador de desafio de código
        │   └── Certificate.tsx      # Emissão de certificado
        └── services/
            ├── api.ts               # Cliente axios com interceptores
            └── geoExplorer.ts       # Tipos DTO + chamadas à API
```

---

## Segurança

> **Regra de ouro:** nunca versionar credenciais, senhas ou chaves de API.

- O banco H2 é **em memória** — não há senha real nem arquivo persistido.
- Nenhuma chave de API é necessária para rodar o projeto.
- O `.gitignore` bloqueia `.env`, `application-local.yml`, keystores (`*.jks`, `*.p12`, `*.pem`) e arquivos com `*secret*`, `*credential*` ou `*password*` no nome. Apenas o `.env.example` é versionado.
- Para sobrescrever configurações localmente sem commitar, use `application-local.yml` (ignorado pelo Git) ou variáveis de ambiente:

```bash
export SPRING_DATASOURCE_PASSWORD=sua_senha_local
java -jar target/geo-explorer-1.0.0.jar --spring.profiles.active=cli
```
