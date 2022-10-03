package com.adventnet.client.view.dynamiccontentarea.web;

import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.view.web.StateAPI;
import com.adventnet.client.view.web.WebViewAPI;
import java.util.List;
import com.adventnet.persistence.DataObject;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.persistence.Row;
import com.adventnet.client.util.DataUtils;
import com.adventnet.client.action.web.MenuVariablesGenerator;
import org.apache.struts.action.ActionForward;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import com.adventnet.client.util.web.WebConstants;
import org.apache.struts.action.Action;

public class ShowViewInContentAreaAction extends Action implements WebConstants
{
    public ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final String menuItemId = request.getParameter("MENUITEM_ID");
        if (menuItemId != null) {
            final DataObject obj = MenuVariablesGenerator.getCompleteMenuItemData(menuItemId);
            ActionForward actFrd = null;
            if (obj.size("OpenViewInContentArea") == 1) {
                actFrd = this.process(obj.getFirstRow("OpenViewInContentArea"), request);
            }
            else {
                final List rowList = DataUtils.getSortedList(obj, "OpenViewInContentArea", "DACINDEX");
                for (int i = 0; i < rowList.size(); ++i) {
                    actFrd = this.process(rowList.get(i), request);
                }
            }
            return actFrd;
        }
        final String viewName = WebClientUtil.getRequiredParameter("VIEW_TO_OPEN", request);
        return this.process(null, request.getParameter("CONTENTAREANAME"), viewName, "true".equals(request.getParameter("ADDTOLIST")), null, true, request);
    }
    
    public ActionForward process(final Row opvRow, final HttpServletRequest request) throws Exception {
        return this.process((String)opvRow.get(3), (String)opvRow.get(4), WebViewAPI.getViewName(opvRow.get(2)), (boolean)opvRow.get(5), (String)opvRow.get(6), (boolean)opvRow.get(7), request);
    }
    
    public ActionForward process(String origParentViewName, String contentAreaName, final String viewName, final boolean addToList, final String params, final boolean retreiveParamsFrmReq, final HttpServletRequest request) throws Exception {
        if (origParentViewName == null) {
            origParentViewName = (String)StateAPI.getRequiredState("_REQS", "_RVID");
        }
        final String parentViewName = origParentViewName;
        if (contentAreaName == null && origParentViewName == null) {
            final String sourceView = request.getParameter("ACTION_SOURCE");
            if (sourceView != null) {
                final ViewContext viewCtx = ViewContext.getViewContext(sourceView, request);
                contentAreaName = DynamicContentAreaAPI.getContentAreaFromState(viewCtx, request);
                if (contentAreaName == null) {
                    contentAreaName = this.handleStandAloneView(viewCtx, request);
                }
            }
        }
        if (contentAreaName == null || contentAreaName.equals("DEFAULTCONTENTAREA")) {
            contentAreaName = DynamicContentAreaUtils.getDynamicContentAreaName(request, null, parentViewName);
        }
        String newViewId = request.getParameter("VIEW_UNIQUEID");
        if (newViewId == null) {
            newViewId = viewName;
        }
        DynamicContentAreaAPI.updateDynamicContentArea(request, viewName, newViewId, contentAreaName, params, retreiveParamsFrmReq, addToList);
        if (origParentViewName != null) {
            return new ActionForward(WebViewAPI.getViewUrlWithoutQueryString(parentViewName));
        }
        if (contentAreaName.equals("GLOBAL_DCA")) {
            return new ActionForward("/" + viewName + ".cc");
        }
        return new ActionForward(WebViewAPI.getRootViewURL(request));
    }
    
    protected String handleStandAloneView(final ViewContext sourceViewCtx, final HttpServletRequest request) {
        if (!sourceViewCtx.getUniqueId().equals(StateAPI.getRequiredState("_REQS", "_RVID"))) {
            return null;
        }
        final DynamicContentAreaModel model = DynamicContentAreaAPI.getDynamicContentAreaModel(request, "GLOBAL_DCA");
        model.addToList(sourceViewCtx);
        sourceViewCtx.setStateParameter("PDCA", "GLOBAL_DCA");
        return "GLOBAL_DCA";
    }
}
