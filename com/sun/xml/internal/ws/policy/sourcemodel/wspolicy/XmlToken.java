package com.sun.xml.internal.ws.policy.sourcemodel.wspolicy;

public enum XmlToken
{
    Policy("Policy", true), 
    ExactlyOne("ExactlyOne", true), 
    All("All", true), 
    PolicyReference("PolicyReference", true), 
    UsingPolicy("UsingPolicy", true), 
    Name("Name", false), 
    Optional("Optional", false), 
    Ignorable("Ignorable", false), 
    PolicyUris("PolicyURIs", false), 
    Uri("URI", false), 
    Digest("Digest", false), 
    DigestAlgorithm("DigestAlgorithm", false), 
    UNKNOWN("", true);
    
    private String tokenName;
    private boolean element;
    
    public static XmlToken resolveToken(final String name) {
        for (final XmlToken token : values()) {
            if (token.toString().equals(name)) {
                return token;
            }
        }
        return XmlToken.UNKNOWN;
    }
    
    private XmlToken(final String name, final boolean element) {
        this.tokenName = name;
        this.element = element;
    }
    
    public boolean isElement() {
        return this.element;
    }
    
    @Override
    public String toString() {
        return this.tokenName;
    }
}
