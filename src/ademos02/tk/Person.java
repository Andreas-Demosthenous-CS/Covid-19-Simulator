package ademos02.tk;


/**
 * Afti i abstract klasi onomazetai Person kai dimiourgei antikeimena typou
 * Person.
 *
 * H klasi exei ena kataskevasti kai synartisis. O kataskevastis pernei 3
 * parametrous.Kai dimiourgounte ta antikeimena Person.
 *
 * @author Andreas Demosthenous
 * @version 3.0
 * @since 13/05/2020
 */
public abstract class Person {

    private String id;
    private int age;
    private char gender;
    private Grid grid;
    private Cell currentCell;
    private boolean isProtected, isTravelling;

    /**
     * Einai o kataskevastis tis klasis Person.
     *
     * Arxikopoiei ta pedia id , grid kai cell analoga me tis parametrous pou
     * pernei. Arxikopia ton anthropo oti den einai protected. kai oti einai se
     * kapio cell
     *
     * @param id
     * @param age
     * @param gender
     * @param grid
     * @param cell
     *
     */
    public Person(String id, int age, char gender, Grid grid, Cell cell) {
        this.id = id;
        this.grid = grid;
        this.currentCell = cell;
        this.isProtected = false;
        this.age = age;
        this.gender = gender;
        currentCell.occupy(this);
        isTravelling = false;

    }

    /**
     * Einai mia public sinartisi tis klasis Person.
     *
     * Einai typou String afou epistrefei ena string. Epistrefei to id tou
     * person(getter method)
     *
     * @return Epistrefei to id
     *
     */
    public String getId() {
        return id;
    }

    /**
     * getter for the age
     * @return
     */
    public int getAge() {
        return age;
    }

    /**
     * getter for the gender
     * @return
     */
    public char getGender() {
        return gender;
    }
    
    /**
     * getter for the grid
     * @return
     */
    public Grid getGrid(){
        return grid;
    }

    /**
     * Einai mia public sinartisi tis klasis Person.
     *
     * Einai typou Cell afou epistrefei ena antikeimeno typou Cell. Epistrefei
     * to cell pou vriskete o anthropos ekini tin stigmi.
     *
     * @return Epistrefei antikeimeno typou Cell
     *
     */
    public Cell getCurrentCell() {
        return currentCell;
    }

    /**
     * Einai mia public sinartisi tis klasis Person.
     *
     * Einai typou boolean afou epistrefei boolean metavliti. Epistrefei tin
     * metavliti isProtected i opia mas lei an o anthropos einai protected i
     * oxi.
     *
     * @return Epistrefei tin metavliti isProtected
     *
     */
    public boolean isProtected() {
        return isProtected;
    }

    /**
     * Einai mia public sinartisi tis klasis Person.
     *
     * Einai typou void afou den epistrefei kati. Thetei tin boolean metavliti
     * isProtected me true.
     *
     *
     */
    public void addProtection() {
        isProtected = true;
    }

    /**
     * Einai mia public sinartisi tis klasis Person.
     *
     * Einai typou void afou den epistrefei kati. allazi to kelli sto opio
     * vriskete
     *
     *
     * @param cell
     *
     */
    public void transfer(Cell cell) {
        //Informing cell that i am leaving
        currentCell.free();
        //changing current cell
        currentCell = cell;
        //adding an occupier to the cell
        currentCell.occupy(this);
    }

    /**
     * Einai mia public sinartisi tis klasis Person.
     *
     * Einai typou void afou den epistrefei kati. Allazi to Grid sto opio
     * vriskete o anthropos
     *
     *
     *
     * @param grid
     */
    public void transfer(Grid grid) {

        //Informing cell that i am leaving
        currentCell.free();
        //changing current cell to a random one on the specified grid
        if(grid.isNonQuarantineFull()){
            //quarantine excluded
            currentCell = InfectionSimulator.getRandomCell(grid, true);
        }
        else{
            //quarantine included
            currentCell = InfectionSimulator.getRandomCell(grid, true);
        }
        
        //adding an occupier to the cell
        currentCell.occupy(this);
        //setting the new grid
        this.grid = grid;
    }

    /**
     * Einai mia public sinartisi tis klasis Person.
     *
     * Einai typou Cell[] epistrefei ena pinaka apo antikeimena cell. Dimiourgei
     * ena pinaka cell me 8 thesis. Ta 8 cell einai ta gitonika cells tou cell
     * pou vriskete o person afti ti stigmi
     *
     * @param cell
     * @return Epistrefei ena pinaka apo cell
     *
     */
    public Cell[] calculateConnectedCells(Cell cell) {
        Cell cells[] = new Cell[8];
        cells[0] = grid.getCell(cell.getX() - 1, cell.getY() + 1);
        cells[1] = grid.getCell(cell.getX(), cell.getY() + 1);
        cells[2] = grid.getCell(cell.getX() + 1, cell.getY() + 1);
        cells[3] = grid.getCell(cell.getX() + 1, cell.getY());
        cells[4] = grid.getCell(cell.getX() + 1, cell.getY() - 1);
        cells[5] = grid.getCell(cell.getX(), cell.getY() - 1);
        cells[6] = grid.getCell(cell.getX() - 1, cell.getY() - 1);
        cells[7] = grid.getCell(cell.getX() - 1, cell.getY());

        return cells;
    }
    
    /**
     * determines whether the person is traveling on a bus
     * @return
     */
    public boolean isTravelling(){
        return isTravelling;       
    }
    
    /**
     * sets the person as a traveler or not
     * @param t
     */
    public void setTravelling(boolean t){
        isTravelling = t;
    }
    

    /**
     * Einai mia public sinartisi tis klasis Person.
     *
     * Einai typou String afou epistrefei ena string. Epistrefei ena string pou
     * periexei to id tou person kai to cell pou vriskete.
     *
     * @return Epistrefei to string pou dimiourgei
     *
     */
    public String toString() {
        return id + " is currently at " + currentCell.getArea().getName() + " at " + currentCell;
    }

    /**
     * Einai mia public abstract sinartisi tis klasis Person.
     *
     * Einai abstract dioti den exei body
     *
     */
    public abstract void move();

    /**
     * Einai mia public abstract sinartisi tis klasis Person.
     *
     * Einai abstract dioti den exei body
     *
     * @return 
     */
    public abstract boolean isVulnerable();
}
