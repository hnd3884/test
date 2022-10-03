package com.adventnet.client.components.table;

import com.adventnet.client.components.table.web.TableViewController;
import com.adventnet.client.view.State;
import com.adventnet.client.view.ViewAPI;
import com.adventnet.client.view.web.ViewContext;
import javax.servlet.http.HttpServletRequest;

public class TableViewAPI
{
    public static long getCount(final String viewName, final HttpServletRequest request) throws Exception {
        final ViewContext viewContext = ViewContext.getViewContext(viewName);
        viewContext.setContextPath(request.getContextPath());
        ViewAPI.updateViewStates(viewContext, request);
        return getCount(viewContext);
    }
    
    public static long getCount(final String viewName, final State state) throws Exception {
        final ViewContext viewContext = ViewContext.getViewContext(viewName, state);
        return getCount(viewContext);
    }
    
    public static long getCount(final ViewContext viewContext) throws Exception {
        return ((TableViewController)viewContext.getModel().getController()).getCount(viewContext);
    }
}
