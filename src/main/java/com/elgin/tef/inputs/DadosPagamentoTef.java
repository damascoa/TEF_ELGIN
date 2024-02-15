package com.elgin.tef.inputs;

import com.elgin.tef.util.MathUtil;
import com.google.gson.JsonObject;

import java.math.BigDecimal;

public class DadosPagamentoTef {
    private String sequencia;
    private BigDecimal valor;

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


    public String toInput(){
        JsonObject object = new JsonObject();
        object.addProperty("sequencia", sequencia);
        object.addProperty("valorTotal", MathUtil.converterBigDecimalParaStringSemPonto(valor));
        return object.toString();
    }
}
