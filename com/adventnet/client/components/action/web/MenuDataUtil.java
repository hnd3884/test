package com.adventnet.client.components.action.web;

import javax.servlet.http.HttpServletRequest;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.client.themes.web.ThemesAPI;
import com.adventnet.i18n.I18N;
import com.adventnet.authorization.AuthorizationException;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.client.action.web.MenuVariablesGenerator;
import com.adventnet.client.components.web.TransformerContext;
import com.adventnet.client.ClientException;
import com.adventnet.client.ClientErrorCodes;
import org.json.JSONObject;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.client.util.web.JavaScriptConstants;
import com.adventnet.client.action.web.MenuActionConstants;

public class MenuDataUtil implements MenuActionConstants, JavaScriptConstants
{
    private static Logger log;
    
    public static JSONObject getMenuData(final String menuId, final ViewContext viewContext) throws Exception {
        if (menuId == null || viewContext == null) {
            throw new ClientException(ClientErrorCodes.VIEW_MENU_NULL);
        }
        return getMenuData(menuId, viewContext, null);
    }
    
    public static JSONObject getMenuData(final String menuId, final TransformerContext transformerContext) throws Exception {
        if (menuId == null || transformerContext == null) {
            throw new ClientException(ClientErrorCodes.TRANSCTX_MENU_NULL);
        }
        return getMenuData(menuId, transformerContext.getViewContext(), transformerContext);
    }
    
    public static JSONObject getMenuData(final String menuId, final ViewContext viewContext, final TransformerContext transformerContext) throws Exception {
        if (menuId != null) {
            final JSONObject menuObjJSON = new JSONObject();
            final DataObject menuObj = MenuVariablesGenerator.getCompleteMenuData((Object)menuId);
            final String handlerClsName = (String)menuObj.getFirstValue("Menu", "HANDLER");
            CSRMenuHandler menuHandler = null;
            if (handlerClsName != null) {
                menuHandler = (CSRMenuHandler)WebClientUtil.createInstance(handlerClsName);
                menuHandler.initMenuHandler(viewContext);
                menuObjJSON.put("renderingAttr", (Object)menuHandler.getRenderingAttrForMenu(viewContext, transformerContext));
            }
            menuObjJSON.put("menuItems", (Object)menuIterator(menuObj, menuHandler, viewContext, transformerContext));
            menuObjJSON.put("menuId", (Object)menuId);
            menuObjJSON.put("templateName", menuObj.getFirstValue("Menu", 9));
            menuObjJSON.put("displayName", menuObj.getFirstValue("Menu", 4));
            menuObjJSON.put("displayStyle", menuObj.getFirstValue("Menu", 8));
            return menuObjJSON;
        }
        MenuDataUtil.log.fine("MenuId should not be null");
        return null;
    }
    
    private static JSONArray menuIterator(final DataObject menuObj, final CSRMenuHandler menuHandler, final ViewContext viewContext, final TransformerContext transformerContext) throws Exception {
        final JSONArray menuArray = new JSONArray();
        final Iterator<Row> iter = menuObj.getRows("MenuAndMenuItem");
        while (iter.hasNext()) {
            final JSONObject menuItem = getMenuItemAsJson(iter.next().get(2), viewContext, menuHandler, true, transformerContext);
            if (menuItem != null) {
                menuArray.put((Object)menuItem);
            }
        }
        return menuArray;
    }
    
    public static JSONObject getMenuItemAsJson(final Object menuItemId_no, final ViewContext viewContext) throws Exception {
        return getMenuItemAsJson(menuItemId_no, viewContext, null, false, null);
    }
    
    public static JSONObject getMenuItemAsJson(final Object menuItemId_no, final ViewContext viewContext, final CSRMenuHandler menuHandler) throws Exception {
        return getMenuItemAsJson(menuItemId_no, viewContext, menuHandler, false, null);
    }
    
    public static JSONObject getMenuItemAsJson(final Object menuItemId_no, final TransformerContext transformerContext) throws Exception {
        return getMenuItemAsJson(menuItemId_no, transformerContext.getViewContext(), null, false, transformerContext);
    }
    
    public static JSONObject getMenuItemAsJson(final Object menuItemId_no, final TransformerContext transformerContext, final CSRMenuHandler menuHandler) throws Exception {
        return getMenuItemAsJson(menuItemId_no, transformerContext.getViewContext(), menuHandler, false, transformerContext);
    }
    
