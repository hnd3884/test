package com.me.mdm.server.command.ios.QueryGenerator;

import com.adventnet.sym.server.mdm.ios.payload.IOSCommandPayload;
import org.json.JSONObject;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import com.me.mdm.server.privacy.PrivacySettingsHandler;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import com.adventnet.sym.server.mdm.DeviceDetails;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.command.CommandQueryCreator;

public class IOSDeviceInformationQueryGenerator implements CommandQueryCreator
{
    private final Logger logger;
    
    public IOSDeviceInformationQueryGenerator() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public String createCmdQuery(final DeviceCommand deviceCommand, final String strUDID, final Long resourceID, final HashMap requestMap) {
        String strQuery = null;
        try {
            Boolean fetchDeviceName = true;
            Boolean fetchPhonenum = true;
            Boolean fetchMacAddr = true;
            int ownedby;
            long customerId;
            if (resourceID != null) {
                final DeviceDetails deviceDetails = new DeviceDetails(resourceID);
                ownedby = deviceDetails.ownedBy;
                customerId = deviceDetails.customerId;
            }
            else {
                final Long enrollmentReqId = requestMap.get("ENROLLMENT_REQUEST_ID");
                customerId = requestMap.get("CUSTOMER_ID");
                ownedby = MDMEnrollmentRequestHandler.getInstance().getOwnedByForEnrollmentRequest(enrollmentReqId);
            }
            final JSONObject privacyJson = new PrivacySettingsHandler().getPrivacyDetails(ownedby, customerId);
            final int fetchPhone = privacyJson.optInt("fetch_phone_number");
            final int fetchDevice = privacyJson.optInt("fetch_device_name");
            final int fetchMac = privacyJson.optInt("fetch_mac_address");
            if (fetchPhone == 2) {
                fetchPhonenum = false;
            }
            if (fetchDevice == 2) {
                fetchDeviceName = false;
            }
            if (fetchMac == 2) {
                fetchMacAddr = false;
            }
            final boolean isEnrollmentCommand = deviceCommand.commandUUID.equals("Enrollment");
            final IOSCommandPayload createDeviceInfoCommand = PayloadHandler.getInstance().createDeviceInformationCommand(fetchDeviceName, fetchPhonenum, fetchMacAddr, isEnrollmentCommand);
            createDeviceInfoCommand.setCommandUUID(deviceCommand.commandUUID, false);
            strQuery = createDeviceInfoCommand.toString();
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in device information query generation.. ", ex);
        }
        return strQuery;
    }
}
