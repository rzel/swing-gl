package org.swinggl;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.RenderingHints.Key;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

/**
 * <p>OpenGL based <code>Graphics2D</code> implementation. </p>
 * @author hakan eryargi (r a f t)
 */
public class GLGraphics extends Graphics2D {
	private static final boolean DEBUG = false;
	
	private static int lastId = 0;
	private static synchronized int nextId() {
		return lastId++;
	} 
	private static Map<Font, GLFont> GL_FONTS = new HashMap<Font, GLFont>();
	private static Map<Image, TexturePack> TEXTURE_PACKS = new HashMap<Image, TexturePack>();
	
	private static GLFont getGLFont(Font font) {
		GLFont glFont = GL_FONTS.get(font);
		if (glFont == null) {
			glFont = new GLFont(font);
			GL_FONTS.put(font, glFont);
		}
		return glFont;
	}
	
	private static TexturePack getTexturePack(Image image) {
		TexturePack pack = TEXTURE_PACKS.get(image);
		if (pack == null) {
			pack = new TexturePack();
			pack.addImage(image);
			pack.pack().bind();
			TEXTURE_PACKS.put(image, pack);
		}
		return pack;
	}
	
	//private final FrameBuffer buffer;
	private final int id = nextId();
	
	private Font font;
	private Color color;
	private int dX, dY;

	private Rectangle userClip;
	private Rectangle glClip;
	
	public GLGraphics() {
	}
	
	private GLGraphics(GLGraphics other) {
		this.font = other.font;
		this.color = other.color;
		this.dX = other.dX;
		this.dY = other.dY;
		this.userClip = other.userClip;
		this.glClip = other.glClip;
	}

	public void clearRect(int x, int y, int width, int height) {
		if (DEBUG) System.out.println(id + " clearRect");
		throw new UnsupportedOperationException();
	}

	public void clipRect(int x, int y, int width, int height) {
		if (DEBUG) System.out.println(id + " clipRect");
		clip(new Rectangle(x, y, width, height));
		//throw new UnsupportedOperationException();
	}

	public void clip(Rectangle rectangle) {
		if (userClip != null) { 
			rectangle = rectangle.intersection(userClip);
		}
		setClip(rectangle);
	}
	
	public void clip(Shape shape) {
		if (DEBUG) System.out.println(id + " clip shape");
		
		if (shape instanceof Rectangle) {
			clip((Rectangle)shape);
		} else {
			throw new UnsupportedOperationException();
		}
	}

	public void setClip(Shape clip) {
		if (DEBUG) System.out.println(id + " setClip shape");
		if (clip instanceof Rectangle) {
			setClip((Rectangle)clip);
		} else {
			throw new UnsupportedOperationException();
		}
	}

	public void setClip(Rectangle rect) {
		if (DEBUG) System.out.println(id + " setClip rect");
		
		if (rect == null) {
			userClip = null;
			glClip = null;
		} else {
			setClip(rect.x, rect.y, rect.width, rect.height);
		}
	}
	
