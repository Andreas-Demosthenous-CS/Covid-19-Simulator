package ademos02.tk;

import java.awt.Color;

/**
 * Afti i klasi onomazetai Cell kai dimiourgei antikeimena typou Cell.
 *
 * H klasi exei ena kataskevasti kai synartisis. O kataskevastis pernei 5
 * parametrous.Kai dimiourgounte ta antikeimena Cell.
 *
 * @author Andreas Demosthenous
 * @version 3.0
 * @since 13/05/2020
 */
public class Cell {

    private final int x;//x tou kleiou sto grid
    private final int y;//y tou keliou sto grid
    private boolean isInfected;//an to keli einai infected
    private boolean isOccupied;//an to keli einai kratimeno
    private boolean isQuarantined;// an einai perioxi karantinas
    private Person occupier;//o athropos pou einai sto keli
    private int beingInfectedTime;//poso xrono einai infected
    private int infectedOccupierTime;//xronos pou einai o infected anthropos sto keli
    private int personInfectsCellTime;//poso xrono xriazete o anthropos gia na kani infect to cell
    private int cellHealingTime;//xronos pou xriazete to cell gia na min einai infected
    private int personInfectsCellProbability;
    private boolean isBorder;//metavliti gia to an einai to cell gitoniko me allo area
    private Grid area;//H perioxi pou aniki to cell
    private Grid connectedArea;//H perioxi pou einai gitoniki me to cell

    /**
     * Einai o kataskevastis tis klasis Cell.
     *
     * Arxikopoiei ta pedia x , y personInfectsCellTime,
     * personInfectsCellProbability,cellHealing  kai to area pou anikei to cell
     * analoga me tis parametrous pou pernei kai arxikopiei
     *  ta kelia oste na min einai infected oute kratimena.
     *
     *
     *
     * @param x
     * @param y
     * @param cellHealingTime
     * @param personInfectsCellTime
     * @param personInfectsCellProbability
     * @param area
     */
    public Cell(int x, int y, int cellHealingTime, int personInfectsCellTime, int personInfectsCellProbability, Grid area) {
        this.x = x;
        this.y = y;
        this.isInfected = false;
        this.isOccupied = false;
        this.occupier = null;
        this.isQuarantined = false;
        
        this.infectedOccupierTime = 0;
        this.beingInfectedTime = 0;
        this.cellHealingTime = cellHealingTime;
        this.personInfectsCellTime = personInfectsCellTime;
        this.personInfectsCellProbability = personInfectsCellProbability;
        this.area = area;
    }

    /**
     * A constructor for creating Cells with only their coords
     * 
     * @param x
     * @param y
     */
    public Cell(int x, int y) {
        this(x, y, 0, 0, 0, null);
    }

    /**
     * Einai mia public sinartisi tis klasis Cell.
     *
     * Einai typou int afou epistrefei integer. Einai getter method.
     *
     * @return Epistrefei to x pou exei to cell
     *
     */
    public int getX() {
        return x;
    }

    /**
     * Einai mia public sinartisi tis klasis Cell.
     *
     * Einai typou int afou epistrefei integer. Einai getter method.
     *
     * @return Epistrefei to y pou exei to cell
     *
     */
    public int getY() {
        return y;
    }

    /**
     * Einai mia public sinartisi tis klasis Cell.
     *
     * Einai typou Person afou epistrefei antikeimeno typou Person. Einai getter
     * method.
     *
     * @return Epistrefei to person pou vriskete sto keli
     *
     */
    public Person getOccupier() {
        return occupier;
    }

    /**
     * Einai mia public sinartisi tis klasis Cell.
     *
     * Einai typou boolean afou epistrefei boolean. Elegxei an to keli einai
     * kratimeno.
     *
     * @return Epistrefei true an to keli einai kratimeno kai false an den einai
     *
     */
    public boolean isOccupied() {
        return isOccupied;
    }

    /**
     * Einai mia public sinartisi tis klasis Cell.
     *
     * Einai typou boolean afou epistrefei boolean. Epitrefei boolean timi
     * analoga me an to kelli einai infected
     *
     * @return Epistrefei tin boolean metavliti isInfected
     *
     */
    public boolean isInfected() {
        return isInfected;
    }

    /**
     * Einai mia public sinartisi tis klasis Cell.
     *
     * Einai typou void afou den epistrefei kati. Allazei ton occupier tou
     * keliou kai antistoixa to infectedOccupierTime ginete 0
     *
     *
     *
     * @param occupier
     */
    public void occupy(Person occupier) {
        isOccupied = true;
        this.occupier = occupier;
        infectedOccupierTime = 0;

    }

    /**
     * Einai mia public sinartisi tis klasis Cell.
     *
     * Einai typou void afou den epistrefei kati. Otan ena person figi apo to
     * cell to cell den exei occupier kai den einai kratimeno
     *
     *
     *
     */
    public void free() {
        isOccupied = false;
        occupier = null;
    }

    /**
     * Einai mia public sinartisi tis klasis Cell.
     *
     * Einai typou void afou den epistrefei kati. Elegxei an to keli einai
     * kratimeno,infected,kai o occupier einai vulnerable kai analoga me to
     * probability ginete infected o occupier.
     *
     * @param probability
     *
     */
    public void infectOccupier(int probability) {
        if (isOccupied() && isInfected() && getOccupier().isVulnerable()) {
            //downcasting to Vulnerable in order to use the infect method
            //and infect the person in contact
            ((Vulnerable) getOccupier()).infect(probability);
        }
    }

