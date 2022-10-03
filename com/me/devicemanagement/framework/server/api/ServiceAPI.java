package com.me.devicemanagement.framework.server.api;

import java.util.ArrayList;
import java.util.Properties;

public interface ServiceAPI
{
    Properties getServiceProperty();
    
    String getTrackingSummary();
    
    ArrayList<String> getBackupFoldersList(final String p0);
}
