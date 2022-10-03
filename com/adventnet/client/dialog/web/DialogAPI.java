package com.adventnet.client.dialog.web;

import com.adventnet.client.util.web.WebClientUtil;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.tpl.TemplateAPI;
import com.adventnet.i18n.I18N;
import javax.servlet.http.HttpServletResponse;
import com.adventnet.client.view.web.ViewContext;

public class DialogAPI
{
    private static boolean matchEvent(final ViewContext vc) {
        final String uri = vc.getRequest().getRequestURI();
        final String attr = (String)vc.getRequest().getAttribute("eventURI");
        return attr != null && uri.indexOf(attr) != -1;
    }
    
    public static String getDialogPrefix(final ViewContext vc, final HttpServletResponse resp) throws Exception {
        appendDialogID(vc, resp);
        boolean appendDialog = false;
        boolean withTitle = false;
        final String dialogBox = vc.getRequest().getParameter("dialogBoxType");
        final String title = vc.getRequest().getParameter("title");
        appendDialog = false;
        withTitle = false;
        final boolean matchURI = vc.getRequest().getRequestURI().indexOf(vc.getUniqueId() + ".cc") != -1;
        if (dialogBox != null && (matchURI || matchEvent(vc))) {
            appendDialog = true;
            if (title != null) {
                withTitle = true;
            }
        }
        if (!appendDialog) {
            return "";
        }
        if (withTitle) {
            return TemplateAPI.givehtml(dialogBox + "_Prefix", null, new Object[][] { { "TITLE", I18N.getMsg(title, new Object[0]) } });
        }
        return TemplateAPI.givehtml(dialogBox + "_Prefix", null, new Object[0][]);
    }
    
    public static String getDialogSuffix(final ViewContext vc) throws Exception {
        boolean appendDialog = false;
        boolean withTitle = false;
        final String dialogBox = vc.getRequest().getParameter("dialogBoxType");
        final String title = vc.getRequest().getParameter("title");
        appendDialog = false;
        withTitle = false;
        final boolean matchURI = vc.getRequest().getRequestURI().indexOf(vc.getUniqueId() + ".cc") != -1;
        if (dialogBox != null && (matchURI || matchEvent(vc))) {
            appendDialog = true;
            if (title != null) {
                withTitle = true;
            }
        }
        if (appendDialog) {
            return TemplateAPI.givehtml(dialogBox + "_Suffix", null, new Object[0][]);
        }
        return "";
    }
    
    public static void setDialogPropertyInRequest(final HttpServletRequest req) {
        if (req.getParameter("dialogBoxType") != null) {
            req.setAttribute("eventURI", (Object)WebClientUtil.getRequestedPathName(req));
        }
    }
    
    private static void appendDialogID(final ViewContext vc, final HttpServletResponse resp) throws Exception {
        if (vc.getRequest().getParameter("dialogBoxType") != null) {
            resp.getWriter().println("<div id='TemplateDialog' class='hide'></div>");
        }
    }
}
