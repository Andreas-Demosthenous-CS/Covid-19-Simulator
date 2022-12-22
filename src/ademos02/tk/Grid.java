package ademos02.tk;

import java.awt.Color;

/**
 * Afti i klasi onomazetai Grid kai dimiourgei antikeimena typou Grid.
 *
 * H klasi exei ena kataskevasti kai synartisis. O kataskevastis pernei 5
 * integer parametrous. Kai dimiourgounte ta antikeimena Grid.
 *
 * @author Andreas Demosthenous
 * @version 3.0
 * @since 13/05/2020
 */
public class Grid {

    private Hospital hospital;//the hospital of the area
    private Color gridColor;//xroma tou grid
    private Cell grid[][];// disdiastatos pinakas apo cells
    private int length;//to mikos tou grid
    private int width;//platos tou grid
    private int initialPop; // initial population of the area
    private String name;//onoma tou grid

    /**
     * Einai o kataskevastis tis klasis Grid.
     *
     * To mikos kai platos tou grid pernoun timi kai dimiourgite enas 2D array
     * apo cells.
     *
     * @param name
     * @param gridColor
     * @param initialPop
     * @param width
     * @param length
     * @param hospital
     * @param cellHealingTime
     * @param personInfectsCellTime
     * @param personInfectsCellProbability
     *
     */
    public Grid(String name, Color gridColor, int length, int width, int initialPop, Hospital hospital, int cellHealingTime,
            int personInfectsCellTime, int personInfectsCellProbability) {

        this.initialPop = initialPop;
        this.hospital = hospital;
        this.gridColor = gridColor;
        this.length = length;
        this.width = width;
        this.name = name;
        grid = new Cell[length][width];
        //inits cells
        for (int i = 0; i < length; i++) {
            for (int k = 0; k < width; k++) {
                grid[i][k] = new Cell(i, k, cellHealingTime, personInfectsCellTime,
                        personInfectsCellProbability, this);
            }
        }
    }

    /**
     * Einai mia public sinartisi tis klasis Grid.
     *
     * Einai typou Cell afou epistrefei antikeimeno Cell. Epistrefei to cell pou
     * vriskete ston piak grid sto x kai y pou perni parametro
     *
     * @param x
     * @param y
     * @return Epistrefei to Cell pou exei to x kai y pou pernei san parametro
     *
     */
    public Cell getCell(int x, int y) {
        //checking cell is inside the bounds
        if (x < 0 || x >= length || y < 0 || y >= width) {
            return null;
        }
        return grid[x][y];
    }

    /**
     * Einai mia public sinartisi tis klasis Grid.
     *
     * Einai typou int afou epistrefei integer. Einai getter method.
     *
     * @return Epistrefei to platos tou grid
     *
     */
    public int getWidth() {
        return width;
    }

    /**
     * Einai mia public sinartisi tis klasis Grid.
     *
     * Einai typou int afou epistrefei integer. Einai getter method.
     *
     * @return Epistrefei to mikos tou grid
     *
     */
    public int getLength() {
        return length;
    }

    /**
     * Einai mia public sinartisi tis klasis Grid.
     *
     * Einai typou String afou epistrefei String. Einai getter method.
     *
     * @return Epistrefei to onoma tou grid
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Einai mia public sinartisi tis klasis Grid.
     *
     * Einai typou Color afou epistrefei antikeimeno typou Color. Einai getter
     * method.
     *
     * @return Epistrefei to xroma tou grid
     *
     */
    public Color getColor() {
        return gridColor;
    }

    /**
     * getter for the initial population
     * 
     * @return the population
     */
    public int getInitialPopulation() {
        return initialPop;
    }
    
    /**
     * getter for the hospital
     * @return
     */
    public Hospital getHospital(){
        return hospital;
    }

    /**
     * getter for the current amount of people
     * 
     * @return
     */
    public int getPeopleAmount() {
        int amount = 0;
        for (int i = 0; i < width; i++) {
            for (int k = 0; k < width; k++) {
                if (getCell(i, k).isOccupied()) {
                    amount++;
                }
            }
        }
        return amount;
    }

    /**
     * determines whether the quarantine space is full
     * 
     * @return
     */
    public boolean isQuarantineFull() {
        for (int i = 0; i < width; i++) {
            for (int k = 0; k < width; k++) {
                if (getCell(i, k).isQuarantined() && !getCell(i, k).isOccupied()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * determines whether the non-quarantine space is full
     * @return
     */
    public boolean isNonQuarantineFull() {
        for (int i = 0; i < width; i++) {
            for (int k = 0; k < width; k++) {
                if (!getCell(i, k).isQuarantined() && !getCell(i, k).isOccupied()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Einai mia public sinartisi tis klasis Grid.
     *
     * Einai typou boolean afou epistrefei boolean timi.
     *
     * @return Epistrefei an to grid einai gemato
     *
     */
    public boolean isFull() {
        for (int i = 0; i < width; i++) {
            for (int k = 0; k < length; k++) {
                if (!grid[i][k].isOccupied()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * determines whether the grid is empty
     * 
     * @return
     */
    public boolean isEmpty() {
        for (int i = 0; i < width; i++) {
            for (int k = 0; k < length; k++) {
                if (grid[i][k].isOccupied()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Einai mia public sinartisi tis klasis Grid.
     *
     * Einai typou void afou den epistrefei kati. Dimiourgei ena Grid me to
     * analogo size kai xroma
     *
     *
     */
    public void toGraph() {
        GraphicalOutput.InitializeGrid(width, getColor());
    }

}
