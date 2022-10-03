package com.adventnet.client.action.web;

import com.adventnet.client.tpl.TemplateAPI;
import java.util.HashMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.zoho.mickeyclient.action.Action;
import com.adventnet.client.util.web.JSUtil;
import com.adventnet.client.view.web.ViewContext;
import org.json.JSONObject;
import java.util.Iterator;
import com.adventnet.client.themes.web.ThemesAPI;
import com.adventnet.client.view.web.StateUtils;
import com.adventnet.i18n.I18N;
import com.adventnet.iam.xss.IAMEncoder;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.persistence.QueryConstructor;
import com.adventnet.persistence.Persistence;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataAccessException;
import java.util.List;
import com.adventnet.authorization.AuthorizationException;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.client.util.LookUpUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.personality.PersonalityConfigurationUtil;
import com.adventnet.client.cache.StaticCache;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;
import com.adventnet.client.util.web.JavaScriptConstants;

public class MenuVariablesGenerator implements JavaScriptConstants, MenuActionConstants
{
    private static Logger out;
    
    public static DataObject getCompleteMenuItemData(final Object menuItemID_NO) throws DataAccessException {
        final String key = "MenuItem:" + menuItemID_NO;
        DataObject menuItemData = (DataObject)StaticCache.getFromCache(key);
        if (menuItemData == null) {
            final List<String> tablesList = PersonalityConfigurationUtil.getConstituentTables(PersonalityConfigurationUtil.getContainedPersonalities("MenuItem"));
            final Row menuItemRow = new Row("MenuItem");
            if (menuItemID_NO instanceof Long) {
                menuItemRow.set(1, menuItemID_NO);
            }
            else if (menuItemID_NO instanceof String) {
                menuItemRow.set(2, menuItemID_NO);
            }
            menuItemData = LookUpUtil.getPersistence().getCompleteData(menuItemRow);
            if (!menuItemData.containsTable("MenuItem")) {
                return menuItemData;
            }
            if (!WebClientUtil.isMenuItemAuthorized(menuItemData)) {
                throw new AuthorizationException("User is not allowed to get the data of MenuItem " + menuItemData.getFirstValue("MenuItem", 2));
            }
            Object keyobj = null;
            if (menuItemID_NO instanceof Long) {
                keyobj = menuItemData.getFirstValue("MenuItem", 2);
            }
            else if (menuItemID_NO instanceof String) {
                keyobj = menuItemData.getFirstValue("MenuItem", 1);
            }
            final String key2 = "MenuItem:" + keyobj;
            StaticCache.addToCache(key, key2, menuItemData, tablesList);
        }
        else if (!WebClientUtil.isMenuItemAuthorized(menuItemData)) {
            throw new AuthorizationException("User is not allowed to get the data of MenuItem " + menuItemData.getFirstValue("MenuItem", 2));
        }
        return menuItemData;
    }
    
    public static String getMenuItemID(final Long menuitemID_NO) throws DataAccessException {
        final DataObject menuitemObj = getCompleteMenuItemData(menuitemID_NO);
        return (String)menuitemObj.getFirstValue("MenuItem", 2);
    }
    
    public static Long getMenuItemNo(final String menuitemID) throws Exception {
        final Persistence persistence = LookUpUtil.getPersistence();
        final Criteria crit = new Criteria(new Column("MenuItem", 2), (Object)menuitemID, 0);
        final DataObject menuitemObj = persistence.get("MenuItem", crit);
        return (Long)menuitemObj.getFirstValue("MenuItem", 1);
    }
    
    public static String getMenuID(final Object menuIdNo) throws DataAccessException {
        final DataObject menuitemObj = getCompleteMenuData(menuIdNo);
        if (menuitemObj == null) {
            return null;
        }
        return (String)menuitemObj.getFirstValue("Menu", 2);
    }
    
