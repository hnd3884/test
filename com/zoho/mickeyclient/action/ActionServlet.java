package com.zoho.mickeyclient.action;

import javax.servlet.ServletException;
import com.adventnet.client.util.web.WebClientUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServlet;

public class ActionServlet extends HttpServlet
{
    private static final long serialVersionUID = -3255203642968160619L;
    
    protected void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
        final String menuAction = WebClientUtil.getRequestedPathName(request);
        final String viewName = request.getParameter("ACTION_SOURCE");
        try {
            ActionUtil.invoke(menuAction, viewName, request, response);
        }
        catch (final Exception e) {
            throw new ServletException("Exception occurred while processing the menu action : " + menuAction, (Throwable)e);
        }
    }
}
