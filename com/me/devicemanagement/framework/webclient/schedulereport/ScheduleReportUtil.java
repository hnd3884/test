package com.me.devicemanagement.framework.webclient.schedulereport;

import org.json.JSONException;
import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import java.util.HashSet;
import java.util.Set;
import com.me.devicemanagement.framework.server.util.CommonUtils;
import com.me.devicemanagement.framework.server.util.FrameworkStatusCodes;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessAPI;
import com.adventnet.ds.query.DataSet;
import com.adventnet.ds.query.Query;
import com.me.devicemanagement.framework.server.scheduler.SchedulerInfo;
import com.adventnet.ds.query.Range;
import com.me.devicemanagement.framework.webclient.reports.query.QueryReportHandler;
import java.io.IOException;
import java.text.DecimalFormat;
import com.me.devicemanagement.framework.server.general.UtilAccessAPI;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.util.ProductClassLoader;
import com.me.devicemanagement.framework.webclient.reports.ReportsProductInvoker;
import java.util.zip.ZipEntry;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;
import com.adventnet.i18n.I18N;
import java.util.Properties;
import java.util.List;
import java.util.HashMap;
import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.webclient.reportcriteria.ReportCriteriaUtil;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.framework.server.mailmanager.MailDetails;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.persistence.Persistence;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Hashtable;
import java.util.Iterator;
import com.me.devicemanagement.framework.webclient.reports.query.QueryReportAttrBean;
import javax.swing.table.TableModel;
import com.me.devicemanagement.framework.server.util.I18NUtil;
import com.me.devicemanagement.framework.server.util.Utils;
import com.adventnet.client.components.sql.SQLQueryAPI;
import java.util.ArrayList;
import com.me.devicemanagement.framework.webclient.export.xsl.ExportAsExcel;
import java.sql.Connection;
import com.me.devicemanagement.framework.webclient.export.csv.TableCSVRenderer;
import com.adventnet.db.api.RelationalAPI;
import java.io.FileOutputStream;
import com.adventnet.client.components.table.web.TableViewController;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.view.web.HttpReqWrapper;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import java.util.logging.Logger;
import org.json.JSONObject;

public class ScheduleReportUtil
{
    static final int KB = 1024;
    static final int MB = 1048576;
    static final int GB = 1073741824;
    public static String date;
    public static JSONObject publishReportDetails;
    public static Boolean store_published_report_details_enabled;
    public static final String TASK_ID = "task_id";
    public static final String FILE_NAME = "file_name";
    private static Logger out;
    private static ScheduleReportUtil scheduleReportUtil;
    
    public static ScheduleReportUtil getInstance() {
        if (ScheduleReportUtil.scheduleReportUtil == null) {
            ScheduleReportUtil.scheduleReportUtil = new ScheduleReportUtil();
        }
        final ScheduleReportUtil scheduleReportUtil = ScheduleReportUtil.scheduleReportUtil;
        ScheduleReportUtil.date = DateTimeUtil.dateString();
        return ScheduleReportUtil.scheduleReportUtil;
    }
    
    public static void setCustomerInfoThread(final long customer_id) {
        CustomerInfoThreadLocal.setIsClientCall("true");
        CustomerInfoThreadLocal.setSkipCustomerFilter("false");
        CustomerInfoThreadLocal.setSummaryPage("false");
        CustomerInfoThreadLocal.setCustomerId(String.valueOf(customer_id));
    }
    
