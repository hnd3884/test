package com.adventnet.sym.webclient.mdm.ember;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.me.devicemanagement.framework.server.common.DMApplicationHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServlet;

public class MdmClientServlet extends HttpServlet
{
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }
    
    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final String allCustomerParam = request.getParameter("setAllCustomer");
        if (allCustomerParam != null) {
            if (allCustomerParam.equalsIgnoreCase("true")) {
                request.setAttribute("customersegmentation", (Object)"false");
            }
            else {
                request.setAttribute("customersegmentation", (Object)"true");
            }
        }
        if (CustomerInfoUtil.getInstance().isMSP() && DMApplicationHandler.isMdmProduct()) {
            MSPWebClientUtil.setCustomerIDSummaryInCookie(request, response, "All");
        }
        final String customerIDParam = request.getParameter("customerID");
        if (CustomerInfoUtil.getInstance().isMSP() && customerIDParam != null) {
            MSPWebClientUtil.setCustomerIDSummaryInCookie(request, response, customerIDParam);
        }
        final String zohoone = request.getParameter("zohoone");
        request.setAttribute("hideTabComp", (Object)false);
        request.setAttribute("isZohoOneIframe", (Object)false);
        if (zohoone != null && zohoone.equalsIgnoreCase("true")) {
            request.setAttribute("hideTabComp", (Object)true);
            request.setAttribute("isZohoOneIframe", (Object)true);
        }
        final String isUemIframe = request.getParameter("isMdmUemIframe");
        request.setAttribute("isMdmUemIframe", (Object)false);
        if (isUemIframe != null && isUemIframe.equalsIgnoreCase("true")) {
            request.setAttribute("hideTabComp", (Object)true);
            request.setAttribute("isMdmUemIframe", (Object)true);
        }
        final String emberJSP = "/jsp/mdm/ember/index.jsp";
        final RequestDispatcher reqDispatch = request.getRequestDispatcher(emberJSP);
        reqDispatch.forward((ServletRequest)request, (ServletResponse)response);
    }
}
