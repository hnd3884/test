package sun.net;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Properties;

public class NetProperties
{
    private static Properties props;
    
    private NetProperties() {
    }
    
    private static void loadDefaultProperties() {
        final String property = System.getProperty("java.home");
        if (property == null) {
            throw new Error("Can't find java.home ??");
        }
        try {
            final BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(new File(new File(property, "lib"), "net.properties").getCanonicalPath()));
            NetProperties.props.load(bufferedInputStream);
            bufferedInputStream.close();
        }
        catch (final Exception ex) {}
    }
    
    public static String get(final String s) {
        final String property = NetProperties.props.getProperty(s);
        try {
            return System.getProperty(s, property);
        }
        catch (final IllegalArgumentException ex) {}
        catch (final NullPointerException ex2) {}
        return null;
    }
    
    public static Integer getInteger(final String s, final int n) {
        String property = null;
        try {
            property = System.getProperty(s, NetProperties.props.getProperty(s));
        }
        catch (final IllegalArgumentException ex) {}
        catch (final NullPointerException ex2) {}
        if (property != null) {
            try {
                return Integer.decode(property);
            }
            catch (final NumberFormatException ex3) {}
        }
        return new Integer(n);
    }
    
    public static Boolean getBoolean(final String s) {
        String property = null;
        try {
            property = System.getProperty(s, NetProperties.props.getProperty(s));
        }
        catch (final IllegalArgumentException ex) {}
        catch (final NullPointerException ex2) {}
        if (property != null) {
            try {
                return Boolean.valueOf(property);
            }
            catch (final NumberFormatException ex3) {}
        }
        return null;
    }
    
    static {
        NetProperties.props = new Properties();
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                loadDefaultProperties();
                return null;
            }
        });
    }
}
