package com.me.mdm.webclient.schedulereport;

import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.webclient.schedulereport.PublishedReportHandler;
import com.adventnet.persistence.DataAccess;
import com.me.devicemanagement.framework.server.util.ProductClassLoader;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessAPI;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Set;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Persistence;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.HashSet;
import java.util.Collection;
import org.json.JSONArray;
import org.json.JSONObject;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.util.pdf.PDFUtil;
import com.me.devicemanagement.framework.webclient.reportcriteria.ReportCriteriaUtil;
import com.adventnet.client.view.web.HttpReqWrapper;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfPTable;
import com.me.mdm.webclient.reports.MDMPdfDocument;
import java.util.Hashtable;
import java.io.OutputStream;
import com.lowagie.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.adventnet.i18n.I18N;
import com.me.mdm.files.FileFacade;
import com.me.devicemanagement.framework.webclient.reports.query.QueryReportHandler;
import com.adventnet.ds.query.Range;
import java.io.ByteArrayOutputStream;
import com.me.mdm.server.query.MDMQueryReportHandler;
import com.me.devicemanagement.framework.webclient.reports.query.QueryReportAttrBean;
import java.util.List;
import com.me.devicemanagement.framework.server.mailmanager.MailDetails;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccessException;
import java.io.File;
import com.me.devicemanagement.framework.server.util.Utils;
import com.me.devicemanagement.framework.webclient.schedulereport.DefaultReportMailTheme;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;
import com.me.devicemanagement.framework.webclient.schedulereport.ScheduleReportUtil;

public class MDMScheduleReportUtil extends ScheduleReportUtil implements SchedulerExecutionInterface
{
    public static Logger logger;
    
    public void executeTask(final Properties taskProps) {
        try {
            Long taskID = null;
            taskID = ((Hashtable<K, Long>)taskProps).get("TASK_ID");
            if (taskID == null) {
                final Long schedulerClassID = Long.valueOf(((Hashtable<K, String>)taskProps).get("schedulerClassID"));
                taskID = ApiFactoryProvider.getSchedulerAPI().getTaskIDForSchedule(schedulerClassID);
            }
            if (ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured()) {
                this.sendReportMail(taskID);
            }
            else {
                MDMScheduleReportUtil.logger.log(Level.WARNING, "Quiting ScheduleReport Execution of {0}..Mail Server Not enabled.", new Object[] { taskID });
            }
        }
        catch (final Exception e) {
            MDMScheduleReportUtil.logger.log(Level.WARNING, "Exception while bacup db operation ", e);
        }
    }
    
