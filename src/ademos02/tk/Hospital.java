package ademos02.tk;

import java.util.ArrayList;

/**
 * This class represents a hospital for a grid
 *
 * @author Andreas Demosthenous
 * @version 3.0
 * @since 13/05/2020
 */
public class Hospital {

    //the cure duration in minutes
    private int cureDuration;
    //the capacity of the hospital
    private int capacity;
    //the list of patients
    private ArrayList<Vulnerable> patients;
    //the parallel list of the recovery remaining for each patient
    private ArrayList<Integer> remainingTime;

    /**
     * The constructor receiving the capacity and healing time of the patients
     *
     * @param capacity
     * @param cureDuration
     */
    public Hospital(int capacity, int cureDuration) {

        this.cureDuration = cureDuration;
        this.capacity = capacity;

        //init patients
        this.patients = new ArrayList<Vulnerable>();
        //init times
        this.remainingTime = new ArrayList<Integer>();

    }

    /**
     * The method for inserting a new patient in the hospital
     *
     * @param p
     * @return
     */
    public boolean addPatient(Vulnerable p) {
        //ensuring there is enough space
        if (getCurrentCapacity() < capacity) {
            patients.add(p);
            remainingTime.add(cureDuration);
            return true;
        }
        return false;
    }

    /**
     * The method for releasing a recovered patient
     *
     * @param p -> patient
     * @param index -> the patient;s position in the list
     */
    public void releasePatient(Vulnerable p, int index) {
        if (isPatient(p)) {
            if (!p.getGrid().isFull()) {
                p.leaveHospital();
                patients.remove(index);
                remainingTime.remove(index);
            }
        }
    }

    /**
     * returns if the person p is in the hospital
     * 
     * @param p
     * @return true - patient exists
     */
    public boolean isPatient(Vulnerable p) {
        for (int i = 0; i < getCurrentCapacity(); i++) {
            if (p.getId().equals(patients.get(i).getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Updates the recovery process and releases the recovered patients as healthy.
     */
    public void updatePatients() {
        for (int i = 0; i < getCurrentCapacity(); i++) {
            remainingTime.set(i, remainingTime.get(i).intValue() - 30);
            if (remainingTime.get(i).intValue() <= 0) {
                patients.get(i).cure();
                releasePatient(patients.get(i), i);
            }
        }
    }

    /**
     * getter for the max capacity 
     * 
     * @return max capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * getter for the current capacity
     * 
     * @return
     */
    public int getCurrentCapacity() {
        return patients.size();
    }

    /**
     * getter for the duration of the recovery
     * @return
     */
    public int getCureDuration() {
        return cureDuration;
    }

    /**
     * returns whether the hospital is full
     * @return true - full
     */
    public boolean isFull() {
        return getCapacity() == getCurrentCapacity();
    }

}
