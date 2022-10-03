package javax.swing.plaf.basic;

import java.awt.Rectangle;
import java.awt.Dimension;
import javax.swing.JSplitPane;
import javax.swing.text.JTextComponent;
import javax.swing.JToolBar;
import sun.swing.SwingUtilities2;
import javax.swing.JButton;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;
import java.awt.Insets;
import javax.swing.border.EmptyBorder;
import java.awt.Container;
import javax.swing.ButtonModel;
import javax.swing.AbstractButton;
import java.awt.Graphics;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;
import java.awt.Color;
import javax.swing.UIDefaults;
import javax.swing.plaf.BorderUIResource;
import javax.swing.UIManager;
import javax.swing.border.Border;

public class BasicBorders
{
    public static Border getButtonBorder() {
        final UIDefaults lookAndFeelDefaults = UIManager.getLookAndFeelDefaults();
        return new BorderUIResource.CompoundBorderUIResource(new ButtonBorder(lookAndFeelDefaults.getColor("Button.shadow"), lookAndFeelDefaults.getColor("Button.darkShadow"), lookAndFeelDefaults.getColor("Button.light"), lookAndFeelDefaults.getColor("Button.highlight")), new MarginBorder());
    }
    
    public static Border getRadioButtonBorder() {
        final UIDefaults lookAndFeelDefaults = UIManager.getLookAndFeelDefaults();
        return new BorderUIResource.CompoundBorderUIResource(new RadioButtonBorder(lookAndFeelDefaults.getColor("RadioButton.shadow"), lookAndFeelDefaults.getColor("RadioButton.darkShadow"), lookAndFeelDefaults.getColor("RadioButton.light"), lookAndFeelDefaults.getColor("RadioButton.highlight")), new MarginBorder());
    }
    
    public static Border getToggleButtonBorder() {
        final UIDefaults lookAndFeelDefaults = UIManager.getLookAndFeelDefaults();
        return new BorderUIResource.CompoundBorderUIResource(new ToggleButtonBorder(lookAndFeelDefaults.getColor("ToggleButton.shadow"), lookAndFeelDefaults.getColor("ToggleButton.darkShadow"), lookAndFeelDefaults.getColor("ToggleButton.light"), lookAndFeelDefaults.getColor("ToggleButton.highlight")), new MarginBorder());
    }
    
    public static Border getMenuBarBorder() {
        final UIDefaults lookAndFeelDefaults = UIManager.getLookAndFeelDefaults();
        return new MenuBarBorder(lookAndFeelDefaults.getColor("MenuBar.shadow"), lookAndFeelDefaults.getColor("MenuBar.highlight"));
    }
    
    public static Border getSplitPaneBorder() {
        final UIDefaults lookAndFeelDefaults = UIManager.getLookAndFeelDefaults();
        return new SplitPaneBorder(lookAndFeelDefaults.getColor("SplitPane.highlight"), lookAndFeelDefaults.getColor("SplitPane.darkShadow"));
    }
    
    public static Border getSplitPaneDividerBorder() {
        final UIDefaults lookAndFeelDefaults = UIManager.getLookAndFeelDefaults();
        return new SplitPaneDividerBorder(lookAndFeelDefaults.getColor("SplitPane.highlight"), lookAndFeelDefaults.getColor("SplitPane.darkShadow"));
    }
    
    public static Border getTextFieldBorder() {
        final UIDefaults lookAndFeelDefaults = UIManager.getLookAndFeelDefaults();
        return new FieldBorder(lookAndFeelDefaults.getColor("TextField.shadow"), lookAndFeelDefaults.getColor("TextField.darkShadow"), lookAndFeelDefaults.getColor("TextField.light"), lookAndFeelDefaults.getColor("TextField.highlight"));
    }
    
    public static Border getProgressBarBorder() {
        UIManager.getLookAndFeelDefaults();
        return new BorderUIResource.LineBorderUIResource(Color.green, 2);
    }
    
    public static Border getInternalFrameBorder() {
        final UIDefaults lookAndFeelDefaults = UIManager.getLookAndFeelDefaults();
        return new BorderUIResource.CompoundBorderUIResource(new BevelBorder(0, lookAndFeelDefaults.getColor("InternalFrame.borderLight"), lookAndFeelDefaults.getColor("InternalFrame.borderHighlight"), lookAndFeelDefaults.getColor("InternalFrame.borderDarkShadow"), lookAndFeelDefaults.getColor("InternalFrame.borderShadow")), BorderFactory.createLineBorder(lookAndFeelDefaults.getColor("InternalFrame.borderColor"), 1));
    }
    
