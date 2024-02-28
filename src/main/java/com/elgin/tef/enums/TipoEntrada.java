package com.elgin.tef.enums;

import java.util.Arrays;

public enum TipoEntrada {
    FLEXIVEL("*"),
    ALFABETICO("A"),
    DATAHORA("D"),
    NUMERO("N"),
    ALFANUMERICO("X");




    private String tipo;

    TipoEntrada(String tipo) {
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }

    public TipoEntrada toEnum(String tipo){
        return Arrays.stream(TipoEntrada.values()).filter(
                p -> p.getTipo().equals(tipo)
        ).findFirst().orElse(null);
    }
}
