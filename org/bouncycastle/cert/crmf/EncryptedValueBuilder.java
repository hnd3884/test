package org.bouncycastle.cert.crmf;

import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfoBuilder;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import java.io.IOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.Strings;
import org.bouncycastle.asn1.crmf.EncryptedValue;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.KeyWrapper;

public class EncryptedValueBuilder
{
    private KeyWrapper wrapper;
    private OutputEncryptor encryptor;
    private EncryptedValuePadder padder;
    
    public EncryptedValueBuilder(final KeyWrapper keyWrapper, final OutputEncryptor outputEncryptor) {
        this(keyWrapper, outputEncryptor, null);
    }
    
    public EncryptedValueBuilder(final KeyWrapper wrapper, final OutputEncryptor encryptor, final EncryptedValuePadder padder) {
        this.wrapper = wrapper;
        this.encryptor = encryptor;
        this.padder = padder;
    }
    
    public EncryptedValue build(final char[] array) throws CRMFException {
        return this.encryptData(this.padData(Strings.toUTF8ByteArray(array)));
    }
    
    public EncryptedValue build(final X509CertificateHolder x509CertificateHolder) throws CRMFException {
        try {
            return this.encryptData(this.padData(x509CertificateHolder.getEncoded()));
        }
        catch (final IOException ex) {
            throw new CRMFException("cannot encode certificate: " + ex.getMessage(), ex);
        }
    }
    
    public EncryptedValue build(final PrivateKeyInfo privateKeyInfo) throws CRMFException {
        final PKCS8EncryptedPrivateKeyInfoBuilder pkcs8EncryptedPrivateKeyInfoBuilder = new PKCS8EncryptedPrivateKeyInfoBuilder(privateKeyInfo);
        final AlgorithmIdentifier privateKeyAlgorithm = privateKeyInfo.getPrivateKeyAlgorithm();
        final AlgorithmIdentifier algorithmIdentifier = this.encryptor.getAlgorithmIdentifier();
        try {
            final PKCS8EncryptedPrivateKeyInfo build = pkcs8EncryptedPrivateKeyInfoBuilder.build(this.encryptor);
            this.wrapper.generateWrappedKey(this.encryptor.getKey());
            return new EncryptedValue(privateKeyAlgorithm, algorithmIdentifier, new DERBitString(this.wrapper.generateWrappedKey(this.encryptor.getKey())), this.wrapper.getAlgorithmIdentifier(), (ASN1OctetString)null, new DERBitString(build.getEncoded()));
        }
        catch (final IOException ex) {
            throw new CRMFException("cannot encode encrypted private key: " + ex.getMessage(), ex);
        }
        catch (final IllegalStateException ex2) {
            throw new CRMFException("cannot encode key: " + ex2.getMessage(), ex2);
        }
        catch (final OperatorException ex3) {
            throw new CRMFException("cannot wrap key: " + ex3.getMessage(), ex3);
        }
    }
    
    private EncryptedValue encryptData(final byte[] array) throws CRMFException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final OutputStream outputStream = this.encryptor.getOutputStream(byteArrayOutputStream);
        try {
            outputStream.write(array);
            outputStream.close();
        }
        catch (final IOException ex) {
            throw new CRMFException("cannot process data: " + ex.getMessage(), ex);
        }
        final AlgorithmIdentifier algorithmIdentifier = null;
        final AlgorithmIdentifier algorithmIdentifier2 = this.encryptor.getAlgorithmIdentifier();
        DERBitString derBitString;
        try {
            this.wrapper.generateWrappedKey(this.encryptor.getKey());
            derBitString = new DERBitString(this.wrapper.generateWrappedKey(this.encryptor.getKey()));
        }
        catch (final OperatorException ex2) {
            throw new CRMFException("cannot wrap key: " + ex2.getMessage(), ex2);
        }
        return new EncryptedValue(algorithmIdentifier, algorithmIdentifier2, derBitString, this.wrapper.getAlgorithmIdentifier(), (ASN1OctetString)null, new DERBitString(byteArrayOutputStream.toByteArray()));
    }
    
    private byte[] padData(final byte[] array) {
        if (this.padder != null) {
            return this.padder.getPaddedData(array);
        }
        return array;
    }
}
