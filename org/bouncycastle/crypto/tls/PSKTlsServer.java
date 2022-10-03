package org.bouncycastle.crypto.tls;

import org.bouncycastle.crypto.agreement.DHStandardGroups;
import org.bouncycastle.crypto.params.DHParameters;
import java.io.IOException;

public class PSKTlsServer extends AbstractTlsServer
{
    protected TlsPSKIdentityManager pskIdentityManager;
    
    public PSKTlsServer(final TlsPSKIdentityManager tlsPSKIdentityManager) {
        this(new DefaultTlsCipherFactory(), tlsPSKIdentityManager);
    }
    
    public PSKTlsServer(final TlsCipherFactory tlsCipherFactory, final TlsPSKIdentityManager pskIdentityManager) {
        super(tlsCipherFactory);
        this.pskIdentityManager = pskIdentityManager;
    }
    
    protected TlsEncryptionCredentials getRSAEncryptionCredentials() throws IOException {
        throw new TlsFatalAlert((short)80);
    }
    
    protected DHParameters getDHParameters() {
        return DHStandardGroups.rfc7919_ffdhe2048;
    }
    
    @Override
    protected int[] getCipherSuites() {
        return new int[] { 49207, 49205, 178, 144 };
    }
    
    public TlsCredentials getCredentials() throws IOException {
        switch (TlsUtils.getKeyExchangeAlgorithm(this.selectedCipherSuite)) {
            case 13:
            case 14:
            case 24: {
                return null;
            }
            case 15: {
                return this.getRSAEncryptionCredentials();
            }
            default: {
                throw new TlsFatalAlert((short)80);
            }
        }
    }
    
    public TlsKeyExchange getKeyExchange() throws IOException {
        final int keyExchangeAlgorithm = TlsUtils.getKeyExchangeAlgorithm(this.selectedCipherSuite);
        switch (keyExchangeAlgorithm) {
            case 13:
            case 14:
            case 15:
            case 24: {
                return this.createPSKKeyExchange(keyExchangeAlgorithm);
            }
            default: {
                throw new TlsFatalAlert((short)80);
            }
        }
    }
    
    protected TlsKeyExchange createPSKKeyExchange(final int n) {
        return new TlsPSKKeyExchange(n, this.supportedSignatureAlgorithms, null, this.pskIdentityManager, this.getDHParameters(), this.namedCurves, this.clientECPointFormats, this.serverECPointFormats);
    }
}
