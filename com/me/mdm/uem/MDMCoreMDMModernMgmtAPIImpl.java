package com.me.mdm.uem;

import com.me.mdm.uem.actionconstants.ConfigurationAction;
import com.me.mdm.uem.actionconstants.LicenseAction;
import org.json.JSONObject;
import com.me.mdm.uem.actionconstants.DeviceAction;
import com.me.mdm.server.factory.MDMModernMgmtAPI;

public class MDMCoreMDMModernMgmtAPIImpl implements MDMModernMgmtAPI
{
    @Override
    public JSONObject deviceListener(final DeviceAction action, final JSONObject params) {
        return null;
    }
    
    @Override
    public JSONObject licenseListener(final LicenseAction action) {
        return null;
    }
    
    @Override
    public JSONObject configurationListener(final ConfigurationAction action, final JSONObject params) {
        return null;
    }
}
