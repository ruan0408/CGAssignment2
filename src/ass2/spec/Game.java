package ass2.spec;

import com.jogamp.opengl.util.FPSAnimator;

import javax.media.opengl.*;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.swing.*;
import javax.swing.text.StringContent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;


/**
 * COMMENT: Comment Game 
 *
 * @author malcolmr
 */
public class Game extends JFrame implements GLEventListener{

    private final String TERRAIN_TEXT = "src/dirtGrass.jpg";
    private final String TERRAIN_TEXT_EXT = "jpg";
    private final String ROAD_TEXT = "src/asphalt.jpg";
    private final String ROAD_TEXT_EXT = "jpg";
    private final String TRUNK_TEXT = "src/trunk.jpg";
    private final String TRUNK_TEXT_EXT = "jpg";
    private final String LEAVES_TEXT = "src/leaves.jpg";
    private final String LEAVES_TEXT_EXT = "jpg";
    private final String SUN_TEXT = "src/sun.jpg";
    private final String SUN_TEXT_EXT = "jpg";

    private final double FOV = 120;
    private final double NEAR_PLANE_DIST = 0.01;
    private final double FAR_PLANE_DIST = 10;

    private Terrain myTerrain;
    private Avatar avatar;

    public Game(Terrain terrain) {
    	super("Assignment 2");
        myTerrain = terrain;
        avatar = new Avatar(terrain);
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

        // Add an animator to call 'display' at 60fps
        FPSAnimator animator = new FPSAnimator(60);
        animator.add(panel);
        animator.start();

        getContentPane().add(panel);
        setSize(800, 600);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    
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

	@Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glEnable(GL2.GL_DEPTH_TEST);

        // enable lighting
        gl.glEnable(GL2.GL_LIGHTING);
        //Turn on default light
        gl.glEnable(GL2.GL_LIGHT0);

        //light settings for day time
        float[] ambient = {1f, 1f, 1f, 1f};     // low ambient light
        float[] diffuse = { 1f, 1f, 1f, 1f };        // full diffuse colour
        float[] sunLight = Arrays.copyOf(myTerrain.getSunlight(), 4);
        sunLight[3] = 1;
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambient, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, sunLight, 0);

        //Night time light settings
        float radius = 3.5f; //light radius
        float[] low_diffuse = {0.8f,0.8f,0.8f,1f}; //lower diffuse light
        float[] low_ambient = {0.4f,0.4f,0.4f,1f}; //very low ambient light

        float[] pos = {(float)(avatar.getPosition()[0]),
                        (float)(avatar.getPosition()[1]),
                        (float)(avatar.getPosition()[2]),1}; //avatar position

        //darker ambient and diffuse light
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, low_ambient, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, low_diffuse, 0);

        //sets light source position to the avatar's
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, pos, 0);

        //attenuates light, making it weaker the further away from the source it is.
        gl.glLightf(GL2.GL_LIGHT1, GL2.GL_CONSTANT_ATTENUATION, 1f);
        gl.glLightf(GL2.GL_LIGHT1, GL2.GL_LINEAR_ATTENUATION, 1f/(2*radius));
        gl.glLightf(GL2.GL_LIGHT1, GL2.GL_QUADRATIC_ATTENUATION, 1f/(2*radius));

        //creates spotlight effect
        gl.glLightf(GL2.GL_LIGHT1, GL2.GL_SPOT_CUTOFF, 45.0F);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPOT_DIRECTION, pos, 0);

        // normalise normals (!)
        // this is necessary to make lighting work properly
        gl.glEnable(GL2.GL_NORMALIZE);
        gl.glEnable(GL2.GL_TEXTURE_2D);

        Terrain.texture = new MyTexture(gl,TERRAIN_TEXT,TERRAIN_TEXT_EXT,true);
        Road.texture =  new MyTexture(gl,ROAD_TEXT,ROAD_TEXT_EXT,true);
        Tree.leavesTexture =  new MyTexture(gl,LEAVES_TEXT,LEAVES_TEXT_EXT,true);
        Tree.trunkTexture =  new MyTexture(gl, TRUNK_TEXT,TRUNK_TEXT_EXT,true);
        Terrain.sunTexture = new MyTexture(gl, SUN_TEXT, SUN_TEXT_EXT, true);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
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
        gl.glLoadIdentity();

        //sets parameters for night time
        if(avatar.isNightTime()) {
            //dark blue background
            gl.glClearColor(0f, 0f, 0.03f, 0);
            gl.glDisable(GL2.GL_LIGHT0);
            gl.glEnable(GL2.GL_LIGHT1);
        }

        //sets default parameters, used during day time
        else {
            float[] lightColor;
            float[] fullDayLight = {1.0f,1.0f,1.0f,1.0f};
            float[] twilight = {0.6f,0.3f,0.6f,1.0f};
            float[] earlyDayLight = {0.7f,0.7f,0.7f,1.0f};

            float sunAngle = avatar.getCurrentRotation();

            if(sunAngle >= 0 && sunAngle <= 120) {
                lightColor = fullDayLight;
            }
            else if(sunAngle > 120 && sunAngle <= 240){
                lightColor = twilight;
            }
            else lightColor = earlyDayLight;

            if(avatar.isSunPositionChanged()){
                float[] sunLight = Arrays.copyOf(myTerrain.getSunlight(), 4);
                sunLight[3] = 1;
                gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, sunLight, 0);
                avatar.setSunPositionChanged(false);
            }
            gl.glDisable(GL2.GL_LIGHT1);
            gl.glEnable(GL2.GL_LIGHT0);
            gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, lightColor, 0);
            gl.glClearColor(1, 1, 1, 0);
        }

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        avatar.updateView(gl);
        myTerrain.draw(gl);
//        Tree t2 = new Tree(0,0,-1);
//        t2.draw(gl);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {}
}
