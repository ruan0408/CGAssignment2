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
    private Terrain myTerrain;
    private Camera camera;
    private Avatar avatar;

    public Game(Terrain terrain) {
    	super("Assignment 2");
        myTerrain = terrain;
        camera = new Camera(180, terrain);
        //view = new View(180, terrain);
        avatar = new Avatar(0, terrain);
    }
    
    /** 
     * Run the game.
     *
     */
    public void run() {
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        GLJPanel panel = new GLJPanel();
        panel.addGLEventListener(this);
        panel.addKeyListener(camera);

        //Avatar avatar = new Avatar(0, myTerrain);
        panel.addKeyListener(avatar);
        //view = new View(avatar);

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

        GLU glu = new GLU();
        glu.gluPerspective(90, 1, 0.01, 20); //TODO: use same aspect ratio as viewport
        //glu.gluLookAt(0, 5, -5, 3, 0, 0, 0, 0, 1);
        gl.glMatrixMode(GL2.GL_MODELVIEW);

	}

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glLoadIdentity();

        gl.glClearColor(1, 1, 1, 0);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

//        double[] trans = camera.getTranslation();
//        gl.glRotated(-camera.getRotation(), 0, 1, 0);
//        gl.glTranslated(-trans[0], -trans[1], -trans[2]);
        //view.setFirstPerson(true);
        //view.updateView(gl);
        avatar.updateView(gl);

        myTerrain.draw(gl);
//        Tree t = new Tree(0,0,0);
//        Tree t1 = new Tree(0,0,1);
//        Tree t2 = new Tree(0,0,-1);
//        t.draw(gl);
//        t1.draw(gl);
//        t2.draw(gl);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        // TODO Auto-generated method stub

    }
}
