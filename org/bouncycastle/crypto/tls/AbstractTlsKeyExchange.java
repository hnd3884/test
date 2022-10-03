package org.bouncycastle.crypto.tls;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

public abstract class AbstractTlsKeyExchange implements TlsKeyExchange
{
    protected int keyExchange;
    protected Vector supportedSignatureAlgorithms;
    protected TlsContext context;
    
    protected AbstractTlsKeyExchange(final int keyExchange, final Vector supportedSignatureAlgorithms) {
        this.keyExchange = keyExchange;
        this.supportedSignatureAlgorithms = supportedSignatureAlgorithms;
    }
    
    protected DigitallySigned parseSignature(final InputStream inputStream) throws IOException {
        final DigitallySigned parse = DigitallySigned.parse(this.context, inputStream);
        final SignatureAndHashAlgorithm algorithm = parse.getAlgorithm();
        if (algorithm != null) {
            TlsUtils.verifySupportedSignatureAlgorithm(this.supportedSignatureAlgorithms, algorithm);
        }
        return parse;
    }
    
    public void init(final TlsContext context) {
        this.context = context;
        final ProtocolVersion clientVersion = context.getClientVersion();
        if (TlsUtils.isSignatureAlgorithmsExtensionAllowed(clientVersion)) {
            if (this.supportedSignatureAlgorithms == null) {
                switch (this.keyExchange) {
                    case 3:
                    case 7:
                    case 22: {
                        this.supportedSignatureAlgorithms = TlsUtils.getDefaultDSSSignatureAlgorithms();
                        break;
                    }
                    case 16:
                    case 17: {
                        this.supportedSignatureAlgorithms = TlsUtils.getDefaultECDSASignatureAlgorithms();
                        break;
                    }
                    case 1:
                    case 5:
                    case 9:
                    case 15:
                    case 18:
                    case 19:
                    case 23: {
                        this.supportedSignatureAlgorithms = TlsUtils.getDefaultRSASignatureAlgorithms();
                        break;
                    }
                    case 13:
                    case 14:
                    case 21:
                    case 24: {
                        break;
                    }
                    default: {
                        throw new IllegalStateException("unsupported key exchange algorithm");
                    }
                }
            }
        }
        else if (this.supportedSignatureAlgorithms != null) {
            throw new IllegalStateException("supported_signature_algorithms not allowed for " + clientVersion);
        }
    }
    
    public void processServerCertificate(final Certificate certificate) throws IOException {
        if (this.supportedSignatureAlgorithms == null) {}
    }
    
    public void processServerCredentials(final TlsCredentials tlsCredentials) throws IOException {
        this.processServerCertificate(tlsCredentials.getCertificate());
    }
    
    public boolean requiresServerKeyExchange() {
        return false;
    }
    
    public byte[] generateServerKeyExchange() throws IOException {
        if (this.requiresServerKeyExchange()) {
            throw new TlsFatalAlert((short)80);
        }
        return null;
    }
    
    public void skipServerKeyExchange() throws IOException {
        if (this.requiresServerKeyExchange()) {
            throw new TlsFatalAlert((short)10);
        }
    }
    
    public void processServerKeyExchange(final InputStream inputStream) throws IOException {
        if (!this.requiresServerKeyExchange()) {
            throw new TlsFatalAlert((short)10);
        }
    }
    
    public void skipClientCredentials() throws IOException {
    }
    
    public void processClientCertificate(final Certificate certificate) throws IOException {
    }
    
    public void processClientKeyExchange(final InputStream inputStream) throws IOException {
        throw new TlsFatalAlert((short)80);
    }
}
