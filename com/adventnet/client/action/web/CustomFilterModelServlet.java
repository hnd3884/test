package com.adventnet.client.action.web;

import javax.servlet.ServletException;
import com.adventnet.client.view.web.WebViewAPI;
import org.json.JSONObject;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.util.web.WebClientUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServlet;

public class CustomFilterModelServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    
    public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
        final ViewContext vc = ViewContext.getViewContext(WebClientUtil.getRequestedPathName(request), request);
        try {
            vc.getModel().getController().updateViewModel(ViewContext.getViewContext(request.getParameter("customFilterViewName"), request));
            response.setContentType("application/json");
            final JSONObject customFiltermodel = new JSONObject();
            customFiltermodel.put("criteriaDefn", request.getAttribute("CRITERIA_DEFN"));
            customFiltermodel.put("relCriteriaList", request.getAttribute("RELCRITERIALIST"));
            WebViewAPI.writeInResponse(request, response, customFiltermodel.toString());
        }
        catch (final Exception e) {
            throw new ServletException((Throwable)e);
        }
    }
}