    public static DataObject getCompleteMenuData(final Object menuId) throws DataAccessException {
        if (menuId == null) {
            return null;
        }
        final String key = "Menu:" + menuId;
        DataObject menuData = (DataObject)StaticCache.getFromCache(key);
        if (menuData == null) {
            final List<String> tablesList = PersonalityConfigurationUtil.getConstituentTables(PersonalityConfigurationUtil.getContainedPersonalities("Menu"));
            final Row menuRow = new Row("Menu");
            if (menuId instanceof String) {
                menuRow.set(2, menuId);
            }
            else {
                menuRow.set(1, menuId);
            }
            final SelectQuery query = QueryConstructor.getForPersonality("Menu", menuRow);
            final Column column = new Column("MenuAndMenuItem", 3);
            final SortColumn sortColumn = new SortColumn(column, true);
            query.addSortColumn(sortColumn);
            menuData = LookUpUtil.getPersistence().get(query);
            if (!WebClientUtil.isMenuAuthorized(menuData)) {
                throw new AuthorizationException("User is not allowed to get the data of Menu " + menuData.getFirstValue("Menu", 2));
            }
            Object keyobj = null;
            if (menuId instanceof Long) {
                keyobj = menuData.getFirstValue("Menu", 2);
            }
            else if (menuId instanceof String) {
                keyobj = menuData.getFirstValue("Menu", 1);
            }
            final String key2 = "Menu:" + keyobj;
            StaticCache.addToCache(key2, menuData, tablesList);
            StaticCache.addToCache(key, menuData, tablesList);
        }
        else if (!WebClientUtil.isMenuAuthorized(menuData)) {
            throw new AuthorizationException("User is not allowed to get the data of Menu " + menuData.getFirstValue("Menu", 2));
        }
        return menuData;
    }
    
    public static String getMenuDisplayName(final String menuId) throws Exception {
        final DataObject menuData = getCompleteMenuData(menuId);
        return (String)menuData.getFirstValue("Menu", 4);
    }
    
    public static String getScriptInclusion(final Object menuItemId, final HttpServletRequest request) throws Exception {
        return getScriptInclusion(getCompleteMenuItemData(menuItemId), request);
    }
    
    public static String getScriptInclusion(final DataObject menuObj, final HttpServletRequest request) throws Exception {
        final String ctxPath = request.getContextPath();
        final StringBuilder include = new StringBuilder();
        List<String> jsIncluded = (List<String>)request.getAttribute("JSIncluded");
        if (jsIncluded == null) {
            jsIncluded = new ArrayList<String>();
        }
        if (menuObj.containsTable("JavaScriptAction")) {
            final Row row = menuObj.getFirstRow("JavaScriptAction");
            final String jsInclude = (String)row.get(2);
            if (jsInclude == null || jsIncluded.contains(jsInclude)) {
                return "";
            }
            jsIncluded.add(jsInclude);
            include.append("<SCRIPT src='");
            include.append(ctxPath);
            include.append(jsInclude);
            include.append("'></SCRIPT>\n");
        }
        request.setAttribute("JSIncluded", (Object)jsIncluded);
        return include.toString();
    }
    
    public static String generateMenuVariableScript(final Object menuItemId, final HttpServletRequest request) throws Exception {
        return generateMenuVariableScript(menuItemId, request, false);
    }
    
    public static String generateMenuVariableScript(final Object menuItemId, final HttpServletRequest request, final boolean isPreGenerated) throws Exception {
        if (toBeGenerated(menuItemId, request)) {
            return generateMenuVariableScript(getCompleteMenuItemData(menuItemId), request, isPreGenerated);
        }
        return "";
    }
    
