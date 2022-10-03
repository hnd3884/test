package java.awt;

import sun.awt.AWTAccessor;
import java.io.InputStream;
import java.io.FileInputStream;
import java.beans.ConstructorProperties;
import java.security.PrivilegedExceptionAction;
import java.util.StringTokenizer;
import sun.java2d.DisposerRecord;
import sun.java2d.Disposer;
import java.io.File;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import sun.util.logging.PlatformLogger;
import java.util.Properties;
import java.util.Hashtable;
import java.io.Serializable;

public class Cursor implements Serializable
{
    public static final int DEFAULT_CURSOR = 0;
    public static final int CROSSHAIR_CURSOR = 1;
    public static final int TEXT_CURSOR = 2;
    public static final int WAIT_CURSOR = 3;
    public static final int SW_RESIZE_CURSOR = 4;
    public static final int SE_RESIZE_CURSOR = 5;
    public static final int NW_RESIZE_CURSOR = 6;
    public static final int NE_RESIZE_CURSOR = 7;
    public static final int N_RESIZE_CURSOR = 8;
    public static final int S_RESIZE_CURSOR = 9;
    public static final int W_RESIZE_CURSOR = 10;
    public static final int E_RESIZE_CURSOR = 11;
    public static final int HAND_CURSOR = 12;
    public static final int MOVE_CURSOR = 13;
    @Deprecated
    protected static Cursor[] predefined;
    private static final Cursor[] predefinedPrivate;
    static final String[][] cursorProperties;
    int type;
    public static final int CUSTOM_CURSOR = -1;
    private static final Hashtable<String, Cursor> systemCustomCursors;
    private static final String systemCustomCursorDirPrefix;
    private static final String systemCustomCursorPropertiesFile;
    private static Properties systemCustomCursorProperties;
    private static final String CursorDotPrefix = "Cursor.";
    private static final String DotFileSuffix = ".File";
    private static final String DotHotspotSuffix = ".HotSpot";
    private static final String DotNameSuffix = ".Name";
    private static final long serialVersionUID = 8028237497568985504L;
    private static final PlatformLogger log;
    private transient long pData;
    private transient Object anchor;
    transient CursorDisposer disposer;
    protected String name;
    
