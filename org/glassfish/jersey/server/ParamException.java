package org.glassfish.jersey.server;

import javax.ws.rs.FormParam;
import javax.ws.rs.CookieParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.lang.annotation.Annotation;
import javax.ws.rs.WebApplicationException;

public abstract class ParamException extends WebApplicationException
{
    private static final long serialVersionUID = -2359567574412607846L;
    private final Class<? extends Annotation> parameterType;
    private final String name;
    private final String defaultStringValue;
    
    protected ParamException(final Throwable cause, final Response.StatusType status, final Class<? extends Annotation> parameterType, final String name, final String defaultStringValue) {
        super(cause, status.getStatusCode());
        this.parameterType = parameterType;
        this.name = name;
        this.defaultStringValue = defaultStringValue;
    }
    
    public Class<? extends Annotation> getParameterType() {
        return this.parameterType;
    }
    
    public String getParameterName() {
        return this.name;
    }
    
    public String getDefaultStringValue() {
        return this.defaultStringValue;
    }
    
    public abstract static class UriParamException extends ParamException
    {
        private static final long serialVersionUID = 44233528459885541L;
        
        protected UriParamException(final Throwable cause, final Class<? extends Annotation> parameterType, final String name, final String defaultStringValue) {
            super(cause, (Response.StatusType)Response.Status.NOT_FOUND, parameterType, name, defaultStringValue);
        }
    }
    
    public static class PathParamException extends UriParamException
    {
        private static final long serialVersionUID = -2708538214692835633L;
        
        public PathParamException(final Throwable cause, final String name, final String defaultStringValue) {
            super(cause, (Class<? extends Annotation>)PathParam.class, name, defaultStringValue);
        }
    }
    
    public static class MatrixParamException extends UriParamException
    {
        private static final long serialVersionUID = -5849392883623736362L;
        
        public MatrixParamException(final Throwable cause, final String name, final String defaultStringValue) {
            super(cause, (Class<? extends Annotation>)MatrixParam.class, name, defaultStringValue);
        }
    }
    
    public static class QueryParamException extends UriParamException
    {
        private static final long serialVersionUID = -4822407467792322910L;
        
        public QueryParamException(final Throwable cause, final String name, final String defaultStringValue) {
            super(cause, (Class<? extends Annotation>)QueryParam.class, name, defaultStringValue);
        }
    }
    
    public static class HeaderParamException extends ParamException
    {
        private static final long serialVersionUID = 6508174603506313274L;
        
        public HeaderParamException(final Throwable cause, final String name, final String defaultStringValue) {
            super(cause, (Response.StatusType)Response.Status.BAD_REQUEST, (Class<? extends Annotation>)HeaderParam.class, name, defaultStringValue);
        }
    }
    
    public static class CookieParamException extends ParamException
    {
        private static final long serialVersionUID = -5288504201234567266L;
        
        public CookieParamException(final Throwable cause, final String name, final String defaultStringValue) {
            super(cause, (Response.StatusType)Response.Status.BAD_REQUEST, (Class<? extends Annotation>)CookieParam.class, name, defaultStringValue);
        }
    }
    
    public static class FormParamException extends ParamException
    {
        private static final long serialVersionUID = -1704379792199980689L;
        
        public FormParamException(final Throwable cause, final String name, final String defaultStringValue) {
            super(cause, (Response.StatusType)Response.Status.BAD_REQUEST, (Class<? extends Annotation>)FormParam.class, name, defaultStringValue);
        }
    }
}
