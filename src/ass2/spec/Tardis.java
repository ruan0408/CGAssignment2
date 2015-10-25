package ass2.spec;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import java.nio.FloatBuffer;

/**
 * Created by gervasio on 18/10/15.
 */

//Corresponds to the "other" objet specified by the assignment spec. It's a (crude) 3D rendering of the Doctor's
// TARDIS (Time And Relative Dimensions In Space) from the tv show Doctor Who. Since the Doctor is a time traveler
//there can be more than one TARDIS per map =)
public class Tardis {

    private static final String vertexShaderDayPath = "/res/vertexShaderDay.glsl";
    private static final String fragmentShaderDayPath = "/res/fragmentShaderDay.glsl";
    private static final String vertexShaderNightPath = "/res/vertexShaderNight.glsl";
    private static final String fragmentShaderNightPath = "/res/fragmentShaderNight.glsl";

    private static final String TARDIS_TEXT_FRONT = "/res/tardisFront.png";
    private static final String TARDIS_TEXT_FRONT_EXT = "png";
    private static final String TARDIS_TEXT_SIDE = "/res/tardisSide.png";
    private static final String TARDIS_TEXT_SIDE_EXT = "png";
    private static final String TARDIS_TEXT_FLOOR = "/res/tardisFloor.png";
    private static final String TARDIS_TEXT_FLOOR_EXT = "png";

    static MyTexture tardisTextFront;
    static MyTexture tardisTextSide;
    static MyTexture tardisTextFloor;

    private static float HEIGHT = 1.5f;
    private static float BASE = 0.75f;

    private static int FLOAT_BYTES = 4;
    private static int shaderProgramDay;
    private static int shaderProgramNight;

    // 4 faces, floor and ceiling
    private static float[] vertexArray = {
            -BASE / 2, 0, -BASE / 2, -BASE / 2, 0, BASE / 2, -BASE / 2, HEIGHT, BASE / 2, -BASE / 2, HEIGHT, -BASE / 2,
            -BASE / 2, 0, BASE / 2, BASE / 2, 0, BASE / 2, BASE / 2, HEIGHT, BASE / 2, -BASE / 2, HEIGHT, BASE / 2,
            BASE / 2, 0, BASE / 2, BASE / 2, 0, -BASE / 2, BASE / 2, HEIGHT, -BASE / 2, BASE / 2, HEIGHT, BASE / 2,
            BASE / 2, 0, -BASE / 2, -BASE / 2, 0, -BASE / 2, -BASE / 2, HEIGHT, -BASE / 2, BASE / 2, HEIGHT, -BASE / 2,
            -BASE / 2, 0, -BASE / 2, -BASE / 2, 0, BASE / 2, BASE / 2, 0, BASE / 2, BASE / 2, 0, -BASE / 2,
            -BASE / 2, HEIGHT, -BASE / 2, -BASE / 2, HEIGHT, BASE / 2, BASE / 2, HEIGHT, BASE / 2, BASE / 2, HEIGHT, -BASE / 2};
    // this is shared between the faces. We need these 3 lines to draw 3 sides of the polygon in one call.
    private static float[] normalsArray = {
            -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0,
            0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1,
            1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0,
            0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1,
            0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0,
            0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0,
    };
    private static float[] textureArray = {
            0, 0, 1, 0, 1, 1, 0, 1,
            0, 0, 1, 0, 1, 1, 0, 1,
            0, 0, 1, 0, 1, 1, 0, 1};

    private static int OFFSET_N = vertexArray.length; //base offset for normals, within the VBO
    private static int OFFSET_T = OFFSET_N + normalsArray.length; // base offset for textures, within the VBO

    private static FloatBuffer vertices;
    private static int vboVerticesId;

    double[] myPos;
    private int shaderProgram;

    public Tardis(double x, double y, double z) {
        myPos = new double[3];
        myPos[0] = x;
        myPos[1] = y;
        myPos[2] = z;
    }

    // this is called from Game.init()
    public static void loadStaticData(GL2 gl) {
        tardisTextFront = new MyTexture(gl, TARDIS_TEXT_FRONT, TARDIS_TEXT_FRONT_EXT, true);
        tardisTextSide = new MyTexture(gl, TARDIS_TEXT_SIDE, TARDIS_TEXT_SIDE_EXT, true);
        tardisTextFloor = new MyTexture(gl, TARDIS_TEXT_FLOOR, TARDIS_TEXT_FLOOR_EXT, true);

        try {
            shaderProgramDay = Shader.initShaders(gl, vertexShaderDayPath, fragmentShaderDayPath);
            shaderProgramNight = Shader.initShaders(gl, vertexShaderNightPath, fragmentShaderNightPath);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        vertices = FloatBuffer.allocate(vertexArray.length+normalsArray.length+textureArray.length);
        vertices.put(vertexArray);
        vertices.put(normalsArray);
        vertices.put(textureArray);
        vertices.rewind();

        int[] temp = new int[1];
        gl.glGenBuffers(1, temp, 0);

        vboVerticesId = temp[0];
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboVerticesId);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, vertices.capacity()*FLOAT_BYTES, vertices, GL.GL_STATIC_DRAW);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
    }

    public void draw(GL2 gl, boolean isNight) {
        gl.glPushMatrix(); {
            gl.glTranslated(myPos[0], myPos[1], myPos[2]);
            drawTardis(gl);
        }
        gl.glPopMatrix();
    }

    private void drawTardis(GL2 gl) {
        float white[] = {1f, 1f, 1f, 1.0f};
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, white,0);

        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
        gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboVerticesId);

        //front face
        gl.glBindTexture(GL2.GL_TEXTURE_2D, tardisTextFront.getTextureId());
        gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);
        gl.glNormalPointer(GL.GL_FLOAT, 0, OFFSET_N*FLOAT_BYTES);
        gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, OFFSET_T*FLOAT_BYTES);//float has 4 bytes
        gl.glDrawArrays(GL2.GL_QUADS, 0, 4);

        //other 3 sides
        gl.glBindTexture(GL2.GL_TEXTURE_2D, tardisTextSide.getTextureId());
        gl.glVertexPointer(3, GL.GL_FLOAT, 0, 4*3*4);
        gl.glNormalPointer(GL.GL_FLOAT, 0, (OFFSET_N+4*3)*FLOAT_BYTES);
        gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, OFFSET_T*FLOAT_BYTES);//float has 4 bytes
        gl.glDrawArrays(GL2.GL_QUADS, 0, 12);

        // floor and ceiling
        gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
        gl.glPolygonOffset(-1f,-1f);//make tardis floor higher than ground.
        gl.glBindTexture(GL2.GL_TEXTURE_2D, tardisTextFloor.getTextureId());
        gl.glVertexPointer(3, GL.GL_FLOAT, 0, 16*3*4);//jump 16 vertices
        gl.glNormalPointer(GL.GL_FLOAT, 0, (OFFSET_N+16*3)*FLOAT_BYTES);
        gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, OFFSET_T*FLOAT_BYTES);//float has 4 bytes
        gl.glDrawArrays(GL2.GL_QUADS, 0, 8);
        gl.glDisable(GL2.GL_POLYGON_OFFSET_FILL);

        gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
        gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
    }
}
