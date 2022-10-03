package com.me.dc.server.util;

import java.util.ArrayList;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.sym.server.admin.SoMUtil;
import java.util.Properties;
import com.me.devicemanagement.framework.server.api.ServiceAPI;

public class ServiceImpl implements ServiceAPI
{
    public Properties getServiceProperty() {
        Properties props = null;
        try {
            props = new Properties();
            SoMUtil.getInstance();
            final String som = SoMUtil.getSoMPropertyForTracking();
            props.setProperty("som", som);
            return props;
        }
        catch (final SyMException ex) {
            Logger.getLogger(ServiceImpl.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
        catch (final Exception ex2) {
            Logger.getLogger(ServiceImpl.class.getName()).log(Level.SEVERE, null, ex2);
        }
        return props;
    }
    
    public String getTrackingSummary() {
        return SoMUtil.getInstance().getSoMTrackingSummary();
    }
    
    public ArrayList<String> getBackupFoldersList(final String serverHome) {
        return new ArrayList<String>();
    }
}
