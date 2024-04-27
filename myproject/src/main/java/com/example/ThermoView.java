package com.example;

import java.util.HashMap;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class ThermoView implements IThermoObserver, ICellObserver {
    private Stage stage;
    
    
    
    private final int WIDTH_SCENE = 1000 ;
    private final int HEIGHT_SCENE = 700 ;
    private final int WIDTH_TIME_AND_HEAT_MODE_AND_HEAT_SOURCES_BUTTONS = 150;
    private final int WIDTH_COST_AND_TEMPERATURES_BUTTONS = 75;
    private final int HEIGHT_TOP_ATTRIBUTES_BUTTONS = 40;
    private final int WIDTH_HEIGHT_TIME_SETTINGS_BUTTONS = 50;
    private final int HEIGHT_HEAT_SOURCES = 100;
    private final int WIDTH_HEIGHT_CELLS = 85;
    private final int TEN = 10;
    private final int TWENTY = 20;
    private final int THIRTY = 30;
    private final int FIFTY = 50;

    //private final static int IDEAL_RGB_GREEN_BLUE_MINUS = 42; Faux, a recalculer !!
    //private final static int IDEAL_RGB_RED_MINUS = 44;

    private final static String BLACK_COLOR ="0,0,0"; //Couleur noir en rgb  

    private Integer numberSeconds= 0;

    private String cellId;
    private String sceneTitle = "HELB Thermo";


    private Button timeButton = createNewButton(""+numberSeconds,WIDTH_TIME_AND_HEAT_MODE_AND_HEAT_SOURCES_BUTTONS,HEIGHT_TOP_ATTRIBUTES_BUTTONS); //est-ce que le time est considéré comme variable magique ??
    private Button costButton = createNewButton("€",WIDTH_COST_AND_TEMPERATURES_BUTTONS,HEIGHT_TOP_ATTRIBUTES_BUTTONS);
    private Button exteriorTemperatureButton = createNewButton("T°ext.",WIDTH_COST_AND_TEMPERATURES_BUTTONS,HEIGHT_TOP_ATTRIBUTES_BUTTONS);
    private Button averageTemperatureButton = createNewButton("T°moy.",WIDTH_COST_AND_TEMPERATURES_BUTTONS,HEIGHT_TOP_ATTRIBUTES_BUTTONS);
    private Button heatModeButton = createNewButton("Chauffe mode ▽",WIDTH_TIME_AND_HEAT_MODE_AND_HEAT_SOURCES_BUTTONS,HEIGHT_TOP_ATTRIBUTES_BUTTONS);
    private Button startButton = createNewButton("▷",WIDTH_HEIGHT_TIME_SETTINGS_BUTTONS,WIDTH_HEIGHT_TIME_SETTINGS_BUTTONS);
    private Button pauseButton=createNewButton("▐▐ ",WIDTH_HEIGHT_TIME_SETTINGS_BUTTONS,WIDTH_HEIGHT_TIME_SETTINGS_BUTTONS);
    private Button resetButton=createNewButton("♻",WIDTH_HEIGHT_TIME_SETTINGS_BUTTONS,WIDTH_HEIGHT_TIME_SETTINGS_BUTTONS);



    private VBox border = new VBox(TWENTY);
    private HBox topHboxAttributes = new HBox(TEN);//c le spacing entre parentheses
    private HBox timeSettingsHBox = new HBox();
    private HBox heatSourcesAndCellsHbox = new HBox(FIFTY);
    private VBox leftVboxHeatSources = new VBox(TWENTY); 
    private GridPane cellBoard = new GridPane();

    private HashMap<String,Button> cellMap= new HashMap<String,Button>(); //Ex Row=10 et col =8 ---> "R10C8"
    
 
    public ThermoView(Stage stage) {
        this.stage = stage;
        //Verif si la configuration est bonne  
        //Est-ce que ce if doit etre ici ? n'est-ce pas de la logique ?
        if(ThermoController.getNumberColumns() >= ThermoController.getMINIMUM_NUMBER_ROWS_AND_COLUMNS() && ThermoController.getNumberRows() <=ThermoController.getMAXIMUM_NUMBER_ROWS_AND_COLUMNS()
           && ThermoController.getNumberColumns()<=ThermoController.getMAXIMUM_NUMBER_ROWS_AND_COLUMNS() && ThermoController.getNumberRows() >=ThermoController.getMINIMUM_NUMBER_ROWS_AND_COLUMNS()){
            initializeUI();
        }////////////Attention max --> dans controller
        else{
            VBox errorMessageVbox = new VBox();
            Label labelError = new Label("La configuration est incorrecte, il doit y avoir minimum 3 lignes et colonnes, et maximum 13 lignes et colonnes");
            errorMessageVbox.getChildren().add(labelError);
            createScene(errorMessageVbox);
        }
        
    }


    private void initializeUI() {
        ///leftVboxHeatSources.getChildren().addAll(sc1,sc2,sc3);
        VBox left=new VBox(TWENTY);
        for(int i=0;i<50;i++){
            Button b=createNewButton(null, WIDTH_TIME_AND_HEAT_MODE_AND_HEAT_SOURCES_BUTTONS, HEIGHT_HEAT_SOURCES);
            left.getChildren().add(b);
        }
        //Ajout d'une scroll bar pour les sources de chaleurs
        ScrollPane scrollPaneHeatSources = new ScrollPane();
        scrollPaneHeatSources.setContent(left);
        scrollPaneHeatSources.setFitToWidth(true);
        scrollPaneHeatSources.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPaneHeatSources.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPaneHeatSources.setPrefHeight(300);

        leftVboxHeatSources.getChildren().add(scrollPaneHeatSources);
        
        topHboxAttributes.getChildren().addAll(timeButton,costButton,exteriorTemperatureButton,averageTemperatureButton,heatModeButton);
        timeSettingsHBox.getChildren().addAll(startButton,pauseButton,resetButton);


        createCells();
        heatSourcesAndCellsHbox.getChildren().addAll(leftVboxHeatSources,cellBoard);

        cellBoard.setHgap(THIRTY);//espacement entre cellules horizontalement
        cellBoard.setVgap(THIRTY); //espacement entre cellules verticalement
        cellBoard.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-padding:30px; -fx-border-radius:30px; -fx-margin:30px");
        cellBoard.setPrefHeight((ThermoController.getNumberRows()*WIDTH_HEIGHT_CELLS)+THIRTY+THIRTY);//hauteur = hauteur de toutes les cellules + le margin


        scrollPaneHeatSources.setPrefHeight(cellBoard.getPrefHeight());

        border.getChildren().addAll(topHboxAttributes,timeSettingsHBox,heatSourcesAndCellsHbox);

        createScene(border);
    }

    //Méthode pour que le controller puisse détecter quand est-ce que le bouton start a été appuyé
    public void getStartButtonListener(EventHandler<ActionEvent> handler) {
        startButton.setOnAction(handler);
    }

    public void getPauseButtonListener(EventHandler<ActionEvent> handler) {
        pauseButton.setOnAction(handler);
    }

    public void getResetButtonListener(EventHandler<ActionEvent> handler) {
        resetButton.setOnAction(handler);
    }

    public void createCells(){
        for(int row=0; row<ThermoController.getNumberRows();row++){
            for(int col=0; col<ThermoController.getNumberColumns();col++){
                Button cell = createNewButton(null, WIDTH_HEIGHT_CELLS, WIDTH_HEIGHT_CELLS);
                cellBoard.add(cell,row,col); //chaque bouton est ajouté dans la gridpane
                cellMap.put(getCellId(row, col),cell); //la hashmap stock l'id de chaque bouton afin qu'on puisse manier le bouton par la suite
                //grâce à son row et col
            }
        }
    }

    public Button createNewButton(String name, int width, int heigth){
        Button newButton = new Button(name);
        newButton.setPrefHeight(heigth);
        newButton.setPrefWidth(width);
        return newButton;
    }

    public void createScene(Parent layout){
        Scene scene = new Scene(layout, WIDTH_SCENE, HEIGHT_SCENE);
        stage.setScene(scene);
        stage.setTitle(sceneTitle);
        stage.show();
    }

    public String getCellId(int row, int col){
        return "R"+row+"C"+col;
    }

    @Override//Pour mettre à jour le temps affiché
    public void update(Object objectToUpdate) {
        if(objectToUpdate instanceof Integer){
            timeButton.setText(""+objectToUpdate);
        }
    }

    @Override//Pour changer la couleur des cellules sources de chaleur et cellules mortes
    public void updateCellColor(int row, int col, boolean isHeatCell, int cellTemperature) { 
        cellId = getCellId(row, col);
        if(isHeatCell || cellTemperature==ThermoController.getDeadCellNoTemperature()){
            Button buttonToChangeColor =cellMap.get(cellId);
            String color;
            if(isHeatCell){//si c est une source de chaleur
                color = getShadeOfRed(cellTemperature);
            }
            else{//sinon si c est une cellule morte
                color ="-fx-background-color: rgb("+BLACK_COLOR+");";
            }
            buttonToChangeColor.setStyle(color);
        }
    }

    public String getShadeOfRed(int temperature) {
        // On assume que la température varie de 0 à 50
        int rgbRedValue = 255;
        int rgbBlueValue = 0;
        int rgbGreenValue = 0;
        //Le blue,green et red qu'on change ici sont pour des températures entre 0 et 50, si cet écart est plus grand alorsfaut changer ses valeurs
        if(temperature<ThermoController.getEstimatedMedianTemperature()){ //si temperature plus petit que 25 alors rouge plus clair
            for(int i=temperature;i<=ThermoController.getEstimatedMedianTemperature();i++){
                rgbBlueValue+=8; //clair ok 
                rgbGreenValue+=8;
            }
        }
        else{ //sinon rouge plus foncé
            for(int i=ThermoController.getEstimatedMedianTemperature();i<=temperature;i++){
                rgbRedValue-=6; //foncé ok
            }
        }
        return "-fx-background-color: rgb("+rgbRedValue+","+rgbGreenValue+","+rgbBlueValue+");";
    }


}