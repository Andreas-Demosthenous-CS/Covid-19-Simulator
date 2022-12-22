package ademos02.tk;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

/**
 * This class is responsible for handling the areas representation
 * during a graphical simulation.
 * 
 * @author Andreas Demosthenous
 * @version 3.0
 * @since 13/05/2020
 */
public class SimulationFrame extends JFrame {

    //default frame size for areas
    private int gridGraphicalWidth = 600;
    private int gridGraphicalLength = 610;
    //list of grids
    private Map map;
    //amount of areas
    private int areasAmount;
    
    private BufferedImage GridImages[];
    //Components
    private JLabel[] gridLabels;
    private JPanel titlesPanel;
    private JPanel gridsPanel;
    private JScrollPane gridsScroller;
    private JLabel minsPassedLabel;
    private JLabel newCasesLabel;
    private JLabel title;
    private JButton increaseSize;
    private JButton decreaseSize;

    /**
     * Einai kataskevastis tis klasis SimulationFrame.
     *
     * Pernei san parametro ena antikeimeno Map Arxikopiei ta components tou
     * parathirou kai ta pedia map,areasAmount,gridLabels.
     *
     * @param map
     *
     */
    public SimulationFrame(Map map) {
        
        this.map = map;
        areasAmount = map.size();

        //Initializing components
        initComponents();
        setVisible(true);
        
    }

