package org.swinggl.util;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

/**
 * panel to show an image 
 * @author  hakan eryargi (r a f t)
 */
public class ImagePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	final Image image;
    
    /** Creates a new instance of ShowSkin */
    public ImagePanel(Image image) {
        this.image = image;
        setPreferredSize(new Dimension(image.getWidth(null), image.getHeight(null)));
    }
    
    protected void paintComponent(Graphics g) {
        g.drawImage(image, 0, 0, null);
    }
}
