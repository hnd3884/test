package com.me.mdm.server.apps.android.afw;

import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import java.util.logging.Level;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.server.apps.api.model.AppListModel;
import com.me.mdm.server.apps.businessstore.service.AndroidBusinessStoreService;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreUtil;
import com.me.mdm.server.apps.businessstore.BusinessStoreSyncConstants;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class PlaystoreAppsAutoApprover
{
    private static Logger logger;
    public static final String SUCCESS_COUNT = "AFW_AUTO_APPROVE_SUCCESS";
    public static final String FAILURE_COUNT = "AFW_AUTO_APPROVE_FAILURE";
    
    public JSONObject approveNonAFWApps(final JSONObject options) {
        final Long customerId = APIUtil.getCustomerID(options);
        final Long userId = APIUtil.getUserID(options);
        final JSONObject response = new JSONObject();
        try {
            if (!GoogleForWorkSettings.isAFWSettingsConfigured(customerId)) {
                throw new APIHTTPException("COM0015", new Object[] { "AFW Not Configured" });
            }
            final Long businessStoreID = MDBusinessStoreUtil.getBusinessStoreID(customerId, BusinessStoreSyncConstants.BS_SERVICE_AFW);
            final AndroidBusinessStoreService bService = new AndroidBusinessStoreService();
            final AppListModel appListModel = new AppListModel();
            appListModel.setIdentifierList(new AppFacade().getAllNonPortalStoreAppIdentifiers(customerId, 2));
            bService.addOrUpdateBusinessStoreApp(userId, businessStoreID, customerId, appListModel);
        }
        catch (final Exception e) {
            PlaystoreAppsAutoApprover.logger.log(Level.SEVERE, "Exception while approving non afw apps ", e);
            throw new APIHTTPException("COM0004", new Object[] { e.getMessage() });
        }
        return response;
    }
    
    public void persistAutoApprovalStatus(final Long cusomerId, Integer successCount, final Integer totalCount) {
        try {
            final CustomerParamsHandler handler = CustomerParamsHandler.getInstance();
            final Integer presentSuccessCount = Integer.parseInt(handler.getParameterValue("AFW_AUTO_APPROVE_SUCCESS", (long)cusomerId));
            final Integer failureCount = successCount - totalCount;
            successCount += presentSuccessCount;
            handler.addOrUpdateParameter("AFW_AUTO_APPROVE_SUCCESS", successCount.toString(), (long)cusomerId);
            handler.addOrUpdateParameter("AFW_AUTO_APPROVE_FAILURE", failureCount.toString(), (long)cusomerId);
        }
        catch (final Exception e) {
            PlaystoreAppsAutoApprover.logger.log(Level.SEVERE, "Exception while persisting AutoApprovalStatus ", e);
        }
    }
    
    static {
        PlaystoreAppsAutoApprover.logger = Logger.getLogger("MDMConfigLogger");
    }
}
