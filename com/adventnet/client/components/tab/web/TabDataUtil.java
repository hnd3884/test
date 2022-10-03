package com.adventnet.client.components.tab.web;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.client.view.web.WebViewAPI;
import org.json.JSONArray;
import org.json.JSONObject;
import com.adventnet.client.view.web.ViewContext;

public class TabDataUtil
{
    public static JSONObject getAsJSON(final ViewContext viewContext) throws Exception {
        final JSONObject tabData = new JSONObject();
        final Object modelObj = viewContext.getViewModel();
        if (!(modelObj instanceof TabModel)) {
            return null;
        }
        final TabModel model = (TabModel)modelObj;
        final TabModel.TabIterator ite = model.getIterator();
        tabData.put("type", (Object)model.getViewType());
        final String routerName = viewContext.getModel().getFeatureValue("ROUTER_NAME");
        tabData.put("routerName", (Object)routerName);
        final String selectedView = model.getSelectedView();
        final JSONArray tabs = new JSONArray();
        while (ite.next()) {
            final JSONObject tab = new JSONObject();
            String isActive = "";
            if (!ite.getCurrentClass().contains("notSelected")) {
                isActive = "active";
                tabData.put("selected", (Object)ite.getCurrentIndex());
            }
            tab.put("index", (Object)ite.getCurrentIndex());
            tab.put("active", (Object)isActive);
            final String icoFile = ite.getChildIconFile();
            if (icoFile != null) {
                tab.put("type", (Object)"icon");
                tab.put("url", (Object)icoFile);
            }
            if (!model.getViewType().equals("icontab")) {
                tab.put("title", (Object)ite.getTitle());
            }
            if (ite.dropDownExists()) {
                tab.put("dropDownMenu", (Object)TabDropDownDataUtil.getDropDownAsJSON(ite, viewContext));
            }
            final String currentView = ite.getCurrentView();
            tab.put("name", (Object)currentView);
            tab.put("routerName", (Object)routerName);
            final ViewContext currentViewContext = ViewContext.getViewContext(currentView);
            tab.put("cssclass", (Object)getCssClassForView(currentViewContext));
            tab.put("templateName", (Object)WebViewAPI.getViewTemplateName(currentViewContext));
            tabs.put((Object)tab);
        }
        tabData.put("tabs", (Object)tabs);
        tabData.put("selectedView", (Object)selectedView);
        tabData.put("name", (Object)viewContext.getUniqueId());
        tabData.put("cssclass", (Object)getCssClassForView(viewContext));
        tabData.put("templateName", (Object)WebViewAPI.getViewTemplateName(viewContext));
        return tabData;
    }
    
    private static String getCssClassForView(final ViewContext viewContext) throws DataAccessException {
        final DataObject dataObject = viewContext.getModel().getViewConfiguration();
        return (String)dataObject.getFirstValue("ViewConfiguration", 12);
    }
}
