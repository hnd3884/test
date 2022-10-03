package com.adventnet.client.action.web;

import com.adventnet.persistence.DataObject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.io.IOException;
import java.rmi.ServerException;
import com.adventnet.client.util.web.WebClientUtil;
import javax.servlet.ServletException;
import com.adventnet.client.util.web.ValidatorUtil;
import com.adventnet.client.view.web.WebViewAPI;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.util.web.WebConstants;
import javax.servlet.http.HttpServlet;

public class MenuActionProcessorSevlet extends HttpServlet implements WebConstants
{
    private static final long serialVersionUID = 1L;
    
    public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        if (WebViewAPI.isAjaxIframeRequest(request)) {
            response.setContentType("text/plain; charset=utf-8");
        }
        final String actionName = this.getRequestedAction(request);
        try {
            final String validate = request.getParameter("validate");
            if (validate != null && validate.equals("true")) {
                ValidatorUtil.validate(actionName, request);
            }
        }
        catch (final Exception exp) {
            throw new ServletException(exp.getMessage());
        }
        if (WebClientUtil.isMenuItemAuthorized(actionName)) {
            try {
                this.includeAction(actionName, request, response);
            }
            catch (final Exception e) {
                e.printStackTrace();
                throw new ServerException(e.getMessage(), e);
            }
        }
    }
    
    protected String getRequestedAction(final HttpServletRequest request) throws IOException {
        String path = null;
        path = (String)request.getAttribute("javax.servlet.include.path_info");
        if (path == null) {
            path = request.getPathInfo();
        }
        if (path != null && path.length() > 0) {
            return path;
        }
        path = (String)request.getAttribute("javax.servlet.include.servlet_path");
        if (path == null) {
            path = request.getServletPath();
        }
        final int slash = path.lastIndexOf(47);
        final int period = path.lastIndexOf(46);
        path = path.substring(slash + 1, period);
        return path;
    }
    
    protected void includeAction(final String actionName, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final String actionUrl = this.getActionToForward(actionName);
        if (actionUrl != null) {
            final RequestDispatcher rd = request.getRequestDispatcher(actionUrl);
            if (response.isCommitted()) {
                rd.include((ServletRequest)request, (ServletResponse)response);
            }
            else {
                rd.forward((ServletRequest)request, (ServletResponse)response);
            }
        }
    }
    
    protected String getActionToForward(final String actionName) {
        String actionUrl = null;
        try {
            final DataObject menuItemDO = MenuVariablesGenerator.getCompleteMenuItemData(actionName);
            if (menuItemDO.containsTable("OpenViewInContentArea")) {
                actionUrl = "/ShowViewInContentAreaAction.do?MENUITEM_ID=" + actionName;
            }
            else {
                actionUrl = this.getDefaultActionToForward(actionName);
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return actionUrl;
    }
    
    protected String getDefaultActionToForward(final String actionName) {
        return actionName + ".do?" + "MA" + "=true";
    }
}
