package com.sun.org.apache.bcel.internal.util;

import java.util.zip.ZipEntry;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.zip.ZipFile;
import java.io.File;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.io.Serializable;

public class ClassPath implements Serializable
{
    public static final ClassPath SYSTEM_CLASS_PATH;
    private PathEntry[] paths;
    private String class_path;
    
    public ClassPath(final String class_path) {
        this.class_path = class_path;
        final ArrayList vec = new ArrayList();
        final StringTokenizer tok = new StringTokenizer(class_path, SecuritySupport.getSystemProperty("path.separator"));
        while (tok.hasMoreTokens()) {
            final String path = tok.nextToken();
            if (!path.equals("")) {
                final File file = new File(path);
                try {
                    if (!SecuritySupport.getFileExists(file)) {
                        continue;
                    }
                    if (file.isDirectory()) {
                        vec.add(new Dir(path));
                    }
                    else {
                        vec.add(new Zip(new ZipFile(file)));
                    }
                }
                catch (final IOException e) {
                    System.err.println("CLASSPATH component " + file + ": " + e);
                }
            }
        }
        vec.toArray(this.paths = new PathEntry[vec.size()]);
    }
    
    @Deprecated
    public ClassPath() {
        this("");
    }
    
    @Override
    public String toString() {
        return this.class_path;
    }
    
    @Override
    public int hashCode() {
        return this.class_path.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof ClassPath && this.class_path.equals(((ClassPath)o).class_path);
    }
    
    private static final void getPathComponents(final String path, final ArrayList list) {
        if (path != null) {
            final StringTokenizer tok = new StringTokenizer(path, File.pathSeparator);
            while (tok.hasMoreTokens()) {
                final String name = tok.nextToken();
                final File file = new File(name);
                if (SecuritySupport.getFileExists(file)) {
                    list.add(name);
                }
            }
        }
    }
    
    public static final String getClassPath() {
        String class_path;
        String boot_path;
        String ext_path;
        try {
            class_path = SecuritySupport.getSystemProperty("java.class.path");
            boot_path = SecuritySupport.getSystemProperty("sun.boot.class.path");
            ext_path = SecuritySupport.getSystemProperty("java.ext.dirs");
        }
        catch (final SecurityException e) {
            return "";
        }
        final ArrayList list = new ArrayList();
        getPathComponents(class_path, list);
        getPathComponents(boot_path, list);
        final ArrayList dirs = new ArrayList();
        getPathComponents(ext_path, dirs);
        final Iterator e2 = dirs.iterator();
        while (e2.hasNext()) {
            final File ext_dir = new File(e2.next());
            final String[] extensions = SecuritySupport.getFileList(ext_dir, new FilenameFilter() {
                @Override
                public boolean accept(final File dir, String name) {
                    name = name.toLowerCase();
                    return name.endsWith(".zip") || name.endsWith(".jar");
                }
            });
            if (extensions != null) {
                for (int i = 0; i < extensions.length; ++i) {
                    list.add(ext_path + File.separatorChar + extensions[i]);
                }
            }
        }
        final StringBuffer buf = new StringBuffer();
        final Iterator e3 = list.iterator();
        while (e3.hasNext()) {
            buf.append(e3.next());
            if (e3.hasNext()) {
                buf.append(File.pathSeparatorChar);
            }
        }
        return buf.toString().intern();
    }
    
    public InputStream getInputStream(final String name) throws IOException {
        return this.getInputStream(name, ".class");
    }
    
    public InputStream getInputStream(final String name, final String suffix) throws IOException {
        InputStream is = null;
        try {
            is = this.getClass().getClassLoader().getResourceAsStream(name + suffix);
        }
        catch (final Exception ex) {}
        if (is != null) {
            return is;
        }
        return this.getClassFile(name, suffix).getInputStream();
    }
    
    public ClassFile getClassFile(final String name, final String suffix) throws IOException {
        for (int i = 0; i < this.paths.length; ++i) {
            final ClassFile cf;
            if ((cf = this.paths[i].getClassFile(name, suffix)) != null) {
                return cf;
            }
        }
        throw new IOException("Couldn't find: " + name + suffix);
    }
    
    public ClassFile getClassFile(final String name) throws IOException {
        return this.getClassFile(name, ".class");
    }
    
    public byte[] getBytes(final String name, final String suffix) throws IOException {
        final InputStream is = this.getInputStream(name, suffix);
        if (is == null) {
            throw new IOException("Couldn't find: " + name + suffix);
        }
        final DataInputStream dis = new DataInputStream(is);
        final byte[] bytes = new byte[is.available()];
        dis.readFully(bytes);
        dis.close();
        is.close();
        return bytes;
    }
    
    public byte[] getBytes(final String name) throws IOException {
        return this.getBytes(name, ".class");
    }
    
    public String getPath(String name) throws IOException {
        final int index = name.lastIndexOf(46);
        String suffix = "";
        if (index > 0) {
            suffix = name.substring(index);
            name = name.substring(0, index);
        }
        return this.getPath(name, suffix);
    }
    
    public String getPath(final String name, final String suffix) throws IOException {
        return this.getClassFile(name, suffix).getPath();
    }
    
    static {
        SYSTEM_CLASS_PATH = new ClassPath();
    }
    
    private abstract static class PathEntry implements Serializable
    {
        abstract ClassFile getClassFile(final String p0, final String p1) throws IOException;
    }
    
    private static class Dir extends PathEntry
    {
        private String dir;
        
        Dir(final String d) {
            this.dir = d;
        }
        
        @Override
        ClassFile getClassFile(final String name, final String suffix) throws IOException {
            final File file = new File(this.dir + File.separatorChar + name.replace('.', File.separatorChar) + suffix);
            return SecuritySupport.getFileExists(file) ? new ClassFile() {
                @Override
                public InputStream getInputStream() throws IOException {
                    return new FileInputStream(file);
                }
                
                @Override
                public String getPath() {
                    try {
                        return file.getCanonicalPath();
                    }
                    catch (final IOException e) {
                        return null;
                    }
                }
                
                @Override
                public long getTime() {
                    return file.lastModified();
                }
                
                @Override
                public long getSize() {
                    return file.length();
                }
                
                @Override
                public String getBase() {
                    return Dir.this.dir;
                }
            } : null;
        }
        
        @Override
        public String toString() {
            return this.dir;
        }
    }
    
    private static class Zip extends PathEntry
    {
        private ZipFile zip;
        
        Zip(final ZipFile z) {
            this.zip = z;
        }
        
        @Override
        ClassFile getClassFile(final String name, final String suffix) throws IOException {
            final ZipEntry entry = this.zip.getEntry(name.replace('.', '/') + suffix);
            return (entry != null) ? new ClassFile() {
                @Override
                public InputStream getInputStream() throws IOException {
                    return Zip.this.zip.getInputStream(entry);
                }
                
                @Override
                public String getPath() {
                    return entry.toString();
                }
                
                @Override
                public long getTime() {
                    return entry.getTime();
                }
                
                @Override
                public long getSize() {
                    return entry.getSize();
                }
                
                @Override
                public String getBase() {
                    return Zip.this.zip.getName();
                }
            } : null;
        }
    }
    
    public interface ClassFile
    {
        InputStream getInputStream() throws IOException;
        
        String getPath();
        
        String getBase();
        
        long getTime();
        
        long getSize();
    }
}
