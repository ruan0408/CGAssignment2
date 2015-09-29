package ass2.spec;

import javax.media.opengl.GL2;

/**
 * COMMENT: Comment Tree 
 *
 * @author malcolmr
 */
public class Tree {

    private final double TRUNK_SIZE = 0.5;
    private final double TRUNK_RADIUS = 0.1;
    private final int SPHERE_RADIUS = 1;

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
            gl.glTranslated(0, TRUNK_SIZE, 0);
            drawLeaves(gl);
        gl.glPopMatrix();
    }

    private void drawTrunk(GL2 gl) {
        //gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        float[] brown = {0.62f, 0.32f, 0.17f, 1.0f};
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, brown, 0);

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

        gl.glBegin(GL2.GL_QUADS);
        {
            double angleStep = 2*Math.PI/slices;
            for (int i = 0; i <= slices ; i++){
                double a0 = i * angleStep;
                double a1 = ((i+1) % slices) * angleStep;

                //Calculate vertices for the quad
                double x0 = TRUNK_RADIUS*Math.cos(a0);
                double z0 = TRUNK_RADIUS*Math.sin(a0);

                double x1 = TRUNK_RADIUS*Math.cos(a1);
                double z1 = TRUNK_RADIUS*Math.sin(a1);

                //Use the face normal for all 4 vertices in the quad.
                //gl.glNormal3d(-(z2-z1)*(y1-y0),(x1-x0)*(z2-z1),0);
                gl.glNormal3d(-(y1 - y0) * (z1 - z0), 0, (y1 - y0) * (x1 - x0));

                gl.glVertex3d(x0, y0, z0);
                gl.glVertex3d(x1, y0, z1);
                gl.glVertex3d(x1, y1, z1);
                gl.glVertex3d(x0, y1, z0);
            }
        }
        gl.glEnd();
    }

    private void drawLeaves(GL2 gl) {
        float[] darkGreen = {0.13f, 0.54f, 0.13f, 1.0f};
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, darkGreen, 0);
    }

}
