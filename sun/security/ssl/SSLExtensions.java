package sun.security.ssl;

import sun.misc.HexDumpEncoder;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Iterator;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;

final class SSLExtensions
{
    private final SSLHandshake.HandshakeMessage handshakeMessage;
    private Map<SSLExtension, byte[]> extMap;
    private int encodedLength;
    private final Map<Integer, byte[]> logMap;
    
    SSLExtensions(final SSLHandshake.HandshakeMessage handshakeMessage) {
        this.extMap = new LinkedHashMap<SSLExtension, byte[]>();
        this.logMap = (SSLLogger.isOn ? new LinkedHashMap<Integer, byte[]>() : null);
        this.handshakeMessage = handshakeMessage;
        this.encodedLength = 2;
    }
    
    SSLExtensions(final SSLHandshake.HandshakeMessage handshakeMessage, final ByteBuffer byteBuffer, final SSLExtension[] array) throws IOException {
        this.extMap = new LinkedHashMap<SSLExtension, byte[]>();
        this.logMap = (SSLLogger.isOn ? new LinkedHashMap<Integer, byte[]>() : null);
        this.handshakeMessage = handshakeMessage;
        int i = Record.getInt16(byteBuffer);
        this.encodedLength = i + 2;
        while (i > 0) {
            final int int16 = Record.getInt16(byteBuffer);
            final int int17 = Record.getInt16(byteBuffer);
            if (int17 > byteBuffer.remaining()) {
                throw handshakeMessage.handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Error parsing extension (" + int16 + "): no sufficient data");
            }
            int n = 1;
            final SSLHandshake handshakeType = handshakeMessage.handshakeType();
            if (SSLExtension.isConsumable(int16) && SSLExtension.valueOf(handshakeType, int16) == null) {
                if (int16 == SSLExtension.CH_SUPPORTED_GROUPS.id && handshakeType == SSLHandshake.SERVER_HELLO) {
                    n = 0;
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.warning("Received buggy supported_groups extension in the ServerHello handshake message", new Object[0]);
                    }
                }
                else {
                    if (handshakeType == SSLHandshake.SERVER_HELLO) {
                        throw handshakeMessage.handshakeContext.conContext.fatal(Alert.UNSUPPORTED_EXTENSION, "extension (" + int16 + ") should not be presented in " + handshakeType.name);
                    }
                    n = 0;
                }
            }
            if (n != 0) {
                n = 0;
                final int length = array.length;
                int j = 0;
                while (j < length) {
                    final SSLExtension sslExtension = array[j];
                    if (sslExtension.id == int16 && sslExtension.onLoadConsumer != null) {
                        if (sslExtension.handshakeType != handshakeType) {
                            throw handshakeMessage.handshakeContext.conContext.fatal(Alert.UNSUPPORTED_EXTENSION, "extension (" + int16 + ") should not be presented in " + handshakeType.name);
                        }
                        final byte[] array2 = new byte[int17];
                        byteBuffer.get(array2);
                        this.extMap.put(sslExtension, array2);
                        if (this.logMap != null) {
                            this.logMap.put(int16, array2);
                        }
                        n = 1;
                        break;
                    }
                    else {
                        ++j;
                    }
                }
            }
            if (n == 0) {
                if (this.logMap != null) {
                    final byte[] array3 = new byte[int17];
                    byteBuffer.get(array3);
                    this.logMap.put(int16, array3);
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.fine("Ignore unknown or unsupported extension", toString(int16, array3));
                    }
                }
                else {
                    byteBuffer.position(byteBuffer.position() + int17);
                }
            }
            i -= int17 + 4;
        }
    }
    
    byte[] get(final SSLExtension sslExtension) {
        return this.extMap.get(sslExtension);
    }
    
    void consumeOnLoad(final HandshakeContext handshakeContext, final SSLExtension[] array) throws IOException {
        for (final SSLExtension sslExtension : array) {
            if (handshakeContext.negotiatedProtocol != null && !sslExtension.isAvailable(handshakeContext.negotiatedProtocol)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unsupported extension: " + sslExtension.name, new Object[0]);
                }
            }
            else if (!this.extMap.containsKey(sslExtension)) {
                if (sslExtension.onLoadAbsence != null) {
                    sslExtension.absentOnLoad(handshakeContext, this.handshakeMessage);
                }
                else if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable extension: " + sslExtension.name, new Object[0]);
                }
            }
            else if (sslExtension.onLoadConsumer == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("Ignore unsupported extension: " + sslExtension.name, new Object[0]);
                }
            }
            else {
                sslExtension.consumeOnLoad(handshakeContext, this.handshakeMessage, ByteBuffer.wrap(this.extMap.get(sslExtension)));
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Consumed extension: " + sslExtension.name, new Object[0]);
                }
            }
        }
    }
    
    void consumeOnTrade(final HandshakeContext handshakeContext, final SSLExtension[] array) throws IOException {
        for (final SSLExtension sslExtension : array) {
            if (!this.extMap.containsKey(sslExtension)) {
                if (sslExtension.onTradeAbsence != null) {
                    sslExtension.absentOnTrade(handshakeContext, this.handshakeMessage);
                }
                else if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable extension: " + sslExtension.name, new Object[0]);
                }
            }
            else if (sslExtension.onTradeConsumer == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("Ignore impact of unsupported extension: " + sslExtension.name, new Object[0]);
                }
            }
            else {
                sslExtension.consumeOnTrade(handshakeContext, this.handshakeMessage);
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Populated with extension: " + sslExtension.name, new Object[0]);
                }
            }
        }
    }
    
    void produce(final HandshakeContext handshakeContext, final SSLExtension[] array) throws IOException {
        for (final SSLExtension sslExtension : array) {
            if (this.extMap.containsKey(sslExtension)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore, duplicated extension: " + sslExtension.name, new Object[0]);
                }
            }
            else if (sslExtension.networkProducer == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("Ignore, no extension producer defined: " + sslExtension.name, new Object[0]);
                }
            }
            else {
                final byte[] produce = sslExtension.produce(handshakeContext, this.handshakeMessage);
                if (produce != null) {
                    this.extMap.put(sslExtension, produce);
                    this.encodedLength += produce.length + 4;
                }
                else if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore, context unavailable extension: " + sslExtension.name, new Object[0]);
                }
            }
        }
    }
    
    void reproduce(final HandshakeContext handshakeContext, final SSLExtension[] array) throws IOException {
        for (final SSLExtension sslExtension : array) {
            if (sslExtension.networkProducer == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("Ignore, no extension producer defined: " + sslExtension.name, new Object[0]);
                }
            }
            else {
                final byte[] produce = sslExtension.produce(handshakeContext, this.handshakeMessage);
                if (produce != null) {
                    if (this.extMap.containsKey(sslExtension)) {
                        final byte[] array2 = this.extMap.replace(sslExtension, produce);
                        if (array2 != null) {
                            this.encodedLength -= array2.length + 4;
                        }
                        this.encodedLength += produce.length + 4;
                    }
                    else {
                        this.extMap.put(sslExtension, produce);
                        this.encodedLength += produce.length + 4;
                    }
                }
                else if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore, context unavailable extension: " + sslExtension.name, new Object[0]);
                }
            }
        }
    }
    
    int length() {
        if (this.extMap.isEmpty()) {
            return 0;
        }
        return this.encodedLength;
    }
    
    void send(final HandshakeOutStream handshakeOutStream) throws IOException {
        final int length = this.length();
        if (length == 0) {
            return;
        }
        handshakeOutStream.putInt16(length - 2);
        for (final SSLExtension sslExtension : SSLExtension.values()) {
            final byte[] array = this.extMap.get(sslExtension);
            if (array != null) {
                handshakeOutStream.putInt16(sslExtension.id);
                handshakeOutStream.putBytes16(array);
            }
        }
    }
    
    @Override
    public String toString() {
        if (this.extMap.isEmpty() && (this.logMap == null || this.logMap.isEmpty())) {
            return "<no extension>";
        }
        final StringBuilder sb = new StringBuilder(512);
        if (this.logMap != null && !this.logMap.isEmpty()) {
            for (final Map.Entry entry : this.logMap.entrySet()) {
                final SSLExtension value = SSLExtension.valueOf(this.handshakeMessage.handshakeType(), (int)entry.getKey());
                if (sb.length() != 0) {
                    sb.append(",\n");
                }
                if (value != null) {
                    sb.append(value.toString(ByteBuffer.wrap((byte[])entry.getValue())));
                }
                else {
                    sb.append(toString((int)entry.getKey(), (byte[])entry.getValue()));
                }
            }
            return sb.toString();
        }
        for (final Map.Entry entry2 : this.extMap.entrySet()) {
            if (sb.length() != 0) {
                sb.append(",\n");
            }
            sb.append(((SSLExtension)entry2.getKey()).toString(ByteBuffer.wrap((byte[])entry2.getValue())));
        }
        return sb.toString();
    }
    
    private static String toString(final int n, final byte[] array) {
        return new MessageFormat("\"{0} ({1})\": '{'\n{2}\n'}'", Locale.ENGLISH).format(new Object[] { SSLExtension.nameOf(n), n, Utilities.indent(new HexDumpEncoder().encodeBuffer(array)) });
    }
}