    public static class RolloverButtonBorder extends ButtonBorder
    {
        public RolloverButtonBorder(final Color color, final Color color2, final Color color3, final Color color4) {
            super(color, color2, color3, color4);
        }
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final AbstractButton abstractButton = (AbstractButton)component;
            final ButtonModel model = abstractButton.getModel();
            Color color = this.shadow;
            final Container parent = abstractButton.getParent();
            if (parent != null && parent.getBackground().equals(this.shadow)) {
                color = this.darkShadow;
            }
            if ((model.isRollover() && (!model.isPressed() || model.isArmed())) || model.isSelected()) {
                final Color color2 = graphics.getColor();
                graphics.translate(n, n2);
                if ((model.isPressed() && model.isArmed()) || model.isSelected()) {
                    graphics.setColor(color);
                    graphics.drawRect(0, 0, n3 - 1, n4 - 1);
                    graphics.setColor(this.lightHighlight);
                    graphics.drawLine(n3 - 1, 0, n3 - 1, n4 - 1);
                    graphics.drawLine(0, n4 - 1, n3 - 1, n4 - 1);
                }
                else {
                    graphics.setColor(this.lightHighlight);
                    graphics.drawRect(0, 0, n3 - 1, n4 - 1);
                    graphics.setColor(color);
                    graphics.drawLine(n3 - 1, 0, n3 - 1, n4 - 1);
                    graphics.drawLine(0, n4 - 1, n3 - 1, n4 - 1);
                }
                graphics.translate(-n, -n2);
                graphics.setColor(color2);
            }
        }
    }
    
    static class RolloverMarginBorder extends EmptyBorder
    {
        public RolloverMarginBorder() {
            super(3, 3, 3, 3);
        }
        
        @Override
        public Insets getBorderInsets(final Component component, final Insets insets) {
            Insets margin = null;
            if (component instanceof AbstractButton) {
                margin = ((AbstractButton)component).getMargin();
            }
            if (margin == null || margin instanceof UIResource) {
                insets.left = this.left;
                insets.top = this.top;
                insets.right = this.right;
                insets.bottom = this.bottom;
            }
            else {
                insets.left = margin.left;
                insets.top = margin.top;
                insets.right = margin.right;
                insets.bottom = margin.bottom;
            }
            return insets;
        }
    }
    
    public static class ButtonBorder extends AbstractBorder implements UIResource
    {
        protected Color shadow;
        protected Color darkShadow;
        protected Color highlight;
        protected Color lightHighlight;
        
        public ButtonBorder(final Color shadow, final Color darkShadow, final Color highlight, final Color lightHighlight) {
            this.shadow = shadow;
            this.darkShadow = darkShadow;
            this.highlight = highlight;
            this.lightHighlight = lightHighlight;
        }
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            boolean b = false;
            boolean defaultButton = false;
            if (component instanceof AbstractButton) {
                final ButtonModel model = ((AbstractButton)component).getModel();
                b = (model.isPressed() && model.isArmed());
                if (component instanceof JButton) {
                    defaultButton = ((JButton)component).isDefaultButton();
                }
            }
            BasicGraphicsUtils.drawBezel(graphics, n, n2, n3, n4, b, defaultButton, this.shadow, this.darkShadow, this.highlight, this.lightHighlight);
        }
        
        @Override
        public Insets getBorderInsets(final Component component, final Insets insets) {
            insets.set(2, 3, 3, 3);
            return insets;
        }
    }
    
    public static class ToggleButtonBorder extends ButtonBorder
    {
        public ToggleButtonBorder(final Color color, final Color color2, final Color color3, final Color color4) {
            super(color, color2, color3, color4);
        }
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            BasicGraphicsUtils.drawBezel(graphics, n, n2, n3, n4, false, false, this.shadow, this.darkShadow, this.highlight, this.lightHighlight);
        }
        
        @Override
        public Insets getBorderInsets(final Component component, final Insets insets) {
            insets.set(2, 2, 2, 2);
            return insets;
        }
    }
    
    public static class RadioButtonBorder extends ButtonBorder
    {
        public RadioButtonBorder(final Color color, final Color color2, final Color color3, final Color color4) {
            super(color, color2, color3, color4);
        }
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            if (component instanceof AbstractButton) {
                final AbstractButton abstractButton = (AbstractButton)component;
                final ButtonModel model = abstractButton.getModel();
                if ((model.isArmed() && model.isPressed()) || model.isSelected()) {
                    BasicGraphicsUtils.drawLoweredBezel(graphics, n, n2, n3, n4, this.shadow, this.darkShadow, this.highlight, this.lightHighlight);
                }
                else {
                    BasicGraphicsUtils.drawBezel(graphics, n, n2, n3, n4, false, abstractButton.isFocusPainted() && abstractButton.hasFocus(), this.shadow, this.darkShadow, this.highlight, this.lightHighlight);
                }
            }
            else {
                BasicGraphicsUtils.drawBezel(graphics, n, n2, n3, n4, false, false, this.shadow, this.darkShadow, this.highlight, this.lightHighlight);
            }
        }
        
        @Override
        public Insets getBorderInsets(final Component component, final Insets insets) {
            insets.set(2, 2, 2, 2);
            return insets;
        }
    }
    
    public static class MenuBarBorder extends AbstractBorder implements UIResource
    {
        private Color shadow;
        private Color highlight;
        
        public MenuBarBorder(final Color shadow, final Color highlight) {
            this.shadow = shadow;
            this.highlight = highlight;
        }
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Color color = graphics.getColor();
            graphics.translate(n, n2);
            graphics.setColor(this.shadow);
            SwingUtilities2.drawHLine(graphics, 0, n3 - 1, n4 - 2);
            graphics.setColor(this.highlight);
            SwingUtilities2.drawHLine(graphics, 0, n3 - 1, n4 - 1);
            graphics.translate(-n, -n2);
            graphics.setColor(color);
        }
        
        @Override
        public Insets getBorderInsets(final Component component, final Insets insets) {
            insets.set(0, 0, 2, 0);
            return insets;
        }
    }
    
    public static class MarginBorder extends AbstractBorder implements UIResource
    {
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
            insets.top = ((insets2 != null) ? insets2.top : 0);
            insets.left = ((insets2 != null) ? insets2.left : 0);
            insets.bottom = ((insets2 != null) ? insets2.bottom : 0);
            insets.right = ((insets2 != null) ? insets2.right : 0);
            return insets;
        }
    }
    
    public static class FieldBorder extends AbstractBorder implements UIResource
    {
        protected Color shadow;
        protected Color darkShadow;
        protected Color highlight;
        protected Color lightHighlight;
        
        public FieldBorder(final Color shadow, final Color darkShadow, final Color highlight, final Color lightHighlight) {
            this.shadow = shadow;
            this.highlight = highlight;
            this.darkShadow = darkShadow;
            this.lightHighlight = lightHighlight;
        }
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            BasicGraphicsUtils.drawEtchedRect(graphics, n, n2, n3, n4, this.shadow, this.darkShadow, this.highlight, this.lightHighlight);
        }
        
        @Override
        public Insets getBorderInsets(final Component component, final Insets insets) {
            Insets margin = null;
            if (component instanceof JTextComponent) {
                margin = ((JTextComponent)component).getMargin();
            }
            insets.top = ((margin != null) ? (2 + margin.top) : 2);
            insets.left = ((margin != null) ? (2 + margin.left) : 2);
            insets.bottom = ((margin != null) ? (2 + margin.bottom) : 2);
            insets.right = ((margin != null) ? (2 + margin.right) : 2);
            return insets;
        }
    }
    
    static class SplitPaneDividerBorder implements Border, UIResource
    {
        Color highlight;
        Color shadow;
        
        SplitPaneDividerBorder(final Color highlight, final Color shadow) {
            this.highlight = highlight;
            this.shadow = shadow;
        }
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            if (!(component instanceof BasicSplitPaneDivider)) {
                return;
            }
            final JSplitPane splitPane = ((BasicSplitPaneDivider)component).getBasicSplitPaneUI().getSplitPane();
            final Dimension size = component.getSize();
            final Component leftComponent = splitPane.getLeftComponent();
            graphics.setColor(component.getBackground());
            graphics.drawRect(n, n2, n3 - 1, n4 - 1);
            if (splitPane.getOrientation() == 1) {
                if (leftComponent != null) {
                    graphics.setColor(this.highlight);
                    graphics.drawLine(0, 0, 0, size.height);
                }
                if (splitPane.getRightComponent() != null) {
                    graphics.setColor(this.shadow);
                    graphics.drawLine(size.width - 1, 0, size.width - 1, size.height);
                }
            }
            else {
                if (leftComponent != null) {
                    graphics.setColor(this.highlight);
                    graphics.drawLine(0, 0, size.width, 0);
                }
                if (splitPane.getRightComponent() != null) {
                    graphics.setColor(this.shadow);
                    graphics.drawLine(0, size.height - 1, size.width, size.height - 1);
                }
            }
        }
        
        @Override
        public Insets getBorderInsets(final Component component) {
            final Insets insets = new Insets(0, 0, 0, 0);
            if (component instanceof BasicSplitPaneDivider) {
                final BasicSplitPaneUI basicSplitPaneUI = ((BasicSplitPaneDivider)component).getBasicSplitPaneUI();
                if (basicSplitPaneUI != null) {
                    final JSplitPane splitPane = basicSplitPaneUI.getSplitPane();
                    if (splitPane != null) {
                        if (splitPane.getOrientation() == 1) {
                            final Insets insets2 = insets;
                            final Insets insets3 = insets;
                            final int n = 0;
                            insets3.bottom = n;
                            insets2.top = n;
                            final Insets insets4 = insets;
                            final Insets insets5 = insets;
                            final int n2 = 1;
                            insets5.right = n2;
                            insets4.left = n2;
                            return insets;
                        }
                        final Insets insets6 = insets;
                        final Insets insets7 = insets;
                        final int n3 = 1;
                        insets7.bottom = n3;
                        insets6.top = n3;
                        final Insets insets8 = insets;
                        final Insets insets9 = insets;
                        final int n4 = 0;
                        insets9.right = n4;
                        insets8.left = n4;
                        return insets;
                    }
                }
            }
            final Insets insets10 = insets;
            final Insets insets11 = insets;
            final Insets insets12 = insets;
            final Insets insets13 = insets;
            final int n5 = 1;
            insets13.right = n5;
            insets12.left = n5;
            insets11.bottom = n5;
            insets10.top = n5;
            return insets;
        }
        
        @Override
        public boolean isBorderOpaque() {
            return true;
        }
    }
    
    public static class SplitPaneBorder implements Border, UIResource
    {
        protected Color highlight;
        protected Color shadow;
        
        public SplitPaneBorder(final Color highlight, final Color shadow) {
            this.highlight = highlight;
            this.shadow = shadow;
        }
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            if (!(component instanceof JSplitPane)) {
                return;
            }
            final JSplitPane splitPane = (JSplitPane)component;
            final Component leftComponent = splitPane.getLeftComponent();
            graphics.setColor(component.getBackground());
            graphics.drawRect(n, n2, n3 - 1, n4 - 1);
            if (splitPane.getOrientation() == 1) {
                if (leftComponent != null) {
                    final Rectangle bounds = leftComponent.getBounds();
                    graphics.setColor(this.shadow);
                    graphics.drawLine(0, 0, bounds.width + 1, 0);
                    graphics.drawLine(0, 1, 0, bounds.height + 1);
                    graphics.setColor(this.highlight);
                    graphics.drawLine(0, bounds.height + 1, bounds.width + 1, bounds.height + 1);
                }
                final Component rightComponent = splitPane.getRightComponent();
                if (rightComponent != null) {
                    final Rectangle bounds2 = rightComponent.getBounds();
                    final int n5 = bounds2.x + bounds2.width;
                    final int n6 = bounds2.y + bounds2.height;
                    graphics.setColor(this.shadow);
                    graphics.drawLine(bounds2.x - 1, 0, n5, 0);
                    graphics.setColor(this.highlight);
                    graphics.drawLine(bounds2.x - 1, n6, n5, n6);
                    graphics.drawLine(n5, 0, n5, n6 + 1);
                }
            }
            else {
                if (leftComponent != null) {
                    final Rectangle bounds3 = leftComponent.getBounds();
                    graphics.setColor(this.shadow);
                    graphics.drawLine(0, 0, bounds3.width + 1, 0);
                    graphics.drawLine(0, 1, 0, bounds3.height);
                    graphics.setColor(this.highlight);
                    graphics.drawLine(1 + bounds3.width, 0, 1 + bounds3.width, bounds3.height + 1);
                    graphics.drawLine(0, bounds3.height + 1, 0, bounds3.height + 1);
                }
                final Component rightComponent2 = splitPane.getRightComponent();
                if (rightComponent2 != null) {
                    final Rectangle bounds4 = rightComponent2.getBounds();
                    final int n7 = bounds4.x + bounds4.width;
                    final int n8 = bounds4.y + bounds4.height;
                    graphics.setColor(this.shadow);
                    graphics.drawLine(0, bounds4.y - 1, 0, n8);
                    graphics.drawLine(n7, bounds4.y - 1, n7, bounds4.y - 1);
                    graphics.setColor(this.highlight);
                    graphics.drawLine(0, n8, bounds4.width + 1, n8);
                    graphics.drawLine(n7, bounds4.y, n7, n8);
                }
            }
        }
        
        @Override
        public Insets getBorderInsets(final Component component) {
            return new Insets(1, 1, 1, 1);
        }
        
        @Override
        public boolean isBorderOpaque() {
            return true;
        }
    }
}
