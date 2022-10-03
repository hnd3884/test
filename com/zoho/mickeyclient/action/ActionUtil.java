package com.zoho.mickeyclient.action;

import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.persistence.DataObject;
import com.adventnet.client.view.web.WebViewAPI;
import com.adventnet.client.action.web.MenuVariablesGenerator;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public class ActionUtil
{
    public static void invoke(final String menuItem, final String viewName, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        if (menuItem == null) {
            throw new MenuActionException(ActionErrors.NULL_MENUITEM, new Object[0]);
        }
        final DataObject menuDO = MenuVariablesGenerator.getCompleteMenuItemData(menuItem);
        if (menuDO.containsTable("ViewToOpen")) {
            final String viewToReturn = WebViewAPI.getViewName(menuDO.getFirstValue("ViewToOpen", "VIEWNAME"));
            final String extension = WebViewAPI.isCSRComponent(viewToReturn) ? ".ec" : ".cc";
            HttpUtil.forward("/" + viewToReturn + extension, request, response);
            return;
        }
        if (menuDO.containsTable("Action")) {
            final Action action = getAction(menuItem, menuDO);
            final ActionContext context = new ActionContext(menuItem, viewName);
            action.set(context, request, response);
            action.execute(context, request, response);
        }
    }
    
    private static Action getAction(final String menuItem, final DataObject menuDO) throws Exception {
        final String actionClass = (String)menuDO.getFirstValue("Action", "TYPE");
        if (actionClass == null) {
            throw new MenuActionException(ActionErrors.ACTION_MISSING, new Object[] { menuItem });
        }
        return (Action)WebClientUtil.createInstance(actionClass);
    }
}
