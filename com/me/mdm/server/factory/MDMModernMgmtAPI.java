package com.me.mdm.server.factory;

import com.me.mdm.uem.actionconstants.ConfigurationAction;
import com.me.mdm.uem.actionconstants.LicenseAction;
import org.json.JSONObject;
import com.me.mdm.uem.actionconstants.DeviceAction;

public interface MDMModernMgmtAPI
{
    JSONObject deviceListener(final DeviceAction p0, final JSONObject p1);
    
    JSONObject licenseListener(final LicenseAction p0);
    
    JSONObject configurationListener(final ConfigurationAction p0, final JSONObject p1);
}
