package com.elgin.tef.enums;

public enum TipoColeta {

    RG(1),
    CPF(2),
    CNPJ(3),
    TELEFONE(4);

    private int codigo;

    TipoColeta(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }
}
