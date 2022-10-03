package com.me.ems.framework.reports.core;

import com.me.devicemanagement.framework.webclient.schedulereport.PublishedReportHandler;
import com.adventnet.persistence.DataAccess;
import com.me.devicemanagement.framework.server.util.ProductClassLoader;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessAPI;
import com.adventnet.persistence.DataAccessException;
import java.util.Set;
import org.json.JSONArray;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Persistence;
import com.adventnet.ds.query.SelectQuery;
import java.util.HashSet;
import java.util.Collection;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.client.util.pdf.PDFUtil;
import java.lang.reflect.Method;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import javax.servlet.http.HttpServletRequest;
import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import org.json.JSONObject;
import com.me.devicemanagement.framework.webclient.reportcriteria.ReportCriteriaUtil;
import com.adventnet.client.view.web.HttpReqWrapper;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfPTable;
import com.me.ems.framework.reports.core.pdf.PdfDocument;
import java.util.Hashtable;
import java.io.OutputStream;
import com.lowagie.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.adventnet.i18n.I18N;
import com.adventnet.ds.query.Range;
import java.io.IOException;
import com.me.devicemanagement.framework.webclient.reports.query.QueryReportHandler;
import com.me.devicemanagement.framework.webclient.reports.query.QueryReportAttrBean;
import java.util.Properties;
import java.util.List;
import com.me.devicemanagement.framework.server.mailmanager.MailDetails;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import com.me.devicemanagement.framework.server.util.Utils;
import com.me.devicemanagement.framework.webclient.schedulereport.DefaultReportMailTheme;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.webclient.schedulereport.ScheduleReportUtil;

public class DCScheduleReportUtil extends ScheduleReportUtil
{
    private static Logger out;
    
    public static void sendReportMail(final long task_id) {
        try {
            final String server_home = SyMUtil.getInstallationDir();
            ArrayList files = new ArrayList();
            final DefaultReportMailTheme mailTheme = new DefaultReportMailTheme();
            String time = Utils.getTime(System.currentTimeMillis());
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
            final List customViewDetails = ScheduleReportUtil.getInstance().getViewDetailsForCustomReportGeneration(task_id);
            final List queryViewDetails = ScheduleReportUtil.getInstance().getViewDetailsForQueryReportGeneration(task_id);
            final List predefinedViewDetails = ScheduleReportUtil.getInstance().getViewDetailsForReportGeneration(task_id);
            final String reportPath = scheduleReportPath + File.separator + DCScheduleReportUtil.date + File.separator + task_id + time + File.separator;
            final String reportPaths = scheduleReportPath + File.separator + DCScheduleReportUtil.date + File.separator + task_id + time;
            final Long user_id = ApiFactoryProvider.getSchedulerAPI().getOrAddUserIDInTask(task_id);
            files = generateReport(predefinedViewDetails, reportPath, format, time, user_id, customer_id, files, isEmptyReportNeeded);
            files = generateReport(customViewDetails, reportPath, format, time, user_id, customer_id, files, isEmptyReportNeeded);
            files = generateQueryReport(queryViewDetails, repFormat, time, task_id, user_id, scheduleReportPath, files, isEmptyReportNeeded);
            if (isEmptyReportNeeded) {
                ScheduleReportUtil.getInstance().sendMail(files, task_id, reportPaths, delFormat, mailTheme, mailDetails);
            }
            else if (files.size() > 0) {
                ScheduleReportUtil.getInstance().sendMail(files, task_id, reportPaths, delFormat, mailTheme, mailDetails);
            }
            else {
                DCScheduleReportUtil.out.log(Level.INFO, "No non empty report found hence ignoring send mail");
            }
            final long attachLimit = (int)row.get("ATTACHMENT_LIMIT") * 1024L * 1024L;
            if (deleteReportFiles(reportPaths, delFormat, attachLimit, scheduleReportPath)) {
                DCScheduleReportUtil.out.log(Level.INFO, "Report Files deleted from server and sent as attachements");
            }
            else {
                DCScheduleReportUtil.out.log(Level.INFO, "Report Files have been saved on the server and links have been sent");
            }
        }
        catch (final Exception ex) {
            DCScheduleReportUtil.out.log(Level.SEVERE, "Exception occurred while sending schedule report mail...", ex);
        }
    }
    
