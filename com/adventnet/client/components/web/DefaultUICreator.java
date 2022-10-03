package com.adventnet.client.components.web;

import javax.servlet.http.HttpServletRequest;
import com.adventnet.iam.xss.IAMEncoder;
import com.adventnet.client.themes.web.ThemesAPI;
import java.util.HashMap;

public class DefaultUICreator implements UICreator
{
    @Override
    public String constructHeader(final TransformerContext context, final boolean isEditMode) {
        final HashMap headerProps = context.getRenderedAttributes();
        String displayName = null;
        if (headerProps != null && headerProps.size() > 0) {
            displayName = headerProps.get("VALUE");
        }
        if (displayName == null) {
            displayName = context.getDisplayName();
        }
        return displayName;
    }
    
    @Override
    public String constructCell(final TransformerContext context, final boolean isEditMode) {
        final HashMap cellProps = context.getRenderedAttributes();
        final HttpServletRequest request = context.getRequest();
        String themeDir;
        try {
            themeDir = ThemesAPI.getThemeDirForRequest(request);
        }
        catch (final Exception e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
        final String icon = ThemesAPI.handlePath((String)cellProps.get("ICON"), request, themeDir);
        final String suffixIcon = ThemesAPI.handlePath((String)cellProps.get("SUFFIX_ICON"), request, themeDir);
        final StringBuffer htmlCode = new StringBuffer();
        if (cellProps.size() > 0) {
            if (cellProps.get("LINK") != null) {
                htmlCode.append("<A HREF='" + IAMEncoder.encodeHTMLAttribute((String)cellProps.get("LINK")) + "'>");
            }
            if (icon != null) {
                htmlCode.append("<IMG SRC='" + IAMEncoder.encodeHTMLAttribute(icon) + "'>");
            }
            if (cellProps.get("PREFIX_TEXT") != null) {
                htmlCode.append(IAMEncoder.encodeHTML((String)cellProps.get("PREFIX_TEXT")));
            }
            Object value = cellProps.get("VALUE");
            if (value == null) {
                value = "&nbsp;";
            }
            htmlCode.append(IAMEncoder.encodeHTML((String)value));
            if (cellProps.get("SUFFIX_TEXT") != null) {
                htmlCode.append(IAMEncoder.encodeHTML((String)cellProps.get("SUFFIX_TEXT")));
            }
            if (suffixIcon != null) {
                htmlCode.append("<IMG SRC='" + IAMEncoder.encodeHTMLAttribute(suffixIcon) + "'>");
            }
            if (cellProps.get("LINK") != null) {
                htmlCode.append("</A>");
            }
        }
        else if (context.getPropertyValue() != null) {
            htmlCode.append(IAMEncoder.encodeHTML((String)context.getPropertyValue()));
        }
        else {
            htmlCode.append("");
        }
        return htmlCode.toString();
    }
}