	public void setClip(int x, int y, int width, int height) {
		if (DEBUG) System.out.println(id + " setClip");
		userClip = new Rectangle(x, y, width, height);
		glClip = new Rectangle(x+dX, y+dY, width, height);
	}
	
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		if (DEBUG) System.out.println(id + " copyArea");
		throw new UnsupportedOperationException();
	}

	public GLGraphics create() {
		if (DEBUG) System.out.println(id + " create");
		return clone();
	}

	public void dispose() {
		if (DEBUG) System.out.println(id + " dispose");
	}

	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		if (DEBUG) System.out.println(id + " drawArc");
		throw new UnsupportedOperationException();
	}

	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		if (DEBUG) System.out.println(id + " drawImage x,y");
		
		//saveImage(img);
		// TODO tmp
		//Texture texture = new Texture(img);
		//buffer.blit(texture, 0, 0, x, y, img.getWidth(null), img.getHeight(null), true);
		//buffer.blit(texture, 0, 0, x, y, texture.getWidth(), texture.getHeight(), true);
		throw new UnsupportedOperationException();
		//return false;
	}

	public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
		if (DEBUG) System.out.println(id + " drawImage x,y,bgcolor");
		throw new UnsupportedOperationException();
		//return false;
	}

	public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
		if (DEBUG) System.out.println(id + " drawImage x,y,w,h");
		throw new UnsupportedOperationException();
		//return false;
	}

	public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor,
			ImageObserver observer) {
		if (DEBUG) System.out.println(id + " drawImage x,y,w,h,bgcolor");
		throw new UnsupportedOperationException();
		//return false;
	}

	// TODO: implement scaling
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1,
			int sx2, int sy2, ImageObserver observer) {
		
		if (DEBUG) System.out.println(id + " drawImage dx,dy,sx,sy");
		
		TexturePack pack = getTexturePack(img);

		initBlit();
		try {
			pack.blitOnly(0, dx1, dy1);
			return true;
		} finally {
			endBlit();
		}
	}

	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1,
			int sx2, int sy2, Color bgcolor, ImageObserver observer) {
		if (DEBUG) System.out.println(id + " drawImage dx,dy,sx1,sx2");
		throw new UnsupportedOperationException();
		//return false;
	}

    public void drawRect(int x, int y, int width, int height) {
		if (DEBUG) System.out.println(id + " drawRect ");
		
    	if ((width < 0) || (height < 0)) {
    	    return;
    	}
    	initDraw();
    	try {
	    	if (height == 0 || width == 0) {
	    	    drawLine(x, y, x + width, y + height);
	    	} else {
	    	    drawLine(x, y, x + width, y);
	    	    drawLine(x + width, y, x + width, y + height);
	    	    drawLine(x + width, y + height, x, y + height);
	    	    drawLine(x, y + height, x, y);
	    	}
    	} finally {
    		endDraw();
    	}
    }
	
	public void drawLine(int x1, int y1, int x2, int y2) {
		if (DEBUG) System.out.println(id + " drawLine ");
		
//        x1 += offsetX;
//        y1 += offsetY;
//        x2 += offsetX;
//        y2 += offsetY;

        initDraw();
        try {
        	GL11.glBegin(GL11.GL_LINES);
            vertex(x1, y1);
            vertex(x2, y2);
            GL11.glEnd();
            
        } finally {
        	endDraw();
        }
	}

	public void drawOval(int x, int y, int width, int height) {
		if (DEBUG) System.out.println(id + " drawOval");
		throw new UnsupportedOperationException();
	}

	public void drawPolygon(int[] points, int[] points2, int points3) {
		if (DEBUG) System.out.println(id + " drawPolygon");
		throw new UnsupportedOperationException();
	}

	public void drawPolyline(int[] points, int[] points2, int points3) {
		if (DEBUG) System.out.println(id + " drawPolyline");
		throw new UnsupportedOperationException();
	}

	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		if (DEBUG) System.out.println(id + " drawRoundRect");
		throw new UnsupportedOperationException();
	}

	public void drawString(String str, int x, int y) {
		if (DEBUG) System.out.println(id + " drawString string int: " + str);
		drawStringInternal(str, x, y);
	}

	public void drawString(String str, float x, float y) {
		if (DEBUG) System.out.println(id + " drawString string f: " + str);
		drawStringInternal(str, (int)x, (int)y);
	}

	private void drawStringInternal(String s, int x, int y) {
		initScissor();
		try {
			GLFont glFont = getGLFont(font);
			// jPCT resets translation so we use +dX,Y here
			glFont.blitString(s, x+dX, y+dY, color);
			//glFont.blitString(buffer, s, x, y, 1, getColor());
		} finally {
			endScissor();
		}
	}
	
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		if (DEBUG) System.out.println(id + " drawString iterator int: " + iterator);
		throw new UnsupportedOperationException();
	}

	public void drawString(AttributedCharacterIterator iterator, float x, float y) {
		if (DEBUG) System.out.println(id + " drawString iterator float: " + iterator);
		throw new UnsupportedOperationException();
	}

	
	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		if (DEBUG) System.out.println(id + " fillArc");
		throw new UnsupportedOperationException();
	}

	public void fillOval(int x, int y, int width, int height) {
		if (DEBUG) System.out.println(id + " fillOval");
		throw new UnsupportedOperationException();
	}

	public void fillPolygon(int[] points, int[] points2, int points3) {
		if (DEBUG) System.out.println(id + " fillPolygon");
		throw new UnsupportedOperationException();
	}

	public void fillRect(int x, int y, int width, int height) {
		if (DEBUG) System.out.println(id + " fillRect");
		
        initDraw();
        try {
        	GL11.glBegin(GL11.GL_QUADS);
    		vertex(x, y);
    		vertex(x + width, y);
    		vertex(x + width, y + height);
    		vertex(x, y + height);
        	GL11.glEnd();
        } finally {
        	endDraw();
        }
	}

	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		if (DEBUG) System.out.println(id + " fillRoundRect");
		throw new UnsupportedOperationException();
	}

	public Shape getClip() {
		if (DEBUG) System.out.println(id + " getClip");
		return copyRect(userClip);
	}

	public Rectangle getClipBounds() {
		if (DEBUG) System.out.println(id + " getClipBounds");
		return copyRect(userClip);
	}

	public Color getColor() {
		if (DEBUG) System.out.println(id + " getColor");
		return color;
	}

	public Font getFont() {
		if (DEBUG) System.out.println(id + " Font");
		return font;
	}

	public FontMetrics getFontMetrics(Font f) {
		if (DEBUG) System.out.println(id + " FontMetrics");
		throw new UnsupportedOperationException();
		//return null;
	}


	public void setColor(Color color) {
		if (DEBUG) System.out.println(id + " setColor");
		this.color = color;
	}

	public void setFont(Font font) {
		if (DEBUG) System.out.println(id + " setFont: " + font);
		this.font = font;
	}

	public void setPaintMode() {
		if (DEBUG) System.out.println(id + " setPaintMode");
		throw new UnsupportedOperationException();
	}

	public void setXORMode(Color c1) {
		if (DEBUG) System.out.println(id + " setXORMode");
		throw new UnsupportedOperationException();
	}

	public void translate(int x, int y) {
		if (DEBUG) System.out.println(id + " translate " + x + "," + y);
		dX += x;
		dY += y;
	}

	protected GLGraphics clone() {
		return new GLGraphics(this); 
	}

	
	public void addRenderingHints(Map<?, ?> hints) {
		throw new UnsupportedOperationException();
	}

	public void draw(Shape s) {
		throw new UnsupportedOperationException();
	}

	public void drawGlyphVector(GlyphVector g, float x, float y) {
		throw new UnsupportedOperationException();
	}

	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
		throw new UnsupportedOperationException();
		//return false;
	}

	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
		throw new UnsupportedOperationException();
	}

	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		throw new UnsupportedOperationException();
	}

	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		throw new UnsupportedOperationException();
	}

	public void fill(Shape s) {
		throw new UnsupportedOperationException();
	}

	public Color getBackground() {
		throw new UnsupportedOperationException();
		//return null;
	}

	public Composite getComposite() {
		throw new UnsupportedOperationException();
		//return null;
	}

	public GraphicsConfiguration getDeviceConfiguration() {
		throw new UnsupportedOperationException();
		//return null;
	}

	public FontRenderContext getFontRenderContext() {
		//return null;
		throw new UnsupportedOperationException();
	}

	public Paint getPaint() {
		throw new UnsupportedOperationException();
		//return null;
	}

	public Object getRenderingHint(Key hintKey) {
		//throw new UnsupportedOperationException();
		return null;
	}

	public RenderingHints getRenderingHints() {
		//return null;
		throw new UnsupportedOperationException();
	}

	public Stroke getStroke() {
		//return null;
		throw new UnsupportedOperationException();
	}

	public AffineTransform getTransform() {
		//return null;
		throw new UnsupportedOperationException();
	}

	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		//return false;
		throw new UnsupportedOperationException();
	}

	public void rotate(double theta) {
		throw new UnsupportedOperationException();
	}

	public void rotate(double theta, double x, double y) {
		throw new UnsupportedOperationException();
	}

	public void scale(double sx, double sy) {
		throw new UnsupportedOperationException();
	}

	public void setBackground(Color color) {
		if (DEBUG) System.out.println(id + " setBackground");
		setColor(color);
		fillRect(0, 0, Display.getDisplayMode().getWidth(), Display.getDisplayMode().getHeight());
		//throw new UnsupportedOperationException();
	}

	public void setComposite(Composite comp) {
		throw new UnsupportedOperationException();
	}

	public void setPaint(Paint paint) {
		throw new UnsupportedOperationException();
	}

	public void setRenderingHint(Key hintKey, Object hintValue) {
		//throw new UnsupportedOperationException();
	}

	public void setRenderingHints(Map<?, ?> hints) {
		throw new UnsupportedOperationException();
	}

	public void setStroke(Stroke s) {
		throw new UnsupportedOperationException();
	}

	public void setTransform(AffineTransform Tx) {
		throw new UnsupportedOperationException();
	}

	public void shear(double shx, double shy) {
		throw new UnsupportedOperationException();
	}

	public void transform(AffineTransform Tx) {
		throw new UnsupportedOperationException();
	}

	public void translate(double tx, double ty) {
		throw new UnsupportedOperationException();
	} 
	
	public void fill3DRect(int x, int y, int width, int height, boolean raised) {
		super.fill3DRect(x, y, width, height, raised);
	}
	
	public void fillPolygon(Polygon p) {
		super.fillPolygon(p);
	}
	
	public void draw3DRect(int x, int y, int width, int height, boolean raised) {
		super.draw3DRect(x, y, width, height, raised);
	}
	
	/** inits drawing mode. projection, no texture, */
	private void initDraw() {
		OpenGL.initDraw();
		OpenGL.setColor(color);
		
		GL11.glTranslatef(dX, dY, 0f);
		initScissor();
	}
	
	/** inits drawing mode. projection, no texture, */
	private void initBlit() {
		OpenGL.initBlit();
		OpenGL.setColor(Color.WHITE);
		
		GL11.glTranslatef(dX, dY, 0f);
		initScissor();
	}
	
	private void endBlit() {
		OpenGL.endBlit();
		endScissor();
	}
	
	private void endDraw() {
		OpenGL.endDraw();
	}

	private void initScissor() {
		if (glClip != null) {
			int height = Display.getDisplayMode().getHeight();
			GL11.glScissor(glClip.x, height-glClip.y-glClip.height+1, glClip.width, glClip.height);
			GL11.glEnable(GL11.GL_SCISSOR_TEST);
		} else {
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
		}
	}
	
	private void endScissor() {
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}
	
	
	private void vertex(int x, int y) {
    	GL11.glVertex2f(x, y);
	}
	
	private Rectangle copyRect(Rectangle rect) {
		return (rect == null) ? null : new Rectangle(rect);
	}

	
	
}
