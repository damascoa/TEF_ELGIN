package com.metre.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainApp extends Application {
    public static void main(String[] args) {
            launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        AnchorPane root = FXMLLoader.load(getClass().getResource("/fxml/Home.fxml"));
        setUserAgentStylesheet(STYLESHEET_MODENA);
        stage.setScene(new Scene(root));
        stage.setResizable(false);

        stage.show();
    }
}
