package com.sun.xml.internal.ws.encoding;

import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.pipe.ContentType;

public final class ContentTypeImpl implements ContentType
{
    @NotNull
    private final String contentType;
    @NotNull
    private final String soapAction;
    private String accept;
    @Nullable
    private final String charset;
    private String boundary;
    private String boundaryParameter;
    private String rootId;
    private com.sun.xml.internal.ws.encoding.ContentType internalContentType;
    
    public ContentTypeImpl(final String contentType) {
        this(contentType, null, null);
    }
    
    public ContentTypeImpl(final String contentType, @Nullable final String soapAction) {
        this(contentType, soapAction, null);
    }
    
    public ContentTypeImpl(final String contentType, @Nullable final String soapAction, @Nullable final String accept) {
        this(contentType, soapAction, accept, null);
    }
    
    public ContentTypeImpl(final String contentType, @Nullable final String soapAction, @Nullable final String accept, final String charsetParam) {
        this.contentType = contentType;
        this.accept = accept;
        this.soapAction = this.getQuotedSOAPAction(soapAction);
        if (charsetParam == null) {
            String tmpCharset = null;
            try {
                this.internalContentType = new com.sun.xml.internal.ws.encoding.ContentType(contentType);
                tmpCharset = this.internalContentType.getParameter("charset");
            }
            catch (final Exception ex) {}
            this.charset = tmpCharset;
        }
        else {
            this.charset = charsetParam;
        }
    }
    
    @Nullable
    public String getCharSet() {
        return this.charset;
    }
    
    private String getQuotedSOAPAction(final String soapAction) {
        if (soapAction == null || soapAction.length() == 0) {
            return "\"\"";
        }
        if (soapAction.charAt(0) != '\"' && soapAction.charAt(soapAction.length() - 1) != '\"') {
            return "\"" + soapAction + "\"";
        }
        return soapAction;
    }
    
    @Override
    public String getContentType() {
        return this.contentType;
    }
    
    @Override
    public String getSOAPActionHeader() {
        return this.soapAction;
    }
    
    @Override
    public String getAcceptHeader() {
        return this.accept;
    }
    
    public void setAcceptHeader(final String accept) {
        this.accept = accept;
    }
    
    public String getBoundary() {
        if (this.boundary == null) {
            if (this.internalContentType == null) {
                this.internalContentType = new com.sun.xml.internal.ws.encoding.ContentType(this.contentType);
            }
            this.boundary = this.internalContentType.getParameter("boundary");
        }
        return this.boundary;
    }
    
    public void setBoundary(final String boundary) {
        this.boundary = boundary;
    }
    
    public String getBoundaryParameter() {
        return this.boundaryParameter;
    }
    
    public void setBoundaryParameter(final String boundaryParameter) {
        this.boundaryParameter = boundaryParameter;
    }
    
    public String getRootId() {
        if (this.rootId == null) {
            if (this.internalContentType == null) {
                this.internalContentType = new com.sun.xml.internal.ws.encoding.ContentType(this.contentType);
            }
            this.rootId = this.internalContentType.getParameter("start");
        }
        return this.rootId;
    }
    
    public void setRootId(final String rootId) {
        this.rootId = rootId;
    }
    
    public static class Builder
    {
        public String contentType;
        public String soapAction;
        public String accept;
        public String charset;
        
        public ContentTypeImpl build() {
            return new ContentTypeImpl(this.contentType, this.soapAction, this.accept, this.charset);
        }
    }
}
