package com.elgin.tef.enums;

public enum TipoCaptura {
    RG(1),
    CPF(2),
    CNPJ(3),
    TELEFONE(4),
    SELECAO(5),
    OPERADORA(6);

    private int codigo;

    TipoCaptura(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }
}
