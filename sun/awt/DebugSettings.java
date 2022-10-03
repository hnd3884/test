package sun.awt;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.Iterator;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;
import sun.util.logging.PlatformLogger;

final class DebugSettings
{
    private static final PlatformLogger log;
    static final String PREFIX = "awtdebug";
    static final String PROP_FILE = "properties";
    private static final String[] DEFAULT_PROPS;
    private static DebugSettings instance;
    private Properties props;
    private static final String PROP_CTRACE = "ctrace";
    private static final int PROP_CTRACE_LEN;
    
    static void init() {
        if (DebugSettings.instance != null) {
            return;
        }
        NativeLibLoader.loadLibraries();
        (DebugSettings.instance = new DebugSettings()).loadNativeSettings();
    }
    
    private DebugSettings() {
        this.props = new Properties();
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                DebugSettings.this.loadProperties();
                return null;
            }
        });
    }
    
    private synchronized void loadProperties() {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                DebugSettings.this.loadDefaultProperties();
                DebugSettings.this.loadFileProperties();
                DebugSettings.this.loadSystemProperties();
                return null;
            }
        });
        if (DebugSettings.log.isLoggable(PlatformLogger.Level.FINE)) {
            DebugSettings.log.fine("DebugSettings:\n{0}", this);
        }
    }
    
    @Override
    public String toString() {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final PrintStream printStream = new PrintStream(byteArrayOutputStream);
        for (final String s : this.props.stringPropertyNames()) {
            printStream.println(s + " = " + this.props.getProperty(s, ""));
        }
        return new String(byteArrayOutputStream.toByteArray());
    }
    
    private void loadDefaultProperties() {
        try {
            for (int i = 0; i < DebugSettings.DEFAULT_PROPS.length; ++i) {
                final StringBufferInputStream stringBufferInputStream = new StringBufferInputStream(DebugSettings.DEFAULT_PROPS[i]);
                this.props.load(stringBufferInputStream);
                stringBufferInputStream.close();
            }
        }
        catch (final IOException ex) {}
    }
    
    private void loadFileProperties() {
        String s = System.getProperty("awtdebug.properties", "");
        if (s.equals("")) {
            s = System.getProperty("user.home", "") + File.separator + "awtdebug" + "." + "properties";
        }
        final File file = new File(s);
        try {
            this.println("Reading debug settings from '" + file.getCanonicalPath() + "'...");
            final FileInputStream fileInputStream = new FileInputStream(file);
            this.props.load(fileInputStream);
            fileInputStream.close();
        }
        catch (final FileNotFoundException ex) {
            this.println("Did not find settings file.");
        }
        catch (final IOException ex2) {
            this.println("Problem reading settings, IOException: " + ex2.getMessage());
        }
    }
    
    private void loadSystemProperties() {
        final Properties properties = System.getProperties();
        for (final String s : properties.stringPropertyNames()) {
            final String property = properties.getProperty(s, "");
            if (s.startsWith("awtdebug")) {
                this.props.setProperty(s, property);
            }
        }
    }
    
    public synchronized boolean getBoolean(final String s, final boolean b) {
        return this.getString(s, String.valueOf(b)).equalsIgnoreCase("true");
    }
    
    public synchronized int getInt(final String s, final int n) {
        return Integer.parseInt(this.getString(s, String.valueOf(n)));
    }
    
    public synchronized String getString(final String s, final String s2) {
        return this.props.getProperty("awtdebug." + s, s2);
    }
    
    private synchronized List<String> getPropertyNames() {
        final LinkedList list = new LinkedList();
        final Iterator<String> iterator = this.props.stringPropertyNames().iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next().substring("awtdebug".length() + 1));
        }
        return list;
    }
    
    private void println(final Object o) {
        if (DebugSettings.log.isLoggable(PlatformLogger.Level.FINER)) {
            DebugSettings.log.finer(o.toString());
        }
    }
    
    private synchronized native void setCTracingOn(final boolean p0);
    
    private synchronized native void setCTracingOn(final boolean p0, final String p1);
    
    private synchronized native void setCTracingOn(final boolean p0, final String p1, final int p2);
    
    private void loadNativeSettings() {
        this.setCTracingOn(this.getBoolean("ctrace", false));
        final LinkedList list = new LinkedList();
        for (final String s : this.getPropertyNames()) {
            if (s.startsWith("ctrace") && s.length() > DebugSettings.PROP_CTRACE_LEN) {
                list.add(s);
            }
        }
        Collections.sort((List<Comparable>)list);
        for (final String s2 : list) {
            final String substring = s2.substring(DebugSettings.PROP_CTRACE_LEN + 1);
            final int index = substring.indexOf(64);
            final String s3 = (index != -1) ? substring.substring(0, index) : substring;
            final String s4 = (index != -1) ? substring.substring(index + 1) : "";
            final boolean boolean1 = this.getBoolean(s2, false);
            if (s4.length() == 0) {
                this.setCTracingOn(boolean1, s3);
            }
            else {
                this.setCTracingOn(boolean1, s3, Integer.parseInt(s4, 10));
            }
        }
    }
    
    static {
        log = PlatformLogger.getLogger("sun.awt.debug.DebugSettings");
        DEFAULT_PROPS = new String[] { "awtdebug.assert=true", "awtdebug.trace=false", "awtdebug.on=true", "awtdebug.ctrace=false" };
        DebugSettings.instance = null;
        PROP_CTRACE_LEN = "ctrace".length();
    }
}