    public void sendReportMail(final long task_id) {
        try {
            final String server_home = SyMUtil.getInstallationDir();
            ArrayList files = new ArrayList();
            final DefaultReportMailTheme mailTheme = new DefaultReportMailTheme();
            String time = Utils.getTime(Long.valueOf(System.currentTimeMillis()));
            time = time.replaceAll(":", "_");
            time = time.replaceAll(" ", "");
            time = time.replaceAll(",", "");
            final Row row = ScheduleReportUtil.getInstance().getSchedulerReportTaskProperties(task_id).getFirstRow("ScheduleRepTask");
            final int repFormat = (int)row.get("REPORT_FORMAT");
            final String format = getRepFormatName(repFormat);
            final int delFormat = (int)row.get("DELIVERY_FORMAT");
            final long customer_id = (long)row.get("CUSTOMER_ID");
            final Boolean isEmptyReportNeeded = (Boolean)row.get("IS_EMPTY_REPORT_NEEDED");
            final String scheduleReportPath = server_home + File.separator + "webapps" + File.separator + "DesktopCentral" + File.separator + "server-data" + File.separator + customer_id + File.separator + "schedulereport";
            final MailDetails mailDetails = ScheduleReportUtil.getInstance().addMailDetails(task_id, mailTheme);
            final List customViewDetails = ScheduleReportUtil.getInstance().getViewDetailsForCustomReportGeneration(Long.valueOf(task_id));
            final List queryViewDetails = ScheduleReportUtil.getInstance().getViewDetailsForQueryReportGeneration(Long.valueOf(task_id));
            final List ViewDetails = ScheduleReportUtil.getInstance().getViewDetailsForReportGeneration(task_id);
            final String reportpath = scheduleReportPath + File.separator + MDMScheduleReportUtil.date + File.separator + task_id + time + File.separator;
            final String reportPaths = scheduleReportPath + File.separator + MDMScheduleReportUtil.date + File.separator + task_id + time;
            final Long user_id = ApiFactoryProvider.getSchedulerAPI().getOrAddUserIDInTask(Long.valueOf(task_id));
            files = this.generateReport(ViewDetails, reportpath, format, time, user_id, customer_id, files, isEmptyReportNeeded);
            files = this.generateReport(customViewDetails, reportpath, format, time, user_id, customer_id, files, isEmptyReportNeeded);
            files = this.generateQueryReport(queryViewDetails, repFormat, time, task_id, scheduleReportPath, files, isEmptyReportNeeded);
            if (files.size() > 0) {
                ScheduleReportUtil.getInstance().sendMail(files, task_id, reportPaths, delFormat, mailTheme, mailDetails);
            }
            else {
                MDMScheduleReportUtil.logger.log(Level.INFO, "No non empty report found hence ignoring send mail");
                this.updateTaskToFinished(Long.valueOf(task_id));
            }
            final long attachLimit = (int)row.get("ATTACHMENT_LIMIT") * 1024L * 1024L;
            if (deleteReportFiles(reportPaths, delFormat, attachLimit, scheduleReportPath)) {
                MDMScheduleReportUtil.logger.log(Level.INFO, "Report Files deleted from server and sent as attachements");
            }
            else {
                MDMScheduleReportUtil.logger.log(Level.INFO, "Report Files have been saved on the server and links have been sent");
            }
        }
        catch (final DataAccessException ex) {
            Logger.getLogger(MDMScheduleReportUtil.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
        catch (final Exception ex2) {
            Logger.getLogger(MDMScheduleReportUtil.class.getName()).log(Level.SEVERE, null, ex2);
        }
    }
    
    public ArrayList generateQueryReport(final List viewdetails, final int rep_format, final String time, final Long task_id, String reportpath, final ArrayList files, final Boolean isEmptyReportNeeded) {
        try {
            for (int length = viewdetails.size(), i = 0; i < length; ++i) {
                final Properties prop = viewdetails.get(i);
                final String queryFromProp = ((Hashtable<K, String>)prop).get("query");
                final String name = ((Hashtable<K, String>)prop).get("name");
                final QueryReportAttrBean queryRepBean = new QueryReportAttrBean();
                queryRepBean.reSetValues();
                final String queryName = name;
                queryRepBean.setQueryNameVal(queryName);
                final String query = queryFromProp;
                queryRepBean.setQueryVal(query);
                reportpath += File.separator;
                if (rep_format == 1) {
                    this.getPDFReport(query, queryRepBean, queryName, task_id, time, reportpath, files, isEmptyReportNeeded);
                }
                else {
                    ScheduleReportUtil.getInstance().getReportAsCSV(query, queryRepBean, queryName, task_id, time, reportpath, files, isEmptyReportNeeded);
                }
                MDMScheduleReportUtil.logger.log(Level.INFO, "Schedule Report :Query report files generated successfully");
            }
        }
        catch (final Exception ex) {
            MDMScheduleReportUtil.logger.log(Level.WARNING, "Schedule Report :Exception in creating query files", ex);
        }
        return files;
    }
    
    public int getPDFReport(final String query, final QueryReportAttrBean queryRepBean, final String title, final Long task_id, final String time, final String schedulerReportPath, final ArrayList files, final Boolean isEmptyReportNeeded) throws Exception {
        final String modifiedQuery = MDMQueryReportHandler.modifyQuery(query);
        String queryType = MDMQueryReportHandler.getQueryType(modifiedQuery).toLowerCase();
        queryType = queryType.trim();
        final ByteArrayOutputStream streamToWritePdf = new ByteArrayOutputStream();
        final MDMQueryReportHandler qurHandler = new MDMQueryReportHandler();
        final MDMPdfDocument dcpdf = null;
        Document document = null;
        MDMPdfDocument dcPDFDoc = null;
        PdfWriter pdfwriter = null;
        PdfPTable pTable = null;
        MDMScheduleReportUtil.logger.log(Level.INFO, "Schedule Report :Entered into ScheduleReportUtil.getReportAsCSV()");
        final String report_file = schedulerReportPath + MDMScheduleReportUtil.date + File.separator + task_id + time + File.separator;
        final File repFile = new File(report_file);
        if (!repFile.exists()) {
            repFile.mkdirs();
        }
        final String fileName = report_file + title + ".pdf";
        final File file = new File(fileName);
        if (file.getCanonicalPath().startsWith(report_file)) {
            int rowcount;
            final int rowConstant = rowcount = 7500;
            String tempquery = null;
            try {
                MDMScheduleReportUtil.logger.log(Level.INFO, "In Schedule Report :getPDFReport method");
                int recordindex = 0;
                while (true) {
                    final Range range = new Range(recordindex, rowConstant);
                    tempquery = QueryReportHandler.setOrderByInQuery(modifiedQuery, range, queryType);
                    if (recordindex == 0) {
                        rowcount = MDMQueryReportHandler.getTableModelData(tempquery, true, queryRepBean);
                        if (!isEmptyReportNeeded && rowcount == 0) {
                            new FileFacade().deleteFile(fileName);
                            return 1112;
                        }
                        final String desc = I18N.getMsg("mdm.rep.pdf.QUERY_REP_DESC", new Object[0]) + " - " + title;
                        final ArrayList dataList = queryRepBean.getDataList();
                        float[] columnWidth = null;
                        final ArrayList columnNameList = dataList.get(0);
                        final int columnNameListCount = columnNameList.size();
                        if (columnNameListCount > 10) {
                            document = new Document(PageSize.A4.rotate());
                        }
                        else {
                            document = new Document(PageSize.A4);
                        }
                        pdfwriter = PdfWriter.getInstance(document, (OutputStream)new FileOutputStream(file));
                        document.open();
                        final Hashtable queryRepDetail_hash = new Hashtable();
                        queryRepDetail_hash.put("TITLE", "Query Report");
                        queryRepDetail_hash.put("NAME", "Query Report PDF");
                        queryRepDetail_hash.put("DESCRIPTION", desc);
                        qurHandler.setLogo(document);
                        dcPDFDoc = new MDMPdfDocument(document);
                        dcPDFDoc.setTitleAndDescription(queryRepDetail_hash);
                        pTable = new PdfPTable(columnNameListCount);
                        pTable.setWidthPercentage(100.0f);
                        columnWidth = qurHandler.getSectionWidths(columnWidth, columnNameListCount);
                        pTable.setWidths(columnWidth);
                        pTable.setComplete(false);
                        final List columnValues = dataList.get(0);
                        qurHandler.setTableHeader(columnValues, pTable);
                        dataList.remove(0);
                        MDMQueryReportHandler.fetchPDFData(dataList, pTable, document);
                    }
                    else {
                        rowcount = MDMQueryReportHandler.getTableModelData(tempquery, true, queryRepBean);
                        final ArrayList dataList2 = queryRepBean.getDataList();
                        MDMQueryReportHandler.fetchPDFData(dataList2, pTable, document);
                    }
                    queryRepBean.getDataList().clear();
                    if (rowcount < rowConstant) {
                        pTable.setComplete(true);
                        if (pTable != null) {
                            pTable.setWidthPercentage(100.0f);
                            pTable.setSpacingAfter(10.0f);
                            document.add((Element)pTable);
                        }
                        MDMScheduleReportUtil.logger.log(Level.INFO, "In Schedule Report :Export PDF is successfully completed");
                        files.add(fileName);
                        return 1113;
                    }
                    if (pTable != null) {
                        document.add((Element)pTable);
                    }
                    recordindex += rowConstant;
                }
            }
            catch (final Exception ex) {
                MDMScheduleReportUtil.logger.log(Level.WARNING, "Exception in creating PDF for query Report", ex);
            }
            finally {
                if (dcPDFDoc != null) {
                    dcPDFDoc.close();
                }
            }
        }
        return 1114;
    }
    
    public ArrayList generateReport(final List viewdetails, final String server_home, final String format, final String time, final Long user_id, final Long customer_id, final ArrayList files, final Boolean isEmptyReportNeeded) {
        FileOutputStream fos = null;
        boolean isMsp = false;
        try {
            for (int length = viewdetails.size(), i = 0; i < length; ++i) {
                final Properties viewprops = viewdetails.get(i);
                final String viewname = ((Hashtable<K, String>)viewprops).get("name");
                final Object viewid = ((Hashtable<K, Object>)viewprops).get("id");
                final Object scheduleRepID = ((Hashtable<K, Object>)viewprops).get("scheduleRepID");
                final String fileName = ScheduleReportUtil.getInstance().getReportFileName(server_home, viewprops, format);
                final File f = new File(fileName);
                if (f.getCanonicalPath().startsWith(server_home)) {
                    fos = new FileOutputStream(f);
                    final String patchReq = this.getpatchRequest(viewid.toString());
                    isMsp = CustomerInfoUtil.getInstance().isMSP();
                    if (isMsp) {
                        ScheduleReportUtil.getInstance();
                        ScheduleReportUtil.setCustomerInfoThread((long)customer_id);
                    }
                    final HttpReqWrapper requestWrapper = new HttpReqWrapper("/" + System.getProperty("contextDIR"));
                    final String toolid = viewid.toString();
                    requestWrapper.setParameter("toolID", toolid);
                    requestWrapper.setParameter("domainName", "");
                    requestWrapper.setParameter("reportInitByScheduledReport", "true");
                    requestWrapper.setParameter("exportType", format);
                    requestWrapper.setParameter("selectedTreeElem", toolid);
                    requestWrapper.setParameter("accessType", "2");
                    requestWrapper.setParameter("ADReportsEmail", "ADReportsEmailEnabled");
                    requestWrapper.setParameter("isExport", "true");
                    requestWrapper.setParameter("patchTypeParam", patchReq);
                    requestWrapper.setParameter("patchStatus", patchReq);
                    requestWrapper.setParameter("health", patchReq);
                    requestWrapper.setParameter("selectedskin", "sdp-blue");
                    if (user_id != null) {
                        final String[] loginDetails = ScheduleReportUtil.getInstance().getLoginName(user_id);
                        ApiFactoryProvider.getAuthUtilAccessAPI().setUserCredential(loginDetails[0], "system", loginDetails[1], user_id);
                        final Row userSettings = SyMUtil.getUserAccountSettings(user_id);
                        if (userSettings != null) {
                            final String theme = (String)userSettings.get("THEME");
                            if (theme != null) {
                                requestWrapper.setParameter("scheduleReportTheme", theme);
                            }
                            else {
                                requestWrapper.setParameter("scheduleReportTheme", SyMUtil.getInstance().getTheme());
                            }
                        }
                    }
                    else {
                        requestWrapper.setParameter("scheduleReportTheme", SyMUtil.getInstance().getTheme());
                    }
                    String criteriaJSON = ReportCriteriaUtil.getInstance().getFilterCriteriaJSON(scheduleRepID);
                    final Long viewID = ReportCriteriaUtil.getInstance().getViewIdForScheduleReport((String)scheduleRepID);
                    final String viewId = (viewID != null) ? String.valueOf(viewID) : null;
                    requestWrapper.setParameter("viewId", viewId);
                    requestWrapper.setParameter("isScheduledReport", "true");
                    if (criteriaJSON != null) {
                        requestWrapper.setParameter("criteriaJSON", criteriaJSON);
                    }
                    else {
                        criteriaJSON = ReportCriteriaUtil.getInstance().constructCriteriaJSONForScheduleID((String)scheduleRepID);
                        if (criteriaJSON != null) {
                            requestWrapper.setParameter("criteriaJSON", criteriaJSON);
                        }
                    }
                    requestWrapper.setParameter("scheduleID", String.valueOf(scheduleRepID));
                    MDMScheduleReportUtil.logger.log(Level.INFO, "Schedule Report :scheduleID SET IS:", String.valueOf(scheduleRepID));
                    final String viewName = viewname;
                    if ("csv".equalsIgnoreCase(format)) {
                        try {
                            final int status = ScheduleReportUtil.generateCSVReport(viewName, requestWrapper, fos, isEmptyReportNeeded);
                            if (status == 1112) {
                                MDMScheduleReportUtil.logger.log(Level.INFO, "{0} view is ignored as it is empty", new Object[] { viewName });
                                fos.close();
                                new FileFacade().deleteFile(fileName);
                                continue;
                            }
                        }
                        catch (final Exception ex) {
                            MDMScheduleReportUtil.logger.log(Level.WARNING, "Schedule Report :Exception while creating csv file", ex);
                        }
                    }
                    else if ("pdf".equalsIgnoreCase(format)) {
                        try {
                            final Boolean isEmptyView = ScheduleReportUtil.checkIfViewIsEmpty(viewName, requestWrapper);
                            if (!isEmptyReportNeeded && isEmptyView) {
                                MDMScheduleReportUtil.logger.log(Level.INFO, "{0} view is ignored as it is empty", new Object[] { viewName });
                                fos.close();
                                new FileFacade().deleteFile(fileName);
                                continue;
                            }
                            PDFUtil.generatePDF(viewName, (HttpServletRequest)requestWrapper, (OutputStream)fos);
                        }
                        catch (final Exception ex) {
                            MDMScheduleReportUtil.logger.log(Level.WARNING, "Schedule Report :Exception while creating pdf file", ex);
                        }
                    }
                    else {
                        try {
                            final int status = ScheduleReportUtil.generateXSLReport(viewName, requestWrapper, fos, isEmptyReportNeeded);
                            if (status == 1112) {
                                MDMScheduleReportUtil.logger.log(Level.INFO, "{0} view is ignored as it is empty", new Object[] { viewName });
                                fos.close();
                                new FileFacade().deleteFile(fileName);
                                continue;
                            }
                        }
                        catch (final Exception ex) {
                            MDMScheduleReportUtil.logger.log(Level.WARNING, "Schedule Report :Exception while creating xslr file", ex);
                        }
                    }
                    files.add(fileName);
                    fos.close();
                }
            }
            MDMScheduleReportUtil.logger.log(Level.INFO, "file generated in {0}format", format);
        }
        catch (final Exception ex2) {
            MDMScheduleReportUtil.logger.log(Level.WARNING, "Schedule Report :Exception in generating files", ex2);
            try {
                if (fos != null) {
                    fos.close();
                }
            }
            catch (final Exception e) {
                MDMScheduleReportUtil.logger.log(Level.WARNING, "Exception in finally block ", e);
            }
        }
        finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            }
            catch (final Exception e2) {
                MDMScheduleReportUtil.logger.log(Level.WARNING, "Exception in finally block ", e2);
            }
        }
        return files;
    }
    
