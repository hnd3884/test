package com.me.devicemanagement.onpremise.summaryserver.summary.webclient.authentication;

import java.io.PrintWriter;
import java.util.logging.Level;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;

public class TicketValidator extends HttpServlet
{
    private static Logger out;
    private static final String TICKET = "ticket";
    
    public void doGet(final HttpServletRequest request, final HttpServletResponse res) throws ServletException, IOException {
        this.doPost(request, res);
    }
    
    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        TicketValidator.out.log(Level.INFO, "---> Inside TicketValidator");
        try {
            String message = "FAILURE";
            String ticket = "";
            ticket = request.getHeader("ticket");
            if (TicketUtil.isValidTicket(ticket)) {
                message = TicketUtil.getUserDomainForTicket(ticket);
            }
            TicketValidator.out.log(Level.INFO, "The ticket message after verifying is " + message);
            final PrintWriter out = response.getWriter();
            response.setContentType("text/html");
            out.println(message);
            out.close();
        }
        catch (final Exception e) {
            TicketValidator.out.log(Level.INFO, " Exception while validating ticket", e);
        }
    }
    
    static {
        TicketValidator.out = Logger.getLogger("TicketValidator");
    }
}
