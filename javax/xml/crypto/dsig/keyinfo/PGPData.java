package javax.xml.crypto.dsig.keyinfo;

import java.util.List;
import javax.xml.crypto.XMLStructure;

public interface PGPData extends XMLStructure
{
    public static final String TYPE = "http://www.w3.org/2000/09/xmldsig#PGPData";
    
    byte[] getKeyId();
    
    byte[] getKeyPacket();
    
    List getExternalElements();
}
