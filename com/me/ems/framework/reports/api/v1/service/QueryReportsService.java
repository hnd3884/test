package com.me.ems.framework.reports.api.v1.service;

import com.me.ems.framework.common.api.utils.APIException;
import javax.ws.rs.core.Response;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.List;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
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

public class QueryReportsService
{
    private static Logger logger;
    
    public Node getAvailableQueryReports(final User dcUser, final Long customerID) {
        Node<String> queryReports = new Node<String>();
        try {
            queryReports.setId("queryReports");
            queryReports.setLabel(I18N.getMsg("dc.rep.scheduleReport.query_reports", new Object[0]));
            queryReports.addProperty("reportType", String.valueOf(3));
            final SelectQuery viewQuery = SYMReportUtil.getCRSaveDetailsQuery(dcUser.getUserID(), customerID, dcUser.isAdminUser(), true);
            final DataObject queryReportDO = SyMUtil.getPersistence().get(viewQuery);
            final Iterator queryIter = queryReportDO.getRows("CRSaveViewDetails");
            while (queryIter.hasNext()) {
                final Row qrRow = queryIter.next();
                final Node<String> oneReport = new Node<String>();
                final Criteria userCriteria = new Criteria(Column.getColumn("AaaUser", "USER_ID"), qrRow.get("USER_ID"), 0);
                final String owner = (String)queryReportDO.getValue("AaaUser", "FIRST_NAME", userCriteria);
                final long milli = (long)qrRow.get("LAST_MODIFIED_TIME");
                final String modifiedDate = DateTimeUtil.longdateToString(milli, dcUser.getTimeFormat());
                oneReport.setLabel((String)qrRow.get("DISPLAY_CRVIEWNAME"));
                final Long id = (Long)qrRow.get("CRSAVEVIEW_ID");
                oneReport.setId(String.valueOf(id));
                oneReport.addProperty("viewName", qrRow.get("CRVIEWNAME"));
                oneReport.addProperty("description", qrRow.get("CRVIEW_DESCRIPTION"));
                oneReport.addProperty("lastModified", modifiedDate);
                oneReport.addProperty("owner", owner);
                oneReport.addProperty("expandable", false);
                oneReport.addProperty("selectable", true);
                queryReports.addChild(oneReport);
            }
            final List children = queryReports.getChildren();
            if (children != null && !children.isEmpty()) {
                queryReports.addProperty("expandable", true);
                queryReports.addProperty("selectable", true);
            }
            else {
                queryReports.addProperty("expandable", false);
                queryReports.addProperty("selectable", false);
            }
        }
        catch (final Exception ex) {
            QueryReportsService.logger.log(Level.WARNING, "Exception while getting all query reports for userID: " + dcUser.getUserID() + " customerID: " + customerID, ex);
            queryReports = null;
        }
        return queryReports;
    }
    
    public boolean showQueryReport(final User dcUser) throws APIException {
        final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
        if (isMSP && !dcUser.isAdminUser()) {
            QueryReportsService.logger.log(Level.WARNING, "Not an MSP admin user. userID: " + dcUser.getUserID());
            throw new APIException(Response.Status.EXPECTATION_FAILED, "REP0008", "ems.rest.authentication.unauthorized");
        }
        return true;
    }
    
    static {
        QueryReportsService.logger = Logger.getLogger("QueryExecutorLogger");
    }
}
