package ass2.spec;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


/**
 * COMMENT: Comment HeightMap 
 *
 * @author malcolmr
 */
public class Terrain {

    static MyTexture texture;
    static MyTexture sunTexture;
    private Dimension mySize;
    private double[][] myAltitude;
    private List<Tree> myTrees;
    private List<Road> myRoads;
    private List<Tardis> myTardis;
    private float[] mySunlight;

    Avatar myAvatar;
    private boolean isNightTime;
    private boolean sunPositionChanged;
    private int currentSunRotation;



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
        mySunlight = new float[4];
        mySunlight[3] = 0; //directional light
        isNightTime = false;
        sunPositionChanged = false;
        currentSunRotation = 0;
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

    public void setNigthTime(boolean nigthTime){isNightTime = nigthTime;}
    public boolean isNightTime(){return isNightTime;}
    public boolean isSunPositionChanged(){return sunPositionChanged;}
    public void setSunPositionChanged(boolean changed){ sunPositionChanged = changed;}
    public int getCurrentSunRotation(){return currentSunRotation;}
    public void setCurrentSunRotation(int rotation){currentSunRotation = rotation;}


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

    public void setMyAvatar(Avatar avatar){
        myAvatar = avatar;
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

        float[] n = MathUtils.normal(p,q,r);
        double altitude = -(n[2]*(z - p[2])+n[0]*(x-p[0]))/n[1] + p[1];
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

    public void draw(GL2 gl) {
        drawTerrain(gl);
        for (Tree t : trees()) t.draw(gl);
        for (Road r : roads()) drawRoad(gl, r);
        if(!isNightTime)drawSun(gl);
        for (Tardis t : myTardis) t.draw(gl, isNightTime);

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
        float[] white = {1f, 1.0f, 1f, 1.0f};
        float[] a, b, c, d;
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, white, 0);

        // Specify how texture values combine with current surface color values.
        //gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, texture.getTextureId());
        for (int z = 0 ; z < size().height-1; z++)
            for (int x = 0; x < size().width-1; x++) {
                a = new float[]{x, (float)getGridAltitude(x, z), z};
                b = new float[]{x, (float)getGridAltitude(x, z+1), z+1};
                c = new float[]{x+1, (float)getGridAltitude(x+1, z), z};
                d = new float[]{x+1, (float)getGridAltitude(x+1, z+1), z+1};

                gl.glBegin(GL2.GL_TRIANGLES);{
                    gl.glNormal3fv(MathUtils.normal(a, b, c), 0);
                    gl.glTexCoord2d(0.0, 1.0);gl.glVertex3fv(a, 0);
                    gl.glTexCoord2d(0.0, 0.0);gl.glVertex3fv(b, 0);
                    gl.glTexCoord2d(1.0, 1.0);gl.glVertex3fv(c, 0);

                    gl.glNormal3fv(MathUtils.normal(b, d, c), 0);
                    gl.glTexCoord2d(0.0, 0.0);gl.glVertex3fv(b, 0);
                    gl.glTexCoord2d(1.0, 0.0);gl.glVertex3fv(d, 0);
                    gl.glTexCoord2d(1.0, 1.0);gl.glVertex3fv(c, 0);

                }gl.glEnd();
            }
        gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
    }

    /**
     * Draws a given road.
     * Roads don't draw themselves because they need information about the terrain.
     * @param gl
     * @param road
     */
    private void drawRoad(GL2 gl, Road road) {
        float[] white = {1.0f, 1.0f, 1.0f, 1.0f};
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, white, 0);
        double x, z, y, x1, z1;
        double w = road.width()/2;

        int numPoints = 16;
        double tIncrement = 1.0/numPoints;
        double t, t1;
        double[] normal = null;

        gl.glBindTexture(GL2.GL_TEXTURE_2D, Road.texture.getTextureId());
        //gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
        // dealing with z-fighting
        gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
        gl.glPolygonOffset(-1f, -1f);
        gl.glBegin(GL2.GL_QUAD_STRIP);
        for(int i = 0; i < numPoints*road.size(); i++){
            t = i*tIncrement;
            t1 = (i+1)*tIncrement;
            //double[] normal = road.normal(t);
            //double[] normal1 = road.normal(t1);
            x = road.point(t)[0]; z = road.point(t)[1];
            // If not last point, estimate new normal. Otherwise, use last normal.
            if (i != numPoints*road.size()-1) {
                x1 = road.point(t1)[0]; z1= road.point(t1)[1];
                normal = MathUtils.normal2d(new double[]{x - x1, z - z1});
            }

            y = altitude(x,z);
            gl.glNormal3d(0, 1, 0);
            gl.glTexCoord2d(0,i%2);gl.glVertex3d(x+w*normal[0], y, z + w * normal[1]);
            gl.glTexCoord2d(1,i%2);gl.glVertex3d(x-w*normal[0], y, z-w*normal[1]);
            //gl.glVertex3d(x1-w*normal1[0], y, z1-w*normal1[1]);
            //gl.glVertex3d(x1+w*normal1[0], y, z1+w*normal1[1]);
        }
        gl.glEnd();
        gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
        gl.glDisable(GL2.GL_POLYGON_OFFSET_FILL);
        //gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        //Connect to the final point - we just get the final control point
        //double[] endPoint = road.controlPoint(road.size()*3);
        //gl.glVertex3d(endPoint[0], altitude(endPoint[0], endPoint[1]),endPoint[1]);
    }

    private void drawSun(GL2 gl){
        float[] white = {1f, 1f, 1f, 1.0f};

        gl.glPushMatrix();
        gl.glTranslated(mySunlight[0],mySunlight[1]+3,mySunlight[2]);

        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, white, 0);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, sunTexture.getTextureId());
        gl.glMatrixMode(GL2.GL_TEXTURE);
        gl.glLoadIdentity();
        gl.glScalef(5, 5, 1);
        GLU glu = new GLU();
        GLUquadric quad = glu.gluNewQuadric();
        glu.gluQuadricTexture(quad, true);
        glu.gluSphere(quad, 0.4, 15, 15);
        glu.gluDeleteQuadric(quad);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
        gl.glLoadIdentity();
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        gl.glPopMatrix();
    }

}
