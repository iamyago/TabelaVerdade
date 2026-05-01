package TabelaVerdade;

import java.util.function.BiFunction;
import java.util.function.Function;

enum Conectivo implements OperacaoLogica {

    CONJUNCAO("∧", (p, q) -> p && q),
    DISJUNCAO("∨", (p, q) -> p || q),
    CONDICIONAL("→", (p, q) -> !p || q),
    BICONDICIONAL("↔", (p, q) -> p == q),
    NEGACAO("¬", p -> !p);

    public final String simbolo;
    public final BiFunction<Boolean, Boolean, Boolean> operacaoBinaria;
    public final Function<Boolean, Boolean> operacaoUnaria;

    Conectivo(String simbolo, BiFunction<Boolean, Boolean, Boolean> op) {
        this.simbolo = simbolo;
        this.operacaoBinaria = op;
        this.operacaoUnaria = null;
    }

    Conectivo(String simbolo, Function<Boolean, Boolean> op) {
        this.simbolo = simbolo;
        this.operacaoUnaria = op;
        this.operacaoBinaria = null;
    }

    public boolean aplicar(boolean p, boolean q) {
        if (operacaoBinaria != null) {
            return operacaoBinaria.apply(p, q);
        }
        throw new IllegalStateException("não aceita 2 parâmetros");
    }

    public boolean aplicar(boolean p) {
        if (operacaoUnaria != null) {
            return operacaoUnaria.apply(p);
        }
        throw new IllegalStateException("não aceita 1 parâmetro");
    }
}
