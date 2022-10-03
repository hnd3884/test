package com.adventnet.client.view.web;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import org.apache.struts.action.ActionForward;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import com.adventnet.client.util.web.ValidatorUtil;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.client.dialog.web.DialogAPI;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.util.web.WebConstants;
import javax.servlet.http.HttpServlet;

public class ViewEventProcessorServlet extends HttpServlet implements WebConstants
{
    public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        try {
            WebViewAPI.setContentType(request, response);
            final long start = System.currentTimeMillis();
            if (request.getAttribute("TIME_TO_LOAD_START_TIME") == null) {
                request.setAttribute("TIME_TO_LOAD_START_TIME", (Object)new Long(start));
            }
            DialogAPI.setDialogPropertyInRequest(request);
            ActionForward forward = null;
            final String uniqueId = WebClientUtil.getRequestedPathName(request);
            final String eventType = request.getParameter("EVENT_TYPE");
            final ViewContext viewCtx = ViewContext.getViewContext(uniqueId, request);
            final String validate = viewCtx.getModel().getFeatureValue("validate");
            if (validate != null && validate.equals("true")) {
                ValidatorUtil.validate(viewCtx);
            }
            final WebViewModel model = viewCtx.getModel();
            forward = model.getController().processEvent(viewCtx, request, response, eventType);
            if (forward != null) {
                final String url = forward.getPath();
                final boolean redirect = forward.getRedirect();
                if (redirect) {
                    response.sendRedirect(url);
                }
                else {
                    final RequestDispatcher rd = request.getRequestDispatcher(url);
                    rd.include((ServletRequest)request, (ServletResponse)response);
                }
            }
        }
        catch (final ServletException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            ex2.printStackTrace();
            throw new ServletException((Throwable)ex2);
        }
    }
}
