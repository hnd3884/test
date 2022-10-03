package javax.swing;

import java.util.Hashtable;
import java.util.ArrayList;
import sun.awt.AWTAccessor;
import java.awt.event.KeyEvent;
import java.awt.KeyEventPostProcessor;
import sun.awt.PaintEventDispatcher;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import sun.awt.AppContext;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.beans.PropertyChangeListener;
import java.util.Vector;
import javax.swing.plaf.ComponentUI;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Component;
import javax.swing.border.Border;
import java.awt.Color;
import java.util.Locale;
import java.awt.Font;
import sun.awt.SunToolkit;
import java.awt.Toolkit;
import sun.awt.OSInfo;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.event.SwingPropertyChangeSupport;
import sun.swing.DefaultLookup;
import java.io.File;
import sun.swing.SwingUtilities2;
import java.io.Serializable;

public class UIManager implements Serializable
{
    private static final Object classLock;
    private static final String defaultLAFKey = "swing.defaultlaf";
    private static final String auxiliaryLAFsKey = "swing.auxiliarylaf";
    private static final String multiplexingLAFKey = "swing.plaf.multiplexinglaf";
    private static final String installedLAFsKey = "swing.installedlafs";
    private static final String disableMnemonicKey = "swing.disablenavaids";
    private static LookAndFeelInfo[] installedLAFs;
    
    private static LAFState getLAFState() {
        LAFState lafState = (LAFState)SwingUtilities.appContextGet(SwingUtilities2.LAF_STATE_KEY);
        if (lafState == null) {
            synchronized (UIManager.classLock) {
                lafState = (LAFState)SwingUtilities.appContextGet(SwingUtilities2.LAF_STATE_KEY);
                if (lafState == null) {
                    SwingUtilities.appContextPut(SwingUtilities2.LAF_STATE_KEY, lafState = new LAFState());
                }
            }
        }
        return lafState;
    }
    
    private static String makeInstalledLAFKey(final String s, final String s2) {
        return "swing.installedlaf." + s + "." + s2;
    }
    
    private static String makeSwingPropertiesFilename() {
        final String separator = File.separator;
        String property = System.getProperty("java.home");
        if (property == null) {
            property = "<java.home undefined>";
        }
        return property + separator + "lib" + separator + "swing.properties";
    }
    
    public static LookAndFeelInfo[] getInstalledLookAndFeels() {
        maybeInitialize();
        LookAndFeelInfo[] array = getLAFState().installedLAFs;
        if (array == null) {
            array = UIManager.installedLAFs;
        }
        final LookAndFeelInfo[] array2 = new LookAndFeelInfo[array.length];
        System.arraycopy(array, 0, array2, 0, array.length);
        return array2;
    }
    
    public static void setInstalledLookAndFeels(final LookAndFeelInfo[] array) throws SecurityException {
        maybeInitialize();
        final LookAndFeelInfo[] installedLAFs = new LookAndFeelInfo[array.length];
        System.arraycopy(array, 0, installedLAFs, 0, array.length);
        getLAFState().installedLAFs = installedLAFs;
    }
    
    public static void installLookAndFeel(final LookAndFeelInfo lookAndFeelInfo) {
        final LookAndFeelInfo[] installedLookAndFeels = getInstalledLookAndFeels();
        final LookAndFeelInfo[] installedLookAndFeels2 = new LookAndFeelInfo[installedLookAndFeels.length + 1];
        System.arraycopy(installedLookAndFeels, 0, installedLookAndFeels2, 0, installedLookAndFeels.length);
        installedLookAndFeels2[installedLookAndFeels.length] = lookAndFeelInfo;
        setInstalledLookAndFeels(installedLookAndFeels2);
    }
    
    public static void installLookAndFeel(final String s, final String s2) {
        installLookAndFeel(new LookAndFeelInfo(s, s2));
    }
    
    public static LookAndFeel getLookAndFeel() {
        maybeInitialize();
        return getLAFState().lookAndFeel;
    }
    
    public static void setLookAndFeel(final LookAndFeel lookAndFeel) throws UnsupportedLookAndFeelException {
        if (lookAndFeel != null && !lookAndFeel.isSupportedLookAndFeel()) {
            throw new UnsupportedLookAndFeelException(lookAndFeel.toString() + " not supported on this platform");
        }
        final LAFState lafState = getLAFState();
        final LookAndFeel lookAndFeel2 = lafState.lookAndFeel;
        if (lookAndFeel2 != null) {
            lookAndFeel2.uninitialize();
        }
        if ((lafState.lookAndFeel = lookAndFeel) != null) {
            DefaultLookup.setDefaultLookup(null);
            lookAndFeel.initialize();
            lafState.setLookAndFeelDefaults(lookAndFeel.getDefaults());
        }
        else {
            lafState.setLookAndFeelDefaults(null);
        }
        final SwingPropertyChangeSupport propertyChangeSupport = lafState.getPropertyChangeSupport(false);
        if (propertyChangeSupport != null) {
            propertyChangeSupport.firePropertyChange("lookAndFeel", lookAndFeel2, lookAndFeel);
        }
    }
    
