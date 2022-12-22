package ademos02.tk;

import java.util.ArrayList;
import java.util.Random;

/**
 * This class is a representation of a bus used to transfer
 * people from an area to another.
 * 
 * @author Andreas Demosthenous
 * @version 3.0
 * @since 13/05/2020
 */
public class MyBus {
    //a randomizer
    private Random randomizer;
    
    //bus name
    private String name;
    
    //time until the next departure
    private int minutesUntilDeparture;
    
    //time until the next arrival
    private int minutesUntilArrival;
    
    //duration of every trip
    private int tripDuration;
    
    //make a trip every tripScheduleTime minutes
    private int tripScheduleTime;
    
    //source and destination grids
    private Grid source, destination;
    
    //capacities
    private int maxCapacity, currentCapacity;
    
    //passengers list
    private ArrayList<Person> people;
    
    private boolean isTravelling;

    /**
     * Constructor for the bus parameters
     *
     * @param name
     * @param maxCapacity
     * @param tripScheduleTime
     * @param tripDuration
     * @param source
     * @param destination
     */
    public MyBus(String name, int maxCapacity, int tripScheduleTime, int tripDuration, Grid source, Grid destination) {
        this.name = name;
        randomizer = new Random();
        people = new ArrayList<Person>();
        this.maxCapacity = maxCapacity;
        this.tripScheduleTime = tripScheduleTime;
        this.tripDuration = tripDuration;
        this.source = source;
        this.destination = destination;
        this.currentCapacity = 0;
        minutesUntilDeparture = tripScheduleTime;
        isTravelling = false;
    }

    //this method collects a random amount of random people from the grid and puts them
    //in the bus
    private void depart() {        
        //departure       
        isTravelling = true;
        minutesUntilArrival = tripDuration;
        int tripCapacity = randomizer.nextInt(maxCapacity - people.size());
        boolean isReady = false;
        while (!isReady) {
            if (source.isEmpty()) {
                isReady = true;
                tripCapacity = people.size();
                break;
            }
            //getting random people for the trip
            Person randPers = InfectionSimulator.getRandomPerson(source);
            people.add(randPers);
            randPers.getCurrentCell().free();
            randPers.setTravelling(true);
            
            if (people.size() >= tripCapacity) {
                isReady = true;
            }
        }
    }

    //This method frees the passengers to random cells in the destination area
    private void arrive() {       
        isTravelling = false;
        minutesUntilDeparture = tripScheduleTime;      
        for(int i =people.size()-1;i>=0;i--){
            if(!destination.isNonQuarantineFull()){
                people.get(i).transfer(destination);
                people.get(i).setTravelling(false);
                people.remove(i);
            } 
        }

        //swaping the source - destination;
        Grid temp = source;
        source = destination;
        destination = temp;
    }

    /**
     * This method updates the movement of the bus 
     */
    public void updateBus() {
        if (isTravelling) {
            minutesUntilArrival -= 30;
            if (minutesUntilArrival <= 0) {
                arrive();
            }
        } else {
            minutesUntilDeparture -= 30;
            if (minutesUntilDeparture <= 0) {
                depart();
            }
        }

    }

    /**
     * getter for the name
     * 
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * getter for the source 
     * @return
     */
    public String getSource() {
        return source.getName();
    }

    /**
     * getter for the destination
     * @return
     */
    public String getDestination() {
        return destination.getName();
    }

    /**
     * determines whether the bus is traveling
     * @return
     */
    public boolean isTravelling() {
        return isTravelling;
    }

    /**
     * getter for the current capacity
     * @return
     */
    public int getCurrentCapacity() {
        return people.size();
    }

    /**
     * getter for the max capacity
     * @return
     */
    public int getMaxCapacity() {
        return maxCapacity;
    }

}
