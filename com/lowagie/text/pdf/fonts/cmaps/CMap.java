package com.lowagie.text.pdf.fonts.cmaps;

import java.io.IOException;
import com.lowagie.text.error_messages.MessageLocalization;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class CMap
{
    private List codeSpaceRanges;
    private Map singleByteMappings;
    private Map doubleByteMappings;
    
    public CMap() {
        this.codeSpaceRanges = new ArrayList();
        this.singleByteMappings = new HashMap();
        this.doubleByteMappings = new HashMap();
    }
    
    public boolean hasOneByteMappings() {
        return !this.singleByteMappings.isEmpty();
    }
    
    public boolean hasTwoByteMappings() {
        return !this.doubleByteMappings.isEmpty();
    }
    
    public String lookup(final char code) {
        String result = null;
        if (this.hasTwoByteMappings()) {
            result = this.doubleByteMappings.get(new Integer(code));
        }
        if (result == null && code <= '\u00ff' && this.hasOneByteMappings()) {
            result = this.singleByteMappings.get(new Integer(code & '\u00ff'));
        }
        return result;
    }
    
    public String lookup(final byte[] code, final int offset, final int length) {
        String result = null;
        Integer key = null;
        if (length == 1) {
            key = new Integer(code[offset] & 0xFF);
            result = this.singleByteMappings.get(key);
        }
        else if (length == 2) {
            int intKey = code[offset] & 0xFF;
            intKey <<= 8;
            intKey += (code[offset + 1] & 0xFF);
            key = new Integer(intKey);
            result = this.doubleByteMappings.get(key);
        }
        return result;
    }
    
    public void addMapping(final byte[] src, final String dest) throws IOException {
        if (src.length == 1) {
            this.singleByteMappings.put(new Integer(src[0] & 0xFF), dest);
        }
        else {
            if (src.length != 2) {
                throw new IOException(MessageLocalization.getComposedMessage("mapping.code.should.be.1.or.two.bytes.and.not.1", src.length));
            }
            int intSrc = src[0] & 0xFF;
            intSrc <<= 8;
            intSrc |= (src[1] & 0xFF);
            this.doubleByteMappings.put(new Integer(intSrc), dest);
        }
    }
    
    public void addCodespaceRange(final CodespaceRange range) {
        this.codeSpaceRanges.add(range);
    }
    
    public List getCodeSpaceRanges() {
        return this.codeSpaceRanges;
    }
}
