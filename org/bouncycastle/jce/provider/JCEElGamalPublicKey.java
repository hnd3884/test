package org.bouncycastle.jce.provider;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import javax.crypto.spec.DHParameterSpec;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
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

public class JCEElGamalPublicKey implements ElGamalPublicKey, DHPublicKey
{
    static final long serialVersionUID = 8712728417091216948L;
    private BigInteger y;
    private ElGamalParameterSpec elSpec;
    
    JCEElGamalPublicKey(final ElGamalPublicKeySpec elGamalPublicKeySpec) {
        this.y = elGamalPublicKeySpec.getY();
        this.elSpec = new ElGamalParameterSpec(elGamalPublicKeySpec.getParams().getP(), elGamalPublicKeySpec.getParams().getG());
    }
    
    JCEElGamalPublicKey(final DHPublicKeySpec dhPublicKeySpec) {
        this.y = dhPublicKeySpec.getY();
        this.elSpec = new ElGamalParameterSpec(dhPublicKeySpec.getP(), dhPublicKeySpec.getG());
    }
    
    JCEElGamalPublicKey(final ElGamalPublicKey elGamalPublicKey) {
        this.y = elGamalPublicKey.getY();
        this.elSpec = elGamalPublicKey.getParameters();
    }
    
    JCEElGamalPublicKey(final DHPublicKey dhPublicKey) {
        this.y = dhPublicKey.getY();
        this.elSpec = new ElGamalParameterSpec(dhPublicKey.getParams().getP(), dhPublicKey.getParams().getG());
    }
    
    JCEElGamalPublicKey(final ElGamalPublicKeyParameters elGamalPublicKeyParameters) {
        this.y = elGamalPublicKeyParameters.getY();
        this.elSpec = new ElGamalParameterSpec(elGamalPublicKeyParameters.getParameters().getP(), elGamalPublicKeyParameters.getParameters().getG());
    }
    
    JCEElGamalPublicKey(final BigInteger y, final ElGamalParameterSpec elSpec) {
        this.y = y;
        this.elSpec = elSpec;
    }
    
    JCEElGamalPublicKey(final SubjectPublicKeyInfo subjectPublicKeyInfo) {
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
        return KeyUtil.getEncodedSubjectPublicKeyInfo(new AlgorithmIdentifier(OIWObjectIdentifiers.elGamalAlgorithm, new ElGamalParameter(this.elSpec.getP(), this.elSpec.getG())), new ASN1Integer(this.y));
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
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        this.y = (BigInteger)objectInputStream.readObject();
        this.elSpec = new ElGamalParameterSpec((BigInteger)objectInputStream.readObject(), (BigInteger)objectInputStream.readObject());
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeObject(this.getY());
        objectOutputStream.writeObject(this.elSpec.getP());
        objectOutputStream.writeObject(this.elSpec.getG());
    }
}