    //This method inits the frame components
    private void initComponents() {
        
        GridImages = new BufferedImage[areasAmount];
        gridLabels = new JLabel[areasAmount];
        titlesPanel = new javax.swing.JPanel();
        title = new javax.swing.JLabel();
        minsPassedLabel = new javax.swing.JLabel();
        newCasesLabel = new javax.swing.JLabel();
        gridsScroller = new javax.swing.JScrollPane();
        gridsPanel = new javax.swing.JPanel();

        //Setting window params
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(gridGraphicalWidth * 2 + 45, gridGraphicalLength + 220));
        setAlwaysOnTop(true);
        setTitle("Graphical sim");
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));

        //Config title's + info panel                
        titlesPanel.setBounds(0, 0, 1090, 100);
        titlesPanel.setMinimumSize(new Dimension(1090, 130));
        titlesPanel.setPreferredSize(new Dimension(1090, 130));
        titlesPanel.setLayout(null);
        title.setFont(new java.awt.Font("Imprint MT Shadow", 1, 36));
        title.setText("Graphical Simulation");
        title.setForeground(Color.RED);
        titlesPanel.add(title);
        title.setBounds(330, 10, 550, 40);
        getContentPane().add(titlesPanel);

        //Config new cases label
        newCasesLabel.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        newCasesLabel.setText("New cases: ");
        newCasesLabel.setBounds(10, 20, 370, 32);
        titlesPanel.add(newCasesLabel);

        //Config mins passed label
        minsPassedLabel.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        minsPassedLabel.setText("Minutes passed: ");
        minsPassedLabel.setBounds(10, 50, 370, 32);
        titlesPanel.add(minsPassedLabel);

        //Config increase size button
        increaseSize = new JButton();
        increaseSize.setBounds(10, 82, 200, 40);
        increaseSize.setText(" Zoom in ");
        increaseSize.setFont(new Font("Arial", Font.BOLD, 25));
        increaseSize.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        increaseSize.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (gridGraphicalWidth <= 750) {
                    gridGraphicalWidth += 50;
                    gridGraphicalLength = gridGraphicalWidth + 10;
                    resizeComponents();
                    decreaseSize.setEnabled(true);
                } else {
                    increaseSize.setEnabled(false);
                }
                
            }
        });
        titlesPanel.add(increaseSize);

        //Config decrease size button
        decreaseSize = new JButton();
        decreaseSize.setBounds(210, 82, 200, 40);
        decreaseSize.setText(" Zoom out ");
        decreaseSize.setFont(new Font("Arial", Font.BOLD, 25));
        decreaseSize.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        decreaseSize.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                
                if (gridGraphicalWidth >= 200) {
                    gridGraphicalWidth -= 50;
                    gridGraphicalLength = gridGraphicalWidth + 10;
                    resizeComponents();
                    increaseSize.setEnabled(true);
                } else {
                    decreaseSize.setEnabled(false);
                    
                }
            }
        });
        titlesPanel.add(decreaseSize);

        //Config the grids panel
        gridsPanel.setLayout(new BoxLayout(gridsPanel, BoxLayout.LINE_AXIS));
        gridsPanel.setBackground(new Color(200, 200, 200));

        //Config a scroller for the grids
        gridsScroller.setViewportView(gridsPanel);
        gridsScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        gridsScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        getContentPane().add(gridsScroller);

        //Setting the panel+scroller sizes
        if (areasAmount <= 3) {
            gridsScroller.setPreferredSize(new java.awt.Dimension(gridGraphicalWidth * areasAmount + 10, gridGraphicalLength + 50));
            gridsPanel.setPreferredSize(new java.awt.Dimension(gridGraphicalWidth * (areasAmount) + 10, gridGraphicalLength + 50));
            gridsScroller.setMinimumSize(new java.awt.Dimension(gridGraphicalWidth * (areasAmount + 10), gridGraphicalLength + 50));
            gridsPanel.setMinimumSize(new java.awt.Dimension(gridGraphicalWidth * (areasAmount) + 10, gridGraphicalLength + 50));
            
        } else {
            gridsScroller.setPreferredSize(new java.awt.Dimension(gridGraphicalWidth * (3), gridGraphicalLength + 50));
            gridsPanel.setPreferredSize(new java.awt.Dimension((gridGraphicalWidth + 10) * (areasAmount), gridGraphicalLength + 50));
            gridsScroller.setMinimumSize(new java.awt.Dimension(gridGraphicalWidth * (3), gridGraphicalLength + 50));
            gridsPanel.setMinimumSize(new java.awt.Dimension((gridGraphicalWidth + 10) * (areasAmount), gridGraphicalLength + 50));
        }

        //Config the grid labels
        for (int i = 0; i < areasAmount; i++) {
            
            gridLabels[i] = new JLabel();
            gridLabels[i].setFont(new Font("Arial", 1, 24));
            gridLabels[i].setForeground(map.get(i).getColor());
            gridLabels[i].setText(map.get(i).getName()+" Hospital: "+map.get(i).getHospital().getCurrentCapacity()+"/"+map.get(i).getHospital().getCapacity());
            gridLabels[i].setPreferredSize(new Dimension(gridGraphicalWidth + 30, gridGraphicalLength));
            gridLabels[i].setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            gridLabels[i].setVerticalTextPosition(javax.swing.SwingConstants.TOP);
            gridsPanel.add(gridLabels[i]);
            
        }
        pack();
    }

    //this method resizes the frame components according to
    //the graphicalWidth and graphical Length
    private void resizeComponents() {

        //resizing the window
        setMinimumSize(new java.awt.Dimension(gridGraphicalWidth * 2 + 45, gridGraphicalLength + 220));
        setSize(getMinimumSize());

        //resizing the labels
        for (int i = 0; i < areasAmount; i++) {
            GridImages[i] = GraphicalOutput.resize(GridImages[i], gridGraphicalWidth, gridGraphicalLength);
            gridLabels[i].setIcon(new ImageIcon(GridImages[i]));
            gridLabels[i].setPreferredSize(new Dimension(gridGraphicalWidth + 30, gridGraphicalLength));
        }

        //Resizing the panel + scroller
        if (areasAmount <= 3) {
            
            gridsScroller.setPreferredSize(new java.awt.Dimension(gridGraphicalWidth * areasAmount + 10, gridGraphicalLength + 50));
            gridsPanel.setPreferredSize(new java.awt.Dimension(gridGraphicalWidth * (areasAmount) + 10, gridGraphicalLength + 50));
            gridsScroller.setMinimumSize(new java.awt.Dimension(gridGraphicalWidth * (areasAmount + 10), gridGraphicalLength + 50));
            gridsPanel.setMinimumSize(new java.awt.Dimension(gridGraphicalWidth * (areasAmount) + 10, gridGraphicalLength + 50));
            
        } else {
            
            gridsScroller.setPreferredSize(new java.awt.Dimension(gridGraphicalWidth * (3), gridGraphicalLength + 50));
            gridsPanel.setPreferredSize(new java.awt.Dimension((gridGraphicalWidth + 10) * (areasAmount), gridGraphicalLength + 50));
            gridsScroller.setMinimumSize(new java.awt.Dimension(gridGraphicalWidth * (3), gridGraphicalLength + 50));
            gridsPanel.setMinimumSize(new java.awt.Dimension((gridGraphicalWidth + 10) * (areasAmount), gridGraphicalLength + 50));
            
        }
    }

    /**
     * Einai mia public sinartisi tis klasis SimulationFrame.
     *
     * Eina void afu den epistrefei kati.
     *
     * @param simTime
     * @param infections
     * @throws java.io.IOException
     *
     */
    public void updateFrame(int simTime, int infections) throws IOException {

        //updating the info labels
        minsPassedLabel.setText(" Minutes passed: " + simTime);
        newCasesLabel.setText(" New cases: " + infections);

        //Updating the grid labels(grid pictures)
        for (int i = 0; i < areasAmount; i++) {
            //Creating the file with the expected name
            File areaPic = new File("Area" + i + ".png");
            //Reaading the image from the file
            GridImages[i] = ImageIO.read(areaPic);
            //Resizing the image
            GridImages[i] = GraphicalOutput.resize(GridImages[i], gridGraphicalWidth, gridGraphicalLength);
            //Loading the image on the label
            gridLabels[i].setIcon(new ImageIcon(GridImages[i]));
            //updating the label text
            gridLabels[i].setText(map.get(i).getName()+"\t\t, Hospital: "+map.get(i).getHospital().getCurrentCapacity()+"/"+map.get(i).getHospital().getCapacity());
            //deleteing the temporary picture from the file system 
            areaPic.delete();
        }
    }
    
}
