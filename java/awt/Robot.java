package java.awt;

import java.lang.reflect.InvocationTargetException;
import java.awt.image.WritableRaster;
import java.util.Hashtable;
import java.awt.image.ColorModel;
import sun.awt.image.SunWritableRaster;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.DataBufferInt;
import java.awt.image.BufferedImage;
import sun.security.util.SecurityConstants;
import java.awt.event.InputEvent;
import sun.awt.SunToolkit;
import sun.java2d.DisposerRecord;
import sun.java2d.Disposer;
import sun.awt.ComponentFactory;
import java.awt.image.DirectColorModel;
import java.awt.peer.RobotPeer;

public class Robot
{
    private static final int MAX_DELAY = 60000;
    private RobotPeer peer;
    private boolean isAutoWaitForIdle;
    private int autoDelay;
    private static int LEGAL_BUTTON_MASK;
    private DirectColorModel screenCapCM;
    private transient Object anchor;
    private transient RobotDisposer disposer;
    
    public Robot() throws AWTException {
        this.isAutoWaitForIdle = false;
        this.autoDelay = 0;
        this.screenCapCM = null;
        this.anchor = new Object();
        if (GraphicsEnvironment.isHeadless()) {
            throw new AWTException("headless environment");
        }
        this.init(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice());
    }
    
    public Robot(final GraphicsDevice graphicsDevice) throws AWTException {
        this.isAutoWaitForIdle = false;
        this.autoDelay = 0;
        this.screenCapCM = null;
        this.anchor = new Object();
        this.checkIsScreenDevice(graphicsDevice);
        this.init(graphicsDevice);
    }
    
    private void init(final GraphicsDevice graphicsDevice) throws AWTException {
        this.checkRobotAllowed();
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        if (defaultToolkit instanceof ComponentFactory) {
            this.peer = ((ComponentFactory)defaultToolkit).createRobot(this, graphicsDevice);
            this.disposer = new RobotDisposer(this.peer);
            Disposer.addRecord(this.anchor, this.disposer);
        }
        initLegalButtonMask();
    }
    
    private static synchronized void initLegalButtonMask() {
        if (Robot.LEGAL_BUTTON_MASK != 0) {
            return;
        }
        int n = 0;
        if (Toolkit.getDefaultToolkit().areExtraMouseButtonsEnabled() && Toolkit.getDefaultToolkit() instanceof SunToolkit) {
            for (int numberOfButtons = ((SunToolkit)Toolkit.getDefaultToolkit()).getNumberOfButtons(), i = 0; i < numberOfButtons; ++i) {
                n |= InputEvent.getMaskForButton(i + 1);
            }
        }
        Robot.LEGAL_BUTTON_MASK = (n | 0x1C1C);
    }
    
