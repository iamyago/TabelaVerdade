package TabelaVerdade;

import java.util.*;

public class TabelaVerdade {

    // É pra retornar V e F ao inves de true ou false por causa do boolean.
    public static String boolToVF(boolean b) {
        if (b) {
            return "V";
        } else {
            return "F";
        }
    }

    public static int prioridadeOP (char op) {
        return switch (op) {
            case '¬' -> 5;
            case '∧' -> 4;
            case '∨' -> 3;
            case '→' -> 2;
            case '↔' -> 1;
            default -> 0;
        };
    }

    public static boolean operadores(char c) {
        return c == '¬' || c == '∧' || c == '∨' || c == '→' || c == '↔';
    }

    // fConverte a expressão infixa (A ∧ B) para pós-fixa (AB∧)
    public static List<String> toPostfix(String expr) {
        List<String> out = new ArrayList<>(); // saída pós-fixa
        // ela é útil para guardar operadores enquanto a expressão está sendo convertida.
        Stack<Character> ops = new Stack<>(); // pilha de operadores

        // Percorre a expressão caractere por caractere.
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);

            if (Character.isWhitespace(c)) continue;
            // converter para caracter em String.
            if (Character.isLetter(c)) {
                out.add(String.valueOf(c));
            } else if (c == '(') {
                ops.push(c);
            } else if (c == ')') {
                while (!ops.isEmpty() && ops.peek() != '(') {
                    out.add(String.valueOf(ops.pop()));
                }
                if (!ops.isEmpty() && ops.peek() == '(') ops.pop();
            } else if (operadores(c)) {
                while (!ops.isEmpty() && operadores(ops.peek()) && prioridadeOP(ops.peek()) >= prioridadeOP(c)) {
                    out.add(String.valueOf(ops.pop()));
                }
                ops.push(c);
            }
        }

        while (!ops.isEmpty()) {
            out.add(String.valueOf(ops.pop()));
        }

        return out;
    }

    public static boolean avaliar(String expr, List<Character> varlist, boolean[] valores) {
        Map<Character, Boolean> mapa = new HashMap<>();
        for (int i = 0; i < varlist.size(); i++) {
            mapa.put(varlist.get(i), valores[i]);
        }

        List<String> postfix = toPostfix(expr);
        Stack<Boolean> pilha = new Stack<>();

        for (String t : postfix) {
            char c = t.charAt(0);

            if (Character.isLetter(c)) {
                pilha.push(mapa.get(c));
            } else if (c == '¬') {
                boolean a = pilha.pop();
                pilha.push(Conectivo.NEGACAO.aplicar(a));
            } else if (c == '∧') {
                boolean b = pilha.pop();
                boolean a = pilha.pop();
                pilha.push(Conectivo.CONJUNCAO.aplicar(a, b));
            } else if (c == '∨') {
                boolean b = pilha.pop();
                boolean a = pilha.pop();
                pilha.push(Conectivo.DISJUNCAO.aplicar(a, b));
            } else if (c == '→') {
                boolean b = pilha.pop();
                boolean a = pilha.pop();
                pilha.push(Conectivo.CONDICIONAL.aplicar(a, b));
            } else if (c == '↔') {
                boolean b = pilha.pop();
                boolean a = pilha.pop();
                pilha.push(Conectivo.BICONDICIONAL.aplicar(a, b));
            }
        }

        return pilha.pop();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Digite a expressão (ex: A∧B, ¬A∨B, (A→B)↔C): ");
        String expr = sc.nextLine().toUpperCase().replace(" ", "");

        Set<Character> vars = new HashSet<>();
        for (char c : expr.toCharArray()) {
            if (Character.isLetter(c)) vars.add(c);
        }

        List<Character> varlist = new ArrayList<>(vars);
        Collections.sort(varlist);

        int nVars = varlist.size();
        int totalLinhas = 1 << nVars;

        for (Character v : varlist) {
            System.out.print(v + "\t");
        }
        System.out.println("R");

        for (int i = 0; i < totalLinhas; i++) {
            boolean[] valores = new boolean[nVars];

            for (int j = 0; j < nVars; j++) {
                valores[j] = (i & (1 << (nVars - 1 - j))) == 0;
            }

            boolean resultado = avaliar(expr, varlist, valores);

            for (boolean v : valores) {
                System.out.print(boolToVF(v) + "\t");
            }
            System.out.println("| " + boolToVF(resultado));
        }

        sc.close();
    }
}
