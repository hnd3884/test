package com.google.api.client.googleapis.json;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public class GoogleJsonError extends GenericJson
{
    @Key
    private List<ErrorInfo> errors;
    @Key
    private int code;
    @Key
    private String message;
    @Key
    private List<Details> details;
    
    public static GoogleJsonError parse(final JsonFactory jsonFactory, final HttpResponse response) throws IOException {
        final JsonObjectParser jsonObjectParser = new JsonObjectParser.Builder(jsonFactory).setWrapperKeys((Collection)Collections.singleton("error")).build();
        return (GoogleJsonError)jsonObjectParser.parseAndClose(response.getContent(), response.getContentCharset(), (Class)GoogleJsonError.class);
    }
    
    public final List<ErrorInfo> getErrors() {
        return this.errors;
    }
    
    public final void setErrors(final List<ErrorInfo> errors) {
        this.errors = (List<ErrorInfo>)ImmutableList.copyOf((Collection)errors);
    }
    
    public final int getCode() {
        return this.code;
    }
    
    public final void setCode(final int code) {
        this.code = code;
    }
    
    public final String getMessage() {
        return this.message;
    }
    
    public final void setMessage(final String message) {
        this.message = message;
    }
    
    public List<Details> getDetails() {
        return this.details;
    }
    
    public void setDetails(final List<Details> details) {
        this.details = (List<Details>)ImmutableList.copyOf((Collection)details);
    }
    
    public GoogleJsonError set(final String fieldName, final Object value) {
        return (GoogleJsonError)super.set(fieldName, value);
    }
    
    public GoogleJsonError clone() {
        return (GoogleJsonError)super.clone();
    }
    
    static {
        Data.nullOf((Class)ErrorInfo.class);
    }
    
    public static class ErrorInfo extends GenericJson
    {
        @Key
        private String domain;
        @Key
        private String reason;
        @Key
        private String message;
        @Key
        private String location;
        @Key
        private String locationType;
        
        public final String getDomain() {
            return this.domain;
        }
        
        public final void setDomain(final String domain) {
            this.domain = domain;
        }
        
        public final String getReason() {
            return this.reason;
        }
        
        public final void setReason(final String reason) {
            this.reason = reason;
        }
        
        public final String getMessage() {
            return this.message;
        }
        
        public final void setMessage(final String message) {
            this.message = message;
        }
        
        public final String getLocation() {
            return this.location;
        }
        
        public final void setLocation(final String location) {
            this.location = location;
        }
        
        public final String getLocationType() {
            return this.locationType;
        }
        
        public final void setLocationType(final String locationType) {
            this.locationType = locationType;
        }
        
        public ErrorInfo set(final String fieldName, final Object value) {
            return (ErrorInfo)super.set(fieldName, value);
        }
        
        public ErrorInfo clone() {
            return (ErrorInfo)super.clone();
        }
    }
    
    public static class Details
    {
        @Key("@type")
        private String type;
        @Key
        private String detail;
        @Key
        private List<ParameterViolations> parameterViolations;
        
        public String getType() {
            return this.type;
        }
        
        public void setType(final String type) {
            this.type = type;
        }
        
        public String getDetail() {
            return this.detail;
        }
        
        public void setDetail(final String detail) {
            this.detail = detail;
        }
        
        public List<ParameterViolations> getParameterViolations() {
            return this.parameterViolations;
        }
        
        public void setParameterViolations(final List<ParameterViolations> parameterViolations) {
            this.parameterViolations = (List<ParameterViolations>)ImmutableList.copyOf((Collection)parameterViolations);
        }
    }
    
    public static class ParameterViolations
    {
        @Key
        private String parameter;
        @Key
        private String description;
        
        public String getDescription() {
            return this.description;
        }
        
        public void setDescription(final String description) {
            this.description = description;
        }
        
        public String getParameter() {
            return this.parameter;
        }
        
        public void setParameter(final String parameter) {
            this.parameter = parameter;
        }
    }
}
