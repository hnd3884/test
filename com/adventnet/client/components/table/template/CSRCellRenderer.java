package com.adventnet.client.components.table.template;

import com.adventnet.client.util.web.HTMLUtil;
import com.adventnet.client.themes.web.ThemesAPI;
import org.json.JSONObject;
import com.adventnet.client.components.table.web.TableTransformerContext;
import com.adventnet.iam.xss.IAMEncoder;
import com.adventnet.client.tpl.TemplateAPI;
import java.util.HashMap;

public class CSRCellRenderer
{
    private static String encodeForHlSearch(final HashMap<String, String> hlvalue, final boolean encode) {
        final String prefix = getValueForHtml(hlvalue.get("prefix"), encode);
        final String suffix = getValueForHtml(hlvalue.get("suffix"), encode);
        final String searchval = getValueForHtml(hlvalue.get("searchval"), encode);
        String hlsearchval = "";
        try {
            hlsearchval = TemplateAPI.givehtml("SearchHighlight", (TemplateAPI.VariableHandler)null, (Object)new Object[][] { { "SEARCHVAL", searchval } });
        }
        catch (final Exception e) {
            hlsearchval = "Exception Occurred";
            e.printStackTrace();
        }
        hlsearchval = hlsearchval.trim();
        return prefix + hlsearchval + suffix;
    }
    
    private static String getValueForHtml(final String actualVal, final boolean encode) {
        if (encode) {
            return IAMEncoder.encodeHTML(actualVal);
        }
        return actualVal;
    }
    
    private static String getValueForHtmlAttribute(final String actualVal, final boolean encode) {
        if (encode) {
            return IAMEncoder.encodeHTMLAttribute(actualVal);
        }
        return actualVal;
    }
    
