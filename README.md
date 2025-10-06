# B+ Tree (Ordem 4)

Implementação acadêmica de uma árvore B+ de ordem 4 (máximo de 3 chaves por nó), escrita inteiramente em Java sem o uso de coleções prontas. O repositório inclui testes manuais guiados por console para confirmar operações de busca, inserção, divisão e remoção.

## Estrutura do Projeto

```
.
├── docs/ # Materiais de apoio (não relacionados ao fluxo atual de testes)
├── scripts/
│   └── run_tests.sh # Compila e executa todos os cenários de teste
├── src/
│   ├── main/java/
│   │   └── BPlusTree.java # Implementação da árvore B+
│   └── test/java/
│       ├── BPlusTreeInsertTest.java # Cenário de inserção e sobrescrita
│       ├── BPlusTreeSearchTest.java # Cenário de busca em folha preparada
│       ├── BPlusTreeSplitTest.java # Inserções que causam splits em cascata
│       ├── BPlusTreeDeleteTest.java # Remoções com redistribuição/merge
│       └── BPlusTreeTestPrinter.java # Utilitário de formatação para os testes
└── README.md
```

## Pré-requisitos

- JDK 8 ou superior instalado e disponível no `PATH`.
- Um terminal compatível com scripts shell (`bash`).

## Executando os Testes

O fluxo recomendado é automatizado via script:

```bash
./scripts/run_tests.sh
```

O script realiza as seguintes etapas:

1. Limpa e recria o diretório `build/classes`.
2. Compila todos os arquivos em `src/main/java` e `src/test/java`.
3. Executa, em ordem, os programas de teste `BPlusTreeInsertTest`, `BPlusTreeSearchTest`, `BPlusTreeSplitTest` e `BPlusTreeDeleteTest`.

A saída de cada teste é narrada em português, destacando ações com o prefixo `→`, resultados com `↳`, snapshots da árvore (`[Árvore]`) e da cadeia de folhas (`[Folhas]`).

- Mensagens `[SUCESSO]` indicam que o cenário validou todas as expectativas.
- Mensagens `[ERRO]` aparecem quando alguma invariância é violada ou quando o retorno de uma operação diverge do esperado.

## Execução Manual (Opcional)

Se preferir rodar individualmente:

```bash
# Compilação
javac -d build/classes $(find src/main/java -name '*.java') $(find src/test/java -name '*.java')

# Execução de um teste específico
java -cp build/classes BPlusTreeSplitTest
```

Cada teste possui um método `main` independente, então você pode executar apenas o cenário desejado.

## Compreendendo os Cenários

- **Inserção:** cria uma árvore vazia, insere três chaves diferentes, sobrescreve um valor duplicado e valida a ordenação final da folha raiz.
- **Busca:** monta manualmente uma folha com duas entradas e confirma os retornos para acertos e ausências.
- **Divisão:** insere seis chaves em sequência, forçando splits de folhas e promoção de chaves até a raiz.
- **Remoção:** popula a árvore com oito chaves, executa um `range` ilustrativo e remove valores para exercitar redistribuições, merges e eventual colapso da raiz.
