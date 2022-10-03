package com.sun.mail.util;

import javax.mail.Folder;
import java.io.IOException;

public class FolderClosedIOException extends IOException
{
    private transient Folder folder;
    private static final long serialVersionUID = 4281122580365555735L;
    
    public FolderClosedIOException(final Folder folder) {
        this(folder, null);
    }
    
    public FolderClosedIOException(final Folder folder, final String message) {
        super(message);
        this.folder = folder;
    }
    
    public Folder getFolder() {
        return this.folder;
    }
}
