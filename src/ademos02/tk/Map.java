package ademos02.tk;

import java.util.ArrayList;
/**
 * Afti i klasi onomazetai Map kai dimiourgei antikeimena typou Map.
 *
 * H klasi klironomei apo tin ArrayList<Grid>, exei 2 kataskevastes
 *  kai kapies synartisis.
 *
 * @author Andreas Demosthenous
 * @version 3.0
 * @since 13/05/2020
 */
public class Map extends ArrayList<Grid> {
	/**
     * Einai  kataskevastis tis klasis Map.
     *
     *Kataskevazi ena keno arraylist(map) 
     *
     * @param Den pernei parametro
     *
     */
    public Map() {

    }
    /**
     * Einai  kataskevastis tis klasis Map.
     *
     *Copy constructor ,dimiourgei ena arraylist me grids 
     *(Kanei copy to ArrayList pou pernei san parametro)
     *
     * @param Pernei parametro ena ArrayList apo grids.
     *
     */
    public Map(ArrayList<Grid> map) {
        for (int i = 0; i < map.size(); i++) {
            add(map.get(i));
        }
    }
    /**
     * Einai mia public sinartisi tis klasis Map.
     *
     * Einai typou Grid afu  epistrefei Grid. 
     * Dexete san parametro to onoma kai epistrefei to grid me afto to onoma
     *
     * @return Epistrefei antikeimeno Grid 
     *
     * @param Pernei parametro ena String pou einai to onoma enos grid
     *
     */
    public Grid getByName(String name) {
        for (int i = 0; i < size(); i++) {
            if(get(i)== null || get(i).getName()==null){continue;}
            if(get(i).getName().equals(name)){
                return get(i);
            }
        }
        return null;
    }
   
    

}
