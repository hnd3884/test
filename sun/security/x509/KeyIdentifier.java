package sun.security.x509;

import java.util.Arrays;
import sun.security.util.DerOutputStream;
import sun.misc.HexDumpEncoder;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.io.IOException;
import sun.security.util.DerValue;

public class KeyIdentifier
{
    private byte[] octetString;
    
    public KeyIdentifier(final byte[] array) {
        this.octetString = array.clone();
    }
    
    public KeyIdentifier(final DerValue derValue) throws IOException {
        this.octetString = derValue.getOctetString();
    }
    
    public KeyIdentifier(final PublicKey publicKey) throws IOException {
        final DerValue derValue = new DerValue(publicKey.getEncoded());
        if (derValue.tag != 48) {
            throw new IOException("PublicKey value is not a valid X.509 public key");
        }
        AlgorithmId.parse(derValue.data.getDerValue());
        final byte[] byteArray = derValue.data.getUnalignedBitString().toByteArray();
        MessageDigest instance;
        try {
            instance = MessageDigest.getInstance("SHA1");
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new IOException("SHA1 not supported");
        }
        instance.update(byteArray);
        this.octetString = instance.digest();
    }
    
    public byte[] getIdentifier() {
        return this.octetString.clone();
    }
    
    @Override
    public String toString() {
        return "KeyIdentifier [\n" + new HexDumpEncoder().encodeBuffer(this.octetString) + "]\n";
    }
    
    void encode(final DerOutputStream derOutputStream) throws IOException {
        derOutputStream.putOctetString(this.octetString);
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        for (byte b = 0; b < this.octetString.length; ++b) {
            n += this.octetString[b] * b;
        }
        return n;
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof KeyIdentifier && Arrays.equals(this.octetString, ((KeyIdentifier)o).octetString));
    }
}
