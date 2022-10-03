package org.apache.lucene.analysis.util;

import java.io.IOException;
import org.apache.lucene.util.AttributeFactory;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.Tokenizer;

public abstract class CharTokenizer extends Tokenizer
{
    private int offset;
    private int bufferIndex;
    private int dataLen;
    private int finalOffset;
    private static final int MAX_WORD_LEN = 255;
    private static final int IO_BUFFER_SIZE = 4096;
    private final CharTermAttribute termAtt;
    private final OffsetAttribute offsetAtt;
    private final CharacterUtils charUtils;
    private final CharacterUtils.CharacterBuffer ioBuffer;
    
    public CharTokenizer() {
        this.offset = 0;
        this.bufferIndex = 0;
        this.dataLen = 0;
        this.finalOffset = 0;
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.offsetAtt = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        this.charUtils = CharacterUtils.getInstance();
        this.ioBuffer = CharacterUtils.newCharacterBuffer(4096);
    }
    
    public CharTokenizer(final AttributeFactory factory) {
        super(factory);
        this.offset = 0;
        this.bufferIndex = 0;
        this.dataLen = 0;
        this.finalOffset = 0;
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.offsetAtt = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        this.charUtils = CharacterUtils.getInstance();
        this.ioBuffer = CharacterUtils.newCharacterBuffer(4096);
    }
    
    protected abstract boolean isTokenChar(final int p0);
    
    protected int normalize(final int c) {
        return c;
    }
    
    public final boolean incrementToken() throws IOException {
        this.clearAttributes();
        int length = 0;
        int start = -1;
        int end = -1;
        char[] buffer = this.termAtt.buffer();
        while (true) {
            if (this.bufferIndex >= this.dataLen) {
                this.offset += this.dataLen;
                this.charUtils.fill(this.ioBuffer, this.input);
                if (this.ioBuffer.getLength() == 0) {
                    this.dataLen = 0;
                    if (length > 0) {
                        break;
                    }
                    this.finalOffset = this.correctOffset(this.offset);
                    return false;
                }
                else {
                    this.dataLen = this.ioBuffer.getLength();
                    this.bufferIndex = 0;
                }
            }
            final int c = this.charUtils.codePointAt(this.ioBuffer.getBuffer(), this.bufferIndex, this.ioBuffer.getLength());
            final int charCount = Character.charCount(c);
            this.bufferIndex += charCount;
            if (this.isTokenChar(c)) {
                if (length == 0) {
                    assert start == -1;
                    start = (end = this.offset + this.bufferIndex - charCount);
                }
                else if (length >= buffer.length - 1) {
                    buffer = this.termAtt.resizeBuffer(2 + length);
                }
                end += charCount;
                length += Character.toChars(this.normalize(c), buffer, length);
                if (length >= 255) {
                    break;
                }
                continue;
            }
            else {
                if (length > 0) {
                    break;
                }
                continue;
            }
        }
        this.termAtt.setLength(length);
        assert start != -1;
        this.offsetAtt.setOffset(this.correctOffset(start), this.finalOffset = this.correctOffset(end));
        return true;
    }
    
    public final void end() throws IOException {
        super.end();
        this.offsetAtt.setOffset(this.finalOffset, this.finalOffset);
    }
    
    public void reset() throws IOException {
        super.reset();
        this.bufferIndex = 0;
        this.offset = 0;
        this.dataLen = 0;
        this.finalOffset = 0;
        this.ioBuffer.reset();
    }
}
