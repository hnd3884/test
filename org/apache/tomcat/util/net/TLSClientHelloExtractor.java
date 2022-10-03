package org.apache.tomcat.util.net;

import org.apache.juli.logging.LogFactory;
import java.util.Locale;
import java.nio.charset.StandardCharsets;
import org.apache.tomcat.util.http.parser.HttpParser;
import java.nio.BufferUnderflowException;
import java.io.IOException;
import org.apache.tomcat.util.buf.HexUtils;
import java.util.ArrayList;
import java.nio.ByteBuffer;
import org.apache.tomcat.util.net.openssl.ciphers.Cipher;
import java.util.List;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;

public class TLSClientHelloExtractor
{
    private static final Log log;
    private static final StringManager sm;
    private final ExtractorResult result;
    private final List<Cipher> clientRequestedCiphers;
    private final List<String> clientRequestedCipherNames;
    private final String sniValue;
    private final List<String> clientRequestedApplicationProtocols;
    private final List<String> clientRequestedProtocols;
    private static final int TLS_RECORD_HEADER_LEN = 5;
    private static final int TLS_EXTENSION_SERVER_NAME = 0;
    private static final int TLS_EXTENSION_ALPN = 16;
    private static final int TLS_EXTENSION_SUPPORTED_VERSION = 43;
    public static byte[] USE_TLS_RESPONSE;
    
    public TLSClientHelloExtractor(final ByteBuffer netInBuffer) throws IOException {
        final int pos = netInBuffer.position();
        final int limit = netInBuffer.limit();
        ExtractorResult result = ExtractorResult.NOT_PRESENT;
        final List<Cipher> clientRequestedCiphers = new ArrayList<Cipher>();
        final List<String> clientRequestedCipherNames = new ArrayList<String>();
        final List<String> clientRequestedApplicationProtocols = new ArrayList<String>();
        final List<String> clientRequestedProtocols = new ArrayList<String>();
        String sniValue = null;
        try {
            netInBuffer.flip();
            if (!isAvailable(netInBuffer, 5)) {
                result = handleIncompleteRead(netInBuffer);
                return;
            }
            if (!isTLSHandshake(netInBuffer)) {
                if (isHttp(netInBuffer)) {
                    result = ExtractorResult.NON_SECURE;
                }
                return;
            }
            if (!isAllRecordAvailable(netInBuffer)) {
                result = handleIncompleteRead(netInBuffer);
                return;
            }
            if (!isClientHello(netInBuffer)) {
                return;
            }
            if (!isAllClientHelloAvailable(netInBuffer)) {
                TLSClientHelloExtractor.log.warn((Object)TLSClientHelloExtractor.sm.getString("sniExtractor.clientHelloTooBig"));
                return;
            }
            final String legacyVersion = readProtocol(netInBuffer);
            skipBytes(netInBuffer, 32);
            skipBytes(netInBuffer, netInBuffer.get() & 0xFF);
            for (int cipherCount = netInBuffer.getChar() / '\u0002', i = 0; i < cipherCount; ++i) {
                final char cipherId = netInBuffer.getChar();
                final Cipher c = Cipher.valueOf(cipherId);
                if (c == null) {
                    clientRequestedCipherNames.add("Unknown(0x" + HexUtils.toHexString(cipherId) + ")");
                }
                else {
                    clientRequestedCiphers.add(c);
                    clientRequestedCipherNames.add(c.name());
                }
            }
            skipBytes(netInBuffer, netInBuffer.get() & 0xFF);
            if (!netInBuffer.hasRemaining()) {
                return;
            }
            skipBytes(netInBuffer, 2);
            while (netInBuffer.hasRemaining() && (sniValue == null || clientRequestedApplicationProtocols.isEmpty() || clientRequestedProtocols.isEmpty())) {
                final char extensionType = netInBuffer.getChar();
                final char extensionDataSize = netInBuffer.getChar();
                switch (extensionType) {
                    case '\0': {
                        sniValue = readSniExtension(netInBuffer);
                        continue;
                    }
                    case '\u0010': {
                        readAlpnExtension(netInBuffer, clientRequestedApplicationProtocols);
                        continue;
                    }
                    case '+': {
                        readSupportedVersions(netInBuffer, clientRequestedProtocols);
                        continue;
                    }
                    default: {
                        skipBytes(netInBuffer, extensionDataSize);
                        continue;
                    }
                }
            }
            if (clientRequestedProtocols.isEmpty()) {
                clientRequestedProtocols.add(legacyVersion);
            }
            result = ExtractorResult.COMPLETE;
        }
        catch (final BufferUnderflowException | IllegalArgumentException e) {
            throw new IOException(TLSClientHelloExtractor.sm.getString("sniExtractor.clientHelloInvalid"), e);
        }
        finally {
            this.result = result;
            this.clientRequestedCiphers = clientRequestedCiphers;
            this.clientRequestedCipherNames = clientRequestedCipherNames;
            this.clientRequestedApplicationProtocols = clientRequestedApplicationProtocols;
            this.sniValue = sniValue;
            this.clientRequestedProtocols = clientRequestedProtocols;
            netInBuffer.limit(limit);
            netInBuffer.position(pos);
        }
    }
    
