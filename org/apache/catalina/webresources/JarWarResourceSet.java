package org.apache.catalina.webresources;

import java.net.MalformedURLException;
import org.apache.tomcat.util.buf.UriUtil;
import java.io.File;
import java.util.jar.JarInputStream;
import java.util.Iterator;
import java.util.Map;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.io.IOException;
import org.apache.tomcat.util.compat.JreCompat;
import java.util.zip.ZipEntry;
import java.util.HashMap;
import org.apache.catalina.WebResource;
import java.util.jar.Manifest;
import java.util.jar.JarEntry;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;

public class JarWarResourceSet extends AbstractArchiveResourceSet
{
    private final String archivePath;
    
    public JarWarResourceSet(final WebResourceRoot root, final String webAppMount, final String base, final String archivePath, final String internalPath) throws IllegalArgumentException {
        this.setRoot(root);
        this.setWebAppMount(webAppMount);
        this.setBase(base);
        this.archivePath = archivePath;
        this.setInternalPath(internalPath);
        if (this.getRoot().getState().isAvailable()) {
            try {
                this.start();
            }
            catch (final LifecycleException e) {
                throw new IllegalStateException(e);
            }
        }
    }
    
    @Override
    protected WebResource createArchiveResource(final JarEntry jarEntry, final String webAppPath, final Manifest manifest) {
        return new JarWarResource(this, webAppPath, this.getBaseUrlString(), jarEntry, this.archivePath);
    }
    
    @Override
    protected HashMap<String, JarEntry> getArchiveEntries(final boolean single) {
        synchronized (this.archiveLock) {
            if (this.archiveEntries == null) {
                JarFile warFile = null;
                InputStream jarFileIs = null;
                this.archiveEntries = new HashMap<String, JarEntry>();
                boolean multiRelease = false;
                try {
                    warFile = this.openJarFile();
                    final JarEntry jarFileInWar = warFile.getJarEntry(this.archivePath);
                    jarFileIs = warFile.getInputStream(jarFileInWar);
                    try (final TomcatJarInputStream jarIs = new TomcatJarInputStream(jarFileIs)) {
                        for (JarEntry entry = jarIs.getNextJarEntry(); entry != null; entry = jarIs.getNextJarEntry()) {
                            this.archiveEntries.put(entry.getName(), entry);
                        }
                        final Manifest m = jarIs.getManifest();
                        this.setManifest(m);
                        if (m != null && JreCompat.isJre9Available()) {
                            final String value = m.getMainAttributes().getValue("Multi-Release");
                            if (value != null) {
                                multiRelease = Boolean.parseBoolean(value);
                            }
                        }
                        JarEntry entry = jarIs.getMetaInfEntry();
                        if (entry != null) {
                            this.archiveEntries.put(entry.getName(), entry);
                        }
                        entry = jarIs.getManifestEntry();
                        if (entry != null) {
                            this.archiveEntries.put(entry.getName(), entry);
                        }
                    }
                    if (multiRelease) {
                        this.processArchivesEntriesForMultiRelease();
                    }
                }
                catch (final IOException ioe) {
                    this.archiveEntries = null;
                    throw new IllegalStateException(ioe);
                }
                finally {
                    if (warFile != null) {
                        this.closeJarFile();
                    }
                    if (jarFileIs != null) {
                        try {
                            jarFileIs.close();
                        }
                        catch (final IOException ex) {}
                    }
                }
            }
            return this.archiveEntries;
        }
    }
    
    protected void processArchivesEntriesForMultiRelease() {
        final int targetVersion = JreCompat.getInstance().jarFileRuntimeMajorVersion();
        final Map<String, VersionedJarEntry> versionedEntries = new HashMap<String, VersionedJarEntry>();
        final Iterator<Map.Entry<String, JarEntry>> iter = this.archiveEntries.entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry<String, JarEntry> entry = iter.next();
            final String name = entry.getKey();
            if (name.startsWith("META-INF/versions/")) {
                iter.remove();
                final int i = name.indexOf(47, 18);
                if (i <= 0) {
                    continue;
                }
                final String baseName = name.substring(i + 1);
                final int version = Integer.parseInt(name.substring(18, i));
                if (version > targetVersion) {
                    continue;
                }
                final VersionedJarEntry versionedJarEntry = versionedEntries.get(baseName);
                if (versionedJarEntry == null) {
                    versionedEntries.put(baseName, new VersionedJarEntry(version, entry.getValue()));
                }
                else {
                    if (version <= versionedJarEntry.getVersion()) {
                        continue;
                    }
                    versionedEntries.put(baseName, new VersionedJarEntry(version, entry.getValue()));
                }
            }
        }
        for (final Map.Entry<String, VersionedJarEntry> versionedJarEntry2 : versionedEntries.entrySet()) {
            this.archiveEntries.put(versionedJarEntry2.getKey(), versionedJarEntry2.getValue().getJarEntry());
        }
    }
    
    @Override
    protected JarEntry getArchiveEntry(final String pathInArchive) {
        throw new IllegalStateException(JarWarResourceSet.sm.getString("jarWarResourceSet.codingError"));
    }
    
    @Override
    protected boolean isMultiRelease() {
        return false;
    }
    
    @Override
    protected void initInternal() throws LifecycleException {
        try (final JarFile warFile = new JarFile(this.getBase())) {
            final JarEntry jarFileInWar = warFile.getJarEntry(this.archivePath);
            final InputStream jarFileIs = warFile.getInputStream(jarFileInWar);
            try (final JarInputStream jarIs = new JarInputStream(jarFileIs)) {
                this.setManifest(jarIs.getManifest());
            }
        }
        catch (final IOException ioe) {
            throw new IllegalArgumentException(ioe);
        }
        try {
            this.setBaseUrl(UriUtil.buildJarSafeUrl(new File(this.getBase())));
        }
        catch (final MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    private static final class VersionedJarEntry
    {
        private final int version;
        private final JarEntry jarEntry;
        
        public VersionedJarEntry(final int version, final JarEntry jarEntry) {
            this.version = version;
            this.jarEntry = jarEntry;
        }
        
        public int getVersion() {
            return this.version;
        }
        
        public JarEntry getJarEntry() {
            return this.jarEntry;
        }
    }
}
