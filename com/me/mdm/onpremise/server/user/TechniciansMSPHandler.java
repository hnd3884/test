package com.me.mdm.onpremise.server.user;

import org.json.JSONArray;
import java.util.List;
import java.util.Arrays;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import org.json.JSONObject;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import java.util.logging.Logger;

public class TechniciansMSPHandler extends TechniciansHandler
{
    private static Logger logger;
    
    @Override
    public Criteria getBaseUserCriteria(final Long customerId) {
        final Criteria umRoleCriteria = new Criteria(Column.getColumn("UMRole", "UM_ROLE_ID"), (Object)null, 1);
        final Criteria aaaLoginCriteia = new Criteria(Column.getColumn("AaaLogin", "LOGIN_ID"), (Object)null, 1);
        final Criteria customerCriteria2 = new Criteria(Column.getColumn("LoginUserCustomerMapping", "CUSTOMER_ID"), (Object)null, 0);
        return umRoleCriteria.and(aaaLoginCriteia).or(customerCriteria2);
    }
    
    @Override
    protected String validateCustomerId(final JSONObject apiRequest, final String roleId) throws Exception {
        String customerIds = "";
        final JSONObject body = apiRequest.getJSONObject("msg_body");
        if (!roleId.equals(DMUserHandler.getRoleID("Administrator"))) {
            final JSONArray customerIdList = body.optJSONArray("customer_id_list");
            if (customerIdList == null || customerIdList.length() == 0) {
                throw new APIHTTPException("COM0014", new Object[0]);
            }
            customerIds = JSONUtil.getInstance().convertJSONArrayToString(customerIdList);
        }
        else {
            final Long[] customerId = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
            customerIds = JSONUtil.getInstance().convertJSONArrayToString(JSONUtil.getInstance().convertListToJSONArray((List)Arrays.asList(customerId)));
        }
        return customerIds;
    }
    
    static {
        TechniciansMSPHandler.logger = Logger.getLogger("UserManagementLogger");
    }
}
