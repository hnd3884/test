package com.adventnet.sym.webclient.mdm.ember;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServlet;

public class MdmClientIntegrationServlet extends HttpServlet
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
        final String emberJSP = "/jsp/mdm/ember/integrationsSpiceworks.jsp";
        final RequestDispatcher requDis = request.getRequestDispatcher(emberJSP);
        requDis.forward((ServletRequest)request, (ServletResponse)response);
    }
}
