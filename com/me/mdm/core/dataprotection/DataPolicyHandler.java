package com.me.mdm.core.dataprotection;

import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.core.dataprotection.data.EnterprisePolicy;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;

public abstract class DataPolicyHandler
{
    public static final String POLICY_ID_API = "policy_id";
    public static final String POLICY_NAME_API = "policy_name";
    
    public JSONObject createPolicy(final JSONObject jsonObject) throws Exception {
        final JSONObject msgBody = jsonObject.getJSONObject("msg_body");
        final Long userID = APIUtil.getUserID(jsonObject);
        final EnterprisePolicy enterprisePolicy = this.convertApiJsonToPolicy(msgBody);
        enterprisePolicy.createdBy = userID;
        enterprisePolicy.lastModifiedBy = userID;
        enterprisePolicy.createdTime = System.currentTimeMillis();
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
        final EnterprisePolicy enterprisePolicy = new EnterprisePolicy(policyID, customerID);
        if (enterprisePolicy.error != 0) {
            throw new APIHTTPException("COM0008", new Object[] { "policy_id : " + policyID });
        }
        return this.convertEnterprisePolicyToApiJSON(enterprisePolicy);
    }
    
    protected abstract EnterprisePolicy convertApiJsonToPolicy(final JSONObject p0) throws Exception;
    
    protected abstract JSONObject convertEnterprisePolicyToApiJSON(final EnterprisePolicy p0) throws Exception;
}
