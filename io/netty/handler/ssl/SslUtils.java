package io.netty.handler.ssl;

import io.netty.util.internal.EmptyArrays;
import java.util.Collections;
import java.util.Collection;
import java.util.LinkedHashSet;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.NetUtil;
import io.netty.handler.codec.base64.Base64;
import io.netty.handler.codec.base64.Base64Dialect;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.buffer.ByteBufUtil;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import io.netty.buffer.ByteBuf;
import javax.net.ssl.SSLHandshakeException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.security.NoSuchProviderException;
import io.netty.util.internal.StringUtil;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;
import javax.net.ssl.SSLContext;
import java.security.Provider;
import java.util.Set;
import io.netty.util.internal.logging.InternalLogger;

final class SslUtils
{
    private static final InternalLogger logger;
    static final Set<String> TLSV13_CIPHERS;
    static final int GMSSL_PROTOCOL_VERSION = 257;
    static final String INVALID_CIPHER = "SSL_NULL_WITH_NULL_NULL";
    static final int SSL_CONTENT_TYPE_CHANGE_CIPHER_SPEC = 20;
    static final int SSL_CONTENT_TYPE_ALERT = 21;
    static final int SSL_CONTENT_TYPE_HANDSHAKE = 22;
    static final int SSL_CONTENT_TYPE_APPLICATION_DATA = 23;
    static final int SSL_CONTENT_TYPE_EXTENSION_HEARTBEAT = 24;
    static final int SSL_RECORD_HEADER_LENGTH = 5;
    static final int NOT_ENOUGH_DATA = -1;
    static final int NOT_ENCRYPTED = -2;
    static final String[] DEFAULT_CIPHER_SUITES;
    static final String[] DEFAULT_TLSV13_CIPHER_SUITES;
    static final String[] TLSV13_CIPHER_SUITES;
    private static final boolean TLSV1_3_JDK_SUPPORTED;
    private static final boolean TLSV1_3_JDK_DEFAULT_ENABLED;
    
    static boolean isTLSv13SupportedByJDK(final Provider provider) {
        if (provider == null) {
            return SslUtils.TLSV1_3_JDK_SUPPORTED;
        }
        return isTLSv13SupportedByJDK0(provider);
    }
    
    private static boolean isTLSv13SupportedByJDK0(final Provider provider) {
        try {
            return arrayContains(newInitContext(provider).getSupportedSSLParameters().getProtocols(), "TLSv1.3");
        }
        catch (final Throwable cause) {
            SslUtils.logger.debug("Unable to detect if JDK SSLEngine with provider {} supports TLSv1.3, assuming no", provider, cause);
            return false;
        }
    }
    
    static boolean isTLSv13EnabledByJDK(final Provider provider) {
        if (provider == null) {
            return SslUtils.TLSV1_3_JDK_DEFAULT_ENABLED;
        }
        return isTLSv13EnabledByJDK0(provider);
    }
    
    private static boolean isTLSv13EnabledByJDK0(final Provider provider) {
        try {
            return arrayContains(newInitContext(provider).getDefaultSSLParameters().getProtocols(), "TLSv1.3");
        }
        catch (final Throwable cause) {
            SslUtils.logger.debug("Unable to detect if JDK SSLEngine with provider {} enables TLSv1.3 by default, assuming no", provider, cause);
            return false;
        }
    }
    
