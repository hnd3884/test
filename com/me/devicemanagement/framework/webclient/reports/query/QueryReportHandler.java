package com.me.devicemanagement.framework.webclient.reports.query;

import com.lowagie.text.Paragraph;
import com.lowagie.text.HeaderFooter;
import com.me.ems.framework.reports.core.pdf.PDFUtil;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.Phrase;
import com.lowagie.text.Chunk;
import com.lowagie.text.Image;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfPTable;
import com.me.ems.framework.reports.core.pdf.PdfDocument;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.me.devicemanagement.framework.webclient.factory.WebclientAPIFactoryProvider;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.jsqlparser.statement.select.PlainSelect;
import com.adventnet.ds.query.DMDataSetWrapper;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Arrays;
import net.sf.jsqlparser.util.TablesNamesFinder;
import java.util.HashMap;
import java.util.Map;
import net.sf.jsqlparser.statement.Statement;
import java.util.List;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.framework.server.util.FrameworkStatusCodes;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.security.SecureRandom;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import com.adventnet.db.api.RelationalAPI;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import java.util.Hashtable;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.UpdateQueryImpl;
import javax.transaction.TransactionManager;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.util.DBConstants;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccessException;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.IOException;
import com.me.devicemanagement.framework.webclient.export.ExportAuditUtils;
import com.me.devicemanagement.framework.webclient.schedulereport.ScheduleReportUtil;
import java.io.OutputStream;
import javax.swing.table.TableModel;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.util.I18NUtil;
import com.me.devicemanagement.framework.server.util.Utils;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.util.ProductClassLoader;
import com.me.devicemanagement.framework.webclient.admin.DBQueryExecutorAPI;
import java.util.logging.Level;
import com.adventnet.ds.query.Range;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;

public class QueryReportHandler
{
    private static Logger logger;
    
    public static String getTableFormData(final QueryReportAttrBean queryRepBean) {
        try {
            SyMLogger.info(QueryReportHandler.logger, "QueryReportHandler", "getTableFormData", "Entering into this method....");
            final String query = queryRepBean.getQueryVal();
            SyMLogger.info(QueryReportHandler.logger, "QueryReportHandler", "getTableFormData", "Query is ...." + query);
            try {
                String modifiedQuery = modifyQuery(query);
                modifiedQuery = modifiedQuery.trim();
                final Range range = new Range(0, Integer.parseInt(queryRepBean.getDataPerPageVal()));
                modifiedQuery = setOrderByInQuery(modifiedQuery, range);
                SyMLogger.info(QueryReportHandler.logger, "QueryReportHandler", "getTableFormData", "Modified Query is ...." + modifiedQuery);
                getTableModelData(modifiedQuery, true, queryRepBean);
            }
            catch (final Exception ex) {
                SyMLogger.error(QueryReportHandler.logger, "QueryReportHandler", "getTableFormData", "Error while getting data from query result...", ex);
            }
            if (queryRepBean.getSqlError().length() == 0) {
                queryRepBean.setSqlError("");
            }
            return queryRepBean.getSqlError();
        }
        catch (final Exception e) {
            QueryReportHandler.logger.log(Level.SEVERE, "Exception occurred : " + e);
            return null;
        }
    }
    
    public static String getCountQuery(final String originalQuery) {
        String countSql = originalQuery.toUpperCase();
        countSql = countSql.replace("\n", " ");
        final int firstIndex = countSql.indexOf(" FROM ");
        final int lastIndex = countSql.lastIndexOf(" FROM ");
        if (firstIndex != lastIndex) {
            return "select count(*) as count from (" + originalQuery + ") sel";
        }
        final int groubByIndex = countSql.lastIndexOf("GROUP BY");
        final int orderByIndex = countSql.lastIndexOf("ORDER BY");
        if (groubByIndex != -1) {
            final String countSqlWithoutGroupBy = originalQuery.substring(lastIndex, groubByIndex);
            String groupByColumns;
            if (orderByIndex != -1) {
                groupByColumns = originalQuery.substring(groubByIndex + 8, orderByIndex).trim();
            }
            else {
                groupByColumns = originalQuery.substring(groubByIndex + 8, originalQuery.length()).trim();
            }
            final String toReturn = "select COUNT(DISTINCT (" + groupByColumns + ") ) as count " + countSqlWithoutGroupBy;
            return toReturn;
        }
        if (groubByIndex == -1 && orderByIndex != -1) {
            final String countSqlWithoutGroupBy = originalQuery.substring(lastIndex, orderByIndex);
            final String orderByColumns = originalQuery.substring(orderByIndex + 8, originalQuery.length()).trim();
            final String toReturn = "select COUNT(DISTINCT (" + orderByColumns + ") ) as count " + countSqlWithoutGroupBy;
            return toReturn;
        }
        return "select count(*) as count " + originalQuery.substring(lastIndex);
    }
    
    public static String getQueryType(String query) {
        query = query.trim();
        query = query.substring(0, query.indexOf(32));
        return query;
    }
    
