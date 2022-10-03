package org.bouncycastle.crypto.tls;

import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.agreement.srp.SRP6Util;
import org.bouncycastle.util.io.TeeInputStream;
import java.io.InputStream;
import org.bouncycastle.crypto.Digest;
import java.io.OutputStream;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import java.io.IOException;
import java.util.Vector;
import java.math.BigInteger;
import org.bouncycastle.crypto.agreement.srp.SRP6Server;
import org.bouncycastle.crypto.agreement.srp.SRP6Client;
import org.bouncycastle.crypto.params.SRP6GroupParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class TlsSRPKeyExchange extends AbstractTlsKeyExchange
{
    protected TlsSigner tlsSigner;
    protected TlsSRPGroupVerifier groupVerifier;
    protected byte[] identity;
    protected byte[] password;
    protected AsymmetricKeyParameter serverPublicKey;
    protected SRP6GroupParameters srpGroup;
    protected SRP6Client srpClient;
    protected SRP6Server srpServer;
    protected BigInteger srpPeerCredentials;
    protected BigInteger srpVerifier;
    protected byte[] srpSalt;
    protected TlsSignerCredentials serverCredentials;
    
    protected static TlsSigner createSigner(final int n) {
        switch (n) {
            case 21: {
                return null;
            }
            case 23: {
                return new TlsRSASigner();
            }
            case 22: {
                return new TlsDSSSigner();
            }
            default: {
                throw new IllegalArgumentException("unsupported key exchange algorithm");
            }
        }
    }
    
    @Deprecated
    public TlsSRPKeyExchange(final int n, final Vector vector, final byte[] array, final byte[] array2) {
        this(n, vector, new DefaultTlsSRPGroupVerifier(), array, array2);
    }
    
    public TlsSRPKeyExchange(final int n, final Vector vector, final TlsSRPGroupVerifier groupVerifier, final byte[] identity, final byte[] password) {
        super(n, vector);
        this.serverPublicKey = null;
        this.srpGroup = null;
        this.srpClient = null;
        this.srpServer = null;
        this.srpPeerCredentials = null;
        this.srpVerifier = null;
        this.srpSalt = null;
        this.serverCredentials = null;
        this.tlsSigner = createSigner(n);
        this.groupVerifier = groupVerifier;
        this.identity = identity;
        this.password = password;
        this.srpClient = new SRP6Client();
    }
    
    public TlsSRPKeyExchange(final int n, final Vector vector, final byte[] identity, final TlsSRPLoginParameters tlsSRPLoginParameters) {
        super(n, vector);
        this.serverPublicKey = null;
        this.srpGroup = null;
        this.srpClient = null;
        this.srpServer = null;
        this.srpPeerCredentials = null;
        this.srpVerifier = null;
        this.srpSalt = null;
        this.serverCredentials = null;
        this.tlsSigner = createSigner(n);
        this.identity = identity;
        this.srpServer = new SRP6Server();
        this.srpGroup = tlsSRPLoginParameters.getGroup();
        this.srpVerifier = tlsSRPLoginParameters.getVerifier();
        this.srpSalt = tlsSRPLoginParameters.getSalt();
    }
    
    @Override
    public void init(final TlsContext tlsContext) {
        super.init(tlsContext);
        if (this.tlsSigner != null) {
            this.tlsSigner.init(tlsContext);
        }
    }
    
    public void skipServerCredentials() throws IOException {
        if (this.tlsSigner != null) {
            throw new TlsFatalAlert((short)10);
        }
    }
    
    @Override
    public void processServerCertificate(final Certificate certificate) throws IOException {
        if (this.tlsSigner == null) {
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
        if (!this.tlsSigner.isValidPublicKey(this.serverPublicKey)) {
            throw new TlsFatalAlert((short)46);
        }
        TlsUtils.validateKeyUsage(certificate2, 128);
        super.processServerCertificate(certificate);
    }
    
    @Override
    public void processServerCredentials(final TlsCredentials tlsCredentials) throws IOException {
        if (this.keyExchange == 21 || !(tlsCredentials instanceof TlsSignerCredentials)) {
            throw new TlsFatalAlert((short)80);
        }
        this.processServerCertificate(tlsCredentials.getCertificate());
        this.serverCredentials = (TlsSignerCredentials)tlsCredentials;
    }
    
    @Override
    public boolean requiresServerKeyExchange() {
        return true;
    }
    
    @Override
    public byte[] generateServerKeyExchange() throws IOException {
        this.srpServer.init(this.srpGroup, this.srpVerifier, TlsUtils.createHash((short)2), this.context.getSecureRandom());
        final ServerSRPParams serverSRPParams = new ServerSRPParams(this.srpGroup.getN(), this.srpGroup.getG(), this.srpSalt, this.srpServer.generateServerCredentials());
        final DigestInputBuffer digestInputBuffer = new DigestInputBuffer();
        serverSRPParams.encode(digestInputBuffer);
        if (this.serverCredentials != null) {
            final SignatureAndHashAlgorithm signatureAndHashAlgorithm = TlsUtils.getSignatureAndHashAlgorithm(this.context, this.serverCredentials);
            final Digest hash = TlsUtils.createHash(signatureAndHashAlgorithm);
            final SecurityParameters securityParameters = this.context.getSecurityParameters();
            hash.update(securityParameters.clientRandom, 0, securityParameters.clientRandom.length);
            hash.update(securityParameters.serverRandom, 0, securityParameters.serverRandom.length);
            digestInputBuffer.updateDigest(hash);
            final byte[] array = new byte[hash.getDigestSize()];
            hash.doFinal(array, 0);
            new DigitallySigned(signatureAndHashAlgorithm, this.serverCredentials.generateCertificateSignature(array)).encode(digestInputBuffer);
        }
        return digestInputBuffer.toByteArray();
    }
    
    @Override
    public void processServerKeyExchange(final InputStream inputStream) throws IOException {
        final SecurityParameters securityParameters = this.context.getSecurityParameters();
        SignerInputBuffer signerInputBuffer = null;
        InputStream inputStream2 = inputStream;
        if (this.tlsSigner != null) {
            signerInputBuffer = new SignerInputBuffer();
            inputStream2 = new TeeInputStream(inputStream, signerInputBuffer);
        }
        final ServerSRPParams parse = ServerSRPParams.parse(inputStream2);
        if (signerInputBuffer != null) {
            final DigitallySigned signature = this.parseSignature(inputStream);
            final Signer initVerifyer = this.initVerifyer(this.tlsSigner, signature.getAlgorithm(), securityParameters);
            signerInputBuffer.updateSigner(initVerifyer);
            if (!initVerifyer.verifySignature(signature.getSignature())) {
                throw new TlsFatalAlert((short)51);
            }
        }
        this.srpGroup = new SRP6GroupParameters(parse.getN(), parse.getG());
        if (!this.groupVerifier.accept(this.srpGroup)) {
            throw new TlsFatalAlert((short)71);
        }
        this.srpSalt = parse.getS();
        try {
            this.srpPeerCredentials = SRP6Util.validatePublicValue(this.srpGroup.getN(), parse.getB());
        }
        catch (final CryptoException ex) {
            throw new TlsFatalAlert((short)47, ex);
        }
        this.srpClient.init(this.srpGroup, TlsUtils.createHash((short)2), this.context.getSecureRandom());
    }
    
    public void validateCertificateRequest(final CertificateRequest certificateRequest) throws IOException {
        throw new TlsFatalAlert((short)10);
    }
    
    public void processClientCredentials(final TlsCredentials tlsCredentials) throws IOException {
        throw new TlsFatalAlert((short)80);
    }
    
    public void generateClientKeyExchange(final OutputStream outputStream) throws IOException {
        TlsSRPUtils.writeSRPParameter(this.srpClient.generateClientCredentials(this.srpSalt, this.identity, this.password), outputStream);
        this.context.getSecurityParameters().srpIdentity = Arrays.clone(this.identity);
    }
    
    @Override
    public void processClientKeyExchange(final InputStream inputStream) throws IOException {
        try {
            this.srpPeerCredentials = SRP6Util.validatePublicValue(this.srpGroup.getN(), TlsSRPUtils.readSRPParameter(inputStream));
        }
        catch (final CryptoException ex) {
            throw new TlsFatalAlert((short)47, ex);
        }
        this.context.getSecurityParameters().srpIdentity = Arrays.clone(this.identity);
    }
    
    public byte[] generatePremasterSecret() throws IOException {
        try {
            return BigIntegers.asUnsignedByteArray((this.srpServer != null) ? this.srpServer.calculateSecret(this.srpPeerCredentials) : this.srpClient.calculateSecret(this.srpPeerCredentials));
        }
        catch (final CryptoException ex) {
            throw new TlsFatalAlert((short)47, ex);
        }
    }
    
    protected Signer initVerifyer(final TlsSigner tlsSigner, final SignatureAndHashAlgorithm signatureAndHashAlgorithm, final SecurityParameters securityParameters) {
        final Signer verifyer = tlsSigner.createVerifyer(signatureAndHashAlgorithm, this.serverPublicKey);
        verifyer.update(securityParameters.clientRandom, 0, securityParameters.clientRandom.length);
        verifyer.update(securityParameters.serverRandom, 0, securityParameters.serverRandom.length);
        return verifyer;
    }
}
