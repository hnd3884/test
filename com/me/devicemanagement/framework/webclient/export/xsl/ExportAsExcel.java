package com.me.devicemanagement.framework.webclient.export.xsl;

import com.me.devicemanagement.framework.webclient.export.ExportAuditUtils;
import java.security.GeneralSecurityException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import java.io.IOException;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import java.sql.Connection;
import com.adventnet.persistence.PersistenceException;
import com.adventnet.client.view.common.ExportUtils;
import java.io.OutputStream;
import javax.servlet.http.HttpServletResponse;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.client.view.common.ExportAuditModel;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import com.adventnet.db.api.RelationalAPI;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import com.adventnet.i18n.I18N;
import com.adventnet.client.view.web.ViewContext;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import com.adventnet.client.components.table.web.TableIterator;
import com.adventnet.client.components.table.web.TableTransformerContext;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import com.me.devicemanagement.framework.utils.SanitizerUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import com.adventnet.client.components.table.web.TableViewModel;

public class ExportAsExcel extends com.adventnet.client.components.table.xls.ExportAsExcel
{
    private int startRow;
    
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
                String temp = sb.toString();
                temp = SanitizerUtil.getInstance().sanitizeValue(temp);
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
        final XSSFCellStyle cellStyle = this.getStyleForRows(wb);
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
                            if (((String)value).indexOf("\n") != -1) {
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
                    String temp = sb.toString();
                    temp = SanitizerUtil.getInstance().sanitizeValue(temp);
                    final SXSSFCell cell = row.createCell((int)(short)j);
                    ++j;
                    cell.setCellValue(temp);
                    cell.setCellStyle((CellStyle)cellStyle);
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
    
    public void generateXLS(final String origView, final HttpServletRequest request, final Object response) throws Exception {
        Connection connection = null;
        boolean isAutoCommitDisabled = false;
        try {
            connection = RelationalAPI.getInstance().getConnection();
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
                isAutoCommitDisabled = true;
            }
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
        }
        catch (final Exception ex) {
            throw new PersistenceException(ex.getMessage(), (Throwable)ex);
        }
        finally {
            try {
                if (connection != null) {
                    if (isAutoCommitDisabled) {
                        connection.setAutoCommit(true);
                    }
                    connection.close();
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
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
    
    public void auditExport(final ExportAuditModel auditModel) {
        ExportAuditUtils.auditExport(auditModel);
    }
}
