package com.me.ems.framework.reports.api.v1.service;

import java.util.List;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.ems.framework.common.api.utils.APIException;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.webclient.reports.SYMReportUtil;
import com.adventnet.i18n.I18N;
import com.me.ems.framework.common.api.v1.model.Node;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.logging.Logger;

public class CustomReportsService
{
    public static Logger logger;
    
    public Node<String> getAvailableCustomReports(final User dcUser, final Long customerID) throws APIException {
        final Node<String> customReports = new Node<String>();
        try {
            customReports.setId("customReports");
            customReports.setLabel(I18N.getMsg("dc.rep.cr.CUSTOM_REPORTS", new Object[0]));
            customReports.addProperty("reportType", String.valueOf(2));
            final SelectQuery crViewQuery = SYMReportUtil.getCRSaveDetailsQuery(dcUser.getUserID(), customerID, dcUser.isAdminUser(), false);
            final DataObject customReportsDO = SyMUtil.getPersistence().get(crViewQuery);
            final Iterator crIterator = customReportsDO.getRows("CRSaveViewDetails");
            while (crIterator.hasNext()) {
                final Row crRow = crIterator.next();
                final Node<String> oneReport = new Node<String>();
                final Criteria userCriteria = new Criteria(Column.getColumn("AaaUser", "USER_ID"), crRow.get("USER_ID"), 0);
                final String owner = (String)customReportsDO.getValue("AaaUser", "FIRST_NAME", userCriteria);
                final long milli = (long)crRow.get("LAST_MODIFIED_TIME");
                final String modifiedDate = DateTimeUtil.longdateToString(milli, dcUser.getTimeFormat());
                final Criteria crSaveIDCrit = new Criteria(Column.getColumn("CRViewParams", "CRSAVEVIEW_ID"), crRow.get("CRSAVEVIEW_ID"), 0);
                final String actionURL = (String)customReportsDO.getValue("CRViewParams", "ACTION_URL", crSaveIDCrit);
                oneReport.setLabel((String)crRow.get("DISPLAY_CRVIEWNAME"));
                final Long crSaveViewID = (Long)crRow.get("CRSAVEVIEW_ID");
                oneReport.setId(String.valueOf(crSaveViewID));
                oneReport.addProperty("viewName", crRow.get("CRVIEWNAME"));
                oneReport.addProperty("description", crRow.get("CRVIEW_DESCRIPTION"));
                oneReport.addProperty("lastModified", modifiedDate);
                oneReport.addProperty("owner", owner);
                oneReport.addProperty("expandable", false);
                oneReport.addProperty("selectable", true);
                oneReport.addProperty("actionURL", actionURL);
                customReports.addChild(oneReport);
            }
            final List children = customReports.getChildren();
            if (children != null && !children.isEmpty()) {
                customReports.addProperty("expandable", true);
                customReports.addProperty("selectable", true);
            }
            else {
                customReports.addProperty("expandable", false);
                customReports.addProperty("selectable", false);
            }
        }
        catch (final Exception ex) {
            CustomReportsService.logger.log(Level.SEVERE, "Exception while fetching available custom reports", ex);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
        return customReports;
    }
    
    static {
        CustomReportsService.logger = Logger.getLogger("CustomReportLogger");
    }
}
