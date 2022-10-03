package com.sun.java.swing.plaf.windows;

import javax.swing.JButton;
import java.awt.GraphicsConfiguration;
import java.awt.image.DataBuffer;
import sun.awt.image.SunWritableRaster;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.Image;
import sun.swing.CachedPainter;
import javax.swing.SwingUtilities;
import javax.swing.CellRendererPane;
import java.awt.Rectangle;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.border.AbstractBorder;
import javax.swing.JComponent;
import javax.swing.JComboBox;
import java.awt.Graphics;
import javax.swing.text.JTextComponent;
import javax.swing.JToolBar;
import javax.swing.AbstractButton;
import javax.swing.plaf.UIResource;
import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Component;
import javax.swing.UIManager;
import sun.awt.windows.ThemeReader;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.awt.Toolkit;
import java.awt.Color;
import javax.swing.border.Border;
import java.util.HashMap;

class XPStyle
{
    private static XPStyle xp;
    private static SkinPainter skinPainter;
    private static Boolean themeActive;
    private HashMap<String, Border> borderMap;
    private HashMap<String, Color> colorMap;
    private boolean flatMenus;
    
    static synchronized void invalidateStyle() {
        XPStyle.xp = null;
        XPStyle.themeActive = null;
        XPStyle.skinPainter.flush();
    }
    
    static synchronized XPStyle getXP() {
        if (XPStyle.themeActive == null) {
            XPStyle.themeActive = (Boolean)Toolkit.getDefaultToolkit().getDesktopProperty("win.xpstyle.themeActive");
            if (XPStyle.themeActive == null) {
                XPStyle.themeActive = Boolean.FALSE;
            }
            if (XPStyle.themeActive && AccessController.doPrivileged((PrivilegedAction<Object>)new GetPropertyAction("swing.noxp")) == null && ThemeReader.isThemed() && !(UIManager.getLookAndFeel() instanceof WindowsClassicLookAndFeel)) {
                XPStyle.xp = new XPStyle();
            }
        }
        return ThemeReader.isXPStyleEnabled() ? XPStyle.xp : null;
    }
    
    static boolean isVista() {
        final XPStyle xp = getXP();
        return xp != null && xp.isSkinDefined(null, TMSchema.Part.CP_DROPDOWNBUTTONRIGHT);
    }
    
    String getString(final Component component, final TMSchema.Part part, final TMSchema.State state, final TMSchema.Prop prop) {
        return getTypeEnumName(component, part, state, prop);
    }
    
    TMSchema.TypeEnum getTypeEnum(final Component component, final TMSchema.Part part, final TMSchema.State state, final TMSchema.Prop prop) {
        return TMSchema.TypeEnum.getTypeEnum(prop, ThemeReader.getEnum(part.getControlName(component), part.getValue(), TMSchema.State.getValue(part, state), prop.getValue()));
    }
    
    private static String getTypeEnumName(final Component component, final TMSchema.Part part, final TMSchema.State state, final TMSchema.Prop prop) {
        final int enum1 = ThemeReader.getEnum(part.getControlName(component), part.getValue(), TMSchema.State.getValue(part, state), prop.getValue());
        if (enum1 == -1) {
            return null;
        }
        return TMSchema.TypeEnum.getTypeEnum(prop, enum1).getName();
    }
    
    int getInt(final Component component, final TMSchema.Part part, final TMSchema.State state, final TMSchema.Prop prop, final int n) {
        return ThemeReader.getInt(part.getControlName(component), part.getValue(), TMSchema.State.getValue(part, state), prop.getValue());
    }
    
    Dimension getDimension(final Component component, final TMSchema.Part part, final TMSchema.State state, final TMSchema.Prop prop) {
        final Dimension position = ThemeReader.getPosition(part.getControlName(component), part.getValue(), TMSchema.State.getValue(part, state), prop.getValue());
        return (position != null) ? position : new Dimension();
    }
    
    Point getPoint(final Component component, final TMSchema.Part part, final TMSchema.State state, final TMSchema.Prop prop) {
        final Dimension position = ThemeReader.getPosition(part.getControlName(component), part.getValue(), TMSchema.State.getValue(part, state), prop.getValue());
        return (position != null) ? new Point(position.width, position.height) : new Point();
    }
    
