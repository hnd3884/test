package com.sun.java.swing.plaf.motif;

import java.awt.event.MouseEvent;
import javax.swing.JSplitPane;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.Color;
import java.awt.Cursor;
import javax.swing.plaf.basic.BasicSplitPaneDivider;

public class MotifSplitPaneDivider extends BasicSplitPaneDivider
{
    private static final Cursor defaultCursor;
    public static final int minimumThumbSize = 6;
    public static final int defaultDividerSize = 18;
    protected static final int pad = 6;
    private int hThumbOffset;
    private int vThumbOffset;
    protected int hThumbWidth;
    protected int hThumbHeight;
    protected int vThumbWidth;
    protected int vThumbHeight;
    protected Color highlightColor;
    protected Color shadowColor;
    protected Color focusedColor;
    
    public MotifSplitPaneDivider(final BasicSplitPaneUI basicSplitPaneUI) {
        super(basicSplitPaneUI);
        this.hThumbOffset = 30;
        this.vThumbOffset = 40;
        this.hThumbWidth = 12;
        this.hThumbHeight = 18;
        this.vThumbWidth = 18;
        this.vThumbHeight = 12;
        this.highlightColor = UIManager.getColor("SplitPane.highlight");
        this.shadowColor = UIManager.getColor("SplitPane.shadow");
        this.focusedColor = UIManager.getColor("SplitPane.activeThumb");
        this.setDividerSize(this.hThumbWidth + 6);
    }
    
    @Override
    public void setDividerSize(final int dividerSize) {
        final Insets insets = this.getInsets();
        int n = 0;
        if (this.getBasicSplitPaneUI().getOrientation() == 1) {
            if (insets != null) {
                n = insets.left + insets.right;
            }
        }
        else if (insets != null) {
            n = insets.top + insets.bottom;
        }
        if (dividerSize < 12 + n) {
            this.setDividerSize(12 + n);
        }
        else {
            final int n2 = dividerSize - 6 - n;
            this.hThumbWidth = n2;
            this.vThumbHeight = n2;
            super.setDividerSize(dividerSize);
        }
    }
    
    @Override
    public void paint(final Graphics graphics) {
        this.getBackground();
        final Dimension size = this.getSize();
        graphics.setColor(this.getBackground());
        graphics.fillRect(0, 0, size.width, size.height);
        if (this.getBasicSplitPaneUI().getOrientation() == 1) {
            final int n = size.width / 2;
            final int n2 = n - this.hThumbWidth / 2;
            final int hThumbOffset = this.hThumbOffset;
            graphics.setColor(this.shadowColor);
            graphics.drawLine(n - 1, 0, n - 1, size.height);
            graphics.setColor(this.highlightColor);
            graphics.drawLine(n, 0, n, size.height);
            graphics.setColor(this.splitPane.hasFocus() ? this.focusedColor : this.getBackground());
            graphics.fillRect(n2 + 1, hThumbOffset + 1, this.hThumbWidth - 2, this.hThumbHeight - 1);
            graphics.setColor(this.highlightColor);
            graphics.drawLine(n2, hThumbOffset, n2 + this.hThumbWidth - 1, hThumbOffset);
            graphics.drawLine(n2, hThumbOffset + 1, n2, hThumbOffset + this.hThumbHeight - 1);
            graphics.setColor(this.shadowColor);
            graphics.drawLine(n2 + 1, hThumbOffset + this.hThumbHeight - 1, n2 + this.hThumbWidth - 1, hThumbOffset + this.hThumbHeight - 1);
            graphics.drawLine(n2 + this.hThumbWidth - 1, hThumbOffset + 1, n2 + this.hThumbWidth - 1, hThumbOffset + this.hThumbHeight - 2);
        }
        else {
            final int n3 = size.height / 2;
            final int n4 = size.width - this.vThumbOffset;
            final int n5 = size.height / 2 - this.vThumbHeight / 2;
            graphics.setColor(this.shadowColor);
            graphics.drawLine(0, n3 - 1, size.width, n3 - 1);
            graphics.setColor(this.highlightColor);
            graphics.drawLine(0, n3, size.width, n3);
            graphics.setColor(this.splitPane.hasFocus() ? this.focusedColor : this.getBackground());
            graphics.fillRect(n4 + 1, n5 + 1, this.vThumbWidth - 1, this.vThumbHeight - 1);
            graphics.setColor(this.highlightColor);
            graphics.drawLine(n4, n5, n4 + this.vThumbWidth, n5);
            graphics.drawLine(n4, n5 + 1, n4, n5 + this.vThumbHeight);
            graphics.setColor(this.shadowColor);
            graphics.drawLine(n4 + 1, n5 + this.vThumbHeight, n4 + this.vThumbWidth, n5 + this.vThumbHeight);
            graphics.drawLine(n4 + this.vThumbWidth, n5 + 1, n4 + this.vThumbWidth, n5 + this.vThumbHeight - 1);
        }
        super.paint(graphics);
    }
    
