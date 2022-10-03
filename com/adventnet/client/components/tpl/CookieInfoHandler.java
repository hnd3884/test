package com.adventnet.client.components.tpl;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.tpl.TemplateAPI;

public class CookieInfoHandler implements TemplateAPI.VariableHandler
{
    public String getVariableValue(final String variableName, final int variablePosition, final Object handlerContext) {
        Cookie[] cookies = null;
        if (handlerContext instanceof ViewContext) {
            final ViewContext vctx = (ViewContext)handlerContext;
            final HttpServletRequest request = vctx.getRequest();
            cookies = request.getCookies();
        }
        else if (handlerContext instanceof HttpServletRequest) {
            cookies = ((HttpServletRequest)handlerContext).getCookies();
        }
        if (cookies == null) {
            throw new RuntimeException("Unable to fetch value of cookie " + variableName);
        }
        for (int i = 0; i < cookies.length; ++i) {
            if (cookies[i].getName().equals(variableName)) {
                return cookies[i].getValue();
            }
        }
        throw new RuntimeException("Unable to fetch value of cookie " + variableName);
    }
}