    Insets getMargin(final Component component, final TMSchema.Part part, final TMSchema.State state, final TMSchema.Prop prop) {
        final Insets themeMargins = ThemeReader.getThemeMargins(part.getControlName(component), part.getValue(), TMSchema.State.getValue(part, state), prop.getValue());
        return (themeMargins != null) ? themeMargins : new Insets(0, 0, 0, 0);
    }
    
    synchronized Color getColor(final Skin skin, final TMSchema.Prop prop, final Color color) {
        final String string = skin.toString() + "." + prop.name();
        final TMSchema.Part part = skin.part;
        Color color2 = this.colorMap.get(string);
        if (color2 == null) {
            color2 = ThemeReader.getColor(part.getControlName(null), part.getValue(), TMSchema.State.getValue(part, skin.state), prop.getValue());
            if (color2 != null) {
                color2 = new ColorUIResource(color2);
                this.colorMap.put(string, color2);
            }
        }
        return (color2 != null) ? color2 : color;
    }
    
    Color getColor(final Component component, final TMSchema.Part part, final TMSchema.State state, final TMSchema.Prop prop, final Color color) {
        return this.getColor(new Skin(component, part, state), prop, color);
    }
    
    synchronized Border getBorder(final Component component, final TMSchema.Part part) {
        if (part != TMSchema.Part.MENU) {
            final Skin skin = new Skin(component, part, null);
            Border border = this.borderMap.get(skin.string);
            if (border == null) {
                final String typeEnumName = getTypeEnumName(component, part, null, TMSchema.Prop.BGTYPE);
                if ("borderfill".equalsIgnoreCase(typeEnumName)) {
                    final int int1 = this.getInt(component, part, null, TMSchema.Prop.BORDERSIZE, 1);
                    final Color color = this.getColor(skin, TMSchema.Prop.BORDERCOLOR, Color.black);
                    border = new XPFillBorder(color, int1);
                    if (part == TMSchema.Part.CP_COMBOBOX) {
                        border = new XPStatefulFillBorder(color, int1, part, TMSchema.Prop.BORDERCOLOR);
                    }
                }
                else if ("imagefile".equalsIgnoreCase(typeEnumName)) {
                    final Insets margin = this.getMargin(component, part, null, TMSchema.Prop.SIZINGMARGINS);
                    if (margin != null) {
                        if (this.getBoolean(component, part, null, TMSchema.Prop.BORDERONLY)) {
                            border = new XPImageBorder(component, part);
                        }
                        else if (part == TMSchema.Part.CP_COMBOBOX) {
                            border = new EmptyBorder(1, 1, 1, 1);
                        }
                        else if (part == TMSchema.Part.TP_BUTTON) {
                            border = new XPEmptyBorder(new Insets(3, 3, 3, 3));
                        }
                        else {
                            border = new XPEmptyBorder(margin);
                        }
                    }
                }
                if (border != null) {
                    this.borderMap.put(skin.string, border);
                }
            }
            return border;
        }
        if (this.flatMenus) {
            return new XPFillBorder(UIManager.getColor("InternalFrame.borderShadow"), 1);
        }
        return null;
    }
    
    boolean isSkinDefined(final Component component, final TMSchema.Part part) {
        return part.getValue() == 0 || ThemeReader.isThemePartDefined(part.getControlName(component), part.getValue(), 0);
    }
    
    synchronized Skin getSkin(final Component component, final TMSchema.Part part) {
        assert this.isSkinDefined(component, part) : "part " + part + " is not defined";
        return new Skin(component, part, null);
    }
    
    long getThemeTransitionDuration(final Component component, final TMSchema.Part part, final TMSchema.State state, final TMSchema.State state2, final TMSchema.Prop prop) {
        return ThemeReader.getThemeTransitionDuration(part.getControlName(component), part.getValue(), TMSchema.State.getValue(part, state), TMSchema.State.getValue(part, state2), (prop != null) ? prop.getValue() : 0);
    }
    
