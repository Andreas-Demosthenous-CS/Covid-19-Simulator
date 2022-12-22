package ademos02.tk;

/**
 * This is a Frame for representing the buses and their status during the
 * simulation
 *
 * @author Andreas Demosthenous
 * @version 3.0
 * @since 13/05/2020
 */
public class BusesStatusFrame extends javax.swing.JFrame {

    //the station of the simulation
    private BusStation station;

    /**
     * The constructor
     *
     * @param station
     */
    public BusesStatusFrame(BusStation station) {

        this.station = station;

        //Initializing the components
        initComponents();

        this.setVisible(true);
    }

    /**
     * This method is responsible for updating the text area with the current
     * information taken from our station
     *
     */
    public void updateStatus() {
        statusArea.setText("\n");
        statusArea.setTabSize(2);
        for (int i = 0; i < station.size(); i++) {
            statusArea.append("\t" + (i + 1) + ". " + station.get(i).getName());
            if (!station.get(i).isTravelling()) {
                statusArea.append("\t|\t" + station.get(i).getSource());
            } else {
                statusArea.append("\t|\t" + station.get(i).getSource() + "  -->  " + station.get(i).getDestination());
            }
            if (station.get(i).isTravelling()) {
                statusArea.append("\t|\t TRAVELING");
            } else {
                statusArea.append("\t|\t WAITING");
            }
            statusArea.append("\t|\t" + station.get(i).getCurrentCapacity() + "/" + station.get(i).getMaxCapacity() + "\n");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new javax.swing.JPanel();
        busStatusLabel = new javax.swing.JLabel();
        scroller = new javax.swing.JScrollPane();
        statusArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Buses status Frame");
        setAlwaysOnTop(true);
        setLocation(new java.awt.Point(1250, 0));

        busStatusLabel.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        busStatusLabel.setText("Bus status");

        statusArea.setEditable(false);
        statusArea.setColumns(20);
        statusArea.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        statusArea.setRows(5);
        scroller.setViewportView(statusArea);

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelLayout.createSequentialGroup()
                        .addGap(215, 215, 215)
                        .addComponent(busStatusLabel))
                    .addGroup(panelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(scroller, javax.swing.GroupLayout.PREFERRED_SIZE, 590, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(busStatusLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scroller, javax.swing.GroupLayout.DEFAULT_SIZE, 693, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel busStatusLabel;
    private javax.swing.JPanel panel;
    private javax.swing.JScrollPane scroller;
    private javax.swing.JTextArea statusArea;
    // End of variables declaration//GEN-END:variables
}
