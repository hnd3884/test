package org.bouncycastle.cert.cmp;

import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.PKIHeader;
import java.io.IOException;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.cmp.PKIMessage;

public class GeneralPKIMessage
{
    private final PKIMessage pkiMessage;
    
    private static PKIMessage parseBytes(final byte[] array) throws IOException {
        try {
            return PKIMessage.getInstance((Object)ASN1Primitive.fromByteArray(array));
        }
        catch (final ClassCastException ex) {
            throw new CertIOException("malformed data: " + ex.getMessage(), ex);
        }
        catch (final IllegalArgumentException ex2) {
            throw new CertIOException("malformed data: " + ex2.getMessage(), ex2);
        }
    }
    
    public GeneralPKIMessage(final byte[] array) throws IOException {
        this(parseBytes(array));
    }
    
    public GeneralPKIMessage(final PKIMessage pkiMessage) {
        this.pkiMessage = pkiMessage;
    }
    
    public PKIHeader getHeader() {
        return this.pkiMessage.getHeader();
    }
    
    public PKIBody getBody() {
        return this.pkiMessage.getBody();
    }
    
    public boolean hasProtection() {
        return this.pkiMessage.getHeader().getProtectionAlg() != null;
    }
    
    public PKIMessage toASN1Structure() {
        return this.pkiMessage;
    }
}