    private XPStyle() {
        this.flatMenus = getSysBoolean(TMSchema.Prop.FLATMENUS);
        this.colorMap = new HashMap<String, Color>();
        this.borderMap = new HashMap<String, Border>();
    }
    
    private boolean getBoolean(final Component component, final TMSchema.Part part, final TMSchema.State state, final TMSchema.Prop prop) {
        return ThemeReader.getBoolean(part.getControlName(component), part.getValue(), TMSchema.State.getValue(part, state), prop.getValue());
    }
    
    static Dimension getPartSize(final TMSchema.Part part, final TMSchema.State state) {
        return ThemeReader.getPartSize(part.getControlName(null), part.getValue(), TMSchema.State.getValue(part, state));
    }
    
    private static boolean getSysBoolean(final TMSchema.Prop prop) {
        return ThemeReader.getSysBoolean("window", prop.getValue());
    }
    
    static {
        XPStyle.skinPainter = new SkinPainter();
        XPStyle.themeActive = null;
        invalidateStyle();
    }
    
    private class XPFillBorder extends LineBorder implements UIResource
    {
        XPFillBorder(final Color color, final int n) {
            super(color, n);
        }
        
        @Override
        public Insets getBorderInsets(final Component component, final Insets insets) {
            Insets insets2 = null;
            if (component instanceof AbstractButton) {
                insets2 = ((AbstractButton)component).getMargin();
            }
            else if (component instanceof JToolBar) {
                insets2 = ((JToolBar)component).getMargin();
            }
            else if (component instanceof JTextComponent) {
                insets2 = ((JTextComponent)component).getMargin();
            }
            insets.top = ((insets2 != null) ? insets2.top : 0) + this.thickness;
            insets.left = ((insets2 != null) ? insets2.left : 0) + this.thickness;
            insets.bottom = ((insets2 != null) ? insets2.bottom : 0) + this.thickness;
            insets.right = ((insets2 != null) ? insets2.right : 0) + this.thickness;
            return insets;
        }
    }
    
    private class XPStatefulFillBorder extends XPFillBorder
    {
        private final TMSchema.Part part;
        private final TMSchema.Prop prop;
        