    private String getpatchRequest(final String viewid) {
        String request = "";
        if (viewid.equals("17253")) {
            request = "-1";
        }
        else if (viewid.equals("17251")) {
            request = "202";
        }
        else if (viewid.equals("17252")) {
            request = "201";
        }
        else if (viewid.equals("17054")) {
            request = "Latest";
        }
        else if (viewid.equals("17055")) {
            request = "Supported";
        }
        else if (viewid.equals("15012")) {
            request = "highlyVul";
        }
        else if (viewid.equals("15011")) {
            request = "vul";
        }
        else if (viewid.equals("15010")) {
            request = "healthy";
        }
        else if (viewid.equals("15009")) {
            request = "";
        }
        return request;
    }
    
    private static String getRepFormatName(final int repFormat) {
        String format;
        if (repFormat == 1) {
            format = "pdf";
        }
        else if (repFormat == 2) {
            format = "xlsx";
        }
        else {
            format = "csv";
        }
        return format;
    }
    
    public void getTaskDetailsAndPopulateForm(final HttpServletRequest request, final Long taskID) {
        try {
            MDMScheduleReportUtil.logger.log(Level.INFO, "Schedule Report :Entered into ScheduleReportAction.getTaskDetailsAndPopulateForm()");
            final ArrayList scheduleRepid = new ArrayList();
            final ArrayList preDefinedRep = new ArrayList();
            final ArrayList report_id = new ArrayList();
            String selectColumn = "";
            request.setAttribute("isedit", (Object)true);
            final Column critcol = Column.getColumn("ScheduleRepToReportRel", "TASK_ID");
            final Criteria cri = new Criteria(critcol, (Object)taskID, 0);
            final Persistence persistence = SyMUtil.getPersistence();
            final DataObject extnTaskDo = persistence.get("ScheduleRepToReportRel", cri);
            MDMScheduleReportUtil.logger.log(Level.FINE, "Data object ScheduleRepToReportRel {0}", extnTaskDo);
            final Iterator ScheduleRep = extnTaskDo.getRows("ScheduleRepToReportRel");
            while (ScheduleRep.hasNext()) {
                final Row rep = ScheduleRep.next();
                final Long report = (Long)rep.get("REPORT_ID");
                final Integer reportType = (Integer)rep.get("REPORT_TYPE");
                if (reportType == 2) {
                    report_id.add("custom");
                }
                if (reportType == 3) {
                    report_id.add("query");
                }
                if (reportType == 1) {
                    preDefinedRep.add(report);
                }
                final Properties viewprop = new Properties();
                ((Hashtable<String, Long>)viewprop).put("report", report);
                scheduleRepid.add(report);
                selectColumn = selectColumn + report + ",";
            }
            final JSONArray existingCriteria;
            if ((existingCriteria = ReportCriteriaUtil.getInstance().buildCriteriaJson(taskID)) != null) {
                for (int index = 0; index < existingCriteria.length(); ++index) {
                    final JSONObject valueDetails = (JSONObject)existingCriteria.get(index);
                    final JSONArray values = (JSONArray)valueDetails.get("VALUES");
                    for (int valueIndex = 0; valueIndex < values.length(); ++valueIndex) {
                        final JSONObject criteria = (JSONObject)values.get(valueIndex);
                        String searchValue = criteria.get("SEARCH_VALUE").toString();
                        searchValue = searchValue.replaceAll("\\$@\\$", ",");
                        criteria.put("SEARCH_VALUE", (Object)searchValue);
                        final String columnID = criteria.get("COLUMN_ID").toString();
                        criteria.put("COLUMN_ID", (Object)columnID);
                        values.put(valueIndex, (Object)criteria);
                    }
                    valueDetails.put("VALUES", (Object)values);
                    existingCriteria.put(index, (Object)valueDetails);
                }
                request.setAttribute("existingCriteria", (Object)existingCriteria.toString());
            }
            final ArrayList report_id2 = ScheduleReportUtil.getInstance().getPredefinedRreportId(preDefinedRep);
            report_id.addAll(report_id2);
            final Set set = new HashSet(report_id);
            Object[] category_idStr = new Object[set.size()];
            category_idStr = set.toArray();
            final String[] arraying = new String[category_idStr.length];
            String reports_id = "";
            for (int i = 0; i < category_idStr.length; ++i) {
                arraying[i] = category_idStr[i].toString() + ",";
                reports_id += arraying[i];
            }
            JSONObject existingFilters = ReportCriteriaUtil.getInstance().getFilterJSON(taskID);
            existingFilters = new JSONUtil().convertLongToString(existingFilters);
            if (existingFilters != null) {
                request.setAttribute("existingFilters", (Object)existingFilters.toString());
            }
            final Hashtable taskDetailHash = ScheduleReportUtil.getInstance().getScheduledTaskDetails(taskID);
            request.setAttribute("taskDetailHash", (Object)taskDetailHash);
            MDMScheduleReportUtil.logger.log(Level.INFO, "Successfully obtained the task details for the task {0}", taskDetailHash);
            request.setAttribute("selectcolumn", (Object)selectColumn);
            request.setAttribute("reportid", (Object)reports_id);
        }
        catch (final Exception ex2) {
            ex2.printStackTrace();
        }
    }
    
