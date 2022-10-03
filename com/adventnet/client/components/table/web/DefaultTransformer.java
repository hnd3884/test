package com.adventnet.client.components.table.web;

import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.client.components.web.SearchOperator;
import com.zoho.authentication.AuthenticationUtil;
import com.adventnet.client.view.web.StateAPI;
import com.adventnet.client.view.web.TabInformationAPI;
import com.adventnet.client.view.dynamiccontentarea.web.DynamicContentAreaAPI;
import com.adventnet.client.view.web.WebViewAPI;
import com.adventnet.iam.xss.IAMEncoder;
import com.adventnet.authorization.AuthorizationException;
import java.util.Iterator;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.components.action.web.MenuDataUtil;
import javax.servlet.jsp.PageContext;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.persistence.DataAccessException;
import java.util.Date;
import com.adventnet.client.action.web.MenuVariablesGenerator;
import com.adventnet.client.themes.web.ThemesAPI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.HashMap;
import com.adventnet.i18n.I18N;
import com.adventnet.client.components.web.TransformerContext;
import org.json.JSONObject;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;
import com.adventnet.client.action.web.MenuActionConstants;
import com.adventnet.client.util.web.JavaScriptConstants;

public class DefaultTransformer implements ColumnTransformer, JavaScriptConstants, MenuActionConstants
{
    protected static final SimpleDateFormat DEFAULTSTRINGTODATEFMT;
    private static Logger out;
    protected SimpleDateFormat sdf;
    protected String defaultText;
    protected String staticText;
    protected int trimLength;
    protected String trimMsgLink;
    protected String icon;
    protected String prefixText;
    protected String suffixIcon;
    protected String suffixText;
    protected boolean isIconDynamic;
    protected boolean isPrefixTextDynamic;
    protected boolean isSuffixIconDynamic;
    protected boolean isSuffixTextDynamic;
    protected String prefixLink;
    protected String suffixLink;
    protected boolean isSuffixLinkDynamic;
    protected boolean isPrefixLinkDynamic;
    protected boolean isViewNameDynamic;
    protected String replaceText;
    protected String replaceIcon;
    protected String actionName;
    protected String actionInvokingStyle;
    protected String viewName;
    protected String columnAlias;
    protected Row viewRow;
    protected String link;
    protected String menuID;
    protected DataObject menuObj;
    protected String themeDir;
    protected boolean autoLink;
    protected JSONObject columnMenuAsView;
    
    public DefaultTransformer() {
        this.sdf = null;
        this.defaultText = null;
        this.staticText = null;
        this.trimLength = 0;
        this.trimMsgLink = null;
        this.icon = null;
        this.prefixText = null;
        this.suffixIcon = null;
        this.suffixText = null;
        this.isIconDynamic = false;
        this.isPrefixTextDynamic = false;
        this.isSuffixIconDynamic = false;
        this.isSuffixTextDynamic = false;
        this.prefixLink = null;
        this.suffixLink = null;
        this.isSuffixLinkDynamic = false;
        this.isPrefixLinkDynamic = false;
        this.isViewNameDynamic = false;
        this.replaceText = null;
        this.replaceIcon = null;
        this.actionName = null;
        this.actionInvokingStyle = null;
        this.viewName = null;
        this.columnAlias = null;
        this.viewRow = null;
        this.link = null;
        this.menuID = null;
        this.menuObj = null;
        this.themeDir = null;
        this.autoLink = false;
        this.columnMenuAsView = null;
    }
    
