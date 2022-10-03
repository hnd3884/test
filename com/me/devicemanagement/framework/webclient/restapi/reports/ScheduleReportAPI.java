package com.me.devicemanagement.framework.webclient.restapi.reports;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Persistence;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import javax.transaction.TransactionManager;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.util.ResponseStatusBean;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.net.URLDecoder;
import com.me.devicemanagement.framework.server.scheduler.SchedulerInfo;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import javax.ws.rs.CookieParam;
import java.util.Iterator;
import java.util.LinkedHashMap;
import org.json.JSONException;
import org.json.JSONObject;
import com.me.devicemanagement.framework.webclient.reportcriteria.ReportCriteriaUtil;
import java.util.Hashtable;
import java.util.ArrayList;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.webclient.reports.SYMReportUtil;
import javax.transaction.NotSupportedException;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import java.util.Properties;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import com.me.devicemanagement.framework.server.util.FrameworkStatusCodes;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.util.CommonUtils;
import com.me.devicemanagement.framework.webclient.schedulereport.ScheduleReportUtil;
import java.util.logging.Level;
import com.me.devicemanagement.framework.webclient.schedulereport.ScheduleReportBean;
import java.util.logging.Logger;
import javax.ws.rs.Path;

@Path("/schedulereport")
public class ScheduleReportAPI
{
    static Logger log;
    
    @POST
    @Path("/addorupdatereporthistoryperiod")
    @Produces({ "application/json" })
    public ScheduleReportBean addOrUpdateScheduleHistoryPeriod(final ScheduleReportBean scheduleReportBean) {
        ScheduleReportAPI.log.log(Level.INFO, "Going to save schedule history period");
        final ScheduleReportUtil scheduleReportUtil = new ScheduleReportUtil();
        final Long task_id = scheduleReportBean.getTaskId();
        final Integer history_period = scheduleReportBean.getScheduleBackupHistoryPeriod();
        final Long user_id = CommonUtils.getUserId();
        final Long customer_id = CustomerInfoUtil.getInstance().getCustomerId();
        final int errorCode = scheduleReportUtil.saveScheduleHistoryWithTaskId("true", history_period.toString(), task_id, user_id, customer_id);
        scheduleReportBean.setResponseStatus(FrameworkStatusCodes.setResponseCodeInBean(errorCode));
        return scheduleReportBean;
    }
    
    @POST
    @Path("/getScheduleBachUpHistoryPeriod")
    @Produces({ "application/json" })
    public ScheduleReportBean getScheduleBachUpHistoryPeriod() {
        final ScheduleReportBean scheduleReportBean = new ScheduleReportBean();
        ScheduleReportAPI.log.log(Level.INFO, "Going to save schedule history period");
        final ScheduleReportUtil scheduleReportUtil = new ScheduleReportUtil();
        final Long user_id = CommonUtils.getUserId();
        final Long customer_id = CustomerInfoUtil.getInstance().getCustomerId();
        final Properties properties = ScheduleReportUtil.getScheduleBackupDetails(null, user_id, customer_id);
        int errorCode;
        if (properties != null) {
            scheduleReportBean.setScheduleBackupHistoryPeriod(Integer.parseInt(properties.getProperty("historyPeriod")));
            errorCode = FrameworkStatusCodes.SUCCESS_RESPONSE_CODE;
        }
        else {
            errorCode = 1001;
        }
        scheduleReportBean.setResponseStatus(FrameworkStatusCodes.setResponseCodeInBean(errorCode));
        return scheduleReportBean;
    }
    
    @POST
    @Path("/getScheduleBachUpHistoryPeriodWithTaskId")
    @Produces({ "application/json" })
    public ScheduleReportBean getScheduleBachUpHistoryPeriodWithTaskId(final ScheduleReportBean scheduleReportBean) {
        ScheduleReportAPI.log.log(Level.INFO, "Going to save schedule history period");
        final ScheduleReportUtil scheduleReportUtil = new ScheduleReportUtil();
        final Properties properties = ScheduleReportUtil.getScheduleBackupDetails(scheduleReportBean.getTaskId());
        int errorCode;
        if (properties != null) {
            scheduleReportBean.setScheduleBackupHistoryPeriod(Integer.parseInt(properties.getProperty("historyPeriod")));
            errorCode = FrameworkStatusCodes.SUCCESS_RESPONSE_CODE;
        }
        else {
            errorCode = 1001;
        }
        scheduleReportBean.setResponseStatus(FrameworkStatusCodes.setResponseCodeInBean(errorCode));
        return scheduleReportBean;
    }
    
