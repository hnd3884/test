package com.me.mdm.server.query;

import org.json.JSONArray;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.ProductClassLoader;
import com.me.devicemanagement.framework.webclient.admin.DBQueryExecutorAPI;
import java.io.ByteArrayOutputStream;
import com.me.mdm.server.reports.MDMReportUtil;
import com.me.devicemanagement.framework.server.util.I18NUtil;
import com.me.devicemanagement.framework.server.util.Utils;
import javax.swing.table.TableModel;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.adventnet.persistence.DataAccess;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.JSONObject;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import java.awt.Color;
import com.lowagie.text.Paragraph;
import com.lowagie.text.HeaderFooter;
import com.me.mdm.webclient.reports.PDFUtil;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.Phrase;
import com.lowagie.text.Chunk;
import com.lowagie.text.Image;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.io.IOException;
import java.util.Iterator;
import java.io.OutputStream;
import com.lowagie.text.Element;
import java.util.List;
import com.lowagie.text.pdf.PdfPTable;
import com.me.mdm.webclient.reports.MDMPdfDocument;
import java.util.Hashtable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import java.util.ArrayList;
import com.adventnet.i18n.I18N;
import com.adventnet.ds.query.Range;
import com.me.devicemanagement.framework.webclient.factory.WebclientAPIFactoryProvider;
import com.me.devicemanagement.framework.webclient.reports.query.QueryReportAttrBean;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.webclient.reports.query.QueryReportHandler;

public class MDMQueryReportHandler extends QueryReportHandler
{
    private static Logger logger;
    