    private static String initCursorDir() {
        return AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("java.home")) + File.separator + "lib" + File.separator + "images" + File.separator + "cursors" + File.separator;
    }
    
    private static native void initIDs();
    
    private void setPData(final long n) {
        this.pData = n;
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        if (this.disposer == null) {
            this.disposer = new CursorDisposer(n);
            if (this.anchor == null) {
                this.anchor = new Object();
            }
            Disposer.addRecord(this.anchor, this.disposer);
        }
        else {
            this.disposer.pData = n;
        }
    }
    
    public static Cursor getPredefinedCursor(final int n) {
        if (n < 0 || n > 13) {
            throw new IllegalArgumentException("illegal cursor type");
        }
        Cursor cursor = Cursor.predefinedPrivate[n];
        if (cursor == null) {
            cursor = (Cursor.predefinedPrivate[n] = new Cursor(n));
        }
        if (Cursor.predefined[n] == null) {
            Cursor.predefined[n] = cursor;
        }
        return cursor;
    }
    
    public static Cursor getSystemCustomCursor(final String s) throws AWTException, HeadlessException {
        GraphicsEnvironment.checkHeadless();
        Cursor cursor = Cursor.systemCustomCursors.get(s);
        if (cursor == null) {
            synchronized (Cursor.systemCustomCursors) {
                if (Cursor.systemCustomCursorProperties == null) {
                    loadSystemCustomCursorProperties();
                }
            }
            final String string = "Cursor." + s;
            final String string2 = string + ".File";
            if (!Cursor.systemCustomCursorProperties.containsKey(string2)) {
                if (Cursor.log.isLoggable(PlatformLogger.Level.FINER)) {
                    Cursor.log.finer("Cursor.getSystemCustomCursor(" + s + ") returned null");
                }
                return null;
            }
            final String property = Cursor.systemCustomCursorProperties.getProperty(string2);
            String property2 = Cursor.systemCustomCursorProperties.getProperty(string + ".Name");
            if (property2 == null) {
                property2 = s;
            }
            final String property3 = Cursor.systemCustomCursorProperties.getProperty(string + ".HotSpot");
            if (property3 == null) {
                throw new AWTException("no hotspot property defined for cursor: " + s);
            }
            final StringTokenizer stringTokenizer = new StringTokenizer(property3, ",");
            if (stringTokenizer.countTokens() != 2) {
                throw new AWTException("failed to parse hotspot property for cursor: " + s);
            }
            int int1;
            int int2;
            try {
                int1 = Integer.parseInt(stringTokenizer.nextToken());
                int2 = Integer.parseInt(stringTokenizer.nextToken());
            }
            catch (final NumberFormatException ex) {
                throw new AWTException("failed to parse hotspot property for cursor: " + s);
            }
            try {
                cursor = AccessController.doPrivileged((PrivilegedExceptionAction<Cursor>)new PrivilegedExceptionAction<Cursor>() {
                    @Override
                    public Cursor run() throws Exception {
                        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
                        return defaultToolkit.createCustomCursor(defaultToolkit.getImage(Cursor.systemCustomCursorDirPrefix + property), new Point(int1, int2), property2);
                    }
                });
            }
            catch (final Exception ex2) {
                throw new AWTException("Exception: " + ex2.getClass() + " " + ex2.getMessage() + " occurred while creating cursor " + s);
            }
            if (cursor == null) {
                if (Cursor.log.isLoggable(PlatformLogger.Level.FINER)) {
                    Cursor.log.finer("Cursor.getSystemCustomCursor(" + s + ") returned null");
                }
            }
            else {
                Cursor.systemCustomCursors.put(s, cursor);
            }
        }
        return cursor;
    }
    
    public static Cursor getDefaultCursor() {
        return getPredefinedCursor(0);
    }
    
    @ConstructorProperties({ "type" })
    public Cursor(final int type) {
        this.type = 0;
        this.anchor = new Object();
        if (type < 0 || type > 13) {
            throw new IllegalArgumentException("illegal cursor type");
        }
        this.type = type;
        this.name = Toolkit.getProperty(Cursor.cursorProperties[type][0], Cursor.cursorProperties[type][1]);
    }
    
    protected Cursor(final String name) {
        this.type = 0;
        this.anchor = new Object();
        this.type = -1;
        this.name = name;
    }
    
    public int getType() {
        return this.type;
    }
    
    public String getName() {
        return this.name;
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "[" + this.getName() + "]";
    }
    
    private static void loadSystemCustomCursorProperties() throws AWTException {
        synchronized (Cursor.systemCustomCursors) {
            Cursor.systemCustomCursorProperties = new Properties();
            try {
                AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Object>() {
                    @Override
                    public Object run() throws Exception {
                        InputStream inputStream = null;
                        try {
                            inputStream = new FileInputStream(Cursor.systemCustomCursorPropertiesFile);
                            Cursor.systemCustomCursorProperties.load(inputStream);
                        }
                        finally {
                            if (inputStream != null) {
                                ((FileInputStream)inputStream).close();
                            }
                        }
                        return null;
                    }
                });
            }
            catch (final Exception ex) {
                Cursor.systemCustomCursorProperties = null;
                throw new AWTException("Exception: " + ex.getClass() + " " + ex.getMessage() + " occurred while loading: " + Cursor.systemCustomCursorPropertiesFile);
            }
        }
    }
    
    private static native void finalizeImpl(final long p0);
    
    static {
        Cursor.predefined = new Cursor[14];
        predefinedPrivate = new Cursor[14];
        cursorProperties = new String[][] { { "AWT.DefaultCursor", "Default Cursor" }, { "AWT.CrosshairCursor", "Crosshair Cursor" }, { "AWT.TextCursor", "Text Cursor" }, { "AWT.WaitCursor", "Wait Cursor" }, { "AWT.SWResizeCursor", "Southwest Resize Cursor" }, { "AWT.SEResizeCursor", "Southeast Resize Cursor" }, { "AWT.NWResizeCursor", "Northwest Resize Cursor" }, { "AWT.NEResizeCursor", "Northeast Resize Cursor" }, { "AWT.NResizeCursor", "North Resize Cursor" }, { "AWT.SResizeCursor", "South Resize Cursor" }, { "AWT.WResizeCursor", "West Resize Cursor" }, { "AWT.EResizeCursor", "East Resize Cursor" }, { "AWT.HandCursor", "Hand Cursor" }, { "AWT.MoveCursor", "Move Cursor" } };
        systemCustomCursors = new Hashtable<String, Cursor>(1);
        systemCustomCursorDirPrefix = initCursorDir();
        systemCustomCursorPropertiesFile = Cursor.systemCustomCursorDirPrefix + "cursors.properties";
        Cursor.systemCustomCursorProperties = null;
        log = PlatformLogger.getLogger("java.awt.Cursor");
        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
        AWTAccessor.setCursorAccessor(new AWTAccessor.CursorAccessor() {
            @Override
            public long getPData(final Cursor cursor) {
                return cursor.pData;
            }
            
            @Override
            public void setPData(final Cursor cursor, final long n) {
                cursor.pData = n;
            }
            
            @Override
            public int getType(final Cursor cursor) {
                return cursor.type;
            }
        });
    }
    
    static class CursorDisposer implements DisposerRecord
    {
        volatile long pData;
        
        public CursorDisposer(final long pData) {
            this.pData = pData;
        }
        
        @Override
        public void dispose() {
            if (this.pData != 0L) {
                finalizeImpl(this.pData);
            }
        }
    }
}