    public static boolean deleteReportFiles(final String reportPaths, final int delFormat, final long attachLimit, final String scheduleReportPath) {
        try {
            long fileSize = 0L;
            final File repFile = new File(reportPaths);
            final File[] listFiles;
            final File[] fileList = listFiles = repFile.listFiles();
            for (final File f : listFiles) {
                fileSize += f.length();
            }
            if (delFormat == 3) {}
            if (delFormat == 3 || fileSize > attachLimit) {
                CustomerInfoUtil.getInstance();
                if (!CustomerInfoUtil.isSAS()) {
                    return false;
                }
            }
            for (final File fileDelete : fileList) {
                fileDelete.delete();
            }
            if (repFile.isDirectory()) {
                final File[] listFiles2;
                final File[] innerFolder = listFiles2 = repFile.listFiles();
                for (final File zipFile : listFiles2) {
                    if (zipFile.exists()) {
                        zipFile.delete();
                    }
                }
            }
            repFile.delete();
            final File dateFolder = new File(scheduleReportPath + File.separator + MDMScheduleReportUtil.date);
            final File[] emptyList = dateFolder.listFiles();
            if (emptyList.length == 0) {
                dateFolder.delete();
            }
            return true;
        }
        catch (final Exception ex) {
            MDMScheduleReportUtil.logger.log(Level.WARNING, "Schedule Report :Exception in deleting files", ex);
        }
        return false;
    }
    
