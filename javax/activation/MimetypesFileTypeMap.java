package javax.activation;

import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.util.Vector;
import com.sun.activation.registries.MimeTypeFile;

public class MimetypesFileTypeMap extends FileTypeMap
{
    private static MimeTypeFile defDB;
    private MimeTypeFile[] DB;
    private static final int PROG = 0;
    private static String defaultType;
    private static boolean debug;
    static /* synthetic */ Class class$javax$activation$MimetypesFileTypeMap;
    
    static {
        MimetypesFileTypeMap.defDB = null;
        MimetypesFileTypeMap.defaultType = "application/octet-stream";
        MimetypesFileTypeMap.debug = false;
        try {
            MimetypesFileTypeMap.debug = Boolean.getBoolean("javax.activation.debug");
        }
        catch (final Throwable t) {}
    }
    
    public MimetypesFileTypeMap() {
        final Vector vector = new Vector(5);
        vector.addElement(null);
        if (MimetypesFileTypeMap.debug) {
            System.out.println("MimetypesFileTypeMap: load HOME");
        }
        try {
            final String property = System.getProperty("user.home");
            if (property != null) {
                final MimeTypeFile loadFile = this.loadFile(String.valueOf(property) + File.separator + ".mime.types");
                if (loadFile != null) {
                    vector.addElement(loadFile);
                }
            }
        }
        catch (final SecurityException ex) {}
        if (MimetypesFileTypeMap.debug) {
            System.out.println("MimetypesFileTypeMap: load SYS");
        }
        try {
            final MimeTypeFile loadFile2 = this.loadFile(String.valueOf(System.getProperty("java.home")) + File.separator + "lib" + File.separator + "mime.types");
            if (loadFile2 != null) {
                vector.addElement(loadFile2);
            }
        }
        catch (final SecurityException ex2) {}
        if (MimetypesFileTypeMap.debug) {
            System.out.println("MimetypesFileTypeMap: load JAR");
        }
        this.loadAllResources(vector, "META-INF/mime.types");
        if (MimetypesFileTypeMap.debug) {
            System.out.println("MimetypesFileTypeMap: load DEF");
        }
        final Class clazz = (MimetypesFileTypeMap.class$javax$activation$MimetypesFileTypeMap != null) ? MimetypesFileTypeMap.class$javax$activation$MimetypesFileTypeMap : (MimetypesFileTypeMap.class$javax$activation$MimetypesFileTypeMap = class$("javax.activation.MimetypesFileTypeMap"));
        synchronized (clazz) {
            if (MimetypesFileTypeMap.defDB == null) {
                MimetypesFileTypeMap.defDB = this.loadResource("/META-INF/mimetypes.default");
            }
        }
        if (MimetypesFileTypeMap.defDB != null) {
            vector.addElement(MimetypesFileTypeMap.defDB);
        }
        vector.copyInto(this.DB = new MimeTypeFile[vector.size()]);
    }
    
    public MimetypesFileTypeMap(final InputStream inputStream) {
        this();
        try {
            this.DB[0] = new MimeTypeFile(inputStream);
        }
        catch (final IOException ex) {}
    }
    
    public MimetypesFileTypeMap(final String s) throws IOException {
        this();
        this.DB[0] = new MimeTypeFile(s);
    }
    
    public synchronized void addMimeTypes(final String s) {
        if (this.DB[0] == null) {
            this.DB[0] = new MimeTypeFile();
        }
        this.DB[0].appendToRegistry(s);
    }
    
    static /* synthetic */ Class class$(final String s) {
        try {
            return Class.forName(s);
        }
        catch (final ClassNotFoundException ex) {
            throw new NoClassDefFoundError(ex.getMessage());
        }
    }
    
    public String getContentType(final File file) {
        return this.getContentType(file.getName());
    }
    
    public synchronized String getContentType(final String s) {
        final int lastIndex = s.lastIndexOf(".");
        if (lastIndex < 0) {
            return MimetypesFileTypeMap.defaultType;
        }
        final String substring = s.substring(lastIndex + 1);
        if (substring.length() == 0) {
            return MimetypesFileTypeMap.defaultType;
        }
        for (int i = 0; i < this.DB.length; ++i) {
            if (this.DB[i] != null) {
                final String mimeTypeString = this.DB[i].getMIMETypeString(substring);
                if (mimeTypeString != null) {
                    return mimeTypeString;
                }
            }
        }
        return MimetypesFileTypeMap.defaultType;
    }
    
