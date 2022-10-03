package sun.nio.fs;

import java.nio.file.FileSystemException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.io.IOException;

class WindowsException extends Exception
{
    static final long serialVersionUID = 2765039493083748820L;
    private int lastError;
    private String msg;
    
    WindowsException(final int lastError) {
        this.lastError = lastError;
        this.msg = null;
    }
    
    WindowsException(final String msg) {
        this.lastError = 0;
        this.msg = msg;
    }
    
    int lastError() {
        return this.lastError;
    }
    
    String errorString() {
        if (this.msg == null) {
            this.msg = WindowsNativeDispatcher.FormatMessage(this.lastError);
            if (this.msg == null) {
                this.msg = "Unknown error: 0x" + Integer.toHexString(this.lastError);
            }
        }
        return this.msg;
    }
    
    @Override
    public String getMessage() {
        return this.errorString();
    }
    
    private IOException translateToIOException(final String s, final String s2) {
        if (this.lastError() == 0) {
            return new IOException(this.errorString());
        }
        if (this.lastError() == 2 || this.lastError() == 3) {
            return new NoSuchFileException(s, s2, null);
        }
        if (this.lastError() == 80 || this.lastError() == 183) {
            return new FileAlreadyExistsException(s, s2, null);
        }
        if (this.lastError() == 5) {
            return new AccessDeniedException(s, s2, null);
        }
        return new FileSystemException(s, s2, this.errorString());
    }
    
    void rethrowAsIOException(final String s) throws IOException {
        throw this.translateToIOException(s, null);
    }
    
    void rethrowAsIOException(final WindowsPath windowsPath, final WindowsPath windowsPath2) throws IOException {
        throw this.translateToIOException((windowsPath == null) ? null : windowsPath.getPathForExceptionMessage(), (windowsPath2 == null) ? null : windowsPath2.getPathForExceptionMessage());
    }
    
    void rethrowAsIOException(final WindowsPath windowsPath) throws IOException {
        this.rethrowAsIOException(windowsPath, null);
    }
    
    IOException asIOException(final WindowsPath windowsPath) {
        return this.translateToIOException(windowsPath.getPathForExceptionMessage(), null);
    }
}
