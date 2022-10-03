package sun.nio.cs;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

class UTF_32Coder
{
    protected static final int BOM_BIG = 65279;
    protected static final int BOM_LITTLE = -131072;
    protected static final int NONE = 0;
    protected static final int BIG = 1;
    protected static final int LITTLE = 2;
    
    protected static class Decoder extends CharsetDecoder
    {
        private int currentBO;
        private int expectedBO;
        
        protected Decoder(final Charset charset, final int expectedBO) {
            super(charset, 0.25f, 1.0f);
            this.expectedBO = expectedBO;
            this.currentBO = 0;
        }
        
        private int getCP(final ByteBuffer byteBuffer) {
            return (this.currentBO == 1) ? ((byteBuffer.get() & 0xFF) << 24 | (byteBuffer.get() & 0xFF) << 16 | (byteBuffer.get() & 0xFF) << 8 | (byteBuffer.get() & 0xFF)) : ((byteBuffer.get() & 0xFF) | (byteBuffer.get() & 0xFF) << 8 | (byteBuffer.get() & 0xFF) << 16 | (byteBuffer.get() & 0xFF) << 24);
        }
        
        @Override
        protected CoderResult decodeLoop(final ByteBuffer byteBuffer, final CharBuffer charBuffer) {
            if (byteBuffer.remaining() < 4) {
                return CoderResult.UNDERFLOW;
            }
            int position = byteBuffer.position();
            try {
                if (this.currentBO == 0) {
                    final int n = (byteBuffer.get() & 0xFF) << 24 | (byteBuffer.get() & 0xFF) << 16 | (byteBuffer.get() & 0xFF) << 8 | (byteBuffer.get() & 0xFF);
                    if (n == 65279 && this.expectedBO != 2) {
                        this.currentBO = 1;
                        position += 4;
                    }
                    else if (n == -131072 && this.expectedBO != 1) {
                        this.currentBO = 2;
                        position += 4;
                    }
                    else {
                        if (this.expectedBO == 0) {
                            this.currentBO = 1;
                        }
                        else {
                            this.currentBO = this.expectedBO;
                        }
                        byteBuffer.position(position);
                    }
                }
                while (byteBuffer.remaining() >= 4) {
                    final int cp = this.getCP(byteBuffer);
                    if (Character.isBmpCodePoint(cp)) {
                        if (!charBuffer.hasRemaining()) {
                            return CoderResult.OVERFLOW;
                        }
                        position += 4;
                        charBuffer.put((char)cp);
                    }
                    else {
                        if (!Character.isValidCodePoint(cp)) {
                            return CoderResult.malformedForLength(4);
                        }
                        if (charBuffer.remaining() < 2) {
                            return CoderResult.OVERFLOW;
                        }
                        position += 4;
                        charBuffer.put(Character.highSurrogate(cp));
                        charBuffer.put(Character.lowSurrogate(cp));
                    }
                }
                return CoderResult.UNDERFLOW;
            }
            finally {
                byteBuffer.position(position);
            }
        }
        
        @Override
        protected void implReset() {
            this.currentBO = 0;
        }
    }
    
    protected static class Encoder extends CharsetEncoder
    {
        private boolean doBOM;
        private boolean doneBOM;
        private int byteOrder;
        
        protected void put(final int n, final ByteBuffer byteBuffer) {
            if (this.byteOrder == 1) {
                byteBuffer.put((byte)(n >> 24));
                byteBuffer.put((byte)(n >> 16));
                byteBuffer.put((byte)(n >> 8));
                byteBuffer.put((byte)n);
            }
            else {
                byteBuffer.put((byte)n);
                byteBuffer.put((byte)(n >> 8));
                byteBuffer.put((byte)(n >> 16));
                byteBuffer.put((byte)(n >> 24));
            }
        }
        
        protected Encoder(final Charset charset, final int byteOrder, final boolean doBOM) {
            super(charset, 4.0f, doBOM ? 8.0f : 4.0f, (byteOrder == 1) ? new byte[] { 0, 0, -1, -3 } : new byte[] { -3, -1, 0, 0 });
            this.doBOM = false;
            this.doneBOM = true;
            this.byteOrder = byteOrder;
            this.doBOM = doBOM;
            this.doneBOM = !doBOM;
        }
        
        @Override
        protected CoderResult encodeLoop(final CharBuffer charBuffer, final ByteBuffer byteBuffer) {
            int position = charBuffer.position();
            if (!this.doneBOM && charBuffer.hasRemaining()) {
                if (byteBuffer.remaining() < 4) {
                    return CoderResult.OVERFLOW;
                }
                this.put(65279, byteBuffer);
                this.doneBOM = true;
            }
            try {
                while (charBuffer.hasRemaining()) {
                    final char value = charBuffer.get();
                    if (!Character.isSurrogate(value)) {
                        if (byteBuffer.remaining() < 4) {
                            return CoderResult.OVERFLOW;
                        }
                        ++position;
                        this.put(value, byteBuffer);
                    }
                    else {
                        if (!Character.isHighSurrogate(value)) {
                            return CoderResult.malformedForLength(1);
                        }
                        if (!charBuffer.hasRemaining()) {
                            return CoderResult.UNDERFLOW;
                        }
                        final char value2 = charBuffer.get();
                        if (!Character.isLowSurrogate(value2)) {
                            return CoderResult.malformedForLength(1);
                        }
                        if (byteBuffer.remaining() < 4) {
                            return CoderResult.OVERFLOW;
                        }
                        position += 2;
                        this.put(Character.toCodePoint(value, value2), byteBuffer);
                    }
                }
                return CoderResult.UNDERFLOW;
            }
            finally {
                charBuffer.position(position);
            }
        }
        
        @Override
        protected void implReset() {
            this.doneBOM = !this.doBOM;
        }
    }
}
