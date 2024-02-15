package com.elgin.tef.util;

import java.math.BigDecimal;

public class MathUtil {

    public static String converterBigDecimalParaStringSemPonto(BigDecimal valor) {
        // Multiplica o valor por 100 para converter o decimal em um número inteiro
        BigDecimal valorMultiplicado = valor.multiply(new BigDecimal("100"));
        // Converte para String e remove qualquer ponto decimal
        // Assume-se que não haverá parte fracionária após a multiplicação por 100
        String resultado = valorMultiplicado.toBigInteger().toString();
        return resultado;
    }
}
