package com.adventnet.beans.utils;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.AttributeSet;
import javax.swing.text.PlainDocument;

public class JTextFieldFilter extends PlainDocument
{
    public static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    public static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String ALPHA = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String NUMERIC = "0123456789";
    public static final String FLOAT = "0123456789.";
    public static final String ALPHA_NUMERIC = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static final String DBCHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";
    private int maxSize;
    private String defaultText;
    protected String acceptedChars;
    protected boolean negativeAccepted;
    
    public JTextFieldFilter() {
        this("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
    }
    
    public JTextFieldFilter(final String acceptedChars) {
        this.maxSize = -1;
        this.acceptedChars = null;
        this.negativeAccepted = false;
        this.acceptedChars = acceptedChars;
    }
    
    public JTextFieldFilter(final String acceptedChars, final int maxSize) {
        this.maxSize = -1;
        this.acceptedChars = null;
        this.negativeAccepted = false;
        this.acceptedChars = acceptedChars;
        if (maxSize > 0) {
            this.maxSize = maxSize;
        }
    }
    
    public JTextFieldFilter(final String acceptedChars, final int maxSize, final String defaultText) throws BadLocationException {
        this.maxSize = -1;
        this.acceptedChars = null;
        this.negativeAccepted = false;
        this.acceptedChars = acceptedChars;
        if (maxSize > 0) {
            this.maxSize = maxSize;
        }
        this.defaultText = defaultText;
        if (this.getLength() == 0) {
            this.insertString(0, defaultText, null);
        }
    }
    
    public void setNegativeAccepted(final boolean negativeAccepted) {
        if (this.acceptedChars.equals("0123456789") || this.acceptedChars.equals("0123456789.") || this.acceptedChars.equals("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789")) {
            this.negativeAccepted = negativeAccepted;
            this.acceptedChars += "-";
        }
    }
    
    public void insertString(final int n, String s, final AttributeSet set) throws BadLocationException {
        if (s == null) {
            return;
        }
        if (this.maxSize > 0 && this.getLength() + s.length() >= this.maxSize) {
            return;
        }
        if (this.acceptedChars.equals("ABCDEFGHIJKLMNOPQRSTUVWXYZ")) {
            s = s.toUpperCase();
        }
        else if (this.acceptedChars.equals("abcdefghijklmnopqrstuvwxyz")) {
            s = s.toLowerCase();
        }
        for (int i = 0; i < s.length(); ++i) {
            if (this.acceptedChars.indexOf(String.valueOf(s.charAt(i))) == -1) {
                return;
            }
        }
        if ((this.acceptedChars.equals("0123456789.") || (this.acceptedChars.equals("0123456789.-") && this.negativeAccepted)) && s.indexOf(".") != -1 && this.getText(0, this.getLength()).indexOf(".") != -1) {
            return;
        }
        if (this.negativeAccepted && s.indexOf("-") != -1 && (s.indexOf("-") != 0 || n != 0)) {
            return;
        }
        super.insertString(n, s, set);
    }
    
    protected void removeUpdate(final DefaultDocumentEvent defaultDocumentEvent) {
        try {
            if (defaultDocumentEvent.getOffset() == 0 && defaultDocumentEvent.getLength() == this.getLength()) {
                this.insertString(defaultDocumentEvent.getLength(), this.defaultText, null);
            }
        }
        catch (final BadLocationException ex) {
            ex.printStackTrace();
        }
        super.removeUpdate(defaultDocumentEvent);
    }
}
