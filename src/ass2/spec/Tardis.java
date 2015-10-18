package ass2.spec;

import com.jogamp.opengl.util.gl2.GLUT;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

/**
 * Created by gervasio on 18/10/15.
 */

//Corresponds to the "other" objet specified by the assignment spec. It's a (crude) 3D rendering of the Doctor's
// TARDIS (Time And Relative Dimensions In Space) from the tv show Doctor Who. Since the Doctor is a time traveler
//there can be more than one TARDIS per map =)
public class Tardis {

    private static final String fragmentShaderSourceCode = "#version 120\n" +
            "varying vec3 N;\n" +
            "varying vec4 v;\n" +
            "/* We are only taking into consideration light0 and assuming it is a point light */\n" +
            "void main (void) {\t\n" +
            "   vec4 ambient, globalAmbient;\n" +
            "    /* Compute the ambient and globalAmbient terms */\n" +
            "\tambient =  gl_LightSource[0].ambient * gl_FrontMaterial.ambient;\n" +
            "\tglobalAmbient = gl_LightModel.ambient * gl_FrontMaterial.ambient;\n" +
            "\t/* Diffuse calculations */\n" +
            "\tvec3 normal, lightDir; \n" +
            "\tvec4 diffuse;\n" +
            "\tfloat NdotL;\n" +
            "\t/* normal has been interpolated and may no longer be unit length so we need to normalise*/\n" +
            "\tnormal = normalize(N);\n" +
            "\t/* normalize the light's direction. */\n" +
            "\tlightDir = normalize(vec3(gl_LightSource[0].position - v));\n" +
            "    NdotL = max(dot(normal, lightDir), 0.0); \n" +
            "    /* Compute the diffuse term */\n" +
            "     diffuse = NdotL * gl_FrontMaterial.diffuse * gl_LightSource[0].diffuse; \n" +
            "    vec4 specular = vec4(0.0,0.0,0.0,1);\n" +
            "    float NdotHV;\n" +
            "    float NdotR;\n" +
            "    vec3 dirToView = normalize(vec3(-v));\n" +
            "    vec3 R = normalize(reflect(-lightDir,normal));\n" +
            "    vec3 H =  normalize(lightDir+dirToView); \n" +
            "    /* compute the specular term if NdotL is  larger than zero */\n" +
            "\tif (NdotL > 0.0) {\n" +
            "\t\tNdotR = max(dot(R,dirToView ),0.0);\n" +
            "\t\t//Can use the halfVector instead of the reflection vector if you wish \n" +
            "\t\tNdotHV = max(dot(normal, H),0.0);\n" +
            "\t\tspecular = gl_FrontMaterial.specular * gl_LightSource[0].specular * pow(NdotHV,gl_FrontMaterial.shininess);\n" +
            "\t}\n" +
            "\tspecular = clamp(specular,0,1);\n" +
            "    gl_FragColor = gl_FrontMaterial.emission + globalAmbient + ambient + diffuse + specular;\t\n" +
            "}\n";

    private static final String vertexShaderSourceCode =
            "#version 120\n" +
            "varying vec3 N;\n" +
            "varying vec4 v;\n" +
            "void main (void) {\n" +
            "    v = gl_ModelViewMatrix * gl_Vertex;\n" +
            "    N = vec3(normalize(gl_NormalMatrix * normalize(gl_Normal)));\n" +
            "        gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;\n" +
            "}\n";


    private int shaderProgram = Integer.MIN_VALUE;

    static MyTexture tardisTexture;
    double[] myPos;

    public Tardis(double x, double y, double z){
        myPos = new double[3];
        myPos[0] = x;
        myPos[1] = y;
        myPos[2] = z;
    }

    public void draw(GL2 gl){
        //if the shader program is yet to be initialized, do the due processing
        if (shaderProgram == Integer.MIN_VALUE){
            try {
                shaderProgram = Shader.initShaders(gl, vertexShaderSourceCode, fragmentShaderSourceCode);
            }catch (Exception e){
                e.printStackTrace();
                System.exit(1);
            }
        }
        gl.glUseProgram(shaderProgram);

        GLUT glut = new GLUT();

        float body[] = {0.08f, 0.0f, 1f, 1.0f};
        float lamp[] = {0.5f,0.5f,0.5f};

        gl.glPushMatrix();{
            // Material properties of the tardis' body
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, body,0);

            gl.glTranslated(myPos[0],myPos[1],myPos[2]);
            glut.glutSolidCube(0.7f);

            gl.glTranslated(0,0.7f,0);
            glut.glutSolidCube(0.7f);
            // Material properties of the tardis' lamp
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, lamp,0);
            gl.glTranslated(0,0.4f,0);
            glut.glutSolidSphere(0.05,15,15);

        }
        gl.glPopMatrix();
    }
}
