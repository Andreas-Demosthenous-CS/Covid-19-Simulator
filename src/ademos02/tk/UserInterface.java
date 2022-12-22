package ademos02.tk;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * This class is the GUI of the simulation that will read the parameters from
 * the user
 *
 * @author Andreas Demosthenous
 * @version 3.0
 * @since 13/05/2020
 */
public class UserInterface extends javax.swing.JFrame {

    /**
     * This variable indicates if the simulation is running
     */
    public static boolean isRunning = false;
    private int busCounter = 0;//metritis leoforion
    private int areaCounter = 0;//metritis perioxon
    private int infectedPeople;//infected people
    private int imunePeople;//anthropoi pou den kollun
    private int protectedPeople;//anthropoi pou pairnoun prostateftika metra
    private int mapCapacity;// pliris xoritikotita tou xarti 
    private int totalPopulation; //pliris plithismos
    private int personInfectsPersonProbability;//pithanotita enas anthropos na kollisi apo anthropo
    private int cellInfectsPersonProbability;//pithanotita ena cell na kollisi kapio anthropo
    private int personInfectsCellProbability;//pithanotita enas anthropos na kani infected kapio cell
    private int labTestFalseProbability;//pithanotita ena test na vgei lanthasmeno
    private int simTime;//xronos se lepta
    private int personInfectsCellTime;//xronos pou xriazete gia na kanei enas infected athropos infect ena cell
    private int cellHealingTime;//xronos pou xriazete ena cell gia na min einai pleon infected
    private int hospitalCureDuration;//xronos gia na giatreftei kapios pou pige sto nosokomio
    private int infectedPersonAsymptomaticTime;//xronos gia na emfanisei simptomata ena atomo me ton io
    private boolean useGraphics;//an tha emfanizete stin othoni grafiki apeikonisi tis epidimias
    private int updatePauseTime;//xronos se ms pou kanei update to grid
    private InfectionSimulator is;//the simulator object
    private SimulatorThread simThread;//the simulation thread
    private Map grids;//Map --> ArrayList me ola ta grids
    private BusStation buses;//Map --> BusStation me ola ta buses 
    private ArrayList<AreaTab> areaTabs;//ArrayList me ta tabs(gui comp) gia tis perioxes
    private ArrayList<BusTab> busTabs;// ArrayList me ta tabs gia ta leoforia

    /**
     * The constructor initializes the graphical components
     */
    public UserInterface() {

        //initializing gui components    
        initComponents();

        //initializing the tab list
        areaTabs = new ArrayList<AreaTab>();

        //adding two tabs (the default ones)
        areaTabs.add(new AreaTab());
        areaTabs.add(new AreaTab());

        busTabs = new ArrayList<BusTab>();
        
        
        //adding the default bus
        busTabs.add(new BusTab());
        busTabs.get(0).getBus().setSource("Area 1");
        busTabs.get(0).getBus().setDestination("Area 2");
        busTabs.get(0).updateBusStatusArea();
        
        //sets the frame visible
        setVisible(true);
        //adds icon to the window
        setIconImage(new ImageIcon("Images/virus.png").getImage());

    }

    //We didnt plan to use Threads but in order to have both StdDraw frame and my JFrame 
    //updating live, we have to run them on different threads.
    private class SimulatorThread extends Thread {

        //the constructor that creates the simulator using the specified parameters
        public SimulatorThread() {

            //Initializing the grid list
            grids = initializeMap();

            //Initializing the buses list
            buses = initializeBuses();

            //create simulator
            is = new InfectionSimulator(totalPopulation, infectedPeople, imunePeople,
                    protectedPeople, grids, buses, simTime, personInfectsCellTime,
                    infectedPersonAsymptomaticTime, personInfectsPersonProbability,
                    cellInfectsPersonProbability, personInfectsCellProbability, labTestFalseProbability, updatePauseTime);
        }

        //returns a list of Grids with characteristics given in the GUI
        private Map initializeMap() {
            Map map = new Map();

            //creating the grids
            for (int i = 0; i < getAreasAmount(); i++) {
                //creating the grid based on the information from the area tab the user entered
                map.add(new Grid(areaTabs.get(i).getName(), areaTabs.get(i).getColor(), areaTabs.get(i).getArea().getSize(), areaTabs.get(i).getArea().getSize(),
                        areaTabs.get(i).getArea().getInitialPopulation(), new Hospital(areaTabs.get(i).getArea().getHospitalCapacity(), hospitalCureDuration),
                        cellHealingTime, personInfectsCellTime, personInfectsCellProbability));
            }

            //adding the border-quarantine area for each cell *after the creation of all areas
            for (int i = 0; i < getAreasAmount(); i++) {
                //getting the list with the border cells
                ArrayList<SpecialCell> borderCells = areaTabs.get(i).getArea().getBorder();
                //for each cell setting it the specified connected area:
                for (int k = 0; k < borderCells.size(); k++) {
                    //getting the Grid from the border Cells list of this area(using its name)
                    Grid areaToConnect = map.getByName(borderCells.get(k).getConnectedArea().getName());
                    //getting the Cell from the border Cells list of this area
                    Cell celltoConnect = borderCells.get(k).getCell();
                    //Connecting the Cell with the Grid
                    //* The array (x,y) is different with the cartesian coordinates(x,y) os the stdDraw
                    //so I transform them like this:
                    //x --> x
                    //y --> size - 1 - y
                    map.get(i).getCell(celltoConnect.getX(), areaTabs.get(i).getArea().getSize() - 1 - celltoConnect.getY())
                            .setConnectedArea(areaToConnect);
                }

                //getting the list with the quarantine cells
                ArrayList<SpecialCell> quarantineCells = areaTabs.get(i).getArea().getQuarantine();

                //for each cell setting it as quarantine cell
                for (int k = 0; k < quarantineCells.size(); k++) {

                    Cell cell = quarantineCells.get(k).getCell();
                    //Connecting the Cell with the Grid
                    //* The array (x,y) is different with the cartesian coordinates(x,y) os the stdDraw
                    //so I transform them like this:
                    //x --> x
                    //y --> size - 1 - y
                    map.get(i).getCell(cell.getX(), areaTabs.get(i).getArea().getSize() - 1 - cell.getY())
                            .setQuarantined();
                }

            }

            return map;
        }

        //Inits the buses for the simulation
        private BusStation initializeBuses() {
            BusStation station = new BusStation();
            //creating the buses
            for (int i = 0; i < getBusesAmount(); i++) {
                //creating the buses based on the information from the Bus tab the user entered
                station.add(new MyBus(busTabs.get(i).getBus().getName(), busTabs.get(i).getBus().getCapacity(),
                        busTabs.get(i).getBus().getTripScheduleTime(), busTabs.get(i).getBus().getTripDuration(),
                        grids.getByName(busTabs.get(i).getBus().getSource()),
                        grids.getByName(busTabs.get(i).getBus().getDestination())));
            }
            return station;
        }

        //the method to run when the Thread is started
        public void run() {

            //starting the simulation
            is.startSimulation(useGraphics);

            //Getting the total infected cases from the static counter in Vulnerable.java
            int finalInfections = Vulnerable.infectionCases;
            int recoveredCases = Vulnerable.curedCases;

            //present results on the screen with an info pane
            JOptionPane.showMessageDialog(UserInterface.this, " Population: " + totalPopulation
                    + "\n Total infection cases: " + (finalInfections + infectedPeople)
                    + "(" + InfectionSimulator.getPercentage(totalPopulation, (finalInfections + infectedPeople)) + "%)"
                    + "\n Initial infection cases: " + (infectedPeople) + "("
                    + InfectionSimulator.getPercentage(totalPopulation, (infectedPeople)) + "%)"
                    + "\n New infection cases: " + (finalInfections) + "("
                    + InfectionSimulator.getPercentage(totalPopulation, finalInfections) + "%)"
                    + "\n Final infection cases: " + (finalInfections + infectedPeople - recoveredCases) + "("
                    + InfectionSimulator.getPercentage(totalPopulation, (finalInfections + infectedPeople - recoveredCases))
                    + "%)" + "\n Recovery cases: " + (recoveredCases) + "("
                    + InfectionSimulator.getPercentage(totalPopulation, recoveredCases)
                    + "%)", "Results", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    //This object is used for handling everything on the tab(GUI component) 
    //for each area.
    private class AreaTab {

        //Tab's are
        private Area area;
        //area's color
        private Color color;
        //name
        private String name;

        //Tabs swing components:
        private JLabel sizeLabel, initialPopLabel, hospitalCapLabel;
        private JTextField sizeText, InitialPopText, HospitalCopText;
        private JButton setBorderButton, setQuarantineButton;
        private JButton clearBorderButton, clearQuarantineButton;
        private JTextArea statusArea;
        private JPanel areaPanel;
        private JScrollPane statusAreaScrollPane;
        private GroupLayout panelLayout;

        AreaTab() {
            //calculating the areas name(MUST be unique)
            this.name = "Area " + (++areaCounter);
            //Initializing the are with the name
            this.area = new Area(name);

            //Picking a random color 
            color = new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
            //Making sure the color is compatible with black
            while (!compatible(color, Color.BLACK)) {
                color = new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
            }

            //Initilizing the graphical components
            initComponents();

            //adding the tab to the tabbedPanel component
            areaTabbedPanes.setBackgroundAt(areaTabbedPanes.getComponentCount() - 1, color);
        }

        //Getter for the area
        public Area getArea() {
            return area;
        }

        //Getter for the name
        public String getName() {
            return name;
        }

        //Getter for the color
        public Color getColor() {
            return color;
        }

        //assisting method into determining color's compatibility
        private double lum(Color c) {
            int r = c.getRed();
            int g = c.getGreen();
            int b = c.getBlue();
            return .299 * r + .587 * g + .114 * b;
        }

        //Chekcing color's a compatibility with color b
        private boolean compatible(Color a, Color b) {
            return Math.abs(lum(a) - lum(b)) >= 128.0;
        }

        //initializing the components of the area tab
        private void initComponents() {

            sizeLabel = new JLabel();
            initialPopLabel = new JLabel();
            hospitalCapLabel = new JLabel();

            sizeText = new JTextField();
            InitialPopText = new JTextField();
            HospitalCopText = new JTextField();

            setBorderButton = new JButton();
            clearBorderButton = new JButton();
            setQuarantineButton = new JButton();
            clearQuarantineButton = new JButton();

            statusArea = new JTextArea();
            areaPanel = new JPanel();
            statusAreaScrollPane = new JScrollPane();
            panelLayout = new GroupLayout(areaPanel);
            sizeLabel.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
            sizeLabel.setText("Size:");

            sizeText.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
            sizeText.setText("5");
            //Adding a keyListener to the textfield in order to 
            //disable the setBorder button when user enters a value greater than 
            //200 as it could crash the application
            sizeText.addKeyListener(new KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent evt) {
                    String txt;
                    int size;
                    txt = sizeText.getText();
                    if (isInteger(txt)) {
                        size = Integer.parseInt(txt);
                        if (size > 200) {
                            setBorderButton.setEnabled(false);
                            setQuarantineButton.setEnabled(false);
                        } else {
                            setBorderButton.setEnabled(true);
                            setQuarantineButton.setEnabled(true);
                        }
                    }
                }
            });

            setBorderButton.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
            setBorderButton.setText("Set Border");
            setBorderButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    try {
                        setBorderButtonActionPerformed(evt);
                    } catch (UnexpectedInputException ex) {
                        // colorises the error component
                        ex.getFaultyArea().setForeground(Color.red);
                        //displayes error msg
                        JOptionPane.showMessageDialog(AreaTab.this.areaPanel, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        // sets the color back to black
                        ex.getFaultyArea().setForeground(Color.black);

                    }
                }
            });

