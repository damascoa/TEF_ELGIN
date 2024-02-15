package com.elgin.tef.enums;

public enum Acao {
    CANCELAR(0),
    CONFIRMAR(1);

    private int codigo;

    Acao(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }
}
