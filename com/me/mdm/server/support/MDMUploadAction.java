package com.me.mdm.server.support;

import java.util.Properties;

public interface MDMUploadAction
{
    boolean checkUploadAccess() throws Exception;
    
    boolean uploadSupportFile(final String p0, final String p1, final String p2, final String p3, final String p4) throws Exception;
    
    String getEnvironment() throws Exception;
    
    Properties getProductInfo() throws Exception;
    
    String getSubject(final int p0) throws Exception;
}