    @Override
    public void renderCell(final TransformerContext tableContext) throws Exception {
        final HashMap<String, Object> columnProperties = tableContext.getRenderedAttributes();
        Object data = tableContext.getPropertyValue();
        final String iconCssClass = this.getRequiredRendererProps("ICONCSSCLASS", tableContext, false);
        if (iconCssClass != null && iconCssClass.length() > 0 && iconCssClass.charAt(0) == '_' && iconCssClass.charAt(1) == '$') {
            final String dynamicIcon = iconCssClass.substring(2, iconCssClass.length());
            this.setProperty("ICONCSSCLASS", dynamicIcon, true, tableContext);
        }
        else if (iconCssClass != null) {
            this.setProperty("ICONCSSCLASS", iconCssClass, false, tableContext);
        }
        final String suffixIconCssClass = this.getRequiredRendererProps("SUFFIXICONCSSCLASS", tableContext, false);
        if (suffixIconCssClass != null && suffixIconCssClass.length() > 0 && suffixIconCssClass.charAt(0) == '_' && suffixIconCssClass.charAt(1) == '$') {
            final String dynamicIcon2 = suffixIconCssClass.substring(2);
            this.setProperty("SUFFIXICONCSSCLASS", dynamicIcon2, true, tableContext);
        }
        else if (suffixIconCssClass != null) {
            this.setProperty("SUFFIXICONCSSCLASS", suffixIconCssClass, false, tableContext);
        }
        this.addToColumnProperties(columnProperties, "CSS", tableContext);
        this.addToColumnProperties(columnProperties, "PREFIX_TEXT_CSS", tableContext);
        this.addToColumnProperties(columnProperties, "SUFFIX_TEXT_CSS", tableContext);
        this.addToColumnProperties(columnProperties, "PREFIX_LINK_CSS", tableContext);
        this.addToColumnProperties(columnProperties, "SUFFIX_LINK_CSS", tableContext);
        this.addToColumnProperties(columnProperties, "PREFIX_LINK_HREF", tableContext);
        this.addToColumnProperties(columnProperties, "SUFFIX_LINK_HREF", tableContext);
        if (this.prefixLink != null) {
            this.setProperty("PREFIX_LINK", this.prefixLink, this.isPrefixLinkDynamic, tableContext);
        }
        if (this.suffixLink != null) {
            this.setProperty("SUFFIX_LINK", this.suffixLink, this.isSuffixLinkDynamic, tableContext);
        }
        final String menuPopup = this.getRequiredRendererProps("MENUPOPUP", tableContext, false);
        columnProperties.put("MENUPOPUP", (menuPopup != null) ? menuPopup : "false");
        final String tooltipName = this.getRequiredRendererProps("TOOLTIPNAME", tableContext, false);
        if (tooltipName != null) {
            columnProperties.put("TOOLTIPNAME", tooltipName);
        }
        else if (menuPopup != null) {
            columnProperties.put("TOOLTIPNAME", "MenuPopupTooltip");
        }
        else {
            columnProperties.put("TOOLTIPNAME", "BubbleTooltip");
        }
        this.addToColumnPropsWithDefault(columnProperties, "TOOLTIPVALUE", tableContext, "");
        if ("true".equals(menuPopup)) {
            this.addToColumnPropsWithDefault(columnProperties, "CSSCLASS_MENUPOPUPCOLUMN", tableContext, "menupopupimg");
            this.addToColumnPropsWithDefault(columnProperties, "POPUPWIDTH", tableContext, "130px");
            this.addToColumnPropsWithDefault(columnProperties, "OFFSETLEFT", tableContext, "0");
            this.addToColumnPropsWithDefault(columnProperties, "OFFSETTOP", tableContext, "0");
            this.addToColumnPropsWithDefault(columnProperties, "SHOWEVENT", tableContext, "mouseover");
            this.addToColumnPropsWithDefault(columnProperties, "HIDEEVENT", tableContext, "mouseover");
            this.addToColumnPropsWithDefault(columnProperties, "CONTENT_MENUPOPUPCOLUMN", tableContext, "");
        }
        if (data == null) {
            data = "";
        }
        if (this.icon != null) {
            this.setProperty("ICON", this.icon, this.isIconDynamic, tableContext);
        }
        if (this.suffixIcon != null) {
            this.setProperty("SUFFIX_ICON", this.suffixIcon, this.isSuffixIconDynamic, tableContext);
        }
        if (this.suffixText != null) {
            this.setProperty("SUFFIX_TEXT", this.suffixText, this.isSuffixTextDynamic, tableContext);
        }
        if (this.prefixText != null) {
            this.setProperty("PREFIX_TEXT", this.prefixText, this.isPrefixTextDynamic, tableContext);
        }
        if (this.staticText != null) {
            data = I18N.getMsg(this.staticText, new Object[0]);
        }
        if (this.replaceText != null) {
            data = this.getValue(this.replaceText, tableContext);
        }
        if (this.replaceIcon != null) {
            final String iconPath = this.replaceIcon.substring(2);
            this.setProperty("REPLACE_ICON", iconPath, true, tableContext);
        }
        if (this.actionName != null && data != null) {
            this.setLinkUsingActionName(tableContext);
        }
        if (this.viewName != null && data != null) {
            this.setLinkUsingViewName(tableContext);
        }
        if (this.menuID != null) {
            final String menuString = this.getLinksForMenu(tableContext);
            if (menuString != null) {
                data = menuString;
                columnProperties.put("ISMENUITEM", true);
            }
        }
        if (data == "") {
            data = this.getDefaultText();
            if (data != null) {
                if (!tableContext.getViewContext().isExportType()) {
                    final Object hldata = this.getHLSearchWrappedText(data, tableContext);
                    if (hldata instanceof HashMap) {
                        columnProperties.put("HLVALUE", hldata);
                    }
                    else {
                        data = hldata;
                    }
                }
                columnProperties.put("VALUE", data);
            }
            return;
        }
        if (this.sdf != null) {
            data = this.getFormatedDate(data);
        }
        if (this.autoLink) {
            columnProperties.put("AUTOLINK", "true");
            this.getLinkEnabled(data, columnProperties);
        }
        if (this.trimLength > 0) {
            data = this.getTrimmedData(data, columnProperties, tableContext);
        }
        final String replaceString = this.getRequiredRendererProps("REPLACE_TEXT", tableContext, false);
        if (replaceString != null) {
            final HashMap<String, String> replaceMap = this.getReplaceMap(replaceString);
            if (replaceMap.containsKey(String.valueOf(data))) {
                data = I18N.getMsg((String)replaceMap.get(String.valueOf(data)), new Object[0]);
            }
        }
        final String replaceIcon = this.getRequiredRendererProps("REPLACE_ICON", tableContext, false);
        if (replaceIcon != null) {
            final HashMap<String, String> replaceMap2 = this.getReplaceMap(replaceIcon);
            if (replaceMap2.containsKey(String.valueOf(data))) {
                columnProperties.put("REPLACE_ICON", replaceMap2.get(String.valueOf(data)));
            }
        }
        if (!tableContext.getViewContext().isExportType()) {
            final Object hldata2 = this.getHLSearchWrappedText(data, tableContext);
            if (hldata2 instanceof HashMap) {
                columnProperties.put("HLVALUE", hldata2);
            }
            else {
                data = hldata2;
            }
        }
        columnProperties.put("VALUE", String.valueOf(data));
    }
    
    private void addToColumnProperties(final HashMap<String, Object> columnProperties, final String property, final TransformerContext context) {
        final String value = this.getRequiredRendererProps(property, context, false);
        if (value != null) {
            columnProperties.put(property, value);
        }
    }
    
    private void addToColumnPropsWithDefault(final HashMap<String, Object> columnProperties, final String property, final TransformerContext context, final String defValue) {
        final String value = this.getRequiredRendererProps(property, context, false);
        columnProperties.put(property, (value != null) ? value : defValue);
    }
    
