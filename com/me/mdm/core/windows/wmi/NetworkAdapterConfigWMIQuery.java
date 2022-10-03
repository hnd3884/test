package com.me.mdm.core.windows.wmi;

import java.net.URLEncoder;
import com.me.mdm.framework.syncml.requestcmds.GetRequestCommand;
import java.util.ArrayList;

public class NetworkAdapterConfigWMIQuery extends WMIQuery
{
    public NetworkAdapterConfigWMIQuery() {
        this.wmiNamespace = "./cimV2";
        this.wmiClassName = "Win32_NetworkAdapterConfiguration";
        this.wmiCommandName = "NetworkAdapterConfig";
        this.wmiClassProperties = new ArrayList<String>() {
            {
                this.add("IPAddress");
                this.add("MACAddress");
            }
        };
    }
    
    @Override
    public GetRequestCommand modifyChildPropertyQuery(final GetRequestCommand baseGetRequestCommand, final String[] wmiInstances) throws Exception {
        for (final String wmiInstance : wmiInstances) {
            final String[] deviceIDsplit = wmiInstance.split("=");
            if (deviceIDsplit.length > 1) {
                final String deviceID = deviceIDsplit[1];
                final String networkAdapterInstance = "Win32_NetworkAdapter.DeviceID=\"" + deviceID + "\"";
                final String wmiUrl = this.getWmiNamespace() + "/" + "Win32_NetworkAdapter" + "/" + URLEncoder.encode(networkAdapterInstance, "UTF-8") + "/" + "NetConnectionID";
                baseGetRequestCommand.addRequestItem(this.createCommandItemTagElement(wmiUrl));
            }
        }
        return baseGetRequestCommand;
    }
}
