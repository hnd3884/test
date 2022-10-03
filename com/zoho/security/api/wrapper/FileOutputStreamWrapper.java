package com.zoho.security.api.wrapper;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;

public class FileOutputStreamWrapper extends FileOutputStream
{
    private static final FileAccessVerify DEFAULT_FILEACCESS_CONFIG;
    
    public FileOutputStreamWrapper(final String path) throws FileAccessDeniedException, IOException {
        this(path, FileOutputStreamWrapper.DEFAULT_FILEACCESS_CONFIG);
    }
    
    public FileOutputStreamWrapper(final File file) throws IOException {
        this(file, FileOutputStreamWrapper.DEFAULT_FILEACCESS_CONFIG);
    }
    
    public FileOutputStreamWrapper(final String path, final FileAccessVerify fav) throws FileAccessDeniedException, IOException {
        super(fav.getAccessVerifiedPath(path));
    }
    
    public FileOutputStreamWrapper(final File file, final FileAccessVerify fav) throws FileAccessDeniedException, IOException {
        super(fav.getAccessVerifiedPath(file));
    }
    
    static {
        DEFAULT_FILEACCESS_CONFIG = new FileAccessVerify();
    }
}
