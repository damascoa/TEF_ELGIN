package com.elgin.tef.retornos;

import com.google.gson.Gson;

public class BaseReturn {
    //0	sucesso	Com confirmação da Aplicação Comercial
    //1	sucesso	Sem confirmação da Aplicação Comercial
    // > 1 (Erro) verificar na documentação https://elgindevelopercommunity.github.io/group__t24.html#tef_api_informacoes
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

    public BaseReturn convert(String json) {
        return new Gson().fromJson(json, BaseReturn.class);
    }
}
