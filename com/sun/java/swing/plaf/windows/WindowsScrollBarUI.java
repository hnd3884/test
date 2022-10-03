package com.sun.java.swing.plaf.windows;

import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.image.IndexColorModel;
import java.awt.image.BufferedImage;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import javax.swing.ButtonModel;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.Insets;
import javax.swing.JScrollBar;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import java.awt.Color;
import javax.swing.UIManager;
import java.awt.Component;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.Dimension;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class WindowsScrollBarUI extends BasicScrollBarUI
{
    private Grid thumbGrid;
    private Grid highlightGrid;
    private Dimension horizontalThumbSize;
    private Dimension verticalThumbSize;
    
    public static ComponentUI createUI(final JComponent component) {
        return new WindowsScrollBarUI();
    }
    
    @Override
    protected void installDefaults() {
        super.installDefaults();
        final XPStyle xp = XPStyle.getXP();
        if (xp != null) {
            this.scrollbar.setBorder(null);
            this.horizontalThumbSize = getSize(this.scrollbar, xp, TMSchema.Part.SBP_THUMBBTNHORZ);
            this.verticalThumbSize = getSize(this.scrollbar, xp, TMSchema.Part.SBP_THUMBBTNVERT);
        }
        else {
            this.horizontalThumbSize = null;
            this.verticalThumbSize = null;
        }
    }
    
    private static Dimension getSize(final Component component, final XPStyle xpStyle, final TMSchema.Part part) {
        final XPStyle.Skin skin = xpStyle.getSkin(component, part);
        return new Dimension(skin.getWidth(), skin.getHeight());
    }
    
    @Override
    protected Dimension getMinimumThumbSize() {
        if (this.horizontalThumbSize == null || this.verticalThumbSize == null) {
            return super.getMinimumThumbSize();
        }
        return (0 == this.scrollbar.getOrientation()) ? this.horizontalThumbSize : this.verticalThumbSize;
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        super.uninstallUI(component);
        final Grid grid = null;
        this.highlightGrid = grid;
        this.thumbGrid = grid;
    }
    
    @Override
    protected void configureScrollBarColors() {
        super.configureScrollBarColors();
        final Color color = UIManager.getColor("ScrollBar.trackForeground");
        if (color != null && this.trackColor != null) {
            this.thumbGrid = Grid.getGrid(color, this.trackColor);
        }
        final Color color2 = UIManager.getColor("ScrollBar.trackHighlightForeground");
        if (color2 != null && this.trackHighlightColor != null) {
            this.highlightGrid = Grid.getGrid(color2, this.trackHighlightColor);
        }
    }
    
    @Override
    protected JButton createDecreaseButton(final int n) {
        return new WindowsArrowButton(n, UIManager.getColor("ScrollBar.thumb"), UIManager.getColor("ScrollBar.thumbShadow"), UIManager.getColor("ScrollBar.thumbDarkShadow"), UIManager.getColor("ScrollBar.thumbHighlight"));
    }
    
    @Override
    protected JButton createIncreaseButton(final int n) {
        return new WindowsArrowButton(n, UIManager.getColor("ScrollBar.thumb"), UIManager.getColor("ScrollBar.thumbShadow"), UIManager.getColor("ScrollBar.thumbDarkShadow"), UIManager.getColor("ScrollBar.thumbHighlight"));
    }
    
    @Override
    protected ArrowButtonListener createArrowButtonListener() {
        if (XPStyle.isVista()) {
            return new ArrowButtonListener() {
                @Override
                public void mouseEntered(final MouseEvent mouseEvent) {
                    this.repaint();
                    super.mouseEntered(mouseEvent);
                }
                
                @Override
                public void mouseExited(final MouseEvent mouseEvent) {
                    this.repaint();
                    super.mouseExited(mouseEvent);
                }
                
                private void repaint() {
                    WindowsScrollBarUI.this.scrollbar.repaint();
                }
            };
        }
        return super.createArrowButtonListener();
    }
    
    @Override
    protected void paintTrack(final Graphics graphics, final JComponent component, final Rectangle rectangle) {
        final boolean b = this.scrollbar.getOrientation() == 1;
        final XPStyle xp = XPStyle.getXP();
        if (xp != null) {
            final JScrollBar scrollBar = (JScrollBar)component;
            TMSchema.State state = TMSchema.State.NORMAL;
            if (!scrollBar.isEnabled()) {
                state = TMSchema.State.DISABLED;
            }
            xp.getSkin(scrollBar, b ? TMSchema.Part.SBP_LOWERTRACKVERT : TMSchema.Part.SBP_LOWERTRACKHORZ).paintSkin(graphics, rectangle, state);
        }
        else if (this.thumbGrid == null) {
            super.paintTrack(graphics, component, rectangle);
        }
        else {
            this.thumbGrid.paint(graphics, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            if (this.trackHighlight == 1) {
                this.paintDecreaseHighlight(graphics);
            }
            else if (this.trackHighlight == 2) {
                this.paintIncreaseHighlight(graphics);
            }
        }
    }
    
    @Override
    protected void paintThumb(final Graphics graphics, final JComponent component, final Rectangle rectangle) {
        final boolean b = this.scrollbar.getOrientation() == 1;
        final XPStyle xp = XPStyle.getXP();
        if (xp != null) {
            final JScrollBar scrollBar = (JScrollBar)component;
            TMSchema.State state = TMSchema.State.NORMAL;
            if (!scrollBar.isEnabled()) {
                state = TMSchema.State.DISABLED;
            }
            else if (this.isDragging) {
                state = TMSchema.State.PRESSED;
            }
            else if (this.isThumbRollover()) {
                state = TMSchema.State.HOT;
            }
            else if (XPStyle.isVista() && ((this.incrButton != null && this.incrButton.getModel().isRollover()) || (this.decrButton != null && this.decrButton.getModel().isRollover()))) {
                state = TMSchema.State.HOVER;
            }
            final TMSchema.Part part = b ? TMSchema.Part.SBP_THUMBBTNVERT : TMSchema.Part.SBP_THUMBBTNHORZ;
            xp.getSkin(scrollBar, part).paintSkin(graphics, rectangle, state);
            final XPStyle.Skin skin = xp.getSkin(scrollBar, b ? TMSchema.Part.SBP_GRIPPERVERT : TMSchema.Part.SBP_GRIPPERHORZ);
            final Insets margin = xp.getMargin(component, part, null, TMSchema.Prop.CONTENTMARGINS);
            if (margin == null || (b && rectangle.height - margin.top - margin.bottom >= skin.getHeight()) || (!b && rectangle.width - margin.left - margin.right >= skin.getWidth())) {
                skin.paintSkin(graphics, rectangle.x + (rectangle.width - skin.getWidth()) / 2, rectangle.y + (rectangle.height - skin.getHeight()) / 2, skin.getWidth(), skin.getHeight(), state);
            }
        }
        else {
            super.paintThumb(graphics, component, rectangle);
        }
    }
    
    @Override
    protected void paintDecreaseHighlight(final Graphics graphics) {
        if (this.highlightGrid == null) {
            super.paintDecreaseHighlight(graphics);
        }
        else {
            final Insets insets = this.scrollbar.getInsets();
            final Rectangle thumbBounds = this.getThumbBounds();
            int left;
            int top;
            int n;
            int n2;
            if (this.scrollbar.getOrientation() == 1) {
                left = insets.left;
                top = this.decrButton.getY() + this.decrButton.getHeight();
                n = this.scrollbar.getWidth() - (insets.left + insets.right);
                n2 = thumbBounds.y - top;
            }
            else {
                left = this.decrButton.getX() + this.decrButton.getHeight();
                top = insets.top;
                n = thumbBounds.x - left;
                n2 = this.scrollbar.getHeight() - (insets.top + insets.bottom);
            }
            this.highlightGrid.paint(graphics, left, top, n, n2);
        }
    }
    
    @Override
    protected void paintIncreaseHighlight(final Graphics graphics) {
        if (this.highlightGrid == null) {
            super.paintDecreaseHighlight(graphics);
        }
        else {
            final Insets insets = this.scrollbar.getInsets();
            final Rectangle thumbBounds = this.getThumbBounds();
            int left;
            int top;
            int n;
            int n2;
            if (this.scrollbar.getOrientation() == 1) {
                left = insets.left;
                top = thumbBounds.y + thumbBounds.height;
                n = this.scrollbar.getWidth() - (insets.left + insets.right);
                n2 = this.incrButton.getY() - top;
            }
            else {
                left = thumbBounds.x + thumbBounds.width;
                top = insets.top;
                n = this.incrButton.getX() - left;
                n2 = this.scrollbar.getHeight() - (insets.top + insets.bottom);
            }
            this.highlightGrid.paint(graphics, left, top, n, n2);
        }
    }
    
    @Override
    protected void setThumbRollover(final boolean thumbRollover) {
        final boolean thumbRollover2 = this.isThumbRollover();
        super.setThumbRollover(thumbRollover);
        if (XPStyle.isVista() && thumbRollover != thumbRollover2) {
            this.scrollbar.repaint();
        }
    }
    
    private class WindowsArrowButton extends BasicArrowButton
    {
        public WindowsArrowButton(final int n, final Color color, final Color color2, final Color color3, final Color color4) {
            super(n, color, color2, color3, color4);
        }
        
        public WindowsArrowButton(final int n) {
            super(n);
        }
        
        @Override
        public void paint(final Graphics graphics) {
            final XPStyle xp = XPStyle.getXP();
            if (xp != null) {
                final ButtonModel model = this.getModel();
                final XPStyle.Skin skin = xp.getSkin(this, TMSchema.Part.SBP_ARROWBTN);
                TMSchema.State state = null;
                final boolean b = XPStyle.isVista() && (WindowsScrollBarUI.this.isThumbRollover() || (this == WindowsScrollBarUI.this.incrButton && WindowsScrollBarUI.this.decrButton.getModel().isRollover()) || (this == WindowsScrollBarUI.this.decrButton && WindowsScrollBarUI.this.incrButton.getModel().isRollover()));
                if (model.isArmed() && model.isPressed()) {
                    switch (this.direction) {
                        case 1: {
                            state = TMSchema.State.UPPRESSED;
                            break;
                        }
                        case 5: {
                            state = TMSchema.State.DOWNPRESSED;
                            break;
                        }
                        case 7: {
                            state = TMSchema.State.LEFTPRESSED;
                            break;
                        }
                        case 3: {
                            state = TMSchema.State.RIGHTPRESSED;
                            break;
                        }
                    }
                }
                else if (!model.isEnabled()) {
                    switch (this.direction) {
                        case 1: {
                            state = TMSchema.State.UPDISABLED;
                            break;
                        }
                        case 5: {
                            state = TMSchema.State.DOWNDISABLED;
                            break;
                        }
                        case 7: {
                            state = TMSchema.State.LEFTDISABLED;
                            break;
                        }
                        case 3: {
                            state = TMSchema.State.RIGHTDISABLED;
                            break;
                        }
                    }
                }
                else if (model.isRollover() || model.isPressed()) {
                    switch (this.direction) {
                        case 1: {
                            state = TMSchema.State.UPHOT;
                            break;
                        }
                        case 5: {
                            state = TMSchema.State.DOWNHOT;
                            break;
                        }
                        case 7: {
                            state = TMSchema.State.LEFTHOT;
                            break;
                        }
                        case 3: {
                            state = TMSchema.State.RIGHTHOT;
                            break;
                        }
                    }
                }
                else if (b) {
                    switch (this.direction) {
                        case 1: {
                            state = TMSchema.State.UPHOVER;
                            break;
                        }
                        case 5: {
                            state = TMSchema.State.DOWNHOVER;
                            break;
                        }
                        case 7: {
                            state = TMSchema.State.LEFTHOVER;
                            break;
                        }
                        case 3: {
                            state = TMSchema.State.RIGHTHOVER;
                            break;
                        }
                    }
                }
                else {
                    switch (this.direction) {
                        case 1: {
                            state = TMSchema.State.UPNORMAL;
                            break;
                        }
                        case 5: {
                            state = TMSchema.State.DOWNNORMAL;
                            break;
                        }
                        case 7: {
                            state = TMSchema.State.LEFTNORMAL;
                            break;
                        }
                        case 3: {
                            state = TMSchema.State.RIGHTNORMAL;
                            break;
                        }
                    }
                }
                skin.paintSkin(graphics, 0, 0, this.getWidth(), this.getHeight(), state);
            }
            else {
                super.paint(graphics);
            }
        }
        
        @Override
        public Dimension getPreferredSize() {
            int n = 16;
            if (WindowsScrollBarUI.this.scrollbar != null) {
                switch (WindowsScrollBarUI.this.scrollbar.getOrientation()) {
                    case 1: {
                        n = WindowsScrollBarUI.this.scrollbar.getWidth();
                        break;
                    }
                    case 0: {
                        n = WindowsScrollBarUI.this.scrollbar.getHeight();
                        break;
                    }
                }
                n = Math.max(n, 5);
            }
            return new Dimension(n, n);
        }
    }
    
    private static class Grid
    {
        private static final int BUFFER_SIZE = 64;
        private static HashMap<String, WeakReference<Grid>> map;
        private BufferedImage image;
        
        public static Grid getGrid(final Color color, final Color color2) {
            final String string = color.getRGB() + " " + color2.getRGB();
            final WeakReference weakReference = Grid.map.get(string);
            Grid grid = (weakReference == null) ? null : ((Grid)weakReference.get());
            if (grid == null) {
                grid = new Grid(color, color2);
                Grid.map.put(string, new WeakReference<Grid>(grid));
            }
            return grid;
        }
        
        public Grid(final Color color, final Color color2) {
            this.image = new BufferedImage(64, 64, 13, new IndexColorModel(8, 2, new int[] { color.getRGB(), color2.getRGB() }, 0, false, -1, 0));
            final Graphics graphics = this.image.getGraphics();
            try {
                graphics.setClip(0, 0, 64, 64);
                this.paintGrid(graphics, color, color2);
            }
            finally {
                graphics.dispose();
            }
        }
        
        public void paint(final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Rectangle clipBounds = graphics.getClipBounds();
            final int max = Math.max(n, clipBounds.x);
            final int max2 = Math.max(n2, clipBounds.y);
            final int min = Math.min(clipBounds.x + clipBounds.width, n + n3);
            final int min2 = Math.min(clipBounds.y + clipBounds.height, n2 + n4);
            if (min <= max || min2 <= max2) {
                return;
            }
            int n5 = (max - n) % 2;
            for (int i = max; i < min; i += 64) {
                int n6 = (max2 - n2) % 2;
                final int min3 = Math.min(64 - n5, min - i);
                for (int j = max2; j < min2; j += 64) {
                    final int min4 = Math.min(64 - n6, min2 - j);
                    graphics.drawImage(this.image, i, j, i + min3, j + min4, n5, n6, n5 + min3, n6 + min4, null);
                    if (n6 != 0) {
                        j -= n6;
                        n6 = 0;
                    }
                }
                if (n5 != 0) {
                    i -= n5;
                    n5 = 0;
                }
            }
        }
        
        private void paintGrid(final Graphics graphics, final Color color, final Color color2) {
            final Rectangle clipBounds = graphics.getClipBounds();
            graphics.setColor(color2);
            graphics.fillRect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
            graphics.setColor(color);
            graphics.translate(clipBounds.x, clipBounds.y);
            int width;
            int height;
            int i;
            for (width = clipBounds.width, height = clipBounds.height, i = clipBounds.x % 2; i < width - height; i += 2) {
                graphics.drawLine(i, 0, i + height, height);
            }
            while (i < width) {
                graphics.drawLine(i, 0, width, width - i);
                i += 2;
            }
            int j;
            for (j = ((clipBounds.x % 2 == 0) ? 2 : 1); j < height - width; j += 2) {
                graphics.drawLine(0, j, width, j + width);
            }
            while (j < height) {
                graphics.drawLine(0, j, height - j, height);
                j += 2;
            }
            graphics.translate(-clipBounds.x, -clipBounds.y);
        }
        
        static {
            Grid.map = new HashMap<String, WeakReference<Grid>>();
        }
    }
}
