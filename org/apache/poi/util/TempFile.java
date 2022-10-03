package org.apache.poi.util;

import java.io.IOException;
import java.io.File;

public final class TempFile
{
    private static TempFileCreationStrategy strategy;
    public static final String JAVA_IO_TMPDIR = "java.io.tmpdir";
    
    private TempFile() {
    }
    
    public static void setTempFileCreationStrategy(final TempFileCreationStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("strategy == null");
        }
        TempFile.strategy = strategy;
    }
    
    public static File createTempFile(final String prefix, final String suffix) throws IOException {
        return TempFile.strategy.createTempFile(prefix, suffix);
    }
    
    public static File createTempDirectory(final String name) throws IOException {
        return TempFile.strategy.createTempDirectory(name);
    }
    
    static {
        TempFile.strategy = new DefaultTempFileCreationStrategy();
    }
}
