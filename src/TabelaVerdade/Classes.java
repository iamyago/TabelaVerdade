package TabelaVerdade;

import java.util.function.BiFunction;
import java.util.function.Function;

enum Conectivo implements OperacaoLogica {
    // Use BiFunction quando a lógica depende de dois valores
    //
    //Ex: soma, comparação, combinação de dados

    // Use Function quando você precisa transformar um valor em outro
    //
    //Ex: converter, formatar, mapear dados

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
        return operacaoUnaria.apply(p);  // q ignorado
    }

    public boolean aplicar(boolean p) {
        return false;
    }
}