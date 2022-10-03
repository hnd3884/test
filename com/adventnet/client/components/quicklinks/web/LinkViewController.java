package com.adventnet.client.components.quicklinks.web;

import com.adventnet.persistence.WritableDataObject;
import com.adventnet.client.view.UserPersonalizationAPI;
import com.adventnet.persistence.DataObject;
import com.adventnet.client.cache.web.ClientDataObjectCache;
import com.adventnet.authorization.AuthorizationException;
import com.adventnet.client.util.LookUpUtil;
import java.util.ArrayList;
import com.adventnet.persistence.Row;
import java.util.Map;
import com.adventnet.client.view.web.WebViewAPI;
import org.apache.struts.action.ActionForward;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.client.util.DataUtils;
import java.util.List;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.util.web.WebConstants;
import com.adventnet.client.view.web.DefaultViewController;

public class LinkViewController extends DefaultViewController implements WebConstants
{
    private static final String SORTEDLINKLIST = "SORTEDLINKLIST";
    
    public void updateViewModel(final ViewContext viewCtx) throws Exception {
        final LinkModel linkModel = this.getLinkModel(viewCtx);
        viewCtx.setViewModel((Object)linkModel);
    }
    
    protected LinkModel getLinkModel(final ViewContext viewCtx) {
        final HttpServletRequest request = viewCtx.getRequest();
        List childConfigList = (List)viewCtx.getModel().getCompiledData((Object)"SORTEDLINKLIST");
        if (childConfigList == null) {
            childConfigList = DataUtils.getSortedList(viewCtx.getModel().getViewConfiguration(), "ACLink", "MENUITEMINDEX");
            viewCtx.getModel().addCompiledData((Object)"SORTEDLINKLIST", (Object)childConfigList);
        }
        return new LinkModel(viewCtx, childConfigList, WebClientUtil.getAccountId());
    }
    
    public ActionForward processEvent(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response, final String eventType) throws Exception {
        try {
            if ("DELETE".equals(eventType)) {
                final String linkName = WebClientUtil.getRequiredParameter("LINKNAME", request);
                final String message = this.deleteAction(viewCtx, linkName, WebClientUtil.getAccountId(), request, response);
                return WebViewAPI.sendResponse(request, response, true, message, (Map)null);
            }
            throw new IllegalArgumentException("Unknown event type passed." + eventType);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            return WebViewAPI.sendResponse(request, response, false, "Operation Failed : " + ex.getMessage(), (Map)null);
        }
    }
    
    public String deleteAction(final ViewContext viewCtx, final String linkName, final long accountId, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final Row menuItem = new Row("MenuItem");
        menuItem.set(2, (Object)linkName);
        final ArrayList ar = new ArrayList();
        ar.add("MenuItem");
        ar.add("ACLink");
        final DataObject dao = LookUpUtil.getPersistence().get((List)ar, menuItem);
        final Long createdBy = (Long)dao.getFirstValue("ACLink", "CREATEDBY");
        if (createdBy == null || createdBy != accountId) {
            throw new AuthorizationException("User is not allowed to delete " + dao.getFirstValue("MenuItem", 4));
        }
        LookUpUtil.getPersistence().delete(menuItem);
        final String viewName = (String)viewCtx.getModel().getViewConfiguration().getFirstValue("ViewConfiguration", "VIEWNAME");
        ClientDataObjectCache.clearCacheForView(viewName);
        return "Link (" + dao.getFirstValue("MenuItem", 4) + ") successfully deleted.";
    }
    
    public void addAction(final ViewContext vc, final DataObject newActionDO, final long accountId, final HttpServletRequest request) throws Exception {
        final String viewName = vc.getModel().getViewName();
        final DataObject dataObject = UserPersonalizationAPI.getPersonalizedView((Object)viewName, accountId);
        final String personalizedViewName = UserPersonalizationAPI.getPersonalizedConfigName(viewName, accountId);
        final int index = DataUtils.getMaxIndex(dataObject, "ACLink", 3);
        final Row newLink = new Row("ACLink");
        newLink.set(1, dataObject.getFirstValue("ViewConfiguration", 1));
        newLink.set(2, newActionDO.getFirstValue("MenuItem", 1));
        newLink.set(3, (Object)new Integer(index + 1));
        newLink.set(4, (Object)WebClientUtil.getAccountId());
        dataObject.addRow(newLink);
        dataObject.merge(newActionDO);
        UserPersonalizationAPI.updatePersonalizedView((WritableDataObject)dataObject, accountId);
        ViewContext.refreshViewContext(vc.getUniqueId(), request);
    }
}
