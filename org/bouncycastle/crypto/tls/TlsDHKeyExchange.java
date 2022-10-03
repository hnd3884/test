package org.bouncycastle.crypto.tls;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import java.io.IOException;
import java.util.Vector;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;
import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DHParameters;

public class TlsDHKeyExchange extends AbstractTlsKeyExchange
{
    protected TlsSigner tlsSigner;
    protected DHParameters dhParameters;
    protected AsymmetricKeyParameter serverPublicKey;
    protected TlsAgreementCredentials agreementCredentials;
    protected DHPrivateKeyParameters dhAgreePrivateKey;
    protected DHPublicKeyParameters dhAgreePublicKey;
    
    public TlsDHKeyExchange(final int n, final Vector vector, final DHParameters dhParameters) {
        super(n, vector);
        switch (n) {
            case 7:
            case 9:
            case 11: {
                this.tlsSigner = null;
                break;
            }
            case 5: {
                this.tlsSigner = new TlsRSASigner();
                break;
            }
            case 3: {
                this.tlsSigner = new TlsDSSSigner();
                break;
            }
            default: {
                throw new IllegalArgumentException("unsupported key exchange algorithm");
            }
        }
        this.dhParameters = dhParameters;
    }
    
    @Override
    public void init(final TlsContext tlsContext) {
        super.init(tlsContext);
        if (this.tlsSigner != null) {
            this.tlsSigner.init(tlsContext);
        }
    }
    
    public void skipServerCredentials() throws IOException {
        if (this.keyExchange != 11) {
            throw new TlsFatalAlert((short)10);
        }
    }
    
    @Override
    public void processServerCertificate(final Certificate certificate) throws IOException {
        if (this.keyExchange == 11) {
            throw new TlsFatalAlert((short)10);
        }
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
        if (this.tlsSigner == null) {
            try {
                this.dhAgreePublicKey = TlsDHUtils.validateDHPublicKey((DHPublicKeyParameters)this.serverPublicKey);
                this.dhParameters = this.validateDHParameters(this.dhAgreePublicKey.getParameters());
            }
            catch (final ClassCastException ex2) {
                throw new TlsFatalAlert((short)46, ex2);
            }
            TlsUtils.validateKeyUsage(certificate2, 8);
        }
        else {
            if (!this.tlsSigner.isValidPublicKey(this.serverPublicKey)) {
                throw new TlsFatalAlert((short)46);
            }
            TlsUtils.validateKeyUsage(certificate2, 128);
        }
        super.processServerCertificate(certificate);
    }
    
    @Override
    public boolean requiresServerKeyExchange() {
        switch (this.keyExchange) {
            case 3:
            case 5:
            case 11: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public byte[] generateServerKeyExchange() throws IOException {
        if (!this.requiresServerKeyExchange()) {
            return null;
        }
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        this.dhAgreePrivateKey = TlsDHUtils.generateEphemeralServerKeyExchange(this.context.getSecureRandom(), this.dhParameters, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    
    @Override
    public void processServerKeyExchange(final InputStream inputStream) throws IOException {
        if (!this.requiresServerKeyExchange()) {
            throw new TlsFatalAlert((short)10);
        }
        this.dhAgreePublicKey = TlsDHUtils.validateDHPublicKey(ServerDHParams.parse(inputStream).getPublicKey());
        this.dhParameters = this.validateDHParameters(this.dhAgreePublicKey.getParameters());
    }
    
    public void validateCertificateRequest(final CertificateRequest certificateRequest) throws IOException {
        if (this.keyExchange == 11) {
            throw new TlsFatalAlert((short)40);
        }
        final short[] certificateTypes = certificateRequest.getCertificateTypes();
        int i = 0;
        while (i < certificateTypes.length) {
            switch (certificateTypes[i]) {
                case 1:
                case 2:
                case 3:
                case 4:
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
        if (this.keyExchange == 11) {
            throw new TlsFatalAlert((short)80);
        }
        if (tlsCredentials instanceof TlsAgreementCredentials) {
            this.agreementCredentials = (TlsAgreementCredentials)tlsCredentials;
        }
        else if (!(tlsCredentials instanceof TlsSignerCredentials)) {
            throw new TlsFatalAlert((short)80);
        }
    }
    
    public void generateClientKeyExchange(final OutputStream outputStream) throws IOException {
        if (this.agreementCredentials == null) {
            this.dhAgreePrivateKey = TlsDHUtils.generateEphemeralClientKeyExchange(this.context.getSecureRandom(), this.dhParameters, outputStream);
        }
    }
    
    @Override
    public void processClientCertificate(final Certificate certificate) throws IOException {
        if (this.keyExchange == 11) {
            throw new TlsFatalAlert((short)10);
        }
    }
    
    @Override
    public void processClientKeyExchange(final InputStream inputStream) throws IOException {
        if (this.dhAgreePublicKey != null) {
            return;
        }
        this.dhAgreePublicKey = TlsDHUtils.validateDHPublicKey(new DHPublicKeyParameters(TlsDHUtils.readDHParameter(inputStream), this.dhParameters));
    }
    
    public byte[] generatePremasterSecret() throws IOException {
        if (this.agreementCredentials != null) {
            return this.agreementCredentials.generateAgreement(this.dhAgreePublicKey);
        }
        if (this.dhAgreePrivateKey != null) {
            return TlsDHUtils.calculateDHBasicAgreement(this.dhAgreePublicKey, this.dhAgreePrivateKey);
        }
        throw new TlsFatalAlert((short)80);
    }
    
    protected int getMinimumPrimeBits() {
        return 1024;
    }
    
    protected DHParameters validateDHParameters(final DHParameters dhParameters) throws IOException {
        if (dhParameters.getP().bitLength() < this.getMinimumPrimeBits()) {
            throw new TlsFatalAlert((short)71);
        }
        return TlsDHUtils.validateDHParameters(dhParameters);
    }
}
