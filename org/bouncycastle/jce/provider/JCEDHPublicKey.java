package org.bouncycastle.jce.provider;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x9.DHDomainParameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.DHParameter;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.ASN1Sequence;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;
import javax.crypto.spec.DHPublicKeySpec;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import javax.crypto.spec.DHParameterSpec;
import java.math.BigInteger;
import javax.crypto.interfaces.DHPublicKey;

public class JCEDHPublicKey implements DHPublicKey
{
    static final long serialVersionUID = -216691575254424324L;
    private BigInteger y;
    private DHParameterSpec dhSpec;
    private SubjectPublicKeyInfo info;
    
    JCEDHPublicKey(final DHPublicKeySpec dhPublicKeySpec) {
        this.y = dhPublicKeySpec.getY();
        this.dhSpec = new DHParameterSpec(dhPublicKeySpec.getP(), dhPublicKeySpec.getG());
    }
    
    JCEDHPublicKey(final DHPublicKey dhPublicKey) {
        this.y = dhPublicKey.getY();
        this.dhSpec = dhPublicKey.getParams();
    }
    
    JCEDHPublicKey(final DHPublicKeyParameters dhPublicKeyParameters) {
        this.y = dhPublicKeyParameters.getY();
        this.dhSpec = new DHParameterSpec(dhPublicKeyParameters.getParameters().getP(), dhPublicKeyParameters.getParameters().getG(), dhPublicKeyParameters.getParameters().getL());
    }
    
    JCEDHPublicKey(final BigInteger y, final DHParameterSpec dhSpec) {
        this.y = y;
        this.dhSpec = dhSpec;
    }
    
    JCEDHPublicKey(final SubjectPublicKeyInfo info) {
        this.info = info;
        ASN1Integer asn1Integer;
        try {
            asn1Integer = (ASN1Integer)info.parsePublicKey();
        }
        catch (final IOException ex) {
            throw new IllegalArgumentException("invalid info structure in DH public key");
        }
        this.y = asn1Integer.getValue();
        final ASN1Sequence instance = ASN1Sequence.getInstance(info.getAlgorithmId().getParameters());
        final ASN1ObjectIdentifier algorithm = info.getAlgorithmId().getAlgorithm();
        if (algorithm.equals(PKCSObjectIdentifiers.dhKeyAgreement) || this.isPKCSParam(instance)) {
            final DHParameter instance2 = DHParameter.getInstance(instance);
            if (instance2.getL() != null) {
                this.dhSpec = new DHParameterSpec(instance2.getP(), instance2.getG(), instance2.getL().intValue());
            }
            else {
                this.dhSpec = new DHParameterSpec(instance2.getP(), instance2.getG());
            }
        }
        else {
            if (!algorithm.equals(X9ObjectIdentifiers.dhpublicnumber)) {
                throw new IllegalArgumentException("unknown algorithm type: " + algorithm);
            }
            final DHDomainParameters instance3 = DHDomainParameters.getInstance(instance);
            this.dhSpec = new DHParameterSpec(instance3.getP().getValue(), instance3.getG().getValue());
        }
    }
    
    public String getAlgorithm() {
        return "DH";
    }
    
    public String getFormat() {
        return "X.509";
    }
    
    public byte[] getEncoded() {
        if (this.info != null) {
            return KeyUtil.getEncodedSubjectPublicKeyInfo(this.info);
        }
        return KeyUtil.getEncodedSubjectPublicKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.dhKeyAgreement, new DHParameter(this.dhSpec.getP(), this.dhSpec.getG(), this.dhSpec.getL())), new ASN1Integer(this.y));
    }
    
    public DHParameterSpec getParams() {
        return this.dhSpec;
    }
    
    public BigInteger getY() {
        return this.y;
    }
    
    private boolean isPKCSParam(final ASN1Sequence asn1Sequence) {
        return asn1Sequence.size() == 2 || (asn1Sequence.size() <= 3 && ASN1Integer.getInstance(asn1Sequence.getObjectAt(2)).getValue().compareTo(BigInteger.valueOf(ASN1Integer.getInstance(asn1Sequence.getObjectAt(0)).getValue().bitLength())) <= 0);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        this.y = (BigInteger)objectInputStream.readObject();
        this.dhSpec = new DHParameterSpec((BigInteger)objectInputStream.readObject(), (BigInteger)objectInputStream.readObject(), objectInputStream.readInt());
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeObject(this.getY());
        objectOutputStream.writeObject(this.dhSpec.getP());
        objectOutputStream.writeObject(this.dhSpec.getG());
        objectOutputStream.writeInt(this.dhSpec.getL());
    }
}
