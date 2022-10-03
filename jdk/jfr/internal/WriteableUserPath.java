package jdk.jfr.internal;

import java.security.PrivilegedExceptionAction;
import java.util.concurrent.Callable;
import java.io.IOException;
import java.nio.file.OpenOption;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.security.AccessController;
import java.nio.file.Path;
import java.security.AccessControlContext;

public final class WriteableUserPath
{
    private final AccessControlContext controlContext;
    private final Path original;
    private final Path real;
    private final String realPathText;
    private final String originalText;
    private volatile boolean inPrivileged;
    
    public WriteableUserPath(final Path original) throws IOException {
        this.controlContext = AccessController.getContext();
        if (Files.exists(original, new LinkOption[0]) && !Files.isWritable(original)) {
            throw new FileNotFoundException("Could not write to file: " + original.toAbsolutePath());
        }
        Files.newBufferedWriter(original, new OpenOption[0]).close();
        this.original = original;
        this.originalText = original.toString();
        this.real = original.toRealPath(new LinkOption[0]);
        this.realPathText = this.real.toString();
    }
    
    public Path getPotentiallyMaliciousOriginal() {
        return this.original;
    }
    
    public String getRealPathText() {
        return this.realPathText;
    }
    
    public String getOriginalText() {
        return this.originalText;
    }
    
    public Path getReal() {
        if (!this.inPrivileged) {
            throw new InternalError("A user path was accessed outside the context it was supplied in");
        }
        return this.real;
    }
    
    public void doPriviligedIO(final Callable<?> callable) throws IOException {
        try {
            this.inPrivileged = true;
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    callable.call();
                    return null;
                }
            }, this.controlContext);
        }
        catch (final Throwable t) {
            throw new IOException("Unexpected error during I/O operation");
        }
        finally {
            this.inPrivileged = false;
        }
    }
}
