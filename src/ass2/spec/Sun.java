package ass2.spec;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

/**
 * Created by ruan0408 on 25/10/15.
 */
public class Sun {

    private static final float[] DAY_LIGHT = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
    private static final float[] TWILIGHT = new float[]{0.6f, 0.3f, 0.6f, 1.0f};
    private static final float[] EARLY_DAY_LIGHT = new float[]{0.7f, 0.7f, 0.7f, 1.0f};
    private static final String SUN_TEXT = "/res/sun.jpg";
    private static final String SUN_TEXT_EXT = "jpg";
    private static final float SUN_RADIUS = 0.4f;
    private static final float MIN_HEIGHT = 3;
    private final int ROTATION_STEP = 30;

    private static MyTexture texture;

    private float[] position = new float[3];
    private float rotation = 0;

    public static void loadStaticData(GL2 gl) {
        texture = new MyTexture(gl, SUN_TEXT, SUN_TEXT_EXT, true);
    }

    /** Vector pointing to the sun */
    public Sun(float dx, float dy, float dz) {
        position[0] = dx;
        position[1] = dy;
        position[2] = dz;
    }

    public void setPosition(float[] newPosition) {position = newPosition;}
    public void setRotation(float newRotation) {rotation = newRotation;}
    public float rotation() {return rotation;}

    public float[] getDirectionHomogeneous() {
        return new float[]{position[0], position[1], position[2], 0};
    }

    public void moveForward() {
        float[] newPosition = Utils.rotatePointAroundX(ROTATION_STEP, position);
        setPosition(newPosition);
        setRotation((rotation+ROTATION_STEP)%360);
    }

    public void moveBackwards() {
        float[] newPosition = Utils.rotatePointAroundX(-ROTATION_STEP, position);
        setPosition(newPosition);

        float currentRotation = rotation();
        currentRotation -= ROTATION_STEP;
        if (currentRotation < 0) currentRotation += 360;
        setRotation(currentRotation);
    }

    public float[] getLightColor() {
        if(rotation >= 0 && rotation <= 120) return DAY_LIGHT;
        else if(rotation > 120 && rotation <= 240) return TWILIGHT;
        else return EARLY_DAY_LIGHT;
    }

    public void draw(GL2 gl) {
        gl.glPushMatrix();{
            gl.glTranslated(position[0], position[1] + MIN_HEIGHT, position[2]);

            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, Utils.LIGHT_FULL, 0);
            gl.glBindTexture(GL2.GL_TEXTURE_2D, texture.getTextureId());
            gl.glMatrixMode(GL2.GL_TEXTURE);
            gl.glLoadIdentity();
            gl.glScalef(5, 5, 1);
            GLU glu = new GLU();
            GLUquadric quad = glu.gluNewQuadric();
            glu.gluQuadricTexture(quad, true);
            glu.gluSphere(quad, SUN_RADIUS, 15, 15);
            glu.gluDeleteQuadric(quad);
            gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
            gl.glLoadIdentity();
            gl.glMatrixMode(GL2.GL_MODELVIEW);
        }gl.glPopMatrix();
    }
}