        XPStatefulFillBorder(final Color color, final int n, final TMSchema.Part part, final TMSchema.Prop prop) {
            super(color, n);
            this.part = part;
            this.prop = prop;
        }
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            TMSchema.State state = TMSchema.State.NORMAL;
            if (component instanceof JComboBox) {
                final JComboBox comboBox = (JComboBox)component;
                if (comboBox.getUI() instanceof WindowsComboBoxUI) {
                    state = ((WindowsComboBoxUI)comboBox.getUI()).getXPComboBoxState(comboBox);
                }
            }
            this.lineColor = XPStyle.this.getColor(component, this.part, state, this.prop, Color.black);
            super.paintBorder(component, graphics, n, n2, n3, n4);
        }
    }
    
    private class XPImageBorder extends AbstractBorder implements UIResource
    {
        Skin skin;
        
        XPImageBorder(final Component component, final TMSchema.Part part) {
            this.skin = XPStyle.this.getSkin(component, part);
        }
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            this.skin.paintSkin(graphics, n, n2, n3, n4, null);
        }
        
        @Override
        public Insets getBorderInsets(final Component component, final Insets insets) {
            Insets insets2 = null;
            Insets contentMargin = this.skin.getContentMargin();
            if (contentMargin == null) {
                contentMargin = new Insets(0, 0, 0, 0);
            }
            if (component instanceof AbstractButton) {
                insets2 = ((AbstractButton)component).getMargin();
            }
            else if (component instanceof JToolBar) {
                insets2 = ((JToolBar)component).getMargin();
            }
            else if (component instanceof JTextComponent) {
                insets2 = ((JTextComponent)component).getMargin();
            }
            insets.top = ((insets2 != null) ? insets2.top : 0) + contentMargin.top;
            insets.left = ((insets2 != null) ? insets2.left : 0) + contentMargin.left;
            insets.bottom = ((insets2 != null) ? insets2.bottom : 0) + contentMargin.bottom;
            insets.right = ((insets2 != null) ? insets2.right : 0) + contentMargin.right;
            return insets;
        }
    }
    
    private class XPEmptyBorder extends EmptyBorder implements UIResource
    {
        XPEmptyBorder(final Insets insets) {
            super(insets.top + 2, insets.left + 2, insets.bottom + 2, insets.right + 2);
        }
        
        @Override
        public Insets getBorderInsets(final Component component, Insets borderInsets) {
            borderInsets = super.getBorderInsets(component, borderInsets);
            Insets insets = null;
            if (component instanceof AbstractButton) {
                final Insets margin = ((AbstractButton)component).getMargin();
                if (component.getParent() instanceof JToolBar && !(component instanceof JRadioButton) && !(component instanceof JCheckBox) && margin instanceof InsetsUIResource) {
                    final Insets insets2 = borderInsets;
                    insets2.top -= 2;
                    final Insets insets3 = borderInsets;
                    insets3.left -= 2;
                    final Insets insets4 = borderInsets;
                    insets4.bottom -= 2;
                    final Insets insets5 = borderInsets;
                    insets5.right -= 2;
                }
                else {
                    insets = margin;
                }
            }
            else if (component instanceof JToolBar) {
                insets = ((JToolBar)component).getMargin();
            }
            else if (component instanceof JTextComponent) {
                insets = ((JTextComponent)component).getMargin();
            }
            if (insets != null) {
                borderInsets.top = insets.top + 2;
                borderInsets.left = insets.left + 2;
                borderInsets.bottom = insets.bottom + 2;
                borderInsets.right = insets.right + 2;
            }
            return borderInsets;
        }
    }
    
    static class Skin
    {
        final Component component;
        final TMSchema.Part part;
        final TMSchema.State state;
        private final String string;
        private Dimension size;
        
        Skin(final Component component, final TMSchema.Part part) {
            this(component, part, null);
        }
        
        Skin(final TMSchema.Part part, final TMSchema.State state) {
            this(null, part, state);
        }
        
        Skin(final Component component, final TMSchema.Part part, final TMSchema.State state) {
            this.size = null;
            this.component = component;
            this.part = part;
            this.state = state;
            String string = part.getControlName(component) + "." + part.name();
            if (state != null) {
                string = string + "(" + state.name() + ")";
            }
            this.string = string;
        }
        
        Insets getContentMargin() {
            final Insets themeBackgroundContentMargins = ThemeReader.getThemeBackgroundContentMargins(this.part.getControlName(null), this.part.getValue(), 0, 100, 100);
            return (themeBackgroundContentMargins != null) ? themeBackgroundContentMargins : new Insets(0, 0, 0, 0);
        }
        
        private int getWidth(final TMSchema.State state) {
            if (this.size == null) {
                this.size = XPStyle.getPartSize(this.part, state);
            }
            return (this.size != null) ? this.size.width : 0;
        }
        
        int getWidth() {
            return this.getWidth((this.state != null) ? this.state : TMSchema.State.NORMAL);
        }
        
        private int getHeight(final TMSchema.State state) {
            if (this.size == null) {
                this.size = XPStyle.getPartSize(this.part, state);
            }
            return (this.size != null) ? this.size.height : 0;
        }
        
        int getHeight() {
            return this.getHeight((this.state != null) ? this.state : TMSchema.State.NORMAL);
        }
        
        @Override
        public String toString() {
            return this.string;
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof Skin && ((Skin)o).string.equals(this.string);
        }
        
        @Override
        public int hashCode() {
            return this.string.hashCode();
        }
        
        void paintSkin(final Graphics graphics, final int n, final int n2, TMSchema.State state) {
            if (state == null) {
                state = this.state;
            }
            this.paintSkin(graphics, n, n2, this.getWidth(state), this.getHeight(state), state);
        }
        
        void paintSkin(final Graphics graphics, final Rectangle rectangle, final TMSchema.State state) {
            this.paintSkin(graphics, rectangle.x, rectangle.y, rectangle.width, rectangle.height, state);
        }
        
        void paintSkin(final Graphics graphics, final int n, final int n2, final int n3, final int n4, final TMSchema.State state) {
            if (XPStyle.getXP() == null) {
                return;
            }
            if (ThemeReader.isGetThemeTransitionDurationDefined() && this.component instanceof JComponent && SwingUtilities.getAncestorOfClass(CellRendererPane.class, this.component) == null) {
                AnimationController.paintSkin((JComponent)this.component, this, graphics, n, n2, n3, n4, state);
            }
            else {
                this.paintSkinRaw(graphics, n, n2, n3, n4, state);
            }
        }
        
        void paintSkinRaw(final Graphics graphics, final int n, final int n2, final int n3, final int n4, final TMSchema.State state) {
            if (XPStyle.getXP() == null) {
                return;
            }
            XPStyle.skinPainter.paint(null, graphics, n, n2, n3, n4, this, state);
        }
        
        void paintSkin(final Graphics graphics, final int n, final int n2, final int n3, final int n4, final TMSchema.State state, final boolean b) {
            if (XPStyle.getXP() == null) {
                return;
            }
            if (b && "borderfill".equals(getTypeEnumName(this.component, this.part, state, TMSchema.Prop.BGTYPE))) {
                return;
            }
            XPStyle.skinPainter.paint(null, graphics, n, n2, n3, n4, this, state);
        }
    }
    
    private static class SkinPainter extends CachedPainter
    {
        SkinPainter() {
            super(30);
            this.flush();
        }
        
        public void flush() {
            super.flush();
        }
        
        @Override
        protected void paintToImage(Component component, final Image image, final Graphics graphics, final int n, final int n2, final Object[] array) {
            final Skin skin = (Skin)array[0];
            final TMSchema.Part part = skin.part;
            TMSchema.State state = (TMSchema.State)array[1];
            if (state == null) {
                state = skin.state;
            }
            if (component == null) {
                component = skin.component;
            }
            final DataBufferInt dataBufferInt = (DataBufferInt)((BufferedImage)image).getRaster().getDataBuffer();
            ThemeReader.paintBackground(SunWritableRaster.stealData(dataBufferInt, 0), part.getControlName(component), part.getValue(), TMSchema.State.getValue(part, state), 0, 0, n, n2, n);
            SunWritableRaster.markDirty(dataBufferInt);
        }
        
        @Override
        protected Image createImage(final Component component, final int n, final int n2, final GraphicsConfiguration graphicsConfiguration, final Object[] array) {
            return new BufferedImage(n, n2, 2);
        }
    }
    
    static class GlyphButton extends JButton
    {
        private Skin skin;
        
        public GlyphButton(final Component component, final TMSchema.Part part) {
            final XPStyle xp = XPStyle.getXP();
            this.skin = ((xp != null) ? xp.getSkin(component, part) : null);
            this.setBorder(null);
            this.setContentAreaFilled(false);
            this.setMinimumSize(new Dimension(5, 5));
            this.setPreferredSize(new Dimension(16, 16));
            this.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        }
        
        @Override
        public boolean isFocusTraversable() {
            return false;
        }
        
        protected TMSchema.State getState() {
            TMSchema.State state = TMSchema.State.NORMAL;
            if (!this.isEnabled()) {
                state = TMSchema.State.DISABLED;
            }
            else if (this.getModel().isPressed()) {
                state = TMSchema.State.PRESSED;
            }
            else if (this.getModel().isRollover()) {
                state = TMSchema.State.HOT;
            }
            return state;
        }
        
        public void paintComponent(final Graphics graphics) {
            if (XPStyle.getXP() == null || this.skin == null) {
                return;
            }
            final Dimension size = this.getSize();
            this.skin.paintSkin(graphics, 0, 0, size.width, size.height, this.getState());
        }
        
        public void setPart(final Component component, final TMSchema.Part part) {
            final XPStyle xp = XPStyle.getXP();
            this.skin = ((xp != null) ? xp.getSkin(component, part) : null);
            this.revalidate();
            this.repaint();
        }
        
        @Override
        protected void paintBorder(final Graphics graphics) {
        }
    }
}
