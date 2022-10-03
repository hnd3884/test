package org.bouncycastle.crypto.tls;

import org.bouncycastle.util.io.Streams;
import org.bouncycastle.util.Arrays;
import java.io.InputStream;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Vector;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;
import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import org.bouncycastle.crypto.params.DHParameters;

public class TlsPSKKeyExchange extends AbstractTlsKeyExchange
{
    protected TlsPSKIdentity pskIdentity;
    protected TlsPSKIdentityManager pskIdentityManager;
    protected DHParameters dhParameters;
    protected int[] namedCurves;
    protected short[] clientECPointFormats;
    protected short[] serverECPointFormats;
    protected byte[] psk_identity_hint;
    protected byte[] psk;
    protected DHPrivateKeyParameters dhAgreePrivateKey;
    protected DHPublicKeyParameters dhAgreePublicKey;
    protected ECPrivateKeyParameters ecAgreePrivateKey;
    protected ECPublicKeyParameters ecAgreePublicKey;
    protected AsymmetricKeyParameter serverPublicKey;
    protected RSAKeyParameters rsaServerPublicKey;
    protected TlsEncryptionCredentials serverCredentials;
    protected byte[] premasterSecret;
    
    public TlsPSKKeyExchange(final int n, final Vector vector, final TlsPSKIdentity pskIdentity, final TlsPSKIdentityManager pskIdentityManager, final DHParameters dhParameters, final int[] namedCurves, final short[] clientECPointFormats, final short[] serverECPointFormats) {
        super(n, vector);
        this.psk_identity_hint = null;
        this.psk = null;
        this.dhAgreePrivateKey = null;
        this.dhAgreePublicKey = null;
        this.ecAgreePrivateKey = null;
        this.ecAgreePublicKey = null;
        this.serverPublicKey = null;
        this.rsaServerPublicKey = null;
        this.serverCredentials = null;
        switch (n) {
            case 13:
            case 14:
            case 15:
            case 24: {
                this.pskIdentity = pskIdentity;
                this.pskIdentityManager = pskIdentityManager;
                this.dhParameters = dhParameters;
                this.namedCurves = namedCurves;
                this.clientECPointFormats = clientECPointFormats;
                this.serverECPointFormats = serverECPointFormats;
                return;
            }
            default: {
                throw new IllegalArgumentException("unsupported key exchange algorithm");
            }
        }
    }
    
