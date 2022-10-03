package com.adventnet.client.view.dynamiccontentarea.web;

import com.adventnet.persistence.DataAccessException;
import java.util.regex.Matcher;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.view.web.StateAPI;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.client.view.web.WebViewAPI;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;
import com.adventnet.client.util.web.WebConstants;
import com.adventnet.client.util.web.JavaScriptConstants;

public class DynamicContentAreaUtils implements JavaScriptConstants, WebConstants
{
    private static final Pattern PAT;
    
    static String getDynamicContentAreaName(final HttpServletRequest request, final String dynamicContentAreaNameArg) {
        return getDynamicContentAreaName(request, dynamicContentAreaNameArg, null);
    }
    
    static String getDynamicContentAreaName(final HttpServletRequest request, String dynamicContentAreaNameArg, final String rootViewId) {
        if (dynamicContentAreaNameArg == null || dynamicContentAreaNameArg.equals("DEFAULTCONTENTAREA")) {
            String origId = WebViewAPI.getOriginalRootViewId(request, true);
            if (origId == null) {
                origId = rootViewId;
            }
            dynamicContentAreaNameArg = origId + "_" + "CONTENTAREA";
        }
        return dynamicContentAreaNameArg;
    }
    
    static List generateDCAIList(final String caString, final HttpServletRequest request) throws Exception {
        final List caList = new ArrayList();
        final String[] dispAreaItems = caString.split("-");
        for (int i = 0; i < dispAreaItems.length; ++i) {
            caList.add(ViewContext.getViewContext(StateAPI.getUniqueId(dispAreaItems[i]), request));
        }
        return caList;
    }
    
    static String getTitleString(final String title, final HttpServletRequest request) throws DataAccessException {
        final Matcher match = DynamicContentAreaUtils.PAT.matcher(title);
        final StringBuffer strBuf = new StringBuffer("");
        while (match.find()) {
            final String paramName = match.group(1);
            final String[] paramValues = request.getParameterValues(paramName);
            if (paramValues == null) {
                continue;
            }
            if (paramValues.length == 1) {
                match.appendReplacement(strBuf, paramValues[0]);
            }
            else {
                final StringBuffer paramValue = new StringBuffer();
                for (int i = 0; i < paramValues.length; ++i) {
                    paramValue.append(paramValues[i]).append(',');
                }
                if (paramValue.length() > 0) {
                    paramValue.deleteCharAt(paramValue.length() - 1);
                }
                match.appendReplacement(strBuf, paramValue.toString());
            }
        }
        match.appendTail(strBuf);
        return strBuf.toString();
    }
    
    static {
        PAT = Pattern.compile("\\$\\{([^}]*)\\}");
    }
}
