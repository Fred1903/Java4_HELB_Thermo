package com.example;

import java.util.ArrayList;
import java.util.HashMap;

<<<<<<< HEAD
//System est le créateur des cellules ---- Controller est le systeme, donc a voir si ce code n est pas a mettre dans le controller
=======
//System est le créateur des cellules 
>>>>>>> db61e8ac2c49f2eaada66b0435a14931b4b7b2f3
public class TheSystem implements ICellObservable {
    //private HashMap<String,Boolean> cellMap;  //String = Position (Ex : R2C4 R=row C=col), Boolean = sourceChaleur ou pas
    private int firstRowOrCol = 0;
    private final int LASTROW = ThermoController.getNumberRows()-1;
    private final int LASTCOLUMN = ThermoController.getNumberColumns()-1;
    private int middleRow;
    private int middleColumn;
<<<<<<< HEAD

    

    private ICellObserver cellObserver;

    private CellFactory cellFactory;


    @Override
    public void attachCellObserver(ICellObserver cellObserver) {
        //cellObservers.add(cellObserver);
        this.cellObserver = cellObserver;
    }

    //Création des cellules par le système
    public void createCells(){
        for (int row = 0; row < ThermoController.getNumberRows(); row++) {
            for (int col = 0; col < ThermoController.getNumberColumns(); col++) {
                Cell cell = new Cell();
                if(isCellEligibleToHeatSource(row,col)){
                    NotifyThermoView(row, col, false); //false pr dire pas cellule morte (attetion constante magique) 
                }
                else{
                    cellFactory= new CellFactory(cell);
                    if(cellFactory.isCellDead(row,col)){ //si cellule morte 
                        //cell.setDead(true);
                        NotifyThermoView(row, col, true); //Attntion constante magique
                    }
                }
            }
        }
    }
    

    @Override
    public void detachCellObserver(ICellObserver cellObserver) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'detachCellObserver'");
    }

=======
    private ArrayList<ICellObserver> cellObservers = new ArrayList<ICellObserver>();


    @Override
    public void attachCellObserver(ICellObserver cellObserver) {
        cellObservers.add(cellObserver);
    }

    //Création des cellules par le système
    public void createCells(){
        for (int row = 0; row < ThermoController.getNumberRows(); row++) {
            for (int col = 0; col < ThermoController.getNumberColumns(); col++) {
                Cell cell = new Cell();
                if(isCellEligibleToHeatSource(row,col)){
                    NotifyThermoView(row, col); 
                }
            }
        }
    }
    

    @Override
    public void detachCellObserver(ICellObserver cellObserver) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'detachCellObserver'");
    }

>>>>>>> db61e8ac2c49f2eaada66b0435a14931b4b7b2f3
    //On verifie si la cellule est dans un des 4 coins ou au milieu, au milieu pas encore verif
    public boolean isCellEligibleToHeatSource(int row, int col){
        //Est-ce que c'est considéré comme de la répétition de code ?
        if((row == firstRowOrCol && col == firstRowOrCol) || (row==firstRowOrCol && col == LASTCOLUMN) ||
            (row==LASTROW && col==firstRowOrCol) || (row==LASTROW && col==LASTCOLUMN) || checkIfMiddle(row,col)){
<<<<<<< HEAD
=======
                System.out.println("lastrow="+LASTROW+" lastcol="+LASTROW+" firstRowCol="+firstRowOrCol);
>>>>>>> db61e8ac2c49f2eaada66b0435a14931b4b7b2f3
                return true;
        }/////////Qd col et row =11/13 coin en bas gauche mauvais affichage
        return false;
    }

    public boolean checkIfMiddle(int row, int col){
        if(LASTCOLUMN%2==0 && LASTROW%2==0){
            middleColumn= LASTCOLUMN/2;
            middleRow = LASTROW/2;
            if(row==middleRow && col == middleColumn)return true;
        }
        return false;
    }

    
    //On dit a la vue qu'elle peut changer la couleur des sources de chaleur
<<<<<<< HEAD
    //et en 3ieme facteur la temperature ? 
    @Override
    public void NotifyThermoView(int row, int col, boolean isCellDead) {
        cellObserver.updateCellColor(row,col, isCellDead); 
=======
    @Override
    public void NotifyThermoView(int row, int col) {
        for (ICellObserver cellObserver : cellObservers) {
            cellObserver.updateCellColor(row,col); //chaque source de chaleur est a true
        }
>>>>>>> db61e8ac2c49f2eaada66b0435a14931b4b7b2f3
    }
    
}
