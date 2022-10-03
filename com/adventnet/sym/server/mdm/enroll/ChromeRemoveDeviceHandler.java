package com.adventnet.sym.server.mdm.enroll;

import com.adventnet.persistence.DataAccessException;
import java.util.List;
import com.me.mdm.chrome.agent.enrollment.ChromeEnrollmentUtil;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.HashMap;

public class ChromeRemoveDeviceHandler extends BaseRemoveDeviceHandler
{
    @Override
    public void handleRemoveDevice(final HashMap hashMap, final Long customerId) throws Exception {
        super.handleRemoveDevice(hashMap, customerId);
        this.validateAndUpdateChromeDeviceStatus(hashMap, customerId);
    }
    
    private void validateAndUpdateChromeDeviceStatus(final HashMap hashMap, final Long customerId) throws DataAccessException {
        final String strStatus = hashMap.get("Status");
        if (strStatus.equalsIgnoreCase("Acknowledged")) {
            this.logger.log(Level.INFO, "Chrome device removed successfully, updating status as UNENROLLED in table CHROMEDEVICEMANAGEDSTATUS");
            final String deviceUDID = hashMap.get("UDID");
            final List<String> deviceUDIDList = new ArrayList<String>();
            deviceUDIDList.add(deviceUDID);
            ChromeEnrollmentUtil.getInstance().addOrUpdateChromeDeviceStatus(customerId, deviceUDIDList, ChromeEnrollmentUtil.CHROME_STATUS_UNENROLLED);
        }
    }
}
