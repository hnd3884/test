package com.adventnet.webclient.util;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import javax.servlet.jsp.PageContext;
import javax.servlet.http.HttpServletRequest;

public class ValueRetriever
{
    private HttpServletRequest request;
    private PageContext pageContext;
    private Properties properties;
    public static final String REQUEST = "request";
    public static final String SESSION = "session";
    public static final String DATAMODEL = "dataModel";
    public static final String APPLICATION = "application";
    public static final String PAGE = "page";
    
    public ValueRetriever(final PageContext pageContext, final Properties properties) {
        this.request = null;
        this.pageContext = null;
        this.properties = null;
        this.pageContext = pageContext;
        this.request = (HttpServletRequest)pageContext.getRequest();
        this.properties = properties;
    }
    
    public ValueRetriever(final PageContext pageContext) {
        this.request = null;
        this.pageContext = null;
        this.properties = null;
        this.pageContext = pageContext;
        this.request = (HttpServletRequest)pageContext.getRequest();
    }
    
    public void setDataModel(final Properties properties) {
        this.properties = properties;
    }
    
    public Object getValue(final String paramName, final String scope) {
        Object value = null;
        if (scope.equals("request")) {
            value = this.request.getParameter(paramName);
            if (value == null) {
                value = this.request.getAttribute(paramName);
            }
        }
        else if (scope.equals("session")) {
            value = this.pageContext.getAttribute(paramName, 3);
        }
        else if (scope.equals("application")) {
            value = this.pageContext.getAttribute(paramName, 4);
        }
        else if (scope.equals("page")) {
            value = this.pageContext.getAttribute(paramName, 1);
        }
        else if (scope.equals("dataModel") && this.properties != null) {
            value = this.properties.getProperty(paramName);
        }
        return value;
    }
    
    public Properties getAllProperties(final Properties prop, final String scope) {
        Iterator iterator = null;
        Enumeration enum1 = null;
        if (scope.equals("dataModel")) {
            iterator = ((Hashtable<Object, V>)this.properties).keySet().iterator();
            if (iterator != null) {
                while (iterator.hasNext()) {
                    final String paramName = iterator.next();
                    final Object paramValue = ((Hashtable<K, Object>)this.properties).get(paramName);
                    ((Hashtable<String, Object>)prop).put(paramName, paramValue);
                }
            }
        }
        else {
            int scopeNum = 1;
            if (scope.equals("session")) {
                scopeNum = 3;
            }
            else if (scope.equals("application")) {
                scopeNum = 4;
            }
            else if (scope.equals("request")) {
                scopeNum = 2;
            }
            if (scope.equalsIgnoreCase("request")) {
                enum1 = this.request.getParameterNames();
                while (enum1.hasMoreElements()) {
                    final String paramName2 = enum1.nextElement();
                    final String paramValue2 = this.request.getParameter(paramName2);
                    if (paramValue2 != null) {
                        ((Hashtable<String, String>)prop).put(paramName2, paramValue2);
                    }
                }
            }
            enum1 = this.pageContext.getAttributeNamesInScope(scopeNum);
            while (enum1.hasMoreElements()) {
                final String paramName2 = enum1.nextElement();
                final Object paramValue3 = this.pageContext.getAttribute(paramName2, scopeNum);
                if (paramValue3 != null) {
                    ((Hashtable<String, Object>)prop).put(paramName2, paramValue3);
                }
            }
        }
        return prop;
    }
    
    public PageContext getPageContext() {
        return this.pageContext;
    }
}