    public static String generateMenuVariableScript(final DataObject menuItemObj, final HttpServletRequest request, final boolean isPreGenerated) throws Exception {
        final String menuItemId = (String)menuItemObj.getFirstRow("MenuItem").get(2);
        if (!toBeGenerated(menuItemId, request)) {
            return "";
        }
        final Boolean alreadyGenerated = (Boolean)menuItemObj.getValue("WebMenuItem", 5, (Row)null);
        if (alreadyGenerated != null && alreadyGenerated) {
            return "";
        }
        final List<String> generatedMenuItems = (List<String>)request.getAttribute("GeneratedMenuIds");
        generatedMenuItems.add(menuItemId);
        request.setAttribute("GeneratedMenuIds", (Object)generatedMenuItems);
        final String ctxPath = request.getContextPath();
        final StringBuffer menuDataProps = new StringBuffer();
        if (!isPreGenerated) {
            menuDataProps.append("\n");
            menuDataProps.append("<script>");
        }
        Row row = menuItemObj.getFirstRow("MenuItem");
        menuDataProps.append("createMenuItem('").append(IAMEncoder.encodeJavaScript(menuItemId)).append("'");
        menuDataProps.append(",");
        menuDataProps.append('[');
        String dispName = (String)row.get(4);
        dispName = I18N.getMsg(dispName, new Object[0]);
        StateUtils.generateArgument(menuDataProps, (dispName == null) ? menuItemId : dispName, 0);
        menuDataProps.append(",");
        final String imageName = (String)row.get(6);
        if (imageName != null && imageName.contains("THEME_DIR")) {
            final String themeDir = ThemesAPI.getThemeDirForRequest(request);
            menuDataProps.append("'").append(imageName.replaceFirst("${THEME_DIR}", themeDir)).append("'");
        }
        else if (imageName != null) {
            menuDataProps.append("'").append(ctxPath).append(IAMEncoder.encodeJavaScript(imageName)).append("'");
        }
        else {
            menuDataProps.append("null");
        }
        menuDataProps.append(",");
        final String cnfStr = (String)row.get(7);
        menuDataProps.append((cnfStr == null) ? "null" : ("'" + IAMEncoder.encodeJavaScript(I18N.getMsg(cnfStr, new Object[0])) + "'")).append(",");
        menuDataProps.append("'").append(getActionLink(menuItemObj, request)).append("'");
        menuDataProps.append(",");
        final String linkParams = getLinkParams(menuItemObj.getRows("ACParams"));
        menuDataProps.append(linkParams.isEmpty() ? "null" : ("'" + IAMEncoder.encodeJavaScript(linkParams) + "'"));
        String atleastselectonerow = (String)row.get("ATLEASTSELECTONESTRING");
        row = menuItemObj.getRow("WebMenuItem");
        if (row == null) {
            row = new Row("WebMenuItem");
        }
        StateUtils.generateArgument(menuDataProps, (String)row.get(3), 2);
        StateUtils.generateArgument(menuDataProps, (String)row.get(4), 2);
        StateUtils.generateArgument(menuDataProps, (String)row.get(6), 2);
        row = menuItemObj.getRow("ACAjaxOptions");
        if (row == null) {
            row = new Row("ACAjaxOptions");
        }
        StateUtils.generateArgument(menuDataProps, (String)row.get(2), 2);
        StateUtils.generateArgument(menuDataProps, (String)row.get(3), 2);
        generateArgument(menuDataProps, menuItemObj, "JavaScriptAction", 3);
        if (atleastselectonerow != null) {
            menuDataProps.append(",");
            atleastselectonerow = I18N.getMsg(atleastselectonerow, new Object[0]);
            menuDataProps.append("'").append(IAMEncoder.encodeJavaScript(atleastselectonerow)).append("'");
        }
        menuDataProps.append("],");
        generateJSOptions(menuDataProps, menuItemObj);
        menuDataProps.append(")");
        menuDataProps.append(";");
        if (!isPreGenerated) {
            menuDataProps.append("</script>");
        }
        return menuDataProps.toString();
    }
    
    @Deprecated
    public static JSONObject generateMenuVariableJSON(final DataObject menuItemObj, final HttpServletRequest request, final boolean isPreGenerated) throws Exception {
        return generateMenuVariableJSON(menuItemObj, (ViewContext)request.getAttribute("VIEW_CTX"), isPreGenerated);
    }
    
