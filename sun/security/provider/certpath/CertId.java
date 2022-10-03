package sun.security.provider.certpath;

import sun.misc.HexDumpEncoder;
import java.util.Arrays;
import sun.security.util.DerOutputStream;
import java.math.BigInteger;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.security.PublicKey;
import javax.security.auth.x500.X500Principal;
import java.io.IOException;
import java.security.cert.X509Certificate;
import sun.security.x509.SerialNumber;
import sun.security.x509.AlgorithmId;

public class CertId
{
    private static final boolean debug = false;
    private static final AlgorithmId SHA1_ALGID;
    private final AlgorithmId hashAlgId;
    private final byte[] issuerNameHash;
    private final byte[] issuerKeyHash;
    private final SerialNumber certSerialNumber;
    private int myhash;
    
    public CertId(final X509Certificate x509Certificate, final SerialNumber serialNumber) throws IOException {
        this(x509Certificate.getSubjectX500Principal(), x509Certificate.getPublicKey(), serialNumber);
    }
    
    public CertId(final X500Principal x500Principal, final PublicKey publicKey, final SerialNumber certSerialNumber) throws IOException {
        this.myhash = -1;
        MessageDigest instance;
        try {
            instance = MessageDigest.getInstance("SHA1");
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new IOException("Unable to create CertId", ex);
        }
        this.hashAlgId = CertId.SHA1_ALGID;
        instance.update(x500Principal.getEncoded());
        this.issuerNameHash = instance.digest();
        final DerValue derValue = new DerValue(publicKey.getEncoded());
        instance.update((new DerValue[] { derValue.data.getDerValue(), derValue.data.getDerValue() })[1].getBitString());
        this.issuerKeyHash = instance.digest();
        this.certSerialNumber = certSerialNumber;
    }
    
    public CertId(final DerInputStream derInputStream) throws IOException {
        this.myhash = -1;
        this.hashAlgId = AlgorithmId.parse(derInputStream.getDerValue());
        this.issuerNameHash = derInputStream.getOctetString();
        this.issuerKeyHash = derInputStream.getOctetString();
        this.certSerialNumber = new SerialNumber(derInputStream);
    }
    
    public AlgorithmId getHashAlgorithm() {
        return this.hashAlgId;
    }
    
    public byte[] getIssuerNameHash() {
        return this.issuerNameHash;
    }
    
    public byte[] getIssuerKeyHash() {
        return this.issuerKeyHash;
    }
    
    public BigInteger getSerialNumber() {
        return this.certSerialNumber.getNumber();
    }
    
    public void encode(final DerOutputStream derOutputStream) throws IOException {
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        this.hashAlgId.encode(derOutputStream2);
        derOutputStream2.putOctetString(this.issuerNameHash);
        derOutputStream2.putOctetString(this.issuerKeyHash);
        this.certSerialNumber.encode(derOutputStream2);
        derOutputStream.write((byte)48, derOutputStream2);
    }
    
    @Override
    public int hashCode() {
        if (this.myhash == -1) {
            this.myhash = this.hashAlgId.hashCode();
            for (byte b = 0; b < this.issuerNameHash.length; ++b) {
                this.myhash += this.issuerNameHash[b] * b;
            }
            for (byte b2 = 0; b2 < this.issuerKeyHash.length; ++b2) {
                this.myhash += this.issuerKeyHash[b2] * b2;
            }
            this.myhash += this.certSerialNumber.getNumber().hashCode();
        }
        return this.myhash;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof CertId)) {
            return false;
        }
        final CertId certId = (CertId)o;
        return this.hashAlgId.equals(certId.getHashAlgorithm()) && Arrays.equals(this.issuerNameHash, certId.getIssuerNameHash()) && Arrays.equals(this.issuerKeyHash, certId.getIssuerKeyHash()) && this.certSerialNumber.getNumber().equals(certId.getSerialNumber());
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("CertId \n");
        sb.append("Algorithm: " + this.hashAlgId.toString() + "\n");
        sb.append("issuerNameHash \n");
        final HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
        sb.append(hexDumpEncoder.encode(this.issuerNameHash));
        sb.append("\nissuerKeyHash: \n");
        sb.append(hexDumpEncoder.encode(this.issuerKeyHash));
        sb.append("\n" + this.certSerialNumber.toString());
        return sb.toString();
    }
    
    static {
        SHA1_ALGID = new AlgorithmId(AlgorithmId.SHA_oid);
    }
}
