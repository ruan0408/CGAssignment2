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
 * @author malcolmr
 */
public class Game extends JFrame implements GLEventListener{

    private final String TERRAIN_TEXT = "src/dirtGrass.jpg";
    private final String TERRAIN_TEXT_EXT = "jpg";
    private final String ROAD_TEXT = "src/dirtRoad.jpg";
    private final String ROAD_TEXT_EXT = "jpg";
    private final String TRUNK_TEXT = "src/trunk.jpg";
    private final String TRUNK_TEXT_EXT = "jpg";
    private final String LEAVES_TEXT = "src/leaves.jpg";
    private final String LEAVES_TEXT_EXT = "jpg";

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
        float[] ambient = {1f, 1f, 1f, 1f};     // low ambient light
        float[] diffuse = { 1f, 1f, 1f, 1f };        // full diffuse colour
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambient, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, myTerrain.getSunlight(), 0);

//        gl.glEnable(GL2.GL_CULL_FACE);
//        gl.glCullFace(GL2.GL_BACK);

        // normalise normals (!)
        // this is necessary to make lighting work properly
        gl.glEnable(GL2.GL_NORMALIZE);

        gl.glEnable(GL2.GL_TEXTURE_2D);

        Terrain.texture = new MyTexture(gl,TERRAIN_TEXT,TERRAIN_TEXT_EXT,true);
        Road.texture =  new MyTexture(gl,ROAD_TEXT,ROAD_TEXT_EXT,true);
        Tree.leavesTexture =  new MyTexture(gl,LEAVES_TEXT,LEAVES_TEXT_EXT,true);
        Tree.trunkTexture =  new MyTexture(gl, TRUNK_TEXT,TRUNK_TEXT_EXT,true);
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

        gl.glClearColor(1, 1, 1, 0);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        avatar.updateView(gl);
        myTerrain.draw(gl);
        Tree t2 = new Tree(0,0,-1);
        t2.draw(gl);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {}
}
