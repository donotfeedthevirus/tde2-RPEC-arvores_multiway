# Escopo técnico

**Objetivo**: implementar uma B+ Tree de ordem `M = 4` (máx. 3 chaves por nó) totalmente isolada, reutilizável e obedecendo às restrições da disciplina.

## Diretrizes de implementação
- **Sem coleções prontas**: nada de `List`, `ArrayList`, `Vector`, `StringBuilder`, `try/catch`, exceções ou similares.
- **Nada de `array.length`**: use apenas `MAX_KEYS`, `MAX_CHILDREN` e contadores (`keyCount`, `childCount`, `valueCount`). `String.length()` continua permitido.
- **Comentários com responsável**: marcar blocos e métodos com `// TODO [Nome] descrição`. Ex.: `// TODO [Tism-man] inicializar contadores`.
- **Formato de commit**: `[Pessoa] tipo: mensagem`. Ex.: `[NeoVini] feat(insert): inserir ordenado (sem split)`.
- **Debug opcional**: constante `static final boolean DEBUG` e helpers como `printNode` para evitar `System.out` solto.
- **Testes**: pequenos cenários dirigidos pelo mesmo integrante que implementa o recurso.

## Estruturas base (esboço)
```java
class BPTNode {
  boolean isLeaf;
  int[] keys = new int[MAX_KEYS];
  int keyCount;

  BPTNode[] children = new BPTNode[MAX_CHILDREN];
  int childCount; // apenas internos

  String[] values = new String[MAX_KEYS];
  int valueCount; // apenas folhas

  BPTNode next; // encadeamento de folhas
}

class BPlusTree {
  BPTNode root;
  static final int ORDER = 4;
  static final int MAX_KEYS = 3;
  static final int MAX_CHILDREN = 4;

  // Métodos: search, insert, remove, range, findLeaf, splits, rebalance...
}
```

## Invariantes da B+ Tree
- Nó interno deve ter entre `⌈M/2⌉` e `M` filhos (exceto raiz).
- Nó folha mantém entre `⌈MAX_KEYS/2⌉` e `MAX_KEYS` chaves (exceto raiz solitária).
- Todas as chaves ordenadas **crescentes** dentro de cada nó.
- Todas as folhas no mesmo nível, encadeadas via `next`.

## Fluxo macro das operações
- **search(k)**: desce via `findLeaf(k)` usando `keyCount`, varre folha e devolve `String` ou `null`.
- **insert(k, v)**: localiza folha, insere deslocando manualmente. Se `keyCount > MAX_KEYS`, chama `splitLeaf`, propaga promoção com `insertIntoParent`, podendo desencadear `splitInternal`. Atualiza raiz quando necessário.
- **remove(k)**: localiza folha, remove deslocando à esquerda. Se `keyCount < min`, chama `rebalanceAfterDelete` tentando redistribuir; se não der, mergea irmãos e ajusta pai (atualiza raiz se ficar vazia).
- **range(k1, k2)**: encontra primeira folha de `k1`, percorre folhas via `next`, filtrando intervalo.

---

# Aplicação prática (exemplo)

## Mini-índice de catálogo de músicas
- Base CSV: `track_id;artista;título`. Valor armazenado = `artista - título`.
- Comandos:
  ```
  load data/tracks_small.csv
  search 205
  insert 777 "Massive Attack - Teardrop"
  range 300 600
  remove 510
  search 510
  ```
- Saída padrão: `FOUND 205 -> Daft Punk - Harder Better Faster Stronger`, `INSERT 777 -> OK`, `RANGE ...`, etc.
- Range deve imprimir `k -> valor` ordenado cruzando várias folhas.

---

# Planejamento por integrante
> Cada bloco inclui tarefas técnicas + commits esperados com o nome no título.

## Tism-man — Núcleo & Busca
- Estruturar `BPTNode` e `BPlusTree` com campos, constantes e construtores. `// TODO [Tism-man] struct base`.
- Implementar `findLeaf(int key)` descendo por nós internos com `keyCount`.
- Implementar `search(int key)` varrendo a folha (`for (int i = 0; i < leaf.keyCount; i++)`).
- Criar helpers `findPosition`, `shiftRight(int[] arr, int limit)`, `copyKeys` reutilizados pelos demais.
- Implementar `DEBUG` + `printNode`.

