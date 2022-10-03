package com.me.mdm.api;

import org.json.JSONArray;
import com.adventnet.client.components.table.web.TableIterator;
import com.adventnet.client.components.table.web.TableTransformerContext;
import java.util.ArrayList;
import java.sql.Connection;
import com.adventnet.persistence.PersistenceException;
import com.adventnet.client.components.table.web.TableViewModel;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.persistence.DataAccess;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.client.view.web.ViewContext;

public class MickeyViewStratergy extends APIEndpointStratergy
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final RequestMapper.Entity.Request request = apiRequest.request;
            apiRequest.setContentType("application/json");
            apiRequest.setCharacterEncoding("UTF-8");
            final RequestMapper.Entity.Request.ViewConfiguration viewConfiguration = request.getViewConfiguration();
            final ViewContext vc = ViewContext.getViewContext((Object)viewConfiguration.getViewName(), apiRequest.httpServletRequest);
            vc.getModel().getController().processPreRendering(vc, apiRequest.httpServletRequest, apiRequest.httpServletResponse, (String)null);
            vc.setRenderType(7);
            JSONObject json = this.generateJSONInOS(viewConfiguration.getViewName(), apiRequest.httpServletRequest);
            vc.getModel().getController().processPostRendering(vc, apiRequest.httpServletRequest, apiRequest.httpServletResponse);
            if (json == null) {
                final String key = String.valueOf(apiRequest.toJSONObject().getJSONObject("msg_header").getJSONObject("filters").get("entity"));
                json = new APIUtil().getResponseForEmptyList(key);
            }
            return json;
        }
        catch (final APIHTTPException ex) {
            Logger.getLogger(ApiRequestHandler.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        catch (final Exception ex2) {
            Logger.getLogger(ApiRequestHandler.class.getName()).log(Level.SEVERE, null, ex2);
            throw new APIHTTPException(500, null, new Object[0]);
        }
    }
    
    private JSONObject generateJSONInOS(final String origView, final HttpServletRequest request) throws Exception {
        final ViewContext vc = ViewContext.getViewContext((Object)origView, (Object)origView, request);
        Connection connection = null;
        JSONObject json = null;
        try {
            DataAccess.getTransactionManager().begin();
            connection = RelationalAPI.getInstance().getConnection();
            vc.setTransientState("CONNECTION", (Object)connection);
            final TableViewModel viewModel = (TableViewModel)vc.getViewModel(true);
            json = this.generateTable(viewModel, request);
            DataAccess.getTransactionManager().commit();
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            try {
                if (DataAccess.getTransactionManager().getTransaction() != null) {
                    DataAccess.getTransactionManager().rollback();
                }
            }
            catch (final Exception e) {
                Logger.getLogger(ApiRequestHandler.class.getName()).log(Level.INFO, "Error while rollback : ", e);
            }
            throw new PersistenceException(ex2.getMessage(), (Throwable)ex2);
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
        return json;
    }
    
    private ArrayList<String> generateHeaders(final TableViewModel viewModel) throws Exception {
        final ArrayList<String> list = new ArrayList<String>();
        final TableTransformerContext transContext = viewModel.getTableTransformerContext();
        final TableIterator iter = viewModel.getNewTableIterator();
        iter.reset();
        while (iter.nextColumn()) {
            iter.initTransCtxForCurrentCell("HEADER");
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
        return list;
    }
    
    private JSONObject generateTable(final TableViewModel viewModel, final HttpServletRequest request) throws Exception {
        boolean hasrows = false;
        final TableTransformerContext transContext = viewModel.getTableTransformerContext();
        final TableIterator globalTableIterator = viewModel.getNewTableIterator();
        globalTableIterator.reset();
        final ArrayList<String> arrayList = this.generateHeaders(viewModel);
        final JSONArray rows = new JSONArray();
        while (globalTableIterator.nextRow()) {
            int k = 0;
            final JSONObject row = new JSONObject();
            hasrows = true;
            globalTableIterator.setCurrentColumn(-1);
            while (globalTableIterator.nextColumn()) {
                globalTableIterator.initTransCtxForCurrentCell("Cell");
                final Object value = transContext.getPropertyValue();
                row.put((String)arrayList.get(k), value);
                ++k;
                if (globalTableIterator.isLastColumn()) {
                    rows.put((Object)row);
                }
            }
        }
        if (!hasrows) {
            return null;
        }
        final JSONObject responseJSON = new JSONObject();
        responseJSON.put("status", 200);
        if (request.getPathInfo().split("/").length > 3) {
            final int pos = request.getPathInfo().split("/").length - 1;
            final JSONObject tmpresponseJSON = new JSONObject();
            tmpresponseJSON.put(request.getPathInfo().split("/")[pos], (Object)rows);
            responseJSON.put("RESPONSE", (Object)tmpresponseJSON);
            return responseJSON;
        }
        responseJSON.put("RESPONSE", (Object)rows);
        return responseJSON;
    }
}
