# Escopo técnico

**Ordem**: `M = 4` → `MAX_KEYS = 3`, `MAX_CHILDREN = 4`.
**Tipos**: `int` como chave, `String` como valor.
**Operações obrigatórias**:

- `search(int key) -> String|null`
- `insert(int key, String value) -> boolean`
- `remove(int key) -> boolean`
- `range(int k1, int k2) -> void` (imprime `k:v` na ordem)
- `findLeaf(int key) -> BPTNode` (helper)
- `splitLeaf(...)`, `splitInternal(...)`, `insertIntoParent(...)`
- `rebalanceAfterDelete(node, parent, parentIdx)` com **redistribuição** e **merge**

**Estruturas** (sem coleções):

```java
class BPTNode {
  boolean isLeaf;
  int[] keys = new int[MAX_KEYS];
  int keyCount;
  BPTNode[] children = new BPTNode[MAX_CHILDREN]; int childCount; // só em internos
  String[] values = new String[MAX_KEYS];        int valueCount;  // só em folhas
  BPTNode next; // encadeia folhas
}
class BPlusTree { /* root, ORDER, métodos acima */ }
```

---

# Aplicação prática (exemplo que faz sentido)

## Mini-Índice de Catálogo de Músicas

Cenário: teremos um arquivinho CSV com **faixas** (id, artista, título). A **B+ tree** indexa por **track_id** para:

- buscar por id (ex.: “me mostra a faixa 1024”),
- **listar intervalos** (ex.: “todas as faixas entre 500 e 800” → range scan perfeito para B+),
- inserir e remover mantendo tudo ordenado e rápido.

### Formato do dataset (simples)

```
205;Daft Punk;Harder Better Faster Stronger
318;Radiohead;Nude
510;Kendrick Lamar;Evidências.
...
```

- **Chave**: `track_id` (int)
- **Valor**: `artista - título` (string concatenada)

### CLI da aplicação

```
# carregar
load data/tracks_small.csv

# operações
search 205
insert 777 "Massive Attack - Teardrop"
range 300 600
remove 510
search 510
```

Saídas sempre claras:

```
FOUND 205 -> Daft Punk - Harder Better Faster Stronger
INSERT 777 -> OK
RANGE 300..600:
318 -> Radiohead - Nude
510 -> Kendrick Lamar - DNA.
REMOVE 510 -> OK
FOUND 510 -> null
```

> Por que esse exemplo? Porque “catálogos” e “índices” é exatamente onde B+/B-Tree brilham (bancos, sistemas de arquivos). E **range por id** é natural pro vídeo.

---

# Planejamento por pessoa

> Cada pessoa tem: **o que contribui**, **tarefas técnicas** e **commits granulares** (para guia).

## Tism-Man — Núcleo & Busca

**Contribui com:** base da árvore, busca e utilitários de navegação.

**Tarefas técnicas**

- Definir `BPTNode` e `BPlusTree` (constantes, root).
- Implementar `findLeaf(key)` (descida por internos).
- Implementar `search(key)` na folha (sequencial até `keyCount`).
- Pequenos utilitários de **inserção ordenada** em arrays (sem `length`).

**Commits**

1. `feat(core): BPTNode com arrays/contadores e encadeamento de folhas`
2. `feat(core): BPlusTree (ORDER/MAX_KEYS/MAX_CHILDREN/root)`
3. `feat(search): findLeaf + search descendo até folha`
4. `test(search): casos básicos (existe/não existe)`

---

## NeoVini — Inserção “folha” & Ordenação

**Contribui com:** inserir sem estourar e manter arrays ordenados.

**Tarefas técnicas**

- `insert(key,value)` até caber na folha (sem split).
- Função de **deslocar à direita** e inserir na posição correta, atualizando `keyCount/valueCount`.
- Rejeitar duplicata ou sobrescrever (definir política simples e documentar).

**Commits** 5. `feat(insert): inserir ordenado em folha (sem overflow)` 6. `refactor(util): helpers de deslocamento nos arrays` 7. `test(insert): lote pequeno + busca validando posições`

---

## Cebolinha — Split & Promoção (folha e interno)

