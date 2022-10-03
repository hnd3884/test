package org.jfree.ui;

import javax.swing.text.BadLocationException;
import javax.swing.text.AttributeSet;
import javax.swing.text.PlainDocument;

public class IntegerDocument extends PlainDocument
{
    public void insertString(final int i, final String s, final AttributeSet attributes) throws BadLocationException {
        super.insertString(i, s, attributes);
        if (s != null) {
            if (s.equals("-") && i == 0) {
                if (s.length() < 2) {
                    return;
                }
            }
            try {
                Integer.parseInt(this.getText(0, this.getLength()));
            }
            catch (final NumberFormatException ex) {
                this.remove(i, s.length());
            }
        }
    }
}
