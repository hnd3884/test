package com.adventnet.sym.webclient.mdm.inv;

import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.ArrayList;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.List;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

public class InventoryRoleCheckUtil
{
    public Logger mdmLogger;
    static InventoryRoleCheckUtil util;
    
    public InventoryRoleCheckUtil() {
        this.mdmLogger = Logger.getLogger("MDMLogger");
    }
    
    public static InventoryRoleCheckUtil getInstance() {
        if (InventoryRoleCheckUtil.util == null) {
            InventoryRoleCheckUtil.util = new InventoryRoleCheckUtil();
        }
        return InventoryRoleCheckUtil.util;
    }
    
    public boolean doesDeviceBelongToCustomer(final HttpServletRequest request, final long resourceId) {
        final Long customerIdInCookie = MSPWebClientUtil.getCustomerID(request);
        return this.doesDeviceBelongToCustomer(customerIdInCookie, resourceId);
    }
    
    public List getCustomerBelongDevices(final HttpServletRequest request, final List resourceId) {
        final Long customerIdInCookie = MSPWebClientUtil.getCustomerID(request);
        return this.getCustomerBelongDevices(customerIdInCookie, resourceId);
    }
    
    public List getCustomerBelongDevices(final Long customerId, final List resourceIds) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            final Criteria resCriteria = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)resourceIds.toArray(), 8);
            selectQuery.setCriteria(resCriteria.and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0)));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (dataObject.size("Resource") > 0) {
                return DBUtil.getColumnValuesAsList(dataObject.getRows("Resource"), "RESOURCE_ID");
            }
        }
        catch (final Exception e) {
            this.mdmLogger.log(Level.SEVERE, "Exception while checking user access ", e);
        }
        return new ArrayList();
    }
    
    public boolean doesDeviceBelongToCustomer(final Long customerId, final long resourceId) {
        try {
            final Long customerIDForResID = CustomerInfoUtil.getInstance().getCustomerIDForResID(Long.valueOf(resourceId));
            if (customerId.equals(customerIDForResID)) {
                return true;
            }
            this.mdmLogger.log(Level.INFO, "Current user {0} does not have required permission to access {1}", new Object[] { customerId, resourceId });
            return false;
        }
        catch (final Exception e) {
            this.mdmLogger.log(Level.INFO, "Error fetching the logged in user ", e);
            return false;
        }
    }
    
    static {
        InventoryRoleCheckUtil.util = null;
    }
}
