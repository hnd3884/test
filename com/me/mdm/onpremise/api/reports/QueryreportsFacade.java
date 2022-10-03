package com.me.mdm.onpremise.api.reports;

import com.me.devicemanagement.framework.webclient.reports.SYMReportUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.api.APIRequest;
import org.apache.commons.codec.binary.Base64;
import com.adventnet.persistence.Row;
import java.security.SecureRandom;
import com.me.mdm.server.reports.MDMReportUtil;
import com.me.mdm.api.APIUtil;
import javax.swing.table.TableModel;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import java.sql.SQLException;
import com.me.devicemanagement.framework.server.util.ProductClassLoader;
import com.me.devicemanagement.framework.webclient.admin.DBQueryExecutorAPI;
import com.adventnet.ds.query.Range;
import com.adventnet.i18n.I18N;
import java.util.ArrayList;
import org.json.JSONObject;
import com.me.mdm.server.query.MDMQueryReportHandler;
import java.util.logging.Logger;

public class QueryreportsFacade
{
    public Logger logger;
    MDMQueryReportHandler queryReportHandler;
    public static final String FILE_CONTENT = "file_content";
    public static final String FILE_NAME = "file_name";
    public static final String FILE_TYPE = "file_type";
    
    public QueryreportsFacade() {
        this.logger = Logger.getLogger("QueryReportFacade");
        this.queryReportHandler = new MDMQueryReportHandler();
    }
    