    public void skipServerCredentials() throws IOException {
        if (this.keyExchange == 15) {
            throw new TlsFatalAlert((short)10);
        }
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
    public byte[] generateServerKeyExchange() throws IOException {
        this.psk_identity_hint = this.pskIdentityManager.getHint();
        if (this.psk_identity_hint == null && !this.requiresServerKeyExchange()) {
            return null;
        }
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (this.psk_identity_hint == null) {
            TlsUtils.writeOpaque16(TlsUtils.EMPTY_BYTES, byteArrayOutputStream);
        }
        else {
            TlsUtils.writeOpaque16(this.psk_identity_hint, byteArrayOutputStream);
        }
        if (this.keyExchange == 14) {
            if (this.dhParameters == null) {
                throw new TlsFatalAlert((short)80);
            }
            this.dhAgreePrivateKey = TlsDHUtils.generateEphemeralServerKeyExchange(this.context.getSecureRandom(), this.dhParameters, byteArrayOutputStream);
        }
        else if (this.keyExchange == 24) {
            this.ecAgreePrivateKey = TlsECCUtils.generateEphemeralServerKeyExchange(this.context.getSecureRandom(), this.namedCurves, this.clientECPointFormats, byteArrayOutputStream);
        }
        return byteArrayOutputStream.toByteArray();
    }
    
    @Override
    public void processServerCertificate(final Certificate certificate) throws IOException {
        if (this.keyExchange != 15) {
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
        if (this.serverPublicKey.isPrivate()) {
            throw new TlsFatalAlert((short)80);
        }
        this.rsaServerPublicKey = this.validateRSAPublicKey((RSAKeyParameters)this.serverPublicKey);
        TlsUtils.validateKeyUsage(certificate2, 32);
        super.processServerCertificate(certificate);
    }
    
    @Override
    public boolean requiresServerKeyExchange() {
        switch (this.keyExchange) {
            case 14:
            case 24: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public void processServerKeyExchange(final InputStream inputStream) throws IOException {
        this.psk_identity_hint = TlsUtils.readOpaque16(inputStream);
        if (this.keyExchange == 14) {
            this.dhAgreePublicKey = TlsDHUtils.validateDHPublicKey(ServerDHParams.parse(inputStream).getPublicKey());
            this.dhParameters = this.dhAgreePublicKey.getParameters();
        }
        else if (this.keyExchange == 24) {
            this.ecAgreePublicKey = TlsECCUtils.validateECPublicKey(TlsECCUtils.deserializeECPublicKey(this.clientECPointFormats, TlsECCUtils.readECParameters(this.namedCurves, this.clientECPointFormats, inputStream), TlsUtils.readOpaque8(inputStream)));
        }
    }
    
    public void validateCertificateRequest(final CertificateRequest certificateRequest) throws IOException {
        throw new TlsFatalAlert((short)10);
    }
    
    public void processClientCredentials(final TlsCredentials tlsCredentials) throws IOException {
        throw new TlsFatalAlert((short)80);
    }
    
    public void generateClientKeyExchange(final OutputStream outputStream) throws IOException {
        if (this.psk_identity_hint == null) {
            this.pskIdentity.skipIdentityHint();
        }
        else {
            this.pskIdentity.notifyIdentityHint(this.psk_identity_hint);
        }
        final byte[] pskIdentity = this.pskIdentity.getPSKIdentity();
        if (pskIdentity == null) {
            throw new TlsFatalAlert((short)80);
        }
        this.psk = this.pskIdentity.getPSK();
        if (this.psk == null) {
            throw new TlsFatalAlert((short)80);
        }
        TlsUtils.writeOpaque16(pskIdentity, outputStream);
        this.context.getSecurityParameters().pskIdentity = Arrays.clone(pskIdentity);
        if (this.keyExchange == 14) {
            this.dhAgreePrivateKey = TlsDHUtils.generateEphemeralClientKeyExchange(this.context.getSecureRandom(), this.dhParameters, outputStream);
        }
        else if (this.keyExchange == 24) {
            this.ecAgreePrivateKey = TlsECCUtils.generateEphemeralClientKeyExchange(this.context.getSecureRandom(), this.serverECPointFormats, this.ecAgreePublicKey.getParameters(), outputStream);
        }
        else if (this.keyExchange == 15) {
            this.premasterSecret = TlsRSAUtils.generateEncryptedPreMasterSecret(this.context, this.rsaServerPublicKey, outputStream);
        }
    }
    
    @Override
    public void processClientKeyExchange(final InputStream inputStream) throws IOException {
        final byte[] opaque16 = TlsUtils.readOpaque16(inputStream);
        this.psk = this.pskIdentityManager.getPSK(opaque16);
        if (this.psk == null) {
            throw new TlsFatalAlert((short)115);
        }
        this.context.getSecurityParameters().pskIdentity = opaque16;
        if (this.keyExchange == 14) {
            this.dhAgreePublicKey = TlsDHUtils.validateDHPublicKey(new DHPublicKeyParameters(TlsDHUtils.readDHParameter(inputStream), this.dhParameters));
        }
        else if (this.keyExchange == 24) {
            this.ecAgreePublicKey = TlsECCUtils.validateECPublicKey(TlsECCUtils.deserializeECPublicKey(this.serverECPointFormats, this.ecAgreePrivateKey.getParameters(), TlsUtils.readOpaque8(inputStream)));
        }
        else if (this.keyExchange == 15) {
            byte[] array;
            if (TlsUtils.isSSL(this.context)) {
                array = Streams.readAll(inputStream);
            }
            else {
                array = TlsUtils.readOpaque16(inputStream);
            }
            this.premasterSecret = this.serverCredentials.decryptPreMasterSecret(array);
        }
    }
    
    public byte[] generatePremasterSecret() throws IOException {
        final byte[] generateOtherSecret = this.generateOtherSecret(this.psk.length);
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(4 + generateOtherSecret.length + this.psk.length);
        TlsUtils.writeOpaque16(generateOtherSecret, byteArrayOutputStream);
        TlsUtils.writeOpaque16(this.psk, byteArrayOutputStream);
        Arrays.fill(this.psk, (byte)0);
        this.psk = null;
        return byteArrayOutputStream.toByteArray();
    }
    
    protected byte[] generateOtherSecret(final int n) throws IOException {
        if (this.keyExchange == 14) {
            if (this.dhAgreePrivateKey != null) {
                return TlsDHUtils.calculateDHBasicAgreement(this.dhAgreePublicKey, this.dhAgreePrivateKey);
            }
            throw new TlsFatalAlert((short)80);
        }
        else if (this.keyExchange == 24) {
            if (this.ecAgreePrivateKey != null) {
                return TlsECCUtils.calculateECDHBasicAgreement(this.ecAgreePublicKey, this.ecAgreePrivateKey);
            }
            throw new TlsFatalAlert((short)80);
        }
        else {
            if (this.keyExchange == 15) {
                return this.premasterSecret;
            }
            return new byte[n];
        }
    }
    
    protected RSAKeyParameters validateRSAPublicKey(final RSAKeyParameters rsaKeyParameters) throws IOException {
        if (!rsaKeyParameters.getExponent().isProbablePrime(2)) {
            throw new TlsFatalAlert((short)47);
        }
        return rsaKeyParameters;
    }
}
