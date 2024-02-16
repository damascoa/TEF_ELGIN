package com.metre.test;

import com.elgin.tef.enums.Operacao;
import com.elgin.tef.impl.TEFElgin;
import com.elgin.tef.enums.TipoColeta;
import com.elgin.tef.inputs.DadosPagamentoTef;
import com.elgin.tef.retornos.BaseReturn;
import com.google.gson.JsonObject;

import java.math.BigDecimal;
import java.rmi.UnexpectedException;

public class StartTest {
    public static JsonObject retorno;
    public static void main(String[] args) throws UnexpectedException {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Integer sequencia = 0;

                TEFElgin tef = new TEFElgin();
                try {
                    tef.SetClientTCP("127.0.0.1", 60906);
                } catch (UnexpectedException e) {
                    e.printStackTrace();
                }
                try {
                    tef.ConfigurarDadosPDV("Metre Sistemas", "v1.06.000", "Metre", "01", "T0004");
                } catch (UnexpectedException e) {
                    e.printStackTrace();
                }
                 retorno = tef.IniciarOperacaoTEF();

                sequencia = Integer.parseInt(retorno.get("tef").getAsJsonObject().get("sequencial").getAsString())+1;
//                if(retorno.getCodigo() != 0 && retorno.getCodigo() != 1){
//                    tef.FinalizarOperacaoTEF(1);
//                }
                Boolean novatransacao = true;
                DadosPagamentoTef dpt =  new DadosPagamentoTef("1", new BigDecimal("10.22"));
                retorno = tef.RealizarPagamentoTEF(Operacao.SELECIONA, dpt, novatransacao);

                    dpt  = new DadosPagamentoTef(null,null);
                    dpt.setAutomacao_coleta_retorno(retorno.get("tef").getAsJsonObject().get("automacao_coleta_retorno").getAsString());
                    dpt.setAutomacao_coleta_sequencial(retorno.get("tef").getAsJsonObject().get("automacao_coleta_sequencial").getAsString());
                    dpt.setSequencia("1");
//
                    retorno =  tef.RealizarPagamentoTEF(Operacao.CREDITO, dpt, false);
                    System.out.println("HERE");

                while(true){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();

//       BaseReturn coleta =  tef.RealizarColetaPinPad(TipoColeta.CNPJ, true);
//        if(coleta.getTef().getRetorno().equals("1") ){
//            System.out.println("DADOS CAPTURADOS DO PIN PAD "+coleta.getTef().getResultadoCapturaPinPad());
//        }


    }
}
