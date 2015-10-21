package ass2.spec;

import com.jogamp.opengl.util.gl2.GLUT;

import javax.media.opengl.GL;
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
    private final double AVATAR_SIZE = 0.15;
    private final double CAM_DIST_Y = 1;    // vertical distance from the avatar (when camera is 3rd person)
    private final double CAM_DIST_GROUND = 3;// how far behind the avatar you want the camera (when camera is 3rd person)
    private final int ROTATION_STEP = 30;

    public static MyTexture bodyTexture;
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

    private int rotateLimbs = 0;
    private int begunMoving = 0;
    public Avatar(Terrain t) {
        rotation = 0;
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

        if (isFirstPerson) {
            gl.glRotated(-(rot-90), 0, 1, 0);//-90 to fix initial orientation
            gl.glTranslated(-pos[0], -(pos[1]+2*AVATAR_SIZE), -pos[2]);
        } else {
            GLU glu = new GLU();
            double rad = Math.toRadians(rot+180);//+180 because the camera is behind the avatar
            double[] cam = {pos[0]+CAM_DIST_GROUND*Math.cos(rad), pos[1]+CAM_DIST_Y, pos[2]-CAM_DIST_GROUND*Math.sin(rad)};
            // camera looking at the horizon
            glu.gluLookAt(cam[0] ,cam[1], cam[2], -1000*Math.cos(rad), pos[1], 1000*Math.sin(rad), 0, 1, 0);
            draw(gl);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                position[0] += MOV_STEP * Math.cos(Math.toRadians(rotation));
                position[2] += -MOV_STEP * Math.sin(Math.toRadians(rotation));
                rotateLimbs = 1 - rotateLimbs;
                if(begunMoving == 0) begunMoving = 1;
                break;
            case KeyEvent.VK_DOWN:
                position[0] += -MOV_STEP * Math.cos(Math.toRadians(rotation));
                position[2] += MOV_STEP * Math.sin(Math.toRadians(rotation));
                rotateLimbs = 1 - rotateLimbs;
                if(begunMoving == 0) begunMoving = 1;
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
                terrain.setNigthTime(!terrain.isNightTime());
                break;
            //forwards time, moving the sun
            case KeyEvent.VK_T:
                float[] newSunPosition = MathUtils.rotatePointAroundX(ROTATION_STEP, terrain.getSunlight());
                terrain.setSunlightDir(newSunPosition[0],newSunPosition[1],newSunPosition[2]);
                terrain.setCurrentSunRotation((terrain.getCurrentSunRotation() +ROTATION_STEP)%360);
                terrain.setSunPositionChanged(true);
                break;
            case KeyEvent.VK_R:
                newSunPosition = MathUtils.rotatePointAroundX(-ROTATION_STEP, terrain.getSunlight());
                terrain.setSunlightDir(newSunPosition[0],newSunPosition[1],newSunPosition[2]);
                int currentRotation = terrain.getCurrentSunRotation();
                currentRotation -= ROTATION_STEP;
                if(currentRotation < 0) currentRotation += 360;
                terrain.setCurrentSunRotation(currentRotation);
                terrain.setSunPositionChanged(true);
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
        float[] white = {1f,1f,1f,1f};
        float[] pink = {1f,0.3f,0.56f,1f};
        float[] yellow = {1,1,0};
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, white, 0);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, bodyTexture.getTextureId());
        gl.glPushMatrix();{
            gl.glTranslated(position[0], position[1]+2*AVATAR_SIZE, position[2]);
            gl.glRotated(rotation, 0, 1, 0);

            GLUT glut = new GLUT();
            gl.glPushMatrix();
            {
                gl.glTranslated(0, -AVATAR_SIZE / 2, 0);
                glut.glutSolidCube((float) AVATAR_SIZE);
                gl.glTranslated(0,AVATAR_SIZE,0);
                glut.glutSolidCube((float) AVATAR_SIZE);

            }gl.glPopMatrix();

            gl.glPushMatrix();{
                gl.glTranslated(0,1.5*AVATAR_SIZE,0);
                glut.glutSolidSphere(AVATAR_SIZE/2,15,15);
                gl.glBindTexture(GL.GL_TEXTURE_2D, 0);

            }gl.glPopMatrix();

            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, white, 0);

            gl.glPushMatrix();
            {
                gl.glTranslated(0, -AVATAR_SIZE, AVATAR_SIZE / 2 - 0.02);
                gl.glRotated(90, 1, (1- rotateLimbs)*begunMoving, 0);
                glut.glutSolidCylinder(AVATAR_SIZE / 8, AVATAR_SIZE, 15, 15);
            }gl.glPopMatrix();

            gl.glPushMatrix();
            {
                gl.glTranslated(0, -AVATAR_SIZE, -AVATAR_SIZE/2 + 0.02);
                gl.glRotated(90, 1, begunMoving*rotateLimbs, 0);
                glut.glutSolidCylinder(AVATAR_SIZE / 8, AVATAR_SIZE, 15, 15);
            }gl.glPopMatrix();

            gl.glPushMatrix();
            {
                gl.glTranslated(0, AVATAR_SIZE, 0.09);
                gl.glRotated(60, 1, rotateLimbs, 0);
                glut.glutSolidCylinder(AVATAR_SIZE / 8, AVATAR_SIZE, 15, 15);
            }gl.glPopMatrix();

            gl.glPushMatrix();
            {
                gl.glTranslated(0, AVATAR_SIZE, -0.09);
                gl.glRotated(120, 1, 1 - rotateLimbs, 0);
                glut.glutSolidCylinder(AVATAR_SIZE / 8, AVATAR_SIZE, 15, 15);
            }gl.glPopMatrix();
           /* gl.glRotated(-90,1,0,0);
            glut.glutSolidCone(AVATAR_SIZE,0.5,15,15);
            gl.glRotated(90,1,0,0);
            gl.glTranslated(0,0.5,0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, red, 0);
            glut.glutSolidSphere(AVATAR_SIZE,15,15);
*/
            //glut.glutSolidTeapot(AVATAR_SIZE);
        }
        gl.glPopMatrix();
        gl.glBindTexture(GL2.GL_TEXTURE_2D, bodyTexture.getTextureId());
    }

    // Debugging method
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
