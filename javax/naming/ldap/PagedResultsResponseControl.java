package javax.naming.ldap;

import java.io.IOException;
import com.sun.jndi.ldap.BerDecoder;

public final class PagedResultsResponseControl extends BasicControl
{
    public static final String OID = "1.2.840.113556.1.4.319";
    private static final long serialVersionUID = -8819778744844514666L;
    private int resultSize;
    private byte[] cookie;
    
    public PagedResultsResponseControl(final String s, final boolean b, final byte[] array) throws IOException {
        super(s, b, array);
        final BerDecoder berDecoder = new BerDecoder(array, 0, array.length);
        berDecoder.parseSeq(null);
        this.resultSize = berDecoder.parseInt();
        this.cookie = berDecoder.parseOctetString(4, null);
    }
    
    public int getResultSize() {
        return this.resultSize;
    }
    
    public byte[] getCookie() {
        if (this.cookie.length == 0) {
            return null;
        }
        return this.cookie;
    }
}
