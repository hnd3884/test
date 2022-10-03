package com.adventnet.client.components.tab.web;

import com.adventnet.client.components.util.web.PersonalizationUtil;
import com.adventnet.client.view.web.WebViewAPI;
import org.apache.struts.action.ActionForward;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.List;
import com.adventnet.client.util.web.WebClientUtil;
import org.json.JSONObject;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.dynamiccontentarea.web.DynamicContentAreaModel;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.client.view.dynamiccontentarea.web.DynamicContentAreaAPI;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.client.components.personalize.web.PersonalizableView;
import com.adventnet.client.util.web.WebConstants;
import com.adventnet.client.view.web.DefaultViewController;

public class TabController extends DefaultViewController implements WebConstants, PersonalizableView
{
    private static final String SORTEDTABLIST = "SORTEDTABLIST";
    private static Logger logger;
    
    public void updateViewModel(final ViewContext viewCtx) throws Exception {
        this.preModelFetch(viewCtx);
        final TabModel tabModel = this.getTabModel(viewCtx);
        viewCtx.setViewModel((Object)tabModel);
        final DataObject viewConfig = viewCtx.getModel().getViewConfiguration();
        if (!viewCtx.isCSRComponent() && viewConfig.containsTable("UINavigationConfig")) {
            final Row uiNavConfig = viewConfig.getRow("UINavigationConfig");
            final String contentAreaName = (String)uiNavConfig.get(2);
            final int refreshLevel = (int)uiNavConfig.get(6);
            final DynamicContentAreaModel model = DynamicContentAreaAPI.getDynamicContentAreaModel(viewCtx.getRequest(), contentAreaName);
            final ViewContext childViewCtx = model.getCurrentItem();
            final HttpServletRequest request = viewCtx.getRequest();
            request.setAttribute("refreshLevel", (Object)refreshLevel);
            final String selViewFromRequest = request.getParameter("SELECTEDVIEW");
            final String dacname = request.getParameter("DACNAME");
            final String viewToShow = request.getParameter("VIEWTOSHOW");
            if (childViewCtx == null) {
                String selView = ((TabModel)viewCtx.getViewModel()).getSelectedView();
                if (selViewFromRequest != null && (dacname == null || dacname.equals(contentAreaName))) {
                    selView = selViewFromRequest;
                }
                if (selView != null) {
                    DynamicContentAreaAPI.handleNavigationAction(viewCtx, viewCtx.getRequest(), (Object)selView, (String)viewCtx.getStateOrURLStateParameter("_D_RP"));
                }
                if (viewToShow != null && (dacname == null || dacname.equals(contentAreaName))) {
                    DynamicContentAreaAPI.handleNavigationAction(viewCtx, viewCtx.getRequest(), (Object)viewToShow, (String)viewCtx.getStateOrURLStateParameter("_D_RP"), true);
                }
            }
            else {
                String viewName = childViewCtx.getModel().getViewName();
                if (selViewFromRequest != null) {
                    viewName = selViewFromRequest;
                }
                if (!tabModel.isViewPresent(viewName)) {
                    tabModel.setSelectedView(null);
                }
                else {
                    tabModel.setSelectedView(viewName);
                }
                if (viewToShow != null) {
                    tabModel.setSelectedView(viewToShow);
                }
            }
        }
        this.postModelFetch(viewCtx);
    }
    
    public JSONObject getModelAsJSON(final ViewContext vc) throws Exception {
        return TabDataUtil.getAsJSON(vc);
    }
    
    protected TabModel getTabModel(final ViewContext viewCtx) throws Exception {
        final long acc_id = WebClientUtil.getAccountId();
        final Object uniqueId = viewCtx.getUniqueId();
        List childConfigList = (List)viewCtx.getModel().getCompiledData((Object)("SORTEDTABLIST:" + acc_id));
        if (childConfigList == null) {
            childConfigList = TabPersonalizationUtil.getTabChildConfigForView(uniqueId);
            TabController.logger.log(Level.FINER, "childList{0}", childConfigList);
            viewCtx.getModel().addCompiledData((Object)("SORTEDTABLIST:" + acc_id), (Object)childConfigList);
        }
        return new TabModel(viewCtx, childConfigList);
    }
    
    public ActionForward processEvent(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response, final String eventType) throws Exception {
        final String newSelectedView = WebClientUtil.getRequiredParameter("SELECTEDVIEWIDX", request);
        if (request.getParameter("AjaxTab") != null && request.getParameter("AjaxTab").equals("true")) {
            final String child = this.getChildTab(viewCtx, newSelectedView);
            return new ActionForward(child + ".cc");
        }
        this.updateSelectedView(viewCtx, new Integer(newSelectedView), request);
        return super.processEvent(viewCtx, request, response, eventType);
    }
    
    public String getChildTab(final ViewContext viewCtx, final String newSelectedView) throws Exception {
        final Long viewName_no = (Long)viewCtx.getModel().getViewConfiguration().getFirstValue("ViewConfiguration", 1);
        final Row row = new Row("ACTabChildConfig");
        row.set(1, (Object)viewName_no);
        row.set(3, (Object)Integer.parseInt(newSelectedView));
        final Row childRow = viewCtx.getModel().getViewConfiguration().getRow("ACTabChildConfig", row);
        return WebViewAPI.getViewName(childRow.get(2));
    }
    
    public void updateSelectedView(final ViewContext viewCtx, final String selectedView, final HttpServletRequest request) throws Exception {
        viewCtx.setStateOrURLStateParam("selectedView", (Object)selectedView);
        DynamicContentAreaAPI.handleNavigationAction(viewCtx, request, (Object)selectedView, (String)viewCtx.getStateOrURLStateParameter("_D_RP"));
    }
    
    public void updateSelectedView(final ViewContext viewCtx, final Integer newSelectedViewIdx, final HttpServletRequest request) throws Exception {
        final Long viewName_no = (Long)viewCtx.getModel().getViewConfiguration().getFirstValue("ViewConfiguration", 1);
        final List<Row> childTab = TabPersonalizationUtil.getTabChildConfigForView(viewName_no);
        long selectedView_no = 0L;
        for (int idx = 0; idx < childTab.size(); ++idx) {
            final int childIndex = (int)childTab.get(idx).get(3);
            if (childIndex == newSelectedViewIdx) {
                selectedView_no = (long)childTab.get(idx).get(2);
                break;
            }
        }
        final String selectedView = WebViewAPI.getViewName((Object)selectedView_no);
        this.updateSelectedView(viewCtx, selectedView, request);
    }
    
    public void deleteView(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response) {
    }
    
    public void createViewFromTemplate(final DataObject viewDataObj, final long accountId) throws Exception {
        PersonalizationUtil.createChildViewsFromTemplate(viewDataObj, accountId, "ACTabChildConfig", 2);
    }
    
    public void addView(final String viewName, final String newChildViewName, final long accountId, final HttpServletRequest request) throws Exception {
        TabPersonalizationUtil.addNewTabToView(viewName, accountId, newChildViewName);
    }
    
    static {
        TabController.logger = Logger.getLogger(TabController.class.getName());
    }
}
