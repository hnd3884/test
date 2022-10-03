package javax.naming.ldap;

import com.sun.jndi.ldap.LdapCtx;
import javax.naming.NamingException;
import java.io.IOException;
import com.sun.jndi.ldap.BerDecoder;

public final class SortResponseControl extends BasicControl
{
    public static final String OID = "1.2.840.113556.1.4.474";
    private static final long serialVersionUID = 5142939176006310877L;
    private int resultCode;
    private String badAttrId;
    
    public SortResponseControl(final String s, final boolean b, final byte[] array) throws IOException {
        super(s, b, array);
        this.resultCode = 0;
        this.badAttrId = null;
        final BerDecoder berDecoder = new BerDecoder(array, 0, array.length);
        berDecoder.parseSeq(null);
        this.resultCode = berDecoder.parseEnumeration();
        if (berDecoder.bytesLeft() > 0 && berDecoder.peekByte() == 128) {
            this.badAttrId = berDecoder.parseStringWithTag(128, true, null);
        }
    }
    
    public boolean isSorted() {
        return this.resultCode == 0;
    }
    
    public int getResultCode() {
        return this.resultCode;
    }
    
    public String getAttributeID() {
        return this.badAttrId;
    }
    
    public NamingException getException() {
        return LdapCtx.mapErrorCode(this.resultCode, null);
    }
}
