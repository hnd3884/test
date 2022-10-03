package sun.awt.windows;

import java.awt.TextComponent;
import java.awt.im.InputMethodRequests;
import java.awt.FontMetrics;
import java.awt.TextArea;
import java.awt.Dimension;
import java.awt.peer.TextAreaPeer;

final class WTextAreaPeer extends WTextComponentPeer implements TextAreaPeer
{
    @Override
    public Dimension getMinimumSize() {
        return this.getMinimumSize(10, 60);
    }
    
    @Override
    public void insert(final String s, final int n) {
        this.replaceRange(s, n, n);
    }
    
    @Override
    public native void replaceRange(final String p0, final int p1, final int p2);
    
    @Override
    public Dimension getPreferredSize(final int n, final int n2) {
        return this.getMinimumSize(n, n2);
    }
    
    @Override
    public Dimension getMinimumSize(final int n, final int n2) {
        final FontMetrics fontMetrics = this.getFontMetrics(((TextArea)this.target).getFont());
        return new Dimension(fontMetrics.charWidth('0') * n2 + 20, fontMetrics.getHeight() * n + 20);
    }
    
    @Override
    public InputMethodRequests getInputMethodRequests() {
        return null;
    }
    
    WTextAreaPeer(final TextArea textArea) {
        super(textArea);
    }
    
    @Override
    native void create(final WComponentPeer p0);
}