    public static ArrayList generateQueryReport(final List viewdetails, final int rep_format, final String time, final Long task_id, final String reportpath, final ArrayList files, final Boolean isEmptyReportNeeded) {
        return generateQueryReport(viewdetails, rep_format, time, task_id, null, reportpath, files, isEmptyReportNeeded);
    }
    
    public static ArrayList generateQueryReport(final List viewdetails, final int rep_format, final String time, final Long task_id, final Long user_id, String reportpath, final ArrayList files, final Boolean isEmptyReportNeeded) {
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
                if (user_id != null) {
                    final String[] loginDetails = ScheduleReportUtil.getInstance().getLoginName(user_id);
                    ApiFactoryProvider.getAuthUtilAccessAPI().setUserCredential(loginDetails[0], "system", loginDetails[1], user_id);
                }
                if (rep_format == 1) {
                    getPDFReport(query, queryRepBean, queryName, task_id, time, reportpath, files, isEmptyReportNeeded);
                }
                else {
                    ScheduleReportUtil.getInstance().getReportAsCSV(query, queryRepBean, queryName, task_id, time, reportpath, files, isEmptyReportNeeded);
                }
                DCScheduleReportUtil.out.log(Level.INFO, "Schedule Report :Query report files generated successfully");
            }
        }
        catch (final Exception ex) {
            DCScheduleReportUtil.out.log(Level.SEVERE, "Schedule Report :Exception in creating query files", ex);
        }
        return files;
    }
    
    public static int getPDFReport(final String query, final QueryReportAttrBean queryRepBean, final String title, final Long task_id, final String time, final String schedulerReportPath, final ArrayList files, final Boolean isEmptyReportNeeded) throws Exception {
        final String modifiedQuery = QueryReportHandler.modifyQuery(query);
        String queryType = QueryReportHandler.getQueryType(modifiedQuery).toLowerCase();
        queryType = queryType.trim();
        final QueryReportHandler qurHandler = new QueryReportHandler();
        Document document = null;
        PdfDocument dcPDFDoc = null;
        PdfWriter pdfwriter = null;
        PdfPTable pTable = null;
        DCScheduleReportUtil.out.log(Level.INFO, "Schedule Report :Entered into ScheduleReportUtil.getReportAsCSV()");
        final String report_file = schedulerReportPath + DCScheduleReportUtil.date + File.separator + task_id + time + File.separator;
        final File repFile = new File(report_file);
        if (!repFile.exists()) {
            repFile.mkdirs();
        }
        final String fileName = report_file + title + ".pdf";
        if (!new File(fileName).getCanonicalPath().startsWith(new File(System.getProperty("server.home")).getCanonicalPath())) {
            throw new IOException("Filepath is outside of the target dir: " + fileName);
        }
        final File file = new File(fileName);
        int rowcount;
        final int rowConstant = rowcount = 7500;
        String tempquery = null;
        try {
            DCScheduleReportUtil.out.log(Level.INFO, "In Schedule Report :getPDFReport method");
            int recordindex = 0;
            while (true) {
                final Range range = new Range(recordindex, rowConstant);
                tempquery = QueryReportHandler.setOrderByInQuery(modifiedQuery, range, queryType);
                if (recordindex == 0) {
                    rowcount = QueryReportHandler.getTableModelData(tempquery, true, queryRepBean);
                    if (!isEmptyReportNeeded && rowcount == 0) {
                        ApiFactoryProvider.getFileAccessAPI().deleteFile(fileName);
                        return 1112;
                    }
                    final String desc = I18N.getMsg("dc.rep.pdf.QUERY_REP_DESC", new Object[0]) + " - " + title;
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
                    dcPDFDoc = new PdfDocument(document);
                    dcPDFDoc.setTitleAndDescription(queryRepDetail_hash);
                    pTable = new PdfPTable(columnNameListCount);
                    pTable.setWidthPercentage(100.0f);
                    columnWidth = qurHandler.getSectionWidths(columnWidth, columnNameListCount);
                    pTable.setWidths(columnWidth);
                    pTable.setComplete(false);
                    final List columnValues = dataList.get(0);
                    qurHandler.setTableHeader(columnValues, pTable);
                    dataList.remove(0);
                    QueryReportHandler.fetchPDFData(dataList, pTable, document);
                }
                else {
                    rowcount = QueryReportHandler.getTableModelData(tempquery, true, queryRepBean);
                    final ArrayList dataList2 = queryRepBean.getDataList();
                    QueryReportHandler.fetchPDFData(dataList2, pTable, document);
                }
                queryRepBean.getDataList().clear();
                if (rowcount < rowConstant) {
                    pTable.setComplete(true);
                    if (pTable != null) {
                        pTable.setWidthPercentage(100.0f);
                        pTable.setSpacingAfter(10.0f);
                        document.add((Element)pTable);
                    }
                    DCScheduleReportUtil.out.log(Level.INFO, "In Schedule Report :Export PDF is successfully completed");
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
            DCScheduleReportUtil.out.log(Level.SEVERE, "Exception in creating PDF for query Report", ex);
        }
        finally {
            if (dcPDFDoc != null) {
                dcPDFDoc.close();
            }
        }
        return 1114;
    }
    
    public static ArrayList generateReport(final List viewdetails, final String server_home, final String format, final String time, final Long user_id, final Long customer_id, final ArrayList files, final Boolean isEmptyReportNeeded) {
        FileOutputStream fos = null;
        boolean isMsp = false;
        try {
            for (int length = viewdetails.size(), i = 0; i < length; ++i) {
                final Properties viewprops = viewdetails.get(i);
                final String viewname = ((Hashtable<K, String>)viewprops).get("name");
                final Object viewid = ((Hashtable<K, Object>)viewprops).get("id");
                final Object scheduleRepID = ((Hashtable<K, Object>)viewprops).get("scheduleRepID");
                final Object reportType = ((Hashtable<K, Object>)viewprops).get("reportType");
                final String title = ((Hashtable<K, String>)viewprops).get("title");
                final String fileName = ScheduleReportUtil.getInstance().getReportFileName(server_home, viewprops, format);
                if (!new File(fileName).getCanonicalPath().startsWith(new File(System.getProperty("server.home")).getCanonicalPath())) {
                    throw new IOException("Filepath is outside of the target dir: " + fileName);
                }
                final File f = new File(fileName);
                fos = new FileOutputStream(f);
                final String patchReq = getpatchRequest(viewid.toString());
                isMsp = CustomerInfoUtil.getInstance().isMSP();
                if (isMsp) {
                    ScheduleReportUtil.setCustomerInfoThread(customer_id);
                }
                final HttpReqWrapper requestWrapper = new HttpReqWrapper("/" + System.getProperty("contextDIR"));
                String toolid;
                if (reportType != null && reportType.equals("2")) {
                    toolid = "3456";
                    requestWrapper.setParameter("reportDisplayName", title);
                }
                else {
                    toolid = viewid.toString();
                }
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
                if (ReportCriteriaUtil.getInstance().getModuleCategoryIdFromViewId(toolid) == 122) {
                    requestWrapper.setParameter("selectedTab", "PatchMgmt");
                }
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
                DCScheduleReportUtil.out.log(Level.INFO, "Schedule Report :scheduleID SET IS:", String.valueOf(scheduleRepID));
                final String viewName = viewname;
                JSONObject formatHandler = (JSONObject)FrameworkConfigurations.getSpecificPropertyIfExists("report_format_handler_map", viewName, (Object)null);
                if ("csv".equalsIgnoreCase(format)) {
                    try {
                        int status;
                        if (formatHandler != null && formatHandler.has("csv")) {
                            final ReportGenerationUtil formatUtil = (ReportGenerationUtil)Class.forName(formatHandler.getString("csv")).newInstance();
                            status = formatUtil.generateCsvReport(viewName, requestWrapper, fos, isEmptyReportNeeded);
                        }
                        else {
                            formatHandler = (JSONObject)FrameworkConfigurations.getSpecificPropertyIfExists("report_format_handler_map", "defaultView", (Object)null);
                            final Method method = Class.forName(formatHandler.getString("csv")).getMethod("generateCSVReport", String.class, HttpReqWrapper.class, FileOutputStream.class, Boolean.class);
                            status = (int)method.invoke(null, viewName, requestWrapper, fos, isEmptyReportNeeded);
                        }
                        if (status == 1112) {
                            DCScheduleReportUtil.out.log(Level.INFO, "{0} view is ignored as it is empty", new Object[] { viewName });
                            fos.close();
                            ApiFactoryProvider.getFileAccessAPI().deleteFile(fileName);
                            continue;
                        }
                    }
                    catch (final Exception ex) {
                        DCScheduleReportUtil.out.log(Level.SEVERE, "Schedule Report :Exception while creating csv file", ex);
                    }
                }
                else if ("pdf".equalsIgnoreCase(format)) {
                    try {
                        final Boolean isEmptyView = ScheduleReportUtil.checkIfViewIsEmpty(viewName, requestWrapper);
                        if (!isEmptyReportNeeded && isEmptyView) {
                            DCScheduleReportUtil.out.log(Level.INFO, "{0} view is ignored as it is empty", new Object[] { viewName });
                            fos.close();
                            ApiFactoryProvider.getFileAccessAPI().deleteFile(fileName);
                            continue;
                        }
                        if (formatHandler != null && formatHandler.has("pdf")) {
                            final ReportGenerationUtil formatUtil = (ReportGenerationUtil)Class.forName(formatHandler.getString("pdf")).newInstance();
                            formatUtil.generatePdfReport(viewName, requestWrapper, fos, customer_id);
                        }
                        else {
                            formatHandler = (JSONObject)FrameworkConfigurations.getSpecificPropertyIfExists("report_format_handler_map", "defaultView", (Object)null);
                            final Method method = Class.forName(formatHandler.getString("pdf")).getMethod("generatePDF", String.class, HttpServletRequest.class, OutputStream.class);
                            method.invoke(null, viewName, requestWrapper, fos);
                        }
                    }
                    catch (final Exception ex) {
                        DCScheduleReportUtil.out.log(Level.SEVERE, "Schedule Report :Exception while creating pdf file", ex);
                    }
                }
                else {
                    try {
                        int status = 0;
                        if (formatHandler != null && formatHandler.has("xsl")) {
                            final ReportGenerationUtil formatUtil = (ReportGenerationUtil)Class.forName(formatHandler.getString("xsl")).newInstance();
                            status = formatUtil.generateXslReport(viewName, requestWrapper, fos, isEmptyReportNeeded);
                        }
                        else {
                            formatHandler = (JSONObject)FrameworkConfigurations.getSpecificPropertyIfExists("report_format_handler_map", "defaultView", (Object)null);
                            final Method method = Class.forName(formatHandler.getString("xsl")).getMethod("generateXSLReport", String.class, HttpReqWrapper.class, FileOutputStream.class);
                            status = (int)method.invoke(null, viewName, requestWrapper, fos);
                        }
                        if (status == 1112) {
                            DCScheduleReportUtil.out.log(Level.INFO, "{0} view is ignored as it is empty", new Object[] { viewName });
                            fos.close();
                            ApiFactoryProvider.getFileAccessAPI().deleteFile(fileName);
                            continue;
                        }
                    }
                    catch (final Exception ex) {
                        DCScheduleReportUtil.out.log(Level.SEVERE, "Schedule Report :Exception while creating xslr file", ex);
                    }
                }
                files.add(fileName);
                fos.close();
            }
            DCScheduleReportUtil.out.log(Level.INFO, "file generated in " + format + "format");
        }
        catch (final Exception ex2) {
            DCScheduleReportUtil.out.log(Level.SEVERE, "Schedule Report :Exception in generating files", ex2);
            if (fos != null) {
                try {
                    CustomerInfoThreadLocal.clearClientThreadSettings();
                    fos.close();
                }
                catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
        finally {
            if (fos != null) {
                try {
                    CustomerInfoThreadLocal.clearClientThreadSettings();
                    fos.close();
                }
                catch (final IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
        return files;
    }
    
    public static String getRepFormatName(final int repFormat) {
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
    
    private static String getpatchRequest(final String viewid) {
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
    
    public static void generatePDFReport(final String viewName, final HttpReqWrapper requestWrapper, final FileOutputStream fos) {
        try {
            PDFUtil.generatePDF(viewName, (HttpServletRequest)requestWrapper, (OutputStream)fos);
        }
        catch (final Exception ex) {
            DCScheduleReportUtil.out.log(Level.SEVERE, "Schedule Report :Exception while creating pdf file", ex);
        }
    }
    
    public void getTaskDetailsAndPopulateForm(final HttpServletRequest request, final Long taskID) {
        try {
            DCScheduleReportUtil.out.log(Level.INFO, "Schedule Report :Entered into ScheduleReportAction.getTaskDetailsAndPopulateForm()");
            final ArrayList scheduleRepid = new ArrayList();
            final ArrayList preDefinedRep = new ArrayList();
            final ArrayList report_id = new ArrayList();
            String selectColumn = "";
            request.setAttribute("isedit", (Object)true);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ScheduleRepToReportRel"));
            final Column critcol = Column.getColumn("ScheduleRepToReportRel", "TASK_ID");
            final Join scheduleRepJoin = new Join("ScheduleRepToReportRel", "ScheduleRepTask", new String[] { "TASK_ID" }, new String[] { "TASK_ID" }, 2);
            Criteria cri = new Criteria(critcol, (Object)taskID, 0);
            final Long loginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            final boolean isAdminUser = DMUserHandler.isUserInAdminRole(loginId);
            if (!isAdminUser) {
                final Criteria managedCustCrit = new Criteria(Column.getColumn("ScheduleRepTask", "CUSTOMER_ID"), (Object)MSPWebClientUtil.getCustomerID(request), 0);
                cri = cri.and(managedCustCrit);
            }
            selectQuery.addJoin(scheduleRepJoin);
            selectQuery.addSelectColumn(Column.getColumn("ScheduleRepToReportRel", "REPORT_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ScheduleRepToReportRel", "REPORT_TYPE"));
            selectQuery.addSelectColumn(Column.getColumn("ScheduleRepToReportRel", "SCHEDULE_REP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ScheduleRepToReportRel", "TASK_ID"));
            selectQuery.setCriteria(cri);
            final Persistence persistence = SyMUtil.getPersistence();
            final DataObject extnTaskDo = persistence.get(selectQuery);
            DCScheduleReportUtil.out.log(Level.FINE, "Data object ScheduleRepToReportRel " + extnTaskDo);
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
            final JSONObject existingFilters = ReportCriteriaUtil.getInstance().getFilterJSON(taskID);
            if (existingFilters != null) {
                request.setAttribute("existingFilters", (Object)existingFilters.toString());
            }
            final Hashtable taskDetailHash = ScheduleReportUtil.getInstance().getScheduledTaskDetails(taskID);
            request.setAttribute("taskDetailHash", (Object)taskDetailHash);
            DCScheduleReportUtil.out.log(Level.INFO, "Successfully obtained the task details for the task {0}", taskDetailHash);
            request.setAttribute("selectcolumn", (Object)selectColumn);
            request.setAttribute("reportid", (Object)reports_id);
        }
        catch (final Exception ex2) {
            ex2.printStackTrace();
        }
    }
    
    public static DataObject getScheduleRepTaskDO() throws DataAccessException {
        DataObject scheduleRepDO = SyMUtil.getPersistence().get("ScheduleRepTask", (Criteria)null);
        if (scheduleRepDO == null || scheduleRepDO.isEmpty()) {
            scheduleRepDO = SyMUtil.getPersistence().constructDataObject();
        }
        return scheduleRepDO;
    }
    
    public static boolean deleteReportFiles(final String reportPaths, final int delFormat, final long attachLimit, final String scheduleReportPath) {
        long fileSize = 0L;
        final File repFile = new File(reportPaths);
        final File[] listFiles;
        final File[] fileList = listFiles = repFile.listFiles();
        for (final File f : listFiles) {
            fileSize += f.length();
        }
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
        final File dateFolder = new File(scheduleReportPath + File.separator + DCScheduleReportUtil.date);
        final File[] emptyList = dateFolder.listFiles();
        if (emptyList.length == 0) {
            dateFolder.delete();
        }
        return true;
    }
    
    @Override
    public int deleteScheduleReport() {
        final Logger deleteLogger = Logger.getLogger("QueryExecutorLogger");
        try {
            final String server_home = SyMUtil.getInstallationDir();
            final SelectQuery queryForStatus = (SelectQuery)new SelectQueryImpl(Table.getTable("ScheduleBackupStatus"));
            queryForStatus.addSelectColumn(new Column((String)null, "*"));
            final DataObject dataObject = SyMUtil.getPersistence().get(queryForStatus);
            final Row dcBackupInfoRow = dataObject.getRow("ScheduleBackupStatus");
            if (dcBackupInfoRow != null) {
                final int noOfDays = (int)dcBackupInfoRow.get("HISTORY_PERIOD");
                final long days_in_millis = noOfDays * 24 * 60 * 60 * 1000L;
                final Long currTime = new Long(System.currentTimeMillis());
                final long days_in_long = currTime - days_in_millis;
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
                        fileName = server_home + File.separator + fileName;
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
                PublishedReportHandler.deletePublishedReportDetailsByGeneratedTime(days_in_long);
            }
            return 1000;
        }
        catch (final Exception ex) {
            deleteLogger.log(Level.WARNING, "Exception in delete file class", ex);
            return 1001;
        }
    }
    
    public int scheduleBackup(final String relativeFile) throws DataAccessException {
        try {
            DCScheduleReportUtil.out.log(Level.INFO, "Schedule Report :Entered into ScheduleReportUtil.scheduleBackup()");
            final String server_home = SyMUtil.getInstallationDir();
            final String file = server_home + File.separator + relativeFile;
            final File logDir = new File(file);
            if (!logDir.isDirectory()) {
                logDir.mkdirs();
            }
            final String fileName = logDir.getName();
            final long lastMod = logDir.lastModified();
            final File[] fileList = logDir.listFiles();
            final Criteria criteria = new Criteria(Column.getColumn("ScheduleBackupDetails", "FILE_NAME"), (Object)relativeFile, 0);
            final DataObject data = SyMUtil.getPersistence().get("ScheduleBackupDetails", criteria);
            Row backupFileInfoRow = data.getRow("ScheduleBackupDetails");
            if (backupFileInfoRow == null) {
                backupFileInfoRow = new Row("ScheduleBackupDetails");
                backupFileInfoRow.set("FILE_NAME", (Object)relativeFile);
                backupFileInfoRow.set("GENRATED_TIME", (Object)lastMod);
                data.addRow(backupFileInfoRow);
            }
            else {
                backupFileInfoRow.set("FILE_NAME", (Object)relativeFile);
                backupFileInfoRow.set("GENRATED_TIME", (Object)lastMod);
                data.updateRow(backupFileInfoRow);
            }
            SyMUtil.getPersistence().update(data);
            return 1000;
        }
        catch (final Exception ex) {
            DCScheduleReportUtil.out.log(Level.WARNING, "Exception in scheduleBackup...", ex);
            return 1001;
        }
    }
    
    public static boolean taskBelongsToUser(final Long taskID, final Long userID) {
        try {
            final Criteria taskCriteria = new Criteria(new Column("TaskToUserRel", "TASK_ID"), (Object)taskID, 0);
            final Criteria userCriteria = new Criteria(new Column("TaskToUserRel", "USER_ID"), (Object)userID, 0);
            final DataObject taskDO = SyMUtil.getPersistence().get("TaskToUserRel", taskCriteria.and(userCriteria));
            if (taskDO != null && !taskDO.isEmpty()) {
                return true;
            }
        }
        catch (final Exception e) {
            DCScheduleReportUtil.out.log(Level.SEVERE, "Exception while checking whether task belongs to User", e);
        }
        return false;
    }
    
    @Deprecated
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
            DCScheduleReportUtil.out.log(Level.SEVERE, "Exception while checking whether task belongs to User", e);
        }
        return false;
    }
    
    public static void initialiseNewScheduleReport(final HttpServletRequest request) {
        request.setAttribute("isedit", (Object)false);
        ApiFactoryProvider.getSchedulerAPI().setScheduledValuesInJson(null, request, true, false, true);
    }
    
    static {
        DCScheduleReportUtil.out = Logger.getLogger("ScheduleReportLogger");
    }
}
