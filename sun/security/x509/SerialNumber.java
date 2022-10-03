package sun.security.x509;

import sun.security.util.DerOutputStream;
import sun.security.util.Debug;
import java.io.InputStream;
import sun.security.util.DerInputStream;
import java.io.IOException;
import sun.security.util.DerValue;
import java.math.BigInteger;

public class SerialNumber
{
    private BigInteger serialNum;
    
    private void construct(final DerValue derValue) throws IOException {
        this.serialNum = derValue.getBigInteger();
        if (derValue.data.available() != 0) {
            throw new IOException("Excess SerialNumber data");
        }
    }
    
    public SerialNumber(final BigInteger serialNum) {
        this.serialNum = serialNum;
    }
    
    public SerialNumber(final int n) {
        this.serialNum = BigInteger.valueOf(n);
    }
    
    public SerialNumber(final DerInputStream derInputStream) throws IOException {
        this.construct(derInputStream.getDerValue());
    }
    
    public SerialNumber(final DerValue derValue) throws IOException {
        this.construct(derValue);
    }
    
    public SerialNumber(final InputStream inputStream) throws IOException {
        this.construct(new DerValue(inputStream));
    }
    
    @Override
    public String toString() {
        return "SerialNumber: [" + Debug.toHexString(this.serialNum) + "]";
    }
    
    public void encode(final DerOutputStream derOutputStream) throws IOException {
        derOutputStream.putInteger(this.serialNum);
    }
    
    public BigInteger getNumber() {
        return this.serialNum;
    }
}