    public static JSONObject generateMenuVariableJSON(final DataObject menuItemObj, final ViewContext viewContext, final boolean isPreGenerated) throws Exception {
        final String menuItemId = (String)menuItemObj.getFirstRow("MenuItem").get(2);
        final String ctxPath = viewContext.getContextPath();
        final JSONObject menuDataProps = new JSONObject();
        Row row = menuItemObj.getFirstRow("MenuItem");
        menuDataProps.put("menuItemId", (Object)menuItemId);
        String dispName = (String)row.get(4);
        dispName = I18N.getMsg(dispName, new Object[0]);
        final JSONObject dataProps = new JSONObject();
        dataProps.put("displayName", (Object)dispName);
        final String imageName = (String)row.get(6);
        if (imageName != null && imageName.contains("THEME_DIR")) {
            final String themeDir = ThemesAPI.getThemeDirForRequest(ctxPath);
            dataProps.put("imageName", (Object)imageName.replaceFirst("${THEME_DIR}", themeDir));
        }
        else if (imageName != null) {
            dataProps.put("imageName", (Object)(ctxPath + IAMEncoder.encodeJavaScript(imageName)));
        }
        final String cnfStr = (String)row.get(7);
        dataProps.put("confirmString", (Object)I18N.getMsg(cnfStr, new Object[0]));
        final String desc = (String)row.get(5);
        if (desc != null) {
            menuDataProps.put("setMenuDescription", (Object)IAMEncoder.encodeJavaScript(desc));
        }
        dataProps.put("actionLink", (Object)getActionLink(menuItemObj, viewContext));
        final String linkParams = getLinkParams(menuItemObj.getRows("ACParams"));
        menuDataProps.put("linkParams", (Object)IAMEncoder.encodeJavaScript(linkParams));
        dataProps.put("linkParams", (Object)linkParams);
        String atleastselectonerow = (String)row.get("ATLEASTSELECTONESTRING");
        row = menuItemObj.getRow("WebMenuItem");
        if (row != null) {
            dataProps.put("target", row.get(3));
            dataProps.put("winparams", row.get(4));
            dataProps.put("actionType", row.get(6));
        }
        row = menuItemObj.getRow("ACAjaxOptions");
        if (row != null) {
            dataProps.put("onSuccessFunc", row.get(2));
            dataProps.put("statusFunc", row.get(3));
            final Row row2 = menuItemObj.getRow("JavaScriptAction");
            String value = null;
            if (row2 != null) {
                value = (String)row2.get(3);
            }
            dataProps.put("jsMethodName", (Object)value);
        }
        if (atleastselectonerow != null) {
            atleastselectonerow = I18N.getMsg(atleastselectonerow, new Object[0]);
            dataProps.put("atleastSelectOneRow", (Object)atleastselectonerow);
        }
        menuDataProps.put("menuDataProps", (Object)dataProps);
        if (menuItemObj.containsTable("ACJSOption")) {
            final JSONObject acjsoption = new JSONObject();
            final Iterator ite = menuItemObj.getRows("ACJSOption");
            while (ite.hasNext()) {
                final Row r = ite.next();
                final String key = r.get(2).toString();
                acjsoption.put(key, r.get(3));
            }
            menuDataProps.put("jsOptions", (Object)acjsoption);
        }
        return menuDataProps;
    }
    
    protected static void generateJSOptions(final StringBuffer strBuf, final DataObject menuItemObj) throws Exception {
        strBuf.append("{");
        if (!menuItemObj.containsTable("ACJSOption")) {
            strBuf.append("}");
            return;
        }
        JSUtil.appendProperties(strBuf, menuItemObj.getRows("ACJSOption"), 2, 3);
        strBuf.append("}");
    }
    
    private static void generateArgument(final StringBuffer strBuf, final DataObject dao, final String tableName, final int colIndex) throws Exception {
        final Row row = dao.getRow(tableName);
        String value = null;
        if (row != null) {
            value = (String)row.get(colIndex);
        }
        StateUtils.generateArgument(strBuf, value, 2);
    }
    
