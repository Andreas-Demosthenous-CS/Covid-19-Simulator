package ademos02.tk;

import java.util.ArrayList;

/**
 * This class is a representation of a list of buses
 *
 * @author Andreas Demosthenous
 * @version 3.0
 * @since 13/05/2020
 */
public class BusStation extends ArrayList<MyBus> {

    /**
     * The default constructor
     */
    public BusStation() {

    }

    /**
     * The copy constructor of the station
     * 
     * @param station
     */
    public BusStation(ArrayList<MyBus> station) {
        for (int i = 0; i < station.size(); i++) {
            add(station.get(i));
        }
    }

}
