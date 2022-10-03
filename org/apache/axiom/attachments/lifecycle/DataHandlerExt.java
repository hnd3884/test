package org.apache.axiom.attachments.lifecycle;

import java.io.IOException;
import java.io.InputStream;

public interface DataHandlerExt
{
    InputStream readOnce() throws IOException;
    
    void purgeDataSource() throws IOException;
    
    @Deprecated
    void deleteWhenReadOnce() throws IOException;
}
