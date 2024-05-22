package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class ThermoView implements IThermoObserver, ICellObserver {
    private Stage stage;
    
    private final int WIDTH_SCENE = 1500 ;
    private final int HEIGHT_SCENE = 850 ;
    private final int WIDTH_SYSTEM_ATTRIBUTES_BUTTONS = 150;
    private final int HEIGHT_TOP_ATTRIBUTES_BUTTONS = 40;
    private final int WIDTH_HEIGHT_TIME_SETTINGS_BUTTONS = 50;
    private final int HEIGHT_HEAT_SOURCES = 100;
    private final int TOP_HBOX_ATTRIBUTES_SPACING = 10;
    private final int VBOX_INSIDE_SCROLL_PANE_SPACING = 20;
    private final int VBOX_BORDER_SPACING = 20;
    private final int CELL_BOARD_HGAP_AND_VGAP = 30;
    private final int HEAT_SOURCES_AND_CELL_HBOX_SPACING = 50;
    private final int TIME_SETTINGS_AND_HEAT_SOURCES_VBOX_SPACING=20;
    private int counterHeatCells =0 ;
    private int startNumberSeconds=0;

    private final double SCROLLPANE_PREF_HEIGHT=HEIGHT_SCENE*0.75;//80% de la hauteur de la fenetre
    private final double CELL_BOARD_PREF_HEIGHT = HEIGHT_SCENE*0.75;
    private final double CELL_BOARD_PREF_WIDTH = WIDTH_SCENE*0.75;
    /*Calcul : Aire du cellBoard = cellBoardPrefWidth*cellBoardPrefHeight
               nombre de cases total = numberRows*numberCols
               aire d'une seule case = AireCellBoard/nbrCases
               longueur largeur d'une case si carré = racine carré de son aire 
    */
    private final double WIDTH_HEIGHT_CELLS = Math.sqrt((CELL_BOARD_PREF_WIDTH*CELL_BOARD_PREF_HEIGHT)/
                                            (ThermoController.getNumberColumns()*ThermoController.getNumberRows()));


    private final static String DEAD_CELL_COLOR ="0,0,0"; //Couleur noir en rgb  
    private final static String UNACTIVE_HEAT_CELL_COLOR = "186,186,186"; //Couleur grise
    private String selectedHeatMode;
    private String cellId;
    private String sceneTitle = "HELB Thermo";

    private ComboBox<String> heatModeCombobox = new ComboBox<String>();

    private Button timeButton = createNewButton("Temps : "+startNumberSeconds+"s",WIDTH_SYSTEM_ATTRIBUTES_BUTTONS,HEIGHT_TOP_ATTRIBUTES_BUTTONS); //est-ce que le time est considéré comme variable magique ??
    private Button costButton = createNewButton("€",WIDTH_SYSTEM_ATTRIBUTES_BUTTONS,HEIGHT_TOP_ATTRIBUTES_BUTTONS);
    private Button exteriorTemperatureButton = createNewButton("T°ext.",WIDTH_SYSTEM_ATTRIBUTES_BUTTONS,HEIGHT_TOP_ATTRIBUTES_BUTTONS);
    private Button averageTemperatureButton = createNewButton("T°moy.",WIDTH_SYSTEM_ATTRIBUTES_BUTTONS,HEIGHT_TOP_ATTRIBUTES_BUTTONS);
    private Button startButton = createNewButton("▷",WIDTH_HEIGHT_TIME_SETTINGS_BUTTONS,WIDTH_HEIGHT_TIME_SETTINGS_BUTTONS);
    private Button pauseButton=createNewButton("▐▐ ",WIDTH_HEIGHT_TIME_SETTINGS_BUTTONS,WIDTH_HEIGHT_TIME_SETTINGS_BUTTONS);
    private Button resetButton=createNewButton("♻",WIDTH_HEIGHT_TIME_SETTINGS_BUTTONS,WIDTH_HEIGHT_TIME_SETTINGS_BUTTONS);

    private VBox border = new VBox(VBOX_BORDER_SPACING);
    private HBox topHboxAttributes = new HBox(TOP_HBOX_ATTRIBUTES_SPACING);//c le spacing entre parentheses
    private HBox timeSettingsHBox = new HBox();
    private VBox timeSettingsAndHeatSourcesVbox = new VBox(TIME_SETTINGS_AND_HEAT_SOURCES_VBOX_SPACING);
    private HBox timeSettingsAndHeatSourcesAndCellsHbox = new HBox(HEAT_SOURCES_AND_CELL_HBOX_SPACING);
    private VBox leftVboxHeatSources = new VBox();  
    private GridPane cellBoard = new GridPane();
    VBox vboxHeatCellsInsideScrollPane=new VBox(VBOX_INSIDE_SCROLL_PANE_SPACING);

    private HashMap<String,Button> cellMap= new HashMap<String,Button>(); //CellId(Ex Row=10 et col =8 ---> "R10C8"), btn
    private HashMap<String, Button> heatCellsMap = new HashMap<String, Button>(); //cellId, btn
    private HashMap<String, Integer> heatCellsCounterMap = new HashMap<String,Integer>(); //cellId,counter
    
 
    public ThermoView(Stage stage, boolean isConfigurationValid) {
        this.stage = stage;
        if(isConfigurationValid){
            initializeUI();
        }
        else{
            StackPane errorMessageStackPane = new StackPane(); //J'utilise stackpane car va mettre le texte direct au centre de la page
            Label labelError = new Label("La configuration est incorrecte, il doit y avoir minimum "+ThermoController.getMINIMUM_NUMBER_ROWS_AND_COLUMNS()
            +" lignes et colonnes, et maximum "+ThermoController.getMAXIMUM_NUMBER_ROWS_AND_COLUMNS()+" lignes et colonnes");
            labelError.setStyle("-fx-font-size: 24px; -fx-text-fill: red;");
            errorMessageStackPane.getChildren().add(labelError);
            createScene(errorMessageStackPane);
        }
    }


    private void initializeUI() {
        heatModeCombobox.getItems().addAll(ThermoController.getManualModeString(),ThermoController.getTargetModeString(),ThermoController.getSuccessiveModeString());
        heatModeCombobox.getSelectionModel().select(ThermoController.getManualModeString());  // selectionne lE mode manuel par défaut pour affichage
        selectedHeatMode=ThermoController.getManualModeString(); //selectionne mode manual par defaut comme valeur

        //Ajout d'une scroll bar pour les sources de chaleurs
        ScrollPane scrollPaneHeatSources = new ScrollPane();
        scrollPaneHeatSources.setContent(vboxHeatCellsInsideScrollPane);
        scrollPaneHeatSources.setFitToWidth(true);
        leftVboxHeatSources.getChildren().add(scrollPaneHeatSources);
        topHboxAttributes.getChildren().addAll(timeButton,costButton,exteriorTemperatureButton,averageTemperatureButton,heatModeCombobox);
        timeSettingsHBox.getChildren().addAll(startButton,pauseButton,resetButton);
        
        createCells();

        cellBoard.setHgap(CELL_BOARD_HGAP_AND_VGAP);//espacement entre cellules horizontalement
        cellBoard.setVgap(CELL_BOARD_HGAP_AND_VGAP); //espacement entre cellules verticalement
        cellBoard.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-padding:30px; -fx-border-radius:30px");
        //cellBoard.setPrefHeight((ThermoController.getNumberRows()*WIDTH_HEIGHT_CELLS)+CELL_BOARD_HGAP_AND_VGAP+CELL_BOARD_HGAP_AND_VGAP);//hauteur = hauteur de toutes les cellules + le margin
        cellBoard.setPrefHeight(CELL_BOARD_PREF_HEIGHT);
        cellBoard.setPrefWidth(CELL_BOARD_PREF_WIDTH);

        scrollPaneHeatSources.setPrefHeight(SCROLLPANE_PREF_HEIGHT);
        timeSettingsAndHeatSourcesVbox.getChildren().addAll(timeSettingsHBox, scrollPaneHeatSources);
        timeSettingsAndHeatSourcesAndCellsHbox.getChildren().addAll(timeSettingsAndHeatSourcesVbox,cellBoard);

        border.getChildren().addAll(topHboxAttributes, timeSettingsAndHeatSourcesAndCellsHbox);

        createScene(border);
    }

    public Button getHeatCellButton(String cellId){
        return heatCellsMap.get(cellId);
    }

    public Button getCellButton(String cellId){
        return cellMap.get(cellId);
    }

    public Button getStartButton(){
        return startButton;
    }

    public Button getPauseButton(){
        return pauseButton;
    }

    public Button getResetButton(){
        return resetButton;
    }


    public void createCells(){
        for(int row=0; row<ThermoController.getNumberRows();row++){
            for(int col=0; col<ThermoController.getNumberColumns();col++){
                Button cell = createNewButton(null, WIDTH_HEIGHT_CELLS, WIDTH_HEIGHT_CELLS);
                //gridpane on ajoute d'abord le col, puis le row !!!
                cellBoard.add(cell,col,row); //chaque bouton est ajouté dans la gridpane
                cellMap.put(ThermoController.getCellId(row, col),cell); //la hashmap stock l'id de chaque bouton afin qu'on puisse manier le bouton par la suite
                //grâce à son row et col
            }
        }
    }

    public Button createNewButton(String text, double width, double heigth){
        Button newButton = new Button(text);
        newButton.setPrefHeight(heigth);
        newButton.setPrefWidth(width);
        return newButton;
    }

    public void createScene(Parent layout){
        Scene scene = new Scene(layout, WIDTH_SCENE, HEIGHT_SCENE);
        stage.setScene(scene);
        stage.setTitle(sceneTitle);
        stage.show();
        stage.setResizable(false); //permet qu'on peut pas agrandir ou diminuer la taille de la fenetre 
    }

    public String getHeatMode(){
        heatModeCombobox.valueProperty().addListener((observable, oldValue, newValue) -> {
            selectedHeatMode=newValue;//=valeur selectionné dans combobox
        });
        return selectedHeatMode;
    }

    public ComboBox<String> getHeatModeCombobox(){
        return heatModeCombobox;
    }

    @Override//Pour mettre à jour le temps affiché
    public void updateSystemAttributes(int time, double averageTemperature, double exteriorTemperature, double cost) {
        timeButton.setText("Temps : "+time+"s");
        averageTemperatureButton.setText("T°moy : "+String.format("%.1f", averageTemperature)+"°C"); //permet d'afficher seulement 1 chiffre après la virgule
        exteriorTemperatureButton.setText("T°ext : "+exteriorTemperature+"°C");
        costButton.setText("Coût : "+String.format("%.1f", cost)+"€");
    }

    @Override//Pour changer la couleur des cellules vivantes et  mortes
    //j'aurais pu ne pas mettre de boolean is deadcell et regarder si cellTemperature = -500 mais c'est pas logique, booleen mieux selon moi
    public void updateCellAttributes(int row, int col, boolean isHeatCell, boolean isHeatDiffuser, boolean isDeadCell, double cellTemperature) { 
        cellId = ThermoController.getCellId(row, col);
        Button buttonToChangeColor =cellMap.get(cellId);
        String stringToAddForHeatCells="";
        String color;
        if(!isDeadCell){
            if(isHeatCell){
                if(!isHeatDiffuser){
                    color = getColorString(UNACTIVE_HEAT_CELL_COLOR); //En gris si la source de chaleur est désactivée
                }
                else{
                    color= getShadeOfRed(cellTemperature); //pour source chaleur activée
                }
                //Pour les sources de chaleur on ajt le numero de la source, pour les autres cellules, ce sera un string vide
                stringToAddForHeatCells="S"+heatCellsCounterMap.get(cellId)+"\n"; 
                addHeatCellToHeatCellMap(cellId,color); //ajout dans la hashmap pour la vbox du scrollpane
            }
            else{
                color=getShadeOfRed(cellTemperature); //Pour cellule normale
                //si avant c'etait une sc et mtn plus, on l'enlève des hashmap de sc
                /*
                 * if(heatCellsCounterMap.containsKey(cellId))heatCellsCounterMap.remove(cellId);
                 * je ne retire pas de cette liste car sinon je dois changer le texte de toutes les cellules et je n'ai pas le temps 
                 */
                
                if(heatCellsMap.containsKey(cellId)){
                    Button buttonToRemove = heatCellsMap.get(cellId);
                    heatCellsMap.remove(cellId);
                    vboxHeatCellsInsideScrollPane.getChildren().remove(buttonToRemove); //faut changer le style des autres
                }
            }
            buttonToChangeColor.setText(stringToAddForHeatCells+String.format("%.1f", cellTemperature)); //1 seul chiffre après la virgule
        }
        else{
            buttonToChangeColor.setText("");//si c'etait une cell avant et que on l'a changé en morte, on ne veut plus voir sa temp affichée
            color = getColorString(DEAD_CELL_COLOR); //sinon si cellule morte en noir
        }
        buttonToChangeColor.setStyle(color); //pr toutes les cellules on met une couleur
    }

    private void addHeatCellToHeatCellMap(String cellId, String color){
        Button heatCellButton;
        if(!heatCellsMap.containsKey(cellId)){//si la map ne contient pas la clé on l'ajoute
            heatCellsCounterMap.put(cellId,heatCellsCounterMap.size()+1); //le int du dernier element ajt=taille de la map avant l'ajout+1 car on veut pas commencer a 0  
            heatCellButton = createNewButton("S"+heatCellsCounterMap.get(cellId), WIDTH_SYSTEM_ATTRIBUTES_BUTTONS, HEIGHT_HEAT_SOURCES);
            heatCellsMap.put(cellId, heatCellButton);
            vboxHeatCellsInsideScrollPane.getChildren().add(heatCellButton); //On ajoute le bouton de la source de chaleur dans la vbox de la scrollpane a gauche 
        }
        else{
            heatCellButton = heatCellsMap.get(cellId);
        }
        heatCellButton.setStyle(color);
    }

    public String getShadeOfRed(double temperature) {
        /* 255 0 0  = Rouge max et 255 255 255 = blanc ---> doit que varier entre ses couleurs 
         * Temp max = 255 0 0 et temp min= 255 255 255 
         * --> G et B = pourcentage --- > ex : min=0 et max = 40 --> temp:35  pourcentage --> (35/40)*100 = 87,5%
         *  G et B = 255 - (255/100)*87,5 =
         * ---> 255 - (255*(87,5/100))
         * 
         * min 0 max 100 temp 50   ->gb attendu : 127,5*/
        int rgbRedValue = 255;
        double rgbBlueValue = 0;
        double rgbGreenValue = 0;
        double rgbGreenBlueValue = 255;

        double totalEcart = ThermoController.getMaximumTemperature()-ThermoController.getMinimumTemperature();
        rgbGreenBlueValue = 255-(255*(temperature/totalEcart));
        rgbBlueValue = rgbGreenBlueValue;
        rgbGreenValue = rgbGreenBlueValue;
        return getColorString(rgbRedValue+","+rgbGreenValue+","+rgbBlueValue);
    }

    private String getColorString(String rgbColor){return "-fx-background-color: rgb("+rgbColor+");";}
}