package ass2.spec;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by ruan0408 on 25/10/15.
 */
public class Camera implements KeyListener{

    private Avatar avatar;
    private boolean isFirstPerson;
    private double distY = 1;
    private double distGround = 3;

    public Camera(Avatar a) {
        avatar = a;
        isFirstPerson = false;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                isFirstPerson = !isFirstPerson;
                break;
            default:
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    public void updateView(GL2 gl) {
        double[] pos =  avatar.getPosition();
        double rot = avatar.getRotation();

        if (isFirstPerson) {
            gl.glRotated(-(rot-90), 0, 1, 0);//-90 to fix initial orientation
            gl.glTranslated(-pos[0], -(pos[1]+avatar.size()+0.01), -pos[2]);
        } else {
            GLU glu = new GLU();
            double rad = Math.toRadians(rot+180);//+180 because the camera is behind the avatar
            double[] cam = {pos[0]+ distGround *Math.cos(rad), pos[1]+distY, pos[2]- distGround *Math.sin(rad)};
            // camera looking at the horizon
            glu.gluLookAt(cam[0] ,cam[1], cam[2], -1000*Math.cos(rad), pos[1], 1000*Math.sin(rad), 0, 1, 0);
        }
    }
}
