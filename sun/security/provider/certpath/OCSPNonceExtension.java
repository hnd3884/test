package sun.security.provider.certpath;

import sun.security.util.Debug;
import java.util.Objects;
import sun.security.util.DerValue;
import java.security.SecureRandom;
import sun.security.x509.PKIXExtensions;
import java.io.IOException;
import sun.security.x509.Extension;

public final class OCSPNonceExtension extends Extension
{
    private static final String EXTENSION_NAME = "OCSPNonce";
    private byte[] nonceData;
    
    public OCSPNonceExtension(final int n) throws IOException {
        this(false, n);
    }
    
    public OCSPNonceExtension(final boolean critical, final int n) throws IOException {
        this.nonceData = null;
        this.extensionId = PKIXExtensions.OCSPNonce_Id;
        this.critical = critical;
        if (n > 0) {
            new SecureRandom().nextBytes(this.nonceData = new byte[n]);
            this.extensionValue = new DerValue((byte)4, this.nonceData).toByteArray();
            return;
        }
        throw new IllegalArgumentException("Length must be a positive integer");
    }
    
    public OCSPNonceExtension(final byte[] array) throws IOException {
        this(false, array);
    }
    
    public OCSPNonceExtension(final boolean critical, final byte[] array) throws IOException {
        this.nonceData = null;
        this.extensionId = PKIXExtensions.OCSPNonce_Id;
        this.critical = critical;
        Objects.requireNonNull(array, "Nonce data must be non-null");
        if (array.length > 0) {
            this.nonceData = array.clone();
            this.extensionValue = new DerValue((byte)4, this.nonceData).toByteArray();
            return;
        }
        throw new IllegalArgumentException("Nonce data must be at least 1 byte in length");
    }
    
    public byte[] getNonceValue() {
        return this.nonceData.clone();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append("OCSPNonce").append(": ");
        sb.append((this.nonceData == null) ? "" : Debug.toString(this.nonceData));
        sb.append("\n");
        return sb.toString();
    }
    
    public String getName() {
        return "OCSPNonce";
    }
}
