package org.bouncycastle.crypto.tls;

import org.bouncycastle.util.io.Streams;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import java.io.IOException;
import java.util.Vector;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class TlsRSAKeyExchange extends AbstractTlsKeyExchange
{
    protected AsymmetricKeyParameter serverPublicKey;
    protected RSAKeyParameters rsaServerPublicKey;
    protected TlsEncryptionCredentials serverCredentials;
    protected byte[] premasterSecret;
    
    public TlsRSAKeyExchange(final Vector vector) {
        super(1, vector);
        this.serverPublicKey = null;
        this.rsaServerPublicKey = null;
        this.serverCredentials = null;
    }
    
    public void skipServerCredentials() throws IOException {
        throw new TlsFatalAlert((short)10);
    }
    
    @Override
    public void processServerCredentials(final TlsCredentials tlsCredentials) throws IOException {
        if (!(tlsCredentials instanceof TlsEncryptionCredentials)) {
            throw new TlsFatalAlert((short)80);
        }
        this.processServerCertificate(tlsCredentials.getCertificate());
        this.serverCredentials = (TlsEncryptionCredentials)tlsCredentials;
    }
    
    @Override
    public void processServerCertificate(final Certificate certificate) throws IOException {
        if (certificate.isEmpty()) {
            throw new TlsFatalAlert((short)42);
        }
        final org.bouncycastle.asn1.x509.Certificate certificate2 = certificate.getCertificateAt(0);
        final SubjectPublicKeyInfo subjectPublicKeyInfo = certificate2.getSubjectPublicKeyInfo();
        try {
            this.serverPublicKey = PublicKeyFactory.createKey(subjectPublicKeyInfo);
        }
        catch (final RuntimeException ex) {
            throw new TlsFatalAlert((short)43, ex);
        }
        if (this.serverPublicKey.isPrivate()) {
            throw new TlsFatalAlert((short)80);
        }
        this.rsaServerPublicKey = this.validateRSAPublicKey((RSAKeyParameters)this.serverPublicKey);
        TlsUtils.validateKeyUsage(certificate2, 32);
        super.processServerCertificate(certificate);
    }
    
    public void validateCertificateRequest(final CertificateRequest certificateRequest) throws IOException {
        final short[] certificateTypes = certificateRequest.getCertificateTypes();
        int i = 0;
        while (i < certificateTypes.length) {
            switch (certificateTypes[i]) {
                case 1:
                case 2:
                case 64: {
                    ++i;
                    continue;
                }
                default: {
                    throw new TlsFatalAlert((short)47);
                }
            }
        }
    }
    
    public void processClientCredentials(final TlsCredentials tlsCredentials) throws IOException {
        if (!(tlsCredentials instanceof TlsSignerCredentials)) {
            throw new TlsFatalAlert((short)80);
        }
    }
    
    public void generateClientKeyExchange(final OutputStream outputStream) throws IOException {
        this.premasterSecret = TlsRSAUtils.generateEncryptedPreMasterSecret(this.context, this.rsaServerPublicKey, outputStream);
    }
    
    @Override
    public void processClientKeyExchange(final InputStream inputStream) throws IOException {
        byte[] array;
        if (TlsUtils.isSSL(this.context)) {
            array = Streams.readAll(inputStream);
        }
        else {
            array = TlsUtils.readOpaque16(inputStream);
        }
        this.premasterSecret = this.serverCredentials.decryptPreMasterSecret(array);
    }
    
    public byte[] generatePremasterSecret() throws IOException {
        if (this.premasterSecret == null) {
            throw new TlsFatalAlert((short)80);
        }
        final byte[] premasterSecret = this.premasterSecret;
        this.premasterSecret = null;
        return premasterSecret;
    }
    
    protected RSAKeyParameters validateRSAPublicKey(final RSAKeyParameters rsaKeyParameters) throws IOException {
        if (!rsaKeyParameters.getExponent().isProbablePrime(2)) {
            throw new TlsFatalAlert((short)47);
        }
        return rsaKeyParameters;
    }
}
