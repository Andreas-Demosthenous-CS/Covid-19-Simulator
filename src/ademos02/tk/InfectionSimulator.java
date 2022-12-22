package ademos02.tk;

/**
 * That class is a representation of a simulator
 *
 * It is responsible for handling the whole process of drawing the graph and
 * extracting the results of the simulation after it is started
 *
 * @author Andreas Demosthenous
 * @version 3.0
 * @since 13/05/2020
 */
import edu.princeton.cs.introcs.StdDraw;
import java.io.IOException;
import java.util.Random;

/**
 * This class is the object that handles the simulation
 *
 * @author Andreas Demosthenous, Andreas Papadopoulos
 * @version 1.0
 * @since 03/05/2020
 */
public class InfectionSimulator {

    /**
     * An object for handling random operations
     */
    public static Random randomizer;

    //The array that will store all the Person objects needed
    private Person persons[];

    //the 2d map
    private Map grids;

    //the buses
    private BusStation buses;

    //the simulation time in minutes
    private int simTime;

    //the amount of minutes needed for an infected person to infect
    //a cell while being on it
    private int personInfectsCellTime;

    //the probability that an infected person infects a cell
    private int personInfectsCellProbability;

    //the probability an infected cell infects a person
    private int cellInfectsPersonProbability;

    //the amount of time the screen pauses in ms
    private int updatePauseTime;

    //The amount of time an infected person remains asymptomatic and not in quarantine
    private int infectedPersonAsymptomaticTime;

    //the probability a lab test gives wrong result
    private int labFaultyProbability;

    //the sim frame
    private SimulationFrame simFrame;

    //the bus frame
    private BusesStatusFrame busFrame;

    /**
     *
     * The constructor receives all the necessary parameters and initializes the
     * persons for the simulation
     *
     * @param people the amount of people
     * @param infectedPeople the amount of infected people
     * @param imunePeople the amount of imune people
     * @param protectedPeople the amount of protected people
     * @param grids
     * @param simTime the simulation time in minutes
     * @param buses
     * @param personInfectsCellTime the amount of minutes needed for an infected
     * person to infect a cell while being on it
     * @param infectedPersonAsymptomaticTime
     *
     * @param personInfectsPersonProbability the probability an infected person
     * infects a person
     * @param cellInfectsPersonProbability the probability a cell infects a
     * person
     * @param personInfectsCellProbability the probability an infected person
     * infects a cell
     * @param updatePauseTime the amount of time the screen pauses in ms
     * @param labFaultyProbability
     */
    public InfectionSimulator(int people, int infectedPeople, int imunePeople,
            int protectedPeople, Map grids, BusStation buses, int simTime, int personInfectsCellTime,
            int infectedPersonAsymptomaticTime, int personInfectsPersonProbability,
            int cellInfectsPersonProbability, int personInfectsCellProbability, int labFaultyProbability,
            int updatePauseTime) {

        this.infectedPersonAsymptomaticTime = infectedPersonAsymptomaticTime;
        this.grids = grids;
        this.simTime = simTime;
        this.personInfectsCellTime = personInfectsCellTime;
        this.cellInfectsPersonProbability = cellInfectsPersonProbability;
        this.personInfectsCellProbability = personInfectsCellProbability;
        this.labFaultyProbability = labFaultyProbability;
        this.buses = buses;
        this.updatePauseTime = updatePauseTime;

        //Inits the randomizer
        randomizer = new Random();

        //initializing the persons
        persons = PersonsFactory(people, infectedPeople, imunePeople, protectedPeople,
                personInfectsPersonProbability);

        //updating the console
        System.out.println("Initialized people: ");
        printPeople();

    }

    /**
     * The method that starts the simulation process and gives the chance to the
     * user to skip the graphical representation as it slows down the
     * performance
     *
     * @param useGraphic true - use graphic, false - not use
     */
    public void startSimulation(boolean useGraphic) {

        //updates the running flag in order to avoid other errors
        UserInterface.isRunning = true;

        //Setting the static infection counter and recovery counter to 0
        Vulnerable.infectionCases = 0;
        Vulnerable.curedCases = 0;

        //for graphic
        if (useGraphic) {

            //Initializing the frames   
            this.simFrame = new SimulationFrame(grids);
            this.busFrame = new BusesStatusFrame(buses);

            //Init the grids
            GraphicalOutput.InitializeMap(grids);

            //do for the specified minutes - simTime
            for (int i = 0; i < simTime; i += 30) {
                UserInterface.loader.setValue((int) getPercentage(simTime, i + 1));
                //update on console
                System.out.println(" Minute " + (i + 1) + " :");
                //run next minute
                nextCycle(i, useGraphic);

            }

            //last graphic update
            updateGraphic(simTime);

            //setting the progress bar to max
            UserInterface.loader.setValue(100);

        } else {
            //not graphic
            for (int i = 0; i < simTime; i += 30) {
                UserInterface.loader.setValue((int) getPercentage(simTime, i + 1));
                nextCycle(i, useGraphic);
            }

            //setting the progress bar to max
            UserInterface.loader.setValue(100);
        }
        //resetting the progress bar
        UserInterface.loader.setValue(0);
        //updates the running flag in order to avoid other errors
        UserInterface.isRunning = false;

    }

