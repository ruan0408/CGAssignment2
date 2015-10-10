package ass2.spec;

import com.jogamp.opengl.util.gl2.GLUT;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by ruan0408 on 9/10/15.
 */
public class Avatar implements KeyListener{

    private final double ANGLE_STEP = 5; //8 degrees per key stroke
    private final double MOV_STEP = 0.4; //0.4 units per key stroke
    private final double AVATAR_SIZE = 0.3;

    private double[] position;
    private double rotation;
    private Terrain terrain;
    private boolean isFirstPerson;

    public Avatar(double angle, Terrain t) {
        rotation = angle;
        position = new double[]{0, 0, 0};
        terrain = t;
        isFirstPerson = true;
    }

    public double[] getPosition() {
        return position;
    }

    public double getRotation() {
        return rotation;
    }

    public void updateView(GL2 gl) {
        double[] pos =  getPosition();
        double rot = getRotation();

//        gl.glRotated(-rot, 0, 1, 0);
//        gl.glTranslated(-(pos[0]), -(pos[1]), -pos[2]);
//        gl.glTranslated(-1, -0.5, 0);
        //gl.glMatrixMode(GL2.GL_PROJECTION);
        //gl.glLoadIdentity();
        if (isFirstPerson) {
            gl.glRotated(-rot, 0, 1, 0);
            gl.glTranslated(-pos[0], -pos[1], -pos[2]);
            drawAxis(gl);
        } else {
            GLU glu = new GLU();
            double rad = Math.toRadians(rot+180);
            double[] cam = {pos[0], pos[1]+1, pos[2]};

            glu.gluLookAt(cam[0] + Math.cos(rad), cam[1], cam[2] - Math.sin(rad), pos[0], pos[1], pos[2], 0, 1, 0);
            draw(gl);
        }
    }

    public void draw(GL2 gl) {
        float[] white = {1f,0f,0f,1f};
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, white, 0);
        gl.glColor3fv(white, 0);
        //gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
        gl.glPushMatrix();
            //0.1 accounts for the height of the teapot
            gl.glTranslated(position[0], position[1]+0.1, position[2]);
            gl.glRotated(rotation, 0, 1, 0);
            drawAxis(gl);
            GLUT glut = new GLUT();
            glut.glutSolidTeapot(AVATAR_SIZE);
        gl.glPopMatrix();
        //gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                position[0] += MOV_STEP * Math.cos(Math.toRadians(rotation));
                position[2] += -MOV_STEP * Math.sin(Math.toRadians(rotation));
                break;
            case KeyEvent.VK_DOWN:
                position[0] += -MOV_STEP * Math.cos(Math.toRadians(rotation));
                position[2] += MOV_STEP * Math.sin(Math.toRadians(rotation));
                break;
            case KeyEvent.VK_LEFT:
                rotation = MathUtils.normaliseAngle(rotation+ANGLE_STEP);
                break;
            case KeyEvent.VK_RIGHT:
                rotation = MathUtils.normaliseAngle(rotation-ANGLE_STEP);
                break;
            case KeyEvent.VK_SPACE:
                isFirstPerson = !isFirstPerson;
                break;
            default:
                break;
        }
        position[1] = terrain.altitude(position[0], position[2])+0.1;
    }

    private void drawAxis(GL2 gl) {
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glLineWidth(3);
        gl.glBegin(GL2.GL_LINES);
            // draw line for x axis
            gl.glColor3f(1, 0, 0);
            gl.glVertex3f(0, 0, 0);
            gl.glVertex3f(3, 0, 0);
            // draw line for y axis
            gl.glColor3f(0, 1, 0);
            gl.glVertex3f(0, 0, 0);
            gl.glVertex3f(0, 3, 0);
            // draw line for Z axis
            gl.glColor3f(0, 0, 1);
            gl.glVertex3f(0, 0, 0);
            gl.glVertex3f(0, 0, 3);
        gl.glEnd();
        gl.glEnable(GL2.GL_LIGHTING);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
