package edu.wpi.cs3733.c21.teamI.parking;

import java.util.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ParkingPriceController extends Application {
  class HBoxCell extends HBox {
    Label label = new Label();
    Button button = new Button();

    HBoxCell(String hours, String price) {
      super();

      label.setText(hours);
      label.setStyle("-fx-font-weight: bold");
      label.setMaxWidth(Double.MAX_VALUE);
      HBox.setHgrow(label, Priority.ALWAYS);
      HBox.setHgrow(button, Priority.ALWAYS);

      button.setText(price);

      button.setStyle("-fx-cursor: hand;-fx-background-color: #012D5A;");
      button.setTextFill(Color.WHITE);
      button.setOnAction(
          new EventHandler<ActionEvent>() {

            /** Implement what you want to be returned on click here */
            @Override
            public void handle(ActionEvent event) {
              System.out.println(button.getText());
            }
          });

      this.getChildren().addAll(label, button);
    }
  }

  public Parent createContent() {
    BorderPane layout = new BorderPane();

    Label priceTag = new Label("Price");

    Label hoursTag = new Label("Minutes");
    priceTag.setStyle("-fx-font-weight: bold");
    hoursTag.setStyle("-fx-font-weight: bold");

    HBox hBox = new HBox(hoursTag, priceTag);
    hBox.setHgrow(priceTag, Priority.ALWAYS);

    hBox.setMargin(hoursTag, new Insets(0, 380, 0, 0));

    layout.setTop(hBox);
    List<HBoxCell> list = new ArrayList<>();

    for (Map.Entry<Integer, Double> entry : ParkingSlip.pricingMap.entrySet()) {
      list.add(new HBoxCell(String.valueOf(entry.getKey()), String.valueOf(entry.getValue())));
    }

    ListView<HBoxCell> listView = new ListView<HBoxCell>();
    ObservableList<HBoxCell> myObservableList = FXCollections.observableList(list);
    listView.setItems(myObservableList);
    layout.setCenter(listView);
    return layout;
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    primaryStage.setScene(new Scene(createContent()));
    primaryStage.setWidth(500);
    primaryStage.setHeight(300);
    primaryStage.show();
  }

  // public void onClick(MouseEvent e) {}
}
