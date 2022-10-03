package sun.applet;

import java.util.Hashtable;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import sun.net.www.ParseUtil;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;
import java.io.File;

public class Main
{
    static File theUserPropertiesFile;
    static final String[][] avDefaultUserProps;
    private static AppletMessageHandler amh;
    private boolean debugFlag;
    private boolean helpFlag;
    private String encoding;
    private boolean noSecurityFlag;
    private static boolean cmdLineTestFlag;
    private static Vector urlList;
    public static final String theVersion;
    
    public Main() {
        this.debugFlag = false;
        this.helpFlag = false;
        this.encoding = null;
        this.noSecurityFlag = false;
    }
    
    public static void main(final String[] array) {
        final int run = new Main().run(array);
        if (run != 0 || Main.cmdLineTestFlag) {
            System.exit(run);
        }
    }
    
    private int run(final String[] array) {
        try {
            if (array.length == 0) {
                usage();
                return 0;
            }
            int decodeArg;
            for (int i = 0; i < array.length; i += decodeArg) {
                decodeArg = this.decodeArg(array, i);
                if (decodeArg == 0) {
                    throw new ParseException(lookup("main.err.unrecognizedarg", array[i]));
                }
            }
        }
        catch (final ParseException ex) {
            System.err.println(ex.getMessage());
            return 1;
        }
        if (this.helpFlag) {
            usage();
            return 0;
        }
        if (Main.urlList.size() == 0) {
            System.err.println(lookup("main.err.inputfile"));
            return 1;
        }
        if (this.debugFlag) {
            return this.invokeDebugger(array);
        }
        if (!this.noSecurityFlag && System.getSecurityManager() == null) {
            this.init();
        }
        for (int j = 0; j < Main.urlList.size(); ++j) {
            try {
                AppletViewer.parse((URL)Main.urlList.elementAt(j), this.encoding);
            }
            catch (final IOException ex2) {
                System.err.println(lookup("main.err.io", ex2.getMessage()));
                return 1;
            }
        }
        return 0;
    }
    
    private static void usage() {
        System.out.println(lookup("usage"));
    }
    
    private int decodeArg(final String[] array, int n) throws ParseException {
        final String s = array[n];
        final int length = array.length;
        if ("-help".equalsIgnoreCase(s) || "-?".equals(s)) {
            this.helpFlag = true;
            return 1;
        }
        if ("-encoding".equals(s) && n < length - 1) {
            if (this.encoding != null) {
                throw new ParseException(lookup("main.err.dupoption", s));
            }
            this.encoding = array[++n];
            return 2;
        }
        else {
            if ("-debug".equals(s)) {
                this.debugFlag = true;
                return 1;
            }
            if ("-Xnosecurity".equals(s)) {
                System.err.println();
                System.err.println(lookup("main.warn.nosecmgr"));
                System.err.println();
                this.noSecurityFlag = true;
                return 1;
            }
            if ("-XcmdLineTest".equals(s)) {
                Main.cmdLineTestFlag = true;
                return 1;
            }
            if (s.startsWith("-")) {
                throw new ParseException(lookup("main.err.unsupportedopt", s));
            }
            final URL url = this.parseURL(s);
            if (url != null) {
                Main.urlList.addElement(url);
                return 1;
            }
            return 0;
        }
    }
    
    private URL parseURL(final String s) throws ParseException {
        final String s2 = "file:";
        URL fileToEncodedURL;
        try {
            if (s.indexOf(58) <= 1) {
                fileToEncodedURL = ParseUtil.fileToEncodedURL(new File(s));
            }
            else if (s.startsWith(s2) && s.length() != s2.length() && !new File(s.substring(s2.length())).isAbsolute()) {
                fileToEncodedURL = new URL("file", "", ParseUtil.fileToEncodedURL(new File(System.getProperty("user.dir"))).getPath() + s.substring(s2.length()));
            }
            else {
                fileToEncodedURL = new URL(s);
            }
        }
        catch (final MalformedURLException ex) {
            throw new ParseException(lookup("main.err.badurl", s, ex.getMessage()));
        }
        return fileToEncodedURL;
    }
    
    private int invokeDebugger(final String[] array) {
        final String[] array2 = new String[array.length + 1];
        int n = 0;
        array2[n++] = "-Djava.class.path=" + (System.getProperty("java.home") + File.separator + "phony");
        array2[n++] = "sun.applet.Main";
        for (int i = 0; i < array.length; ++i) {
            if (!"-debug".equals(array[i])) {
                array2[n++] = array[i];
            }
        }
        try {
            Class.forName("com.sun.tools.example.debug.tty.TTY", true, ClassLoader.getSystemClassLoader()).getDeclaredMethod("main", String[].class).invoke(null, array2);
        }
        catch (final ClassNotFoundException ex) {
            System.err.println(lookup("main.debug.cantfinddebug"));
            return 1;
        }
        catch (final NoSuchMethodException ex2) {
            System.err.println(lookup("main.debug.cantfindmain"));
            return 1;
        }
        catch (final InvocationTargetException ex3) {
            System.err.println(lookup("main.debug.exceptionindebug"));
            return 1;
        }
        catch (final IllegalAccessException ex4) {
            System.err.println(lookup("main.debug.cantaccess"));
            return 1;
        }
        return 0;
    }
    
