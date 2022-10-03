package com.adventnet.authentication.filter;

import java.io.IOException;
import javax.servlet.http.HttpSession;
import com.adventnet.authentication.util.AuthUtil;
import java.util.Objects;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import java.util.logging.Level;
import javax.servlet.FilterConfig;
import java.util.logging.Logger;
import javax.servlet.Filter;

public class RememberMeFilter implements Filter
{
    private static final Logger LOGGER;
    
    public void init(final FilterConfig filterConfig) throws ServletException {
        RememberMeFilter.LOGGER.log(Level.FINER, "init invoked with Filter Config : {0}", filterConfig);
    }
    
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest hreq = (HttpServletRequest)servletRequest;
        final HttpServletResponse hres = (HttpServletResponse)servletResponse;
        final HttpSession session = hreq.getSession();
        final Object addRememberMeCookie = (session != null) ? session.getAttribute("UpdateRemCookie") : Boolean.valueOf(false);
        if (Objects.equals(addRememberMeCookie, true) && hreq.getUserPrincipal() != null) {
            AuthUtil.getRememberMeService().updateRememberMeInfo(hreq, hres);
            session.removeAttribute("UpdateRemCookie");
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
    
    public void destroy() {
    }
    
    static {
        LOGGER = Logger.getLogger(RememberMeFilter.class.getName());
    }
}