    private Object getHLSearchWrappedText(final Object data, final TransformerContext context) throws Exception {
        final String hlsearch = this.getRequiredRendererProps("hlsearch", context, false);
        if ((hlsearch != null && hlsearch.equals("true")) || (context instanceof TableTransformerContext && ((TableTransformerContext)context).isHlSearch())) {
            String searchval = ((TableTransformerContext)context).getSearchValue();
            if (searchval != null && !searchval.trim().equals("")) {
                final String datavalue = String.valueOf(data);
                final Matcher match = Pattern.compile(Pattern.quote(searchval), 2).matcher(datavalue);
                if (match.find()) {
                    final HashMap<String, String> hlsearchMap = new HashMap<String, String>();
                    final int start = match.start();
                    final int end = match.end();
                    match.reset();
                    final String prefix = datavalue.substring(0, start);
                    final String suffix = datavalue.substring(end);
                    searchval = datavalue.substring(start, end);
                    hlsearchMap.put("prefix", prefix);
                    hlsearchMap.put("suffix", suffix);
                    hlsearchMap.put("searchval", searchval);
                    return hlsearchMap;
                }
            }
        }
        return data;
    }
    
    @Override
    public void renderHeader(final TransformerContext tableContext) {
        final HashMap<String, Object> headerProperties = tableContext.getRenderedAttributes();
        headerProperties.put("VALUE", tableContext.getDisplayName());
    }
    
    @Override
    public void initCellRendering(final TransformerContext context) throws Exception {
        final DataObject columnConfig = context.getColumnConfiguration();
        final Row columnConfigRow = columnConfig.getFirstRow("ACColumnConfiguration");
        if (columnConfigRow.get(12) != null) {
            this.sdf = new SimpleDateFormat((String)columnConfigRow.get(12), I18N.getLocale());
        }
        this.defaultText = I18N.getMsg((String)columnConfigRow.get(11), new Object[0]);
        this.staticText = (String)columnConfigRow.get(21);
        if (columnConfigRow.get(13) != null) {
            this.trimLength = (int)columnConfigRow.get(13);
            this.trimMsgLink = (String)columnConfigRow.get(14);
        }
        this.icon = (String)columnConfigRow.get(18);
        if (this.icon != null && this.icon.length() > 0 && this.icon.charAt(0) == '_' && this.icon.charAt(1) == '$') {
            this.isIconDynamic = true;
            this.icon = this.icon.substring(2);
        }
        this.prefixText = (String)columnConfigRow.get(15);
        if (this.prefixText != null && this.prefixText.length() > 0 && this.prefixText.charAt(0) == '_' && this.prefixText.charAt(1) == '$') {
            this.isPrefixTextDynamic = true;
            this.prefixText = this.prefixText.substring(2);
        }
        this.suffixText = (String)columnConfigRow.get(16);
        if (this.suffixText != null && this.suffixText.length() > 0 && this.suffixText.charAt(0) == '_' && this.suffixText.charAt(1) == '$') {
            this.isSuffixTextDynamic = true;
            this.suffixText = this.suffixText.substring(2);
        }
        this.suffixIcon = (String)columnConfigRow.get(17);
        if (this.suffixIcon != null && this.suffixIcon.length() > 0 && this.suffixIcon.charAt(0) == '_' && this.suffixIcon.charAt(1) == '$') {
            this.isSuffixIconDynamic = true;
            this.suffixIcon = this.suffixIcon.substring(2);
        }
        this.viewName = (String)columnConfigRow.get(20);
        if (this.viewName != null && this.viewName.charAt(0) == '_' && this.viewName.charAt(1) == '$') {
            this.isViewNameDynamic = true;
            this.viewName = this.viewName.substring(2);
        }
        this.replaceText = (String)columnConfigRow.get(27);
        this.replaceIcon = (String)columnConfigRow.get(28);
        this.actionName = (String)columnConfigRow.get(8);
        this.actionInvokingStyle = (String)columnConfigRow.get(30);
        final boolean isCSRComponent = context.getViewContext().isCSRComponent();
        if (this.actionName != null && !isCSRComponent) {
            this.initLinkForActionName(context, columnConfigRow);
        }
        if (this.viewName != null) {
            if (isCSRComponent) {
                this.columnMenuAsView = this.getLinkParamsAsJson(context);
            }
            else {
                this.initLinkForViewName(context);
            }
        }
        if (columnConfigRow.get("MENUID") != null) {
            this.menuID = (String)columnConfigRow.get("MENUID");
            if (!isCSRComponent) {
                this.initLinksForMenu(context);
            }
        }
        this.themeDir = ThemesAPI.getThemeDirForRequest(context.getViewContext().getContextPath());
        if (columnConfigRow.get(26) != null) {
            this.autoLink = (boolean)columnConfigRow.get(26);
        }
        if (columnConfigRow.get("PREFIX_LINK") != null) {
            this.prefixLink = (String)columnConfigRow.get("PREFIX_LINK");
            if (this.prefixLink.length() > 0 && this.prefixLink.charAt(0) == '_' && this.prefixLink.charAt(1) == '$') {
                this.isPrefixLinkDynamic = true;
                this.prefixLink = this.prefixLink.substring(2);
            }
        }
        if (columnConfigRow.get("SUFFIX_LINK") != null) {
            this.suffixLink = (String)columnConfigRow.get("SUFFIX_LINK");
            if (this.suffixLink.length() > 0 && this.suffixLink.charAt(0) == '_' && this.suffixLink.charAt(1) == '$') {
                this.isSuffixLinkDynamic = true;
                this.suffixLink = this.suffixLink.substring(2);
            }
        }
    }
    
    private JSONObject getLinkParamsAsJson(final TransformerContext context) throws Exception {
        final DataObject viewModel = context.getViewContext().getModel().getViewConfiguration();
        final String contentAreaName = (String)viewModel.getValue("UINavigationConfig", 2, (Row)null);
        final JSONObject viewLinkParams = new JSONObject();
        final DataObject columnConfig = context.getColumnConfiguration();
        final String linkParams = MenuVariablesGenerator.getLinkParams(columnConfig.getRows("ACLinkParams"));
        viewLinkParams.put("viewToOpen", (Object)this.viewName);
        viewLinkParams.put("id", (Object)context.getViewContext().getUniqueId());
        viewLinkParams.put("linkParams", (Object)linkParams);
        viewLinkParams.put("contentAreaName", (Object)contentAreaName);
        viewLinkParams.put("index", (Object)(context.getRowIndex() + ""));
        return viewLinkParams;
    }
    
