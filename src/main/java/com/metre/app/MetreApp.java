package com.metre.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MetreApp extends Application {
    public static void main(String[] args) {
            launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        AnchorPane root = FXMLLoader.load(getClass().getResource("/fxml/PagamentoMetre.fxml"));
        setUserAgentStylesheet(STYLESHEET_MODENA);
        stage.setScene(new Scene(root));
        stage.setResizable(false);

        stage.show();
    }
}