    private static String getViewControllerFromViewName(final String viewName) {
        String viewController = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ViewConfiguration"));
            selectQuery.addJoin(new Join("ViewConfiguration", "WebViewConfig", new String[] { "VIEWNAME_NO" }, new String[] { "VIEWNAME" }, 2));
            selectQuery.setCriteria(new Criteria(Column.getColumn("ViewConfiguration", "VIEWNAME"), (Object)viewName, 0));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            final DataObject dao = DataAccess.get(selectQuery);
            if (!dao.isEmpty()) {
                final Row row = dao.getFirstRow("WebViewConfig");
                viewController = (String)row.get("VIEWCONTROLLER");
            }
        }
        catch (final Exception ex) {
            ScheduleReportUtil.out.log(Level.SEVERE, "Exception in getting view controller name", ex);
        }
        return viewController;
    }
    
    public static boolean checkIfViewIsEmpty(final String viewName, final HttpReqWrapper httpReqWrapper) {
        Boolean isEmptyView = false;
        try {
            final ViewContext viewContext = ViewContext.getViewContext((Object)viewName, (HttpServletRequest)httpReqWrapper);
            final String viewController = getViewControllerFromViewName(viewName);
            if (viewController != null) {
                final TableViewController tableViewController = (TableViewController)Class.forName(viewController).newInstance();
                final long recordCount = tableViewController.getCount(viewContext);
                if (recordCount == 0L) {
                    isEmptyView = true;
                }
            }
        }
        catch (final Exception ex) {
            ScheduleReportUtil.out.log(Level.SEVERE, "Exception in checkIfViewIsEmpty", ex);
        }
        return isEmptyView;
    }
    
    public static int generateCSVReport(final String viewName, final HttpReqWrapper requestWrapper, final FileOutputStream fileOutputStream) {
        return generateCSVReport(viewName, requestWrapper, fileOutputStream, null);
    }
    
    public static int generateCSVReport(final String viewName, final HttpReqWrapper requestWrapper, final FileOutputStream fileOutputStream, final Boolean isEmptyReportNeeded) {
        Connection con = null;
        try {
            if (isEmptyReportNeeded != null && !isEmptyReportNeeded && checkIfViewIsEmpty(viewName, requestWrapper)) {
                return 1112;
            }
            final ViewContext viewCtx = ViewContext.getViewContext((Object)viewName, (HttpServletRequest)requestWrapper);
            viewCtx.setRenderType(5);
            con = RelationalAPI.getInstance().getConnection();
            viewCtx.setTransientState("CONNECTION_OBJ", (Object)con);
            final TableCSVRenderer csvformat = new TableCSVRenderer();
            csvformat.generateCSVInOutputStream(viewName, (HttpServletRequest)requestWrapper, fileOutputStream);
            return 1106;
        }
        catch (final Exception ex) {
            ScheduleReportUtil.out.log(Level.WARNING, "Schedule Report :Exception while creating csv file", ex);
            return 1107;
        }
        finally {
            try {
                if (con != null) {
                    con.close();
                }
            }
            catch (final Exception exc) {
                ScheduleReportUtil.out.log(Level.WARNING, "Exception while closing the connection for CSV file", exc);
            }
        }
    }
    
    public static int generateXSLReport(final String viewName, final HttpReqWrapper requestWrapper, final FileOutputStream fileOutputStream) {
        return generateXSLReport(viewName, requestWrapper, fileOutputStream, null);
    }
    
    public static int generateXSLReport(final String viewName, final HttpReqWrapper requestWrapper, final FileOutputStream fileOutputStream, final Boolean isEmptyReportNeeded) {
        Connection con = null;
        try {
            if (isEmptyReportNeeded != null && !isEmptyReportNeeded && checkIfViewIsEmpty(viewName, requestWrapper)) {
                return 1112;
            }
            final ViewContext viewCtx = ViewContext.getViewContext((Object)viewName, (HttpServletRequest)requestWrapper);
            viewCtx.setRenderType(6);
            con = RelationalAPI.getInstance().getConnection();
            viewCtx.setTransientState("CONNECTION", (Object)con);
            final ExportAsExcel exportAsExcel = new ExportAsExcel();
            exportAsExcel.generateXLS(viewName, (HttpServletRequest)requestWrapper, fileOutputStream);
            return 1108;
        }
        catch (final Exception ex) {
            ScheduleReportUtil.out.log(Level.WARNING, "Schedule Report :Exception while creating xls file", ex);
            return 1109;
        }
        finally {
            try {
                if (con != null) {
                    con.close();
                }
            }
            catch (final Exception exc) {
                ScheduleReportUtil.out.log(Level.WARNING, "Exception while closing the connection for XSLV file", exc);
            }
        }
    }
    
    public static ArrayList getTableModelData(final String query) {
        ArrayList colList = null;
        TableModel tm = null;
        int rowCount = 0;
        final ArrayList rowDataList = new ArrayList();
        try {
            ScheduleReportUtil.out.log(Level.INFO, "Schedule Report :Entered into ScheduleReportUtil.getTableModelData()");
            tm = (TableModel)SQLQueryAPI.getAsTableModel(query, (String)null);
            rowCount = tm.getRowCount();
            final int colCount = tm.getColumnCount();
            final ArrayList colNameList = new ArrayList();
            int count = 0;
            for (int i = 0; i < colCount; ++i) {
                String columnName = tm.getColumnName(i);
                if (columnName.endsWith("_DATE_FORMAT")) {
                    columnName = columnName.substring(0, columnName.indexOf("_DATE_FORMAT"));
                }
                if (columnName.endsWith("_I18N_REMARK_" + count)) {
                    columnName = columnName.substring(0, columnName.indexOf("_I18N_REMARK_" + count));
                    ++count;
                }
                if (!columnName.contains("_I18N_REMARK_ARGS_")) {
                    colNameList.add(columnName);
                }
            }
            rowDataList.add(colNameList);
            if (tm != null && rowCount > 0) {
                for (int i = 0; i < rowCount; ++i) {
                    colList = new ArrayList();
                    count = 0;
                    for (int j = 0; j < colCount; ++j) {
                        if (tm.getColumnName(j).toUpperCase().endsWith("_DATE_FORMAT")) {
                            final Long val = (Long)tm.getValueAt(i, j);
                            if (val > 0L) {
                                final String colValue = Utils.getEventTime(val);
                                colList.add(colValue);
                            }
                            else if (tm.getColumnName(j).toUpperCase().endsWith("_I18N_REMARK_" + count)) {
                                final String remarks = (String)tm.getValueAt(i, j);
                                String remarksargs = null;
                                for (int k = 0; k < colCount; ++k) {
                                    if (tm.getColumnName(k).toUpperCase().endsWith("_I18N_REMARK_ARGS_" + count)) {
                                        remarksargs = (String)tm.getValueAt(i, k);
                                        break;
                                    }
                                }
                                final String i18nremarks = I18NUtil.transformRemarks(remarks, remarksargs);
                                colList.add(i18nremarks);
                                ++count;
                            }
                            else if (tm.getColumnName(j).toUpperCase().contains("_I18N_REMARK_ARGS_")) {}
                        }
                        else {
                            colList.add(tm.getValueAt(i, j));
                        }
                    }
                    rowDataList.add(colList);
                }
            }
            ScheduleReportUtil.out.log(Level.INFO, "Schedule Report :Executed query for query report successfully");
        }
        catch (final Exception e) {
            ScheduleReportUtil.out.log(Level.WARNING, "Error while executing  query for query report", e);
        }
        return rowDataList;
    }
    
    public static String getCSVString(final QueryReportAttrBean queryRepBean, final Logger out) {
        StringBuffer csvReport = null;
        String colValue = "";
        int dataListSize = 0;
        csvReport = new StringBuffer();
        dataListSize = queryRepBean.getDataList().size();
        final Level all = Level.ALL;
        out.log(Level.INFO, "dataListSize === " + dataListSize);
        for (int i = 0; i < dataListSize; ++i) {
            final Iterator itr = queryRepBean.getDataList().get(i).iterator();
            while (itr.hasNext()) {
                colValue = itr.next() + "";
                if (colValue.contains(",")) {
                    csvReport.append("\"" + colValue + "\"");
                }
                else {
                    csvReport.append(colValue);
                }
                csvReport.append(",");
            }
            csvReport.deleteCharAt(csvReport.length() - 1);
            csvReport.append("\n");
        }
        return csvReport.toString();
    }
    
    private static String replaceDateTemplate(String query) {
        Hashtable ht = null;
        ht = new Hashtable();
        try {
            if (query.indexOf("<from_today>") > 0) {
                ht = DateTimeUtil.determine_From_To_Times("today");
                query = query.replaceAll("<from_today>", ht.get("date1").toString());
            }
            if (query.indexOf("<to_today>") > 0) {
                ht = DateTimeUtil.determine_From_To_Times("today");
                query = query.replaceAll("<to_today>", ht.get("date2").toString());
            }
            if (query.indexOf("<from_thisweek>") > 0) {
                ht = DateTimeUtil.determine_From_To_Times("this_week");
                query = query.replaceAll("<from_thisweek>", ht.get("date1").toString());
            }
            if (query.indexOf("<to_thisweek>") > 0) {
                ht = DateTimeUtil.determine_From_To_Times("this_week");
                query = query.replaceAll("<to_thisweek>", ht.get("date2").toString());
            }
            if (query.indexOf("<from_lastweek>") > 0) {
                ht = DateTimeUtil.determine_From_To_Times("last_week");
                query = query.replaceAll("<from_lastweek>", ht.get("date1").toString());
            }
            if (query.indexOf("<to_lastweek>") > 0) {
                ht = DateTimeUtil.determine_From_To_Times("last_week");
                query = query.replaceAll("<to_lastweek>", ht.get("date2").toString());
            }
            if (query.indexOf("<from_thismonth>") > 0) {
                ht = DateTimeUtil.determine_From_To_Times("this_month");
                query = query.replaceAll("<from_thismonth>", ht.get("date1").toString());
            }
            if (query.indexOf("<to_thismonth>") > 0) {
                ht = DateTimeUtil.determine_From_To_Times("this_month");
                query = query.replaceAll("<to_thismonth>", ht.get("date2").toString());
            }
            if (query.indexOf("<from_lastmonth>") > 0) {
                ht = DateTimeUtil.determine_From_To_Times("last_month");
                query = query.replaceAll("<from_lastmonth>", ht.get("date1").toString());
            }
            if (query.indexOf("<to_lastmonth>") > 0) {
                ht = DateTimeUtil.determine_From_To_Times("last_month");
                query = query.replaceAll("<to_lastmonth>", ht.get("date2").toString());
            }
            if (query.indexOf("<from_thisquarter>") > 0) {
                ht = DateTimeUtil.determine_From_To_Times("current_quarter");
                query = query.replaceAll("<from_thisquarter>", ht.get("date1").toString());
            }
            if (query.indexOf("<to_thisquarter>") > 0) {
                ht = DateTimeUtil.determine_From_To_Times("current_quarter");
                query = query.replaceAll("<to_thisquarter>", ht.get("date2").toString());
            }
            if (query.indexOf("<from_lastquarter>") > 0) {
                ht = DateTimeUtil.determine_From_To_Times("last_quarter");
                query = query.replaceAll("<from_lastquarter>", ht.get("date1").toString());
            }
            if (query.indexOf("<to_lastquarter>") > 0) {
                ht = DateTimeUtil.determine_From_To_Times("last_quarter");
                query = query.replaceAll("<to_lastquarter>", ht.get("date2").toString());
            }
            if (query.indexOf("<from_yesterday>") > 0) {
                ht = DateTimeUtil.determine_From_To_Times("yesterday");
                query = query.replaceAll("<from_yesterday>", ht.get("date1").toString());
            }
            if (query.indexOf("<to_yesterday>") > 0) {
                ht = DateTimeUtil.determine_From_To_Times("yesterday");
                query = query.replaceAll("<to_yesterday>", ht.get("date2").toString());
            }
        }
        catch (final Exception e) {
            ScheduleReportUtil.out.log(Level.WARNING, "Error while replaceDateTemplate in query  ...", e);
        }
        return query;
    }
    
    public static String getTaskIdFromScheduleId(final String schedule_id) {
        final Persistence persistence = SyMUtil.getPersistence();
        final Criteria criteria = new Criteria(new Column("ScheduleRepToReportRel", "SCHEDULE_REP_ID"), (Object)schedule_id, 0);
        try {
            final DataObject dataObject = persistence.get("ScheduleRepToReportRel", criteria);
            if (!dataObject.isEmpty()) {
                final Row scheduleBackupRow = dataObject.getFirstRow("ScheduleRepToReportRel");
                final String task_id = scheduleBackupRow.get("TASK_ID").toString();
                return task_id;
            }
        }
        catch (final DataAccessException dae) {
            ScheduleReportUtil.out.log(Level.INFO, "Exception while retrieving task id from schedule id");
        }
        return null;
    }
    
    public static String getRedactTypeFromTaskId(final String task_id) {
        final Persistence persistence = SyMUtil.getPersistence();
        final Criteria criteria = new Criteria(new Column("ScheduleRepTask", "TASK_ID"), (Object)task_id, 0);
        try {
            final DataObject dataObject = persistence.get("ScheduleRepTask", criteria);
            if (!dataObject.isEmpty()) {
                final Row scheduleBackupRow = dataObject.getFirstRow("ScheduleRepTask");
                final String redact_type = scheduleBackupRow.get("REDACT_TYPE").toString();
                return redact_type;
            }
        }
        catch (final DataAccessException dae) {
            ScheduleReportUtil.out.log(Level.INFO, "Exception while retrieving task id from schedule id");
        }
        return null;
    }
    
    public static int setRedactTypeForAllScheduleTasks(final int redact_type) {
        try {
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("ScheduleRepTask");
            updateQuery.setUpdateColumn("REDACT_TYPE", (Object)redact_type);
            SyMUtil.getPersistence().update(updateQuery);
        }
        catch (final DataAccessException dae) {
            ScheduleReportUtil.out.log(Level.INFO, "Exception while retrieving task id from schedule id");
            return 1001;
        }
        return 1000;
    }
    
    public DataObject getSchedulerReportTaskProperties(final long task_id) throws DataAccessException {
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ScheduleRepToReportRel"));
            final Column SchedulerRepTask_task_id_column = Column.getColumn("ScheduleRepTask", "TASK_ID");
            final Criteria SchedulerRepTaskCriteria = new Criteria(SchedulerRepTask_task_id_column, (Object)task_id, 0);
            final Persistence persistence = SyMUtil.getPersistence();
            final DataObject ScheduleRepTaskDo = persistence.get("ScheduleRepTask", SchedulerRepTaskCriteria);
            ScheduleReportUtil.out.log(Level.FINE, "Data object ScheduleRepTask " + ScheduleRepTaskDo);
            return ScheduleRepTaskDo;
        }
        catch (final DataAccessException dae) {
            throw new DataAccessException();
        }
    }
    
    public Row getTaskDetailsProperties(final long task_id) throws DataAccessException {
        try {
            final Column TaskDetails_task_id_column = Column.getColumn("TaskDetails", "TASK_ID");
            final Criteria TaskDetailsCriteria = new Criteria(TaskDetails_task_id_column, (Object)task_id, 0);
            final Persistence persistence = SyMUtil.getPersistence();
            final DataObject taskName = persistence.get("TaskDetails", TaskDetailsCriteria);
            final Row taskRow = taskName.getFirstRow("TaskDetails");
            return taskRow;
        }
        catch (final DataAccessException dae) {
            throw new DataAccessException();
        }
    }
    
    public MailDetails addMailDetails(final long task_id, DefaultReportMailTheme mailTheme) throws DataAccessException {
        final MailDetails mailDetails = new MailDetails(null, null);
        try {
            ScheduleReportUtil.out.log(Level.INFO, "Schedule Report :Entered into ScheduleReportUtil.addMailDetails()for taskid=" + task_id);
            if (mailTheme == null) {
                mailTheme = new DefaultReportMailTheme();
            }
            final Row row = this.getSchedulerReportTaskProperties(task_id).getFirstRow("ScheduleRepTask");
            final Row taskRow = this.getTaskDetailsProperties(task_id);
            final String prodName = ProductUrlLoader.getInstance().getValue("displayname");
            final String email = (String)row.get("EMAIL_ADDRESS");
            final String subject = (String)row.get("SUBJECT");
            final String schedulerContent = (String)row.get("CONTENT");
            final String schedulerName = (String)taskRow.get("TASKNAME");
            String content = mailTheme.getHtmlStartTag() + mailTheme.getStyleTag() + mailTheme.getBodyStartTag();
            String reportName = mailTheme.getReportname_template();
            reportName = reportName.replaceAll("<report_name>", schedulerName);
            if (!schedulerContent.trim().isEmpty()) {
                final String schedulerTheme = mailTheme.getSchedulerContent_template();
                final String mailContent = schedulerTheme.replaceAll("<schedulerContent>", schedulerContent);
                content += mailContent.replaceAll("(?:\r\n|\r|\n)", "<br />");
            }
            final long taskId = (long)row.get("TASK_ID");
            final String criteriaDetails = ReportCriteriaUtil.getInstance().getReportCriteria(taskId);
            final String filterDetails = ReportCriteriaUtil.getInstance().getReportFilter(taskId);
            content += criteriaDetails;
            content += filterDetails;
            final String sender = ApiFactoryProvider.getMailSettingAPI().getMailSenderDetails().get("mail.fromAddress");
            if (sender != null) {
                mailDetails.fromAddress = sender;
            }
            else {
                mailDetails.fromAddress = "admin@desktopcentral.com";
            }
            mailDetails.toAddress = email;
            mailDetails.subject = subject;
            mailDetails.bodyContent = content + reportName;
        }
        catch (final Exception ex) {
            ScheduleReportUtil.out.log(Level.WARNING, "Schedule Report :Exception in sending mail", ex);
        }
        return mailDetails;
    }
    
    public int sendMail(final ArrayList attachments, final long task_id, final String scheduleReportPath, final int deliveryFormat, final DefaultReportMailTheme mailTheme, final MailDetails mailDetails) {
        return this.sendMail(attachments, task_id, scheduleReportPath, deliveryFormat, mailTheme, mailDetails, null);
    }
    
    public int sendMail(final ArrayList attachments, final long task_id, final String zipStoragePath, int deliveryFormat, DefaultReportMailTheme mailTheme, final MailDetails mailDetails, final String zipName) {
        try {
            final JSONObject taskRelevantDetails = new JSONObject();
            taskRelevantDetails.put("task_id", task_id);
            if (mailTheme == null) {
                mailTheme = new DefaultReportMailTheme();
            }
            long fileSize = 0L;
            String[] attach = null;
            final File repFile = new File(zipStoragePath);
            final File[] listFiles;
            final File[] fileList = listFiles = repFile.listFiles();
            for (final File f : listFiles) {
                fileSize += f.length();
            }
            ScheduleReportUtil.out.log(Level.INFO, "File Length :" + fileSize);
            ScheduleReportUtil.out.log(Level.INFO, "Delivery Format :" + deliveryFormat);
            if (deliveryFormat == 1) {
                attach = new String[attachments.size()];
                attachments.toArray(attach);
            }
            if (deliveryFormat == 2) {
                attach = new String[attachments.size()];
                attachments.toArray(attach);
                String attachment_path;
                if (zipName == null) {
                    attachment_path = zipStoragePath + File.separator + task_id + ".zip";
                }
                else {
                    attachment_path = zipStoragePath + File.separator + zipName + ".zip";
                }
                this.zipEntireReport(attach, task_id, zipStoragePath, attachment_path);
                attach = new String[] { attachment_path };
                final File zfile = new File(attachment_path);
                fileSize = zfile.length();
            }
            ScheduleReportUtil.out.log(Level.INFO, "filesize :" + fileSize);
            final Row row = this.getSchedulerReportTaskProperties(task_id).getFirstRow("ScheduleRepTask");
            final int attachLimit = (int)row.get("ATTACHMENT_LIMIT") * 1024 * 1024;
            ScheduleReportUtil.out.log(Level.INFO, "Attachlimit :" + attachLimit);
            if (fileSize > attachLimit) {
                ScheduleReportUtil.out.log(Level.INFO, "File size exceeded so sending as link");
                attach = null;
                deliveryFormat = 3;
            }
            if (deliveryFormat == 3) {
                HashMap<String, String> reports_id = null;
                if (ScheduleReportUtil.store_published_report_details_enabled) {
                    reports_id = PublishedReportHandler.addReportRelatedDetails(attachments, false);
                    if (reports_id == null) {
                        return 1001;
                    }
                }
                final String urlMessage = this.generateURL(mailTheme, attachments, reports_id, taskRelevantDetails);
                if (urlMessage == null) {
                    return 1401;
                }
                final String download_report = mailTheme.getDownload_url_template();
                mailDetails.bodyContent += download_report.replaceAll("<urlMesage>", urlMessage);
            }
            final String mailFooter = mailTheme.getMailFooter_template(ApiFactoryProvider.getUtilAccessAPI().getServerURLForMailNotification(), ProductUrlLoader.getInstance().getValue("displayname"));
            mailDetails.bodyContent = mailDetails.bodyContent + mailFooter + mailTheme.getBodyEndTag() + mailTheme.getHtmlEndTag();
            mailDetails.attachment = attach;
            ApiFactoryProvider.getMailSettingAPI().sendMail(mailDetails);
            ScheduleReportUtil.out.log(Level.INFO, "Schedule Report :mail send for task_id = " + task_id);
        }
        catch (final Exception ex) {
            ScheduleReportUtil.out.log(Level.WARNING, "Schedule Report :Exception in sending mail", ex);
            return 1301;
        }
        this.updateTaskToFinished(task_id);
        return 1300;
    }
    
    public Long getScheduleBackupStatusID(final Long task_id) {
        return this.getScheduleBackupStatusID(task_id, null, null);
    }
    
    public Long getScheduleBackupStatusID(final Long task_id, final Long user_id) {
        return this.getScheduleBackupStatusID(task_id, user_id, null);
    }
    
    public Long getScheduleBackupStatusID(final Long task_id, final Long user_id, final Long customer_id) {
        final SelectQuery queryForStatus = (SelectQuery)new SelectQueryImpl(Table.getTable("ScheduleBackupStatus"));
        queryForStatus.addSelectColumn(new Column("ScheduleBackupStatus", "SCHEDULE_BACKUP_STATUS_ID"));
        queryForStatus.addSelectColumn(new Column("ScheduleBackupStatus", "TASK_ID"));
        final Criteria criteria = null;
        if (task_id != null) {
            final Criteria taskIdCriteria = new Criteria(Column.getColumn("ScheduleBackupStatus", "TASK_ID"), (Object)task_id, 0);
            criteria.and(taskIdCriteria);
        }
        if (user_id != null) {
            final Criteria userIdCriteria = new Criteria(Column.getColumn("ScheduleBackupStatus", "USER_ID"), (Object)user_id, 0);
            criteria.and(userIdCriteria);
        }
        if (customer_id != null) {
            final Criteria customerIdCriteria = new Criteria(Column.getColumn("ScheduleBackupStatus", "CUSTOMER_ID"), (Object)customer_id, 0);
            criteria.and(customerIdCriteria);
        }
        if (criteria == null) {
            final Criteria defaultCriteria = new Criteria(Column.getColumn("ScheduleBackupStatus", "TASK_ID"), (Object)null, 0);
            final Criteria taskIdCriteria2 = new Criteria(Column.getColumn("ScheduleBackupStatus", "USER_ID"), (Object)null, 0);
            final Criteria customerIdCriteria2 = new Criteria(Column.getColumn("ScheduleBackupStatus", "CUSTOMER_ID"), (Object)null, 0);
            criteria.and(defaultCriteria);
            criteria.and(taskIdCriteria2);
            criteria.and(customerIdCriteria2);
        }
        try {
            queryForStatus.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(queryForStatus);
            final Row row = dataObject.getFirstRow("ScheduleBackupStatus");
            return (Long)row.get("SCHEDULE_BACKUP_STATUS_ID");
        }
        catch (final DataAccessException dae) {
            ScheduleReportUtil.out.log(Level.INFO, "Exception while getting schedule backup status id" + dae);
            return null;
        }
    }
    
    public List getViewDetailsForReportGeneration(final long task_id) {
        final List viewList = new ArrayList();
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ScheduleRepToReportRel"));
            final Column column_report_id = Column.getColumn("ScheduleRepToReportRel", "REPORT_ID");
            final Column column_task_id = Column.getColumn("ScheduleRepToReportRel", "TASK_ID");
            final Column column_report_type = Column.getColumn("ScheduleRepToReportRel", "REPORT_TYPE");
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            final Criteria taskIdCriteria = new Criteria(column_task_id, (Object)task_id, 0);
            final Criteria reportTypeCriteria = new Criteria(column_report_type, (Object)1, 0);
            final Criteria criteria = taskIdCriteria.and(reportTypeCriteria);
            query.setCriteria(criteria);
            final Join join = new Join("ScheduleRepToReportRel", "ViewParams", new String[] { "REPORT_ID" }, new String[] { "VIEW_ID" }, 2);
            query.addJoin(join);
            final DataObject dataObject = SyMUtil.getPersistence().get(query);
            ScheduleReportUtil.out.log(Level.FINE, "query for reports for predefined reports" + query);
            ScheduleReportUtil.out.log(Level.FINE, "query for reports for predefined reports" + query);
            final Iterator viewParamsIterator = dataObject.getRows("ViewParams");
            while (viewParamsIterator.hasNext()) {
                final Properties viewProperties = new Properties();
                final Row tableRows = viewParamsIterator.next();
                final String viewName = (String)tableRows.get("VIEW_NAME");
                final String title = (String)tableRows.get("TITLE");
                final int view_id = (int)tableRows.get("VIEW_ID");
                ScheduleReportUtil.out.log(Level.FINE, "Schedule Report :tables in DO:" + dataObject.getTableNames().toString());
                final String scheduleRepID = String.valueOf(dataObject.getRow("ScheduleRepToReportRel", new Criteria(column_report_id, (Object)view_id, 0)).get("SCHEDULE_REP_ID"));
                ScheduleReportUtil.out.log(Level.FINE, "Schedule Report :scheduleRepID:" + scheduleRepID);
                ((Hashtable<String, String>)viewProperties).put("title", title);
                ((Hashtable<String, String>)viewProperties).put("name", viewName);
                ((Hashtable<String, Integer>)viewProperties).put("id", view_id);
                ((Hashtable<String, String>)viewProperties).put("scheduleRepID", scheduleRepID);
                viewList.add(viewProperties);
            }
        }
        catch (final DataAccessException dae) {
            ScheduleReportUtil.out.log(Level.WARNING, "Schedule Report :Exception in getting files", (Throwable)dae);
        }
        return viewList;
    }
    
    public List getViewDetailsForCustomReportGeneration(final Long task_id) {
        final List view_details = new ArrayList();
        try {
            ScheduleReportUtil.out.log(Level.INFO, "Schedule Report :Entered into ScheduleReportUtil.getViewDetailsForCustomReportGeneration()");
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ScheduleRepToReportRel"));
            final Column column_report_id = Column.getColumn("ScheduleRepToReportRel", "REPORT_ID");
            final Column column_task_id = Column.getColumn("ScheduleRepToReportRel", "TASK_ID");
            final Column column_report_type = Column.getColumn("ScheduleRepToReportRel", "REPORT_TYPE");
            final Column column_cr_view_name = Column.getColumn("CRSaveViewDetails", "CRVIEWNAME");
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            final Criteria task_id_criteria = new Criteria(column_task_id, (Object)task_id, 0);
            final Criteria cr_criteria = new Criteria(column_report_type, (Object)2, 0);
            final Criteria criteria = task_id_criteria.and(cr_criteria);
            query.setCriteria(criteria);
            final Join join = new Join("ScheduleRepToReportRel", "CRSaveViewDetails", new String[] { "REPORT_ID" }, new String[] { "CRSAVEVIEW_ID" }, 2);
            query.addJoin(join);
            ScheduleReportUtil.out.log(Level.FINE, "query for custom reports " + query);
            final DataObject dataObject = SyMUtil.getPersistence().get(query);
            final Iterator viewConfIterator = dataObject.getRows("CRSaveViewDetails");
            while (viewConfIterator.hasNext()) {
                final Row tableRows = viewConfIterator.next();
                final String title = (String)tableRows.get("DISPLAY_CRVIEWNAME");
                final String viewName = (String)tableRows.get("CRVIEWNAME");
                final Long viewId = (Long)tableRows.get("CRSAVEVIEW_ID");
                final String scheduleRepID = String.valueOf(dataObject.getRow("ScheduleRepToReportRel", new Criteria(column_report_id, (Object)viewId, 0)).get("SCHEDULE_REP_ID"));
                final Properties view_prop = new Properties();
                ((Hashtable<String, String>)view_prop).put("title", title);
                ((Hashtable<String, String>)view_prop).put("name", viewName);
                ((Hashtable<String, Long>)view_prop).put("id", viewId);
                ((Hashtable<String, String>)view_prop).put("scheduleRepID", scheduleRepID);
                ((Hashtable<String, String>)view_prop).put("reportType", "2");
                view_details.add(view_prop);
            }
        }
        catch (final Exception ex) {
            ScheduleReportUtil.out.log(Level.WARNING, "Schedule Report :Exception in getting custom files", ex);
        }
        return view_details;
    }
    
    public String getReportFileName(final String server_home, final Properties viewDetails, final String format) {
        try {
            String fileName = "";
            final File repFile = new File(server_home);
            if (!repFile.exists()) {
                repFile.mkdirs();
            }
            String viewTitle = ((Hashtable<K, String>)viewDetails).get("title");
            viewTitle = I18N.getMsg(viewTitle, new Object[0]);
            if (viewTitle.contains("/")) {
                viewTitle = viewTitle.replace("/", "or");
            }
            if (viewTitle.contains("\"")) {
                viewTitle = viewTitle.replace("\"", "");
            }
            viewTitle = new String(viewTitle.getBytes(), "UTF-8");
            fileName = server_home + viewTitle + "." + format;
            return fileName;
        }
        catch (final Exception e) {
            ScheduleReportUtil.out.log(Level.WARNING, "Schedule Report :Exception in generating files", e);
            return null;
        }
    }
    
    public String[] getLoginName(final Long user_id) throws DataAccessException {
        ScheduleReportUtil.out.log(Level.INFO, "Schedule Report :Entered into ScheduleReportUtil.getLoginName() for user ID" + user_id.toString());
        String loginName = null;
        String domainName = null;
        String[] loginDetails = null;
        final Criteria criteria = new Criteria(Column.getColumn("AaaLogin", "USER_ID"), (Object)user_id, 0);
        final DataObject dataObject = SyMUtil.getPersistence().get("AaaLogin", criteria);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getRow("AaaLogin");
            loginName = (String)row.get("NAME");
            domainName = (String)row.get("DOMAINNAME");
            loginDetails = new String[] { loginName, domainName };
        }
        ScheduleReportUtil.out.log(Level.INFO, "Schedule Report :Entered into ScheduleReportUtil.getLoginName() for user ID" + loginName + "domain name" + domainName);
        return loginDetails;
    }
    
    public int zipEntireReport(final String[] fileNames, final Long task_id, final String scheduleReportPath) throws Exception {
        return this.zipEntireReport(fileNames, task_id, scheduleReportPath, scheduleReportPath + File.separator + task_id + ".zip");
    }
    
    public int zipEntireReport(final String[] fileNames, final Long task_id, final String scheduleReportPath, final String outputZipFile) throws Exception {
        ZipOutputStream output = null;
        try {
            ScheduleReportUtil.out.log(Level.INFO, "Schedule Report :Entered into ScheduleReportUtil.zipEntireReport()");
            final File directory = new File(scheduleReportPath);
            final byte[] buffer = new byte[4096];
            output = new ZipOutputStream(new FileOutputStream(outputZipFile));
            for (final String file : fileNames) {
                final String fileName = new File(file).getName();
                final File f = new File(directory, fileName);
                if (!f.isDirectory()) {
                    final FileInputStream in = new FileInputStream(f);
                    try {
                        final ZipEntry entry = new ZipEntry(task_id + File.separator + fileName);
                        output.putNextEntry(entry);
                        int bytesRead;
                        while ((bytesRead = in.read(buffer)) != -1) {
                            output.write(buffer, 0, bytesRead);
                        }
                    }
                    catch (final Exception ex) {
                        ScheduleReportUtil.out.log(Level.WARNING, "Schedule Report :Exception in zipping files" + ex);
                    }
                    finally {
                        in.close();
                    }
                }
            }
            ScheduleReportUtil.out.log(Level.INFO, "Schedule Report :Entire report is zipped");
            return 1110;
        }
        catch (final Exception ex2) {
            ScheduleReportUtil.out.log(Level.INFO, "Exception in zipping report", ex2);
            return 1111;
        }
        finally {
            if (output != null) {
                output.close();
            }
        }
    }
    
    public String generateURL(final DefaultReportMailTheme mailTheme, final ArrayList<String> report_paths, final HashMap<String, String> reports_id, final JSONObject taskRelevantDetails) throws Exception {
        StringBuilder sb = new StringBuilder();
        try {
            ScheduleReportUtil.out.log(Level.INFO, "Schedule Report :Entered into ScheduleReportUtil.zipEntireReport()");
            sb.append(mailTheme.getTableStartTag() + mailTheme.getTableBodyTag());
            sb.append(mailTheme.getTableTitleRow());
            if (ScheduleReportUtil.store_published_report_details_enabled) {
                sb = this.getUrlLinkPathFromDB(reports_id, mailTheme, sb);
            }
            else {
                sb = this.getURLLinkFromPath(mailTheme, report_paths, sb, taskRelevantDetails);
            }
            if (sb == null) {
                return null;
            }
            sb.append(mailTheme.getTableEndTag());
            ScheduleReportUtil.out.log(Level.INFO, "url in string format" + (Object)sb);
        }
        catch (final Exception ex) {
            ScheduleReportUtil.out.log(Level.WARNING, "Schedule Report :Exception in creating url", ex);
        }
        return sb.toString();
    }
    
    public StringBuilder getURLLinkFromPath(final DefaultReportMailTheme mailTheme, final ArrayList<String> report_paths, final StringBuilder mailContent, final JSONObject taskRelevantDetails) {
        int oddEven = 1;
        String row_theme = "";
        try {
            for (final String filePath : report_paths) {
                row_theme = ((oddEven % 2 == 0) ? mailTheme.getEvenRowTheme() : mailTheme.getOddRowTheme());
                ++oddEven;
                mailContent.append(row_theme);
                final File file = new File(filePath);
                final String fileName = file.getName();
                final String fileName_Theme_template = mailTheme.getFileNameTheme() + mailTheme.getCellEndTag();
                mailContent.append(fileName_Theme_template.replaceAll("<file_name>", fileName));
                taskRelevantDetails.put("file_name", (Object)file);
                final ReportsProductInvoker reportsProductInvoker = (ReportsProductInvoker)Class.forName(ProductClassLoader.getSingleImplProductClass("DM_REPORT_INVOKER_CLASS")).newInstance();
                final String link = reportsProductInvoker.getDownloadUrlForFile(taskRelevantDetails);
                final String downloadLinkTemplate = mailTheme.getDownloadLinkTemplate();
                final String fileSize = mailTheme.getFileSizeTemplate();
                mailContent.append(downloadLinkTemplate.replaceAll("<download_link>", link) + mailTheme.getSpace() + fileSize.replaceAll("<file_size>", this.getFileSize(new Double((double)file.length()))) + mailTheme.getCellEndTag() + mailTheme.getRowEndTag());
                CustomerInfoUtil.getInstance();
                if (CustomerInfoUtil.isSAS()) {
                    ApiFactoryProvider.getFileAccessAPI().writeFile(file.getCanonicalPath(), FileAccessUtil.getFileAsInputStream(file.getCanonicalPath()));
                }
            }
        }
        catch (final Exception ex) {
            ScheduleReportUtil.out.log(Level.WARNING, "Schedule Report :Exception in creating url", ex);
        }
        return mailContent;
    }
    
    public StringBuilder getUrlLinkPathFromDB(final HashMap<String, String> reports_id, final DefaultReportMailTheme mailTheme, final StringBuilder mailContent) {
        int oddEven = 1;
        String row_theme = "";
        String serverUrl = null;
        try {
            final String serverURLClass = ProductClassLoader.getSingleImplProductClass("DM_UTIL_ACCESS_API_CLASS");
            final UtilAccessAPI utilAccessAPI = (UtilAccessAPI)Class.forName(serverURLClass).newInstance();
            serverUrl = utilAccessAPI.getServerURL();
        }
        catch (final Exception exception) {
            ScheduleReportUtil.out.log(Level.INFO, "Exception while loading server url" + exception);
            return null;
        }
        try {
            final Iterator itr = reports_id.keySet().iterator();
            while (itr.hasNext()) {
                row_theme = ((oddEven % 2 == 0) ? mailTheme.getEvenRowTheme() : mailTheme.getOddRowTheme());
                ++oddEven;
                mailContent.append(row_theme);
                final String report_id = itr.next();
                String fileName = reports_id.get(report_id);
                final File file = new File(fileName);
                fileName = file.getName();
                final String fileName_Theme_template = mailTheme.getFileNameTheme() + mailTheme.getCellEndTag();
                mailContent.append(fileName_Theme_template.replaceAll("<file_name>", fileName));
                final String url = ScheduleReportUtil.publishReportDetails.get("url").toString();
                final String link = serverUrl + "/" + url.replace("<report_id>", report_id);
                final String downloadLinkTemplate = mailTheme.getDownloadLinkTemplate();
                final String fileSize = mailTheme.getFileSizeTemplate();
                mailContent.append(downloadLinkTemplate.replaceAll("<download_link>", link) + mailTheme.getSpace() + fileSize.replaceAll("<file_size>", this.getFileSize(new Double((double)file.length()))) + mailTheme.getCellEndTag() + mailTheme.getRowEndTag());
                CustomerInfoUtil.getInstance();
                if (CustomerInfoUtil.isSAS()) {
                    ApiFactoryProvider.getFileAccessAPI().writeFile(file.getCanonicalPath(), FileAccessUtil.getFileAsInputStream(file.getCanonicalPath()));
                }
            }
        }
        catch (final DataAccessException dae) {
            ScheduleReportUtil.out.log(Level.INFO, "Exception while iterating rows", (Throwable)dae);
        }
        catch (final Exception e) {
            ScheduleReportUtil.out.log(Level.INFO, "Exception while writing reports in DFS", e);
        }
        return mailContent;
    }
    
    private String getFileSize(Double fileSize) throws Exception {
        String actualFileSize = "";
        actualFileSize = ((fileSize > 1024.0 && fileSize < 1048576.0) ? (String.valueOf(fileSize / 1024.0) + " KB") : ((fileSize > 1048576.0 && fileSize < 1.073741824E9) ? (String.valueOf(fileSize / 1048576.0) + " MB") : ((fileSize > 1.073741824E9) ? (String.valueOf(fileSize / 1.073741824E9) + " GB") : (String.valueOf(fileSize) + " Bytes"))));
        if (!actualFileSize.contains("Bytes")) {
            final String[] sp = actualFileSize.split(" ");
            final DecimalFormat df = new DecimalFormat("####.#");
            fileSize = df.parse(df.format(new Double(sp[0].trim()))).doubleValue();
            actualFileSize = String.valueOf(fileSize) + " " + sp[1];
        }
        return actualFileSize;
    }
    
    public List getViewDetailsForQueryReportGeneration(final Long task_id) {
        try {
            ScheduleReportUtil.out.log(Level.INFO, "Schedule Report :Entered into ScheduleReportUtil.getViewDetailsForQueryReportGeneration()");
            final List viewdetails = new ArrayList();
            final SelectQuery Query = (SelectQuery)new SelectQueryImpl(Table.getTable("ScheduleRepToReportRel"));
            final Column column_task_id = Column.getColumn("ScheduleRepToReportRel", "TASK_ID");
            final Column column_report_type = Column.getColumn("ScheduleRepToReportRel", "REPORT_TYPE");
            Query.addSelectColumn(Column.getColumn((String)null, "*"));
            final Criteria task_id_criteria = new Criteria(column_task_id, (Object)task_id, 0);
            final Criteria report_type_criteria = new Criteria(column_report_type, (Object)3, 0);
            final Criteria criteria = task_id_criteria.and(report_type_criteria);
            Query.setCriteria(criteria);
            final Join join = new Join("ScheduleRepToReportRel", "CRSaveViewDetails", new String[] { "REPORT_ID" }, new String[] { "CRSAVEVIEW_ID" }, 2);
            Query.addJoin(join);
            final DataObject dataObject = SyMUtil.getPersistence().get(Query);
            ScheduleReportUtil.out.log(Level.FINE, "query for reports for query reports" + Query);
            final Iterator iter = dataObject.getRows("CRSaveViewDetails");
            while (iter.hasNext()) {
                final Row tableRows = iter.next();
                final String viewName = (String)tableRows.get("DISPLAY_CRVIEWNAME");
                final String queryToExec = (String)tableRows.get("QR_QUERY");
                final Long vi_id = (Long)tableRows.get("CRSAVEVIEW_ID");
                final Properties viewprop = new Properties();
                ((Hashtable<String, String>)viewprop).put("query", queryToExec);
                ((Hashtable<String, String>)viewprop).put("name", viewName);
                ((Hashtable<String, Long>)viewprop).put("id", vi_id);
                viewdetails.add(viewprop);
            }
            return viewdetails;
        }
        catch (final Exception ex) {
            ScheduleReportUtil.out.log(Level.WARNING, "Schedule Report :Exception in creating query files", ex);
            return null;
        }
    }
    
    public int getReportAsCSV(final String query, final QueryReportAttrBean queryRepBean, final String title, final Long task_id, final String time, final String schedulerReportPath, final ArrayList files) {
        return this.getReportAsCSV(query, queryRepBean, title, task_id, time, schedulerReportPath, files, null);
    }
    
    public int getReportAsCSV(final String query, final QueryReportAttrBean queryRepBean, final String title, final Long task_id, final String time, final String schedulerReportPath, final ArrayList files, final Boolean isEmptyReportNeeded) {
        FileOutputStream fos = null;
        try {
            ScheduleReportUtil.out.log(Level.INFO, "Schedule Report :Entered into ScheduleReportUtil.getReportAsCSV()");
            final String report_file = schedulerReportPath + ScheduleReportUtil.date + File.separator + task_id + time + File.separator;
            final File repFile = new File(report_file);
            if (!repFile.exists()) {
                repFile.mkdirs();
            }
            final String fileName = report_file + title + ".csv";
            if (!new File(fileName).getCanonicalPath().startsWith(new File(report_file).getCanonicalPath())) {
                throw new IOException("Filepath is outside of the target dir: " + fileName);
            }
            final TableModel tm = null;
            final File f = new File(fileName);
            fos = new FileOutputStream(f);
            final String modifiedQuery = QueryReportHandler.modifyQuery(query);
            String queryType = QueryReportHandler.getQueryType(modifiedQuery).toLowerCase();
            queryType = queryType.trim();
            int rowcount;
            final int rowConstant = rowcount = 7500;
            String tempquery = null;
            int recordindex = 0;
            while (true) {
                final Range range = new Range(recordindex, rowConstant);
                tempquery = QueryReportHandler.setOrderByInQuery(modifiedQuery, range, queryType);
                if (recordindex == 0) {
                    rowcount = QueryReportHandler.getTableModelData(tempquery, true, queryRepBean);
                    if (isEmptyReportNeeded != null && !isEmptyReportNeeded && rowcount == 0) {
                        f.delete();
                        return 1112;
                    }
                }
                else {
                    rowcount = QueryReportHandler.getTableModelData(tempquery, false, queryRepBean);
                }
                final String csvString = getCSVString(queryRepBean, ScheduleReportUtil.out);
                if (rowcount < rowConstant) {
                    fos.write(csvString.getBytes());
                    fos.flush();
                    files.add(fileName);
                    ScheduleReportUtil.out.log(Level.INFO, "Export CSV is successfully completed");
                }
                else {
                    fos.write(csvString.getBytes());
                    fos.flush();
                    queryRepBean.getDataList().clear();
                    recordindex += rowConstant;
                }
            }
        }
        catch (final Exception ex) {
            queryRepBean.setSqlError(ex.getMessage());
            ScheduleReportUtil.out.log(Level.WARNING, "Error while generating CSV...", ex);
            return 1107;
        }
        finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            }
            catch (final Exception ex2) {
                ScheduleReportUtil.out.log(Level.WARNING, "Error while closing outputstream...", ex2);
            }
        }
        ScheduleReportUtil.out.log(Level.INFO, "Schedule Report :CSV file generated for query report");
        return 1106;
    }
    
    public int updateTaskToFinished(final Long taskID) {
        try {
            ScheduleReportUtil.out.log(Level.INFO, "Schedule Report :Entered into ScheduleReportUtil.updateTaskToFinished()");
            final Column col = Column.getColumn("TaskDetails", "TASK_ID");
            final Criteria cri = new Criteria(col, (Object)taskID, 0);
            final DataObject dobj = SyMUtil.getPersistence().get("TaskDetails", cri);
            if (!dobj.isEmpty()) {
                final Row row = dobj.getRow("TaskDetails");
                row.set("COMPLETIONTIME", (Object)System.currentTimeMillis());
                final String frequency = SchedulerInfo.getScheduleType(taskID);
                if (!"Daily".equals(frequency) && !"Weekly".equals(frequency) && !"Monthly".equals(frequency)) {
                    row.set("STATUS", (Object)"COMPLETED");
                }
                else {
                    row.set("STATUS", (Object)"SCHEDULED");
                }
                dobj.updateRow(row);
                synchronized (dobj) {
                    SyMUtil.getPersistence().update(dobj);
                }
            }
        }
        catch (final DataAccessException ex) {
            ScheduleReportUtil.out.log(Level.WARNING, "Caught exception while updating schedule reports task details : ", (Throwable)ex);
            return 1001;
        }
        catch (final Exception ex2) {
            ScheduleReportUtil.out.log(Level.WARNING, "Caught exception while updating schedule reports task details : ", ex2);
            return 1001;
        }
        return 1000;
    }
    
    public Hashtable getScheduledTaskDetails(final Long taskID) {
        final Hashtable taskDetailHash = new Hashtable();
        try {
            ScheduleReportUtil.out.log(Level.INFO, "Schedule Report :Entered into ScheduleReportUtil.getScheduledTaskDetails()");
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("TaskDetails"));
            query.addJoin(new Join("TaskDetails", "ScheduleRepTask", new String[] { "TASK_ID" }, new String[] { "TASK_ID" }, 2));
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            final Criteria criteria = new Criteria(Column.getColumn("TaskDetails", "TASK_ID"), (Object)taskID, 0);
            query.setCriteria(criteria);
            final DataObject dobj = SyMUtil.getPersistence().get(query);
            if (dobj.containsTable("ScheduleRepTask")) {
                final Row ScheduleRepTask = dobj.getRow("ScheduleRepTask");
                if (ScheduleRepTask != null) {
                    taskDetailHash.put("DELIVERY_FORMAT", ScheduleRepTask.get("DELIVERY_FORMAT"));
                    taskDetailHash.put("REPORT_FORMAT", ScheduleRepTask.get("REPORT_FORMAT"));
                    final String mail_id = ScheduleRepTask.get("EMAIL_ADDRESS").toString();
                    if (mail_id != null) {
                        taskDetailHash.put("EMAIL_ADDRESS", mail_id);
                    }
                    final String subject = (String)ScheduleRepTask.get("SUBJECT");
                    if (subject != null) {
                        taskDetailHash.put("SUBJECT", subject);
                    }
                    final String content = (String)ScheduleRepTask.get("CONTENT");
                    if (content != null) {
                        taskDetailHash.put("CONTENT", content);
                    }
                    final String desc = (String)ScheduleRepTask.get("DESCRIPTION");
                    if (desc != null) {
                        taskDetailHash.put("DESCRIPTION", desc);
                    }
                    taskDetailHash.put("ATTACHMENT_LIMIT", ScheduleRepTask.get("ATTACHMENT_LIMIT"));
                    taskDetailHash.put("DELIVERY_FLAG", ScheduleRepTask.get("DELIVERY_FLAG"));
                    taskDetailHash.put("USE_NAT_LINK", ScheduleRepTask.get("USE_NAT_LINK"));
                    taskDetailHash.put("IS_EMPTY_REPORT_NEEDED", ScheduleRepTask.get("IS_EMPTY_REPORT_NEEDED"));
                }
            }
            if (dobj.containsTable("TaskDetails")) {
                final Row taskDetailsRow = dobj.getRow("TaskDetails");
                if (taskDetailsRow != null) {
                    taskDetailHash.put("TASK_ID", taskDetailsRow.get("TASK_ID"));
                    taskDetailHash.put("TASKNAME", taskDetailsRow.get("TASKNAME"));
                    taskDetailHash.put("OWNER", taskDetailsRow.get("OWNER"));
                    Long creationTime = (Long)taskDetailsRow.get("CREATIONTIME");
                    if (creationTime == null) {
                        creationTime = -1L;
                    }
                    Long startTime = (Long)taskDetailsRow.get("STARTTIME");
                    if (startTime == null) {
                        startTime = -1L;
                    }
                    Long completionTime = (Long)taskDetailsRow.get("COMPLETIONTIME");
                    if (completionTime == null) {
                        completionTime = -1L;
                    }
                    taskDetailHash.put("CREATIONTIME", creationTime);
                    taskDetailHash.put("STARTTIME", startTime);
                    taskDetailHash.put("COMPLETIONTIME", completionTime);
                }
            }
        }
        catch (final Exception exp) {
            ScheduleReportUtil.out.log(Level.WARNING, "Exception while shutdown task details...", exp);
        }
        return taskDetailHash;
    }
    
    public int deleteADAndUserLogonReportScheduledReports(final Integer[] reportList) {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ScheduleRepToReportRel"));
        query.addJoin(new Join("ScheduleRepToReportRel", "ViewParams", new String[] { "REPORT_ID" }, new String[] { "VIEW_ID" }, 2));
        query.addJoin(new Join("ViewParams", "ReportSubCategory", new String[] { "SUB_CATEGORY_ID" }, new String[] { "SUB_CATEGORY_ID" }, 2));
        query.addJoin(new Join("ReportSubCategory", "ReportCategory", new String[] { "CATEGORY_ID" }, new String[] { "CATEGORY_ID" }, 2));
        final Criteria userAndADReportCrit = new Criteria(Column.getColumn("ReportCategory", "CATEGORY_ID"), (Object)reportList, 8);
        query.setCriteria(userAndADReportCrit);
        query.addSelectColumn(new Column("ScheduleRepToReportRel", "TASK_ID"));
        query.setDistinct(true);
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        DataSet ds = null;
        final List list = new ArrayList();
        try {
            conn = relapi.getConnection();
            ds = relapi.executeQuery((Query)query, conn);
            while (ds.next()) {
                final Long taskId = (Long)ds.getValue(1);
                list.add(taskId);
            }
            if (ds != null) {
                ds.close();
            }
            if (conn != null) {
                conn.close();
            }
            for (final Long taskId2 : list) {
                SchedulerInfo.getInstance().removeScheduleForTask(taskId2);
            }
            return 1000;
        }
        catch (final Exception ee) {
            ee.printStackTrace();
            try {
                if (ds != null) {
                    ds.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
        finally {
            try {
                if (ds != null) {
                    ds.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception e2) {
                e2.printStackTrace();
            }
        }
        return 1001;
    }
    
    public DataObject getScheduleScheduleBackupStatusDO() {
        final SelectQuery queryForStatus = (SelectQuery)new SelectQueryImpl(Table.getTable("ScheduleBackupStatus"));
        queryForStatus.addSelectColumn(new Column((String)null, "*"));
        try {
            final DataObject dataObject = SyMUtil.getPersistence().get(queryForStatus);
            return dataObject;
        }
        catch (final DataAccessException e) {
            ScheduleReportUtil.out.log(Level.INFO, "Exception while retrieving schedule status details" + e);
            return null;
        }
    }
    
    public static long getScheduleReportBackupPeriod(final Row scheduleBackUpRow) {
        final int noOfDays = (int)scheduleBackUpRow.get("HISTORY_PERIOD");
        final long days_in_millis = noOfDays * 24 * 60 * 60 * 1000L;
        final Long currTime = new Long(System.currentTimeMillis());
        return currTime - days_in_millis;
    }
    
    public int deleteScheduleReport() {
        final Logger deleteLogger = Logger.getLogger("QueryExecutorLogger");
        try {
            final DataObject dataObject = this.getScheduleScheduleBackupStatusDO();
            final Row backupInfoRow = dataObject.getRow("ScheduleBackupStatus");
            if (backupInfoRow != null) {
                final long days_in_long = getScheduleReportBackupPeriod(backupInfoRow);
                final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ScheduleBackupDetails"));
                query.addSelectColumn(new Column((String)null, "*"));
                final Criteria criteria = new Criteria(Column.getColumn("ScheduleBackupDetails", "GENRATED_TIME"), (Object)days_in_long, 7);
                query.setCriteria(criteria);
                final DataObject data = SyMUtil.getPersistence().get(query);
                deleteLogger.log(Level.FINE, "Query to get details from ScheduleBackupDetails to delete files", query);
                final Iterator iterator = data.getRows("ScheduleBackupDetails");
                while (iterator.hasNext()) {
                    final Row dbBackFilesRow = iterator.next();
                    String fileName = (String)dbBackFilesRow.get("FILE_NAME");
                    try {
                        fileName = fileName.replaceAll("\\\\", "\\\\\\\\");
                        final boolean isDeleted = ((FileAccessAPI)Class.forName(ProductClassLoader.getSingleImplProductClass("DM_FILE_ACCESS_API_CLASS")).newInstance()).deleteDirectory(fileName);
                        if (!isDeleted) {
                            continue;
                        }
                        deleteLogger.log(Level.FINEST, "file: " + fileName + " deleted successfully");
                    }
                    catch (final Exception e) {
                        deleteLogger.log(Level.WARNING, "Exception while deleting file", e);
                    }
                }
                DataAccess.delete(criteria);
                if (ScheduleReportUtil.store_published_report_details_enabled) {
                    PublishedReportHandler.deletePublishedReportDetailsByGeneratedTime();
                }
            }
            return FrameworkStatusCodes.SUCCESS_RESPONSE_CODE;
        }
        catch (final Exception ex) {
            deleteLogger.log(Level.WARNING, "Exception in delete file class", ex);
            return FrameworkStatusCodes.UNPROCESSABLE_DATA_CODE;
        }
    }
    
    public ArrayList getReportList() {
        try {
            final DataObject dobj = SyMUtil.getPersistence().get("ExcludeScheduleReports", (Criteria)null);
            final Iterator iterator = dobj.getRows("ExcludeScheduleReports");
            final ArrayList excludedReports = new ArrayList();
            while (iterator.hasNext()) {
                final Row tableRows = iterator.next();
                final int view_id = (int)tableRows.get("VIEW_ID");
                excludedReports.add(view_id);
            }
            ScheduleReportUtil.out.log(Level.INFO, "report:" + excludedReports);
            return excludedReports;
        }
        catch (final DataAccessException dae) {
            return null;
        }
    }
    
    public long getviewID(final String report_id) {
        final String rep_id = report_id.substring(6);
        final long view_id = new Long(rep_id);
        return view_id;
    }
    
    public long getType(final String report_id) {
        final String checkbox_id = report_id.substring(0, 6);
        long view_id;
        if (checkbox_id.equalsIgnoreCase("pchbox")) {
            view_id = 1L;
        }
        else if (checkbox_id.equalsIgnoreCase("cchbox")) {
            view_id = 2L;
        }
        else {
            view_id = 3L;
        }
        return view_id;
    }
    
    public boolean isReportNameExist(final String reportName) {
        boolean nameExist = false;
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("TaskDetails"));
            final Column c1 = Column.getColumn("TaskDetails", "TASKNAME");
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            final Criteria cri = new Criteria(c1, (Object)reportName, 0, false);
            query.setCriteria(cri);
            final DataObject dataObject = SyMUtil.getPersistence().get(query);
            nameExist = !dataObject.isEmpty();
        }
        catch (final DataAccessException dae) {
            ScheduleReportUtil.out.log(Level.INFO, "Exception while checking report name existence" + dae);
        }
        return nameExist;
    }
    
    public ArrayList getPredefinedRreportId(final ArrayList preDefinedRep) {
        final ArrayList report_id = new ArrayList();
        try {
            for (int i = 0; i < preDefinedRep.size(); ++i) {
                final String subcatid = preDefinedRep.get(i).toString();
                report_id.add(this.getScheduleReportId(subcatid));
            }
        }
        catch (final Exception dae) {
            ScheduleReportUtil.out.log(Level.INFO, "Exception while processing report sub category");
        }
        return report_id;
    }
    
    public List getList(final String parameter) {
        final String[] strArray = parameter.split(",");
        final int count = strArray.length;
        final List list = new ArrayList();
        for (int i = 0; i < count; ++i) {
            final Long id = new Long(strArray[i]);
            list.add(id);
        }
        return list;
    }
    
    public Integer getScheduleReportId(final String subCatId) {
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ReportSubCategory"));
            final Column viewIdColumn = Column.getColumn("ViewParams", "VIEW_ID");
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            final Criteria criteria = new Criteria(viewIdColumn, (Object)subCatId, 0);
            final Join join = new Join("ReportSubCategory", "ViewParams", new String[] { "SUB_CATEGORY_ID" }, new String[] { "SUB_CATEGORY_ID" }, 2);
            query.addJoin(join);
            query.setCriteria(criteria);
            final Persistence persis = SyMUtil.getPersistence();
            final DataObject TaskDo = persis.get(query);
            ScheduleReportUtil.out.log(Level.FINE, "query for getting report id" + query);
            if (!TaskDo.isEmpty()) {
                final Row categoryReport = TaskDo.getRow("ReportSubCategory");
                return (Integer)categoryReport.get("CATEGORY_ID");
            }
        }
        catch (final DataAccessException dae) {
            ScheduleReportUtil.out.log(Level.INFO, "Exception while getting report Id for sub category ID " + subCatId);
            return null;
        }
        return null;
    }
    
    public int saveScheduleHistory(final String enableBackup, final String historyDays) {
        return this.saveScheduleHistoryWithTaskId(enableBackup, historyDays, null);
    }
    
    public int saveScheduleHistoryWithTaskId(final String enableBackup, final String historyDays, final Long task_id) {
        final Long user_id = CommonUtils.getUserId();
        final Long customer_id = CustomerInfoUtil.getInstance().getCustomerId();
        return this.saveScheduleHistoryWithTaskId(enableBackup, historyDays, task_id, user_id, customer_id);
    }
    
    public int saveScheduleHistoryWithTaskId(final String enableBackup, final String historyDays, final Long task_id, final Long user_id, final Long customer_id) {
        try {
            final Persistence persistence = SyMUtil.getPersistence();
            final Criteria criteria = new Criteria(new Column("ScheduleBackupStatus", "TASK_ID"), (Object)task_id, 0);
            final DataObject dataObject = persistence.get("ScheduleBackupStatus", criteria);
            Row scheduleBackupRow = null;
            if (dataObject.isEmpty()) {
                scheduleBackupRow = new Row("ScheduleBackupStatus");
                scheduleBackupRow.set("ENABLE_SCHEDULE_REPORT", (Object)enableBackup);
                scheduleBackupRow.set("HISTORY_PERIOD", (Object)historyDays);
                scheduleBackupRow.set("TASK_ID", (Object)task_id);
                scheduleBackupRow.set("USER_ID", (Object)user_id);
                scheduleBackupRow.set("CUSTOMER_ID", (Object)customer_id);
                dataObject.addRow(scheduleBackupRow);
                persistence.add(dataObject);
            }
            else {
                scheduleBackupRow = dataObject.getRow("ScheduleBackupStatus");
                scheduleBackupRow.set("ENABLE_SCHEDULE_REPORT", (Object)enableBackup);
                scheduleBackupRow.set("HISTORY_PERIOD", (Object)historyDays);
                scheduleBackupRow.set("TASK_ID", (Object)task_id);
                scheduleBackupRow.set("USER_ID", (Object)user_id);
                scheduleBackupRow.set("CUSTOMER_ID", (Object)customer_id);
                dataObject.updateRow(scheduleBackupRow);
                persistence.update(dataObject);
            }
            ScheduleReportUtil.out.log(Level.FINE, "Dataobject for ScheduleBackupStatus table" + dataObject);
            return FrameworkStatusCodes.SUCCESS_RESPONSE_CODE;
        }
        catch (final DataAccessException dae) {
            ScheduleReportUtil.out.log(Level.INFO, "Exception while updating ScheduleBackupStatus table" + dae);
            return FrameworkStatusCodes.UNPROCESSABLE_DATA_CODE;
        }
    }
    
    public void replaceScheduleReportCustomReportViewID(final String oldCRViewID, final String newCRViewName) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CRSaveViewDetails"));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            final Criteria c1 = new Criteria(Column.getColumn("CRSaveViewDetails", "CRVIEWNAME"), (Object)newCRViewName, 0);
            selectQuery.setCriteria(c1);
            final DataObject data = DataAccess.get(selectQuery);
            final Persistence persistence = SyMUtil.getPersistence();
            if (data != null) {
                final Row customViewRow = data.getFirstRow("CRSaveViewDetails");
                final Long newCRViewID = (Long)customViewRow.get("CRSAVEVIEW_ID");
                final Long oldViewID = Long.valueOf(oldCRViewID);
                final UpdateQuery updateNewCustomViewId = (UpdateQuery)new UpdateQueryImpl("ScheduleRepToReportRel");
                final Criteria viewIdCriteria = new Criteria(new Column("ScheduleRepToReportRel", "REPORT_ID"), (Object)oldViewID, 0);
                final Criteria reportTypeCriteria = new Criteria(new Column("ScheduleRepToReportRel", "REPORT_TYPE"), (Object)2, 0);
                updateNewCustomViewId.setCriteria(viewIdCriteria.and(reportTypeCriteria));
                updateNewCustomViewId.setUpdateColumn("REPORT_ID", (Object)newCRViewID);
                persistence.update(updateNewCustomViewId);
            }
        }
        catch (final Exception e) {
            ScheduleReportUtil.out.log(Level.WARNING, "Exeception while replacing old CRSaveViewDetails with new One");
        }
    }
    
    public static Properties getScheduleBackupDetails() {
        return getScheduleBackupDetails(null, null, null);
    }
    
    public static Properties getScheduleBackupDetails(final Long task_id) {
        return getScheduleBackupDetails(task_id, null, null);
    }
    
    public static Properties getScheduleBackupDetails(final Long user_id, final Long customer_id) {
        return getScheduleBackupDetails(null, user_id, customer_id);
    }
    
    public static Properties getScheduleBackupDetails(final Long task_id, final Long user_id, final Long customer_id) {
        try {
            ScheduleReportUtil.out.log(Level.INFO, "Entered editBackupSchedule method");
            final Persistence persistence = SyMUtil.getPersistence();
            Criteria criteria = null;
            if (user_id != null) {
                final Criteria user_criteria = criteria = new Criteria(new Column("ScheduleBackupStatus", "USER_ID"), (Object)user_id, 0);
            }
            if (customer_id != null) {
                final Criteria customer_criteria = new Criteria(new Column("ScheduleBackupStatus", "CUSTOMER_ID"), (Object)customer_id, 0);
                criteria = ((criteria == null) ? customer_criteria : criteria.and(customer_criteria));
            }
            if (task_id != null) {
                final Criteria task_criteria = new Criteria(new Column("ScheduleBackupStatus", "TASK_ID"), (Object)task_id, 0);
                criteria = ((criteria == null) ? task_criteria : criteria.and(task_criteria));
            }
            final DataObject dataObject = persistence.get("ScheduleBackupStatus", criteria);
            Row scheduleBackuprow = null;
            final Properties properties = new Properties();
            if (!dataObject.isEmpty()) {
                scheduleBackuprow = dataObject.getFirstRow("ScheduleBackupStatus");
                final String isEnable = scheduleBackuprow.get("ENABLE_SCHEDULE_REPORT").toString();
                final String historyPeriod = scheduleBackuprow.get("HISTORY_PERIOD").toString();
                final String scheduleBackUpId = scheduleBackuprow.get("SCHEDULE_BACKUP_STATUS_ID").toString();
                properties.setProperty("isEnable", isEnable);
                properties.setProperty("historyPeriod", historyPeriod);
                properties.setProperty("schedule_backup_status_id", scheduleBackUpId);
            }
            return properties;
        }
        catch (final Exception ex) {
            ScheduleReportUtil.out.log(Level.WARNING, "Exception while showing ScheduleReport Page", ex);
            ex.printStackTrace();
            return null;
        }
    }
    
    public static Set<Long> getTaskIds(final Set<Long> viewIds) {
        final Set<Long> taskIds = new HashSet<Long>();
        try {
            if (viewIds != null && viewIds.size() > 0) {
                final Criteria criteria = new Criteria(Column.getColumn("ScheduleRepToReportRel", "REPORT_ID"), (Object)viewIds.toArray(), 8);
                final DataObject taskDO = DataAccess.get("ScheduleRepToReportRel", criteria);
                if (!taskDO.isEmpty()) {
                    final Iterator iterator = taskDO.getRows("ScheduleRepToReportRel");
                    while (iterator.hasNext()) {
                        final Row scheduleTaskRow = iterator.next();
                        taskIds.add(Long.parseLong(scheduleTaskRow.get("TASK_ID").toString()));
                    }
                }
            }
        }
        catch (final Exception dae) {
            ScheduleReportUtil.out.log(Level.INFO, "Exception while fetching taskId" + dae.getMessage());
            ScheduleReportUtil.out.log(Level.INFO, "Exception while fetching taskId" + dae.getStackTrace());
        }
        return taskIds;
    }
    
    public static HashMap<Long, Set<Long>> getTaskViewMappings(final Set<Long> taskIds) {
        final HashMap<Long, Set<Long>> taskMap = new HashMap<Long, Set<Long>>();
        try {
            if (taskIds != null && taskIds.size() > 0) {
                Criteria criteria = new Criteria(Column.getColumn("ScheduleRepToReportRel", "TASK_ID"), (Object)taskIds.toArray(), 8);
                final DataObject taskDO = DataAccess.get("ScheduleRepToReportRel", criteria);
                if (!taskDO.isEmpty()) {
                    for (final Long taskId : taskIds) {
                        final Set<Long> viewIds = new HashSet<Long>();
                        criteria = new Criteria(Column.getColumn("ScheduleRepToReportRel", "TASK_ID"), (Object)taskId, 0);
                        final Iterator iterator = taskDO.getRows("ScheduleRepToReportRel", criteria);
                        while (iterator.hasNext()) {
                            final Row scheduleTaskRow = iterator.next();
                            viewIds.add(Long.parseLong(scheduleTaskRow.get("REPORT_ID").toString()));
                        }
                        taskMap.put(taskId, viewIds);
                    }
                }
            }
        }
        catch (final Exception dae) {
            ScheduleReportUtil.out.log(Level.INFO, "Exception while fetching taskId" + dae.getMessage());
            ScheduleReportUtil.out.log(Level.INFO, "Exception while fetching taskId" + dae.getStackTrace());
        }
        return taskMap;
    }
    
    public static void deleteTaskMappings(final Set<Long> viewIds) {
        try {
            if (viewIds != null && viewIds.size() > 0) {
                final Criteria criteria = new Criteria(Column.getColumn("ScheduleRepToReportRel", "REPORT_ID"), (Object)viewIds.toArray(), 8);
                DataAccess.delete(criteria);
            }
        }
        catch (final Exception dae) {
            ScheduleReportUtil.out.log(Level.INFO, "Exception while deleting taskId" + dae.getMessage());
            ScheduleReportUtil.out.log(Level.INFO, "Exception while deleting taskId" + dae.getStackTrace());
        }
    }
    
    public static Set<Long> getViewIdsUnderCategory(final Integer categoryId) {
        final Set<Long> viewIds = new HashSet<Long>();
        try {
            if (categoryId != null && categoryId > 0) {
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ReportSubCategory"));
                selectQuery.addJoin(new Join("ReportSubCategory", "ViewParams", new String[] { "SUB_CATEGORY_ID" }, new String[] { "SUB_CATEGORY_ID" }, 2));
                selectQuery.setCriteria(new Criteria(Column.getColumn("ReportSubCategory", "CATEGORY_ID"), (Object)categoryId, 0));
                selectQuery.addSelectColumn(Column.getColumn("ViewParams", "VIEW_ID"));
                final DataObject dataObject = DataAccess.get(selectQuery);
                if (!dataObject.isEmpty()) {
                    final Iterator iterator = dataObject.getRows("ViewParams");
                    while (iterator.hasNext()) {
                        final Row viewRow = iterator.next();
                        viewIds.add(Long.parseLong(viewRow.get("VIEW_ID").toString()));
                    }
                }
            }
        }
        catch (final Exception dae) {
            ScheduleReportUtil.out.log(Level.INFO, "Exception while deleting taskId" + dae.getMessage());
            ScheduleReportUtil.out.log(Level.INFO, "Exception while deleting taskId" + dae.getStackTrace());
        }
        return viewIds;
    }
    
    static {
        ScheduleReportUtil.date = DateTimeUtil.dateString();
        ScheduleReportUtil.out = Logger.getLogger("ScheduleReportLogger");
        ScheduleReportUtil.scheduleReportUtil = null;
        final JSONObject frameworkConfigurations = FrameworkConfigurations.getFrameworkConfigurations();
        try {
            ScheduleReportUtil.publishReportDetails = (JSONObject)frameworkConfigurations.get("store_published_report_details");
            if (ScheduleReportUtil.publishReportDetails != null && ScheduleReportUtil.publishReportDetails.get("enable").equals("true")) {
                ScheduleReportUtil.store_published_report_details_enabled = true;
            }
            else {
                ScheduleReportUtil.store_published_report_details_enabled = false;
            }
        }
        catch (final JSONException jsonExcep) {
            ScheduleReportUtil.out.log(Level.INFO, "Exception while retrieving store_published_report_details from " + frameworkConfigurations);
        }
    }
}
