package org.swinggl;

import java.awt.Color;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

/**
 *
 * @author hakan eryargi (r a f t)
 */
class OpenGL {
	static final void initDraw() {
		initProjection();
		initOrthoMode();
		disableTextures();
	}
	
	static final void endDraw() {
		endProjection();
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}
	
	static final void initBlit() {
		initProjection();
		initOrthoMode();
		enableTextures();
    	
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	static final void endBlit() {
    	GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
	}

	static void bindTexture(int textureId) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
	}

	static void enableTextures() {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	static void disableTextures() {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	
	static void setColor(Color color) {
		if (color != null)
			GL11.glColor4f(color.getRed()/255f, color.getGreen()/255f, 
					color.getBlue()/255f, color.getAlpha()/255f);
	}
	
	static void initOrthoMode() {
		int width = Display.getDisplayMode().getWidth();
		int height = Display.getDisplayMode().getHeight();
		GLU.gluOrtho2D(0, width, height, 0); //reverse y-coordinate
	}
	
	static void initProjection() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
	}
	
	static void endProjection() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
	}
}
