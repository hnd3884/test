package org.jscep.transaction;

public enum MessageType
{
    CERT_REP(3), 
    PKCS_REQ(19), 
    GET_CERT_INITIAL(20), 
    GET_CERT(21), 
    GET_CRL(22);
    
    private final int value;
    
    private MessageType(final int value) {
        this.value = value;
    }
    
    public int getValue() {
        return this.value;
    }
    
    public static MessageType valueOf(final int value) {
        for (final MessageType msgType : values()) {
            if (msgType.getValue() == value) {
                return msgType;
            }
        }
        throw new IllegalArgumentException();
    }
    
    @Override
    public String toString() {
        return this.name();
    }
}
