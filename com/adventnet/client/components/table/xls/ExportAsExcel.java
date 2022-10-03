package com.adventnet.client.components.table.xls;

import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFFont;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import java.security.GeneralSecurityException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import java.io.IOException;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import java.sql.Connection;
import com.adventnet.persistence.PersistenceException;
import java.util.logging.Level;
import com.adventnet.client.view.common.ExportUtils;
import java.io.OutputStream;
import javax.servlet.http.HttpServletResponse;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.client.view.common.ExportAuditModel;
import com.adventnet.i18n.I18N;
import com.adventnet.client.components.table.web.TableViewModel;
import com.adventnet.client.view.web.ViewContext;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.persistence.DataAccess;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.TableTransformerContext;
import com.adventnet.client.components.table.web.TableIterator;
import com.adventnet.client.view.xls.XLSTheme;

public class ExportAsExcel implements XLSTheme
{
    private int startRow;
    public TableIterator globalTableIterator;
    protected TableTransformerContext transContext;
    private static final String RENDER_EXCEPTION = "[RENDERING EXCEPTION]";
    private static Logger out;
    
    public ExportAsExcel() {
        this.transContext = null;
    }
    
    public void generateXLS(final String origView, final HttpServletRequest request, final Object response) throws Exception {
        Connection connection = null;
        boolean localTrans = false;
        try {
            if (DataAccess.getTransactionManager().getStatus() == 6) {
                DataAccess.getTransactionManager().begin();
                localTrans = true;
            }
            connection = RelationalAPI.getInstance().getConnection();
            final SXSSFWorkbook wb = new SXSSFWorkbook(100);
            String[] viewNames = null;
            if (origView.contains(",")) {
                viewNames = origView.split(",");
            }
            else {
                viewNames = new String[] { origView };
            }
            final POIFSFileSystem fs = new POIFSFileSystem();
            for (int size = viewNames.length, cnt = 0; cnt < size; ++cnt) {
                final long startTime = System.currentTimeMillis();
                final ViewContext vc = ViewContext.getViewContext((Object)viewNames[cnt], (Object)viewNames[cnt], request);
                vc.setTransientState("CONNECTION", (Object)connection);
                final TableViewModel viewmodel = (TableViewModel)vc.getViewModel(true);
                final String viewTitle = vc.getTitle().replaceAll("[\\\\*/,:?\\[\\]]", "");
                final SXSSFSheet sheet = wb.createSheet(I18N.getMsg(viewTitle, new Object[0]));
                sheet.setColumnWidth(0, 10000);
                sheet.setColumnWidth(1, 10000);
                sheet.setColumnWidth(2, 10000);
                sheet.setColumnWidth(3, 10000);
                sheet.setColumnWidth(4, 10000);
                this.generateHeaders(viewmodel, sheet, wb);
                this.generateRows(vc, viewmodel, sheet, wb);
                this.encryptData(fs, wb, ViewContext.getViewContext((Object)viewNames[0], (Object)viewNames[0], request));
                final ExportAuditModel exportInfo = new ExportAuditModel();
                exportInfo.setStartTime(startTime);
                exportInfo.setAccountID(WebClientUtil.getAccountId());
                exportInfo.setViewName((long)vc.getModel().getViewNameNo());
                exportInfo.setViewContext(vc);
                exportInfo.setExportedTime(System.currentTimeMillis());
                this.auditExport(exportInfo);
            }
            OutputStream os = null;
            if (response instanceof HttpServletResponse) {
                os = (OutputStream)((HttpServletResponse)response).getOutputStream();
            }
            else {
                os = (OutputStream)response;
            }
            if (ExportUtils.isExportPasswordProtected()) {
                fs.writeFilesystem(os);
            }
            else {
                wb.write(os);
                wb.dispose();
            }
            os.flush();
            os.close();
            if (localTrans) {
                DataAccess.getTransactionManager().commit();
            }
        }
        catch (final Exception ex) {
            try {
                if (localTrans && DataAccess.getTransactionManager().getTransaction() != null) {
                    DataAccess.getTransactionManager().rollback();
                }
            }
            catch (final Exception e) {
                ExportAsExcel.out.log(Level.INFO, "Error while rollback : ", e);
            }
            throw new PersistenceException(ex.getMessage(), (Throwable)ex);
        }
        finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (final Exception e2) {
                e2.printStackTrace();
            }
        }
    }
    
    private void encryptData(final POIFSFileSystem fs, final SXSSFWorkbook wb, final ViewContext viewCtx) throws IOException, InvalidFormatException, GeneralSecurityException {
        if (ExportUtils.isExportPasswordProtected()) {
            final EncryptionInfo info = new EncryptionInfo(EncryptionMode.agile);
            final Encryptor enc = info.getEncryptor();
            enc.confirmPassword(ExportUtils.getExportPassword(viewCtx));
            final OutputStream encryptOS = enc.getDataStream(fs);
            wb.write(encryptOS);
        }
    }
    
    protected void generateHeaders(final TableViewModel viewmodel, final SXSSFSheet sheet, final SXSSFWorkbook wb) throws Exception {
        this.startRow = 1;
        int k = 0;
        final XSSFCellStyle cellStyle = this.getStyleForHeaders(wb);
        final TableTransformerContext transContext1 = viewmodel.getTableTransformerContext();
        final TableIterator iter = viewmodel.getNewTableIterator();
        iter.reset();
        final SXSSFRow row = sheet.createRow((int)(short)(this.startRow - 1));
        while (iter.nextColumn()) {
            final StringBuilder sb = new StringBuilder();
            iter.initTransCtxForCurrentCell("HEADER");
            if (transContext1.isExportable()) {
                if (transContext1.getRenderedAttributes().get("VALUE") != null && !transContext1.getRenderedAttributes().get("VALUE").equals("&nbsp;")) {
                    final Object obj = transContext1.getRenderedAttributes().get("VALUE");
                    sb.append((String)obj);
                }
                else {
                    sb.append("");
                }
                final String temp = sb.toString();
                final SXSSFCell cell = row.createCell((int)(short)k);
                ++k;
                cell.setCellStyle((CellStyle)cellStyle);
                cell.setCellValue(temp);
            }
        }
    }
    
    protected void generateRows(final ViewContext vc, final TableViewModel viewmodel, final SXSSFSheet sheet, final SXSSFWorkbook wb) throws Exception {
        this.transContext = viewmodel.getTableTransformerContext();
        (this.globalTableIterator = viewmodel.getNewTableIterator()).reset();
        int j = 0;
        int x = -1;
        boolean hasRows = false;
        while (this.globalTableIterator.nextRow()) {
            j = 0;
            ++x;
            hasRows = true;
            final SXSSFRow row = sheet.createRow(this.startRow + x);
            this.globalTableIterator.setCurrentColumn(-1);
            while (this.globalTableIterator.nextColumn()) {
                final StringBuilder sb = new StringBuilder();
                this.globalTableIterator.initTransCtxForCurrentCell("Cell");
                if (this.transContext.isExportable()) {
                    final HashMap<String, Object> props = this.transContext.getRenderedAttributes();
                    if (props.size() > 0) {
                        if (props.get("PREFIX_TEXT") != null) {
                            sb.append(props.get("PREFIX_TEXT"));
                        }
                        Object value = props.get("ACTUAL_VALUE");
                        if (value == null || value.equals("")) {
                            value = props.get("VALUE");
                        }
                        if (value instanceof String) {
                            if (((String)value).contains("\n")) {
                                value = "\"" + (String)value + "\"";
                            }
                            sb.append(value);
                        }
                        else {
                            value = ((value != null) ? value : "");
                            sb.append(value);
                        }
                        if (props.get("SUFFIX_TEXT") != null) {
                            sb.append(props.get("SUFFIX_TEXT"));
                        }
                    }
                    final SXSSFCell cell = row.createCell((int)(short)j);
                    ++j;
                    this.setXLSXCellValue(sb.toString(), cell);
                }
            }
        }
        if (!hasRows) {
            String noRowMsg = (String)viewmodel.getTableViewConfigRow().get("EMPTY_TABLE_MESSAGE");
            if (noRowMsg == null) {
                noRowMsg = "No Rows found";
            }
            final SXSSFRow row2 = sheet.createRow((int)(short)this.startRow);
            final SXSSFCell cell = row2.createCell(0);
            cell.setCellValue(I18N.getMsg(noRowMsg, new Object[0]));
        }
    }
    
    private void setXLSXCellValue(final String data, final SXSSFCell cell) {
        if (data.isEmpty()) {
            ExportAsExcel.out.finest("Skipping the null values to be exported in XLSX cells");
            return;
        }
        final XSSFCellStyle cellStyle = this.getStyleForRows(cell.getSheet().getWorkbook());
        if (this.transContext.isPII()) {
            ExportAsExcel.out.finest("Exporting the values as String for the PII redacted column : " + this.transContext.getPropertyName());
            cell.setCellValue(data);
        }
        else {
            final String transformedDataType = this.transContext.getTransformedType();
            try {
                final String s = transformedDataType;
                switch (s) {
                    case "DOUBLE":
                    case "BIGINT":
                    case "DECIMAL":
                    case "TINYINT":
                    case "INTEGER":
                    case "FLOAT": {
                        cell.setCellValue(Double.parseDouble(data));
                        break;
                    }
                    case "DATE": {
                        this.handleForDateCell(data, cell, (CellStyle)cellStyle, "yyyy-MM-dd");
                        break;
                    }
                    case "TIME": {
                        this.handleForDateCell(data, cell, (CellStyle)cellStyle, "HH:mm:ss");
                        break;
                    }
                    case "DATETIME":
                    case "TIMESTAMP": {
                        this.handleForDateCell(data, cell, (CellStyle)cellStyle, "yyyy-MM-dd HH:mm:ss");
                        break;
                    }
                    case "BOOLEAN": {
                        cell.setCellValue(Boolean.parseBoolean(data));
                        break;
                    }
                    default: {
                        cell.setCellValue(data);
                        break;
                    }
                }
            }
            catch (final Exception e) {
                ExportAsExcel.out.severe("Problem while setting cell value '" + data + "' as " + transformedDataType + " for XLSX export column '" + this.transContext.getColumnConfigRow().get("COLUMNALIAS") + "'");
                e.printStackTrace();
                cell.setCellValue("[RENDERING EXCEPTION]");
            }
        }
        cell.setCellStyle((CellStyle)cellStyle);
    }
    
    private void handleForDateCell(final String data, final SXSSFCell cell, final CellStyle cellStyle, final String defaultFormat) throws ParseException {
        if (data.equals("null")) {
            ExportAsExcel.out.finest("Skipping the null values to be exported in XLSX cells");
            return;
        }
        String dateFormat = (String)this.transContext.getColumnConfigRow().get("DATE_FORMAT");
        if (dateFormat == null) {
            ExportAsExcel.out.warning("Date format is not configured for the XLSX export column '" + this.transContext.getColumnConfigRow().get("COLUMNALIAS") + "',thereby using default format '" + defaultFormat + "'");
            dateFormat = defaultFormat;
        }
        SimpleDateFormat sdf;
        try {
            sdf = new SimpleDateFormat(dateFormat);
        }
        catch (final IllegalArgumentException iae) {
            ExportAsExcel.out.severe("Issue with Date format '" + dateFormat + "' configured for XLSX export column " + this.transContext.getColumnConfigRow().get("COLUMNALIAS"));
            throw iae;
        }
        cell.setCellValue(sdf.parse(data));
        cellStyle.setDataFormat(cell.getSheet().getWorkbook().getCreationHelper().createDataFormat().getFormat(dateFormat));
    }
    
    protected XSSFCellStyle getStyleForHeaders(final SXSSFWorkbook wb) {
        final XSSFCellStyle cellStyle = (XSSFCellStyle)wb.createCellStyle();
        final XSSFFont font = (XSSFFont)wb.createFont();
        font.setBold(true);
        font.setFontName("Arial");
        cellStyle.setFont((Font)font);
        return cellStyle;
    }
    
    protected XSSFCellStyle getStyleForRows(final SXSSFWorkbook wb) {
        final XSSFCellStyle cellStyle = (XSSFCellStyle)wb.createCellStyle();
        final XSSFFont font = (XSSFFont)wb.createFont();
        font.setFontName("Arial");
        cellStyle.setFont((Font)font);
        return cellStyle;
    }
    
    public void auditExport(final ExportAuditModel auditModel) {
        ExportAsExcel.out.log(Level.FINEST, "Post Export Handling can be done here. Do nothing");
    }
    
    static {
        ExportAsExcel.out = Logger.getLogger(ExportAsExcel.class.getName());
    }
}
