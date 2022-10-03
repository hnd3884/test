package sun.security.action;

import java.io.File;
import java.io.FileInputStream;
import java.security.PrivilegedExceptionAction;

public class OpenFileInputStreamAction implements PrivilegedExceptionAction<FileInputStream>
{
    private final File file;
    
    public OpenFileInputStreamAction(final File file) {
        this.file = file;
    }
    
    public OpenFileInputStreamAction(final String s) {
        this.file = new File(s);
    }
    
    @Override
    public FileInputStream run() throws Exception {
        return new FileInputStream(this.file);
    }
}
