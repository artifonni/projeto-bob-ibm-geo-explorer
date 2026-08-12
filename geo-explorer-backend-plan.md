# Geo-Explorer — Plano de Backend Spring Boot

## Visão Geral

Construir um projeto Spring Boot (Java 21 / Spring Boot 3.3.x / Maven) que expõe três
comandos — **Trilha**, **Desafio** e **Certificado** — a partir de uma base de trilhas
fictícias persistida no H2 em memória via Spring Data JPA.

### Conflito Arquitetural Resolvido: CLI vs MCP

Spring Shell e o servidor MCP STDIO disputam `System.in` / `System.out`. A solução
adotada é a separação por **Spring Profiles**:

| Profile | O que sobe | O que fica desligado |
|---------|-----------|----------------------|
| `cli`   | Spring Shell interativo, pacote `command` | Pacote `mcp`, autoconfiguração MCP |
| `mcp`   | Servidor MCP STDIO, pacote `mcp` | Spring Shell interativo, banner, logs no stdout |

Cada perfil é ativado na linha de comando via `--spring.profiles.active=<perfil>`.

---

## Estrutura de Arquivos de Configuração

```
src/main/resources/
├── application.yml          # Base compartilhada (datasource H2, JPA, sem segredos)
├── application-cli.yml      # Sobrescritas do profile cli
├── application-mcp.yml      # Sobrescritas do profile mcp
├── logback-spring.xml       # Redireciona logs para stderr no profile mcp
└── data/
    └── trails-seed.json
```

## Estrutura de Pacotes Alvo

```
geo-explorer/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/geoexplorer/
    │   │   ├── GeoExplorerApplication.java
    │   │   ├── domain/
    │   │   │   ├── model/
    │   │   │   │   ├── Trail.java
    │   │   │   │   ├── Module.java
    │   │   │   │   └── Challenge.java
    │   │   │   └── repository/
    │   │   │       ├── TrailRepository.java
    │   │   │       ├── ModuleRepository.java
    │   │   │       └── ChallengeRepository.java
    │   │   ├── command/                        # @Profile("cli")
    │   │   │   ├── TrailCommand.java
    │   │   │   ├── ChallengeCommand.java
    │   │   │   └── CertificateCommand.java
    │   │   ├── service/                        # Sem @Profile — compartilhado
    │   │   │   ├── TrailService.java
    │   │   │   ├── ChallengeService.java
    │   │   │   └── CertificateService.java
    │   │   ├── mcp/                            # @Profile("mcp")
    │   │   │   └── McpToolsConfig.java         # Classe única — registra as 3 tools via @Tool
    │   │   └── config/
    │   │       └── DataInitializer.java        # Sem @Profile — compartilhado
    │   └── resources/
    │       ├── application.yml
    │       ├── application-cli.yml
    │       ├── application-mcp.yml
    │       ├── logback-spring.xml
    │       └── data/
    │           └── trails-seed.json
    └── test/
        └── java/com/geoexplorer/
            ├── command/
            │   ├── TrailCommandTest.java
            │   ├── ChallengeCommandTest.java
            │   └── CertificateCommandTest.java
            ├── service/
            │   ├── TrailServiceTest.java
            │   ├── ChallengeServiceTest.java
            │   └── CertificateServiceTest.java
            └── mcp/
                └── McpToolsConfigTest.java
```

---

## Conteúdo dos Arquivos de Configuração

### `application.yml` — Base Compartilhada

```yaml
spring:
  application:
    name: geo-explorer
  datasource:
    url: jdbc:h2:mem:geoexplorer;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
    username: sa
    password: ""          # H2 em memória — sem segredo real
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: false
  h2:
    console:
      enabled: false

server:
  port: 8080
```

### `application-cli.yml` — Profile CLI

```yaml
spring:
  h2:
    console:
      enabled: true       # útil para inspeção manual no modo CLI
  shell:
    interactive:
      enabled: true
```

### `application-mcp.yml` — Profile MCP

