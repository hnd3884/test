package com.me.mdm.server.msp.sync;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.logging.Logger;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;

public abstract class BaseConfigurationsSyncEngine
{
    public Long customerId;
    public Long childCustomerId;
    public Integer platform;
    public Integer profileType;
    public JSONObject qData;
    public DataObject parentProfileDO;
    public DataObject childProfileDO;
    public Long modifiedByUser;
    public Long aaaLoginId;
    public String userName;
    protected static Logger logger;
    
    BaseConfigurationsSyncEngine(final DCQueueData dcQueueData) {
        try {
            this.qData = new JSONObject(dcQueueData.queueData.toString());
            this.customerId = this.qData.getLong("CUSTOMER_ID");
            this.childCustomerId = this.qData.optLong("childCustomerId", -1L);
            this.platform = this.qData.optInt("PLATFORM_TYPE");
            this.profileType = this.qData.optInt("PROFILE_TYPE");
            this.modifiedByUser = this.qData.optLong("LAST_MODIFIED_BY");
            this.aaaLoginId = this.qData.optLong("LOGIN_ID");
            this.userName = DMUserHandler.getUserNameFromUserID(this.modifiedByUser);
            CustomerInfoThreadLocal.setSummaryPage("false");
            CustomerInfoThreadLocal.setIsClientCall("true");
            CustomerInfoThreadLocal.setSkipCustomerFilter("true");
            ApiFactoryProvider.getAuthUtilAccessAPI().setUserCredential(this.userName, "System", DMUserHandler.getDCUserDomain(this.aaaLoginId), this.modifiedByUser);
        }
        catch (final Exception ex) {
            BaseConfigurationsSyncEngine.logger.log(Level.SEVERE, "Exception in initializing BaseConfigurationsSyncEngine()", ex);
        }
    }
    
    public abstract JSONObject getChildSpecificUVH(final Long p0) throws Exception;
    
    public abstract void setParentDO() throws Exception;
    
    public abstract void sync();
    
    static {
        BaseConfigurationsSyncEngine.logger = Logger.getLogger("MDMConfigLogger");
    }
}
