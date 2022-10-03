package com.me.mdm.webclient.audit;

import javax.servlet.http.HttpServletRequest;
import com.me.devicemanagement.framework.webclient.audit.EventLogUtil;
import com.me.devicemanagement.framework.server.util.Utils;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.webclient.audit.EventViewerTRAction;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class MDMEmberEventViewerTRAction extends MDMEmberTableRetrieverAction
{
    String className;
    Logger logger;
    
    public MDMEmberEventViewerTRAction() {
        this.className = EventViewerTRAction.class.getName();
        this.logger = Logger.getLogger(this.className);
    }
    
    @Override
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        final HttpServletRequest request = viewCtx.getRequest();
        final String resourceIDStr = request.getParameter("resourceID");
        final String user = request.getParameter("user");
        final String days = request.getParameter("days");
        final String startDate = request.getParameter("startDate");
        final String endDate = request.getParameter("endDate");
        Criteria criteria = selectQuery.getCriteria();
        if (user != null && !user.equals("") && !user.equals("all")) {
            final Criteria crit = new Criteria(Column.getColumn("EventLog", "LOGON_USER_NAME"), (Object)user, 0);
            if (criteria != null) {
                criteria = criteria.and(crit);
            }
            else {
                criteria = crit;
            }
            selectQuery.setCriteria(criteria);
        }
        if (startDate != null && !startDate.equals("") && endDate != null && !endDate.equals("")) {
            final Criteria crit = this.getTimeCriteria(startDate, endDate);
            if (criteria != null) {
                criteria = criteria.and(crit);
            }
            else {
                criteria = crit;
            }
            selectQuery.setCriteria(criteria);
        }
        else if (days != null && !days.equals("")) {
            final long noOfDayL = Long.parseLong(days);
            long timeNow;
            if (startDate != null && !startDate.equalsIgnoreCase("")) {
                timeNow = Utils.getTime(startDate) + 86400000L - 2000L;
            }
            else {
                timeNow = System.currentTimeMillis();
            }
            final long timePeriod = noOfDayL * 24L * 60L * 60L * 1000L;
            final long timeBefore = timeNow - timePeriod;
            final Criteria crit2 = this.getTimeDaysCriteria(startDate, timeBefore);
            if (criteria != null) {
                criteria = criteria.and(crit2);
            }
            else {
                criteria = crit2;
            }
            selectQuery.setCriteria(criteria);
        }
        if (resourceIDStr != null && !resourceIDStr.equals("")) {
            final long resourceID = Long.parseLong(resourceIDStr);
            final Criteria crit3 = new Criteria(Column.getColumn("ResourceEventLogRel", "RESOURCE_ID"), (Object)resourceID, 0);
            if (criteria != null) {
                criteria = criteria.and(crit3);
            }
            else {
                criteria = crit3;
            }
            selectQuery.setCriteria(criteria);
        }
        final Criteria crit = EventLogUtil.getInstance().getLicenseCriteriaForEventCode();
        if (crit != null) {
            if (criteria != null) {
                criteria = criteria.and(crit);
            }
            else {
                criteria = crit;
            }
            selectQuery.setCriteria(criteria);
        }
        final Criteria desktopModuleCri = EventLogUtil.getInstance().getDesktopModuleCriteriaForEventCode();
        if (desktopModuleCri != null) {
            if (criteria != null) {
                criteria = criteria.and(desktopModuleCri);
            }
            else {
                criteria = desktopModuleCri;
            }
            selectQuery.setCriteria(criteria);
        }
        super.setCriteria(selectQuery, viewCtx);
    }
    
    public Criteria getTimeCriteria(final String startDateStr, final String endDateStr) {
        Long startDate = null;
        Long endDate = null;
        startDate = Utils.getTime(startDateStr);
        endDate = Utils.getTime(endDateStr) + 86400000L - 2000L;
        Criteria crit = null;
        if (startDate != null) {
            crit = new Criteria(Column.getColumn("EventTimeDuration", "EVENT_START_TIME"), (Object)startDate, 4);
        }
        if (endDate != null) {
            final Criteria criteria = new Criteria(Column.getColumn("EventTimeDuration", "EVENT_END_TIME"), (Object)endDate, 6);
            if (crit != null) {
                crit = crit.and(criteria);
            }
            else {
                crit = criteria;
            }
        }
        return crit;
    }
    
    public Criteria getTimeDaysCriteria(final String startDateStr, final Long endTime) {
        Long startDate = null;
        startDate = Utils.getTime(startDateStr) + 86400000L - 2000L;
        Criteria crit = null;
        if (startDate != null) {
            crit = new Criteria(Column.getColumn("EventLog", "EVENT_TIMESTAMP"), (Object)startDate, 6);
        }
        if (endTime != null) {
            final Criteria criteria = new Criteria(Column.getColumn("EventLog", "EVENT_TIMESTAMP"), (Object)endTime, 4);
            if (crit != null) {
                crit = crit.and(criteria);
            }
            else {
                crit = criteria;
            }
        }
        return crit;
    }
}
