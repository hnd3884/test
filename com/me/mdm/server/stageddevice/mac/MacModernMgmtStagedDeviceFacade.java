package com.me.mdm.server.stageddevice.mac;

import com.me.mdm.uem.ModernMgmtDeviceForEnrollmentHandler;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.Set;
import org.json.JSONArray;
import java.util.logging.Logger;
import com.me.mdm.server.stageddevice.ModernMgmtStagedDeviceFacade;

public class MacModernMgmtStagedDeviceFacade extends ModernMgmtStagedDeviceFacade
{
    protected static Logger logger;
    
    public MacModernMgmtStagedDeviceFacade(final Integer platformType) {
        super(platformType);
    }
    
    @Override
    protected JSONObject processDeviceAddRequest(final JSONArray deviceArray, final Set<String> serialNumberList, final Set<String> udidList, final Set<String> genericList) throws Exception {
        MacModernMgmtStagedDeviceFacade.logger.log(Level.INFO, "MacModernMgmt: Inside processDeviceAddRequest()....");
        MacModernMgmtStagedDeviceFacade.logger.log(Level.INFO, "MacModernMgmt: Going to add following serialNos to deviceforEnrollment: {0}", serialNumberList);
        boolean isDeviceAllowed = false;
        final JSONObject apiResponse = new ModernMgmtDeviceForEnrollmentHandler().processMacModernMgmtDeviceForEnrollmentList(deviceArray, serialNumberList, udidList, genericList);
        isDeviceAllowed = Boolean.TRUE;
        apiResponse.put("isDeviceAllowed", isDeviceAllowed);
        return apiResponse;
    }
    
    @Override
    protected Boolean processDevicesDeleteRequest(final JSONObject devicesDeleteBodyJSON) throws Exception {
        MacModernMgmtStagedDeviceFacade.logger.log(Level.INFO, "MacModernMgmt: Inside processDevicesDeleteRequest()....");
        MacModernMgmtStagedDeviceFacade.logger.log(Level.INFO, "MacModernMgmt: Going to infovke DeleteRequest for devices: {0}", devicesDeleteBodyJSON);
        return this.deleteModernDeviceForEnrollment(devicesDeleteBodyJSON);
    }
    
    static {
        MacModernMgmtStagedDeviceFacade.logger = Logger.getLogger("MDMModernMgmtLogger");
    }
}
