package com.zoho.security.api.wrapper;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;

public class FileWriterWrapper extends FileWriter
{
    private static final FileAccessVerify DEFAULT_FILE_ACCESS_VERIFIER;
    
    public FileWriterWrapper(final String fileName) throws FileAccessDeniedException, IOException {
        this(fileName, FileWriterWrapper.DEFAULT_FILE_ACCESS_VERIFIER);
    }
    
    public FileWriterWrapper(final File file) throws FileAccessDeniedException, IOException {
        this(file, FileWriterWrapper.DEFAULT_FILE_ACCESS_VERIFIER);
    }
    
    public FileWriterWrapper(final String path, final FileAccessVerify fav) throws FileAccessDeniedException, IOException {
        super(fav.getAccessVerifiedPath(path));
    }
    
    public FileWriterWrapper(final File file, final FileAccessVerify fav) throws FileAccessDeniedException, IOException {
        super(fav.getAccessVerifiedPath(file));
    }
    
    static {
        DEFAULT_FILE_ACCESS_VERIFIER = new FileAccessVerify();
    }
}
