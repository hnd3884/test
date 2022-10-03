package com.me.mdm.server.service;

import java.io.File;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Properties;
import com.me.devicemanagement.framework.server.api.ServiceAPI;

public class ServiceImpl implements ServiceAPI
{
    public Properties getServiceProperty() {
        return null;
    }
    
    public String getTrackingSummary() {
        return MDMUtil.getInstance().getMDMPropertyForTracking();
    }
    
    public ArrayList<String> getBackupFoldersList(final String serverHome) {
        final ArrayList<String> mdmBackupFileslist = new ArrayList<String>();
        mdmBackupFileslist.add(serverHome + File.separator + "webapps" + File.separator + "DesktopCentral" + File.separator + "mdm");
        return mdmBackupFileslist;
    }
}
