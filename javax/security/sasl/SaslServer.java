package javax.security.sasl;

public interface SaslServer
{
    String getMechanismName();
    
    byte[] evaluateResponse(final byte[] p0) throws SaslException;
    
    boolean isComplete();
    
    String getAuthorizationID();
    
    byte[] unwrap(final byte[] p0, final int p1, final int p2) throws SaslException;
    
    byte[] wrap(final byte[] p0, final int p1, final int p2) throws SaslException;
    
    Object getNegotiatedProperty(final String p0);
    
    void dispose() throws SaslException;
}
