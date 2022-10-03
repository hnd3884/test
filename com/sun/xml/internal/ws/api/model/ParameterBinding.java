package com.sun.xml.internal.ws.api.model;

public final class ParameterBinding
{
    public static final ParameterBinding BODY;
    public static final ParameterBinding HEADER;
    public static final ParameterBinding UNBOUND;
    public final Kind kind;
    private String mimeType;
    
    public static ParameterBinding createAttachment(final String mimeType) {
        return new ParameterBinding(Kind.ATTACHMENT, mimeType);
    }
    
    private ParameterBinding(final Kind kind, final String mimeType) {
        this.kind = kind;
        this.mimeType = mimeType;
    }
    
    @Override
    public String toString() {
        return this.kind.toString();
    }
    
    public String getMimeType() {
        if (!this.isAttachment()) {
            throw new IllegalStateException();
        }
        return this.mimeType;
    }
    
    public boolean isBody() {
        return this == ParameterBinding.BODY;
    }
    
    public boolean isHeader() {
        return this == ParameterBinding.HEADER;
    }
    
    public boolean isUnbound() {
        return this == ParameterBinding.UNBOUND;
    }
    
    public boolean isAttachment() {
        return this.kind == Kind.ATTACHMENT;
    }
    
    static {
        BODY = new ParameterBinding(Kind.BODY, null);
        HEADER = new ParameterBinding(Kind.HEADER, null);
        UNBOUND = new ParameterBinding(Kind.UNBOUND, null);
    }
    
    public enum Kind
    {
        BODY, 
        HEADER, 
        UNBOUND, 
        ATTACHMENT;
    }
}
