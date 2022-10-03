package org.apache.tomcat.util.scan;

import java.util.jar.Manifest;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.io.InputStream;
import org.apache.tomcat.util.compat.JreCompat;
import java.net.URISyntaxException;
import java.io.IOException;
import java.io.File;
import java.net.JarURLConnection;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.Enumeration;
import java.net.URL;
import java.util.jar.JarFile;
import org.apache.tomcat.Jar;

public class JarFileUrlJar implements Jar
{
    private final JarFile jarFile;
    private final URL jarFileURL;
    private final boolean multiRelease;
    private Enumeration<JarEntry> entries;
    private Set<String> entryNamesSeen;
    private JarEntry entry;
    
    public JarFileUrlJar(final URL url, final boolean startsWithJar) throws IOException {
        this.entry = null;
        if (startsWithJar) {
            final JarURLConnection jarConn = (JarURLConnection)url.openConnection();
            jarConn.setUseCaches(false);
            this.jarFile = jarConn.getJarFile();
            this.jarFileURL = jarConn.getJarFileURL();
        }
        else {
            File f;
            try {
                f = new File(url.toURI());
            }
            catch (final URISyntaxException e) {
                throw new IOException(e);
            }
            this.jarFile = JreCompat.getInstance().jarFileNewInstance(f);
            this.jarFileURL = url;
        }
        this.multiRelease = JreCompat.getInstance().jarFileIsMultiRelease(this.jarFile);
    }
    
    public URL getJarFileURL() {
        return this.jarFileURL;
    }
    
    @Deprecated
    public boolean entryExists(final String name) {
        return false;
    }
    
    public InputStream getInputStream(final String name) throws IOException {
        final ZipEntry entry = this.jarFile.getEntry(name);
        if (entry == null) {
            return null;
        }
        return this.jarFile.getInputStream(entry);
    }
    
    public long getLastModified(final String name) throws IOException {
        final ZipEntry entry = this.jarFile.getEntry(name);
        if (entry == null) {
            return -1L;
        }
        return entry.getTime();
    }
    
    public boolean exists(final String name) throws IOException {
        final ZipEntry entry = this.jarFile.getEntry(name);
        return entry != null;
    }
    
    public String getURL(final String entry) {
        final StringBuilder result = new StringBuilder("jar:");
        result.append(this.getJarFileURL().toExternalForm());
        result.append("!/");
        result.append(entry);
        return result.toString();
    }
    
    public void close() {
        if (this.jarFile != null) {
            try {
                this.jarFile.close();
            }
            catch (final IOException ex) {}
        }
    }
    
    public void nextEntry() {
        if (this.entries == null) {
            this.entries = this.jarFile.entries();
            if (this.multiRelease) {
                this.entryNamesSeen = new HashSet<String>();
            }
        }
        if (this.multiRelease) {
            String name = null;
            while (this.entries.hasMoreElements()) {
                this.entry = this.entries.nextElement();
                name = this.entry.getName();
                if (name.startsWith("META-INF/versions/")) {
                    final int i = name.indexOf(47, 18);
                    if (i == -1) {
                        continue;
                    }
                    name = name.substring(i + 1);
                }
                if (name.length() != 0) {
                    if (this.entryNamesSeen.contains(name)) {
                        continue;
                    }
                    this.entryNamesSeen.add(name);
                    this.entry = this.jarFile.getJarEntry(this.entry.getName());
                    return;
                }
            }
            this.entry = null;
            return;
        }
        if (this.entries.hasMoreElements()) {
            this.entry = this.entries.nextElement();
        }
        else {
            this.entry = null;
        }
    }
    
    public String getEntryName() {
        if (this.entry == null) {
            return null;
        }
        return this.entry.getName();
    }
    
    public InputStream getEntryInputStream() throws IOException {
        if (this.entry == null) {
            return null;
        }
        return this.jarFile.getInputStream(this.entry);
    }
    
    public Manifest getManifest() throws IOException {
        return this.jarFile.getManifest();
    }
    
    public void reset() throws IOException {
        this.entries = null;
        this.entryNamesSeen = null;
        this.entry = null;
    }
}