    public static JSONObject getData(final HashMap<String, Object> props, final TableTransformerContext transContext) throws Exception {
        final String contextPath = transContext.getViewContext().getContextPath();
        final String themeDir = ThemesAPI.getThemeDirForRequest(contextPath);
        final boolean encode = transContext.getViewContext().getRenderType() != 8;
        final JSONObject cellVal = new JSONObject();
        cellVal.put("icon", (Object)getValueForHtmlAttribute(ThemesAPI.handlePath((String)props.get("ICON"), contextPath, themeDir), encode));
        cellVal.put("icontitle", (Object)getValueForHtmlAttribute(props.get("ICONTITLE"), encode));
        cellVal.put("iconCss", (Object)getValueForHtmlAttribute(props.get("ICONCSSCLASS"), encode));
        cellVal.put("autoLink", props.get("AUTOLINK"));
        cellVal.put("link", (Object)getValueForHtmlAttribute(props.get("LINK"), encode));
        cellVal.put("linkTitle", (Object)getValueForHtmlAttribute(props.get("LINKTITLE"), encode));
        cellVal.put("invoke", (Object)getValueForHtmlAttribute(props.get("INVOKE"), encode));
        cellVal.put("invokeStyle", (Object)getValueForHtmlAttribute(props.get("INVOKESTYLE"), encode));
        cellVal.put("linkId", props.get("LINK_ID"));
        cellVal.put("replaceIcon", (Object)getValueForHtmlAttribute(ThemesAPI.handlePath((String)props.get("REPLACE_ICON"), contextPath, themeDir), encode));
        final Object value = props.get("VALUE");
        if (props.get("HLVALUE") != null) {
            final String hlvalue = encodeForHlSearch(props.get("HLVALUE"), encode);
            cellVal.put("value", (Object)hlvalue);
        }
        else {
            cellVal.put("value", (Object)((value != null) ? (encode ? HTMLUtil.encode(value.toString()) : value.toString()) : ""));
        }
        final String tooltipName = props.get("TOOLTIPNAME");
        if (!"BubbleTooltip".equals(tooltipName)) {
            cellVal.put("tooltip", (Object)getValueForHtmlAttribute(tooltipName, encode));
        }
        if (props.get("ACTUAL_VALUE") != null) {
            cellVal.put("tooltipDisplayer", (Object)getValueForHtmlAttribute(props.get("MESSAGE_DISPLAYER"), encode));
            final String actualVal = getValueForHtmlAttribute(props.get("ACTUAL_VALUE"), encode);
            cellVal.put("tooltipValue", (Object)actualVal);
            cellVal.put("actionLink", (Object)getValueForHtmlAttribute(props.get("ACTION_LINK"), encode));
            if (value == null || ((String)value).isEmpty()) {
                cellVal.put("value", (Object)actualVal);
            }
        }
        if (props.get("CSS") != null) {
            cellVal.put("cssClass", (Object)getValueForHtmlAttribute(props.get("CSS"), encode));
        }
        if (props.get("PREFIX_TEXT") != null) {
            final JSONObject prefixText = new JSONObject();
            prefixText.put("value", (Object)getValueForHtml(props.get("PREFIX_TEXT"), encode));
            if (props.get("PREFIX_TEXT_CSS") != null) {
                prefixText.put("css", (Object)getValueForHtmlAttribute(props.get("PREFIX_TEXT_CSS"), encode));
            }
            cellVal.put("prefixTextContent", (Object)prefixText);
        }
        if (props.get("SUFFIX_TEXT") != null) {
            final JSONObject suffixText = new JSONObject();
            suffixText.put("value", (Object)getValueForHtml(props.get("SUFFIX_TEXT"), encode));
            if (props.get("SUFFIX_TEXT_CSS") != null) {
                suffixText.put("css", (Object)getValueForHtmlAttribute(props.get("SUFFIX_TEXT_CSS"), encode));
            }
            cellVal.put("suffixTextContent", (Object)suffixText);
        }
        if (props.get("PREFIX_LINK") != null) {
            final JSONObject prefixLink = new JSONObject();
            prefixLink.put("value", (Object)getValueForHtml(props.get("PREFIX_LINK"), encode));
            if (props.get("PREFIX_LINK_CSS") != null) {
                prefixLink.put("css", (Object)getValueForHtmlAttribute(props.get("PREFIX_LINK_CSS"), encode));
            }
            prefixLink.put("href", (Object)((props.get("PREFIX_LINK_HREF") != null) ? getValueForHtmlAttribute(props.get("PREFIX_LINK_HREF"), encode) : "#0"));
            cellVal.put("prefixLinkContent", (Object)prefixLink);
        }
        if (props.get("SUFFIX_LINK") != null) {
            final JSONObject suffixLink = new JSONObject();
            suffixLink.put("value", (Object)getValueForHtml(props.get("SUFFIX_LINK"), encode));
            if (props.get("SUFFIX_LINK_CSS") != null) {
                suffixLink.put("css", (Object)getValueForHtmlAttribute(props.get("SUFFIX_LINK_CSS"), encode));
            }
            suffixLink.put("href", (Object)((props.get("SUFFIX_LINK_HREF") != null) ? getValueForHtmlAttribute(props.get("SUFFIX_LINK_HREF"), encode) : "#0"));
            cellVal.put("suffixLinkContent", (Object)suffixLink);
        }
        cellVal.put("suffixIcon", (Object)getValueForHtmlAttribute(ThemesAPI.handlePath((String)props.get("SUFFIX_ICON"), contextPath, themeDir), encode));
        cellVal.put("sicontitle", (Object)getValueForHtmlAttribute(props.get("SUFFIXICONTITLE"), encode));
        cellVal.put("suffixIconCss", (Object)getValueForHtmlAttribute(props.get("SUFFIXICONCSSCLASS"), encode));
        final String menuPopup = props.get("MENUPOPUP");
        if (menuPopup != null && menuPopup.equals("true")) {
            cellVal.put("menupopup", (Object)getValueForHtmlAttribute(menuPopup, encode));
            String css_class = props.get("CSSCLASS_MENUPOPUPCOLUMN");
            if (css_class == null || css_class.equals("")) {
                css_class = "menupopupimg";
            }
            cellVal.put("menu_popup_css_class", (Object)getValueForHtmlAttribute(css_class, encode));
            final String menuPopupCol = props.get("CONTENT_MENUPOPUPCOLUMN");
            if (menuPopupCol != null && !menuPopupCol.isEmpty()) {
                cellVal.put("menupopup_col_content", (Object)getValueForHtmlAttribute(menuPopupCol, encode));
            }
            cellVal.put("popupWidth", props.get("POPUPWIDTH"));
            cellVal.put("offsetLeft", props.get("OFFSETLEFT"));
            cellVal.put("offsetTop", props.get("OFFSETTOP"));
            cellVal.put("showEvent", (Object)getValueForHtmlAttribute(props.get("SHOWEVENT"), encode));
            cellVal.put("hideEvent", (Object)getValueForHtmlAttribute(props.get("HIDEEVENT"), encode));
        }
        cellVal.put("action", props.get("ACTION"));
        cellVal.put("view", props.get("VIEW"));
        cellVal.put("menu", props.get("MENU"));
        cellVal.put("payload", props.get("PAYLOAD"));
        return cellVal;
    }
}
