package io.netty.handler.ssl;

import java.security.cert.X509Certificate;
import io.netty.util.ReferenceCounted;

interface OpenSslKeyMaterial extends ReferenceCounted
{
    X509Certificate[] certificateChain();
    
    long certificateChainAddress();
    
    long privateKeyAddress();
    
    OpenSslKeyMaterial retain();
    
    OpenSslKeyMaterial retain(final int p0);
    
    OpenSslKeyMaterial touch();
    
    OpenSslKeyMaterial touch(final Object p0);
    
    boolean release();
    
    boolean release(final int p0);
}
