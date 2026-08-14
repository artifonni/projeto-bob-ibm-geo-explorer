-- =============================================================================
-- V2__more_challenges.sql
-- Adiciona mais desafios por (trilha, nível) para que o gerador de desafios
-- tenha variedade e cada clique gere um desafio diferente.
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Desafios Java (trail_id = 1)
-- -----------------------------------------------------------------------------

INSERT INTO challenges (trail_id, title, description, level) VALUES
    (1, 'Inversão de String',
     'Escreva um método que receba uma String e retorne ela invertida, sem usar StringBuilder.reverse() nem APIs prontas de inversão. Implemente com um loop trocando caracteres das pontas.',
     'BEGINNER'),
    (1, 'Verificador de Número Primo',
     'Implemente uma função que informe se um número é primo. Otimize o algoritmo: teste divisores apenas até a raiz quadrada do número e pule os divisores pares após o 2.',
     'BEGINNER'),
    (1, 'Estatísticas de Notas',
     'Crie um método que receba uma lista de notas, calcule a média, a maior e a menor nota, e retorne um relatório formatado. Use streams do Java 8+ e trate listas vazias com uma exceção.',
     'INTERMEDIATE'),
    (1, 'Fila com LinkedList',
     'Implemente uma fila (FIFO) usando LinkedList com os métodos enqueue(), dequeue() e isEmpty(). Garanta que dequeue() em uma fila vazia lance uma exceção com mensagem clara.',
     'INTERMEDIATE'),
    (1, 'Cache LRU',
     'Implemente um cache LRU (Least Recently Used) usando LinkedHashMap com accessOrder=true. Inclua get() e put() e o controle de capacidade máxima, removendo automaticamente o item menos usado.',
     'ADVANCED'),
    (1, 'Contador com Threads',
     'Crie um programa concorrente que incremente um contador com 10 threads usando ExecutorService e AtomicInteger. Ao final, exiba o valor final e comprove que corresponde ao esperado.',
     'ADVANCED');

-- -----------------------------------------------------------------------------
-- Desafios Python (trail_id = 2)
-- -----------------------------------------------------------------------------

INSERT INTO challenges (trail_id, title, description, level) VALUES
    (2, 'Gerador de Senhas',
     'Escreva um script que gere senhas aleatórias com comprimento configurável, combinando letras, números e símbolos. Garanta ao menos um caractere de cada tipo na senha gerada.',
     'BEGINNER'),
    (2, 'Validador de Palíndromos',
     'Crie uma função que verifique se uma frase é palíndroma, ignorando espaços, acentos e pontuação. Compare a string normalizada com a sua inversa usando slicing.',
     'BEGINNER'),
    (2, 'CRUD de Tarefas em JSON',
     'Implemente um gerenciador de tarefas que persista em um arquivo JSON com as operações criar, listar, concluir e remover. Use o módulo json e trate a ausência do arquivo.',
     'INTERMEDIATE'),
    (2, 'Agrupador por Categoria',
     'Dada uma lista de produtos (nome, preço, categoria), agrupe-os por categoria usando collections.defaultdict e exiba o total gasto em cada uma, ordenado do maior para o menor.',
     'INTERMEDIATE'),
    (2, 'Context Manager de Tempo',
     'Crie um context manager (classe com __enter__/__exit__ ou contextlib.contextmanager) que meça e imprima o tempo de execução do bloco, funcionando com a sintaxe with.',
     'ADVANCED'),
    (2, 'Pipeline com Generators',
     'Monte um pipeline de processamento em streaming com generators: leia linhas de um arquivo, limpe cada linha, remova as vazias e agrupe em blocos de 10 para consumo posterior.',
     'ADVANCED');

-- -----------------------------------------------------------------------------
-- Desafios JavaScript (trail_id = 3)
-- -----------------------------------------------------------------------------

INSERT INTO challenges (trail_id, title, description, level) VALUES
    (3, 'Validador de CPF',
     'Implemente uma função que valide um CPF aplicando o algoritmo dos dígitos verificadores. Retorne true/false e trate entradas inválidas, como sequências repetidas (111.111.111-11).',
     'BEGINNER'),
    (3, 'Conversor de Moedas',
     'Crie um conversor de moedas com taxas fixas. Receba o valor, a moeda de origem e a de destino, e devolva o valor convertido com duas casas decimais.',
     'BEGINNER'),
    (3, 'Memoização de Funções',
     'Implemente uma função memoize(fn) que armazene o resultado das chamadas em um Map para evitar recomputação. Teste com Fibonacci e compare o número de execuções.',
     'INTERMEDIATE'),
    (3, 'Flatten de Arrays',
     'Escreva uma função flatten(arr) que achate arrays aninhados de qualquer profundidade usando recursão. Depois, reescreva a versão iterativa com uma pilha.',
     'INTERMEDIATE'),
    (3, 'Proxy com Validação',
     'Use Proxy para criar um objeto que valide as propriedades: números entre 0 e 100 e strings sem espaços extras. Lance erro ao tentar violar qualquer regra.',
     'ADVANCED'),
    (3, 'Debounce com AbortController',
     'Implemente um debounce que, além de atrasar a chamada, use AbortController para cancelar requisições anteriores de uma função async que busca dados.',
     'ADVANCED');
