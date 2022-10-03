package sun.awt.windows;

import sun.java2d.opengl.WGLSurfaceData;
import sun.java2d.d3d.D3DSurfaceData;
import sun.java2d.pipe.RenderQueue;
import sun.java2d.InvalidPipeException;
import sun.java2d.pipe.BufferedContext;
import sun.java2d.pipe.hw.AccelSurface;
import sun.java2d.Surface;
import sun.awt.image.BufImgSurfaceData;
import sun.java2d.DestSurfaceProvider;
import java.awt.image.VolatileImage;
import java.awt.image.DataBufferInt;
import java.awt.image.BufferedImage;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.awt.Color;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.image.ImageObserver;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.GraphicsConfiguration;
import sun.java2d.pipe.hw.AccelGraphicsConfig;
import java.awt.Window;

abstract class TranslucentWindowPainter
{
    protected Window window;
    protected WWindowPeer peer;
    private static final boolean forceOpt;
    private static final boolean forceSW;
    
    public static TranslucentWindowPainter createInstance(final WWindowPeer wWindowPeer) {
        final GraphicsConfiguration graphicsConfiguration = wWindowPeer.getGraphicsConfiguration();
        if (!TranslucentWindowPainter.forceSW && graphicsConfiguration instanceof AccelGraphicsConfig) {
            final String simpleName = graphicsConfiguration.getClass().getSimpleName();
            if ((((AccelGraphicsConfig)graphicsConfiguration).getContextCapabilities().getCaps() & 0x100) != 0x0 || TranslucentWindowPainter.forceOpt) {
                if (simpleName.startsWith("D3D")) {
                    return new VIOptD3DWindowPainter(wWindowPeer);
                }
                if (TranslucentWindowPainter.forceOpt && simpleName.startsWith("WGL")) {
                    return new VIOptWGLWindowPainter(wWindowPeer);
                }
            }
        }
        return new BIWindowPainter(wWindowPeer);
    }
    
    protected TranslucentWindowPainter(final WWindowPeer peer) {
        this.peer = peer;
        this.window = (Window)peer.getTarget();
    }
    
    protected abstract Image getBackBuffer(final boolean p0);
    
    protected abstract boolean update(final Image p0);
    
    public abstract void flush();
    
    public void updateWindow(boolean b) {
        boolean update = false;
        Image image = this.getBackBuffer(b);
        while (!update) {
            if (b) {
                final Graphics2D graphics2D = (Graphics2D)image.getGraphics();
                try {
                    this.window.paintAll(graphics2D);
                }
                finally {
                    graphics2D.dispose();
                }
            }
            update = this.update(image);
            if (!update) {
                b = true;
                image = this.getBackBuffer(true);
            }
        }
    }
    
    private static final Image clearImage(final Image image) {
        final Graphics2D graphics2D = (Graphics2D)image.getGraphics();
        final int width = image.getWidth(null);
        final int height = image.getHeight(null);
        graphics2D.setComposite(AlphaComposite.Src);
        graphics2D.setColor(new Color(0, 0, 0, 0));
        graphics2D.fillRect(0, 0, width, height);
        return image;
    }
    
