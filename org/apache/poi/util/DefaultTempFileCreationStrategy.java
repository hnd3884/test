package org.apache.poi.util;

import java.io.IOException;
import java.io.File;
import java.security.SecureRandom;

public class DefaultTempFileCreationStrategy implements TempFileCreationStrategy
{
    static final String POIFILES = "poifiles";
    public static final String KEEP_FILES = "poi.keep.tmp.files";
    private static final SecureRandom random;
    private File dir;
    
    public DefaultTempFileCreationStrategy() {
        this(null);
    }
    
    public DefaultTempFileCreationStrategy(final File dir) {
        this.dir = dir;
    }
    
    private void createPOIFilesDirectory() throws IOException {
        if (this.dir == null) {
            final String tmpDir = System.getProperty("java.io.tmpdir");
            if (tmpDir == null) {
                throw new IOException("Systems temporary directory not defined - set the -Djava.io.tmpdir jvm property!");
            }
            this.dir = new File(tmpDir, "poifiles");
        }
        this.createTempDirectory(this.dir);
    }
    
    private synchronized void createTempDirectory(final File directory) throws IOException {
        final boolean dirExists = directory.exists() || directory.mkdirs();
        if (!dirExists) {
            throw new IOException("Could not create temporary directory '" + directory + "'");
        }
        if (!directory.isDirectory()) {
            throw new IOException("Could not create temporary directory. '" + directory + "' exists but is not a directory.");
        }
    }
    
    @Override
    public File createTempFile(final String prefix, final String suffix) throws IOException {
        this.createPOIFilesDirectory();
        final File newFile = File.createTempFile(prefix, suffix, this.dir);
        if (System.getProperty("poi.keep.tmp.files") == null) {
            newFile.deleteOnExit();
        }
        return newFile;
    }
    
    @Override
    public File createTempDirectory(final String prefix) throws IOException {
        this.createPOIFilesDirectory();
        final long n = DefaultTempFileCreationStrategy.random.nextLong();
        final File newDirectory = new File(this.dir, prefix + Long.toString(n));
        this.createTempDirectory(newDirectory);
        if (System.getProperty("poi.keep.tmp.files") == null) {
            newDirectory.deleteOnExit();
        }
        return newDirectory;
    }
    
    static {
        random = new SecureRandom();
    }
}
