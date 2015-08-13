# SwingGL - Swing on top of OpenGL #

SwingGL is an attempt to make [Swing](http://java.sun.com/javase/6/docs/technotes/guides/swing/) run on top of OpenGL, specifically on top of [LWJGL](http://www.lwjgl.org/).

A web startable demo can be found [here](http://www.aptalkarga.com/swinggl/jws/swinggl.jnlp).

## Goals ##
Make as much as Swing components render correctly on top of OpenGL and also make AWT/Swing event system work correctly. It's very desirable that, all development should be transparent to Swing developers.

## Non Goals ##
Modify Swing components so that they can work on top of OpenGL.

## Current Status ##
We can currently render most of the basic Swing components on OpenGL. This is done, by using an OpenGL based `Graphics2D` implementation. No work has been done for `JMenuBar` and `JPopupMenu`'s yet.

There is an ongoing work to make AWT/Swing event system work too. At the moment, we can dispatch mouse move, press and release events which make `JButton`'s and other `ActionListener` sources work correctly. Other mouse events (enter, exit, drag etc) can be implemented too. No work has been done yet for keyboard events and focus system.

**Important:** At the moment we use a nasty hack to make event system work: Set `dispatcher` field of `Container` class with reflection. This hack most probably won't work on non-SUN JVM's and will break in the future. However we beleive event system can also be made work with a custom `Toolkit` and a dummy `Peer`. _Indeed we seek help from AWT/Swing gurus about this subject._

## Restrictions ##
  * Unicode strings are not supported. Only a predefined set of characters can be rendered. However, there is no restriction on Font variants.
  * Each image to be rendered is converted to a texture and uploaded to video card. So an application constanly creating offline images and rendering them will crash.
  * There are some rendering differences for some primitive operations. For example, two successive line segments with one pixel space between them renders continuous with awt `Graphics` but remains distinct with OpenGL. So some border effects may render differently.
  * Most of the `Graphics2D` functions not imlemented yet. Albeit most of them will be implemented when required or when the time comes ;) Note however, some of them cannot be implemented easily or at all. Arbitrary shape clipping is an example.