package com.elgin.tef.enums;

public enum Operacao {
    SELECIONA(0),
    CREDITO(1),
    DEBITO(2),
    VOUCHER(3),
    FROTA(4),
    PRIVATE_LABEL(5);

    Operacao(int codigo) {
        this.codigo = codigo;
    }

    private int codigo;

    public int getCodigo() {
        return codigo;
    }
}