**Commits**
1. `[Tism-man] feat(core): definir BPTNode e contadores`
2. `[Tism-man] feat(core): raiz da BPlusTree e constantes de ordem`
3. `[Tism-man] feat(search): findLeaf e search percorrendo folhas`
4. `[Tism-man] test(search): cenários hit e miss`

## NeoVini — Inserção em folha
- `insert(int key, String value)` até antes do split. `// TODO [NeoVini] insert core`.
- `shiftRightLeaf(BPTNode leaf, int start)` deslocando `keys`/`values` usando `keyCount`.
- Política para duplicatas: sobrescrever valor existente e retornar `false` (documentar no JavaDoc).
- Retornar `true` apenas quando inserir novo par.

**Commits**
5. `[NeoVini] feat(insert): inserir ordenado na folha (sem overflow)`
6. `[NeoVini] refactor(util): helpers de deslocamento para folhas`
7. `[NeoVini] test(insert): cenários básicos e duplicata`

## Cebolinha — Split & Promoção
- `splitLeaf(BPTNode leaf)` criando nó direito, movendo metade final, atualizando `next`. `// TODO [Cebolinha] splitLeaf`.
- `insertIntoParent(BPTNode parent, int promotedKey, BPTNode left, BPTNode right)` mantendo arrays ordenados.
- `splitInternal(BPTNode node)` com promoção da chave central. `// TODO [Cebolinha] splitInternal`.
- Garantir caso especial da raiz (criar nova raiz interna). `// TODO [Cebolinha] root split`.
- Integrar com `insert` para propagar splits em cascata.

**Commits**
8. `[Cebolinha] feat(split-leaf): dividir folha e atualizar encadeamento`
9. `[Cebolinha] feat(parent-insert): promover chave para o pai`
10. `[Cebolinha] feat(split-internal): dividir nó interno com promoção`
11. `[Cebolinha] refactor(insert): conectar splits até a raiz`
12. `[Cebolinha] test(split): inserir lote que causa cascata`

## Du — Range & Remoção
- `range(int k1, int k2)` percorrendo folhas via `next`. `// TODO [Du] range`.
- `remove(int key)` em folha: localizar índice, deslocar à esquerda (`shiftLeftLeaf`).
- `rebalanceAfterDelete(node, parent, parentIndex)` implementando redistribuição e merge:
  - Redistribuir emprestando do irmão com mais chaves.
  - Mergear com irmão quando insuficiente, ajustando chave do pai.
  - Atualizar raiz se ficar com um único filho.
- Logs opcionais (`if (DEBUG) System.out.println("MERGE L+R")`).

**Commits**
13. `[Du] feat(range): varrer intervalo usando folhas encadeadas`
14. `[Du] feat(delete): remover da folha com ajustes`
15. `[Du] feat(rebalance): redistribuir/mergear após delete`
16. `[Du] test(delete): cenários de redistribuição e merge`

---

# Integração da aplicação (todos)
- CLI simples lendo linha a linha, separando tokens pelo primeiro espaço.
- Comando `load <arquivo>`: cada linha lida cria par `int`/`String` e chama `insert`.
- Guardar script de demonstração em `docs/demo_script.txt` com a sequência oficial.
- Dataset mínimo de 20–40 registros em `data/tracks_small.csv` para acionar splits e deletes.

---

# Critérios de aceitação
- **Corretude**: busca retorna valor correto; inserção mantém ordenação; remoção deixa invariantes válidas; range lista ordenado.
- **Restrições atendidas**: zero coleções prontas; zero `array.length`; deslocamentos manuais.
- **Aplicação**: comandos `load`, `search`, `insert`, `remove`, `range` produzem mensagens legíveis (`FOUND`, `INSERT`, `REMOVE`, `RANGE`).
- **Testes manuais**: script de demonstração + casos específicos de split, redistribuição e merge.

---

# Riscos e mitigação
- **Off-by-one em deslocamentos**: escrever helpers reutilizáveis e testar com arrays de tamanho 3.
- **Underflow pós-delete**: validar min. chaves constante (`MIN_KEYS_LEAF = 2`, `MIN_CHILDREN = 2`).
- **Duplicatas**: deixar claro no JavaDoc do `insert` que sobrescreve valor existente.
- **Revisão cruzada**: Tism-man ↔ Cebolinha, NeoVini ↔ Du antes de fechar PR.

---
