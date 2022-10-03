package org.bouncycastle.crypto.tls;

import org.bouncycastle.crypto.params.DHParameters;
import java.io.IOException;

public class PSKTlsClient extends AbstractTlsClient
{
    protected TlsPSKIdentity pskIdentity;
    
    public PSKTlsClient(final TlsPSKIdentity tlsPSKIdentity) {
        this(new DefaultTlsCipherFactory(), tlsPSKIdentity);
    }
    
    public PSKTlsClient(final TlsCipherFactory tlsCipherFactory, final TlsPSKIdentity pskIdentity) {
        super(tlsCipherFactory);
        this.pskIdentity = pskIdentity;
    }
    
    public int[] getCipherSuites() {
        return new int[] { 49207, 49205, 178, 144 };
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
    
    public TlsAuthentication getAuthentication() throws IOException {
        throw new TlsFatalAlert((short)80);
    }
    
    protected TlsKeyExchange createPSKKeyExchange(final int n) {
        return new TlsPSKKeyExchange(n, this.supportedSignatureAlgorithms, this.pskIdentity, null, null, this.namedCurves, this.clientECPointFormats, this.serverECPointFormats);
    }
}
