package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import javafx.stage.Stage;

public class ThermoController implements IThermoObservable {

    private ThermoView thermoView;
    private CellConfigurationView cellConfigurationView;

    private Timeline timeline;
    private KeyFrame keyFrame; 

    private boolean areCellsCreated=false;
    private boolean isSystemPlaying = false;

    private final static int FIRST_ROW_COL = 0;
    private final static int MINIMUM_NUMBER_ROWS_AND_COLUMNS = 3;
    private final static int MAXIMUM_NUMBER_ROWS_AND_COLUMNS = 12;
    private final static int NUMBER_ROWS =4;
    private final static int NUMBER_COLUMNS = 5;
    private final static int LASTROW = NUMBER_ROWS-1;
    private final static int LASTCOLUMN = NUMBER_COLUMNS-1;
    private final static int HEAT_CELL_START_TEMPERATURE = 18; 
    private final static int DEAD_CELL_NO_TEMPERATURE = -500; //cellule morte n'a pas de temperature mais comme un int ne peut pas etre 'null' on va lui mettre -500
    private final static int ESTIMATED_MIN_TEMPERATURE = 0;
    private final static int ESTIMATED_MAX_TEMPERATURE = 50;
    private final static int DISTANCE_MIN_MAX_TEMPERATURE = ESTIMATED_MAX_TEMPERATURE-(+ESTIMATED_MIN_TEMPERATURE);
    private final static int ESTIMATED_MEDIAN_TEMPERATURE = DISTANCE_MIN_MAX_TEMPERATURE/2;

    
    private int zero = 0;
    private int numberSeconds = 0;
    private int numberAliveCells=0;

    private double outsideTemperature;
    private double averageTemperature;

    private HashMap<String,Cell> cellMap = new HashMap<String,Cell>();

    private final String exteriorTemperatureFile = "src/main/java/com/example/simul.data.txt" ;
    private String currentStrategy = "Manual Mode"; //on commence avec manual mode comme strategie

    private CellFactory cellFactory;

    private ICellObserver cellObserver;
    private IThermoObserver thermoObserver;

    private HeatCellStrategy heatCellStrategy = new ManualStrategy();//on commence avec manual strategie

    private ExteriorTemperatureParser exteriorTemperatureParser;
    private int[][] startHeatSources; //on ne peut pas mettre en final ici car alors on ne pourra pas initialisée par la suite
    private final int [][] ADJACENT_ITEMS_MATRIX = {{-1,0},{-1,-1},{-1,1},{0,-1},{0,1},{1,0},{1,1},{1,-1}} ; //a gauche row et a droite col
    private final int ROW_POSITION_IN_MATRIX = 0;
    private final int COL_POSITION_IN_MATRIX = 1;

    private int middleRow;
    private int middleColumn;
    private int firstTemperature;
    private String selectedHeatMode;

    private double cost;  //n = nbr sec source allumée, d=temperature : n*d^2  <--- 1 source  ---> cost toutes les sources 
    /*
     * ex temp = 10
     * sec 1 : 1*10^2 = 100
     * sec 2 : 2*10^2 = 200 ... 300, 400
     * 
     * cost += 10^2
     */

    Log log = new Log();

    private HashMap<String,Cell> heatCellMap = new HashMap<String,Cell>();
    //private HashMap<String,Integer> heatCellMapSeconds = new HashMap<String,Integer>();
    private ArrayList<String> heatCellSecondsList = new ArrayList<String>();
    

    public ThermoController(Stage primaryStage){
        this.thermoView = new ThermoView(primaryStage);
        cellConfigurationView=new CellConfigurationView();
        if(LASTCOLUMN%2==0 && LASTROW%2==0){ /////A changer  ---> pas optimal si on veut changer le nombre de sources de chaleurs au debut
            startHeatSources = new int[][]{
                {0, 0}, {0, NUMBER_COLUMNS - 1}, {NUMBER_ROWS - 1, 0}, {NUMBER_ROWS - 1, NUMBER_COLUMNS - 1}, {NUMBER_ROWS / 2, NUMBER_COLUMNS / 2}
            };
        }
        else{
            startHeatSources = new int[][]{
                {0, 0}, {0, NUMBER_COLUMNS - 1}, {NUMBER_ROWS - 1, 0}, {NUMBER_ROWS - 1, NUMBER_COLUMNS - 1}
            };
        }

        for (int[] heatSource : startHeatSources) { //on met les 4/5 premieres sc dans la liste
            int row = heatSource[ROW_POSITION_IN_MATRIX];  
            int col = heatSource[COL_POSITION_IN_MATRIX]; 
            heatCellSecondsList.add(getCellId(row, col));
            //heatCellMap.put(getCellId(row, col),);
        }
        setActions();

        primaryStage.setOnCloseRequest(e ->{ //quand on ferme l'app on créé le fichier log
            log.createLogFile(); 
        });
    }