    @POST
    @Produces({ "application/json" })
    @Path("/isTimeExpired")
    public ScheduleReportBean isTimeExpired(final ScheduleReportBean scheduleReportBean) {
        ScheduleReportAPI.log.log(Level.INFO, "Schedule Report :Entered into ScheduleReportAPI.isTimeExpired()");
        final String onceTime = scheduleReportBean.getOnceTime();
        try {
            scheduleReportBean.setIsTimeExpired(DateTimeUtil.isTimeExpired(onceTime, "MM/dd/yyyy HH:mm:ss"));
        }
        catch (final NotSupportedException nse) {
            nse.printStackTrace();
            scheduleReportBean.setResponseStatus(FrameworkStatusCodes.setResponseCodeInBean(FrameworkStatusCodes.UNPROCESSABLE_DATA_CODE));
        }
        scheduleReportBean.setResponseStatus(FrameworkStatusCodes.setResponseCodeInBean(FrameworkStatusCodes.SUCCESS_RESPONSE_CODE));
        return scheduleReportBean;
    }
    
    @POST
    @Produces({ "application/json" })
    @Path("/reportlist")
    public ScheduleReportBean getReportList(final ScheduleReportBean scheduleReportBean) {
        ScheduleReportAPI.log.log(Level.INFO, "Schedule Report :Entered into ScheduleReportAPI.getReportList()");
        final ScheduleReportUtil scheduleReportUtil = new ScheduleReportUtil();
        final ArrayList excludedReports = scheduleReportUtil.getReportList();
        ScheduleReportAPI.log.log(Level.INFO, "report:" + excludedReports);
        Integer reportId = scheduleReportBean.getReportCategoryId();
        scheduleReportBean.setReportTypeName("predefined");
        if (reportId == -1) {
            reportId = null;
        }
        LinkedHashMap viewList = null;
        try {
            viewList = SYMReportUtil.getViewList(reportId, SYMReportUtil.viewListWithSubCategoryId(), null, SYMReportUtil.getViewQuery(null));
        }
        catch (final SyMException symException) {
            ScheduleReportAPI.log.log(Level.INFO, "Exception while getting report list" + symException);
            symException.printStackTrace();
        }
        final JSONArray reportList = new JSONArray();
        final Iterator itr = viewList.keySet().iterator();
        while (itr.hasNext()) {
            final ArrayList repList = viewList.get(itr.next());
            for (int j = 0; j < repList.size(); ++j) {
                final Hashtable reports = repList.get(j);
                final Integer Id = reports.get("VIEW_ID");
                final boolean isCriApplicable = ReportCriteriaUtil.getInstance().isCriteriaApplicable(Id);
                if (!excludedReports.contains(Id)) {
                    try {
                        final JSONObject report = new JSONObject();
                        report.put("viewId", (Object)Id.toString());
                        report.put("name", reports.get("TITLE"));
                        report.put("isCriApplicable", isCriApplicable);
                        reportList.put((Object)report);
                    }
                    catch (final JSONException jsonException) {
                        ScheduleReportAPI.log.log(Level.INFO, "Exception while adding view id : " + Id + "in JsonObject" + jsonException);
                        jsonException.printStackTrace();
                    }
                }
            }
        }
        scheduleReportBean.setJsonArrayOfReportsList(reportList.toString());
        scheduleReportBean.setResponseStatus(FrameworkStatusCodes.setResponseCodeInBean(FrameworkStatusCodes.SUCCESS_RESPONSE_CODE));
        return scheduleReportBean;
    }
    
    @POST
    @Produces({ "application/json" })
    @Path("/isReportNameAlreadyExist")
    public ScheduleReportBean isReportNameAlreadyExist(final ScheduleReportBean scheduleReportBean) {
        try {
            final String reportName = scheduleReportBean.getReportName();
            if (reportName != null && !"".equals(reportName.trim())) {
                scheduleReportBean.setIsReportNameAlreadyExist(ScheduleReportUtil.getInstance().isReportNameExist(reportName));
            }
            else {
                scheduleReportBean.setResponseStatus(FrameworkStatusCodes.setResponseCodeInBean(FrameworkStatusCodes.NO_CONTENT_RESPONSE_CODE));
            }
            scheduleReportBean.setResponseStatus(FrameworkStatusCodes.setResponseCodeInBean(FrameworkStatusCodes.SUCCESS_RESPONSE_CODE));
            return scheduleReportBean;
        }
        catch (final Exception e) {
            e.printStackTrace();
            scheduleReportBean.setResponseStatus(FrameworkStatusCodes.setResponseCodeInBean(FrameworkStatusCodes.UNPROCESSABLE_DATA_CODE));
            return scheduleReportBean;
        }
    }
    
