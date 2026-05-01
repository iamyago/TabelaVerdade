package TabelaVerdade;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TabelaVerdade {

    // Converte boolean para representação "V" Verdadeiro ou "F" Falso
    private static String boolToVF(boolean b) {
        return b ? "V" : "F";
    }

    private static int prioridadeOP(char op) {
        return switch (op) {
            case '¬', '!' -> 5;
            case '∧', '&' -> 4;
            case '∨', '|' -> 3;
            case '→' -> 2;
            case '↔' -> 1;
            default -> 0;
        };
    }

    private static boolean ehOperadorUnario(char c) {
        return c == '¬' || c == '!';
    }

    private static boolean ehOperadorBinario(char c) {
        return c == '∧' || c == '∨' || c == '→' || c == '↔' ||
                c == '&' || c == '|';
    }

    private static boolean ehOperador(char c) {
        return ehOperadorUnario(c) || ehOperadorBinario(c);
    }

    // Exibe os operadores aceitos para ajudar o usuário
    private static void imprimirAjuda() {
        System.err.println("Use letras para proposições e os seguintes operadores:");
        System.err.println("  Negação:       !  ou  ¬");
        System.err.println("  Conjunção:     &  ou  ∧");
        System.err.println("  Disjunção:     |  ou  ∨");
        System.err.println("  Implicação:    -> ou  →");
        System.err.println("  Bicondicional: <-> ou ↔");
    }

    // Converte operadores ASCII multi-caractere para Unicode antes do processamento
    private static String preprocessarExpressao(String expr) {
        expr = expr.replace("<->", "↔")
                .replace("<=>", "↔")
                .replace("->", "→")
                .replace("=>", "→")
                .replace("~", "¬");
        return expr;
    }

    // Mostra visualmente onde está o erro na expressão
    private static void imprimirPosicaoErro(String expr, int pos) {
        System.err.println("  " + expr);
        System.err.println("  " + " ".repeat(pos) + "^");
    }

    // VALIDAÇÃO — dividida em etapas com responsabilidade única

    public static boolean validarExpressao(String expr) {
        return validarVaziaOuSemVariaveis(expr) &&
                validarCaracteresEParenteses(expr) &&
                validarExtremos(expr) &&
                validarCaracteresAdjacentes(expr);
    }

    private static boolean validarVaziaOuSemVariaveis(String expr) {

        if (expr == null || expr.trim().isEmpty()) {
            System.err.println("Erro: A expressão não pode ser vazia.");
            return false;
        }

        for (int i = 0; i < expr.length(); i++) {
            if (Character.isLetter(expr.charAt(i))) {
                return true;
            }
        }


        System.err.println("Erro: A expressão deve conter pelo menos uma proposição (letra).");
        imprimirAjuda();
        return false;
    }

    private static boolean validarCaracteresEParenteses(String expr) {
        int contadorParenteses = 0;
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);

            if (expr.length() == 1 && Character.isLetter(expr.charAt(0))) {
                System.err.println("Erro: A expressão não pode ser composta por apenas uma proposição ('" + expr + "').");
                System.err.println("É necessária pelo menos uma operação lógica valida.");
                return false;
            }

            if (!Character.isLetter(c) && !ehOperador(c) && c != '(' && c != ')') {
                if (Character.isDigit(c)) {
                    System.err.println("Erro na posição " + (i + 1) + ": Número '" + c
                            + "' não é permitido. Use letras para proposições.");
                } else {
                    System.err.println("Erro na posição " + (i + 1) + ": Caractere inválido '" + c + "'.");
                }
                imprimirPosicaoErro(expr, i);
                imprimirAjuda();
                return false;
            }

            if (c == '(')
                contadorParenteses++;
            else if (c == ')')
                contadorParenteses--;

            if (contadorParenteses < 0) {
                System.err.println("Erro na posição " + (i + 1) + ": ')' fechando sem '(' correspondente.");
                imprimirPosicaoErro(expr, i);
                return false;
            }
        }

        if (contadorParenteses != 0) {
            System.err.println("Erro: " + contadorParenteses + " parêntese(s) '(' aberto(s) sem fechamento.");
            return false;
        }
        return true;
    }

    private static boolean validarExtremos(String expr) {
        char primeiro = expr.charAt(0);
        if (ehOperadorBinario(primeiro) || primeiro == ')') {
            System.err.println("Erro na posição 1: Expressão não pode começar com '" + primeiro + "'.");
            imprimirPosicaoErro(expr, 0);
            return false;
        }

        char ultimo = expr.charAt(expr.length() - 1);
        if (ehOperador(ultimo) || ultimo == '(') {
            System.err.println(
                    "Erro na posição " + expr.length() + ": Expressão não pode terminar com '" + ultimo + "'.");
            imprimirPosicaoErro(expr, expr.length() - 1);
            return false;
        }
        return true;
    }

    private static boolean erroAdjacente(String mensagem, String expr, int i) {
        System.err.println("Erro na posição " + (i + 2) + ": " + mensagem + ".");
        imprimirPosicaoErro(expr, i + 1);
        return false;
    }

    private static boolean validarCaracteresAdjacentes(String expr) {
        for (int i = 0; i < expr.length() - 1; i++) {
            char atual = expr.charAt(i);
            char proximo = expr.charAt(i + 1);

            if (ehOperadorBinario(atual) && ehOperadorBinario(proximo))
                return erroAdjacente("Dois operadores binários seguidos", expr, i);
            if (Character.isLetter(atual) && Character.isLetter(proximo))
                return erroAdjacente("Duas proposições seguidas sem operador", expr, i);
            if (Character.isLetter(atual) && proximo == '(')
                return erroAdjacente("Proposição seguida de '(' — falta operador", expr, i);
            if (atual == ')' && Character.isLetter(proximo))
                return erroAdjacente("')' seguido de proposição — falta operador", expr, i);
            if (ehOperadorBinario(atual) && proximo == ')')
                return erroAdjacente("Operador seguido de ')' — falta operando", expr, i);
            if (atual == '(' && ehOperadorBinario(proximo))
                return erroAdjacente("'(' seguido de operador binário — falta operando", expr, i);
            if (atual == '(' && proximo == ')')
                return erroAdjacente("Parênteses vazios '()'", expr, i);
            if (atual == ')' && proximo == '(')
                return erroAdjacente("')' seguido de '(' — falta operador", expr, i);
            if ((Character.isLetter(atual) || atual == ')') && ehOperadorUnario(proximo))
                return erroAdjacente("Proposição/Parêntese seguido de negação — falta operador binário", expr, i);
            if (ehOperadorUnario(atual) && proximo == ')')
                return erroAdjacente("Negação seguida de ')' — falta operando", expr, i);
            if (ehOperadorUnario(atual) && ehOperadorBinario(proximo))
                return erroAdjacente("Negação seguida de operador binário — falta operando", expr, i);
        }
        return true;
    }

    // CONVERSÃO INFIXA → PÓS-FIXA (Algoritmo Shunting Yard)

    private static List<String> paraPosfixo(String expr) {
        List<String> saida = new ArrayList<>();
        Stack<Character> ops = new Stack<>();

        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);

            if (Character.isWhitespace(c))
                continue;

            if (Character.isLetter(c)) {
                saida.add(String.valueOf(c));
            } else if (c == '(') {
                ops.push(c);
            } else if (c == ')') {
                while (!ops.isEmpty() && ops.peek() != '(') {
                    saida.add(String.valueOf(ops.pop()));
                }
                if (!ops.isEmpty() && ops.peek() == '(')
                    ops.pop();
                else {
                    System.err.println("Erro de sintaxe: Parênteses desbalanceados durante conversão para pós-fixa.");
                    return Collections.emptyList();
                }
            } else if (ehOperador(c)) {
                while (!ops.isEmpty() && ehOperador(ops.peek()) && prioridadeOP(ops.peek()) >= prioridadeOP(c)) {
                    saida.add(String.valueOf(ops.pop()));
                }
                ops.push(c);
            }
        }

        while (!ops.isEmpty()) {
            if (ops.peek() == '(') {
                System.err.println("Erro de sintaxe: Parênteses desbalanceados durante conversão para pós-fixa.");
                return Collections.emptyList();
            }
            saida.add(String.valueOf(ops.pop()));
        }

        return saida;
    }

    // AVALIAÇÃO DA EXPRESSÃO PÓS-FIXA

    private static boolean avaliar(String expr, List<Character> variaveis, boolean[] valores) {
        Map<Character, Boolean> mapa = new HashMap<>();
        for (int i = 0; i < variaveis.size(); i++) {
            mapa.put(variaveis.get(i), valores[i]);
        }

        List<String> posfixo = paraPosfixo(expr);
        if (posfixo.isEmpty() && !expr.isEmpty()) {
            return false;
        }
        Stack<Boolean> pilha = new Stack<>();

        for (String token : posfixo) {
            char c = token.charAt(0);

            if (Character.isLetter(c)) {
                pilha.push(mapa.get(c));
            } else if (ehOperadorUnario(c)) {
                if (pilha.isEmpty()) {
                    System.err.println("Erro de sintaxe: Operador unário '" + c + "' sem operando.");
                    return false;
                }
                boolean a = pilha.pop();
                pilha.push(Conectivo.NEGACAO.aplicar(a));
            } else if (ehOperador(c)) {
                if (pilha.size() < 2) {
                    System.err.println("Erro de sintaxe: Operador binário '" + c + "' sem operandos suficientes.");
                    return false;
                }
                boolean b = pilha.pop();
                boolean a = pilha.pop();
                switch (c) {
                    case '∧', '&' -> pilha.push(Conectivo.CONJUNCAO.aplicar(a, b));
                    case '∨', '|' -> pilha.push(Conectivo.DISJUNCAO.aplicar(a, b));
                    case '→' -> pilha.push(Conectivo.CONDICIONAL.aplicar(a, b));
                    case '↔' -> pilha.push(Conectivo.BICONDICIONAL.aplicar(a, b));
                    default -> {
                        System.err.println("Erro interno: Operador desconhecido '" + c + "'.");
                        return false;
                    }
                }
            }
        }
        if (pilha.size() != 1) {
            System.err.println("Erro de sintaxe: Expressão malformada, resultado ambíguo.");
            return false;
        }
        return pilha.pop();
    }

    // PROGRAMA PRINCIPAL

    public static void main(String[] args) {
        // Configura UTF-8 para exibir símbolos Unicode corretamente no console
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

        Scanner sc = new Scanner(System.in, StandardCharsets.UTF_8);

        while (true) {
            System.out.println("\nDigite a expressão (ex: A&B, !A|B, (A->B)<->C) ou digite 'sair' para encerrar ");
            String entrada = sc.nextLine().trim();

            if (entrada.isEmpty() || entrada.equalsIgnoreCase("sair")) {
                System.out.println("Encerrando. Até mais!");
                break;
            }

            String expr = entrada.toUpperCase().replace(" ", "");
            expr = preprocessarExpressao(expr);

            if (!validarExpressao(expr)) {
                continue;
            }

            List<Character> variaveis = extrairVariaveis(expr);
            List<Boolean> resultados = processarTabela(expr, variaveis);
            analisarResultados(resultados);
        }

        sc.close();
    }

    private static List<Character> extrairVariaveis(String expr) {
        Set<Character> vars = new TreeSet<>();
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (Character.isLetter(c)) {
                vars.add(c);
            }
        }
        return new ArrayList<>(vars);
    }

    private static List<Boolean> processarTabela(String expr, List<Character> variaveis) {
        int nVars = variaveis.size();
        int totalLinhas = 1 << nVars;
        List<Boolean> resultados = new ArrayList<>();

        // Cabeçalho
        for (Character v : variaveis) {
            System.out.print(v + "\t");
        }
        System.out.println("R");

        // Cada linha é uma combinação de V/F
        for (int i = 0; i < totalLinhas; i++) {
            boolean[] valores = new boolean[nVars];
            for (int j = 0; j < nVars; j++) {
                valores[j] = (i & (1 << (nVars - 1 - j))) == 0;
            }

            boolean resultado = avaliar(expr, variaveis, valores);
            resultados.add(resultado);

            for (boolean v : valores) {
                System.out.print(boolToVF(v) + "\t");
            }
            System.out.println("| " + boolToVF(resultado));
        }

        return resultados;
    }

    private static void analisarResultados(List<Boolean> resultados) {
        boolean ehTautologia = true;
        boolean ehContradicao = true;

        for (Boolean res : resultados) {
            if (!res)
                ehTautologia = false;
            if (res)
                ehContradicao = false;
        }

        System.out.println("\n--- Análise da Expressão ---");
        if (ehTautologia) {
            System.out.println("A expressão é uma TAUTOLOGIA.");
        } else if (ehContradicao) {
            System.out.println("A expressão é uma CONTRADIÇÃO.");
        } else {
            System.out.println("A expressão é uma CONTINGÊNCIA.");
        }
    }
}