package ass2.spec;

/**
 * Created by ruan0408 on 28/09/15.
 */
public class MathUtils {

    // Compute the normal based on 3 vertices of the face
    public static float[] normal(float[] a, float[]b, float[] c) {
        float[] v = {b[0]-a[0], b[1]-a[1],b[2]-a[2]};
        float[] w = {c[0]-a[0], c[1]-a[1],c[2]-a[2]};

        return new float[]{v[1]*w[2]-v[2]*w[1], v[2]*w[0]-v[0]*w[2], v[0]*w[1]-v[1]*w[0]};
    }
}