    protected String getRequiredRendererProps(final String propName, final TransformerContext context) {
        return this.getRequiredRendererProps(propName, context, true);
    }
    
    protected String getRequiredRendererProps(final String propName, final TransformerContext context, final boolean throwerror) {
        final HashMap<String, String> propHash = context.getRendererConfigProps();
        String propValue = null;
        if (propHash != null) {
            propValue = propHash.get(propName);
        }
        if (propValue == null && throwerror) {
            throw new RuntimeException("The required renderer config " + propName + " not configured.");
        }
        return propValue;
    }
    
    private String getDefaultText() {
        if (this.defaultText != null) {
            return this.defaultText;
        }
        return null;
    }
    
    private String getFormatedDate(final Object data) {
        Date date = null;
        if (data instanceof Date) {
            date = (Date)data;
        }
        else {
            if (!(data instanceof Long)) {
                if (data instanceof String) {
                    try {
                        date = DefaultTransformer.DEFAULTSTRINGTODATEFMT.parse((String)data);
                        return this.sdf.format(date);
                    }
                    catch (final Exception e) {
                        return this.getDefaultText();
                    }
                }
                return this.getDefaultText();
            }
            final long time = (long)data;
            if (time <= 0L) {
                return this.getDefaultText();
            }
            date = new Date(time);
        }
        return this.sdf.format(date);
    }
    
    private String getTrimmedData(final Object data, final Map<String, Object> columnProperties, final TransformerContext transformerContext) throws DataAccessException {
        String trimmedContent;
        final String actualVal = trimmedContent = String.valueOf(data);
        if (trimmedContent.length() > this.trimLength) {
            trimmedContent = actualVal.substring(0, this.trimLength) + " ...";
            if (this.trimMsgLink != null) {
                columnProperties.put("ACTUAL_VALUE", actualVal);
                columnProperties.put("MESSAGE_DISPLAYER", this.trimMsgLink);
                columnProperties.put("TRIMMED_VALUE", trimmedContent);
                if (columnProperties.containsKey("LINK")) {
                    columnProperties.put("ACTION_LINK", columnProperties.remove("LINK"));
                }
                return "";
            }
        }
        return trimmedContent;
    }
    
    private void setProperty(final String key, final String valueKey, final boolean isDynamic, final TransformerContext tableContext) {
        if (isDynamic) {
            tableContext.getRenderedAttributes().put(key, tableContext.getAssociatedPropertyValue(valueKey));
        }
        else {
            tableContext.getRenderedAttributes().put(key, valueKey);
        }
    }
    
    private String getValue(final String valueKey, final TransformerContext tableContext) {
        final Object value = tableContext.getAssociatedPropertyValue(valueKey.substring(2, valueKey.length()));
        return (String)value;
    }
    
    protected void initLinkForActionName(final TransformerContext tableContext, final Row columnConfigRow) throws Exception {
        this.actionName = (String)columnConfigRow.get(8);
        if (this.actionName == null) {
            return;
        }
        final PageContext pageContext = tableContext.getPageContext();
        if (WebClientUtil.isMenuItemAuthorized(this.actionName)) {
            final DataObject columnConfig = tableContext.getColumnConfiguration();
            this.columnAlias = (String)columnConfig.getFirstValue("ACColumnConfiguration", 3);
            if (pageContext != null) {
                pageContext.getOut().println(MenuVariablesGenerator.getScriptInclusion((Object)this.actionName, (HttpServletRequest)pageContext.getRequest()));
                pageContext.getOut().println(MenuVariablesGenerator.generateMenuVariableScript((Object)this.actionName, (HttpServletRequest)pageContext.getRequest()));
                String linkParams = null;
                if (columnConfig.containsTable("ACLinkParams")) {
                    linkParams = MenuVariablesGenerator.getLinkParams(columnConfig.getRows("ACLinkParams"));
                }
                if (tableContext.getColumnIndex() >= 0) {
                    pageContext.getOut().println(MenuVariablesGenerator.generateMenuInvokerFunction(this.actionName, tableContext.getViewContext().getReferenceId(), linkParams, (Object)(tableContext.getColumnIndex() + "")));
                }
                else {
                    pageContext.getOut().println(MenuVariablesGenerator.generateMenuInvokerFunction(this.actionName, tableContext.getViewContext().getReferenceId(), linkParams, (Object)this.columnAlias));
                }
            }
        }
    }
    
    protected void setLinkUsingActionName(final TransformerContext tableContext) throws Exception {
        if (tableContext.getViewContext().isCSRComponent()) {
            final JSONObject actionJson = MenuDataUtil.getMenuItemAsJson(this.actionName, tableContext);
            if (actionJson != null) {
                actionJson.put("invokeStyle", (Object)this.actionInvokingStyle);
                tableContext.getRenderedAttributes().put("ACTION", actionJson);
            }
            return;
        }
        if (WebClientUtil.isMenuItemAuthorized(this.actionName)) {
            final int rowIdx = tableContext.getRowIndex();
            final String refId = tableContext.getViewContext().getReferenceId();
            tableContext.getRenderedAttributes().put("LINK_ID", tableContext.getViewContext().getUniqueId() + '_' + tableContext.getDisplayName() + '_' + rowIdx);
            String temp;
            if (this.actionInvokingStyle == null) {
                temp = "LINK";
            }
            else {
                temp = "INVOKE";
                tableContext.getRenderedAttributes().put("INVOKESTYLE", this.actionInvokingStyle);
            }
            if (tableContext.getColumnIndex() >= 0) {
                tableContext.getRenderedAttributes().put(temp, getInvokerLinkStringForMenu(refId, tableContext.getColumnIndex() + "", rowIdx, tableContext));
            }
            else {
                tableContext.getRenderedAttributes().put(temp, getInvokerLinkStringForMenu(refId, this.columnAlias, rowIdx, tableContext));
            }
        }
    }
    