    public void mdmgetReportAsPDF(final String query, final HttpServletResponse response, final HttpServletRequest req) throws IOException {
        MDMQueryReportHandler.logger.log(Level.INFO, "Going to Export Report as PDF .... ");
        final QueryReportAttrBean queryRepBean = (QueryReportAttrBean)WebclientAPIFactoryProvider.getSessionAPI().getSessionAttribute(req, "QRBean");
        String repName = queryRepBean.getQueryNameVal();
        if (repName == null || "".equals(repName)) {
            repName = "CustomQueryReport";
        }
        OutputStream os = null;
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
        MDMPdfDocument mdmPDFDoc = null;
        PdfWriter pdfwriter = null;
        PdfPTable pTable = null;
        try {
            int recordindex = 0;
            while (true) {
                final Range range = new Range(recordindex, rowConstant);
                tempquery = QueryReportHandler.setOrderByInQuery(modifiedQuery, range, queryType);
                if (recordindex == 0) {
                    rowcount = getTableModelData(tempquery, true, queryRepBean);
                    final String desc = I18N.getMsg("mdm.rep.pdf.QUERY_REP_DESC", new Object[0]) + " - " + repName;
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
                    mdmPDFDoc = new MDMPdfDocument(document);
                    mdmPDFDoc.setTitleAndDescription(queryRepDetail_hash);
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
            MDMQueryReportHandler.logger.log(Level.INFO, "Export PDF is successfully completed");
        }
        catch (final Exception ex) {
            queryRepBean.setSqlError(ex.getMessage());
            MDMQueryReportHandler.logger.log(Level.WARNING, "Error while generating PDF...", ex);
            try {
                os.flush();
                if (mdmPDFDoc != null) {
                    mdmPDFDoc.close();
                }
                if (os != null) {
                    os.close();
                }
            }
            catch (final Exception ex) {
                MDMQueryReportHandler.logger.log(Level.WARNING, "Error while closing outputstream...", ex);
            }
        }
        finally {
            try {
                os.flush();
                if (mdmPDFDoc != null) {
                    mdmPDFDoc.close();
                }
                if (os != null) {
                    os.close();
                }
            }
            catch (final Exception ex2) {
                MDMQueryReportHandler.logger.log(Level.WARNING, "Error while closing outputstream...", ex2);
            }
        }
    }
    
    public void setLogo(final Document doc) throws Exception {
        try {
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100.0f);
            table.getDefaultCell().setPadding(0.0f);
            final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
            final String logoPath = CustomerInfoUtil.getInstance().getLogoPath(customerId);
            final Image i = Image.getInstance(logoPath);
            i.scaleToFit(272.0f, 40.0f);
            final Chunk chunk = new Chunk(i, 0.0f, 0.0f);
            final PdfPCell logo = new PdfPCell(new Phrase(chunk));
            logo.setBorder(0);
            logo.setVerticalAlignment(4);
            logo.setHorizontalAlignment(0);
            table.addCell(logo);
            table = PDFUtil.addCustomerNameToPDFReport(table);
            doc.add((Element)table);
            final PdfPTable topline_img = new PdfPTable(1);
            topline_img.setWidthPercentage(100.0f);
            final PdfPCell emptyRow = new PdfPCell(new Phrase(""));
            emptyRow.setBackgroundColor(PDFUtil.SDP_BLUE_STRIP);
            emptyRow.setBorder(0);
            emptyRow.setPaddingRight(1.0f);
            emptyRow.setPaddingLeft(1.0f);
            topline_img.addCell(emptyRow);
            doc.add((Element)topline_img);
            final Phrase footerPhrase = new Phrase();
            footerPhrase.add((Object)new Chunk(I18N.getMsg("mdm.rep.pdf.page", new Object[0]), MDMPdfDocument.black_text_medium));
            final HeaderFooter footer = new HeaderFooter(footerPhrase, new Phrase(""));
            footer.setAlignment(1);
            doc.setFooter(footer);
        }
        catch (final Exception ex) {
            MDMQueryReportHandler.logger.log(Level.WARNING, "Exception while setting logo");
        }
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
            MDMQueryReportHandler.logger.log(Level.WARNING, "Exception while column width", e);
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
                final PdfPCell cell = new PdfPCell((Phrase)new Paragraph(cellVal, MDMPdfDocument.black_text_medium));
                cell.setBackgroundColor(PDFUtil.ROW_HEADER_SDP_BLUE);
                cell.setBorderColor(Color.WHITE);
                pTable.addCell(cell);
            }
            pTable.setHeaderRows(1);
        }
        catch (final Exception e) {
            MDMQueryReportHandler.logger.log(Level.WARNING, "Exception while setting table header", e);
            throw e;
        }
    }
    
    public static void fetchPDFData(final ArrayList valuesList, final PdfPTable pTable, final Document document) throws Exception {
        int countRow = 0;
        final Font black_text_small = FontFactory.getFont("Lato", 8.0f, 0, Color.BLACK);
        for (final Object row : valuesList) {
            final List columnValues = (List)row;
            for (int columnCount = columnValues.size(), column = 0; column < columnCount; ++column) {
                String cellVal = "";
                if (columnValues.get(column) != null) {
                    cellVal = columnValues.get(column).toString();
                }
                final PdfPCell cell = new PdfPCell((Phrase)new Paragraph(cellVal, black_text_small));
                if (countRow % 2 != 0) {
                    cell.setBackgroundColor(PDFUtil.ODD_ROW_SDP_BLUE);
                }
                cell.setBorderColor(Color.WHITE);
                pTable.addCell(cell);
            }
            if ((countRow + 1) % 10 == 0) {
                document.add((Element)pTable);
            }
            ++countRow;
        }
    }
    
    public boolean saveQueryReport(final JSONObject queryDetails) {
        Boolean result = false;
        try {
            final String query = (String)queryDetails.get("query");
            final String queryName = queryDetails.optString("query_name");
            final int dataperPage = queryDetails.optInt("data_per_page");
            final long loginuserID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            final Long subModuleId = (Long)DBUtil.getRowFromDB("CRSubModule", "SUB_MODULE_NAME", (Object)"ScheduleReportCriteria").get("SUB_MODULE_ID");
            final String dbName = DBUtil.getActiveDBName();
            final Long crSaveViewId = this.saveCRViewDetailswithoutQueryBean(queryName, query, loginuserID, dataperPage, dbName, subModuleId);
            Long customerId = 0L;
            customerId = CustomerInfoUtil.getInstance().getCustomerId();
            this.saveCrToCustomerRelRowDetails(customerId, crSaveViewId);
            final String action_url = "queryReport.do?actionToCall=deleteQueryReport&viewID=";
            this.saveCRViewParamsDetails(action_url, Boolean.valueOf(true), crSaveViewId);
            result = true;
        }
        catch (final Exception ex) {
            MDMQueryReportHandler.logger.log(Level.SEVERE, "error in saving  query reports...", ex);
        }
        return result;
    }
    
    public boolean updateQueryReport(final JSONObject queryDetails) {
        Boolean result = false;
        try {
            final String query = (String)queryDetails.get("query");
            final String queryName = queryDetails.optString("query_name");
            final int dataperPage = queryDetails.optInt("data_per_page");
            final SelectQuery editQuery = getBaseQueryForQueryReport(queryName);
            final DataObject dataObject = DataAccess.get(editQuery);
            if (dataObject.isEmpty()) {
                try {
                    this.saveQueryReport(queryDetails);
                }
                catch (final Exception e) {
                    MDMQueryReportHandler.logger.log(Level.INFO, "Error while tring to save reports", e);
                }
            }
            else {
                this.updateQueryReportwithoutQueryBean(query, queryName, dataperPage);
                SyMLogger.info(MDMQueryReportHandler.logger, "QueryReportHandler", "saveOrUpdateQueryReport", "Query Report has been updated successfully ...");
            }
            result = true;
        }
        catch (final Exception ex) {
            MDMQueryReportHandler.logger.log(Level.SEVERE, "error in updating  query reports...", ex);
        }
        return result;
    }
    
    public ArrayList getDataFromTableModel(final TableModel tm, final Boolean setColumnNameFlag, final ArrayList dataList) {
        try {
            int rowCount = 0;
            rowCount = tm.getRowCount();
            final int colCount = tm.getColumnCount();
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
                dataList.add(colNameList);
            }
            if (tm != null && rowCount > 0) {
                for (int j = 0; j < rowCount; ++j) {
                    final JSONObject rowValues = new JSONObject();
                    final ArrayList colList = new ArrayList();
                    int dataCount = 0;
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
                        else if (tm.getColumnName(k).toUpperCase().endsWith("_I18N_REMARK_" + dataCount)) {
                            String remarks = (String)tm.getValueAt(j, k);
                            String remarks_args = null;
                            for (int l = 0; l < colCount; ++l) {
                                if (tm.getColumnName(l).toUpperCase().endsWith("_I18N_REMARK_ARGS_" + dataCount)) {
                                    remarks_args = (String)tm.getValueAt(j, l);
                                    break;
                                }
                            }
                            remarks = I18NUtil.transformRemarks(remarks, remarks_args);
                            colList.add(remarks);
                            ++dataCount;
                        }
                        else if (!tm.getColumnName(k).toUpperCase().contains("_I18N_REMARK_ARGS_")) {
                            if (!tm.getColumnName(k).toUpperCase().contains("ROW_NUM")) {
                                colList.add(tm.getValueAt(j, k));
                            }
                        }
                    }
                    dataList.add(colList);
                }
            }
        }
        catch (final Exception ex) {
            MDMQueryReportHandler.logger.log(Level.SEVERE, "error in getting Data from Table model..", ex);
        }
        return dataList;
    }
    
    public byte[] downloadasPDF(final String queryName, final Long customerId) throws Exception {
        byte[] pdfInBytes = null;
        ArrayList dataList = new ArrayList();
        final Row crViewRow = MDMReportUtil.getCRDetailsFromViewName(queryName, customerId);
        if (crViewRow != null) {
            final String query = (String)crViewRow.get("QR_QUERY");
            final String modifiedQuery = modifyQuery(query);
            String queryType = getQueryType(modifiedQuery).toLowerCase();
            final ByteArrayOutputStream buffOut = new ByteArrayOutputStream();
            queryType = queryType.trim();
            TableModel tm = null;
            int rowcount;
            final int rowConstant = rowcount = 7500;
            String tempquery = null;
            Document document = null;
            MDMPdfDocument mdmPDFDoc = null;
            PdfWriter pdfwriter = null;
            PdfPTable pTable = null;
            try {
                int recordindex = 0;
                while (true) {
                    final Range range = new Range(recordindex, rowConstant);
                    tempquery = setOrderByInQuery(modifiedQuery, range, queryType);
                    if (recordindex == 0) {
                        final String desc = I18N.getMsg("mdm.rep.pdf.QUERY_REP_DESC", new Object[0]) + " - " + queryName;
                        tm = (TableModel)((DBQueryExecutorAPI)Class.forName(ProductClassLoader.getSingleImplProductClass("DM_DB_QUERY_EXECUTOR_IMPL_CLASS")).newInstance()).getTableModel(tempquery, (String)null, false);
                        dataList = this.getDataFromTableModel(tm, true, dataList);
                        rowcount = tm.getRowCount();
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
                        pdfwriter = PdfWriter.getInstance(document, (OutputStream)buffOut);
                        document.open();
                        final Hashtable queryRepDetail_hash = new Hashtable();
                        queryRepDetail_hash.put("TITLE", "Query Report");
                        queryRepDetail_hash.put("NAME", "Query Report PDF");
                        queryRepDetail_hash.put("DESCRIPTION", desc);
                        this.setLogo(document);
                        mdmPDFDoc = new MDMPdfDocument(document);
                        mdmPDFDoc.setTitleAndDescription(queryRepDetail_hash);
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
                        tm = (TableModel)((DBQueryExecutorAPI)Class.forName(ProductClassLoader.getSingleImplProductClass("DM_DB_QUERY_EXECUTOR_IMPL_CLASS")).newInstance()).getTableModel(modifiedQuery, (String)null, false);
                        dataList = this.getDataFromTableModel(tm, true, dataList);
                        rowcount = tm.getRowCount();
                        fetchPDFData(dataList, pTable, document);
                    }
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
                MDMQueryReportHandler.logger.log(Level.INFO, "Export PDF is successfully completed");
            }
            catch (final Exception ex) {
                MDMQueryReportHandler.logger.log(Level.SEVERE, "error in exporting query report details...", ex);
                try {
                    buffOut.flush();
                    if (mdmPDFDoc != null) {
                        mdmPDFDoc.close();
                    }
                    if (buffOut != null) {
                        pdfInBytes = buffOut.toByteArray();
                        buffOut.close();
                    }
                }
                catch (final Exception ex) {
                    MDMQueryReportHandler.logger.log(Level.WARNING, "Error while closing outputstream...", ex);
                }
            }
            finally {
                try {
                    buffOut.flush();
                    if (mdmPDFDoc != null) {
                        mdmPDFDoc.close();
                    }
                    if (buffOut != null) {
                        pdfInBytes = buffOut.toByteArray();
                        buffOut.close();
                    }
                }
                catch (final Exception ex2) {
                    MDMQueryReportHandler.logger.log(Level.WARNING, "Error while closing outputstream...", ex2);
                }
            }
        }
        else {
            MDMQueryReportHandler.logger.log(Level.WARNING, "Report ''{0}'' does not exist for given customer ''{1}''", new String[] { queryName, customerId.toString() });
        }
        return pdfInBytes;
    }
    
    public byte[] downloadasCSV(final String queryName, final Long customerId) throws Exception {
        ArrayList dataList = new ArrayList();
        TableModel tm = null;
        final Row crViewRow = MDMReportUtil.getCRDetailsFromViewName(queryName, customerId);
        if (crViewRow != null) {
            final String query = (String)crViewRow.get("QR_QUERY");
            final String modifiedQuery = modifyQuery(query);
            final ByteArrayOutputStream buffOut = new ByteArrayOutputStream();
            int rowCount = 10000;
            String tempQuery = null;
            try {
                int recordIndex = 0;
                String csvString;
                while (true) {
                    final Range range = new Range(recordIndex, 10000);
                    tempQuery = setOrderByInQuery(modifiedQuery, range);
                    if (recordIndex == 0) {
                        tm = (TableModel)((DBQueryExecutorAPI)Class.forName(ProductClassLoader.getSingleImplProductClass("DM_DB_QUERY_EXECUTOR_IMPL_CLASS")).newInstance()).getTableModel(tempQuery, (String)null, false);
                        rowCount = tm.getRowCount();
                        dataList = this.getDataFromTableModel(tm, true, dataList);
                        SyMLogger.info(MDMQueryReportHandler.logger, "QueryReportHandler", "getReportAsCSV", "RowCount 12 " + rowCount);
                    }
                    else {
                        tm = (TableModel)((DBQueryExecutorAPI)Class.forName(ProductClassLoader.getSingleImplProductClass("DM_DB_QUERY_EXECUTOR_IMPL_CLASS")).newInstance()).getTableModel(tempQuery, (String)null, false);
                        rowCount = tm.getRowCount();
                        dataList = this.getDataFromTableModel(tm, false, dataList);
                    }
                    SyMLogger.info(MDMQueryReportHandler.logger, "QueryReportHandler", "getReportAsCSV", "RowCount 2 " + rowCount);
                    csvString = getCSVString(dataList);
                    if (rowCount < 10000) {
                        break;
                    }
                    buffOut.write(csvString.getBytes());
                    buffOut.flush();
                    if (recordIndex == 0) {
                        ++recordIndex;
                    }
                    recordIndex += 10000;
                }
                buffOut.write(csvString.getBytes());
                buffOut.flush();
                MDMQueryReportHandler.logger.log(Level.INFO, "Export CSV is successfully completed");
                return buffOut.toByteArray();
            }
            catch (final Exception ex) {
                MDMQueryReportHandler.logger.log(Level.SEVERE, "error in exporting  query report details as CSV...", ex);
                try {
                    if (buffOut != null) {
                        buffOut.close();
                    }
                }
                catch (final Exception ex) {
                    MDMQueryReportHandler.logger.log(Level.SEVERE, "error in exporting  query report details as CSV...", ex);
                }
            }
            finally {
                try {
                    if (buffOut != null) {
                        buffOut.close();
                    }
                }
                catch (final Exception ex2) {
                    MDMQueryReportHandler.logger.log(Level.SEVERE, "error in exporting  query report details as CSV...", ex2);
                }
            }
        }
        else {
            MDMQueryReportHandler.logger.log(Level.WARNING, "Report ''{0}'' does not exist for given customer ''{1}''", new String[] { queryName, customerId.toString() });
        }
        return null;
    }
    
    public static String getCSVString(final ArrayList dataList) {
        StringBuffer csvReport = null;
        String colValue = "";
        int dataListSize = 0;
        csvReport = new StringBuffer();
        dataListSize = dataList.size();
        final Logger logger = MDMQueryReportHandler.logger;
        final Level all = Level.ALL;
        logger.log(Level.INFO, "dataListSize === {0}", dataListSize);
        for (int i = 0; i < dataListSize; ++i) {
            final Iterator itr = dataList.get(i).iterator();
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
    
    public static JSONObject getDataJsonFromArrayList(final ArrayList dataList) {
        final JSONObject resultJson = new JSONObject();
        try {
            final List columnValues = dataList.get(0);
            final JSONArray columnArray = new JSONArray();
            for (int i = 0; i < columnValues.size(); ++i) {
                columnArray.put(columnValues.get(i));
            }
            resultJson.put("column_values", (Object)columnArray);
            final int rowCount = dataList.size();
            final int colCount = columnValues.size();
            final JSONArray dataArray = new JSONArray();
            for (int j = 1; j < rowCount; ++j) {
                final List colList = dataList.get(j);
                final JSONObject colJson = new JSONObject();
                for (int k = 0; k < colCount; ++k) {
                    colJson.put((String)columnArray.get(k), colList.get(k));
                }
                dataArray.put((Object)colJson);
            }
            resultJson.put("result_data", (Object)dataArray);
        }
        catch (final Exception ex) {
            MDMQueryReportHandler.logger.log(Level.SEVERE, "error in getting json from Arraylist...", ex);
        }
        return resultJson;
    }
    
    static {
        MDMQueryReportHandler.logger = Logger.getLogger("QueryExecutorLogger");
    }
}
