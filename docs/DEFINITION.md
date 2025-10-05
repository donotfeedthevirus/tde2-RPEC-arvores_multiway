# Planejamento (B+)

## Pesquisa

### O que é?

Rapazes pensem num shopping:

- Os corredores têm placas com números (“lojas 1–50 → à esquerda; 51–100 → à direita”).
- As lojas são onde ficam os produtos de verdade.

Na Árvore B+:

- Nó interno = placa de corredor (só te guia).
- Nó folha = loja (onde ficam todas as chaves e valores).
- As lojas estão em fila, uma do lado da outra (cada folha aponta para a próxima). Isso deixa ler um intervalo muito fácil: você entra na primeira loja e vai andando para a direita.

### O que as B+ prometem? (o porque delas existirem)

- Buscar rápido: você lê poucas placas até entrar na loja certa (caminho curto).
- Inserir/Apagar mantendo tudo organizado: quando lota, a loja divide em duas; quando fica vazia demais, pode juntar com a vizinha.
- Ler intervalos (ex.: de 100 a 200) muito fácil: começa na primeira loja que tem 100 e vai passando para as próximas.

### O que compõe?

> Vamos usar números como chaves (ex.: CPF sem pontos) e valores como strings (ex.: nome).

- Nó interno (placa)
  - Guarda algumas chaves em ordem crescente (ex.: 50, 100, 150).
  - Guarda ponteiros para filhos (corredores/lojas).
  - Não tem valor; é só guia.

- Nó folha (loja)
  - Guarda pares (chave, valor) em ordem (ex.: (12,Ana), (30,Bia), (47,Caio)).
  - Tem um ponteiro para a próxima folha (a loja ao lado).
  - Aqui ficam todos os valores.

> Capacidade (regra simples): cada nó tem um limite de quantas chaves cabe. Para estudar, usamos um limite pequeno (ex.: cabe até 3 chaves por nó).
>
> - Se passar do limite, divide.
> - Se ficar muito abaixo (após remover), pega emprestado do vizinho ou junta.

### Como Buscar (passo a passo bem de boa)

Quero a chave 77.

1. Começo na raiz (pode ser placa ou loja).
2. Se for placa com “50 | 100”, pergunto: 77 é menor que 50? não. Menor que 100? sim = desço pelo filho do meio.
3. Repito até chegar numa loja.
4. Na loja, as chaves estão ordenadas; olho uma por uma até achar 77 (ou ver que não tem).

Por que é rápido? Porque cada placa “corta” o caminho em fatias grandes, desce poucos níveis.

### Como inserir (sem lotar o bagulho)

Quero inserir (40, “Davi”).

1. Desço até a loja onde 40 deveria ficar.
2. Empurro as chaves maiores para a direita e coloco 40 na posição correta (mantendo ordem).
3. Se ainda coube, acabou.

#### E se lota o bagulho?

Suponha que a loja comporta 3 chaves e, ao inserir, ficou com 4 (ex.: (10,A),(20,B),(30,C),(40,D)).

- Divide em duas lojas:
  - Metade esquerda: (10,A),(20,B)
  - Metade direita: (30,C),(40,D)

- Liga a esquerda = direita por `next`.
- Sobe uma chave-guia para a placa acima (normalmente a primeira da direita, aqui 30).
- Se a placa acima lota, você repete o processo com placas (divide a placa e pode até criar uma nova raiz).

> Pensa assim: “Lotou? Parto no meio e conto para a placa do corredor a partir de qual número começa a loja nova.”

### Como ler um intervalo (range pros íntimos)

Quero todos entre 25 e 85.

1. Vou até a primeira loja que poderia ter 25.

2. Imprimo tudo ≥ 25 nessa loja.

3. Pulo para a próxima loja usando next e continuo, parando em 85.

> Isso é o tchan da B+: as lojas são encadeadas. Você anda “na horizontal” sem voltar para as placas.

### Como remover (sem quebrar a ordem)

Quero remover a chave 30.

1. Vou até a loja. tiro o par (30, …) e puxo os que estão à direita uma posição para a esquerda.
2. Se ainda ficou com um tamanho ok, acabou.

#### E quando a loja fica muito vazia?

- Tenta pegar emprestado do irmão ao lado (se o irmão tem “sobra”, manda 1 par pra cá).
- Se o irmão não pode emprestar, junta as duas lojas (merge).
- Quando junta/pega emprestado, a placa acima pode precisar ajustar a chave-guia (porque o “ponto de corte” mudou).
- Em casos raros, isso “sobe” mais um nível (igual aconteceu com os splits).
