package com.adventnet.client.components.table.template;

import com.adventnet.client.components.table.web.TableIterator;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.components.table.web.TableTransformerContext;
import com.adventnet.client.tpl.TemplateAPI;
import com.adventnet.client.util.web.HTMLUtil;
import com.adventnet.iam.xss.IAMEncoder;
import com.adventnet.client.themes.web.ThemesAPI;
import com.adventnet.client.components.table.web.TableViewModel;
import java.util.HashMap;

public class CellRenderer
{
    public static String getHTML(final HashMap<String, Object> props, final TableViewModel viewModel) throws Exception {
        final TableTransformerContext transContext = viewModel.getTableTransformerContext();
        final ViewContext vc = transContext.getViewContext();
        final String referenceId = vc.getReferenceId();
        final HttpServletRequest request = vc.getRequest();
        final String themeDir = ThemesAPI.getThemeDirForRequest(request);
        final TableIterator tableIter = viewModel.getTableIterator();
        final String icon = ThemesAPI.handlePath((String)props.get("ICON"), request, themeDir);
        String icontitle = props.get("ICONTITLE");
        icontitle = ((icontitle != null) ? (" title = \"" + IAMEncoder.encodeHTMLAttribute(icontitle) + "\"") : "");
        final String suffixIcon = ThemesAPI.handlePath((String)props.get("SUFFIX_ICON"), request, themeDir);
        String sicontitle = props.get("SUFFIXICONTITLE");
        sicontitle = ((sicontitle != null) ? (" title = \"" + IAMEncoder.encodeHTMLAttribute(sicontitle) + "\"") : "");
        String linkTitle = props.get("LINKTITLE");
        linkTitle = ((linkTitle != null) ? (" title = \"" + IAMEncoder.encodeHTMLAttribute(linkTitle) + "\"") : "");
        String autoLink = props.get("AUTOLINK");
        final String replaceIcon = ThemesAPI.handlePath((String)props.get("REPLACE_ICON"), request, themeDir);
        final String IconCss = props.get("ICONCSSCLASS");
        final String SuffixIconCss = props.get("SUFFIXICONCSSCLASS");
        final boolean isMenuItem = props.get("ISMENUITEM") != null && props.get("ISMENUITEM");
        final StringBuffer sb = new StringBuffer();
        if (autoLink == null) {
            autoLink = "false";
        }
        if ((props.get("LINK") != null || props.get("INVOKE") != null) && autoLink == "true") {
            sb.append("<a " + ((props.get("INVOKE") != null) ? ("href=javascript:void(); " + props.get("INVOKESTYLE")) : "href") + "='" + ((props.get("INVOKE") != null) ? props.get("INVOKE") : props.get("LINK")) + "' id='" + props.get("LINK_ID") + "' target='_blank'" + linkTitle + ">");
        }
        else if (props.get("LINK") != null || props.get("INVOKE") != null) {
            sb.append("<a " + ((props.get("INVOKE") != null) ? ("href=javascript:void(); " + props.get("INVOKESTYLE")) : "href") + "='" + ((props.get("INVOKE") != null) ? props.get("INVOKE") : props.get("LINK")) + "' id='" + props.get("LINK_ID") + "'" + linkTitle + ">");
        }
        if (icon != null) {
            final String iconCss = (IconCss != null) ? ("class='" + IconCss + "'") : "";
            sb.append("<img src='" + IAMEncoder.encodeHTMLAttribute(icon) + "' " + icontitle + " " + iconCss + ">");
        }
        else if (icon == null && IconCss != null) {
            sb.append("<span  class='" + IAMEncoder.encodeHTMLAttribute(IconCss) + "' ></span>");
        }
        if (props.get("PREFIX_LINK") != null) {
            if (props.get("PREFIX_LINK_CSS") != null) {
                sb.append("<a href='" + IAMEncoder.encodeHTMLAttribute((String)props.get("PREFIX_LINK_HREF")) + "' class='" + IAMEncoder.encodeHTMLAttribute((String)props.get("PREFIX_LINK_CSS")) + "'>" + IAMEncoder.encodeHTML((String)props.get("PREFIX_LINK")) + "</a>");
            }
            else {
                sb.append("<a href='" + IAMEncoder.encodeHTMLAttribute((String)props.get("PREFIX_LINK_HREF")) + "'>" + IAMEncoder.encodeHTML((String)props.get("PREFIX_LINK")) + "</a>");
            }
        }
        if (props.get("PREFIX_TEXT") != null) {
            if (props.get("PREFIX_TEXT_CSS") != null) {
                sb.append("<span class='" + IAMEncoder.encodeHTMLAttribute((String)props.get("PREFIX_TEXT_CSS")) + "'>" + IAMEncoder.encodeHTML((String)props.get("PREFIX_TEXT")) + "</span>");
            }
            else {
                sb.append(IAMEncoder.encodeHTML((String)props.get("PREFIX_TEXT")));
            }
        }
        if (props.get("ACTUAL_VALUE") != null) {
            final String key = "r" + referenceId + "r" + transContext.getRowIndex() + "c" + transContext.getColumnIndex();
            sb.append("<div id='" + key + "'></div>");
            sb.append("<Script>" + IAMEncoder.encodeJavaScript(String.valueOf(props.get("MESSAGE_DISPLAYER"))) + "('" + HTMLUtil.encode(String.valueOf(props.get("TRIMMED_VALUE"))) + "','" + HTMLUtil.encode(String.valueOf(props.get("ACTUAL_VALUE"))) + "','" + key + "','" + IAMEncoder.encodeJavaScript(String.valueOf(props.get("ACTION_LINK"))) + "',document);</Script>");
        }
        Object value = props.get("VALUE");
        String hlvalue = "";
        if (props.get("HLVALUE") != null) {
            hlvalue = encodeForHlSearch(props.get("HLVALUE"));
        }
        if (value == null || value.equals("")) {
            value = "";
        }
        final String menupopup = props.get("MENUPOPUP");
        if (menupopup == null || !menupopup.equals("true")) {
            if (replaceIcon != null) {
                sb.append("<img src='" + IAMEncoder.encodeHTMLAttribute(replaceIcon) + "'/>");
            }
            else if (!hlvalue.equals("")) {
                sb.append(hlvalue);
            }
            else if (isMenuItem) {
                sb.append(value);
            }
            else if (props.get("CSS") == null) {
                sb.append(HTMLUtil.encode(value.toString()));
            }
            else {
                sb.append("<span class='" + IAMEncoder.encodeHTMLAttribute((String)props.get("CSS")) + "'>" + HTMLUtil.encode(value.toString()) + "</span>");
            }
        }
        if (props.get("SUFFIX_TEXT") != null) {
            if (props.get("SUFFIX_TEXT_CSS") != null) {
                sb.append("<span class='" + IAMEncoder.encodeHTMLAttribute((String)props.get("SUFFIX_TEXT_CSS")) + "'>" + IAMEncoder.encodeHTML((String)props.get("SUFFIX_TEXT")) + "</span>");
            }
            else {
                sb.append(IAMEncoder.encodeHTML((String)props.get("SUFFIX_TEXT")));
            }
        }
        if (props.get("SUFFIX_LINK") != null) {
            if (props.get("SUFFIX_LINK_CSS") != null) {
                sb.append("<a href='" + IAMEncoder.encodeHTMLAttribute((String)props.get("SUFFIX_LINK_HREF")) + "' class='" + IAMEncoder.encodeHTMLAttribute((String)props.get("SUFFIX_LINK_CSS")) + "'>" + IAMEncoder.encodeHTML((String)props.get("SUFFIX_LINK")) + "</a>");
            }
            else {
                sb.append("<a href='" + IAMEncoder.encodeHTMLAttribute((String)props.get("SUFFIX_LINK_HREF")) + "'>" + IAMEncoder.encodeHTML((String)props.get("SUFFIX_LINK")) + "</a>");
            }
        }
        if (suffixIcon != null) {
            final String suffixIconCss = (SuffixIconCss != null) ? ("class=" + IAMEncoder.encodeHTMLAttribute(SuffixIconCss)) : "";
            sb.append("<img src='" + IAMEncoder.encodeHTMLAttribute(suffixIcon) + "'" + sicontitle + " " + suffixIconCss + ">");
        }
        else if (suffixIcon == null && SuffixIconCss != null) {
            sb.append("<span  class='" + IAMEncoder.encodeHTMLAttribute(SuffixIconCss) + "' ></span>");
        }
        if (props.get("LINK") != null || props.get("INVOKE") != null) {
            sb.append("</a>");
        }
        final String tooltip = props.get("TOOLTIPNAME");
        final String tooltip_id = tableIter.getCurrentRow() + "_" + tableIter.getCurrentColumn() + "_TOOLTIP";
        final String link_id = tableIter.getCurrentRow() + "_" + tableIter.getCurrentColumn() + "_LINK";
        if (menupopup != null && menupopup.equals("true")) {
            String css_class = props.get("CSSCLASS_MENUPOPUPCOLUMN");
            if (css_class == null || css_class.equals("")) {
                css_class = "menupopupimg";
            }
            String spanvalue = "";
            if (props.get("CONTENT_MENUPOPUPCOLUMN") != null && props.get("CONTENT_MENUPOPUPCOLUMN") != "") {
                spanvalue = props.get("CONTENT_MENUPOPUPCOLUMN");
            }
            sb.append("<span id='" + link_id + "' class='" + IAMEncoder.encodeHTMLAttribute(css_class) + "'>&nbsp;" + IAMEncoder.encodeHTML(spanvalue) + "</span>");
            sb.append(TemplateAPI.givehtml(tooltip + "_Prefix", (TemplateAPI.VariableHandler)null, (Object)new Object[][] { { "TOOLTIP_ID", tooltip_id }, { "POPUPWIDTH", props.get("POPUPWIDTH") } }));
            sb.append(props.get("VALUE"));
            sb.append(TemplateAPI.givehtml(tooltip + "_Suffix", (TemplateAPI.VariableHandler)null, (Object)new Object[][] { { "LINK_ID", link_id }, { "TOOLTIP_ID", tooltip_id }, { "OFFSETLEFT", props.get("OFFSETLEFT") }, { "OFFSETTOP", props.get("OFFSETTOP") }, { "SHOWEVENT", props.get("SHOWEVENT") }, { "HIDEEVENT", props.get("HIDEEVENT") } }));
        }
        return sb.toString();
    }
    
    public static String encodeForHlSearch(final HashMap<String, String> hlvalue) {
        final String prefix = IAMEncoder.encodeHTML((String)hlvalue.get("prefix"));
        final String suffix = IAMEncoder.encodeHTML((String)hlvalue.get("suffix"));
        final String searchval = IAMEncoder.encodeHTML((String)hlvalue.get("searchval"));
        String hlsearchval = "";
        try {
            hlsearchval = TemplateAPI.givehtml("SearchHighlight", (TemplateAPI.VariableHandler)null, (Object)new Object[][] { { "SEARCHVAL", searchval } });
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        hlsearchval = hlsearchval.trim();
        return prefix + hlsearchval + suffix;
    }
}
