package java.nio.file.spi;

import java.io.IOException;
import java.nio.file.Path;
import java.security.Permission;

public abstract class FileTypeDetector
{
    private static Void checkPermission() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new RuntimePermission("fileTypeDetector"));
        }
        return null;
    }
    
    private FileTypeDetector(final Void void1) {
    }
    
    protected FileTypeDetector() {
        this(checkPermission());
    }
    
    public abstract String probeContentType(final Path p0) throws IOException;
}
