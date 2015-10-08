package ass2.spec;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by ruan0408 on 5/10/15.
 */
public class Camera implements KeyListener {

    private final double ANGLE_STEP = 5; //8 degrees per key stroke
    private final double MOV_STEP = 0.4; //0.4 units per key stroke

    private double[] translation; // Translation is always regarding the z axis
    private double rotation;    // Rotation is always over the y axis.
    private Terrain terrain;

    /**
     * Create a camera rotated by angle degrees
     * @param angle
     */
    public Camera(double angle, Terrain t) {
        rotation = angle;
        translation = new double[]{0, 0, 0};
        terrain = t;
    }

    public double[] getTranslation() {
        return translation;
    }

    public double getRotation() {
        return rotation;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                translation[0] += -MOV_STEP*Math.sin(Math.toRadians(rotation));
                translation[2] += -MOV_STEP*Math.cos(Math.toRadians(rotation));
                break;
            case KeyEvent.VK_DOWN:
                translation[0] += MOV_STEP*Math.sin(Math.toRadians(rotation));
                translation[2] += MOV_STEP*Math.cos(Math.toRadians(rotation));
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
        translation[1] = terrain.altitude(translation[0], translation[2])+0.1;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