    public static int getTableModelData(final String query, final boolean setColumnNameFlag, final QueryReportAttrBean queryRepBean) {
        ArrayList colList = null;
        TableModel tm = null;
        int rowCount = 0;
        try {
            tm = (TableModel)((DBQueryExecutorAPI)Class.forName(ProductClassLoader.getSingleImplProductClass("DM_DB_QUERY_EXECUTOR_IMPL_CLASS")).newInstance()).getTableModel(query, null, false);
            rowCount = tm.getRowCount();
            final int colCount = tm.getColumnCount();
            ArrayList rowDataList = new ArrayList();
            rowDataList = queryRepBean.getDataList();
            if (setColumnNameFlag) {
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
                        if (!columnName.contains("ROW_NUM")) {
                            colNameList.add(columnName);
                        }
                    }
                }
                rowDataList.add(colNameList);
            }
            final String queryType = getQueryType(query).toLowerCase().trim();
            if (rowCount == 0) {
                queryRepBean.navigationList[1] = "0 - 0";
                queryRepBean.navigationList[2] = "false";
            }
            if (tm != null && rowCount > 0) {
                for (int j = 0; j < rowCount; ++j) {
                    colList = new ArrayList();
                    int count2 = 0;
                    for (int k = 0; k < colCount; ++k) {
                        if (tm.getColumnName(k).toUpperCase().endsWith("_DATE_FORMAT")) {
                            final Long val = (Long)tm.getValueAt(j, k);
                            if (val != null && val > 0L) {
                                final String colValue = Utils.getEventTime(val);
                                colList.add(colValue);
                            }
                            else {
                                colList.add(tm.getValueAt(j, k));
                            }
                        }
                        else if (tm.getColumnName(k).toUpperCase().endsWith("_I18N_REMARK_" + count2)) {
                            String remarks = (String)tm.getValueAt(j, k);
                            String remarks_args = null;
                            for (int l = 0; l < colCount; ++l) {
                                if (tm.getColumnName(l).toUpperCase().endsWith("_I18N_REMARK_ARGS_" + count2)) {
                                    remarks_args = (String)tm.getValueAt(j, l);
                                    break;
                                }
                            }
                            remarks = I18NUtil.transformRemarks(remarks, remarks_args);
                            colList.add(remarks);
                            ++count2;
                        }
                        else if (!tm.getColumnName(k).toUpperCase().contains("_I18N_REMARK_ARGS_")) {
                            if (!tm.getColumnName(k).toUpperCase().contains("ROW_NUM")) {
                                colList.add(tm.getValueAt(j, k));
                            }
                        }
                    }
                    rowDataList.add(colList);
                }
                final int dataListSize = rowDataList.size();
                final int data_range = Integer.parseInt(queryRepBean.getDataPerPageVal());
                final int totalRecord = queryRepBean.getTotalRecord();
                if (totalRecord > data_range + queryRepBean.getRangeVal() && queryType.equals("select")) {
                    queryRepBean.navigationList[2] = "true";
                }
                else {
                    queryRepBean.navigationList[2] = "false";
                }
                queryRepBean.navigationList[1] = "1 - " + (dataListSize - 1);
            }
            else {
                rowDataList.clear();
                final ArrayList colNameList2 = new ArrayList();
                for (int i = 0; i < colCount; ++i) {
                    colNameList2.add(tm.getColumnName(i));
                }
                rowDataList.add(colNameList2);
                queryRepBean.setSqlError(I18N.getMsg("dc.common.NO_DATA_AVAILABLE", new Object[0]));
            }
        }
        catch (final Exception e) {
            queryRepBean.setSqlError(e.getMessage());
            SyMLogger.error(QueryReportHandler.logger, "QueryReportHandler", "getTableModelData", "Error while executing  query...", e);
        }
        return rowCount;
    }
    
    public int getReportAsCSV(final String query, final QueryReportAttrBean queryRepBean, final OutputStream outputStream) throws IOException {
        try {
            SyMLogger.info(QueryReportHandler.logger, "QueryReportHandler", "getReportAsCSV", "Going to Export Report as CSV .... ");
            queryRepBean.getDataList().clear();
            final String modifiedQuery = modifyQuery(query);
            int rowCount = 10000;
            String tempQuery = null;
            try {
                int recordIndex = 0;
                String csvString;
                while (true) {
                    final Range range = new Range(recordIndex, 10000);
                    tempQuery = setOrderByInQuery(modifiedQuery, range);
                    if (recordIndex == 0) {
                        rowCount = getTableModelData(tempQuery, true, queryRepBean);
                        SyMLogger.info(QueryReportHandler.logger, "QueryReportHandler", "getReportAsCSV", "RowCount 12 " + rowCount);
                    }
                    else {
                        rowCount = getTableModelData(tempQuery, false, queryRepBean);
                    }
                    SyMLogger.info(QueryReportHandler.logger, "QueryReportHandler", "getReportAsCSV", "RowCount 2 " + rowCount);
                    csvString = ScheduleReportUtil.getCSVString(queryRepBean, QueryReportHandler.logger);
                    if (rowCount < 10000) {
                        break;
                    }
                    outputStream.write(csvString.getBytes());
                    outputStream.flush();
                    queryRepBean.getDataList().clear();
                    if (recordIndex == 0) {
                        ++recordIndex;
                    }
                    recordIndex += 10000;
                }
                outputStream.write(csvString.getBytes());
                outputStream.flush();
                SyMLogger.info(QueryReportHandler.logger, "QueryReportHandler", "getReportAsCSV", "Export CSV is successfully completed");
                ExportAuditUtils.queryReportLogger(queryRepBean, 5);
            }
            catch (final Exception ex) {
                queryRepBean.setSqlError(ex.getMessage());
                SyMLogger.error(QueryReportHandler.logger, "QueryReportHandler", "getReportAsCSV", "Error while generating CSV...", ex);
                return 1107;
            }
            finally {
                try {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                }
                catch (final Exception ex2) {
                    SyMLogger.error(QueryReportHandler.logger, "QueryReportHandler", "getReportAsCSV", "Error while closing outputstream...", ex2);
                }
            }
        }
        catch (final Exception e) {
            QueryReportHandler.logger.log(Level.SEVERE, "Exception occurred : " + e);
            return 1107;
        }
        return 1106;
    }
    
    public static void getNavigationData(final String query, final QueryReportAttrBean queryRepBean, final String nextOrPrevious, int nextRange) {
        try {
            queryRepBean.getDataList().clear();
            int rangeVal = queryRepBean.getRangeVal();
            if (nextOrPrevious != null) {
                if (nextOrPrevious.equals("showNext")) {
                    rangeVal += nextRange;
                    queryRepBean.navigationList[0] = "true";
                    queryRepBean.navigationList[2] = "true";
                    if (rangeVal + nextRange > queryRepBean.getTotalRecord()) {
                        nextRange = queryRepBean.getTotalRecord() - rangeVal;
                    }
                }
                else if (rangeVal > nextRange) {
                    rangeVal -= nextRange;
                }
                else {
                    rangeVal = 0;
                    queryRepBean.navigationList[0] = "false";
                    queryRepBean.navigationList[2] = "true";
                }
                queryRepBean.setRangeVal(rangeVal);
            }
            String modifiedQuery = modifyQuery(query);
            final Range range = new Range((int)queryRepBean.getRangeVal(), nextRange);
            modifiedQuery = setOrderByInQuery(modifiedQuery, range);
            if (query == null) {
                return;
            }
            getTableModelData(modifiedQuery, true, queryRepBean);
            final int dataListSize = queryRepBean.getDataList().size();
            queryRepBean.navigationList[1] = queryRepBean.getRangeVal() + 1 + " - " + (queryRepBean.getRangeVal() + (dataListSize - 1));
            if (dataListSize < nextRange) {
                queryRepBean.navigationList[2] = "false";
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(QueryReportHandler.logger, "QueryReportHandler", "getNavigationData", "Error while nevigating report ...", ex);
        }
    }
    
    public static String modifyQuery(String query) {
        try {
            if (query != null) {
                String firstPart = "";
                String dateValue = "";
                String columnName = "";
                String columnNameAlias = "";
                String fullColumnName = "";
                DateFormat format1;
                Date date;
                Long dateInLong;
                for (query = query.replaceAll("\\r\\n|\\r|\\n", " "); query.indexOf("DATE_TO_LONG") != -1; query = query.replaceFirst("DATE_TO_LONG", ""), query = query.replaceFirst(dateValue, dateInLong.toString())) {
                    firstPart = query.substring(query.indexOf("DATE_TO_LONG"), query.length());
                    dateValue = firstPart.substring(firstPart.indexOf("(", 0) + 1, firstPart.indexOf(")", 0));
                    format1 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    date = format1.parse(dateValue.trim());
                    dateInLong = date.getTime();
                }
                while (query.indexOf("LONG_TO_DATE") != -1) {
                    firstPart = query.substring(query.indexOf("LONG_TO_DATE"), query.length());
                    fullColumnName = (columnName = firstPart.substring(firstPart.indexOf("(", 0) + 1, firstPart.indexOf(")", 0)));
                    if (fullColumnName.indexOf(44) >= 0) {
                        columnName = firstPart.substring(firstPart.indexOf("(", 0) + 1, firstPart.indexOf(",", 0));
                        columnNameAlias = firstPart.substring(firstPart.indexOf(",", 0) + 1, firstPart.indexOf(")", 0));
                        if (columnNameAlias.contains("\"") && !columnNameAlias.contains(",")) {
                            columnNameAlias = columnNameAlias.substring(columnNameAlias.indexOf("\"") + 1, columnNameAlias.lastIndexOf("\""));
                        }
                    }
                    else {
                        columnNameAlias = columnName;
                    }
                    if (columnNameAlias.indexOf(44) >= 0 || columnNameAlias.length() >= 52) {
                        break;
                    }
                    query = query.replaceFirst("LONG_TO_DATE", " ");
                    final String replaceStr = columnName + " as \"" + columnNameAlias + "_DATE_FORMAT" + "\"";
                    columnName = "(" + fullColumnName + ")";
                    query = query.replace(columnName, replaceStr);
                }
                String replaceStr2;
                for (int count = 0; query.indexOf("I18N_TRANSLATE") != -1; query = query.replace(columnName, replaceStr2), ++count) {
                    firstPart = query.substring(query.indexOf("I18N_TRANSLATE"), query.length());
                    columnName = firstPart.substring(firstPart.indexOf("(", 0) + 1, firstPart.indexOf(")", 0));
                    query = query.replaceFirst("I18N_TRANSLATE", " ");
                    replaceStr2 = "";
                    final String[] I18NColumns = columnName.split(",");
                    final String remarksreplace = I18NColumns[0] + " as \"" + I18NColumns[0] + "_I18N_REMARK_" + count + "\"";
                    query = query.replaceFirst(I18NColumns[0], remarksreplace);
                    replaceStr2 = remarksreplace;
                    if (I18NColumns.length > 1) {
                        final String remarksArgsReplace = I18NColumns[1] + " as \"" + I18NColumns[1] + "_I18N_REMARK_ARGS_" + count + "\"";
                        query = query.replaceFirst(I18NColumns[1], remarksArgsReplace);
                        replaceStr2 = replaceStr2 + "," + remarksArgsReplace;
                    }
                    columnName = "(" + replaceStr2 + ")";
                }
            }
        }
        catch (final Exception e) {
            SyMLogger.error(QueryReportHandler.logger, "QueryReportHandler", "modifyQuery", "Error while modifying the query ...", e);
        }
        query = replaceDateTemplate(query);
        SyMLogger.info(QueryReportHandler.logger, "QueryReportHandler", "getTableFormData", " Modified query after Date Template and Date function Query is === " + query);
        return query;
    }
    
    public Long saveCRViewDetails(final String queryName, final String query, final long loginUserID, final int noOfRecords, final QueryReportAttrBean queryReportAttrBean, final String dbName, final Long subModuleId) throws DataAccessException {
        queryReportAttrBean.setRunTypeVal("runandsave");
        return this.saveCRViewDetailswithoutQueryBean(queryName, query, loginUserID, noOfRecords, dbName, subModuleId);
    }
    
    public Long saveCRViewDetailswithoutQueryBean(final String queryName, final String query, final long loginUserID, final int noOfRecords, final String dbName, final Long subModuleId) throws DataAccessException {
        final Row crViewDetailsRow = new Row("CRSaveViewDetails");
        final DataObject crSaveDO = (DataObject)new WritableDataObject();
        try {
            final int db_type = DBConstants.getDBTypeByDBName(dbName);
            final Date date = new Date();
            crViewDetailsRow.set("QUERYID", (Object)null);
            crViewDetailsRow.set("VIEWID", (Object)null);
            crViewDetailsRow.set("CRVIEWNAME", (Object)queryName.toUpperCase());
            crViewDetailsRow.set("DISPLAY_CRVIEWNAME", (Object)queryName);
            crViewDetailsRow.set("SUB_MODULE_ID", (Object)subModuleId);
            crViewDetailsRow.set("CRVIEW_DESCRIPTION", (Object)"Test Description");
            crViewDetailsRow.set("LAST_MODIFIED_TIME", (Object)date.getTime());
            crViewDetailsRow.set("QR_QUERY", (Object)query);
            crViewDetailsRow.set("NUM_OF_RECORDS", (Object)noOfRecords);
            crViewDetailsRow.set("USER_ID", (Object)loginUserID);
            crViewDetailsRow.set("DB_TYPE", (Object)db_type);
            crSaveDO.addRow(crViewDetailsRow);
            SyMUtil.getPersistence().add(crSaveDO);
        }
        catch (final DataAccessException dataAccessException) {
            dataAccessException.printStackTrace();
            throw dataAccessException;
        }
        return (Long)crViewDetailsRow.get("CRSAVEVIEW_ID");
    }
    
    public int saveCrToCustomerRelRowDetails(final Long customerId, final Long crSaveViewId) throws DataAccessException {
        final Row crToCustomerRelRow = new Row("CRToCustomerRel");
        try {
            final DataObject crSaveDO = (DataObject)new WritableDataObject();
            if (customerId != 0L) {
                crToCustomerRelRow.set("CR_VIEW_ID", (Object)crSaveViewId);
                crToCustomerRelRow.set("CUSTOMER_ID", (Object)customerId);
                crSaveDO.addRow(crToCustomerRelRow);
            }
            SyMUtil.getPersistence().add(crSaveDO);
        }
        catch (final DataAccessException dataAccessException) {
            dataAccessException.printStackTrace();
            throw dataAccessException;
        }
        return 1000;
    }
    
    public int saveCRViewParamsDetails(String action_URL, final Boolean isViewIdAppendNeeded, final Long crSaveViewId) throws DataAccessException {
        try {
            final DataObject dataObject = (DataObject)new WritableDataObject();
            if (isViewIdAppendNeeded) {
                action_URL += crSaveViewId;
            }
            final Row crViewParamsRow = new Row("CRViewParams");
            crViewParamsRow.set("CRSAVEVIEW_ID", (Object)crSaveViewId);
            crViewParamsRow.set("ACTION_URL", (Object)action_URL);
            dataObject.addRow(crViewParamsRow);
            SyMUtil.getPersistence().add(dataObject);
            return 1000;
        }
        catch (final DataAccessException dataAccessException) {
            dataAccessException.printStackTrace();
            throw dataAccessException;
        }
    }
    
    public int saveQueryReportDetails(final String queryName, final String query, final long loginUserID, final int noOfRecords, final QueryReportAttrBean queryReportAttrBean, final String dbType, final String action_URL, final Boolean isViewIdAppendNeededInActionUrl, final Long submoduleid) throws Exception {
        final TransactionManager txMgr = SyMUtil.getUserTransaction();
        try {
            txMgr.begin();
            final Long crSaveViewId = this.saveCRViewDetails(queryName, query, loginUserID, noOfRecords, queryReportAttrBean, dbType, submoduleid);
            QueryReportHandler.logger.log(Level.INFO, crSaveViewId.toString());
            Long customerId = 0L;
            customerId = CustomerInfoUtil.getInstance().getCustomerId();
            this.saveCrToCustomerRelRowDetails(customerId, crSaveViewId);
            this.saveCRViewParamsDetails(action_URL, isViewIdAppendNeededInActionUrl, crSaveViewId);
            QueryReportHandler.logger.log(Level.INFO, "update success");
        }
        catch (final Exception e) {
            QueryReportHandler.logger.log(Level.INFO, "Exception while saving report " + queryName);
            e.printStackTrace();
            txMgr.rollback();
            return 1001;
        }
        txMgr.commit();
        return 1000;
    }
    
    public int updateQueryReport(final String query, final String queryName, final int noOfRecords, final QueryReportAttrBean queryReportAttrBean) {
        queryReportAttrBean.setRunTypeVal("runandsave");
        return this.updateQueryReportwithoutQueryBean(query, queryName, noOfRecords);
    }
    
    public int updateQueryReportwithoutQueryBean(final String query, final String queryName, final int noOfRecords) {
        try {
            final Date date = new Date();
            final UpdateQuery updatequery = (UpdateQuery)new UpdateQueryImpl("CRSaveViewDetails");
            final String queryNameInUpperCase = queryName.toUpperCase();
            final Criteria criteria = new Criteria(new Column("CRSaveViewDetails", "CRVIEWNAME"), (Object)queryNameInUpperCase, 0);
            updatequery.setCriteria(criteria);
            updatequery.setUpdateColumn("QR_QUERY", (Object)query);
            updatequery.setUpdateColumn("DISPLAY_CRVIEWNAME", (Object)queryName);
            updatequery.setUpdateColumn("NUM_OF_RECORDS", (Object)noOfRecords);
            updatequery.setUpdateColumn("LAST_MODIFIED_TIME", (Object)date.getTime());
            SyMUtil.getPersistence().update(updatequery);
            return 1000;
        }
        catch (final Exception ex) {
            SyMLogger.error(QueryReportHandler.logger, "QueryReportHandler", "updateQueryReport", "Error while update query report ...", ex);
            return 1001;
        }
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
            if (query.indexOf("<from_currentmonth>") > 0) {
                ht = DateTimeUtil.determine_From_To_Times("current_month");
                query = query.replaceAll("<from_currentmonth>", ht.get("date1").toString());
            }
            if (query.indexOf("<to_currentmonth>") > 0) {
                ht = DateTimeUtil.determine_From_To_Times("current_month");
                query = query.replaceAll("<to_currentmonth>", ht.get("date2").toString());
            }
        }
        catch (final Exception e) {
            SyMLogger.error(QueryReportHandler.logger, "QueryReportHandler", "replaceDateTemplate", "Error while replaceDateTemplate in query  ...", e);
        }
        return query;
    }
    
    public static String setOrderByColumn(final String modifiedQuery) throws SQLException {
        String column_name = "";
        TableModel tm = null;
        try {
            final Range rangeforOrderBy = new Range(0, 1);
            final String queryforOrderBy = RelationalAPI.getInstance().getDBAdapter().getSQLModifier().getSQLForSelectWithRange(modifiedQuery, rangeforOrderBy);
            tm = (TableModel)((DBQueryExecutorAPI)Class.forName(ProductClassLoader.getSingleImplProductClass("DM_DB_QUERY_EXECUTOR_IMPL_CLASS")).newInstance()).getTableModel(queryforOrderBy, null, false);
            column_name = tm.getColumnName(1);
        }
        catch (final Exception ex) {
            SyMLogger.error(QueryReportHandler.logger, "QueryReportHandler", "setOrderByColumn", "Error while setOrderByColumn in query  ...", ex);
        }
        return column_name;
    }
    
    public static String setOrderByInQuery(final String modifiedQuery, final Range range) {
        String queryType = "";
        queryType = getQueryType(modifiedQuery).toLowerCase();
        queryType = queryType.trim();
        return setOrderByInQuery(modifiedQuery, range, queryType);
    }
    
    public static String setOrderByInQuery(String modifiedQuery, final Range range, final String queryType) {
        try {
            if (queryType != null && queryType.equals("select")) {
                final String modifiedQuery_order = modifiedQuery.toUpperCase();
                if (modifiedQuery_order.indexOf("ORDER BY") == -1) {
                    if (modifiedQuery_order.startsWith("SELECT * FROM")) {
                        final String column_name = setOrderByColumn(modifiedQuery);
                        modifiedQuery = modifiedQuery + " ORDER BY " + column_name;
                    }
                    else {
                        modifiedQuery += " ORDER BY 1";
                    }
                }
                modifiedQuery = RelationalAPI.getInstance().getDBAdapter().getSQLModifier().getSQLForSelectWithRange(modifiedQuery, range);
            }
        }
        catch (final SQLException sqlException) {
            SyMLogger.error(QueryReportHandler.logger, "QueryReportHandler", "setOrderByInQuery", "Error while setting orderBy for the query...", sqlException);
        }
        catch (final QueryConstructionException queryConstructionException) {
            SyMLogger.error(QueryReportHandler.logger, "QueryReportHandler", "setOrderByInQuery", "Error while setting orderBy for the query...", (Throwable)queryConstructionException);
        }
        return modifiedQuery;
    }
    
    public static QueryReportAttrBean setCRSaveViewDetailsInBean(final String viewName, final QueryReportAttrBean queryRepBean, final Boolean isQueryNameWithRandomInt, final String recordCountCriteria) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CRSaveViewDetails"));
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            final Criteria criteria = new Criteria(Column.getColumn("CRSaveViewDetails", "CRVIEWNAME"), (Object)viewName, 0);
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = DataAccess.get(selectQuery);
            if (!dataObject.isEmpty()) {
                final String query = (String)dataObject.getFirstValue("CRSaveViewDetails", "QR_QUERY");
                String queryName = (String)dataObject.getFirstValue("CRSaveViewDetails", "DISPLAY_CRVIEWNAME");
                if (isQueryNameWithRandomInt) {
                    final SecureRandom randomGenerator = new SecureRandom();
                    final int randomInt = randomGenerator.nextInt(100);
                    queryName = queryName + "_" + randomInt;
                }
                final int noOfRecord = (int)dataObject.getFirstValue("CRSaveViewDetails", "NUM_OF_RECORDS");
                if ("".equals(queryRepBean.getQueryVal())) {
                    queryRepBean.setQueryVal(query);
                }
                if ("".equals(queryRepBean.getQueryNameVal())) {
                    queryRepBean.setQueryNameVal(queryName);
                }
                if ("100".equals(queryRepBean.getDataPerPageVal())) {
                    queryRepBean.setDataPerPageVal(noOfRecord + "");
                }
                if (queryRepBean.getTotalRecord() == 0) {
                    String countQuery = getCountQuery(query);
                    if (recordCountCriteria != null && !"".equals(recordCountCriteria.trim())) {
                        countQuery += recordCountCriteria;
                    }
                    final String modifiedCountQuery = modifyQuery(countQuery);
                    final int totalCount = DBUtil.getRecordCount(modifiedCountQuery);
                    queryRepBean.setTotalRecord(totalCount);
                }
            }
        }
        catch (final Exception e) {
            SyMLogger.error(QueryReportHandler.logger, "QueryReportHandler", "setCRSaveViewDetailsInBean", "Error while editing query Report...", e);
        }
        return queryRepBean;
    }
    
    public static SelectQuery getBaseQueryForQueryReport(final String queryName) {
        final SelectQuery baseQueryOfQueryReport = (SelectQuery)new SelectQueryImpl(Table.getTable("CRSaveViewDetails"));
        baseQueryOfQueryReport.addSelectColumn(Column.getColumn((String)null, "*"));
        final Criteria queryNameCri = new Criteria(Column.getColumn("CRSaveViewDetails", "DISPLAY_CRVIEWNAME"), (Object)queryName, 0, false);
        final Criteria qrQueryCri = new Criteria(Column.getColumn("CRSaveViewDetails", "QR_QUERY"), (Object)null, 1);
        final Criteria queryReportCriteria = queryNameCri.and(qrQueryCri);
        baseQueryOfQueryReport.setCriteria(queryReportCriteria);
        return baseQueryOfQueryReport;
    }
    
    public static int saveOrUpdateQueryReport(final String operationType, final QueryReportAttrBean queryRepBean, final long loginUserID, final String action_url, final Boolean isViewIdAppendNeededInActionUrl) {
        return saveOrUpdateQueryReport(operationType, queryRepBean, loginUserID, action_url, isViewIdAppendNeededInActionUrl, "ScheduleReportCriteria");
    }
    
    public static int saveOrUpdateQueryReport(final String operationType, final QueryReportAttrBean queryRepBean, final long loginUserID, final String action_url, final Boolean isViewIdAppendNeededInActionUrl, final String subModuleName) {
        try {
            final Long subModuleId = (Long)DBUtil.getRowFromDB("CRSubModule", "SUB_MODULE_NAME", subModuleName).get("SUB_MODULE_ID");
            final String dbName = DBUtil.getActiveDBName();
            if (operationType != null) {
                final String query = queryRepBean.getQueryVal();
                final String queryName = queryRepBean.getQueryNameVal();
                int dataPerPageVal = 0;
                final String dataPerPage = queryRepBean.getDataPerPageVal();
                if (dataPerPage != null) {
                    dataPerPageVal = Integer.parseInt(dataPerPage);
                }
                final QueryReportHandler queryReportHandler = new QueryReportHandler();
                if (operationType.equals("newQueryReport")) {
                    if (queryName != null && query != null) {
                        try {
                            queryReportHandler.saveQueryReportDetails(queryName, query, loginUserID, dataPerPageVal, queryRepBean, dbName, action_url, isViewIdAppendNeededInActionUrl, subModuleId);
                            SyMLogger.info(QueryReportHandler.logger, "QueryReportHandler", "saveOrUpdateQueryReport", "Query Report has been saved successfully ...");
                        }
                        catch (final Exception e) {
                            QueryReportHandler.logger.log(Level.INFO, "Error while tring to save reports" + e);
                        }
                    }
                }
                else if (operationType.equals("editReport")) {
                    final SelectQuery editQuery = getBaseQueryForQueryReport(queryName);
                    final DataObject dataObject = DataAccess.get(editQuery);
                    if (dataObject.isEmpty()) {
                        try {
                            queryReportHandler.saveQueryReportDetails(queryName, query, loginUserID, dataPerPageVal, queryRepBean, dbName, action_url, isViewIdAppendNeededInActionUrl, subModuleId);
                        }
                        catch (final Exception e2) {
                            QueryReportHandler.logger.log(Level.INFO, "Error while tring to save reports" + e2);
                        }
                    }
                    else {
                        queryReportHandler.updateQueryReport(query, queryName, dataPerPageVal, queryRepBean);
                        SyMLogger.info(QueryReportHandler.logger, "QueryReportHandler", "saveOrUpdateQueryReport", "Query Report has been updated successfully ...");
                    }
                }
            }
            return FrameworkStatusCodes.SUCCESS_RESPONSE_CODE;
        }
        catch (final DataAccessException dataAccessException) {
            SyMLogger.error(QueryReportHandler.logger, "QueryReportHandler", "editQueryReport", "Error while Saving and Retrieving  query data...", (Throwable)dataAccessException);
            return FrameworkStatusCodes.UNPROCESSABLE_DATA_CODE;
        }
        catch (final Exception exception) {
            SyMLogger.error(QueryReportHandler.logger, "QueryReportHandler", "editQueryReport", "Error while Saving and Retrieving  query data...", exception);
            return 1001;
        }
    }
    
    public static int deleteQueryReport(final Long viewID) {
        try {
            SyMLogger.info(QueryReportHandler.logger, "QueryReportHandler", "deleteQueryReport", "Going to delte query Report View ... ViewID is" + viewID);
            if (viewID != 0L) {
                final Criteria criteria = new Criteria(Column.getColumn("CRSaveViewDetails", "CRSAVEVIEW_ID"), (Object)viewID, 0);
                DataAccess.delete("CRSaveViewDetails", criteria);
                SyMLogger.info(QueryReportHandler.logger, "QueryReportHandler", "deleteQueryReport", "Reports has been deleted successfully. Query Report ID is " + viewID);
            }
            return FrameworkStatusCodes.SUCCESS_RESPONSE_CODE;
        }
        catch (final DataAccessException dataAccessException) {
            dataAccessException.printStackTrace();
            return FrameworkStatusCodes.UNPROCESSABLE_DATA_CODE;
        }
    }
    
    public static int setEmptyMessageInQueryReportView(final String message) {
        try {
            final Criteria criteria = new Criteria(Column.getColumn("CRSaveViewDetails", "QR_QUERY"), (Object)null, 1);
            final DataObject dataObject = SyMUtil.getPersistence().get("CRSaveViewDetails", criteria);
            if (dataObject != null && dataObject.isEmpty()) {
                MessageProvider.getInstance().unhideMessage(message);
            }
        }
        catch (final DataAccessException e) {
            e.printStackTrace();
            return 1201;
        }
        return 1200;
    }
    
    public static Boolean isQueryNameAvailableInQueryReport(final String queryName) {
        try {
            final SelectQuery query = getBaseQueryForQueryReport(queryName);
            final DataObject dataObject = DataAccess.get(query);
            if (dataObject.isEmpty()) {
                return true;
            }
            return false;
        }
        catch (final DataAccessException dataAccessException) {
            dataAccessException.printStackTrace();
            return null;
        }
    }
    
    public static boolean isQueryAllowed(final String query) {
        try {
            final Statements statements = CCJSqlParserUtil.parseStatements(query);
            final List<Statement> statementsList = statements.getStatements();
            if (!(statementsList.get(0) instanceof Select) || statementsList.size() > 1) {
                SyMLogger.warning(QueryReportHandler.logger, "QueryReportAction", "getQueryReportDataandSave", "Query Not Allowed");
                return false;
            }
            return true;
        }
        catch (final JSQLParserException e) {
            SyMLogger.error(QueryReportHandler.logger, "QueryReportAction", "isQueryAllowed", "Incorrect Query", (Throwable)e);
            return false;
        }
    }
    
    public static Map<String, String> queryValidation(final String query) throws Exception {
        final Map<String, String> queryValidation = new HashMap<String, String>();
        String isValidQuery = "true";
        String message = "";
        try {
            final Statements statements = CCJSqlParserUtil.parseStatements(query);
            final List<Statement> statementsList = statements.getStatements();
            if (!(statementsList.get(0) instanceof Select) || statementsList.size() > 1) {
                isValidQuery = "false";
                message = I18N.getMsg("Select query only supported", new Object[0]);
            }
            final Statement statement = CCJSqlParserUtil.parse(query);
            final TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
            final Select select = (Select)statement;
            final List<String> tableNames = tablesNamesFinder.getTableList((Statement)select);
            final ListIterator<String> iterator = tableNames.listIterator();
            while (iterator.hasNext()) {
                String tableName = iterator.next();
                if (tableName.contains("\"")) {
                    tableName = tableName.replace("\"", "");
                }
                iterator.set(tableName.toLowerCase());
            }
            final List<String> restrictList = Arrays.asList("pg_shadow", "aaaacchttpsession", "Credential", "aaapassword", "DCConfigCredentials", "LocalUserProps", "SchedulerConfig", "WirelessConfigProps", "InstallCertificateConfig", "JiraCredentials", "SERVICENOWCREDENTIALS", "OSImagingAttributes", "OSDSettings", "OSDClientCertAuth", "OSDDeploymentTaskInfo", "SDTaskDetails", "DeploymentUsers", "TemplateUsmtUserPassword", "MacUsmtUserPassword", "USMTEncryptionDetails", "RestoreUserProfileRel", "TemporaryAccess", "EMailAddr", "RCOnGoingMeetings", "GatewaySession", "GatewaySessionMembers", "ComputerChatSession", "ToolsChatMapping", "RemoteEventLogs", "RemoteRegistryKeyValue", "RemoteFileDetails", "SystemManagerExportHistory", "SystemManagerExportSettings", "ApnPolicy", "SCEPConfigurations", "VPNL2TP", "VPNIKEv2", "VPNJuniperSSL", "VPNF5SSL", "VPNPaloAlto", "AndroidPasscodePolicy", "OpenVPNPolicy", "PayloadProxyConfig", "IOSNativeAppAuthentication", "AndroidAgentSettings", "MDMClientToken", "EASServerDetails", "ActiveDirectoryInfo", "SchedulerAdvProps", "SelectedComputer", "BranchOffProxyDetails", "DCServiceInfo", "DeviceAuditEventMail", "APIKeyDetails", "AAAUSERLINKDETAILS", "PublishedReportDetails");
            for (final String restrictTable : restrictList) {
                if (tableNames.contains(restrictTable.toLowerCase())) {
                    isValidQuery = "false";
                    message = I18N.getMsg("dc.rep.customReport.invalid_query", new Object[0]);
                    break;
                }
            }
        }
        catch (final JSQLParserException ex) {
            SyMLogger.error(QueryReportHandler.logger, "QueryReportHandler", "queryValidation", "Incorrect Query :" + query, (Throwable)ex);
            isValidQuery = "false";
            message = I18N.getMsg("dc.rep.customReport.given_query_wrong", new Object[0]);
        }
        queryValidation.put("isValidQuery", isValidQuery);
        queryValidation.put("message", message);
        return queryValidation;
    }
    
    public static DMDataSetWrapper executeQuery(final String query, final Integer startIndex, final Integer limit, final boolean validateOnly) throws Exception {
        DMDataSetWrapper dataSet = null;
        String modifiedQuery = modifyQuery(query);
        final Map<String, String> queryValidation = queryValidation(modifiedQuery);
        if (Boolean.valueOf(queryValidation.get("isValidQuery"))) {
            final Statements statements = CCJSqlParserUtil.parseStatements(modifiedQuery);
            final List<Statement> statementsList = statements.getStatements();
            final Select statement = (Select)statementsList.get(0);
            final PlainSelect plainSelect = (PlainSelect)statement.getSelectBody();
            final List orderByElements = plainSelect.getOrderByElements();
            modifiedQuery = statement.toString();
            if (orderByElements == null || orderByElements.isEmpty()) {
                final String orderByClause = " ORDER BY 1";
                modifiedQuery = modifiedQuery.concat(orderByClause);
            }
            final Range range = validateOnly ? new Range(0, 1) : new Range((int)startIndex, (int)limit);
            modifiedQuery = RelationalAPI.getInstance().getDBAdapter().getSQLModifier().getSQLForSelectWithRange(modifiedQuery, range);
            dataSet = DMDataSetWrapper.executeQuery(modifiedQuery, false);
            return dataSet;
        }
        final String message = queryValidation.get("message");
        throw new QueryConstructionException(message);
    }
    
    public static ArrayList transformColumnValues(final DMDataSetWrapper dataSet) {
        final ArrayList data = new ArrayList();
        try {
            final int columnCount = dataSet.getColumnCount();
            while (dataSet.next()) {
                final HashMap row = new HashMap();
                int count = 0;
                for (int columnIndex = 1; columnIndex <= columnCount; ++columnIndex) {
                    final String columnName = dataSet.getColumnName(columnIndex);
                    Object value = dataSet.getValue(columnName);
                    String displayName;
                    if (columnName.endsWith("_DATE_FORMAT")) {
                        displayName = columnName.substring(0, columnName.indexOf("_DATE_FORMAT"));
                        if (value != null) {
                            value = Utils.getEventTime(Long.parseLong(String.valueOf(value)));
                        }
                    }
                    else if (columnName.endsWith("_I18N_REMARK_" + count)) {
                        displayName = columnName.substring(0, columnName.indexOf("_I18N_REMARK_" + count));
                        final int argsColumnIndex = columnIndex + 1;
                        String remarksArgs = null;
                        if (argsColumnIndex <= columnCount && dataSet.getColumnName(argsColumnIndex).endsWith("_I18N_REMARK_ARGS_" + count)) {
                            final String argsColumn = dataSet.getColumnName(argsColumnIndex);
                            remarksArgs = String.valueOf(dataSet.getValue(argsColumn));
                        }
                        value = I18NUtil.transformRemarks(String.valueOf(value), remarksArgs);
                        ++count;
                    }
                    else {
                        if (columnName.contains("_I18N_REMARK_ARGS_")) {
                            continue;
                        }
                        if (columnName.contains("ROW_NUM")) {
                            continue;
                        }
                        displayName = columnName;
                    }
                    row.put(displayName, String.valueOf(value));
                }
                data.add(row);
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(QueryReportHandler.logger, "QueryReportHandler", "transformColumnValues", "Exception while transformColumnValues for query report result", ex);
        }
        return data;
    }
    
    public static ArrayList transformColumnDetails(final DMDataSetWrapper dataSet) {
        final ArrayList columnDetails = new ArrayList();
        try {
            final int colCount = dataSet.getColumnCount();
            int remarkCount = 0;
            for (int columnIndex = 1; columnIndex <= colCount; ++columnIndex) {
                final HashMap column = new HashMap();
                String columnName = dataSet.getColumnName(columnIndex);
                if (columnName.endsWith("_DATE_FORMAT")) {
                    columnName = columnName.substring(0, columnName.indexOf("_DATE_FORMAT"));
                }
                else if (columnName.endsWith("_I18N_REMARK_" + remarkCount)) {
                    columnName = columnName.substring(0, columnName.indexOf("_I18N_REMARK_" + remarkCount));
                    ++remarkCount;
                }
                else {
                    if (columnName.contains("_I18N_REMARK_ARGS_")) {
                        continue;
                    }
                    if (columnName.contains("ROW_NUM")) {
                        continue;
                    }
                }
                column.put("columnName", columnName);
                column.put("columnDataType", dataSet.getColumnType(columnIndex));
                columnDetails.add(column);
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(QueryReportHandler.logger, "QueryReportHandler", "transformColumnDetails", "Exception while transformColumnDetails for query report result", ex);
        }
        return columnDetails;
    }
    
    public static Long saveQueryReport(final String reportName, final String query, final Long userID, final Long customerID, final Integer limit) throws Exception {
        Long crSaveViewID = null;
        final QueryReportHandler queryReportHandler = new QueryReportHandler();
        final TransactionManager txMgr = SyMUtil.getUserTransaction();
        boolean txnbegin = false;
        if (txMgr != null) {
            try {
                final String subModuleName = "ScheduleReportCriteria";
                final String actionURL = "";
                final Long subModuleID = (Long)DBUtil.getValueFromDB("CRSubModule", "SUB_MODULE_NAME", subModuleName, "SUB_MODULE_ID");
                final String dbName = DBUtil.getActiveDBName();
                if (txMgr.getStatus() != 0) {
                    txnbegin = true;
                    txMgr.begin();
                }
                crSaveViewID = queryReportHandler.saveCRViewDetailswithoutQueryBean(reportName, query, userID, limit, dbName, subModuleID);
                final int customerRelStatus = queryReportHandler.saveCrToCustomerRelRowDetails(customerID, crSaveViewID);
                final int viewStatus = queryReportHandler.saveCRViewParamsDetails(actionURL, true, crSaveViewID);
                if (customerRelStatus != 1000 || viewStatus != 1000) {
                    if (txnbegin) {
                        txMgr.rollback();
                    }
                    QueryReportHandler.logger.log(Level.WARNING, "Exception while saveQueryReport name: " + reportName + " query: " + query + " userID: " + userID + " limit: " + limit);
                }
            }
            catch (final Exception ex) {
                if (txnbegin) {
                    txMgr.rollback();
                }
                SyMLogger.error(QueryReportHandler.logger, "QueryReportHandler", "saveQueryReport", "Exception while saveQueryReport name: " + reportName + " query: " + query + " userID: " + userID + " limit: " + limit, ex);
                throw ex;
            }
            if (txnbegin) {
                txMgr.commit();
            }
            return crSaveViewID;
        }
        QueryReportHandler.logger.log(Level.WARNING, "TransactionManager is null fir saving query report");
        throw new Exception("TransactionManager is null for saving query report");
    }
    
    public static String getCSVString(final ArrayList columnNames, final ArrayList data) {
        final StringBuilder csvBuilder = new StringBuilder();
        for (final Object row : data) {
            final Map rowMap = (Map)row;
            for (final Object columnName : columnNames) {
                final Object value = rowMap.get(columnName);
                final String valueStr = (value != null) ? value.toString() : "";
                if (valueStr.contains(",")) {
                    csvBuilder.append("\"").append(valueStr).append("\"");
                }
                else {
                    csvBuilder.append(valueStr);
                }
                csvBuilder.append(",");
            }
            csvBuilder.deleteCharAt(csvBuilder.length() - 1);
            csvBuilder.append("\n");
        }
        return csvBuilder.toString();
    }
    
    public static String getCSVHeaderString(final ArrayList columnNames) {
        final StringBuilder csvBuilder = new StringBuilder();
        for (final Object columnName : columnNames) {
            final String valueStr = (columnName != null) ? columnName.toString() : "";
            if (valueStr.contains(",")) {
                csvBuilder.append("\"").append(valueStr).append("\"");
            }
            else {
                csvBuilder.append(valueStr);
            }
            csvBuilder.append(",");
        }
        csvBuilder.deleteCharAt(csvBuilder.length() - 1);
        csvBuilder.append("\n");
        return csvBuilder.toString();
    }
    
    public void getReportAsPDF(final String query, final HttpServletResponse response, final HttpServletRequest req) {
        QueryReportHandler.logger.log(Level.INFO, "Going to Export Report as PDF .... ");
        final QueryReportAttrBean queryRepBean = (QueryReportAttrBean)WebclientAPIFactoryProvider.getSessionAPI().getSessionAttribute(req, "QRBean");
        OutputStream os = null;
        PdfDocument dcPdfDocument = null;
        try {
            String repName = queryRepBean.getQueryNameVal();
            if (repName == null || "".equals(repName)) {
                repName = "CustomQueryReport";
            }
            queryRepBean.getDataList().clear();
            final String csvFileName = repName + ".pdf";
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename= \"" + csvFileName + "\"");
            os = (OutputStream)response.getOutputStream();
            final String modifiedQuery = modifyQuery(query);
            String queryType = getQueryType(modifiedQuery).toLowerCase();
            queryType = queryType.trim();
            int rowcount;
            final int rowConstant = rowcount = 7500;
            String tempquery = null;
            Document document = null;
            PdfWriter pdfwriter = null;
            PdfPTable pTable = null;
            int recordindex = 0;
            while (true) {
                final Range range = new Range(recordindex, rowConstant);
                tempquery = setOrderByInQuery(modifiedQuery, range, queryType);
                if (recordindex == 0) {
                    rowcount = getTableModelData(tempquery, true, queryRepBean);
                    final String desc = I18N.getMsg("dc.rep.pdf.QUERY_REP_DESC", new Object[0]) + " - " + repName;
                    final ArrayList dataList = queryRepBean.getDataList();
                    String colValue = null;
                    float[] columnWidth = null;
                    final ArrayList columnNameList = dataList.get(0);
                    final int columnNameListCount = columnNameList.size();
                    int columnCnt = 0;
                    columnWidth = new float[columnNameListCount];
                    final float columnWidthVal = 100.0f / columnNameListCount;
                    final Iterator itr = columnNameList.iterator();
                    while (itr.hasNext()) {
                        colValue = itr.next() + "";
                        if (colValue.contains(",")) {
                            colValue = "\"" + colValue + "\"";
                        }
                        columnWidth[columnCnt] = columnWidthVal;
                        ++columnCnt;
                    }
                    if (columnNameListCount > 10) {
                        document = new Document(PageSize.A4.rotate());
                    }
                    else {
                        document = new Document(PageSize.A4);
                    }
                    pdfwriter = PdfWriter.getInstance(document, os);
                    document.open();
                    final Hashtable queryRepDetail_hash = new Hashtable();
                    queryRepDetail_hash.put("TITLE", "Query Report");
                    queryRepDetail_hash.put("NAME", "Query Report PDF");
                    queryRepDetail_hash.put("DESCRIPTION", desc);
                    this.setLogo(document);
                    dcPdfDocument = new PdfDocument(document);
                    dcPdfDocument.setTitleAndDescription(queryRepDetail_hash);
                    pTable = new PdfPTable(columnNameListCount);
                    pTable.setWidthPercentage(100.0f);
                    columnWidth = this.getSectionWidths(columnWidth, columnNameListCount);
                    pTable.setWidths(columnWidth);
                    pTable.setComplete(false);
                    final List columnValues = dataList.get(0);
                    this.setTableHeader(columnValues, pTable);
                    dataList.remove(0);
                    fetchPDFData(dataList, pTable, document);
                }
                else {
                    rowcount = getTableModelData(tempquery, true, queryRepBean);
                    final ArrayList dataList2 = queryRepBean.getDataList();
                    fetchPDFData(dataList2, pTable, document);
                }
                queryRepBean.getDataList().clear();
                if (rowcount < rowConstant) {
                    break;
                }
                if (pTable != null) {
                    document.add((Element)pTable);
                }
                recordindex += rowConstant;
            }
            pTable.setComplete(true);
            if (pTable != null) {
                pTable.setWidthPercentage(100.0f);
                pTable.setSpacingAfter(10.0f);
                document.add((Element)pTable);
            }
            QueryReportHandler.logger.log(Level.INFO, "Export PDF is successfully completed");
        }
        catch (final Exception ex) {
            queryRepBean.setSqlError(ex.getMessage());
            QueryReportHandler.logger.log(Level.WARNING, "Error while generating PDF...", ex);
            try {
                os.flush();
                if (dcPdfDocument != null) {
                    dcPdfDocument.close();
                }
                if (os != null) {
                    os.close();
                }
            }
            catch (final Exception ex) {
                QueryReportHandler.logger.log(Level.WARNING, "Error while closing outputstream...", ex);
            }
        }
        finally {
            try {
                os.flush();
                if (dcPdfDocument != null) {
                    dcPdfDocument.close();
                }
                if (os != null) {
                    os.close();
                }
            }
            catch (final Exception ex2) {
                QueryReportHandler.logger.log(Level.WARNING, "Error while closing outputstream...", ex2);
            }
        }
    }
    
    @Deprecated
    public void setLogo(final Document doc) throws Exception {
        final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
        setLogo(doc, customerId);
    }
    
    public static void setLogo(final Document document, final Long customerID) throws Exception {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100.0f);
        table.getDefaultCell().setPadding(0.0f);
        final String logoPath = CustomerInfoUtil.getInstance().getLogoPath(customerID);
        final Image i = Image.getInstance(logoPath);
        i.scalePercent(60.0f);
        final Chunk chunk = new Chunk(i, 0.0f, 0.0f);
        final PdfPCell logo = new PdfPCell(new Phrase(chunk));
        logo.setBorder(0);
        logo.setVerticalAlignment(4);
        logo.setHorizontalAlignment(0);
        table.addCell(logo);
        table = PDFUtil.addCustomerNameToPDFReport(table);
        document.add((Element)table);
        final PdfPTable topline_img = new PdfPTable(1);
        topline_img.setWidthPercentage(100.0f);
        final PdfPCell emptyRow = new PdfPCell(new Phrase(""));
        emptyRow.setBackgroundColor(PDFUtil.SDP_BLUE_STRIP);
        emptyRow.setBorder(0);
        emptyRow.setPaddingRight(1.0f);
        emptyRow.setPaddingLeft(1.0f);
        topline_img.addCell(emptyRow);
        document.add((Element)topline_img);
        final Phrase footerPhrase = new Phrase();
        footerPhrase.add((Object)new Chunk(I18N.getMsg("dc.rep.pdf.page", new Object[0]), PDFUtil.black_text_medium));
        final HeaderFooter footer = new HeaderFooter(footerPhrase, new Phrase(""));
        footer.setAlignment(1);
        document.setFooter(footer);
    }
    
    public float[] getSectionWidths(float[] columnWidths, final int tableSize) {
        try {
            if (columnWidths == null) {
                final float width = (float)(100 / tableSize);
                columnWidths = new float[tableSize];
                for (int cs = 0; cs < tableSize; ++cs) {
                    columnWidths[cs] = width;
                }
            }
        }
        catch (final Exception e) {
            QueryReportHandler.logger.log(Level.WARNING, "Exception while column width", e);
            throw e;
        }
        return columnWidths;
    }
    
    public void setTableHeader(final List columnValues, final PdfPTable pTable) {
        try {
            for (int w = 0; w < columnValues.size(); ++w) {
                String cellVal = "";
                if (columnValues.get(w) != null) {
                    cellVal = columnValues.get(w).toString();
                }
                final PdfPCell cell = new PdfPCell((Phrase)new Paragraph(cellVal, PDFUtil.black_text_medium));
                cell.setBackgroundColor(PDFUtil.ROW_HEADER_SDP_BLUE);
                cell.setBorderColor(PDFUtil.WHITE);
                pTable.addCell(cell);
            }
            pTable.setHeaderRows(1);
        }
        catch (final Exception e) {
            QueryReportHandler.logger.log(Level.WARNING, "Exception while setting table header", e);
            throw e;
        }
    }
    
    @Deprecated
    public static void fetchPDFData(final ArrayList valuesList, final PdfPTable pTable, final Document document) throws Exception {
        int countRow = 0;
        for (final Object row : valuesList) {
            final List columnValues = (List)row;
            for (int columnCount = columnValues.size(), column = 0; column < columnCount; ++column) {
                String cellVal = "";
                if (columnValues.get(column) != null) {
                    cellVal = columnValues.get(column).toString();
                }
                final PdfPCell cell = new PdfPCell((Phrase)new Paragraph(cellVal, PDFUtil.black_text_small));
                if (countRow % 2 != 0) {
                    cell.setBackgroundColor(PDFUtil.ODD_ROW_SDP_BLUE);
                }
                cell.setBorderColor(PDFUtil.WHITE);
                pTable.addCell(cell);
            }
            if ((countRow + 1) % 10 == 0) {
                document.add((Element)pTable);
            }
            ++countRow;
        }
    }
    
    public void writePDFTable(final ArrayList data, final ArrayList columnNames, final PdfPTable pdfPTable, final Document document) throws Exception {
        int rowCount = 0;
        for (final Object row : data) {
            final Map rowMap = (Map)row;
            for (final Object columnName : columnNames) {
                Object value = rowMap.get(columnName);
                value = ((value == null) ? "" : value);
                final PdfPCell pdfPCell = new PdfPCell((Phrase)new Paragraph(value.toString(), PDFUtil.black_text_small));
                if (rowCount % 2 != 0) {
                    pdfPCell.setBackgroundColor(PDFUtil.ODD_ROW_SDP_BLUE);
                }
                pdfPCell.setBorderColor(PDFUtil.WHITE);
                pdfPTable.addCell(pdfPCell);
            }
            if ((rowCount + 1) % 10 == 0) {
                document.add((Element)pdfPTable);
            }
            ++rowCount;
        }
    }
    
    public static boolean isUserAllowed(final HttpServletRequest request) {
        if (!request.isUserInRole("Common_Write") && !request.isUserInRole("Report_Admin") && !request.isUserInRole("PatchMgmt_Write") && !request.isUserInRole("Patch_Edition_Role")) {
            SyMLogger.warning(QueryReportHandler.logger, "QueryReportAction", "isQueryAllowed", "User Not Allowed");
            return false;
        }
        return true;
    }
    
    static {
        QueryReportHandler.logger = Logger.getLogger("QueryExecutorLogger");
    }
}
