package com.adventnet.client.components.tpl;

import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.tpl.TemplateAPI;

public class HttpRequestHandler implements TemplateAPI.VariableHandler
{
    public String getVariableValue(final String variableName, final int variablePosition, final Object handlerContext) {
        if (handlerContext instanceof ViewContext) {
            final ViewContext vc = (ViewContext)handlerContext;
            return (vc.getRequest() != null) ? vc.getRequest().getParameter(variableName) : null;
        }
        return ((HttpServletRequest)handlerContext).getParameter(variableName);
    }
}
