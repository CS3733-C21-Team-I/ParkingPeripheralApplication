package edu.wpi.cs3733.c21.teamI.view;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class SlipPayController extends Application {

  @FXML private TextField idField;

  @FXML private javafx.scene.control.Button payButton;

  @FXML private Pane slipPane;

  @FXML
  public void initialize() {
    slipPane.setVisible(false);
  }

  @FXML
  public void payButtonClicked(ActionEvent e) {
    slipPane.setVisible(true);
  }

  @FXML
  public void doneButtonClicked(ActionEvent e) {
    slipPane.setVisible(false);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    AnchorPane root = FXMLLoader.load(getClass().getResource("/fxml/SlipPay.fxml"));
    primaryStage.setTitle("Parking App");
    Scene applicationScene = new Scene(root, 973, 800);
    primaryStage.setScene(applicationScene);
    primaryStage.show();
  }
}
