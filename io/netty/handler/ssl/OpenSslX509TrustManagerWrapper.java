package io.netty.handler.ssl;

import java.security.KeyManagementException;
import java.security.AccessController;
import java.lang.reflect.Field;
import javax.net.ssl.X509ExtendedTrustManager;
import java.security.PrivilegedAction;
import java.security.SecureRandom;
import javax.net.ssl.KeyManager;
import io.netty.util.internal.EmptyArrays;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.TrustManager;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.SuppressJava6Requirement;

@SuppressJava6Requirement(reason = "Usage guarded by java version check")
final class OpenSslX509TrustManagerWrapper
{
    private static final InternalLogger LOGGER;
    private static final TrustManagerWrapper WRAPPER;
    
    private OpenSslX509TrustManagerWrapper() {
    }
    
    static X509TrustManager wrapIfNeeded(final X509TrustManager trustManager) {
        return OpenSslX509TrustManagerWrapper.WRAPPER.wrapIfNeeded(trustManager);
    }
    
    private static SSLContext newSSLContext() throws NoSuchAlgorithmException, NoSuchProviderException {
        return SSLContext.getInstance("TLS", "SunJSSE");
    }
    
    static {
        LOGGER = InternalLoggerFactory.getInstance(OpenSslX509TrustManagerWrapper.class);
        TrustManagerWrapper wrapper = new TrustManagerWrapper() {
            @Override
            public X509TrustManager wrapIfNeeded(final X509TrustManager manager) {
                return manager;
            }
        };
        Throwable cause = null;
        final Throwable unsafeCause = PlatformDependent.getUnsafeUnavailabilityCause();
        if (unsafeCause == null) {
            SSLContext context;
            try {
                context = newSSLContext();
                context.init(null, new TrustManager[] { new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(final X509Certificate[] x509Certificates, final String s) throws CertificateException {
                            throw new CertificateException();
                        }
                        
                        @Override
                        public void checkServerTrusted(final X509Certificate[] x509Certificates, final String s) throws CertificateException {
                            throw new CertificateException();
                        }
                        
                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return EmptyArrays.EMPTY_X509_CERTIFICATES;
                        }
                    } }, null);
            }
            catch (final Throwable error) {
                context = null;
                cause = error;
            }
            if (cause != null) {
                OpenSslX509TrustManagerWrapper.LOGGER.debug("Unable to access wrapped TrustManager", cause);
            }
            else {
                final SSLContext finalContext = context;
                final Object maybeWrapper = AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                    @Override
                    public Object run() {
                        try {
                            final Field contextSpiField = SSLContext.class.getDeclaredField("contextSpi");
                            final long spiOffset = PlatformDependent.objectFieldOffset(contextSpiField);
                            final Object spi = PlatformDependent.getObject(finalContext, spiOffset);
                            if (spi != null) {
                                Class<?> clazz = spi.getClass();
                                do {
                                    try {
                                        final Field trustManagerField = clazz.getDeclaredField("trustManager");
                                        final long tmOffset = PlatformDependent.objectFieldOffset(trustManagerField);
                                        final Object trustManager = PlatformDependent.getObject(spi, tmOffset);
                                        if (trustManager instanceof X509ExtendedTrustManager) {
                                            return new UnsafeTrustManagerWrapper(spiOffset, tmOffset);
                                        }
                                    }
                                    catch (final NoSuchFieldException ex) {}
                                    clazz = clazz.getSuperclass();
                                } while (clazz != null);
                            }
                            throw new NoSuchFieldException();
                        }
                        catch (final NoSuchFieldException e) {
                            return e;
                        }
                        catch (final SecurityException e2) {
                            return e2;
                        }
                    }
                });
                if (maybeWrapper instanceof Throwable) {
                    OpenSslX509TrustManagerWrapper.LOGGER.debug("Unable to access wrapped TrustManager", (Throwable)maybeWrapper);
                }
                else {
                    wrapper = (TrustManagerWrapper)maybeWrapper;
                }
            }
        }
        else {
            OpenSslX509TrustManagerWrapper.LOGGER.debug("Unable to access wrapped TrustManager", cause);
        }
        WRAPPER = wrapper;
    }
    
    private static final class UnsafeTrustManagerWrapper implements TrustManagerWrapper
    {
        private final long spiOffset;
        private final long tmOffset;
        
        UnsafeTrustManagerWrapper(final long spiOffset, final long tmOffset) {
            this.spiOffset = spiOffset;
            this.tmOffset = tmOffset;
        }
        
        @SuppressJava6Requirement(reason = "Usage guarded by java version check")
        @Override
        public X509TrustManager wrapIfNeeded(final X509TrustManager manager) {
            if (!(manager instanceof X509ExtendedTrustManager)) {
                try {
                    final SSLContext ctx = newSSLContext();
                    ctx.init(null, new TrustManager[] { manager }, null);
                    final Object spi = PlatformDependent.getObject(ctx, this.spiOffset);
                    if (spi != null) {
                        final Object tm = PlatformDependent.getObject(spi, this.tmOffset);
                        if (tm instanceof X509ExtendedTrustManager) {
                            return (X509TrustManager)tm;
                        }
                    }
                }
                catch (final NoSuchAlgorithmException e) {
                    PlatformDependent.throwException(e);
                }
                catch (final KeyManagementException e2) {
                    PlatformDependent.throwException(e2);
                }
                catch (final NoSuchProviderException e3) {
                    PlatformDependent.throwException(e3);
                }
            }
            return manager;
        }
    }
    
    private interface TrustManagerWrapper
    {
        X509TrustManager wrapIfNeeded(final X509TrustManager p0);
    }
}
