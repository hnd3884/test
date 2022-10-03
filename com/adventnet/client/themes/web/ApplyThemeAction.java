package com.adventnet.client.themes.web;

import com.adventnet.client.view.UserPersonalizationAPI;
import com.adventnet.client.util.web.WebClientUtil;
import org.apache.struts.action.ActionForward;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.Action;

public class ApplyThemeAction extends Action
{
    public ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final String userSelTheme = request.getParameter("selectedTheme");
        if (userSelTheme != null) {
            UserPersonalizationAPI.changeThemeForAccount(userSelTheme, WebClientUtil.getAccountId());
        }
        return mapping.findForward("mcReloadParentWindow");
    }
}
