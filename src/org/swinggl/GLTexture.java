package org.swinggl;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;

/**
 * <p>OpenGL texture</p>
 * @author hakan eryargi (r a f t)
 */
public class GLTexture {
	private static final int NO_ID = -1;
	
	private final int width, height;
	private final int[] pixels;
	private int id = NO_ID;
	
	public GLTexture(Image image) {
		this(createBufferedImage(image));
	}
	
	public GLTexture(BufferedImage image) {
		assert isPowerOfTwo(image.getWidth()) : "width is not power of two: " + image.getWidth(); 
		assert isPowerOfTwo(image.getHeight()) : "hieght is not power of two: " + image.getHeight();
		
		image = convertImage(image);
		
		DataBufferInt dataBuffer = (DataBufferInt) (image.getRaster().getDataBuffer());
		this.pixels = dataBuffer.getData();
		this.width = image.getWidth();
		this.height = image.getHeight();
	}
	
	int getId() {
		return id;
	}
	
	/** uploads texture to video card and binds it. */
	void bind() {
		if (id != NO_ID) // already bound
			return; 
		
		ByteBuffer textureBuffer = ByteBuffer.allocateDirect(pixels.length << 2); // * 4
		textureBuffer.order(ByteOrder.LITTLE_ENDIAN);

		for (int i = 0; i < pixels.length; i++) {
			textureBuffer.putInt(i * 4, correctPixel(pixels[i]));
		}
			
		IntBuffer idBuffer = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
		GL11.glGenTextures(idBuffer);
		id = idBuffer.get(0);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, textureBuffer);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);		
	}

	private static int correctPixel(int pixel) {
		// swap red and blue: 0xaarrggbb -> 0xaabbggrr
		return (pixel & 0xff00ff00) | ((pixel & 0x00ff0000) >> 16) | ((pixel & 0x000000ff) << 16);   
	}
	
	private static BufferedImage createBufferedImage(Image image) {
		if (image instanceof BufferedImage) 
			return (BufferedImage) image;
		return copyImage(image);
	}

	private static BufferedImage convertImage(BufferedImage image) {
		if (image.getType() == BufferedImage.TYPE_INT_ARGB)
			return image;
		return copyImage(image);
	}
	
	private static BufferedImage copyImage(Image image) {
		BufferedImage buffImage = new BufferedImage(image.getWidth(null), 
				image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		buffImage.getGraphics().drawImage(image, 0, 0, null);
		return buffImage;
	}
	
	private static boolean isPowerOfTwo(int value) {
		int i = 2;
		while (i < value)
			i <<= 1;
		return (i == value);
	}
}
