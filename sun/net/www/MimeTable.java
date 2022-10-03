package sun.net.www;

import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.StringTokenizer;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Properties;
import java.util.Enumeration;
import java.util.Hashtable;
import java.net.FileNameMap;

public class MimeTable implements FileNameMap
{
    private Hashtable<String, MimeEntry> entries;
    private Hashtable<String, MimeEntry> extensionMap;
    private static String tempFileTemplate;
    private static final String filePreamble = "sun.net.www MIME content-types table";
    private static final String fileMagic = "#sun.net.www MIME content-types table";
    protected static String[] mailcapLocations;
    
    MimeTable() {
        this.entries = new Hashtable<String, MimeEntry>();
        this.extensionMap = new Hashtable<String, MimeEntry>();
        this.load();
    }
    
    public static MimeTable getDefaultTable() {
        return DefaultInstanceHolder.defaultInstance;
    }
    
    public static FileNameMap loadTable() {
        return getDefaultTable();
    }
    
    public synchronized int getSize() {
        return this.entries.size();
    }
    
    @Override
    public synchronized String getContentTypeFor(final String s) {
        final MimeEntry byFileName = this.findByFileName(s);
        if (byFileName != null) {
            return byFileName.getType();
        }
        return null;
    }
    
    public synchronized void add(final MimeEntry mimeEntry) {
        this.entries.put(mimeEntry.getType(), mimeEntry);
        final String[] extensions = mimeEntry.getExtensions();
        if (extensions == null) {
            return;
        }
        for (int i = 0; i < extensions.length; ++i) {
            this.extensionMap.put(extensions[i], mimeEntry);
        }
    }
    
    public synchronized MimeEntry remove(final String s) {
        return this.remove(this.entries.get(s));
    }
    
    public synchronized MimeEntry remove(final MimeEntry mimeEntry) {
        final String[] extensions = mimeEntry.getExtensions();
        if (extensions != null) {
            for (int i = 0; i < extensions.length; ++i) {
                this.extensionMap.remove(extensions[i]);
            }
        }
        return this.entries.remove(mimeEntry.getType());
    }
    
    public synchronized MimeEntry find(final String s) {
        final MimeEntry mimeEntry = this.entries.get(s);
        if (mimeEntry == null) {
            final Enumeration<MimeEntry> elements = this.entries.elements();
            while (elements.hasMoreElements()) {
                final MimeEntry mimeEntry2 = elements.nextElement();
                if (mimeEntry2.matches(s)) {
                    return mimeEntry2;
                }
            }
        }
        return mimeEntry;
    }
    
    public MimeEntry findByFileName(String substring) {
        String lowerCase = "";
        final int lastIndex = substring.lastIndexOf(35);
        if (lastIndex > 0) {
            substring = substring.substring(0, lastIndex - 1);
        }
        final int max = Math.max(Math.max(substring.lastIndexOf(46), substring.lastIndexOf(47)), substring.lastIndexOf(63));
        if (max != -1 && substring.charAt(max) == '.') {
            lowerCase = substring.substring(max).toLowerCase();
        }
        return this.findByExt(lowerCase);
    }
    
    public synchronized MimeEntry findByExt(final String s) {
        return this.extensionMap.get(s);
    }
    
    public synchronized MimeEntry findByDescription(final String s) {
        final Enumeration<MimeEntry> elements = this.elements();
        while (elements.hasMoreElements()) {
            final MimeEntry mimeEntry = elements.nextElement();
            if (s.equals(mimeEntry.getDescription())) {
                return mimeEntry;
            }
        }
        return this.find(s);
    }
    
    String getTempFileTemplate() {
        return MimeTable.tempFileTemplate;
    }
    
    public synchronized Enumeration<MimeEntry> elements() {
        return this.entries.elements();
    }
    
    public synchronized void load() {
        final Properties properties = new Properties();
        File file = null;
        try {
            final String property = System.getProperty("content.types.user.table");
            if (property != null) {
                file = new File(property);
                if (!file.exists()) {
                    file = new File(System.getProperty("java.home") + File.separator + "lib" + File.separator + "content-types.properties");
                }
            }
            else {
                file = new File(System.getProperty("java.home") + File.separator + "lib" + File.separator + "content-types.properties");
            }
            final BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
            properties.load(bufferedInputStream);
            bufferedInputStream.close();
        }
        catch (final IOException ex) {
            System.err.println("Warning: default mime table not found: " + file.getPath());
            return;
        }
        this.parse(properties);
    }
    
    void parse(final Properties properties) {
        final String tempFileTemplate = ((Hashtable<K, String>)properties).get("temp.file.template");
        if (tempFileTemplate != null) {
            properties.remove("temp.file.template");
            MimeTable.tempFileTemplate = tempFileTemplate;
        }
        final Enumeration<?> propertyNames = properties.propertyNames();
        while (propertyNames.hasMoreElements()) {
            final String s = (String)propertyNames.nextElement();
            this.parse(s, properties.getProperty(s));
        }
    }
    