    /**
     * This method prints all the people on the console
     */
    private void printPeople() {
        for (int i = 0; i < persons.length; i++) {
            System.out.println(persons[i]);
        }
    }

    /**
     * This method handles every cycle(minute) of the simulation
     *
     * @param useGraphic true - use graphic, false - not use
     */
    private void nextCycle(int simTime, boolean useGraphic) {

        //In case of using StdDraw
        if (useGraphic) {
            updateGraphic(simTime);
        }

        //print people
        printPeople();

        //update cell's condition infect owners etc.
        updateCells();

        //update people - everyone moves
        updatePeople();

        //update buses
        updateBuses();

    }

    /**
     * This method initializes the people for the simulation
     *
     * @param people people amount
     * @param infectedPeople inf people
     * @param imunePeopleim people
     * @param protectedPeople prot people
     * @param personInfectsPersonProbability person Infects a Person Probability
     * @return the array with the people
     */
    private Person[] PersonsFactory(int people, int infectedPeople, int imunePeople,
            int protectedPeople, int personInfectsPersonProbability) {

        //inits the people array
        Person persons[] = new Person[people];
        int personCounter = 0;
        int imuneP = imunePeople;
        int infectedP = infectedPeople;
        int healthyP = people - infectedPeople - imunePeople;
        int protectedP = protectedPeople;

        //creating the people for each area
        for (int i = 0; i < grids.size(); i++) {
            int count = 0;
            //creating the initial population for each area
            while (count < grids.get(i).getInitialPopulation()) {
                //Throwing a dice to enerate random type of people to the grid
                int randomNumber = randomizer.nextInt(3);
                //0 -> imune
                //1 -> infected
                //2 -> healthy
                switch (randomNumber) {
                    case 0:
                        //create imune if possible
                        if (imuneP > 0) {
                            Cell randomCell = getRandomCell(grids.get(i), true);
                            if (randomCell.isQuarantined() && !grids.get(i).isNonQuarantineFull()) {
                                break;
                            }
                            //Creates a person with random generated gender/age 
                            persons[personCounter] = new Imune("P" + personCounter, getRandomAge(), getRandomGender(), grids.get(i), randomCell);
                            randomCell.occupy(persons[personCounter]);
                            imuneP--;
                            count++;
                            personCounter++;
                        }
                        break;

                    case 1:
                        //create infected if possible
                        if (infectedP > 0) {
                            //picking a random cell excluding the quarantine area(true)
                            Cell randomCell = getRandomCell(grids.get(i), true);
                            //Creating infected person on a unique cell-position
                            persons[personCounter] = new Vulnerable("P" + personCounter, getRandomAge(), getRandomGender(), grids.get(i), randomCell,
                                    true, infectedPersonAsymptomaticTime, personInfectsPersonProbability);
                            randomCell.occupy(persons[personCounter]);
                            infectedP--;
                            count++;
                            personCounter++;
                        }
                        break;

                    case 2:
                        //create healthy if possible
                        if (healthyP > 0) {
                            //picking a random cell excluding the quarantine area(true)
                            Cell randomCell = getRandomCell(grids.get(i), true);           
                            //Creating Healthy person on a unique cell-position
                            persons[personCounter] = new Vulnerable("P" + personCounter, getRandomAge(), getRandomGender(), grids.get(i), randomCell, false,
                                    infectedPersonAsymptomaticTime, personInfectsPersonProbability);
                            //Occuping the cell
                            randomCell.occupy(persons[personCounter]);
                            
                            //updating the counters
                            healthyP--;
                            count++;
                            personCounter++;
                        }
                        break;
                }
            }
        }

        //Randomly adding protection to (protectedPeople) amount of people
        while (protectedP > 0) {
            int randomPerson = randomizer.nextInt(people);
            if (!persons[randomPerson].isProtected()) {
                persons[randomPerson].addProtection();
                protectedP--;
            }
        }

        return persons;
    }

    // epistrefei ena tixeo grid apo to Map
    private Grid getRandomGrid(Map map) {
        boolean picked = false;
        int randomNum = 0;
        Grid newGrid = null;
        while (!picked) {
            randomNum = randomizer.nextInt(map.size());
            newGrid = map.get(randomNum);
            if (!newGrid.isFull()) {
                picked = true;
            }
        }
        return newGrid;
    }

    private int getRandomAge() {
        //100<=age>=0
        return randomizer.nextInt(100);
    }

    private char getRandomGender() {
        boolean g = randomizer.nextBoolean();
        if (g) {
            return 'M';
        }
        return 'F';
    }

