package com.sun.java.swing.plaf.windows;

import java.awt.event.MouseEvent;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;

public class WindowsSliderUI extends BasicSliderUI
{
    private boolean rollover;
    private boolean pressed;
    
    public WindowsSliderUI(final JSlider slider) {
        super(slider);
        this.rollover = false;
        this.pressed = false;
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new WindowsSliderUI((JSlider)component);
    }
    
    @Override
    protected TrackListener createTrackListener(final JSlider slider) {
        return new WindowsTrackListener();
    }
    
    @Override
    public void paintTrack(final Graphics graphics) {
        final XPStyle xp = XPStyle.getXP();
        if (xp != null) {
            final boolean b = this.slider.getOrientation() == 1;
            final XPStyle.Skin skin = xp.getSkin(this.slider, b ? TMSchema.Part.TKP_TRACKVERT : TMSchema.Part.TKP_TRACK);
            if (b) {
                skin.paintSkin(graphics, this.trackRect.x + (this.trackRect.width - skin.getWidth()) / 2, this.trackRect.y, skin.getWidth(), this.trackRect.height, null);
            }
            else {
                skin.paintSkin(graphics, this.trackRect.x, this.trackRect.y + (this.trackRect.height - skin.getHeight()) / 2, this.trackRect.width, skin.getHeight(), null);
            }
        }
        else {
            super.paintTrack(graphics);
        }
    }
    
    @Override
    protected void paintMinorTickForHorizSlider(final Graphics graphics, final Rectangle rectangle, final int n) {
        final XPStyle xp = XPStyle.getXP();
        if (xp != null) {
            graphics.setColor(xp.getColor(this.slider, TMSchema.Part.TKP_TICS, null, TMSchema.Prop.COLOR, Color.black));
        }
        super.paintMinorTickForHorizSlider(graphics, rectangle, n);
    }
    
    @Override
    protected void paintMajorTickForHorizSlider(final Graphics graphics, final Rectangle rectangle, final int n) {
        final XPStyle xp = XPStyle.getXP();
        if (xp != null) {
            graphics.setColor(xp.getColor(this.slider, TMSchema.Part.TKP_TICS, null, TMSchema.Prop.COLOR, Color.black));
        }
        super.paintMajorTickForHorizSlider(graphics, rectangle, n);
    }
    
    @Override
    protected void paintMinorTickForVertSlider(final Graphics graphics, final Rectangle rectangle, final int n) {
        final XPStyle xp = XPStyle.getXP();
        if (xp != null) {
            graphics.setColor(xp.getColor(this.slider, TMSchema.Part.TKP_TICSVERT, null, TMSchema.Prop.COLOR, Color.black));
        }
        super.paintMinorTickForVertSlider(graphics, rectangle, n);
    }
    
    @Override
    protected void paintMajorTickForVertSlider(final Graphics graphics, final Rectangle rectangle, final int n) {
        final XPStyle xp = XPStyle.getXP();
        if (xp != null) {
            graphics.setColor(xp.getColor(this.slider, TMSchema.Part.TKP_TICSVERT, null, TMSchema.Prop.COLOR, Color.black));
        }
        super.paintMajorTickForVertSlider(graphics, rectangle, n);
    }
    
    @Override
    public void paintThumb(final Graphics graphics) {
        final XPStyle xp = XPStyle.getXP();
        if (xp != null) {
            final TMSchema.Part xpThumbPart = this.getXPThumbPart();
            TMSchema.State state = TMSchema.State.NORMAL;
            if (this.slider.hasFocus()) {
                state = TMSchema.State.FOCUSED;
            }
            if (this.rollover) {
                state = TMSchema.State.HOT;
            }
            if (this.pressed) {
                state = TMSchema.State.PRESSED;
            }
            if (!this.slider.isEnabled()) {
                state = TMSchema.State.DISABLED;
            }
            xp.getSkin(this.slider, xpThumbPart).paintSkin(graphics, this.thumbRect.x, this.thumbRect.y, state);
        }
        else {
            super.paintThumb(graphics);
        }
    }
    
    @Override
    protected Dimension getThumbSize() {
        final XPStyle xp = XPStyle.getXP();
        if (xp != null) {
            final Dimension dimension = new Dimension();
            final XPStyle.Skin skin = xp.getSkin(this.slider, this.getXPThumbPart());
            dimension.width = skin.getWidth();
            dimension.height = skin.getHeight();
            return dimension;
        }
        return super.getThumbSize();
    }
    
    private TMSchema.Part getXPThumbPart() {
        final boolean b = this.slider.getOrientation() == 1;
        final boolean leftToRight = this.slider.getComponentOrientation().isLeftToRight();
        final Boolean b2 = (Boolean)this.slider.getClientProperty("Slider.paintThumbArrowShape");
        TMSchema.Part part;
        if ((!this.slider.getPaintTicks() && b2 == null) || b2 == Boolean.FALSE) {
            part = (b ? TMSchema.Part.TKP_THUMBVERT : TMSchema.Part.TKP_THUMB);
        }
        else {
            part = (b ? (leftToRight ? TMSchema.Part.TKP_THUMBRIGHT : TMSchema.Part.TKP_THUMBLEFT) : TMSchema.Part.TKP_THUMBBOTTOM);
        }
        return part;
    }
    
    private class WindowsTrackListener extends TrackListener
    {
        @Override
        public void mouseMoved(final MouseEvent mouseEvent) {
            this.updateRollover(WindowsSliderUI.this.thumbRect.contains(mouseEvent.getX(), mouseEvent.getY()));
            super.mouseMoved(mouseEvent);
        }
        
        @Override
        public void mouseEntered(final MouseEvent mouseEvent) {
            this.updateRollover(WindowsSliderUI.this.thumbRect.contains(mouseEvent.getX(), mouseEvent.getY()));
            super.mouseEntered(mouseEvent);
        }
        
        @Override
        public void mouseExited(final MouseEvent mouseEvent) {
            this.updateRollover(false);
            super.mouseExited(mouseEvent);
        }
        
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            this.updatePressed(WindowsSliderUI.this.thumbRect.contains(mouseEvent.getX(), mouseEvent.getY()));
            super.mousePressed(mouseEvent);
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
            this.updatePressed(false);
            super.mouseReleased(mouseEvent);
        }
        
        public void updatePressed(final boolean b) {
            if (!WindowsSliderUI.this.slider.isEnabled()) {
                return;
            }
            if (WindowsSliderUI.this.pressed != b) {
                WindowsSliderUI.this.pressed = b;
                WindowsSliderUI.this.slider.repaint(WindowsSliderUI.this.thumbRect);
            }
        }
        
        public void updateRollover(final boolean b) {
            if (!WindowsSliderUI.this.slider.isEnabled()) {
                return;
            }
            if (WindowsSliderUI.this.rollover != b) {
                WindowsSliderUI.this.rollover = b;
                WindowsSliderUI.this.slider.repaint(WindowsSliderUI.this.thumbRect);
            }
        }
    }
}
