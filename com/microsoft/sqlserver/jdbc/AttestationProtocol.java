package com.microsoft.sqlserver.jdbc;

enum AttestationProtocol
{
    HGS("HGS"), 
    AAS("AAS");
    
    private final String protocol;
    
    private AttestationProtocol(final String protocol) {
        this.protocol = protocol;
    }
    
    static boolean isValidAttestationProtocol(final String protocol) {
        for (final AttestationProtocol p : values()) {
            if (protocol.equalsIgnoreCase(p.toString())) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return this.protocol;
    }
}
