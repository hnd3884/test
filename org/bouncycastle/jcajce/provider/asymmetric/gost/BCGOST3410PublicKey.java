package org.bouncycastle.jcajce.provider.asymmetric.gost;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import org.bouncycastle.util.Strings;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import java.io.IOException;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.GOST3410PublicKeyParameters;
import org.bouncycastle.jce.spec.GOST3410ParameterSpec;
import org.bouncycastle.jce.spec.GOST3410PublicKeyParameterSetSpec;
import org.bouncycastle.jce.spec.GOST3410PublicKeySpec;
import org.bouncycastle.jce.interfaces.GOST3410Params;
import java.math.BigInteger;
import org.bouncycastle.jce.interfaces.GOST3410PublicKey;

public class BCGOST3410PublicKey implements GOST3410PublicKey
{
    static final long serialVersionUID = -6251023343619275990L;
    private BigInteger y;
    private transient GOST3410Params gost3410Spec;
    
    BCGOST3410PublicKey(final GOST3410PublicKeySpec gost3410PublicKeySpec) {
        this.y = gost3410PublicKeySpec.getY();
        this.gost3410Spec = new GOST3410ParameterSpec(new GOST3410PublicKeyParameterSetSpec(gost3410PublicKeySpec.getP(), gost3410PublicKeySpec.getQ(), gost3410PublicKeySpec.getA()));
    }
    
    BCGOST3410PublicKey(final GOST3410PublicKey gost3410PublicKey) {
        this.y = gost3410PublicKey.getY();
        this.gost3410Spec = gost3410PublicKey.getParameters();
    }
    
    BCGOST3410PublicKey(final GOST3410PublicKeyParameters gost3410PublicKeyParameters, final GOST3410ParameterSpec gost3410Spec) {
        this.y = gost3410PublicKeyParameters.getY();
        this.gost3410Spec = gost3410Spec;
    }
    
    BCGOST3410PublicKey(final BigInteger y, final GOST3410ParameterSpec gost3410Spec) {
        this.y = y;
        this.gost3410Spec = gost3410Spec;
    }
    
    BCGOST3410PublicKey(final SubjectPublicKeyInfo subjectPublicKeyInfo) {
        final GOST3410PublicKeyAlgParameters gost3410PublicKeyAlgParameters = new GOST3410PublicKeyAlgParameters((ASN1Sequence)subjectPublicKeyInfo.getAlgorithmId().getParameters());
        try {
            final byte[] octets = ((DEROctetString)subjectPublicKeyInfo.parsePublicKey()).getOctets();
            final byte[] array = new byte[octets.length];
            for (int i = 0; i != octets.length; ++i) {
                array[i] = octets[octets.length - 1 - i];
            }
            this.y = new BigInteger(1, array);
        }
        catch (final IOException ex) {
            throw new IllegalArgumentException("invalid info structure in GOST3410 public key");
        }
        this.gost3410Spec = GOST3410ParameterSpec.fromPublicKeyAlg(gost3410PublicKeyAlgParameters);
    }
    
    public String getAlgorithm() {
        return "GOST3410";
    }
    
    public String getFormat() {
        return "X.509";
    }
    
    public byte[] getEncoded() {
        final byte[] byteArray = this.getY().toByteArray();
        byte[] array;
        if (byteArray[0] == 0) {
            array = new byte[byteArray.length - 1];
        }
        else {
            array = new byte[byteArray.length];
        }
        for (int i = 0; i != array.length; ++i) {
            array[i] = byteArray[byteArray.length - 1 - i];
        }
        try {
            SubjectPublicKeyInfo subjectPublicKeyInfo;
            if (this.gost3410Spec instanceof GOST3410ParameterSpec) {
                if (this.gost3410Spec.getEncryptionParamSetOID() != null) {
                    subjectPublicKeyInfo = new SubjectPublicKeyInfo(new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3410_94, new GOST3410PublicKeyAlgParameters(new ASN1ObjectIdentifier(this.gost3410Spec.getPublicKeyParamSetOID()), new ASN1ObjectIdentifier(this.gost3410Spec.getDigestParamSetOID()), new ASN1ObjectIdentifier(this.gost3410Spec.getEncryptionParamSetOID()))), new DEROctetString(array));
                }
                else {
                    subjectPublicKeyInfo = new SubjectPublicKeyInfo(new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3410_94, new GOST3410PublicKeyAlgParameters(new ASN1ObjectIdentifier(this.gost3410Spec.getPublicKeyParamSetOID()), new ASN1ObjectIdentifier(this.gost3410Spec.getDigestParamSetOID()))), new DEROctetString(array));
                }
            }
            else {
                subjectPublicKeyInfo = new SubjectPublicKeyInfo(new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3410_94), new DEROctetString(array));
            }
            return KeyUtil.getEncodedSubjectPublicKeyInfo(subjectPublicKeyInfo);
        }
        catch (final IOException ex) {
            return null;
        }
    }
    
    public GOST3410Params getParameters() {
        return this.gost3410Spec;
    }
    
    public BigInteger getY() {
        return this.y;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        final String lineSeparator = Strings.lineSeparator();
        sb.append("GOST3410 Public Key").append(lineSeparator);
        sb.append("            y: ").append(this.getY().toString(16)).append(lineSeparator);
        return sb.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof BCGOST3410PublicKey) {
            final BCGOST3410PublicKey bcgost3410PublicKey = (BCGOST3410PublicKey)o;
            return this.y.equals(bcgost3410PublicKey.y) && this.gost3410Spec.equals(bcgost3410PublicKey.gost3410Spec);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.y.hashCode() ^ this.gost3410Spec.hashCode();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        final String s = (String)objectInputStream.readObject();
        if (s != null) {
            this.gost3410Spec = new GOST3410ParameterSpec(s, (String)objectInputStream.readObject(), (String)objectInputStream.readObject());
        }
        else {
            this.gost3410Spec = new GOST3410ParameterSpec(new GOST3410PublicKeyParameterSetSpec((BigInteger)objectInputStream.readObject(), (BigInteger)objectInputStream.readObject(), (BigInteger)objectInputStream.readObject()));
            objectInputStream.readObject();
            objectInputStream.readObject();
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.gost3410Spec.getPublicKeyParamSetOID() != null) {
            objectOutputStream.writeObject(this.gost3410Spec.getPublicKeyParamSetOID());
            objectOutputStream.writeObject(this.gost3410Spec.getDigestParamSetOID());
            objectOutputStream.writeObject(this.gost3410Spec.getEncryptionParamSetOID());
        }
        else {
            objectOutputStream.writeObject(null);
            objectOutputStream.writeObject(this.gost3410Spec.getPublicKeyParameters().getP());
            objectOutputStream.writeObject(this.gost3410Spec.getPublicKeyParameters().getQ());
            objectOutputStream.writeObject(this.gost3410Spec.getPublicKeyParameters().getA());
            objectOutputStream.writeObject(this.gost3410Spec.getDigestParamSetOID());
            objectOutputStream.writeObject(this.gost3410Spec.getEncryptionParamSetOID());
        }
    }
}
