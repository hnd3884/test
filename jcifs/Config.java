package jcifs;

import java.util.Hashtable;
import java.io.UnsupportedEncodingException;
import java.io.FileInputStream;
import java.util.StringTokenizer;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.io.PrintStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import jcifs.smb.SmbExtendedAuthenticator;
import jcifs.util.LogStream;
import java.util.Properties;

public class Config
{
    private static Properties prp;
    private static LogStream log;
    public static String DEFAULT_OEM_ENCODING;
    private static SmbExtendedAuthenticator smbAuthenticator;
    
    public static void registerSmbURLHandler() {
        final String ver = System.getProperty("java.version");
        if (ver.startsWith("1.1.") || ver.startsWith("1.2.")) {
            throw new RuntimeException("jcifs-0.7.0b4+ requires Java 1.3 or above. You are running " + ver);
        }
        String pkgs = System.getProperty("java.protocol.handler.pkgs");
        if (pkgs == null) {
            System.setProperty("java.protocol.handler.pkgs", "jcifs");
        }
        else if (pkgs.indexOf("jcifs") == -1) {
            pkgs += "|jcifs";
            System.setProperty("java.protocol.handler.pkgs", pkgs);
        }
    }
    
    Config() {
    }
    
    public static void setProperties(final Properties prp) {
        Config.prp = new Properties(prp);
        try {
            Config.prp.putAll(System.getProperties());
        }
        catch (final SecurityException se) {
            final LogStream log = Config.log;
            if (LogStream.level > 1) {
                Config.log.println("SecurityException: jcifs will ignore System properties");
            }
        }
    }
    
    public static void load(final InputStream in) throws IOException {
        if (in != null) {
            Config.prp.load(in);
        }
        try {
            Config.prp.putAll(System.getProperties());
        }
        catch (final SecurityException se) {
            final LogStream log = Config.log;
            if (LogStream.level > 1) {
                Config.log.println("SecurityException: jcifs will ignore System properties");
            }
        }
    }
    
    public static void store(final OutputStream out, final String header) throws IOException {
        Config.prp.store(out, header);
    }
    
    public static void list(final PrintStream out) throws IOException {
        Config.prp.list(out);
    }
    
    public static Object setProperty(final String key, final String value) {
        return Config.prp.setProperty(key, value);
    }
    
    public static Object get(final String key) {
        return ((Hashtable<K, Object>)Config.prp).get(key);
    }
    
    public static String getProperty(final String key, final String def) {
        return Config.prp.getProperty(key, def);
    }
    
    public static String getProperty(final String key) {
        return Config.prp.getProperty(key);
    }
    
    public static int getInt(final String key, int def) {
        final String s = Config.prp.getProperty(key);
        if (s != null) {
            try {
                def = Integer.parseInt(s);
            }
            catch (final NumberFormatException nfe) {
                final LogStream log = Config.log;
                if (LogStream.level > 0) {
                    nfe.printStackTrace(Config.log);
                }
            }
        }
        return def;
    }
    
    public static int getInt(final String key) {
        final String s = Config.prp.getProperty(key);
        int result = -1;
        if (s != null) {
            try {
                result = Integer.parseInt(s);
            }
            catch (final NumberFormatException nfe) {
                final LogStream log = Config.log;
                if (LogStream.level > 0) {
                    nfe.printStackTrace(Config.log);
                }
            }
        }
        return result;
    }
    
    public static long getLong(final String key, long def) {
        final String s = Config.prp.getProperty(key);
        if (s != null) {
            try {
                def = Long.parseLong(s);
            }
            catch (final NumberFormatException nfe) {
                final LogStream log = Config.log;
                if (LogStream.level > 0) {
                    nfe.printStackTrace(Config.log);
                }
            }
        }
        return def;
    }
    
    public static InetAddress getInetAddress(final String key, InetAddress def) {
        final String addr = Config.prp.getProperty(key);
        if (addr != null) {
            try {
                def = InetAddress.getByName(addr);
            }
            catch (final UnknownHostException uhe) {
                final LogStream log = Config.log;
                if (LogStream.level > 0) {
                    Config.log.println(addr);
                    uhe.printStackTrace(Config.log);
                }
            }
        }
        return def;
    }
    
    public static InetAddress getLocalHost() {
        final String addr = Config.prp.getProperty("jcifs.smb.client.laddr");
        if (addr != null) {
            try {
                return InetAddress.getByName(addr);
            }
            catch (final UnknownHostException uhe) {
                final LogStream log = Config.log;
                if (LogStream.level > 0) {
                    Config.log.println("Ignoring jcifs.smb.client.laddr address: " + addr);
                    uhe.printStackTrace(Config.log);
                }
            }
        }
        return null;
    }
    
    public static boolean getBoolean(final String key, boolean def) {
        final String b = getProperty(key);
        if (b != null) {
            def = b.toLowerCase().equals("true");
        }
        return def;
    }
    
    public static InetAddress[] getInetAddressArray(final String key, final String delim, final InetAddress[] def) {
        final String p = getProperty(key);
        if (p != null) {
            final StringTokenizer tok = new StringTokenizer(p, delim);
            final int len = tok.countTokens();
            final InetAddress[] arr = new InetAddress[len];
            for (int i = 0; i < len; ++i) {
                final String addr = tok.nextToken();
                try {
                    arr[i] = InetAddress.getByName(addr);
                }
                catch (final UnknownHostException uhe) {
                    final LogStream log = Config.log;
                    if (LogStream.level > 0) {
                        Config.log.println(addr);
                        uhe.printStackTrace(Config.log);
                    }
                    return def;
                }
            }
            return arr;
        }
        return def;
    }
    
    public static SmbExtendedAuthenticator getOpneConnectionAuthenticator() {
        return Config.smbAuthenticator;
    }
    
    public static void setOpneConnectionAuthenticator(final SmbExtendedAuthenticator smbAuthenticator) {
        Config.smbAuthenticator = smbAuthenticator;
    }
    
    static {
        Config.prp = new Properties();
        Config.DEFAULT_OEM_ENCODING = "Cp850";
        FileInputStream in = null;
        Config.log = LogStream.getInstance();
        try {
            final String filename = System.getProperty("jcifs.properties");
            if (filename != null && filename.length() > 1) {
                in = new FileInputStream(filename);
            }
            load(in);
        }
        catch (final IOException ioe) {
            final LogStream log = Config.log;
            if (LogStream.level > 0) {
                ioe.printStackTrace(Config.log);
            }
        }
        final int level;
        if ((level = getInt("jcifs.util.loglevel", -1)) != -1) {
            LogStream.setLevel(level);
        }
        try {
            "".getBytes(Config.DEFAULT_OEM_ENCODING);
        }
        catch (final UnsupportedEncodingException uee) {
            final LogStream log2 = Config.log;
            if (LogStream.level >= 2) {
                Config.log.println("WARNING: The default OEM encoding " + Config.DEFAULT_OEM_ENCODING + " does not appear to be supported by this JRE. The default encoding will be US-ASCII.");
            }
            Config.DEFAULT_OEM_ENCODING = "US-ASCII";
        }
        final LogStream log3 = Config.log;
        if (LogStream.level >= 4) {
            try {
                Config.prp.store(Config.log, "JCIFS PROPERTIES");
            }
            catch (final IOException ex) {}
        }
        Config.smbAuthenticator = null;
    }
}
