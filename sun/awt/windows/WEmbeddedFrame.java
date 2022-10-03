package sun.awt.windows;

import sun.security.action.GetPropertyAction;
import java.awt.Dialog;
import java.awt.AWTKeyStroke;
import java.awt.AWTEvent;
import java.awt.event.InvocationEvent;
import sun.awt.SunToolkit;
import java.awt.EventQueue;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.awt.Graphics;
import sun.awt.image.ByteInterleavedRaster;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.peer.ComponentPeer;
import java.awt.Toolkit;
import sun.awt.EmbeddedFrame;

public class WEmbeddedFrame extends EmbeddedFrame
{
    private long handle;
    private int bandWidth;
    private int bandHeight;
    private int imgWid;
    private int imgHgt;
    private static int pScale;
    private static final int MAX_BAND_SIZE = 30720;
    private boolean isEmbeddedInIE;
    private static String printScale;
    
    public WEmbeddedFrame() {
        this(0L);
    }
    
    @Deprecated
    public WEmbeddedFrame(final int n) {
        this((long)n);
    }
    
    public WEmbeddedFrame(final long handle) {
        this.bandWidth = 0;
        this.bandHeight = 0;
        this.imgWid = 0;
        this.imgHgt = 0;
        this.isEmbeddedInIE = false;
        this.handle = handle;
        if (handle != 0L) {
            this.addNotify();
            this.show();
        }
    }
    
    @Override
    public void addNotify() {
        if (this.getPeer() == null) {
            this.setPeer(((WToolkit)Toolkit.getDefaultToolkit()).createEmbeddedFrame(this));
        }
        super.addNotify();
    }
    
    public long getEmbedderHandle() {
        return this.handle;
    }
    
    void print(final long n) {
        BufferedImage bufferedImage = null;
        int printScaleFactor = 1;
        int n2 = 1;
        if (this.isPrinterDC(n)) {
            n2 = (printScaleFactor = getPrintScaleFactor());
        }
        final int height = this.getHeight();
        if (bufferedImage == null) {
            this.bandWidth = this.getWidth();
            if (this.bandWidth % 4 != 0) {
                this.bandWidth += 4 - this.bandWidth % 4;
            }
            if (this.bandWidth <= 0) {
                return;
            }
            this.bandHeight = Math.min(30720 / this.bandWidth, height);
            this.imgWid = this.bandWidth * printScaleFactor;
            this.imgHgt = this.bandHeight * n2;
            bufferedImage = new BufferedImage(this.imgWid, this.imgHgt, 5);
        }
        final Graphics graphics = bufferedImage.getGraphics();
        graphics.setColor(Color.white);
        final Graphics2D graphics2D = (Graphics2D)bufferedImage.getGraphics();
        graphics2D.translate(0, this.imgHgt);
        graphics2D.scale(printScaleFactor, -n2);
        final byte[] dataStorage = ((ByteInterleavedRaster)bufferedImage.getRaster()).getDataStorage();
        for (int i = 0; i < height; i += this.bandHeight) {
            graphics.fillRect(0, 0, this.bandWidth, this.bandHeight);
            this.printComponents(graphics2D);
            int n3 = 0;
            int bandHeight = this.bandHeight;
            int imgHgt = this.imgHgt;
            if (i + this.bandHeight > height) {
                bandHeight = height - i;
                imgHgt = bandHeight * n2;
                n3 = this.imgWid * (this.imgHgt - imgHgt) * 3;
            }
            this.printBand(n, dataStorage, n3, 0, 0, this.imgWid, imgHgt, 0, i, this.bandWidth, bandHeight);
            graphics2D.translate(0, -this.bandHeight);
        }
    }
    
    protected static int getPrintScaleFactor() {
        if (WEmbeddedFrame.pScale != 0) {
            return WEmbeddedFrame.pScale;
        }
        if (WEmbeddedFrame.printScale == null) {
            WEmbeddedFrame.printScale = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
                @Override
                public String run() {
                    return System.getenv("JAVA2D_PLUGIN_PRINT_SCALE");
                }
            });
        }
        int int1;
        final int n = int1 = 4;
        if (WEmbeddedFrame.printScale != null) {
            try {
                int1 = Integer.parseInt(WEmbeddedFrame.printScale);
                if (int1 > 8 || int1 < 1) {
                    int1 = n;
                }
            }
            catch (final NumberFormatException ex) {}
        }
        return WEmbeddedFrame.pScale = int1;
    }
    
    private native boolean isPrinterDC(final long p0);
    
    private native void printBand(final long p0, final byte[] p1, final int p2, final int p3, final int p4, final int p5, final int p6, final int p7, final int p8, final int p9, final int p10);
    
    private static native void initIDs();
    
    public void activateEmbeddingTopLevel() {
    }
    
    @Override
    public void synthesizeWindowActivation(final boolean b) {
        if (!b || EventQueue.isDispatchThread()) {
            ((WFramePeer)this.getPeer()).emulateActivation(b);
        }
        else {
            SunToolkit.postEvent(SunToolkit.targetToAppContext(this), new InvocationEvent(this, new Runnable() {
                @Override
                public void run() {
                    ((WFramePeer)WEmbeddedFrame.this.getPeer()).emulateActivation(true);
                }
            }));
        }
    }
    
    @Override
    public void registerAccelerator(final AWTKeyStroke awtKeyStroke) {
    }
    
    @Override
    public void unregisterAccelerator(final AWTKeyStroke awtKeyStroke) {
    }
    
    @Override
    public void notifyModalBlocked(final Dialog dialog, final boolean b) {
        try {
            this.notifyModalBlockedImpl((WEmbeddedFramePeer)WToolkit.targetToPeer(this), (WWindowPeer)WToolkit.targetToPeer(dialog), b);
        }
        catch (final Exception ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    native void notifyModalBlockedImpl(final WEmbeddedFramePeer p0, final WWindowPeer p1, final boolean p2);
    
    static {
        initIDs();
        WEmbeddedFrame.pScale = 0;
        WEmbeddedFrame.printScale = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.java2d.print.pluginscalefactor"));
    }
}
