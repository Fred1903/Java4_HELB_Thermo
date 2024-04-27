package com.example;

import javafx.stage.Stage;

import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

///Notes : Fabrique pour cellule normale ou morte  --> fabrique si morte mets juste le boolean à true pour isDead, fabrique renvoie pas forcément une classe
public class ThermoController implements IThermoObservable, ICellObservable {

    private ThermoView thermoView;
    private Timeline timeline;
    private KeyFrame keyFrame; 

    private final static int FIRST_ROW_COL = 0;
    
    private final static int MINIMUM_NUMBER_ROWS_AND_COLUMNS = 3;
    private final static int MAXIMUM_NUMBER_ROWS_AND_COLUMNS = 12;
    private final static int NUMBER_ROWS =11;
    private final static int NUMBER_COLUMNS = 11;
    private final static int LASTROW = NUMBER_ROWS-1;
    private final static int LASTCOLUMN = NUMBER_COLUMNS-1;
    private final static int HEAT_CELL_START_TEMPERATURE = 18; 
    private final static int DEAD_CELL_NO_TEMPERATURE = -500; //cellule morte n'a pas de temperature mais comme un int ne peut pas etre 'null' on va lui mettre -500
    private final static int ESTIMATED_MIN_TEMPERATURE = 0;
    private final static int ESTIMATED_MAX_TEMPERATURE = 50;
    private final static int DISTANCE_MIN_MAX_TEMPERATURE = ESTIMATED_MAX_TEMPERATURE-(+ESTIMATED_MIN_TEMPERATURE);
    private final static int ESTIMATED_MEDIAN_TEMPERATURE = DISTANCE_MIN_MAX_TEMPERATURE/2;



    private int middleRow;
    private int middleColumn;
    private int zero = 0;
    private int numberSeconds = 0;

    private CellFactory cellFactory;

    private ICellObserver cellObserver;
    private IThermoObserver thermoObserver;

    private static int[][] startHeatSources; //on ne peut pas mettre en final ici car alors on ne pourra pas initialisée par la suite 
    

    public ThermoController(Stage primaryStage){
        this.thermoView = new ThermoView(primaryStage);
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
        setActions();
    }

    @Override
    public void attach(IThermoObserver thermoObserver){
        //thermoObservers.add(thermoObserver);
        this.thermoObserver =thermoObserver;
    }

    @Override
    public void attachCellObserver(ICellObserver cellObserver) {
        this.cellObserver = cellObserver;
    }

    //Regarde si la case qu'on a donné en paramètre est la case du milieu
    public boolean checkIfMiddle(int row, int col){
        if(LASTCOLUMN%2==0 && LASTROW%2==0){
            middleColumn= LASTCOLUMN/2;
            middleRow = LASTROW/2;
            if(row==middleRow && col == middleColumn)return true;
        }
        return false;
    }

    //Création des cellules par le système (controller = système)
    public void createCells(){
        for (int row = 0; row < ThermoController.getNumberRows(); row++) {
            for (int col = 0; col < ThermoController.getNumberColumns(); col++) {
                Cell cell = new Cell();
                if(isCellEligibleToHeatSource(row,col)){
                    cell.setDiffuseHeat(true);
                    cell.setTemperature(HEAT_CELL_START_TEMPERATURE); 
                    //NotifyThermoView(row, col, false); //false pr dire pas cellule morte (attetion constante magique) 
                }
                else{
                    cellFactory= new CellFactory(cell);
                    if(cellFactory.isCellDead(row,col)){ //si cellule morte 
                        //cell.setDead(true);  //on le fait dans la factory
                        cell.setTemperature(DEAD_CELL_NO_TEMPERATURE);
                        //NotifyThermoView(row, col, true); //Attntion constante magique
                    }
                }
                NotifyThermoView(row, col,cell.iHeatDiffuser(), cell.getTemperature()); //on notifie la temperature de la cellule
            }
        }
    }

    @Override
    public void detach(IThermoObserver thermoObserver){
        //thermoObservers.remove(thermoObserver);
    }

    @Override
    public void detachCellObserver(ICellObserver cellObserver) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'detachCellObserver'");
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

    public static int[][] getStartHeatSources(){
        return startHeatSources;
    }

    public void incrementTime(){
        numberSeconds++;
        Notify();//chaque seconde on apl la vue pour changer le nombre de secondes sur le bouton temps
    }

    //On verifie si la cellule est dans un des 4 coins ou au milieu, au milieu pas encore verif
    public boolean isCellEligibleToHeatSource(int row, int col){
        //Est-ce que c'est considéré comme de la répétition de code ?
        if((row == FIRST_ROW_COL && col == FIRST_ROW_COL) || (row==FIRST_ROW_COL && col == LASTCOLUMN) ||
            (row==LASTROW && col==FIRST_ROW_COL) || (row==LASTROW && col==LASTCOLUMN) || checkIfMiddle(row,col)){
                return true;
        }
        return false;
    }

    //On notifie à la vue qu'elle peut mettre à jour les secondes
    @Override
    public void Notify(){
        thermoObserver.update(numberSeconds);
    }


    //On dit a la vue qu'elle peut changer la couleur des sources de chaleur
    //et en 4ieme parametre la temperature pr la couleur ?  //ou par ex cell morte a temperature null ? et null=noir
    @Override
    public void NotifyThermoView(int row, int col, boolean isHeatCell, int cellTemperature) {
        cellObserver.updateCellColor(row,col, isHeatCell, cellTemperature); 
    }
    

    private void setActions() {
        //Lorsque le bouton start dans la vue a été cliqué, ...
        thermoView.getStartButtonListener(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //On créé les cellules une fois la timeline activé, si on veut directement créé, juste mettre au début du setActions 
                attachCellObserver(thermoView);
                createCells();
                startTimer();
            }
        });
        thermoView.getPauseButtonListener(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                timeline.pause();
            }
        });
        thermoView.getResetButtonListener(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                timeline.stop();
                timeline.jumpTo(Duration.ZERO);
                numberSeconds = zero;
                Notify(); //quand on appuie sur reset on notifie la vue ...
            }
        });
        attach(thermoView);
    }


    private void startTimer() {
        Duration duration = Duration.seconds(1);
        EventHandler<ActionEvent> eventHandler = e -> incrementTime(); //toutes les secondes on incrémente le temps
        keyFrame = new KeyFrame(duration, eventHandler);
        timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }    
}
