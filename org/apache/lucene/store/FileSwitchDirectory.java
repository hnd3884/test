package org.apache.lucene.store;

import java.nio.file.AtomicMoveNotSupportedException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.nio.file.NoSuchFileException;
import java.util.HashSet;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import java.io.IOException;
import java.util.Set;

public class FileSwitchDirectory extends Directory
{
    private final Directory secondaryDir;
    private final Directory primaryDir;
    private final Set<String> primaryExtensions;
    private boolean doClose;
    
    public FileSwitchDirectory(final Set<String> primaryExtensions, final Directory primaryDir, final Directory secondaryDir, final boolean doClose) {
        this.primaryExtensions = primaryExtensions;
        this.primaryDir = primaryDir;
        this.secondaryDir = secondaryDir;
        this.doClose = doClose;
    }
    
    public Directory getPrimaryDir() {
        return this.primaryDir;
    }
    
    public Directory getSecondaryDir() {
        return this.secondaryDir;
    }
    
    @Override
    public Lock obtainLock(final String name) throws IOException {
        return this.getDirectory(name).obtainLock(name);
    }
    
    @Override
    public void close() throws IOException {
        if (this.doClose) {
            IOUtils.close(this.primaryDir, this.secondaryDir);
            this.doClose = false;
        }
    }
    
    @Override
    public String[] listAll() throws IOException {
        final Set<String> files = new HashSet<String>();
        NoSuchFileException exc = null;
        try {
            for (final String f : this.primaryDir.listAll()) {
                files.add(f);
            }
        }
        catch (final NoSuchFileException e) {
            exc = e;
        }
        try {
            for (final String f : this.secondaryDir.listAll()) {
                files.add(f);
            }
        }
        catch (final NoSuchFileException e) {
            if (exc != null) {
                throw exc;
            }
            if (files.isEmpty()) {
                throw e;
            }
        }
        if (exc != null && files.isEmpty()) {
            throw exc;
        }
        return files.toArray(new String[files.size()]);
    }
    
    public static String getExtension(final String name) {
        final int i = name.lastIndexOf(46);
        if (i == -1) {
            return "";
        }
        return name.substring(i + 1, name.length());
    }
    
    private Directory getDirectory(final String name) {
        final String ext = getExtension(name);
        if (this.primaryExtensions.contains(ext)) {
            return this.primaryDir;
        }
        return this.secondaryDir;
    }
    
    @Override
    public void deleteFile(final String name) throws IOException {
        this.getDirectory(name).deleteFile(name);
    }
    
    @Override
    public long fileLength(final String name) throws IOException {
        return this.getDirectory(name).fileLength(name);
    }
    
    @Override
    public IndexOutput createOutput(final String name, final IOContext context) throws IOException {
        return this.getDirectory(name).createOutput(name, context);
    }
    
    @Override
    public void sync(final Collection<String> names) throws IOException {
        final List<String> primaryNames = new ArrayList<String>();
        final List<String> secondaryNames = new ArrayList<String>();
        for (final String name : names) {
            if (this.primaryExtensions.contains(getExtension(name))) {
                primaryNames.add(name);
            }
            else {
                secondaryNames.add(name);
            }
        }
        this.primaryDir.sync(primaryNames);
        this.secondaryDir.sync(secondaryNames);
    }
    
    @Override
    public void renameFile(final String source, final String dest) throws IOException {
        final Directory sourceDir = this.getDirectory(source);
        if (sourceDir != this.getDirectory(dest)) {
            throw new AtomicMoveNotSupportedException(source, dest, "source and dest are in different directories");
        }
        sourceDir.renameFile(source, dest);
    }
    
    @Override
    public IndexInput openInput(final String name, final IOContext context) throws IOException {
        return this.getDirectory(name).openInput(name, context);
    }
}
