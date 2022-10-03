package com.adventnet.client.components.userpreference.web;

import com.adventnet.client.view.UserPersonalizationAPI;
import com.adventnet.client.util.web.WebClientUtil;
import org.apache.struts.action.ActionForward;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.Action;

public class UpdateUserPreferenceAction extends Action
{
    public ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final String prefName = WebClientUtil.getRequiredParameter("PREFNAME", request);
        final String prefValue = WebClientUtil.getRequiredParameter("PREFVALUE", request);
        final long accId = WebClientUtil.getAccountId();
        UserPersonalizationAPI.setUserPreference(prefName, accId, prefValue);
        response.setContentType("text/html");
        response.getWriter().println("<script>var ISEMPTYRESPONSE=true;</script>");
        response.flushBuffer();
        return null;
    }
}