    public JSONObject executeQuery(final JSONObject jsonObject) {
        JSONObject resultJson = new JSONObject();
        String result = "failed";
        try {
            final JSONObject queryDetails = jsonObject.getJSONObject("msg_body");
            TableModel tm = null;
            ArrayList dataList = new ArrayList();
            final String operationType = (String)queryDetails.get("operation_type");
            final Boolean newReport = queryDetails.optBoolean("is_new_report");
            final String queryName = queryDetails.optString("query_name");
            final String query = queryDetails.optString("query");
            final int dataPerPage = queryDetails.optInt("data_per_page");
            int pageNumber = 1;
            pageNumber = queryDetails.optInt("page_number");
            final Boolean needPageCount = queryDetails.optBoolean("need_page_count");
            final Boolean replaceReport = queryDetails.optBoolean("replace_report");
            Boolean reportExist = false;
            final MDMQueryReportHandler queryReportHandler = this.queryReportHandler;
            if (MDMQueryReportHandler.isQueryNameAvailableInQueryReport(queryName) != null) {
                final MDMQueryReportHandler queryReportHandler2 = this.queryReportHandler;
                reportExist = MDMQueryReportHandler.isQueryNameAvailableInQueryReport(queryName);
            }
            if (operationType.equalsIgnoreCase("runandsave") && newReport && !reportExist && !replaceReport) {
                resultJson.put("result", (Object)result);
                resultJson.put("failure_message", (Object)I18N.getMsg("mdm.rep.customReport.report_name_exists", new Object[0]));
                return resultJson;
            }
            if (operationType.equalsIgnoreCase("run") || operationType.equalsIgnoreCase("runandsave")) {
                final MDMQueryReportHandler queryReportHandler3 = this.queryReportHandler;
                String modifiedQuery = MDMQueryReportHandler.modifyQuery(query);
                modifiedQuery = modifiedQuery.trim();
                final int startIndex = (pageNumber - 1) * dataPerPage;
                final int lastIndex = pageNumber * dataPerPage;
                final Range range = new Range(startIndex, lastIndex);
                final MDMQueryReportHandler queryReportHandler4 = this.queryReportHandler;
                modifiedQuery = MDMQueryReportHandler.setOrderByInQuery(modifiedQuery, range);
                tm = (TableModel)((DBQueryExecutorAPI)Class.forName(ProductClassLoader.getSingleImplProductClass("DM_DB_QUERY_EXECUTOR_IMPL_CLASS")).newInstance()).getTableModel(modifiedQuery, (String)null, false);
                dataList = this.queryReportHandler.getDataFromTableModel(tm, Boolean.valueOf(true), dataList);
                result = "success";
                final MDMQueryReportHandler queryReportHandler5 = this.queryReportHandler;
                resultJson = MDMQueryReportHandler.getDataJsonFromArrayList(dataList);
            }
            if (operationType.equalsIgnoreCase("runandsave") || operationType.equalsIgnoreCase("save")) {
                Boolean saveReport = false;
                if (newReport && !replaceReport) {
                    saveReport = this.queryReportHandler.saveQueryReport(queryDetails);
                }
                else {
                    saveReport = this.queryReportHandler.updateQueryReport(queryDetails);
                }
                if (saveReport) {
                    result = "success";
                }
            }
            if (needPageCount) {
                final MDMQueryReportHandler queryReportHandler6 = this.queryReportHandler;
                final String modifiedQuery = MDMQueryReportHandler.modifyQuery(query);
                tm = (TableModel)((DBQueryExecutorAPI)Class.forName(ProductClassLoader.getSingleImplProductClass("DM_DB_QUERY_EXECUTOR_IMPL_CLASS")).newInstance()).getTableModel(modifiedQuery, (String)null, false);
                int pageCount = tm.getRowCount() / dataPerPage;
                final int extraData = tm.getRowCount() % dataPerPage;
                if (extraData != 0) {
                    ++pageCount;
                }
                resultJson.put("page_count", pageCount);
            }
            resultJson.put("result", (Object)result);
            return resultJson;
        }
        catch (final Exception ex) {
            if (ex instanceof SQLException) {
                try {
                    resultJson.put("result", (Object)result);
                    resultJson.put("failure_message", (Object)ex.getMessage());
                    return resultJson;
                }
                catch (final Exception exception) {
                    throw new APIHTTPException("QRREP001", new Object[0]);
                }
            }
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "error in executing query report", ex);
            throw new APIHTTPException("QRREP001", new Object[0]);
        }
    }
    
    public JSONObject getQueryValue(final JSONObject requestJSON) {
        try {
            TableModel tm = null;
            ArrayList dataList = new ArrayList();
            TableModel pageCountTable = null;
            JSONObject resultJson = new JSONObject();
            String queryName = APIUtil.getResourceIDString(requestJSON, "query_repor_id");
            final Boolean isClone = APIUtil.getBooleanFilter(requestJSON, "is_clone", Boolean.valueOf(false));
            final Row crViewRow = MDMReportUtil.getCRDetailsFromViewName(queryName, APIUtil.getCustomerID(requestJSON));
            if (crViewRow != null) {
                final String query = (String)crViewRow.get("QR_QUERY");
                final Integer dataPerPage = (Integer)crViewRow.get("NUM_OF_RECORDS");
                final MDMQueryReportHandler queryReportHandler = this.queryReportHandler;
                String modifiedQuery = MDMQueryReportHandler.modifyQuery(query);
                modifiedQuery = modifiedQuery.trim();
                pageCountTable = (TableModel)((DBQueryExecutorAPI)Class.forName(ProductClassLoader.getSingleImplProductClass("DM_DB_QUERY_EXECUTOR_IMPL_CLASS")).newInstance()).getTableModel(modifiedQuery, (String)null, false);
                int pageCount = pageCountTable.getRowCount() / dataPerPage;
                final int extraData = pageCountTable.getRowCount() % dataPerPage;
                if (extraData != 0) {
                    ++pageCount;
                }
                final int pageNumber = 1;
                final int startIndex = (pageNumber - 1) * dataPerPage;
                final int lastIndex = pageNumber * dataPerPage;
                final Range range = new Range(startIndex, lastIndex);
                final MDMQueryReportHandler queryReportHandler2 = this.queryReportHandler;
                modifiedQuery = MDMQueryReportHandler.setOrderByInQuery(modifiedQuery, range);
                tm = (TableModel)((DBQueryExecutorAPI)Class.forName(ProductClassLoader.getSingleImplProductClass("DM_DB_QUERY_EXECUTOR_IMPL_CLASS")).newInstance()).getTableModel(modifiedQuery, (String)null, false);
                dataList = this.queryReportHandler.getDataFromTableModel(tm, Boolean.valueOf(true), dataList);
                final MDMQueryReportHandler queryReportHandler3 = this.queryReportHandler;
                resultJson = MDMQueryReportHandler.getDataJsonFromArrayList(dataList);
                if (isClone) {
                    final SecureRandom randomGenerator = new SecureRandom();
                    final int randomInt = randomGenerator.nextInt(100);
                    queryName = queryName + "_" + randomInt;
                }
                resultJson.put("query_name", (Object)queryName);
                resultJson.put("query", (Object)query);
                resultJson.put("dataPerPage", (Object)dataPerPage);
                resultJson.put("page_count", pageCount);
                return resultJson;
            }
            resultJson.put("failure_code", (Object)"QRREP002");
            return resultJson;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "error in getting query report details...", ex);
            throw new APIHTTPException("QRREP003", new Object[0]);
        }
    }
    
    public void deleteReport(final JSONObject jsonObject) {
        try {
            final String queryName = APIUtil.getResourceIDString(jsonObject, "query_repor_id");
            final Row crViewRow = MDMReportUtil.getCRDetailsFromViewName(queryName, APIUtil.getCustomerID(jsonObject));
            if (crViewRow != null) {
                final Long queryID = (Long)crViewRow.get("CRSAVEVIEW_ID");
                if (MDMReportUtil.customReportBelongsToUser(queryID)) {
                    MDMQueryReportHandler.deleteQueryReport(queryID);
                    MDMQueryReportHandler.setEmptyMessageInQueryReportView("QUERY_REPORT_NOT_CREATED");
                }
            }
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "error in deleting query report details...", ex);
            throw new APIHTTPException("QRREP004", new Object[0]);
        }
    }
    
    public JSONObject downloadReport(final JSONObject requestJson) throws APIHTTPException {
        try {
            final Long customerID = APIUtil.getCustomerID(requestJson);
            final String fileType = APIUtil.getStringFilter(requestJson, "file_type");
            final String queryReportName = APIUtil.getResourceIDString(requestJson, "query_repor_id");
            byte[] reportContentInBytes;
            if (fileType.equalsIgnoreCase("pdf")) {
                reportContentInBytes = this.queryReportHandler.downloadasPDF(queryReportName, customerID);
            }
            else {
                reportContentInBytes = this.queryReportHandler.downloadasCSV(queryReportName, customerID);
            }
            if (reportContentInBytes == null) {
                this.logger.log(Level.WARNING, "Report content should not be null");
                throw new APIHTTPException("QRREP005", new Object[0]);
            }
            final String encodedReportContent = new String(Base64.encodeBase64(reportContentInBytes));
            return new JSONObject().put("file_name", (Object)queryReportName).put("file_type", (Object)fileType).put("file_content", (Object)encodedReportContent);
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "error while downloading query report details...", ex);
            throw new APIHTTPException("QRREP005", new Object[0]);
        }
    }
    
    public JSONObject reportNameCheck(final APIRequest apiRequest) {
        try {
            final JSONObject jsonObject = apiRequest.toJSONObject();
            final JSONObject resultJson = new JSONObject();
            final Long loginID = APIUtil.getLoginID(jsonObject);
            final Long userID = APIUtil.getUserID(jsonObject);
            final Long customerID = APIUtil.getCustomerID(jsonObject);
            final boolean isAdmin = DMUserHandler.isUserInAdminRole(loginID);
            final String reportName = apiRequest.getParameterList().get("query_name");
            boolean status = false;
            status = SYMReportUtil.isCustomReportNameExists(reportName, userID, customerID, isAdmin, true);
            if (status) {
                this.logger.log(Level.INFO, "Report Name is Already Exist...");
                throw new APIHTTPException("QRREP006", new Object[0]);
            }
            resultJson.put("report_name_already_exists", status);
            return resultJson;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "error while getting query report details...", ex);
            throw new APIHTTPException("QRREP003", new Object[0]);
        }
    }
}
