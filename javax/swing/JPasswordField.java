package javax.swing;

import javax.accessibility.AccessibleTextSequence;
import java.util.Arrays;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleContext;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.swing.text.Segment;
import javax.swing.text.BadLocationException;
import java.awt.Component;
import javax.swing.text.Document;

public class JPasswordField extends JTextField
{
    private static final String uiClassID = "PasswordFieldUI";
    private char echoChar;
    private boolean echoCharSet;
    
    public JPasswordField() {
        this(null, null, 0);
    }
    
    public JPasswordField(final String s) {
        this(null, s, 0);
    }
    
    public JPasswordField(final int n) {
        this(null, null, n);
    }
    
    public JPasswordField(final String s, final int n) {
        this(null, s, n);
    }
    
    public JPasswordField(final Document document, final String s, final int n) {
        super(document, s, n);
        this.enableInputMethods(this.echoCharSet = false);
    }
    
    @Override
    public String getUIClassID() {
        return "PasswordFieldUI";
    }
    
    @Override
    public void updateUI() {
        if (!this.echoCharSet) {
            this.echoChar = '*';
        }
        super.updateUI();
    }
    
    public char getEchoChar() {
        return this.echoChar;
    }
    
    public void setEchoChar(final char echoChar) {
        this.echoChar = echoChar;
        this.echoCharSet = true;
        this.repaint();
        this.revalidate();
    }
    
    public boolean echoCharIsSet() {
        return this.echoChar != '\0';
    }
    
    @Override
    public void cut() {
        if (this.getClientProperty("JPasswordField.cutCopyAllowed") != Boolean.TRUE) {
            UIManager.getLookAndFeel().provideErrorFeedback(this);
        }
        else {
            super.cut();
        }
    }
    
    @Override
    public void copy() {
        if (this.getClientProperty("JPasswordField.cutCopyAllowed") != Boolean.TRUE) {
            UIManager.getLookAndFeel().provideErrorFeedback(this);
        }
        else {
            super.copy();
        }
    }
    
    @Deprecated
    @Override
    public String getText() {
        return super.getText();
    }
    
    @Deprecated
    @Override
    public String getText(final int n, final int n2) throws BadLocationException {
        return super.getText(n, n2);
    }
    
    public char[] getPassword() {
        final Document document = this.getDocument();
        final Segment segment = new Segment();
        try {
            document.getText(0, document.getLength(), segment);
        }
        catch (final BadLocationException ex) {
            return null;
        }
        final char[] array = new char[segment.count];
        System.arraycopy(segment.array, segment.offset, array, 0, segment.count);
        return array;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.getUIClassID().equals("PasswordFieldUI")) {
            final byte b = (byte)(JComponent.getWriteObjCounter(this) - 1);
            JComponent.setWriteObjCounter(this, b);
            if (b == 0 && this.ui != null) {
                this.ui.installUI(this);
            }
        }
    }
    
    @Override
    protected String paramString() {
        return super.paramString() + ",echoChar=" + this.echoChar;
    }
    
    boolean customSetUIProperty(final String s, final Object o) {
        if (s == "echoChar") {
            if (!this.echoCharSet) {
                this.setEchoChar((char)o);
                this.echoCharSet = false;
            }
            return true;
        }
        return false;
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJPasswordField();
        }
        return this.accessibleContext;
    }
    
    protected class AccessibleJPasswordField extends AccessibleJTextField
    {
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.PASSWORD_TEXT;
        }
        
        @Override
        public AccessibleText getAccessibleText() {
            return this;
        }
        
        private String getEchoString(final String s) {
            if (s == null) {
                return null;
            }
            final char[] array = new char[s.length()];
            Arrays.fill(array, JPasswordField.this.getEchoChar());
            return new String(array);
        }
        
        @Override
        public String getAtIndex(final int n, final int n2) {
            String atIndex;
            if (n == 1) {
                atIndex = super.getAtIndex(n, n2);
            }
            else {
                final char[] password = JPasswordField.this.getPassword();
                if (password == null || n2 < 0 || n2 >= password.length) {
                    return null;
                }
                atIndex = new String(password);
            }
            return this.getEchoString(atIndex);
        }
        
        @Override
        public String getAfterIndex(final int n, final int n2) {
            if (n == 1) {
                return this.getEchoString(super.getAfterIndex(n, n2));
            }
            return null;
        }
        
        @Override
        public String getBeforeIndex(final int n, final int n2) {
            if (n == 1) {
                return this.getEchoString(super.getBeforeIndex(n, n2));
            }
            return null;
        }
        
        @Override
        public String getTextRange(final int n, final int n2) {
            return this.getEchoString(super.getTextRange(n, n2));
        }
        
        @Override
        public AccessibleTextSequence getTextSequenceAt(final int n, final int n2) {
            if (n == 1) {
                final AccessibleTextSequence textSequence = super.getTextSequenceAt(n, n2);
                if (textSequence == null) {
                    return null;
                }
                return new AccessibleTextSequence(textSequence.startIndex, textSequence.endIndex, this.getEchoString(textSequence.text));
            }
            else {
                final char[] password = JPasswordField.this.getPassword();
                if (password == null || n2 < 0 || n2 >= password.length) {
                    return null;
                }
                return new AccessibleTextSequence(0, password.length - 1, this.getEchoString(new String(password)));
            }
        }
        
        @Override
        public AccessibleTextSequence getTextSequenceAfter(final int n, final int n2) {
            if (n != 1) {
                return null;
            }
            final AccessibleTextSequence textSequenceAfter = super.getTextSequenceAfter(n, n2);
            if (textSequenceAfter == null) {
                return null;
            }
            return new AccessibleTextSequence(textSequenceAfter.startIndex, textSequenceAfter.endIndex, this.getEchoString(textSequenceAfter.text));
        }
        
        @Override
        public AccessibleTextSequence getTextSequenceBefore(final int n, final int n2) {
            if (n != 1) {
                return null;
            }
            final AccessibleTextSequence textSequenceBefore = super.getTextSequenceBefore(n, n2);
            if (textSequenceBefore == null) {
                return null;
            }
            return new AccessibleTextSequence(textSequenceBefore.startIndex, textSequenceBefore.endIndex, this.getEchoString(textSequenceBefore.text));
        }
    }
}
