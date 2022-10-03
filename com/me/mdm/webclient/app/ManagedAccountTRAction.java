package com.me.mdm.webclient.app;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.i18n.I18N;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class ManagedAccountTRAction extends MDMEmberTableRetrieverAction
{
    public Logger logger;
    
    public ManagedAccountTRAction() {
        this.logger = Logger.getLogger(ManagedAccountTRAction.class.getName());
    }
    
    @Override
    public void setCriteria(final SelectQuery query, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            final Long customerId = MSPWebClientUtil.getCustomerID(request);
            final Criteria androidCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)2, 0);
            final Criteria managedCritiera = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            Criteria criteria = androidCriteria.and(managedCritiera).and(customerCriteria);
            final String osCategory = request.getParameter("os_version");
            if (osCategory != null && !"all".equals(osCategory)) {
                final String platformType = I18N.getMsg("dc.mdm.android", new Object[0]);
                final String osVer = osCategory.replace("*", "x");
                request.setAttribute("os_version", (Object)platformType);
                request.setAttribute("osVer", (Object)osVer);
                final String[] osCat = osCategory.split(";");
                for (int i = 0; i < osCat.length; ++i) {
                    final Criteria osCategoryCriteria = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)osCat[i], 2);
                    criteria = criteria.and(osCategoryCriteria);
                }
            }
            final String resourceId = request.getParameter("resource_id");
            if (resourceId != null && !MDMStringUtils.isEmpty(resourceId)) {
                request.setAttribute("resource_id", (Object)resourceId);
                final Criteria resourceC = new Criteria(Column.getColumn("MdDeviceInfo", "RESOURCE_ID", "MDDeviceInfo.RESOURCE_ID"), (Object)Long.parseLong(resourceId), 0);
                criteria = criteria.and(resourceC);
            }
            final String udid = request.getParameter("udid");
            if (udid != null && !MDMStringUtils.isEmpty(udid)) {
                request.setAttribute("udid", (Object)udid);
                final Criteria udidCriteria = new Criteria(Column.getColumn("ManagedDevice", "UDID"), (Object)udid, 12, false);
                criteria = criteria.and(udidCriteria);
            }
            final String deviceName = request.getParameter("device_name");
            if (deviceName != null && !MDMStringUtils.isEmpty(deviceName)) {
                request.setAttribute("device_name", (Object)deviceName);
                final Criteria deviceNameC = new Criteria(Column.getColumn("ManagedDeviceExtn", "NAME"), (Object)deviceName, 12, false);
                criteria = criteria.and(deviceNameC);
            }
            final String status = request.getParameter("status");
            if (status != null && !status.equalsIgnoreCase("all") && !MDMStringUtils.isEmpty(status)) {
                final int statusValue = Integer.parseInt(status);
                Criteria statusCriteria;
                if (statusValue == 10) {
                    final Criteria notSuccessStatusPresentC = new Criteria(Column.getColumn("AFWAccountStatus", "ACCOUNT_STATUS"), (Object)2, 1);
                    final Criteria notSuccessStatusNAC = new Criteria(Column.getColumn("AFWAccountStatus", "ACCOUNT_STATUS"), (Object)null, 0);
                    statusCriteria = notSuccessStatusPresentC.or(notSuccessStatusNAC);
                }
                else {
                    statusCriteria = new Criteria(Column.getColumn("AFWAccountStatus", "ACCOUNT_STATUS"), (Object)statusValue, 0);
                }
                criteria = criteria.and(statusCriteria);
            }
            query.setCriteria(criteria);
            super.setCriteria(query, viewCtx);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in retrieving data for Managed account", e);
        }
    }
}
