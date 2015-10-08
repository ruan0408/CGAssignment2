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

    static MyTexture leavesTexture;
    static MyTexture trunkTexture;
    private final double TRUNK_SIZE = 0.5;
    private final double TRUNK_RADIUS = 0.05;
    private final double SPHERE_RADIUS = 0.4;

    private double[] myPos;

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
            gl.glTranslated(0, TRUNK_SIZE+SPHERE_RADIUS-0.1, 0);
            drawLeaves(gl);
        gl.glPopMatrix();
    }

    private void drawTrunk(GL2 gl) {
        float[] white = {1f, 1f, 1f, 1.0f};
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, white, 0);

        double y0 = 0;
        double y1 = TRUNK_SIZE;
        int slices = 32;

        //lower circle
        gl.glBegin(GL2.GL_TRIANGLE_FAN);{
            gl.glNormal3d(0,-1,0);
            gl.glVertex3d(0,y0,0);
            double angleStep = 2*Math.PI/slices;
            for (int i = 0; i <= slices ; i++){
                double a0 = i * angleStep;

                double x0 = TRUNK_RADIUS*Math.cos(a0);
                double z0 = TRUNK_RADIUS*Math.sin(a0);

                gl.glVertex3d(x0,y0,z0);
            }
        }gl.glEnd();
        //upper circle
        gl.glBegin(GL2.GL_TRIANGLE_FAN);{
            gl.glNormal3d(0,1,0);
            gl.glVertex3d(0,y1,0);
            double angleStep = 2*Math.PI/slices;
            for (int i = 0; i <= slices ; i++){
                double a0 = i * angleStep;

                double x0 = TRUNK_RADIUS*Math.cos(a0);
                double z0 = TRUNK_RADIUS*Math.sin(a0);

                gl.glVertex3d(x0, y1,z0);
            }
        }gl.glEnd();

        //gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, trunkTexture.getTextureId());

        gl.glBegin(GL2.GL_QUADS);
        {
            double start = 0;
            double angleStep = 2*Math.PI/slices;
            for (int i = 0; i < slices ; i++){
                double a0 = i * angleStep;
                double a1 = ((i+1) % slices) * angleStep;

                //Calculate vertices for the quad
                double x0 = TRUNK_RADIUS*Math.cos(a0);
                double z0 = TRUNK_RADIUS*Math.sin(a0);

                double x1 = TRUNK_RADIUS*Math.cos(a1);
                double z1 = TRUNK_RADIUS*Math.sin(a1);

                //Use the face normal for all 4 vertices in the quad.
                gl.glNormal3d(-(y1 - y0) * (z1 - z0), 0, (y1 - y0) * (x1 - x0));

                gl.glTexCoord2d((double)i/slices, 0.0);gl.glVertex3d(x0, y0, z0);
                gl.glTexCoord2d((double)(i+1)/slices, 0.0);gl.glVertex3d(x1, y0, z1);
                gl.glTexCoord2d((double)(i+1)/slices, 1.0);gl.glVertex3d(x1, y1, z1);
                gl.glTexCoord2d((double)i/slices, 1.0);gl.glVertex3d(x0, y1, z0);
            }
        }
        gl.glEnd();
        gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
    }

    /**
     * Draws tree leaves using GLU.
     * @param gl
     */
    private void drawLeaves(GL2 gl) {
        float[] white = {1f, 1f, 1f, 1.0f};
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, white, 0);
        //gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
        //gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
        //gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, leavesTexture.getTextureId());
        gl.glMatrixMode(GL2.GL_TEXTURE);
        gl.glLoadIdentity();
        //gl.glRotated(0,1,0,0);
        gl.glScalef(5, 5, 1);
        GLU glu = new GLU();
        GLUquadric quad = glu.gluNewQuadric();
        glu.gluQuadricTexture(quad, true);
        glu.gluSphere(quad, SPHERE_RADIUS, 15, 15);
        glu.gluDeleteQuadric(quad);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
        gl.glLoadIdentity();
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
}