    private void checkRobotAllowed() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(SecurityConstants.AWT.CREATE_ROBOT_PERMISSION);
        }
    }
    
    private void checkIsScreenDevice(final GraphicsDevice graphicsDevice) {
        if (graphicsDevice == null || graphicsDevice.getType() != 0) {
            throw new IllegalArgumentException("not a valid screen device");
        }
    }
    
    public synchronized void mouseMove(final int n, final int n2) {
        this.peer.mouseMove(n, n2);
        this.afterEvent();
    }
    
    public synchronized void mousePress(final int n) {
        this.checkButtonsArgument(n);
        this.peer.mousePress(n);
        this.afterEvent();
    }
    
    public synchronized void mouseRelease(final int n) {
        this.checkButtonsArgument(n);
        this.peer.mouseRelease(n);
        this.afterEvent();
    }
    
    private void checkButtonsArgument(final int n) {
        if ((n | Robot.LEGAL_BUTTON_MASK) != Robot.LEGAL_BUTTON_MASK) {
            throw new IllegalArgumentException("Invalid combination of button flags");
        }
    }
    
    public synchronized void mouseWheel(final int n) {
        this.peer.mouseWheel(n);
        this.afterEvent();
    }
    
    public synchronized void keyPress(final int n) {
        this.checkKeycodeArgument(n);
        this.peer.keyPress(n);
        this.afterEvent();
    }
    
    public synchronized void keyRelease(final int n) {
        this.checkKeycodeArgument(n);
        this.peer.keyRelease(n);
        this.afterEvent();
    }
    
    private void checkKeycodeArgument(final int n) {
        if (n == 0) {
            throw new IllegalArgumentException("Invalid key code");
        }
    }
    
    public synchronized Color getPixelColor(final int n, final int n2) {
        checkScreenCaptureAllowed();
        return new Color(this.peer.getRGBPixel(n, n2));
    }
    
    public synchronized BufferedImage createScreenCapture(final Rectangle rectangle) {
        checkScreenCaptureAllowed();
        checkValidRect(rectangle);
        if (this.screenCapCM == null) {
            this.screenCapCM = new DirectColorModel(24, 16711680, 65280, 255);
        }
        Toolkit.getDefaultToolkit().sync();
        final int[] array = new int[3];
        final int[] rgbPixels = this.peer.getRGBPixels(rectangle);
        final DataBufferInt dataBufferInt = new DataBufferInt(rgbPixels, rgbPixels.length);
        array[0] = this.screenCapCM.getRedMask();
        array[1] = this.screenCapCM.getGreenMask();
        array[2] = this.screenCapCM.getBlueMask();
        final WritableRaster packedRaster = Raster.createPackedRaster(dataBufferInt, rectangle.width, rectangle.height, rectangle.width, array, null);
        SunWritableRaster.makeTrackable(dataBufferInt);
        return new BufferedImage(this.screenCapCM, packedRaster, false, null);
    }
    
    private static void checkValidRect(final Rectangle rectangle) {
        if (rectangle.width <= 0 || rectangle.height <= 0) {
            throw new IllegalArgumentException("Rectangle width and height must be > 0");
        }
    }
    
    private static void checkScreenCaptureAllowed() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(SecurityConstants.AWT.READ_DISPLAY_PIXELS_PERMISSION);
        }
    }
    
    private void afterEvent() {
        this.autoWaitForIdle();
        this.autoDelay();
    }
    
    public synchronized boolean isAutoWaitForIdle() {
        return this.isAutoWaitForIdle;
    }
    
    public synchronized void setAutoWaitForIdle(final boolean isAutoWaitForIdle) {
        this.isAutoWaitForIdle = isAutoWaitForIdle;
    }
    
    private void autoWaitForIdle() {
        if (this.isAutoWaitForIdle) {
            this.waitForIdle();
        }
    }
    
    public synchronized int getAutoDelay() {
        return this.autoDelay;
    }
    
    public synchronized void setAutoDelay(final int autoDelay) {
        this.checkDelayArgument(autoDelay);
        this.autoDelay = autoDelay;
    }
    
    private void autoDelay() {
        this.delay(this.autoDelay);
    }
    
    public synchronized void delay(final int n) {
        this.checkDelayArgument(n);
        try {
            Thread.sleep(n);
        }
        catch (final InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    private void checkDelayArgument(final int n) {
        if (n < 0 || n > 60000) {
            throw new IllegalArgumentException("Delay must be to 0 to 60,000ms");
        }
    }
    
    public synchronized void waitForIdle() {
        this.checkNotDispatchThread();
        try {
            SunToolkit.flushPendingEvents();
            EventQueue.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                }
            });
        }
        catch (final InterruptedException ex) {
            System.err.println("Robot.waitForIdle, non-fatal exception caught:");
            ex.printStackTrace();
        }
        catch (final InvocationTargetException ex2) {
            System.err.println("Robot.waitForIdle, non-fatal exception caught:");
            ex2.printStackTrace();
        }
    }
    
    private void checkNotDispatchThread() {
        if (EventQueue.isDispatchThread()) {
            throw new IllegalThreadStateException("Cannot call method from the event dispatcher thread");
        }
    }
    
    @Override
    public synchronized String toString() {
        return this.getClass().getName() + "[ " + ("autoDelay = " + this.getAutoDelay() + ", autoWaitForIdle = " + this.isAutoWaitForIdle()) + " ]";
    }
    
    static {
        Robot.LEGAL_BUTTON_MASK = 0;
    }
    
    static class RobotDisposer implements DisposerRecord
    {
        private final RobotPeer peer;
        
        public RobotDisposer(final RobotPeer peer) {
            this.peer = peer;
        }
        
        @Override
        public void dispose() {
            if (this.peer != null) {
                this.peer.dispose();
            }
        }
    }
}
