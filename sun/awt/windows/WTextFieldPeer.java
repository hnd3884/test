package sun.awt.windows;

import java.awt.TextComponent;
import java.awt.im.InputMethodRequests;
import java.awt.AWTEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.FontMetrics;
import java.awt.TextField;
import java.awt.Dimension;
import java.awt.peer.TextFieldPeer;

final class WTextFieldPeer extends WTextComponentPeer implements TextFieldPeer
{
    @Override
    public Dimension getMinimumSize() {
        final FontMetrics fontMetrics = this.getFontMetrics(((TextField)this.target).getFont());
        return new Dimension(fontMetrics.stringWidth(this.getText()) + 24, fontMetrics.getHeight() + 8);
    }
    
    @Override
    public boolean handleJavaKeyEvent(final KeyEvent keyEvent) {
        switch (keyEvent.getID()) {
            case 400: {
                if (keyEvent.getKeyChar() == '\n' && !keyEvent.isAltDown() && !keyEvent.isControlDown()) {
                    this.postEvent(new ActionEvent(this.target, 1001, this.getText(), keyEvent.getWhen(), keyEvent.getModifiers()));
                    return true;
                }
                break;
            }
        }
        return false;
    }
    
    @Override
    public native void setEchoChar(final char p0);
    
    @Override
    public Dimension getPreferredSize(final int n) {
        return this.getMinimumSize(n);
    }
    
    @Override
    public Dimension getMinimumSize(final int n) {
        final FontMetrics fontMetrics = this.getFontMetrics(((TextField)this.target).getFont());
        return new Dimension(fontMetrics.charWidth('0') * n + 24, fontMetrics.getHeight() + 8);
    }
    
    @Override
    public InputMethodRequests getInputMethodRequests() {
        return null;
    }
    
    WTextFieldPeer(final TextField textField) {
        super(textField);
    }
    
    @Override
    native void create(final WComponentPeer p0);
    
    @Override
    void initialize() {
        final TextField textField = (TextField)this.target;
        if (textField.echoCharIsSet()) {
            this.setEchoChar(textField.getEchoChar());
        }
        super.initialize();
    }
}