    private static boolean toBeGenerated(Object menuItemId, final HttpServletRequest request) throws DataAccessException {
        List generatedMenuItems = (List)request.getAttribute("GeneratedMenuIds");
        if (generatedMenuItems == null) {
            generatedMenuItems = new ArrayList();
            request.setAttribute("GeneratedMenuIds", (Object)generatedMenuItems);
        }
        if (menuItemId instanceof Long) {
            menuItemId = getMenuItemID((Long)menuItemId);
        }
        return !generatedMenuItems.contains(menuItemId);
    }
    
    @Deprecated
    public static String getActionLink(final DataObject dataObject, final HttpServletRequest request) throws Exception {
        return getActionLink(dataObject, (ViewContext)request.getAttribute("VIEW_CTX"));
    }
    
    public static String getActionLink(final DataObject dataObject, final ViewContext viewContext) throws Exception {
        final String menuItemId = (String)dataObject.getFirstValue("MenuItem", 2);
        String actionLink = (String)dataObject.getValue("WebMenuItem", 2, (Row)null);
        if (actionLink == null) {
            final String extension = isNewAction(dataObject) ? ".mx" : (viewContext.isCSRComponent() ? ".ema" : ".ma");
            actionLink = menuItemId + extension;
        }
        return actionLink;
    }
    
    private static boolean isNewAction(final DataObject menuDO) throws DataAccessException, ClassNotFoundException {
        return menuDO.containsTable("ViewToOpen") || extendsAction(menuDO);
    }
    
    private static boolean extendsAction(final DataObject menuDO) throws DataAccessException, ClassNotFoundException {
        if (menuDO.containsTable("Action")) {
            final String actionClass = (String)menuDO.getFirstValue("Action", "TYPE");
            if (actionClass != null) {
                return Action.class.isAssignableFrom(WebClientUtil.loadClass(actionClass));
            }
        }
        return false;
    }
    
    public static String getLinkStringForView(final String menuItemName, final String viewUniqueId, final String additionalParams, final int rowIndex) throws DataAccessException {
        final DataObject obj = getCompleteMenuItemData(menuItemName);
        String jsMethodName = null;
        if (obj.containsTable("JavaScriptAction")) {
            jsMethodName = (String)obj.getFirstValue("JavaScriptAction", 3);
        }
        else {
            jsMethodName = "invokeMenuAction";
        }
        final String linkBuffer = "javascript:" + IAMEncoder.encodeJavaScript(jsMethodName) + "(" + "'" + IAMEncoder.encodeJavaScript(menuItemName) + "','" + IAMEncoder.encodeJavaScript(viewUniqueId) + "','" + IAMEncoder.encodeJavaScript(additionalParams) + "','" + rowIndex + "'" + ")";
        return linkBuffer;
    }
    
    public static String getInvokerLinkStringForMenu(final String referenceId, final Object columnName, final int rowIndex) throws DataAccessException {
        final String linkBuffer = "javascript:r" + referenceId + "c" + columnName + "(" + rowIndex + ",this" + ")";
        return linkBuffer;
    }
    
    public static String generateMenuInvokerFunction(final String menuItemName, final String referenceId, final String linkParams, final Object columnName) throws DataAccessException {
        final DataObject obj = getCompleteMenuItemData(menuItemName);
        String jsMethodName = (String)obj.getValue("JavaScriptAction", 3, (Row)null);
        if (jsMethodName == null) {
            jsMethodName = "invokeMenuAction";
        }
        return getViewFunction(referenceId, columnName, jsMethodName + "('" + IAMEncoder.encodeJavaScript(menuItemName) + "','" + IAMEncoder.encodeJavaScript(referenceId) + "','" + IAMEncoder.encodeJavaScript(linkParams) + "',index,el)");
    }
    
    public static String getViewFunction(final String referenceId, final Object columnName, final String functionString) {
        final String functionName = "r" + referenceId + "c" + columnName;
        final String linkBuffer = "<script>evalViewFun('" + IAMEncoder.encodeJavaScript(functionName) + "',\"" + functionString + "\")" + "</script>";
        return linkBuffer;
    }
    
