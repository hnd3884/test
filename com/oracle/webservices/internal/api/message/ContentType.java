package com.oracle.webservices.internal.api.message;

import com.sun.xml.internal.ws.encoding.ContentTypeImpl;

public interface ContentType
{
    String getContentType();
    
    String getSOAPActionHeader();
    
    String getAcceptHeader();
    
    public static class Builder
    {
        private String contentType;
        private String soapAction;
        private String accept;
        private String charset;
        
        public Builder contentType(final String s) {
            this.contentType = s;
            return this;
        }
        
        public Builder soapAction(final String s) {
            this.soapAction = s;
            return this;
        }
        
        public Builder accept(final String s) {
            this.accept = s;
            return this;
        }
        
        public Builder charset(final String s) {
            this.charset = s;
            return this;
        }
        
        public ContentType build() {
            return new ContentTypeImpl(this.contentType, this.soapAction, this.accept, this.charset);
        }
    }
}
