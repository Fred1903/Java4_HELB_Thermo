package com.example;

import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
//import main.java.com.example.ExteriorTemperature;
//import main.java.com.example.ExteriorTemperatureParser;

///Notes : Fabrique pour cellule normale ou morte  --> fabrique si morte mets juste le boolean à true pour isDead, fabrique renvoie pas forcément une classe
public class ThermoController implements IThermoObservable, ICellObservable {

    private ThermoView thermoView;
    private Timeline timeline;
    private KeyFrame keyFrame; 

    private boolean areCellsCreated=false;
    private boolean isSystemPlaying = false;

    
    private final static int FIRST_ROW_COL = 0;
    
    private final static int MINIMUM_NUMBER_ROWS_AND_COLUMNS = 3;
    private final static int MAXIMUM_NUMBER_ROWS_AND_COLUMNS = 12;
    private final static int NUMBER_ROWS =3;
    private final static int NUMBER_COLUMNS = 3;
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
    private int numberAliveCells=0;

    private double outsideTemperature;
    private double averageTemperature;

    private HashMap<String,Cell> cellMap = new HashMap<String,Cell>();

    private final String exteriorTemperatureFile = "src/main/java/com/example/simul.data.txt" ;

    private CellFactory cellFactory;

    private ICellObserver cellObserver;
    private IThermoObserver thermoObserver;

    private ExteriorTemperatureParser exteriorTemperatureParser;

    private static int[][] startHeatSources; //on ne peut pas mettre en final ici car alors on ne pourra pas initialisée par la suite
    private final int [][] ADJACENT_ITEMS_MATRIX = {{-1,0},{-1,-1},{-1,1},{0,-1},{0,1},{1,0},{1,1},{1,-1}} ; //a gauche row et a droite col
    private final int ROW_POSITION_ADJACENT_ITEMS_MATRIX = 0;
    private final int COL_POSITION_ADJACENT_ITEMS_MATRIX = 1;
    

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
        for (int row = 0; row < NUMBER_ROWS; row++) {
            for (int col = 0; col < NUMBER_COLUMNS; col++) {
                Cell cell = new Cell();
                if(isCellEligibleToHeatSource(row,col)){
                    cell.setDiffuseHeat(true);
                    cell.setTemperature(HEAT_CELL_START_TEMPERATURE); 
                }
                else{
                    cellFactory= new CellFactory(cell);
                    if(cellFactory.isCellDead(row,col)){ //si cellule morte 
                        //cell.setDead(true);  //on le fait dans la factory
                        cell.setTemperature(DEAD_CELL_NO_TEMPERATURE);
                    }
                }
                if(!cell.isCellDead())numberAliveCells++;//pour calculer la temp.moyenne par la suite on prend en compte que les cellules vivantes
                String cellId = getCellId(row,col);
                cellMap.put(cellId,cell); //attention put pas add pour hashmap
                NotifyThermoView(row, col,cell.isHeatDiffuser(), cell.getTemperature()); //on notifie la temperature de la cellule
            }
        }
        areCellsCreated=true;
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