    public static String getLinkParams(final Iterator<Row> paramDetails) throws Exception {
        final StringBuilder linkParams = new StringBuilder();
        while (paramDetails.hasNext()) {
            final Row currentRow = paramDetails.next();
            final String paramName = (String)currentRow.get("NAME");
            final String paramValue = (String)currentRow.get("VALUE");
            final String scope = (String)currentRow.get("SCOPE");
            if (linkParams.length() > 0) {
                linkParams.append("&");
            }
            linkParams.append(paramName);
            linkParams.append("=");
            if (!scope.equals("STATIC")) {
                linkParams.append("$").append(scope.charAt(0)).append("{").append(paramValue).append("}");
            }
            else {
                linkParams.append(paramValue);
            }
        }
        return linkParams.toString();
    }
    
    public static String getToolBarLinks(final ViewContext viewCtx, final HttpServletResponse response) throws Exception {
        final Row viewConfigRow = viewCtx.getModel().getViewConfiguration().getFirstRow("ViewConfiguration");
        final String menuID = (String)viewConfigRow.get(15);
        if (menuID == null) {
            return "";
        }
        final StringBuilder buffer = new StringBuilder();
        final DataObject menuObj = getCompleteMenuData(menuID);
        final String displayStyle = (String)menuObj.getFirstValue("Menu", 8);
        String templatename = "null";
        if (displayStyle != null) {
            templatename = (String)menuObj.getFirstValue("Menu", 9);
        }
        final HttpServletRequest request = viewCtx.getRequest();
        if (displayStyle == null || !displayStyle.contains("DROPDOWN")) {
            String displayType = (String)menuObj.getFirstValue("Menu", 7);
            if (displayType == null) {
                displayType = "IMAGE";
            }
            final String themeDir = ThemesAPI.getThemeDirForRequest(viewCtx.getRequest());
            final Iterator<Row> iter = menuObj.getRows("MenuAndMenuItem");
            while (iter.hasNext()) {
                final Row indRow = iter.next();
                final Long menuItemNameNo = (Long)indRow.get(2);
                DataObject menuItemDO = null;
                try {
                    menuItemDO = getCompleteMenuItemData(menuItemNameNo);
                }
                catch (final AuthorizationException ae) {
                    MenuVariablesGenerator.out.warning(ae.getMessage());
                    continue;
                }
                final String menuItemName = getMenuItemID(menuItemNameNo);
                final Row menuItemRow = menuItemDO.getFirstRow("MenuItem");
                final String imageCssClass = (String)menuItemRow.get("IMAGECSSCLASS");
                buffer.append(getScriptInclusion(menuItemName, request));
                buffer.append(generateMenuVariableScript(menuItemName, request));
                buffer.append("<a href=\"javascript:invokeMenuAction('").append(IAMEncoder.encodeJavaScript(menuItemName)).append("','").append(IAMEncoder.encodeJavaScript(viewCtx.getUniqueId())).append("', null)\">");
                if (menuItemRow.get(6) != null && ("IMAGE".equals(displayType) || "BOTH".equals(displayType))) {
                    final String imgSrc = (String)menuItemRow.get("IMAGE");
                    final String icon = ThemesAPI.handlePath(imgSrc, request, themeDir);
                    buffer.append("<img src='").append(IAMEncoder.encodeHTMLAttribute(icon)).append("'>");
                }
                else if (imageCssClass != null) {
                    buffer.append("<span class=\"").append(IAMEncoder.encodeHTMLAttribute(imageCssClass)).append("\"").append("</span>");
                }
                if (menuItemRow.get("DISPLAYNAME") != null && ("TEXT".equals(displayType) || "BOTH".equals(displayType))) {
                    final String displayname = (String)menuItemRow.get("DISPLAYNAME");
                    buffer.append(IAMEncoder.encodeHTML(I18N.getMsg(displayname, new Object[0])));
                }
                buffer.append("</a>");
            }
            return buffer.toString();
        }
        final RequestDispatcher rd = request.getRequestDispatcher("/framework/jsp/MenuDropDownGenerator.jsp");
        request.setAttribute("MENU_ID", (Object)menuID);
        request.setAttribute("MENU_ID_DISPLAYSTYLE", (Object)displayStyle);
        request.setAttribute("MENU_ID_TEMPLATENAME", (Object)templatename);
        request.setAttribute("VIEWUNIQUEID", (Object)viewCtx.getUniqueId());
        if (displayStyle.equals("TEMPLATEDROPDOWN") && templatename.equals("null")) {
            throw new Exception("Specify proper templatename for menu:" + menuID);
        }
        rd.include((ServletRequest)request, (ServletResponse)response);
        return getScriptAndMenuHolder(menuID);
    }
    
