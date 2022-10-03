package com.me.mdm.server.apps.android.afw;

import org.json.JSONArray;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.apps.android.afw.usermgmt.GoogleManagedAccountHandler;
import com.me.mdm.server.device.DeviceFacade;
import com.me.mdm.api.APIUtil;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AFWAccountRetryHandler
{
    private Logger logger;
    
    public AFWAccountRetryHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public JSONObject handleRetryRequest(final JSONObject request) throws Exception {
        try {
            return this.handleRetryRequest(request, false);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception when retrying to add managed account", e);
            throw e;
        }
    }
    
    public JSONObject handleRetryRequest(final JSONObject request, final boolean forceRetry) throws Exception {
        try {
            final Long customerId = APIUtil.getCustomerID(request);
            final Long deviceId = APIUtil.getResourceID(request, "device_id");
            new DeviceFacade().validateIfDeviceExists(deviceId, customerId);
            new GoogleManagedAccountHandler().validateManagedAccountActions(customerId);
            this.validateRetryRequest(deviceId, customerId, forceRetry);
            this.retryAccountAddition(deviceId);
            final JSONObject response = new JSONObject();
            response.put("status", (Object)"success");
            return response;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in  retrying to add managed account", e);
            throw e;
        }
    }
    
    private void validateRetryRequest(final Long deviceId, final Long customerId, final boolean forceRetry) throws Exception {
        if (!forceRetry) {
            final JSONObject device = this.getAccountStatusForDevice(deviceId, customerId);
            if (!device.getBoolean("retry_allowed")) {
                if (device.getInt("account_status") == 2) {
                    throw new APIHTTPException("COM0010", new Object[] { "Managed account already added for the resource. No need to retry" });
                }
                throw new APIHTTPException("COM0015", new Object[] { "Managed account already failed due to non retry-able error" });
            }
        }
    }
    
    public JSONObject getAccountStatusForDevice(final Long deviceId, final Long customerId) throws Exception {
        try {
            final JSONObject filterParams = new JSONObject();
            filterParams.put("resource_id", (Object)deviceId);
            final AFWAccountStatusHandler handler = new AFWAccountStatusHandler();
            SelectQuery dataQuery = handler.getAccountDetailsQuery(filterParams, customerId);
            dataQuery = handler.addSelectColumnsForData(dataQuery);
            final DataObject dO = MDMUtil.getPersistence().get(dataQuery);
            final JSONArray devicesStatus = handler.getAFWAccountDetails(dO);
            if (devicesStatus.length() > 1) {
                throw new APIHTTPException("COM0004", new Object[0]);
            }
            return devicesStatus.optJSONObject(0);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception when getting AFW account status", e);
            throw e;
        }
    }
    
    public void retryAccountAddition(final Long resourceId) throws Exception {
        new GoogleManagedAccountHandler().addAFWAccountAdditionCmd(resourceId);
    }
}
