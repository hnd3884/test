package io.netty.handler.codec.http.websocketx;

import java.security.NoSuchAlgorithmException;
import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import io.netty.buffer.Unpooled;
import java.util.Base64;
import io.netty.util.internal.PlatformDependent;
import java.security.MessageDigest;
import io.netty.util.concurrent.FastThreadLocal;

final class WebSocketUtil
{
    private static final FastThreadLocal<MessageDigest> MD5;
    private static final FastThreadLocal<MessageDigest> SHA1;
    
    static byte[] md5(final byte[] data) {
        return digest(WebSocketUtil.MD5, data);
    }
    
    static byte[] sha1(final byte[] data) {
        return digest(WebSocketUtil.SHA1, data);
    }
    
    private static byte[] digest(final FastThreadLocal<MessageDigest> digestFastThreadLocal, final byte[] data) {
        final MessageDigest digest = digestFastThreadLocal.get();
        digest.reset();
        return digest.digest(data);
    }
    
    @SuppressJava6Requirement(reason = "Guarded with java version check")
    static String base64(final byte[] data) {
        if (PlatformDependent.javaVersion() >= 8) {
            return Base64.getEncoder().encodeToString(data);
        }
        final ByteBuf encodedData = Unpooled.wrappedBuffer(data);
        String encodedString;
        try {
            final ByteBuf encoded = io.netty.handler.codec.base64.Base64.encode(encodedData);
            try {
                encodedString = encoded.toString(CharsetUtil.UTF_8);
            }
            finally {
                encoded.release();
            }
        }
        finally {
            encodedData.release();
        }
        return encodedString;
    }
    
    static byte[] randomBytes(final int size) {
        final byte[] bytes = new byte[size];
        PlatformDependent.threadLocalRandom().nextBytes(bytes);
        return bytes;
    }
    
    static int randomNumber(final int minimum, final int maximum) {
        assert minimum < maximum;
        final double fraction = PlatformDependent.threadLocalRandom().nextDouble();
        return (int)(minimum + fraction * (maximum - minimum));
    }
    
    private WebSocketUtil() {
    }
    
    static {
        MD5 = new FastThreadLocal<MessageDigest>() {
            @Override
            protected MessageDigest initialValue() throws Exception {
                try {
                    return MessageDigest.getInstance("MD5");
                }
                catch (final NoSuchAlgorithmException e) {
                    throw new InternalError("MD5 not supported on this platform - Outdated?");
                }
            }
        };
        SHA1 = new FastThreadLocal<MessageDigest>() {
            @Override
            protected MessageDigest initialValue() throws Exception {
                try {
                    return MessageDigest.getInstance("SHA1");
                }
                catch (final NoSuchAlgorithmException e) {
                    throw new InternalError("SHA-1 not supported on this platform - Outdated?");
                }
            }
        };
    }
}
