package com.elgin.tef.retornos;

public class BaseReturn {
    private int codigo;
    private String mensagem;
    private Tef tef;

    public BaseReturn() {
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Tef getTef() {
        return tef;
    }

    public void setTef(Tef tef) {
        this.tef = tef;
    }
}
