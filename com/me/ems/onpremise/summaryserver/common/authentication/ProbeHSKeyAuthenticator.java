package com.me.ems.onpremise.summaryserver.common.authentication;

import java.io.PrintWriter;
import java.util.logging.Level;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;

public class ProbeHSKeyAuthenticator extends HttpServlet
{
    private static Logger out;
    private static final String HANDSHAKEKEY = "handshake-key";
    
    public void doGet(final HttpServletRequest request, final HttpServletResponse res) throws ServletException, IOException {
        this.doPost(request, res);
    }
    
    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        ProbeHSKeyAuthenticator.out.log(Level.INFO, "---> Inside ProbeHSKeyAuthenticator");
        try {
            String message = "FAILED";
            String handShakeKey = null;
            boolean validate = false;
            handShakeKey = request.getHeader("handshake-key");
            validate = ProbeHSKeyGenerator.validateKey(handShakeKey);
            if (validate) {
                message = "SUCCESS";
            }
            ProbeHSKeyAuthenticator.out.log(Level.INFO, "The hskey message after verifying is " + message);
            final PrintWriter out = response.getWriter();
            response.setContentType("text/html");
            out.println(message);
            out.close();
        }
        catch (final Exception e) {
            ProbeHSKeyAuthenticator.out.log(Level.INFO, " Exception while validating hs key", e);
        }
    }
    
    static {
        ProbeHSKeyAuthenticator.out = Logger.getLogger("ProbeHSKeyAuthenticator");
    }
}
