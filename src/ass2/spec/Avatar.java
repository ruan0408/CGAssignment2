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

    public Avatar(double angle, Terrain t) {
        rotation = angle;
        position = new double[]{0, 0, 0};
        terrain = t;
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
        gl.glLoadIdentity();
        GLU glu = new GLU();
        glu.gluLookAt(pos[0]-1, pos[1]+1, pos[2], pos[0], pos[1], pos[2], 0, 1, 0);

        draw(gl);
        //if (isFirstPerson)
        //    gl.glTranslated(5, 0, 0);
//        if (isFirstPerson) {
//
//        } else {
//            gl.glRotated(-rot, 0, 1, 0);
//            gl.glTranslated(-pos[0], -pos[1], -pos[2]);
//        }
    }

    public void draw(GL2 gl) {
        float[] white = {1f,0f,0f,1f};
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, white, 0);
        gl.glColor3fv(white, 0);
        //gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
        gl.glPushMatrix();
            gl.glTranslated(position[0], position[1], position[2]);
            gl.glRotated(rotation, 0, 1, 0);
            GLUT glut = new GLUT();
            glut.glutSolidTeapot(AVATAR_SIZE);
        gl.glPopMatrix();
        //gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                position[0] += MOV_STEP*Math.sin(Math.toRadians(rotation));
                position[2] += MOV_STEP*Math.cos(Math.toRadians(rotation));
                break;
            case KeyEvent.VK_DOWN:
                position[0] += -MOV_STEP*Math.sin(Math.toRadians(rotation));
                position[2] += -MOV_STEP*Math.cos(Math.toRadians(rotation));
                break;
            case KeyEvent.VK_LEFT:
                rotation = MathUtils.normaliseAngle(rotation+ANGLE_STEP);
                break;
            case KeyEvent.VK_RIGHT:
                rotation = MathUtils.normaliseAngle(rotation-ANGLE_STEP);
                break;
            default:
                break;
        }
        position[1] = terrain.altitude(position[0], position[2])+0.2;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
