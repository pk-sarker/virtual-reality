package balance;

/**
 *
 * @author pijuskumar
 */

import java.awt.*;
import java.awt.event.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;

public class Balance extends Frame
    implements ActionListener, WindowListener, KeyListener {

    private static final double FRONT_CLIP_DISTANCE = 0.05;
    private static final double BACK_CLIP_DISTANCE = 20000.0;
    private static final double Field_Of_View = 1.0;
    private MenuItem close_menu;
    private Transform3D plane_trans, ball_trans;
    private TransformGroup transformBG, planeTG, ballTG;
    private ColorCube col_cube = new ColorCube(1.5);
    private Sphere ball = createSphere(0.3f,"fire.jpg");
    
    private long stime=5000;
    private float spos = -1.5f;
    private float epos = 4.7f;
    
    public Balance(){
	super("Java3D Project");

	MenuBar menubar = new MenuBar();
        
	// File menu
	Menu file_menu = new Menu("File");
	menubar.add(file_menu);

        //Appearance polygon1Appearance = new Appearance();
        
	close_menu = new MenuItem("Exit");
	close_menu.addActionListener(this);
	file_menu.add(close_menu);

	setMenuBar(menubar);
	addWindowListener(this);
		
	GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
	template.setSceneAntialiasing(GraphicsConfigTemplate.PREFERRED);
	GraphicsEnvironment env =
	    GraphicsEnvironment.getLocalGraphicsEnvironment();
	GraphicsDevice device[] = env.getScreenDevices();
	GraphicsConfiguration config =
	    device[0].getBestConfiguration(template);
	Canvas3D canvas = new Canvas3D(config);
	canvas.addKeyListener(this);

	// add the canvas to this frame. Since this is the only thing added to
	// the main frame we don't care about layout managers etc.
	add(canvas, BorderLayout.CENTER);

	VirtualUniverse universe = new VirtualUniverse();
	Locale locale = new Locale(universe);

	BranchGroup viewBG = new BranchGroup();
	View view = new View();
	PhysicalBody body = new PhysicalBody();
	PhysicalEnvironment environment = new PhysicalEnvironment();
	view.addCanvas3D(canvas);
	view.setPhysicalBody(body);
	view.setPhysicalEnvironment(environment);
	view.setFrontClipDistance(FRONT_CLIP_DISTANCE);
	view.setBackClipDistance(BACK_CLIP_DISTANCE);
	view.setFieldOfView(Field_Of_View);

	ViewPlatform platform = new ViewPlatform(); 
	view.attachViewPlatform(platform);

	// move the viewer back 10000.0 meters
	Transform3D viewer = new Transform3D();
	viewer.set(new Vector3f(0.0f, 1.4f, 10000.0f));	
	TransformGroup platformTG = new TransformGroup(viewer);	
	platformTG.addChild(platform);
	viewBG.addChild(platformTG);
	locale.addBranchGraph(viewBG);
			
	BranchGroup sceneBG = new BranchGroup();
        
	//sceneBG.addChild(new Ground(10000.0f,10000.0f));
        
        //sceneBG.addChild(new Background(10000.0f,10000.0f));
        
        
        
        transformBG = new TransformGroup();
	transformBG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	transformBG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        
        plane_trans = new Transform3D();
	plane_trans.set(new Vector3f(0.0f, 1.5f, 9985.0f));
        
        planeTG = new TransformGroup(plane_trans);
        planeTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	planeTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        
        Point3f[] top_plane ={  new Point3f(-5.0f,5.0f,0.0f) , 
                                new Point3f(-5.00f,2.0f,0.0f), 
                                new Point3f(5.00f,2.0f,0.0f), 
                                new Point3f(5.0f,5.0f,0.0f) };
        planeTG.addChild(new Plane("grass.jpg", top_plane));
        
        Point3f[] mid_plane ={  new Point3f(-5.00f,2.0f,0.0f) , 
                                new Point3f(-5.0f,-1.0f,0.0f), 
                                new Point3f(5.0f,-1.0f,0.0f), 
                                new Point3f(5.00f,2.0f,0.0f) };
        planeTG.addChild(new Plane("courtyard_seamless.jpg", mid_plane));
        
        Point3f[] bottom_plane ={  new Point3f(-5.00f,-1.0f,0.0f) , 
                                new Point3f(-5.00f,-4.0f,0.0f), 
                                new Point3f(5.00f,-4.0f,0.0f), 
                                new Point3f(5.00f,-1.0f,0.0f) };
        planeTG.addChild(new Plane("wall.jpg", bottom_plane));
        
        
        ball_trans = new Transform3D();
	ball_trans.set(new Vector3f(0.0f, 0.0f, 0.3f));
        
        ballTG = new TransformGroup(ball_trans);
	ballTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	ballTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        
        Vector3f translate_ball = new Vector3f();
        ball_trans.get( translate_ball ); 
        
        translate_ball.x = 0.0f;
        translate_ball.y =  -3.00f;
        
        ball_trans.setTranslation( translate_ball );
        ballTG.setTransform(ball_trans);
        ballTG.addChild(ball);
        
        planeTG.addChild(ballTG);
        
        
        /* * * * Initial Rotation of the Plane * * * */
        Vector3f translate = new Vector3f();
        
      
        plane_trans.get( translate );
        
        Transform3D rotate = new Transform3D();
        rotate.rotX(-Math.PI/3.0d);
        plane_trans.mul(rotate);
        plane_trans.setTranslation( translate );
        planeTG.setTransform(plane_trans);   
        
        /* * * * End Initial Plane Rotation * * * */
        
        
        /* Position Interpolator */
        
	Alpha posAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE|Alpha.DECREASING_ENABLE,
				     0, 0,
				     stime, 0, 0,
				     stime, 0, 0);
	PositionInterpolator posit =
	    new PositionInterpolator(posAlpha,
				     ballTG,
				     ball_trans,
				     spos, epos);
        BoundingSphere rotatorBounds = new BoundingSphere(
                new Point3d(0.0f,-3.0f,0.0f), 5.0);
		posit.setSchedulingBounds(rotatorBounds);
        ballTG.addChild(posit);
        /*  END */
        
        
        transformBG.addChild(planeTG);
        
    	sceneBG.addChild(transformBG);
        
        
	sceneBG.compile();
	locale.addBranchGraph( sceneBG );
		
	setVisible(true);
	setSize(1024,1024);			
    }
    
    public Sphere createSphere(float radius, String imagefile){
    
        Appearance sunApp = new Appearance();
        TextureLoader texture = new TextureLoader(imagefile, "RGB", new Container());
        sunApp.setTexture(texture.getTexture());        
        Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
        Color3f white = new Color3f(1.0f, 1.0f, 1.0f);  
        sunApp.setMaterial(new Material(white, black, white, black, 1.0f));   
                  
        Sphere sphere = new Sphere(radius, Sphere.GENERATE_TEXTURE_COORDS |
                                         Sphere.GENERATE_NORMALS, sunApp);
        
        return sphere;
        
    }
    
    /* * * * * Movement Function * * * * */
    
    public PositionInterpolator movement(){
        
        Alpha posAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE|Alpha.DECREASING_ENABLE,
				     0, 0,
				     5000, 0, 0,
				     5000, 0, 0);
	PositionInterpolator posit =
	    new PositionInterpolator(posAlpha,
				     ballTG,
				     ball_trans,
				     0.0f, 4.7f);
        BoundingSphere rotatorBounds = new BoundingSphere(
                new Point3d(0.0f,-3.0f,0.0f), 5.0);
		posit.setSchedulingBounds(rotatorBounds);
        ballTG.addChild(posit);
        
        return posit;
    }
    
    /**
     * An mouse action has occurred. Used to process menu item selection.
     * @param evt The event that caused this method to be called.
     */
    public void actionPerformed(ActionEvent evt)
    {
	Object src = evt.getSource();

	if(src == close_menu)
	    System.exit(0);
    }

    /**
     * Exit the application
     * @param evt The event that caused this method to be called.
     */
    public void windowClosing(WindowEvent evt)
    {
	System.exit(0);
    }

    //     Ignored
    public void windowActivated(WindowEvent evt){}

    //     Ignored
    public void windowClosed(WindowEvent evt){}

    //     Ignored
    public void windowDeactivated(WindowEvent evt){}

    //     Ignored
    public void windowDeiconified(WindowEvent evt){}

    //     Ignored
    public void windowIconified(WindowEvent evt){}

    //     Ignored
    public void windowOpened(WindowEvent evt){}
   
    //     Ignored
    public void keyReleased(KeyEvent evt) {}

    //     Ignored
    public void keyTyped(KeyEvent evt) {}

    // The function to work on for the exercise
    public void keyPressed(KeyEvent evt) {
        Vector3f translate = new Vector3f();
        Vector3f transball = new Vector3f();
      
        plane_trans.get( translate ); 
        ball_trans.get( transball ); 
      
        if (evt.getKeyChar()=='a') {
            Transform3D rotate = new Transform3D();
            rotate.rotY(Math.PI/36.0d);
            plane_trans.mul(rotate);
            
            //transball.x += 0.4f;
            
            
            
        }
        if (evt.getKeyChar()=='d') {
            Transform3D rotate = new Transform3D();
            rotate.rotY(-Math.PI/36.0d);
            plane_trans.mul(rotate);
            //transball.x -= 0.3f;
        }
        
        /*if (evt.getKeyChar()=='s') {
            Transform3D rotate = new Transform3D();
            rotate.rotX(-Math.PI/36.0d);
            plane_trans.mul(rotate);
            transball.y += 0.5f;
        }*/
        
        /*if (evt.getKeyChar()=='w') {
            Transform3D rotate = new Transform3D();
            rotate.rotX(Math.PI/36.0d);
            plane_trans.mul(rotate);
            transball.y -= 0.4f;
            
        }*/
        
        if (evt.getKeyChar()=='i') {
            //transball.x = 0.0f;
            //transball.y = -3.00f;
            //transball.z = 0.3f;
            plane_trans.set(new Vector3f(0.0f, 1.5f, 9985.0f));
            
            Transform3D rotate = new Transform3D();
            rotate.rotX(-Math.PI/3.0d);
            plane_trans.mul(rotate);
        }
        
        if(evt.getKeyChar()=='l'){
        stime = 2000;
        movement();
            
        
        }
        
        // (-5,5) (-5,-4) (5,-4) (5,5)
        // x varies from -5 to 5
        // y varies from 5 to -4
        
        /*if(transball.x< -5 || transball.x>5){
            transball.x = 0.0f;
            transball.y = -3.00f;
        }
        if(transball.y < -4 || transball.y>5){
            transball.x = 0.0f;
            transball.y = -3.00f;
        }*/
        
            
        //ball_trans.setTranslation( transball );
        //ballTG.setTransform(ball_trans);
        
        plane_trans.setTranslation( translate );
        planeTG.setTransform(plane_trans);
        
        // make a rectangle and usse its four coordinates to check where the ball is in the area or outside
        // resetball();
        
        
        
    }
    
    public void resetball(){
        Vector3f transball = new Vector3f();
        Vector3f translate = new Vector3f();
        int inside;
        plane_trans.get( translate ); 
        ball_trans.get( transball ); 
         
        /*if(transball.y<-4){
            transball.x = 0.0f;
            transball.y = -3.65f;
            //transball.z = 0.3f;
            plane_trans.set(new Vector3f(0.0f, 1.5f, 9985.0f));
        }*/
        
        inside = InsidePolygon(transball.x, transball.y);
        if(inside==0){
            transball.x = 0.0f;
            transball.y = -3.65f;
            
            ball_trans.setTranslation( transball );
            ballTG.setTransform(ball_trans);

            plane_trans.setTranslation( translate );
            planeTG.setTransform(plane_trans);
        }
        
        
        
    } 
    
    public int InsidePolygon(double x, double y)
{
   int i;
   double angle=0, p1x, p2x,p1y,p2y;
   //Point p1,p2;

   // (-5,5) (-3.5,-4) (3.5,-4) (5,5)
   // (-4,-1) (-3.5,-4) (3.5,-4) (4,-1)
   p1x = -4.00f - x;
   p1y = -1.00f - y;
   p2x = -3.50f - x;
   p2y = -4.00f - y;
   
   angle += Angle2D(p1x,p1y,p2x,p2y);
   
   p1x = -3.50f - x;
   p1y = -4.00f - y;
   p2x =  3.50f - x;
   p2y =  4.00f - y;
   
   angle += Angle2D(p1x,p1y,p2x,p2y);
   
   p1x = 3.50f - x;
   p1y = -4.00f - y;
   p2x = 4.00f - x;
   p2y = -1.00f - y;
   
   angle += Angle2D(p1x,p1y,p2x,p2y);
   
   p1x = 4.00f - x;
   p1y = -1.00f - y;
   p2x = -4.00f - x;
   p2y = -1.00f - y;
   
   angle += Angle2D(p1x,p1y,p2x,p2y);
   
   if(Math.abs(angle)<Math.PI){
       return 0;
   }else{
       return 1;
   }
   
   /*for (i=0;i<n;i++) {
      p1.h = polygon[i].h - p.h;
      p1.v = polygon[i].v - p.v;
      p2.h = polygon[(i+1)%n].h - p.h;
      p2.v = polygon[(i+1)%n].v - p.v;
      angle += Angle2D(p1.h,p1.v,p2.h,p2.v);
   }

   if (ABS(angle) < PI)
      return(FALSE);
   else
      return(TRUE);*/
}

    /*
   Return the angle between two vectors on a plane
   The angle is from vector 1 to vector 2, positive anticlockwise
   The result is between -pi -> pi
*/
    public double Angle2D(double x1, double y1, double x2, double y2)
    {
   double dtheta,theta1,theta2;

   theta1 = Math.atan2(y1,x1);
   theta2 = Math.atan2(y2,x2);
   dtheta = theta2 - theta1;
   while (dtheta > Math.PI)
      dtheta -= 2*Math.PI;
   while (dtheta < -Math.PI)
      dtheta += 2*Math.PI;

   return(dtheta);
}
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Frame frame = new Balance();
    }
}

/*
 TODO
 * 1. Change the Appearence of the plane so that it dose not looks we are looking form above of the plane.
 * 2. Check the Ball is out of plane
 * 3. Restrict the rotation.
 * 4. Add gravity
 */