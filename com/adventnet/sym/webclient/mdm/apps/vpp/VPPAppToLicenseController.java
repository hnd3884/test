package com.adventnet.sym.webclient.mdm.apps.vpp;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.util.DBUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.components.form.web.AjaxFormController;

public class VPPAppToLicenseController extends AjaxFormController
{
    public void processPostRendering(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        super.processPostRendering(viewCtx, request, response);
    }
    
    public String processPreRendering(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response, final String viewUrl) throws Exception {
        try {
            super.processPreRendering(viewCtx, request, response, viewUrl);
            viewCtx.getRequest().setAttribute("emailId", (Object)request.getParameter("emailId"));
            viewCtx.getRequest().setAttribute("licenseCount", (Object)request.getParameter("licenseCount"));
            viewCtx.getRequest().setAttribute("vppUserId", (Object)request.getParameter("vppUserId"));
            final long userId = Long.parseLong(request.getParameter("managedUserId"));
            viewCtx.getRequest().setAttribute("userName", DBUtil.getValueFromDB("Resource", "RESOURCE_ID", (Object)userId, "NAME"));
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedUserToDevice"));
            sQuery.addJoin(new Join("ManagedUserToDevice", "Resource", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            sQuery.addJoin(new Join("Resource", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
            sQuery.setCriteria(new Criteria(new Column("ManagedUserToDevice", "MANAGED_USER_ID"), (Object)userId, 0));
            sQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "*"));
            final DataObject DO = MDMUtil.getPersistence().get(sQuery);
            String devices = "--";
            if (!DO.isEmpty()) {
                final Iterator item = DO.getRows("ManagedDeviceExtn");
                devices = "";
                while (item.hasNext()) {
                    final Row mgRow = item.next();
                    if (devices.equals("")) {
                        devices = (String)mgRow.get("NAME");
                    }
                    else {
                        devices = devices + ", " + mgRow.get("NAME");
                    }
                }
            }
            viewCtx.getRequest().setAttribute("deviceName", (Object)devices);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return viewUrl;
    }
}
