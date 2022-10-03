package org.apache.axiom.attachments.lifecycle;

import java.io.File;
import java.io.IOException;
import org.apache.axiom.attachments.lifecycle.impl.FileAccessor;

public interface LifecycleManager
{
    FileAccessor create(final String p0) throws IOException;
    
    void delete(final File p0) throws IOException;
    
    void deleteOnExit(final File p0) throws IOException;
    
    void deleteOnTimeInterval(final int p0, final File p1) throws IOException;
    
    FileAccessor getFileAccessor(final String p0) throws IOException;
}
