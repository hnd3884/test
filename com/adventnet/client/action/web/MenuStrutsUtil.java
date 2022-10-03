package com.adventnet.client.action.web;

import org.apache.struts.config.ModuleConfig;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import org.apache.struts.action.ActionMapping;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public class MenuStrutsUtil
{
    public static ActionMapping getActionMapping(final HttpServletRequest request, final HttpServletResponse response, final String path, final ServletContext servletCtx) throws Exception {
        MenuActionMapping mapping = null;
        final String isMA = request.getParameter("MA");
        if (isMA == null) {
            return null;
        }
        final int index = path.lastIndexOf(47);
        String menuItemId = path.substring(index + 1);
        final int endIndex = menuItemId.indexOf(46);
        if (endIndex > -1) {
            menuItemId = menuItemId.substring(0, endIndex);
        }
        try {
            if (menuItemId != null) {
                final DataObject menuItemDO = MenuVariablesGenerator.getCompleteMenuItemData(menuItemId);
                if (menuItemDO.containsTable("Action")) {
                    Row actionRow = new Row("Action");
                    actionRow.set("MENUITEMID", (Object)menuItemDO.getFirstValue("MenuItem", "MENUITEMID_NO"));
                    actionRow = menuItemDO.getFirstRow("Action", actionRow);
                    mapping = new MenuActionMapping();
                    mapping.setDataObject(menuItemDO);
                    mapping.setPath("/" + menuItemId);
                    mapping.setForward((String)actionRow.get("FORWARD"));
                    mapping.setType((String)actionRow.get("TYPE"));
                    mapping.setName((String)actionRow.get("NAME"));
                    mapping.setInput((String)actionRow.get("INPUT"));
                    final Boolean validate = (Boolean)actionRow.get("VALIDATED");
                    if (validate != null) {
                        mapping.setValidate((boolean)validate);
                    }
                    mapping.setModuleConfig(getModuleConfig(request, servletCtx));
                    return mapping;
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return mapping;
    }
    
    protected static ModuleConfig getModuleConfig(final HttpServletRequest request, final ServletContext servletCtx) {
        ModuleConfig config = (ModuleConfig)request.getAttribute("org.apache.struts.action.MODULE");
        if (config == null) {
            config = (ModuleConfig)servletCtx.getAttribute("org.apache.struts.action.MODULE");
        }
        return config;
    }
}
