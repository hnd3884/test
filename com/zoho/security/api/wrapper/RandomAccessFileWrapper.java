package com.zoho.security.api.wrapper;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RandomAccessFileWrapper extends RandomAccessFile
{
    public RandomAccessFileWrapper(final String name, final String mode, final FileAccessVerify fav) throws FileAccessDeniedException, IOException {
        super(fav.getAccessVerifiedPath(name), mode);
    }
    
    public RandomAccessFileWrapper(final File file, final String mode, final FileAccessVerify fav) throws FileAccessDeniedException, IOException {
        super(fav.getAccessVerifiedPath(file), mode);
    }
}
