package com.adventnet.client.view.web;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import java.io.IOException;
import com.adventnet.client.util.web.WebClientUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.util.logging.Level;
import javax.servlet.FilterConfig;
import java.util.logging.Logger;
import com.adventnet.client.util.web.WebConstants;
import javax.servlet.Filter;

public class StateFilter implements Filter, WebConstants
{
    private static Logger logger;
    
    public void init(final FilterConfig filterConfig) {
        StateFilter.logger.log(Level.FINEST, "State Filter initialized");
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        try {
            final Long startTime = new Long(System.currentTimeMillis());
            request.setAttribute("TIME_TO_LOAD_START_TIME", (Object)startTime);
            StateFilter.logger.log(Level.FINEST, "doFilter called for {0} ", ((HttpServletRequest)request).getRequestURI());
            StateParserGenerator.processState((HttpServletRequest)request, (HttpServletResponse)response);
            final String forwardPath = ((HttpServletRequest)request).getRequestURI();
            if (!WebClientUtil.isRestful((HttpServletRequest)request) || forwardPath.indexOf("STATE_ID") != -1) {
                final String path = this.getForwardPath((HttpServletRequest)request);
                final RequestDispatcher rd = request.getRequestDispatcher(path);
                rd.forward(request, response);
            }
            else {
                chain.doFilter(request, response);
            }
            StateFilter.logger.log(Level.FINEST, "end of doFilter for {0} ", ((HttpServletRequest)request).getRequestURI());
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw ex2;
        }
        catch (final Exception ex3) {
            throw new ServletException((Throwable)ex3);
        }
        finally {
            StateAPI.clearStateForThread();
        }
    }
    
    public void destroy() {
    }
    
    private String getForwardPath(final HttpServletRequest request) {
        final String path = request.getContextPath() + "/STATE_ID/";
        String forwardPath = request.getRequestURI();
        if (!forwardPath.startsWith(path)) {
            return forwardPath;
        }
        final int index = forwardPath.indexOf(47, path.length());
        if (WebClientUtil.isRestful(request)) {
            forwardPath = forwardPath.substring(path.length() - 1);
        }
        else if (index > 0) {
            forwardPath = forwardPath.substring(index);
        }
        return forwardPath;
    }
    
    static {
        StateFilter.logger = Logger.getLogger(StateFilter.class.getName());
    }
}