    static {
        forceOpt = Boolean.valueOf(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.java2d.twp.forceopt", "false")));
        forceSW = Boolean.valueOf(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.java2d.twp.forcesw", "false")));
    }
    
    private static class BIWindowPainter extends TranslucentWindowPainter
    {
        private BufferedImage backBuffer;
        
        protected BIWindowPainter(final WWindowPeer wWindowPeer) {
            super(wWindowPeer);
        }
        
        @Override
        protected Image getBackBuffer(final boolean b) {
            final int width = this.window.getWidth();
            final int height = this.window.getHeight();
            if (this.backBuffer == null || this.backBuffer.getWidth() != width || this.backBuffer.getHeight() != height) {
                this.flush();
                this.backBuffer = new BufferedImage(width, height, 3);
            }
            return b ? clearImage(this.backBuffer) : this.backBuffer;
        }
        
        @Override
        protected boolean update(final Image image) {
            VolatileImage volatileImage = null;
            if (image instanceof BufferedImage) {
                final BufferedImage bufferedImage = (BufferedImage)image;
                this.peer.updateWindowImpl(((DataBufferInt)bufferedImage.getRaster().getDataBuffer()).getData(), bufferedImage.getWidth(), bufferedImage.getHeight());
                return true;
            }
            if (image instanceof VolatileImage) {
                volatileImage = (VolatileImage)image;
                if (image instanceof DestSurfaceProvider) {
                    final Surface destSurface = ((DestSurfaceProvider)image).getDestSurface();
                    if (destSurface instanceof BufImgSurfaceData) {
                        final int width = volatileImage.getWidth();
                        final int height = volatileImage.getHeight();
                        this.peer.updateWindowImpl(((DataBufferInt)((BufImgSurfaceData)destSurface).getRaster(0, 0, width, height).getDataBuffer()).getData(), width, height);
                        return true;
                    }
                }
            }
            final BufferedImage bufferedImage2 = (BufferedImage)clearImage(this.backBuffer);
            this.peer.updateWindowImpl(((DataBufferInt)bufferedImage2.getRaster().getDataBuffer()).getData(), bufferedImage2.getWidth(), bufferedImage2.getHeight());
            return volatileImage == null || !volatileImage.contentsLost();
        }
        
        @Override
        public void flush() {
            if (this.backBuffer != null) {
                this.backBuffer.flush();
                this.backBuffer = null;
            }
        }
    }
    
    private static class VIWindowPainter extends BIWindowPainter
    {
        private VolatileImage viBB;
        
        protected VIWindowPainter(final WWindowPeer wWindowPeer) {
            super(wWindowPeer);
        }
        
        @Override
        protected Image getBackBuffer(final boolean b) {
            final int width = this.window.getWidth();
            final int height = this.window.getHeight();
            final GraphicsConfiguration graphicsConfiguration = this.peer.getGraphicsConfiguration();
            if (this.viBB == null || this.viBB.getWidth() != width || this.viBB.getHeight() != height || this.viBB.validate(graphicsConfiguration) == 2) {
                this.flush();
                if (graphicsConfiguration instanceof AccelGraphicsConfig) {
                    this.viBB = ((AccelGraphicsConfig)graphicsConfiguration).createCompatibleVolatileImage(width, height, 3, 2);
                }
                if (this.viBB == null) {
                    this.viBB = graphicsConfiguration.createCompatibleVolatileImage(width, height, 3);
                }
                this.viBB.validate(graphicsConfiguration);
            }
            return b ? clearImage(this.viBB) : this.viBB;
        }
        
        @Override
        public void flush() {
            if (this.viBB != null) {
                this.viBB.flush();
                this.viBB = null;
            }
        }
    }
    
    private abstract static class VIOptWindowPainter extends VIWindowPainter
    {
        protected VIOptWindowPainter(final WWindowPeer wWindowPeer) {
            super(wWindowPeer);
        }
        
        protected abstract boolean updateWindowAccel(final long p0, final int p1, final int p2);
        
        @Override
        protected boolean update(final Image image) {
            if (image instanceof DestSurfaceProvider) {
                final Surface destSurface = ((DestSurfaceProvider)image).getDestSurface();
                if (destSurface instanceof AccelSurface) {
                    final int width = image.getWidth(null);
                    final int height = image.getHeight(null);
                    final boolean[] array = { false };
                    final AccelSurface accelSurface = (AccelSurface)destSurface;
                    final RenderQueue renderQueue = accelSurface.getContext().getRenderQueue();
                    renderQueue.lock();
                    try {
                        BufferedContext.validateContext(accelSurface);
                        renderQueue.flushAndInvokeNow(new Runnable() {
                            @Override
                            public void run() {
                                array[0] = VIOptWindowPainter.this.updateWindowAccel(accelSurface.getNativeOps(), width, height);
                            }
                        });
                    }
                    catch (final InvalidPipeException ex) {}
                    finally {
                        renderQueue.unlock();
                    }
                    return array[0];
                }
            }
            return super.update(image);
        }
    }
    
    private static class VIOptD3DWindowPainter extends VIOptWindowPainter
    {
        protected VIOptD3DWindowPainter(final WWindowPeer wWindowPeer) {
            super(wWindowPeer);
        }
        
        @Override
        protected boolean updateWindowAccel(final long n, final int n2, final int n3) {
            return D3DSurfaceData.updateWindowAccelImpl(n, this.peer.getData(), n2, n3);
        }
    }
    
    private static class VIOptWGLWindowPainter extends VIOptWindowPainter
    {
        protected VIOptWGLWindowPainter(final WWindowPeer wWindowPeer) {
            super(wWindowPeer);
        }
        
        @Override
        protected boolean updateWindowAccel(final long n, final int n2, final int n3) {
            return WGLSurfaceData.updateWindowAccelImpl(n, this.peer, n2, n3);
        }
    }
}
