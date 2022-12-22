package ademos02.tk;

/**
 * Afti i klasi onomazetai Vulnerable kai dimiourgei antikeimena typou
 * Vulnerable.
 *
 * H klasi klironomei apo tin Person, exei ena kataskevasti kai synartisis. O
 * kataskevastis pernei 5 parametrous.Kai dimiourgounte ta antikeimena
 * Vulnerable.
 *
 *
 * @author Andreas Demosthenous
 * @version 3.0
 * @since 13/05/2020
 */
public class Vulnerable extends Person {

    /**
     * the live counter of infection cases
     */
    public static int infectionCases = 0;

    /**
     * Counter for the recovery cases
     */
    public static int curedCases = 0;

    //the probability a person infects another person
    private int personInfectsPersonProbability;
    //the amount o time in days an infected person remains asymptomatic
    private int infectedPersonAsymptomaticTime;
    //the amount of time in minutes the person remained infected
    private int beingInfectedTime;

    private boolean isInfected, isInHospital, isKnowinglyInfected, isSeverelySick;

    /**
     * Einai o kataskevastis tis klasis Vulnerable.
     *
     * Kalei ton constructor tis Person kai tou perna tis parametrous pou
     * dexete(id,grid kai cell) kai episis arxikopia ta pedia isInfected kai
     * personInfectsPersonProbability analoga me tis parametrus
     *
     * @param id
     * @param age
     * @param gender
     * @param grid
     * @param infectedPersonAsymptomaticTime
     * @param cell
     * @param isInfected
     * @param personInfectsPersonProbability
     *
     */
    public Vulnerable(String id, int age, char gender, Grid grid, Cell cell, boolean isInfected,
            int infectedPersonAsymptomaticTime, int personInfectsPersonProbability) {

        super(id, age, gender, grid, cell);
        this.isInfected = isInfected;
        this.infectedPersonAsymptomaticTime = infectedPersonAsymptomaticTime * 30 * 48;
        this.personInfectsPersonProbability = personInfectsPersonProbability;
        this.isInHospital = false;
        this.isKnowinglyInfected = false;
        this.isSeverelySick = false;
        beingInfectedTime = 0;

    }

    /**
     * Einai mia public sinartisi tis klasis Vulnerable.
     *
     * Einai boolean sinartisi afou epistrefei boolean metavliti
     *
     * @return Epistrefei kata poso in molismeno to atomo
     *
     */
    public boolean isInfected() {
        return isInfected;
    }

    /**
     * determines whether the person knows that he is infected
     * @return
     */
    public boolean isKnowinglyInfected() {
        return isKnowinglyInfected;
    }

    /**
     * informs/or not the victim if he is infected
     * 
     * @param a
     */
    public void setKnowinglyInfected(boolean a) {
        isKnowinglyInfected = a;
    }

    /**
     * determines whether the person if severely sick
     * 
     * @return
     */
    public boolean isSeverelySick() {
        return isSeverelySick;
    }

    /**
     * Einai mia public sinartisi tis klasis Vulnerable.
     *
     * I sinartisi epistrefei panta true
     *
     * @return Epistrefei true
     *
     */
    public boolean isVulnerable() {
        return true;
    }

    /**
     * Einai mia private sinartisi tis klasis Vulnerable.
     *
     * Einai void afou den epistrefei kati.Kanei mia apopeira na molinei osa
     * atoma vriskonte se gitonika kelia
     *
     * @return Den epistrefei kati
     *
     * @param Pernei parametro ena integer pou einai i pithanotita na kollisi
     * kapoion allo anthropo
     *
     */
    private void infectSurroundingPeople(int probability) {

        //gets array with directly connected cells
        Cell[] connectedCells = calculateConnectedCells(getCurrentCell());

        for (int i = 0; i < connectedCells.length; i++) {

            //checking cells occupier and if occupier is vulnerable to the virus
            if (connectedCells[i] != null && connectedCells[i].isOccupied()
                    && connectedCells[i].getOccupier().isVulnerable()) {

                //protected people have 30% less chance to infect other people
                if (isProtected()) {
                    probability -= 30;
                    //probabilities are always positive
                    if (probability < 0) {
                        probability = 0;
                    }
                }
                //Attempt to infect:
                //downcasting to Vulnerable in order to use the infect method
                //and infect the person in contact
                ((Vulnerable) connectedCells[i].getOccupier()).infect(probability);
            }

        }
    }

    /**
     * Einai mia public sinartisi tis klasis Vulnerable.
     *
     * I sinartisi molinei me kapoia pithanotita to atomo Person pano sto opoio
     * ekteleite
     *
     *
     * @param probability
     * @return Epistrefei kata poso exei telika molinthei to atomo
     *
     */
    public boolean infect(int probability) {
        //infects only healthy persons
        if (isInfected()) {
            return false;
        }
        //protection measures decrease infection prob by 20%
        if (isProtected()) {
            probability -= 20;
            //not allowing negative probability
            if (probability < 0) {
                probability = 0;
            }
        }
        //attempting to infect with the specified probability
        isInfected = InfectionSimulator.getRandomResult(probability);

        if (isInfected) {
            infectionCases++;
        }

        return true;
    }

