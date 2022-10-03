package com.me.ems.framework.reports.api.v1.service;

import java.util.Hashtable;
import com.me.ems.framework.reports.core.ScheduleReportTaskExecutor;
import java.util.Properties;
import com.me.devicemanagement.framework.webclient.schedulereport.ScheduleReportUtil;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.scheduler.SchedulerProviderInterface;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.scheduler.SchedulerInfo;
import java.util.Set;
import com.adventnet.persistence.WritableDataObject;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import com.me.ems.framework.common.api.utils.APIException;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import com.me.ems.framework.reports.core.ScheduleReportsCoreUtil;
import java.util.ArrayList;
import com.me.ems.framework.common.api.v1.model.Node;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.logging.Logger;

public class ScheduleReportsService
{
    private static Logger logger;
    
    public Node<String> getAvailableReports(final User user) throws APIException {
        try {
            final List<String> applicableReportModulesForCurrentLicense = new ArrayList<String>();
            return ScheduleReportsCoreUtil.getAllPredefinedReports(applicableReportModulesForCurrentLicense, user);
        }
        catch (final Exception ex) {
            ScheduleReportsService.logger.log(Level.SEVERE, "Exception while fetching available custom reports", ex);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "dc.rc.viewer.error_internalServerError");
        }
    }
    
    public Map getRetentionPeriod() throws APIException {
        final Map<String, Integer> retentionPeriod = new HashMap<String, Integer>();
        final Integer defaultDays = 30;
        retentionPeriod.put("retentionPeriod", defaultDays);
        Integer actualDays = null;
        try {
            actualDays = (Integer)DBUtil.getFirstValueFromDBWithOutCriteria("ScheduleBackupStatus", "HISTORY_PERIOD");
            if (actualDays != null) {
                retentionPeriod.put("retentionPeriod", actualDays);
            }
        }
        catch (final Exception ex) {
            ScheduleReportsService.logger.log(Level.SEVERE, "Exception while getting retention period.", ex);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
        return retentionPeriod;
    }
    
    public Map saveScheduleReportWithEventLog(final Map scheduleReport, final User dcUser, final Long customerID) throws APIException {
        final Map responseMap = this.saveScheduleReport(scheduleReport, dcUser, customerID);
        if (responseMap.get("status")) {
            final String srScheduled = "dc.reports.schedule_report_scheduled";
            DCEventLogUtil.getInstance().addEvent(786, responseMap.get("owner"), null, srScheduled, responseMap.get("reportName"), true, customerID);
        }
        return responseMap;
    }
    
    public Map saveScheduleReport(final Map scheduleReport, final User dcUser, final Long customerID) throws APIException {
        final String redactType = scheduleReport.get("redactType").toString();
        if ((redactType.equals("3") || redactType.equals("0")) && !scheduleReport.getOrDefault("userConsent", false)) {
            throw new APIException(Response.Status.BAD_REQUEST, "REP0009", "dc.gdpr.report.consent.error");
        }
        final Map responseMap = new HashMap();
        responseMap.put("status", false);
        try {
            final Map<String, String> schedulerMap = scheduleReport.get("scheduler");
            final String owner = dcUser.getName();
            final String reportName = scheduleReport.get("reportName");
            final Long taskID = this.setNewScheduler(schedulerMap, reportName, owner, customerID);
            final DataObject scheduleReportDO = (DataObject)new WritableDataObject();
            ScheduleReportsCoreUtil.addScheduleReportTask(scheduleReportDO, scheduleReport, taskID, customerID);
            final List<Map> selectedReports = (ArrayList)scheduleReport.get("selectedReports");
            final List<String> allRoles = dcUser.getAllRoles();
            final Map<Integer, Set<Long>> eligibleReports = ScheduleReportsCoreUtil.getEligibleReports(allRoles);
            for (final Map oneReport : selectedReports) {
                final String viewIDStr = oneReport.get("viewID");
                final Long viewID = Long.valueOf(viewIDStr);
                final String reportTypeStr = oneReport.get("reportType");
                final Integer repType = Integer.valueOf(reportTypeStr);
                switch (repType) {
                    case 1: {
                        final Set<Long> repSet = eligibleReports.get(1);
                        if (!repSet.contains(viewID)) {
                            continue;
                        }
                        break;
                    }
                    case 2: {
                        if (!eligibleReports.containsKey(2)) {
                            continue;
                        }
                        break;
                    }
                    case 3: {
                        if (!eligibleReports.containsKey(3)) {
                            continue;
                        }
                        break;
                    }
                }
                ScheduleReportsCoreUtil.setRepToTaskRel(oneReport, scheduleReportDO, taskID);
                if (!scheduleReportDO.getRows("ScheduleRepToReportRel").hasNext()) {
                    ScheduleReportsService.logger.log(Level.WARNING, "No Eligible reports for Schedule report,userID: " + dcUser.getUserID() + " customerID: " + customerID);
                    SchedulerInfo.getInstance().removeScheduleForTask(taskID);
                    throw new APIException(Response.Status.FORBIDDEN, "REP0008", "dc.rest.authentication.unauthorized");
                }
                final String appliedFilterStr = oneReport.get("filter");
                if (appliedFilterStr == null || appliedFilterStr.trim().isEmpty()) {
                    continue;
                }
                final Long appliedFilter = Long.valueOf(appliedFilterStr);
                ScheduleReportsCoreUtil.setFilterToSR(appliedFilter, scheduleReportDO, oneReport);
            }
            SyMUtil.getPersistence().add(scheduleReportDO);
            responseMap.put("taskID", taskID.toString());
            responseMap.put("reportName", reportName);
            responseMap.put("owner", owner);
            if (scheduleReport.getOrDefault("userConsent", false)) {
                final String userConsent = "dc.rep.scheduleReport.schedule_created";
                DCEventLogUtil.getInstance().addEvent(121, owner, null, userConsent, "", true, customerID);
            }
            responseMap.put("status", true);
        }
        catch (final DataAccessException ex) {
            ScheduleReportsService.logger.log(Level.WARNING, "Exception while saving schedule report.", (Throwable)ex);
        }
        catch (final Exception ex2) {
            ScheduleReportsService.logger.log(Level.SEVERE, "Exception while saving schedule report.", ex2);
            if (ex2 instanceof APIException) {
                throw ex2;
            }
        }
        return responseMap;
    }
    
    public Map updateScheduleReport(final Long taskID, final Map scheduleReport, final User user, final Long customerID) throws APIException {
        final Map responseMap = this.saveScheduleReport(scheduleReport, user, customerID);
        final String srModified = "dc.reports.schedule_report_modified";
        if (responseMap.get("status")) {
            DCEventLogUtil.getInstance().addEvent(787, responseMap.get("owner"), null, srModified, responseMap.get("reportName"), true, customerID);
            SchedulerInfo.getInstance().removeScheduleForTask(taskID);
        }
        return responseMap;
    }
    
    public Map getScheduleReport(final Long taskID, final User dcUser, final Long customerID) throws APIException {
        final Map response = new HashMap();
        final Long userID = dcUser.getUserID();
        final boolean isAdmin = dcUser.isAdminUser();
        try {
            final DataObject scheduleRepDO = ScheduleReportsCoreUtil.getScheduleRepDO(taskID, userID, customerID, isAdmin);
            if (scheduleRepDO.isEmpty()) {
                ScheduleReportsService.logger.log(Level.SEVERE, "Schedule report : " + taskID + " is un-authorized for user : " + userID);
                throw new APIException(Response.Status.FORBIDDEN, "REP0008", "ems.rest.authentication.unauthorized");
            }
            final List<String> allRoles = dcUser.getAllRoles();
            final Map<Integer, Set<Long>> eligibleReports = ScheduleReportsCoreUtil.getEligibleReports(allRoles);
            response.put("selectedReports", this.getSelectedReports(scheduleRepDO, eligibleReports));
            final Row srRow = scheduleRepDO.getFirstRow("ScheduleRepTask");
            final Row taskRow = scheduleRepDO.getFirstRow("TaskDetails");
            response.put("taskID", srRow.get("TASK_ID"));
            response.put("reportName", taskRow.get("TASKNAME"));
            response.put("reportDescription", srRow.get("DESCRIPTION"));
            response.put("deliveryFormat", srRow.get("DELIVERY_FORMAT").toString());
            response.put("attachmentLimits", srRow.get("ATTACHMENT_LIMIT"));
            response.put("notificationEmails", srRow.get("EMAIL_ADDRESS"));
            response.put("emailSubject", srRow.get("SUBJECT"));
            response.put("emailContent", srRow.get("CONTENT"));
            response.put("redactType", srRow.get("REDACT_TYPE"));
            response.put("useNATLink", srRow.get("USE_NAT_LINK"));
            response.put("reportFormats", srRow.get("REPORT_FORMAT").toString());
            response.put("isEmptyReportNeeded", srRow.get("IS_EMPTY_REPORT_NEEDED"));
            final SchedulerProviderInterface schedulerAPI = ApiFactoryProvider.getSchedulerAPI();
            final String scheduleName = schedulerAPI.getScheduleNameForTask(taskID);
            final Long dateTime = System.currentTimeMillis();
            final JSONObject scheduler = schedulerAPI.getScheduleJsonValues(scheduleName, true, false, false, false, dateTime);
            final Map schedulerMap = SyMUtil.jsonToMap(scheduler, false);
            final String scheduleType = schedulerMap.get("scheduleType");
            if (scheduleType != null) {
                if (scheduleType.equals("Weekly")) {
                    final String[] daysOfWeek = schedulerMap.get("daysOfWeek").toString().split(",");
                    schedulerMap.replace("daysOfWeek", daysOfWeek);
                }
                else if (scheduleType.equals("Monthly")) {
                    final String[] monthsList = schedulerMap.get("monthsList").toString().split(",");
                    schedulerMap.replace("monthsList", monthsList);
                    if (schedulerMap.get("monthlyPerform").equals("WeekDay")) {
                        final String[] monthlyWeekNum = schedulerMap.get("monthlyWeekNum").toString().split(",");
                        schedulerMap.replace("monthlyWeekNum", monthlyWeekNum);
                    }
                }
            }
            response.put("scheduler", schedulerMap);
        }
        catch (final APIException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            ScheduleReportsService.logger.log(Level.SEVERE, "Exception while getting schedule report: " + taskID, ex2);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
        return response;
    }
    
    public List<Map> getSelectedReports(final DataObject scheduleRepDO, final Map<Integer, Set<Long>> eligibleReports) {
        final List<Map> selectedReports = new ArrayList<Map>();
        try {
            final Iterator srToRepIter = scheduleRepDO.getRows("ScheduleRepToReportRel");
            while (srToRepIter.hasNext()) {
                final Map oneReport = new HashMap();
                final Row srToRepRow = srToRepIter.next();
                final Long viewID = (Long)srToRepRow.get("REPORT_ID");
                final Long scheduleID = (Long)srToRepRow.get("SCHEDULE_REP_ID");
                final Integer repType = (Integer)srToRepRow.get("REPORT_TYPE");
                switch (repType) {
                    case 1: {
                        final Set<Long> repSet = eligibleReports.get(1);
                        if (!repSet.contains(viewID)) {
                            continue;
                        }
                        break;
                    }
                    case 2: {
                        if (!eligibleReports.containsKey(2)) {
                            continue;
                        }
                        break;
                    }
                    case 3: {
                        if (!eligibleReports.containsKey(3)) {
                            continue;
                        }
                        break;
                    }
                }
                final Criteria viewCrit = new Criteria(Column.getColumn("SRToFilterRel", "SCHEDULE_REP_ID"), (Object)scheduleID, 0);
                final Long filterID = (Long)scheduleRepDO.getValue("SRToFilterRel", "FILTER_ID", viewCrit);
                oneReport.put("viewID", viewID);
                oneReport.put("reportType", repType);
                oneReport.put("filter", filterID);
                selectedReports.add(oneReport);
            }
        }
        catch (final DataAccessException ex) {
            ScheduleReportsService.logger.log(Level.WARNING, "Exception while getting selected reports.", (Throwable)ex);
        }
        return selectedReports;
    }
    
    public Long setNewScheduler(final Map schedulerMap, final String reportName, final String owner, final Long customerID) {
        final String scheduleType = schedulerMap.get("scheduleType");
        if (scheduleType.equals("Weekly")) {
            final String daysOfWeek = schedulerMap.get("daysOfWeek").stream().collect((Collector<? super Object, ?, String>)Collectors.joining(",")).toString();
            schedulerMap.replace("daysOfWeek", daysOfWeek);
        }
        else if (scheduleType.equals("Monthly")) {
            final String monthsList = schedulerMap.get("monthsList").stream().collect((Collector<? super Object, ?, String>)Collectors.joining(",")).toString();
            schedulerMap.replace("monthsList", monthsList);
            if (schedulerMap.get("monthlyPerform").equals("WeekDay")) {
                final String monthlyWeekNum = schedulerMap.get("monthlyWeekNum").stream().collect((Collector<? super Object, ?, String>)Collectors.joining(",")).toString();
                schedulerMap.replace("monthlyWeekNum", monthlyWeekNum);
            }
        }
        final JSONObject scheduler = new JSONObject(schedulerMap);
        final String workEngineID = "DesktopCentral";
        final String workFlowName = "DCScheduleReport";
        final String className = "com.me.ems.framework.reports.core.ScheduleReportTask";
        final String scheduleName = "scheduleShutdown_" + System.currentTimeMillis();
        return ApiFactoryProvider.getSchedulerAPI().createScheduleFromJson(scheduler, "2", reportName, scheduleName, workFlowName, className, null, null, owner, customerID, null, true);
    }
    
    public Map deleteScheduleReports(final String owner, final Long customerID, final List taskIDs) throws APIException {
        final Map responseMap = new HashMap();
        boolean isDeleted = false;
        final String scheduleReportDeletedI18N = "dc.reports.schedule_report_deleted";
        final Long taskID = taskIDs.get(0);
        try {
            final Row taskRow = ScheduleReportUtil.getInstance().getTaskDetailsProperties(taskID);
            isDeleted = SchedulerInfo.getInstance().removeScheduleForTask(taskID);
            final String taskName = (String)taskRow.get("TASKNAME");
            DCEventLogUtil.getInstance().addEvent(788, owner, null, scheduleReportDeletedI18N, taskName, true, customerID);
        }
        catch (final Exception ex) {
            ScheduleReportsService.logger.log(Level.WARNING, "Exception while deleting schedule report: " + taskID, ex);
        }
        if (!isDeleted) {
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
        responseMap.put("deletedTasks", taskID);
        return responseMap;
    }
    
    public boolean executeScheduleReports(final String owner, final Long customerID, final Long taskID) {
        boolean isExecuted = false;
        final Properties prop = new Properties();
        ((Hashtable<String, Boolean>)prop).put("ONDEMAND", false);
        ((Hashtable<String, Long>)prop).put("TASK_ID", taskID);
        final String scheduleReportExecutedI18N = "dc.reports.schedule_report_executed";
        try {
            ScheduleReportTaskExecutor.executeTaskNow(prop);
            isExecuted = true;
            final Row taskDetails = ScheduleReportUtil.getInstance().getTaskDetailsProperties(taskID);
            final String taskName = (String)taskDetails.get("TASKNAME");
            DCEventLogUtil.getInstance().addEvent(799, owner, null, scheduleReportExecutedI18N, taskName, true, customerID);
        }
        catch (final Exception ex) {
            ScheduleReportsService.logger.log(Level.SEVERE, "Exception in executing schedule report: " + taskID + ",", ex);
        }
        return isExecuted;
    }
    
    static {
        ScheduleReportsService.logger = Logger.getLogger("ScheduleReportLogger");
    }
}
