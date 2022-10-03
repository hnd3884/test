package com.me.mdm.core.lockdown;

import com.me.mdm.core.lockdown.data.LockdownApplication;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.core.lockdown.data.LockdownPolicy;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;

public abstract class LockdownHandler
{
    public JSONObject createPolicy(final JSONObject jsonObject) throws Exception {
        final JSONObject msgBody = jsonObject.getJSONObject("msg_body");
        final Long userID = APIUtil.getUserID(jsonObject);
        final LockdownPolicy enterprisePolicy = this.convertApiJsonToPolicy(msgBody);
        enterprisePolicy.createdBy = userID;
        enterprisePolicy.lastModifiedBy = userID;
        enterprisePolicy.createdTime = System.currentTimeMillis();
        enterprisePolicy.modifiedTime = System.currentTimeMillis();
        enterprisePolicy.customerID = APIUtil.getCustomerID(jsonObject);
        enterprisePolicy.createAndPersistPolicy();
        final JSONObject response = new JSONObject();
        response.put("policy_id", (Object)enterprisePolicy.policyID);
        response.put("policy_name", (Object)enterprisePolicy.policyName);
        return response;
    }
    
    public JSONObject getPolicy(final JSONObject jsonObject) throws Exception {
        final Long policyID = APIUtil.getResourceID(jsonObject, "policie_id");
        final Long customerID = APIUtil.getCustomerID(jsonObject);
        final LockdownPolicy enterprisePolicy = new LockdownPolicy(policyID, customerID);
        if (enterprisePolicy.error != 0) {
            throw new APIHTTPException("COM0008", new Object[] { "policy_id : " + policyID });
        }
        return this.convertEnterprisePolicyToApiJSON(enterprisePolicy);
    }
    
    protected abstract JSONObject convertEnterprisePolicyToApiJSON(final LockdownPolicy p0) throws JSONException, DataAccessException;
    
    protected abstract LockdownPolicy convertApiJsonToPolicy(final JSONObject p0);
    
    public static int getAppType(final String identifier) {
        Integer appType = LockdownApplication.MODERN_APP_TYPE;
        if (identifier.matches("[a-zA-Z0-9]*(\\.[a-zA-Z0-9]*)*_[a-zA-Z0-9]*![a-zA-Z0-9\\.]*")) {
            appType = LockdownApplication.MODERN_APP_TYPE;
        }
        else {
            appType = LockdownApplication.LEGACY_APP_TYPE;
        }
        return appType;
    }
}
