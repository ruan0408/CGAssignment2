package ass2.spec;

import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES2;

/**
 * Created by gervasio on 18/10/15.
 */
public class Shader {
    private String[] mySource;
    private int myType;
    private int myID;

    public Shader(int type, String sourceCode){
        myType = type;
        mySource = new String[1];
        mySource[0] = sourceCode;
    }

    public void compile(GL2 gl2) throws Exception {
        myID = gl2.glCreateShader(myType);
        gl2.glShaderSource(myID, 1, mySource, new int[]{mySource[0].length()}, 0);
        gl2.glCompileShaderARB(myID);

        //Check compile status.
        int[] compiled = new int[1];
        gl2.glGetShaderiv(myID, GL2ES2.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            int[] logLength = new int[1];
            gl2.glGetShaderiv(myID, GL2ES2.GL_INFO_LOG_LENGTH, logLength, 0);

            byte[] log = new byte[logLength[0]];
            gl2.glGetShaderInfoLog(myID, logLength[0], (int[]) null, 0, log, 0);

            throw new Exception("Error compiling the shader: " + new String(log));
        }
    }

    public static int initShaders(GL2 gl, String vertexShaderSouce, String fragmentShaderSource) throws Exception {
        Shader vertexShader = new Shader(GL2.GL_VERTEX_SHADER, vertexShaderSouce);
        vertexShader.compile(gl);

        Shader fragmentShader = new Shader(GL2.GL_FRAGMENT_SHADER, fragmentShaderSource);
        fragmentShader.compile(gl);

        //Each shaderProgram must have
        //one vertex shader and one fragment shader.
        int shaderprogram = gl.glCreateProgram();
        gl.glAttachShader(shaderprogram, vertexShader.myID);
        gl.glAttachShader(shaderprogram, fragmentShader.myID);


        gl.glLinkProgram(shaderprogram);


        int[] error = new int[2];
        gl.glGetProgramiv(shaderprogram, GL2.GL_LINK_STATUS, error, 0);
        if (error[0] != GL2.GL_TRUE) {
            int[] logLength = new int[1];
            gl.glGetProgramiv(shaderprogram, GL2ES2.GL_INFO_LOG_LENGTH, logLength, 0);

            byte[] log = new byte[logLength[0]];
            gl.glGetProgramInfoLog(shaderprogram, logLength[0], (int[]) null, 0, log, 0);

            System.out.printf("Failed to link shader! %s\n", new String(log));
            throw new Exception("Error linking the shader: " + new String(log));
        }

        gl.glValidateProgram(shaderprogram);

        gl.glGetProgramiv(shaderprogram, GL2.GL_VALIDATE_STATUS, error, 0);
        if (error[0] != GL2.GL_TRUE) {
            System.out.printf("Failed to validate shader!\n");
            throw new Exception("program failed to validate");
        }


        return shaderprogram;
    }
}
