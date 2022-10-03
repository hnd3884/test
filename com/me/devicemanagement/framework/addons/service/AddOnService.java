package com.me.devicemanagement.framework.addons.service;

import com.me.devicemanagement.framework.addons.AddOnHandler;
import com.adventnet.persistence.DataObject;
import com.adventnet.mfw.service.Service;

public class AddOnService implements Service
{
    public void create(final DataObject dataObject) throws Exception {
    }
    
    public void start() throws Exception {
    }
    
    public void stop() throws Exception {
    }
    
    public void destroy() throws Exception {
        AddOnHandler.getInstance().updateAddOnUpdateRunningStatus(false);
    }
}