    private void loadAllResources(final Vector vector, final String s) {
        boolean b = false;
        try {
            ClassLoader classLoader = SecuritySupport.getInstance().getContextClassLoader();
            if (classLoader == null) {
                classLoader = this.getClass().getClassLoader();
            }
            URL[] array;
            if (classLoader != null) {
                array = SecuritySupport.getInstance().getResources(classLoader, s);
            }
            else {
                array = SecuritySupport.getInstance().getSystemResources(s);
            }
            if (array != null) {
                if (MimetypesFileTypeMap.debug) {
                    pr("MimetypesFileTypeMap: getResources");
                }
                for (int i = 0; i < array.length; ++i) {
                    final URL url = array[i];
                    InputStream openStream = null;
                    Label_0113: {
                        if (!MimetypesFileTypeMap.debug) {
                            break Label_0113;
                        }
                        pr("MimetypesFileTypeMap: URL " + url);
                        try {
                            openStream = SecuritySupport.getInstance().openStream(url);
                            if (openStream != null) {
                                vector.addElement(new MimeTypeFile(openStream));
                                b = true;
                                if (MimetypesFileTypeMap.debug) {
                                    pr("MimetypesFileTypeMap: successfully loaded mime types from URL: " + url);
                                }
                            }
                            else if (MimetypesFileTypeMap.debug) {
                                pr("MimetypesFileTypeMap: not loading mime types from URL: " + url);
                            }
                        }
                        catch (final IOException ex) {
                            if (MimetypesFileTypeMap.debug) {
                                pr("MimetypesFileTypeMap: " + ex);
                            }
                        }
                        catch (final SecurityException ex2) {
                            if (MimetypesFileTypeMap.debug) {
                                pr("MimetypesFileTypeMap: " + ex2);
                            }
                        }
                        finally {
                            try {
                                if (openStream != null) {
                                    openStream.close();
                                }
                            }
                            catch (final IOException ex3) {}
                        }
                    }
                }
            }
        }
        catch (final Exception ex4) {
            if (MimetypesFileTypeMap.debug) {
                pr("MimetypesFileTypeMap: " + ex4);
            }
        }
        if (!b) {
            if (MimetypesFileTypeMap.debug) {
                pr("MimetypesFileTypeMap: !anyLoaded");
            }
            final MimeTypeFile loadResource = this.loadResource("/" + s);
            if (loadResource != null) {
                vector.addElement(loadResource);
            }
        }
    }
    
    private MimeTypeFile loadFile(final String s) {
        MimeTypeFile mimeTypeFile = null;
        try {
            mimeTypeFile = new MimeTypeFile(s);
        }
        catch (final IOException ex) {}
        return mimeTypeFile;
    }
    
    private MimeTypeFile loadResource(final String s) {
        InputStream resourceAsStream = null;
        try {
            resourceAsStream = SecuritySupport.getInstance().getResourceAsStream(this.getClass(), s);
            if (resourceAsStream != null) {
                final MimeTypeFile mimeTypeFile = new MimeTypeFile(resourceAsStream);
                if (MimetypesFileTypeMap.debug) {
                    pr("MimetypesFileTypeMap: successfully loaded mime types file: " + s);
                }
                return mimeTypeFile;
            }
            if (MimetypesFileTypeMap.debug) {
                pr("MimetypesFileTypeMap: not loading mime types file: " + s);
            }
        }
        catch (final IOException ex) {
            if (MimetypesFileTypeMap.debug) {
                pr("MimetypesFileTypeMap: " + ex);
                ex.printStackTrace();
            }
        }
        catch (final SecurityException ex2) {
            if (MimetypesFileTypeMap.debug) {
                pr("MimetypesFileTypeMap: " + ex2);
            }
        }
        finally {
            try {
                if (resourceAsStream != null) {
                    resourceAsStream.close();
                }
            }
            catch (final IOException ex3) {}
        }
        return null;
    }
    
    private static final void pr(final String s) {
        System.out.println(s);
    }
}
