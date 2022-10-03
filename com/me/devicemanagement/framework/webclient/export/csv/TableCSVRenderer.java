package com.me.devicemanagement.framework.webclient.export.csv;

import java.util.HashMap;
import com.adventnet.i18n.I18N;
import java.io.IOException;
import com.adventnet.client.components.table.web.TableIterator;
import com.adventnet.client.components.table.web.TableTransformerContext;
import com.me.devicemanagement.framework.utils.SanitizerUtil;
import java.lang.reflect.Field;
import java.sql.Connection;
import com.adventnet.persistence.PersistenceException;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.client.components.table.web.TableViewModel;
import com.adventnet.db.api.RelationalAPI;
import java.nio.file.Path;
import com.zoho.framework.utils.FileUtils;
import com.adventnet.client.view.web.ViewContext;
import java.io.FileOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import com.me.devicemanagement.framework.webclient.common.FileUploadUtil;
import java.io.OutputStream;
import com.adventnet.client.view.common.ExportUtils;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.me.devicemanagement.framework.webclient.export.ExportAuditUtils;
import com.adventnet.client.view.common.ExportAuditModel;
import java.util.logging.Level;
import java.util.Locale;
import com.me.devicemanagement.framework.server.util.I18NUtil;
import java.util.logging.Logger;

public class TableCSVRenderer extends com.adventnet.client.components.table.csv.TableCSVRenderer
{
    private static Logger out;
    private String encodeFormat;
    
    public TableCSVRenderer() {
        this.encodeFormat = "UTF-8";
        this.setEncodeFormat();
    }
    
    private void setEncodeFormat() {
        try {
            final String language = I18NUtil.getUserLocaleFromSession().getLanguage();
            if (language.equalsIgnoreCase(Locale.CHINESE.getLanguage())) {
                this.encodeFormat = "GBK";
            }
        }
        catch (final Exception ex) {
            TableCSVRenderer.out.log(Level.SEVERE, "CSV Export Proceeding with default UTF-8 encode since failed to set Encode type : ", ex);
        }
    }
    
    public void auditExport(final ExportAuditModel exportAuditModel) {
        ExportAuditUtils.auditExport(exportAuditModel);
    }
    
    public void generateCSV(final String origView, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        if (!ExportUtils.isExportPasswordProtected()) {
            this.generateCSVInOS(origView, request, (OutputStream)response.getOutputStream(), System.getProperty("server.home"));
        }
        else {
            Path tempFileDir = null;
            final String fileName = (request.getParameter("fileName") != null) ? request.getParameter("fileName") : origView;
            if (FileUploadUtil.hasVulnerabilityInFileName(fileName)) {
                TableCSVRenderer.out.log(Level.SEVERE, "Contains vulnerability in fileName: {0} ", fileName);
                throw new Exception("contains vulnerability in file name");
            }
            tempFileDir = Files.createTempDirectory(fileName, (FileAttribute<?>[])new FileAttribute[0]);
            final File exportDataFile = new File(tempFileDir.toAbsolutePath().toString(), fileName + ".csv");
            try (final FileOutputStream fos = new FileOutputStream(exportDataFile)) {
                this.generateCSVInOS(origView, request, fos, exportDataFile.getCanonicalPath());
                ExportUtils.generateZipOnOS(ViewContext.getViewContext((Object)origView, (Object)origView, request), (OutputStream)response.getOutputStream(), exportDataFile, fileName);
            }
            finally {
                if (tempFileDir != null) {
                    TableCSVRenderer.out.log(Level.FINER, "going to delete the temporary folder : {0} ", tempFileDir);
                    FileUtils.deleteDir(tempFileDir.toFile());
                }
            }
        }
    }
    
