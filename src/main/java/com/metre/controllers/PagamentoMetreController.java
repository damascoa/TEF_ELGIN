package com.metre.controllers;

import com.elgin.tef.enums.Acao;
import com.elgin.tef.enums.Operacao;
import com.elgin.tef.enums.OperacaoAdministrativa;
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
    public TextField iptValor,iptData, iptNsu, iptValorC;

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
      new Thread(() -> {
          pagamentoInPregress.setValue(true);
          pagar(Operacao.CREDITO);
      }).start();

    }
    @FXML
    public void cancelar(){
        retorno = new JsonObject();
        TEFElgin tef = new TEFElgin(this);
        try {
            tef.SetClientTCP("127.0.0.1", 60906);
            tef.ConfigurarDadosPDV("Metre Sistemas", "v1.06.000", "Metre", "01", "T0004");
        } catch (UnexpectedException e) {
            e.printStackTrace();
        }
        retorno = tef.IniciarOperacaoTEF();
        int sequencia = retorno.getAsJsonObject("tef").get("sequencial").getAsInt()+1;
        retorno = tef.RealizarAdmTEF(OperacaoAdministrativa.CANCELAMENTO, "{}", true);
        Integer coletaSequencial = 0;
        while(retorno.getAsJsonObject("tef").asMap().containsKey("automacao_coleta_retorno") &&
                retorno.getAsJsonObject("tef").get("automacao_coleta_retorno").getAsString().equals("0")) {
            coletaSequencial = retorno.getAsJsonObject("tef").get("automacao_coleta_sequencial").getAsInt();
            System.out.println("INLOOP");
            if(retorno.getAsJsonObject("tef").asMap().containsKey("automacao_coleta_palavra_chave") && coletaSequencial != null
                    && retorno.getAsJsonObject("tef").get("automacao_coleta_palavra_chave").getAsString().equalsIgnoreCase("transacao_data")){
                retorno =  tef.RealizarAdmTEF(OperacaoAdministrativa.CANCELAMENTO, "{\"automacao_coleta_informacao\":\"07/03/2024\",\"automacao_coleta_retorno\":\"0\",\"automacao_coleta_sequencial\":\""+coletaSequencial+"\"}", false);
                coletaSequencial = retorno.getAsJsonObject("tef").get("automacao_coleta_sequencial").getAsInt();
                System.out.println(retorno);
            }else if(retorno.getAsJsonObject("tef").asMap().containsKey("automacao_coleta_palavra_chave") && coletaSequencial != null
                    && retorno.getAsJsonObject("tef").get("automacao_coleta_palavra_chave").getAsString().equalsIgnoreCase("transacao_nsu")){
                retorno =  tef.RealizarAdmTEF(OperacaoAdministrativa.CANCELAMENTO, "{\"automacao_coleta_informacao\":\"001027\",\"automacao_coleta_retorno\":\"0\",\"automacao_coleta_sequencial\":\""+coletaSequencial+"\"}", false);
                System.out.println(retorno);
            }else if(retorno.getAsJsonObject("tef").asMap().containsKey("automacao_coleta_palavra_chave") && coletaSequencial != null
                    && retorno.getAsJsonObject("tef").get("automacao_coleta_palavra_chave").getAsString().equalsIgnoreCase("transacao_valor")){
                retorno =  tef.RealizarAdmTEF(OperacaoAdministrativa.CANCELAMENTO, "{\"automacao_coleta_informacao\":\"1110\",\"automacao_coleta_retorno\":\"0\",\"automacao_coleta_sequencial\":\""+coletaSequencial+"\"}", false);
                System.out.println(retorno);
            }else if(retorno.getAsJsonObject("tef").asMap().containsKey("automacao_coleta_opcao") && coletaSequencial != null
                    && retorno.getAsJsonObject("tef").get("automacao_coleta_opcao").getAsString().equalsIgnoreCase("Sim;Nao")) {
                retorno = tef.RealizarAdmTEF(OperacaoAdministrativa.CANCELAMENTO, "{\"automacao_coleta_informacao\":\"Sim\",\"automacao_coleta_retorno\":\"0\",\"automacao_coleta_sequencial\":\"" + coletaSequencial + "\"}", false);
                System.out.println(retorno);
            }else {
                retorno = tef.RealizarAdmTEF(OperacaoAdministrativa.CANCELAMENTO, "{\"automacao_coleta_retorno\":\"0\",\"automacao_coleta_sequencial\":\"" + coletaSequencial + "\"}", false);
                System.out.println(retorno);
            }

        }
        System.out.println("while morreu!");
        Integer sequencial = retorno.getAsJsonObject("tef").asMap().containsKey("sequencial") ? retorno.getAsJsonObject("tef").get("sequencial").getAsInt()  : null;
        if(retorno.toString().contains("cancelada") || retorno.toString().contains("CARTAO REMOVIDO")){
            tef.FinalizarOperacaoTEF(1);
        }else {
            tef.ConfirmarOperacaoTEF(sequencial, Acao.CONFIRMAR);
            tef.FinalizarOperacaoTEF(sequencial);
            System.out.println(retorno);
            System.out.println(tef.dadosAprovacao.getComprovanteDiferenciadoPortador());
        }
    }


    @FXML
    public void reimprimir(){
        retorno = new JsonObject();
        TEFElgin tef = new TEFElgin(this);
        try {
            tef.SetClientTCP("127.0.0.1", 60906);
            tef.ConfigurarDadosPDV("Metre Sistemas", "v1.06.000", "Metre", "01", "T0004");
        } catch (UnexpectedException e) {
            e.printStackTrace();
        }
        retorno = tef.IniciarOperacaoTEF();
        int sequencia = retorno.getAsJsonObject("tef").get("sequencial").getAsInt()+1;
        retorno = tef.RealizarAdmTEF(OperacaoAdministrativa.REIMPRESSAO, "{}", true);
        Integer coletaSequencial = 0;
        while(retorno.getAsJsonObject("tef").asMap().containsKey("automacao_coleta_retorno") &&
                retorno.getAsJsonObject("tef").get("automacao_coleta_retorno").getAsString().equals("0")) {
            coletaSequencial = retorno.getAsJsonObject("tef").get("automacao_coleta_sequencial").getAsInt();
            System.out.println("INLOOP");
            if(retorno.getAsJsonObject("tef").asMap().containsKey("automacao_coleta_palavra_chave") && coletaSequencial != null
                    && retorno.getAsJsonObject("tef").get("automacao_coleta_palavra_chave").getAsString().equalsIgnoreCase("transacao_data")){
                retorno =  tef.RealizarAdmTEF(OperacaoAdministrativa.REIMPRESSAO, "{\"automacao_coleta_informacao\":\"07/03/2024\",\"automacao_coleta_retorno\":\"0\",\"automacao_coleta_sequencial\":\""+coletaSequencial+"\"}", false);
                coletaSequencial = retorno.getAsJsonObject("tef").get("automacao_coleta_sequencial").getAsInt();
                System.out.println(retorno);
            }else if(retorno.getAsJsonObject("tef").asMap().containsKey("automacao_coleta_palavra_chave") && coletaSequencial != null
                    && retorno.getAsJsonObject("tef").get("automacao_coleta_palavra_chave").getAsString().equalsIgnoreCase("transacao_nsu")){
                retorno =  tef.RealizarAdmTEF(OperacaoAdministrativa.REIMPRESSAO, "{\"automacao_coleta_informacao\":\"001027\",\"automacao_coleta_retorno\":\"0\",\"automacao_coleta_sequencial\":\""+coletaSequencial+"\"}", false);
                System.out.println(retorno);
            }else if(retorno.getAsJsonObject("tef").asMap().containsKey("automacao_coleta_opcao") && coletaSequencial != null
                    && retorno.getAsJsonObject("tef").get("automacao_coleta_opcao").getAsString().equalsIgnoreCase("Via Portador;Via Lojista;Todos")) {
                retorno = tef.RealizarAdmTEF(OperacaoAdministrativa.REIMPRESSAO, "{\"automacao_coleta_informacao\":\"Todos\",\"automacao_coleta_retorno\":\"0\",\"automacao_coleta_sequencial\":\"" + coletaSequencial + "\"}", false);
                System.out.println(retorno);
            }else {
                retorno = tef.RealizarAdmTEF(OperacaoAdministrativa.REIMPRESSAO, "{\"automacao_coleta_retorno\":\"0\",\"automacao_coleta_sequencial\":\"" + coletaSequencial + "\"}", false);
                System.out.println(retorno);
            }
        }
        System.out.println("while morreu!");
        Integer sequencial = retorno.getAsJsonObject("tef").asMap().containsKey("sequencial") ? retorno.getAsJsonObject("tef").get("sequencial").getAsInt()  : null;
        if(retorno.toString().contains("cancelada") || retorno.toString().contains("CARTAO REMOVIDO")){
            tef.FinalizarOperacaoTEF(1);
        }else {
            tef.ConfirmarOperacaoTEF(sequencial, Acao.CONFIRMAR);
            tef.FinalizarOperacaoTEF(sequencial);
            System.out.println(retorno);
            System.out.println(tef.dadosAprovacao.getComprovanteDiferenciadoPortador());
        }
    }

    public void pagar(Operacao operacao){
        retorno = new JsonObject();
        TEFElgin tef = new TEFElgin(this);
        try {
            tef.SetClientTCP("127.0.0.1", 60906);
            tef.ConfigurarDadosPDV("Metre Sistemas", "v1.06.000", "Metre", "01", "T0004");
        } catch (UnexpectedException e) {
            e.printStackTrace();
        }
        retorno = tef.IniciarOperacaoTEF();
        int sequencia = retorno.getAsJsonObject("tef").get("sequencial").getAsInt()+1;
        //VALIDA SE TEM PENDENCIAS
        retorno = tef.RealizarPagamentoTEF(operacao, new DadosPagamentoTef(sequencia+"", new BigDecimal(iptValor.getText())), true);
        System.out.println(retorno);
        //FAZ PAGAMENTO
        retorno = tef.RealizarPagamentoTEF(operacao, new DadosPagamentoTef(sequencia + "", new BigDecimal(iptValor.getText())), false);
        System.out.println(retorno);

        Integer coletaSequencial = 0;
        while(retorno.getAsJsonObject("tef").asMap().containsKey("automacao_coleta_retorno") &&
                retorno.getAsJsonObject("tef").get("automacao_coleta_retorno").getAsString().equals("0")) {
            coletaSequencial = retorno.getAsJsonObject("tef").get("automacao_coleta_sequencial").getAsInt();
            retorno = tef.RealizarPagamentoTEF2(operacao, "{\"automacao_coleta_retorno\":\"0\",\"automacao_coleta_sequencial\":\""+coletaSequencial+"\"}", false);

            coletaSequencial = retorno.getAsJsonObject("tef").asMap().containsKey("automacao_coleta_sequencial") ? retorno.getAsJsonObject("tef").get("automacao_coleta_sequencial").getAsInt() : null;
            System.out.println(retorno);

            if(retorno.getAsJsonObject("tef").asMap().containsKey("automacao_coleta_palavra_chave") && coletaSequencial != null
                    && retorno.getAsJsonObject("tef").get("automacao_coleta_palavra_chave").getAsString().equalsIgnoreCase("transacao_pagamento")){
                retorno = tef.RealizarPagamentoTEF2(operacao, "{\"automacao_coleta_informacao\":\"1-A vista\",\"automacao_coleta_retorno\":\"0\",\"automacao_coleta_sequencial\":\""+coletaSequencial+"\"}", false);
                coletaSequencial = retorno.getAsJsonObject("tef").get("automacao_coleta_sequencial").getAsInt();
                System.out.println(retorno);
            }
        }
        System.out.println("while morreu!");
        Integer sequencial = retorno.getAsJsonObject("tef").asMap().containsKey("sequencial") ? retorno.getAsJsonObject("tef").get("sequencial").getAsInt()  : null;
        if(retorno.toString().contains("cancelada") || retorno.toString().contains("CARTAO REMOVIDO")){
            tef.FinalizarOperacaoTEF(1);
        }else {
            tef.ConfirmarOperacaoTEF(sequencial, Acao.CONFIRMAR);
            tef.FinalizarOperacaoTEF(sequencial);
            System.out.println(tef.dadosAprovacao.getComprovanteDiferenciadoPortador());
        }
        pagamentoInPregress.setValue(false);
    }

    @FXML
    public void pagarDebito(){
        new Thread(() -> {
            pagamentoInPregress.setValue(true);
            pagar(Operacao.DEBITO);
        }).start();
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
