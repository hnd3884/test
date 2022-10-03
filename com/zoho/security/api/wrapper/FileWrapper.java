package com.zoho.security.api.wrapper;

import java.net.URI;
import java.io.IOException;
import java.io.File;

public class FileWrapper extends File
{
    public FileWrapper(final String pathname, final FileAccessVerify fav) throws FileAccessDeniedException, IOException {
        super(pathname);
        fav.getAccessVerifiedPath(this);
    }
    
    public FileWrapper(final String parent, final String child, final FileAccessVerify fav) throws FileAccessDeniedException, IOException {
        super(parent, child);
        fav.getAccessVerifiedPath(this);
    }
    
    public FileWrapper(final File parent, final String child, final FileAccessVerify fav) throws FileAccessDeniedException, IOException {
        super(parent, child);
        fav.getAccessVerifiedPath(this);
    }
    
    public FileWrapper(final URI uri, final FileAccessVerify fav) throws FileAccessDeniedException, IOException {
        super(uri);
        fav.getAccessVerifiedPath(this);
    }
}
