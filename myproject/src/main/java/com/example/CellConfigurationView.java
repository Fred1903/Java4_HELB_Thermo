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
import javafx.geometry.Pos;

public class CellConfigurationView{
    
    
    private Button submitButton;
    private boolean isClickedOnDeadCell=false;
    private boolean isClickedOnHeatCell=false;

    private Stage window;

    private final int MIN_WIDTH = 350;
    private final int MIN_HEIGHT = 450;
    private final int SPACING_HBOX = 50;
    private final int SPACING_CONFIGURATION_VBOX = 30;
    private final int VISIBLE_ROW_COUNT = 10;  //ligne du dessous pour que si on a temp negative ca fonctionne aussi
    private final int HALF_TEMPERATURE = ThermoController.getMaximumTemperature()-(ThermoController.getGapBetweenMinMaxTemperature()/2);
    private final int HALF_TEMPERATURE_INDEX = ThermoController.getGapBetweenMinMaxTemperature()/2;
    private int row;
    private int col;
    private int choiceTemperature;

    public CellConfigurationView(){
        submitButton = new Button("Valider"); ///////////OBLIGER de l'instancier dans le ctor et pas dans display car sinon ca ne fonctionnera pas !!!!!!!!!!!
    }

    private Label createLabel(String text){
        Label label = new Label(text);
        label.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-padding:30px; -fx-border-radius:15px; -fx-margin:30px");
        return label;
    }

    public void closeWindow(){
        window.close();
    }

    //Cell a enlever car apres va faire logique cote modele on veut pas
    public void display(int row, int col, boolean isHeatCell, boolean isDeadCell){ //Faut laisser en ou pas ?
        this.row=row;
        this.col=col;

        window = new Stage();        
        window.initModality(Modality.APPLICATION_MODAL); //focus sur la fenetre
        window.setTitle("Cell Configuration : ");
        window.setMinWidth(MIN_WIDTH);
        window.setMinHeight(MIN_HEIGHT);
     
        Label cellPositionLabel = createLabel("Position de la cellule : \nLigne : "+row+" Colonne : "+col);
        Label defineAsDeadCellLabel = createLabel("Définir comme cellule morte");
        Label defineAsHeatCellLabel = createLabel("Définir comme source de chaleur");
        Label textTemperatureLabel = createLabel("T° de la source quand activée");

        ComboBox<Integer> temperatureCombobox = new ComboBox<Integer>(); //0 min, 100 max 
        for (int i = ThermoController.getMinimumTemperature(); i <= ThermoController.getMaximumTemperature(); i++) { 
            temperatureCombobox.getItems().add(i);
        }
        temperatureCombobox.setVisibleRowCount(VISIBLE_ROW_COUNT); //Fait que on voit que 10 nombre a la fois et pas tout
        System.out.println("half temp : "+HALF_TEMPERATURE);
        temperatureCombobox.getSelectionModel().select(HALF_TEMPERATURE_INDEX);  // selectionne la valeur du milieu
        choiceTemperature=HALF_TEMPERATURE;//on met la temp a celle par defaut, au cas ou elle n'est pas changée
        
        submitButton.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-padding:30px; -fx-border-radius:15px;");

        CheckBox defineAsDeadCellCheckbox = new CheckBox();
        defineAsDeadCellCheckbox.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-padding:30px; -fx-border-radius:15px;");
        CheckBox defineAsHeatCellCheckbox = new CheckBox();
        defineAsHeatCellCheckbox.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-padding:30px; -fx-border-radius:15px;");

        HBox defineAsDeadCellHbox = new HBox(SPACING_HBOX);
        HBox defineAsHeatCellHbox = new HBox(SPACING_HBOX);
        HBox defineTemperatureHBox = new HBox(SPACING_HBOX);

        defineAsHeatCellHbox.getChildren().addAll(defineAsHeatCellLabel, defineAsHeatCellCheckbox);
        defineAsDeadCellHbox.getChildren().addAll(defineAsDeadCellLabel, defineAsDeadCellCheckbox);
        defineTemperatureHBox.getChildren().addAll(textTemperatureLabel,temperatureCombobox);

        VBox configurationLayout = new VBox(SPACING_CONFIGURATION_VBOX);
        configurationLayout.getChildren().addAll(cellPositionLabel,defineAsDeadCellHbox,defineAsHeatCellHbox,defineTemperatureHBox,submitButton);
        configurationLayout.setAlignment(Pos.CENTER);

        if(isHeatCell){//si sc, alors la case sc sera cochée, si on la déchoche ne sera plus sc
            isClickedOnHeatCell=true;
            defineAsHeatCellCheckbox.setSelected(true);
            defineAsDeadCellCheckbox.setDisable(true);
        }
        if(isDeadCell){//si morte, alors la case morte sera cochée, si on la déchoche ne sera plus morte
            defineAsDeadCellCheckbox.setSelected(true);
            defineAsHeatCellCheckbox.setDisable(true);
        }

        defineAsDeadCellCheckbox.setOnAction(event -> {
            if (defineAsDeadCellCheckbox.isSelected()) { //si checkbox sélectionné, l'autre n'est pas sélectionnable
                defineAsHeatCellCheckbox.setDisable(true);
                isClickedOnHeatCell=false;
                isClickedOnDeadCell=true;
                System.out.println("CLICK DEAD TRUE");
            }
            else{
                isClickedOnDeadCell=false; //dans le cas ou aucune des deux cases est cochés
                defineAsHeatCellCheckbox.setDisable(false);
            }
        });
        
        defineAsHeatCellCheckbox.setOnAction(event -> {
            if (defineAsHeatCellCheckbox.isSelected()) {
                defineAsDeadCellCheckbox.setDisable(true);
                isClickedOnHeatCell=true;
                isClickedOnDeadCell=false;
            }
            else{
                isClickedOnHeatCell=false; //dans le cas ou aucune des deux cases est cochés
                defineAsDeadCellCheckbox.setDisable(false);
            }
        });

        temperatureCombobox.valueProperty().addListener((observable, oldValue, newValue) -> {
            choiceTemperature=newValue;
            System.out.println("Choosed temp is :"+choiceTemperature);
        });

        Scene scene = new Scene(configurationLayout);
        window.setScene(scene);
        window.showAndWait();  
    }

    public String getCellId(){
        return ThermoController.getCellId(row, col);
    }

    public int getChoiceTemperature() {
        return choiceTemperature;
    }

    public Button getSubmitButton(){
        return submitButton;
    }

    public boolean isClickedOnDeadCell() {
        boolean isClickedDeadToReturn = isClickedOnDeadCell;
        isClickedOnDeadCell = false;
        return isClickedDeadToReturn;
    }

    public boolean isClickedOnHeatCell() {
        boolean isClickedHeatToReturn = isClickedOnHeatCell;
        isClickedOnHeatCell = false;
        return isClickedHeatToReturn;
    }
}