    public ExtractorResult getResult() {
        return this.result;
    }
    
    public String getSNIValue() {
        if (this.result == ExtractorResult.COMPLETE) {
            return this.sniValue;
        }
        throw new IllegalStateException(TLSClientHelloExtractor.sm.getString("sniExtractor.tooEarly"));
    }
    
    public List<Cipher> getClientRequestedCiphers() {
        if (this.result == ExtractorResult.COMPLETE || this.result == ExtractorResult.NOT_PRESENT) {
            return this.clientRequestedCiphers;
        }
        throw new IllegalStateException(TLSClientHelloExtractor.sm.getString("sniExtractor.tooEarly"));
    }
    
    public List<String> getClientRequestedCipherNames() {
        if (this.result == ExtractorResult.COMPLETE || this.result == ExtractorResult.NOT_PRESENT) {
            return this.clientRequestedCipherNames;
        }
        throw new IllegalStateException(TLSClientHelloExtractor.sm.getString("sniExtractor.tooEarly"));
    }
    
    public List<String> getClientRequestedApplicationProtocols() {
        if (this.result == ExtractorResult.COMPLETE || this.result == ExtractorResult.NOT_PRESENT) {
            return this.clientRequestedApplicationProtocols;
        }
        throw new IllegalStateException(TLSClientHelloExtractor.sm.getString("sniExtractor.tooEarly"));
    }
    
    public List<String> getClientRequestedProtocols() {
        if (this.result == ExtractorResult.COMPLETE || this.result == ExtractorResult.NOT_PRESENT) {
            return this.clientRequestedProtocols;
        }
        throw new IllegalStateException(TLSClientHelloExtractor.sm.getString("sniExtractor.tooEarly"));
    }
    
    private static ExtractorResult handleIncompleteRead(final ByteBuffer bb) {
        if (bb.limit() == bb.capacity()) {
            return ExtractorResult.UNDERFLOW;
        }
        return ExtractorResult.NEED_READ;
    }
    
    private static boolean isAvailable(final ByteBuffer bb, final int size) {
        if (bb.remaining() < size) {
            bb.position(bb.limit());
            return false;
        }
        return true;
    }
    
    private static boolean isTLSHandshake(final ByteBuffer bb) {
        if (bb.get() != 22) {
            return false;
        }
        final byte b2 = bb.get();
        final byte b3 = bb.get();
        return b2 >= 3 && (b2 != 3 || b3 != 0);
    }
    
