package javax.xml.crypto.dsig.keyinfo;

import java.security.KeyException;
import java.security.PublicKey;
import javax.xml.crypto.XMLStructure;

public interface KeyValue extends XMLStructure
{
    public static final String DSA_TYPE = "http://www.w3.org/2000/09/xmldsig#DSAKeyValue";
    public static final String RSA_TYPE = "http://www.w3.org/2000/09/xmldsig#RSAKeyValue";
    
    PublicKey getPublicKey() throws KeyException;
}
