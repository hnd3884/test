package com.me.devicemanagement.framework.server.api;

import java.util.Properties;
import java.util.Map;

public interface SupportTabAPI
{
    default Properties uploadAgentLogs(final Map supportFileDetails) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    default boolean isAgentAndDSAvailableForUser(final Map supportFileDetails) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
