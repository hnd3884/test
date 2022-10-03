package io.netty.handler.ssl.util;

import javax.net.ssl.SSLEngine;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.net.Socket;
import java.security.Principal;
import io.netty.util.internal.ObjectUtil;
import javax.net.ssl.X509KeyManager;
import io.netty.util.internal.SuppressJava6Requirement;
import javax.net.ssl.X509ExtendedKeyManager;

@SuppressJava6Requirement(reason = "Usage guarded by java version check")
final class X509KeyManagerWrapper extends X509ExtendedKeyManager
{
    private final X509KeyManager delegate;
    
    X509KeyManagerWrapper(final X509KeyManager delegate) {
        this.delegate = ObjectUtil.checkNotNull(delegate, "delegate");
    }
    
    @Override
    public String[] getClientAliases(final String var1, final Principal[] var2) {
        return this.delegate.getClientAliases(var1, var2);
    }
    
    @Override
    public String chooseClientAlias(final String[] var1, final Principal[] var2, final Socket var3) {
        return this.delegate.chooseClientAlias(var1, var2, var3);
    }
    
    @Override
    public String[] getServerAliases(final String var1, final Principal[] var2) {
        return this.delegate.getServerAliases(var1, var2);
    }
    
    @Override
    public String chooseServerAlias(final String var1, final Principal[] var2, final Socket var3) {
        return this.delegate.chooseServerAlias(var1, var2, var3);
    }
    
    @Override
    public X509Certificate[] getCertificateChain(final String var1) {
        return this.delegate.getCertificateChain(var1);
    }
    
    @Override
    public PrivateKey getPrivateKey(final String var1) {
        return this.delegate.getPrivateKey(var1);
    }
    
    @Override
    public String chooseEngineClientAlias(final String[] keyType, final Principal[] issuers, final SSLEngine engine) {
        return this.delegate.chooseClientAlias(keyType, issuers, null);
    }
    
    @Override
    public String chooseEngineServerAlias(final String keyType, final Principal[] issuers, final SSLEngine engine) {
        return this.delegate.chooseServerAlias(keyType, issuers, null);
    }
}