    // This methods returns a random cell that is not inside the usedCells array
    /**
     * Returns a random unoccupied cell from the given grid
     *
     * @param grid
     * @return
     */
    public static Cell getRandomCell(Grid grid, boolean quarantineExclussive) {
        boolean isUnique = false;
        Cell newCell = null;
        while (!isUnique) {
            isUnique = true;
            //0 < x < grid length
            int x = randomizer.nextInt(grid.getLength());
            //0 < y < grid width
            int y = randomizer.nextInt(grid.getWidth());
            newCell = grid.getCell(x, y);

            //checking the randomly generated cell
            if (grid.getCell(x, y).isOccupied()) {
                isUnique = false;
                
            }
            else if(quarantineExclussive && grid.getCell(x, y).isQuarantined()){
                isUnique = false;
            }

        }
        return newCell;
    }

    /**
     * Returns a random unoccupied cell from the quarantine area of the given
     * grid
     *
     * @param grid
     * @return
     */
    public static Cell getRandomQuarantinedCell(Grid grid) {
        Cell cell = getRandomCell(grid,false);
        if (grid.isQuarantineFull()) {
            return null;
        }
        while (!cell.isQuarantined()) {
            cell = getRandomCell(grid,false);
        }
        return cell;
    }

    /**
     * Returns a random person from the given grid
     *
     * @param grid
     * @return
     */
    public static Person getRandomPerson(Grid grid) {

        if (grid.isEmpty()) {
            return null;
        }

        boolean isFound = false;
        Cell newCell = null;
        while (!isFound) {
            //0 < x < grid length
            int x = randomizer.nextInt(grid.getLength());
            //0 < y < grid width
            int y = randomizer.nextInt(grid.getWidth());
            newCell = grid.getCell(x, y);

            //checking the randomly generated cell
            if (newCell.isOccupied()) {
                isFound = true;
            }

        }
        return newCell.getOccupier();
    }

    //updates every person(move) and also updates the infection cases variable
    private void updatePeople() {

        for (int i = 0; i < persons.length; i++) {

            persons[i].move();

        }

    }

    //This method updates the infection on every cell 
    private void updateCells() {
        for (int j = 0; j < grids.size(); j++) {

            //Running lab tests every 30 minutes on every grid
            executeLabTest(grids.get(j));

            //updating hospitals on every grid every 30 minutes
            grids.get(j).getHospital().updatePatients();

            for (int i = 0; i < grids.get(j).getLength(); i++) {
                for (int k = 0; k < grids.get(j).getWidth(); k++) {
                    //infecting the owner
                    grids.get(j).getCell(i, k).infectOccupier(cellInfectsPersonProbability);
                    //updating the infection time on the cell
                    grids.get(j).getCell(i, k).updateInfection();

                }
            }

        }
    }

    //this method updates all the buses movement
    private void updateBuses() {
        for (int i = 0; i < buses.size(); i++) {
            buses.get(i).updateBus();
        }
    }

    //this method runs labTests to a random amount of people
    private void executeLabTest(Grid grid) {
        if (grid.getPeopleAmount() < 1) {
            return;
        }
        int randomAmount = randomizer.nextInt(grid.getPeopleAmount());
        Person people[] = new Person[randomAmount];
        for (int i = 0; i < randomAmount; i++) {
            people[i] = getRandomPerson(grid);
        }
        VirusLab.runLabTest(people, labFaultyProbability);
    }

    //updates every cell's graphical representation on the graphical grid
    private void updateGraphic(int simTime) {
        for (int j = 0; j < grids.size(); j++) {

            //Drawing the empty grid before drawing the cells
            grids.get(j).toGraph();

            //drawing the cells on the grid
            for (int i = 0; i < grids.get(j).getLength(); i++) {
                for (int k = 0; k < grids.get(j).getWidth(); k++) {
                    grids.get(j).getCell(i, k).toGraph();
                }
            }

            //saving a temporary picture of the grid for the frame to load along with all
            //the other are pics(parallel area update)
            StdDraw.save("Area" + j + ".png");

        }
        try {

            //Updating the sim frame after all areas are updated
            simFrame.updateFrame(simTime, Vulnerable.infectionCases);

            //Updating the bus frame after all areas are updated
            busFrame.updateStatus();

            //pauses the frame for the specified amount of time
            StdDraw.pause(updatePauseTime);

        } catch (IOException ex) {
            System.out.println("Fatal Error reading the saved image");
            System.exit(0);
        }

    }


    /**
     * This method calculates a random boolean with a given probability
     *
     * @param probability the prob
     * @return the result
     */
    public static boolean getRandomResult(double probability) {
        //random number from 1 to 100 inclussive
        int randomNumber = randomizer.nextInt(100) + 1;
        if (randomNumber <= probability) {
            return true;
        }
        return false;
    }

    /**
     * This method return the % of a fraction
     *
     * @param denominator
     * @param numerator
     * @return the %
     */
    public static double getPercentage(double denominator, double numerator) {
        return ((int) (numerator * 100 / denominator) * 10000) / 10000;
    }

}
