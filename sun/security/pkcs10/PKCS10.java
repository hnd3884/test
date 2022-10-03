package sun.security.pkcs10;

import java.util.Arrays;
import java.util.Base64;
import java.io.PrintStream;
import java.security.cert.CertificateException;
import java.security.AlgorithmParameters;
import java.io.OutputStream;
import sun.security.util.DerOutputStream;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;
import sun.security.util.DerValue;
import java.security.ProviderException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import sun.security.util.SignatureUtil;
import java.security.Signature;
import sun.security.x509.X509Key;
import java.math.BigInteger;
import sun.security.x509.AlgorithmId;
import sun.security.util.DerInputStream;
import java.security.PublicKey;
import sun.security.x509.X500Name;

public class PKCS10
{
    private X500Name subject;
    private PublicKey subjectPublicKeyInfo;
    private String sigAlg;
    private PKCS10Attributes attributeSet;
    private byte[] encoded;
    
    public PKCS10(final PublicKey subjectPublicKeyInfo) {
        this.subjectPublicKeyInfo = subjectPublicKeyInfo;
        this.attributeSet = new PKCS10Attributes();
    }
    
    public PKCS10(final PublicKey subjectPublicKeyInfo, final PKCS10Attributes attributeSet) {
        this.subjectPublicKeyInfo = subjectPublicKeyInfo;
        this.attributeSet = attributeSet;
    }
    
    public PKCS10(byte[] byteArray) throws IOException, SignatureException, NoSuchAlgorithmException {
        this.encoded = byteArray;
        final DerValue[] sequence = new DerInputStream(byteArray).getSequence(3);
        if (sequence.length != 3) {
            throw new IllegalArgumentException("not a PKCS #10 request");
        }
        byteArray = sequence[0].toByteArray();
        final AlgorithmId parse = AlgorithmId.parse(sequence[1]);
        final byte[] bitString = sequence[2].getBitString();
        if (!sequence[0].data.getBigInteger().equals(BigInteger.ZERO)) {
            throw new IllegalArgumentException("not PKCS #10 v1");
        }
        this.subject = new X500Name(sequence[0].data);
        this.subjectPublicKeyInfo = X509Key.parse(sequence[0].data.getDerValue());
        if (sequence[0].data.available() != 0) {
            this.attributeSet = new PKCS10Attributes(sequence[0].data);
        }
        else {
            this.attributeSet = new PKCS10Attributes();
        }
        if (sequence[0].data.available() != 0) {
            throw new IllegalArgumentException("illegal PKCS #10 data");
        }
        try {
            this.sigAlg = parse.getName();
            final Signature instance = Signature.getInstance(this.sigAlg);
            SignatureUtil.initVerifyWithParam(instance, this.subjectPublicKeyInfo, SignatureUtil.getParamSpec(this.sigAlg, parse.getParameters()));
            instance.update(byteArray);
            if (!instance.verify(bitString)) {
                throw new SignatureException("Invalid PKCS #10 signature");
            }
        }
        catch (final InvalidKeyException ex) {
            throw new SignatureException("Invalid key");
        }
        catch (final InvalidAlgorithmParameterException ex2) {
            throw new SignatureException("Invalid signature parameters", ex2);
        }
        catch (final ProviderException ex3) {
            throw new SignatureException("Error parsing signature parameters", ex3.getCause());
        }
    }
    
    public void encodeAndSign(final X500Name subject, final Signature signature) throws CertificateException, IOException, SignatureException {
        if (this.encoded != null) {
            throw new SignatureException("request is already signed");
        }
        this.subject = subject;
        final DerOutputStream derOutputStream = new DerOutputStream();
        derOutputStream.putInteger(BigInteger.ZERO);
        subject.encode(derOutputStream);
        derOutputStream.write(this.subjectPublicKeyInfo.getEncoded());
        this.attributeSet.encode(derOutputStream);
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream2.write((byte)48, derOutputStream);
        final byte[] byteArray = derOutputStream2.toByteArray();
        final DerOutputStream derOutputStream3 = derOutputStream2;
        signature.update(byteArray, 0, byteArray.length);
        final byte[] sign = signature.sign();
        this.sigAlg = signature.getAlgorithm();
        AlgorithmId algorithmId;
        try {
            final AlgorithmParameters parameters = signature.getParameters();
            algorithmId = ((parameters == null) ? AlgorithmId.get(signature.getAlgorithm()) : AlgorithmId.get(parameters));
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new SignatureException(ex);
        }
        algorithmId.encode(derOutputStream3);
        derOutputStream3.putBitString(sign);
        final DerOutputStream derOutputStream4 = new DerOutputStream();
        derOutputStream4.write((byte)48, derOutputStream3);
        this.encoded = derOutputStream4.toByteArray();
    }
    
    public X500Name getSubjectName() {
        return this.subject;
    }
    
    public PublicKey getSubjectPublicKeyInfo() {
        return this.subjectPublicKeyInfo;
    }
    
    public String getSigAlg() {
        return this.sigAlg;
    }
    
    public PKCS10Attributes getAttributes() {
        return this.attributeSet;
    }
    
    public byte[] getEncoded() {
        if (this.encoded != null) {
            return this.encoded.clone();
        }
        return null;
    }
    
    public void print(final PrintStream printStream) throws IOException, SignatureException {
        if (this.encoded == null) {
            throw new SignatureException("Cert request was not signed");
        }
        final byte[] array = { 13, 10 };
        printStream.println("-----BEGIN NEW CERTIFICATE REQUEST-----");
        printStream.println(Base64.getMimeEncoder(64, array).encodeToString(this.encoded));
        printStream.println("-----END NEW CERTIFICATE REQUEST-----");
    }
    
    @Override
    public String toString() {
        return "[PKCS #10 certificate request:\n" + this.subjectPublicKeyInfo.toString() + " subject: <" + this.subject + ">\n attributes: " + this.attributeSet.toString() + "\n]";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PKCS10)) {
            return false;
        }
        if (this.encoded == null) {
            return false;
        }
        final byte[] encoded = ((PKCS10)o).getEncoded();
        return encoded != null && Arrays.equals(this.encoded, encoded);
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        if (this.encoded != null) {
            for (byte b = 1; b < this.encoded.length; ++b) {
                n += this.encoded[b] * b;
            }
        }
        return n;
    }
}
