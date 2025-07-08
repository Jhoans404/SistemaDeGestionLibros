/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gestionlibros.interfaz;

/**
 *
 * @author JHOANS
 */
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginController implements Initializable {

    @FXML
    private AnchorPane panelLogin;

    @FXML
    private Button btSalir;

    @FXML
    private void clickSalir(MouseEvent event) {
        // Código para cerrar la app, por ejemplo:
        Platform.exit();
    }
    
    @FXML
    private void actionEntrar(ActionEvent event){
        ConexionMongo conex = new ConexionMongo();
        conex.conectar();
    }

    @FXML
    private void minimizarVentana(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);

    }

    @FXML
    private void btnCrearCuenta(ActionEvent event) {
        System.out.println("Click me");
    }

    @FXML
    public void btnRecuerdame(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Recuerdame.fxml"));
            Parent nuevaVista = loader.load();

            if (panelLogin != null) {
                // Transición de salida
                if (!panelLogin.getChildren().isEmpty()) {
                    FadeTransition fadeOut = new FadeTransition(Duration.millis(300), panelLogin.getChildren().get(0));
                    fadeOut.setFromValue(1.0);
                    fadeOut.setToValue(0.0);

                    fadeOut.setOnFinished(e -> {
                        panelLogin.getChildren().setAll(nuevaVista);

                        // Transición de entrada
                        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), nuevaVista);
                        fadeIn.setFromValue(0.0);
                        fadeIn.setToValue(1.0);
                        fadeIn.play();
                    });

                    fadeOut.play();
                } else {
                    panelLogin.getChildren().setAll(nuevaVista);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

}