    /**
     * Einai mia public sinartisi tis klasis Cell.
     *
     * Einai typou void afou den epistrefei kati. An to keli einai infected
     * afksanei to xrono pou ine infected. An to infected time exei ftasi sto
     * healing time tou keliou tote to keli den einai pleon infected. An o
     * ocuupier einai infected afxanei ton xrono pou vriskete o occupier kai
     * elegxei an exei kseperasi ton xrono pou kapios inftected person mpori na
     * kani infect ena keli.
     *
     *
     */
    public void updateInfection() {
        if (isInfected()) {
            beingInfectedTime+=30;

            //infection removes
            if (beingInfectedTime >= cellHealingTime) {
                isInfected = false;
                beingInfectedTime = 0;
            }

        } else if (isOccupied() && occupier.isVulnerable() && ((Vulnerable) occupier).isInfected()) {
            infectedOccupierTime+=30;
            

            //infection starts
            if (infectedOccupierTime >= personInfectsCellTime) {
                isInfected = InfectionSimulator.getRandomResult(personInfectsCellProbability);
                
            }
        }

    }

    /**
     * Einai mia public sinartisi tis klasis Cell.
     *
     * Einai typou String afou epistrefei String. Dimiourgi ena string sto opio
     * iparxei to x kai to y tou cell.
     *
     * @return Epistrefei to String pou dimiourgise
     *
     */
    public String toString() {
        return "( " + x + " , " + y + " )";
    }

    /**
     * Einai mia public sinartisi tis klasis Cell.
     *
     * Einai typou void afou den epistrefei kati. Zografizi to keli sto grid
     * analoga me ta xaraktiristika tou.Episis elegxei an an einai border
     *
     *
     *
     */
    public void toGraph() {
        //clears cell on graph
        GraphicalOutput.clearCell(x, y);
        
        //draw BorderCell 
        if(isBorder()){
            GraphicalOutput.drawBorderCell(x, y, getConnectedArea().getColor());
        }
        //draw Quarantine
        if(isQuarantined()){
            GraphicalOutput.drawQuarantineCell(x, y, Color.red);
        }
        //draws virus
        if (isInfected()) {
            GraphicalOutput.drawVirus(x, y);
        }
        //draws person
        if (isOccupied()) {
            GraphicalOutput.drawPerson(x, y, occupier);

        }

    }

    //Equals method that respects itself
    /**
     * Einai mia public sinartisi tis klasis Cell.
     *
     * Einai typou boolean afou epistrefei boolean. Elegxei an to object den
     * einai null,exei tin idia klasi me to cell kai an exun to idio x kai y
     *
     * @return Epistrefei boolean timi kata poso einai isa ta antikeimena
     *
     */
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj.getClass() != this.getClass()) {
            return false;
        }
        Cell objCell = (Cell) obj;
        return objCell.x == x && objCell.y == y;

    }
    /**
     * Einai mia public sinartisi tis klasis Cell.
     *
     * Einai typou boolean afou epistrefei boolean. Einai getter method.
     *
     * @return Epistrefei an einai sinoriako to cell me alli perioxi
     *
     */
    public boolean isBorder() {
        return isBorder;				
    }
    /**
     * Einai mia public sinartisi tis klasis Cell.
     *
     * Einai typou Grid afou epistrefei antikeimeno typou Grid. Einai getter method.
     *
     * @return Epistrefei to Grid pou to cell anikei
     *
     */
    public Grid getArea() {
        return area;
    }
    /**
     * Einai mia public sinartisi tis klasis Cell.
     *
     * Einai typou Grid afou epistrefei antikeimeno typou Grid. Einai getter method.
     *
     * @return Epistrefei to Grid pou to cell einai sinoriako.
     *
     */
    public Grid getConnectedArea() {
        return connectedArea;
    }
    /**
     * Einai mia public sinartisi tis klasis Cell.
     *
     * Einai typou void afou den epistrefei kati.
     *Thetei to connectedArea (sinoriaki perioxi) me to grid 
     *pou pernei san parametro kai orizi to cell san sinoriako
     *(isBorder=true)
     *
     * @param area
     *
     */
    public void setConnectedArea(Grid area) {
        if(area != null){
            connectedArea = area;
            isBorder = true;
        }

    }
    /**
     * Einai mia public sinartisi tis klasis Cell.
     *
     * Einai typou void afou den epistrefei kati.
     *Thetei to connectedArea (sinoriaki perioxi) me null
     * kai orizi to cell oti den einai sinoriako
     *(isBorder=false)
     *
     *
     */
    public void removeConnectedArea() {
        connectedArea = null;
        isBorder = false;
    }
    
    /**
     * Method for setting the cells as a quarantined cell
     */
    public void setQuarantined(){
        isQuarantined = true;
    }
    
    /**
     * Method for determining if the cell is a part of the quarantine area
     * 
     * @return true - cell belongs to the quarantine
     */
    public boolean isQuarantined(){
       return isQuarantined;
    }

}
