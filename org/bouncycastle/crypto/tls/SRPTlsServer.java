package org.bouncycastle.crypto.tls;

import java.util.Hashtable;
import java.io.IOException;

public class SRPTlsServer extends AbstractTlsServer
{
    protected TlsSRPIdentityManager srpIdentityManager;
    protected byte[] srpIdentity;
    protected TlsSRPLoginParameters loginParameters;
    
    public SRPTlsServer(final TlsSRPIdentityManager tlsSRPIdentityManager) {
        this(new DefaultTlsCipherFactory(), tlsSRPIdentityManager);
    }
    
    public SRPTlsServer(final TlsCipherFactory tlsCipherFactory, final TlsSRPIdentityManager srpIdentityManager) {
        super(tlsCipherFactory);
        this.srpIdentity = null;
        this.loginParameters = null;
        this.srpIdentityManager = srpIdentityManager;
    }
    
    protected TlsSignerCredentials getDSASignerCredentials() throws IOException {
        throw new TlsFatalAlert((short)80);
    }
    
    protected TlsSignerCredentials getRSASignerCredentials() throws IOException {
        throw new TlsFatalAlert((short)80);
    }
    
    @Override
    protected int[] getCipherSuites() {
        return new int[] { 49186, 49183, 49185, 49182, 49184, 49181 };
    }
    
    @Override
    public void processClientExtensions(final Hashtable hashtable) throws IOException {
        super.processClientExtensions(hashtable);
        this.srpIdentity = TlsSRPUtils.getSRPExtension(hashtable);
    }
    
    @Override
    public int getSelectedCipherSuite() throws IOException {
        final int selectedCipherSuite = super.getSelectedCipherSuite();
        if (TlsSRPUtils.isSRPCipherSuite(selectedCipherSuite)) {
            if (this.srpIdentity != null) {
                this.loginParameters = this.srpIdentityManager.getLoginParameters(this.srpIdentity);
            }
            if (this.loginParameters == null) {
                throw new TlsFatalAlert((short)115);
            }
        }
        return selectedCipherSuite;
    }
    
    public TlsCredentials getCredentials() throws IOException {
        switch (TlsUtils.getKeyExchangeAlgorithm(this.selectedCipherSuite)) {
            case 21: {
                return null;
            }
            case 22: {
                return this.getDSASignerCredentials();
            }
            case 23: {
                return this.getRSASignerCredentials();
            }
            default: {
                throw new TlsFatalAlert((short)80);
            }
        }
    }
    
    public TlsKeyExchange getKeyExchange() throws IOException {
        final int keyExchangeAlgorithm = TlsUtils.getKeyExchangeAlgorithm(this.selectedCipherSuite);
        switch (keyExchangeAlgorithm) {
            case 21:
            case 22:
            case 23: {
                return this.createSRPKeyExchange(keyExchangeAlgorithm);
            }
            default: {
                throw new TlsFatalAlert((short)80);
            }
        }
    }
    
    protected TlsKeyExchange createSRPKeyExchange(final int n) {
        return new TlsSRPKeyExchange(n, this.supportedSignatureAlgorithms, this.srpIdentity, this.loginParameters);
    }
}
