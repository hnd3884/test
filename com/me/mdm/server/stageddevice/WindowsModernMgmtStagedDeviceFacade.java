package com.me.mdm.server.stageddevice;

import com.me.mdm.uem.ModernMgmtDeviceForEnrollmentHandler;
import org.json.JSONObject;
import java.util.Set;
import org.json.JSONArray;

public class WindowsModernMgmtStagedDeviceFacade extends ModernMgmtStagedDeviceFacade
{
    protected WindowsModernMgmtStagedDeviceFacade(final Integer platformType) {
        super(platformType);
    }
    
    @Override
    protected JSONObject processDeviceAddRequest(final JSONArray deviceArray, final Set<String> serialNumberList, final Set<String> udidList, final Set<String> genericList) throws Exception {
        boolean isDeviceAllowed = false;
        final JSONObject apiResponse = new ModernMgmtDeviceForEnrollmentHandler().processWindowsModernMgmtDeviceList(deviceArray, serialNumberList, udidList, genericList);
        isDeviceAllowed = Boolean.TRUE;
        apiResponse.put("isDeviceAllowed", isDeviceAllowed);
        return apiResponse;
    }
    
    @Override
    protected Boolean processDevicesDeleteRequest(final JSONObject devicesDeleteBodyJSON) throws Exception {
        return this.deleteModernDeviceForEnrollment(devicesDeleteBodyJSON);
    }
}
