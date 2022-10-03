package com.adventnet.beans.rangenavigator;

import javax.swing.text.BadLocationException;
import javax.swing.text.AttributeSet;
import javax.swing.text.PlainDocument;

public class NumericTextField extends PlainDocument
{
    public static final String NUMERIC = "0123456789";
    public static final String PAGE = "0123456789-";
    private String acceptedChars;
    
    public NumericTextField() {
        this.acceptedChars = null;
    }
    
    public NumericTextField(final String acceptedChars) {
        this.acceptedChars = null;
        this.acceptedChars = acceptedChars;
    }
    
    public void insertString(final int n, final String s, final AttributeSet set) throws BadLocationException {
        if (s == null) {
            return;
        }
        for (int i = 0; i < s.length(); ++i) {
            if (this.acceptedChars.indexOf(String.valueOf(s.charAt(i))) == -1) {
                return;
            }
        }
        if ((this.acceptedChars.equals("0123456789-") || this.acceptedChars.equals("0123456789--")) && s.indexOf("-") != -1 && n != 0) {
            return;
        }
        if (n != 0 || !s.equals("-")) {
            super.insertString(n, s, set);
        }
    }
}
