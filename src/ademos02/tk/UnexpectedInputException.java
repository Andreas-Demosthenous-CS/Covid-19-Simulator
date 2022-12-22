package ademos02.tk;
/**
 *Afti i klasi onomazetai UnexpectedInputException
 * kai dimiourgei antikeimena typou UnexpectedInputException.
 *
 *Klironomei apo tin klasi Exception
 * H klasi exei 3 kataskevastes .
 *
 * @author Andreas Demosthenous
 * @version 3.0
 * @since 13/05/2020
 */
import javax.swing.JTextField;

public class UnexpectedInputException extends Exception {

    private JTextField component;

    /**
     * Einai enas kataskevastis tis klasis UnexpectedInputException
     *
     * Kalei ton constructor tis (me parametro String kai JTextField)
     *
     * @param Pernei parametro String.
     *
     */
    public UnexpectedInputException(String msg) {
        this(msg, null);
    }

    /**
     * Einai enas kataskevastis tis klasis UnexpectedInputException
     *
     * Kalei ton constructor tis Exception me parametro string kai apothikevei
     * to component.
     *
     * @param Pernei parametro String kai JTextField.
     *
     */
    public UnexpectedInputException(String msg, JTextField component) {
        super(msg);
        this.component = component;
    }

    /**
     * Einai enas kataskevastis tis klasis UnexpectedInputException
     *
     * Kalei ton constructor tis me parametro ena keno string.
     *
     * @param Den Pernei parametro .
     *
     */
    public UnexpectedInputException() {
        this("");
    }

    /**
     * Einai mia getter methodos pou epistrefei to component
     *
     * @param Den Pernei parametro .
     *
     */
    public JTextField getFaultyArea() {
        return component;
    }

}
