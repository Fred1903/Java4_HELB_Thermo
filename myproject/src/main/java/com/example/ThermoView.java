package com.example;

import java.util.HashMap;

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
    
    
    
    private final int WIDTH_SCENE = 1000 ;
    private final int HEIGHT_SCENE = 700 ;
    private final int WIDTH_SYSTEM_ATTRIBUTES_BUTTONS = 150;
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

    private final static String DEAD_CELL_COLOR ="0,0,0"; //Couleur noir en rgb  
    private final static String UNACTIVE_HEAT_CELL_COLOR = "186,186,186"; //Couleur grise
    private final String MANUAL_MODE_STRING = "Manual Mode";
    private final String TARGET_MODE_STRING = "Target Mode";

    private String selectedHeatMode;

    private int counterHeatCells =0 ;

    private Integer numberSeconds= 0;

    private String cellId;
    private String sceneTitle = "HELB Thermo";


    private Button timeButton = createNewButton(""+numberSeconds,WIDTH_SYSTEM_ATTRIBUTES_BUTTONS,HEIGHT_TOP_ATTRIBUTES_BUTTONS); //est-ce que le time est considéré comme variable magique ??
    private Button costButton = createNewButton("€",WIDTH_SYSTEM_ATTRIBUTES_BUTTONS,HEIGHT_TOP_ATTRIBUTES_BUTTONS);
    private Button exteriorTemperatureButton = createNewButton("T°ext.",WIDTH_SYSTEM_ATTRIBUTES_BUTTONS,HEIGHT_TOP_ATTRIBUTES_BUTTONS);
    private Button averageTemperatureButton = createNewButton("T°moy.",WIDTH_SYSTEM_ATTRIBUTES_BUTTONS,HEIGHT_TOP_ATTRIBUTES_BUTTONS);
    private ComboBox<String> heatModeCombobox = new ComboBox<String>();
    private Button startButton = createNewButton("▷",WIDTH_HEIGHT_TIME_SETTINGS_BUTTONS,WIDTH_HEIGHT_TIME_SETTINGS_BUTTONS);
    private Button pauseButton=createNewButton("▐▐ ",WIDTH_HEIGHT_TIME_SETTINGS_BUTTONS,WIDTH_HEIGHT_TIME_SETTINGS_BUTTONS);
    private Button resetButton=createNewButton("♻",WIDTH_HEIGHT_TIME_SETTINGS_BUTTONS,WIDTH_HEIGHT_TIME_SETTINGS_BUTTONS);



    private VBox border = new VBox(TWENTY);
    private HBox topHboxAttributes = new HBox(TEN);//c le spacing entre parentheses
    private HBox timeSettingsHBox = new HBox();
    private HBox heatSourcesAndCellsHbox = new HBox(FIFTY);
    private VBox leftVboxHeatSources = new VBox(TWENTY); 
    private GridPane cellBoard = new GridPane();

    VBox vboxHeatCellsInsideScrollPane=new VBox(TWENTY);

    private HashMap<String,Button> cellMap= new HashMap<String,Button>(); //CellId(Ex Row=10 et col =8 ---> "R10C8"), btn
    private HashMap<String, Button> heatCellsMap = new HashMap<String, Button>(); //cellId, btn
    private HashMap<String, Integer> heatCellsCounterMap = new HashMap<String,Integer>(); //cellId,counter
    
 
    public ThermoView(Stage stage) {
        this.stage = stage;
        //Verif si la configuration est bonne  
        //Est-ce que ce if doit etre ici ? n'est-ce pas de la logique ? REPONSE DANS CTRL
        if(ThermoController.getNumberColumns() >= ThermoController.getMINIMUM_NUMBER_ROWS_AND_COLUMNS() && ThermoController.getNumberRows() <=ThermoController.getMAXIMUM_NUMBER_ROWS_AND_COLUMNS()
           && ThermoController.getNumberColumns()<=ThermoController.getMAXIMUM_NUMBER_ROWS_AND_COLUMNS() && ThermoController.getNumberRows() >=ThermoController.getMINIMUM_NUMBER_ROWS_AND_COLUMNS()){
            initializeUI();
        }////////////Attention max --> dans controller
        else{
            StackPane errorMessageStackPane = new StackPane(); //J'utilise stackpane car va mettre le texte direct au centre de la page
            Label labelError = new Label("La configuration est incorrecte, il doit y avoir minimum"+ThermoController.getMINIMUM_NUMBER_ROWS_AND_COLUMNS()+
            " lignes et colonnes, et maximum"+ThermoController.getMAXIMUM_NUMBER_ROWS_AND_COLUMNS()+" lignes et colonnes");
            labelError.setStyle("-fx-font-size: 24px; -fx-text-fill: red;");
            errorMessageStackPane.getChildren().add(labelError);
            createScene(errorMessageStackPane);
        }
    }


    private void initializeUI() {
        heatModeCombobox.getItems().add(MANUAL_MODE_STRING);
        heatModeCombobox.getItems().add(TARGET_MODE_STRING);
        heatModeCombobox.getSelectionModel().select(MANUAL_MODE_STRING);  // selectionne lE mode manuel par défaut pour affichage
        selectedHeatMode=MANUAL_MODE_STRING; //selectionne mode manual par defaut comme valeur

        //Ajout d'une scroll bar pour les sources de chaleurs
        ScrollPane scrollPaneHeatSources = new ScrollPane();
        scrollPaneHeatSources.setContent(vboxHeatCellsInsideScrollPane);
        scrollPaneHeatSources.setFitToWidth(true);
        scrollPaneHeatSources.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPaneHeatSources.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPaneHeatSources.setPrefHeight(300);

        leftVboxHeatSources.getChildren().add(scrollPaneHeatSources);
        
        topHboxAttributes.getChildren().addAll(timeButton,costButton,exteriorTemperatureButton,averageTemperatureButton,heatModeCombobox);
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

    public Button getHeatCellButton(String cellId){
        if(cellId.equals("R1C1")){
            System.out.println("Dans get");   
        }
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
                ////////////////////!!!!!!!!!!!!!!!! gridpane on ajoute d'abord le col, puis le row !!!!!!!!!!!!
                cellBoard.add(cell,col,row); //chaque bouton est ajouté dans la gridpane
                cellMap.put(ThermoController.getCellId(row, col),cell); //la hashmap stock l'id de chaque bouton afin qu'on puisse manier le bouton par la suite
                //grâce à son row et col
            }
        }
    }

    public Button createNewButton(String text, int width, int heigth){
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
    public void updateSystemAttributes(int time, double averageTemperature, double exteriorTemperature) {
        timeButton.setText(""+time);
        averageTemperatureButton.setText("T°moy : "+String.format("%.1f", averageTemperature)+"°C"); //permet d'afficher seulement 1 chiffre après la virgule
        exteriorTemperatureButton.setText("T°ext : "+exteriorTemperature+"°C");
    }

    @Override//Pour changer la couleur des cellules vivantes et  mortes
    //j'aurais pu ne pas mettre de boolean is deadcell et regarder si cellTemperature = -500 mais c'est pas logique, booleen mieux selon moi
    public void updateCellColor(int row, int col, boolean isHeatCell, boolean isHeatDiffuser, boolean isDeadCell, double cellTemperature) { 
        cellId = ThermoController.getCellId(row, col);
        Button buttonToChangeColor =cellMap.get(cellId);
        String stringToAddForHeatCells="";
        String color;
        if(!isDeadCell){
            if(isHeatCell){
                if(!isHeatDiffuser){
                    color = "-fx-background-color: rgb("+UNACTIVE_HEAT_CELL_COLOR+");"; //En gris si la source de chaleur est désactivée
                }
                else{
                    color= getShadeOfRed(cellTemperature); //pour source chaleur activée
                }
                stringToAddForHeatCells="S"+heatCellsCounterMap.get(cellId)+"\n"; //Pour les sources de chaleur on ajt le numero de la source, pour les autres cellules, ce sera un string vide
                addHeatCellToHeatCellMap(cellId,color); //ajout dans la hashmap pour la vbox du scrollpane
            }
            else{
                color=getShadeOfRed(cellTemperature); //Pour cellule normale
            }
            buttonToChangeColor.setText(stringToAddForHeatCells+String.format("%.1f", cellTemperature)); //1 seul chiffre après la virgule
        }
        else{
            buttonToChangeColor.setText("");//si c'etait une cell avant et que on l'a changé en morte, on ne veut plus voir sa temp affichée
            color ="-fx-background-color: rgb("+DEAD_CELL_COLOR+");"; //sinon si cellule morte en noir
        }
        buttonToChangeColor.setStyle(color); //pr toutes les cellules on met une couleur
    }

    private void addHeatCellToHeatCellMap(String cellId, String color){
        if(cellId.equals("R1C1")){
            System.out.println("Dans add");
        }
        Button heatCellButton;
        if(!heatCellsMap.containsKey(cellId)){
            if(cellId.equals("R1C1")){
                System.out.println("Dans if");
            }
            heatCellsCounterMap.put(cellId,heatCellsCounterMap.size()+1); //le int du dernier element ajt=taille de la map avant l'ajout+1 car on veut pas commencer a 0  
            heatCellButton = createNewButton("S"+heatCellsCounterMap.get(cellId), WIDTH_SYSTEM_ATTRIBUTES_BUTTONS, HEIGHT_HEAT_SOURCES);
            heatCellsMap.put(cellId, heatCellButton);
            vboxHeatCellsInsideScrollPane.getChildren().add(heatCellButton); //On ajoute le bouton de la source de chaleur dans la vbox de la scrollpane a gauche 
        }
        else{
            if(cellId.equals("R1C1")){
                System.out.println("Dans else");
            }
            heatCellButton = heatCellsMap.get(cellId);
        }
        heatCellButton.setStyle(color);
    }

    public String getShadeOfRed(double temperature) {
        // On assume que la température varie de 0 à 50

        /*
         * 255 0 0  = Rouge max et 255 255 255 = blanc ---> doit que varier entre ses couleurs 
         * Temp max = 255 0 0 et temp min= 255 255 255 
         * --> G et B = pourcentage --- > ex : min=0 et max = 40 --> temp:35  pourcentage --> (35/40)*100 = 87,5%
         *  G et B = 255 - (255/100)*87,5 =
         * ---> 255 - (255*(87,5/100))
         * 
         * min 0 max 100 temp 50   ->gb attendu : 127,5
         * 
         * probleme = GB avec temp 100 = 55 et GB avec temp 50 = 155 alors que devrait = 0 et 127,5
         */
        int rgbRedValue = 255;
        double rgbBlueValue = 0;
        double rgbGreenValue = 0;
        double rgbGreenBlueValue = 255;

        double max = 100;
        double min = 0;
        double totalEcart = max-min;
        rgbGreenBlueValue = 255-(255*(temperature/totalEcart));
        rgbBlueValue = rgbGreenBlueValue;
        rgbGreenValue = rgbGreenBlueValue;
        return "-fx-background-color: rgb("+rgbRedValue+","+rgbGreenValue+","+rgbBlueValue+");";
    }


}