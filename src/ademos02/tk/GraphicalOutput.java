package ademos02.tk;

/**
 * Afti i klasi onomazetai GraphicalOutput.
 *
 * Afti i klasi einai ipefthini gia tin sxediasi tou grid , ton cells kai ton
 * anthropon pou kinunte sto grid.
 *
 *
 * @author Andreas Demosthenous
 * @version 3.0
 * @since 13/05/2020
 */

import edu.princeton.cs.introcs.StdDraw;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 *
 * @author andre
 */
public class GraphicalOutput {

    /**
     *
     */
    public static double penRadius;

    /**
     * Einai mia public sinartisi tis klasis GraphicalOutput.
     *
     * Einai typou void afou den epistrefei kati. Dimiourga ton canva me
     * sigkekrimeno megethos kai sxediazi grammes orizontes kai kathetes gia na
     * dimiourgithi to grid
     *
     * @param size
     * @param color
     *
     */
    public static void InitializeGrid(int size, Color color) {

        StdDraw.clear();
        penRadius = 0.3 / size;

        //Setting Canvas size
        StdDraw.setCanvasSize(400, 400);

        //Setting Scale for X - Y 
        StdDraw.setXscale(-0.1, size + 0.1);
        StdDraw.setYscale(-0.1, size + 0.1);

        //Setting the pen color for the map
        StdDraw.setPenColor(color);
        //Setting the pen radius
        StdDraw.setPenRadius(penRadius);

        //Drawing the grid by drawing one horizontal and one vertical line for each 'Scale' distance.
        //iterating from 0 -> size-1 instead of -1 -> size 
        //so that the grid is a bit smaller than the window
        for (int i = 0; i <= size; i++) {
            //Vertical
            StdDraw.line(i, 0, i, size);
            //Horizontal
            StdDraw.line(0, i, size, i);
        }

    }

    /**
     * Einai mia public sinartisi tis klasis GraphicalOutput.
     *
     * Pernei ena Map kai zografizi ta grids tou map
     *
     *
     * @param map
     */
    public static void InitializeMap(Map map) {
        for (int i = 0; i < map.size(); i++) {
            InitializeGrid(map.get(i).getLength(), map.get(i).getColor());
        }
    }

    /**
     * Einai mia public sinartisi tis klasis GraphicalOutput.
     *
     * Einai typou void afou den epistrefei kati. Anaparista sto sigkekrimeno
     * cell tin apikonisi tu virus
     *
     * @param x
     * @param y
     */
    public static void drawVirus(int x, int y) {

        StdDraw.picture(x + 0.5, y + 0.5, "Images/virus.png", 0.75, 0.75);

    }

    /**
     * Einai mia public sinartisi tis klasis GraphicalOutput.
     *
     * Einai void method afu den epistrefei kati. Vazi tin eikona sinoriakou
     * simiou sto grid kai zografizi to cell me to xroma tis perioxis pou
     * sinorevei
     *
     * @param x
     * @param y
     * @param col
     */
    public static void drawBorderCell(int x, int y, Color col) {
        StdDraw.setPenColor(col);
        StdDraw.filledSquare(x + 0.5, y + 0.5, 0.42);
        StdDraw.picture(x + 0.5, y + 0.5, "Images/transport.jpg", 0.70, 0.70);

    }

    /**
     * a method responsible for drawing a quarantine cell on the grid
     * 
     * @param x
     * @param y
     * @param col - red = the quarantine color
     */
    public static void drawQuarantineCell(int x, int y, Color col) {
        StdDraw.setPenColor(col);
        StdDraw.setPenRadius(penRadius / 3);
        StdDraw.square(x + 0.5, y + 0.5, 0.42);

    }

    /**
     * Einai mia public sinartisi tis klasis GraphicalOutput.
     *
     * Einai typou void afou den epistrefei kati. Analoga me to person an einai
     * Vulnerable eite Imune eite Infected kai protected sxediazi tin analogi
     * eikona
     *
     * @param x
     * @param y
     * @param p
     */
    public static void drawPerson(int x, int y, Person p) {
        //Drawing the person according its current state
        
        if (p.isTravelling()) {
            return;
        }

        if (!p.isVulnerable()) {
            StdDraw.picture(x + 0.5, y + 0.5, "Images/Imune.png", 0.80, 0.80);

        } else {
            Vulnerable vulnP = (Vulnerable) p;
            if (vulnP.isKnowinglyInfected()) {
                StdDraw.picture(x + 0.5, y + 0.5, "Images/infected.png", 0.80, 0.80);

            } else if (vulnP.isInfected()) {
                StdDraw.picture(x + 0.5, y + 0.5, "Images/infectedAsymptomatic.png", 0.80, 0.80);

            } else {
                StdDraw.picture(x + 0.5, y + 0.5, "Images/healthy.png", 0.80, 0.80);

            }

            if (vulnP.isSeverelySick()) {
                StdDraw.picture(x + 0.3, y + 0.7 , "Images/severlySick.png", 0.40, 0.40);
            }
        }

        if (p.isProtected()) {
            StdDraw.picture(x + 0.7, y + 0.7, "Images/shield.png", 0.55, 0.55);
        }

    }

    /**
     * Einai mia public sinartisi tis klasis GraphicalOutput.
     *
     * Einai typou void afou den epistrefei kati. Xromatizi aspro to cell gia na
     * fenete oti einai adeio
     *
     * @param x
     * @param y
     */
    public static void clearCell(int x, int y) {
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.filledSquare(x + 0.5, y + 0.5, 0.42);
    }

    /**
     * Einai mia public sinartisi tis klasis GraphicalOutput.
     *
     * Pernei mia eikona kai tis allazi to size tis analoga me to height kai to
     * width
     *
     * @param img
     * @param width
     * @param height
     * @return Epistrefei tin eikona metasximatismeni
     */
    public static BufferedImage resize(BufferedImage img, int height, int width) {
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }

}
