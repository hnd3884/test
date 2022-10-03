package org.apache.taglibs.standard.tag.common.core;

import java.util.Vector;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import org.apache.taglibs.standard.resources.Resources;

public class Util
{
    private static final String REQUEST = "request";
    private static final String SESSION = "session";
    private static final String APPLICATION = "application";
    private static final String DEFAULT = "default";
    private static final String SHORT = "short";
    private static final String MEDIUM = "medium";
    private static final String LONG = "long";
    private static final String FULL = "full";
    
    public static int getScope(final String scope) {
        int ret = 1;
        if ("request".equalsIgnoreCase(scope)) {
            ret = 2;
        }
        else if ("session".equalsIgnoreCase(scope)) {
            ret = 3;
        }
        else if ("application".equalsIgnoreCase(scope)) {
            ret = 4;
        }
        return ret;
    }
    
    public static int getStyle(final String style, final String errCode) throws JspException {
        int ret = 2;
        if (style != null) {
            if ("default".equalsIgnoreCase(style)) {
                ret = 2;
            }
            else if ("short".equalsIgnoreCase(style)) {
                ret = 3;
            }
            else if ("medium".equalsIgnoreCase(style)) {
                ret = 2;
            }
            else if ("long".equalsIgnoreCase(style)) {
                ret = 1;
            }
            else {
                if (!"full".equalsIgnoreCase(style)) {
                    throw new JspException(Resources.getMessage(errCode, style));
                }
                ret = 0;
            }
        }
        return ret;
    }
    
    public static String getContentTypeAttribute(String input, final String name) {
        int index = input.toUpperCase().indexOf(name.toUpperCase());
        if (index == -1) {
            return null;
        }
        index += name.length();
        index = input.indexOf(61, index);
        if (index == -1) {
            return null;
        }
        ++index;
        input = input.substring(index).trim();
        int begin;
        int end;
        if (input.charAt(0) == '\"') {
            begin = 1;
            end = input.indexOf(34, begin);
            if (end == -1) {
                return null;
            }
        }
        else {
            begin = 0;
            end = input.indexOf(59);
            if (end == -1) {
                end = input.indexOf(32);
            }
            if (end == -1) {
                end = input.length();
            }
        }
        return input.substring(begin, end).trim();
    }
    
    public static Enumeration getRequestLocales(final HttpServletRequest request) {
        final Enumeration values = request.getHeaders("accept-language");
        if (values == null) {
            return new Vector().elements();
        }
        if (values.hasMoreElements()) {
            return request.getLocales();
        }
        return values;
    }
}
