package ass2.spec;

import com.jogamp.opengl.util.gl2.GLUT;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by ruan0408 on 9/10/15.
 */
public class Avatar implements KeyListener{

    private static final String AVATAR_TEXT = "/res/sun.jpg";
    private static final String AVATAR_TEXT_EXT = "jpg";
    private static final int ANGLE_SHIFT = -90;

    private final double ANGLE_STEP = 10;    // 5 degrees per key stroke
    private final double MOV_STEP = 0.4;    // 0.4 units per key stroke

    private final float HEAD_RADIUS = 0.05f;
    private final float LIMB_SIZE = 0.20f;
    private final float BODY_SIZE = 0.20f;
    private final float SIZE = LIMB_SIZE + BODY_SIZE + 2*HEAD_RADIUS;

    public static MyTexture texture;
    private double[] position;  //absolute position of avatar, in world coordinates.
    private double rotation;    //rotation over y axis, starting from the x axis counter clockwise.
    private Terrain terrain;

    private int rotateLimbs = 0;
    private int begunMoving = 0;

    public static void loadStaticData(GL2 gl) {
        texture = new MyTexture(gl, AVATAR_TEXT, AVATAR_TEXT_EXT, true);
    }

    public Avatar(Terrain t) {
        rotation = 0;
        position = new double[]{0, 0, 0};
        terrain = t;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        double rotRad = rotationRadians();
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                position[0] += MOV_STEP * Math.sin(rotRad);
                position[2] += MOV_STEP * Math.cos(rotRad);
                rotateLimbs = 1 - rotateLimbs;
                if(begunMoving == 0) begunMoving = 1;
                break;
            case KeyEvent.VK_DOWN:
                position[0] += -MOV_STEP * Math.sin(rotRad);
                position[2] += -MOV_STEP * Math.cos(rotRad);
                rotateLimbs = 1 - rotateLimbs;
                if(begunMoving == 0) begunMoving = 1;
                break;
            case KeyEvent.VK_LEFT:
                setRotation(rotation + ANGLE_STEP);
                break;
            case KeyEvent.VK_RIGHT:
                setRotation(rotation - ANGLE_STEP);
                break;
            default:
                break;
        }
        position[1] = terrain.altitude(position[0], position[2]);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    public double[] getPosition() {return position;}

    public float[] getPositionHomogeneousFloat() {
        return new float[] {(float)position[0], (float)position[1], (float)position[2], 1};
    }

    public double rotation() {return rotation;}
    public void setRotation(double angle) {rotation = Utils.normaliseAngle(angle);}
    public double rotationRadians() {return Math.toRadians(rotation);}
    public float size() {return SIZE;}

    public float[] getSpotlightVector() {
        return new float[]{(float)Math.sin(rotationRadians()), -0.5f, (float)Math.cos(rotationRadians())};
    }

    /**
     * Draw the avatar
     * @param gl
     */
    public void draw(GL2 gl) {
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, Utils.LIGHT_FULL, 0);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, texture.getTextureId());

        gl.glPushMatrix();{
            gl.glTranslated(position[0], position[1], position[2]);
            gl.glRotated(rotation()+ANGLE_SHIFT, 0, 1, 0);

            // Rigth leg
            gl.glTranslated(0, LIMB_SIZE, BODY_SIZE/4 -0.02);
            drawLimb(gl, 90, (1- rotateLimbs)*begunMoving);

            // Left leg
            gl.glTranslated(0, 0, -2*BODY_SIZE/4 +0.04);
            drawLimb(gl, 90, begunMoving*rotateLimbs);

            gl.glTranslated(0, 0, BODY_SIZE/4 -0.02);
            drawBody(gl);

//            // Rigth arm
            gl.glTranslated(0, BODY_SIZE, BODY_SIZE/4-0.01);
            drawLimb(gl, 60, rotateLimbs);
//
//            // Left arm
            gl.glTranslated(0, 0, -2*BODY_SIZE/4+0.02);
            drawLimb(gl, 120, 1 - rotateLimbs);
//
            gl.glTranslated(0, HEAD_RADIUS, BODY_SIZE/4 - 0.01);
            drawHead(gl);
        }
        gl.glPopMatrix();
        gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
    }

    private void drawLimb(GL2 gl, float angle, float yRotation) {
        GLUT glut = new GLUT();
        gl.glPushMatrix();{
            gl.glRotated(angle, 1, yRotation, 0);
            glut.glutSolidCylinder(LIMB_SIZE/8, LIMB_SIZE, 15, 15);
        }gl.glPopMatrix();
    }

    private void drawHead(GL2 gl) {
        GLUT glut = new GLUT();
        gl.glPushMatrix();{
            glut.glutSolidSphere(HEAD_RADIUS,15,15);
        }gl.glPopMatrix();
    }

    private void drawBody(GL2 gl) {
        GLUT glut = new GLUT();
        gl.glPushMatrix();{
            gl.glTranslated(0, BODY_SIZE/4, 0);
            glut.glutSolidCube(BODY_SIZE/2);
            gl.glTranslated(0,BODY_SIZE/2,0);
            glut.glutSolidCube(BODY_SIZE/2);
        }gl.glPopMatrix();
    }

    // Debugging method
    private void drawAxis(GL2 gl) {
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glLineWidth(3);
        gl.glBegin(GL2.GL_LINES);
            // drawScene line for x axis
            gl.glColor3f(1, 0, 0);
            gl.glVertex3f(0, 0, 0);
            gl.glVertex3f(3, 0, 0);
            // drawScene line for y axis
            gl.glColor3f(0, 1, 0);
            gl.glVertex3f(0, 0, 0);
            gl.glVertex3f(0, 3, 0);
            // drawScene line for Z axis
            gl.glColor3f(0, 0, 1);
            gl.glVertex3f(0, 0, 0);
            gl.glVertex3f(0, 0, 3);
        gl.glEnd();
        gl.glEnable(GL2.GL_LIGHTING);
    }
}
