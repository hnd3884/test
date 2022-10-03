package java.security;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

@Deprecated
public interface Certificate
{
    Principal getGuarantor();
    
    Principal getPrincipal();
    
    PublicKey getPublicKey();
    
    void encode(final OutputStream p0) throws KeyException, IOException;
    
    void decode(final InputStream p0) throws KeyException, IOException;
    
    String getFormat();
    
    String toString(final boolean p0);
}
