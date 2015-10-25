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

    /**
     * Normalise an angle to the range (-180, 180]
     *
     * @param angle
     * @return
     */
    static public double normaliseAngle(double angle) {
        return ((angle + 180.0) % 360.0 + 360.0) % 360.0 - 180.0;
    }

    /**
     * Rotate a point v by an angle theta using a rotation matrix and matrix-vector multiplication
     * @param theta
     * @param p
     * @return
     */
    public static float[] rotatePointAroundX(double theta, float[] p) {
        double rotationAngle = normaliseAngle(theta);
        rotationAngle = Math.toRadians(rotationAngle);
        double sin = Math.sin(rotationAngle);
        double cos = Math.cos(rotationAngle);
        double[][] rotationMatrix = {{1,0,0,0}, {0, cos, -sin, 0}, {0, sin, cos, 0}, {0,0,0,1}};

        double[] pointInHomogenousCoordinates = {p[0],p[1],p[2],1};
        double[] u = new double[4];

        for (int i = 0; i < 3; i++) {
            u[i] = 0;
            for (int j = 0; j < 3; j++) {
                u[i] += rotationMatrix[i][j] * pointInHomogenousCoordinates[j];
            }
        }

        return new float[]{(float)u[0],(float)u[1],(float)u[2]};
    }

}