    protected void setLinkUsingViewName(final TransformerContext context) throws Exception {
        if (context.getViewContext().isCSRComponent()) {
            context.getRenderedAttributes().put("VIEW", this.updateIndexInLinkParams(context.getRowIndex()));
            return;
        }
        final int rowIdx = context.getRowIndex();
        if (this.isViewNameDynamic) {
            context.getRenderedAttributes().put("LINK", this.link + rowIdx + ",'" + context.getAssociatedPropertyValue(this.viewName) + "')");
        }
        else {
            try {
                ViewContext.getViewContext((Object)this.viewName, (Object)this.viewName, context.getRequest());
                if (!WebClientUtil.isRestful(context.getViewContext().getRequest())) {
                    context.getRenderedAttributes().put("LINK", this.link + rowIdx + ")");
                }
                else {
                    final DataObject columnConfig = context.getColumnConfiguration();
                    String tmplink = null;
                    if (this.link.indexOf(63) != -1) {
                        tmplink = this.link + "&";
                    }
                    else {
                        tmplink = this.link + "?";
                    }
                    tmplink += getLinkParams(columnConfig.getRows("ACLinkParams"), context);
                    context.getRenderedAttributes().put("LINK", tmplink);
                }
            }
            catch (final AuthorizationException ae) {
                DefaultTransformer.out.finer("User is not allowed to view " + this.viewName);
            }
        }
    }
    
    private JSONObject updateIndexInLinkParams(final int index) throws Exception {
        final JSONObject params = new JSONObject();
        if (this.columnMenuAsView != null) {
            params.put("viewToOpen", this.columnMenuAsView.get("viewToOpen"));
            params.put("id", this.columnMenuAsView.get("id"));
            params.put("linkParams", this.columnMenuAsView.get("linkParams"));
            params.put("index", index);
        }
        return params;
    }
    
    public void initLinkForViewName(final TransformerContext context) throws Exception {
        if (!this.isViewNameDynamic) {
            try {
                ViewContext.getViewContext((Object)this.viewName, (Object)this.viewName, context.getRequest());
            }
            catch (final AuthorizationException ae) {
                DefaultTransformer.out.finer("User is not allowed to view " + this.viewName);
                return;
            }
        }
        String contentAreaName = null;
        final DataObject viewModel = context.getViewContext().getModel().getViewConfiguration();
        if (viewModel.containsTable("UINavigationConfig")) {
            final Row uiNavConfig = viewModel.getFirstRow("UINavigationConfig");
            contentAreaName = (String)uiNavConfig.get(2);
        }
        final String referenceId = context.getViewContext().getReferenceId();
        final StringBuffer function = new StringBuffer();
        String name = null;
        if (this.isViewNameDynamic) {
            name = "viewName";
        }
        else {
            name = "'" + IAMEncoder.encodeJavaScript(this.viewName) + "'";
        }
        final DataObject columnConfig = context.getColumnConfiguration();
        String linkParams = null;
        if (columnConfig.containsTable("ACLinkParams")) {
            linkParams = MenuVariablesGenerator.getLinkParams(columnConfig.getRows("ACLinkParams"));
        }
        function.append("addViewToCA(").append(name).append(",").append(IAMEncoder.encodeJavaScript(referenceId)).append(",'").append(IAMEncoder.encodeJavaScript(linkParams)).append("',");
        if (contentAreaName != null) {
            function.append("'").append(IAMEncoder.encodeJavaScript(contentAreaName)).append("',");
        }
        else {
            function.append("null,");
        }
        function.append("index);");
        String functionScript = null;
        this.columnAlias = (String)columnConfig.getFirstValue("ACColumnConfiguration", 3);
        if (!WebClientUtil.isRestful(context.getRequest())) {
            if (context.getColumnIndex() >= 0) {
                functionScript = MenuVariablesGenerator.getViewFunction(referenceId, (Object)String.valueOf(context.getColumnIndex()), function.toString());
                this.link = "javascript:r" + referenceId + "c" + context.getColumnIndex() + "(";
            }
            else {
                functionScript = MenuVariablesGenerator.getViewFunction(referenceId, (Object)this.columnAlias, function.toString());
                this.link = "javascript:r" + referenceId + "c" + this.columnAlias + "(";
            }
            final PageContext pageContext = context.getPageContext();
            if (pageContext != null) {
                pageContext.getOut().println(functionScript);
            }
        }
        else {
            final ViewContext vc = context.getViewContext();
            String rootview = WebViewAPI.getRootViewContext(vc.getRequest()).toString();
            if (WebViewAPI.isAjaxRequest(vc.getRequest())) {
                rootview = (String)vc.getURLStateParameter("rootview");
            }
            if (contentAreaName == null) {
                contentAreaName = DynamicContentAreaAPI.getContentAreaFromState(vc, vc.getRequest());
            }
            if (contentAreaName == null) {
                contentAreaName = rootview + "_CONTENTAREA";
            }
            this.link = context.getRequest().getContextPath() + "/view/" + rootview + TabInformationAPI.getTabsInfoAsURL(rootview, vc.getRequest(), contentAreaName) + "/" + this.viewName;
            this.link = this.link + "?" + TabInformationAPI.getDACListParams(contentAreaName, vc.getRequest());
        }
    }
    