    void parse(final String s, final String s2) {
        final MimeEntry mimeEntry = new MimeEntry(s);
        final StringTokenizer stringTokenizer = new StringTokenizer(s2, ";");
        while (stringTokenizer.hasMoreTokens()) {
            this.parse(stringTokenizer.nextToken(), mimeEntry);
        }
        this.add(mimeEntry);
    }
    
    void parse(final String s, final MimeEntry mimeEntry) {
        String trim = null;
        String trim2 = null;
        int n = 0;
        final StringTokenizer stringTokenizer = new StringTokenizer(s, "=");
        while (stringTokenizer.hasMoreTokens()) {
            if (n != 0) {
                trim2 = stringTokenizer.nextToken().trim();
            }
            else {
                trim = stringTokenizer.nextToken().trim();
                n = 1;
            }
        }
        this.fill(mimeEntry, trim, trim2);
    }
    
    void fill(final MimeEntry mimeEntry, final String s, final String s2) {
        if ("description".equalsIgnoreCase(s)) {
            mimeEntry.setDescription(s2);
        }
        else if ("action".equalsIgnoreCase(s)) {
            mimeEntry.setAction(this.getActionCode(s2));
        }
        else if ("application".equalsIgnoreCase(s)) {
            mimeEntry.setCommand(s2);
        }
        else if ("icon".equalsIgnoreCase(s)) {
            mimeEntry.setImageFileName(s2);
        }
        else if ("file_extensions".equalsIgnoreCase(s)) {
            mimeEntry.setExtensions(s2);
        }
    }
    
    String[] getExtensions(final String s) {
        final StringTokenizer stringTokenizer = new StringTokenizer(s, ",");
        final int countTokens = stringTokenizer.countTokens();
        final String[] array = new String[countTokens];
        for (int i = 0; i < countTokens; ++i) {
            array[i] = stringTokenizer.nextToken();
        }
        return array;
    }
    
    int getActionCode(final String s) {
        for (int i = 0; i < MimeEntry.actionKeywords.length; ++i) {
            if (s.equalsIgnoreCase(MimeEntry.actionKeywords[i])) {
                return i;
            }
        }
        return 0;
    }
    
    public synchronized boolean save(String property) {
        if (property == null) {
            property = System.getProperty("user.home" + File.separator + "lib" + File.separator + "content-types.properties");
        }
        return this.saveAsProperties(new File(property));
    }
    
    public Properties getAsProperties() {
        final Properties properties = new Properties();
        final Enumeration<MimeEntry> elements = this.elements();
        while (elements.hasMoreElements()) {
            final MimeEntry mimeEntry = elements.nextElement();
            ((Hashtable<String, String>)properties).put(mimeEntry.getType(), mimeEntry.toProperty());
        }
        return properties;
    }
    
    protected boolean saveAsProperties(final File file) {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            final Properties asProperties = this.getAsProperties();
            ((Hashtable<String, String>)asProperties).put("temp.file.template", MimeTable.tempFileTemplate);
            final String property = System.getProperty("user.name");
            if (property != null) {
                asProperties.store(outputStream, "sun.net.www MIME content-types table" + ("; customized for " + property));
            }
            else {
                asProperties.store(outputStream, "sun.net.www MIME content-types table");
            }
        }
        catch (final IOException ex) {
            ex.printStackTrace();
            return false;
        }
        finally {
            if (outputStream != null) {
                try {
                    ((FileOutputStream)outputStream).close();
                }
                catch (final IOException ex2) {}
            }
        }
        return true;
    }
    
    static {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                MimeTable.tempFileTemplate = System.getProperty("content.types.temp.file.template", "/tmp/%s");
                MimeTable.mailcapLocations = new String[] { System.getProperty("user.mailcap"), System.getProperty("user.home") + "/.mailcap", "/etc/mailcap", "/usr/etc/mailcap", "/usr/local/etc/mailcap", System.getProperty("hotjava.home", "/usr/local/hotjava") + "/lib/mailcap" };
                return null;
            }
        });
    }
    
    private static class DefaultInstanceHolder
    {
        static final MimeTable defaultInstance;
        
        static MimeTable getDefaultInstance() {
            return AccessController.doPrivileged((PrivilegedAction<MimeTable>)new PrivilegedAction<MimeTable>() {
                @Override
                public MimeTable run() {
                    final MimeTable fileNameMap = new MimeTable();
                    URLConnection.setFileNameMap(fileNameMap);
                    return fileNameMap;
                }
            });
        }
        
        static {
            defaultInstance = getDefaultInstance();
        }
    }
}