    public static HashMap<String, String> getMenuItemProps(final String menuName) throws Exception {
        final HashMap<String, String> hm = new HashMap<String, String>(75, 0.75f);
        final Persistence persistence = LookUpUtil.getPersistence();
        Column col = new Column("Menu", "MENUID");
        Criteria crit = new Criteria(col, (Object)menuName, 0);
        DataObject dobj = persistence.get("Menu", crit);
        final Row menuRow = dobj.getFirstRow("Menu");
        col = new Column("MenuAndMenuItem", "MENUID");
        crit = new Criteria(col, menuRow.get("MENUID_NO"), 0);
        dobj = persistence.get("MenuAndMenuItem", crit);
        final Iterator<Row> it = dobj.getRows("MenuAndMenuItem");
        while (it.hasNext()) {
            final Row row = it.next();
            final Integer MenuItemIndex = (Integer)row.get("MENUITEMINDEX");
            col = new Column("MenuItem", "MENUITEMID_NO");
            crit = new Criteria(col, row.get("MENUITEMID"), 0);
            dobj = persistence.get("MenuItem", crit);
            final Row menuItemRow = dobj.getFirstRow("MenuItem");
            final String menuItemName = (String)menuItemRow.get("MENUITEMID");
            hm.put(MenuItemIndex + "_MENUITEMID", menuItemName);
            hm.put(MenuItemIndex + "_DISPLAYNAME", (String)menuItemRow.get("DISPLAYNAME"));
            hm.put(MenuItemIndex + "_IMAGE", (String)menuItemRow.get("IMAGE"));
            hm.put(MenuItemIndex + "_IMAGECSSCLASS", (String)menuItemRow.get("IMAGECSSCLASS"));
        }
        return hm;
    }
    
    public static String getDropDownMenuActivatorHTML(final String menuId, final String menuTemplate) throws Exception {
        return TemplateAPI.givehtml(menuTemplate, null, new Object[][] { { "MENUID", menuId }, { "DISPLAYNAME", getMenuDisplayName(menuId) } });
    }
    
    public static String getDropDownMenuActivatorHTML(final String menuId, final String menuTemplate, final String displayName) throws Exception {
        return TemplateAPI.givehtml(menuTemplate, null, new Object[][] { { "MENUID", menuId }, { "DISPLAYNAME", displayName } });
    }
    
    public static String getScriptAndMenuHolder(String menuID) {
        final StringBuilder toolBarContent = new StringBuilder();
        toolBarContent.append("<div id='").append("dropDownMenu_shown_").append(IAMEncoder.encodeHTMLAttribute(menuID)).append("'></div>");
        menuID = IAMEncoder.encodeJavaScript(menuID);
        toolBarContent.append("<script> document.getElementById('dropDownMenu_shown_").append(menuID).append("')");
        toolBarContent.append(".innerHTML=").append("document.getElementById('dropDownMenu_hidden_").append(menuID).append("')");
        toolBarContent.append(".innerHTML;");
        toolBarContent.append("document.getElementById('dropDownMenu_hidden_").append(menuID).append("')");
        toolBarContent.append(".innerHTML='").append("';</script>");
        return toolBarContent.toString();
    }
    
    static {
        MenuVariablesGenerator.out = Logger.getLogger(MenuVariablesGenerator.class.getName());
    }
}
