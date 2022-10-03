package org.bouncycastle.jcajce.provider.asymmetric.elgamal;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import javax.crypto.spec.DHParameterSpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.oiw.ElGamalParameter;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.ElGamalPublicKeyParameters;
import javax.crypto.spec.DHPublicKeySpec;
import org.bouncycastle.jce.spec.ElGamalPublicKeySpec;
import org.bouncycastle.jce.spec.ElGamalParameterSpec;
import java.math.BigInteger;
import javax.crypto.interfaces.DHPublicKey;
import org.bouncycastle.jce.interfaces.ElGamalPublicKey;

public class BCElGamalPublicKey implements ElGamalPublicKey, DHPublicKey
{
    static final long serialVersionUID = 8712728417091216948L;
    private BigInteger y;
    private transient ElGamalParameterSpec elSpec;
    
    BCElGamalPublicKey(final ElGamalPublicKeySpec elGamalPublicKeySpec) {
        this.y = elGamalPublicKeySpec.getY();
        this.elSpec = new ElGamalParameterSpec(elGamalPublicKeySpec.getParams().getP(), elGamalPublicKeySpec.getParams().getG());
    }
    
    BCElGamalPublicKey(final DHPublicKeySpec dhPublicKeySpec) {
        this.y = dhPublicKeySpec.getY();
        this.elSpec = new ElGamalParameterSpec(dhPublicKeySpec.getP(), dhPublicKeySpec.getG());
    }
    
    BCElGamalPublicKey(final ElGamalPublicKey elGamalPublicKey) {
        this.y = elGamalPublicKey.getY();
        this.elSpec = elGamalPublicKey.getParameters();
    }
    
    BCElGamalPublicKey(final DHPublicKey dhPublicKey) {
        this.y = dhPublicKey.getY();
        this.elSpec = new ElGamalParameterSpec(dhPublicKey.getParams().getP(), dhPublicKey.getParams().getG());
    }
    
    BCElGamalPublicKey(final ElGamalPublicKeyParameters elGamalPublicKeyParameters) {
        this.y = elGamalPublicKeyParameters.getY();
        this.elSpec = new ElGamalParameterSpec(elGamalPublicKeyParameters.getParameters().getP(), elGamalPublicKeyParameters.getParameters().getG());
    }
    
    BCElGamalPublicKey(final BigInteger y, final ElGamalParameterSpec elSpec) {
        this.y = y;
        this.elSpec = elSpec;
    }
    
    BCElGamalPublicKey(final SubjectPublicKeyInfo subjectPublicKeyInfo) {
        final ElGamalParameter instance = ElGamalParameter.getInstance(subjectPublicKeyInfo.getAlgorithm().getParameters());
        ASN1Integer asn1Integer;
        try {
            asn1Integer = (ASN1Integer)subjectPublicKeyInfo.parsePublicKey();
        }
        catch (final IOException ex) {
            throw new IllegalArgumentException("invalid info structure in DSA public key");
        }
        this.y = asn1Integer.getValue();
        this.elSpec = new ElGamalParameterSpec(instance.getP(), instance.getG());
    }
    
    public String getAlgorithm() {
        return "ElGamal";
    }
    
    public String getFormat() {
        return "X.509";
    }
    
    public byte[] getEncoded() {
        try {
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(OIWObjectIdentifiers.elGamalAlgorithm, new ElGamalParameter(this.elSpec.getP(), this.elSpec.getG())), new ASN1Integer(this.y)).getEncoded("DER");
        }
        catch (final IOException ex) {
            return null;
        }
    }
    
    public ElGamalParameterSpec getParameters() {
        return this.elSpec;
    }
    
    public DHParameterSpec getParams() {
        return new DHParameterSpec(this.elSpec.getP(), this.elSpec.getG());
    }
    
    public BigInteger getY() {
        return this.y;
    }
    
    @Override
    public int hashCode() {
        return this.getY().hashCode() ^ this.getParams().getG().hashCode() ^ this.getParams().getP().hashCode() ^ this.getParams().getL();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof DHPublicKey)) {
            return false;
        }
        final DHPublicKey dhPublicKey = (DHPublicKey)o;
        return this.getY().equals(dhPublicKey.getY()) && this.getParams().getG().equals(dhPublicKey.getParams().getG()) && this.getParams().getP().equals(dhPublicKey.getParams().getP()) && this.getParams().getL() == dhPublicKey.getParams().getL();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.elSpec = new ElGamalParameterSpec((BigInteger)objectInputStream.readObject(), (BigInteger)objectInputStream.readObject());
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(this.elSpec.getP());
        objectOutputStream.writeObject(this.elSpec.getG());
    }
}
