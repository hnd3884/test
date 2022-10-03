package sun.security.timestamp;

import sun.security.util.DerValue;
import java.io.IOException;
import java.util.Date;
import sun.security.x509.AlgorithmId;
import java.math.BigInteger;
import sun.security.util.ObjectIdentifier;

public class TimestampToken
{
    private int version;
    private ObjectIdentifier policy;
    private BigInteger serialNumber;
    private AlgorithmId hashAlgorithm;
    private byte[] hashedMessage;
    private Date genTime;
    private BigInteger nonce;
    
    public TimestampToken(final byte[] array) throws IOException {
        if (array == null) {
            throw new IOException("No timestamp token info");
        }
        this.parse(array);
    }
    
    public Date getDate() {
        return this.genTime;
    }
    
    public AlgorithmId getHashAlgorithm() {
        return this.hashAlgorithm;
    }
    
    public byte[] getHashedMessage() {
        return this.hashedMessage;
    }
    
    public BigInteger getNonce() {
        return this.nonce;
    }
    
    public String getPolicyID() {
        return this.policy.toString();
    }
    
    public BigInteger getSerialNumber() {
        return this.serialNumber;
    }
    
    private void parse(final byte[] array) throws IOException {
        final DerValue derValue = new DerValue(array);
        if (derValue.tag != 48) {
            throw new IOException("Bad encoding for timestamp token info");
        }
        this.version = derValue.data.getInteger();
        this.policy = derValue.data.getOID();
        final DerValue derValue2 = derValue.data.getDerValue();
        this.hashAlgorithm = AlgorithmId.parse(derValue2.data.getDerValue());
        this.hashedMessage = derValue2.data.getOctetString();
        this.serialNumber = derValue.data.getBigInteger();
        this.genTime = derValue.data.getGeneralizedTime();
        while (derValue.data.available() > 0) {
            final DerValue derValue3 = derValue.data.getDerValue();
            if (derValue3.tag == 2) {
                this.nonce = derValue3.getBigInteger();
                break;
            }
        }
    }
}
