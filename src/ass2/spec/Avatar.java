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

    private final double ANGLE_STEP = 5;    // 8 degrees per key stroke
    private final double MOV_STEP = 0.4;    // 0.4 units per key stroke
    private final double AVATAR_SIZE = 0.3;
    private final double CAM_DIST_Y = 1;    // vertical distance from the avatar (when camera is 3rd person)
    private final int ROTATION_STEP = 30;

    /**
     * Keep in mind that Opengl axis are like this:
     *  y   x
     *  |  /
     *  | /
     *  |/_ _ _z
     */
    private double[] position;  //absolute position of avatar, in world coordinates.
    private double rotation;    //rotation over y axis, starting from the x axis counter clockwise.
    private Terrain terrain;
    private boolean isFirstPerson;
    private boolean isNightTime;
    private boolean sunPositionChanged;
    private int currentRotation;

    public Avatar(Terrain t) {
        rotation = 0;
        position = new double[]{0, 0, 0};
        terrain = t;
        isFirstPerson = true;
        isNightTime = false;
        sunPositionChanged = false;
        currentRotation = 0;
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

        if (isFirstPerson) {
            gl.glRotated(-(rot-90), 0, 1, 0);//-90 to fix initial orientation
            gl.glTranslated(-pos[0], -pos[1], -pos[2]);
        } else {
            GLU glu = new GLU();
            double rad = Math.toRadians(rot+180);//+180 because the camera is behind the avatar
            double[] cam = {pos[0]+Math.cos(rad), pos[1]+CAM_DIST_Y, pos[2]-Math.sin(rad)};
            glu.gluLookAt(cam[0] ,cam[1], cam[2], pos[0], pos[1], pos[2], 0, 1, 0);
            draw(gl);
        }
    }

    public boolean isNightTime(){return isNightTime;}

    public boolean isSunPositionChanged() {return sunPositionChanged;}

    public void setSunPositionChanged(boolean bool){sunPositionChanged = bool;}

    public int getCurrentRotation(){return currentRotation;}
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
            case KeyEvent.VK_N:
                isNightTime = !isNightTime;
                break;
            //forwards time, moving the sun
            case KeyEvent.VK_T:
                float[] newSunPosition = MathUtils.rotatePoint(ROTATION_STEP, terrain.getSunlight());
                terrain.setSunlightDir(newSunPosition[0],newSunPosition[1],newSunPosition[2]);
                currentRotation = (currentRotation + ROTATION_STEP)%360;
                sunPositionChanged = true;
                break;
            case KeyEvent.VK_R:
                newSunPosition = MathUtils.rotatePoint(-ROTATION_STEP, terrain.getSunlight());
                terrain.setSunlightDir(newSunPosition[0],newSunPosition[1],newSunPosition[2]);
                currentRotation = (currentRotation - ROTATION_STEP)%360;
                sunPositionChanged = true;
                break;
            default:
                break;
        }
        //0.1 to make things clearly above ground
        position[1] = terrain.altitude(position[0], position[2])+0.1;
    }

    /**
     * Draw the avatar
     * @param gl
     */
    private void draw(GL2 gl) {
        float[] red = {1f,0f,0f,1f};
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, red, 0);
        gl.glPushMatrix();{
            //0.1 accounts for the height of the teapot
            gl.glTranslated(position[0], position[1] + 0.1, position[2]);
            gl.glRotated(rotation, 0, 1, 0);

            GLUT glut = new GLUT();
            glut.glutSolidTeapot(AVATAR_SIZE);
        }
        gl.glPopMatrix();
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
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
