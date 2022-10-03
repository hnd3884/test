package javax.swing.plaf.metal;

import java.awt.Container;
import java.awt.LayoutManager;
import javax.swing.JSplitPane;
import javax.swing.JComponent;
import java.awt.Cursor;
import javax.swing.border.Border;
import javax.swing.JButton;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.Color;
import javax.swing.plaf.basic.BasicSplitPaneDivider;

class MetalSplitPaneDivider extends BasicSplitPaneDivider
{
    private MetalBumps bumps;
    private MetalBumps focusBumps;
    private int inset;
    private Color controlColor;
    private Color primaryControlColor;
    
    public MetalSplitPaneDivider(final BasicSplitPaneUI basicSplitPaneUI) {
        super(basicSplitPaneUI);
        this.bumps = new MetalBumps(10, 10, MetalLookAndFeel.getControlHighlight(), MetalLookAndFeel.getControlDarkShadow(), MetalLookAndFeel.getControl());
        this.focusBumps = new MetalBumps(10, 10, MetalLookAndFeel.getPrimaryControlHighlight(), MetalLookAndFeel.getPrimaryControlDarkShadow(), UIManager.getColor("SplitPane.dividerFocusColor"));
        this.inset = 2;
        this.controlColor = MetalLookAndFeel.getControl();
        this.primaryControlColor = UIManager.getColor("SplitPane.dividerFocusColor");
    }
    
    @Override
    public void paint(final Graphics graphics) {
        MetalBumps metalBumps;
        if (this.splitPane.hasFocus()) {
            metalBumps = this.focusBumps;
            graphics.setColor(this.primaryControlColor);
        }
        else {
            metalBumps = this.bumps;
            graphics.setColor(this.controlColor);
        }
        final Rectangle clipBounds = graphics.getClipBounds();
        final Insets insets = this.getInsets();
        graphics.fillRect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
        final Dimension size;
        final Dimension bumpArea = size = this.getSize();
        size.width -= this.inset * 2;
        final Dimension dimension = bumpArea;
        dimension.height -= this.inset * 2;
        int inset = this.inset;
        int inset2 = this.inset;
        if (insets != null) {
            final Dimension dimension2 = bumpArea;
            dimension2.width -= insets.left + insets.right;
            final Dimension dimension3 = bumpArea;
            dimension3.height -= insets.top + insets.bottom;
            inset += insets.left;
            inset2 += insets.top;
        }
        metalBumps.setBumpArea(bumpArea);
        metalBumps.paintIcon(this, graphics, inset, inset2);
        super.paint(graphics);
    }
    
    @Override
    protected JButton createLeftOneTouchButton() {
        final JButton button = new JButton() {
            int[][] buffer = { { 0, 0, 0, 2, 2, 0, 0, 0, 0 }, { 0, 0, 2, 1, 1, 1, 0, 0, 0 }, { 0, 2, 1, 1, 1, 1, 1, 0, 0 }, { 2, 1, 1, 1, 1, 1, 1, 1, 0 }, { 0, 3, 3, 3, 3, 3, 3, 3, 3 } };
            
            @Override
            public void setBorder(final Border border) {
            }
            
            @Override
            public void paint(final Graphics graphics) {
                if (MetalSplitPaneDivider.this.getSplitPaneFromSuper() != null) {
                    final int oneTouchSizeFromSuper = MetalSplitPaneDivider.this.getOneTouchSizeFromSuper();
                    final int orientationFromSuper = MetalSplitPaneDivider.this.getOrientationFromSuper();
                    final int min = Math.min(MetalSplitPaneDivider.this.getDividerSize(), oneTouchSizeFromSuper);
                    final Color[] array = { this.getBackground(), MetalLookAndFeel.getPrimaryControlDarkShadow(), MetalLookAndFeel.getPrimaryControlInfo(), MetalLookAndFeel.getPrimaryControlHighlight() };
                    graphics.setColor(this.getBackground());
                    if (this.isOpaque()) {
                        graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
                    }
                    if (this.getModel().isPressed()) {
                        array[1] = array[2];
                    }
                    if (orientationFromSuper == 0) {
                        for (int i = 1; i <= this.buffer[0].length; ++i) {
                            for (int j = 1; j < min; ++j) {
                                if (this.buffer[j - 1][i - 1] != 0) {
                                    graphics.setColor(array[this.buffer[j - 1][i - 1]]);
                                    graphics.drawLine(i, j, i, j);
                                }
                            }
                        }
                    }
                    else {
                        for (int k = 1; k <= this.buffer[0].length; ++k) {
                            for (int l = 1; l < min; ++l) {
                                if (this.buffer[l - 1][k - 1] != 0) {
                                    graphics.setColor(array[this.buffer[l - 1][k - 1]]);
                                    graphics.drawLine(l, k, l, k);
                                }
                            }
                        }
                    }
                }
            }
            
            @Override
            public boolean isFocusTraversable() {
                return false;
            }
        };
        button.setRequestFocusEnabled(false);
        button.setCursor(Cursor.getPredefinedCursor(0));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        this.maybeMakeButtonOpaque(button);
        return button;
    }
    
    private void maybeMakeButtonOpaque(final JComponent component) {
        final Object value = UIManager.get("SplitPane.oneTouchButtonsOpaque");
        if (value != null) {
            component.setOpaque((boolean)value);
        }
    }
    
