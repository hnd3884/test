package org.apache.commons.chain.web.servlet;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;
import java.util.Map;
import org.apache.commons.chain.web.WebContext;

public class ServletWebContext extends WebContext
{
    private Map applicationScope;
    protected ServletContext context;
    private Map header;
    private Map headerValues;
    private Map initParam;
    private Map cookieValues;
    private Map param;
    private Map paramValues;
    protected HttpServletRequest request;
    private Map requestScope;
    protected HttpServletResponse response;
    private Map sessionScope;
    
    public ServletWebContext() {
        this.applicationScope = null;
        this.context = null;
        this.header = null;
        this.headerValues = null;
        this.initParam = null;
        this.cookieValues = null;
        this.param = null;
        this.paramValues = null;
        this.request = null;
        this.requestScope = null;
        this.response = null;
        this.sessionScope = null;
    }
    
    public ServletWebContext(final ServletContext context, final HttpServletRequest request, final HttpServletResponse response) {
        this.applicationScope = null;
        this.context = null;
        this.header = null;
        this.headerValues = null;
        this.initParam = null;
        this.cookieValues = null;
        this.param = null;
        this.paramValues = null;
        this.request = null;
        this.requestScope = null;
        this.response = null;
        this.sessionScope = null;
        this.initialize(context, request, response);
    }
    
    public ServletContext getContext() {
        return this.context;
    }
    
    public HttpServletRequest getRequest() {
        return this.request;
    }
    
    public HttpServletResponse getResponse() {
        return this.response;
    }
    
    public void initialize(final ServletContext context, final HttpServletRequest request, final HttpServletResponse response) {
        this.context = context;
        this.request = request;
        this.response = response;
    }
    
    public void release() {
        this.applicationScope = null;
        this.header = null;
        this.headerValues = null;
        this.initParam = null;
        this.param = null;
        this.paramValues = null;
        this.cookieValues = null;
        this.requestScope = null;
        this.sessionScope = null;
        this.context = null;
        this.request = null;
        this.response = null;
    }
    
    public Map getApplicationScope() {
        if (this.applicationScope == null && this.context != null) {
            this.applicationScope = new ServletApplicationScopeMap(this.context);
        }
        return this.applicationScope;
    }
    
    public Map getHeader() {
        if (this.header == null && this.request != null) {
            this.header = new ServletHeaderMap(this.request);
        }
        return this.header;
    }
    
    public Map getHeaderValues() {
        if (this.headerValues == null && this.request != null) {
            this.headerValues = new ServletHeaderValuesMap(this.request);
        }
        return this.headerValues;
    }
    
    public Map getInitParam() {
        if (this.initParam == null && this.context != null) {
            this.initParam = new ServletInitParamMap(this.context);
        }
        return this.initParam;
    }
    
    public Map getParam() {
        if (this.param == null && this.request != null) {
            this.param = new ServletParamMap(this.request);
        }
        return this.param;
    }
    
    public Map getParamValues() {
        if (this.paramValues == null && this.request != null) {
            this.paramValues = new ServletParamValuesMap(this.request);
        }
        return this.paramValues;
    }
    
    public Map getCookies() {
        if (this.cookieValues == null && this.request != null) {
            this.cookieValues = new ServletCookieMap(this.request);
        }
        return this.cookieValues;
    }
    
    public Map getRequestScope() {
        if (this.requestScope == null && this.request != null) {
            this.requestScope = new ServletRequestScopeMap(this.request);
        }
        return this.requestScope;
    }
    
    public Map getSessionScope() {
        if (this.sessionScope == null && this.request != null) {
            this.sessionScope = new ServletSessionScopeMap(this.request);
        }
        return this.sessionScope;
    }
}
