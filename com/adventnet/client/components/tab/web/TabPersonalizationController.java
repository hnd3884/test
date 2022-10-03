package com.adventnet.client.components.tab.web;

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

public class TabPersonalizationController extends TablePersonalizationRetrieverAction
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
                customizeViewName = this.addNewTab(request.getParameter("TITLE"), dummyViewName, WebClientUtil.getAccountId(), request);
            }
            else {
                this.updateTabForAccount(viewCtx, customizeViewName, WebClientUtil.getAccountId(), request);
            }
        }
        else if (request.getParameter("RESTORE") != null) {
            UserPersonalizationAPI.removePersonalizedView(customizeViewName, WebClientUtil.getAccountId());
        }
        final Map params = new HashMap();
        params.put("VIEWNAME", customizeViewName);
        return WebViewAPI.sendResponse(request, response, true, "View " + (isNewView ? "created" : "updated") + "Successfully", params);
    }
    
    public String addNewTab(final String title, final String dummyViewName, final long accountId, final HttpServletRequest request) throws Exception {
        final String viewName = PersonalizationUtil.genNameFromTitle(title);
        final DataObject dataObject = PersonalizationUtil.createUpdateViewFromDummy(dummyViewName, viewName, title, accountId);
        final Object viewNameNo = dataObject.getFirstValue("ViewConfiguration", 1);
        this.updateChildren(request, viewNameNo, dataObject);
        LookUpUtil.getPersistence().update(dataObject);
        return viewName;
    }
    
    public void updateTabForAccount(final ViewContext viewCtx, final String viewName, final long accountId, final HttpServletRequest request) throws Exception {
        DataObject dataObject = UserPersonalizationAPI.getPersonalizedView((Object)viewName, accountId);
        dataObject = PersonalizationUtil.getDataObjectIfPlaceHolderView(dataObject, viewCtx.getModel().getFeatureValue("VIEWNAME"));
        final Object persViewName = dataObject.getFirstValue("ViewConfiguration", 1);
        dataObject.deleteRows("ACTabChildConfig", (Row)null);
        UserPersonalizationAPI.updatePersonalizedView((WritableDataObject)dataObject, accountId);
        dataObject = UserPersonalizationAPI.getPersonalizedView((Object)viewName, accountId);
        this.updateChildren(request, persViewName, dataObject);
        UserPersonalizationAPI.updatePersonalizedView((WritableDataObject)dataObject, accountId);
    }
    
    protected void updateChildren(final HttpServletRequest request, final Object viewName, final DataObject dataObject) throws Exception {
        final String[] showList = request.getParameterValues("selectedViewList");
        for (int size = showList.length, i = 0; i < size; ++i) {
            final String childViewName = showList[i];
            final Row currentRow = new Row("ACTabChildConfig");
            currentRow.set(1, viewName);
            Long childViewID = null;
            try {
                childViewID = Long.parseLong(childViewName);
            }
            catch (final NumberFormatException nfe) {
                childViewID = WebViewAPI.getViewNameNo((Object)childViewName);
            }
            currentRow.set(2, (Object)childViewID);
            currentRow.set(3, (Object)new Integer(i));
            dataObject.addRow(currentRow);
        }
    }
}
