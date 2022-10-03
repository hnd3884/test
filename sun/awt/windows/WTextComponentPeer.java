package sun.awt.windows;

import java.awt.AWTEvent;
import java.awt.event.TextEvent;
import java.awt.Component;
import java.awt.TextComponent;
import java.awt.peer.TextComponentPeer;

abstract class WTextComponentPeer extends WComponentPeer implements TextComponentPeer
{
    @Override
    public void setEditable(final boolean b) {
        this.enableEditing(b);
        this.setBackground(((TextComponent)this.target).getBackground());
    }
    
    @Override
    public native String getText();
    
    @Override
    public native void setText(final String p0);
    
    @Override
    public native int getSelectionStart();
    
    @Override
    public native int getSelectionEnd();
    
    @Override
    public native void select(final int p0, final int p1);
    
    WTextComponentPeer(final TextComponent textComponent) {
        super(textComponent);
    }
    
    @Override
    void initialize() {
        final TextComponent textComponent = (TextComponent)this.target;
        final String text = textComponent.getText();
        if (text != null) {
            this.setText(text);
        }
        this.select(textComponent.getSelectionStart(), textComponent.getSelectionEnd());
        this.setEditable(textComponent.isEditable());
        super.initialize();
    }
    
    native void enableEditing(final boolean p0);
    
    @Override
    public boolean isFocusable() {
        return true;
    }
    
    @Override
    public void setCaretPosition(final int n) {
        this.select(n, n);
    }
    
    @Override
    public int getCaretPosition() {
        return this.getSelectionStart();
    }
    
    public void valueChanged() {
        this.postEvent(new TextEvent(this.target, 900));
    }
    
    private static native void initIDs();
    
    @Override
    public boolean shouldClearRectBeforePaint() {
        return false;
    }
    
    static {
        initIDs();
    }
}