    @POST
    @Produces({ "application/json" })
    @Path("/saveScheduleReport")
    public ScheduleReportBean saveScheduleReport(@CookieParam("customerid") String customerId, final ScheduleReportBean scheduleReportBean) {
        ScheduleReportAPI.log.log(Level.INFO, "Schedule Report :Entered into executeScheduleReport API");
        final TransactionManager txMgr = SyMUtil.getUserTransaction();
        try {
            txMgr.begin();
            final String className = scheduleReportBean.getClassName();
            if (className == null || "".equals(className)) {
                scheduleReportBean.setResponseStatus(FrameworkStatusCodes.setResponseCodeInBean(FrameworkStatusCodes.SCHEDULER_CLASS_NOT_DEFINED_CODE));
                txMgr.rollback();
                return scheduleReportBean;
            }
            final String reportName = scheduleReportBean.getReportName();
            final String scheduleName = scheduleReportBean.getScheduleName();
            final String description = scheduleReportBean.getDescription();
            final Integer reportFormat = scheduleReportBean.getReportFormat();
            final Integer deliveryFormat = scheduleReportBean.getDeliveryFormat();
            final String reportListAsString = scheduleReportBean.getReportList();
            final String sender = scheduleReportBean.getSender();
            final String subject = scheduleReportBean.getSubject();
            String content = scheduleReportBean.getContent();
            if (content == null) {
                content = "";
            }
            final Integer attachLimit = scheduleReportBean.getAttachLimit();
            Boolean attachLimitFlag = scheduleReportBean.getAttachLimitFlag();
            if (attachLimitFlag == null) {
                attachLimitFlag = false;
            }
            final String criteriaColumnsListAsString = scheduleReportBean.getCriteriaColsList();
            final Long taskId = scheduleReportBean.getTaskId();
            String scheduleJsonObj = scheduleReportBean.getScheduleJsonObj();
            final String workflowName = scheduleReportBean.getWorkflowName();
            HashMap<Long, ArrayList<HashMap<String, String>>> criteriaColumnMap = new HashMap<Long, ArrayList<HashMap<String, String>>>();
            if (customerId == null) {
                customerId = scheduleReportBean.getCutomerId();
            }
            final Long customerID = MSPWebClientUtil.getCustomerID(customerId);
            final JSONArray criteriaColumnsList = CommonUtils.createJsonArray(criteriaColumnsListAsString);
            ScheduleReportAPI.log.log(Level.INFO, "Criteria '" + criteriaColumnsList + "'");
            if (criteriaColumnsList != null) {
                criteriaColumnMap = ReportCriteriaUtil.getInstance().convertCriteriaJsonToHash(criteriaColumnsList);
                ScheduleReportAPI.log.log(Level.INFO, "Criteria set for schedule report is {0}", criteriaColumnMap);
            }
            else {
                ScheduleReportAPI.log.log(Level.INFO, "No criteria set by user for scheduled report");
            }
            if (taskId != null) {
                SchedulerInfo.getInstance().removeScheduleForTask(taskId);
                ScheduleReportAPI.log.log(Level.FINE, "Successfully removed schedule for task : " + taskId);
            }
            else if (ScheduleReportUtil.getInstance().isReportNameExist(reportName)) {
                scheduleReportBean.setResponseStatus(FrameworkStatusCodes.setResponseCodeInBean(FrameworkStatusCodes.CONFLICT_ERROR_CODE));
                txMgr.rollback();
                return scheduleReportBean;
            }
            JSONObject schedulerJson;
            if (scheduleJsonObj != null) {
                scheduleJsonObj = URLDecoder.decode(scheduleJsonObj, "UTF-8");
                schedulerJson = new JSONObject(scheduleJsonObj);
            }
            else {
                schedulerJson = new JSONObject("{}");
            }
            final String onceTime = (String)schedulerJson.get("onceTime");
            if (onceTime != null && DateTimeUtil.isTimeExpired(onceTime, "MM/dd/yyyy HH:mm:ss")) {
                ScheduleReportAPI.log.log(Level.INFO, "Scheduler time has been expired");
                scheduleReportBean.setResponseStatus(FrameworkStatusCodes.setResponseCodeInBean(FrameworkStatusCodes.SCHEDULER_TIME_EXPIRED_CODE));
                txMgr.rollback();
                return scheduleReportBean;
            }
            final String loginName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
            if (loginName == null || "".equals(loginName)) {
                ScheduleReportAPI.log.log(Level.INFO, "Cannot find a login name");
                scheduleReportBean.setResponseStatus(FrameworkStatusCodes.setResponseCodeInBean(FrameworkStatusCodes.NO_CONTENT_RESPONSE_CODE));
                txMgr.rollback();
                return scheduleReportBean;
            }
            final Long task_id = ApiFactoryProvider.getSchedulerAPI().createScheduleFromJson(schedulerJson, "2", reportName, scheduleName, workflowName, className, description, sender, loginName, CustomerInfoUtil.getInstance().getCustomerId(), null, Boolean.TRUE);
            final Integer historyPeriod = scheduleReportBean.getScheduleBackupHistoryPeriod();
            if (deliveryFormat == 3 && historyPeriod != null && ScheduleReportUtil.getInstance().saveScheduleHistoryWithTaskId("true", historyPeriod.toString(), task_id, CommonUtils.getUserId(), customerID) != FrameworkStatusCodes.SUCCESS_RESPONSE_CODE) {
                ScheduleReportAPI.log.log(Level.INFO, "Problem while storing schedule history period");
                scheduleReportBean.setResponseStatus(FrameworkStatusCodes.setResponseCodeInBean(1001));
                txMgr.rollback();
                return scheduleReportBean;
            }
            final JSONObject scheduleRepObj = new JSONObject();
            scheduleRepObj.put("TASK_ID", (Object)task_id);
            scheduleRepObj.put("CUSTOMER_ID", (Object)customerID);
            scheduleRepObj.put("REPORT_FORMAT", (Object)reportFormat);
            scheduleRepObj.put("DELIVERY_FORMAT", (Object)deliveryFormat);
            scheduleRepObj.put("EMAIL_ADDRESS", (Object)sender);
            scheduleRepObj.put("SUBJECT", (Object)subject);
            scheduleRepObj.put("CONTENT", (Object)content);
            scheduleRepObj.put("DESCRIPTION", (Object)description);
            scheduleRepObj.put("ATTACHMENT_LIMIT", (Object)attachLimit);
            scheduleRepObj.put("DELIVERY_FLAG", (Object)(boolean)attachLimitFlag);
            scheduleRepObj.put("report_list", (Object)reportListAsString);
            final int status_code = SYMReportUtil.createScheduleReport(task_id, scheduleRepObj);
            if (status_code == 1001) {
                ScheduleReportAPI.log.log(Level.INFO, "Exception while creating schedule report");
                onFailure(scheduleReportBean, txMgr);
            }
            ScheduleReportAPI.log.log(Level.INFO, "Successfully added schedule details in db for taskid  : {0}", task_id);
            final String i18n = "dc.rep.scheduleReport.schedule_created";
            DCEventLogUtil.getInstance().addEvent(121, loginName, null, i18n, loginName, true, customerID);
            final ResponseStatusBean responseStatusBean = new ResponseStatusBean(FrameworkStatusCodes.SUCCESS_RESPONSE_CODE);
            scheduleReportBean.setResponseStatus(responseStatusBean);
            txMgr.commit();
        }
        catch (final DataAccessException dae) {
            ScheduleReportAPI.log.log(Level.INFO, "Exception while getting data object from DBUtil.getDataObjectFromDB" + dae);
            dae.printStackTrace();
            return onFailure(scheduleReportBean, txMgr);
        }
        catch (final NotSupportedException nse) {
            scheduleReportBean.setResponseStatus(FrameworkStatusCodes.setResponseCodeInBean(FrameworkStatusCodes.UNPROCESSABLE_DATA_CODE));
            nse.printStackTrace();
            return onFailure(scheduleReportBean, txMgr);
        }
        catch (final Exception e) {
            ScheduleReportAPI.log.log(Level.INFO, "Exception while getting data object from DBUtil.getDataObjectFromDB" + e);
            e.printStackTrace();
            return onFailure(scheduleReportBean, txMgr);
        }
        return scheduleReportBean;
    }
    
