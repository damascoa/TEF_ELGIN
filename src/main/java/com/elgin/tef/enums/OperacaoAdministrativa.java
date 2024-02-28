package com.elgin.tef.enums;

public enum OperacaoAdministrativa {
    SELECIONA(0),
    CANCELAMENTO(1),
    PENDENCIAS(2),
    REIMPRESSAO(3);


    private int codigo;

    OperacaoAdministrativa(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }
}
