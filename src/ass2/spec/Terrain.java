package ass2.spec;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;


/**
 * COMMENT: Comment HeightMap 
 *
 * @author malcolmr
 */
public class Terrain {

    private Dimension mySize;
    private double[][] myAltitude;
    private List<Tree> myTrees;
    private List<Road> myRoads;
    private float[] mySunlight;

    /**
     * Create a new terrain
     *
     * @param width The number of vertices in the x-direction
     * @param depth The number of vertices in the z-direction
     */
    public Terrain(int width, int depth) {
        mySize = new Dimension(width, depth);
        myAltitude = new double[width][depth];
        myTrees = new ArrayList<Tree>();
        myRoads = new ArrayList<Road>();
        mySunlight = new float[4];
        mySunlight[3] = 0; //directional light
    }
    
    public Terrain(Dimension size) {
        this(size.width, size.height);
    }

    public Dimension size() {
        return mySize;
    }

    public List<Tree> trees() {
        return myTrees;
    }

    public List<Road> roads() {
        return myRoads;
    }

    public float[] getSunlight() {
        return mySunlight;
    }

    /**
     * Set the sunlight direction. 
     * 
     * Note: the sun should be treated as a directional light, without a position
     * 
     * @param dx
     * @param dy
     * @param dz
     */
    public void setSunlightDir(float dx, float dy, float dz) {
        mySunlight[0] = dx;
        mySunlight[1] = dy;
        mySunlight[2] = dz;        
    }
    
    /**
     * Resize the terrain, copying any old altitudes. 
     * 
     * @param width
     * @param height
     */
    public void setSize(int width, int height) {
        mySize = new Dimension(width, height);
        double[][] oldAlt = myAltitude;
        myAltitude = new double[width][height];
        
        for (int i = 0; i < width && i < oldAlt.length; i++) {
            for (int j = 0; j < height && j < oldAlt[i].length; j++) {
                myAltitude[i][j] = oldAlt[i][j];
            }
        }
    }

    /**
     * Get the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public double getGridAltitude(int x, int z) {
        return myAltitude[x][z];
    }

    /**
     * Set the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public void setGridAltitude(int x, int z, double h) {
        myAltitude[x][z] = h;
    }

    /**
     * Get the altitude at an arbitrary point. 
     * Non-integer points should be interpolated from neighbouring grid points
     * 
     * Ruan: We are using bilinear interpolation here. Check out wikipedia...
     * 
     * @param x
     * @param z
     * @return
     */
    public double altitude(double x, double z) {
        double altitude = 0;
        int x1 = (int) x;
        int z1 = (int) z;
        double fxz1 = (x1+1 - x)*getGridAltitude(x1, z1) +(x-x1)*getGridAltitude(x1+1, z1);
        double fxzPlus1 = (x1+1 - x)*getGridAltitude(x1, z1+1) +(x-x1)*getGridAltitude(x1+1, z1+1);

        altitude = (z1+1 - z)*fxz1 + (z - z1)*fxzPlus1;
        return altitude;
    }

    /**
     * Add a tree at the specified (x,z) point. 
     * The tree's y coordinate is calculated from the altitude of the terrain at that point.
     * 
     * @param x
     * @param z
     */
    public void addTree(double x, double z) {
        double y = altitude(x, z);
        Tree tree = new Tree(x, y, z);
        myTrees.add(tree);
    }


    /**
     * Add a road. 
     * 
     * @param width
     * @param spine
     */
    public void addRoad(double width, double[] spine) {
        Road road = new Road(width, spine);
        myRoads.add(road);        
    }

    public void draw(GL2 gl) {
        drawTerrain(gl);
        for (Tree t : trees()) t.draw(gl);
        for (Road r : roads()) drawRoad(gl, r);
    }

    /**
     * Draws the terrain. Each group of 4 vertices are labeled as follows:
     *   a -- c
     *   |  / |
     *   | /  |
     *   b -- d
     * @param gl
     */

    private void drawTerrain(GL2 gl) {
        float[] green = {0.0f, 1.0f, 0.0f, 1.0f};
        float[] a, b, c, d;
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, green, 0);

        //gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        for (int z = 0 ; z < size().height-1; z++)
            for (int x = 0; x < size().width-1; x++) {
                a = new float[]{x, (float)getGridAltitude(x, z), z};
                b = new float[]{x, (float)getGridAltitude(x, z+1), z+1};
                c = new float[]{x+1, (float)getGridAltitude(x+1, z), z};
                d = new float[]{x+1, (float)getGridAltitude(x+1, z+1), z+1};

                gl.glBegin(GL2.GL_TRIANGLES);{

                    gl.glNormal3fv(MathUtils.normal(a, b, c), 0);
                    gl.glVertex3fv(a, 0);
                    gl.glVertex3fv(b, 0);
                    gl.glVertex3fv(c, 0);

                    gl.glNormal3fv(MathUtils.normal(b, d, c), 0);
                    gl.glVertex3fv(b, 0);
                    gl.glVertex3fv(d, 0);
                    gl.glVertex3fv(c, 0);

                }gl.glEnd();
            }
        //gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
    }

    /**
     * Draws a given road.
     * Roads don't draw themselves because they need information about the terrain.
     * This is not pretty.
     * @param gl
     * @param road
     */
    private void drawRoad(GL2 gl, Road road) {

        float[] black = {0.0f, 0.0f, 0.0f, 1.0f};
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, black, 0);

        double x, z;
        gl.glBegin(GL2.GL_LINE_STRIP);
        //gl.glBegin(GL2.GL_POINTS);
        int numPoints = 16;
        double tIncrement = 1.0/numPoints;
        //double tIncrement = ((double)curve.size())/numPoints;
        //System.out.println("numPoints " + numPoints + " " + tIncrement);
        for(int i = 0; i < numPoints*road.size(); i++){
            double t = i*tIncrement;
            //System.out.println("t " + t);
            gl.glNormal3d(0,1,0);
            x = road.point(t)[0];
            z = road.point(t)[1];
            gl.glVertex3d(x, altitude(x, z), z);
        }
        //Connect to the final point - we just get the final control
        //point
        //gl.glVertex2dv(controlPoint(size()*3),0);
        gl.glEnd();
    }
}
