package com.me.devicemanagement.framework.server.logger.seconelinelogger;

import java.util.logging.Logger;
import javax.servlet.ServletRequest;

public interface SecOneLineLoggerAccessAPI
{
    String getSessionUniqId(final ServletRequest p0);
    
    Logger getLogger(final String p0);
    
    boolean isSecurityLoggerEnabled();
}
