package ass2.spec;

import javax.media.opengl.GL2;

/**
 * Created by ruan0408 on 9/10/15.
 */
public class View {

    private Avatar avatar;
    private boolean isFirstPerson;
    private double LIFT = 0.1;

    public View(Avatar a) {
        avatar = a;
        isFirstPerson = true;
    }

    public void setFirstPerson(boolean b) {
        isFirstPerson = b;
    }

    public void updateView(GL2 gl) {
        double[] pos = avatar.getPosition();
        double rot = avatar.getRotation();

        gl.glRotated(-rot, 0, 1, 0);
        gl.glTranslated(-(pos[0]), -(pos[1]), -pos[2]);
        gl.glTranslated(-1, -0.5, 0);
        avatar.draw(gl);
        //if (isFirstPerson)
        //    gl.glTranslated(5, 0, 0);
//        if (isFirstPerson) {
//
//        } else {
//            gl.glRotated(-rot, 0, 1, 0);
//            gl.glTranslated(-pos[0], -pos[1], -pos[2]);
//        }
    }


}
