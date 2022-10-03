package com.adventnet.client.components.layout.grid.web;

import com.adventnet.client.components.tab.web.TabModel;
import com.adventnet.client.view.web.WebViewAPI;
import org.json.JSONArray;
import org.json.JSONObject;
import com.adventnet.client.view.web.ViewContext;

public class GridLayoutUtil
{
    public static JSONObject getAsJSON(final ViewContext viewContext) throws Exception {
        final GridModel model = (GridModel)viewContext.getViewModel();
        final GridModel.GridIterator ite = model.getIterator();
        final JSONObject gridLayout = new JSONObject();
        final JSONArray childGridArray = new JSONArray();
        String selectedView = null;
        while (ite.next()) {
            final JSONObject childGrid = new JSONObject();
            childGrid.put("viewName", (Object)ite.getCurrentView());
            if (selectedView == null) {
                final ViewContext vc = ViewContext.getViewContext(ite.getCurrentView());
                final String componentNo = WebViewAPI.getUIComponentName(vc);
                if (componentNo != null && componentNo.equals("CSRTab")) {
                    final TabModel tabModel = (TabModel)vc.getViewModel();
                    selectedView = tabModel.getSelectedView();
                }
            }
            childGrid.put("childIndex", ite.getChildIndex());
            childGrid.put("title", (Object)ite.getTitle());
            childGridArray.put((Object)childGrid);
        }
        gridLayout.put("type", (Object)"grid");
        gridLayout.put("selectedView", (Object)selectedView);
        gridLayout.put("childCount", model.getChildCount());
        gridLayout.put("currentView", (Object)viewContext.getUniqueId());
        gridLayout.put("childGridArray", (Object)childGridArray);
        gridLayout.put("templateName", (Object)WebViewAPI.getViewTemplateName(viewContext));
        return gridLayout;
    }
}
