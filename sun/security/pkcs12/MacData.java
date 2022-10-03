package sun.security.pkcs12;

import sun.security.util.DerOutputStream;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;
import sun.security.util.DerValue;
import sun.security.x509.AlgorithmId;
import sun.security.pkcs.ParsingException;
import sun.security.util.DerInputStream;
import java.security.AlgorithmParameters;

class MacData
{
    private String digestAlgorithmName;
    private AlgorithmParameters digestAlgorithmParams;
    private byte[] digest;
    private byte[] macSalt;
    private int iterations;
    private byte[] encoded;
    
    MacData(final DerInputStream derInputStream) throws IOException, ParsingException {
        this.encoded = null;
        final DerValue[] sequence = derInputStream.getSequence(2);
        if (sequence.length < 2 || sequence.length > 3) {
            throw new ParsingException("Invalid length for MacData");
        }
        final DerValue[] sequence2 = new DerInputStream(sequence[0].toByteArray()).getSequence(2);
        if (sequence2.length != 2) {
            throw new ParsingException("Invalid length for DigestInfo");
        }
        final AlgorithmId parse = AlgorithmId.parse(sequence2[0]);
        this.digestAlgorithmName = parse.getName();
        this.digestAlgorithmParams = parse.getParameters();
        this.digest = sequence2[1].getOctetString();
        this.macSalt = sequence[1].getOctetString();
        if (sequence.length > 2) {
            this.iterations = sequence[2].getInteger();
        }
        else {
            this.iterations = 1;
        }
    }
    
    MacData(final String s, final byte[] array, final byte[] macSalt, final int iterations) throws NoSuchAlgorithmException {
        this.encoded = null;
        if (s == null) {
            throw new NullPointerException("the algName parameter must be non-null");
        }
        final AlgorithmId value = AlgorithmId.get(s);
        this.digestAlgorithmName = value.getName();
        this.digestAlgorithmParams = value.getParameters();
        if (array == null) {
            throw new NullPointerException("the digest parameter must be non-null");
        }
        if (array.length == 0) {
            throw new IllegalArgumentException("the digest parameter must not be empty");
        }
        this.digest = array.clone();
        this.macSalt = macSalt;
        this.iterations = iterations;
        this.encoded = null;
    }
    
    MacData(final AlgorithmParameters algorithmParameters, final byte[] array, final byte[] macSalt, final int iterations) throws NoSuchAlgorithmException {
        this.encoded = null;
        if (algorithmParameters == null) {
            throw new NullPointerException("the algParams parameter must be non-null");
        }
        final AlgorithmId value = AlgorithmId.get(algorithmParameters);
        this.digestAlgorithmName = value.getName();
        this.digestAlgorithmParams = value.getParameters();
        if (array == null) {
            throw new NullPointerException("the digest parameter must be non-null");
        }
        if (array.length == 0) {
            throw new IllegalArgumentException("the digest parameter must not be empty");
        }
        this.digest = array.clone();
        this.macSalt = macSalt;
        this.iterations = iterations;
        this.encoded = null;
    }
    
    String getDigestAlgName() {
        return this.digestAlgorithmName;
    }
    
    byte[] getSalt() {
        return this.macSalt;
    }
    
    int getIterations() {
        return this.iterations;
    }
    
    byte[] getDigest() {
        return this.digest;
    }
    
    public byte[] getEncoded() throws NoSuchAlgorithmException, IOException {
        if (this.encoded != null) {
            return this.encoded.clone();
        }
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        final DerOutputStream derOutputStream3 = new DerOutputStream();
        AlgorithmId.get(this.digestAlgorithmName).encode(derOutputStream3);
        derOutputStream3.putOctetString(this.digest);
        derOutputStream2.write((byte)48, derOutputStream3);
        derOutputStream2.putOctetString(this.macSalt);
        derOutputStream2.putInteger(this.iterations);
        derOutputStream.write((byte)48, derOutputStream2);
        this.encoded = derOutputStream.toByteArray();
        return this.encoded.clone();
    }
}
