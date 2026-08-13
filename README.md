# Geo-Explorer

Backend Spring Boot que expõe três comandos de aprendizado — **Trilha**, **Desafio** e
**Certificado** — a partir de uma base de trilhas fictícias, operável tanto como CLI
interativo quanto como **Servidor MCP** para integração com Claude Desktop e IBM Bob.

---

## Índice

1. [Visão Geral](#visão-geral)
2. [Pré-requisitos](#pré-requisitos)
3. [Build](#build)
4. [Executar no Profile CLI](#executar-no-profile-cli)
5. [Executar no Profile MCP](#executar-no-profile-mcp)
6. [Exemplos de Comandos CLI](#exemplos-de-comandos-cli)
7. [Integração MCP com Claude Desktop e IBM Bob](#integração-mcp)
8. [Estrutura do Projeto](#estrutura-do-projeto)
9. [Testes](#testes)
10. [Segurança](#segurança)

---

## Visão Geral

O Geo-Explorer reúne três comandos principais:

| Comando | Descrição |
|---------|-----------|
| `trail` | Apresenta o plano de estudos de uma tecnologia |
| `challenge` | Gera um desafio de código conforme a tecnologia e o nível |
| `certificate` | Cria um certificado fictício para uma trilha concluída |

Os dados são armazenados em um banco H2 em memória, populado na inicialização com
trilhas fictícias de **Java**, **Python** e **JavaScript**.

### Separação por Spring Profiles

O projeto resolve o conflito entre Spring Shell e o servidor MCP STDIO (ambos disputam
`System.in`/`System.out`) usando **Spring Profiles** dedicados:

| Profile | Modo | Comando |
|---------|------|---------|
| `cli` | Shell interativo | `java -jar ... --spring.profiles.active=cli` |
| `mcp` | Servidor MCP STDIO | `java -jar ... --spring.profiles.active=mcp` |

---

## Pré-requisitos

| Ferramenta | Versão mínima |
|------------|--------------|
| Java (JDK) | 21 |
| Maven | 3.9+ |
| Git | qualquer |

### Instalar no Pop!\_OS / Debian (Zsh)

```zsh
sudo apt-get update
sudo apt-get install -y openjdk-21-jdk maven

# Verificar
java -version   # deve exibir: openjdk 21...
mvn -version    # deve exibir: Apache Maven 3.x
```

---

## Build

```zsh
# Clonar (se ainda não fez)
git clone https://github.com/<seu-usuario>/geo-explorer.git
cd geo-explorer

# Compilar e gerar o JAR (pula os testes)
mvn clean package -DskipTests

# Compilar e rodar todos os testes
mvn clean install
```

O JAR gerado estará em `target/geo-explorer-1.0.0.jar`.

---

## Executar no Profile CLI

Inicia o Spring Shell interativo. O console H2 fica disponível em
`http://localhost:8080/h2-console` para inspeção do banco.

```zsh
java -jar target/geo-explorer-1.0.0.jar --spring.profiles.active=cli
```

Após a inicialização você verá o prompt:

```
geo-explorer:>
```

Use `help` para listar os comandos disponíveis.

### Acessando o Banco de Dados H2

O Geo-Explorer usa um banco **H2 em memória** chamado `geoexplorer`. O H2 Console
preenche o campo **JDBC URL** com `jdbc:h2:~/test` por padrão, o que causa erro de
conexão. Para acessar os dados do seed siga estes passos:

1. Com o profile `cli` rodando, abra `http://localhost:8080/h2-console` no navegador.
2. No campo **JDBC URL**, apague o texto padrão e cole exatamente:
   `jdbc:h2:mem:geoexplorer`
3. Deixe o campo **User Name** como `sa` e a **Password** em **branco**.
4. Clique em **Connect**.

A partir daí você pode inspecionar as tabelas `TRAILS`, `MODULES` e `CHALLENGES`,
populadas pela migração Flyway `V1__init_schema_and_data.sql`, que também cria o schema.

> **Observação:** por ser em memória, os dados existem apenas enquanto o processo
> estiver de pé e são recriados a cada inicialização.

---

## Executar no Profile MCP

Inicia o servidor MCP com **transport STDIO**. Neste modo:

- O banner do Spring é desligado (`banner-mode: off`)
- O Tomcat não sobe (`web-application-type: none`)
- O Spring Shell é desabilitado
- **Todos os logs vão para `stderr`** — o `stdout` fica exclusivamente para JSON-RPC

```zsh
java -jar target/geo-explorer-1.0.0.jar --spring.profiles.active=mcp
```

> **Importante:** em modo MCP o processo não exibe prompt interativo. Ele aguarda
> requisições JSON-RPC no `stdin` e responde no `stdout`. Use um cliente MCP para
> interagir (ver seção [Integração MCP](#integração-mcp)).

---

## Exemplos de Comandos CLI

### `trail` — Plano de estudos

```
geo-explorer:> trail --technology java
geo-explorer:> trail --technology python
geo-explorer:> trail --technology javascript
```

**Saída esperada:**

```
📚 Trilha de JAVA
────────────────────────────────────────────────────────
1. Introdução ao Java
   Conheça a história do Java, a JVM e escreva seu primeiro programa Hello World...

2. Tipos, Variáveis e Operadores
   Explore os tipos primitivos (int, double, boolean, char)...

3. Orientação a Objetos
   Aprenda os quatro pilares da OO: encapsulamento, herança, polimorfismo...
```

---

### `challenge` — Desafio de código

```
geo-explorer:> challenge --technology java --level BEGINNER
geo-explorer:> challenge --technology python --level INTERMEDIATE
geo-explorer:> challenge --technology javascript --level ADVANCED
```

O parâmetro `--level` aceita: `BEGINNER`, `INTERMEDIATE`, `ADVANCED` (default: `BEGINNER`).

**Saída esperada:**

```
🎯 Desafio: FizzBuzz Clássico
────────────────────────────────────────────────────────
Tecnologia : JAVA
Nível      : BEGINNER

Escreva um programa Java que imprima de 1 a 100...
```

---

### `certificate` — Certificado fictício

```
geo-explorer:> certificate --technology javascript --user "Ana Lima"
geo-explorer:> certificate --technology python --user "Carlos Silva"
```

**Saída esperada:**

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
║  Descrição: Trilha de JavaScript moderno (ES2020+), focada em║
║    fundamentos e assincronicidade.                           ║
║                                                              ║
║  Data de emissão: 12/08/2026                                 ║
║                                                              ║
╚══════════════════════════════════════════════════════════════╝
```

---

## Integração MCP

O Geo-Explorer expõe três **tools MCP** que podem ser consumidas por qualquer cliente
compatível com o Model Context Protocol.

### Tools disponíveis

| Nome da Tool | Descrição |
|---|---|
| `geo_trail` | Retorna o plano de estudos completo de uma tecnologia |
| `geo_challenge` | Gera um desafio de código para uma tecnologia e nível |
| `geo_certificate` | Emite um certificado fictício para uma trilha concluída |

### Configurar no Claude Desktop

Edite o arquivo de configuração do Claude Desktop
(`~/Library/Application Support/Claude/claude_desktop_config.json` no macOS ou
`%APPDATA%\Claude\claude_desktop_config.json` no Windows):

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

Adicione o servidor MCP ao arquivo de configuração do Bob apontando para o JAR com
o profile `mcp`:

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

---

## Estrutura do Projeto

```
geo-explorer/
├── pom.xml                                      # Maven — Spring Boot 3.3.13 + Spring AI BOM 1.0.0
├── geo-explorer-backend-plan.md                 # Plano de desenvolvimento
└── src/
    ├── main/
    │   ├── java/com/geoexplorer/
    │   │   ├── GeoExplorerApplication.java      # Classe main
    │   │   ├── command/                         # @Profile("cli") — Spring Shell
    │   │   │   ├── TrailCommand.java
    │   │   │   ├── ChallengeCommand.java
    │   │   │   └── CertificateCommand.java
    │   │   ├── domain/
    │   │   │   ├── model/
    │   │   │   │   ├── Level.java               # Enum: BEGINNER, INTERMEDIATE, ADVANCED
    │   │   │   │   ├── Trail.java
    │   │   │   │   ├── Module.java
    │   │   │   │   └── Challenge.java
    │   │   │   ├── repository/
    │   │   │   │   ├── TrailRepository.java
    │   │   │   │   └── ChallengeRepository.java
    │   │   │   └── dto/                         # Records de saída
    │   │   │       ├── TrailDTO.java
    │   │   │       ├── ModuleDTO.java
    │   │   │       └── ChallengeDTO.java
    │   │   ├── exception/
    │   │   │   ├── GeoExplorerException.java    # Classe base
    │   │   │   ├── ResourceNotFoundException.java
    │   │   │   ├── InvalidInputException.java
    │   │   │   └── InvalidLevelException.java
    │   │   ├── common/
    │   │   │   └── AppConstants.java            # Constantes compartilhadas
    │   │   ├── mcp/                             # @Profile("mcp") — Servidor MCP STDIO
    │   │   │   ├── GeoExplorerTools.java        # Métodos @Tool
    │   │   │   └── McpToolsConfig.java          # Registra ToolCallbackProvider
    │   │   └── service/                         # Compartilhado entre cli e mcp
    │   │       ├── TrailService.java
    │   │       ├── ChallengeService.java
    │   │       └── CertificateService.java
    │   └── resources/
    │       ├── application.yml                  # Base compartilhada (sem segredos)
    │       ├── application-cli.yml              # Profile cli
    │       ├── application-mcp.yml              # Profile mcp
    │       ├── logback-spring.xml               # Logs → stderr no profile mcp
    │       └── db/migration/
    │           └── V1__init_schema_and_data.sql # Schema + seed (3 tecnologias × 3 módulos × 3 desafios)
    └── test/
        └── java/com/geoexplorer/
            ├── command/                         # Testes de integração (@ActiveProfiles("cli"))
            ├── mcp/                             # Teste de fumaça (@ActiveProfiles("mcp"))
            ├── service/                         # Testes unitários (Mockito)
            └── domain/                          # Entidades + repositórios (@DataJpaTest)
```

---

## Testes

A suíte tem **58 testes** (unitários, de persistência e de integração) com cobertura
atual de **Line ≈ 93%, Branch ≈ 90%, Method ≈ 90%**.

```zsh
# Todos os testes
mvn test

# Apenas testes unitários de services e tools MCP
mvn test -Dtest="TrailServiceTest,ChallengeServiceTest,CertificateServiceTest,GeoExplorerToolsTest"

# Testes de persistência (@DataJpaTest + Flyway) e de entidades
mvn test -Dtest="TrailRepositoryTest,EntityModelTest"

# Testes de integração do profile cli
mvn test -Dtest="TrailCommandTest,ChallengeCommandTest,CertificateCommandTest"

# Teste de fumaça do profile mcp
mvn test -Dtest="McpToolsConfigTest"

# Build completo com gate de cobertura (LINE ≥ 90%, BRANCH ≥ 80%, METHOD ≥ 85%)
mvn clean verify
```

---

## Segurança

> **Regra de ouro:** nunca versionar credenciais, senhas ou chaves de API.

- O banco H2 é **em memória** — não há senha real nem arquivo persistido
- O `application-local.yml` está no `.gitignore` (nunca criar com senha)
- Certificados (`*.jks`, `*.p12`, `*.pem`), keystores e arquivos com `*secret*`
  ou `*credential*` também são ignorados pelo Git
- Nenhuma chave de API é necessária para rodar o projeto

Para sobrescrever configurações localmente sem commitar, use `application-local.yml`
(ignorado pelo Git) ou variáveis de ambiente:

```zsh
export SPRING_DATASOURCE_PASSWORD=sua_senha_local
java -jar target/geo-explorer-1.0.0.jar --spring.profiles.active=cli
```
