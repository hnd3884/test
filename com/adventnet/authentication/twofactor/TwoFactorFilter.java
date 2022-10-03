package com.adventnet.authentication.twofactor;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import java.util.logging.Level;
import com.adventnet.authentication.util.AuthUtil;
import com.adventnet.authentication.PAM;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;
import java.util.logging.Logger;
import javax.servlet.Filter;

public class TwoFactorFilter implements Filter
{
    private static Logger logger;
    FilterConfig filterConfig;
    
    public TwoFactorFilter() {
        this.filterConfig = null;
    }
    
    public void init(final FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws ServletException, IOException {
        Long userId = null;
        try {
            if (request.getParameter(PAM.DOMAINNAME) != null) {
                try {
                    userId = AuthUtil.getUserId(request.getParameter("j_username"), request.getParameter(PAM.DOMAINNAME));
                }
                catch (final Exception e) {
                    request.setAttribute("ERROR", (Object)"Error Occured kindly retry");
                    final RequestDispatcher trd = request.getRequestDispatcher(this.filterConfig.getInitParameter("firstpage").toString());
                    trd.include(request, response);
                }
            }
            else {
                userId = AuthUtil.getUserId(request.getParameter("j_username"));
            }
        }
        catch (final Exception e) {
            userId = null;
        }
        RequestDispatcher rd = null;
        try {
            if (request.getParameter("j_username") == null || userId == null) {
                request.setAttribute("ERROR", (Object)"Error Occured kindly retry");
                rd = request.getRequestDispatcher(this.filterConfig.getInitParameter("firstpage").toString());
            }
            else {
                this.handleTwoFactorAuth(request, response, userId);
                rd = request.getRequestDispatcher(this.filterConfig.getInitParameter("secondpage").toString());
            }
        }
        catch (final Exception e2) {
            TwoFactorFilter.logger.log(Level.SEVERE, "username entered is not valid");
            rd = request.getRequestDispatcher(this.filterConfig.getInitParameter("firstpage").toString());
        }
        rd.include(request, response);
    }
    
    public void handleTwoFactorAuth(final ServletRequest request, final ServletResponse response, final Long userId) {
        try {
            ((TwoFactorAuth)AuthUtil.getTwoFactorImpl(userId)).handle(userId, request, response);
        }
        catch (final NullPointerException ex) {}
        catch (final Exception e) {
            TwoFactorFilter.logger.log(Level.SEVERE, "Exception while instantiating Two Factor implementation class");
        }
    }
    
    public void destroy() {
    }
    
    static {
        TwoFactorFilter.logger = Logger.getLogger(TwoFactorFilter.class.getName());
    }
}
