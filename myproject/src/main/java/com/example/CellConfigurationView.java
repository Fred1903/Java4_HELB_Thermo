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
    
    private static Button submitButton;
    private static boolean clickedOnDeadCell=false;
    private static boolean clickedOnHeatCell=false;
    private static int choiceTemperature;

    private static Stage window;

    public static void display(Cell cell,int row, int col){ //Faut laisser en static ou pas ?
        //choiceTemperature = cell.getTemperature(); //On met la temperature a retourner de base=

        window = new Stage();        
        window.initModality(Modality.APPLICATION_MODAL); //focus sur la fenetre
        window.setTitle("Cell Configuration : ");
        window.setMinWidth(minWidth);
        window.setMinHeight(minHeight);

        
        Label cellPositionLabel = createLabel("Position de la cellule : \nLigne : "+row+" Colonne : "+col);
        Label defineAsDeadCellLabel = createLabel("Définir comme cellule morte");
        Label defineAsHeatCellLabel = createLabel("Définir comme source de chaleur");
        Label textTemperatureLabel = createLabel("T° de la source quand activée");

        ComboBox<Integer> temperatureCombobox = new ComboBox<Integer>(); //0 min, 100 max 
        for (int i = 0; i <= 100; i++) { //100 = max degrés a declarer qql part
            temperatureCombobox.getItems().add(i);
        }
        temperatureCombobox.setVisibleRowCount(10); //Fait que on voit que 10 nombre a la fois et pas tout
        temperatureCombobox.getSelectionModel().select(100/2);  // selectionne la valeur du milieu
        choiceTemperature=100/2;//on met la temp a celle par defaut, au cas ou elle n'est pas changée
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

        if(cell.isHeatCell()){//si sc, alors la case sc sera cochée, si on la déchoche ne sera plus sc
            defineAsHeatCellCheckbox.setSelected(true);
            defineAsDeadCellCheckbox.setDisable(true);
        }
        if(cell.isCellDead()){//si morte, alors la case morte sera cochée, si on la déchoche ne sera plus morte
            defineAsDeadCellCheckbox.setSelected(true);
            defineAsHeatCellCheckbox.setDisable(true);
        }


        defineAsDeadCellCheckbox.setOnAction(event -> {
            if (defineAsDeadCellCheckbox.isSelected()) {
                defineAsHeatCellCheckbox.setDisable(true);
                clickedOnHeatCell=false;
                clickedOnDeadCell=true;
            }
            else{
                defineAsHeatCellCheckbox.setDisable(false);
            }
        });
        
        defineAsHeatCellCheckbox.setOnAction(event -> {
            if (defineAsHeatCellCheckbox.isSelected()) {
                defineAsDeadCellCheckbox.setDisable(true);
                clickedOnHeatCell=true;
                clickedOnDeadCell=false;
            }
            else{
                defineAsDeadCellCheckbox.setDisable(false);
            }
        });

        temperatureCombobox.valueProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Valeur sélectionnée : " + newValue);
            choiceTemperature=newValue;
        });
        
        submitButton.setOnAction(e-> { //si le formulaire a été validé on ferme la vue, ok de le mettre ici car c'est de la logique mais côté vue   
            cell.setDead(clickedOnDeadCell);
            if(clickedOnHeatCell){//si on a click sur sc alors on la definit comme sc et on met a jour sa temp
                cell.setDiffuseHeat(clickedOnHeatCell);//si deja sc le redit mais alz pas grave... sinon juste if pour sa temp
                cell.setTemperature(choiceTemperature);
            }
            System.out.println(cell.isCellDead());
            window.close();
        });

        Scene scene = new Scene(configurationLayout);
        window.setScene(scene);
        window.showAndWait();  
        
        
    }

    public static Button getSubmitButton(){
        System.out.println("get sub btn");
        return submitButton;
    }

    public boolean isClickedOnDeadCell() {
        return clickedOnDeadCell;
    }

    public boolean isClickedOnHeatCell() {
        return clickedOnHeatCell;
    }

    public int getChoiceTemperature() {
        return choiceTemperature;
    }

    private static Label createLabel(String text){
        Label label = new Label(text);
        label.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-padding:30px; -fx-border-radius:15px; -fx-margin:30px");
        return label;
    }

    public static void closeWindow(){
        window.close();
    }


}