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
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

public class VendaCartaoController implements Initializable, TitleListener {
    public static JsonObject retorno;
    @FXML
    public TextField txtInput;
    @FXML
    public Label titulo;
    @FXML
    public TextArea output;
    @FXML
    public ListView<String> listOptions;
    @FXML
    public Button btnConfirm;

    public String selectedOption = null;

    public Boolean confirmed = false;


    public VendaCartaoController() {
        Sessao.tef.setTitleChangeListener(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void iniciarVenda(ActionEvent actionEvent) {

       new Thread(new Runnable() {
           @Override
           public void run() {
               retorno =  Sessao.tef.IniciarOperacaoTEF();
               Integer sequencia = retorno.getAsJsonObject("tef").get("sequencial").getAsInt()+1;
               retorno =  Sessao.tef.RealizarPagamentoTEF(Operacao.SELECIONA, new DadosPagamentoTef(sequencia+"", null), true);
               //VALIDAR SE NAO TEM TRANSACAO PENDENTE!
               System.out.println(retorno);
               if(retorno.get("tef").getAsJsonObject().asMap().containsKey("mensagemResultado") &&
                       retorno.get("tef").getAsJsonObject().get("mensagemResultado").getAsString().contains("Existem transações pendentes")){

//                   Sessao.tef.ConfirmarTodasTransacoesPendentes();
                   retorno = Sessao.tef.RealizarPagamentoTEF2(Operacao.SELECIONA, "{\"automacao_coleta_retorno\":\"0\",\"automacao_coleta_sequencial\":\""+sequencia+"\"}", false);
               }
               int coletaSequencial = 0;
               while(Sessao.tef.isEmColeta()){
                   coletaSequencial = retorno.getAsJsonObject("tef").get("automacao_coleta_sequencial").getAsInt();
                   if(Sessao.tef.emColeta){
                       String opcao = null;
                       if(Sessao.tef.getOpcoes().size() > 1){
                           listOptions.setVisible(true);
                           txtInput.setVisible(false);
                           btnConfirm.setVisible(false);
                           while(listOptions.getSelectionModel().getSelectedItem() == null){
                           }
                       }else{
                           listOptions.setVisible(false);
                           txtInput.setVisible(true);
                           btnConfirm.setVisible(true);
                           //AGUARDANDO ACAO OU SELECAO
                           while(!confirmed && txtInput.getText() != null){
                           }
                       }

                           opcao =  Sessao.tef.getOpcoes().size() > 1 ?  Sessao.tef.getOpcoes().get(listOptions.getSelectionModel().getSelectedIndex()).trim() : txtInput.getText();
                           retorno = Sessao.tef.RealizarPagamentoTEF2(Operacao.SELECIONA, "{\"automacao_coleta_informacao\":\""+opcao+"\",\"automacao_coleta_retorno\":\"0\",\"automacao_coleta_sequencial\":\""+coletaSequencial+"\"}", false);
                           confirmed = false;
                           txtInput.setText("");
                   }else{
                       retorno = Sessao.tef.RealizarPagamentoTEF2(Operacao.SELECIONA, "{\"automacao_coleta_retorno\":\"0\",\"automacao_coleta_sequencial\":\""+coletaSequencial+"\"}", false);
                       confirmed = false;
                       txtInput.setText("");
                   }
               }
               System.out.println("FIM DA COLETA! ");
               String mensagemResultado = Sessao.tef.getMensagemResultado();
               Integer sequencial = retorno.getAsJsonObject("tef").asMap().containsKey("sequencial") ? retorno.getAsJsonObject("tef").get("sequencial").getAsInt()  : null;
               System.out.println(retorno.getAsJsonObject("tef"));
               if(mensagemResultado.contains("Transacao em andamento")){
                   System.out.println("CANCELAR AÇAO");
                   String op =  Sessao.tef.ConfirmarOperacaoTEF(sequencial, Acao.CANCELAR);
                   System.out.println(op);
               }else{
                   System.err.println(mensagemResultado);
                   Sessao.tef.ConfirmarOperacaoTEF(sequencial, Acao.CONFIRMAR);
                   Sessao.tef.FinalizarOperacaoTEF(sequencial);
               }
               JsonObject o = Sessao.tef.FinalizarOperacaoTEF(sequencial);
               System.out.println(o);

               if(Sessao.tef.dadosAprovacao != null){
                   System.out.println("COMPROVANTE VIA CLIENTE");
                   output.setText(Sessao.tef.dadosAprovacao.getComprovanteDiferenciadoPortador());
                   System.out.println(Sessao.tef.dadosAprovacao.getComprovanteDiferenciadoPortador());
               }
           }
       }).start();
        
        
    }

    @FXML
    public void confirmarOperacao(ActionEvent actionEvent) {
        confirmed = true;
    }

    @Override
    public void change(String string) {
        Platform.runLater(() -> titulo.setText(string));
    }

    @Override
    public void changeOptions(List<String> options) {
        Platform.runLater(() ->listOptions.setItems(FXCollections.observableList(options)));


    }

    @FXML
    public void ok(ActionEvent actionEvent) {
        confirmed = true;
    }
}
