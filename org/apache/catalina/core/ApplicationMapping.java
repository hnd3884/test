package org.apache.catalina.core;

import javax.servlet.http.HttpServletRequestWrapper;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.RequestFacade;
import javax.servlet.http.HttpServletRequest;
import org.apache.catalina.mapper.MappingData;

public class ApplicationMapping
{
    private final MappingData mappingData;
    private volatile ApplicationMappingImpl mapping;
    
    public ApplicationMapping(final MappingData mappingData) {
        this.mapping = null;
        this.mappingData = mappingData;
    }
    
    public ApplicationMappingImpl getHttpServletMapping() {
        if (this.mapping == null) {
            if (this.mappingData == null) {
                this.mapping = new ApplicationMappingImpl("", "", null, "");
            }
            else {
                String servletName;
                if (this.mappingData.wrapper == null) {
                    servletName = "";
                }
                else {
                    servletName = this.mappingData.wrapper.getName();
                }
                if (this.mappingData.matchType == null) {
                    this.mapping = new ApplicationMappingImpl("", "", null, servletName);
                }
                else {
                    switch (this.mappingData.matchType) {
                        case CONTEXT_ROOT: {
                            this.mapping = new ApplicationMappingImpl("", "", this.mappingData.matchType, servletName);
                            break;
                        }
                        case DEFAULT: {
                            this.mapping = new ApplicationMappingImpl("", "/", this.mappingData.matchType, servletName);
                            break;
                        }
                        case EXACT: {
                            this.mapping = new ApplicationMappingImpl(this.mappingData.wrapperPath.toString().substring(1), this.mappingData.wrapperPath.toString(), this.mappingData.matchType, servletName);
                            break;
                        }
                        case EXTENSION: {
                            final String path = this.mappingData.wrapperPath.toString();
                            final int extIndex = path.lastIndexOf(46);
                            this.mapping = new ApplicationMappingImpl(path.substring(1, extIndex), "*" + path.substring(extIndex), this.mappingData.matchType, servletName);
                            break;
                        }
                        case PATH: {
                            String matchValue;
                            if (this.mappingData.pathInfo.isNull()) {
                                matchValue = null;
                            }
                            else {
                                matchValue = this.mappingData.pathInfo.toString().substring(1);
                            }
                            this.mapping = new ApplicationMappingImpl(matchValue, this.mappingData.wrapperPath.toString() + "/*", this.mappingData.matchType, servletName);
                            break;
                        }
                    }
                }
            }
        }
        return this.mapping;
    }
    
    public void recycle() {
        this.mapping = null;
    }
    
    public static ApplicationMappingImpl getHttpServletMapping(final HttpServletRequest request) {
        if (request instanceof RequestFacade) {
            return ((RequestFacade)request).getHttpServletMapping();
        }
        if (request instanceof Request) {
            return ((Request)request).getHttpServletMapping();
        }
        if (request instanceof ApplicationHttpRequest) {
            return ((ApplicationHttpRequest)request).getHttpServletMapping();
        }
        return new ApplicationMapping(null).getHttpServletMapping();
    }
    
    public static ApplicationMappingImpl getHttpServletMapping(final HttpServletRequestWrapper wrapper) {
        if (wrapper instanceof ApplicationHttpRequest) {
            return ((ApplicationHttpRequest)wrapper).getHttpServletMapping();
        }
        return new ApplicationMapping(null).getHttpServletMapping();
    }
}
