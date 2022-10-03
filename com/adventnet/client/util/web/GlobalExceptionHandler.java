package com.adventnet.client.util.web;

import javax.servlet.ServletException;
import java.util.logging.Level;
import org.apache.struts.action.ActionForward;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.config.ExceptionConfig;
import java.util.logging.Logger;
import org.apache.struts.action.ExceptionHandler;

public class GlobalExceptionHandler extends ExceptionHandler
{
    Logger logger;
    
    public GlobalExceptionHandler() {
        this.logger = Logger.getLogger(GlobalExceptionHandler.class.getName());
    }
    
    public ActionForward execute(final Exception ex, final ExceptionConfig ae, final ActionMapping mapping, final ActionForm formInstance, final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
        this.logger.log(Level.WARNING, "Exception thrown by struts action : ", ex);
        return super.execute(ex, ae, mapping, formInstance, request, response);
    }
}