```yaml
# CRÍTICO: qualquer saída não-JSON no stdout quebra o cliente MCP (Claude Desktop, Bob)
spring:
  main:
    web-application-type: none   # Não sobe Tomcat — stdio não precisa de HTTP
    banner-mode: off             # Desliga o ASCII do Spring no stdout
  shell:
    interactive:
      enabled: false             # Spring Shell desligado — stdio pertence ao MCP

logging:
  level:
    root: INFO
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
  # Todos os logs vão para stderr via logback-spring.xml

mcp:
  server:
    enabled: true
    transport: stdio
    name: geo-explorer-mcp
    version: "1.0.0"
```

### `logback-spring.xml` — Redirecionamento de Logs para STDERR no Profile MCP

```xml
<configuration>
  <!-- Profile padrão: stdout normal -->
  <springProfile name="!mcp">
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
      <encoder>
        <pattern>%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n</pattern>
      </encoder>
    </appender>
    <root level="INFO">
      <appender-ref ref="CONSOLE"/>
    </root>
  </springProfile>

  <!-- Profile mcp: STDERR para não poluir o canal JSON-RPC do stdout -->
  <springProfile name="mcp">
    <appender name="STDERR" class="ch.qos.logback.core.ConsoleAppender">
      <target>System.err</target>
      <encoder>
        <pattern>%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n</pattern>
      </encoder>
    </appender>
    <root level="INFO">
      <appender-ref ref="STDERR"/>
    </root>
  </springProfile>
</configuration>
```

---

## Estrutura do `pom.xml` — Pontos-Chave

### BOM do Spring AI (obrigatório em `<dependencyManagement>`)

O Spring AI é modular e atualizado com frequência. Importar o BOM antes de declarar
qualquer dependência do Spring AI evita conflitos de versão entre o core e o módulo MCP:

```xml
<dependencyManagement>
  <dependencies>
    <!-- BOM do Spring Boot (já herdado do parent, listado para clareza) -->
    <!-- BOM do Spring AI — DEVE vir antes das dependências individuais -->
    <dependency>
      <groupId>org.springframework.ai</groupId>
      <artifactId>spring-ai-bom</artifactId>
      <version>${spring-ai.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>

<dependencies>
  <!-- Spring AI MCP Server Starter — versão gerenciada pelo BOM acima -->
  <dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-mcp-server-spring-boot-starter</artifactId>
  </dependency>
  <!-- demais dependências... -->
</dependencies>

<properties>
  <java.version>21</java.version>
  <spring-ai.version>1.0.0</spring-ai.version> <!-- confirmar latest no Maven Central -->
</properties>
```