    //La meme methode dans view, ok ou duplication ?
    public String getCellId(int row, int col){
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
        NotifyThermoViewOfSystemAttributes(numberSeconds,averageTemperature,outsideTemperature);//chaque seconde on apl la vue pour changer le nombre de secondes sur le bouton temps
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
    public void NotifyThermoViewOfSystemAttributes(int numberSeconds, double averageTemperature, double outsideTemperature){
        thermoObserver.updateSystemAttributes(numberSeconds, averageTemperature, outsideTemperature);
    }


    //On dit a la vue qu'elle peut changer la couleur des sources de chaleur
    //et en 4ieme parametre la temperature pr la couleur ?  //ou par ex cell morte a temperature null ? et null=noir
    @Override
    public void NotifyThermoView(int row, int col, boolean isHeatCell, double cellTemperature) {
        cellObserver.updateCellColor(row,col, isHeatCell, cellTemperature); 
    }
    

    private void setActions() {
        exteriorTemperatureParser=new ExteriorTemperatureParser(exteriorTemperatureFile);
        attachCellObserver(thermoView);

        //Lorsque le bouton start dans la vue a été cliqué, ...
        thermoView.getStartButtonListener(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {  
                if(!areCellsCreated)createCells(); //on veut creer les cellules que une seule fois au debut
                if(!isSystemPlaying)startTimer(); //si on a déjà appuyé sur play, alors re-appuyé sur play n'aura pas d'effets
            }
        });
        thermoView.getPauseButtonListener(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                timeline.pause();
                isSystemPlaying=false;
            }
        });
        thermoView.getResetButtonListener(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                timeline.stop();
                timeline.jumpTo(Duration.ZERO);
                numberSeconds = zero;
                areCellsCreated=false; //aussi couleur ... a reset
                NotifyThermoViewOfSystemAttributes(numberSeconds,averageTemperature,outsideTemperature); //quand on appuie sur reset on notifie la vue ...
            }
        });
        attach(thermoView);
    }


    private void startTimer() {
        isSystemPlaying=true;
        Duration duration = Duration.seconds(1);
        EventHandler<ActionEvent> eventHandler = e -> {
            incrementTime(); //toutes les secondes on incrémente le temps
            ExteriorTemperature ext = exteriorTemperatureParser.getNexExteriorTemperature();
            calculate();
        };
        keyFrame = new KeyFrame(duration, eventHandler);
        timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }    

    private void calculate(){
        ExteriorTemperature exteriorTemperature = exteriorTemperatureParser.getNexExteriorTemperature();
        outsideTemperature = exteriorTemperature.getExteriorTemperature(); 
        double allAliveCellsTemperature=0;
        for (int row = 0; row < NUMBER_ROWS; row++) {
            for (int col = 0; col < NUMBER_COLUMNS; col++) {
                Cell cell = cellMap.get(getCellId(row, col));
                if(/*!cell.iHeatDiffuser() &&*/  !cell.isCellDead()){
                    if(!cell.isHeatDiffuser()){
                        double [] adjacentItemsTemperatures=new double[ADJACENT_ITEMS_MATRIX.length];//nombre de temperatures de cases adjacentes d'une cellule
                        //La 9ieme temperature est celle de la cellule meme qu'on peut directement recuperer dans la classe cell

                        for(int adjacentItem=0; adjacentItem<adjacentItemsTemperatures.length;adjacentItem++){
                            int rowOfAdjacentItem = ADJACENT_ITEMS_MATRIX[adjacentItem][0];
                            int colOfadjacentItem = ADJACENT_ITEMS_MATRIX [adjacentItem][1];
                            /// rowOfAdjacentItem n'est pas son row mais son row par rapport a notre row
                            adjacentItemsTemperatures[adjacentItem]=getTemperatureOfAdjacentItem((row+rowOfAdjacentItem), (col+colOfadjacentItem), outsideTemperature);
                        }
                        cell.calculateTemperature(adjacentItemsTemperatures);   
                                
                        NotifyThermoView(row, col,cell.isHeatDiffuser(), cell.getTemperature()); //on notifie la temperature de la cellule
                    }
                    allAliveCellsTemperature+=cell.getTemperature();
                }
            }
        }
        averageTemperature = allAliveCellsTemperature/numberAliveCells; //on met la variable contenant la temp moyenne a jour, celle-ci est présente dans le notify donc fonctionne
    }

    private double getTemperatureOfAdjacentItem(int row, int col, double outsideTemperature){
        Cell cellNextTo = cellMap.get(getCellId(row, col));
        //si l'id de la case est dans la hashmap alors c'est une cellule et on renvoie sa temperature, sinon c'est a l'exterieur et on renvoie temp ext.
        if(cellNextTo != null){
            return cellNextTo.getTemperature();
        }
        else{
            return outsideTemperature;
        }
    }

    
}
