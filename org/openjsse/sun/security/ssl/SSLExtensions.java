package org.openjsse.sun.security.ssl;

import org.openjsse.sun.security.util.HexDumpEncoder;
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
    
    SSLExtensions(final SSLHandshake.HandshakeMessage hm, final ByteBuffer m, final SSLExtension[] extensions) throws IOException {
        this.extMap = new LinkedHashMap<SSLExtension, byte[]>();
        this.logMap = (SSLLogger.isOn ? new LinkedHashMap<Integer, byte[]>() : null);
        this.handshakeMessage = hm;
        int len = Record.getInt16(m);
        this.encodedLength = len + 2;
        while (len > 0) {
            final int extId = Record.getInt16(m);
            final int extLen = Record.getInt16(m);
            if (extLen > m.remaining()) {
                throw hm.handshakeContext.conContext.fatal(Alert.ILLEGAL_PARAMETER, "Error parsing extension (" + extId + "): no sufficient data");
            }
            boolean isSupported = true;
            final SSLHandshake handshakeType = hm.handshakeType();
            if (SSLExtension.isConsumable(extId) && SSLExtension.valueOf(handshakeType, extId) == null) {
                if (extId == SSLExtension.CH_SUPPORTED_GROUPS.id && handshakeType == SSLHandshake.SERVER_HELLO) {
                    isSupported = false;
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.warning("Received buggy supported_groups extension in the ServerHello handshake message", new Object[0]);
                    }
                }
                else {
                    if (handshakeType == SSLHandshake.SERVER_HELLO) {
                        throw hm.handshakeContext.conContext.fatal(Alert.UNSUPPORTED_EXTENSION, "extension (" + extId + ") should not be presented in " + handshakeType.name);
                    }
                    isSupported = false;
                }
            }
            if (isSupported) {
                isSupported = false;
                final int length = extensions.length;
                int i = 0;
                while (i < length) {
                    final SSLExtension extension = extensions[i];
                    if (extension.id == extId && extension.onLoadConsumer != null) {
                        if (extension.handshakeType != handshakeType) {
                            throw hm.handshakeContext.conContext.fatal(Alert.UNSUPPORTED_EXTENSION, "extension (" + extId + ") should not be presented in " + handshakeType.name);
                        }
                        final byte[] extData = new byte[extLen];
                        m.get(extData);
                        this.extMap.put(extension, extData);
                        if (this.logMap != null) {
                            this.logMap.put(extId, extData);
                        }
                        isSupported = true;
                        break;
                    }
                    else {
                        ++i;
                    }
                }
            }
            if (!isSupported) {
                if (this.logMap != null) {
                    final byte[] extData2 = new byte[extLen];
                    m.get(extData2);
                    this.logMap.put(extId, extData2);
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.fine("Ignore unknown or unsupported extension", toString(extId, extData2));
                    }
                }
                else {
                    final int pos = m.position() + extLen;
                    m.position(pos);
                }
            }
            len -= extLen + 4;
        }
    }
    
    byte[] get(final SSLExtension ext) {
        return this.extMap.get(ext);
    }
    
    void consumeOnLoad(final HandshakeContext context, final SSLExtension[] extensions) throws IOException {
        for (final SSLExtension extension : extensions) {
            if (context.negotiatedProtocol != null && !extension.isAvailable(context.negotiatedProtocol)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unsupported extension: " + extension.name, new Object[0]);
                }
            }
            else if (!this.extMap.containsKey(extension)) {
                if (extension.onLoadAbsence != null) {
                    extension.absentOnLoad(context, this.handshakeMessage);
                }
                else if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable extension: " + extension.name, new Object[0]);
                }
            }
            else if (extension.onLoadConsumer == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("Ignore unsupported extension: " + extension.name, new Object[0]);
                }
            }
            else {
                final ByteBuffer m = ByteBuffer.wrap(this.extMap.get(extension));
                extension.consumeOnLoad(context, this.handshakeMessage, m);
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Consumed extension: " + extension.name, new Object[0]);
                }
            }
        }
    }
    
    void consumeOnTrade(final HandshakeContext context, final SSLExtension[] extensions) throws IOException {
        for (final SSLExtension extension : extensions) {
            if (!this.extMap.containsKey(extension)) {
                if (extension.onTradeAbsence != null) {
                    extension.absentOnTrade(context, this.handshakeMessage);
                }
                else if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore unavailable extension: " + extension.name, new Object[0]);
                }
            }
            else if (extension.onTradeConsumer == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("Ignore impact of unsupported extension: " + extension.name, new Object[0]);
                }
            }
            else {
                extension.consumeOnTrade(context, this.handshakeMessage);
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Populated with extension: " + extension.name, new Object[0]);
                }
            }
        }
    }
    
    void produce(final HandshakeContext context, final SSLExtension[] extensions) throws IOException {
        for (final SSLExtension extension : extensions) {
            if (this.extMap.containsKey(extension)) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore, duplicated extension: " + extension.name, new Object[0]);
                }
            }
            else if (extension.networkProducer == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("Ignore, no extension producer defined: " + extension.name, new Object[0]);
                }
            }
            else {
                final byte[] encoded = extension.produce(context, this.handshakeMessage);
                if (encoded != null) {
                    this.extMap.put(extension, encoded);
                    this.encodedLength += encoded.length + 4;
                }
                else if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore, context unavailable extension: " + extension.name, new Object[0]);
                }
            }
        }
    }
    
    void reproduce(final HandshakeContext context, final SSLExtension[] extensions) throws IOException {
        for (final SSLExtension extension : extensions) {
            if (extension.networkProducer == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.warning("Ignore, no extension producer defined: " + extension.name, new Object[0]);
                }
            }
            else {
                final byte[] encoded = extension.produce(context, this.handshakeMessage);
                if (encoded != null) {
                    if (this.extMap.containsKey(extension)) {
                        final byte[] old = this.extMap.replace(extension, encoded);
                        if (old != null) {
                            this.encodedLength -= old.length + 4;
                        }
                        this.encodedLength += encoded.length + 4;
                    }
                    else {
                        this.extMap.put(extension, encoded);
                        this.encodedLength += encoded.length + 4;
                    }
                }
                else if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                    SSLLogger.fine("Ignore, context unavailable extension: " + extension.name, new Object[0]);
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
    
    void send(final HandshakeOutStream hos) throws IOException {
        final int extsLen = this.length();
        if (extsLen == 0) {
            return;
        }
        hos.putInt16(extsLen - 2);
        for (final SSLExtension ext : SSLExtension.values()) {
            final byte[] extData = this.extMap.get(ext);
            if (extData != null) {
                hos.putInt16(ext.id);
                hos.putBytes16(extData);
            }
        }
    }
    
    @Override
    public String toString() {
        if (this.extMap.isEmpty() && (this.logMap == null || this.logMap.isEmpty())) {
            return "<no extension>";
        }
        final StringBuilder builder = new StringBuilder(512);
        if (this.logMap != null && !this.logMap.isEmpty()) {
            for (final Map.Entry<Integer, byte[]> en : this.logMap.entrySet()) {
                final SSLExtension ext = SSLExtension.valueOf(this.handshakeMessage.handshakeType(), en.getKey());
                if (builder.length() != 0) {
                    builder.append(",\n");
                }
                if (ext != null) {
                    builder.append(ext.toString(ByteBuffer.wrap(en.getValue())));
                }
                else {
                    builder.append(toString(en.getKey(), en.getValue()));
                }
            }
            return builder.toString();
        }
        for (final Map.Entry<SSLExtension, byte[]> en2 : this.extMap.entrySet()) {
            if (builder.length() != 0) {
                builder.append(",\n");
            }
            builder.append(en2.getKey().toString(ByteBuffer.wrap(en2.getValue())));
        }
        return builder.toString();
    }
    
    private static String toString(final int extId, final byte[] extData) {
        final String extName = SSLExtension.nameOf(extId);
        final MessageFormat messageFormat = new MessageFormat("\"{0} ({1})\": '{'\n{2}\n'}'", Locale.ENGLISH);
        final HexDumpEncoder hexEncoder = new HexDumpEncoder();
        final String encoded = hexEncoder.encodeBuffer(extData);
        final Object[] messageFields = { extName, extId, Utilities.indent(encoded) };
        return messageFormat.format(messageFields);
    }
}
