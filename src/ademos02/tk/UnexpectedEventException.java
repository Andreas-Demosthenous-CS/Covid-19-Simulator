package ademos02.tk;

import javax.swing.JComponent;
/**
 *Afti i klasi onomazetai UnexpectedEventException
 * kai dimiourgei antikeimena typou UnexpectedEventException.
 *
 *Klironomei apo tin klasi Exception
 * H klasi exei 3 kataskevastes .
 *
 * @author Andreas Demosthenous
 * @version 3.0
 * @since 13/05/2020
 */
public class UnexpectedEventException extends Exception {
    private JComponent faultyComponents[];//pinakas apo JComponents(label,buttons,etc)
    /**
     * Einai enas kataskevastis tis klasis UnexpectedEventException.
     *
     * Kalei ton constructor tis me parametro keno String 
     *
     * @param Den Pernei parametro.
     */
    public UnexpectedEventException() {
        this("");
    }
    /**
     * Einai enas kataskevastis tis klasis UnexpectedEventException.
     *
     * Kalei ton constructor tis Exception (me parametro String )
     *
     * @param Pernei parametro String.
     *
     */
    public UnexpectedEventException(String msg) {
        super(msg);
    }
    /**
     * Einai enas kataskevastis tis klasis UnexpectedEventException.
     *
     * Kalei ton constructor tis Exception (me parametro String )
     * kai arxikopiei ton pinaka faultyComponents analoga me tin parametro
     *
     * @param Pernei parametro String kai ena pinaka apo JComponent.
     */
    public UnexpectedEventException(String msg, JComponent j[]) {
        super(msg);
        this.faultyComponents = j;
    }
    /**
     * Einai mia sinartisi tis klasis UnexpectedEventException.
     *
     * Einai typou JComponent[] afou epistrefei pinaka 
     * apo JComponents. Einai getter method
     *
     * @return Epistrefei ton pinaka faultyComponents
     *
     * @param Pernei parametro String.
     * 
     */
    public JComponent[] getFaultyComponents(){
        return faultyComponents;
    }
}
