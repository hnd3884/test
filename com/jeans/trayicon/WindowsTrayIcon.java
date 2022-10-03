package com.jeans.trayicon;

import java.util.Enumeration;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.Frame;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.image.PixelGrabber;
import java.awt.Image;
import java.util.Vector;
import java.awt.Window;
import java.awt.event.MouseListener;

public class WindowsTrayIcon
{
    public static final String TRAY_VERSION = "1.7.9b";
    private static TrayIconKeeper m_Keeper;
    private static TrayDummyComponent m_Dummy;
    private static MouseListener m_MouseHook;
    private static Window m_CurrentWindow;
    public static final int BALLOON_NONE = 0;
    public static final int BALLOON_INFO = 1;
    public static final int BALLOON_WARNING = 2;
    public static final int BALLOON_ERROR = 3;
    public static final int BALLOON_NOSOUND = 16;
    public static final int UNICODE_CONV_BALLOON = 2;
    public static final int UNICODE_CONV_SUPPORT = 1;
    public static final int NOERR = 0;
    public static final int NOTIFYPROCERR = -1;
    public static final int TOOMANYICONS = -2;
    public static final int NOTENOUGHMEM = -3;
    public static final int WRONGICONID = -4;
    public static final int DLLNOTFOUND = -5;
    public static final int NOVMS = -6;
    public static final int ERRTHREAD = -7;
    public static final int METHODID = -8;
    public static final int NOLISTENER = -9;
    public static final int JNIERR = -10;
    public static final int ERRORBALLOON = -18;
    public static final int FLASHW_STOP = 0;
    public static final int FLASHW_CAPTION = 1;
    public static final int FLASHW_TRAY = 2;
    public static final int FLASHW_ALL = 3;
    public static final int FLASHW_TIMER = 4;
    public static final int FLASHW_TIMERNOFG = 12;
    public static final int WIN_VER_UNKNOWN = 0;
    public static final int WIN_VER_WIN32 = 1;
    public static final int WIN_VER_95 = 2;
    public static final int WIN_VER_98 = 3;
    public static final int WIN_VER_ME = 4;
    public static final int WIN_VER_NT = 5;
    public static final int WIN_VER_2K = 6;
    public static final int WIN_VER_XP = 7;
    public static final int WIN_VER_NET = 8;
    private int m_ID;
    private TrayIconPopup m_Popup;
    private Vector m_ActList;
    private Vector m_MouseList;
    private Vector m_BalloonList;
    private static TrayIconCallback m_WMessageCallback;
    private static final int MOUSE_BTN_UP = 1;
    private static final int MOUSE_BTN_DOUBLE = 2;
    static final int POPUP_TYPE_ITEM = 0;
    static final int POPUP_TYPE_SEPARATOR = 1;
    static final int POPUP_TYPE_CHECKBOX = 2;
    static final int POPUP_TYPE_INIT_LEVEL = 3;
    static final int POPUP_TYPE_DONE_LEVEL = 4;
    static final int POPUP_MODE_ENABLE = 1;
    static final int POPUP_MODE_CHECK = 2;
    static final int POPUP_MODE_DEFAULT = 4;
    
    public static void initTrayIcon(final String s) {
        initTrayIcon(s, new WindowsTrayIcon());
    }
    
    public static void cleanUp() {
        if (WindowsTrayIcon.m_Keeper != null) {
            WindowsTrayIcon.m_Keeper.doNotify();
            WindowsTrayIcon.m_Keeper = null;
        }
        termTrayIcon();
    }
    
    public WindowsTrayIcon(final Image image, final int n, final int n2) throws TrayIconException, InterruptedException {
        this.m_ID = getFreeId();
        if (this.m_ID == -2) {
            throw new TrayIconException("Too many icons allocated");
        }
        if (this.m_ID == -5) {
            throw new TrayIconException("Error initializing native code DLL");
        }
        if (this.m_ID == -1) {
            throw new TrayIconException("Error setting up Windows notify procedure");
        }
        this.setImage(image, n, n2);
    }
    
    public void setImage(final Image image, final int n, final int n2) throws TrayIconException, InterruptedException {
        try {
            final int[] array = new int[n * n2];
            final PixelGrabber pixelGrabber = new PixelGrabber(image, 0, 0, n, n2, array, 0, n);
            pixelGrabber.grabPixels();
            if ((pixelGrabber.getStatus() & 0x80) != 0x0) {
                this.freeIcon();
                throw new TrayIconException("Error loading icon image");
            }
            setIconData(this.m_ID, n, n2, array);
        }
        catch (final InterruptedException ex) {
            this.freeIcon();
            throw ex;
        }
        catch (final NullPointerException ex2) {
            this.freeIcon();
            throw ex2;
        }
    }
    
