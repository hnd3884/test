package com.adventnet.client.action.web;

import com.adventnet.persistence.DataAccessException;
import java.util.List;
import com.adventnet.client.view.web.WebViewAPI;
import com.adventnet.persistence.Row;
import com.adventnet.client.util.DataUtils;
import com.adventnet.persistence.DataObject;

public class CSRMenuActionProcessorSevlet extends MenuActionProcessorSevlet
{
    private static final long serialVersionUID = 1L;
    
    @Override
    protected String getActionToForward(final String actionName) {
        String actionUrl = null;
        try {
            final DataObject menuItemDO = MenuVariablesGenerator.getCompleteMenuItemData(actionName);
            if (menuItemDO.containsTable("OpenViewInContentArea")) {
                actionUrl = this.getActionForwardUrl(actionName);
            }
            else {
                actionUrl = this.getDefaultActionToForward(actionName);
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return actionUrl;
    }
    
    private String getActionForwardUrl(final String menuItemId) throws DataAccessException {
        final DataObject obj = MenuVariablesGenerator.getCompleteMenuItemData(menuItemId);
        String actionUrl = "";
        Row row = null;
        if (obj.size("OpenViewInContentArea") == 1) {
            row = obj.getFirstRow("OpenViewInContentArea");
        }
        else {
            final List rowList = DataUtils.getSortedList(obj, "OpenViewInContentArea", "DACINDEX");
            row = ((rowList.size() > 0) ? rowList.get(rowList.size() - 1) : null);
        }
        if (row != null) {
            final String viewName = WebViewAPI.getViewName(row.get(2));
            actionUrl = "/" + viewName + ".ec";
            return actionUrl;
        }
        throw new RuntimeException("No rows found for the table OpenViewInContentArea");
    }
}
