package com.elgin.tef.inputs;

import com.elgin.tef.util.MathUtil;
import com.google.gson.JsonObject;

import java.math.BigDecimal;

public class DadosPagamentoTef {
    private String sequencia;
    private BigDecimal valor;

    private String automacao_coleta_retorno;
    private String automacao_coleta_sequencial;


    public DadosPagamentoTef(String sequencia, BigDecimal valor) {
        this.sequencia = sequencia;
        this.valor = valor;
    }

    public String getSequencia() {
        return sequencia;
    }

    public void setSequencia(String sequencia) {
        this.sequencia = sequencia;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getAutomacao_coleta_retorno() {
        return automacao_coleta_retorno;
    }

    public void setAutomacao_coleta_retorno(String automacao_coleta_retorno) {
        this.automacao_coleta_retorno = automacao_coleta_retorno;
    }

    public String getAutomacao_coleta_sequencial() {
        return automacao_coleta_sequencial;
    }

    public void setAutomacao_coleta_sequencial(String automacao_coleta_sequencial) {
        this.automacao_coleta_sequencial = automacao_coleta_sequencial;
    }

    public String toInput(){
        JsonObject object = new JsonObject();
        if(sequencia != null) {
            object.addProperty("sequencia", sequencia);
        }
        if(valor != null) {
            object.addProperty("valorTotal", valor.toString());
        }
        if(automacao_coleta_retorno != null){
            object.addProperty("automacao_coleta_retorno", automacao_coleta_retorno);
        }
        if(automacao_coleta_sequencial != null){
            object.addProperty("automacao_coleta_sequencial", automacao_coleta_sequencial);
        }
        return object.toString();
    }
}
