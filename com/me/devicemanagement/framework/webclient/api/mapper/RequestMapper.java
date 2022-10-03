package com.me.devicemanagement.framework.webclient.api.mapper;

import java.util.Collection;
import java.util.Arrays;
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
            protected String operationName;
            @XmlAttribute
            protected String className;
            @XmlAttribute
            protected String methodName;
            @XmlAttribute
            protected String roleList;
            @XmlAttribute
            protected String denyEdition;
            @XmlElement(name = "ViewConfiguration")
            protected ViewConfiguration viewConfiguration;
            
            public String getOperationName() {
                return this.operationName;
            }
            
            public void setOperationName(final String operationName) {
                this.operationName = operationName;
            }
            
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
            
            public String getMethodName() {
                return this.methodName;
            }
            
            public void setMethodName(final String methodName) {
                this.methodName = methodName;
            }
            
            public String getRoleList() {
                return this.roleList;
            }
            
            public void setRoleList(final String roleList) {
                this.roleList = roleList;
            }
            
            public List<String> getDenyEdition() {
                if (this.denyEdition != null) {
                    final List<String> denyEditionList = new ArrayList<String>(Arrays.asList(this.denyEdition.split(",")));
                    return denyEditionList;
                }
                return null;
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
                protected String cvName;
                @XmlAttribute
                protected String viewController;
                @XmlAttribute
                protected String sortCol;
                @XmlAttribute
                protected String sortOrder;
                
                public String getCvName() {
                    return this.cvName;
                }
                
                public void setCvName(final String cvName) {
                    this.cvName = cvName;
                }
                
                public String getViewController() {
                    return this.viewController;
                }
                
                public void setViewController(final String viewController) {
                    this.viewController = viewController;
                }
                
                public String getSortCol() {
                    return this.sortCol;
                }
                
                public void setSortCol(final String sortCol) {
                    this.sortCol = sortCol;
                }
                
                public String getSortOrder() {
                    return this.sortOrder;
                }
                
                public void setSortOrder(final String sortOrder) {
                    this.sortOrder = sortOrder;
                }
            }
        }
    }
}