**Contribui com:** quando lota, dividir e promover para manter invariantes.

**Tarefas técnicas**

- `splitLeaf(leaf)` → cria `right`, move metade de trás, conecta `leaf.next`.
- `insertIntoParent(parent, promotedKey, left, right)`
- `splitInternal(internal)` com **promoção da chave do meio**.
- Ajustar caso **root** (criar nova raiz interna).

**Commits** 8. `feat(split-leaf): dividir folha + encadear next` 9. `feat(parent-insert): inserir chave-guia/filhos no pai` 10. `feat(split-internal): dividir interno + promoção` 11. `refactor(insert): ligar caminho completo (sem/ com split)` 12. `test(split): inserir até provocar splits em cascata`

---

## Du — Range & Remoção com Rebalanceamento

**Contribui com:** range scan (folhas encadeadas) e delete robusto.

**Tarefas técnicas**

- `range(k1,k2)` caminhando pela primeira folha de `k1` e seguindo `next`.
- `remove(key)` na folha (deslocar à esquerda; `--keyCount/valueCount`).
- `rebalanceAfterDelete(...)`:
  - **redistribuição** (empresta do irmão se ele tem > mínimo),
  - **merge** (concatena e ajusta pai) quando não dá pra emprestar,
  - tratamento de raiz “afinando” (root = único filho).

- Mensagens de log simples (“REDIST L←R”, “MERGE L+R”).

**Commits** 13. `feat(range): varrer k1..k2 pelas folhas encadeadas` 14. `feat(delete): remoção em folha com underflow check` 15. `feat(rebalance): redistribuição e merge com atualização no pai` 16. `test(delete): casos com redistribuição e merge`

---

# Integração da aplicação (todos participam)

## “loader” simples

- Parser do CSV (linha por linha) transformando `id` (int) e `valor` (`artist - title`).
- Comando `load <arquivo>` no CLI.

## “comandos de mutação”

- Comandos `insert <id> "<artista - título>"` e `remove <id>`.
- Validar “cabem aspas” (caminho simples: tudo que vem depois do id vira o valor).

## “comandos de leitura”

- Comandos `search <id>` e `range <k1> <k2>` com prints limpos.

## “roteiro de demonstração”

- Arquivo `demo_script.txt` com a ordem de comandos a rodar no vídeo:
  1. `load data/tracks_small.csv`
  2. `search 205`
  3. `range 300 600`
  4. `insert 777 "Massive Attack - Teardrop"`
  5. `range 700 800`
  6. `remove 510` → observar log “redistrib/merge”
  7. `search 510`

---

# Critérios de aceitação (prontos pra apresentação)

- **Corretude**:
  - Busca retorna `valor` correto ou `null`.
  - Inserção mantém **ordem** em folhas; splits criam **nova raiz** quando necessário.
  - Remoção deixa a árvore **válida** (sem buracos), com redistribuição/merge quando precisa.
  - Range imprime em **ordem crescente** e respeita `[k1, k2]`.

- **Restrições**:
  - Sem `List/ArrayList/Vector/StringBuilder/try-catch/exceptions`.
  - Sem `arr.length` (exceto `String.length()`); usar **contadores** e **constantes**.
  - Deslocamentos **manuais** em arrays.

- **Aplicação**:
  - `load`, `search`, `insert`, `remove`, `range` funcionando no terminal.
  - Saídas legíveis (FOUND/null, RANGE com pares `id -> valor`).

- **Teste mínimo**:
  - Dataset com ~20–40 faixas para **forçar splits**.
  - Dois deletes que acionarão **redistribuição** e **merge**.
  - Dois ranges cruzando mais de uma folha.

---

# Risco & combinados

- **Ponto de atenção**: Off-by-one nos deslocamentos. Mitigar com helpers pequenos e **prints de debug** (ativáveis por constante `DEBUG`).
- **Definição de “mínimo”**: manter **uma única ordem (M=4)** e **um único tipo** `int->String`. Evitar features extras (persistência, múltiplas colunas).
- **Revisão cruzada**: cada PR/commit de uma pessoa é revisado por outra (A⇄C, B⇄D).

---
