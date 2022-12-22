package ademos02.tk;


/**
 * Afti i klasi onomazetai Imune kai dimiourgei antikeimena typou Imune.
 *
 * H klasi klironomei apo tin Person, exei ena kataskevasti kai synartisis. O
 * kataskevastis pernei 3 parametrous.Kai dimiourgounte ta antikeimena Imune.
 *
 * @author Andreas Demosthenous
 * @version 3.0
 * @since 13/05/2020
 */
public class Imune extends Person {

    /**
     * Einai o kataskevastis tis klasis Imune.
     *
     * Kalei ton constructor tis Person kai tou perna tis parametrous pou dexete
     *
     * @param Pernei 3 parametrous(gia to cell pou vriskete ,to grid kai to id)
     *
     */
    public Imune(String id,int age, char gender, Grid grid, Cell cell) {
        super(id,age, gender,  grid, cell);
    }

    /**
     * Einai mia public sinartisi tis klasis Person.
     *
     * I sinartisi epistrefei panda false
     *
     *
     * @return Epistrefei false
     *
     * @param Den pernei parametro
     *
     */
    public boolean isVulnerable() {
        return false;
    }

    /**
     * Einai mia public sinartisi tis klasis Imune.
     *
     * Einai void afu den epistrefei kati. Kineitai o imune person.Dialegei 1
     * cell apo ta 8 geitonika tou i epilegei na min kinithei. Episis an
     * vriskete se sinoriako cell iparxei pithanotita na pai se allo grid pou
     * einai sinoriako me to cell
     *
     * @return Den epistrefei kari
     *
     * @param Den pernei parametro
     *
     */
    public void move() {

        //getting connected cells
        Cell[] connectedCells = calculateConnectedCells(getCurrentCell());
        boolean hasMoved = false;

        //attempt to move
        while (!hasMoved) {

            if (InfectionSimulator.randomizer.nextBoolean()) {
                //Decides not to move by 50%
                hasMoved = true;
                break;
            }
            //Decides to move. In case of invalid(null cell) or occupied dicision, 
            //decides again
            int randomMove = InfectionSimulator.randomizer.nextInt(8);

            //In case of a null decision and cell is border cell
            //--> person transfer to the cell's area
            if (connectedCells[randomMove] == null && getCurrentCell().isBorder() && !getCurrentCell().isQuarantined()
                    && !getCurrentCell().getConnectedArea().isFull()) {
                
                System.out.println(getId() + " TRANSAFERED FROM " + getCurrentCell() + getCurrentCell().getArea().getName() + " to " + getCurrentCell().getConnectedArea().getName());
                
                transfer(getCurrentCell().getConnectedArea());
                hasMoved = true;
                
            } else if (connectedCells[randomMove] != null && !connectedCells[randomMove].isOccupied() && !connectedCells[randomMove].isQuarantined()) {
                //transfers person * cannot move into a quarantine area
                transfer(connectedCells[randomMove]);
                hasMoved = true;
            }
        }
    }

    /**
     * Einai mia public sinartisi tis klasis Person.
     *
     * Einai typou String afu dimiourgei ena string ksi to epistrefei. To String
     * pou dimiourgei einai to idio me tis toSttring()tis klasis person apla
     * prosthetei oti einai imune kai an einai protected.
     *
     * @return Epistrefei ena String
     *
     * @param Den pernei parametro
     *
     */
    public String toString() {
        String str = super.toString() + ", is imune";

        if (isProtected()) {
            str += ", is protected ";
        }
        return str;
    }

}
