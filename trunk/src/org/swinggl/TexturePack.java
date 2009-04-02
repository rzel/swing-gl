package org.swinggl;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

/**
 * packs several arbitrary sized images into a jPCT texture. 
 * the created texture is 2^n x 2^m as  required by jPCT. useful for
 * blitting purposes.
 * 
 * <p>see <a href='http://www.blackpawn.com/texts/lightmaps/default.html'
 * target='_blank'>this page</a> for an explanation of packing algorithm</p>
 * 
 * @author hakan eryargi (r a f t)
 */
public class TexturePack {
	private static final boolean DEBUG = false;

	private final int imageType;
	private boolean packed = false;
	private final Dimension blittedSize = new Dimension();

	private final List<Entry> entries = new ArrayList<Entry>();
	private GLTexture texture = null;

	/**
	 * creates an TexturePack using BufferedImage.TYPE_INT_ARGB as image type
	 * 
	 * @see BufferedImage#TYPE_INT_ARGB
	 */
	public TexturePack() {
		this(BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * creates an TexturePack
	 * 
	 * @param imageType must be one of the image types defined in
	 *            {@link BufferedImage} class
	 * @see BufferedImage
	 */
	public TexturePack(int imageType) {
		this.imageType = imageType;
	}

	/**
	 * add image to be included in pack.
	 * 
	 * @return image id which can later be used for blit(..) methods
	 */
	public int addImage(Image image) {
		checkPacked();
		
		if (image == null)
			throw new NullPointerException();
		if (image.getWidth(null) <= 0 || image.getHeight(null) <= 0)
			throw new IllegalArgumentException("width and height must be positive");

		this.entries.add(new Entry(image));
		return entries.size() - 1;
	}

	/**
	 * packs images into an 2^n x 2^m size d image. after packing clears all
	 * references to given images hence this method can only be called once
	 * 
	 * @return created image
	 */
	private BufferedImage packImage() {
		checkPacked();

		if (entries.isEmpty())
			throw new IllegalStateException("nothing to pack");

		int maxWidth = 0;
		int maxHeight = 0;
		int totalArea = 0;

		for (Entry entry : entries) {
			int width = entry.image.getWidth(null);
			int height = entry.image.getHeight(null);

			if (width > maxWidth)
				maxWidth = width;
			if (height > maxHeight)
				maxHeight = height;

			totalArea += width * height;
		}

		Dimension size = new Dimension(closestTwoPower(maxWidth), closestTwoPower(maxHeight));
		boolean fitAll = false;
		if (DEBUG)
			System.out.println("initial size " + size);

		loop: 
		while (!fitAll) {
			int area = size.width * size.height;
			if (area < totalArea) {
				nextSize(size);
				if (DEBUG)
					System.out.println("enlarging to " + size);
				continue;
			}

			Node root = new Node(size.width, size.height);
			for (Entry entry : entries) {
				Node inserted = root.insert(entry);
				if (inserted == null) {
					nextSize(size);
					if (DEBUG)
						System.out.println("couldnt fit, enlarging to " + size);
					continue loop;
				}
			}
			fitAll = true;
			if (DEBUG)
				printTree(root, "", 0);
		}

		BufferedImage image = new BufferedImage(size.width, size.height, imageType);
		Graphics2D g2d = image.createGraphics();
		for (Entry entry : entries) {
			g2d.drawImage(entry.image, entry.bounds.x, entry.bounds.y, null);
			// UV calculations are done again and again so cache them
			entry.setUvBounds(size.width, size.height);
			entry.image = null;
		}
		g2d.dispose();
		packed = true;
		if (DEBUG) org.swinggl.util.SwingUtil.showImage(image);

		return image;
	}

	/**
	 * packs image, creates a Texture out of it and adds texture to
	 * TextureManager.
	 * 
	 * @return created texture
	 */
	public GLTexture pack() {
		if (texture != null)
			return texture;

		BufferedImage image = packImage();
		texture = new GLTexture(image);

		return texture;
	}

	void initBlit() {
		OpenGL.initBlit();
	}
	
	/**
	 * <p>blits one of packed images completely without scaling. 
	 * first initializes blitting and finally ends</p>
	 * 
	 */
	public Dimension blit(int imageId, int x, int y) {
		OpenGL.initBlit();
		try {
			return blitOnly(imageId, x, y);
		} finally {
			OpenGL.endBlit();
		}
	}
	
	/** blits without GL settings */
	Dimension blitOnly(int imageId, int x, int y) {
		Entry entry = entries.get(imageId);
		OpenGL.bindTexture(texture.getId());
		entry.makeQuad(x, y);

		blittedSize.setSize(entry.bounds.width, entry.bounds.height);
		return blittedSize;
	}
	
	private void checkPacked() {
		if (packed)
			throw new IllegalStateException("already packed");
	}

	private void nextSize(Dimension size) {
		if (size.width > size.height)
			size.height <<= 1;
		else
			size.width <<= 1;
	}

	/** recursively prints placement tree starting from given node */
	private void printTree(Node node, String prefix, int depth) {
		if (node == null)
			return;

		System.out.println(depth + ":\t" + prefix + "--" + node);
		printTree(node.child[0], "  |" + prefix, depth + 1);
		printTree(node.child[1], "  |" + prefix, depth + 1);
	}

	/**
	 * returns the closest power of two that is equal or greater than given
	 * number
	 */
	private int closestTwoPower(int i) {
		int power = 1;
		while (power < i) {
			power <<= 1;
		}
		return power;
	}

	/** contents of a node if any */
	private static class Entry {
		private final Rectangle bounds = new Rectangle();
		private float u1, u2, v1, v2; // texture coordinates 
		private Image image;

		private Entry(Image image) {
			this.image = image;
		}

		private void makeQuad(int x, int y) {
			float x2 = x + bounds.width;
			float y2 = y + bounds.height;
			
			GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
			GL11.glTexCoord2f(u1, v2);
			GL11.glVertex2f(x, y2);
			GL11.glTexCoord2f(u2, v2);
			GL11.glVertex2f(x2, y2);
			GL11.glTexCoord2f(u1, v1);
			GL11.glVertex2f(x, y);
			GL11.glTexCoord2f(u2, v1);
			GL11.glVertex2f(x2, y);
			GL11.glEnd();
		}

		private void setUvBounds(int overallWidth, int overallHeight) {
			u1 = bounds.x / (float)overallWidth; 
			u2 = (bounds.x + bounds.width) / (float)overallWidth;
			
			v1 = bounds.y / (float)overallHeight; 
			v2 = (bounds.y + bounds.height) / (float)overallHeight; 
		}

		@Override
		public String toString() {
			return "Entry: " + bounds;
		}
	}

	/** a node in our placement tree */
	private static class Node {
		private final Node[] child = new Node[2];
		private final Rectangle bounds = new Rectangle();
		private Entry entry;

		private Node() {
		}

		private Node(int width, int height) {
			bounds.setBounds(0, 0, width, height);
		}

		private boolean isLeaf() {
			return (child[0] == null) && (child[1] == null);
		}

		private Node insert(Entry entry) {
			if (isLeaf()) {
				// if there's already a image here, return
				if (this.entry != null)
					return null;

				int width = entry.image.getWidth(null);
				int height = entry.image.getHeight(null);

				// (if we're too small, return)
				if ((width > bounds.width) || (height > bounds.height))
					return null;

				// (if we're just right, accept)
				if ((width == bounds.width) && (height == bounds.height)) {
					this.entry = entry;
					this.entry.bounds.setBounds(this.bounds);
					return this;
				}

				// otherwise, split this node
				child[0] = new Node();
				child[1] = new Node();

				// (decide which way to split)
				int dw = bounds.width - width;
				int dh = bounds.height - height;

				if (dw > dh) { // split horizontally
					child[0].bounds.setBounds(bounds.x, bounds.y, width, bounds.height);
					child[1].bounds.setBounds(bounds.x + width, bounds.y, bounds.width - width, bounds.height);
				} else { // split vertically
					child[0].bounds.setBounds(bounds.x, bounds.y, bounds.width, height);
					child[1].bounds.setBounds(bounds.x, bounds.y + height, bounds.width, bounds.height - height);
				}
				// insert into first child we created
				return child[0].insert(entry);
			} else {
				// try inserting into first child
				Node newNode = child[0].insert(entry);
				if (newNode != null)
					return newNode;

				// no room, insert into second
				return child[1].insert(entry);
			}
		}

		@Override
		public String toString() {
			return bounds + ((entry == null) ? " <no entry>" : " " + entry.toString());
		}
	}
}
