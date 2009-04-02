package demo;

import java.awt.Color;

import javax.swing.JPanel;

import org.swinggl.GLFrame;


import com.threed.jpct.Config;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.IRenderer;
import com.threed.jpct.Logger;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;

/**
 * A simple HelloWorld using the OpenGL-renderer.
 * @author EgonOlsen
 *
 */
public class HelloWorldOGL {

	private World world;
	private FrameBuffer buffer;
	private Object3D box;

	private GLFrame glFrame;
	
	static JPanel createTestPanel() {
//		JPanel panel = new JPanel();
//		panel.setBackground(Color.YELLOW);
//		panel.add(new JLabel("long long label"));
//		panel.add(new JButton("button"));
//		return panel;
		
		return new TestPanel();
	}
	
	
	public static void main(String[] args) throws Exception {
		// uncomment following line to show test panel in a JFrame (as a reference) 
		//raft.swinggl.util.SwingUtil.showComponent("Swing - Reference Frame", createTestPanel());
		
		Config.glWindowName = "SwingGL - Swing on top of OpenGL";
		Config.glColorDepth = System.getProperty("os.name").startsWith("Linux") ? 24 : 32;		Logger.setOnError(Logger.ON_ERROR_THROW_EXCEPTION);
		new HelloWorldOGL().loop();
	}

	public HelloWorldOGL() throws Exception {
		world = new World();
		world.setAmbientLight(0, 255, 0);

		TextureManager.getInstance().addTexture("box", new Texture(getClass().getResourceAsStream("box.jpg")));
		
		box = Primitives.getBox(13f, 2f);
		box.setTexture("box");
		box.setEnvmapped(Object3D.ENVMAP_ENABLED);
		box.build();
		world.addObject(box);

		world.getCamera().setPosition(50, -50, -5);
		world.getCamera().lookAt(box.getTransformedCenter());
	}

	private void loop() throws Exception {
		buffer = new FrameBuffer(800, 600, FrameBuffer.SAMPLINGMODE_NORMAL);
		buffer.disableRenderer(IRenderer.RENDERER_SOFTWARE);
		buffer.enableRenderer(IRenderer.RENDERER_OPENGL);

		JPanel panel = createTestPanel();
		glFrame = new GLFrame(panel); 
		
		glFrame.setLocation(30, 30);
		
		while (!org.lwjgl.opengl.Display.isCloseRequested()) {
			box.rotateY(0.01f);
			buffer.clear(Color.BLUE);
			world.renderScene(buffer);
			world.draw(buffer);
			buffer.update();
			
			glFrame.paint();
			
			buffer.displayGLOnly();
			Thread.sleep(10);
		}
		buffer.disableRenderer(IRenderer.RENDERER_OPENGL);
		buffer.dispose();
		System.exit(0);
	}
}

	
