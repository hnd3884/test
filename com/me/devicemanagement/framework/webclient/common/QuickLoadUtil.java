package com.me.devicemanagement.framework.webclient.common;

import java.util.logging.Level;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

public class QuickLoadUtil
{
    private static Logger logger;
    
    public static void redirectURL(String url, final HttpServletRequest request, final HttpServletResponse response) {
        try {
            final String isQuickLoad = request.getParameter("ignoreHeader");
            if (isQuickLoad != null && isQuickLoad.equalsIgnoreCase("true")) {
                final String time = request.getParameter("qlt");
                final String prevTab = request.getParameter("previousTab");
                final String parameters = "ignoreHeader=" + isQuickLoad + "&qlt=" + time + "&previousTab=" + prevTab;
                if (url.contains("?") && url.contains("=")) {
                    url = url + "&" + parameters;
                }
                else if (!url.contains("?")) {
                    url = url + "?" + parameters;
                }
            }
            response.sendRedirect(url);
        }
        catch (final Exception e) {
            QuickLoadUtil.logger.log(Level.WARNING, "Exception while forming redirect url request");
        }
    }
    
    static {
        QuickLoadUtil.logger = Logger.getLogger(QuickLoadUtil.class.getName());
    }
}
