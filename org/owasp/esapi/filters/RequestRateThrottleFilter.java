package org.owasp.esapi.filters;

import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.http.HttpSession;
import java.util.LinkedList;
import org.owasp.esapi.ESAPI;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.FilterConfig;
import javax.servlet.Filter;

public class RequestRateThrottleFilter implements Filter
{
    private int hits;
    private int period;
    private static final String HITS = "hits";
    private static final String PERIOD = "period";
    
    public RequestRateThrottleFilter() {
        this.hits = 5;
        this.period = 10;
    }
    
    public void init(final FilterConfig filterConfig) {
        this.hits = ((filterConfig.getInitParameter("hits") == null) ? 5 : Integer.parseInt(filterConfig.getInitParameter("hits")));
        this.period = ((filterConfig.getInitParameter("period") == null) ? 10 : Integer.parseInt(filterConfig.getInitParameter("period")));
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest)request;
        final HttpSession session = httpRequest.getSession(true);
        synchronized (session.getId().intern()) {
            List<Long> times = ESAPI.httpUtilities().getSessionAttribute("times");
            if (times == null) {
                times = new LinkedList<Long>();
                session.setAttribute("times", (Object)times);
            }
            final Long newest = System.currentTimeMillis();
            times.add(newest);
            if (times.size() > this.hits) {
                final Long oldest = times.remove(0);
                final long elapsed = newest - oldest;
                if (elapsed < this.period * 1000) {
                    response.getWriter().println("Request rate too high");
                    return;
                }
            }
        }
        chain.doFilter(request, response);
    }
    
    public void destroy() {
    }
}