    @Override
    protected JButton createRightOneTouchButton() {
        final JButton button = new JButton() {
            int[][] buffer = { { 2, 2, 2, 2, 2, 2, 2, 2 }, { 0, 1, 1, 1, 1, 1, 1, 3 }, { 0, 0, 1, 1, 1, 1, 3, 0 }, { 0, 0, 0, 1, 1, 3, 0, 0 }, { 0, 0, 0, 0, 3, 0, 0, 0 } };
            
            @Override
            public void setBorder(final Border border) {
            }
            
            @Override
            public void paint(final Graphics graphics) {
                if (MetalSplitPaneDivider.this.getSplitPaneFromSuper() != null) {
                    final int oneTouchSizeFromSuper = MetalSplitPaneDivider.this.getOneTouchSizeFromSuper();
                    final int orientationFromSuper = MetalSplitPaneDivider.this.getOrientationFromSuper();
                    final int min = Math.min(MetalSplitPaneDivider.this.getDividerSize(), oneTouchSizeFromSuper);
                    final Color[] array = { this.getBackground(), MetalLookAndFeel.getPrimaryControlDarkShadow(), MetalLookAndFeel.getPrimaryControlInfo(), MetalLookAndFeel.getPrimaryControlHighlight() };
                    graphics.setColor(this.getBackground());
                    if (this.isOpaque()) {
                        graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
                    }
                    if (this.getModel().isPressed()) {
                        array[1] = array[2];
                    }
                    if (orientationFromSuper == 0) {
                        for (int i = 1; i <= this.buffer[0].length; ++i) {
                            for (int j = 1; j < min; ++j) {
                                if (this.buffer[j - 1][i - 1] != 0) {
                                    graphics.setColor(array[this.buffer[j - 1][i - 1]]);
                                    graphics.drawLine(i, j, i, j);
                                }
                            }
                        }
                    }
                    else {
                        for (int k = 1; k <= this.buffer[0].length; ++k) {
                            for (int l = 1; l < min; ++l) {
                                if (this.buffer[l - 1][k - 1] != 0) {
                                    graphics.setColor(array[this.buffer[l - 1][k - 1]]);
                                    graphics.drawLine(l, k, l, k);
                                }
                            }
                        }
                    }
                }
            }
            
            @Override
            public boolean isFocusTraversable() {
                return false;
            }
        };
        button.setCursor(Cursor.getPredefinedCursor(0));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setRequestFocusEnabled(false);
        this.maybeMakeButtonOpaque(button);
        return button;
    }
    
    int getOneTouchSizeFromSuper() {
        return 6;
    }
    
    int getOneTouchOffsetFromSuper() {
        return 2;
    }
    
    int getOrientationFromSuper() {
        return super.orientation;
    }
    
    JSplitPane getSplitPaneFromSuper() {
        return super.splitPane;
    }
    
    JButton getLeftButtonFromSuper() {
        return super.leftButton;
    }
    
    JButton getRightButtonFromSuper() {
        return super.rightButton;
    }
    
    public class MetalDividerLayout implements LayoutManager
    {
        @Override
        public void layoutContainer(final Container container) {
            final JButton leftButtonFromSuper = MetalSplitPaneDivider.this.getLeftButtonFromSuper();
            final JButton rightButtonFromSuper = MetalSplitPaneDivider.this.getRightButtonFromSuper();
            final JSplitPane splitPaneFromSuper = MetalSplitPaneDivider.this.getSplitPaneFromSuper();
            final int orientationFromSuper = MetalSplitPaneDivider.this.getOrientationFromSuper();
            final int oneTouchSizeFromSuper = MetalSplitPaneDivider.this.getOneTouchSizeFromSuper();
            final int oneTouchOffsetFromSuper = MetalSplitPaneDivider.this.getOneTouchOffsetFromSuper();
            final Insets insets = MetalSplitPaneDivider.this.getInsets();
            if (leftButtonFromSuper != null && rightButtonFromSuper != null && container == MetalSplitPaneDivider.this) {
                if (splitPaneFromSuper.isOneTouchExpandable()) {
                    if (orientationFromSuper == 0) {
                        final int n = (insets != null) ? insets.top : 0;
                        int dividerSize = MetalSplitPaneDivider.this.getDividerSize();
                        if (insets != null) {
                            dividerSize -= insets.top + insets.bottom;
                        }
                        final int min = Math.min(dividerSize, oneTouchSizeFromSuper);
                        leftButtonFromSuper.setBounds(oneTouchOffsetFromSuper, n, min * 2, min);
                        rightButtonFromSuper.setBounds(oneTouchOffsetFromSuper + oneTouchSizeFromSuper * 2, n, min * 2, min);
                    }
                    else {
                        int dividerSize2 = MetalSplitPaneDivider.this.getDividerSize();
                        final int n2 = (insets != null) ? insets.left : 0;
                        if (insets != null) {
                            dividerSize2 -= insets.left + insets.right;
                        }
                        final int min2 = Math.min(dividerSize2, oneTouchSizeFromSuper);
                        leftButtonFromSuper.setBounds(n2, oneTouchOffsetFromSuper, min2, min2 * 2);
                        rightButtonFromSuper.setBounds(n2, oneTouchOffsetFromSuper + oneTouchSizeFromSuper * 2, min2, min2 * 2);
                    }
                }
                else {
                    leftButtonFromSuper.setBounds(-5, -5, 1, 1);
                    rightButtonFromSuper.setBounds(-5, -5, 1, 1);
                }
            }
        }
        
        @Override
        public Dimension minimumLayoutSize(final Container container) {
            return new Dimension(0, 0);
        }
        
        @Override
        public Dimension preferredLayoutSize(final Container container) {
            return new Dimension(0, 0);
        }
        
        @Override
        public void removeLayoutComponent(final Component component) {
        }
        
        @Override
        public void addLayoutComponent(final String s, final Component component) {
        }
    }
}
