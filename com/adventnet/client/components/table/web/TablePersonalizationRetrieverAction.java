package com.adventnet.client.components.table.web;

import com.adventnet.client.components.form.web.FormAPI;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.client.util.LookUpUtil;
import java.util.Map;
import org.apache.struts.action.ActionForward;
import javax.servlet.http.HttpServletResponse;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.adventnet.client.components.rangenavigator.web.NavigationConfig;
import java.util.ArrayList;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Column;
import com.adventnet.client.view.web.WebViewAPI;
import com.adventnet.client.view.UserPersonalizationAPI;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.client.components.util.web.PersonalizationUtil;
import com.adventnet.client.view.web.ViewContext;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.persistence.DataObject;
import com.adventnet.client.util.web.WebConstants;

public class TablePersonalizationRetrieverAction extends TableRetrieverAction implements WebConstants
{
    @Override
    protected HashMap getCustomCriteria(final DataObject tableViewDO, final HttpServletRequest request) throws Exception {
        final HashMap criteriaMap = new HashMap();
        final String uniqueId = (String)tableViewDO.getFirstValue("ViewConfiguration", 2);
        final ViewContext viewCtx = ViewContext.getViewContext((Object)uniqueId, request);
        final String viewName = PersonalizationUtil.getFromReqOrFeatureParams(viewCtx, "VIEWNAME");
        final Object personalizedViewName = UserPersonalizationAPI.getPersonalizedViewNameNo((Object)viewName, WebClientUtil.getAccountId());
        if (personalizedViewName != null) {
            criteriaMap.put("CUSTOMIZEVIEW", personalizedViewName);
        }
        else {
            criteriaMap.put("CUSTOMIZEVIEW", WebViewAPI.getViewNameNo((Object)viewName));
        }
        final ViewContext listViewCtx = ViewContext.getViewContext((Object)"ListViewConfigurations", (Object)"ListViewConfigurations", request);
        listViewCtx.setStateParameter("VIEWNAME", (Object)viewName);
        String searchColumn = (String)listViewCtx.getURLStateParameter("SEARCH_COLUMN");
        if (searchColumn == null) {
            searchColumn = (String)listViewCtx.getStateParameter("SEARCH_COLUMN");
        }
        String searchValue = (String)listViewCtx.getURLStateParameter("SEARCH_VALUE");
        if (searchValue == null) {
            searchValue = (String)listViewCtx.getStateParameter("SEARCH_VALUE");
        }
        if (searchColumn == null || searchValue == null) {
            String defaultGroup = PersonalizationUtil.getFromReqOrFeatureParams(viewCtx, "DefaultGroup");
            if (defaultGroup == null) {
                defaultGroup = "*";
            }
            listViewCtx.setStateOrURLStateParam("SEARCH_COLUMN", (Object)"GroupName");
            listViewCtx.setStateOrURLStateParam("SEARCH_VALUE", (Object)defaultGroup);
        }
        final TableViewModel model = (TableViewModel)listViewCtx.getViewModel(false);
        final NavigationConfig navigConfig = model.getNavigationConfig();
        request.setAttribute("NAVIGATION_CONFIG", (Object)navigConfig);
        final Column column = new Column("ACViewToGroupMapping", "GROUPNAME");
        final SelectQueryImpl sql = new SelectQueryImpl(new Table("ACViewToGroupMapping"));
        sql.addSelectColumn(column.distinct());
        final RelationalAPI api = RelationalAPI.getInstance();
        Connection conn = null;
        DataSet ds = null;
        try {
            conn = api.getConnection();
            ds = api.executeQuery((Query)sql, conn);
            final ArrayList list = new ArrayList();
            while (ds.next()) {
                final String value = (String)ds.getValue(1);
                list.add(value);
            }
            request.setAttribute("AvailableGroups", (Object)list);
        }
        finally {
            if (ds != null) {
                try {
                    ds.close();
                }
                catch (final Exception e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                }
                catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return criteriaMap;
    }
    
    public ActionForward processEvent(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response, final String eventType) throws Exception {
        final String viewNames = request.getParameter("TL_VIEWNAMES");
        String customizeViewName = request.getParameter("VIEWNAME");
        final boolean isNewView = "true".equals(request.getParameter("ISNEWVIEW"));
        if (isNewView) {
            try {
                PersonalizationUtil.isViewPresent(customizeViewName);
            }
            catch (final Exception e) {
                return WebViewAPI.sendResponse(request, response, false, "Already a view exists with name " + customizeViewName, (Map)null);
            }
        }
        if (viewNames != null) {
            final DataObject dao = WebViewAPI.getViewConfiguration((Object)customizeViewName);
            if (!dao.containsTable("ViewConfiguration")) {
                final String dummyViewName = viewCtx.getModel().getFeatureValue("VIEWNAME");
                customizeViewName = this.addNewTableLayout(request.getParameter("TITLE"), dummyViewName, WebClientUtil.getAccountId(), request);
            }
            else {
                this.updateTableLayoutForAccount(viewCtx, customizeViewName, WebClientUtil.getAccountId(), request);
            }
        }
        else if (request.getParameter("RESTORE") != null) {
            UserPersonalizationAPI.removePersonalizedView(request.getParameter("VIEWNAME"), WebClientUtil.getAccountId());
        }
        final Map params = new HashMap();
        params.put("VIEWNAME", customizeViewName);
        return WebViewAPI.sendResponse(request, response, true, "View " + (isNewView ? "created" : "updated") + "Successfully", params);
    }
    
    @Deprecated
    public ActionForward getForward(final ViewContext viewCtx, final String customizeViewName, final HttpServletRequest request) throws Exception {
        return PersonalizationUtil.getForward(viewCtx, customizeViewName, request);
    }
    
    public String addNewTableLayout(final String title, final String dummyViewName, final long accountId, final HttpServletRequest request) throws Exception {
        final String viewName = PersonalizationUtil.genNameFromTitle(title);
        final DataObject dataObject = PersonalizationUtil.createUpdateViewFromDummy(dummyViewName, viewName, title, accountId);
        final HashMap map = this.getMap(dataObject);
        this.updateChildren(request, viewName, dataObject, map);
        LookUpUtil.getPersistence().update((DataObject)dataObject);
        return viewName;
    }
    
    public void updateTableLayoutForAccount(final ViewContext viewCtx, final String viewName, final long accountId, final HttpServletRequest request) throws Exception {
        DataObject dataObject = UserPersonalizationAPI.getPersonalizedView((Object)viewName, accountId);
        dataObject = PersonalizationUtil.getDataObjectIfPlaceHolderView(dataObject, viewCtx.getModel().getFeatureValue("VIEWNAME"));
        final String persViewName = UserPersonalizationAPI.getPersonalizedConfigName(viewName, accountId);
        final HashMap map = this.getMap(dataObject);
        dataObject.deleteRows("ACTableLayoutChildConfig", (Row)null);
        UserPersonalizationAPI.updatePersonalizedView((WritableDataObject)dataObject, accountId);
        dataObject = UserPersonalizationAPI.getPersonalizedView((Object)viewName, accountId);
        this.updateChildren(request, persViewName, dataObject, map);
        UserPersonalizationAPI.updatePersonalizedView((WritableDataObject)dataObject, accountId);
    }
    
    protected HashMap getMap(final DataObject dataObject) throws Exception {
        final HashMap map = new HashMap();
        final Iterator iterator = dataObject.getRows("ACTableLayoutChildConfig");
        while (iterator.hasNext()) {
            final Row currentRow = iterator.next();
            final Object[] array = { currentRow.get(9), null, null };
            map.put(WebViewAPI.getViewName(currentRow.get(2)), array);
        }
        return map;
    }
    
    protected void updateChildren(final HttpServletRequest request, final String viewName, final DataObject dataObject, final HashMap map) throws Exception {
        final String[] viewNames = request.getParameterValues("TL_VIEWNAMES");
        final String[] rowIndex = request.getParameterValues("TL_ROWINDEX");
        final String[] columnIndex = request.getParameterValues("TL_COLUMNINDEX");
        final String[] rowspan = request.getParameterValues("TL_ROWSPAN");
        final String[] colspan = request.getParameterValues("TL_COLSPAN");
        for (int size = viewNames.length, i = 0; i < size; ++i) {
            final Row currentRow = new Row("ACTableLayoutChildConfig");
            currentRow.set(1, (Object)WebViewAPI.getViewNameNo((Object)viewName));
            currentRow.set(2, (Object)WebViewAPI.getViewNameNo((Object)viewNames[i]));
            currentRow.set(3, (Object)new Integer(rowIndex[i]));
            currentRow.set(4, (Object)new Integer(columnIndex[i]));
            currentRow.set(6, (Object)new Integer(rowspan[i]));
            currentRow.set(5, (Object)new Integer(colspan[i]));
            if (map.containsKey(viewNames[i])) {
                currentRow.set(9, ((Object[])map.get(viewNames[i]))[0]);
            }
            dataObject.addRow(currentRow);
        }
    }
    
    public void processPostRendering(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        response.getWriter().println(FormAPI.getAjaxFormScript(viewCtx));
    }
}
