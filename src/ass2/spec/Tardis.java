package ass2.spec;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.gl2.GLUT;
import com.sun.deploy.util.BufferUtil;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.media.opengl.GL2;

/**
 * Created by gervasio on 18/10/15.
 */

//Corresponds to the "other" objet specified by the assignment spec. It's a (crude) 3D rendering of the Doctor's
// TARDIS (Time And Relative Dimensions In Space) from the tv show Doctor Who. Since the Doctor is a time traveler
//there can be more than one TARDIS per map =)
public class Tardis {

    private static final String vertexShaderSourceCode =
            "#version 120\n" +
                    "varying vec3 N;\n" +
                    "varying vec4 v;\n" +
                    "void main(){\n" +
                    "  v = gl_ModelViewMatrix * gl_Vertex;\n" +
                    "  N = vec3(normalize(gl_NormalMatrix * normalize(gl_Normal)));\n" +
                    "  gl_TexCoord[0] = gl_MultiTexCoord0;\n" +
                    "  gl_Position = ftransform();\n" +
                    " }";

    private static final String fragmentShaderSourceCode =
            "#version 120\n" +
                    "uniform sampler2D colorMap;\n" +
                    "varying vec3 N;\n" +
                    "            varying vec4 v;\n" +
                    "            void main (void) {\n" +
                    "               vec4 ambient, globalAmbient;\n" +
                    "            ambient =  gl_LightSource[0].ambient * gl_FrontMaterial.ambient;\n" +
                    "            globalAmbient = gl_LightModel.ambient * gl_FrontMaterial.ambient;\n" +
                    "            vec3 normal, lightDir; \n" +
                    "            vec4 diffuse;\n" +
                    "            float NdotL;\n" +
                    "            normal = normalize(N);\n" +
                    "            lightDir = normalize(vec3(gl_LightSource[0].position));\n" +
                    "                NdotL = max(dot(normal, lightDir), 0.0); \n" +
                    "                 diffuse = NdotL * gl_FrontMaterial.diffuse * gl_LightSource[0].diffuse; \n" +
                    "                vec4 specular = vec4(0.0,0.0,0.0,1);\n" +
                    "                float NdotHV;\n" +
                    "                float NdotR;\n" +
                    "                vec3 dirToView = normalize(vec3(-v));\n" +
                    "                vec3 R = normalize(reflect(-lightDir,normal));\n" +
                    "                vec3 H =  normalize(lightDir+dirToView); \n" +
                    "            if (NdotL > 0.0) {\n" +
                    "            NdotR = max(dot(R,dirToView ),0.0);\n" +
                    "            NdotHV = max(dot(normal, H),0.0);\n" +
                    "            specular = gl_FrontMaterial.specular * gl_LightSource[0].specular * pow(NdotHV,gl_FrontMaterial.shininess);\n" +
                    "            }\n" +
                    "            specular = clamp(specular,0,1);\n" +
                    "                gl_FragColor = (gl_FrontMaterial.emission + globalAmbient + ambient + diffuse + specular)*texture2D(colorMap, gl_TexCoord[0].st );\n" +
                    "            }";

    private static final String vertexShaderSourceCodeNight =
            "#version 120\n" +
            "uniform sampler2D colorMap;\n" +
            "varying vec4 v;\n" +
            "void main()\n" +
            "{   \n" +
            "    v = gl_ModelViewMatrix * gl_Vertex;\n" +
            "    gl_TexCoord[0] = gl_MultiTexCoord0;\n" +
            "    gl_Position = ftransform();\n" +
            "} ";

    private static final String fragmentShaderSourceCodeNight = "#version 120\n" +
            "varying vec4 v;\n" +
            "uniform sampler2D colorMap;\n" +
            "void main()\n" +
            "{\n" +
            "    vec3 lightDir;\n" +
            "    float dist;\n" +
            "    lightDir = vec3(gl_LightSource[1].position-v);\n" +
            "    dist = length(lightDir);\n" +
            "    gl_FragColor = (2.0/(dist*dist))*texture2D(colorMap, gl_TexCoord[0].st);\n" +
            "}";

    private static double HEIGHT = 1.5;
    private static double BASE = 0.75;

    private int shaderProgram = Integer.MIN_VALUE;
    private int shaderProgramNight;

    static MyTexture tardisTextFront;
    static MyTexture tardisTextSide;
    static MyTexture tardisTextFloor;
    static MyTexture texture;

    double[] myPos;
    private FloatBuffer cubeFaceVertices;
    private ShortBuffer indices;
    private int VBOVertices;
    private int VBOIndices;

    public Tardis(double x, double y, double z){
        myPos = new double[3];
        myPos[0] = x;
        myPos[1] = y;
        myPos[2] = z;
    }

    public void draw(GL2 gl, boolean isNight){
        //if the shader program is yet to be initialized, do the due processing
        if (shaderProgram == Integer.MIN_VALUE){
            try {
                shaderProgram = Shader.initShaders(gl, vertexShaderSourceCode, fragmentShaderSourceCode);
                shaderProgramNight = Shader.initShaders(gl,vertexShaderSourceCodeNight,fragmentShaderSourceCodeNight);
                initTardisBodyVBO(gl);
            }catch (Exception e){
                e.printStackTrace();
                System.exit(1);
            }
        }
        if(isNight) gl.glUseProgram(shaderProgramNight);
        else gl.glUseProgram(shaderProgram);

        gl.glPushMatrix();
        {
            gl.glTranslated(myPos[0], myPos[1]+1, myPos[2]);
            drawTardisBody(gl);
           // drawTardis(gl);
        }gl.glPopMatrix();
    }

