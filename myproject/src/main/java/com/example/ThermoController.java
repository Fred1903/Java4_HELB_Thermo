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
    private CellFactory cellFactory;
    private ICellObserver cellObserver;
    private IThermoObserver thermoObserver;
    private HeatCellStrategy heatCellStrategy = ManualStrategy.getInstance();//on commence avec manual strategie
    private ExteriorTemperatureParser exteriorTemperatureParser;
    private Log log = new Log();

    private boolean areCellsCreated=false;
    private boolean isSystemPlaying = false;
    private boolean numberAliveCellsIsNull = true;
    private boolean isConfigurationValid;

    
    private static int HEAT_CELL_START_TEMPERATURE = 27; 
    private final int TIMER_NUMBER_SECONDS_AT_START = 0;
    private final int START_COST = 0;
    private final static int FIRST_ROW_COL = 0;
    private final static int MINIMUM_NUMBER_ROWS_AND_COLUMNS = 3;
    private final static int MAXIMUM_NUMBER_ROWS_AND_COLUMNS = 12;
    private final static int NUMBER_ROWS = 4;
    private final static int NUMBER_COLUMNS = 4;
    private final static int LASTROW = NUMBER_ROWS-1;
    private final static int LASTCOLUMN = NUMBER_COLUMNS-1;
    private final static int DEAD_CELL_NO_TEMPERATURE = -500; //cellule morte n'a pas de temperature mais comme un int ne peut pas etre 'null' on va lui mettre -500
    private final static int MAXIMUM_TEMPERATURE = 100;
    private final static int MINIMUM_TEMPERATURE = 0;
    private final static int GAP_BETWEEN_MIN_MAX_TEMPERATURE = MAXIMUM_TEMPERATURE-MINIMUM_TEMPERATURE;
    private final int ROW_POSITION_IN_MATRIX = 0;
    private final int COL_POSITION_IN_MATRIX = 1;
    private final int [][] ADJACENT_ITEMS_MATRIX = {{-1,0},{-1,-1},{-1,1},{0,-1},{0,1},{1,0},{1,1},{1,-1}} ; //a gauche row et a droite col
    private int[][] startHeatSources; //on ne peut pas mettre en final ici car alors on ne pourra pas initialisée par la suite
    private int numberSeconds = TIMER_NUMBER_SECONDS_AT_START;
    private int numberAliveCells=0;
    private int middleRow;
    private int middleColumn;
    private int firstTemperature;

    private double outsideTemperature;
    private double averageTemperature;
    private double cost = START_COST;  //n = nbr sec source allumée, d=temperature : n*d^2  <--- 1 source  ---> cost toutes les sources 

    private HashMap<String,Cell> cellMap = new HashMap<String,Cell>();
    private HashMap<String,Cell> heatCellMap = new HashMap<String,Cell>();

    private ArrayList<String> heatCellSecondsList = new ArrayList<String>();
    
    private final String exteriorTemperatureFile = "src/main/java/com/example/simul.data.txt" ;
    private final static String MANUAL_MODE_STRING = "Manual Mode";
    private final static String TARGET_MODE_STRING = "Target Mode";
    private final static String SUCCESSIVE_MODE_STRING = "Successive Mode";
    private String currentStrategy = "Manual Mode"; //on commence avec manual mode comme strategie
    private String selectedHeatMode;


    public ThermoController(Stage primaryStage){

        if(NUMBER_COLUMNS >= MINIMUM_NUMBER_ROWS_AND_COLUMNS && NUMBER_ROWS <=MAXIMUM_NUMBER_ROWS_AND_COLUMNS
           && NUMBER_COLUMNS<=MAXIMUM_NUMBER_ROWS_AND_COLUMNS && NUMBER_ROWS>=MINIMUM_NUMBER_ROWS_AND_COLUMNS){
            isConfigurationValid=true;
        }
        else{
            isConfigurationValid=false;
        }

        this.thermoView = new ThermoView(primaryStage, isConfigurationValid);

        if(isConfigurationValid){ //on fait le reste que si ok pck sinon erreur
            cellConfigurationView=new CellConfigurationView();

            if(HEAT_CELL_START_TEMPERATURE<MINIMUM_TEMPERATURE)HEAT_CELL_START_TEMPERATURE=MINIMUM_TEMPERATURE;
            if(HEAT_CELL_START_TEMPERATURE>MAXIMUM_TEMPERATURE)HEAT_CELL_START_TEMPERATURE=MAXIMUM_TEMPERATURE;

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
            }
            setActions();

            primaryStage.setOnCloseRequest(e ->{ //quand on ferme l'app on créé le fichier log
                System.out.println("on close request");
                log.createLogFile(); 
            });
        } 
    }

    @Override
    public void attach(IThermoObserver thermoObserver){
        this.thermoObserver =thermoObserver;
    }

    private void calculate(){ //Calcule temperature pour chaque case et regarde si source chaleur a été activé/desactive pour mettre a jour´
        //numberAliceC, calcule le cout et fait temperature moyenne        
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
                    cost += cell.getTemperature()*cell.getTemperature(); //chaque seconde on rajoute le cout de la seconde
                }
            }
        }
        averageTemperature = allAliveCellsTemperature/numberAliveCells; //on met la variable contenant la temp moyenne a jour, celle-ci est présente dans le notify donc fonctionne
    }

    private void checkChanges(){//fais la stratégie manuel, détecte si une cellule a été clické et ouvre popup de config et détecte si changement de mode chauffe
        for (int row = 0; row < NUMBER_ROWS; row++) {
            for (int col = 0; col < NUMBER_COLUMNS; col++) {
                final int rowCopy = row;
                final int colCopy = col;
                Cell cell = cellMap.get(getCellId(row, col));
                if(!cell.isCellDead() && numberAliveCellsIsNull)numberAliveCells++; //pour calculer la temp.moyenne par la suite on prend en compte que les cellules vivantes
                if(cell.isHeatCell()){ //On doit mettre ce if avant l'autre car dans l'autre if a la fin du calculateCellTemp ya un notify pour la vue
                    heatCellMap.put(getCellId(rowCopy,colCopy),cell); 
                    //dans le cas ou strategie manuel
                    if(currentStrategy.equals(MANUAL_MODE_STRING)){
                        if(thermoView.getHeatCellButton(getCellId(row, col))!=null){//doit faire ce if car lorsque créer sc, pas encore dans heatCellbtn
                            thermoView.getHeatCellButton(getCellId(row, col)).setOnAction(e -> {//lorsque un click est effectué sur une sc a gauche
                                heatCellStrategy.applyStrategy(cell, averageTemperature, heatCellMap, numberAliveCells);
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
                    pauseSystem(); 
                    //il est interdit de mettre en paramètre une valeur qui n'est pas finale et qui s'incrémente à chaque fois  --> faire une copie en final
                    cellConfigurationView.display(rowCopy,colCopy,cell.isHeatCell(),cell.isCellDead());
                });
            }
        }
        if(numberAliveCellsIsNull)numberAliveCellsIsNull=false; //on les compte une fois au début et pas quand rappel le check
        //Fonctionne ici mais pas dans le for car la boucle for continue et on aura pas les bonnes valeurs pour row et col donc attribuera ce qu'on a fait a la derniere valeur
        //si on enleve ou ajoute cellule morte, faut changer le numberAlivesCells
        if(cellConfigurationView.getSubmitButton()!=null){//Il faut vérifier que c'est pas null sinon nullpointerexception);
            cellConfigurationView.getSubmitButton().setOnAction(e -> {//lors de la validation du formulaire
                Cell cell = cellMap.get(cellConfigurationView.getCellId());
                //va update la cellule en fonction du formulaire dans son modèle et récupérer en meme temps un changement dans nombre cell vivantes
                numberAliveCells += cell.updateCell(cellConfigurationView.isClickedOnDeadCell(),cellConfigurationView.isClickedOnHeatCell(),cellConfigurationView.getChoiceTemperature(),averageTemperature);
                if(cell.isHeatDiffuser() && !heatCellMap.containsKey(cellConfigurationView.getCellId())){//qd sc activée via panneau config on la met dans la liste  //attention duplication !!!!!!!!!
                    heatCellSecondsList.add(cellConfigurationView.getCellId());
                    heatCellMap.put(cellConfigurationView.getCellId(),cell);
                }
                //si on a enlevé la sc on la retire de la map des sc et des secondes sc
                else if(!cell.isHeatCell() && heatCellMap.containsKey(cellConfigurationView.getCellId())){
                    heatCellMap.remove(cellConfigurationView.getCellId());
                    heatCellSecondsList.remove(cellConfigurationView.getCellId());
                }
                cellConfigurationView.closeWindow(); //une fois les données enregistrées, on ferme la popup
                startButtonClicked(); //pr remettre le systeme en play
            });
        } 
        thermoView.getHeatModeCombobox().valueProperty().addListener((observable, oldValue, newValue) -> {
            selectedHeatMode=newValue;//=valeur selectionné dans combobox
            currentStrategy=newValue; 
            if(currentStrategy.equals(MANUAL_MODE_STRING)){
                heatCellStrategy=ManualStrategy.getInstance();
            }
            else if(currentStrategy.equals(TARGET_MODE_STRING)){
                heatCellStrategy=TargetStrategy.getInstance();
            } 
            else{
                heatCellStrategy = SuccesiveStrategy.getInstance();
            }
        });
    }

    public static String getCellId(int row, int col){
        return "R"+row+"C"+col;
    }

    public static int getDeadCellNoTemperature() {
        return DEAD_CELL_NO_TEMPERATURE;
    }

    public static int getHeatCellStartTemperature() {
        return HEAT_CELL_START_TEMPERATURE;
    }

    public static int getGapBetweenMinMaxTemperature() {
        return GAP_BETWEEN_MIN_MAX_TEMPERATURE;
    }

    public static String getManualModeString() {
        return MANUAL_MODE_STRING;
    }

    public static int getMAXIMUM_NUMBER_ROWS_AND_COLUMNS() {
        return MAXIMUM_NUMBER_ROWS_AND_COLUMNS;
    }

    public static int getMaximumTemperature() {
        return MAXIMUM_TEMPERATURE;
    }

    public static int getMINIMUM_NUMBER_ROWS_AND_COLUMNS() {
        return MINIMUM_NUMBER_ROWS_AND_COLUMNS;
    }

    public static int getMinimumTemperature() {
        return MINIMUM_TEMPERATURE;
    }

    public static int getNumberColumns() {
        return NUMBER_COLUMNS;
    }

    public static int getNumberRows() {
        return NUMBER_ROWS;
    }

    public static String getTargetModeString() {
        return TARGET_MODE_STRING;
    }

    public static String getSuccessiveModeString() {
        return SUCCESSIVE_MODE_STRING;
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

    private void pauseSystem(){
        if(timeline!=null)timeline.pause();
        isSystemPlaying=false;
    }
    
    private void setActions() {
        exteriorTemperatureParser=new ExteriorTemperatureParser(exteriorTemperatureFile);
        firstTemperature=exteriorTemperatureParser.getFirstTemperature();
        attach(thermoView);//on met la vue comme observer

        if(!areCellsCreated){//creation des cellules
            cellFactory=new CellFactory(firstTemperature, startHeatSources, thermoView);
            cellMap = cellFactory.getCellMap();
            areCellsCreated=true;
            
        }  
        //Lorsque le bouton start dans la vue a été cliqué, ...
        thermoView.getStartButton().setOnAction(e -> {
            startButtonClicked();
        });
        thermoView.getPauseButton().setOnAction(e -> {
            pauseSystem();
        });
        thermoView.getResetButton().setOnAction(e -> {
            timeline.stop();
            timeline.jumpTo(Duration.ZERO);
            numberSeconds = TIMER_NUMBER_SECONDS_AT_START;
            cost = START_COST;
            pauseSystem();
            NotifyThermoViewOfSystemAttributes(numberSeconds,averageTemperature,outsideTemperature,cost); //quand on appuie sur reset on notifie la vue ...
        });  
    }
    

    public void startButtonClicked(){
        if(!isSystemPlaying)startTimer(); //si on a déjà appuyé sur play, alors re-appuyé sur play n'aura pas d'effets
        checkChanges();
    }

    private void startTimer() {
        isSystemPlaying=true;
        Duration duration = Duration.seconds(1);
        EventHandler<ActionEvent> eventHandler = e -> {
            incrementTime(); //toutes les secondes on incrémente le temps
            Cell uselessCell = new Cell();
            if(currentStrategy.equals(TARGET_MODE_STRING)){
                TargetStrategy targetStrategy = (TargetStrategy) heatCellStrategy;//on doit mettre en target car ya getNewAverageTemp
                targetStrategy.applyStrategy(uselessCell, averageTemperature, heatCellMap, numberAliveCells);
                //TargetStrategy heatCellStrategy = (TargetStrategy) heatCellStrategy;
                //TargetStrategy targetStrategy = (TargetStrategy) heatCellStrategy; 
                //On caste heatCellStrategy en target car la methode getTemperature n'Est pas dans la  classe mere
                //averageTemperature=heatCellStrategy.getNewAverageTemperature();
                averageTemperature = targetStrategy.getNewAverageTemperature();
            } 
            else if(currentStrategy.equals(SUCCESSIVE_MODE_STRING)){
                heatCellStrategy.applyStrategy(uselessCell, averageTemperature, heatCellMap, numberAliveCells);

            }
            calculate(); 
            log.addLog(numberSeconds,cost,outsideTemperature,averageTemperature); //ajout a chaque seconde des infos pour le log
        };
        keyFrame = new KeyFrame(duration, eventHandler);
        timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }    
}            