    private static ScheduleReportBean onFailure(final ScheduleReportBean scheduleReportBean, final TransactionManager txMgr) {
        scheduleReportBean.setResponseStatus(FrameworkStatusCodes.setResponseCodeInBean(FrameworkStatusCodes.DATA_OBJECT_CREATION_FAILURE_CODE));
        try {
            txMgr.rollback();
        }
        catch (final Exception e) {
            ScheduleReportAPI.log.log(Level.INFO, "Exception while rollback " + e);
            e.printStackTrace();
        }
        return scheduleReportBean;
    }
    
    @POST
    @Produces({ "application/json" })
    @Path("/removeScheduledTasks")
    public ScheduleReportBean removeScheduledTasks(final ScheduleReportBean scheduleReportBean) {
        try {
            ScheduleReportAPI.log.log(Level.INFO, "Schedule Report :Entered into removeScheduledTasks()");
            final Long[] removeList = scheduleReportBean.getTasksList();
            ScheduleReportAPI.log.log(Level.INFO, "Going to delete tasks " + removeList);
            if (removeList != null && removeList.length > 0) {
                for (int index = 0; index < removeList.length; ++index) {
                    final Long taskID = removeList[index];
                    SchedulerInfo.getInstance().removeScheduleForTask(taskID);
                }
                scheduleReportBean.setResponseStatus(FrameworkStatusCodes.setResponseCodeInBean(FrameworkStatusCodes.SUCCESS_RESPONSE_CODE));
                ScheduleReportAPI.log.log(Level.INFO, "successfully deleted tasks");
            }
            else {
                scheduleReportBean.setResponseStatus(FrameworkStatusCodes.setResponseCodeInBean(FrameworkStatusCodes.NO_CONTENT_RESPONSE_CODE));
            }
            return scheduleReportBean;
        }
        catch (final Exception exp) {
            ScheduleReportAPI.log.log(Level.WARNING, "Caught exception while deleting scheduled report task " + exp);
            exp.printStackTrace();
            scheduleReportBean.setResponseStatus(FrameworkStatusCodes.setResponseCodeInBean(FrameworkStatusCodes.UNPROCESSABLE_DATA_CODE));
            return scheduleReportBean;
        }
    }
    