    private static String getLinkParams(final Iterator<Row> paramDetails, final TransformerContext context) {
        final StringBuilder linkParams = new StringBuilder();
        while (paramDetails.hasNext()) {
            final Row currentRow = paramDetails.next();
            final String paramName = (String)currentRow.get("NAME");
            final String paramValue = (String)currentRow.get("VALUE");
            final String scope = (String)currentRow.get("SCOPE");
            linkParams.append(paramName);
            linkParams.append("=");
            if (scope.equals("STATIC")) {
                linkParams.append(paramValue);
                linkParams.append("&");
            }
            if (scope.equals("REQUEST")) {
                linkParams.append(StateAPI.getRequestState(paramValue));
                linkParams.append("&");
            }
            if (scope.equals("DATAMODEL")) {
                linkParams.append(context.getAssociatedPropertyValue(paramValue, true));
                linkParams.append("&");
            }
            if (scope.equals("STATE")) {
                linkParams.append(StateAPI.getState(context.getViewContext().toString(), paramValue));
                linkParams.append("&");
            }
        }
        return linkParams.toString();
    }
    
    public void initLinksForMenu(final TransformerContext tableContext) throws Exception {
        final PageContext pageContext = tableContext.getPageContext();
        this.menuObj = MenuVariablesGenerator.getCompleteMenuData((Object)this.menuID);
        final Iterator<Row> iter = this.menuObj.getRows("MenuAndMenuItem");
        final DataObject columnConfig = tableContext.getColumnConfiguration();
        final String linkParams = MenuVariablesGenerator.getLinkParams(columnConfig.getRows("ACLinkParams"));
        int i = 0;
        while (iter.hasNext()) {
            final Row indRow = iter.next();
            final Long menuItemID_NO = (Long)indRow.get(2);
            final String menuItemName = MenuVariablesGenerator.getMenuItemID(menuItemID_NO);
            if (WebClientUtil.isMenuItemAuthorized(menuItemName)) {
                this.columnAlias = (String)columnConfig.getFirstValue("ACColumnConfiguration", 3);
                if (pageContext != null) {
                    pageContext.getOut().println(MenuVariablesGenerator.getScriptInclusion((Object)menuItemName, (HttpServletRequest)pageContext.getRequest()));
                    pageContext.getOut().println(MenuVariablesGenerator.generateMenuVariableScript((Object)menuItemName, (HttpServletRequest)pageContext.getRequest()));
                    if (tableContext.getColumnIndex() >= 0) {
                        pageContext.getOut().println(MenuVariablesGenerator.generateMenuInvokerFunction(menuItemName, tableContext.getViewContext().getReferenceId(), linkParams, (Object)(tableContext.getColumnIndex() + "_" + i)));
                    }
                    else {
                        pageContext.getOut().println(MenuVariablesGenerator.generateMenuInvokerFunction(menuItemName, tableContext.getViewContext().getReferenceId(), linkParams, (Object)(this.columnAlias + "_" + i)));
                    }
                }
            }
            ++i;
        }
    }
    
    public String getLinksForMenu(final TransformerContext tableContext) throws Exception {
        if (tableContext.getViewContext().isCSRComponent()) {
            tableContext.getRenderedAttributes().put("MENU", MenuDataUtil.getMenuData(this.menuID, tableContext));
            return null;
        }
        String displayType = (String)this.menuObj.getFirstValue("Menu", 7);
        if (displayType == null) {
            displayType = "IMAGE";
        }
        final StringBuilder buffer = new StringBuilder();
        final Iterator<Row> iter = this.menuObj.getRows("MenuAndMenuItem");
        int i = 0;
        int popupwidth = 0;
        while (iter.hasNext()) {
            final Row indRow = iter.next();
            final Long menuItemID_NO = (Long)indRow.get(2);
            DataObject menuItemDO = null;
            try {
                menuItemDO = MenuVariablesGenerator.getCompleteMenuItemData((Object)menuItemID_NO);
            }
            catch (final AuthorizationException ae) {
                DefaultTransformer.out.warning(ae.getMessage());
                ++i;
                continue;
            }
            final Row menuItemRow = menuItemDO.getFirstRow("MenuItem");
            final int rowIdx = tableContext.getRowIndex();
            final String refId = tableContext.getViewContext().getReferenceId();
            String linkString = null;
            String colInd = tableContext.getColumnIndex() + "";
            if (tableContext.getColumnIndex() < 0) {
                colInd = this.columnAlias;
            }
            if (menuItemRow.get("LINKOPTION").equals("LINK")) {
                linkString = getMenuItemLink(menuItemRow.get(2).toString(), tableContext.getViewContext().getUniqueId(), colInd, tableContext);
            }
            else {
                linkString = getInvokerLinkStringForMenu(refId, colInd + "_" + i, rowIdx, tableContext, true);
            }
            if (tableContext.getPageContext() != null) {
                final String imageStr = (String)menuItemRow.get(6);
                final String icon = ThemesAPI.handlePath(imageStr, ((HttpServletRequest)tableContext.getPageContext().getRequest()).getContextPath(), this.themeDir);
                buffer.append("<a href=\"").append(IAMEncoder.encodeHTMLAttribute(linkString)).append("\">");
                String dispName = (String)menuItemRow.get(4);
                String title = (String)menuItemRow.get(10);
                title = ((title != null) ? I18N.getMsg(title, new Object[0]) : I18N.getMsg(dispName, new Object[0]));
                dispName = I18N.getMsg(dispName, new Object[0]);
                final String imageCss = (String)menuItemRow.get("IMAGECSSCLASS");
                int mpopupwidth = 0;
                if (imageStr != null && ("IMAGE".equals(displayType) || "BOTH".equals(displayType))) {
                    buffer.append("<img src='").append(IAMEncoder.encodeHTMLAttribute(icon)).append("' title='").append(IAMEncoder.encodeHTMLAttribute(title)).append("' class='menuItemImage'>");
                    mpopupwidth += 16;
                }
                else if (imageCss != null) {
                    buffer.append("<span  title=\"").append(IAMEncoder.encodeHTMLAttribute(title)).append("\"").append("  class=").append("\"").append(IAMEncoder.encodeHTMLAttribute(imageCss)).append("\"").append("></span>");
                    mpopupwidth += 16;
                }
                if (dispName != null && ("TEXT".equals(displayType) || "BOTH".equals(displayType))) {
                    buffer.append(IAMEncoder.encodeHTML(dispName));
                    mpopupwidth += dispName.length() * 10;
                }
                if (mpopupwidth > popupwidth) {
                    popupwidth = mpopupwidth;
                }
                buffer.append("</a>&nbsp;");
            }
            ++i;
        }
        tableContext.getRenderedAttributes().put("POPUPWIDTH", popupwidth);
        return buffer.toString();
    }
    
