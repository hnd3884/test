package com.me.mdm.onpremise.util;

import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.customer.MDMCustomerInfoUtil;
import java.util.HashMap;
import com.me.mdm.server.factory.APIUserDetailsUtil;

public class APIUserDetailsUtilImpl implements APIUserDetailsUtil
{
    public HashMap getAPIUserDetails(final String apiKey, final Long customerID) throws Exception {
        if (customerID != null) {
            final boolean isValid = MDMCustomerInfoUtil.getInstance().isCustomerIDValidForUser(ApiFactoryProvider.getAuthUtilAccessAPI().getUserID(), customerID);
            if (!isValid) {
                throw new APIHTTPException("COM0013", new Object[0]);
            }
        }
        final HashMap parametersList = new HashMap();
        parametersList.put("user_id", ApiFactoryProvider.getAuthUtilAccessAPI().getUserID());
        parametersList.put("user_name", ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName());
        parametersList.put("login_id", ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID());
        return parametersList;
    }
}
