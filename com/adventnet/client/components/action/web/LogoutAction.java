package com.adventnet.client.components.action.web;

import java.io.IOException;
import org.apache.struts.action.ActionForward;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.Action;

public class LogoutAction extends Action
{
    public ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        if ("true".equals(request.getSession(false).getAttribute("saml"))) {
            return new ActionForward("/SamlLogoutRequestServlet", true);
        }
        request.getSession().invalidate();
        final ActionForward actionForward = new ActionForward("/", true);
        return actionForward;
    }
}
