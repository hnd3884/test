package org.openjsse.sun.security.ssl;

import java.io.InterruptedIOException;
import java.io.EOFException;
import javax.net.ssl.SSLHandshakeException;
import javax.crypto.BadPaddingException;
import javax.crypto.AEADBadTagException;
import java.nio.ByteBuffer;
import java.io.IOException;

interface SSLTransport
{
    String getPeerHost();
    
    int getPeerPort();
    
    default void shutdown() throws IOException {
    }
    
    boolean useDelegatedTask();
    
    default Plaintext decode(final TransportContext context, final ByteBuffer[] srcs, final int srcsOffset, final int srcsLength, final ByteBuffer[] dsts, int dstsOffset, final int dstsLength) throws IOException {
        Plaintext[] plaintexts = null;
        try {
            plaintexts = context.inputRecord.decode(srcs, srcsOffset, srcsLength);
        }
        catch (final UnsupportedOperationException unsoe) {
            if (!context.sslContext.isDTLS()) {
                context.outputRecord.encodeV2NoCipher();
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.finest("may be talking to SSLv2", new Object[0]);
                }
            }
            throw context.fatal(Alert.UNEXPECTED_MESSAGE, unsoe);
        }
        catch (final AEADBadTagException bte) {
            throw context.fatal(Alert.BAD_RECORD_MAC, bte);
        }
        catch (final BadPaddingException bpe) {
            final Alert alert = (context.handshakeContext != null) ? Alert.HANDSHAKE_FAILURE : Alert.BAD_RECORD_MAC;
            throw context.fatal(alert, bpe);
        }
        catch (final SSLHandshakeException she) {
            throw context.fatal(Alert.HANDSHAKE_FAILURE, she);
        }
        catch (final EOFException eofe) {
            throw eofe;
        }
        catch (final InterruptedIOException iioe) {
            throw iioe;
        }
        catch (final IOException ioe) {
            throw context.fatal(Alert.UNEXPECTED_MESSAGE, ioe);
        }
        if (plaintexts == null || plaintexts.length == 0) {
            return Plaintext.PLAINTEXT_NULL;
        }
        Plaintext finalPlaintext = Plaintext.PLAINTEXT_NULL;
        for (Plaintext plainText : plaintexts) {
            if (plainText == Plaintext.PLAINTEXT_NULL) {
                if (context.handshakeContext != null && context.handshakeContext.sslConfig.enableRetransmissions && context.sslContext.isDTLS()) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,verbose")) {
                        SSLLogger.finest("retransmited handshake flight", new Object[0]);
                    }
                    context.outputRecord.launchRetransmission();
                }
            }
            else if (plainText != null && plainText.contentType != ContentType.APPLICATION_DATA.id) {
                context.dispatch(plainText);
            }
            if (plainText == null) {
                plainText = Plaintext.PLAINTEXT_NULL;
            }
            else if (plainText.contentType == ContentType.APPLICATION_DATA.id) {
                if (!context.isNegotiated) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,verbose")) {
                        SSLLogger.warning("unexpected application data before handshake completion", new Object[0]);
                    }
                    throw context.fatal(Alert.UNEXPECTED_MESSAGE, "Receiving application data before handshake complete");
                }
                if (dsts != null && dstsLength > 0) {
                    final ByteBuffer fragment = plainText.fragment;
                    int remains = fragment.remaining();
                    for (int limit = dstsOffset + dstsLength, i = dstsOffset; i < limit && remains > 0; ++i) {
                        final int amount = Math.min(dsts[i].remaining(), remains);
                        fragment.limit(fragment.position() + amount);
                        dsts[i].put(fragment);
                        remains -= amount;
                        if (!dsts[i].hasRemaining()) {
                            ++dstsOffset;
                        }
                    }
                    if (remains > 0) {
                        throw context.fatal(Alert.INTERNAL_ERROR, "no sufficient room in the destination buffers");
                    }
                }
            }
            finalPlaintext = plainText;
        }
        return finalPlaintext;
    }
}