            clearBorderButton.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
            clearBorderButton.setText("Clear Border");
            clearBorderButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    clearBorderActionPerformed(evt);
                }
            });

            statusArea.setColumns(20);
            statusArea.setRows(5);
            statusArea.setForeground(color);
            statusArea.setEditable(false);
            statusArea.setFont(new Font("Arial", Font.BOLD, 19));
            statusAreaScrollPane.setViewportView(statusArea);

            initialPopLabel.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
            initialPopLabel.setText("Initial Population:");

            InitialPopText.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
            InitialPopText.setText("10"); // NOI18N

            HospitalCopText.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
            HospitalCopText.setText("10");

            hospitalCapLabel.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
            hospitalCapLabel.setText("Hospital Capacity:");

            setQuarantineButton.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
            setQuarantineButton.setText("Set Quarantine Area");
            setQuarantineButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    try {
                        setQuarantineButtonActionPerformed(evt);
                    } catch (UnexpectedInputException ex) {
                        // colorises the error component
                        ex.getFaultyArea().setForeground(Color.red);
                        //displayes error msg
                        JOptionPane.showMessageDialog(AreaTab.this.areaPanel, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        // sets the color back to black
                        ex.getFaultyArea().setForeground(Color.black);

                    }
                }
            });

            clearQuarantineButton.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
            clearQuarantineButton.setText("Clear Quarantine Area");
            clearQuarantineButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    clearQuarantineActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(areaPanel);
            areaPanel.setLayout(panelLayout);
            panelLayout.setHorizontalGroup(
                    panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(clearQuarantineButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(setQuarantineButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(setBorderButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(clearBorderButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelLayout.createSequentialGroup()
                                                            .addComponent(sizeLabel)
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                            .addComponent(sizeText))
                                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelLayout.createSequentialGroup()
                                                            .addComponent(hospitalCapLabel)
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                            .addComponent(HospitalCopText, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE))
                                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelLayout.createSequentialGroup()
                                                            .addComponent(initialPopLabel)
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                            .addComponent(InitialPopText))))
                                    .addGap(30, 30, 30)
                                    .addComponent(statusAreaScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
                                    .addContainerGap())
            );
            panelLayout.setVerticalGroup(
                    panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(panelLayout.createSequentialGroup()
                                                    .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                            .addComponent(sizeLabel)
                                                            .addComponent(sizeText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                            .addComponent(initialPopLabel)
                                                            .addComponent(InitialPopText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                            .addComponent(hospitalCapLabel)
                                                            .addComponent(HospitalCopText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                    .addComponent(setBorderButton)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(clearBorderButton)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(setQuarantineButton)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(clearQuarantineButton))
                                            .addComponent(statusAreaScrollPane))
                                    .addContainerGap())
            );

            //Adding the graphical tab to the GUI
            areaTabbedPanes.addTab(name, areaPanel);
            //changing the focus to the newly created tab
            areaTabbedPanes.setSelectedComponent(areaPanel);

        }

        //method for the setBorder Button when clicked
        private void setBorderButtonActionPerformed(ActionEvent evt) throws UnexpectedInputException {
            //First getting the areas size and validating it
            String txt;
            int size;
            //read grid size
            //read grid and check
            txt = sizeText.getText();

            if (isInteger(txt)) {
                size = Integer.parseInt(sizeText.getText());
                if (size < 0) {
                    throw new UnexpectedInputException("Grid size > 0", sizeText);
                }
            } else {
                throw new UnexpectedInputException("Grid size must be an integer", sizeText);
            }

            //If size is valid
            area.setSize(size);
            //Creating a new popup window for the user to enter the area's border
            new BorderSetPopup(this.getArea());
        }

        //method for the clear Border Button when clicked
        private void clearBorderActionPerformed(ActionEvent evt) {
            //clearing the already specified border in the area object
            this.getArea().clearBorder();
            //clearing the border status area on the GUI
            updateStatusArea();
        }

        //method for the setQuarantine Button when clicked
        private void setQuarantineButtonActionPerformed(ActionEvent evt) throws UnexpectedInputException {
            //First getting the areas size and validating it
            String txt;
            int size;
            //read grid size
            //read grid and check
            txt = sizeText.getText();

            if (isInteger(txt)) {
                size = Integer.parseInt(sizeText.getText());
                if (size < 0) {
                    throw new UnexpectedInputException("Grid size > 0", sizeText);
                }
            } else {
                throw new UnexpectedInputException("Grid size must be an integer", sizeText);
            }

            //If size is valid
            area.setSize(size);
            //Creating a new popup window for the user to enter the area's quarantine
            new QuarantineSetPopup(this.getArea());
        }

        //method for the clear Quarantine Button when clicked
        private void clearQuarantineActionPerformed(ActionEvent evt) {
            //clearing the already specified in the area object
            this.getArea().clearQuarantine();
            //updating status area on the GUI
            updateStatusArea();
        }

        //method for updating the text area on the gui
        private void updateStatusArea() {
            statusArea.setText("");
            statusArea.append("Border: \n");
            for (int i = 0; i < getArea().getBorder().size(); i++) {
                statusArea.append(getArea().getBorder().get(i).toString() + "\n");
            }

            statusArea.append("\nQuarantine: \n");
            for (int i = 0; i < getArea().getQuarantine().size(); i++) {
                statusArea.append(getArea().getQuarantine().get(i).toString() + "\n");
            }
        }

        //This object is used for handling the popup window for setting the border
        //for each area
        private class BorderSetPopup extends JFrame {

            //the size of the square(cell representation)
            private int squareSize;
            //2d array for the border cells
            private SpecialCell cells[][];
            //boolean flag that is true if the mouse has clicked a cell
            private boolean cellIsPressed = false;
            //areas size
            private int size;
            //area
            private Area area;

            //GUI components
            private JPanel panel;
            private JLabel sizeXsizeLabel;
            private JButton submitButton;
            private JRadioButton areasRadioButtons[];
            private ButtonGroup radioGroup;

            public BorderSetPopup(Area area) {
                this.size = area.getSize();
                this.area = area;
                //fixed window is 800 x 800
                this.squareSize = 650 / size;

                //Init the cells
                cells = new SpecialCell[size][size];

                //Init components
                initComponents();
                //Loading the old config
                loadOldConfiguration();

                setVisible(true);

            }

            //Method for loading the old configuration on the window
            private void loadOldConfiguration() {
                //Getting the already defined border fo the area
                ArrayList<SpecialCell> bCells = area.getBorder();

                //Loading it on the window
                for (int i = 0; i < bCells.size(); i++) {
                    int x = bCells.get(i).getCell().getX();
                    int y = bCells.get(i).getCell().getY();
                    if (bCells.get(i).getConnectedArea().getSize() != 0) {
                        cells[x][y].setBackground(bCells.get(i).getColor());
                        cells[x][y].setConnectedArea(bCells.get(i).getConnectedArea());
                        cells[x][y].setColor(bCells.get(i).getColor());
                    }
                }
            }

            private void initComponents() {
                //setting window's parameters
                setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
                setTitle("Set Border for " + AreaTab.this.getName());
                setAlwaysOnTop(true);
                setBounds(new java.awt.Rectangle(100 + squareSize * size, 90 + squareSize * size, 206, 206));
                setLocation(areaTabbedPanes.getLocation());
                setMinimumSize(new java.awt.Dimension(150 + squareSize * size, 140 + squareSize * size));
                setSize(new java.awt.Dimension(264, 236));

                //Init radio buttons array
                areasRadioButtons = new JRadioButton[areaTabs.size()];
                //Init the radio group of the radio buttons
                radioGroup = new ButtonGroup();

                //Config the panel
                panel = new JPanel();
                panel.setLayout(null);
                panel.setBounds(0, 0, 100 + squareSize * size, 90 + squareSize * size);
                setContentPane(panel);

                //Config the Size x Size label
                sizeXsizeLabel = new JLabel();
                sizeXsizeLabel.setFont(new java.awt.Font("Arial", 1, 20));
                sizeXsizeLabel.setText(size + " x " + size);
                sizeXsizeLabel.setBounds(10, 13, 100, 16);
                panel.add(sizeXsizeLabel);

                //create cells
                int i;
                for (i = 0; i < size; i++) {

                    for (int k = 0; k < size; k++) {
                        cells[i][k] = createCell(i, k);
                        panel.add(cells[i][k]);

                    }

                }

                //add radio buttons
                for (int k = 0; k < areasRadioButtons.length; k++) {

                    areasRadioButtons[k] = new JRadioButton();
                    radioGroup.add(areasRadioButtons[k]);
                    areasRadioButtons[k].setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
                    areasRadioButtons[k].setForeground(areaTabs.get(k).getColor());
                    areasRadioButtons[k].setText(areaTabs.get(k).getName());
                    areasRadioButtons[k].setBounds(10 + (squareSize * i), 40 + (35 * k), 120, 25);

                    //not allowing to set the same area as a neighboor.
                    if (areaTabs.get(k).getName() == AreaTab.this.getName()) {
                        areasRadioButtons[k].setEnabled(false);
                    }
                    panel.add(areasRadioButtons[k]);
                }
                //first enabled radio always selected
                if (areasRadioButtons.length > 1) {
                    if (areasRadioButtons[0].isEnabled()) {
                        areasRadioButtons[0].setSelected(true);
                    } else {
                        areasRadioButtons[1].setSelected(true);
                    }
                }

                //Config the submit button
                submitButton = new JButton();
                submitButton.setFont(new java.awt.Font("Arial", 1, 20));
                submitButton.setText("Submit");
                submitButton.setBounds(10, 47 + squareSize * size, 112, 33);
                submitButton.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        //Making the specified changes in the area's border
                        for (int i = 0; i < size; i++) {
                            for (int k = 0; k < size; k++) {
                                if (cells[i][k].getConnectedArea() == null) {
                                    area.removeBorderCell(cells[i][k]);

                                } else {
                                    area.addBorderCell(cells[i][k]);
                                }

                            }
                        }
                        updateStatusArea();

                        //disposing the windows after submition                                              
                        dispose();
                    }

                });
                panel.add(submitButton);

                pack();
            }

            //Method for creating a cell on specified coord
            private SpecialCell createCell(int x, int y) {

                return new SpecialCell(getArea(), new Cell(x, y)) {

                    private int x = getCell().getX();
                    private int y = getCell().getY();
                    private SpecialCell thisCell = this;

                    //instance init block
                    {
                        //Initializing the each graphical cell on the setBorder window popup
                        setBackground(Color.white);
                        setText("   ");
                        setBorder(javax.swing.BorderFactory.createLineBorder(Color.black));
                        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                        setMaximumSize(new Dimension(35, 35));
                        setMinimumSize(new Dimension(squareSize, squareSize));
                        setOpaque(true);
                        setPreferredSize(new Dimension(20, 20));
                        setBounds(10 + (squareSize * x), 40 + (squareSize * y), squareSize, squareSize);

                        //enabling and adding mouse listener to border cells ONLY
                        if (!(x == 0 || x == size - 1 || y == 0 || y == size - 1)) {
                            this.setEnabled(false);
                            this.setBackground(Color.LIGHT_GRAY);
                        } else {
                            //Handling the color change of the cells using the mouse listeners
                            addMouseListener(new MouseAdapter() {

                                public void mousePressed(java.awt.event.MouseEvent evt) {
                                    cellIsPressed = true;
                                    changeCellState();
                                }

                                public void mouseReleased(java.awt.event.MouseEvent evt) {
                                    cellIsPressed = false;
                                }

                                public void mouseEntered(java.awt.event.MouseEvent evt) {
                                    if (cellIsPressed == true) {
                                        changeCellState();
                                    }
                                }

                                //Changing the color of the square with the color-area
                                //of the selected radio button
                                private void changeCellState() {
                                    //Collectiong all the radio buttons in order to
                                    //find which is selected
                                    Enumeration radioButtons = radioGroup.getElements();

                                    while (radioButtons.hasMoreElements()) {
                                        JRadioButton radio = (JRadioButton) radioButtons.nextElement();
                                        if (radio.isSelected()) {

                                            if (radio.getForeground().equals(getColor())) {
                                                clear();
                                                //finding and 
                                                //clearing the connectedArea of the cell to the specified area
                                                for (int i = 0; i < areaTabs.size(); i++) {
                                                    if (areaTabs.get(i).getName().equals(radio.getText())) {
                                                        thisCell.setConnectedArea(null);
                                                        break;
                                                    }
                                                }

                                            } else {
                                                setColor(radio.getForeground());

                                                //finding and 
                                                //changing the connectedArea of the cell to the specified area
                                                for (int i = 0; i < areaTabs.size(); i++) {
                                                    if (areaTabs.get(i).getName().equals(radio.getText())) {
                                                        thisCell.setConnectedArea(areaTabs.get(i).getArea());

                                                        break;
                                                    }
                                                }

                                            }
                                            break;
                                        }

                                    }
                                }

                            });
                        }

                    }
                };
            }

        }

        //This object is used for handling the popup window for setting the quarantine
        //for each area
        private class QuarantineSetPopup extends JFrame {

            //the size of the square(cell representation)
            private int squareSize;
            //2d array for the quarantine cells
            private SpecialCell cells[][];
            //boolean flag that is true if the mouse has clicked a cell
            private boolean cellIsPressed = false;
            //areas size
            private int size;
            //area
            private Area area;

            //GUI components
            private JPanel panel;
            private JLabel sizeXsizeLabel;
            private JButton submitButton;

            public QuarantineSetPopup(Area area) {
                this.size = area.getSize();
                this.area = area;
                //fixed window is 800 x 800
                this.squareSize = 650 / size;

                //Init the cells
                cells = new SpecialCell[size][size];

                //Init components
                initComponents();
                //Loading the old config
                loadOldConfiguration();

                setVisible(true);

            }

            //Method for loading the old configuration on the window
            private void loadOldConfiguration() {
                //Getting the already defined quarantine for the area
                ArrayList<SpecialCell> bCells = area.getQuarantine();

                //Loading it on the window
                for (int i = 0; i < bCells.size(); i++) {
                    int x = bCells.get(i).getCell().getX();
                    int y = bCells.get(i).getCell().getY();
                    if (bCells.get(i).isQuarantine()) {
                        cells[x][y].setBackground(bCells.get(i).getColor());
                        cells[x][y].setColor(bCells.get(i).getColor());
                        cells[x][y].setQuarantine(true);
                    }
                }
            }

            private void initComponents() {
                //setting window's parameters
                setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
                setTitle("Set Quarantine for " + AreaTab.this.getName());
                setAlwaysOnTop(true);
                setBounds(new java.awt.Rectangle(100 + squareSize * size, 90 + squareSize * size, 206, 206));
                setLocation(getContentPane().getLocation());
                setMinimumSize(new java.awt.Dimension(150 + squareSize * size, 140 + squareSize * size));
                setSize(new java.awt.Dimension(264, 236));

                //Config the panel
                panel = new JPanel();
                panel.setLayout(null);
                panel.setBounds(0, 0, 100 + squareSize * size, 90 + squareSize * size);
                setContentPane(panel);

                //Config the Size x Size label
                sizeXsizeLabel = new JLabel();
                sizeXsizeLabel.setFont(new java.awt.Font("Arial", 1, 20));
                sizeXsizeLabel.setText(size + " x " + size);
                sizeXsizeLabel.setBounds(10, 13, 100, 16);
                panel.add(sizeXsizeLabel);

                //create cells
                int i;
                for (i = 0; i < size; i++) {

                    for (int k = 0; k < size; k++) {
                        cells[i][k] = createCell(i, k);
                        panel.add(cells[i][k]);

                    }

                }

                //Config the submit button
                submitButton = new JButton();
                submitButton.setFont(new java.awt.Font("Arial", 1, 20));
                submitButton.setText("Submit");
                submitButton.setBounds(10, 47 + squareSize * size, 112, 33);
                submitButton.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        statusArea.setText("");

                        for (int i = 0; i < size; i++) {
                            for (int k = 0; k < size; k++) {
                                if (!cells[i][k].isQuarantine()) {
                                    area.removeQuarantineCell(cells[i][k]);

                                } else {
                                    area.addQuarantineCell(cells[i][k]);
                                }

                            }
                        }
                        updateStatusArea();
                        //disposing the windows after submition
                        dispose();
                    }
                });
                panel.add(submitButton);

                pack();
            }

            //Method for creating a cell on specified coord
            private SpecialCell createCell(int x, int y) {

                return new SpecialCell(getArea(), new Cell(x, y)) {
                    private final Color quarantineColor = Color.red;
                    private int x = getCell().getX();
                    private int y = getCell().getY();
                    private SpecialCell thisCell = this;

                    //instance init block
                    {
                        //Initializing the each graphical cell on the setBorder window popup
                        setBackground(Color.white);
                        setText("   ");
                        setBorder(javax.swing.BorderFactory.createLineBorder(Color.black));
                        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                        setMaximumSize(new Dimension(35, 35));
                        setMinimumSize(new Dimension(squareSize, squareSize));
                        setOpaque(true);
                        setPreferredSize(new Dimension(20, 20));
                        setBounds(10 + (squareSize * x), 40 + (squareSize * y), squareSize, squareSize);

                        //Handling the color change of the cells using the mouse listeners
                        addMouseListener(new MouseAdapter() {

                            public void mousePressed(java.awt.event.MouseEvent evt) {
                                cellIsPressed = true;
                                changeCellState();
                            }

                            public void mouseReleased(java.awt.event.MouseEvent evt) {
                                cellIsPressed = false;
                            }

                            public void mouseEntered(java.awt.event.MouseEvent evt) {
                                if (cellIsPressed == true) {
                                    changeCellState();
                                }
                            }

                            //Changing the color of the square with the color-area
                            //of the selected radio button
                            private void changeCellState() {

                                if (getColor().equals(quarantineColor)) {
                                    setQuarantine(false);
                                    clear();
                                } else {
                                    setColor(quarantineColor);
                                    setQuarantine(true);
                                }

                            }

                        });

                    }
                };
            }

        }

    }

    private class BusTab {

        //Tab's are
        private Bus bus;
        //area's color
        private Color color;
        //name
        private String name;

        //Tabs swing components:
        private JLabel sourceArea, destinationArea, capacity, tripScheduleTime1,
                tripScheduleTime2, tripDuration;
        private JTextField sourceAreaText, destinationAreaText, capacityText,
                tripScheduleTimeText, tripDurationText;
        private JButton addSourceButton;
        private JButton addDestinationButton;
        private JTextArea busStatusArea;
        private JPanel busPanel;
        private JScrollPane busStatusAreaScroller;
        private GroupLayout panelLayout;

        BusTab() {
            //calculating the areas name(MUST be unique)
            this.name = "Bus " + (++busCounter);
            //Initializing the are with the name
            this.bus = new Bus(name);
            //Picking a random color 
            color = Color.BLACK;
            //Initilizing the graphical components
            initComponents();

            //adding the tab to the tabbedPanel component
            BusTabbedPanes.setBackgroundAt(BusTabbedPanes.getComponentCount() - 1, color);
        }

        //Getter for the area
        public Bus getBus() {
            return bus;
        }

        //Getter for the name
        public String getName() {
            return name;
        }

        //Getter for the color
        public Color getColor() {
            return color;
        }

        //initializing the components of the bus tab
        private void initComponents() {
            sourceArea = new JLabel();
            destinationArea = new JLabel();
            capacity = new JLabel();
            tripScheduleTime1 = new JLabel();
            tripScheduleTime2 = new JLabel();
            tripDuration = new JLabel();

            sourceAreaText = new JTextField();
            destinationAreaText = new JTextField();
            capacityText = new JTextField();
            tripScheduleTimeText = new JTextField();
            tripDurationText = new JTextField();

            busStatusArea = new JTextArea();
            busPanel = new JPanel();
            busStatusAreaScroller = new JScrollPane();
            panelLayout = new GroupLayout(busPanel);

            addDestinationButton = new JButton();
            addSourceButton = new JButton();

            capacity.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
            capacity.setText("Capacity:");

            capacityText.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
            capacityText.setText("10");

            tripScheduleTime1.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
            tripScheduleTime1.setText("Trip Schedule Time(minutes):");

            tripScheduleTimeText.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
            tripScheduleTimeText.setText("120");

            tripDuration.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
            tripDuration.setText("Trip Duration(minutes):");

            tripDurationText.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
            tripDurationText.setText("90");

            tripScheduleTime2.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
            tripScheduleTime2.setText("(trip every x minutes)");

            addDestinationButton.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
            addDestinationButton.setText("Add Destination");
            addDestinationButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    addDestinationButtonActionPerformed(evt);
                }
            });

            addSourceButton.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
            addSourceButton.setText("Add Source");
            addSourceButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    addSourceButtonActionPerformed(evt);
                }
            });

            busStatusArea.setColumns(20);
            busStatusArea.setEditable(false);
            busStatusArea.setFont(new java.awt.Font("Monospaced", 0, 18)); // NOI18N
            busStatusArea.setRows(3);
            busStatusAreaScroller.setViewportView(busStatusArea);

            panelLayout = new javax.swing.GroupLayout(busPanel);
            busPanel.setLayout(panelLayout);
            panelLayout.setHorizontalGroup(
                    panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(panelLayout.createSequentialGroup()
                                                    .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                            .addComponent(addDestinationButton, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                                                            .addComponent(addSourceButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                    .addComponent(busStatusAreaScroller, javax.swing.GroupLayout.PREFERRED_SIZE, 418, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(panelLayout.createSequentialGroup()
                                                    .addComponent(capacity)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                    .addComponent(capacityText, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(panelLayout.createSequentialGroup()
                                                    .addComponent(tripScheduleTime1)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                    .addComponent(tripScheduleTimeText, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                    .addComponent(tripScheduleTime2))
                                            .addGroup(panelLayout.createSequentialGroup()
                                                    .addComponent(tripDuration)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                    .addComponent(tripDurationText, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            panelLayout.setVerticalGroup(
                    panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(capacityText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(capacity))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(tripScheduleTimeText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(tripScheduleTime1)
                                            .addComponent(tripScheduleTime2))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(tripDurationText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(tripDuration))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(panelLayout.createSequentialGroup()
                                                    .addComponent(addSourceButton, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(addDestinationButton, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(busStatusAreaScroller))
                                    .addContainerGap(25, Short.MAX_VALUE))
            );
            //Adding the graphical tab to the GUI
            BusTabbedPanes.addTab(name, busPanel);
            //changing the focus to the newly created tab
            BusTabbedPanes.setSelectedComponent(busPanel);
            /*
            //setting a border for the area
            areaPanel.setBorder(new MatteBorder(null));
            //setting the font
            areaPanel.setFont(new Font("Arial", 0, 24)); // NOI18N

            setBorderButton.setFont(new Font("Arial", 0, 24)); // NOI18N
            setBorderButton.setText("Set Border");
            //setting the cursors format to the Hand cursor
            setBorderButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            //Setting an action listener in order to handle button clicks
            setBorderButton.addActionListener(new ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    try {
                        setBorderButtonActionPerformed(evt);
                    } catch (UnexpectedInputException ex) {
                        // colorises the error component
                        ex.getFaultyArea().setForeground(Color.red);
                        //displayes error msg
                        JOptionPane.showMessageDialog(AreaTab.this.areaPanel, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        // sets the color back to black
                        ex.getFaultyArea().setForeground(Color.black);

                    }
                }
            });

            sizeLabel.setFont(new Font("Arial", 0, 24)); // NOI18N
            sizeLabel.setText("Size:");

            sizeText.setFont(new Font("Arial", 0, 24)); // NOI18N
            sizeText.setText("5");
            //Adding a keyListener to the textfield in order to 
            //disable the setBorder button when user enters a value greater than 
            //200 as it could crash the application
            sizeText.addKeyListener(new KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent evt) {
                    String txt;
                    int size;
                    txt = sizeText.getText();
                    if (isInteger(txt)) {
                        size = Integer.parseInt(txt);
                        if (size > 200) {
                            setBorderButton.setEnabled(false);
                        } else {
                            setBorderButton.setEnabled(true);
                        }
                    }
                }
            });

            clearBorderButton.setFont(new Font("Arial", 0, 24)); // NOI18N
            clearBorderButton.setText("Clear Border");
            clearBorderButton.addActionListener(new ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    clearBorderActionPerformed(evt);
                }
            });
            clearBorderButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            statusArea.setFont(new Font("Arial", Font.PLAIN, 20));
            statusArea.setEditable(false);
            statusArea.setForeground(getColor());
            statusArea.setColumns(10);
            statusArea.setRows(3);
            statusArea.setTabSize(6);
            statusArea.setText("");
            //Adding the scrollpane to the area
            statusAreaScrollPane.setViewportView(statusArea);

            //Adding each component to the panel 
            areaPanel.setLayout(panelLayout);
            panelLayout.setHorizontalGroup(
                    panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(panelLayout.createSequentialGroup()
                                                    .addComponent(sizeLabel)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(sizeText, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                                                    .addComponent(clearBorderButton, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(setBorderButton, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(statusAreaScrollPane))
                                    .addContainerGap())
            );
            panelLayout.setVerticalGroup(
                    panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(sizeLabel)
                                            .addComponent(sizeText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(setBorderButton)
                                            .addComponent(clearBorderButton))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(statusAreaScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                                    .addContainerGap())
            );
            //Adding the graphical tab to the GUI
            areaTabbedPanes.addTab(name, areaPanel);
            //changing the focus to the newly created tab
            areaTabbedPanes.setSelectedComponent(areaPanel);
             */
        }

        //initializing the destination popup frame
        private void addDestinationButtonActionPerformed(ActionEvent evt) {
            new Source_DestinationSetPopup(1);
        }

        //initializing the source popup frame
        private void addSourceButtonActionPerformed(ActionEvent evt) {
            new Source_DestinationSetPopup(0);
        }

        //This object is used for handling the popup window for setting the border
        //for each area
        private class Source_DestinationSetPopup extends JFrame {

            private int squareSize;

            //1 -> destination , 0 -> source
            private int type;

            //GUI components
            private JPanel panel;
            private JButton submitButton;
            private JRadioButton areasRadioButtons[];
            private ButtonGroup radioGroup;

            public Source_DestinationSetPopup(int type) {
                //0 --> source, 1 -> destination
                this.type = type;

                this.squareSize = 25;

                //Init components
                initComponents();

                setVisible(true);

            }

            private void initComponents() {
                //setting window's parameters
                setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
                if (type == 0) {
                    setTitle("Set Source for " + BusTab.this.getName());
                } else {
                    setTitle("Set Destination for " + BusTab.this.getName());
                }
                setAlwaysOnTop(true);
                //setBounds(new java.awt.Rectangle(100 , 90, 256, areaCounter*squareSize));
                setLocation(UserInterface.this.getLocation());
                setMinimumSize(new java.awt.Dimension(300, (areaCounter + 1) * squareSize + 120));

                //Init radio buttons array
                areasRadioButtons = new JRadioButton[areaTabs.size()];
                //Init the radio group of the radio buttons
                radioGroup = new ButtonGroup();

                //Config the panel
                panel = new JPanel();
                panel.setLayout(null);
                panel.setBounds(0, 0, 200 + squareSize, 90 + squareSize);
                setContentPane(panel);
                boolean selected = false;
                //add radio buttons
                for (int k = 0; k < areasRadioButtons.length; k++) {

                    areasRadioButtons[k] = new JRadioButton();

                    radioGroup.add(areasRadioButtons[k]);
                    areasRadioButtons[k].setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
                    areasRadioButtons[k].setForeground(areaTabs.get(k).getColor());
                    areasRadioButtons[k].setText(areaTabs.get(k).getName());
                    areasRadioButtons[k].setBounds(10, 40 + (25 * k), 120, 25);
                    if (areaTabs.get(k).getName().equals(getBus().getSource()) || areaTabs.get(k).getName().equals(getBus().getDestination())) {
                        areasRadioButtons[k].setEnabled(false);
                    }
                    if (!selected && areasRadioButtons[k].isEnabled()) {
                        areasRadioButtons[k].setSelected(true);
                        selected = true;
                    }

                    panel.add(areasRadioButtons[k]);
                }

                //Config the submit button
                submitButton = new JButton();
                submitButton.setFont(new java.awt.Font("Arial", 1, 20));
                submitButton.setText("Submit");
                submitButton.setBounds(10, (squareSize) * (areaCounter) + 50, 112, 33);
                submitButton.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        //Making the specified changes
                        busStatusArea.append(name);
                        Enumeration radioButtons = radioGroup.getElements();
                        String newSource_Destination = null;
                        while (radioButtons.hasMoreElements()) {
                            JRadioButton radio = (JRadioButton) radioButtons.nextElement();
                            if (radio.isSelected()) {
                                newSource_Destination = radio.getText();
                            }
                            if (type == 0) {
                                getBus().setSource(newSource_Destination);
                            } else if (type == 1) {
                                getBus().setDestination(newSource_Destination);
                            }

                        }

                        //updating the area
                        updateBusStatusArea();
                                
                        //disposing the windows after submition
                        dispose();
                    }
                });
                panel.add(submitButton);

                pack();
            }

        }

        //method for updating the text area on the gui
        private void updateBusStatusArea() {
            busStatusArea.setText("");
            busStatusArea.append("Source: "+ getBus().getSource()+"\n");
            busStatusArea.append("Destination: "+ getBus().getDestination()+"\n");
        }
    }

    //This class hold the information for an area
    private class Area {

        //area's size
        private int size;

        //inital population
        int initialPopulation;

        //hospital's capacity
        int hospitalCapacity;

        //areas name
        private String name;

        //areas list of border cells
        private ArrayList<SpecialCell> borderCellsList;

        //areas list of border cells
        private ArrayList<SpecialCell> quarantineCellsList;

        public Area(String name) {
            //default size = 5
            this(name, 5);
        }

        public Area(String name, int size) {
            this.name = name;
            this.size = size;
            borderCellsList = new ArrayList<SpecialCell>(size);
            quarantineCellsList = new ArrayList<SpecialCell>(size);
        }

        //method for adding a Border cell to the area
        public void addBorderCell(SpecialCell borderCell) {
            //replacing potentially existing borderCell with the new one
            for (int i = 0; i < borderCellsList.size(); i++) {
                if (borderCellsList.get(i).hasSameCell(borderCell)) {
                    borderCellsList.remove(borderCellsList.get(i));
                }
            }

            borderCellsList.add(borderCell);
        }

        //method for removing a border cell to the area
        public void removeBorderCell(SpecialCell borderCell) {

            for (int i = 0; i < borderCellsList.size(); i++) {
                if (borderCellsList.get(i).getCell().equals(borderCell.getCell())) {
                    borderCellsList.remove(i);
                    break;
                }
            }

        }

        //method for adding a Border cell to the area
        public void addQuarantineCell(SpecialCell quarantineCell) {
            //replacing potentially existing borderCell with the new one
            for (int i = 0; i < quarantineCellsList.size(); i++) {
                if (quarantineCellsList.get(i).hasSameCell(quarantineCell)) {
                    quarantineCellsList.remove(quarantineCellsList.get(i));
                }
            }

            quarantineCellsList.add(quarantineCell);
        }

        //method for removing a border cell to the area
        public void removeQuarantineCell(SpecialCell quarantineCell) {

            for (int i = 0; i < quarantineCellsList.size(); i++) {
                if (quarantineCellsList.get(i).getCell().equals(quarantineCell.getCell())) {
                    quarantineCellsList.remove(i);
                    break;
                }
            }

        }

        //getter for the border list
        public ArrayList<SpecialCell> getBorder() {
            return borderCellsList;
        }

        //getter for the border list
        public ArrayList<SpecialCell> getQuarantine() {
            return quarantineCellsList;
        }

        //getter for the size
        public int getSize() {
            return size;
        }

        //setter for the size
        public void setSize(int size) {
            if (this.size != size) {
                this.size = size;
                borderCellsList = new ArrayList<SpecialCell>(size);
                quarantineCellsList = new ArrayList<SpecialCell>(size);
            }

        }

        //getter for the name
        public String getName() {
            return name;
        }

        public String toString() {
            return name;
        }

        //clearing the border
        public void clearBorder() {

            borderCellsList = new ArrayList<SpecialCell>(size);

        }

        //clearing the border
        public void clearQuarantine() {

            quarantineCellsList = new ArrayList<SpecialCell>(size);

        }

        //destroying the area
        public void destroy() {
            this.size = 0;
            this.name = null;
            this.borderCellsList = null;
            this.quarantineCellsList = null;
        }

        private void setInitialPopulation(int initialPopulation) {
            this.initialPopulation = initialPopulation;
        }

        //setter for the hospital capacity
        private void setHospitalCapacity(int hospitalCapacity) {
            this.hospitalCapacity = hospitalCapacity;
        }

        //getter for th init population
        private int getInitialPopulation() {
            return initialPopulation;
        }

        //getter for the hospital capacity
        private int getHospitalCapacity() {
            return hospitalCapacity;
        }
    }

    //This class hold the information for an area
    private class Bus {

        //areas name, source and dest names
        private String name, sourceName, destName;
        //capacity
        private int capacity;
        //trip time
        private int tripScheduleTime;
        //trip duaration
        private int tripDuration;

        public Bus(String name) {
            this.name = name;
            this.sourceName = null;
            this.destName = null;
        }

        private Bus(String name, int capacity, int tripScheduleTime, int tripDuration, Grid byName, Grid byName0) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        //getter for the name
        public String getName() {
            return name;
        }

        //getter for the destination
        public String getDestination() {
            return destName;
        }

        //getter for the source
        public String getSource() {
            return sourceName;
        }

        //setter for the destination
        public void setDestination(String dest) {
            destName = dest;
        }

        //setter for the source
        public void setSource(String source) {
            sourceName = source;
        }

        public String toString() {
            return name;
        }

        //destroying the area
        public void destroy() {
            this.name = null;
        }

        private void setCapacity(int capacity) {
            this.capacity = capacity;
        }

        private void setTripScheduleTime(int tripScheduleTime) {
            this.tripScheduleTime = tripScheduleTime;
        }

        private void setTripDuration(int tripDuration) {
            this.tripDuration = tripDuration;
        }

        private int getCapacity() {
            return capacity;
        }

        private int getTripScheduleTime() {
            return tripScheduleTime;
        }

        private int getTripDuration() {
            return tripDuration;
        }
    }

    //this class holds the information for a special cell(border / quarantine)
    //which is a JLabel too(on the GUI)
    private class SpecialCell extends JLabel {

        //cell of the border cell
        private Cell cell;
        //color
        private Color color;

        private boolean isBorder, isQuarantine;

        private Area connectedArea, area;

        public SpecialCell(Area area, Cell cell, Color color) {
            this.area = area;
            this.cell = cell;
            this.color = color;
            this.connectedArea = null;
            this.isBorder = false;
            this.isQuarantine = false;
        }

        public SpecialCell(Area area, Cell cell) {
            this(area, cell, Color.white);
        }

        //getter for the cell
        public Cell getCell() {
            return cell;
        }

        //setter for the color
        public void setColor(Color col) {
            color = col;
            this.setBackground(col);
        }

        //getter for the color
        public Color getColor() {
            return color;
        }

        //returns if the 2 borders have the same sell
        public boolean hasSameCell(SpecialCell borderCell) {
            return borderCell.cell.equals(cell);
        }

        //clearing the cell
        public void clear() {
            this.color = Color.WHITE;
            this.setBackground(Color.WHITE);
        }

        public boolean isQuarantine() {
            return isQuarantine;
        }

        public boolean isBorder() {
            return isBorder;
        }
        //getter for the area connected

        public Area getConnectedArea() {
            return connectedArea;
        }

        //setter for the area connected
        public void setConnectedArea(Area area) {
            isBorder = true;
            connectedArea = area;
        }

        public void setQuarantine(boolean q) {
            isQuarantine = q;
        }

        public void setBorder() {
            isBorder = true;
        }

        public String toString() {

            //Converting the array coords(Border cell)
            //to cartesian coords representation
            //x --> x
            //y --> size - 1 - y
            if (getCell() != null && connectedArea != null) {
                return "( " + getCell().getX() + " , "
                        + (area.getSize() - 1 - getCell().getY()) + " )"
                        + " --> " + connectedArea.toString();
            } else if (isQuarantine()) {
                return "( " + getCell().getX() + " , "
                        + (area.getSize() - 1 - getCell().getY()) + " )"
                        + " is quarantined ";
            }
            return "";
        }
    }

//startign the simulator method
    private void startSimulation() {
        //init the Thread
        simThread = new SimulatorThread();
        //starting the thread for the simulator to run
        simThread.start();

    }

    //the method for loading all the specified parameters and throwing input exceptions
    private void loadParameters() throws UnexpectedInputException, UnexpectedEventException {
        mapCapacity = 0;
        totalPopulation = 0;
        String txt;
        //checking for each area
        for (int i = 0; i < getAreasAmount(); i++) {
            int size;
            //read grid size
            //read grid and check
            txt = areaTabs.get(i).sizeText.getText();
            if (isInteger(txt)) {
                size = Integer.parseInt(txt);
                if (size <= 0) {
                    throw new UnexpectedInputException("Grid size > 0", areaTabs.get(i).sizeText);
                }
            } else {
                throw new UnexpectedInputException("Grid size must be an integer", areaTabs.get(i).sizeText);
            }
            areaTabs.get(i).getArea().setSize(size);
            mapCapacity += size * size;

            //loading initial population
            int initialPopulation;

            //checking size for each
            txt = areaTabs.get(i).InitialPopText.getText();

            if (isInteger(txt)) {
                initialPopulation = Integer.parseInt(txt);
                if (initialPopulation < 0) {
                    throw new UnexpectedInputException("Initial population >= 0", areaTabs.get(i).InitialPopText);
                } else if (initialPopulation > size * size - areaTabs.get(i).getArea().getQuarantine().size()) {
                    throw new UnexpectedInputException(initialPopulation + " people can't fit in " + (size * size - areaTabs.get(i).getArea().getQuarantine().size()) + " blocks!", areaTabs.get(i).InitialPopText);
                }

            } else {
                throw new UnexpectedInputException("Initial population must be an integer", areaTabs.get(i).InitialPopText);
            }
            areaTabs.get(i).getArea().setInitialPopulation(initialPopulation);
            totalPopulation += initialPopulation;

            //loading hospital capacity
            int hospitalCapacity;

            //checking size for each
            txt = areaTabs.get(i).HospitalCopText.getText();

            if (isInteger(txt)) {
                hospitalCapacity = Integer.parseInt(txt);
                if (hospitalCapacity < 0) {
                    throw new UnexpectedInputException("Hospital Capacity >= 0", areaTabs.get(i).HospitalCopText);
                }
            } else {
                throw new UnexpectedInputException("Hospital Capacity must be an integer", areaTabs.get(i).HospitalCopText);
            }
            areaTabs.get(i).getArea().setHospitalCapacity(hospitalCapacity);

        }

        //checking for each bus
        for (int i = 0; i < getBusesAmount(); i++) {

            int capacity;
            txt = busTabs.get(i).capacityText.getText();
            if (isInteger(txt)) {
                capacity = Integer.parseInt(txt);
                if (capacity <= 0) {
                    throw new UnexpectedInputException("Bus capacity > 0", busTabs.get(i).capacityText);
                }
            } else {
                throw new UnexpectedInputException("Bus capacity must be an integer", busTabs.get(i).capacityText);
            }
            busTabs.get(i).getBus().setCapacity(capacity);

            int tripScheduleTime;
            txt = busTabs.get(i).tripScheduleTimeText.getText();
            if (isInteger(txt)) {
                tripScheduleTime = Integer.parseInt(txt);
                if (tripScheduleTime <= 0 || tripScheduleTime % 30 != 0) {
                    throw new UnexpectedInputException("tripScheduleTime > 0 and tripScheduleTime % 30 = 0", busTabs.get(i).tripScheduleTimeText);
                }
            } else {
                throw new UnexpectedInputException("tripScheduleTime must be an integer", busTabs.get(i).tripScheduleTimeText);
            }
            busTabs.get(i).getBus().setTripScheduleTime(tripScheduleTime);

            int tripDuration;
            txt = busTabs.get(i).tripDurationText.getText();
            if (isInteger(txt)) {
                tripDuration = Integer.parseInt(txt);
                if (tripDuration <= 0 || tripDuration % 30 != 0) {
                    throw new UnexpectedInputException("tripDuration > 0 and tripDuration % 30 = 0", busTabs.get(i).tripDurationText);
                }
            } else {
                throw new UnexpectedInputException("tripDuration must be an integer", busTabs.get(i).tripDurationText);
            }
            busTabs.get(i).getBus().setTripDuration(tripDuration);

            if (busTabs.get(i).getBus().getSource() == null || busTabs.get(i).getBus().getDestination() == null) {
                JComponent faulty[] = {busTabs.get(i).busStatusArea};
                throw new UnexpectedEventException("Bus Source and Destination must be set", faulty);
            }
        }

        //read imune
        txt = imunePeopleText.getText();

        if (isInteger(txt)) {
            imunePeople = Integer.parseInt(imunePeopleText.getText());
            if (imunePeople < 0) {
                throw new UnexpectedInputException("Imune people >= 0", imunePeopleText);
            }

        } else {
            throw new UnexpectedInputException("Imune people must be an integer", imunePeopleText);
        }

        //read infected
        txt = infectedPeopleText.getText();

        if (isInteger(txt)) {
            infectedPeople = Integer.parseInt(infectedPeopleText.getText());
            if (infectedPeople < 1) {
                throw new UnexpectedInputException("Infected people >= 1", infectedPeopleText);
            } else if (imunePeople + infectedPeople > totalPopulation) {
                throw new UnexpectedInputException("(Infected + Imune) people <= people", infectedPeopleText);
            }
        } else {
            throw new UnexpectedInputException("Infected people must be an integer", infectedPeopleText);
        }

        //read protected
        txt = protectedPeopleText.getText();

        if (isInteger(txt)) {
            protectedPeople = Integer.parseInt(protectedPeopleText.getText());
            if (protectedPeople < 0) {
                throw new UnexpectedInputException("Protected people >= 0", protectedPeopleText);

            } else if (protectedPeople > totalPopulation) {
                throw new UnexpectedInputException("Protected people <= people", protectedPeopleText);

            }
        } else {
            throw new UnexpectedInputException("Protected people must be an integer", protectedPeopleText);
        }

        //read personInfectsPersonProbability
        txt = personInfectsPersonProbabilityText.getText();

        if (isInteger(txt)) {
            personInfectsPersonProbability = Integer.parseInt(personInfectsPersonProbabilityText.getText());
            if (personInfectsPersonProbability < 0 || personInfectsPersonProbability > 100) {
                throw new UnexpectedInputException(" 0 <= Probability >= 100 ", personInfectsPersonProbabilityText);
            }
        } else {
            throw new UnexpectedInputException("Probabilities must be an integer", personInfectsPersonProbabilityText);
        }

        //read personInfectsCellProbability
        txt = personInfectsCellProbabilityText.getText();

        if (isInteger(txt)) {
            personInfectsCellProbability = Integer.parseInt(personInfectsCellProbabilityText.getText());
            if (personInfectsCellProbability < 0 || personInfectsCellProbability > 100) {
                throw new UnexpectedInputException(" 0 <= Probability >= 100 ", personInfectsCellProbabilityText);
            }
        } else {
            throw new UnexpectedInputException("Probabilities must be an integer", personInfectsCellProbabilityText);
        }

        //read cellInfectsPersonProbability
        txt = cellInfectsPersonProbabilityText.getText();

        if (isInteger(txt)) {
            cellInfectsPersonProbability = Integer.parseInt(cellInfectsPersonProbabilityText.getText());
            if (cellInfectsPersonProbability < 0 || cellInfectsPersonProbability > 100) {
                throw new UnexpectedInputException(" 0 <= Probability >= 100 ", cellInfectsPersonProbabilityText);
            }

            if (cellInfectsPersonProbability > personInfectsPersonProbability) {
                throw new UnexpectedInputException("Cell Infects Person Probability < Person Infects Person Probability", cellInfectsPersonProbabilityText);
            }

        } else {
            throw new UnexpectedInputException("Probabilities must be an integer", cellInfectsPersonProbabilityText);
        }

        //read labtestfalseProbability
        txt = LabTestFalseProbabilityText.getText();

        if (isInteger(txt)) {
            labTestFalseProbability = Integer.parseInt(txt);
            if (labTestFalseProbability < 0 || labTestFalseProbability > 100) {
                throw new UnexpectedInputException(" 0 <= Probability >= 100 ", LabTestFalseProbabilityText);
            }

        } else {
            throw new UnexpectedInputException("Probabilities must be an integer", LabTestFalseProbabilityText);
        }

        //read simTime
        txt = simTimeText.getText();

        if (isInteger(txt)) {
            simTime = Integer.parseInt(simTimeText.getText());
            if (simTime <= 0 || simTime % 30 != 0) {
                throw new UnexpectedInputException("Times > 0 and Sim Time % 30 = 0", simTimeText);
            }
        } else {
            throw new UnexpectedInputException("Times must be an integer", simTimeText);
        }

        //read personInfectsCellTime
        txt = personInfectsCellTimeText.getText();

        if (isInteger(txt)) {
            personInfectsCellTime = Integer.parseInt(personInfectsCellTimeText.getText());
            if (personInfectsCellTime <= 30 || personInfectsCellTime % 30 != 0) {
                throw new UnexpectedInputException("Person Infects Cell Time >= 60 and Person Infects Cell Time % 30 = 0", personInfectsCellTimeText);
            }
        } else {
            throw new UnexpectedInputException("Times must be an integer", personInfectsCellTimeText);
        }

        //read cellHealingTime
        txt = cellHealingTimeText.getText();

        if (isInteger(txt)) {
            cellHealingTime = Integer.parseInt(cellHealingTimeText.getText());
            if (cellHealingTime <= 0 || cellHealingTime % 30 != 0) {
                throw new UnexpectedInputException("Times > 0 and Cell Healing Time % 30 = 0", cellHealingTimeText);
            }
        } else {
            throw new UnexpectedInputException("Times must be an integer", cellHealingTimeText);
        }

        //read infectedPersonAsymptomaticTime
        txt = infectedPersonAsymptomaticTimeText.getText();

        if (isInteger(txt)) {
            infectedPersonAsymptomaticTime = Integer.parseInt(infectedPersonAsymptomaticTimeText.getText());
            if (infectedPersonAsymptomaticTime < 2 || infectedPersonAsymptomaticTime > 5) {
                throw new UnexpectedInputException(" 2 <= infectedPersonAsymptomaticTime <= 5 ", infectedPersonAsymptomaticTimeText);
            }
        } else {
            throw new UnexpectedInputException("Times must be an integer", infectedPersonAsymptomaticTimeText);
        }

        //read hospitalCureDuration
        txt = hospitalCureDurationText.getText();

        if (isInteger(txt)) {
            hospitalCureDuration = Integer.parseInt(hospitalCureDurationText.getText());
            if (hospitalCureDuration <= 0 || hospitalCureDuration % 30 != 0) {
                throw new UnexpectedInputException("Hospital Cure Duration > 0 and hospitalCureDuration % 30 = 0", hospitalCureDurationText);
            }
        } else {
            throw new UnexpectedInputException("Times must be an integer", hospitalCureDurationText);
        }

        //read update Frame time
        txt = updatePauseTimeText.getText();

        if (isInteger(txt)) {
            updatePauseTime = Integer.parseInt(updatePauseTimeText.getText());
            if (updatePauseTime <= 0) {
                throw new UnexpectedInputException("Times > 0 ", updatePauseTimeText);
            }
        } else {
            throw new UnexpectedInputException("Times must be an integer", updatePauseTimeText);
        }

        //use graphics?
        if (showGraphicsBox.isSelected()) {
            useGraphics = true;
        } else {
            useGraphics = false;
        }
    }
//this method receives a string and returns whether it is a valid integer 

    private boolean isInteger(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        str.trim();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '-') {
                if (str.length() == 1) {
                    return false;
                }
                continue;
            }
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    //getter for the areas amount
    private int getAreasAmount() {
        return areaTabs.size();
    }

    //getter for the bus amount
    private int getBusesAmount() {
        return busTabs.size();
    }

    //When the start button is pressed
    //the simulation starts
    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        //Cant run two simulators simulteniously
        if (isRunning) {
            //pop warning msg
            JOptionPane.showMessageDialog(this, "Simulator already running!", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {

            //catches all parameters exceptions
            try {
                //loads the parameters from the text files
                loadParameters();

                //start
                startSimulation();

            } catch (UnexpectedInputException ex) {
                // colorises the error component
                ex.getFaultyArea().setForeground(Color.red);
                //displayes error msg
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                // sets the color back to black
                ex.getFaultyArea().setForeground(Color.black);

            } catch (UnexpectedEventException ex) {
                //Marking error components
                JComponent faultyComps[] = ex.getFaultyComponents();
                for (int i = 0; i < faultyComps.length; i++) {
                    faultyComps[i].setForeground(Color.red);

                }

                //Throwing error pane
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

                //Unmarking error components
                for (int i = 0; i < faultyComps.length; i++) {
                    faultyComps[i].setForeground(Color.black);

                }
            }
        }
    }//GEN-LAST:event_startButtonActionPerformed

    //when the check box is selected 
    private void showGraphicsBoxItemStateChanged(java.awt.event.ItemEvent evt) {

        if (showGraphicsBox.isSelected()) {
            updatePauseTimeText.setEditable(true);
        } else {
            updatePauseTimeText.setEditable(false);
        }
    }

    //Initializes all gui components and configuration
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        titleLabel = new javax.swing.JLabel();
        startButton = new javax.swing.JButton();
        showGraphicsBox = new javax.swing.JCheckBox();
        updatePauseTimeText = new javax.swing.JTextField();
        updateFrame = new javax.swing.JLabel();
        version = new javax.swing.JLabel();
        developed = new javax.swing.JLabel();
        andreasDemosthenous = new javax.swing.JLabel();
        graphics = new javax.swing.JLabel();
        virusIcon = new javax.swing.JLabel();
        tabbedPane = new javax.swing.JTabbedPane();
        areasPanel = new javax.swing.JPanel();
        addAreaButton = new javax.swing.JButton();
        areaTabbedPanes = new javax.swing.JTabbedPane();
        removeAreaButton = new javax.swing.JButton();
        BusesPanel = new javax.swing.JPanel();
        addBusButton = new javax.swing.JButton();
        BusTabbedPanes = new javax.swing.JTabbedPane();
        removeBusButton = new javax.swing.JButton();
        probabilitiesPanel = new javax.swing.JPanel();
        probabilityParams = new javax.swing.JLabel();
        personInfectsPerson = new javax.swing.JLabel();
        personInfectsPersonProbabilityText = new javax.swing.JTextField();
        persInfectsCellTime = new javax.swing.JLabel();
        personInfectsCellProbabilityText = new javax.swing.JTextField();
        cellInfectsPersTime = new javax.swing.JLabel();
        cellInfectsPersonProbabilityText = new javax.swing.JTextField();
        LabTestFalseProbability = new javax.swing.JLabel();
        LabTestFalseProbabilityText = new javax.swing.JTextField();
        populationPanel = new javax.swing.JPanel();
        populationParams = new javax.swing.JLabel();
        imuneP = new javax.swing.JLabel();
        imunePeopleText = new javax.swing.JTextField();
        InfectedP = new javax.swing.JLabel();
        infectedPeopleText = new javax.swing.JTextField();
        ProtectedP = new javax.swing.JLabel();
        protectedPeopleText = new javax.swing.JTextField();
        timesPanel = new javax.swing.JPanel();
        timeParams = new javax.swing.JLabel();
        personInfectsCell = new javax.swing.JLabel();
        simDuration = new javax.swing.JLabel();
        simTimeText = new javax.swing.JTextField();
        personInfectsCellTimeText = new javax.swing.JTextField();
        cellHealingTimeText = new javax.swing.JTextField();
        cellHealTime = new javax.swing.JLabel();
        InfectedPersonAsymptomaticTime = new javax.swing.JLabel();
        infectedPersonAsymptomaticTimeText = new javax.swing.JTextField();
        hospitalCureDurationLabel = new javax.swing.JLabel();
        hospitalCureDurationText = new javax.swing.JTextField();
        config = new javax.swing.JLabel();
        sep1 = new javax.swing.JSeparator();
        sep2 = new javax.swing.JSeparator();
        sep3 = new javax.swing.JSeparator();
        loader = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Covid19-SIM");
        setAlwaysOnTop(true);
        setFocusable(false);
        setLocation(new java.awt.Point(500, 20));

        titleLabel.setFont(new java.awt.Font("Imprint MT Shadow", 1, 36)); // NOI18N
        titleLabel.setForeground(new java.awt.Color(255, 0, 0));
        titleLabel.setText("COVID-19 SIMULATOR ");

        startButton.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        startButton.setText("START");
        startButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        startButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        startButton.setDoubleBuffered(true);
        startButton.setFocusCycleRoot(true);
        startButton.setFocusTraversalPolicyProvider(true);
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        showGraphicsBox.setFont(new java.awt.Font("Arial", 0, 26)); // NOI18N
        showGraphicsBox.setSelected(true);
        showGraphicsBox.setText("Show Graphics");
        showGraphicsBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                showGraphicsBoxItemStateChanged(evt);
            }
        });

        updatePauseTimeText.setFont(new java.awt.Font("Arial", 0, 26)); // NOI18N
        updatePauseTimeText.setText("1000");

        updateFrame.setFont(new java.awt.Font("Arial", 0, 26)); // NOI18N
        updateFrame.setText("Update Frame (ms)");

        version.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        version.setText("v3.0");

        developed.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        developed.setText("® Developed by");

        andreasDemosthenous.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        andreasDemosthenous.setText(" - Andreas Demosthenous");

        graphics.setFont(new java.awt.Font("Arial", 1, 28)); // NOI18N
        graphics.setText("Graphics");

        virusIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/team15/hw6/Images/virus.png"))); // NOI18N

        tabbedPane.setBackground(new java.awt.Color(0, 0, 0));
        tabbedPane.setForeground(new java.awt.Color(255, 255, 255));
        tabbedPane.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N

        areasPanel.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N

        addAreaButton.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        addAreaButton.setText("Add Area");
        addAreaButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        addAreaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAreaButtonActionPerformed(evt);
            }
        });

        areaTabbedPanes.setBackground(new java.awt.Color(0, 0, 0));
        areaTabbedPanes.setForeground(new java.awt.Color(255, 255, 255));
        areaTabbedPanes.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N

        removeAreaButton.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        removeAreaButton.setText("Remove Area");
        removeAreaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeAreaButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout areasPanelLayout = new javax.swing.GroupLayout(areasPanel);
        areasPanel.setLayout(areasPanelLayout);
        areasPanelLayout.setHorizontalGroup(
            areasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(areasPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(areasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(areasPanelLayout.createSequentialGroup()
                        .addComponent(areaTabbedPanes)
                        .addContainerGap())
                    .addGroup(areasPanelLayout.createSequentialGroup()
                        .addComponent(addAreaButton, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(removeAreaButton, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(21, 21, 21))))
        );
        areasPanelLayout.setVerticalGroup(
            areasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(areasPanelLayout.createSequentialGroup()
                .addComponent(areaTabbedPanes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(areasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addAreaButton)
                    .addComponent(removeAreaButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        tabbedPane.addTab("Areas", areasPanel);

        addBusButton.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        addBusButton.setText("Add Bus");
        addBusButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        addBusButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBusButtonActionPerformed(evt);
            }
        });

        BusTabbedPanes.setBackground(new java.awt.Color(0, 0, 0));
        BusTabbedPanes.setForeground(new java.awt.Color(255, 255, 255));
        BusTabbedPanes.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N

        removeBusButton.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        removeBusButton.setText("Remove Bus");
        removeBusButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeBusButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout BusesPanelLayout = new javax.swing.GroupLayout(BusesPanel);
        BusesPanel.setLayout(BusesPanelLayout);
        BusesPanelLayout.setHorizontalGroup(
            BusesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BusesPanelLayout.createSequentialGroup()
                .addGroup(BusesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(BusesPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(addBusButton, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(removeBusButton, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(BusTabbedPanes))
                .addContainerGap())
        );
        BusesPanelLayout.setVerticalGroup(
            BusesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BusesPanelLayout.createSequentialGroup()
                .addComponent(BusTabbedPanes)
                .addGap(42, 42, 42)
                .addGroup(BusesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(removeBusButton)
                    .addComponent(addBusButton))
                .addContainerGap())
        );

        tabbedPane.addTab("Buses", BusesPanel);

        probabilitiesPanel.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N

        probabilityParams.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        probabilityParams.setText("Probability Parameters (%)");

        personInfectsPerson.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        personInfectsPerson.setText("Infected person infects a healthy person:");

        personInfectsPersonProbabilityText.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        personInfectsPersonProbabilityText.setText("75");

        persInfectsCellTime.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        persInfectsCellTime.setText("Infected person infects a healthy cell:");

        personInfectsCellProbabilityText.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        personInfectsCellProbabilityText.setText("50");

        cellInfectsPersTime.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        cellInfectsPersTime.setText("Infected cell infects a healthy person:");

        cellInfectsPersonProbabilityText.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        cellInfectsPersonProbabilityText.setText("50");

        LabTestFalseProbability.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        LabTestFalseProbability.setText("Lab virus test is false:");

        LabTestFalseProbabilityText.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        LabTestFalseProbabilityText.setText("2");

        javax.swing.GroupLayout probabilitiesPanelLayout = new javax.swing.GroupLayout(probabilitiesPanel);
        probabilitiesPanel.setLayout(probabilitiesPanelLayout);
        probabilitiesPanelLayout.setHorizontalGroup(
            probabilitiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(probabilitiesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(probabilitiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(probabilityParams)
                    .addGroup(probabilitiesPanelLayout.createSequentialGroup()
                        .addComponent(personInfectsPerson)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(personInfectsPersonProbabilityText, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(probabilitiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, probabilitiesPanelLayout.createSequentialGroup()
                            .addComponent(cellInfectsPersTime)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cellInfectsPersonProbabilityText))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, probabilitiesPanelLayout.createSequentialGroup()
                            .addComponent(persInfectsCellTime)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(personInfectsCellProbabilityText, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(probabilitiesPanelLayout.createSequentialGroup()
                        .addComponent(LabTestFalseProbability)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(LabTestFalseProbabilityText, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(177, Short.MAX_VALUE))
        );
        probabilitiesPanelLayout.setVerticalGroup(
            probabilitiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(probabilitiesPanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(probabilityParams)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(probabilitiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(personInfectsPerson)
                    .addComponent(personInfectsPersonProbabilityText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(probabilitiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(persInfectsCellTime)
                    .addComponent(personInfectsCellProbabilityText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(probabilitiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cellInfectsPersTime)
                    .addComponent(cellInfectsPersonProbabilityText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(probabilitiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LabTestFalseProbability)
                    .addComponent(LabTestFalseProbabilityText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 157, Short.MAX_VALUE))
        );

        tabbedPane.addTab("Probabilities", probabilitiesPanel);

        populationPanel.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N

        populationParams.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        populationParams.setText("Population Parameters");

        imuneP.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        imuneP.setText("Imune:");

        imunePeopleText.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        imunePeopleText.setText("5");

        InfectedP.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        InfectedP.setText("Infected:");

        infectedPeopleText.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        infectedPeopleText.setText("5");

        ProtectedP.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        ProtectedP.setText("Protected:");

        protectedPeopleText.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        protectedPeopleText.setText("5");

        javax.swing.GroupLayout populationPanelLayout = new javax.swing.GroupLayout(populationPanel);
        populationPanel.setLayout(populationPanelLayout);
        populationPanelLayout.setHorizontalGroup(
            populationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(populationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(populationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(populationParams)
                    .addGroup(populationPanelLayout.createSequentialGroup()
                        .addComponent(ProtectedP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(protectedPeopleText, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(populationPanelLayout.createSequentialGroup()
                        .addComponent(imuneP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(imunePeopleText, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(populationPanelLayout.createSequentialGroup()
                        .addComponent(InfectedP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(infectedPeopleText, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(298, Short.MAX_VALUE))
        );
        populationPanelLayout.setVerticalGroup(
            populationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(populationPanelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(populationParams)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(populationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(imuneP)
                    .addComponent(imunePeopleText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(populationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(InfectedP)
                    .addComponent(infectedPeopleText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(populationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ProtectedP)
                    .addComponent(protectedPeopleText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(204, Short.MAX_VALUE))
        );

        tabbedPane.addTab("Population", populationPanel);

        timesPanel.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N

        timeParams.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        timeParams.setText("Time Parameters (30 minutes - 1 cycle)");

        personInfectsCell.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        personInfectsCell.setText("Infected person infects a cell(minutes):");

        simDuration.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        simDuration.setText("Simulation duration(minutes): ");

        simTimeText.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        simTimeText.setText("600");

        personInfectsCellTimeText.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        personInfectsCellTimeText.setText("60");

        cellHealingTimeText.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        cellHealingTimeText.setText("90");

        cellHealTime.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        cellHealTime.setText("Infected cell healing time(minutes):");

        InfectedPersonAsymptomaticTime.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        InfectedPersonAsymptomaticTime.setText("Infected person is asymptomatic(days):");

        infectedPersonAsymptomaticTimeText.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        infectedPersonAsymptomaticTimeText.setText("2");

        hospitalCureDurationLabel.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        hospitalCureDurationLabel.setText("Hospital Recovery Duration(minutes)");

        hospitalCureDurationText.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        hospitalCureDurationText.setText("120");

        javax.swing.GroupLayout timesPanelLayout = new javax.swing.GroupLayout(timesPanel);
        timesPanel.setLayout(timesPanelLayout);
        timesPanelLayout.setHorizontalGroup(
            timesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(timesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(timesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(timeParams)
                    .addGroup(timesPanelLayout.createSequentialGroup()
                        .addComponent(personInfectsCell)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(personInfectsCellTimeText, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(timesPanelLayout.createSequentialGroup()
                        .addComponent(simDuration)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(simTimeText, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(timesPanelLayout.createSequentialGroup()
                        .addComponent(cellHealTime)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cellHealingTimeText, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(timesPanelLayout.createSequentialGroup()
                        .addComponent(InfectedPersonAsymptomaticTime)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(infectedPersonAsymptomaticTimeText, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(timesPanelLayout.createSequentialGroup()
                        .addComponent(hospitalCureDurationLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(hospitalCureDurationText, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        timesPanelLayout.setVerticalGroup(
            timesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(timesPanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(timeParams)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(timesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(simTimeText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(simDuration))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(timesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(personInfectsCell)
                    .addComponent(personInfectsCellTimeText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(timesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cellHealTime)
                    .addComponent(cellHealingTimeText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(timesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(InfectedPersonAsymptomaticTime)
                    .addComponent(infectedPersonAsymptomaticTimeText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(timesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hospitalCureDurationLabel)
                    .addComponent(hospitalCureDurationText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 126, Short.MAX_VALUE))
        );

        tabbedPane.addTab("Times", timesPanel);

        config.setFont(new java.awt.Font("Arial", 1, 28)); // NOI18N
        config.setText("Configuration Parameters");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sep1)
            .addComponent(sep2)
            .addComponent(sep3)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addComponent(titleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(66, 66, 66)
                        .addComponent(version))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(updateFrame)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(updatePauseTimeText)
                                .addGap(50, 50, 50))
                            .addComponent(showGraphicsBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(graphics, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(startButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(andreasDemosthenous, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(developed, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGap(68, 68, 68))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(virusIcon)
                                .addGap(24, 24, 24))))
                    .addComponent(loader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tabbedPane)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(config, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(219, 219, 219)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(titleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(version, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sep1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(config, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addComponent(sep2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(graphics, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(showGraphicsBox, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(updateFrame, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(updatePauseTimeText)))
                    .addComponent(virusIcon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sep3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(startButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(developed, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(andreasDemosthenous, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(29, 29, 29)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(loader, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void removeAreaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeAreaButtonActionPerformed
        //Removing the selected area         
        try {

            //Cant have 0 areas
            if (getAreasAmount() <= 1) {
                JComponent faultyComps[] = {removeAreaButton};
                throw new UnexpectedEventException("There must be at least 1 area", faultyComps);

            } else {

                //Removing all the are components                
                areaTabs.get(areaTabbedPanes.getSelectedIndex()).getArea().destroy();
                areaTabs.remove(areaTabbedPanes.getSelectedIndex());
                areaTabbedPanes.removeTabAt(areaTabbedPanes.getSelectedIndex());

            }

        } catch (UnexpectedEventException ex) {
            //Marking error components
            JComponent faultyComps[] = ex.getFaultyComponents();
            for (int i = 0; i < faultyComps.length; i++) {
                faultyComps[i].setForeground(Color.red);

            }

            //Throwing error pane
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

            //Unmarking error components
            for (int i = 0; i < faultyComps.length; i++) {
                faultyComps[i].setForeground(Color.black);

            }
        }

    }//GEN-LAST:event_removeAreaButtonActionPerformed

    private void addAreaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAreaButtonActionPerformed

        areaTabs.add(new AreaTab());

    }//GEN-LAST:event_addAreaButtonActionPerformed

    private void addBusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBusButtonActionPerformed
        busTabs.add(new BusTab());
    }//GEN-LAST:event_addBusButtonActionPerformed

    private void removeBusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeBusButtonActionPerformed
        if (busCounter > 0) {
            //Removing all the are components                
            busTabs.get(BusTabbedPanes.getSelectedIndex()).getBus().destroy();
            busTabs.remove(BusTabbedPanes.getSelectedIndex());
            BusTabbedPanes.removeTabAt(BusTabbedPanes.getSelectedIndex());
        }

    }//GEN-LAST:event_removeBusButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane BusTabbedPanes;
    private javax.swing.JPanel BusesPanel;
    private javax.swing.JLabel InfectedP;
    private javax.swing.JLabel InfectedPersonAsymptomaticTime;
    private javax.swing.JLabel LabTestFalseProbability;
    private javax.swing.JTextField LabTestFalseProbabilityText;
    private javax.swing.JLabel ProtectedP;
    private javax.swing.JButton addAreaButton;
    private javax.swing.JButton addBusButton;
    private javax.swing.JLabel andreasDemosthenous;
    private javax.swing.JTabbedPane areaTabbedPanes;
    private javax.swing.JPanel areasPanel;
    private javax.swing.JLabel cellHealTime;
    private javax.swing.JTextField cellHealingTimeText;
    private javax.swing.JLabel cellInfectsPersTime;
    private javax.swing.JTextField cellInfectsPersonProbabilityText;
    private javax.swing.JLabel config;
    private javax.swing.JLabel developed;
    private javax.swing.JLabel graphics;
    private javax.swing.JLabel hospitalCureDurationLabel;
    private javax.swing.JTextField hospitalCureDurationText;
    private javax.swing.JLabel imuneP;
    private javax.swing.JTextField imunePeopleText;
    private javax.swing.JTextField infectedPeopleText;
    private javax.swing.JTextField infectedPersonAsymptomaticTimeText;
    public static javax.swing.JProgressBar loader;
    private javax.swing.JLabel persInfectsCellTime;
    private javax.swing.JLabel personInfectsCell;
    private javax.swing.JTextField personInfectsCellProbabilityText;
    private javax.swing.JTextField personInfectsCellTimeText;
    private javax.swing.JLabel personInfectsPerson;
    private javax.swing.JTextField personInfectsPersonProbabilityText;
    private javax.swing.JPanel populationPanel;
    private javax.swing.JLabel populationParams;
    private javax.swing.JPanel probabilitiesPanel;
    private javax.swing.JLabel probabilityParams;
    private javax.swing.JTextField protectedPeopleText;
    private javax.swing.JButton removeAreaButton;
    private javax.swing.JButton removeBusButton;
    private javax.swing.JSeparator sep1;
    private javax.swing.JSeparator sep2;
    private javax.swing.JSeparator sep3;
    private javax.swing.JCheckBox showGraphicsBox;
    private javax.swing.JLabel simDuration;
    private javax.swing.JTextField simTimeText;
    private javax.swing.JButton startButton;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JLabel timeParams;
    private javax.swing.JPanel timesPanel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JLabel updateFrame;
    private javax.swing.JTextField updatePauseTimeText;
    private javax.swing.JLabel version;
    private javax.swing.JLabel virusIcon;
    // End of variables declaration//GEN-END:variables
}
