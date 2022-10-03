package javax.security.sasl;

public interface SaslClient
{
    String getMechanismName();
    
    boolean hasInitialResponse();
    
    byte[] evaluateChallenge(final byte[] p0) throws SaslException;
    
    boolean isComplete();
    
    byte[] unwrap(final byte[] p0, final int p1, final int p2) throws SaslException;
    
    byte[] wrap(final byte[] p0, final int p1, final int p2) throws SaslException;
    
    Object getNegotiatedProperty(final String p0);
    
    void dispose() throws SaslException;
}
