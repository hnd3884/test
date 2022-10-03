package org.apache.tomcat.util.scan;

import java.util.Iterator;
import java.util.HashMap;
import org.apache.tomcat.util.compat.JreCompat;
import java.util.jar.Manifest;
import java.io.InputStream;
import java.io.IOException;
import java.util.Map;
import java.util.jar.JarEntry;
import java.net.URL;
import org.apache.tomcat.Jar;

public abstract class AbstractInputStreamJar implements Jar
{
    private final URL jarFileURL;
    private NonClosingJarInputStream jarInputStream;
    private JarEntry entry;
    private Boolean multiRelease;
    private Map<String, String> mrMap;
    
    public AbstractInputStreamJar(final URL jarFileUrl) {
        this.jarInputStream = null;
        this.entry = null;
        this.multiRelease = null;
        this.mrMap = null;
        this.jarFileURL = jarFileUrl;
    }
    
    public URL getJarFileURL() {
        return this.jarFileURL;
    }
    
    public void nextEntry() {
        if (this.jarInputStream == null) {
            try {
                this.reset();
            }
            catch (final IOException e) {
                this.entry = null;
                return;
            }
        }
        try {
            this.entry = this.jarInputStream.getNextJarEntry();
            if (this.multiRelease) {
                while (this.entry != null && (this.mrMap.keySet().contains(this.entry.getName()) || (this.entry.getName().startsWith("META-INF/versions/") && !this.mrMap.values().contains(this.entry.getName())))) {
                    this.entry = this.jarInputStream.getNextJarEntry();
                }
            }
            else {
                while (this.entry != null && this.entry.getName().startsWith("META-INF/versions/")) {
                    this.entry = this.jarInputStream.getNextJarEntry();
                }
            }
        }
        catch (final IOException ioe) {
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
        return this.jarInputStream;
    }
    
    @Deprecated
    public boolean entryExists(final String name) throws IOException {
        return false;
    }
    
    public InputStream getInputStream(final String name) throws IOException {
        this.gotoEntry(name);
        if (this.entry == null) {
            return null;
        }
        this.entry = null;
        return this.jarInputStream;
    }
    
    public long getLastModified(final String name) throws IOException {
        this.gotoEntry(name);
        if (this.entry == null) {
            return -1L;
        }
        return this.entry.getTime();
    }
    
    public boolean exists(final String name) throws IOException {
        this.gotoEntry(name);
        return this.entry != null;
    }
    
    public String getURL(final String entry) {
        final StringBuilder result = new StringBuilder("jar:");
        result.append(this.getJarFileURL().toExternalForm());
        result.append("!/");
        result.append(entry);
        return result.toString();
    }
    
    public Manifest getManifest() throws IOException {
        this.reset();
        return this.jarInputStream.getManifest();
    }
    
    public void reset() throws IOException {
        this.closeStream();
        this.entry = null;
        this.jarInputStream = this.createJarInputStream();
        if (this.multiRelease == null) {
            if (JreCompat.isJre9Available()) {
                final Manifest manifest = this.jarInputStream.getManifest();
                if (manifest == null) {
                    this.multiRelease = Boolean.FALSE;
                }
                else {
                    final String mrValue = manifest.getMainAttributes().getValue("Multi-Release");
                    if (mrValue == null) {
                        this.multiRelease = Boolean.FALSE;
                    }
                    else {
                        this.multiRelease = Boolean.valueOf(mrValue);
                    }
                }
            }
            else {
                this.multiRelease = Boolean.FALSE;
            }
            if (this.multiRelease && this.mrMap == null) {
                this.populateMrMap();
            }
        }
    }
    
    protected void closeStream() {
        if (this.jarInputStream != null) {
            try {
                this.jarInputStream.reallyClose();
            }
            catch (final IOException ex) {}
        }
    }
    
    protected abstract NonClosingJarInputStream createJarInputStream() throws IOException;
    
    private void gotoEntry(String name) throws IOException {
        boolean needsReset = true;
        if (this.multiRelease == null) {
            this.reset();
            needsReset = false;
        }
        if (this.multiRelease) {
            final String mrName = this.mrMap.get(name);
            if (mrName != null) {
                name = mrName;
            }
        }
        else if (name.startsWith("META-INF/versions/")) {
            this.entry = null;
            return;
        }
        if (this.entry != null && name.equals(this.entry.getName())) {
            return;
        }
        if (needsReset) {
            this.reset();
        }
        for (JarEntry jarEntry = this.jarInputStream.getNextJarEntry(); jarEntry != null; jarEntry = this.jarInputStream.getNextJarEntry()) {
            if (name.equals(jarEntry.getName())) {
                this.entry = jarEntry;
                break;
            }
        }
    }
    
    private void populateMrMap() throws IOException {
        final int targetVersion = JreCompat.getInstance().jarFileRuntimeMajorVersion();
        final Map<String, Integer> mrVersions = new HashMap<String, Integer>();
        for (JarEntry jarEntry = this.jarInputStream.getNextJarEntry(); jarEntry != null; jarEntry = this.jarInputStream.getNextJarEntry()) {
            final String name = jarEntry.getName();
            if (name.startsWith("META-INF/versions/") && name.endsWith(".class")) {
                final int i = name.indexOf(47, 18);
                if (i > 0) {
                    final String baseName = name.substring(i + 1);
                    final int version = Integer.parseInt(name.substring(18, i));
                    if (version <= targetVersion) {
                        final Integer mappedVersion = mrVersions.get(baseName);
                        if (mappedVersion == null) {
                            mrVersions.put(baseName, version);
                        }
                        else if (version > mappedVersion) {
                            mrVersions.put(baseName, version);
                        }
                    }
                }
            }
        }
        this.mrMap = new HashMap<String, String>();
        for (final Map.Entry<String, Integer> mrVersion : mrVersions.entrySet()) {
            this.mrMap.put(mrVersion.getKey(), "META-INF/versions/" + mrVersion.getValue().toString() + "/" + mrVersion.getKey());
        }
        this.closeStream();
        this.jarInputStream = this.createJarInputStream();
    }
}
