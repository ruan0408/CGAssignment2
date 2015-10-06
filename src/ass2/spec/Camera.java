package ass2.spec;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by ruan0408 on 5/10/15.
 */
public class Camera implements KeyListener {

    private double translation; // Translation is always regarding the z axis
    private double rotation;    // Rotation is always over the y axis.

    public Camera() {
        rotation = 0;
        translation = 0;
    }

    public double getTranslation() {
        return translation;
    }

    public double getRotation() {
        return rotation;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                translation += 0.1;
                break;
            case KeyEvent.VK_DOWN:
                translation += -0.1;
                break;
            case KeyEvent.VK_LEFT:
                rotation += 1;
                break;
            case KeyEvent.VK_RIGHT:
                rotation += -1;
                break;
            default:
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
