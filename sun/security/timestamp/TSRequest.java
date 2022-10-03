package sun.security.timestamp;

import java.io.IOException;
import sun.security.util.ObjectIdentifier;
import sun.security.util.DerOutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.security.cert.X509Extension;
import java.math.BigInteger;
import sun.security.x509.AlgorithmId;

public class TSRequest
{
    private int version;
    private AlgorithmId hashAlgorithmId;
    private byte[] hashValue;
    private String policyId;
    private BigInteger nonce;
    private boolean returnCertificate;
    private X509Extension[] extensions;
    
    public TSRequest(final String policyId, final byte[] array, final MessageDigest messageDigest) throws NoSuchAlgorithmException {
        this.version = 1;
        this.hashAlgorithmId = null;
        this.policyId = null;
        this.nonce = null;
        this.returnCertificate = false;
        this.extensions = null;
        this.policyId = policyId;
        this.hashAlgorithmId = AlgorithmId.get(messageDigest.getAlgorithm());
        this.hashValue = messageDigest.digest(array);
    }
    
    public byte[] getHashedMessage() {
        return this.hashValue.clone();
    }
    
    public void setVersion(final int version) {
        this.version = version;
    }
    
    public void setPolicyId(final String policyId) {
        this.policyId = policyId;
    }
    
    public void setNonce(final BigInteger nonce) {
        this.nonce = nonce;
    }
    
    public void requestCertificate(final boolean returnCertificate) {
        this.returnCertificate = returnCertificate;
    }
    
    public void setExtensions(final X509Extension[] extensions) {
        this.extensions = extensions;
    }
    
    public byte[] encode() throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        derOutputStream.putInteger(this.version);
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        this.hashAlgorithmId.encode(derOutputStream2);
        derOutputStream2.putOctetString(this.hashValue);
        derOutputStream.write((byte)48, derOutputStream2);
        if (this.policyId != null) {
            derOutputStream.putOID(new ObjectIdentifier(this.policyId));
        }
        if (this.nonce != null) {
            derOutputStream.putInteger(this.nonce);
        }
        if (this.returnCertificate) {
            derOutputStream.putBoolean(true);
        }
        final DerOutputStream derOutputStream3 = new DerOutputStream();
        derOutputStream3.write((byte)48, derOutputStream);
        return derOutputStream3.toByteArray();
    }
}