    public void setVisible(final boolean b) {
        showIcon(this.m_ID, b);
    }
    
    public boolean isVisible() {
        return testVisible(this.m_ID) == 1;
    }
    
    public void setToolTipText(final String s) {
        setToolTip(this.m_ID, s);
    }
    
    public void showBalloon(final String s, final String s2, final int n, final int n2) throws TrayIconException {
        if (showBalloon(this.m_ID, s, s2, n, n2) == 0) {
            throw new TrayIconException("Error showing Balloon message");
        }
    }
    
    public void addActionListener(final ActionListener actionListener) {
        if (this.m_ActList == null) {
            this.m_ActList = new Vector();
            clickEnable(this, this.m_ID, true);
        }
        this.m_ActList.addElement(actionListener);
    }
    
    public void removeActionListener(final ActionListener actionListener) {
        this.m_ActList.removeElement(actionListener);
    }
    
    public void addMouseListener(final MouseListener mouseListener) {
        if (this.m_MouseList == null) {
            this.m_MouseList = new Vector();
            clickEnable(this, this.m_ID, true);
        }
        this.m_MouseList.addElement(mouseListener);
    }
    
    public void removeMouseListener(final MouseListener mouseListener) {
        this.m_MouseList.removeElement(mouseListener);
    }
    
    public void addBalloonListener(final TrayBalloonListener trayBalloonListener) {
        if (this.m_BalloonList == null) {
            this.m_BalloonList = new Vector();
            clickEnable(this, this.m_ID, true);
        }
        this.m_BalloonList.addElement(trayBalloonListener);
    }
    
    public void removeBalloonListener(final TrayBalloonListener trayBalloonListener) {
        this.m_BalloonList.removeElement(trayBalloonListener);
    }
    
    public void setPopup(final TrayIconPopup popup) {
        if (popup == null) {
            this.m_Popup = null;
            initPopup(this.m_ID, -1);
        }
        else {
            if (this.m_Popup == null) {
                clickEnable(this, this.m_ID, true);
            }
            initPopup(this.m_ID, popup.getNbLevels());
            popup.setTrayIcon(this, this.m_ID, -1);
            this.m_Popup = popup;
        }
    }
    
    public void freeIcon() {
        clickEnable(this, this.m_ID, false);
        freeIcon(this.m_ID);
    }
    
    public static native void setAlwaysOnTop(final Component p0, final boolean p1);
    
    public static native void enableUnicodeConversion(final int p0, final boolean p1);
    
    public static native boolean hasUnicodeConversion(final int p0);
    
    public static native boolean supportsBalloonMessages();
    
    public static native int getLastError();
    
    public static native boolean isRunning(final String p0);
    
    public static native int sendWindowsMessage(final String p0, final int p1);
    
    public static void setWindowsMessageCallback(final TrayIconCallback wMessageCallback) {
        WindowsTrayIcon.m_WMessageCallback = wMessageCallback;
    }
    
    public static void keepAlive() {
        if (WindowsTrayIcon.m_Keeper == null) {
            (WindowsTrayIcon.m_Keeper = new TrayIconKeeper()).start();
        }
    }
    
    public static void flashWindow(final Frame frame) throws TrayIconException {
        flashWindow(frame, 15, 0, 0);
    }
    
    public static void flashWindow(final Frame frame, final int n, final int n2, final int n3) throws TrayIconException {
        flashWindow(frame.getTitle(), n, n2, n3);
    }
    
    public static void flashWindow(final String s, final int n, final int n2, final int n3) throws TrayIconException {
        if (!flashWindowImpl(s, n, n2, n3)) {
            throw new TrayIconException("Flash window not supported");
        }
    }
    
    public static native boolean flashWindowImpl(final String p0, final int p1, final int p2, final int p3);
    
    public static void setCurrentWindow(final Window currentWindow) {
        WindowsTrayIcon.m_CurrentWindow = currentWindow;
    }
    
    public static native String getWindowsVersionString();
    
    public static native int getWindowsVersion();
    
    public static boolean supportsBallonInfo() {
        return getWindowsVersion() >= 6;
    }
    
    private WindowsTrayIcon() {
    }
    
    public static TrayDummyComponent getDummyComponent() {
        if (WindowsTrayIcon.m_Dummy == null) {
            WindowsTrayIcon.m_Dummy = new TrayDummyComponent();
        }
        return WindowsTrayIcon.m_Dummy;
    }
    