    private void initTardisBodyVBO(GL2 gl) {
        float[] vertexArray = {
                -0.5f,  1.3f, -0.5f, //0
                0.5f,  1.3f, -0.5f,//1
                0.5f, -1.3f, -0.5f,//2
                -0.5f, -1.3f, -0.5f,//3
                -0.5f,  1.3f, 0.5f,//4
                0.5f,  1.3f, 0.5f,//5
                0.5f, -1.3f, 0.5f,//6
                -0.5f, -1.3f, 0.5f//7
                };

        cubeFaceVertices = FloatBuffer.allocate(vertexArray.length);
        cubeFaceVertices.put(vertexArray);
        cubeFaceVertices.flip();

        short[] indexArray={0, 1, 2, 0, 2, 3,
                            4, 5, 6, 4, 6, 7,
                            5, 1, 2, 5, 2, 6,
                            4, 0, 3, 4, 3, 7,
                            4, 0, 1, 4, 1, 5,
                            7, 3, 2, 7, 2, 5
                            };
        indices = ShortBuffer.allocate(indexArray.length);
        indices.put(indexArray);
        indices.flip();

        int[] temp = new int[2];
        gl.glGenBuffers(2, temp, 0);

        VBOVertices = temp[0];
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBOVertices);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeFaceVertices.capacity() * 4, cubeFaceVertices, GL.GL_STATIC_DRAW);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

        VBOIndices = temp[1];
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, VBOIndices);
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, indices.capacity() *2, indices, GL.GL_STATIC_DRAW);
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    private void drawTardisBody(GL2 gl) {
        float white[] = {1f, 1f, 1f, 1.0f};
        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        {
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBOVertices);
            gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);
            gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, VBOIndices);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, white,0);

            gl.glBindTexture(GL2.GL_TEXTURE_2D, texture.getTextureId());
            gl.glDrawElements(GL.GL_TRIANGLES, indices.capacity(), GL.GL_UNSIGNED_SHORT, 0);

        }gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);

        gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
    }

    private void drawTardis(GL2 gl) {
        gl.glBindTexture(GL2.GL_TEXTURE_2D, Tardis.tardisTextFloor.getTextureId());
        gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
        gl.glPolygonOffset(-1f,-1f);//make tardis floor higher than ground.
        gl.glBegin(GL2.GL_QUADS);
        {   //ceiling
            gl.glTexCoord2d(0, 0);gl.glVertex3d(-BASE/2, 0, -BASE/2);
            gl.glTexCoord2d(1, 0);gl.glVertex3d(BASE/2, 0, -BASE/2);
            gl.glTexCoord2d(1, 1);gl.glVertex3d(BASE/2, 0, BASE/2);
            gl.glTexCoord2d(0, 1);gl.glVertex3d(-BASE/2, 0, BASE/2);

            //floor
            gl.glTexCoord2d(0, 0);gl.glVertex3d(-BASE/2, HEIGHT, -BASE/2);
            gl.glTexCoord2d(1, 0);gl.glVertex3d(-BASE/2, HEIGHT, BASE/2);
            gl.glTexCoord2d(1, 1);gl.glVertex3d(BASE/2, HEIGHT, BASE/2);
            gl.glTexCoord2d(0, 1);gl.glVertex3d(BASE/2, HEIGHT, -BASE/2);
        }gl.glEnd();

        gl.glDisable(GL2.GL_POLYGON_OFFSET_FILL);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, Tardis.tardisTextFront.getTextureId());
        gl.glBegin(GL2.GL_QUADS); {
            gl.glTexCoord2d(0, 0);gl.glVertex3d(-BASE/ 2, 0, -BASE / 2);
            gl.glTexCoord2d(1, 0);gl.glVertex3d(-BASE/ 2, 0, BASE / 2);
            gl.glTexCoord2d(1, 1);gl.glVertex3d(-BASE/ 2, HEIGHT, BASE / 2);
            gl.glTexCoord2d(0, 1);gl.glVertex3d(-BASE/ 2, HEIGHT, -BASE / 2);
        }gl.glEnd();

        gl.glBindTexture(GL2.GL_TEXTURE_2D, Tardis.tardisTextSide.getTextureId());
        gl.glBegin(GL2.GL_QUADS);{
            gl.glTexCoord2d(0, 0);gl.glVertex3d(-BASE/ 2, 0, BASE / 2);
            gl.glTexCoord2d(1, 0);gl.glVertex3d(BASE/ 2, 0, BASE / 2);
            gl.glTexCoord2d(1, 1);gl.glVertex3d(BASE/ 2, HEIGHT, BASE / 2);
            gl.glTexCoord2d(0, 1);gl.glVertex3d(-BASE/ 2, HEIGHT, BASE / 2);

            gl.glTexCoord2d(0, 0);gl.glVertex3d(BASE / 2, 0, BASE / 2);
            gl.glTexCoord2d(1, 0);gl.glVertex3d(BASE / 2, 0, -BASE / 2);
            gl.glTexCoord2d(1, 1);gl.glVertex3d(BASE / 2, HEIGHT, -BASE / 2);
            gl.glTexCoord2d(0, 1);gl.glVertex3d(BASE / 2, HEIGHT, BASE / 2);

            gl.glTexCoord2d(0, 0);gl.glVertex3d(BASE / 2, 0, -BASE / 2);
            gl.glTexCoord2d(1, 0);gl.glVertex3d(-BASE / 2, 0, -BASE / 2);
            gl.glTexCoord2d(1, 1);gl.glVertex3d(-BASE / 2, HEIGHT, -BASE / 2);
            gl.glTexCoord2d(0, 1);gl.glVertex3d(BASE / 2, HEIGHT, -BASE / 2);
        }gl.glEnd();
        gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
    }
}
