package com.example;

import javafx.stage.Stage;

import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

<<<<<<< HEAD
///Notes : Fabrique pour cellule normale ou morte  --> fabrique si morte mets juste le boolean à true pour isDead, fabrique renvoie pas forcément une classe



public class ThermoController implements IThermoObservable {

=======
public class ThermoController implements IThermoObservable { //pq ca a fonctionné sans le implement ???????????????

>>>>>>> db61e8ac2c49f2eaada66b0435a14931b4b7b2f3
    private ThermoView thermoView;
    private TheSystem theSystem = new TheSystem();
    private Timeline timeline;
    private KeyFrame keyFrame; 

<<<<<<< HEAD
    private final static int NUMBER_ROWS =9;
    private final static int NUMBER_COLUMNS = 12;
=======
    private final static int NUMBER_ROWS = 11;
    private final static int NUMBER_COLUMNS = 11;
>>>>>>> db61e8ac2c49f2eaada66b0435a14931b4b7b2f3
    private final static int MINIMUM_NUMBER_ROWS_AND_COLUMNS = 3;
    private final static int MAXIMUM_NUMBER_ROWS_AND_COLUMNS = 12;

    private int zero = 0;
    private int numberSeconds = 0;

    private ArrayList<IThermoObserver> thermoObservers = new ArrayList<>();
    

    public ThermoController(Stage primaryStage){
        this.thermoView = new ThermoView(primaryStage);
        setActions();
    }

    @Override
    public void attach(IThermoObserver thermoObserver){
        thermoObservers.add(thermoObserver);
    }

    @Override
    public void detach(IThermoObserver thermoObserver){
        thermoObservers.remove(thermoObserver);
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
        numberSeconds++;
        Notify();//chaque seconde on apl la vue pour changer le nombre de secondes sur le bouton temps
    }

    @Override
    public void Notify(){
        for(int i = 0 ; i < thermoObservers.size() ; i++){
            thermoObservers.get(i).update(numberSeconds);
        }
    }
    
    private void setActions() {
        
        //Lorsque le bouton start dans la vue a été cliqué, ...
        thermoView.getStartButtonListener(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //On créé les cellules une fois la timeline activé, si on veut directement créé, juste mettre au début du setActions 
                theSystem.attachCellObserver(thermoView);
                theSystem.createCells();
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
