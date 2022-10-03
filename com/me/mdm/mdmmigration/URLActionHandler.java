package com.me.mdm.mdmmigration;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.util.logging.Level;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

public abstract class URLActionHandler
{
    private static Logger logger;
    
    public void dispatchAction(final HttpServletRequest req, final HttpServletResponse resp) {
        try {
            String action = req.getParameter("action");
            if (action == null) {
                action = "unknown";
            }
            this.serveMigrationPage(req, resp);
        }
        catch (final Exception e) {
            URLActionHandler.logger.log(Level.SEVERE, "Error while dispatchAction(): ", e);
            try {
                this.serveInvalidPage(resp);
            }
            catch (final Exception ex) {
                URLActionHandler.logger.log(Level.SEVERE, "Error while going back to homepage due to error(): ", e);
            }
        }
    }
    
    protected void serveMigrationPage(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        this.setAttributes(req);
        req.getServletContext().getRequestDispatcher("/jsp/mdm/mdmmigration/migration.jsp").forward((ServletRequest)req, (ServletResponse)resp);
    }
    
    protected void setAttributes(final HttpServletRequest request) {
    }
    
    protected void serveInvalidPage(final HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.getWriter().write("<!DOCTYPE html><html><body><h1>Internal Server Error! Unable to Find Page!</h1></body></html>");
    }
    
    static {
        URLActionHandler.logger = Logger.getLogger("MDMLogger");
    }
}
