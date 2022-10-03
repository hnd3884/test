package sun.awt.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Label;
import java.awt.Dimension;
import java.awt.peer.LabelPeer;

final class WLabelPeer extends WComponentPeer implements LabelPeer
{
    @Override
    public Dimension getMinimumSize() {
        final FontMetrics fontMetrics = this.getFontMetrics(((Label)this.target).getFont());
        String text = ((Label)this.target).getText();
        if (text == null) {
            text = "";
        }
        return new Dimension(fontMetrics.stringWidth(text) + 14, fontMetrics.getHeight() + 8);
    }
    
    native void lazyPaint();
    
    @Override
    synchronized void start() {
        super.start();
        this.lazyPaint();
    }
    
    @Override
    public boolean shouldClearRectBeforePaint() {
        return false;
    }
    
    @Override
    public native void setText(final String p0);
    
    @Override
    public native void setAlignment(final int p0);
    
    WLabelPeer(final Label label) {
        super(label);
    }
    
    @Override
    native void create(final WComponentPeer p0);
    
    @Override
    void initialize() {
        final Label label = (Label)this.target;
        final String text = label.getText();
        if (text != null) {
            this.setText(text);
        }
        final int alignment = label.getAlignment();
        if (alignment != 0) {
            this.setAlignment(alignment);
        }
        final Color background = ((Component)this.target).getBackground();
        if (background != null) {
            this.setBackground(background);
        }
        super.initialize();
    }
}
