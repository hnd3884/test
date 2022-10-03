package com.adventnet.webclient.struts.action;

import javax.servlet.ServletException;
import java.io.IOException;
import org.apache.struts.action.ActionForward;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.Action;

public class SaveTokenAction extends Action
{
    public ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        this.saveToken(request);
        return mapping.findForward("success");
    }
}
