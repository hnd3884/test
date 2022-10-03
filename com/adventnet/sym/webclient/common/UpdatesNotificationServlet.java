package com.adventnet.sym.webclient.common;

import com.me.mdm.onpremise.server.admin.MDMPFlashMessage;
import com.me.devicemanagement.framework.server.util.UpdatesParamUtil;
import java.util.logging.Level;
import java.io.IOException;
import javax.servlet.ServletException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;

public class UpdatesNotificationServlet extends HttpServlet
{
    String className;
    Logger logger;
    
    public UpdatesNotificationServlet() {
        this.className = UpdatesNotificationServlet.class.getName();
        this.logger = Logger.getLogger(this.className);
    }
    
    protected void processRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        final PrintWriter out = response.getWriter();
    }
    
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) {
        this.logger.log(Level.INFO, "********************* UpdatesNotificationServelet.doGet starts ******************");
        try {
            final String disableNews = request.getParameter("FLASH_NEWS_DISABLE");
            this.logger.log(Level.INFO, "FLASH_NEWS_DISABLE got from request : {0}", disableNews);
            if (disableNews != null) {
                UpdatesParamUtil.updateUpdParams("FLASH_NEWS_DISABLE", disableNews);
                final MDMPFlashMessage flashMessage = new MDMPFlashMessage();
                flashMessage.flashMessageStatusUpdate();
                flashMessage.checkAndDownloadFlashMessage(false);
            }
            this.logger.log(Level.INFO, "Successfully updated UpdateParams with param name FLASH_NEWS_DISABLE and value {0}", disableNews);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Caught exception while saving FLASH_NEWS_DISABLE.", ex);
        }
        this.logger.log(Level.INFO, "********************* UpdatesNotificationServelet.doGet ends ******************");
    }
    
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.processRequest(request, response);
    }
    
    public String getServletInfo() {
        return "Short description";
    }
}
