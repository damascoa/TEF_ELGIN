package com.metre.controllers;

import com.elgin.tef.enums.Acao;
import com.elgin.tef.enums.Operacao;
import com.elgin.tef.enums.OperacaoAdministrativa;
import com.elgin.tef.impl.TEFElgin;
import com.google.gson.JsonObject;
import com.metre.app.Sessao;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.net.URL;
import java.rmi.UnexpectedException;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeController implements Initializable {
    @FXML
    public TextField iptValor;
    @FXML
    public TextArea txtLog;
    @FXML
    public ListView<String> list;

    JsonObject retorno;


    private static final int OPERACAO_ADM = 0;
    private static final int OPERACAO_VENDER = 1; // Adicione constantes conforme necessário
    private int operacaoAtual = OPERACAO_VENDER;

    // Simulação do Executor para rodar tarefas em background
    private ExecutorService executor = Executors.newSingleThreadExecutor();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
       Sessao.tef = new TEFElgin();

        try {
            Sessao.tef.SetClientTCP("127.0.0.1", 60906);
            Sessao.tef.ConfigurarDadosPDV("Metre Sistemas", "v1.06.000", "Metre", "01", "T0004");
        } catch (UnexpectedException e) {
            e.printStackTrace();
        }
    }



    @FXML
    public void vendaCartao(ActionEvent actionEvent) {
        Stage stage = new Stage();
        try {
            AnchorPane root = FXMLLoader.load(getClass().getResource("/fxml/VendaCartao.fxml"));
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @FXML
    public void opAdministrativo(ActionEvent actionEvent) {
        Stage stage = new Stage();
        try {
            AnchorPane root = FXMLLoader.load(getClass().getResource("/fxml/Administrativo.fxml"));
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}