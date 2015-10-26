package ass2.spec;

import javax.media.opengl.GL2;
import java.util.ArrayList;
import java.util.List;

/**
 * COMMENT: Comment Road 
 *
 * @author malcolmr
 */
public class Road {
    private static final String ROAD_TEXT = "/res/asphalt.jpg";
    private static final String ROAD_TEXT_EXT = "jpg";

    static MyTexture texture;
    private List<Double> myPoints;
    private double myWidth;

    public static void loadStaticData(GL2 gl) {
        texture =  new MyTexture(gl, ROAD_TEXT, ROAD_TEXT_EXT, true);
    }
    
    /** 
     * Create a new road starting at the specified point
     */
    public Road(double width, double x0, double y0) {
        myWidth = width;
        myPoints = new ArrayList<Double>();
        myPoints.add(x0);
        myPoints.add(y0);
    }

    /**
     * Create a new road with the specified spine 
     *
     * @param width
     * @param spine
     */
    public Road(double width, double[] spine) {
        myWidth = width;
        myPoints = new ArrayList<Double>();
        for (int i = 0; i < spine.length; i++) {
            myPoints.add(spine[i]);
        }
    }

    /**
     * The width of the road.
     * 
     * @return
     */
    public double width() {
        return myWidth;
    }

    /**
     * Add a new segment of road, beginning at the last point added and ending at (x3, y3).
     * (x1, y1) and (x2, y2) are interpolated as bezier control points.
     * 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     */
    public void addSegment(double x1, double y1, double x2, double y2, double x3, double y3) {
        myPoints.add(x1);
        myPoints.add(y1);
        myPoints.add(x2);
        myPoints.add(y2);
        myPoints.add(x3);
        myPoints.add(y3);
    }
    
    /**
     * Get the number of segments in the curve
     * 
     * @return
     */
    public int size() {
        return myPoints.size() / 6;
    }

    /**
     * Get the specified control point.
     * 
     * @param i
     * @return
     */
    public double[] controlPoint(int i) {
        double[] p = new double[2];
        p[0] = myPoints.get(i*2);
        p[1] = myPoints.get(i*2+1);
        return p;
    }
    
    /**
     * Get a point on the spine. The parameter t may vary from 0 to size().
     * Points on the kth segment take have parameters in the range (k, k+1).
     * 
     * @param t
     * @return
     */
    public double[] point(double t) {
        int i = (int)Math.floor(t);
        t = t - i;
        
        i *= 6;
        
        double x0 = myPoints.get(i++);
        double y0 = myPoints.get(i++);
        double x1 = myPoints.get(i++);
        double y1 = myPoints.get(i++);
        double x2 = myPoints.get(i++);
        double y2 = myPoints.get(i++);
        double x3 = myPoints.get(i++);
        double y3 = myPoints.get(i++);
        
        double[] p = new double[2];

        p[0] = b(0, t) * x0 + b(1, t) * x1 + b(2, t) * x2 + b(3, t) * x3;
        p[1] = b(0, t) * y0 + b(1, t) * y1 + b(2, t) * y2 + b(3, t) * y3;
        
        return p;
    }

    /**
     * Get the unit normal vector at t
     * @param t
     * @return
     */
    public double[] normal(double t) {
        double[] tan = tangent(t);
        return Utils.normal2d(tan);
    }

    private double[] tangent(double t) {
        int i = (int)Math.floor(t);
        t = t - i;

        i *= 6;

        double x0 = myPoints.get(i++);
        double y0 = myPoints.get(i++);
        double x1 = myPoints.get(i++);
        double y1 = myPoints.get(i++);
        double x2 = myPoints.get(i++);
        double y2 = myPoints.get(i++);
        double x3 = myPoints.get(i++);
        double y3 = myPoints.get(i++);

        double[] tan = new double[2];

        tan[0] = 3*(b(0, t)*(x1-x0) + b(1, t)*(x2-x1) + b(2, t)*(x3-x2));
        tan[1] = 3*(b(0, t)*(y1-y0) + b(1, t)*(y2-y1) + b(2, t)*(y3-y2));

        return tan;
    }
    
    /**
     * Calculate the Bezier coefficients
     * 
     * @param i
     * @param t
     * @return
     */
    private double b(int i, double t) {
        
        switch(i) {
        
        case 0:
            return (1-t) * (1-t) * (1-t);

        case 1:
            return 3 * (1-t) * (1-t) * t;
            
        case 2:
            return 3 * (1-t) * t * t;

        case 3:
            return t * t * t;
        }
        
        // this should never happen
        throw new IllegalArgumentException("" + i);
    }

    /**
     * Draws this road, given the terrain.
     * @param gl
     * @param terrain
     */
    public void draw(GL2 gl, Terrain terrain) {
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, Utils.LIGHT_FULL, 0);
        double x, z, y, x1, z1;
        x = z = y = x1 = z1 = 0;
        double w = width()/2;

        int numPoints = 16;
        double tIncrement = 1.0/numPoints;
        double t, t1;
        double[] normal = null;

        gl.glBindTexture(GL2.GL_TEXTURE_2D, Road.texture.getTextureId());
        // dealing with z-fighting
        gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
        gl.glPolygonOffset(-1f, -1f);

        gl.glBegin(GL2.GL_QUAD_STRIP);
        for(int i = 0; i < numPoints*size(); i++){
            t = i*tIncrement;
            t1 = (i+1)*tIncrement;
            x = point(t)[0]; z = point(t)[1];
            // If not last point, estimate new normal. Otherwise, use last normal.
            if (i != numPoints*size()-1) {
                x1 = point(t1)[0];
                z1 = point(t1)[1];
            } else { // use last point to calculate normal
                x1 = controlPoint(size()*3)[0];
                z1 = controlPoint(size()*3)[1];
            }
            normal = Utils.normal2d(new double[]{x - x1, z - z1});

            y = terrain.altitude(x, z);
            gl.glNormal3d(0, 1, 0);
            gl.glTexCoord2d(0,i%2);gl.glVertex3d(x+w* normal[0], y, z + w * normal[1]);
            gl.glTexCoord2d(1,i%2);gl.glVertex3d(x-w*normal[0], y, z-w*normal[1]);
        }
        // Adds final point to the road.
        gl.glTexCoord2d(0,0);gl.glVertex3d(x1+w*normal[0], y, z1 + w * normal[1]);
        gl.glTexCoord2d(1,0);gl.glVertex3d(x1-w*normal[0], y, z1-w*normal[1]);

        gl.glEnd();
        gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
        gl.glDisable(GL2.GL_POLYGON_OFFSET_FILL);
    }

}