    /**
     * this method attempts to make the person severely
     * sick depending on his gender and age
     * 
     */
    public void makeSeverelySick() {

        // only infected persons can get severly sick
        if (isInfected() && !isSeverelySick) {
            isSeverelySick = VirusLab.makeStronglySick(this);
        }

    }

    /**
     * This method sets the person to healthy right after recoverin from 
     * the sever sickness.
     * 
     */
    public void cure() {
        this.isInfected = false;
        this.isKnowinglyInfected = false;
        this.isSeverelySick = false;
        beingInfectedTime = 0;
        curedCases++;
    }

    /**
     * Einai mia public sinartisi tis klasis Vulnerable.
     *
     * Einai void afu den epistrefei kati Kineitai o vulnerable person. Dialegei
     * 1 cell apo ta 8 geitonika tou i epilegei na min kinithei. Episis an
     * vriskete se sinoriako cell iparxei pithanotita na pai se allo grid pou
     * einai sinoriako me to cell
     *
     *
     *
     */
    public void move() {

        //getting connected cells
        Cell[] connectedCells = calculateConnectedCells(getCurrentCell());
        boolean hasMoved = false;

        //transferring in quarantine in case of he is knowingly infected
        if (isKnowinglyInfected && !getCurrentCell().isQuarantined() && !getGrid().isQuarantineFull()) {
            transfer(InfectionSimulator.getRandomQuarantinedCell(getGrid()));
            hasMoved = true;
        }

        if (isInHospital) {
            return;
        }

        //attempting to move
        while (!hasMoved) {
            //in case of deciding not to move breks the loop
            if (InfectionSimulator.getRandomResult(30)) {
                //Decides not to move by 30%
                hasMoved = true;
                break;
            }
            //Decides to move. In case of occupied dicision, 
            //decides again
            int randomMove = InfectionSimulator.randomizer.nextInt(8);
            //In case of a null decision and cell is border cell
            //--> person transfer to the cell's area

            if (connectedCells[randomMove] == null && getCurrentCell().isBorder() && !getCurrentCell().isQuarantined()
                    && !getCurrentCell().getConnectedArea().isFull()) {

                System.out.println(getId() + " TRANSFERED FROM " + getCurrentCell() + getCurrentCell().getArea().getName() + " to " + getCurrentCell().getConnectedArea().getName());

                transfer(getCurrentCell().getConnectedArea());
                hasMoved = true;
            } else if (connectedCells[randomMove] != null && !connectedCells[randomMove].isOccupied()) {

                if (connectedCells[randomMove].isQuarantined()) {

                    if (isKnowinglyInfected) {
                        //transfer person
                        transfer(connectedCells[randomMove]);
                        hasMoved = true;
                    }

                } else {
                    if (!isKnowinglyInfected || isKnowinglyInfected && getGrid().isQuarantineFull()) {
                        //transfer person
                        transfer(connectedCells[randomMove]);
                        hasMoved = true;
                    }
                }

            }
        }
        //in case of severe sickness --> join hospital
        if (isSeverelySick && !isInHospital) {
            joinHospital();
        }

        if (isInfected) {
            beingInfectedTime += 30;
            if (beingInfectedTime >= infectedPersonAsymptomaticTime) {
                isKnowinglyInfected = true;
            }
            //attempts to make this person severly sick
            makeSeverelySick();

            if (!getCurrentCell().isQuarantined()) {
                //if infected attempts to infect surrounding people
                infectSurroundingPeople(personInfectsPersonProbability);
            }

        }

    }

    /**
     * This method adds the person to the hospital
     */
    public void joinHospital() {
        if (!getGrid().getHospital().isFull()) {
            this.getCurrentCell().free();
            this.getGrid().getHospital().addPatient(this);
            isInHospital = true;
        }

    }

    /**
     * This method releases the person from the hospital.He is healthy again
     */
    public void leaveHospital() {
        if (!getGrid().isNonQuarantineFull()) {
            isInHospital = false;
            this.transfer(InfectionSimulator.getRandomCell(getGrid(), true));
        }

    }

    /**
     * Determines whether the person is recovering in the hospital
     * @return
     */
    public boolean isInHospital() {
        return isInHospital;
    }

    /**
     * Einai mia public sinartisi tis klasis Vulnerable.
     *
     * Einai string sinartisi afou epistrefei string. To string einai to idio me
     * to string pou epistrefei i toSting() stin person kai prosthetei an einai
     * infected kai an einai protected
     *
     * @return Epistrefei to string pou dimiourgei
     *
     */
    public String toString() {
        String str = super.toString();
        if (isInfected()) {
            str += ", is infected ";
        } else {
            str += ", is healthy ";

        }

        if (isProtected()) {
            str += ", is protected ";
        }
        return str;
    }

}
