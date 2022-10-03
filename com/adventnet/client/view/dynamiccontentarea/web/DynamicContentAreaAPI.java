package com.adventnet.client.view.dynamiccontentarea.web;

import java.io.IOException;
import org.apache.struts.action.ActionForward;
import com.adventnet.iam.xss.IAMEncoder;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.client.view.web.StateAPI;
import java.util.List;
import java.util.ArrayList;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.view.web.WebViewAPI;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.util.web.WebConstants;
import com.adventnet.client.util.web.JavaScriptConstants;

public class DynamicContentAreaAPI implements JavaScriptConstants, WebConstants
{
    public static DynamicContentAreaModel getDynamicContentAreaModel(final HttpServletRequest request, String dynamicContentAreaNameArg) {
        try {
            dynamicContentAreaNameArg = DynamicContentAreaUtils.getDynamicContentAreaName(request, dynamicContentAreaNameArg);
            DynamicContentAreaModel vcModel = (DynamicContentAreaModel)request.getAttribute(dynamicContentAreaNameArg);
            if (vcModel == null) {
                final String rootViewId = WebViewAPI.getOriginalRootViewId(request, false);
                ViewContext viewCtx = null;
                if (rootViewId != null) {
                    viewCtx = ViewContext.getViewContext(rootViewId, request);
                }
                final String state = (viewCtx != null) ? ((String)viewCtx.getStateParameter(dynamicContentAreaNameArg + "_LIST")) : null;
                vcModel = new DynamicContentAreaModel(dynamicContentAreaNameArg, (state != null) ? DynamicContentAreaUtils.generateDCAIList(state, request) : new ArrayList());
                request.setAttribute(dynamicContentAreaNameArg, (Object)vcModel);
            }
            return vcModel;
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static void updateDynamicContentArea(final HttpServletRequest request, final Object viewNameToUpdate, final String uniqueIdForNewView, final String contentAreaName, final String requestParams, final boolean retrieveParamsFromRequest, final boolean addToList) throws Exception {
        final ViewContext vc = getViewContext(viewNameToUpdate, uniqueIdForNewView, request, retrieveParamsFromRequest, requestParams);
        final DynamicContentAreaModel model = getDynamicContentAreaModel(request, contentAreaName);
        if (addToList) {
            model.addToList(vc);
        }
        else {
            model.replaceList(vc);
        }
        vc.setStateOrURLStateParam("PDCA", model.getContentAreaName());
    }
    
    public static ViewContext getViewContext(final Object viewName, Object uniqueId, final HttpServletRequest request, final boolean retrieveParamsFromRequest, String params) throws DataAccessException {
        if (uniqueId == null) {
            uniqueId = viewName;
        }
        final ViewContext viewCtx = ViewContext.getViewContext(uniqueId, viewName, request);
        final String oldParams = (String)viewCtx.getStateOrURLStateParameter("_D_RP");
        if (retrieveParamsFromRequest) {
            final String queryString = WebViewAPI.getParamsForView(viewCtx.getModel().getViewConfiguration(), request);
            if (queryString != null) {
                params = ((params != null) ? (params + "&" + queryString) : queryString);
            }
        }
        if (params != null) {
            viewCtx.setStateOrURLStateParam("_D_RP", params);
        }
        else {
            viewCtx.setStateOrURLStateParam("_D_RP", null);
        }
        if ((params == null && oldParams != null) || (params != null && !params.equals(oldParams))) {
            StateAPI.clearOldStateForViewHeirarchy(viewCtx.getUniqueId());
            viewCtx.clearPreviousState();
        }
        return viewCtx;
    }
    
    public static void handleNavigationAction(final ViewContext sourceCtx, final HttpServletRequest request, final Object selectedView, final String params) throws Exception {
        final DataObject uiNavigConfigDO = sourceCtx.getModel().getViewConfiguration();
        final Row uiNavigRow = uiNavigConfigDO.getRow("UINavigationConfig");
        final boolean addToList = (boolean)uiNavigRow.get(3);
        handleNavigationAction(sourceCtx, request, selectedView, params, addToList);
    }
    
    public static void handleNavigationAction(final ViewContext sourceCtx, final HttpServletRequest request, final Object selectedView, final String params, final boolean addlist) {
        try {
            final DataObject uiNavigConfigDO = sourceCtx.getModel().getViewConfiguration();
            final Row uiNavigRow = uiNavigConfigDO.getRow("UINavigationConfig");
            final String contentAreaName = (String)uiNavigRow.get(2);
            final boolean addToList = addlist;
            updateDynamicContentArea(request, selectedView, null, contentAreaName, params, false, addToList);
            if (Boolean.TRUE.equals(uiNavigRow.get(5))) {
                final DynamicContentAreaModel defaultModel = getDynamicContentAreaModel(request, null);
                defaultModel.clearList();
            }
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static String genContentAreaStateScript(final HttpServletRequest request, final DynamicContentAreaModel model) {
        if (WebViewAPI.isAjaxRequest(request)) {
            final String origId = WebViewAPI.getOriginalRootViewId(request, false);
            if (origId != null) {
                final ViewContext origRoot = ViewContext.getViewContext(origId, request);
                origRoot.setStateParameter(model.getContentAreaName() + "_LIST", null);
            }
        }
        final ViewContext parentContext = (ViewContext)request.getAttribute("VIEW_CTX");
        final StringBuilder strBuilder = new StringBuilder("<script>");
        if (parentContext != null) {
            strBuilder.append("associateParentViewForDCA('").append(IAMEncoder.encodeJavaScript(model.getContentAreaName())).append("','").append(IAMEncoder.encodeJavaScript(parentContext.getUniqueId())).append("');");
        }
        strBuilder.append("updateDac('").append(IAMEncoder.encodeJavaScript(model.getContentIdsAsString())).append("','").append(IAMEncoder.encodeJavaScript(model.getContentAreaName())).append("');").append("</script>");
        return strBuilder.toString();
    }
    
    @Deprecated
    public static ActionForward closeView(final ViewContext viewCtx, final HttpServletRequest request) {
        final String dca = getContentAreaFromState(viewCtx, request);
        if (dca == null) {
            return new ActionForward("/mcReloadParentWindow.do");
        }
        final DynamicContentAreaModel model = getDynamicContentAreaModel(request, dca);
        model.popListTo(viewCtx);
        if (dca.equals("GLOBAL_DCA")) {
            return new ActionForward(WebViewAPI.getViewURL(request, model.getCurrentItem().getUniqueId()));
        }
        return new ActionForward(WebViewAPI.getRootViewURL(request));
    }
    
    @Deprecated
    public static ActionForward replaceView(final ViewContext viewCtx, final String newView, final String params, final boolean retrieveParamsFromRequest) throws Exception {
        final HttpServletRequest request = viewCtx.getRequest();
        final String dca = getContentAreaFromState(viewCtx, request);
        if (dca == null) {
            String url = "/" + newView + ".cc";
            if (params != null) {
                url = url + "?" + params;
            }
            return new ActionForward(url);
        }
        final DynamicContentAreaModel model = getDynamicContentAreaModel(request, dca);
        model.popListTo(viewCtx);
        updateDynamicContentArea(viewCtx.getRequest(), newView, null, dca, params, retrieveParamsFromRequest, true);
        if (dca.equals("GLOBAL_DCA")) {
            return new ActionForward(WebViewAPI.getViewURL(request, model.getCurrentItem().getUniqueId()));
        }
        return new ActionForward(WebViewAPI.getRootViewURL(request));
    }
    
    public static String generateOldStateForViews(final DynamicContentAreaModel model, final HttpServletRequest request) throws IOException {
        final List vcList = model.getContentList();
        final StringBuffer oldState = new StringBuffer();
        for (int i = 0, j = vcList.size() - 1; i < j; ++i) {
            final ViewContext viewCtx = vcList.get(i);
            StateAPI.generateStateScriptForView(viewCtx, oldState, true);
        }
        return oldState.toString();
    }
    
    public static String getContentAreaFromState(ViewContext vc, final HttpServletRequest request) {
        String dca;
        for (dca = (String)vc.getStateParameter("PDCA"); dca == null; dca = (String)vc.getStateParameter("PDCA")) {
            final String parentViewID = (String)vc.getStateParameter("_PV");
            if (parentViewID == null) {
                break;
            }
            final String parentView = StateAPI.getUniqueId(parentViewID);
            vc = ViewContext.getViewContext(parentView, request);
        }
        return dca;
    }
    
    public static ViewContext getViewCtxForCurrentContent(final HttpServletRequest request, final String dynamicContentAreaNameArg) {
        try {
            final DynamicContentAreaModel caiModel = getDynamicContentAreaModel(request, dynamicContentAreaNameArg);
            return caiModel.getCurrentItem();
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static ViewContext getPreviousViewInDCAFor(final ViewContext viewCtx) {
        final String contentAreaName = getContentAreaFromState(viewCtx, viewCtx.getRequest());
        final DynamicContentAreaModel dcModel = getDynamicContentAreaModel(viewCtx.getRequest(), contentAreaName);
        final List l = dcModel.getContentList();
        if (l.size() < 2) {
            return null;
        }
        final ViewContext prevCtx = l.get(l.size() - 2);
        return prevCtx;
    }
}
