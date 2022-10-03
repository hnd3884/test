package com.adventnet.client.components.table.json;

import java.util.HashMap;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.ArrayList;
import java.sql.Connection;
import com.adventnet.persistence.PersistenceException;
import com.adventnet.client.components.table.web.TableViewModel;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.persistence.DataAccess;
import java.nio.file.Path;
import com.zoho.framework.utils.FileUtils;
import java.util.logging.Level;
import java.io.OutputStream;
import com.adventnet.client.view.web.ViewContext;
import java.io.FileOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import com.adventnet.client.view.common.ExportUtils;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.components.table.web.TableTransformerContext;
import com.adventnet.client.components.table.web.TableIterator;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.TableConstants;

public class TableJSONRenderer implements TableConstants, JsonTheme
{
    private static Logger out;
    public TableIterator globalTableIterator;
    protected TableTransformerContext transContext;
    
    public TableJSONRenderer() {
        this.transContext = null;
    }
    
    @Override
    public void generateJSON(final String origView, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        if (!ExportUtils.isExportPasswordProtected()) {
            this.generateJSONInOS(origView, request, response);
        }
        else {
            final String fileName = (request.getParameter("fileName") != null) ? request.getParameter("fileName") : origView;
            final Path tempFileDir = Files.createTempDirectory(fileName, (FileAttribute<?>[])new FileAttribute[0]);
            final File exportDataFile = new File(tempFileDir.toAbsolutePath().toString(), fileName + ".json");
            try {
                try (final FileOutputStream fos = new FileOutputStream(exportDataFile)) {
                    this.generateJSONInOS(origView, request, fos);
                }
                ExportUtils.generateZipOnOS(ViewContext.getViewContext((Object)origView, (Object)origView, request), (OutputStream)response.getOutputStream(), exportDataFile, fileName);
            }
            finally {
                if (tempFileDir != null) {
                    TableJSONRenderer.out.log(Level.FINER, "going to delete temporary folder : {0} ", tempFileDir);
                    FileUtils.deleteDir(tempFileDir.toFile());
                }
            }
        }
    }
    
    private void generateJSONInOS(final String origView, final HttpServletRequest request, final Object response) throws Exception {
        final ViewContext vc = ViewContext.getViewContext((Object)origView, (Object)origView, request);
        Connection connection = null;
        try {
            DataAccess.getTransactionManager().begin();
            connection = RelationalAPI.getInstance().getConnection();
            vc.setTransientState("CONNECTION", (Object)connection);
            final TableViewModel viewModel = (TableViewModel)vc.getViewModel(true);
            OutputStream os = null;
            if (response instanceof HttpServletResponse) {
                os = (OutputStream)((HttpServletResponse)response).getOutputStream();
            }
            else {
                os = (OutputStream)response;
            }
            this.generateTable(viewModel, origView, vc, os);
            DataAccess.getTransactionManager().commit();
        }
        catch (final Exception ex) {
            try {
                if (DataAccess.getTransactionManager().getTransaction() != null) {
                    DataAccess.getTransactionManager().rollback();
                }
            }
            catch (final Exception e) {
                TableJSONRenderer.out.log(Level.INFO, "Error while rollback : ", e);
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
    
    public void generateJSONInOutputStream(final String origView, final HttpServletRequest request, final FileOutputStream fos) throws Exception {
        this.generateJSONInOS(origView, request, fos);
    }
    
    private ArrayList<String> generateHeaders(final TableViewModel viewModel) throws Exception {
        final ArrayList<String> list = new ArrayList<String>();
        final TableTransformerContext transContext = viewModel.getTableTransformerContext();
        final TableIterator iter = viewModel.getNewTableIterator();
        iter.reset();
        while (iter.nextColumn()) {
            iter.initTransCtxForCurrentCell("HEADER");
            if (transContext.isExportable()) {
                if (transContext.getRenderedAttributes().get("VALUE") != null && !transContext.getRenderedAttributes().get("VALUE").equals("&nbsp;")) {
                    final Object obj = transContext.getRenderedAttributes().get("VALUE");
                    if (((String)obj).indexOf(",") == -1) {
                        list.add((String)obj);
                    }
                    else {
                        list.add("\"" + (String)obj + "\"");
                    }
                }
                else {
                    list.add("");
                }
            }
        }
        return list;
    }
    
    private void generateTable(final TableViewModel viewModel, final String origView, final ViewContext vc, final OutputStream os) throws Exception {
        this.transContext = viewModel.getTableTransformerContext();
        this.globalTableIterator = viewModel.getNewTableIterator();
        final JSONArray jsonArray = new JSONArray();
        JSONObject json = null;
        StringBuilder sbRow = null;
        final ArrayList<String> arrayList = this.generateHeaders(viewModel);
        while (this.globalTableIterator.nextRow()) {
            json = new JSONObject();
            int k = 0;
            this.globalTableIterator.setCurrentColumn(-1);
            while (this.globalTableIterator.nextColumn()) {
                this.globalTableIterator.initTransCtxForCurrentCell("Cell");
                if (this.transContext.isExportable()) {
                    final HashMap<String, Object> props = this.transContext.getRenderedAttributes();
                    if (props.size() <= 0) {
                        continue;
                    }
                    sbRow = new StringBuilder();
                    if (props.get("PREFIX_TEXT") != null) {
                        sbRow.append(props.get("PREFIX_TEXT"));
                    }
                    Object value = props.get("ACTUAL_VALUE");
                    if (value == null || value.equals("")) {
                        value = props.get("VALUE");
                    }
                    value = ((value != null) ? value : "");
                    sbRow.append(value);
                    if (props.get("SUFFIX_TEXT") != null) {
                        sbRow.append(props.get("SUFFIX_TEXT"));
                    }
                    json.put((String)arrayList.get(k), (Object)sbRow.toString());
                    ++k;
                }
            }
            jsonArray.put((Object)json);
        }
        final JSONObject jsonData = new JSONObject();
        jsonData.put(origView, (Object)jsonArray);
        os.write(jsonData.toString().getBytes("UTF-8"));
    }
    
    static {
        TableJSONRenderer.out = Logger.getLogger(TableJSONRenderer.class.getName());
    }
}
