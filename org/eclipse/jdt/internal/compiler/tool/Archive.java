package org.eclipse.jdt.internal.compiler.tool;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.io.IOException;
import java.util.zip.ZipException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.io.File;
import java.util.zip.ZipFile;

public class Archive
{
    public static final Archive UNKNOWN_ARCHIVE;
    ZipFile zipFile;
    File file;
    protected Hashtable<String, ArrayList<String>> packagesCache;
    
    static {
        UNKNOWN_ARCHIVE = new Archive();
    }
    
    private Archive() {
    }
    
    public Archive(final File file) throws ZipException, IOException {
        this.file = file;
        this.zipFile = new ZipFile(file);
        this.initialize();
    }
    
    private void initialize() {
        this.packagesCache = new Hashtable<String, ArrayList<String>>();
        final Enumeration<? extends ZipEntry> e = this.zipFile.entries();
        while (e.hasMoreElements()) {
            final String fileName = ((ZipEntry)e.nextElement()).getName();
            final int last = fileName.lastIndexOf(47);
            final String packageName = fileName.substring(0, last + 1);
            final String typeName = fileName.substring(last + 1);
            ArrayList<String> types = this.packagesCache.get(packageName);
            if (types == null) {
                if (typeName.length() == 0) {
                    continue;
                }
                types = new ArrayList<String>();
                types.add(typeName);
                this.packagesCache.put(packageName, types);
            }
            else {
                types.add(typeName);
            }
        }
    }
    
    public ArchiveFileObject getArchiveFileObject(final String entryName, final Charset charset) {
        return new ArchiveFileObject(this.file, entryName, charset);
    }
    
    public boolean contains(final String entryName) {
        return this.zipFile.getEntry(entryName) != null;
    }
    
    public Set<String> allPackages() {
        if (this.packagesCache == null) {
            this.initialize();
        }
        return this.packagesCache.keySet();
    }
    
    public List<String> getTypes(final String packageName) {
        if (this.packagesCache == null) {
            try {
                this.zipFile = new ZipFile(this.file);
            }
            catch (final IOException ex) {
                return Collections.emptyList();
            }
            this.initialize();
        }
        return this.packagesCache.get(packageName);
    }
    
    public void flush() {
        this.packagesCache = null;
    }
    
    public void close() {
        try {
            if (this.zipFile != null) {
                this.zipFile.close();
            }
            this.packagesCache = null;
        }
        catch (final IOException ex) {}
    }
    
    @Override
    public String toString() {
        return "Archive: " + ((this.file == null) ? "UNKNOWN_ARCHIVE" : this.file.getAbsolutePath());
    }
}
