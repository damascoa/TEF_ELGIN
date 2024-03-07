package com.metre.controllers;


import com.elgin.tef.enums.Acao;
import com.elgin.tef.enums.OperacaoAdministrativa;
import com.elgin.tef.impl.TEFElgin;
import com.google.gson.JsonObject;
import com.metre.app.Sessao;
import com.metre.listener.TitleListener;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

public class AdministrativoController implements Initializable, TitleListener {
    public static JsonObject retorno;
    @FXML
    public ListView<String> listOptions;
    @FXML
    public Label titulo;
    @FXML
    public TextField txtIn2;
    @FXML
    public Button btnok;


    public Boolean confirmed = false;


    public AdministrativoController() {
        System.out.println("call const");
        Sessao.tef.setTitleChangeListener(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        listOptions.setVisible(false);
        txtIn2.setVisible(false);
        btnok.setVisible(false);

        System.out.println("initialize");
               new Thread(() -> {
                   Sessao.tef.IniciarOperacaoTEF();

                   retorno = Sessao.tef.RealizarAdmTEF(OperacaoAdministrativa.SELECIONA, "{}", true);
                   int coletaSequencial = 0;

                   while(Sessao.tef.isEmColeta()){

                       coletaSequencial = retorno.getAsJsonObject("tef").get("automacao_coleta_sequencial").getAsInt();
                       String opcao = null;
                       if(Sessao.tef.emColeta){
                           if(Sessao.tef.getOpcoes().size() > 1){
                               listOptions.setVisible(true);
                               txtIn2.setVisible(false);
                               btnok.setVisible(false);
                               while(listOptions.getSelectionModel().getSelectedItem() == null){
                               }
                           }else{
                               listOptions.setVisible(false);
                               txtIn2.setVisible(true);
                               btnok.setVisible(true);
                               while(!confirmed && txtIn2.getText() != null){
                               }
                           }
                           opcao =  Sessao.tef.getOpcoes().size() > 1 ?  Sessao.tef.getOpcoes().get(listOptions.getSelectionModel().getSelectedIndex()).trim() : txtIn2.getText();
                           retorno = Sessao.tef.RealizarAdmTEF(OperacaoAdministrativa.SELECIONA, "{\"automacao_coleta_informacao\":\""+opcao+"\",\"automacao_coleta_retorno\":\"0\",\"automacao_coleta_sequencial\":\""+coletaSequencial+"\"}", false);
                       }else{
                           retorno = Sessao.tef.RealizarAdmTEF(OperacaoAdministrativa.SELECIONA, "{\"automacao_coleta_retorno\":\"0\",\"automacao_coleta_sequencial\":\""+coletaSequencial+"\"}", false);
                       }
                   }
                   System.out.println("FIM DA COLETA! ");
                   confirmed = false;
                   String mensagemResultado = Sessao.tef.getMensagemResultado();
                   if(mensagemResultado.contains("Transacao em andamento")){
                       String op =  Sessao.tef.ConfirmarOperacaoTEF(retorno.getAsJsonObject("tef").get("sequencial").getAsInt(), Acao.CANCELAR);
                       System.out.println(op);
                   }else{
                       System.err.println(mensagemResultado);
                   }
               }).start();

    }

    @Override
    public void change(String string) {
        Platform.runLater(() -> titulo.setText(string));
    }

    @Override
    public void changePalavraChave(String chave) {

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
