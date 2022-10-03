package sun.security.ssl;

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
    
    default Plaintext decode(final TransportContext transportContext, final ByteBuffer[] array, final int n, final int n2, final ByteBuffer[] array2, int n3, final int n4) throws IOException {
        Plaintext[] decode;
        try {
            decode = transportContext.inputRecord.decode(array, n, n2);
        }
        catch (final UnsupportedOperationException ex) {
            transportContext.outputRecord.encodeV2NoCipher();
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.finest("may be talking to SSLv2", new Object[0]);
            }
            throw transportContext.fatal(Alert.UNEXPECTED_MESSAGE, ex);
        }
        catch (final AEADBadTagException ex2) {
            throw transportContext.fatal(Alert.BAD_RECORD_MAC, ex2);
        }
        catch (final BadPaddingException ex3) {
            throw transportContext.fatal((transportContext.handshakeContext != null) ? Alert.HANDSHAKE_FAILURE : Alert.BAD_RECORD_MAC, ex3);
        }
        catch (final SSLHandshakeException ex4) {
            throw transportContext.fatal(Alert.HANDSHAKE_FAILURE, ex4);
        }
        catch (final EOFException ex5) {
            throw ex5;
        }
        catch (final InterruptedIOException ex6) {
            throw ex6;
        }
        catch (final IOException ex7) {
            throw transportContext.fatal(Alert.UNEXPECTED_MESSAGE, ex7);
        }
        if (decode == null || decode.length == 0) {
            return Plaintext.PLAINTEXT_NULL;
        }
        Plaintext plaintext_NULL = Plaintext.PLAINTEXT_NULL;
        for (Plaintext plaintext_NULL2 : decode) {
            if (plaintext_NULL2 != null && plaintext_NULL2 != Plaintext.PLAINTEXT_NULL && plaintext_NULL2.contentType != ContentType.APPLICATION_DATA.id) {
                transportContext.dispatch(plaintext_NULL2);
            }
            if (plaintext_NULL2 == null) {
                plaintext_NULL2 = Plaintext.PLAINTEXT_NULL;
            }
            else if (plaintext_NULL2.contentType == ContentType.APPLICATION_DATA.id) {
                if (!transportContext.isNegotiated) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,verbose")) {
                        SSLLogger.warning("unexpected application data before handshake completion", new Object[0]);
                    }
                    throw transportContext.fatal(Alert.UNEXPECTED_MESSAGE, "Receiving application data before handshake complete");
                }
                if (array2 != null && n4 > 0) {
                    final ByteBuffer fragment = plaintext_NULL2.fragment;
                    int remaining = fragment.remaining();
                    for (int n5 = n3 + n4, n6 = n3; n6 < n5 && remaining > 0; ++n6) {
                        final int min = Math.min(array2[n6].remaining(), remaining);
                        fragment.limit(fragment.position() + min);
                        array2[n6].put(fragment);
                        remaining -= min;
                        if (!array2[n6].hasRemaining()) {
                            ++n3;
                        }
                    }
                    if (remaining > 0) {
                        throw transportContext.fatal(Alert.INTERNAL_ERROR, "no sufficient room in the destination buffers");
                    }
                }
            }
            plaintext_NULL = plaintext_NULL2;
        }
        return plaintext_NULL;
    }
}
