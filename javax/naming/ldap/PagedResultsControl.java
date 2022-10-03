package javax.naming.ldap;

import com.sun.jndi.ldap.BerEncoder;
import java.io.IOException;

public final class PagedResultsControl extends BasicControl
{
    public static final String OID = "1.2.840.113556.1.4.319";
    private static final byte[] EMPTY_COOKIE;
    private static final long serialVersionUID = 6684806685736844298L;
    
    public PagedResultsControl(final int n, final boolean b) throws IOException {
        super("1.2.840.113556.1.4.319", b, null);
        this.value = this.setEncodedValue(n, PagedResultsControl.EMPTY_COOKIE);
    }
    
    public PagedResultsControl(final int n, byte[] empty_COOKIE, final boolean b) throws IOException {
        super("1.2.840.113556.1.4.319", b, null);
        if (empty_COOKIE == null) {
            empty_COOKIE = PagedResultsControl.EMPTY_COOKIE;
        }
        this.value = this.setEncodedValue(n, empty_COOKIE);
    }
    
    private byte[] setEncodedValue(final int n, final byte[] array) throws IOException {
        final BerEncoder berEncoder = new BerEncoder(10 + array.length);
        berEncoder.beginSeq(48);
        berEncoder.encodeInt(n);
        berEncoder.encodeOctetString(array, 4);
        berEncoder.endSeq();
        return berEncoder.getTrimmedBuf();
    }
    
    static {
        EMPTY_COOKIE = new byte[0];
    }
}