    public int scheduleBackup(final String reportPath) throws DataAccessException {
        try {
            MDMScheduleReportUtil.logger.log(Level.INFO, "Schedule Report :Entered into ScheduleReportUtil.scheduleBackup()");
            final String server_home = SyMUtil.getInstallationDir();
            final String file = server_home + File.separator + reportPath;
            final File logDir = new File(file);
            if (!logDir.isDirectory()) {
                logDir.mkdirs();
            }
            final String fileName = logDir.getName();
            final long lastMod = logDir.lastModified();
            final File[] fileList = logDir.listFiles();
            final Criteria criteria = new Criteria(Column.getColumn("ScheduleBackupDetails", "FILE_NAME"), (Object)reportPath, 0);
            final DataObject data = SyMUtil.getPersistence().get("ScheduleBackupDetails", criteria);
            Row backupFileInfoRow = data.getRow("ScheduleBackupDetails");
            if (backupFileInfoRow == null) {
                backupFileInfoRow = new Row("ScheduleBackupDetails");
                backupFileInfoRow.set("FILE_NAME", (Object)reportPath);
                backupFileInfoRow.set("GENRATED_TIME", (Object)lastMod);
                data.addRow(backupFileInfoRow);
            }
            else {
                backupFileInfoRow.set("FILE_NAME", (Object)reportPath);
                backupFileInfoRow.set("GENRATED_TIME", (Object)lastMod);
                data.updateRow(backupFileInfoRow);
            }
            SyMUtil.getPersistence().update(data);
            return 1000;
        }
        catch (final Exception ex) {
            MDMScheduleReportUtil.logger.log(Level.WARNING, "Exception in scheduleBackup...", ex);
            return 1001;
        }
    }
    