    private static SSLContext newInitContext(final Provider provider) throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext context;
        if (provider == null) {
            context = SSLContext.getInstance("TLS");
        }
        else {
            context = SSLContext.getInstance("TLS", provider);
        }
        context.init(null, new TrustManager[0], null);
        return context;
    }
    
    static SSLContext getSSLContext(final String provider) throws NoSuchAlgorithmException, KeyManagementException, NoSuchProviderException {
        SSLContext context;
        if (StringUtil.isNullOrEmpty(provider)) {
            context = SSLContext.getInstance(getTlsVersion());
        }
        else {
            context = SSLContext.getInstance(getTlsVersion(), provider);
        }
        context.init(null, new TrustManager[0], null);
        return context;
    }
    
    private static String getTlsVersion() {
        return SslUtils.TLSV1_3_JDK_SUPPORTED ? "TLSv1.3" : "TLSv1.2";
    }
    
    static boolean arrayContains(final String[] array, final String value) {
        for (final String v : array) {
            if (value.equals(v)) {
                return true;
            }
        }
        return false;
    }
    
    static void addIfSupported(final Set<String> supported, final List<String> enabled, final String... names) {
        for (final String n : names) {
            if (supported.contains(n)) {
                enabled.add(n);
            }
        }
    }
    
    static void useFallbackCiphersIfDefaultIsEmpty(final List<String> defaultCiphers, final Iterable<String> fallbackCiphers) {
        if (defaultCiphers.isEmpty()) {
            for (final String cipher : fallbackCiphers) {
                if (!cipher.startsWith("SSL_")) {
                    if (cipher.contains("_RC4_")) {
                        continue;
                    }
                    defaultCiphers.add(cipher);
                }
            }
        }
    }
    
    static void useFallbackCiphersIfDefaultIsEmpty(final List<String> defaultCiphers, final String... fallbackCiphers) {
        useFallbackCiphersIfDefaultIsEmpty(defaultCiphers, Arrays.asList(fallbackCiphers));
    }
    
    static SSLHandshakeException toSSLHandshakeException(final Throwable e) {
        if (e instanceof SSLHandshakeException) {
            return (SSLHandshakeException)e;
        }
        return (SSLHandshakeException)new SSLHandshakeException(e.getMessage()).initCause(e);
    }
    
    static int getEncryptedPacketLength(final ByteBuf buffer, final int offset) {
        int packetLength = 0;
        boolean tls = false;
        switch (buffer.getUnsignedByte(offset)) {
            case 20:
            case 21:
            case 22:
            case 23:
            case 24: {
                tls = true;
                break;
            }
            default: {
                tls = false;
                break;
            }
        }
        if (tls) {
            final int majorVersion = buffer.getUnsignedByte(offset + 1);
            if (majorVersion == 3 || buffer.getShort(offset + 1) == 257) {
                packetLength = unsignedShortBE(buffer, offset + 3) + 5;
                if (packetLength <= 5) {
                    tls = false;
                }
            }
            else {
                tls = false;
            }
        }
        if (!tls) {
            final int headerLength = ((buffer.getUnsignedByte(offset) & 0x80) != 0x0) ? 2 : 3;
            final int majorVersion2 = buffer.getUnsignedByte(offset + headerLength + 1);
            if (majorVersion2 != 2 && majorVersion2 != 3) {
                return -2;
            }
            packetLength = ((headerLength == 2) ? ((shortBE(buffer, offset) & 0x7FFF) + 2) : ((shortBE(buffer, offset) & 0x3FFF) + 3));
            if (packetLength <= headerLength) {
                return -1;
            }
        }
        return packetLength;
    }
    
    private static int unsignedShortBE(final ByteBuf buffer, final int offset) {
        return (buffer.order() == ByteOrder.BIG_ENDIAN) ? buffer.getUnsignedShort(offset) : buffer.getUnsignedShortLE(offset);
    }
    
    private static short shortBE(final ByteBuf buffer, final int offset) {
        return (buffer.order() == ByteOrder.BIG_ENDIAN) ? buffer.getShort(offset) : buffer.getShortLE(offset);
    }
    
    private static short unsignedByte(final byte b) {
        return (short)(b & 0xFF);
    }
    
    private static int unsignedShortBE(final ByteBuffer buffer, final int offset) {
        return shortBE(buffer, offset) & 0xFFFF;
    }
    
    private static short shortBE(final ByteBuffer buffer, final int offset) {
        return (buffer.order() == ByteOrder.BIG_ENDIAN) ? buffer.getShort(offset) : ByteBufUtil.swapShort(buffer.getShort(offset));
    }
    
    static int getEncryptedPacketLength(final ByteBuffer[] buffers, int offset) {
        ByteBuffer buffer = buffers[offset];
        if (buffer.remaining() >= 5) {
            return getEncryptedPacketLength(buffer);
        }
        final ByteBuffer tmp = ByteBuffer.allocate(5);
        do {
            buffer = buffers[offset++].duplicate();
            if (buffer.remaining() > tmp.remaining()) {
                buffer.limit(buffer.position() + tmp.remaining());
            }
            tmp.put(buffer);
        } while (tmp.hasRemaining());
        tmp.flip();
        return getEncryptedPacketLength(tmp);
    }
    
    private static int getEncryptedPacketLength(final ByteBuffer buffer) {
        int packetLength = 0;
        final int pos = buffer.position();
        boolean tls = false;
        switch (unsignedByte(buffer.get(pos))) {
            case 20:
            case 21:
            case 22:
            case 23:
            case 24: {
                tls = true;
                break;
            }
            default: {
                tls = false;
                break;
            }
        }
        if (tls) {
            final int majorVersion = unsignedByte(buffer.get(pos + 1));
            if (majorVersion == 3 || buffer.getShort(pos + 1) == 257) {
                packetLength = unsignedShortBE(buffer, pos + 3) + 5;
                if (packetLength <= 5) {
                    tls = false;
                }
            }
            else {
                tls = false;
            }
        }
        if (!tls) {
            final int headerLength = ((unsignedByte(buffer.get(pos)) & 0x80) != 0x0) ? 2 : 3;
            final int majorVersion2 = unsignedByte(buffer.get(pos + headerLength + 1));
            if (majorVersion2 != 2 && majorVersion2 != 3) {
                return -2;
            }
            packetLength = ((headerLength == 2) ? ((shortBE(buffer, pos) & 0x7FFF) + 2) : ((shortBE(buffer, pos) & 0x3FFF) + 3));
            if (packetLength <= headerLength) {
                return -1;
            }
        }
        return packetLength;
    }
    
    static void handleHandshakeFailure(final ChannelHandlerContext ctx, final Throwable cause, final boolean notify) {
        ctx.flush();
        if (notify) {
            ctx.fireUserEventTriggered((Object)new SslHandshakeCompletionEvent(cause));
        }
        ctx.close();
    }
    
    static void zeroout(final ByteBuf buffer) {
        if (!buffer.isReadOnly()) {
            buffer.setZero(0, buffer.capacity());
        }
    }
    
    static void zerooutAndRelease(final ByteBuf buffer) {
        zeroout(buffer);
        buffer.release();
    }
    
    static ByteBuf toBase64(final ByteBufAllocator allocator, final ByteBuf src) {
        final ByteBuf dst = Base64.encode(src, src.readerIndex(), src.readableBytes(), true, Base64Dialect.STANDARD, allocator);
        src.readerIndex(src.writerIndex());
        return dst;
    }
    
    static boolean isValidHostNameForSNI(final String hostname) {
        return hostname != null && hostname.indexOf(46) > 0 && !hostname.endsWith(".") && !NetUtil.isValidIpV4Address(hostname) && !NetUtil.isValidIpV6Address(hostname);
    }
    
    static boolean isTLSv13Cipher(final String cipher) {
        return SslUtils.TLSV13_CIPHERS.contains(cipher);
    }
    
    static boolean isEmpty(final Object[] arr) {
        return arr == null || arr.length == 0;
    }
    
    private SslUtils() {
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(SslUtils.class);
        TLSV13_CIPHERS = Collections.unmodifiableSet((Set<? extends String>)new LinkedHashSet<String>(Arrays.asList("TLS_AES_256_GCM_SHA384", "TLS_CHACHA20_POLY1305_SHA256", "TLS_AES_128_GCM_SHA256", "TLS_AES_128_CCM_8_SHA256", "TLS_AES_128_CCM_SHA256")));
        TLSV13_CIPHER_SUITES = new String[] { "TLS_AES_128_GCM_SHA256", "TLS_AES_256_GCM_SHA384" };
        TLSV1_3_JDK_SUPPORTED = isTLSv13SupportedByJDK0(null);
        TLSV1_3_JDK_DEFAULT_ENABLED = isTLSv13EnabledByJDK0(null);
        if (SslUtils.TLSV1_3_JDK_SUPPORTED) {
            DEFAULT_TLSV13_CIPHER_SUITES = SslUtils.TLSV13_CIPHER_SUITES;
        }
        else {
            DEFAULT_TLSV13_CIPHER_SUITES = EmptyArrays.EMPTY_STRINGS;
        }
        final Set<String> defaultCiphers = new LinkedHashSet<String>();
        defaultCiphers.add("TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384");
        defaultCiphers.add("TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256");
        defaultCiphers.add("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256");
        defaultCiphers.add("TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384");
        defaultCiphers.add("TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA");
        defaultCiphers.add("TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA");
        defaultCiphers.add("TLS_RSA_WITH_AES_128_GCM_SHA256");
        defaultCiphers.add("TLS_RSA_WITH_AES_128_CBC_SHA");
        defaultCiphers.add("TLS_RSA_WITH_AES_256_CBC_SHA");
        Collections.addAll(defaultCiphers, SslUtils.DEFAULT_TLSV13_CIPHER_SUITES);
        DEFAULT_CIPHER_SUITES = defaultCiphers.toArray(EmptyArrays.EMPTY_STRINGS);
    }
}
