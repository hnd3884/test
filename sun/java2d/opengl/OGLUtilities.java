package sun.java2d.opengl;

import sun.java2d.pipe.Region;
import java.awt.Rectangle;
import java.awt.GraphicsConfiguration;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.hw.AccelSurface;
import sun.java2d.pipe.BufferedContext;
import sun.java2d.SunGraphics2D;
import java.awt.Graphics;

class OGLUtilities
{
    public static final int UNDEFINED = 0;
    public static final int WINDOW = 1;
    public static final int TEXTURE = 3;
    public static final int FLIP_BACKBUFFER = 4;
    public static final int FBOBJECT = 5;
    
    private OGLUtilities() {
    }
    
    public static boolean isQueueFlusherThread() {
        return OGLRenderQueue.isQueueFlusherThread();
    }
    
    public static boolean invokeWithOGLContextCurrent(final Graphics graphics, final Runnable runnable) {
        final OGLRenderQueue instance = OGLRenderQueue.getInstance();
        instance.lock();
        try {
            if (graphics != null) {
                if (!(graphics instanceof SunGraphics2D)) {
                    return false;
                }
                final SurfaceData surfaceData = ((SunGraphics2D)graphics).surfaceData;
                if (!(surfaceData instanceof OGLSurfaceData)) {
                    return false;
                }
                BufferedContext.validateContext((AccelSurface)surfaceData);
            }
            instance.flushAndInvokeNow(runnable);
            OGLContext.invalidateCurrentContext();
        }
        finally {
            instance.unlock();
        }
        return true;
    }
    
    public static boolean invokeWithOGLSharedContextCurrent(final GraphicsConfiguration graphicsConfiguration, final Runnable runnable) {
        if (!(graphicsConfiguration instanceof OGLGraphicsConfig)) {
            return false;
        }
        final OGLRenderQueue instance = OGLRenderQueue.getInstance();
        instance.lock();
        try {
            OGLContext.setScratchSurface((OGLGraphicsConfig)graphicsConfiguration);
            instance.flushAndInvokeNow(runnable);
            OGLContext.invalidateCurrentContext();
        }
        finally {
            instance.unlock();
        }
        return true;
    }
    
    public static Rectangle getOGLViewport(final Graphics graphics, final int n, final int n2) {
        if (!(graphics instanceof SunGraphics2D)) {
            return null;
        }
        final SunGraphics2D sunGraphics2D = (SunGraphics2D)graphics;
        return new Rectangle(sunGraphics2D.transX, sunGraphics2D.surfaceData.getBounds().height - (sunGraphics2D.transY + n2), n, n2);
    }
    
    public static Rectangle getOGLScissorBox(final Graphics graphics) {
        if (!(graphics instanceof SunGraphics2D)) {
            return null;
        }
        final SunGraphics2D sunGraphics2D = (SunGraphics2D)graphics;
        final SurfaceData surfaceData = sunGraphics2D.surfaceData;
        final Region compClip = sunGraphics2D.getCompClip();
        if (!compClip.isRectangular()) {
            return null;
        }
        final int loX = compClip.getLoX();
        final int loY = compClip.getLoY();
        final int width = compClip.getWidth();
        final int height = compClip.getHeight();
        return new Rectangle(loX, surfaceData.getBounds().height - (loY + height), width, height);
    }
    
    public static Object getOGLSurfaceIdentifier(final Graphics graphics) {
        if (!(graphics instanceof SunGraphics2D)) {
            return null;
        }
        return ((SunGraphics2D)graphics).surfaceData;
    }
    
    public static int getOGLSurfaceType(final Graphics graphics) {
        if (!(graphics instanceof SunGraphics2D)) {
            return 0;
        }
        final SurfaceData surfaceData = ((SunGraphics2D)graphics).surfaceData;
        if (!(surfaceData instanceof OGLSurfaceData)) {
            return 0;
        }
        return ((OGLSurfaceData)surfaceData).getType();
    }
    
    public static int getOGLTextureType(final Graphics graphics) {
        if (!(graphics instanceof SunGraphics2D)) {
            return 0;
        }
        final SurfaceData surfaceData = ((SunGraphics2D)graphics).surfaceData;
        if (!(surfaceData instanceof OGLSurfaceData)) {
            return 0;
        }
        return ((OGLSurfaceData)surfaceData).getTextureTarget();
    }
}
