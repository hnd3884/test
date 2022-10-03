package org.bouncycastle.jcajce.provider.asymmetric.dh;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.asn1.x9.ValidationParams;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.params.DHValidationParameters;
import org.bouncycastle.asn1.x9.DomainParameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.DHParameter;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.ASN1Sequence;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.crypto.params.DHParameters;
import javax.crypto.spec.DHPublicKeySpec;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import javax.crypto.spec.DHParameterSpec;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;
import java.math.BigInteger;
import javax.crypto.interfaces.DHPublicKey;

public class BCDHPublicKey implements DHPublicKey
{
    static final long serialVersionUID = -216691575254424324L;
    private BigInteger y;
    private transient DHPublicKeyParameters dhPublicKey;
    private transient DHParameterSpec dhSpec;
    private transient SubjectPublicKeyInfo info;
    
    BCDHPublicKey(final DHPublicKeySpec dhPublicKeySpec) {
        this.y = dhPublicKeySpec.getY();
        this.dhSpec = new DHParameterSpec(dhPublicKeySpec.getP(), dhPublicKeySpec.getG());
        this.dhPublicKey = new DHPublicKeyParameters(this.y, new DHParameters(dhPublicKeySpec.getP(), dhPublicKeySpec.getG()));
    }
    
    BCDHPublicKey(final DHPublicKey dhPublicKey) {
        this.y = dhPublicKey.getY();
        this.dhSpec = dhPublicKey.getParams();
        this.dhPublicKey = new DHPublicKeyParameters(this.y, new DHParameters(this.dhSpec.getP(), this.dhSpec.getG()));
    }
    
    BCDHPublicKey(final DHPublicKeyParameters dhPublicKey) {
        this.y = dhPublicKey.getY();
        this.dhSpec = new DHParameterSpec(dhPublicKey.getParameters().getP(), dhPublicKey.getParameters().getG(), dhPublicKey.getParameters().getL());
        this.dhPublicKey = dhPublicKey;
    }
    
    BCDHPublicKey(final BigInteger y, final DHParameterSpec dhSpec) {
        this.y = y;
        this.dhSpec = dhSpec;
        this.dhPublicKey = new DHPublicKeyParameters(y, new DHParameters(dhSpec.getP(), dhSpec.getG()));
    }
    
    public BCDHPublicKey(final SubjectPublicKeyInfo info) {
        this.info = info;
        ASN1Integer asn1Integer;
        try {
            asn1Integer = (ASN1Integer)info.parsePublicKey();
        }
        catch (final IOException ex) {
            throw new IllegalArgumentException("invalid info structure in DH public key");
        }
        this.y = asn1Integer.getValue();
        final ASN1Sequence instance = ASN1Sequence.getInstance(info.getAlgorithm().getParameters());
        final ASN1ObjectIdentifier algorithm = info.getAlgorithm().getAlgorithm();
        if (algorithm.equals(PKCSObjectIdentifiers.dhKeyAgreement) || this.isPKCSParam(instance)) {
            final DHParameter instance2 = DHParameter.getInstance(instance);
            if (instance2.getL() != null) {
                this.dhSpec = new DHParameterSpec(instance2.getP(), instance2.getG(), instance2.getL().intValue());
            }
            else {
                this.dhSpec = new DHParameterSpec(instance2.getP(), instance2.getG());
            }
            this.dhPublicKey = new DHPublicKeyParameters(this.y, new DHParameters(this.dhSpec.getP(), this.dhSpec.getG()));
        }
        else {
            if (!algorithm.equals(X9ObjectIdentifiers.dhpublicnumber)) {
                throw new IllegalArgumentException("unknown algorithm type: " + algorithm);
            }
            final DomainParameters instance3 = DomainParameters.getInstance(instance);
            this.dhSpec = new DHParameterSpec(instance3.getP(), instance3.getG());
            final ValidationParams validationParams = instance3.getValidationParams();
            if (validationParams != null) {
                this.dhPublicKey = new DHPublicKeyParameters(this.y, new DHParameters(instance3.getP(), instance3.getG(), instance3.getQ(), instance3.getJ(), new DHValidationParameters(validationParams.getSeed(), validationParams.getPgenCounter().intValue())));
            }
            else {
                this.dhPublicKey = new DHPublicKeyParameters(this.y, new DHParameters(instance3.getP(), instance3.getG(), instance3.getQ(), instance3.getJ(), null));
            }
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
        return KeyUtil.getEncodedSubjectPublicKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.dhKeyAgreement, new DHParameter(this.dhSpec.getP(), this.dhSpec.getG(), this.dhSpec.getL()).toASN1Primitive()), new ASN1Integer(this.y));
    }
    
    public DHParameterSpec getParams() {
        return this.dhSpec;
    }
    
    public BigInteger getY() {
        return this.y;
    }
    
    public DHPublicKeyParameters engineGetKeyParameters() {
        return this.dhPublicKey;
    }
    
    private boolean isPKCSParam(final ASN1Sequence asn1Sequence) {
        return asn1Sequence.size() == 2 || (asn1Sequence.size() <= 3 && ASN1Integer.getInstance(asn1Sequence.getObjectAt(2)).getValue().compareTo(BigInteger.valueOf(ASN1Integer.getInstance(asn1Sequence.getObjectAt(0)).getValue().bitLength())) <= 0);
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
        this.dhSpec = new DHParameterSpec((BigInteger)objectInputStream.readObject(), (BigInteger)objectInputStream.readObject(), objectInputStream.readInt());
        this.info = null;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(this.dhSpec.getP());
        objectOutputStream.writeObject(this.dhSpec.getG());
        objectOutputStream.writeInt(this.dhSpec.getL());
    }
}
