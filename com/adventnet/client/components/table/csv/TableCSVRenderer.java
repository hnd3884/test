package com.adventnet.client.components.table.csv;

import java.util.HashMap;
import com.adventnet.i18n.I18N;
import java.io.IOException;
import java.sql.Connection;
import com.adventnet.persistence.PersistenceException;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.client.view.common.ExportAuditModel;
import com.adventnet.client.components.table.web.TableViewModel;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.persistence.DataAccess;
import java.nio.file.Path;
import com.zoho.framework.utils.FileUtils;
import java.util.logging.Level;
import com.adventnet.client.view.web.ViewContext;
import java.io.FileOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.io.OutputStream;
import com.adventnet.client.view.common.ExportUtils;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.components.table.web.TableTransformerContext;
import com.adventnet.client.components.table.web.TableIterator;
import java.util.logging.Logger;
import com.adventnet.client.view.csv.CSVTheme;
import com.adventnet.client.components.table.web.TableConstants;

public class TableCSVRenderer implements TableConstants, CSVTheme
{
    private static Logger out;
    public TableIterator globalTableIterator;
    protected TableTransformerContext transContext;
    
    public TableCSVRenderer() {
        this.transContext = null;
    }
    
    public void generateCSV(final String origView, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        if (!ExportUtils.isExportPasswordProtected()) {
            this.generateCSVInOS(origView, request, (OutputStream)response.getOutputStream());
        }
        else {
            final String fileName = (request.getParameter("fileName") != null) ? request.getParameter("fileName") : origView;
            final Path tempFileDir = Files.createTempDirectory(fileName, (FileAttribute<?>[])new FileAttribute[0]);
            final File exportDataFile = new File(tempFileDir.toAbsolutePath().toString(), fileName + ".csv");
            try {
                try (final FileOutputStream fos = new FileOutputStream(exportDataFile)) {
                    this.generateCSVInOS(origView, request, fos);
                }
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
    
    private void generateCSVInOS(final String origView, final HttpServletRequest request, final OutputStream os) throws Exception {
        final ViewContext vc = ViewContext.getViewContext((Object)origView, (Object)origView, request);
        Connection connection = null;
        boolean localTrans = false;
        try {
            if (DataAccess.getTransactionManager().getStatus() == 6) {
                DataAccess.getTransactionManager().begin();
                localTrans = true;
            }
            final long startTime = System.currentTimeMillis();
            connection = RelationalAPI.getInstance().getConnection();
            vc.setTransientState("CONNECTION", (Object)connection);
            final TableViewModel viewModel = (TableViewModel)vc.getViewModel(true);
            this.generateHeaders(viewModel, os);
            this.generateRows(viewModel, vc, os);
            if (localTrans) {
                DataAccess.getTransactionManager().commit();
            }
            final ExportAuditModel exportInfo = new ExportAuditModel();
            exportInfo.setStartTime(startTime);
            exportInfo.setAccountID(WebClientUtil.getAccountId());
            exportInfo.setViewName((long)vc.getModel().getViewNameNo());
            exportInfo.setViewContext(vc);
            exportInfo.setExportedTime(System.currentTimeMillis());
            this.auditExport(exportInfo);
        }
        catch (final Exception ex) {
            try {
                if (localTrans && DataAccess.getTransactionManager().getTransaction() != null) {
                    DataAccess.getTransactionManager().rollback();
                }
            }
            catch (final Exception e) {
                TableCSVRenderer.out.log(Level.INFO, "Error while rollback : ", e);
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
    
    public void generateCSVInOutputStream(final String origView, final HttpServletRequest request, final FileOutputStream fos) throws Exception {
        this.generateCSVInOS(origView, request, fos);
    }
    
    private void generateHeaders(final TableViewModel viewModel, final OutputStream os) throws Exception {
        final TableTransformerContext transContext = viewModel.getTableTransformerContext();
        final TableIterator iter = viewModel.getNewTableIterator();
        iter.reset();
        final StringBuilder sb = new StringBuilder();
        while (iter.nextColumn()) {
            iter.initTransCtxForCurrentCell("HEADER");
            if (transContext.isExportable()) {
                if (transContext.getRenderedAttributes().get("VALUE") != null && !transContext.getRenderedAttributes().get("VALUE").equals("&nbsp;")) {
                    final Object obj = transContext.getRenderedAttributes().get("VALUE");
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
        this.writeData(os, sb.toString());
        os.flush();
    }
    
    private void writeData(final OutputStream os, final String sb) throws IOException {
        os.write(sb.getBytes("UTF-8"));
    }
    
    private void generateRows(final TableViewModel viewModel, final ViewContext vc, final OutputStream os) throws Exception {
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
            this.writeData(os, sb.toString());
        }
        if (!hasrows) {
            String noRowMsg = (String)viewModel.getTableViewConfigRow().get("EMPTY_TABLE_MESSAGE");
            if (noRowMsg == null) {
                noRowMsg = "No Rows found";
            }
            this.writeData(os, I18N.getMsg(noRowMsg, new Object[0]));
        }
    }
    
    public void auditExport(final ExportAuditModel auditModel) {
        TableCSVRenderer.out.log(Level.FINEST, "Post Export Handling can be done here. Do nothing");
    }
    
    static {
        TableCSVRenderer.out = Logger.getLogger(TableCSVRenderer.class.getName());
    }
}
