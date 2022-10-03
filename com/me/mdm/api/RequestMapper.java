package com.me.mdm.api;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "entity" })
@XmlRootElement(name = "RequestMapper")
public class RequestMapper
{
    @XmlElement(name = "Entity")
    protected List<Entity> entity;
    @XmlAttribute(name = "version")
    protected String version;
    
    public List<Entity> getEntity() {
        if (this.entity == null) {
            this.entity = new ArrayList<Entity>();
        }
        return this.entity;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    public void setVersion(final String version) {
        this.version = version;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = { "request" })
    public static class Entity
    {
        @XmlAttribute(name = "name")
        protected String name;
        @XmlElement(name = "Request")
        protected List<Request> request;
        
        public String getName() {
            return this.name;
        }
        
        public void setName(final String name) {
            this.name = name;
        }
        
        public List<Request> getRequest() {
            if (this.request == null) {
                this.request = new ArrayList<Request>();
            }
            return this.request;
        }
        
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = { "viewConfiguration" })
        public static class Request
        {
            @XmlAttribute
            protected String uri;
            @XmlAttribute
            protected String className;
            @XmlAttribute
            protected String caseInsensitiveResponse;
            @XmlAttribute
            protected String caseInsensitiveRequest;
            @XmlAttribute
            protected String inputContentType;
            @XmlElement(name = "ViewConfiguration")
            protected ViewConfiguration viewConfiguration;
            
            public String getUri() {
                return this.uri;
            }
            
            public void setUri(final String uri) {
                this.uri = uri;
            }
            
            public String getClassName() {
                return this.className;
            }
            
            public void setClassName(final String className) {
                this.className = className;
            }
            
            public String getCaseInsensitiveResponse() {
                return this.caseInsensitiveResponse;
            }
            
            public void setCaseInsensitiveResponse(final String caseInsensitiveResponse) {
                this.caseInsensitiveResponse = caseInsensitiveResponse;
            }
            
            public String getCaseInsensitiveRequest() {
                return this.caseInsensitiveRequest;
            }
            
            public String getInputContentType() {
                return this.inputContentType;
            }
            
            public void setInputContentType(final String inputContentType) {
                this.inputContentType = inputContentType;
            }
            
            public void setCaseInsensitiveRequest(final String caseInsensitiveRequest) {
                this.caseInsensitiveRequest = caseInsensitiveRequest;
            }
            
            public ViewConfiguration getViewConfiguration() {
                if (this.viewConfiguration == null) {
                    this.viewConfiguration = new ViewConfiguration();
                }
                return this.viewConfiguration;
            }
            
            public void setViewConfiguration(final ViewConfiguration viewConfiguration) {
                this.viewConfiguration = viewConfiguration;
            }
            
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "")
            public static class ViewConfiguration
            {
                @XmlAttribute
                protected String viewName;
                
                public String getViewName() {
                    return this.viewName;
                }
                
                public void setViewName(final String viewName) {
                    this.viewName = viewName;
                }
            }
        }
    }
}
