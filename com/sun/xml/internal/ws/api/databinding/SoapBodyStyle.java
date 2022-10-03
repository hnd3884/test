package com.sun.xml.internal.ws.api.databinding;

public enum SoapBodyStyle
{
    DocumentBare, 
    DocumentWrapper, 
    RpcLiteral, 
    RpcEncoded, 
    Unspecificed;
    
    public boolean isDocument() {
        return this.equals(SoapBodyStyle.DocumentBare) || this.equals(SoapBodyStyle.DocumentWrapper);
    }
    
    public boolean isRpc() {
        return this.equals(SoapBodyStyle.RpcLiteral) || this.equals(SoapBodyStyle.RpcEncoded);
    }
    
    public boolean isLiteral() {
        return this.equals(SoapBodyStyle.RpcLiteral) || this.isDocument();
    }
    
    public boolean isBare() {
        return this.equals(SoapBodyStyle.DocumentBare);
    }
    
    public boolean isDocumentWrapper() {
        return this.equals(SoapBodyStyle.DocumentWrapper);
    }
}
