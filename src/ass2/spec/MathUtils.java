package ass2.spec;

import java.util.Arrays;

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

    /**
     * Compute the normalized normal of the given tangent vector
     * @param tangent
     * @return
     */
    public static double[] normal2d(double[] tangent) {
        return normalize(new double[]{-tangent[1], tangent[0]});
    }

    /**
     * Normalizes the given vector.
     * @param vector
     * @return
     */
    public static double[] normalize(double[] vector) {
        double sum = 0;
        double[] normalized = Arrays.copyOf(vector,vector.length);
        for (int i = 0; i < vector.length; i++)
            sum += vector[i]*vector[i];

        double factor = Math.sqrt(sum);
        for (int i = 0; i < vector.length; i++)
            normalized[i] = normalized[i]/factor;

        return normalized;
    }
}
