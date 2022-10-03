package org.bouncycastle.crypto.tls;

import org.bouncycastle.crypto.params.DHParameters;
import java.io.IOException;

public abstract class DefaultTlsClient extends AbstractTlsClient
{
    public DefaultTlsClient() {
    }
    
    public DefaultTlsClient(final TlsCipherFactory tlsCipherFactory) {
        super(tlsCipherFactory);
    }
    
    public int[] getCipherSuites() {
        return new int[] { 49195, 49187, 49161, 49199, 49191, 49171, 162, 64, 50, 158, 103, 51, 156, 60, 47 };
    }
    
    public TlsKeyExchange getKeyExchange() throws IOException {
        final int keyExchangeAlgorithm = TlsUtils.getKeyExchangeAlgorithm(this.selectedCipherSuite);
        switch (keyExchangeAlgorithm) {
            case 7:
            case 9:
            case 11: {
                return this.createDHKeyExchange(keyExchangeAlgorithm);
            }
            case 3:
            case 5: {
                return this.createDHEKeyExchange(keyExchangeAlgorithm);
            }
            case 16:
            case 18:
            case 20: {
                return this.createECDHKeyExchange(keyExchangeAlgorithm);
            }
            case 17:
            case 19: {
                return this.createECDHEKeyExchange(keyExchangeAlgorithm);
            }
            case 1: {
                return this.createRSAKeyExchange();
            }
            default: {
                throw new TlsFatalAlert((short)80);
            }
        }
    }
    
    protected TlsKeyExchange createDHKeyExchange(final int n) {
        return new TlsDHKeyExchange(n, this.supportedSignatureAlgorithms, null);
    }
    
    protected TlsKeyExchange createDHEKeyExchange(final int n) {
        return new TlsDHEKeyExchange(n, this.supportedSignatureAlgorithms, null);
    }
    
    protected TlsKeyExchange createECDHKeyExchange(final int n) {
        return new TlsECDHKeyExchange(n, this.supportedSignatureAlgorithms, this.namedCurves, this.clientECPointFormats, this.serverECPointFormats);
    }
    
    protected TlsKeyExchange createECDHEKeyExchange(final int n) {
        return new TlsECDHEKeyExchange(n, this.supportedSignatureAlgorithms, this.namedCurves, this.clientECPointFormats, this.serverECPointFormats);
    }
    
    protected TlsKeyExchange createRSAKeyExchange() {
        return new TlsRSAKeyExchange(this.supportedSignatureAlgorithms);
    }
}