    @Override
    public boolean canRenderColumn(final TransformerContext tableContext) throws Exception {
        return this.checkIfColumnRendererable(tableContext);
    }
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final DataObject dob = tableContext.getColumnConfiguration();
        final String vname = (String)dob.getFirstValue("ACColumnConfiguration", 20);
        final String aname = (String)dob.getFirstValue("ACColumnConfiguration", 8);
        if (tableContext.getColumnIndex() == -1) {
            if (vname != null && !vname.startsWith("_$")) {
                try {
                    ViewContext.getViewContext((Object)vname, (Object)vname, tableContext.getViewContext().getRequest());
                }
                catch (final AuthorizationException ae) {
                    DefaultTransformer.out.finer("User is not allowed to view " + vname);
                    return false;
                }
            }
            if (aname != null && !aname.startsWith("_$") && !WebClientUtil.isMenuItemAuthorized(aname)) {
                return false;
            }
        }
        if (dob.getFirstValue("ACColumnConfiguration", 31) != null) {
            final String rolename = (String)dob.getFirstValue("ACColumnConfiguration", 31);
            return WebClientUtil.isNewAuthPropertySet() ? AuthenticationUtil.isUserExists(rolename) : WebClientUtil.getAuthImpl().userExists(rolename);
        }
        return true;
    }
    
    public void getLinkEnabled(final Object data, final HashMap<String, Object> columnProperties) {
        String content = data.toString();
        if (content.contains("www.") && !content.contains("http://")) {
            content = "http://" + content;
        }
        columnProperties.put("LINK", content);
    }
    
    private HashMap<String, String> getReplaceMap(String replaceString) {
        final HashMap<String, String> hm = new HashMap<String, String>(100, 0.75f);
        while (replaceString.contains("_$")) {
            final int index = replaceString.indexOf("_$");
            if ((replaceString.charAt(index + 2) + "").equals(",")) {
                replaceString = replaceString.substring(0, index) + "%=comma=%" + replaceString.substring(index + 3);
            }
            else if ((replaceString.charAt(index + 2) + "").equals("=")) {
                replaceString = replaceString.substring(0, index) + "%=equalto=%" + replaceString.substring(index + 3);
            }
            else {
                replaceString = replaceString.substring(0, index) + "" + replaceString.substring(index + 2);
            }
        }
        while (replaceString.length() != 0 && replaceString.contains(",")) {
            final String exp = replaceString.substring(0, replaceString.indexOf(","));
            final int index2 = exp.indexOf("=");
            if (index2 != -1) {
                final String key = exp.substring(0, index2);
                final String value = exp.substring(index2 + 1);
                hm.put(key, this.replaceCommaAndEquals(value));
            }
            replaceString = replaceString.substring(replaceString.indexOf(",") + 1);
        }
        if (replaceString.length() != 0) {
            final int index = replaceString.indexOf("=");
            if (index != -1) {
                final String key2 = replaceString.substring(0, index);
                final String value2 = replaceString.substring(index + 1);
                hm.put(key2, value2);
            }
        }
        return hm;
    }
    
    private String replaceCommaAndEquals(final String value) {
        return value.replaceAll("%=comma=%", "").replaceAll("%=equalto=%", "");
    }
    
    public static String getInvokerLinkStringForMenu(final String referenceId, final Object columnIndex, final int rowIndex) throws Exception {
        return getInvokerLinkStringForMenu(referenceId, columnIndex, rowIndex, null);
    }
    
    public static String getInvokerLinkStringForMenu(final String referenceId, final Object columnIndex, final int rowIndex, final TransformerContext context) throws Exception {
        return getInvokerLinkStringForMenu(referenceId, columnIndex, rowIndex, context, false);
    }
    
    public static String getInvokerLinkStringForMenu(final String referenceId, final Object columnIndex, final int rowIndex, final TransformerContext context, final boolean isJavascript) throws Exception {
        if (!isJavascript && context != null && WebClientUtil.isRestful(context.getRequest()) && isMenuItemALink(referenceId, columnIndex.toString(), rowIndex, context)) {
            return getMenuItemLinkForCell(referenceId, columnIndex.toString(), rowIndex, context);
        }
        return MenuVariablesGenerator.getInvokerLinkStringForMenu(referenceId, columnIndex, rowIndex);
    }
    
    public static boolean isMenuItemALink(final String referenceId, final String columnIndex, final int rowIndex, final TransformerContext context) throws DataAccessException {
        final DataObject columnConfig = context.getColumnConfiguration();
        final Row columnConfigRow = columnConfig.getFirstRow("ACColumnConfiguration");
        final String menuItemName = (String)columnConfigRow.get(8);
        final DataObject menuItemDataObject = MenuVariablesGenerator.getCompleteMenuItemData((Object)menuItemName);
        final Row menuItemRow = menuItemDataObject.getFirstRow("MenuItem");
        final String linkOption = (String)menuItemRow.get("LINKOPTION");
        return "LINK".equals(linkOption);
    }
    
    public static String getMenuItemLinkForCell(final String referenceId, final String columnIndex, final int rowIndex, final TransformerContext context) throws Exception {
        final DataObject columnConfig = context.getColumnConfiguration();
        final Row columnConfigRow = columnConfig.getFirstRow("ACColumnConfiguration");
        final String menuItemName = (String)columnConfigRow.get(8);
        return getMenuItemLink(menuItemName, context.getViewContext().getUniqueId(), Integer.toString(rowIndex), context);
    }
    
    public static String getMenuItemLink(final String menuItemName, final String sourceView, final String index, final TransformerContext context) throws Exception {
        final DataObject menuItemDataObject = MenuVariablesGenerator.getCompleteMenuItemData((Object)menuItemName);
        StringBuffer url = new StringBuffer("/" + System.getProperty("contextDIR") + "/STATE_ID/" + MenuVariablesGenerator.getActionLink(menuItemDataObject, context.getRequest()));
        if (WebClientUtil.isRestful(context.getRequest())) {
            url = new StringBuffer("/" + MenuVariablesGenerator.getActionLink(menuItemDataObject, context.getRequest()));
        }
        if (menuItemDataObject.containsTable("ACParams")) {
            if (!url.toString().contains("?")) {
                url.append("?");
            }
            else {
                url.append("&");
            }
            url.append(getLinkParams(menuItemDataObject.getRows("ACParams"), context));
        }
        if (menuItemDataObject.containsTable("ACLinkParams")) {
            if (!url.toString().contains("?")) {
                url.append("?");
            }
            else {
                url.append("&");
            }
            url.append(getLinkParams(menuItemDataObject.getRows("ACLinkParams"), context));
        }
        return url.toString();
    }
    
    @Override
    public String formatSumValue(final String viewname, final String columnAlias, final String value, final boolean totalSum) throws Exception {
        return value;
    }
    
    @Override
    public SearchOperator[] getSearchOperators(final TransformerContext context) throws Exception {
        final String upperCase;
        final String type = upperCase = ((TableTransformerContext)context).getSQLDataType().toUpperCase();
        switch (upperCase) {
            case "INTEGER":
            case "TINYINT":
            case "BIGINT":
            case "SMALLINT":
            case "FLOAT":
            case "DOUBLE":
            case "DECIMAL": {
                return SearchOperator.NUMERIC_SEARCH_OPEARTORS;
            }
            case "BOOLEAN":
            case "BIT": {
                return SearchOperator.BOOLEAN_SEARCH_OPERATORS;
            }
            case "DATE":
            case "TIME":
            case "TIMESTAMP":
            case "DATETIME": {
                return SearchOperator.DATE_SEARCH_OPERATORS;
            }
            default: {
                return SearchOperator.STRING_SEARCH_OPERATORS;
            }
        }
    }
    
    @Override
    public Criteria formCriteria(final Column column, final String value, final int compare, final int columnType) throws Exception {
        Criteria crit = new Criteria(column, (Object)value, compare, false);
        switch (compare) {
            case 16: {
                crit = new Criteria(column, (Object)null, 0, false);
                break;
            }
            case 17: {
                crit = new Criteria(column, (Object)null, 1, false);
                break;
            }
            case 18: {
                crit = new Criteria(column, (Object)"true", 0, true);
                break;
            }
            case 19: {
                crit = new Criteria(column, (Object)"false", 0, true);
                break;
            }
            case 25: {
                crit = null;
                break;
            }
        }
        return crit;
    }
    
    @Override
    public String getValidateJSFunction(final TransformerContext context) {
        final String type = ((TableTransformerContext)context).getSQLDataType().toUpperCase();
        String methodName = "";
        final String s = type;
        switch (s) {
            case "INTEGER":
            case "TINYINT":
            case "BIGINT":
            case "FLOAT":
            case "DOUBLE":
            case "DECIMAL": {
                methodName = "isValidNumeric";
                break;
            }
            case "CHAR":
            case "SCHAR":
            case "NCHAR": {
                methodName = "isStringSet";
                break;
            }
            case "DATE": {
                methodName = "isValidDate";
                break;
            }
            case "TIME": {
                methodName = "isTime";
                break;
            }
            case "DATETIME":
            case "TIMESTAMP": {
                methodName = "isTimeStamp";
                break;
            }
        }
        return methodName;
    }
    
    @Override
    public String getErrorMsg(final TransformerContext context) throws Exception {
        return I18N.getMsg("mc.component.tables.WRONG_FORMAT_OR_WRONG_VALUE", new Object[0]);
    }
    
    @Override
    public String getInputFormat(final TransformerContext context) {
        final String type = ((TableTransformerContext)context).getSQLDataType().toUpperCase();
        String inputFormat = "";
        String[] result = null;
        try {
            final DataObject columnConfig = context.getColumnConfiguration();
            final Row columnConfigRow = columnConfig.getFirstRow("ACColumnConfiguration");
            if (columnConfigRow.get(12) != null) {
                inputFormat = (String)columnConfigRow.get(12);
                final Pattern p = Pattern.compile("[\\s]+");
                result = p.split(inputFormat);
            }
            final String s = type;
            switch (s) {
                case "INTEGER":
                case "TINYINT":
                case "BIGINT":
                case "FLOAT":
                case "DOUBLE":
                case "DECIMAL": {
                    inputFormat = I18N.getMsg("mc.component.tables.INTEGER_ONLY", new Object[0]);
                    break;
                }
                case "CHAR":
                case "SCHAR":
                case "NCHAR": {
                    inputFormat = I18N.getMsg("mc.component.tables.STRING_ONLY", new Object[0]);
                    break;
                }
                case "DATE": {
                    inputFormat = ((!inputFormat.isEmpty() && result != null) ? result[0] : "yyyy-MM-dd");
                    break;
                }
                case "TIME": {
                    inputFormat = ((!inputFormat.isEmpty() && result != null) ? result[1] : "hh:mm:ss");
                    break;
                }
                case "DATETIME":
                case "TIMESTAMP": {
                    inputFormat = (inputFormat.isEmpty() ? "yyyy-MM-dd hh:mm:ss" : inputFormat);
                    break;
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return inputFormat;
    }
    
    static {
        DEFAULTSTRINGTODATEFMT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DefaultTransformer.out = Logger.getLogger(DefaultTransformer.class.getName());
    }
}
