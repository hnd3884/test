package org.tukaani.xz.rangecoder;

import java.io.IOException;
import org.tukaani.xz.CorruptedInputException;
import java.io.InputStream;
import java.io.DataInputStream;

public final class RangeDecoderFromStream extends RangeDecoder
{
    private final DataInputStream inData;
    
    public RangeDecoderFromStream(final InputStream inputStream) throws IOException {
        this.inData = new DataInputStream(inputStream);
        if (this.inData.readUnsignedByte() != 0) {
            throw new CorruptedInputException();
        }
        this.code = this.inData.readInt();
        this.range = -1;
    }
    
    public boolean isFinished() {
        return this.code == 0;
    }
    
    @Override
    public void normalize() throws IOException {
        if ((this.range & 0xFF000000) == 0x0) {
            this.code = (this.code << 8 | this.inData.readUnsignedByte());
            this.range <<= 8;
        }
    }
}