    private void init() {
        final Properties avProps = this.getAVProps();
        ((Hashtable<String, String>)avProps).put("browser", "sun.applet.AppletViewer");
        ((Hashtable<String, String>)avProps).put("browser.version", "1.06");
        ((Hashtable<String, String>)avProps).put("browser.vendor", "Oracle Corporation");
        ((Hashtable<String, String>)avProps).put("http.agent", "Java(tm) 2 SDK, Standard Edition v" + Main.theVersion);
        ((Hashtable<String, String>)avProps).put("package.restrict.definition.java", "true");
        ((Hashtable<String, String>)avProps).put("package.restrict.definition.sun", "true");
        ((Hashtable<String, String>)avProps).put("java.version.applet", "true");
        ((Hashtable<String, String>)avProps).put("java.vendor.applet", "true");
        ((Hashtable<String, String>)avProps).put("java.vendor.url.applet", "true");
        ((Hashtable<String, String>)avProps).put("java.class.version.applet", "true");
        ((Hashtable<String, String>)avProps).put("os.name.applet", "true");
        ((Hashtable<String, String>)avProps).put("os.version.applet", "true");
        ((Hashtable<String, String>)avProps).put("os.arch.applet", "true");
        ((Hashtable<String, String>)avProps).put("file.separator.applet", "true");
        ((Hashtable<String, String>)avProps).put("path.separator.applet", "true");
        ((Hashtable<String, String>)avProps).put("line.separator.applet", "true");
        final Properties properties = System.getProperties();
        final Enumeration<?> propertyNames = properties.propertyNames();
        while (propertyNames.hasMoreElements()) {
            final String s = (String)propertyNames.nextElement();
            final String property = properties.getProperty(s);
            final String s2;
            if ((s2 = (String)avProps.setProperty(s, property)) != null) {
                System.err.println(lookup("main.warn.prop.overwrite", s, s2, property));
            }
        }
        System.setProperties(avProps);
        if (!this.noSecurityFlag) {
            System.setSecurityManager(new AppletSecurity());
        }
        else {
            System.err.println(lookup("main.nosecmgr"));
        }
    }
    
    private Properties getAVProps() {
        final Properties properties = new Properties();
        final File theUserPropertiesFile = Main.theUserPropertiesFile;
        Properties properties2;
        if (theUserPropertiesFile.exists()) {
            if (theUserPropertiesFile.canRead()) {
                properties2 = this.getAVProps(theUserPropertiesFile);
            }
            else {
                System.err.println(lookup("main.warn.cantreadprops", theUserPropertiesFile.toString()));
                properties2 = this.setDefaultAVProps();
            }
        }
        else {
            final File file = new File(new File(new File(System.getProperty("user.home")), ".hotjava"), "properties");
            if (file.exists()) {
                properties2 = this.getAVProps(file);
            }
            else {
                System.err.println(lookup("main.warn.cantreadprops", file.toString()));
                properties2 = this.setDefaultAVProps();
            }
            try (final FileOutputStream fileOutputStream = new FileOutputStream(theUserPropertiesFile)) {
                properties2.store(fileOutputStream, lookup("main.prop.store"));
            }
            catch (final IOException ex) {
                System.err.println(lookup("main.err.prop.cantsave", theUserPropertiesFile.toString()));
            }
        }
        return properties2;
    }
    
    private Properties setDefaultAVProps() {
        final Properties properties = new Properties();
        for (int i = 0; i < Main.avDefaultUserProps.length; ++i) {
            properties.setProperty(Main.avDefaultUserProps[i][0], Main.avDefaultUserProps[i][1]);
        }
        return properties;
    }
    
    private Properties getAVProps(final File file) {
        final Properties properties = new Properties();
        final Properties properties2 = new Properties();
        try (final FileInputStream fileInputStream = new FileInputStream(file)) {
            properties2.load(new BufferedInputStream(fileInputStream));
        }
        catch (final IOException ex) {
            System.err.println(lookup("main.err.prop.cantread", file.toString()));
        }
        for (int i = 0; i < Main.avDefaultUserProps.length; ++i) {
            final String property = properties2.getProperty(Main.avDefaultUserProps[i][0]);
            if (property != null) {
                properties.setProperty(Main.avDefaultUserProps[i][0], property);
            }
            else {
                properties.setProperty(Main.avDefaultUserProps[i][0], Main.avDefaultUserProps[i][1]);
            }
        }
        return properties;
    }
    
    private static String lookup(final String s) {
        return Main.amh.getMessage(s);
    }
    
    private static String lookup(final String s, final String s2) {
        return Main.amh.getMessage(s, s2);
    }
    
    private static String lookup(final String s, final String s2, final String s3) {
        return Main.amh.getMessage(s, s2, s3);
    }
    
    private static String lookup(final String s, final String s2, final String s3, final String s4) {
        return Main.amh.getMessage(s, s2, s3, s4);
    }
    
    static {
        avDefaultUserProps = new String[][] { { "http.proxyHost", "" }, { "http.proxyPort", "80" }, { "package.restrict.access.sun", "true" } };
        final File file = new File(System.getProperty("user.home"));
        file.canWrite();
        Main.theUserPropertiesFile = new File(file, ".appletviewer");
        Main.amh = new AppletMessageHandler("appletviewer");
        Main.cmdLineTestFlag = false;
        Main.urlList = new Vector(1);
        theVersion = System.getProperty("java.version");
    }
    
    class ParseException extends RuntimeException
    {
        Throwable t;
        
        public ParseException(final String s) {
            super(s);
            this.t = null;
        }
        
        public ParseException(final Throwable t) {
            super(t.getMessage());
            this.t = null;
            this.t = t;
        }
    }
}
