/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package balance;
/**
 *
 * @author pijuskumar
 */
import java.awt.Container;

import javax.media.j3d.*;

import javax.vecmath.*;

import com.sun.j3d.utils.image.TextureLoader;

public class Plane extends Shape3D{
    
    public Plane(String imagefile, Point3f[] verts)
    {
	//create_plane();
        //Point3f[] grass ={ new Point3f(1.5f,-1.5f,-1.5f) , new Point3f(-1.5f,-1.5f,-1.5f), new Point3f(-1.5f,1.5f,-1.5f), new Point3f(1.5f,1.5f,-1.5f)};
	QuadArray plane = new QuadArray(4, GeometryArray.COORDINATES | GeometryArray.TEXTURE_COORDINATE_2);
	//Point3f[] verts ={ new Point3f(-4.0f,4.0f,0.0f) , new Point3f(-3.70f,3.0f,0.0f), new Point3f(3.70f,3.0f,0.0f), new Point3f(4.0f,4.0f,0.0f)};
        plane.setCoordinates(0, verts);
	
        TextureLoader tloader = new TextureLoader(imagefile, "RGB", new Container());
        ImageComponent2D img = tloader.getImage();

        Texture2D timage = new Texture2D(Texture2D.BASE_LEVEL,Texture2D.RGBA,img.getWidth(),img.getHeight());
        timage.setImage(0, img);

        timage.setEnable(true);

        Appearance app = new Appearance();

        app.setTexture(timage);

        float uv0[]={0.0f,1.0f};
        float uv1[]={0.0f,0.0f};
        float uv2[]={1.0f,0.0f};
        float uv3[]={1.0f,1.0f};

        plane.setTextureCoordinate (0,0,uv0);
        plane.setTextureCoordinate (0,1,uv1);
        plane.setTextureCoordinate (0,2,uv2);
        plane.setTextureCoordinate (0,3,uv3);
        
        this.setGeometry(plane);
        this.setAppearance(app);
    }
    
}
