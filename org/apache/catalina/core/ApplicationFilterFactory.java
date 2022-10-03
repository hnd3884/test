package org.apache.catalina.core;

import org.apache.tomcat.util.descriptor.web.FilterMap;
import javax.servlet.DispatcherType;
import javax.servlet.FilterChain;
import org.apache.catalina.Globals;
import org.apache.catalina.connector.Request;
import javax.servlet.Servlet;
import org.apache.catalina.Wrapper;
import javax.servlet.ServletRequest;

public final class ApplicationFilterFactory
{
    private ApplicationFilterFactory() {
    }
    
    public static ApplicationFilterChain createFilterChain(final ServletRequest request, final Wrapper wrapper, final Servlet servlet) {
        if (servlet == null) {
            return null;
        }
        ApplicationFilterChain filterChain = null;
        if (request instanceof Request) {
            final Request req = (Request)request;
            if (Globals.IS_SECURITY_ENABLED) {
                filterChain = new ApplicationFilterChain();
            }
            else {
                filterChain = (ApplicationFilterChain)req.getFilterChain();
                if (filterChain == null) {
                    filterChain = new ApplicationFilterChain();
                    req.setFilterChain((FilterChain)filterChain);
                }
            }
        }
        else {
            filterChain = new ApplicationFilterChain();
        }
        filterChain.setServlet(servlet);
        filterChain.setServletSupportsAsync(wrapper.isAsyncSupported());
        final StandardContext context = (StandardContext)wrapper.getParent();
        final FilterMap[] filterMaps = context.findFilterMaps();
        if (filterMaps == null || filterMaps.length == 0) {
            return filterChain;
        }
        final DispatcherType dispatcher = (DispatcherType)request.getAttribute("org.apache.catalina.core.DISPATCHER_TYPE");
        String requestPath = null;
        final Object attribute = request.getAttribute("org.apache.catalina.core.DISPATCHER_REQUEST_PATH");
        if (attribute != null) {
            requestPath = attribute.toString();
        }
        final String servletName = wrapper.getName();
        for (final FilterMap filterMap : filterMaps) {
            if (matchDispatcher(filterMap, dispatcher)) {
                if (matchFiltersURL(filterMap, requestPath)) {
                    final ApplicationFilterConfig filterConfig = (ApplicationFilterConfig)context.findFilterConfig(filterMap.getFilterName());
                    if (filterConfig != null) {
                        filterChain.addFilter(filterConfig);
                    }
                }
            }
        }
        for (final FilterMap filterMap : filterMaps) {
            if (matchDispatcher(filterMap, dispatcher)) {
                if (matchFiltersServlet(filterMap, servletName)) {
                    final ApplicationFilterConfig filterConfig = (ApplicationFilterConfig)context.findFilterConfig(filterMap.getFilterName());
                    if (filterConfig != null) {
                        filterChain.addFilter(filterConfig);
                    }
                }
            }
        }
        return filterChain;
    }
    
    private static boolean matchFiltersURL(final FilterMap filterMap, final String requestPath) {
        if (filterMap.getMatchAllUrlPatterns()) {
            return true;
        }
        if (requestPath == null) {
            return false;
        }
        final String[] arr$;
        final String[] testPaths = arr$ = filterMap.getURLPatterns();
        for (final String testPath : arr$) {
            if (matchFiltersURL(testPath, requestPath)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean matchFiltersURL(final String testPath, final String requestPath) {
        if (testPath == null) {
            return false;
        }
        if (testPath.equals(requestPath)) {
            return true;
        }
        if (testPath.equals("/*")) {
            return true;
        }
        if (testPath.endsWith("/*")) {
            if (testPath.regionMatches(0, requestPath, 0, testPath.length() - 2)) {
                if (requestPath.length() == testPath.length() - 2) {
                    return true;
                }
                if ('/' == requestPath.charAt(testPath.length() - 2)) {
                    return true;
                }
            }
            return false;
        }
        if (testPath.startsWith("*.")) {
            final int slash = requestPath.lastIndexOf(47);
            final int period = requestPath.lastIndexOf(46);
            if (slash >= 0 && period > slash && period != requestPath.length() - 1 && requestPath.length() - period == testPath.length() - 1) {
                return testPath.regionMatches(2, requestPath, period + 1, testPath.length() - 2);
            }
        }
        return false;
    }
    
    private static boolean matchFiltersServlet(final FilterMap filterMap, final String servletName) {
        if (servletName == null) {
            return false;
        }
        if (filterMap.getMatchAllServletNames()) {
            return true;
        }
        final String[] arr$;
        final String[] servletNames = arr$ = filterMap.getServletNames();
        for (final String name : arr$) {
            if (servletName.equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean matchDispatcher(final FilterMap filterMap, final DispatcherType type) {
        switch (type) {
            case FORWARD: {
                if ((filterMap.getDispatcherMapping() & 0x2) != 0x0) {
                    return true;
                }
                break;
            }
            case INCLUDE: {
                if ((filterMap.getDispatcherMapping() & 0x4) != 0x0) {
                    return true;
                }
                break;
            }
            case REQUEST: {
                if ((filterMap.getDispatcherMapping() & 0x8) != 0x0) {
                    return true;
                }
                break;
            }
            case ERROR: {
                if ((filterMap.getDispatcherMapping() & 0x1) != 0x0) {
                    return true;
                }
                break;
            }
            case ASYNC: {
                if ((filterMap.getDispatcherMapping() & 0x10) != 0x0) {
                    return true;
                }
                break;
            }
        }
        return false;
    }
}
