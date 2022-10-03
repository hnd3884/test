package org.jscep.transport.response;

public enum Capability
{
    AES("AES", "AES Encryption"), 
    GET_NEXT_CA_CERT("GetNextCACert", "Certificate Rollover"), 
    POST_PKI_OPERATION("POSTPKIOperation", "HTTP POST"), 
    RENEWAL("Renewal", "Certificate Renewal"), 
    SCEP_STANDARD("SCEPStandard", "SCEP Standard"), 
    SHA_512("SHA-512", "SHA-512 Message Digest"), 
    SHA_256("SHA-256", "SHA-256 Message Digest"), 
    SHA_1("SHA-1", "SHA-1 Message Digest"), 
    TRIPLE_DES("DES3", "Triple DES Encryption"), 
    UPDATE("Update", "Certificate Update");
    
    private final String capability;
    private final String description;
    
    private Capability(final String capability, final String description) {
        this.capability = capability;
        this.description = description;
    }
    
    @Override
    public String toString() {
        return this.capability;
    }
    
    public String getDescription() {
        return this.description;
    }
}