    @Override
    public void attach(IThermoObserver thermoObserver){
        this.thermoObserver =thermoObserver;
    }

    @Override
    public void detach(IThermoObserver thermoObserver){
    }

    public static String getCellId(int row, int col){
        return "R"+row+"C"+col;
    }

    public static int getDeadCellNoTemperature() {
        return DEAD_CELL_NO_TEMPERATURE;
    }

    public static int getDistanceMinMaxTemperature() {
        return DISTANCE_MIN_MAX_TEMPERATURE;
    }

    public static int getEstimatedMedianTemperature() {
        return ESTIMATED_MEDIAN_TEMPERATURE;
    }

    public static int getHeatCellStartTemperature() {
        return HEAT_CELL_START_TEMPERATURE;
    }

    public static int getMAXIMUM_NUMBER_ROWS_AND_COLUMNS() {
        return MAXIMUM_NUMBER_ROWS_AND_COLUMNS;
    }

    public static int getMINIMUM_NUMBER_ROWS_AND_COLUMNS() {
        return MINIMUM_NUMBER_ROWS_AND_COLUMNS;
    }

    public static int getNumberColumns() {
        return NUMBER_COLUMNS;
    }

    public static int getNumberRows() {
        return NUMBER_ROWS;
    }

    public void incrementTime(){
        NotifyThermoViewOfSystemAttributes(numberSeconds,averageTemperature,outsideTemperature, cost);//chaque seconde on apl la vue pour changer le nombre de secondes sur le bouton temps
        numberSeconds++; //on doit le mettre après le notify car sinon on a un décalage de 1s
    }    

    //On notifie à la vue qu'elle peut mettre à jour les secondes
    @Override
    public void NotifyThermoViewOfSystemAttributes(int numberSeconds, double averageTemperature, double outsideTemperature, double cost){
        thermoObserver.updateSystemAttributes(numberSeconds, averageTemperature, outsideTemperature, cost);
    }
    
    private void setActions() {
        exteriorTemperatureParser=new ExteriorTemperatureParser(exteriorTemperatureFile);
        firstTemperature=exteriorTemperatureParser.getFirstTemperature();
        attach(thermoView);//on met la vue comme observer

        if(!areCellsCreated){//creation des cellules
            cellFactory=new CellFactory(firstTemperature);
            cellFactory.setHeatSources(startHeatSources); //important de le faire avant la création des cellules
            cellFactory.createCells(thermoView); //on veut creer les cellules que une seule fois au debut
            cellMap = cellFactory.getCellMap();
            
            areCellsCreated=true;
        } 

        //Lorsque le bouton start dans la vue a été cliqué, ...
        thermoView.getStartButton().setOnAction(e -> {
            if(!isSystemPlaying)startTimer(); //si on a déjà appuyé sur play, alors re-appuyé sur play n'aura pas d'effets
            check();
        });
        thermoView.getPauseButton().setOnAction(e -> {
            pauseSystem();
        });
        thermoView.getResetButton().setOnAction(e -> {
            timeline.stop();
            timeline.jumpTo(Duration.ZERO);
            numberSeconds = zero;
            areCellsCreated=false; //aussi couleur ... a reset
            NotifyThermoViewOfSystemAttributes(numberSeconds,averageTemperature,outsideTemperature,cost); //quand on appuie sur reset on notifie la vue ...
        });
        
    }
    private void check(){
        for (int row = 0; row < NUMBER_ROWS; row++) {
            for (int col = 0; col < NUMBER_COLUMNS; col++) {
                final int rowCopy = row;
                final int colCopy = col;
                Cell cell = cellMap.get(getCellId(row, col));
                //a voir si nbrAliveCells initial on peut pas le recup de la factory ?
                if(!cell.isCellDead())numberAliveCells++; //pour calculer la temp.moyenne par la suite on prend en compte que les cellules vivantes
                if(cell.isHeatCell()){ //On doit mettre ce if avant l'autre car dans l'autre if a la fin du calculateCellTemp ya un notify pour la vue
                    heatCellMap.put(getCellId(rowCopy,colCopy),cell);
                    //dans le cas ou strategie manuel!!!!!!! 
                    if(currentStrategy.equals("Manual Mode")){
                        if(thermoView.getHeatCellButton(getCellId(row, col))!=null){//doit faire ce if car lorsque créer sc, pas encore dans heatCellbtn
                            thermoView.getHeatCellButton(getCellId(row, col)).setOnAction(e -> {//lorsque un click est effectué sur une sc a gauche
                                System.out.println("Dans ctrll");
                                heatCellStrategy.applyStrategy(cell, averageTemperature, heatCellMap, numberAliveCells);
                                //cell.setDiffuseHeat(!cell.isHeatDiffuser());//si diffuseHeat=true alors on le passe a false, si false alors on met true
                                
                                if(cell.isHeatDiffuser()){//qd sc activée on la met dans la liste
                                    heatCellSecondsList.add(getCellId(rowCopy,colCopy));
                                }
                                else{//qd desac on l'enleve de la liste
                                    heatCellSecondsList.remove(getCellId(rowCopy,colCopy));
                                }
                            });
                        }  
                    }       
                }
                thermoView.getCellButton(getCellId(row, col)).setOnAction(event -> {//click sur une cellule de la grille
                    //il est interdit de mettre en paramètre une valeur qui n'est pas finale et qui s'incrémente à chaque fois  --> faire une copie en final
                    pauseSystem();  //--> enlever des commentaires une fois que setAction du form fonctionne
                    cellConfigurationView.display(rowCopy,colCopy,cell.isHeatCell(),cell.isCellDead());
                });
            }
        }
        //Fonctionne ici mais pas dans le for car la boucle for continue et on aura pas les bonnes valeurs pour row et col donc attribuera ce qu'on a fait a la derniere valeur
        //si on enleve ou ajoute cellule morte, faut changer le numberAlivesCells
        if(cellConfigurationView.getSubmitButton()!=null){//Il faut vérifier que c'est pas null sinon nullpointerexception);
            cellConfigurationView.getSubmitButton().setOnAction(e -> {//lors de la validation du formulaire
                Cell cell = cellMap.get(cellConfigurationView.getCellId());
                //va update la cellule en fonction du formulaire dans son modèle et récupérer en meme temps un changement dans nombre cell vivantes
                numberAliveCells += cell.updateCell(cellConfigurationView.isClickedOnDeadCell(),cellConfigurationView.isClickedOnHeatCell(),cellConfigurationView.getChoiceTemperature(),averageTemperature);
                if(cell.isHeatDiffuser()){//qd sc activée via panneau config on la met dans la liste  //attention duplication !!!!!!!!!
                    heatCellSecondsList.add(cellConfigurationView.getCellId());
                }
                cellConfigurationView.closeWindow(); //une fois les données enregistrées, on ferme la popupc
                startTimer();
            });
        } 
        thermoView.getHeatModeCombobox().valueProperty().addListener((observable, oldValue, newValue) -> {
            selectedHeatMode=newValue;//=valeur selectionné dans combobox
            currentStrategy=newValue; 
            if(currentStrategy.equals("Manual Mode")){
                System.out.println("target mode");
                heatCellStrategy=new ManualStrategy();
            }
            else{
                System.out.println("target mode");
                heatCellStrategy=new TargetStrategy();
            } 
        });
    }

