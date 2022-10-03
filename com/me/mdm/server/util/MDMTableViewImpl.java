package com.me.mdm.server.util;

import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.components.web.TransformerContext;
import com.me.mdm.server.factory.MDMTableViewAPI;

public class MDMTableViewImpl implements MDMTableViewAPI
{
    @Override
    public String getIsExport(final TransformerContext tableContext) {
        final ViewContext vc = tableContext.getViewContext();
        final HttpServletRequest request = vc.getRequest();
        final String isExport = request.getParameter("isExport");
        return isExport;
    }
    
    @Override
    public String[] getViewContextParameterValues(final ViewContext viewCtx, final String paramName) {
        String[] params = new String[0];
        if (paramName != null && !paramName.isEmpty()) {
            params = viewCtx.getRequest().getParameterValues(paramName);
        }
        return (params != null && params.length != 0) ? params[0].split(",") : params;
    }
}