    public int deleteScheduleReport() {
        final Logger deleteLogger = Logger.getLogger("QueryExecutorLogger");
        try {
            final String server_home = SyMUtil.getInstallationDir();
            final SelectQuery queryForStatus = (SelectQuery)new SelectQueryImpl(Table.getTable("ScheduleBackupStatus"));
            queryForStatus.addSelectColumn(new Column((String)null, "*"));
            final DataObject dataObject = SyMUtil.getReadOnlyPersistence().get(queryForStatus);
            final Row dcBackupInfoRow = dataObject.getRow("ScheduleBackupStatus");
            if (dcBackupInfoRow != null) {
                final int noOfDays = (int)dcBackupInfoRow.get("HISTORY_PERIOD");
                final long days_in_millis = noOfDays * 24 * 60 * 60 * 1000;
                final Long currTime = new Long(System.currentTimeMillis());
                final long days_in_long = currTime - days_in_millis;
                final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ScheduleBackupDetails"));
                query.addSelectColumn(new Column((String)null, "*"));
                final Criteria criteria = new Criteria(Column.getColumn("ScheduleBackupDetails", "GENRATED_TIME"), (Object)days_in_long, 7);
                query.setCriteria(criteria);
                final DataObject data = SyMUtil.getReadOnlyPersistence().get(query);
                deleteLogger.log(Level.FINE, "Query to get details from ScheduleBackupDetails to delete files", query);
                final Iterator iterator = data.getRows("ScheduleBackupDetails");
                while (iterator.hasNext()) {
                    final Row dbBackFilesRow = iterator.next();
                    String fileName = (String)dbBackFilesRow.get("FILE_NAME");
                    fileName = server_home + File.separator + fileName;
                    try {
                        fileName = fileName.replaceAll("\\\\", "\\\\\\\\");
                        final boolean isDeleted = ((FileAccessAPI)Class.forName(ProductClassLoader.getSingleImplProductClass("DM_FILE_ACCESS_API_CLASS")).newInstance()).deleteDirectory(fileName);
                        if (!isDeleted) {
                            continue;
                        }
                        deleteLogger.log(Level.FINEST, "file: {0} deleted successfully", fileName);
                    }
                    catch (final Exception e) {
                        deleteLogger.log(Level.WARNING, "Exception while deleting file", e);
                    }
                }
                DataAccess.delete(criteria);
                PublishedReportHandler.deletePublishedReportDetailsByGeneratedTime(Long.valueOf(days_in_long));
            }
            return 1000;
        }
        catch (final Exception ex) {
            deleteLogger.log(Level.WARNING, "Exception in delete file class", ex);
            return 1001;
        }
    }
    
    public static boolean taskBelongsToUser(final Long taskID) {
        try {
            final Long userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            final Criteria taskCriteria = new Criteria(new Column("TaskToUserRel", "TASK_ID"), (Object)taskID, 0);
            final Criteria userCriteria = new Criteria(new Column("TaskToUserRel", "USER_ID"), (Object)userID, 0);
            final DataObject taskDO = SyMUtil.getPersistence().get("TaskToUserRel", taskCriteria.and(userCriteria));
            if (taskDO != null && !taskDO.isEmpty()) {
                return true;
            }
        }
        catch (final Exception e) {
            MDMScheduleReportUtil.logger.log(Level.SEVERE, "Exception while checking whether task belongs to User", e);
        }
        return false;
    }
    
    public static void initialiseNewScheduleReport(final HttpServletRequest request) {
        request.setAttribute("isedit", (Object)false);
        ApiFactoryProvider.getSchedulerAPI().setScheduledValuesInJson((String)null, request, Boolean.valueOf(true), Boolean.valueOf(false), Boolean.valueOf(true));
    }
    
    static {
        MDMScheduleReportUtil.logger = Logger.getLogger(MDMScheduleReportUtil.class.getName());
    }
}