    public static JSONObject getMenuItemAsJson(final Object menuItemId, final ViewContext viewContext, final CSRMenuHandler menuHandler, final boolean includeCreateMenu, final TransformerContext transformerContext) throws Exception {
        DataObject menuItemDataObj = null;
        JSONObject menuitemJson = null;
        if (menuItemId == null) {
            throw new RuntimeException("menu item ID should not be null");
        }
        try {
            try {
                menuItemDataObj = MenuVariablesGenerator.getCompleteMenuItemData(menuItemId);
            }
            catch (final AuthorizationException ae) {
                MenuDataUtil.log.fine(ae.getMessage());
                return null;
            }
            final String menuItemName = (String)menuItemDataObj.getFirstValue("MenuItem", 2);
            menuitemJson = new JSONObject();
            String imageSrc = null;
            String imageCSSClass = null;
            String cssClass = null;
            String title = null;
            final String contextPath = viewContext.getContextPath();
            final int statusOfMenuItem = checkAndProcessMenuItemHandling(viewContext, menuItemName, menuHandler, transformerContext);
            if (statusOfMenuItem == 0) {
                return null;
            }
            if (statusOfMenuItem == 2) {
                menuitemJson.put("disabled", true);
                imageSrc = (String)menuItemDataObj.getFirstValue("MenuItem", "IMAGEFORDISABLE");
                imageCSSClass = (String)menuItemDataObj.getFirstValue("MenuItem", "IMAGECSSCLASSFORDISABLE");
                cssClass = (String)menuItemDataObj.getFirstValue("MenuItem", "CSSCLASSFORDISABLE");
                title = (String)menuItemDataObj.getFirstValue("MenuItem", "TITLEFORDISABLE");
            }
            else if (statusOfMenuItem == 1) {
                imageSrc = (String)menuItemDataObj.getFirstValue("MenuItem", 6);
                imageCSSClass = (String)menuItemDataObj.getFirstValue("MenuItem", 8);
                cssClass = (String)menuItemDataObj.getFirstValue("MenuItem", "CSSCLASS");
                title = (String)menuItemDataObj.getFirstValue("MenuItem", 10);
            }
            menuitemJson.put("cssClass", (Object)cssClass);
            menuitemJson.put("title", (Object)title);
            menuitemJson.put("displayName", (Object)I18N.getMsg((String)menuItemDataObj.getFirstValue("MenuItem", 4), new Object[0]));
            if (imageSrc != null) {
                menuitemJson.put("imageSrc", (Object)ThemesAPI.handlePath(imageSrc, contextPath, ThemesAPI.getThemeDirForRequest(contextPath)));
            }
            menuitemJson.put("imageCSSClass", (Object)imageCSSClass);
            menuitemJson.put("menuItemId", (Object)menuItemName);
            if (statusOfMenuItem == 1) {
                if (menuHandler != null) {
                    menuitemJson.put("extraOptions", (Object)menuHandler.getExtraOptions(menuItemName, viewContext, transformerContext));
                }
                menuitemJson.put("emberActionName", menuItemDataObj.containsTable("CSRMenuItem") ? menuItemDataObj.getValue("CSRMenuItem", "ACTIONNAME", (Row)null) : "invokeMenuAction");
                final String actionArg = (String)menuItemDataObj.getValue("CSRMenuItem", "ACTIONARG", (Row)null);
                menuitemJson.put("emberActionArg", (Object)((actionArg != null) ? new JSONObject(actionArg) : null));
                final String routeto = (String)menuItemDataObj.getValue("CSRMenuItem", "ROUTETO", (Row)null);
                menuitemJson.put("routeTo", (Object)routeto);
                if (includeCreateMenu) {
                    menuitemJson.put("createMenu", (Object)MenuVariablesGenerator.generateMenuVariableJSON(menuItemDataObj, viewContext, true));
                }
                menuitemJson.put("scriptInclusion", (Object)getScriptInclusion(menuItemDataObj, contextPath));
                String reqParams = null;
                if (transformerContext != null) {
                    reqParams = MenuVariablesGenerator.getLinkParams(transformerContext.getColumnConfiguration().getRows("ACLinkParams"));
                }
                final String jsMethodName = (String)menuItemDataObj.getValue("JavaScriptAction", 3, (Row)null);
                final JSONObject menuInvoker = new JSONObject();
                menuInvoker.put("jsMethodName", (Object)jsMethodName);
                menuInvoker.put("menuItemId", (Object)menuItemName);
                menuInvoker.put("uniqueId", (Object)viewContext.getUniqueId());
                menuInvoker.put("reqParams", (Object)reqParams);
                menuInvoker.put("rowIndex", (Object)((transformerContext != null) ? Integer.valueOf(transformerContext.getRowIndex()) : null));
                menuitemJson.put("menuInvoker", (Object)menuInvoker);
            }
        }
        catch (final DataAccessException dae) {
            throw new RuntimeException("No menu item is found for the menu item id " + menuItemId, (Throwable)dae);
        }
        return menuitemJson;
    }
    
    private static int checkAndProcessMenuItemHandling(final ViewContext viewContext, final String menuItemId, final CSRMenuHandler menuHandler, final TransformerContext transformerContext) {
        return (menuHandler != null) ? menuHandler.handleMenuItem(menuItemId, viewContext, transformerContext) : 1;
    }
    
    @Deprecated
    public static String getScriptInclusion(final DataObject menuObj, final HttpServletRequest request) throws Exception {
        return getScriptInclusion(menuObj, request.getContextPath());
    }
    
    public static String getScriptInclusion(final DataObject menuObj, final String contextPath) throws Exception {
        final String jsInclude = (String)menuObj.getValue("JavaScriptAction", 2, (Row)null);
        return (jsInclude == null) ? null : (contextPath + jsInclude);
    }
    
    static {
        MenuDataUtil.log = Logger.getLogger(MenuDataUtil.class.getName());
    }
}
