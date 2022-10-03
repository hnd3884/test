package com.adventnet.client.view.web;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import com.adventnet.client.view.ViewAPI;
import com.adventnet.client.util.web.WebClientUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.util.web.JavaScriptConstants;
import com.adventnet.client.util.web.WebConstants;

public class CSRViewProcessorServlet extends ViewProcessorServlet implements WebConstants, JavaScriptConstants
{
    private static final long serialVersionUID = 1L;
    
    @Override
    public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final PrintWriter out = response.getWriter();
        try {
            final String viewName = WebClientUtil.getRequestedPathName(request);
            final ViewContext vc = ViewContext.getViewContext(viewName, request);
            vc.setContextPath(request.getContextPath());
            final String includeViewContent = vc.getModel().getFeatureValue("includeOldViewContent");
            if (includeViewContent != null) {
                super.service(request, response);
            }
            else {
                final String params = WebViewAPI.getParamsForView(vc.getModel().getViewConfiguration(), request);
                vc.setTransientState("_D_RP", params);
                request.setAttribute("VIEW_CTX", (Object)vc);
                if (vc.isCSRComponent()) {
                    response.setContentType("application/json");
                    ViewAPI.updateViewStates(vc, request);
                    out.println(ViewAPI.getViewModel(vc));
                }
                else {
                    response.setContentType("text/html");
                    if (request.getParameter("PARENT_CTX") != null) {
                        vc.setParentContext(ViewContext.getViewContext(request.getParameter("PARENT_CTX"), request));
                    }
                    this.includeEmberView(vc, response);
                }
            }
        }
        catch (final Exception ex) {
            throw new ServletException((Throwable)ex);
        }
    }
    
    @Override
    public void endRenderingPhase(final ViewContext rootViewCtx, final HttpServletRequest request, final HttpServletResponse response, final boolean subRequest) throws Exception {
    }
    
    @Override
    public void initializeRenderingPhase(final ViewContext rootViewCtx, final HttpServletRequest request, final HttpServletResponse response, final boolean subRequest) throws Exception {
    }
    
    public void includeEmberView(final ViewContext vc, final HttpServletResponse response) throws Exception {
        final HttpServletRequest request = vc.getRequest();
        String compUrl = vc.getModel().getForwardURL();
        compUrl = vc.getModel().getController().processPreRendering(vc, request, response, compUrl);
        if (compUrl != null) {
            final RequestDispatcher rd = request.getRequestDispatcher(compUrl);
            rd.include((ServletRequest)request, (ServletResponse)response);
            vc.getModel().getController().processPostRendering(vc, request, response);
        }
    }
}
