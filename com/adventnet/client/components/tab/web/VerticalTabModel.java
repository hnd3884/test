package com.adventnet.client.components.tab.web;

import java.io.IOException;
import javax.servlet.ServletException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import com.adventnet.client.components.layout.grid.web.GridLayoutUtil;
import org.json.JSONObject;
import com.adventnet.client.view.web.ViewContext;
import javax.servlet.http.HttpServlet;

public class VerticalTabModel extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    
    public static JSONObject getAsJSON(final ViewContext viewContext) throws Exception {
        final JSONObject gridJSON = GridLayoutUtil.getAsJSON(viewContext);
        final JSONArray childGridArray = gridJSON.getJSONArray("childGridArray");
        String selectedView = null;
        for (int i = 0; i < childGridArray.length(); ++i) {
            final JSONObject childGrid = childGridArray.getJSONObject(i);
            final String childGridViewName = childGrid.get("viewName").toString();
            final ViewContext vc = ViewContext.getViewContext((Object)childGridViewName, viewContext.getRequest());
            vc.getViewModel(true);
            final JSONObject tempTab = TabDataUtil.getAsJSON(vc);
            if (selectedView == null && tempTab != null && tempTab.has("selectedView")) {
                selectedView = tempTab.get("selectedView").toString();
            }
            childGrid.put("childGrid", (Object)tempTab);
            childGridArray.put(i, (Object)childGrid);
        }
        gridJSON.put("childGridArray", (Object)childGridArray);
        gridJSON.put("selectedView", (Object)selectedView);
        return gridJSON;
    }
    
    public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final PrintWriter out = response.getWriter();
        response.setContentType("application/json;charset=utf-8");
        final String id = request.getParameter("viewName");
        try {
            if (id == null) {
                throw new RuntimeException("REQUEST-PARAM : viewName should not be null");
            }
            final ViewContext vc = ViewContext.getViewContext((Object)id, request);
            vc.getViewModel();
            out.println(getAsJSON(vc));
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
}
