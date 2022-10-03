package com.adventnet.iam.security;

import javax.servlet.FilterConfig;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.Filter;

public class ActionForwardFilter implements Filter
{
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final ActionRule rule = (ActionRule)request.getAttribute("urlrule");
        if (rule == null || rule.getActionForward() == null || rule.getActionForward().trim().isEmpty()) {
            chain.doFilter(request, response);
            return;
        }
        request.getRequestDispatcher(this.getForwardURL(rule, (HttpServletRequest)request)).forward(request, response);
    }
    
    public String getForwardURL(final ActionRule rule, final HttpServletRequest request) {
        final boolean pathRegex = rule.getActionForward().indexOf(36) != -1;
        String fwdUrl = rule.getActionForward();
        if (pathRegex) {
            final Pattern pathPattern = Pattern.compile(rule.getPath());
            final String uri = SecurityUtil.getRequestPath(request);
            final Matcher m = pathPattern.matcher(uri);
            if (m.matches()) {
                for (int gcnt = m.groupCount(), i = 1; i <= gcnt; ++i) {
                    fwdUrl = fwdUrl.replace("$" + i, m.group(i));
                }
            }
        }
        return fwdUrl;
    }
    
    public void init(final FilterConfig fc) throws ServletException {
    }
    
    public void destroy() {
    }
}
