package org.bouncycastle.crypto.tls;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import java.io.IOException;
import java.util.Vector;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class TlsECDHKeyExchange extends AbstractTlsKeyExchange
{
    protected TlsSigner tlsSigner;
    protected int[] namedCurves;
    protected short[] clientECPointFormats;
    protected short[] serverECPointFormats;
    protected AsymmetricKeyParameter serverPublicKey;
    protected TlsAgreementCredentials agreementCredentials;
    protected ECPrivateKeyParameters ecAgreePrivateKey;
    protected ECPublicKeyParameters ecAgreePublicKey;
    
    public TlsECDHKeyExchange(final int n, final Vector vector, final int[] namedCurves, final short[] clientECPointFormats, final short[] serverECPointFormats) {
        super(n, vector);
        switch (n) {
            case 19: {
                this.tlsSigner = new TlsRSASigner();
                break;
            }
            case 17: {
                this.tlsSigner = new TlsECDSASigner();
                break;
            }
            case 16:
            case 18:
            case 20: {
                this.tlsSigner = null;
                break;
            }
            default: {
                throw new IllegalArgumentException("unsupported key exchange algorithm");
            }
        }
        this.namedCurves = namedCurves;
        this.clientECPointFormats = clientECPointFormats;
        this.serverECPointFormats = serverECPointFormats;
    }
    
    @Override
    public void init(final TlsContext tlsContext) {
        super.init(tlsContext);
        if (this.tlsSigner != null) {
            this.tlsSigner.init(tlsContext);
        }
    }
    
    public void skipServerCredentials() throws IOException {
        if (this.keyExchange != 20) {
            throw new TlsFatalAlert((short)10);
        }
    }
    
    @Override
    public void processServerCertificate(final Certificate certificate) throws IOException {
        if (this.keyExchange == 20) {
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
                this.ecAgreePublicKey = TlsECCUtils.validateECPublicKey((ECPublicKeyParameters)this.serverPublicKey);
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
            case 17:
            case 19:
            case 20: {
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
        this.ecAgreePrivateKey = TlsECCUtils.generateEphemeralServerKeyExchange(this.context.getSecureRandom(), this.namedCurves, this.clientECPointFormats, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    
    @Override
    public void processServerKeyExchange(final InputStream inputStream) throws IOException {
        if (!this.requiresServerKeyExchange()) {
            throw new TlsFatalAlert((short)10);
        }
        this.ecAgreePublicKey = TlsECCUtils.validateECPublicKey(TlsECCUtils.deserializeECPublicKey(this.clientECPointFormats, TlsECCUtils.readECParameters(this.namedCurves, this.clientECPointFormats, inputStream), TlsUtils.readOpaque8(inputStream)));
    }
    
    public void validateCertificateRequest(final CertificateRequest certificateRequest) throws IOException {
        if (this.keyExchange == 20) {
            throw new TlsFatalAlert((short)40);
        }
        final short[] certificateTypes = certificateRequest.getCertificateTypes();
        int i = 0;
        while (i < certificateTypes.length) {
            switch (certificateTypes[i]) {
                case 1:
                case 2:
                case 64:
                case 65:
                case 66: {
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
        if (this.keyExchange == 20) {
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
            this.ecAgreePrivateKey = TlsECCUtils.generateEphemeralClientKeyExchange(this.context.getSecureRandom(), this.serverECPointFormats, this.ecAgreePublicKey.getParameters(), outputStream);
        }
    }
    
    @Override
    public void processClientCertificate(final Certificate certificate) throws IOException {
        if (this.keyExchange == 20) {
            throw new TlsFatalAlert((short)10);
        }
    }
    
    @Override
    public void processClientKeyExchange(final InputStream inputStream) throws IOException {
        if (this.ecAgreePublicKey != null) {
            return;
        }
        this.ecAgreePublicKey = TlsECCUtils.validateECPublicKey(TlsECCUtils.deserializeECPublicKey(this.serverECPointFormats, this.ecAgreePrivateKey.getParameters(), TlsUtils.readOpaque8(inputStream)));
    }
    
    public byte[] generatePremasterSecret() throws IOException {
        if (this.agreementCredentials != null) {
            return this.agreementCredentials.generateAgreement(this.ecAgreePublicKey);
        }
        if (this.ecAgreePrivateKey != null) {
            return TlsECCUtils.calculateECDHBasicAgreement(this.ecAgreePublicKey, this.ecAgreePrivateKey);
        }
        throw new TlsFatalAlert((short)80);
    }
}
