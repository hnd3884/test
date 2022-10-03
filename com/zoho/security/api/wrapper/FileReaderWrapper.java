package com.zoho.security.api.wrapper;

import java.io.File;
import java.io.IOException;
import java.io.FileReader;

public class FileReaderWrapper extends FileReader
{
    private static final FileAccessVerify DEFAULT_FILE_ACCESS_VERIFIER;
    
    public FileReaderWrapper(final String fileName) throws FileAccessDeniedException, IOException {
        this(fileName, FileReaderWrapper.DEFAULT_FILE_ACCESS_VERIFIER);
    }
    
    public FileReaderWrapper(final File file) throws FileAccessDeniedException, IOException {
        this(file, FileReaderWrapper.DEFAULT_FILE_ACCESS_VERIFIER);
    }
    
    public FileReaderWrapper(final String path, final FileAccessVerify fav) throws FileAccessDeniedException, IOException {
        super(fav.getAccessVerifiedPath(path));
    }
    
    public FileReaderWrapper(final File file, final FileAccessVerify fav) throws FileAccessDeniedException, IOException {
        super(fav.getAccessVerifiedPath(file));
    }
    
    static {
        DEFAULT_FILE_ACCESS_VERIFIER = new FileAccessVerify();
    }
}