    private void pauseSystem(){
        timeline.pause();
        isSystemPlaying=false;
    }

    private void startTimer() {
        isSystemPlaying=true;
        Duration duration = Duration.seconds(1);
        EventHandler<ActionEvent> eventHandler = e -> {
            incrementTime(); //toutes les secondes on incrémente le temps
            if(currentStrategy.equals("Target Mode")){
                Cell uselessCell = new Cell();
                heatCellStrategy.applyStrategy(uselessCell, averageTemperature, heatCellMap, numberAliveCells);
            } 
            calculate(); 
            log.addLog(numberSeconds,outsideTemperature,averageTemperature); //ajout a chaque seconde des infos pour le log
        };
        keyFrame = new KeyFrame(duration, eventHandler);
        timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }    

    ///////////////////////////////A CHANGER
    ///////// LES SET ON ACTION NE SONT PAS A RECUP TOUTES LES SECONDES !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private void calculate(){ //Calcule temperature pour chaque case et regarde si source chaleur a été activé/desactive et fait moyenne
       ///a voir si on peut pas mettre la premiere ligne dans la classe extTemp
        
        outsideTemperature = exteriorTemperatureParser.getNexExteriorTemperature(); 
        double allAliveCellsTemperature=0;
        for (int row = 0; row < NUMBER_ROWS; row++) {
            for (int col = 0; col < NUMBER_COLUMNS; col++) {
                Cell cell = cellMap.get(getCellId(row, col));      
                cell.calculateCellTemperature(ADJACENT_ITEMS_MATRIX, outsideTemperature, row, col,cellMap);
                if(!cell.isCellDead()){
                    allAliveCellsTemperature+=cell.getTemperature();
                }

                //Pour le cout
                if(heatCellSecondsList.contains(getCellId(row, col))){ 
                    cost += cell.getTemperature()*cell.getTemperature();
                }

            }
        }
        averageTemperature = allAliveCellsTemperature/numberAliveCells; //on met la variable contenant la temp moyenne a jour, celle-ci est présente dans le notify donc fonctionne
    }
}            