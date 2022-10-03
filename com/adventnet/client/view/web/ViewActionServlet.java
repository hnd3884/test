package com.adventnet.client.view.web;

import javax.servlet.ServletException;
import com.adventnet.client.util.web.WebClientUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.util.web.WebConstants;
import javax.servlet.http.HttpServlet;

public class ViewActionServlet extends HttpServlet implements WebConstants
{
    private static final long serialVersionUID = 7641003725512967476L;
    
    protected void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
        final String uniqueId = WebClientUtil.getRequestedPathName(request);
        final String eventType = request.getParameter("EVENT_TYPE");
        final ViewContext viewCtx = ViewContext.getViewContext(uniqueId, request);
        try {
            viewCtx.getModel().getController().processViewEvent(viewCtx, request, response, eventType);
        }
        catch (final Exception e) {
            throw new ServletException((Throwable)e);
        }
    }
}
