package ass2.spec;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

/**
 * COMMENT: Comment Tree 
 *
 * @author malcolmr
 */
public class Tree {

    private static final String TRUNK_TEXT = "/res/trunk.jpg";
    private static final String TRUNK_TEXT_EXT = "jpg";
    private static final String LEAVES_TEXT = "/res/leaves.jpg";
    private static final String LEAVES_TEXT_EXT = "jpg";

    public static final int CIRCLE_SLICES = 32;
    public static final double BASE_HEIGHT = 0;
    public static final float[] TRUNK_DIFFUSE = Utils.LIGHT_80;

    private static MyTexture leavesTexture;
    private static MyTexture trunkTexture;

    private final double TRUNK_SIZE = 0.5;
    private final double TRUNK_RADIUS = 0.05;
    private final double SPHERE_RADIUS = 0.4;

    private double[] myPos;

    public static void loadStaticData(GL2 gl) {
        leavesTexture = new MyTexture(gl,LEAVES_TEXT,LEAVES_TEXT_EXT,true);
        trunkTexture =  new MyTexture(gl, TRUNK_TEXT,TRUNK_TEXT_EXT,true);
    }

    public Tree(double x, double y, double z) {
        myPos = new double[3];
        myPos[0] = x;
        myPos[1] = y;
        myPos[2] = z;
    }
    
    public double[] getPosition() {
        return myPos;
    }

    public void draw(GL2 gl) {
        gl.glPushMatrix();
            gl.glTranslated(myPos[0], myPos[1], myPos[2]);
            drawTrunk(gl);
            gl.glTranslated(0, BASE_HEIGHT+TRUNK_SIZE+SPHERE_RADIUS-0.2, 0);
            drawLeaves(gl);
        gl.glPopMatrix();
    }

    private void drawTrunk(GL2 gl) {
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, TRUNK_DIFFUSE, 0);

        // draws the base of the trunk
        drawCircleAtHeight(gl, BASE_HEIGHT, Utils.NORMAL_DOWN);
        // draws the top of the trunk
        drawCircleAtHeight(gl, TRUNK_SIZE, Utils.NORMAL_UP);

        // draws the body of the trunk
        gl.glBindTexture(GL2.GL_TEXTURE_2D, trunkTexture.getTextureId());
        gl.glBegin(GL2.GL_QUADS); {
            double angleStep = 2*Math.PI/ CIRCLE_SLICES;
            for (int i = 0; i < CIRCLE_SLICES; i++){
                double a0 = i * angleStep;
                double a1 = ((i+1) % CIRCLE_SLICES) * angleStep;

                //Calculate vertices for the quad
                double x0 = TRUNK_RADIUS*Math.cos(a0);
                double z0 = TRUNK_RADIUS*Math.sin(a0);

                double x1 = TRUNK_RADIUS*Math.cos(a1);
                double z1 = TRUNK_RADIUS*Math.sin(a1);

                //Use the face normal for all 4 vertices in the quad.
                gl.glNormal3d(-(TRUNK_SIZE - BASE_HEIGHT) * (z1 - z0), 0, (TRUNK_SIZE - BASE_HEIGHT) * (x1 - x0));

                gl.glTexCoord2d((double)i/ CIRCLE_SLICES, 0.0);gl.glVertex3d(x0, BASE_HEIGHT, z0);
                gl.glTexCoord2d((double)(i+1)/ CIRCLE_SLICES, 0.0);gl.glVertex3d(x1, BASE_HEIGHT, z1);
                gl.glTexCoord2d((double)(i+1)/ CIRCLE_SLICES, 1.0);gl.glVertex3d(x1, TRUNK_SIZE, z1);
                gl.glTexCoord2d((double)i/ CIRCLE_SLICES, 1.0);gl.glVertex3d(x0, TRUNK_SIZE, z0);
            }
        } gl.glEnd();
        gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
    }

    /**
     * Draws tree leaves using GLU.
     * @param gl
     */
    private void drawLeaves(GL2 gl) {
        gl.glBindTexture(GL2.GL_TEXTURE_2D, leavesTexture.getTextureId());
        gl.glMatrixMode(GL2.GL_TEXTURE);
        gl.glLoadIdentity();
        gl.glScalef(5, 5, 1);
        GLU glu = new GLU();
        GLUquadric quad = glu.gluNewQuadric();
        glu.gluQuadricTexture(quad, true);
        glu.gluSphere(quad, SPHERE_RADIUS, 15, 15);
        glu.gluDeleteQuadric(quad);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
        gl.glLoadIdentity();
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    private void drawCircleAtHeight(GL2 gl, double height, double[] normal) {
        gl.glDisable(GL2.GL_TEXTURE);
        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
        gl.glColor3dv(Utils.BROWN, 0);
        gl.glBegin(GL2.GL_TRIANGLE_FAN);{
            gl.glNormal3dv(normal, 0);
            gl.glVertex3d(0, height, 0);
            double angleStep = 2*Math.PI/CIRCLE_SLICES;
            for (int i = 0; i <= CIRCLE_SLICES ; i++){
                double a0 = i * angleStep;

                double x0 = TRUNK_RADIUS*Math.cos(a0);
                double z0 = TRUNK_RADIUS*Math.sin(a0);

                gl.glVertex3d(x0, height,z0);
            }
        }gl.glEnd();
        gl.glEnable(GL2.GL_TEXTURE);
    }
}
