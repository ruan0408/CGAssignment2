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

    private static final String vertexShaderSourceCode =
            "#version 120\n" +
                    "varying vec3 N;\n" +
                    "varying vec4 v;\n" +
                    "void main (void) {\n" +
                    "    v = gl_ModelViewMatrix * gl_Vertex;\n" +
                    "    N = vec3(normalize(gl_NormalMatrix * normalize(gl_Normal)));\n" +
                    "        gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;\n" +
                    "}\n";

    private static final String fragmentShaderSourceCode = "#version 120\n" +
            "varying vec3 N;\n" +
            "varying vec4 v;\n" +
            "void main (void) {\t\n" +
            "   vec4 ambient, globalAmbient;\n" +
            "ambient =  gl_LightSource[0].ambient * gl_FrontMaterial.ambient;\n" +
            "globalAmbient = gl_LightModel.ambient * gl_FrontMaterial.ambient;\n" +
            "vec3 normal, lightDir; \n" +
            "vec4 diffuse;\n" +
            "float NdotL;\n" +
            "normal = normalize(N);\n" +
            "lightDir = normalize(vec3(gl_LightSource[0].position));\n" +
            "    NdotL = max(dot(normal, lightDir), 0.0); \n" +
            "     diffuse = NdotL * gl_FrontMaterial.diffuse * gl_LightSource[0].diffuse; \n" +
            "    vec4 specular = vec4(0.0,0.0,0.0,1);\n" +
            "    float NdotHV;\n" +
            "    float NdotR;\n" +
            "    vec3 dirToView = normalize(vec3(-v));\n" +
            "    vec3 R = normalize(reflect(-lightDir,normal));\n" +
            "    vec3 H =  normalize(lightDir+dirToView); \n" +
            "if (NdotL > 0.0) {\n" +
            "NdotR = max(dot(R,dirToView ),0.0);\n" +
            "NdotHV = max(dot(normal, H),0.0);\n" +
            "specular = gl_FrontMaterial.specular * gl_LightSource[0].specular * pow(NdotHV,gl_FrontMaterial.shininess);\n" +
            "}\n" +
            "specular = clamp(specular,0,1);\n" +
            "    gl_FragColor = gl_FrontMaterial.emission + globalAmbient + ambient + diffuse + specular;\t\n" +
            "}\n";

    private static String vertexShaderSourceCodeNight =
            "#version 120\n" +
            "varying vec4 diffuse,globalAmbient,ambient, v;\n" +
            "varying vec3 normal,halfVector;\n" +
            "void main()\n" +
            "{   \n" +
            "    vec3 aux;\n" +
            "    normal = normalize(gl_NormalMatrix * gl_Normal);\n" +
            "    v = gl_ModelViewMatrix * gl_Vertex;\n" +
            "    halfVector = gl_LightSource[1].halfVector.xyz;\n" +
            "    diffuse = gl_FrontMaterial.diffuse * gl_LightSource[1].diffuse;\n" +
            "    ambient = gl_FrontMaterial.ambient * gl_LightSource[1].ambient;\n" +
            "    globalAmbient = gl_LightModel.ambient * gl_FrontMaterial.ambient;\n" +
            "    gl_Position = ftransform();\n" +
            "} ";

    private static String fragmentShaderSourceCodeNight = "#version 120\n" +
            "varying vec4 diffuse,globalAmbient, ambient, v;\n" +
            "varying vec3 normal,halfVector;\n" +
            "void main()\n" +
            "{\n" +
            "    vec3 N,normalizedHalfVector,viewV,lightDir;\n" +
            "    float NdotL,NdotHV;\n" +
            "    vec4 color = globalAmbient;\n" +
            "    float attenuation, dist;\n" +
            "    N = normalize(normal);\n" +
            "    lightDir = vec3(gl_LightSource[1].position-v);\n" +
            "    dist = length(lightDir);\n" +
            "    NdotL = max(dot(N,normalize(lightDir)),0.0);\n" +
            "    if (NdotL > 0.0) {\n" +
            "        attenuation = 1.0 / (gl_LightSource[1].constantAttenuation +\n" +
            "                gl_LightSource[1].linearAttenuation * dist +\n" +
            "                gl_LightSource[1].quadraticAttenuation * dist * dist);\n" +
            "        color += attenuation * (diffuse * NdotL + ambient);\n" +
            "        normalizedHalfVector = normalize(halfVector);\n" +
            "        NdotHV = max(dot(N,normalizedHalfVector),0.0);\n" +
            "        color += attenuation * gl_FrontMaterial.specular * gl_LightSource[1].specular * pow(NdotHV,gl_FrontMaterial.shininess);\n" +
            "    }\n" +
            "    gl_FragColor = color;\n" +
            "}";


    private int shaderProgram = Integer.MIN_VALUE;
    private int shaderProgramNight;

    static MyTexture tardisTexture;
    double[] myPos;

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
            }catch (Exception e){
                e.printStackTrace();
                System.exit(1);
            }
        }
        if(isNight) gl.glUseProgram(shaderProgramNight);
        else gl.glUseProgram(shaderProgram);

        GLUT glut = new GLUT();

        float body[] = {0.08f, 0.0f, 1f, 1.0f};
        float lamp[] = {0.5f,0.5f,0.5f};

        gl.glPushMatrix();{
            // Material properties of the tardis' body
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, body,0);
            float matSpec[] = { 1.0f, 1.0f, 1,0f, 1.0f };
            float matShine[] = { 10f };
            float emm[] = {0.0f, 0.0f, 0.4f, 1.0f};
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, matSpec,0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, matShine,0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, emm,0);

            gl.glTranslated(myPos[0],myPos[1],myPos[2]);
            glut.glutSolidCube(0.7f);

            gl.glTranslated(0,0.7f,0);
            glut.glutSolidCube(0.7f);
            // Material properties of the tardis' lamp
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, lamp,0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, new float[] {0.0f,0.0f,0.0f},0);

            gl.glTranslated(0,0.4f,0);
            glut.glutSolidSphere(0.05,15,15);

        }
        gl.glPopMatrix();
    }
}