    private static boolean isHttp(final ByteBuffer bb) {
        byte chr = 0;
        bb.position(0);
        while (bb.hasRemaining()) {
            chr = bb.get();
            if (chr != 13 && chr != 10) {
                while (HttpParser.isToken(chr) && bb.hasRemaining()) {
                    chr = bb.get();
                    if (chr == 32 || chr == 9) {
                        while (chr == 32 || chr == 9) {
                            if (!bb.hasRemaining()) {
                                return false;
                            }
                            chr = bb.get();
                        }
                        while (chr != 32 && chr != 9) {
                            if (HttpParser.isNotRequestTarget(chr) || !bb.hasRemaining()) {
                                return false;
                            }
                            chr = bb.get();
                        }
                        while (chr == 32 || chr == 9) {
                            if (!bb.hasRemaining()) {
                                return false;
                            }
                            chr = bb.get();
                        }
                        while (HttpParser.isHttpProtocol(chr) && bb.hasRemaining()) {
                            chr = bb.get();
                            if (chr == 13 || chr == 10) {
                                return true;
                            }
                        }
                        return false;
                    }
                }
                return false;
            }
        }
        return false;
    }
    
    private static boolean isAllRecordAvailable(final ByteBuffer bb) {
        final int size = bb.getChar();
        return isAvailable(bb, size);
    }
    
    private static boolean isClientHello(final ByteBuffer bb) {
        return bb.get() == 1;
    }
    
    private static boolean isAllClientHelloAvailable(final ByteBuffer bb) {
        final int size = ((bb.get() & 0xFF) << 16) + ((bb.get() & 0xFF) << 8) + (bb.get() & 0xFF);
        return isAvailable(bb, size);
    }
    
    private static void skipBytes(final ByteBuffer bb, final int size) {
        bb.position(bb.position() + size);
    }
    
    private static String readProtocol(final ByteBuffer bb) {
        final char protocol = bb.getChar();
        switch (protocol) {
            case '\u0300': {
                return "SSLv3";
            }
            case '\u0301': {
                return "TLSv1.0";
            }
            case '\u0302': {
                return "TLSv1.1";
            }
            case '\u0303': {
                return "TLSv1.2";
            }
            case '\u0304': {
                return "TLSv1.3";
            }
            default: {
                return "Unknown(0x" + HexUtils.toHexString(protocol) + ")";
            }
        }
    }
    
    private static String readSniExtension(final ByteBuffer bb) {
        skipBytes(bb, 3);
        final char serverNameSize = bb.getChar();
        final byte[] serverNameBytes = new byte[serverNameSize];
        bb.get(serverNameBytes);
        return new String(serverNameBytes, StandardCharsets.UTF_8).toLowerCase(Locale.ENGLISH);
    }
    
    private static void readAlpnExtension(final ByteBuffer bb, final List<String> protocolNames) {
        char toRead = bb.getChar();
        final byte[] inputBuffer = new byte[255];
        while (toRead > '\0') {
            final int len = bb.get() & 0xFF;
            bb.get(inputBuffer, 0, len);
            protocolNames.add(new String(inputBuffer, 0, len, StandardCharsets.UTF_8));
            --toRead;
            toRead -= (char)len;
        }
    }
    
    private static void readSupportedVersions(final ByteBuffer bb, final List<String> protocolNames) {
        for (int count = (bb.get() & 0xFF) / 2, i = 0; i < count; ++i) {
            protocolNames.add(readProtocol(bb));
        }
    }
    
    static {
        log = LogFactory.getLog((Class)TLSClientHelloExtractor.class);
        sm = StringManager.getManager((Class)TLSClientHelloExtractor.class);
        TLSClientHelloExtractor.USE_TLS_RESPONSE = "HTTP/1.1 400 \r\nContent-Type: text/plain;charset=UTF-8\r\nConnection: close\r\n\r\nBad Request\r\nThis combination of host and port requires TLS.\r\n".getBytes(StandardCharsets.UTF_8);
    }
    
    public enum ExtractorResult
    {
        COMPLETE, 
        NOT_PRESENT, 
        UNDERFLOW, 
        NEED_READ, 
        NON_SECURE;
    }
}