    @Override
    public Dimension getMinimumSize() {
        return this.getPreferredSize();
    }
    
    @Override
    public void setBasicSplitPaneUI(final BasicSplitPaneUI splitPaneUI) {
        if (this.splitPane != null) {
            this.splitPane.removePropertyChangeListener(this);
            if (this.mouseHandler != null) {
                this.splitPane.removeMouseListener(this.mouseHandler);
                this.splitPane.removeMouseMotionListener(this.mouseHandler);
                this.removeMouseListener(this.mouseHandler);
                this.removeMouseMotionListener(this.mouseHandler);
                this.mouseHandler = null;
            }
        }
        if ((this.splitPaneUI = splitPaneUI) != null) {
            this.splitPane = splitPaneUI.getSplitPane();
            if (this.splitPane != null) {
                if (this.mouseHandler == null) {
                    this.mouseHandler = new MotifMouseHandler();
                }
                this.splitPane.addMouseListener(this.mouseHandler);
                this.splitPane.addMouseMotionListener(this.mouseHandler);
                this.addMouseListener(this.mouseHandler);
                this.addMouseMotionListener(this.mouseHandler);
                this.splitPane.addPropertyChangeListener(this);
                if (this.splitPane.isOneTouchExpandable()) {
                    this.oneTouchExpandableChanged();
                }
            }
        }
        else {
            this.splitPane = null;
        }
    }
    
    private boolean isInThumb(final int n, final int n2) {
        final Dimension size = this.getSize();
        int n3;
        int hThumbOffset;
        int n4;
        int n5;
        if (this.getBasicSplitPaneUI().getOrientation() == 1) {
            n3 = size.width / 2 - this.hThumbWidth / 2;
            hThumbOffset = this.hThumbOffset;
            n4 = this.hThumbWidth;
            n5 = this.hThumbHeight;
        }
        else {
            final int n6 = size.height / 2;
            n3 = size.width - this.vThumbOffset;
            hThumbOffset = size.height / 2 - this.vThumbHeight / 2;
            n4 = this.vThumbWidth;
            n5 = this.vThumbHeight;
        }
        return n >= n3 && n < n3 + n4 && n2 >= hThumbOffset && n2 < hThumbOffset + n5;
    }
    
    private DragController getDragger() {
        return this.dragger;
    }
    
    private JSplitPane getSplitPane() {
        return this.splitPane;
    }
    
    static {
        defaultCursor = Cursor.getPredefinedCursor(0);
    }
    
    private class MotifMouseHandler extends MouseHandler
    {
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            if (mouseEvent.getSource() == MotifSplitPaneDivider.this && MotifSplitPaneDivider.this.getDragger() == null && MotifSplitPaneDivider.this.getSplitPane().isEnabled() && MotifSplitPaneDivider.this.isInThumb(mouseEvent.getX(), mouseEvent.getY())) {
                super.mousePressed(mouseEvent);
            }
        }
        
        @Override
        public void mouseMoved(final MouseEvent mouseEvent) {
            if (MotifSplitPaneDivider.this.getDragger() != null) {
                return;
            }
            if (!MotifSplitPaneDivider.this.isInThumb(mouseEvent.getX(), mouseEvent.getY())) {
                if (MotifSplitPaneDivider.this.getCursor() != MotifSplitPaneDivider.defaultCursor) {
                    MotifSplitPaneDivider.this.setCursor(MotifSplitPaneDivider.defaultCursor);
                }
                return;
            }
            super.mouseMoved(mouseEvent);
        }
    }
}
