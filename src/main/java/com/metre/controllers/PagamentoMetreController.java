package com.metre.controllers;

import com.elgin.tef.enums.Acao;
import com.elgin.tef.enums.Operacao;
import com.elgin.tef.impl.TEFElgin;
import com.elgin.tef.inputs.DadosPagamentoTef;
import com.google.gson.JsonObject;
import com.metre.app.Sessao;
import com.metre.listener.TitleListener;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.net.URL;
import java.rmi.UnexpectedException;
import java.util.List;
import java.util.ResourceBundle;

public class PagamentoMetreController implements Initializable, TitleListener {
    @FXML
    public Label status;
    @FXML
    public VBox cardProcess, cardStart;
    @FXML
    public TextField iptValor;

    public static JsonObject retorno;
    public BooleanProperty pagamentoInPregress = new SimpleBooleanProperty();

    public StringProperty palavraChave = new SimpleStringProperty();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pagamentoInPregress.setValue(false);
        cardProcess.visibleProperty().bind(pagamentoInPregress);
        cardStart.visibleProperty().bind(pagamentoInPregress.not());
    }
    @FXML
    public void pagarCredito(){
        TEFElgin tef = new TEFElgin(this);
      new Thread(new Runnable() {
          @Override
          public void run() {
              pagamentoInPregress.setValue(true);
              try {
                  tef.SetClientTCP("127.0.0.1", 60906);
                  tef.ConfigurarDadosPDV("Metre Sistemas", "v1.06.000", "Metre", "01", "T0004");
              } catch (UnexpectedException e) {
                  e.printStackTrace();
              }
              retorno = tef.IniciarOperacaoTEF();
              int sequencia = retorno.getAsJsonObject("tef").get("sequencial").getAsInt()+1;
              //VALIDA SE TEM PENDENCIAS
              retorno = tef.RealizarPagamentoTEF(Operacao.CREDITO, new DadosPagamentoTef(sequencia+"", new BigDecimal(iptValor.getText())), true);
              System.out.println(retorno);
              //FAZ PAGAMENTO
              retorno = tef.RealizarPagamentoTEF(Operacao.CREDITO, new DadosPagamentoTef(sequencia + "", new BigDecimal(iptValor.getText())), false);
              System.out.println(retorno);

              Integer coletaSequencial = 0;
              while(retorno.getAsJsonObject("tef").asMap().containsKey("automacao_coleta_retorno") &&
                      retorno.getAsJsonObject("tef").get("automacao_coleta_retorno").getAsString().equals("0")) {
                  coletaSequencial = retorno.getAsJsonObject("tef").get("automacao_coleta_sequencial").getAsInt();
                  retorno = tef.RealizarPagamentoTEF2(Operacao.CREDITO, "{\"automacao_coleta_retorno\":\"0\",\"automacao_coleta_sequencial\":\""+coletaSequencial+"\"}", false);

                  coletaSequencial = retorno.getAsJsonObject("tef").asMap().containsKey("automacao_coleta_sequencial") ? retorno.getAsJsonObject("tef").get("automacao_coleta_sequencial").getAsInt() : null;
                  System.out.println(retorno);

                  if(retorno.getAsJsonObject("tef").asMap().containsKey("automacao_coleta_palavra_chave") && coletaSequencial != null
                          && retorno.getAsJsonObject("tef").get("automacao_coleta_palavra_chave").getAsString().equalsIgnoreCase("transacao_pagamento")){
                      retorno = tef.RealizarPagamentoTEF2(Operacao.CREDITO, "{\"automacao_coleta_informacao\":\"1-A vista\",\"automacao_coleta_retorno\":\"0\",\"automacao_coleta_sequencial\":\""+coletaSequencial+"\"}", false);
                      coletaSequencial = retorno.getAsJsonObject("tef").get("automacao_coleta_sequencial").getAsInt();
                      System.out.println(retorno);
                  }
              }
              System.out.println("while morreu!");
              Integer sequencial = retorno.getAsJsonObject("tef").asMap().containsKey("sequencial") ? retorno.getAsJsonObject("tef").get("sequencial").getAsInt()  : null;
              tef.ConfirmarOperacaoTEF(sequencial, Acao.CONFIRMAR);
              tef.FinalizarOperacaoTEF(sequencial);
              System.out.println(tef.dadosAprovacao);
          }
      }).start();

    }
    @FXML
    public void pagarDebito(){
        pagamentoInPregress.setValue(true);
    }

    public void setStatusLabel(String text){
        Platform.runLater(() -> {
                status.setText(text);
        });
    }

    @Override
    public void change(String string) {
        setStatusLabel(string);
    }

    @Override
    public void changePalavraChave(String chave) {
        palavraChave.setValue(chave);
        System.out.println("chave: "+chave);
    }

    @Override
    public void changeOptions(List<String> options) {

    }
}
