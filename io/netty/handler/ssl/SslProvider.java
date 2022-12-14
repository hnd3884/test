package io.netty.handler.ssl;

import java.security.Provider;

public enum SslProvider
{
    JDK, 
    OPENSSL, 
    OPENSSL_REFCNT;
    
    public static boolean isAlpnSupported(final SslProvider provider) {
        switch (provider) {
            case JDK: {
                return JdkAlpnApplicationProtocolNegotiator.isAlpnSupported();
            }
            case OPENSSL:
            case OPENSSL_REFCNT: {
                return OpenSsl.isAlpnSupported();
            }
            default: {
                throw new Error("Unknown SslProvider: " + provider);
            }
        }
    }
    
    public static boolean isTlsv13Supported(final SslProvider sslProvider) {
        return isTlsv13Supported(sslProvider, null);
    }
    
    public static boolean isTlsv13Supported(final SslProvider sslProvider, final Provider provider) {
        switch (sslProvider) {
            case JDK: {
                return SslUtils.isTLSv13SupportedByJDK(provider);
            }
            case OPENSSL:
            case OPENSSL_REFCNT: {
                return OpenSsl.isTlsv13Supported();
            }
            default: {
                throw new Error("Unknown SslProvider: " + sslProvider);
            }
        }
    }
    
    static boolean isTlsv13EnabledByDefault(final SslProvider sslProvider, final Provider provider) {
        switch (sslProvider) {
            case JDK: {
                return SslUtils.isTLSv13EnabledByJDK(provider);
            }
            case OPENSSL:
            case OPENSSL_REFCNT: {
                return OpenSsl.isTlsv13Supported();
            }
            default: {
                throw new Error("Unknown SslProvider: " + sslProvider);
            }
        }
    }
}
