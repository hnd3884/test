package sun.awt;

import java.nio.charset.CodingErrorAction;
import java.nio.charset.CoderResult;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.Charset;

public class AWTCharset extends Charset
{
    protected Charset awtCs;
    protected Charset javaCs;
    
    public AWTCharset(final String s, final Charset javaCs) {
        super(s, null);
        this.javaCs = javaCs;
        this.awtCs = this;
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return this.javaCs != null && this.javaCs.contains(charset);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        if (this.javaCs == null) {
            throw new Error("Encoder is not supported by this Charset");
        }
        return new Encoder(this.javaCs.newEncoder());
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        if (this.javaCs == null) {
            throw new Error("Decoder is not supported by this Charset");
        }
        return new Decoder(this.javaCs.newDecoder());
    }
    
    public class Encoder extends CharsetEncoder
    {
        protected CharsetEncoder enc;
        
        protected Encoder(final AWTCharset awtCharset) {
            this(awtCharset, awtCharset.javaCs.newEncoder());
        }
        
        protected Encoder(final CharsetEncoder enc) {
            super(AWTCharset.this.awtCs, enc.averageBytesPerChar(), enc.maxBytesPerChar());
            this.enc = enc;
        }
        
        @Override
        public boolean canEncode(final char c) {
            return this.enc.canEncode(c);
        }
        
        @Override
        public boolean canEncode(final CharSequence charSequence) {
            return this.enc.canEncode(charSequence);
        }
        
        @Override
        protected CoderResult encodeLoop(final CharBuffer charBuffer, final ByteBuffer byteBuffer) {
            return this.enc.encode(charBuffer, byteBuffer, true);
        }
        
        @Override
        protected CoderResult implFlush(final ByteBuffer byteBuffer) {
            return this.enc.flush(byteBuffer);
        }
        
        @Override
        protected void implReset() {
            this.enc.reset();
        }
        
        @Override
        protected void implReplaceWith(final byte[] array) {
            if (this.enc != null) {
                this.enc.replaceWith(array);
            }
        }
        
        @Override
        protected void implOnMalformedInput(final CodingErrorAction codingErrorAction) {
            this.enc.onMalformedInput(codingErrorAction);
        }
        
        @Override
        protected void implOnUnmappableCharacter(final CodingErrorAction codingErrorAction) {
            this.enc.onUnmappableCharacter(codingErrorAction);
        }
        
        @Override
        public boolean isLegalReplacement(final byte[] array) {
            return true;
        }
    }
    
    public class Decoder extends CharsetDecoder
    {
        protected CharsetDecoder dec;
        private String nr;
        ByteBuffer fbb;
        
        protected Decoder(final AWTCharset awtCharset) {
            this(awtCharset, awtCharset.javaCs.newDecoder());
        }
        
        protected Decoder(final CharsetDecoder dec) {
            super(AWTCharset.this.awtCs, dec.averageCharsPerByte(), dec.maxCharsPerByte());
            this.fbb = ByteBuffer.allocate(0);
            this.dec = dec;
        }
        
        @Override
        protected CoderResult decodeLoop(final ByteBuffer byteBuffer, final CharBuffer charBuffer) {
            return this.dec.decode(byteBuffer, charBuffer, true);
        }
        
        @Override
        protected CoderResult implFlush(final CharBuffer charBuffer) {
            this.dec.decode(this.fbb, charBuffer, true);
            return this.dec.flush(charBuffer);
        }
        
        @Override
        protected void implReset() {
            this.dec.reset();
        }
        
        @Override
        protected void implReplaceWith(final String s) {
            if (this.dec != null) {
                this.dec.replaceWith(s);
            }
        }
        
        @Override
        protected void implOnMalformedInput(final CodingErrorAction codingErrorAction) {
            this.dec.onMalformedInput(codingErrorAction);
        }
        
        @Override
        protected void implOnUnmappableCharacter(final CodingErrorAction codingErrorAction) {
            this.dec.onUnmappableCharacter(codingErrorAction);
        }
    }
}
