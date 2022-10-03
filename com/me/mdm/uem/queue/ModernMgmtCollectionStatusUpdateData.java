package com.me.mdm.uem.queue;

import com.me.mdm.uem.actionconstants.ConfigurationAction;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.logging.Level;
import com.me.mdm.uem.ModernCollectionUtil;
import com.me.mdm.uem.ModernDeviceUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class ModernMgmtCollectionStatusUpdateData implements ModernMgmtOperationData
{
    Long collectionID;
    Long resourceID;
    String remarks;
    Integer status;
    Logger logger;
    
    public ModernMgmtCollectionStatusUpdateData(final Long collectionID, final Long resourceID, final String remarks, final Integer status) {
        this.logger = Logger.getLogger("MDMConfigLogger");
        this.collectionID = collectionID;
        this.remarks = remarks;
        this.resourceID = resourceID;
        this.status = status;
    }
    
    ModernMgmtCollectionStatusUpdateData(final JSONObject jsonObject) {
        this.logger = Logger.getLogger("MDMConfigLogger");
        this.resourceID = ((Number)jsonObject.get("RESOURCE_ID")).longValue();
        this.collectionID = ((Number)jsonObject.get("COLLECTION_ID")).longValue();
        this.status = (Integer)jsonObject.get("STATUS");
        this.remarks = (String)jsonObject.get("REMARKS");
    }
    
    @Override
    public void processData() {
        try {
            if (ModernDeviceUtil.isModernManagementCapableResource(this.resourceID) && ModernCollectionUtil.isModernCollection(this.collectionID)) {
                this.logger.log(Level.INFO, "[Modern][Configuration] : Posting status update to legacy : {0}", this.toJSON());
                MDMApiFactoryProvider.getMDMModernMgmtAPI().configurationListener(ConfigurationAction.POST_CONFIGURATION_STATUS_UPDATE, this.toJSON());
                this.logger.log(Level.INFO, "[Modern][Configuration] : Posting status update to legacy success");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, e, () -> "[Modern][Configuration] : Error when posting data to legact for collection " + this.toJSON().toString());
        }
    }
    
    @Override
    public JSONObject toJSON() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("RESOURCE_ID", (Object)this.resourceID);
        jsonObject.put("COLLECTION_ID", (Object)this.collectionID);
        jsonObject.put("STATUS", (Object)this.status);
        jsonObject.put("REMARKS", (Object)this.remarks);
        return jsonObject;
    }
}
