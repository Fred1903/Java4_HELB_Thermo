package com.example;


import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import com.example.Cell;

import javafx.geometry.Pos;

public class CellConfigurationView{
    private static final int minWidth = 350;
    private static final int minHeight = 450;
    
    private Button submitButton;

    public void display(Cell cell,int row, int col){
        Stage window = new Stage();        
        window.initModality(Modality.APPLICATION_MODAL); //focus sur la fenetre
        window.setTitle("Cell Configuration : ");
        window.setMinWidth(minWidth);
        window.setMinHeight(minHeight);

        
        Label cellPositionLabel = createLabel("Position de la cellule : \nLigne : "+row+" Colonne : "+col);
        Label defineAsDeadCellLabel = createLabel("Définir comme cellule morte");
        Label defineAsHeatCellLabel = createLabel("Définir comme source de chaleur");
        Label textTemperatureLabel = createLabel("T° de la source quand activée");

        ComboBox<Integer> temperatureCombobox = new ComboBox<Integer>(); //0 min, 100 max 
        for (int i = 0; i <= 100; i++) {
            temperatureCombobox.getItems().add(i);
        }
        temperatureCombobox.setVisibleRowCount(10); //Fait que on voit que 10 nombre a la fois et pas tout
        submitButton = new Button("Valider");
        submitButton.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-padding:30px; -fx-border-radius:15px;");

        CheckBox defineAsDeadCellCheckbox = new CheckBox();
        defineAsDeadCellCheckbox.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-padding:30px; -fx-border-radius:15px;");
        CheckBox defineAsHeatCellCheckbox = new CheckBox();
        defineAsHeatCellCheckbox.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-padding:30px; -fx-border-radius:15px;");


        HBox defineAsDeadCellHbox = new HBox(50);
        HBox defineAsHeatCellHbox = new HBox(50);
        HBox defineTemperatureHBox = new HBox(50);

        defineAsHeatCellHbox.getChildren().addAll(defineAsHeatCellLabel, defineAsHeatCellCheckbox);
        defineAsDeadCellHbox.getChildren().addAll(defineAsDeadCellLabel, defineAsDeadCellCheckbox);
        defineTemperatureHBox.getChildren().addAll(textTemperatureLabel,temperatureCombobox);

        VBox configurationLayout = new VBox(30);
        configurationLayout.getChildren().addAll(cellPositionLabel,defineAsDeadCellHbox,defineAsHeatCellHbox,defineTemperatureHBox,submitButton);
        configurationLayout.setAlignment(Pos.CENTER);

        if(cell.isHeatCell()){
            System.out.println("heat cell");
            defineAsHeatCellCheckbox.setSelected(true);
            defineAsDeadCellCheckbox.setDisable(true);
        }


        defineAsDeadCellCheckbox.setOnAction(event -> {
            if (defineAsDeadCellCheckbox.isSelected()) {
                defineAsHeatCellCheckbox.setDisable(true);
            }
            else{
                defineAsHeatCellCheckbox.setDisable(false);
            }
        });
        
        defineAsHeatCellCheckbox.setOnAction(event -> {
            if (defineAsHeatCellCheckbox.isSelected()) {
                defineAsDeadCellCheckbox.setDisable(true);
            }
            else{
                defineAsDeadCellCheckbox.setDisable(false);
            }
        });

        temperatureCombobox.valueProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Valeur sélectionnée : " + newVal);
        });
        

        Scene scene = new Scene(configurationLayout);
        window.setScene(scene);
        window.showAndWait();        
    }

    public Button getSubmitButton(){
        return Button();
    }

    private static Label createLabel(String text){
        Label label = new Label(text);
        label.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-padding:30px; -fx-border-radius:15px; -fx-margin:30px");
        return label;
    }


}