    private void generateCSVInOS(final String origView, final HttpServletRequest request, final OutputStream os, final String filePath) throws Exception {
        final ViewContext vc = ViewContext.getViewContext((Object)origView, (Object)origView, request);
        Connection connection = null;
        boolean isAutoCommitDisabled = false;
        try {
            final long startTime = System.currentTimeMillis();
            connection = RelationalAPI.getInstance().getConnection();
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
                isAutoCommitDisabled = true;
            }
            vc.setTransientState("CONNECTION", (Object)connection);
            final TableViewModel viewModel = (TableViewModel)vc.getViewModel(true);
            this.generateHeaders(viewModel, os, filePath);
            this.generateRows(viewModel, vc, os, filePath);
            final ExportAuditModel exportInfo = new ExportAuditModel();
            exportInfo.setStartTime(startTime);
            exportInfo.setAccountID(WebClientUtil.getAccountId());
            exportInfo.setViewName((long)vc.getModel().getViewNameNo());
            exportInfo.setViewContext(vc);
            exportInfo.setExportedTime(System.currentTimeMillis());
            this.auditExport(exportInfo);
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
                TableCSVRenderer.out.log(Level.INFO, "Exception: ", e);
            }
        }
    }
    
    public void generateCSVInOutputStream(final String origView, final HttpServletRequest request, final FileOutputStream fos) throws Exception {
        final Field field = fos.getClass().getDeclaredField("path");
        field.setAccessible(true);
        final Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & 0xFFFFFFEF);
        final String filePath = String.valueOf(field.get(fos));
        this.generateCSVInOS(origView, request, fos, filePath);
    }
    
    private void generateHeaders(final TableViewModel viewModel, final OutputStream os, final String filePath) throws Exception {
        final TableTransformerContext transContext = viewModel.getTableTransformerContext();
        final TableIterator iter = viewModel.getNewTableIterator();
        iter.reset();
        final StringBuilder sb = new StringBuilder();
        while (iter.nextColumn()) {
            iter.initTransCtxForCurrentCell("HEADER");
            if (transContext.isExportable()) {
                if (transContext.getRenderedAttributes().get("VALUE") != null && !transContext.getRenderedAttributes().get("VALUE").equals("&nbsp;")) {
                    Object obj = transContext.getRenderedAttributes().get("VALUE");
                    obj = SanitizerUtil.getInstance().sanitizeValue(obj.toString());
                    if (((String)obj).indexOf(",") == -1) {
                        sb.append((String)obj);
                    }
                    else {
                        sb.append("\"" + (String)obj + "\"");
                    }
                }
                else {
                    sb.append("");
                }
                if (!iter.isLastColumn()) {
                    sb.append(",");
                }
                else {
                    sb.append("\n");
                }
            }
        }
        this.writeData(os, sb.toString(), filePath);
        os.flush();
    }
    
    private void writeData(final OutputStream os, final String sb, final String filePath) throws Exception {
        if (!new File(filePath).getCanonicalPath().startsWith(new File(System.getProperty("server.home")).getCanonicalPath())) {
            throw new IOException("Filepath is outside of the target dir: " + filePath);
        }
        os.write(sb.getBytes(this.encodeFormat));
    }
    
    private void generateRows(final TableViewModel viewModel, final ViewContext vc, final OutputStream os, final String filePath) throws Exception {
        boolean hasrows = false;
        this.transContext = viewModel.getTableTransformerContext();
        (this.globalTableIterator = viewModel.getNewTableIterator()).reset();
        while (this.globalTableIterator.nextRow()) {
            final StringBuilder sb = new StringBuilder();
            hasrows = true;
            this.globalTableIterator.setCurrentColumn(-1);
            while (this.globalTableIterator.nextColumn()) {
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
                            value = SanitizerUtil.getInstance().sanitizeValue(value.toString());
                            if (((String)value).indexOf("\"") != -1) {
                                value = ((String)value).replace("\"", "\"\"");
                            }
                            if (((String)value).indexOf(",") != -1 || ((String)value).indexOf("\n") != -1) {
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
                    if (!this.globalTableIterator.isLastColumn()) {
                        sb.append(",");
                    }
                    else {
                        sb.append("\n");
                    }
                }
            }
            this.writeData(os, sb.toString(), filePath);
        }
        if (!hasrows) {
            String noRowMsg = (String)viewModel.getTableViewConfigRow().get("EMPTY_TABLE_MESSAGE");
            if (noRowMsg == null) {
                noRowMsg = "No Rows found";
            }
            this.writeData(os, I18N.getMsg(noRowMsg, new Object[0]), filePath);
        }
    }
    
    static {
        TableCSVRenderer.out = Logger.getLogger(TableCSVRenderer.class.getName());
    }
}
