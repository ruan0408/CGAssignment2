package ass2.spec;

import com.jogamp.opengl.util.FPSAnimator;

import javax.media.opengl.*;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;


/**
 * COMMENT: Comment Game 
 *
 * @author malcolmrn
 */
public class Game extends JFrame implements GLEventListener{

    private static final String vertexShaderDayPath = "/res/vertexShaderDay.glsl";
    private static final String fragmentShaderDayPath = "/res/fragmentShaderDay.glsl";
    private static final String vertexShaderNightPath = "/res/vertexShaderNight.glsl";
    private static final String fragmentShaderNightPath = "/res/fragmentShaderNight.glsl";

    private static final float[] AMBIENT_LIGHT = new float[]{1f, 1f, 1f, 1f};
    private static final float[] DIFFUSE_LIGHT = new float[]{1f, 1f, 1f, 1f};
    private static final float SPOTLIGHT_RADIUS = 2f;
    private static final float[] AMBIENT_LIGHT_NIGHT = new float[]{1f, 1f, 1f, 1f};

    private static final double FOV = 60;
    private static final double NEAR_PLANE_DIST = 0.01;
    private static final double FAR_PLANE_DIST = 20;
    private static final float SPOTLIGHT_CUTOFF = 30F;

    private static int shaderProgramDay;
    private static int shaderProgramNight;

    private Terrain myTerrain;
    private Avatar avatar;
    private Camera camera;

    private int shaderProgram;

    /**
     * Load a level file and display it.
     * 
     * @param args - The first argument is a level file in JSON format
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        Terrain terrain = LevelIO.load(new File(args[0]));
        Game game = new Game(terrain);
        game.run();
    }

    public Game(Terrain terrain) {
        super("Assignment 2");
        myTerrain = terrain;
        avatar = new Avatar(terrain);
        myTerrain.setAvatar(avatar);
        camera = new Camera(avatar);
    }

    /**
     * Run the game.
     */
    public void run() {
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        GLJPanel panel = new GLJPanel();
        panel.addGLEventListener(this);
        panel.addKeyListener(avatar);
        panel.addKeyListener(myTerrain);
        panel.addKeyListener(camera);

        // Add an animator to call 'display' at 60fps
        FPSAnimator animator = new FPSAnimator(60);
        animator.add(panel);
        animator.start();

        getContentPane().add(panel);
        setSize(800, 600);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

	@Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        setEnables(gl);
        setSunLightProperties(gl);
        setSpotlightProperties(gl);
        loadStaticData(gl);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        double ratio = (double)getWidth()/getHeight();
        GLU glu = new GLU();
        glu.gluPerspective(FOV, ratio, NEAR_PLANE_DIST, FAR_PLANE_DIST);
        gl.glMatrixMode(GL2.GL_MODELVIEW);

	}

    @Override
    public void display(GLAutoDrawable drawable) {

        GL2 gl = drawable.getGL().getGL2();
        if (myTerrain.isNight()) gl.glClearColor(0f, 0f, 0.1f, 0);
        else gl.glClearColor(0.95f, 0.95f, 1,0);

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        setShader(gl);
        camera.updateView(gl);
        myTerrain.drawScene(gl);
        correctLighting(gl);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {}

    private void setEnables(GL2 gl) {
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_NORMALIZE);
        gl.glEnable(GL2.GL_TEXTURE_2D);
    }

    private void setShader(GL2 gl) {

        if (myTerrain.isNight()) shaderProgram = Game.shaderProgramNight;
        else shaderProgram = Game.shaderProgramDay;

        gl.glUseProgram(shaderProgram);
        int colorMap = gl.glGetUniformLocation(shaderProgram, "colorMap");
        gl.glUniform1i(colorMap, 0);
    }

    private void setSunLightProperties(GL2 gl) {
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, AMBIENT_LIGHT, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, DIFFUSE_LIGHT, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, myTerrain.getSunDirectionHomogeneous(), 0);
    }

    private void setSpotlightProperties(GL2 gl) {
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, avatar.getPositionHomogeneousFloat(), 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, AMBIENT_LIGHT_NIGHT, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, DIFFUSE_LIGHT, 0);

        gl.glLightf(GL2.GL_LIGHT1, GL2.GL_SPOT_CUTOFF, SPOTLIGHT_CUTOFF);
    }

    private void loadStaticData(GL2 gl) {
        try {
            shaderProgramDay = Shader.initShaders(gl, vertexShaderDayPath, fragmentShaderDayPath);
            shaderProgramNight = Shader.initShaders(gl, vertexShaderNightPath, fragmentShaderNightPath);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        Terrain.loadStaticData(gl);
        Road.loadStaticData(gl);
        Tree.loadStaticData(gl);
        Sun.loadStaticData(gl);
        Avatar.loadStaticData(gl);
        Tardis.loadStaticData(gl);
    }

    private void correctLighting(GL2 gl) {
        if(myTerrain.isNight()) {
            float[] pos = avatar.getPositionHomogeneousFloat();
            pos[1] += 1;

            float[] spotLightDirection = avatar.getSpotlightVector();
            gl.glDisable(GL2.GL_LIGHT0);
            gl.glEnable(GL2.GL_LIGHT1);
            gl.glLightf(GL2.GL_LIGHT1, GL2.GL_SPOT_CUTOFF, SPOTLIGHT_CUTOFF);
            gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, pos, 0);
            gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPOT_DIRECTION, spotLightDirection, 0);
        } else {
            gl.glDisable(GL2.GL_LIGHT1);    // Disable spotlight
            gl.glEnable(GL2.GL_LIGHT0);     // Enable sun
            gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, myTerrain.getSunDirectionHomogeneous(), 0);
            gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, myTerrain.getSunlightColor(), 0);
        }
    }
}
