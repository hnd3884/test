package org.msgpack.unpacker;

import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import org.msgpack.MessageTypeException;
import java.nio.ByteBuffer;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

final class StringAccept extends Accept
{
    String value;
    private CharsetDecoder decoder;
    
    public StringAccept() {
        this.decoder = Charset.forName("UTF-8").newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
    }
    
    @Override
    void acceptRaw(final byte[] raw) {
        try {
            this.value = this.decoder.decode(ByteBuffer.wrap(raw)).toString();
        }
        catch (final CharacterCodingException ex) {
            throw new MessageTypeException(ex);
        }
    }
    
    @Override
    void acceptEmptyRaw() {
        this.value = "";
    }
    
    @Override
    public void refer(final ByteBuffer bb, final boolean gift) throws IOException {
        try {
            this.value = this.decoder.decode(bb).toString();
        }
        catch (final CharacterCodingException ex) {
            throw new MessageTypeException(ex);
        }
    }
}
