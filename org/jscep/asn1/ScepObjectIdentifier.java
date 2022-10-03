package org.jscep.asn1;

public enum ScepObjectIdentifier
{
    MESSAGE_TYPE("2.16.840.1.113733.1.9.2"), 
    PKI_STATUS("2.16.840.1.113733.1.9.3"), 
    FAIL_INFO("2.16.840.1.113733.1.9.4"), 
    SENDER_NONCE("2.16.840.1.113733.1.9.5"), 
    RECIPIENT_NONCE("2.16.840.1.113733.1.9.6"), 
    TRANS_ID("2.16.840.1.113733.1.9.7");
    
    private final String objId;
    
    private ScepObjectIdentifier(final String objId) {
        this.objId = objId;
    }
    
    public String id() {
        return this.objId;
    }
}
