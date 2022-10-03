package com.me.mdm.uem.queue;

import com.me.mdm.uem.actionconstants.DeviceAction;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.logging.Level;
import com.me.mdm.uem.ModernMgmtDeviceForEnrollmentHandler;
import com.me.mdm.uem.ModernDeviceUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class ModernMgmtContactTimeData implements ModernMgmtOperationData
{
    Long resourceID;
    Long lastConact;
    Logger logger;
    
    public ModernMgmtContactTimeData(final Long resourceID, final Long lastContact) {
        this.logger = Logger.getLogger("MDMEnrollment");
        this.resourceID = resourceID;
        this.lastConact = lastContact;
    }
    
    public ModernMgmtContactTimeData(final JSONObject jsonObject) {
        this.logger = Logger.getLogger("MDMEnrollment");
        this.resourceID = jsonObject.getLong("RESOURCE_ID");
        this.lastConact = jsonObject.getLong("LAST_CONTACT_TIME");
    }
    
    @Override
    public void processData() {
        try {
            if (ModernDeviceUtil.isModernManagementCapableResource(this.resourceID)) {
                final JSONObject jsonObject = this.toJSON();
                ModernMgmtDeviceForEnrollmentHandler.addModernDetails(jsonObject, this.resourceID);
                jsonObject.put("LAST_CONTACT_TIME", (Object)this.lastConact);
                jsonObject.put("RESOURCE_ID", (Object)this.resourceID);
                this.logger.log(Level.FINE, "Data Being posted to Legacy management for last contact update is  {0}", this.toJSON());
                MDMApiFactoryProvider.getMDMModernMgmtAPI().deviceListener(DeviceAction.UPDATE_LAST_CONTACT, jsonObject);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "failed to update last conatct time to legacy server ", e);
        }
    }
    
    @Override
    public JSONObject toJSON() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("LAST_CONTACT_TIME", (Object)this.lastConact);
        jsonObject.put("RESOURCE_ID", (Object)this.resourceID);
        return jsonObject;
    }
}