> **Atenção:** Antes de codar a Sub-Tarefa 1, verificar a versão mais recente do
> `spring-ai-bom` disponível em [Maven Central](https://central.sonatype.com/artifact/org.springframework.ai/spring-ai-bom).

---

## Anotações de Profile por Classe

| Classe | Anotação |
|--------|----------|
| `TrailCommand` | `@Profile("cli")` |
| `ChallengeCommand` | `@Profile("cli")` |
| `CertificateCommand` | `@Profile("cli")` |
| `McpToolsConfig` | `@Profile("mcp")` |
| `TrailService` | *(sem @Profile)* |
| `ChallengeService` | *(sem @Profile)* |
| `CertificateService` | *(sem @Profile)* |
| `DataInitializer` | *(sem @Profile)* |

---

## Registro de Tools MCP — Abordagem Simplificada

Uma única classe `McpToolsConfig` com `@Tool` do Spring AI substitui as classes
`TrailTool`, `ChallengeTool`, `CertificateTool` e `McpToolHandler` separadas.
A autoconfiguração do Spring AI:

1. Detecta métodos anotados com `@Tool`.
2. Gera o JSON Schema automaticamente a partir da assinatura do método.
3. Registra os callbacks no servidor MCP sem boilerplate manual.

```java
// Esboço conceitual — não é código final
@Configuration
@Profile("mcp")
public class McpToolsConfig {

    @Tool(description = "Retorna o plano de estudos de uma tecnologia")
    public List<ModuleDto> geoTrail(String technology) { ... }

    @Tool(description = "Gera um desafio de código para uma tecnologia e nível")
    public ChallengeDto geoChallenge(String technology, String level) { ... }

    @Tool(description = "Cria um certificado fictício para uma trilha concluída")
    public String geoCertificate(String technology, String userName) { ... }
}
```

---

## Comandos de Execução (Zsh / Debian)

### Build

```zsh
# Na raiz do projeto
mvn clean package -DskipTests
```

### Executar no Profile CLI (Spring Shell interativo)

```zsh
java -jar target/geo-explorer-1.0.0.jar --spring.profiles.active=cli
```

### Executar no Profile MCP (servidor STDIO — uso com Claude Desktop / Bob)

```zsh
java -jar target/geo-explorer-1.0.0.jar --spring.profiles.active=mcp
```

### Executar testes isolados por profile

```zsh
# Testes do profile cli
mvn test -Dspring.profiles.active=cli

# Testes do profile mcp
mvn test -Dspring.profiles.active=mcp
```

---

## Sub-Tarefas

---

### Sub-Tarefa 1 — Scaffold do Projeto Maven / Spring Boot

**Status:** `[x] done`

**Intent**
Criar a fundação do projeto: `pom.xml` com BOM do Spring AI, classe `main`, os três
arquivos `application*.yml` e o `logback-spring.xml` com stderr para o profile MCP —
tudo sem segredos hardcoded.

**Expected Outcomes**
- `pom.xml` compilável com Java 21, Spring Boot 3.3.x, BOM do Spring AI em
  `<dependencyManagement>`, e dependências: Spring Data JPA, H2, Spring Shell,
  `spring-ai-mcp-server-spring-boot-starter`, Spring Test.
- `GeoExplorerApplication.java` inicializa o contexto sem erros em ambos os profiles.
- `application.yml` (base), `application-cli.yml` e `application-mcp.yml` criados
  conforme a seção "Conteúdo dos Arquivos de Configuração".
- `logback-spring.xml` com bloco `<springProfile name="mcp">` usando
  `<target>System.err</target>`.
- `mvn clean install -DskipTests` passa sem erros.

**Todo List**
1. Verificar a versão mais recente do `spring-ai-bom` no Maven Central.
2. Criar `pom.xml` com:
   - Parent `spring-boot-starter-parent 3.3.x`.
   - Propriedade `<spring-ai.version>` na seção `<properties>`.
   - BOM do Spring AI em `<dependencyManagement>`.
   - Dependências: `spring-boot-starter-data-jpa`, `spring-boot-starter-web`,
     `spring-shell-starter`, `h2`, `spring-ai-mcp-server-spring-boot-starter`,
     `spring-boot-starter-test`.
3. Criar `GeoExplorerApplication.java` no pacote `com.geoexplorer`.
4. Criar `application.yml` (base compartilhada — sem segredos).
5. Criar `application-cli.yml` (shell interativo ligado, console H2 habilitado).
6. Criar `application-mcp.yml` com `banner-mode: off`, `web-application-type: none`,
   `shell.interactive.enabled: false`.
7. Criar `logback-spring.xml` em `src/main/resources/` com os dois blocos
   `<springProfile>` conforme a seção "Conteúdo dos Arquivos de Configuração".
8. Executar `mvn clean install -DskipTests` e confirmar build verde.

**Relevant Context**
- `application-local.yml` está no `.gitignore` — nunca criar com segredos.
- `spring.main.web-application-type=none` libera `System.in`/`System.out` para STDIO.
- O BOM em `<dependencyManagement>` garante que as versões do core do Spring AI e do
  módulo MCP não entrem em conflito — ponto crítico para builds estáveis.

---

### Sub-Tarefa 2 — Camada de Domínio: Entidades JPA e Repositórios

**Status:** `[x] done`

**Intent**
Modelar os dados das trilhas fictícias com entidades JPA e expor acesso via repositórios
Spring Data. Camada compartilhada entre os dois profiles.

**Expected Outcomes**
- Entidades `Trail`, `Module` e `Challenge` mapeadas com `@Entity`.
- Relacionamentos: `Trail` 1→N `Module`; `Trail` 1→N `Challenge`.
- Repositórios com métodos de busca por `technology` e `level`.
- `DataInitializer` popula o banco ao subir — funciona nos dois profiles.

**Todo List**
1. Criar enum `Level` (BEGINNER, INTERMEDIATE, ADVANCED) em `domain/model/`.
2. Criar `Trail.java` com campos: `id`, `technology`, `description`, `level`.
3. Criar `Module.java` com campos: `id`, `title`, `content`, `order`,
   relação `@ManyToOne Trail`.
4. Criar `Challenge.java` com campos: `id`, `title`, `description`, `level`,
   relação `@ManyToOne Trail`.
5. Criar `TrailRepository`, `ModuleRepository`, `ChallengeRepository`
   estendendo `JpaRepository`.
6. Criar `DataInitializer.java` (`@Component` + `CommandLineRunner`, sem `@Profile`)
   que carrega `trails-seed.json` e salva via repositórios.
7. Criar `trails-seed.json` com 3 tecnologias × 3 módulos × 2 desafios por nível.

**Relevant Context**
- Pacote: `com.geoexplorer.domain`.
- `DataInitializer` não recebe `@Profile` — o banco H2 é necessário nos dois modos.

---

### Sub-Tarefa 3 — Camada de Serviço

**Status:** `[x] done`

**Intent**
Implementar a lógica dos três comandos em serviços Spring sem `@Profile`, reutilizados
tanto pelo CLI quanto pelo MCP.

**Expected Outcomes**
- `TrailService.getTrail(technology)` retorna lista de módulos.
- `ChallengeService.getChallenge(technology, level)` retorna desafio aleatório.
- `CertificateService.generateCertificate(technology, userName)` retorna string formatada.
- `ResourceNotFoundException` lançada quando tecnologia/nível não existe.

**Todo List**
1. Criar `TrailService.java` — método `getTrail(String technology)`.
2. Criar `ChallengeService.java` — método `getChallenge(String technology, String level)`.
3. Criar `CertificateService.java` — método `generateCertificate(String technology, String userName)`.
4. Criar `exception/ResourceNotFoundException.java` (extends `RuntimeException`).

**Relevant Context**
- Pacote: `com.geoexplorer.service`.
- Sem `@Profile` — beans sobem em qualquer profile ativo.

---

### Sub-Tarefa 4 — Pacote `command`: Spring Shell com `@Profile("cli")`

**Status:** `[x] done`

**Intent**
Expor os três serviços como comandos interativos via Spring Shell, ativos somente no
profile `cli`.

**Expected Outcomes**
- `trail --technology java` imprime os módulos da trilha Java.
- `challenge --technology python --level BEGINNER` imprime um desafio aleatório.
- `certificate --technology javascript --user "Ana Lima"` imprime o certificado.
- No profile `mcp`, nenhuma dessas classes é carregada no contexto Spring.

**Todo List**
1. Criar `TrailCommand.java` com `@ShellComponent` + `@Profile("cli")`.
2. Criar `ChallengeCommand.java` com `@ShellComponent` + `@Profile("cli")`.
3. Criar `CertificateCommand.java` com `@ShellComponent` + `@Profile("cli")`.
4. Injetar os respectivos serviços em cada comando via construtor.
5. Tratar `ResourceNotFoundException` e exibir mensagem amigável no shell.

**Relevant Context**
- Pacote: `com.geoexplorer.command`.
- Spring Shell 3.x: `@ShellComponent`, `@ShellMethod`, `@ShellOption`.

---

### Sub-Tarefa 5 — Pacote `mcp`: Servidor MCP com `@Profile("mcp")`

**Status:** `[x] done`

**Intent**
Expor os três serviços como tools MCP via transport STDIO usando a abordagem simplificada
de classe única com `@Tool`, ativa somente no profile `mcp`.

**Expected Outcomes**
- `McpToolsConfig.java` registra as três tools (`geo_trail`, `geo_challenge`,
  `geo_certificate`) via `@Tool` do Spring AI.
- JSON Schema gerado automaticamente pela autoconfiguração do Spring AI MCP.
- No profile `cli`, a classe não é carregada.
- `java -jar ... --spring.profiles.active=mcp` integra com Claude Desktop / Bob.

**Todo List**
1. Confirmar artifact e versão do `spring-ai-mcp-server-spring-boot-starter`
   alinhada ao BOM declarado no `pom.xml` (ver Sub-Tarefa 1).
2. Criar `mcp/McpToolsConfig.java` com `@Configuration` + `@Profile("mcp")`.
3. Anotar os três métodos com `@Tool` fornecendo `description` clara.
4. Cada método delega para o respectivo `Service` injetado via construtor.
5. Confirmar que `stdout` está limpo no profile `mcp`:
   banner off ✓, logs em stderr ✓, web-application-type=none ✓.

**Relevant Context**
- Pacote: `com.geoexplorer.mcp`.
- `@Tool` é do pacote `org.springframework.ai.tool.annotation`.
- A autoconfiguração do Spring AI detecta beans com `@Tool` e os registra no servidor MCP.
- Referência: [Spring AI MCP Server Boot Starter Docs](https://docs.spring.io/spring-ai/reference/api/mcp/mcp-server-boot-starter-docs.html).

---

### Sub-Tarefa 6 — Testes Automatizados

**Status:** `[x] done`

**Intent**
Cobrir serviços, comandos e tools MCP com testes unitários e de integração, validando
isolamento de profiles.

**Expected Outcomes**
- Testes unitários dos três serviços com Mockito.
- Testes de integração dos comandos com `@ActiveProfiles("cli")`.
- Teste de fumaça das tools MCP com `@ActiveProfiles("mcp")`.
- `mvn test` passa com 100% de sucesso.

**Todo List**
1. Criar `TrailServiceTest.java`, `ChallengeServiceTest.java`, `CertificateServiceTest.java`
   com `@ExtendWith(MockitoExtension.class)` — sem Spring context.
2. Criar `TrailCommandTest.java`, `ChallengeCommandTest.java`, `CertificateCommandTest.java`
   com `@SpringBootTest` + `@ActiveProfiles("cli")`.
3. Criar `McpToolsConfigTest.java` com `@SpringBootTest` + `@ActiveProfiles("mcp")`
   verificando que as três tools são registradas como beans.
4. Executar `mvn test` e corrigir falhas antes de marcar concluído.

**Relevant Context**
- Usar `@ActiveProfiles` nos testes de integração para garantir isolamento.
- Testes do profile `mcp` herdam `spring.main.web-application-type=none` do `application-mcp.yml`.

---

### Sub-Tarefa 7 — Documentação e Revisão Final

**Status:** `[x] done`

**Intent**
Documentar execução, profiles e integração MCP para que qualquer desenvolvedor consiga
rodar o projeto do zero sem expor segredos.

**Expected Outcomes**
- `README.md` completo com pré-requisitos, build, execução por profile, exemplos CLI
  e instruções de integração MCP.
- Nenhum segredo em arquivos versionados.
- `mvn clean install` passa do zero em máquina limpa.
- Tag `v1.0.0` criada.

**Todo List**
1. Criar `README.md` com seções: Visão Geral, Pré-requisitos, Build, Executar no Profile CLI,
   Executar no Profile MCP, Exemplos de Comandos, Integração com Claude Desktop / Bob,
   Estrutura do Projeto, Segurança.
2. Documentar os comandos exatos de execução (Zsh/Debian) para cada profile.
3. Revisar todos os `.yml` — confirmar ausência de senhas ou chaves de API.
4. Confirmar que `application-local.yml` está no `.gitignore`.
5. Executar `mvn clean install` final e registrar resultado.
6. Criar tag `v1.0.0` no repositório.

**Relevant Context**
- `.gitignore` já cobre `application-local.yml`, `*.jks`, `*secret*` e demais padrões de segurança.
