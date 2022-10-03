package io.netty.handler.ssl;

import io.netty.buffer.ByteBufUtil;
import java.lang.reflect.AccessibleObject;
import io.netty.util.internal.ReflectionUtil;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.SystemPropertyUtil;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLEngine;
import io.netty.channel.ChannelHandlerContext;
import javax.net.ssl.SSLSession;
import javax.crypto.SecretKey;
import java.lang.reflect.Field;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.channel.ChannelInboundHandlerAdapter;

public abstract class SslMasterKeyHandler extends ChannelInboundHandlerAdapter
{
    private static final InternalLogger logger;
    private static final Class<?> SSL_SESSIONIMPL_CLASS;
    private static final Field SSL_SESSIONIMPL_MASTER_SECRET_FIELD;
    public static final String SYSTEM_PROP_KEY = "io.netty.ssl.masterKeyHandler";
    private static final Throwable UNAVAILABILITY_CAUSE;
    
    protected SslMasterKeyHandler() {
    }
    
    public static void ensureSunSslEngineAvailability() {
        if (SslMasterKeyHandler.UNAVAILABILITY_CAUSE != null) {
            throw new IllegalStateException("Failed to find SSLSessionImpl on classpath", SslMasterKeyHandler.UNAVAILABILITY_CAUSE);
        }
    }
    
    public static Throwable sunSslEngineUnavailabilityCause() {
        return SslMasterKeyHandler.UNAVAILABILITY_CAUSE;
    }
    
    public static boolean isSunSslEngineAvailable() {
        return SslMasterKeyHandler.UNAVAILABILITY_CAUSE == null;
    }
    
    protected abstract void accept(final SecretKey p0, final SSLSession p1);
    
    @Override
    public final void userEventTriggered(final ChannelHandlerContext ctx, final Object evt) {
        if (evt == SslHandshakeCompletionEvent.SUCCESS && this.masterKeyHandlerEnabled()) {
            final SslHandler handler = ctx.pipeline().get(SslHandler.class);
            final SSLEngine engine = handler.engine();
            final SSLSession sslSession = engine.getSession();
            if (isSunSslEngineAvailable() && sslSession.getClass().equals(SslMasterKeyHandler.SSL_SESSIONIMPL_CLASS)) {
                SecretKey secretKey;
                try {
                    secretKey = (SecretKey)SslMasterKeyHandler.SSL_SESSIONIMPL_MASTER_SECRET_FIELD.get(sslSession);
                }
                catch (final IllegalAccessException e) {
                    throw new IllegalArgumentException("Failed to access the field 'masterSecret' via reflection.", e);
                }
                this.accept(secretKey, sslSession);
            }
            else if (OpenSsl.isAvailable() && engine instanceof ReferenceCountedOpenSslEngine) {
                final SecretKeySpec secretKey2 = ((ReferenceCountedOpenSslEngine)engine).masterKey();
                this.accept(secretKey2, sslSession);
            }
        }
        ctx.fireUserEventTriggered(evt);
    }
    
    protected boolean masterKeyHandlerEnabled() {
        return SystemPropertyUtil.getBoolean("io.netty.ssl.masterKeyHandler", false);
    }
    
    public static SslMasterKeyHandler newWireSharkSslMasterKeyHandler() {
        return new WiresharkSslMasterKeyHandler();
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(SslMasterKeyHandler.class);
        Class<?> clazz = null;
        Field field = null;
        Throwable cause;
        try {
            clazz = Class.forName("sun.security.ssl.SSLSessionImpl");
            field = clazz.getDeclaredField("masterSecret");
            cause = ReflectionUtil.trySetAccessible(field, true);
        }
        catch (final Throwable e) {
            cause = e;
            if (SslMasterKeyHandler.logger.isTraceEnabled()) {
                SslMasterKeyHandler.logger.debug("sun.security.ssl.SSLSessionImpl is unavailable.", e);
            }
            else {
                SslMasterKeyHandler.logger.debug("sun.security.ssl.SSLSessionImpl is unavailable: {}", e.getMessage());
            }
        }
        UNAVAILABILITY_CAUSE = cause;
        SSL_SESSIONIMPL_CLASS = clazz;
        SSL_SESSIONIMPL_MASTER_SECRET_FIELD = field;
    }
    
    private static final class WiresharkSslMasterKeyHandler extends SslMasterKeyHandler
    {
        private static final InternalLogger wireshark_logger;
        
        @Override
        protected void accept(final SecretKey masterKey, final SSLSession session) {
            if (masterKey.getEncoded().length != 48) {
                throw new IllegalArgumentException("An invalid length master key was provided.");
            }
            final byte[] sessionId = session.getId();
            WiresharkSslMasterKeyHandler.wireshark_logger.warn("RSA Session-ID:{} Master-Key:{}", ByteBufUtil.hexDump(sessionId).toLowerCase(), ByteBufUtil.hexDump(masterKey.getEncoded()).toLowerCase());
        }
        
        static {
            wireshark_logger = InternalLoggerFactory.getInstance("io.netty.wireshark");
        }
    }
}
