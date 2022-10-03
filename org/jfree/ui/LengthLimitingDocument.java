package org.jfree.ui;

import javax.swing.text.BadLocationException;
import javax.swing.text.AttributeSet;
import javax.swing.text.PlainDocument;

public class LengthLimitingDocument extends PlainDocument
{
    private int maxlen;
    
    public LengthLimitingDocument() {
        this(-1);
    }
    
    public LengthLimitingDocument(final int maxlen) {
        this.maxlen = maxlen;
    }
    
    public int getMaxLength() {
        return this.maxlen;
    }
    
    public void insertString(final int offs, final String str, final AttributeSet a) throws BadLocationException {
        if (str == null) {
            return;
        }
        if (this.maxlen < 0) {
            super.insertString(offs, str, a);
        }
        final char[] numeric = str.toCharArray();
        final StringBuffer b = new StringBuffer();
        b.append(numeric, 0, Math.min(this.maxlen, numeric.length));
        super.insertString(offs, b.toString(), a);
    }
    
    public void setMaxLength(final int maxlen) {
        this.maxlen = maxlen;
    }
}
