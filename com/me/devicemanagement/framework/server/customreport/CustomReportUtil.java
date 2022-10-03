package com.me.devicemanagement.framework.server.customreport;

import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomReportUtil
{
    protected static Logger logger;
    
    public int deleteExistingCR(final String crSaveViewName, final Long customerID, final Boolean setMessage) {
        String viewName = null;
        try {
            CustomReportUtil.logger.log(Level.INFO, "This Report  " + crSaveViewName + "is Going to delete");
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CRSaveViewDetails"));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            final Criteria c1 = new Criteria(Column.getColumn("CRSaveViewDetails", "CRVIEWNAME"), (Object)crSaveViewName, 0);
            selectQuery.setCriteria(c1);
            final DataObject data = DataAccess.get(selectQuery);
            if (!data.isEmpty()) {
                final Row row = data.getRow("CRSaveViewDetails");
                if (row != null) {
                    final String queryID = row.get("QUERYID") + "";
                    final String viewID = row.get("VIEWID") + "";
                    viewName = row.get("CRVIEWNAME") + "";
                    CustomReportUtil.logger.log(Level.INFO, "In Deleteting existing VIEW NAME is " + viewName);
                    final Criteria deleCri = new Criteria(Column.getColumn("SelectQuery", "QUERYID"), (Object)new Long(queryID), 0);
                    DataAccess.delete(deleCri);
                    final Criteria deleCriView = new Criteria(Column.getColumn("ViewConfiguration", "VIEWNAME_NO"), (Object)new Long(viewID), 0);
                    DataAccess.delete(deleCriView);
                    final Criteria deleCri2 = new Criteria(Column.getColumn("ACColumnConfigurationList", "NAME"), (Object)viewName, 0);
                    DataAccess.delete(deleCri2);
                    final Criteria delcriteria = new Criteria(Column.getColumn("CRSaveViewDetails", "CRVIEWNAME"), (Object)crSaveViewName, 0);
                    DataAccess.delete(delcriteria);
                    final int code;
                    if (setMessage && (code = this.messageSettings(customerID)) == 2001) {
                        return code;
                    }
                }
            }
            return 1104;
        }
        catch (final Exception ee) {
            CustomReportUtil.logger.log(Level.WARNING, "Exception while deleting temp customReport :", ee);
            return 1105;
        }
    }
    
    private int messageSettings(final Long customerID) {
        try {
            final Criteria crit = null;
            final DataObject dobj = SyMUtil.getPersistence().get("CRSaveViewDetails", crit);
            if (!dobj.isEmpty()) {
                final Criteria crCrit = new Criteria(Column.getColumn("CRSaveViewDetails", "QR_QUERY"), (Object)null, 0);
                final Row crRow = dobj.getRow("CRSaveViewDetails", crCrit);
                final Criteria queryCrit = new Criteria(Column.getColumn("CRSaveViewDetails", "QR_QUERY"), (Object)null, 1);
                final Row queryRow = dobj.getRow("CRSaveViewDetails", queryCrit);
                if (crRow == null) {
                    MessageProvider.getInstance().unhideMessage("CR_NOT_CREATED", customerID);
                }
                if (queryRow == null) {
                    MessageProvider.getInstance().unhideMessage("QUERY_REPORT_NOT_CREATED", customerID);
                }
            }
            else {
                MessageProvider.getInstance().unhideMessage("CR_NOT_CREATED", customerID);
                MessageProvider.getInstance().unhideMessage("QUERY_REPORT_NOT_CREATED", customerID);
            }
            return 1200;
        }
        catch (final Exception ee) {
            CustomReportUtil.logger.log(Level.WARNING, "Exception while doing Message Settings...", ee);
            return 1201;
        }
    }
    
    static {
        CustomReportUtil.logger = Logger.getLogger("CustomReportLogger");
    }
}
