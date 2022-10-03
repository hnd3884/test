package javax.servlet.jsp.jstl.core;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.PageContext;

public class Config
{
    public static final String FMT_LOCALE = "javax.servlet.jsp.jstl.fmt.locale";
    public static final String FMT_FALLBACK_LOCALE = "javax.servlet.jsp.jstl.fmt.fallbackLocale";
    public static final String FMT_LOCALIZATION_CONTEXT = "javax.servlet.jsp.jstl.fmt.localizationContext";
    public static final String FMT_TIME_ZONE = "javax.servlet.jsp.jstl.fmt.timeZone";
    public static final String SQL_DATA_SOURCE = "javax.servlet.jsp.jstl.sql.dataSource";
    public static final String SQL_MAX_ROWS = "javax.servlet.jsp.jstl.sql.maxRows";
    private static final String PAGE_SCOPE_SUFFIX = ".page";
    private static final String REQUEST_SCOPE_SUFFIX = ".request";
    private static final String SESSION_SCOPE_SUFFIX = ".session";
    private static final String APPLICATION_SCOPE_SUFFIX = ".application";
    
    public static Object get(final PageContext pc, final String name, final int scope) {
        switch (scope) {
            case 1: {
                return pc.getAttribute(name + ".page", scope);
            }
            case 2: {
                return pc.getAttribute(name + ".request", scope);
            }
            case 3: {
                return get(pc.getSession(), name);
            }
            case 4: {
                return pc.getAttribute(name + ".application", scope);
            }
            default: {
                throw new IllegalArgumentException("unknown scope");
            }
        }
    }
    
    public static Object get(final ServletRequest request, final String name) {
        return request.getAttribute(name + ".request");
    }
    
    public static Object get(final HttpSession session, final String name) {
        Object ret = null;
        if (session != null) {
            try {
                ret = session.getAttribute(name + ".session");
            }
            catch (final IllegalStateException ex) {}
        }
        return ret;
    }
    
    public static Object get(final ServletContext context, final String name) {
        return context.getAttribute(name + ".application");
    }
    
    public static void set(final PageContext pc, final String name, final Object value, final int scope) {
        switch (scope) {
            case 1: {
                pc.setAttribute(name + ".page", value, scope);
                break;
            }
            case 2: {
                pc.setAttribute(name + ".request", value, scope);
                break;
            }
            case 3: {
                pc.setAttribute(name + ".session", value, scope);
                break;
            }
            case 4: {
                pc.setAttribute(name + ".application", value, scope);
                break;
            }
            default: {
                throw new IllegalArgumentException("unknown scope");
            }
        }
    }
    
    public static void set(final ServletRequest request, final String name, final Object value) {
        request.setAttribute(name + ".request", value);
    }
    
    public static void set(final HttpSession session, final String name, final Object value) {
        session.setAttribute(name + ".session", value);
    }
    
    public static void set(final ServletContext context, final String name, final Object value) {
        context.setAttribute(name + ".application", value);
    }
    
    public static void remove(final PageContext pc, final String name, final int scope) {
        switch (scope) {
            case 1: {
                pc.removeAttribute(name + ".page", scope);
                break;
            }
            case 2: {
                pc.removeAttribute(name + ".request", scope);
                break;
            }
            case 3: {
                pc.removeAttribute(name + ".session", scope);
                break;
            }
            case 4: {
                pc.removeAttribute(name + ".application", scope);
                break;
            }
            default: {
                throw new IllegalArgumentException("unknown scope");
            }
        }
    }
    
    public static void remove(final ServletRequest request, final String name) {
        request.removeAttribute(name + ".request");
    }
    
    public static void remove(final HttpSession session, final String name) {
        session.removeAttribute(name + ".session");
    }
    
    public static void remove(final ServletContext context, final String name) {
        context.removeAttribute(name + ".application");
    }
    
    public static Object find(final PageContext pc, final String name) {
        Object ret = get(pc, name, 1);
        if (ret == null) {
            ret = get(pc, name, 2);
            if (ret == null) {
                if (pc.getSession() != null) {
                    ret = get(pc, name, 3);
                }
                if (ret == null) {
                    ret = get(pc, name, 4);
                    if (ret == null) {
                        ret = pc.getServletContext().getInitParameter(name);
                    }
                }
            }
        }
        return ret;
    }
}
