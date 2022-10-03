package com.adventnet.webclient.struts.action;

import org.apache.struts.action.ActionForward;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.Action;

public class ApplyTheme extends Action
{
    public ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) {
        final String theme = request.getParameter("theme");
        request.getSession().setAttribute("currentTheme", (Object)theme);
        final String referer = request.getHeader("referer");
        if (referer != null) {
            return new ActionForward(referer, true);
        }
        return null;
    }
}
