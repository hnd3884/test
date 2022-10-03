package com.adventnet.persistence.fos.slave;

import com.adventnet.persistence.fos.FOS;
import java.util.logging.Level;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;

public class DownSlave extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG;
    
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.processRequest(request, response);
    }
    
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.processRequest(request, response);
    }
    
    private void Write(final HttpServletResponse response, final String message) throws Exception {
        response.setContentType("text/plain");
        response.getWriter().write(message);
    }
    
    private void processRequest(final HttpServletRequest request, final HttpServletResponse response) {
        try {
            DownSlave.LOG.log(Level.SEVERE, "Downslave called by " + request.getRemoteAddr());
            final String message = request.getParameter("message");
            if (message != null) {
                this.Write(response, "ok");
                FOS.restart(message);
            }
            else {
                DownSlave.LOG.log(Level.SEVERE, "The parameter message required for processing this request is missing.");
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    static {
        LOG = Logger.getLogger(DownSlave.class.getName());
    }
}
