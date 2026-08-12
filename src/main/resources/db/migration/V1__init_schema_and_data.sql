-- =============================================================================
-- V1__init_schema_and_data.sql
-- Cria o schema e popula os dados fictícios das trilhas de estudo.
-- Gerenciado pelo Flyway — não editar manualmente após aplicação.
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Schema
-- -----------------------------------------------------------------------------

CREATE TABLE trails (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    technology  VARCHAR(100) NOT NULL,
    description VARCHAR(500) NOT NULL,
    level       VARCHAR(20)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_trails_technology UNIQUE (technology)
);

CREATE TABLE modules (
    id           BIGINT        NOT NULL AUTO_INCREMENT,
    trail_id     BIGINT        NOT NULL,
    title        VARCHAR(200)  NOT NULL,
    content      VARCHAR(2000) NOT NULL,
    module_order INT           NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_modules_trail FOREIGN KEY (trail_id) REFERENCES trails(id)
);

CREATE TABLE challenges (
    id          BIGINT        NOT NULL AUTO_INCREMENT,
    trail_id    BIGINT        NOT NULL,
    title       VARCHAR(200)  NOT NULL,
    description VARCHAR(2000) NOT NULL,
    level       VARCHAR(20)   NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_challenges_trail FOREIGN KEY (trail_id) REFERENCES trails(id)
);

-- -----------------------------------------------------------------------------
-- Seed — Trilhas
-- -----------------------------------------------------------------------------

INSERT INTO trails (technology, description, level) VALUES
    ('java',       'Trilha completa de Java, do básico à orientação a objetos avançada.',             'BEGINNER'),
    ('python',     'Trilha de Python focada em produtividade, scripting e ciência de dados básica.',  'BEGINNER'),
    ('javascript', 'Trilha de JavaScript moderno (ES2020+), focada em fundamentos e assincronicidade.', 'BEGINNER');

-- -----------------------------------------------------------------------------
-- Seed — Módulos Java (trail_id = 1)
-- -----------------------------------------------------------------------------

INSERT INTO modules (trail_id, title, content, module_order) VALUES
    (1, 'Introdução ao Java',
     'Conheça a história do Java, a JVM e escreva seu primeiro programa Hello World. Entenda o ciclo de compilação .java → .class e como o classloader funciona.',
     1),
    (1, 'Tipos, Variáveis e Operadores',
     'Explore os tipos primitivos (int, double, boolean, char), wrappers (Integer, Double), casting, e os principais operadores aritméticos, relacionais e lógicos.',
     2),
    (1, 'Orientação a Objetos',
     'Aprenda os quatro pilares da OO: encapsulamento, herança, polimorfismo e abstração. Implemente classes, interfaces e use o modificador final de forma estratégica.',
     3);

-- -----------------------------------------------------------------------------
-- Seed — Módulos Python (trail_id = 2)
-- -----------------------------------------------------------------------------

INSERT INTO modules (trail_id, title, content, module_order) VALUES
    (2, 'Python Básico e Ambiente',
     'Configure o ambiente com pyenv e venv. Entenda a tipagem dinâmica, indentação obrigatória e escreva scripts simples com input/output e formatação de strings (f-strings).',
     1),
    (2, 'Estruturas de Dados Nativas',
     'Domine list, tuple, dict e set com suas operações, comprehensions e métodos embutidos. Saiba quando usar cada estrutura e entenda a imutabilidade de tuples.',
     2),
    (2, 'Funções e Módulos',
     'Crie funções com parâmetros default, *args e **kwargs. Organize código em módulos e pacotes. Entenda os conceitos de escopo (LEGB) e closures.',
     3);

-- -----------------------------------------------------------------------------
-- Seed — Módulos JavaScript (trail_id = 3)
-- -----------------------------------------------------------------------------

INSERT INTO modules (trail_id, title, content, module_order) VALUES
    (3, 'Fundamentos do JavaScript',
     'Entenda var, let e const, hoisting e o event loop. Explore os tipos primitivos, coerção de tipos e as peculiaridades do == vs ===. Execute código no Node.js e no browser.',
     1),
    (3, 'Funções e Closures',
     'Domine function declarations, expressions, arrow functions e o valor de this em cada contexto. Entenda closures, IIFE e currying com exemplos práticos.',
     2),
    (3, 'Assincronicidade: Callbacks, Promises e Async/Await',
     'Evolua de callbacks para Promises e finalmente para async/await. Aprenda a tratar erros com try/catch em contextos assíncronos e use Promise.all() para paralelismo.',
     3);

-- -----------------------------------------------------------------------------
-- Seed — Desafios Java (trail_id = 1)
-- -----------------------------------------------------------------------------

INSERT INTO challenges (trail_id, title, description, level) VALUES
    (1, 'FizzBuzz Clássico',
     'Escreva um programa Java que imprima de 1 a 100. Para múltiplos de 3, imprima Fizz; para múltiplos de 5, Buzz; para múltiplos de ambos, FizzBuzz.',
     'BEGINNER'),
    (1, 'Calculadora de Fibonacci',
     'Implemente um método que retorne o n-ésimo número da sequência de Fibonacci usando recursão e depois com memoização. Compare a performance das duas abordagens.',
     'INTERMEDIATE'),
    (1, 'Pilha Genérica com Generics',
     'Implemente uma estrutura de dados Stack<T> genérica com os métodos push(), pop(), peek() e isEmpty(). Trate adequadamente a exceção para pop() em pilha vazia.',
     'ADVANCED');

-- -----------------------------------------------------------------------------
-- Seed — Desafios Python (trail_id = 2)
-- -----------------------------------------------------------------------------

INSERT INTO challenges (trail_id, title, description, level) VALUES
    (2, 'Analisador de Frequência de Palavras',
     'Dado um texto qualquer, escreva um script Python que conte a frequência de cada palavra (ignorando maiúsculas/minúsculas e pontuação) e exiba as 10 mais frequentes.',
     'BEGINNER'),
    (2, 'Decorador de Tempo de Execução',
     'Implemente um decorador @timer que meça e imprima o tempo de execução de qualquer função decorada. Use o módulo time e teste com funções de ordenação de listas grandes.',
     'INTERMEDIATE'),
    (2, 'Mini ORM com Metaclasses',
     'Crie uma classe base Model usando metaclasses que inspecione os atributos de classe para gerar automaticamente uma instrução SQL CREATE TABLE e um método to_dict().',
     'ADVANCED');

-- -----------------------------------------------------------------------------
-- Seed — Desafios JavaScript (trail_id = 3)
-- -----------------------------------------------------------------------------

INSERT INTO challenges (trail_id, title, description, level) VALUES
    (3, 'Debounce Function',
     'Implemente do zero a função debounce(fn, delay) que atrasa a execução de fn até que delay milissegundos tenham passado desde a última chamada. Teste com chamadas rápidas consecutivas.',
     'BEGINNER'),
    (3, 'Pipeline de Transformação Assíncrono',
     'Crie uma função asyncPipeline(...fns) que receba uma lista de funções assíncronas e as execute em sequência, passando o resultado de cada uma como input da próxima. Use async/await.',
     'INTERMEDIATE'),
    (3, 'Implementação de Observable Simples',
     'Implemente uma classe Observable com os métodos subscribe(observer), map(fn), filter(fn) e take(n) seguindo os princípios básicos do padrão Observer, sem bibliotecas externas.',
     'ADVANCED');
