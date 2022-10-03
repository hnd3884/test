package org.apache.commons.chain.web.portlet;

import java.util.Collections;
import javax.portlet.PortletResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletContext;
import java.util.Map;
import org.apache.commons.chain.web.WebContext;

public class PortletWebContext extends WebContext
{
    private Map applicationScope;
    protected PortletContext context;
    private Map header;
    private Map headerValues;
    private Map initParam;
    private Map param;
    private Map paramValues;
    protected PortletRequest request;
    private Map requestScope;
    protected PortletResponse response;
    private Map sessionScope;
    
    public PortletWebContext() {
        this.applicationScope = null;
        this.context = null;
        this.header = null;
        this.headerValues = null;
        this.initParam = null;
        this.param = null;
        this.paramValues = null;
        this.request = null;
        this.requestScope = null;
        this.response = null;
        this.sessionScope = null;
    }
    
    public PortletWebContext(final PortletContext context, final PortletRequest request, final PortletResponse response) {
        this.applicationScope = null;
        this.context = null;
        this.header = null;
        this.headerValues = null;
        this.initParam = null;
        this.param = null;
        this.paramValues = null;
        this.request = null;
        this.requestScope = null;
        this.response = null;
        this.sessionScope = null;
        this.initialize(context, request, response);
    }
    
    public PortletContext getContext() {
        return this.context;
    }
    
    public PortletRequest getRequest() {
        return this.request;
    }
    
    public PortletResponse getResponse() {
        return this.response;
    }
    
    public void initialize(final PortletContext context, final PortletRequest request, final PortletResponse response) {
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
        this.requestScope = null;
        this.sessionScope = null;
        this.context = null;
        this.request = null;
        this.response = null;
    }
    
    public Map getApplicationScope() {
        if (this.applicationScope == null && this.context != null) {
            this.applicationScope = new PortletApplicationScopeMap(this.context);
        }
        return this.applicationScope;
    }
    
    public Map getHeader() {
        if (this.header == null && this.request != null) {
            this.header = Collections.EMPTY_MAP;
        }
        return this.header;
    }
    
    public Map getHeaderValues() {
        if (this.headerValues == null && this.request != null) {
            this.headerValues = Collections.EMPTY_MAP;
        }
        return this.headerValues;
    }
    
    public Map getInitParam() {
        if (this.initParam == null && this.context != null) {
            this.initParam = new PortletInitParamMap(this.context);
        }
        return this.initParam;
    }
    
    public Map getParam() {
        if (this.param == null && this.request != null) {
            this.param = new PortletParamMap(this.request);
        }
        return this.param;
    }
    
    public Map getParamValues() {
        if (this.paramValues == null && this.request != null) {
            this.paramValues = new PortletParamValuesMap(this.request);
        }
        return this.paramValues;
    }
    
    public Map getCookies() {
        return Collections.EMPTY_MAP;
    }
    
    public Map getRequestScope() {
        if (this.requestScope == null && this.request != null) {
            this.requestScope = new PortletRequestScopeMap(this.request);
        }
        return this.requestScope;
    }
    
    public Map getSessionScope() {
        if (this.sessionScope == null && this.request != null) {
            this.sessionScope = new PortletSessionScopeMap(this.request);
        }
        return this.sessionScope;
    }
}
