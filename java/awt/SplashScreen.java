package java.awt;

import java.awt.image.DataBuffer;
import sun.awt.image.SunWritableRaster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.DataBufferInt;
import java.net.MalformedURLException;
import java.io.File;
import java.io.InputStream;
import java.net.URLConnection;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.util.logging.PlatformLogger;
import java.net.URL;
import java.awt.image.BufferedImage;

public final class SplashScreen
{
    private BufferedImage image;
    private final long splashPtr;
    private static boolean wasClosed;
    private URL imageURL;
    private static SplashScreen theInstance;
    private static final PlatformLogger log;
    
    SplashScreen(final long splashPtr) {
        this.splashPtr = splashPtr;
    }
    
    public static SplashScreen getSplashScreen() {
        synchronized (SplashScreen.class) {
            if (GraphicsEnvironment.isHeadless()) {
                throw new HeadlessException();
            }
            if (!SplashScreen.wasClosed && SplashScreen.theInstance == null) {
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                    @Override
                    public Void run() {
                        System.loadLibrary("splashscreen");
                        return null;
                    }
                });
                final long getInstance = _getInstance();
                if (getInstance != 0L && _isVisible(getInstance)) {
                    SplashScreen.theInstance = new SplashScreen(getInstance);
                }
            }
            return SplashScreen.theInstance;
        }
    }
    
    public void setImageURL(final URL imageURL) throws NullPointerException, IOException, IllegalStateException {
        this.checkVisible();
        final URLConnection openConnection = imageURL.openConnection();
        openConnection.connect();
        int contentLength = openConnection.getContentLength();
        final InputStream inputStream = openConnection.getInputStream();
        byte[] array = new byte[contentLength];
        int n = 0;
        while (true) {
            int available = inputStream.available();
            if (available <= 0) {
                available = 1;
            }
            if (n + available > contentLength) {
                contentLength = n * 2;
                if (n + available > contentLength) {
                    contentLength = available + n;
                }
                final byte[] array2 = array;
                array = new byte[contentLength];
                System.arraycopy(array2, 0, array, 0, n);
            }
            final int read = inputStream.read(array, n, available);
            if (read < 0) {
                break;
            }
            n += read;
        }
        synchronized (SplashScreen.class) {
            this.checkVisible();
            if (!_setImageData(this.splashPtr, array)) {
                throw new IOException("Bad image format or i/o error when loading image");
            }
            this.imageURL = imageURL;
        }
    }
    
    private void checkVisible() {
        if (!this.isVisible()) {
            throw new IllegalStateException("no splash screen available");
        }
    }
    
    public URL getImageURL() throws IllegalStateException {
        synchronized (SplashScreen.class) {
            this.checkVisible();
            if (this.imageURL == null) {
                try {
                    final String getImageFileName = _getImageFileName(this.splashPtr);
                    final String getImageJarName = _getImageJarName(this.splashPtr);
                    if (getImageFileName != null) {
                        if (getImageJarName != null) {
                            this.imageURL = new URL("jar:" + new File(getImageJarName).toURL().toString() + "!/" + getImageFileName);
                        }
                        else {
                            this.imageURL = new File(getImageFileName).toURL();
                        }
                    }
                }
                catch (final MalformedURLException ex) {
                    if (SplashScreen.log.isLoggable(PlatformLogger.Level.FINE)) {
                        SplashScreen.log.fine("MalformedURLException caught in the getImageURL() method", ex);
                    }
                }
            }
            return this.imageURL;
        }
    }
    
    public Rectangle getBounds() throws IllegalStateException {
        synchronized (SplashScreen.class) {
            this.checkVisible();
            final float getScaleFactor = _getScaleFactor(this.splashPtr);
            final Rectangle getBounds = _getBounds(this.splashPtr);
            assert getScaleFactor > 0.0f;
            if (getScaleFactor > 0.0f && getScaleFactor != 1.0f) {
                getBounds.setSize((int)(getBounds.getWidth() / getScaleFactor), (int)(getBounds.getHeight() / getScaleFactor));
            }
            return getBounds;
        }
    }
    
    public Dimension getSize() throws IllegalStateException {
        return this.getBounds().getSize();
    }
    
    public Graphics2D createGraphics() throws IllegalStateException {
        synchronized (SplashScreen.class) {
            this.checkVisible();
            if (this.image == null) {
                final Dimension size = _getBounds(this.splashPtr).getSize();
                this.image = new BufferedImage(size.width, size.height, 2);
            }
            float getScaleFactor = _getScaleFactor(this.splashPtr);
            final Graphics2D graphics = this.image.createGraphics();
            assert getScaleFactor > 0.0f;
            if (getScaleFactor <= 0.0f) {
                getScaleFactor = 1.0f;
            }
            graphics.scale(getScaleFactor, getScaleFactor);
            return graphics;
        }
    }
    
    public void update() throws IllegalStateException {
        final BufferedImage image;
        synchronized (SplashScreen.class) {
            this.checkVisible();
            image = this.image;
        }
        if (image == null) {
            throw new IllegalStateException("no overlay image available");
        }
        final DataBuffer dataBuffer = image.getRaster().getDataBuffer();
        if (!(dataBuffer instanceof DataBufferInt)) {
            throw new AssertionError((Object)("Overlay image DataBuffer is of invalid type == " + ((DataBufferInt)dataBuffer).getClass().getName()));
        }
        final int numBanks = dataBuffer.getNumBanks();
        if (numBanks != 1) {
            throw new AssertionError((Object)("Invalid number of banks ==" + numBanks + " in overlay image DataBuffer"));
        }
        if (!(image.getSampleModel() instanceof SinglePixelPackedSampleModel)) {
            throw new AssertionError((Object)("Overlay image has invalid sample model == " + image.getSampleModel().getClass().getName()));
        }
        final int scanlineStride = ((SinglePixelPackedSampleModel)image.getSampleModel()).getScanlineStride();
        final Rectangle bounds = image.getRaster().getBounds();
        final int[] stealData = SunWritableRaster.stealData((DataBufferInt)dataBuffer, 0);
        synchronized (SplashScreen.class) {
            this.checkVisible();
            _update(this.splashPtr, stealData, bounds.x, bounds.y, bounds.width, bounds.height, scanlineStride);
        }
    }
    
    public void close() throws IllegalStateException {
        synchronized (SplashScreen.class) {
            this.checkVisible();
            _close(this.splashPtr);
            this.image = null;
            markClosed();
        }
    }
    
    static void markClosed() {
        synchronized (SplashScreen.class) {
            SplashScreen.wasClosed = true;
            SplashScreen.theInstance = null;
        }
    }
    
    public boolean isVisible() {
        synchronized (SplashScreen.class) {
            return !SplashScreen.wasClosed && _isVisible(this.splashPtr);
        }
    }
    
    private static native void _update(final long p0, final int[] p1, final int p2, final int p3, final int p4, final int p5, final int p6);
    
    private static native boolean _isVisible(final long p0);
    
    private static native Rectangle _getBounds(final long p0);
    
    private static native long _getInstance();
    
    private static native void _close(final long p0);
    
    private static native String _getImageFileName(final long p0);
    
    private static native String _getImageJarName(final long p0);
    
    private static native boolean _setImageData(final long p0, final byte[] p1);
    
    private static native float _getScaleFactor(final long p0);
    
    static {
        SplashScreen.wasClosed = false;
        SplashScreen.theInstance = null;
        log = PlatformLogger.getLogger("java.awt.SplashScreen");
    }
}
