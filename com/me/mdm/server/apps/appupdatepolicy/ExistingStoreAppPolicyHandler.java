package com.me.mdm.server.apps.appupdatepolicy;

import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.device.api.model.apps.AppUpdatePolicyModel;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.DeleteQuery;
import java.util.List;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.DeleteQueryImpl;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExistingStoreAppPolicyHandler
{
    private static ExistingStoreAppPolicyHandler existingStoreAppPolicyHandler;
    private Logger mdmConfigLogger;
    
    public ExistingStoreAppPolicyHandler() {
        this.mdmConfigLogger = Logger.getLogger("MDMConfigLogger");
    }
    
    public static ExistingStoreAppPolicyHandler getInstance() {
        if (ExistingStoreAppPolicyHandler.existingStoreAppPolicyHandler == null) {
            ExistingStoreAppPolicyHandler.existingStoreAppPolicyHandler = new ExistingStoreAppPolicyHandler();
        }
        return ExistingStoreAppPolicyHandler.existingStoreAppPolicyHandler;
    }
    
    public void deleteExistingStoreAppPolicy(final Long customerId) throws DataAccessException {
        final List<Long> storeAppPolicyIds = AppUpdatePolicyDBHandler.getInstance().getExistingStoreAppPolicies(customerId);
        this.mdmConfigLogger.log(Level.INFO, "Deleting existing store app policies for customer {0} policyIds {1}", new Object[] { customerId, storeAppPolicyIds });
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("AutoAppUpdateConfigDetails");
        deleteQuery.setCriteria(new Criteria(Column.getColumn("AutoAppUpdateConfigDetails", "APP_UPDATE_CONF_ID"), (Object)storeAppPolicyIds.toArray(), 8));
        DataAccess.delete(deleteQuery);
    }
    
    public void addStoreAppPolicy(final Long customerId) {
        final AppUpdatePolicyModel appUpdatePolicyModel = new AppUpdatePolicyModel();
        appUpdatePolicyModel.setCustomerId(customerId);
        final Long userId = MDMUtil.getAdminUserId();
        appUpdatePolicyModel.setUserId(userId);
        final Long loginId = DMUserHandler.getLoginIdForUserId(userId);
        appUpdatePolicyModel.setLogInId(loginId);
        appUpdatePolicyModel.setUserName(DMUserHandler.getUserName(loginId));
        appUpdatePolicyModel.setPolicyName("Store Apps - update policy");
        appUpdatePolicyModel.setDistributionType(AppUpdatePolicyConstants.DistributionType.AUTOMATIC_DISTRIBUTION);
        appUpdatePolicyModel.setPolicyType(AppUpdatePolicyConstants.PolicyType.ANYTIME);
        appUpdatePolicyModel.setIsAllApps(Boolean.TRUE);
        this.mdmConfigLogger.log(Level.INFO, "Adding default store app policy for customer {0} with props {1}", new Object[] { customerId, appUpdatePolicyModel });
        new AppUpdatePolicyService().addAppUpdatePolicy(appUpdatePolicyModel);
        this.mdmConfigLogger.log(Level.INFO, "Default store app policy successfully created for customer {0} with props {1}", new Object[] { customerId, appUpdatePolicyModel });
    }
    
    static {
        ExistingStoreAppPolicyHandler.existingStoreAppPolicyHandler = null;
    }
}
