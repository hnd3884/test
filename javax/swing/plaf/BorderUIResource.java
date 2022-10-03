package javax.swing.plaf;

import java.awt.Font;
import javax.swing.border.TitledBorder;
import javax.swing.Icon;
import javax.swing.border.MatteBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;
import java.beans.ConstructorProperties;
import javax.swing.border.CompoundBorder;
import java.awt.Insets;
import java.awt.Graphics;
import java.awt.Component;
import java.awt.Color;
import java.io.Serializable;
import javax.swing.border.Border;

public class BorderUIResource implements Border, UIResource, Serializable
{
    static Border etched;
    static Border loweredBevel;
    static Border raisedBevel;
    static Border blackLine;
    private Border delegate;
    
    public static Border getEtchedBorderUIResource() {
        if (BorderUIResource.etched == null) {
            BorderUIResource.etched = new EtchedBorderUIResource();
        }
        return BorderUIResource.etched;
    }
    
    public static Border getLoweredBevelBorderUIResource() {
        if (BorderUIResource.loweredBevel == null) {
            BorderUIResource.loweredBevel = new BevelBorderUIResource(1);
        }
        return BorderUIResource.loweredBevel;
    }
    
    public static Border getRaisedBevelBorderUIResource() {
        if (BorderUIResource.raisedBevel == null) {
            BorderUIResource.raisedBevel = new BevelBorderUIResource(0);
        }
        return BorderUIResource.raisedBevel;
    }
    
    public static Border getBlackLineBorderUIResource() {
        if (BorderUIResource.blackLine == null) {
            BorderUIResource.blackLine = new LineBorderUIResource(Color.black);
        }
        return BorderUIResource.blackLine;
    }
    
    public BorderUIResource(final Border delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("null border delegate argument");
        }
        this.delegate = delegate;
    }
    
    @Override
    public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        this.delegate.paintBorder(component, graphics, n, n2, n3, n4);
    }
    
    @Override
    public Insets getBorderInsets(final Component component) {
        return this.delegate.getBorderInsets(component);
    }
    
    @Override
    public boolean isBorderOpaque() {
        return this.delegate.isBorderOpaque();
    }
    
    public static class CompoundBorderUIResource extends CompoundBorder implements UIResource
    {
        @ConstructorProperties({ "outsideBorder", "insideBorder" })
        public CompoundBorderUIResource(final Border border, final Border border2) {
            super(border, border2);
        }
    }
    
    public static class EmptyBorderUIResource extends EmptyBorder implements UIResource
    {
        public EmptyBorderUIResource(final int n, final int n2, final int n3, final int n4) {
            super(n, n2, n3, n4);
        }
        
        @ConstructorProperties({ "borderInsets" })
        public EmptyBorderUIResource(final Insets insets) {
            super(insets);
        }
    }
    
    public static class LineBorderUIResource extends LineBorder implements UIResource
    {
        public LineBorderUIResource(final Color color) {
            super(color);
        }
        
        @ConstructorProperties({ "lineColor", "thickness" })
        public LineBorderUIResource(final Color color, final int n) {
            super(color, n);
        }
    }
    
    public static class BevelBorderUIResource extends BevelBorder implements UIResource
    {
        public BevelBorderUIResource(final int n) {
            super(n);
        }
        
        public BevelBorderUIResource(final int n, final Color color, final Color color2) {
            super(n, color, color2);
        }
        
        @ConstructorProperties({ "bevelType", "highlightOuterColor", "highlightInnerColor", "shadowOuterColor", "shadowInnerColor" })
        public BevelBorderUIResource(final int n, final Color color, final Color color2, final Color color3, final Color color4) {
            super(n, color, color2, color3, color4);
        }
    }
    
    public static class EtchedBorderUIResource extends EtchedBorder implements UIResource
    {
        public EtchedBorderUIResource() {
        }
        
        public EtchedBorderUIResource(final int n) {
            super(n);
        }
        
        public EtchedBorderUIResource(final Color color, final Color color2) {
            super(color, color2);
        }
        
        @ConstructorProperties({ "etchType", "highlightColor", "shadowColor" })
        public EtchedBorderUIResource(final int n, final Color color, final Color color2) {
            super(n, color, color2);
        }
    }
    
    public static class MatteBorderUIResource extends MatteBorder implements UIResource
    {
        public MatteBorderUIResource(final int n, final int n2, final int n3, final int n4, final Color color) {
            super(n, n2, n3, n4, color);
        }
        
        public MatteBorderUIResource(final int n, final int n2, final int n3, final int n4, final Icon icon) {
            super(n, n2, n3, n4, icon);
        }
        
        public MatteBorderUIResource(final Icon icon) {
            super(icon);
        }
    }
    
    public static class TitledBorderUIResource extends TitledBorder implements UIResource
    {
        public TitledBorderUIResource(final String s) {
            super(s);
        }
        
        public TitledBorderUIResource(final Border border) {
            super(border);
        }
        
        public TitledBorderUIResource(final Border border, final String s) {
            super(border, s);
        }
        
        public TitledBorderUIResource(final Border border, final String s, final int n, final int n2) {
            super(border, s, n, n2);
        }
        
        public TitledBorderUIResource(final Border border, final String s, final int n, final int n2, final Font font) {
            super(border, s, n, n2, font);
        }
        
        @ConstructorProperties({ "border", "title", "titleJustification", "titlePosition", "titleFont", "titleColor" })
        public TitledBorderUIResource(final Border border, final String s, final int n, final int n2, final Font font, final Color color) {
            super(border, s, n, n2, font, color);
        }
    }
}
