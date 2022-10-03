package com.me.mdm.server.support;

import java.io.IOException;
import java.util.Properties;

public interface MDMCompressAPI
{
    void createSupportFile(final Properties p0) throws Exception;
    
    String compressSupportFile(final String p0) throws Exception;
    
    String getSupportFileName() throws Exception;
    
    String getSupportMailSubject() throws Exception;
    
    void cancelOperation() throws Exception;
    
    void cleanSupportFolder(final boolean p0) throws Exception;
    
    void cleanSupportLogFolder(final boolean p0) throws Exception;
    
    String createFailoverSupportFile(final String p0, final String p1, final String p2) throws IOException;
}
