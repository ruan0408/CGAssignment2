package ass2.spec;

import javax.media.opengl.GL2;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;


/**
 * COMMENT: Comment HeightMap 
 *
 * @author malcolmr
 */
public class Terrain implements KeyListener {

    private static final String TERRAIN_TEXT = "/res/dirtGrass.jpg";
    private static final String TERRAIN_TEXT_EXT = "jpg";

    static MyTexture texture;

    private Dimension mySize;
    private double[][] myAltitude;
    private List<Tree> myTrees;
    private List<Road> myRoads;
    private List<Tardis> myTardis;
    private Sun sun;

    Avatar avatar;
    private boolean isNight;

    public static void loadStaticData(GL2 gl) {
        texture = new MyTexture(gl,TERRAIN_TEXT,TERRAIN_TEXT_EXT,true);
    }

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
        myTardis = new ArrayList<Tardis>();
        isNight = false;
    }

    public Terrain(Dimension size) {this(size.width, size.height);}

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_N:
                setIsNight(!isNight());
                break;
            case KeyEvent.VK_T:
                sun.moveForward();
                break;
            case KeyEvent.VK_R:
                sun.moveBackwards();
                break;
            default:
                break;
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
    public Dimension size() {
        return mySize;
    }
    public List<Tree> trees() {
        return myTrees;
    }
    public List<Road> roads() {
        return myRoads;
    }
    public float[] getSunlight() {return sun.getDirectionHomogeneous();}

    /** Sunlight is a vector pointing to the sun. */
    public float[] getSunDirectionHomogeneous() {return sun.getDirectionHomogeneous();}
    public void setIsNight(boolean b){isNight = b;}
    public boolean isNight(){return isNight;}
    public float[] getSunlightColor(){return sun.getLightColor();}
    public void setAvatar(Avatar a){
        avatar = a;
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
    public void setSunlightDir(float dx, float dy, float dz) {sun = new Sun(dx, dy, dz);}

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

    public void addTardis(double x, double z){
        double y = altitude(x,z);
        Tardis tardis = new Tardis(x,y,z);
        myTardis.add(tardis);
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
     * Ruan: We are using triangle barycenter here. Check out wikipedia...
     * To decide in which triangle the point (x,z) is, we find the closest corner.
     *   (x1,z1) -- (x1,z1+1)
     *      |       /     |
     *      |      /      |
     *      |     /       |
     *   (x1+1,z1)--(x1+1,z1+1)
     * @param x
     * @param z
     * @return
     */
    public double altitude(double x, double z) {
        if (x < 0 || z < 0 || x+1 >= mySize.getWidth() || z+1 >= mySize.getHeight()) return 0;
        float[] p,q,r;
        int x1 = (int) x;
        int z1 = (int) z;

        double dist1 = (x-x1)*(x-x1)+(z-z1)*(z-z1);
        double dist2 = (x1+1-x)*(x1+1-x)+(z1+1-z)*(z1+1-z);

        if (dist1 < dist2) {
            p = new float[]{x1, (float)getGridAltitude(x1, z1), z1};
            q = new float[]{x1, (float)getGridAltitude(x1, z1+1), z1+1};
            r = new float[]{x1+1, (float)getGridAltitude(x1+1, z1), z1};
        } else {
            p = new float[]{x1+1, (float)getGridAltitude(x1+1, z1+1), z1+1};
            q = new float[]{x1+1, (float)getGridAltitude(x1+1, z1), z1};
            r = new float[]{x1, (float)getGridAltitude(x1, z1+1), z1+1};
        }

        float[] n = Utils.normal(p, q, r);
        double altitude = -(n[2]*(z - p[2])+n[0]*(x-p[0]))/n[1] + p[1];
        return altitude;
    }

    public void drawScene(GL2 gl) {
        draw(gl);
        for (Tree t : trees()) t.draw(gl);
        for (Road r : roads()) r.draw(gl, this);
        for (Tardis t : myTardis) t.draw(gl);
        avatar.draw(gl);
        if (!isNight()) sun.draw(gl);

    }

    /**
     * Draws the terrain. Each group of 4 vertices are labeled as follows:
     *   a -- c
     *   |  / |
     *   | /  |
     *   b -- d
     * @param gl
     */

    private void draw(GL2 gl) {
        float[] a, b, c, d;
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, Utils.LIGHT_FULL, 0);

        gl.glBindTexture(GL2.GL_TEXTURE_2D, texture.getTextureId());
        for (int z = 0 ; z < size().height-1; z++)
            for (int x = 0; x < size().width-1; x++) {
                a = new float[]{x, (float)getGridAltitude(x, z), z};
                b = new float[]{x, (float)getGridAltitude(x, z+1), z+1};
                c = new float[]{x+1, (float)getGridAltitude(x+1, z), z};
                d = new float[]{x+1, (float)getGridAltitude(x+1, z+1), z+1};

                gl.glBegin(GL2.GL_TRIANGLES);{
                    gl.glNormal3fv(Utils.normal(a, b, c), 0);
                    gl.glTexCoord2d(0.0, 1.0);gl.glVertex3fv(a, 0);
                    gl.glTexCoord2d(0.0, 0.0);gl.glVertex3fv(b, 0);
                    gl.glTexCoord2d(1.0, 1.0);gl.glVertex3fv(c, 0);

                    gl.glNormal3fv(Utils.normal(b, d, c), 0);
                    gl.glTexCoord2d(0.0, 0.0);gl.glVertex3fv(b, 0);
                    gl.glTexCoord2d(1.0, 0.0);gl.glVertex3fv(d, 0);
                    gl.glTexCoord2d(1.0, 1.0);gl.glVertex3fv(c, 0);

                }gl.glEnd();
            }
        gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
    }
}
