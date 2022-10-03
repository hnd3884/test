package com.me.mdm.server.reports;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import java.util.LinkedHashMap;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.webclient.reports.SYMReportUtil;

public class MDMReportUtil extends SYMReportUtil
{
    private static final Logger logger;
    
    public static LinkedHashMap getCRViewList(final HttpServletRequest request) throws SyMException, Exception {
        if (!SYMClientUtil.isUserInAdminRole(request)) {
            return getCRViewList(ApiFactoryProvider.getAuthUtilAccessAPI().getUserID());
        }
        return getCRViewList();
    }
    
    public static void setViewList(final HttpServletRequest request) throws SyMException {
        try {
            MDMReportUtil.logger.log(Level.INFO, "***Inside the set view List***");
            final Hashtable reportID = getReportNameList();
            final Map<String, LinkedHashMap> viewList = getViewList(reportID, (String)null, viewListWithSubCategoryName());
            for (final Map.Entry<String, LinkedHashMap> entry : viewList.entrySet()) {
                request.setAttribute((String)entry.getKey(), (Object)entry.getValue());
            }
        }
        catch (final Exception e) {
            MDMReportUtil.logger.log(Level.WARNING, "Exception while setViewList for Report...", e);
        }
    }
    
    public static Hashtable getReportNameList() {
        final Hashtable reportName = new Hashtable();
        reportName.put(40000, "MDM_LIST");
        return reportName;
    }
    
    public static String getReportParamter(final String key) {
        try {
            final Criteria criteria = new Criteria(Column.getColumn("ReportParams", "PARAM_NAME"), (Object)key, 0);
            final DataObject reportParams = SyMUtil.getPersistence().get("ReportParams", criteria);
            final Row reportParamsRow = reportParams.getRow("ReportParams");
            if (reportParamsRow == null) {
                return null;
            }
            final String paramValue = (String)reportParamsRow.get("PARAM_VALUE");
            return paramValue;
        }
        catch (final Exception ex) {
            MDMReportUtil.logger.log(Level.WARNING, ex, () -> "Caught exception while retrieving Report Parameter:" + s + " from DB.");
            return null;
        }
    }
    
    public static void updateReportParameter(final String paramName, final String paramValue) {
        try {
            final Criteria criteria = new Criteria(Column.getColumn("ReportParams", "PARAM_NAME"), (Object)paramName, 0);
            final DataObject reportParams = SyMUtil.getPersistence().get("ReportParams", criteria);
            Row reportParamsRow = reportParams.getRow("ReportParams");
            if (reportParamsRow == null) {
                reportParamsRow = new Row("ReportParams");
                reportParamsRow.set("PARAM_NAME", (Object)paramName);
                reportParamsRow.set("PARAM_VALUE", (Object)paramValue);
                reportParams.addRow(reportParamsRow);
                MDMReportUtil.logger.log(Level.INFO, "Report Parameter added in DB  param name: {0}  param value: {1}", new Object[] { paramName, paramValue });
            }
            else {
                reportParamsRow.set("PARAM_VALUE", (Object)paramValue);
                reportParams.updateRow(reportParamsRow);
                MDMReportUtil.logger.log(Level.INFO, "Report Parameter updated in DB:- param name: {0}  param value: {1}", new Object[] { paramName, paramValue });
            }
            SyMUtil.getPersistence().update(reportParams);
        }
        catch (final Exception ex) {
            MDMReportUtil.logger.log(Level.SEVERE, ex, () -> "Caught exception while updating the Report Parameter:" + s + " in DB.");
        }
    }
    
    public static void incrementParameterValue(final String parameter) {
        int count = 1;
        final String usageCount = getReportParamter(parameter);
        if (usageCount != null) {
            count = Integer.parseInt(usageCount);
            ++count;
        }
        updateReportParameter(parameter, String.valueOf(count));
    }
    
    public static Row getCRDetailsFromViewName(String viewName, final Long customerId) {
        Row crViewRow = null;
        try {
            viewName = viewName.toUpperCase();
            final SelectQuery qu = (SelectQuery)new SelectQueryImpl(Table.getTable("CRSaveViewDetails"));
            qu.addJoin(new Join("CRSaveViewDetails", "CRToCustomerRel", new String[] { "CRSAVEVIEW_ID" }, new String[] { "CR_VIEW_ID" }, 2));
            qu.addSelectColumn(Column.getColumn((String)null, "*"));
            final Criteria cri = new Criteria(Column.getColumn("CRSaveViewDetails", "CRVIEWNAME"), (Object)viewName, 0).and(new Criteria(Column.getColumn("CRToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0));
            qu.setCriteria(cri);
            final DataObject dobj = DataAccess.get(qu);
            crViewRow = dobj.getFirstRow("CRSaveViewDetails");
        }
        catch (final Exception e) {
            MDMReportUtil.logger.log(Level.INFO, "Exception when getting CRDetails from viewName ", e);
        }
        return crViewRow;
    }
    
    public static Boolean customReportBelongsToUser(final Long crViewID) {
        final Boolean crBelongsToUser = false;
        try {
            final Long userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            final Criteria customCriteria = new Criteria(new Column("CRSaveViewDetails", "CRSAVEVIEW_ID"), (Object)crViewID, 0);
            final Criteria userCriteria = new Criteria(new Column("CRSaveViewDetails", "USER_ID"), (Object)userID, 0);
            final DataObject taskDO = SyMUtil.getPersistence().get("CRSaveViewDetails", customCriteria.and(userCriteria));
            if (taskDO != null && !taskDO.isEmpty()) {
                return true;
            }
        }
        catch (final Exception e) {
            MDMReportUtil.logger.log(Level.INFO, "Exception when checking Custom Report and User Relation ", e);
        }
        return crBelongsToUser;
    }
    
    static {
        logger = Logger.getLogger(MDMReportUtil.class.getName());
    }
}
