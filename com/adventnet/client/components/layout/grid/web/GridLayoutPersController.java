package com.adventnet.client.components.layout.grid.web;

import java.util.Iterator;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.Row;
import com.adventnet.client.util.LookUpUtil;
import com.adventnet.persistence.DataObject;
import java.util.HashMap;
import com.adventnet.client.view.UserPersonalizationAPI;
import com.adventnet.client.util.web.WebClientUtil;
import java.util.Map;
import com.adventnet.client.view.web.WebViewAPI;
import com.adventnet.client.components.util.web.PersonalizationUtil;
import org.apache.struts.action.ActionForward;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.components.table.web.TablePersonalizationRetrieverAction;

public class GridLayoutPersController extends TablePersonalizationRetrieverAction
{
    @Override
    public ActionForward processEvent(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response, final String eventType) throws Exception {
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
        if (request.getParameter("selectedViewList") != null) {
            final DataObject dao = WebViewAPI.getViewConfiguration((Object)customizeViewName);
            if (!dao.containsTable("ViewConfiguration")) {
                final String dummyViewName = viewCtx.getModel().getFeatureValue("VIEWNAME");
                customizeViewName = this.addNewGrid(request.getParameter("TITLE"), dummyViewName, WebClientUtil.getAccountId(), request);
            }
            else {
                this.updateGridForAccount(viewCtx, customizeViewName, WebClientUtil.getAccountId(), request);
            }
        }
        else if (request.getParameter("RESTORE") != null) {
            UserPersonalizationAPI.removePersonalizedView(customizeViewName, WebClientUtil.getAccountId());
        }
        final Map params = new HashMap();
        params.put("VIEWNAME", customizeViewName);
        return WebViewAPI.sendResponse(request, response, true, "View " + (isNewView ? "created" : "updated") + "Successfully", params);
    }
    
    public String addNewGrid(final String title, final String dummyViewName, final long accountId, final HttpServletRequest request) throws Exception {
        final String viewName = PersonalizationUtil.genNameFromTitle(title);
        PersonalizationUtil.isViewPresent(viewName);
        final DataObject dataObject = PersonalizationUtil.createUpdateViewFromDummy(dummyViewName, viewName, title, accountId);
        final String[] showList = request.getParameterValues("selectedViewList");
        updateChildren(request, viewName, dataObject, showList, null, null);
        LookUpUtil.getPersistence().update(dataObject);
        return viewName;
    }
    
    public void updateGridForAccount(final ViewContext viewCtx, final String viewName, final long accountId, final HttpServletRequest request) throws Exception {
        final String[] showList = request.getParameterValues("selectedViewList");
        updateGridForAccount(viewName, accountId, request, showList, viewCtx.getModel().getFeatureValue("VIEWNAME"), null);
    }
    
    public static void updateGridForAccount(final String viewName, final long accountId, final HttpServletRequest request, final String[] newViewList, final String dummyViewName, final Map newState) throws Exception {
        DataObject dataObject = UserPersonalizationAPI.getPersonalizedView((Object)viewName, accountId);
        if (dummyViewName != null) {
            dataObject = PersonalizationUtil.getDataObjectIfPlaceHolderView(dataObject, dummyViewName);
        }
        final String persViewName = (String)dataObject.getFirstValue("ViewConfiguration", 2);
        final Iterator iterator = dataObject.getRows("ACGridLayoutChildConfig");
        final HashMap map = new HashMap();
        while (iterator.hasNext()) {
            final Row currentRow = iterator.next();
            map.put(WebViewAPI.getViewName((Object)currentRow.get(2)), currentRow);
        }
        dataObject.deleteRows("ACGridLayoutChildConfig", (Row)null);
        UserPersonalizationAPI.updatePersonalizedView((WritableDataObject)dataObject, accountId);
        dataObject = UserPersonalizationAPI.getPersonalizedView((Object)viewName, accountId);
        updateChildren(request, persViewName, dataObject, newViewList, map, newState);
        UserPersonalizationAPI.updatePersonalizedView((WritableDataObject)dataObject, accountId);
    }
    
    private static void updateChildren(final HttpServletRequest request, final String viewName, final DataObject dataObject, final String[] newList, final Map origList, final Map newState) throws Exception {
        for (int size = newList.length, i = 0; i < size; ++i) {
            final String childViewName = newList[i];
            final Row currentRow = new Row("ACGridLayoutChildConfig");
            currentRow.set(1, (Object)WebViewAPI.getViewNameNo((Object)viewName));
            currentRow.set(2, (Object)WebViewAPI.getViewNameNo((Object)childViewName));
            currentRow.set(3, (Object)new Integer(i));
            if (origList != null) {
                final Row oldRow = origList.get(childViewName);
                if (oldRow != null) {
                    currentRow.set(4, oldRow.get(4));
                    currentRow.set(5, oldRow.get(5));
                }
            }
            if (newState != null) {
                final String state = newState.get("S_" + childViewName);
                if (state != null) {
                    currentRow.set(5, (Object)new Boolean(state));
                }
            }
            dataObject.addRow(currentRow);
        }
    }
}