    @POST
    @Produces({ "application/json" })
    @Path("/getScheduleReportCategoryId")
    public ScheduleReportBean getScheduleReportCategoryId(final ScheduleReportBean scheduleReportBean) {
        try {
            final String subCatId = scheduleReportBean.getSubCategoryId();
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ReportSubCategory"));
            final Column column = Column.getColumn("ReportSubCategory", "SUB_CATEGORY_ID");
            final Column c3 = Column.getColumn("ViewParams", "VIEW_ID");
            final Column c4 = Column.getColumn("ViewParams", "SUB_CATEGORY_ID");
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            final Criteria criteria = new Criteria(c3, (Object)subCatId, 0);
            final Join join = new Join("ReportSubCategory", "ViewParams", new String[] { "SUB_CATEGORY_ID" }, new String[] { "SUB_CATEGORY_ID" }, 2);
            query.addJoin(join);
            query.setCriteria(criteria);
            final Persistence persis = SyMUtil.getPersistence();
            final DataObject TaskDo = persis.get(query);
            ScheduleReportAPI.log.log(Level.FINE, "query for getting report id" + query);
            final Row categoryReport = TaskDo.getRow("ReportSubCategory");
            scheduleReportBean.setCategoryId((Integer)categoryReport.get("CATEGORY_ID"));
            scheduleReportBean.setResponseStatus(FrameworkStatusCodes.setResponseCodeInBean(FrameworkStatusCodes.SUCCESS_RESPONSE_CODE));
        }
        catch (final DataAccessException dae) {
            ScheduleReportAPI.log.log(Level.INFO, "Exception while retrieving subcategory id " + dae);
            dae.printStackTrace();
            scheduleReportBean.setResponseStatus(FrameworkStatusCodes.setResponseCodeInBean(1001));
        }
        return scheduleReportBean;
    }
    
    static {
        ScheduleReportAPI.log = Logger.getLogger(ScheduleReportAPI.class.getName());
    }
}