    private void notifyMouseListeners(final int n, final int n2, final int n3, final int n4) {
        final int n5 = ((n2 & 0x2) != 0x0) ? 2 : 1;
        final boolean b = (n2 & 0x1) != 0x0;
        if (this.m_ActList != null && n5 == 1 && !b) {
            ActionEvent actionEvent;
            if (n == 0) {
                actionEvent = new ActionEvent(this, 0, "Left");
            }
            else if (n == 1) {
                actionEvent = new ActionEvent(this, 0, "Right");
            }
            else {
                actionEvent = new ActionEvent(this, 0, "Middle");
            }
            final Enumeration elements = this.m_ActList.elements();
            while (elements.hasMoreElements()) {
                ((ActionListener)elements.nextElement()).actionPerformed(actionEvent);
            }
        }
        if (this.m_MouseList != null) {
            final int n6 = 0;
            int n7;
            if (n == 0) {
                n7 = (n6 | 0x10);
            }
            else if (n == 1) {
                n7 = (n6 | 0x8);
            }
            else {
                n7 = (n6 | 0x4);
            }
            final MouseEvent mouseEvent = new MouseEvent(getDummyComponent(), 0, 0L, n7, n3, n4, n5, n == 1);
            final Enumeration elements2 = this.m_MouseList.elements();
            while (elements2.hasMoreElements()) {
                final MouseListener mouseListener = (MouseListener)elements2.nextElement();
                if (b) {
                    mouseListener.mouseReleased(mouseEvent);
                }
                else {
                    mouseListener.mousePressed(mouseEvent);
                }
            }
        }
    }
    
    private void notifyBalloonListeners(final int n) {
        if (this.m_BalloonList != null) {
            final TrayBalloonEvent trayBalloonEvent = new TrayBalloonEvent(n);
            final Enumeration elements = this.m_BalloonList.elements();
            while (elements.hasMoreElements()) {
                ((TrayBalloonListener)elements.nextElement()).balloonChanged(trayBalloonEvent);
            }
        }
    }
    
    private void notifyMenuListeners(final int n) {
        if (this.m_Popup != null) {
            this.m_Popup.onSelected(n);
        }
    }
    
    private static int callWindowsMessage(final int n) {
        if (WindowsTrayIcon.m_WMessageCallback != null) {
            return WindowsTrayIcon.m_WMessageCallback.callback(n);
        }
        return 0;
    }
    
    private static void callMouseHook(final int n, final int n2) {
        if (WindowsTrayIcon.m_MouseHook != null) {
            WindowsTrayIcon.m_MouseHook.mousePressed(new MouseEvent(getDummyComponent(), 0, 0L, 0, n, n2, 1, true));
        }
    }
    
    void modifyPopup(final int n, final int n2, final boolean b) {
        modifyPopup(this.m_ID, n, n2, b);
    }
    
    static native void initPopup(final int p0, final int p1);
    
    static native int subPopup(final int p0, final int p1, final String p2, final int p3, final int p4);
    
    private static native void modifyPopup(final int p0, final int p1, final int p2, final boolean p3);
    
    private static native int getFreeId();
    
    private static native void setIconData(final int p0, final int p1, final int p2, final int[] p3);
    
    private static native void showIcon(final int p0, final boolean p1);
    
    private static native int testVisible(final int p0);
    
    private static native void clickEnable(final WindowsTrayIcon p0, final int p1, final boolean p2);
    
    private static native void setToolTip(final int p0, final String p1);
    
    private static native void freeIcon(final int p0);
    
    private static native void detectAllClicks(final int p0);
    
    public static native void initJAWT();
    
    public static native void initHook();
    
    public static native void setMouseHookEnabled(final int p0);
    
    public static void setMouseClickHook(final MouseListener mouseHook) {
        WindowsTrayIcon.m_MouseHook = mouseHook;
        setMouseHookEnabled((mouseHook != null) ? 1 : 0);
    }
    
    private static native void initTrayIcon(final String p0, final WindowsTrayIcon p1);
    
    private static native int showBalloon(final int p0, final String p1, final String p2, final int p3, final int p4);
    
    private static native void termTrayIcon();
    
    public static Window getCurrentWindow() {
        return WindowsTrayIcon.m_CurrentWindow;
    }
    
    static {
        boolean b = false;
        final String property = System.getProperty("java.version");
        if (property.length() >= 3) {
            if (property.substring(0, 3).equals("1.1")) {
                System.loadLibrary("TrayIcon11");
                b = true;
            }
            else {
                System.loadLibrary("TrayIcon12");
                b = true;
            }
        }
        if (!b) {
            System.out.println("Wrong Java VM version: " + property);
            System.exit(-1);
        }
    }
}
