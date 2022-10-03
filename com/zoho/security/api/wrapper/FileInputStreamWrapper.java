package com.zoho.security.api.wrapper;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;

public class FileInputStreamWrapper extends FileInputStream
{
    private static final FileAccessVerify DEFAULT_FILE_ACCESS_VERIFIER;
    
    public FileInputStreamWrapper(final String path) throws FileAccessDeniedException, IOException {
        this(path, FileInputStreamWrapper.DEFAULT_FILE_ACCESS_VERIFIER);
    }
    
    public FileInputStreamWrapper(final File file) throws IOException {
        this(file, FileInputStreamWrapper.DEFAULT_FILE_ACCESS_VERIFIER);
    }
    
    public FileInputStreamWrapper(final String path, final FileAccessVerify fav) throws FileAccessDeniedException, IOException {
        super(fav.getAccessVerifiedPath(path));
    }
    
    public FileInputStreamWrapper(final File file, final FileAccessVerify fav) throws FileAccessDeniedException, IOException {
        super(fav.getAccessVerifiedPath(file));
    }
    
    static {
        DEFAULT_FILE_ACCESS_VERIFIER = new FileAccessVerify();
    }
}
