package com.adventnet.sym.webclient.mdm.inv;

import java.util.Hashtable;
import com.me.devicemanagement.framework.webclient.reports.SYMReportUtil;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import com.adventnet.sym.server.mdm.apps.AppSettingsDataHandler;
import com.me.mdm.server.apps.blacklist.BlacklistQueryUtils;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class MDMAppViewTRAction extends MDMEmberTableRetrieverAction
{
    @Override
    protected SelectQuery fetchAndCacheSelectQuery(final ViewContext viewContext) throws Exception {
        final SelectQuery selectQuery = super.fetchAndCacheSelectQuery(viewContext);
        final String unique = viewContext.getUniqueId();
        if (!selectQuery.getTableList().contains(Table.getTable("BlacklistAppCollectionStatus"))) {
            if (!unique.equalsIgnoreCase("MDMDevByAppSummary")) {
                if (!unique.equalsIgnoreCase("MDMDeviceByAppSummary")) {
                    return selectQuery;
                }
            }
            try {
                final Criteria collnCriteria = new Criteria(Column.getColumn("BlacklistAppToCollection", "COLLECTION_ID"), (Object)Column.getColumn("BlacklistAppCollectionStatus", "COLLECTION_ID"), 0);
                final Criteria resCriteria = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)Column.getColumn("BlacklistAppCollectionStatus", "RESOURCE_ID"), 0);
                selectQuery.addJoin(new Join("BlacklistAppToCollection", "BlacklistAppCollectionStatus", collnCriteria.and(resCriteria), 1));
                selectQuery.addSelectColumn(Column.getColumn("BlacklistAppCollectionStatus", "COLLECTION_ID"));
                selectQuery.addSelectColumn(new Column("BlacklistAppCollectionStatus", "STATUS", "BlacklistAppCollectionStatus.STATUS"));
                selectQuery.addSelectColumn(Column.getColumn("BlacklistAppCollectionStatus", "NOTIFIED_COUNT"));
                selectQuery.addSelectColumn(Column.getColumn("BlacklistAppCollectionStatus", "LAST_NOTIFIED_TIME"));
            }
            catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
        return selectQuery;
    }
    
    @Override
    public void setCriteria(SelectQuery query, final ViewContext viewCtx) {
        final HttpServletRequest request = viewCtx.getRequest();
        String resId = request.getParameter("resId");
        final String isExpired = request.getParameter("isExpired");
        final String platform = request.getParameter("platform");
        final String installedInVal = request.getParameter("installedIn");
        final String unique = viewCtx.getUniqueId();
        Criteria criteria = null;
        this.setReportParameters(viewCtx);
        if (unique.equalsIgnoreCase("MDMDeviceByAppSummary")) {
            if (resId != null && !resId.equalsIgnoreCase("")) {
                criteria = new Criteria(new Column("MdInstalledAppResourceRel", "RESOURCE_ID"), (Object)Long.parseLong(resId), 0);
            }
            final BlacklistQueryUtils blackList = new BlacklistQueryUtils();
            final Criteria filterCriteria = blackList.getCriteriaforDeviceForApps(1);
            if (criteria != null) {
                criteria = criteria.and(filterCriteria);
            }
            else {
                criteria = filterCriteria;
            }
            if (platform != null && !platform.equals("0") && !platform.equalsIgnoreCase("all")) {
                if (criteria == null) {
                    criteria = new Criteria(new Column("MdAppDetails", "PLATFORM_TYPE"), (Object)Integer.parseInt(platform), 0);
                }
                else {
                    criteria = criteria.and(new Criteria(new Column("MdAppDetails", "PLATFORM_TYPE"), (Object)Integer.parseInt(platform), 0));
                }
            }
            query = AppSettingsDataHandler.getInstance().setOnViewFilterCriteria(query, request, unique);
        }
        if (unique.equalsIgnoreCase("MDMDevByAppSummary")) {
            resId = request.getParameter("RESOURCE_ID");
            final String status = request.getParameter("status");
            if (resId != null && !resId.equalsIgnoreCase("")) {
                if (criteria == null) {
                    criteria = new Criteria(new Column("MdInstalledAppResourceRel", "RESOURCE_ID"), (Object)Long.parseLong(resId), 0);
                }
                else {
                    criteria = criteria.and(new Criteria(new Column("MdInstalledAppResourceRel", "RESOURCE_ID"), (Object)Long.parseLong(resId), 0));
                }
            }
            if (status != null && !status.equalsIgnoreCase("") && !status.equalsIgnoreCase("0")) {
                final BlacklistQueryUtils blackList2 = new BlacklistQueryUtils();
                Criteria filterCriteria2 = new Criteria();
                filterCriteria2 = blackList2.getCriteriaforDeviceForApps(Integer.parseInt(status));
                if (criteria != null) {
                    criteria = criteria.and(filterCriteria2);
                }
                else {
                    criteria = filterCriteria2;
                }
            }
            if (installedInVal != null && !installedInVal.equals("-1") && !installedInVal.equalsIgnoreCase("all")) {
                final Criteria cri = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "SCOPE"), (Object)Integer.parseInt(installedInVal), 0);
                if (criteria == null) {
                    criteria = cri;
                }
                else {
                    criteria = criteria.and(cri);
                }
            }
            query = AppSettingsDataHandler.getInstance().setOnViewFilterCriteria(query, request, unique);
        }
        if (unique.equalsIgnoreCase("MDMNewAppList")) {
            criteria = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            String period = viewCtx.getRequest().getParameter("period");
            final String startDate = viewCtx.getRequest().getParameter("startDate");
            final String endDate = viewCtx.getRequest().getParameter("endDate");
            final String criteriaTable = "MdInstalledAppResourceRel";
            final String criteriaColumn = "UPDATED_AT";
            Criteria periodCrit = null;
            if (((startDate == null && endDate == null) || (startDate.equalsIgnoreCase("") && endDate.equalsIgnoreCase(""))) && (period == null || period.equalsIgnoreCase("all"))) {
                period = "30";
            }
            if (period != null && !period.equalsIgnoreCase("all") && !period.equalsIgnoreCase("custom")) {
                final Calendar cal = Calendar.getInstance();
                int noOfDays = Integer.parseInt(period);
                noOfDays *= -1;
                final Date today = new Date();
                cal.setTime(today);
                cal.add(5, noOfDays);
                cal.set(11, 0);
                cal.set(12, 0);
                cal.set(13, 0);
                final long filter = cal.getTime().getTime();
                periodCrit = new Criteria(Column.getColumn(criteriaTable, criteriaColumn), (Object)new Long(filter), 4);
            }
            if (startDate != null && endDate != null && !startDate.equalsIgnoreCase("") && !endDate.equalsIgnoreCase("")) {
                final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                long start = 0L;
                long end = 0L;
                try {
                    start = sdf.parse(startDate).getTime();
                    end = sdf.parse(endDate).getTime();
                    if (start > end) {
                        final long temp = start;
                        start = end;
                        end = temp;
                    }
                }
                catch (final Exception exp) {
                    exp.printStackTrace();
                }
                final Criteria criteria2 = new Criteria(Column.getColumn(criteriaTable, criteriaColumn), (Object)start, 4);
                final Criteria criteria3 = new Criteria(Column.getColumn(criteriaTable, criteriaColumn), (Object)end, 6);
                periodCrit = criteria2.and(criteria3);
            }
            if (criteria != null) {
                criteria = criteria.and(periodCrit);
            }
            else {
                criteria = periodCrit;
            }
            if (platform != null && !platform.equals("0") && !platform.equalsIgnoreCase("all")) {
                if (criteria == null) {
                    criteria = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)Integer.parseInt(platform), 0);
                }
                else {
                    criteria = criteria.and(new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)Integer.parseInt(platform), 0));
                }
            }
            query = AppSettingsDataHandler.getInstance().setOnViewFilterCriteria(query, request, unique);
        }
        if (criteria != null) {
            final Criteria crit = query.getCriteria();
            if (crit != null) {
                query.setCriteria(crit.and(criteria));
            }
            else {
                query.setCriteria(criteria);
            }
        }
        super.setCriteria(query, viewCtx);
    }
    
    private void setReportParameters(final ViewContext viewCtx) {
        final String toolID = (String)SYMClientUtil.getStateValue(viewCtx, "toolID");
        if (toolID != null) {
            final int reportConstant = Integer.parseInt(toolID);
            try {
                final Hashtable viewProps = SYMReportUtil.getViewParams(Integer.valueOf(reportConstant));
                viewCtx.getRequest().setAttribute("viewProps", (Object)viewProps);
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }
}