    public static void setLookAndFeel(final String s) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        if ("javax.swing.plaf.metal.MetalLookAndFeel".equals(s)) {
            setLookAndFeel(new MetalLookAndFeel());
        }
        else {
            setLookAndFeel((LookAndFeel)SwingUtilities.loadSystemClass(s).newInstance());
        }
    }
    
    public static String getSystemLookAndFeelClassName() {
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("swing.systemlaf"));
        if (s != null) {
            return s;
        }
        final OSInfo.OSType osType = AccessController.doPrivileged(OSInfo.getOSTypeAction());
        if (osType == OSInfo.OSType.WINDOWS) {
            return "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
        }
        final String s2 = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.desktop"));
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        if ("gnome".equals(s2) && defaultToolkit instanceof SunToolkit && ((SunToolkit)defaultToolkit).isNativeGTKAvailable()) {
            return "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
        }
        if (osType == OSInfo.OSType.MACOSX && ((SunToolkit)defaultToolkit).getClass().getName().equals("sun.lwawt.macosx.LWCToolkit")) {
            return "com.apple.laf.AquaLookAndFeel";
        }
        if (osType == OSInfo.OSType.SOLARIS) {
            return "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
        }
        return getCrossPlatformLookAndFeelClassName();
    }
    
    public static String getCrossPlatformLookAndFeelClassName() {
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("swing.crossplatformlaf"));
        if (s != null) {
            return s;
        }
        return "javax.swing.plaf.metal.MetalLookAndFeel";
    }
    
    public static UIDefaults getDefaults() {
        maybeInitialize();
        return getLAFState().multiUIDefaults;
    }
    
    public static Font getFont(final Object o) {
        return getDefaults().getFont(o);
    }
    
    public static Font getFont(final Object o, final Locale locale) {
        return getDefaults().getFont(o, locale);
    }
    
    public static Color getColor(final Object o) {
        return getDefaults().getColor(o);
    }
    
    public static Color getColor(final Object o, final Locale locale) {
        return getDefaults().getColor(o, locale);
    }
    
    public static Icon getIcon(final Object o) {
        return getDefaults().getIcon(o);
    }
    
    public static Icon getIcon(final Object o, final Locale locale) {
        return getDefaults().getIcon(o, locale);
    }
    
    public static Border getBorder(final Object o) {
        return getDefaults().getBorder(o);
    }
    
    public static Border getBorder(final Object o, final Locale locale) {
        return getDefaults().getBorder(o, locale);
    }
    
    public static String getString(final Object o) {
        return getDefaults().getString(o);
    }
    
    public static String getString(final Object o, final Locale locale) {
        return getDefaults().getString(o, locale);
    }
    
    static String getString(final Object o, final Component component) {
        return getString(o, (component == null) ? Locale.getDefault() : component.getLocale());
    }
    
    public static int getInt(final Object o) {
        return getDefaults().getInt(o);
    }
    
    public static int getInt(final Object o, final Locale locale) {
        return getDefaults().getInt(o, locale);
    }
    
    public static boolean getBoolean(final Object o) {
        return getDefaults().getBoolean(o);
    }
    
    public static boolean getBoolean(final Object o, final Locale locale) {
        return getDefaults().getBoolean(o, locale);
    }
    
    public static Insets getInsets(final Object o) {
        return getDefaults().getInsets(o);
    }
    
    public static Insets getInsets(final Object o, final Locale locale) {
        return getDefaults().getInsets(o, locale);
    }
    
    public static Dimension getDimension(final Object o) {
        return getDefaults().getDimension(o);
    }
    
    public static Dimension getDimension(final Object o, final Locale locale) {
        return getDefaults().getDimension(o, locale);
    }
    
    public static Object get(final Object o) {
        return getDefaults().get(o);
    }
    
    public static Object get(final Object o, final Locale locale) {
        return getDefaults().get(o, locale);
    }
    
    public static Object put(final Object o, final Object o2) {
        return getDefaults().put(o, o2);
    }
    
    public static ComponentUI getUI(final JComponent component) {
        maybeInitialize();
        maybeInitializeFocusPolicy(component);
        ComponentUI componentUI = null;
        final LookAndFeel multiLookAndFeel = getLAFState().multiLookAndFeel;
        if (multiLookAndFeel != null) {
            componentUI = multiLookAndFeel.getDefaults().getUI(component);
        }
        if (componentUI == null) {
            componentUI = getDefaults().getUI(component);
        }
        return componentUI;
    }
    
    public static UIDefaults getLookAndFeelDefaults() {
        maybeInitialize();
        return getLAFState().getLookAndFeelDefaults();
    }
    
    private static LookAndFeel getMultiLookAndFeel() {
        LookAndFeel multiLookAndFeel = getLAFState().multiLookAndFeel;
        if (multiLookAndFeel == null) {
            final String property = getLAFState().swingProps.getProperty("swing.plaf.multiplexinglaf", "javax.swing.plaf.multi.MultiLookAndFeel");
            try {
                multiLookAndFeel = (LookAndFeel)SwingUtilities.loadSystemClass(property).newInstance();
            }
            catch (final Exception ex) {
                System.err.println("UIManager: failed loading " + property);
            }
        }
        return multiLookAndFeel;
    }
    
    public static void addAuxiliaryLookAndFeel(final LookAndFeel lookAndFeel) {
        maybeInitialize();
        if (!lookAndFeel.isSupportedLookAndFeel()) {
            return;
        }
        Vector<LookAndFeel> auxLookAndFeels = getLAFState().auxLookAndFeels;
        if (auxLookAndFeels == null) {
            auxLookAndFeels = new Vector<LookAndFeel>();
        }
        if (!auxLookAndFeels.contains(lookAndFeel)) {
            auxLookAndFeels.addElement(lookAndFeel);
            lookAndFeel.initialize();
            getLAFState().auxLookAndFeels = auxLookAndFeels;
            if (getLAFState().multiLookAndFeel == null) {
                getLAFState().multiLookAndFeel = getMultiLookAndFeel();
            }
        }
    }
    
    public static boolean removeAuxiliaryLookAndFeel(final LookAndFeel lookAndFeel) {
        maybeInitialize();
        final Vector<LookAndFeel> auxLookAndFeels = getLAFState().auxLookAndFeels;
        if (auxLookAndFeels == null || auxLookAndFeels.size() == 0) {
            return false;
        }
        final boolean removeElement = auxLookAndFeels.removeElement(lookAndFeel);
        if (removeElement) {
            if (auxLookAndFeels.size() == 0) {
                getLAFState().auxLookAndFeels = null;
                getLAFState().multiLookAndFeel = null;
            }
            else {
                getLAFState().auxLookAndFeels = auxLookAndFeels;
            }
        }
        lookAndFeel.uninitialize();
        return removeElement;
    }
    
    public static LookAndFeel[] getAuxiliaryLookAndFeels() {
        maybeInitialize();
        final Vector<LookAndFeel> auxLookAndFeels = getLAFState().auxLookAndFeels;
        if (auxLookAndFeels == null || auxLookAndFeels.size() == 0) {
            return null;
        }
        final LookAndFeel[] array = new LookAndFeel[auxLookAndFeels.size()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = (LookAndFeel)auxLookAndFeels.elementAt(i);
        }
        return array;
    }
    
    public static void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        synchronized (UIManager.classLock) {
            getLAFState().getPropertyChangeSupport(true).addPropertyChangeListener(propertyChangeListener);
        }
    }
    
    public static void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        synchronized (UIManager.classLock) {
            getLAFState().getPropertyChangeSupport(true).removePropertyChangeListener(propertyChangeListener);
        }
    }
    
    public static PropertyChangeListener[] getPropertyChangeListeners() {
        synchronized (UIManager.classLock) {
            return getLAFState().getPropertyChangeSupport(true).getPropertyChangeListeners();
        }
    }
    
    private static Properties loadSwingProperties() {
        if (UIManager.class.getClassLoader() != null) {
            return new Properties();
        }
        final Properties properties = new Properties();
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                if (AccessController.doPrivileged(OSInfo.getOSTypeAction()) == OSInfo.OSType.MACOSX) {
                    ((Hashtable<String, String>)properties).put("swing.defaultlaf", UIManager.getSystemLookAndFeelClassName());
                }
                try {
                    final File file = new File(makeSwingPropertiesFilename());
                    if (file.exists()) {
                        final FileInputStream fileInputStream = new FileInputStream(file);
                        properties.load(fileInputStream);
                        fileInputStream.close();
                    }
                }
                catch (final Exception ex) {}
                checkProperty(properties, "swing.defaultlaf");
                checkProperty(properties, "swing.auxiliarylaf");
                checkProperty(properties, "swing.plaf.multiplexinglaf");
                checkProperty(properties, "swing.installedlafs");
                checkProperty(properties, "swing.disablenavaids");
                return null;
            }
        });
        return properties;
    }
    
    private static void checkProperty(final Properties properties, final String s) {
        final String property = System.getProperty(s);
        if (property != null) {
            ((Hashtable<String, String>)properties).put(s, property);
        }
    }
    
    private static void initializeInstalledLAFs(final Properties properties) {
        final String property = properties.getProperty("swing.installedlafs");
        if (property == null) {
            return;
        }
        final Vector vector = new Vector();
        final StringTokenizer stringTokenizer = new StringTokenizer(property, ",", false);
        while (stringTokenizer.hasMoreTokens()) {
            vector.addElement(stringTokenizer.nextToken());
        }
        final Vector vector2 = new Vector<LookAndFeelInfo>(vector.size());
        for (final String s : vector) {
            final String property2 = properties.getProperty(makeInstalledLAFKey(s, "name"), s);
            final String property3 = properties.getProperty(makeInstalledLAFKey(s, "class"));
            if (property3 != null) {
                vector2.addElement(new LookAndFeelInfo(property2, property3));
            }
        }
        final LookAndFeelInfo[] installedLAFs = new LookAndFeelInfo[vector2.size()];
        for (int i = 0; i < vector2.size(); ++i) {
            installedLAFs[i] = vector2.elementAt(i);
        }
        getLAFState().installedLAFs = installedLAFs;
    }
    
    private static void initializeDefaultLAF(final Properties properties) {
        if (getLAFState().lookAndFeel != null) {
            return;
        }
        String crossPlatformLookAndFeelClassName = null;
        final HashMap hashMap = (HashMap)AppContext.getAppContext().remove("swing.lafdata");
        if (hashMap != null) {
            crossPlatformLookAndFeelClassName = (String)hashMap.remove("defaultlaf");
        }
        if (crossPlatformLookAndFeelClassName == null) {
            crossPlatformLookAndFeelClassName = getCrossPlatformLookAndFeelClassName();
        }
        final String property = properties.getProperty("swing.defaultlaf", crossPlatformLookAndFeelClassName);
        try {
            setLookAndFeel(property);
        }
        catch (final Exception ex) {
            throw new Error("Cannot load " + property);
        }
        if (hashMap != null) {
            for (final Object next : hashMap.keySet()) {
                put(next, hashMap.get(next));
            }
        }
    }
    
    private static void initializeAuxiliaryLAFs(final Properties properties) {
        final String property = properties.getProperty("swing.auxiliarylaf");
        if (property == null) {
            return;
        }
        Vector auxLookAndFeels = new Vector();
        final StringTokenizer stringTokenizer = new StringTokenizer(property, ",");
        while (stringTokenizer.hasMoreTokens()) {
            final String nextToken = stringTokenizer.nextToken();
            try {
                final LookAndFeel lookAndFeel = (LookAndFeel)SwingUtilities.loadSystemClass(nextToken).newInstance();
                lookAndFeel.initialize();
                auxLookAndFeels.addElement(lookAndFeel);
            }
            catch (final Exception ex) {
                System.err.println("UIManager: failed loading auxiliary look and feel " + nextToken);
            }
        }
        if (auxLookAndFeels.size() == 0) {
            auxLookAndFeels = null;
        }
        else {
            getLAFState().multiLookAndFeel = getMultiLookAndFeel();
            if (getLAFState().multiLookAndFeel == null) {
                auxLookAndFeels = null;
            }
        }
        getLAFState().auxLookAndFeels = auxLookAndFeels;
    }
    
    private static void initializeSystemDefaults(final Properties swingProps) {
        getLAFState().swingProps = swingProps;
    }
    
    private static void maybeInitialize() {
        synchronized (UIManager.classLock) {
            if (!getLAFState().initialized) {
                getLAFState().initialized = true;
                initialize();
            }
        }
    }
    
    private static void maybeInitializeFocusPolicy(final JComponent component) {
        if (component instanceof JRootPane) {
            synchronized (UIManager.classLock) {
                if (!getLAFState().focusPolicyInitialized) {
                    getLAFState().focusPolicyInitialized = true;
                    if (FocusManager.isFocusManagerEnabled()) {
                        KeyboardFocusManager.getCurrentKeyboardFocusManager().setDefaultFocusTraversalPolicy(new LayoutFocusTraversalPolicy());
                    }
                }
            }
        }
    }
    
    private static void initialize() {
        final Properties loadSwingProperties = loadSwingProperties();
        initializeSystemDefaults(loadSwingProperties);
        initializeDefaultLAF(loadSwingProperties);
        initializeAuxiliaryLAFs(loadSwingProperties);
        initializeInstalledLAFs(loadSwingProperties);
        if (RepaintManager.HANDLE_TOP_LEVEL_PAINT) {
            PaintEventDispatcher.setPaintEventDispatcher(new SwingPaintEventDispatcher());
        }
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(new KeyEventPostProcessor() {
            @Override
            public boolean postProcessKeyEvent(final KeyEvent keyEvent) {
                final Component component = keyEvent.getComponent();
                if ((!(component instanceof JComponent) || (component != null && !component.isEnabled())) && JComponent.KeyboardState.shouldProcess(keyEvent) && SwingUtilities.processKeyBindings(keyEvent)) {
                    keyEvent.consume();
                    return true;
                }
                return false;
            }
        });
        AWTAccessor.getComponentAccessor().setRequestFocusController(JComponent.focusController);
    }
    
    static {
        classLock = new Object();
        final ArrayList list = new ArrayList(4);
        list.add(new LookAndFeelInfo("Metal", "javax.swing.plaf.metal.MetalLookAndFeel"));
        list.add(new LookAndFeelInfo("Nimbus", "javax.swing.plaf.nimbus.NimbusLookAndFeel"));
        list.add(new LookAndFeelInfo("CDE/Motif", "com.sun.java.swing.plaf.motif.MotifLookAndFeel"));
        final OSInfo.OSType osType = AccessController.doPrivileged(OSInfo.getOSTypeAction());
        if (osType == OSInfo.OSType.WINDOWS) {
            list.add(new LookAndFeelInfo("Windows", "com.sun.java.swing.plaf.windows.WindowsLookAndFeel"));
            if (Toolkit.getDefaultToolkit().getDesktopProperty("win.xpstyle.themeActive") != null) {
                list.add(new LookAndFeelInfo("Windows Classic", "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel"));
            }
        }
        else if (osType == OSInfo.OSType.MACOSX) {
            list.add(new LookAndFeelInfo("Mac OS X", "com.apple.laf.AquaLookAndFeel"));
        }
        else {
            list.add(new LookAndFeelInfo("GTK+", "com.sun.java.swing.plaf.gtk.GTKLookAndFeel"));
        }
        UIManager.installedLAFs = list.toArray(new LookAndFeelInfo[list.size()]);
    }
    
    private static class LAFState
    {
        Properties swingProps;
        private UIDefaults[] tables;
        boolean initialized;
        boolean focusPolicyInitialized;
        MultiUIDefaults multiUIDefaults;
        LookAndFeel lookAndFeel;
        LookAndFeel multiLookAndFeel;
        Vector<LookAndFeel> auxLookAndFeels;
        SwingPropertyChangeSupport changeSupport;
        LookAndFeelInfo[] installedLAFs;
        
        private LAFState() {
            this.tables = new UIDefaults[2];
            this.initialized = false;
            this.focusPolicyInitialized = false;
            this.multiUIDefaults = new MultiUIDefaults(this.tables);
            this.multiLookAndFeel = null;
            this.auxLookAndFeels = null;
        }
        
        UIDefaults getLookAndFeelDefaults() {
            return this.tables[0];
        }
        
        void setLookAndFeelDefaults(final UIDefaults uiDefaults) {
            this.tables[0] = uiDefaults;
        }
        
        UIDefaults getSystemDefaults() {
            return this.tables[1];
        }
        
        void setSystemDefaults(final UIDefaults uiDefaults) {
            this.tables[1] = uiDefaults;
        }
        
        public synchronized SwingPropertyChangeSupport getPropertyChangeSupport(final boolean b) {
            if (b && this.changeSupport == null) {
                this.changeSupport = new SwingPropertyChangeSupport(UIManager.class);
            }
            return this.changeSupport;
        }
    }
    
    public static class LookAndFeelInfo
    {
        private String name;
        private String className;
        
        public LookAndFeelInfo(final String name, final String className) {
            this.name = name;
            this.className = className;
        }
        
        public String getName() {
            return this.name;
        }
        
        public String getClassName() {
            return this.className;
        }
        
        @Override
        public String toString() {
            return this.getClass().getName() + "[" + this.getName() + " " + this.getClassName() + "]";
        }
    }
}
