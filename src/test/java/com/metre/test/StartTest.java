package com.metre.test;

import com.elgin.tef.enums.Acao;
import com.elgin.tef.enums.Operacao;
import com.elgin.tef.impl.TEFElgin;
import com.elgin.tef.enums.TipoColeta;
import com.elgin.tef.inputs.DadosPagamentoTef;
import com.elgin.tef.retornos.BaseReturn;
import com.google.gson.JsonObject;
import com.sun.prism.shader.AlphaTexture_RadialGradient_AlphaTest_Loader;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.rmi.UnexpectedException;
import java.util.Scanner;

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

                sequencia = Integer.parseInt(ExtriarConteudoTEF(retorno, "sequencial"))+1;
                JsonObject pendencias = tef.VerificarPendencia();

//                Boolean temPendencias = pendencias.toString().contains("Erro ao verificar se existem") ? false  : ExtriarConteudoTEF(pendencias, "existeTransacaoPendente") == "false" ? false  : (pendencias.toString().contains("Transacao em andamento") ? true : true);
//                if(!temPendencias){
                    int sequenciaColeta = sequencia;
                    JsonObject obj;
                    obj =  tef.RealizarPagamentoTEF(Operacao.CREDITO, new DadosPagamentoTef(sequenciaColeta+"", new BigDecimal("10.2")), false);
                    Boolean emColeta = true;
                  while(emColeta){

                      String tipo = ExtriarConteudoTEF(obj, "automacao_coleta_palavra_chave");
                      System.out.println("TIPO "+tipo);
                      if(tipo.equals("transacao_valor")){
//                          Scanner myObj = new Scanner(System.in);  // Create a Scanner object
//                          System.out.println("Informe o valor!");
//                          String valor = "1000";  // Read user input
                          obj = tef.RealizarPagamentoTEF2(Operacao.CREDITO, "{\"automacao_coleta_informacao\":\"10.04\",\"automacao_coleta_retorno\":\"0\",\"automacao_coleta_sequencial\":\"1\"}", false);
                          if(obj.toString().contains("Transacao em andamento")){
                              String j = tef.ConfirmarOperacaoTEF(1, Acao.CANCELAR);
                              System.out.println(j);
                          }
                      }else if(tipo.equals("transacao_tipo_cartao")){
                          System.out.println("SELECIONADO CREDITO");
                          obj = tef.RealizarPagamentoTEF2(Operacao.CREDITO, "{\"automacao_coleta_informacao\":\"Credito\",\"automacao_coleta_retorno\":\"0\",\"automacao_coleta_sequencial\":\"2\"}", false);
                      }else if(tipo.equals("transacao_cartao_numero")){
                          obj = tef.RealizarPagamentoTEF2(Operacao.CREDITO, "{\"automacao_coleta_retorno\":\"0\",\"automacao_coleta_sequencial\":\"3\"}", false);
                      }else if(tipo.equals("transacao_pagamento")){
                          System.out.println("SELCIONADO A VISTA");
                          obj = tef.RealizarPagamentoTEF2(Operacao.CREDITO, "{\"automacao_coleta_informacao\":\"1-A vista\",\"automacao_coleta_retorno\":\"0\",\"automacao_coleta_sequencial\":\"4\"}", false);
                          obj.addProperty("automacao_coleta_retorno","0");
                      }else if(tipo.equals("wait_password")){
                          obj = tef.RealizarPagamentoTEF2(Operacao.CREDITO, "{\"automacao_coleta_retorno\":\"0\",\"automacao_coleta_sequencial\":\"5\"}", false);
                          obj.addProperty("automacao_coleta_retorno","0");
                      }else if(tipo.equals("wait_process")){
                          obj = tef.RealizarPagamentoTEF2(Operacao.CREDITO, "{\"automacao_coleta_retorno\":\"0\",\"automacao_coleta_sequencial\":\"6\"}", false);
                          obj.addProperty("automacao_coleta_retorno","0");
                      }else if(tipo.equals("approved")){
                          obj = tef.RealizarPagamentoTEF2(Operacao.CREDITO, "{\"automacao_coleta_retorno\":\"0\",\"automacao_coleta_sequencial\":\"7\"}", false);
                          obj.addProperty("automacao_coleta_retorno","999999");
                      }

                      String ret = ExtriarConteudoTEF(obj, "automacao_coleta_retorno");
                      emColeta = ret.equalsIgnoreCase("0") || ret.equals("wait_password") ||  ret.equals("wait_process") || ret.equals("approved");
                      System.out.println(obj);
                      System.out.println("EM COLETA "+emColeta +" - "+ ExtriarConteudoTEF(obj, "automacao_coleta_retorno"));
                  }

                    System.out.println(obj.get("tef").getAsJsonObject().get("mensagemResultado"));
                  tef.ConfirmarOperacaoTEF(1, Acao.CONFIRMAR);
                  tef.FinalizarOperacaoTEF(1);

//                }else{
//                    System.err.println("Existem transações pendentes no TEF!");
//                }


            }
        }).start();

//       BaseReturn coleta =  tef.RealizarColetaPinPad(TipoColeta.CNPJ, true);
//        if(coleta.getTef().getRetorno().equals("1") ){
//            System.out.println("DADOS CAPTURADOS DO PIN PAD "+coleta.getTef().getResultadoCapturaPinPad());
//        }


    }

    private static String ExtriarConteudoTEF(JsonObject obj, String key) {
      try {
          String mensagem = obj.get("tef").toString().contains("mensagemResultado") ? obj.get("tef").getAsJsonObject().get("mensagemResultado").getAsString() : "";
          if(mensagem.equals("AGUARDE A SENHA")) {
              return "wait_password";
          }else   if(mensagem.contains("Processando a transacao") || mensagem.contains("SOLICITANDO AUTORIZACAO")){
                  return "wait_process";
          }else   if(mensagem.contains("APROVADA")){
              return "approved";
          }else {
              return obj.get("tef").getAsJsonObject().get(key).getAsString();
          }
      }catch (Exception e){
          System.out.println(obj);
          e.printStackTrace();
          System.exit(1);
      }
      return "";
    }



}
