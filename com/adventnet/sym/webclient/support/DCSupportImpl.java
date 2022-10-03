package com.adventnet.sym.webclient.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.HashMap;
import com.adventnet.sym.server.util.SyMUtil;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.api.SupportAPI;

public class DCSupportImpl implements SupportAPI
{
    private Logger out;
    
    public DCSupportImpl() {
        this.out = Logger.getLogger("UserManagementLogger");
    }
    
    public String getServerOS() throws Exception {
        return SyMUtil.getServerOS();
    }
    
    public void logComputerEvents(final String serverName, final String logName, final String destinationPath) throws Exception {
    }
    
    public HashMap<String, String> getSupportParam() throws Exception {
        final HashMap<String, String> supportParams = new HashMap<String, String>();
        return supportParams;
    }
    
    public Properties getSupportUploadState() throws Exception {
        final Properties props = new Properties();
        return props;
    }
    
    private HashMap<String, ArrayList<String>> getComputerGroupedByDomain(final List uploadedLogs) {
        final HashMap<String, ArrayList<String>> uploadMap = new HashMap<String, ArrayList<String>>();
        return uploadMap;
    }
}
