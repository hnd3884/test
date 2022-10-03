package javax.swing.plaf.metal;

import javax.swing.JViewport;
import javax.swing.JScrollPane;
import javax.swing.text.JTextComponent;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import sun.swing.SwingUtilities2;
import javax.swing.JMenuBar;
import java.awt.Dialog;
import java.awt.Window;
import java.awt.Frame;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ColorUIResource;
import javax.swing.JInternalFrame;
import javax.swing.UIManager;
import javax.swing.JComponent;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.AbstractButton;
import java.awt.Insets;
import java.awt.Graphics;
import java.awt.Component;
import javax.swing.plaf.UIResource;
import javax.swing.border.AbstractBorder;
import sun.swing.StringUIClientPropertyKey;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;
import java.awt.Color;
import javax.swing.border.LineBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.border.Border;

public class MetalBorders
{
    static Object NO_BUTTON_ROLLOVER;
    private static Border buttonBorder;
    private static Border textBorder;
    private static Border textFieldBorder;
    private static Border toggleButtonBorder;
    
    public static Border getButtonBorder() {
        if (MetalBorders.buttonBorder == null) {
            MetalBorders.buttonBorder = new BorderUIResource.CompoundBorderUIResource(new ButtonBorder(), new BasicBorders.MarginBorder());
        }
        return MetalBorders.buttonBorder;
    }
    
    public static Border getTextBorder() {
        if (MetalBorders.textBorder == null) {
            MetalBorders.textBorder = new BorderUIResource.CompoundBorderUIResource(new Flush3DBorder(), new BasicBorders.MarginBorder());
        }
        return MetalBorders.textBorder;
    }
    
    public static Border getTextFieldBorder() {
        if (MetalBorders.textFieldBorder == null) {
            MetalBorders.textFieldBorder = new BorderUIResource.CompoundBorderUIResource(new TextFieldBorder(), new BasicBorders.MarginBorder());
        }
        return MetalBorders.textFieldBorder;
    }
    
    public static Border getToggleButtonBorder() {
        if (MetalBorders.toggleButtonBorder == null) {
            MetalBorders.toggleButtonBorder = new BorderUIResource.CompoundBorderUIResource(new ToggleButtonBorder(), new BasicBorders.MarginBorder());
        }
        return MetalBorders.toggleButtonBorder;
    }
    
    public static Border getDesktopIconBorder() {
        return new BorderUIResource.CompoundBorderUIResource(new LineBorder(MetalLookAndFeel.getControlDarkShadow(), 1), new MatteBorder(2, 2, 1, 2, MetalLookAndFeel.getControl()));
    }
    
    static Border getToolBarRolloverBorder() {
        if (MetalLookAndFeel.usingOcean()) {
            return new CompoundBorder(new ButtonBorder(), new RolloverMarginBorder());
        }
        return new CompoundBorder(new RolloverButtonBorder(), new RolloverMarginBorder());
    }
    
    static Border getToolBarNonrolloverBorder() {
        if (MetalLookAndFeel.usingOcean()) {
            new CompoundBorder(new ButtonBorder(), new RolloverMarginBorder());
        }
        return new CompoundBorder(new ButtonBorder(), new RolloverMarginBorder());
    }
    
    static {
        MetalBorders.NO_BUTTON_ROLLOVER = new StringUIClientPropertyKey("NoButtonRollover");
    }
    
    public static class Flush3DBorder extends AbstractBorder implements UIResource
    {
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            if (component.isEnabled()) {
                MetalUtils.drawFlush3DBorder(graphics, n, n2, n3, n4);
            }
            else {
                MetalUtils.drawDisabledBorder(graphics, n, n2, n3, n4);
            }
        }
        
        @Override
        public Insets getBorderInsets(final Component component, final Insets insets) {
            insets.set(2, 2, 2, 2);
            return insets;
        }
    }
    
    public static class ButtonBorder extends AbstractBorder implements UIResource
    {
        protected static Insets borderInsets;
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            if (!(component instanceof AbstractButton)) {
                return;
            }
            if (MetalLookAndFeel.usingOcean()) {
                this.paintOceanBorder(component, graphics, n, n2, n3, n4);
                return;
            }
            final AbstractButton abstractButton = (AbstractButton)component;
            final ButtonModel model = abstractButton.getModel();
            if (model.isEnabled()) {
                final boolean b = model.isPressed() && model.isArmed();
                final boolean b2 = abstractButton instanceof JButton && ((JButton)abstractButton).isDefaultButton();
                if (b && b2) {
                    MetalUtils.drawDefaultButtonPressedBorder(graphics, n, n2, n3, n4);
                }
                else if (b) {
                    MetalUtils.drawPressed3DBorder(graphics, n, n2, n3, n4);
                }
                else if (b2) {
                    MetalUtils.drawDefaultButtonBorder(graphics, n, n2, n3, n4, false);
                }
                else {
                    MetalUtils.drawButtonBorder(graphics, n, n2, n3, n4, false);
                }
            }
            else {
                MetalUtils.drawDisabledBorder(graphics, n, n2, n3 - 1, n4 - 1);
            }
        }
        
        private void paintOceanBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final AbstractButton abstractButton = (AbstractButton)component;
            final ButtonModel model = ((AbstractButton)component).getModel();
            graphics.translate(n, n2);
            if (MetalUtils.isToolBarButton(abstractButton)) {
                if (model.isEnabled()) {
                    if (model.isPressed()) {
                        graphics.setColor(MetalLookAndFeel.getWhite());
                        graphics.fillRect(1, n4 - 1, n3 - 1, 1);
                        graphics.fillRect(n3 - 1, 1, 1, n4 - 1);
                        graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
                        graphics.drawRect(0, 0, n3 - 2, n4 - 2);
                        graphics.fillRect(1, 1, n3 - 3, 1);
                    }
                    else if (model.isSelected() || model.isRollover()) {
                        graphics.setColor(MetalLookAndFeel.getWhite());
                        graphics.fillRect(1, n4 - 1, n3 - 1, 1);
                        graphics.fillRect(n3 - 1, 1, 1, n4 - 1);
                        graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
                        graphics.drawRect(0, 0, n3 - 2, n4 - 2);
                    }
                    else {
                        graphics.setColor(MetalLookAndFeel.getWhite());
                        graphics.drawRect(1, 1, n3 - 2, n4 - 2);
                        graphics.setColor(UIManager.getColor("Button.toolBarBorderBackground"));
                        graphics.drawRect(0, 0, n3 - 2, n4 - 2);
                    }
                }
                else {
                    graphics.setColor(UIManager.getColor("Button.disabledToolBarBorderBackground"));
                    graphics.drawRect(0, 0, n3 - 2, n4 - 2);
                }
            }
            else if (model.isEnabled()) {
                final boolean pressed = model.isPressed();
                model.isArmed();
                if (component instanceof JButton && ((JButton)component).isDefaultButton()) {
                    graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
                    graphics.drawRect(0, 0, n3 - 1, n4 - 1);
                    graphics.drawRect(1, 1, n3 - 3, n4 - 3);
                }
                else if (pressed) {
                    graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
                    graphics.fillRect(0, 0, n3, 2);
                    graphics.fillRect(0, 2, 2, n4 - 2);
                    graphics.fillRect(n3 - 1, 1, 1, n4 - 1);
                    graphics.fillRect(1, n4 - 1, n3 - 2, 1);
                }
                else if (model.isRollover() && abstractButton.getClientProperty(MetalBorders.NO_BUTTON_ROLLOVER) == null) {
                    graphics.setColor(MetalLookAndFeel.getPrimaryControl());
                    graphics.drawRect(0, 0, n3 - 1, n4 - 1);
                    graphics.drawRect(2, 2, n3 - 5, n4 - 5);
                    graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
                    graphics.drawRect(1, 1, n3 - 3, n4 - 3);
                }
                else {
                    graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
                    graphics.drawRect(0, 0, n3 - 1, n4 - 1);
                }
            }
            else {
                graphics.setColor(MetalLookAndFeel.getInactiveControlTextColor());
                graphics.drawRect(0, 0, n3 - 1, n4 - 1);
                if (component instanceof JButton && ((JButton)component).isDefaultButton()) {
                    graphics.drawRect(1, 1, n3 - 3, n4 - 3);
                }
            }
        }
        
        @Override
        public Insets getBorderInsets(final Component component, final Insets insets) {
            insets.set(3, 3, 3, 3);
            return insets;
        }
        
        static {
            ButtonBorder.borderInsets = new Insets(3, 3, 3, 3);
        }
    }
    
    public static class InternalFrameBorder extends AbstractBorder implements UIResource
    {
        private static final int corner = 14;
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            ColorUIResource color;
            ColorUIResource color2;
            ColorUIResource color3;
            if (component instanceof JInternalFrame && ((JInternalFrame)component).isSelected()) {
                color = MetalLookAndFeel.getPrimaryControlDarkShadow();
                color2 = MetalLookAndFeel.getPrimaryControlShadow();
                color3 = MetalLookAndFeel.getPrimaryControlInfo();
            }
            else {
                color = MetalLookAndFeel.getControlDarkShadow();
                color2 = MetalLookAndFeel.getControlShadow();
                color3 = MetalLookAndFeel.getControlInfo();
            }
            graphics.setColor(color);
            graphics.drawLine(1, 0, n3 - 2, 0);
            graphics.drawLine(0, 1, 0, n4 - 2);
            graphics.drawLine(n3 - 1, 1, n3 - 1, n4 - 2);
            graphics.drawLine(1, n4 - 1, n3 - 2, n4 - 1);
            for (int i = 1; i < 5; ++i) {
                graphics.drawRect(n + i, n2 + i, n3 - i * 2 - 1, n4 - i * 2 - 1);
            }
            if (component instanceof JInternalFrame && ((JInternalFrame)component).isResizable()) {
                graphics.setColor(color2);
                graphics.drawLine(15, 3, n3 - 14, 3);
                graphics.drawLine(3, 15, 3, n4 - 14);
                graphics.drawLine(n3 - 2, 15, n3 - 2, n4 - 14);
                graphics.drawLine(15, n4 - 2, n3 - 14, n4 - 2);
                graphics.setColor(color3);
                graphics.drawLine(14, 2, n3 - 14 - 1, 2);
                graphics.drawLine(2, 14, 2, n4 - 14 - 1);
                graphics.drawLine(n3 - 3, 14, n3 - 3, n4 - 14 - 1);
                graphics.drawLine(14, n4 - 3, n3 - 14 - 1, n4 - 3);
            }
        }
        
        @Override
        public Insets getBorderInsets(final Component component, final Insets insets) {
            insets.set(5, 5, 5, 5);
            return insets;
        }
    }
    
    static class FrameBorder extends AbstractBorder implements UIResource
    {
        private static final int corner = 14;
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Window windowAncestor = SwingUtilities.getWindowAncestor(component);
            ColorUIResource color;
            ColorUIResource color2;
            ColorUIResource color3;
            if (windowAncestor != null && windowAncestor.isActive()) {
                color = MetalLookAndFeel.getPrimaryControlDarkShadow();
                color2 = MetalLookAndFeel.getPrimaryControlShadow();
                color3 = MetalLookAndFeel.getPrimaryControlInfo();
            }
            else {
                color = MetalLookAndFeel.getControlDarkShadow();
                color2 = MetalLookAndFeel.getControlShadow();
                color3 = MetalLookAndFeel.getControlInfo();
            }
            graphics.setColor(color);
            graphics.drawLine(n + 1, n2 + 0, n + n3 - 2, n2 + 0);
            graphics.drawLine(n + 0, n2 + 1, n + 0, n2 + n4 - 2);
            graphics.drawLine(n + n3 - 1, n2 + 1, n + n3 - 1, n2 + n4 - 2);
            graphics.drawLine(n + 1, n2 + n4 - 1, n + n3 - 2, n2 + n4 - 1);
            for (int i = 1; i < 5; ++i) {
                graphics.drawRect(n + i, n2 + i, n3 - i * 2 - 1, n4 - i * 2 - 1);
            }
            if (windowAncestor instanceof Frame && ((Frame)windowAncestor).isResizable()) {
                graphics.setColor(color2);
                graphics.drawLine(15, 3, n3 - 14, 3);
                graphics.drawLine(3, 15, 3, n4 - 14);
                graphics.drawLine(n3 - 2, 15, n3 - 2, n4 - 14);
                graphics.drawLine(15, n4 - 2, n3 - 14, n4 - 2);
                graphics.setColor(color3);
                graphics.drawLine(14, 2, n3 - 14 - 1, 2);
                graphics.drawLine(2, 14, 2, n4 - 14 - 1);
                graphics.drawLine(n3 - 3, 14, n3 - 3, n4 - 14 - 1);
                graphics.drawLine(14, n4 - 3, n3 - 14 - 1, n4 - 3);
            }
        }
        
        @Override
        public Insets getBorderInsets(final Component component, final Insets insets) {
            insets.set(5, 5, 5, 5);
            return insets;
        }
    }
    
    static class DialogBorder extends AbstractBorder implements UIResource
    {
        private static final int corner = 14;
        
        protected Color getActiveBackground() {
            return MetalLookAndFeel.getPrimaryControlDarkShadow();
        }
        
        protected Color getActiveHighlight() {
            return MetalLookAndFeel.getPrimaryControlShadow();
        }
        
        protected Color getActiveShadow() {
            return MetalLookAndFeel.getPrimaryControlInfo();
        }
        
        protected Color getInactiveBackground() {
            return MetalLookAndFeel.getControlDarkShadow();
        }
        
        protected Color getInactiveHighlight() {
            return MetalLookAndFeel.getControlShadow();
        }
        
        protected Color getInactiveShadow() {
            return MetalLookAndFeel.getControlInfo();
        }
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Window windowAncestor = SwingUtilities.getWindowAncestor(component);
            Color color;
            Color color2;
            Color color3;
            if (windowAncestor != null && windowAncestor.isActive()) {
                color = this.getActiveBackground();
                color2 = this.getActiveHighlight();
                color3 = this.getActiveShadow();
            }
            else {
                color = this.getInactiveBackground();
                color2 = this.getInactiveHighlight();
                color3 = this.getInactiveShadow();
            }
            graphics.setColor(color);
            graphics.drawLine(n + 1, n2 + 0, n + n3 - 2, n2 + 0);
            graphics.drawLine(n + 0, n2 + 1, n + 0, n2 + n4 - 2);
            graphics.drawLine(n + n3 - 1, n2 + 1, n + n3 - 1, n2 + n4 - 2);
            graphics.drawLine(n + 1, n2 + n4 - 1, n + n3 - 2, n2 + n4 - 1);
            for (int i = 1; i < 5; ++i) {
                graphics.drawRect(n + i, n2 + i, n3 - i * 2 - 1, n4 - i * 2 - 1);
            }
            if (windowAncestor instanceof Dialog && ((Dialog)windowAncestor).isResizable()) {
                graphics.setColor(color2);
                graphics.drawLine(15, 3, n3 - 14, 3);
                graphics.drawLine(3, 15, 3, n4 - 14);
                graphics.drawLine(n3 - 2, 15, n3 - 2, n4 - 14);
                graphics.drawLine(15, n4 - 2, n3 - 14, n4 - 2);
                graphics.setColor(color3);
                graphics.drawLine(14, 2, n3 - 14 - 1, 2);
                graphics.drawLine(2, 14, 2, n4 - 14 - 1);
                graphics.drawLine(n3 - 3, 14, n3 - 3, n4 - 14 - 1);
                graphics.drawLine(14, n4 - 3, n3 - 14 - 1, n4 - 3);
            }
        }
        
        @Override
        public Insets getBorderInsets(final Component component, final Insets insets) {
            insets.set(5, 5, 5, 5);
            return insets;
        }
    }
    
    static class ErrorDialogBorder extends DialogBorder implements UIResource
    {
        @Override
        protected Color getActiveBackground() {
            return UIManager.getColor("OptionPane.errorDialog.border.background");
        }
    }
    
    static class QuestionDialogBorder extends DialogBorder implements UIResource
    {
        @Override
        protected Color getActiveBackground() {
            return UIManager.getColor("OptionPane.questionDialog.border.background");
        }
    }
    
    static class WarningDialogBorder extends DialogBorder implements UIResource
    {
        @Override
        protected Color getActiveBackground() {
            return UIManager.getColor("OptionPane.warningDialog.border.background");
        }
    }
    
    public static class PaletteBorder extends AbstractBorder implements UIResource
    {
        int titleHeight;
        
        public PaletteBorder() {
            this.titleHeight = 0;
        }
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            graphics.translate(n, n2);
            graphics.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
            graphics.drawLine(0, 1, 0, n4 - 2);
            graphics.drawLine(1, n4 - 1, n3 - 2, n4 - 1);
            graphics.drawLine(n3 - 1, 1, n3 - 1, n4 - 2);
            graphics.drawLine(1, 0, n3 - 2, 0);
            graphics.drawRect(1, 1, n3 - 3, n4 - 3);
            graphics.translate(-n, -n2);
        }
        
        @Override
        public Insets getBorderInsets(final Component component, final Insets insets) {
            insets.set(1, 1, 1, 1);
            return insets;
        }
    }
    
    public static class OptionDialogBorder extends AbstractBorder implements UIResource
    {
        int titleHeight;
        
        public OptionDialogBorder() {
            this.titleHeight = 0;
        }
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            graphics.translate(n, n2);
            int intValue = -1;
            if (component instanceof JInternalFrame) {
                final Object clientProperty = ((JInternalFrame)component).getClientProperty("JInternalFrame.messageType");
                if (clientProperty instanceof Integer) {
                    intValue = (int)clientProperty;
                }
            }
            Color color = null;
            switch (intValue) {
                case 0: {
                    color = UIManager.getColor("OptionPane.errorDialog.border.background");
                    break;
                }
                case 3: {
                    color = UIManager.getColor("OptionPane.questionDialog.border.background");
                    break;
                }
                case 2: {
                    color = UIManager.getColor("OptionPane.warningDialog.border.background");
                    break;
                }
                default: {
                    color = MetalLookAndFeel.getPrimaryControlDarkShadow();
                    break;
                }
            }
            graphics.setColor(color);
            graphics.drawLine(1, 0, n3 - 2, 0);
            graphics.drawLine(0, 1, 0, n4 - 2);
            graphics.drawLine(n3 - 1, 1, n3 - 1, n4 - 2);
            graphics.drawLine(1, n4 - 1, n3 - 2, n4 - 1);
            for (int i = 1; i < 3; ++i) {
                graphics.drawRect(i, i, n3 - i * 2 - 1, n4 - i * 2 - 1);
            }
            graphics.translate(-n, -n2);
        }
        
        @Override
        public Insets getBorderInsets(final Component component, final Insets insets) {
            insets.set(3, 3, 3, 3);
            return insets;
        }
    }
    
    public static class MenuBarBorder extends AbstractBorder implements UIResource
    {
        protected static Insets borderInsets;
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            graphics.translate(n, n2);
            if (MetalLookAndFeel.usingOcean()) {
                if (component instanceof JMenuBar && !MetalToolBarUI.doesMenuBarBorderToolBar((JMenuBar)component)) {
                    graphics.setColor(MetalLookAndFeel.getControl());
                    SwingUtilities2.drawHLine(graphics, 0, n3 - 1, n4 - 2);
                    graphics.setColor(UIManager.getColor("MenuBar.borderColor"));
                    SwingUtilities2.drawHLine(graphics, 0, n3 - 1, n4 - 1);
                }
            }
            else {
                graphics.setColor(MetalLookAndFeel.getControlShadow());
                SwingUtilities2.drawHLine(graphics, 0, n3 - 1, n4 - 1);
            }
            graphics.translate(-n, -n2);
        }
        
        @Override
        public Insets getBorderInsets(final Component component, final Insets insets) {
            if (MetalLookAndFeel.usingOcean()) {
                insets.set(0, 0, 2, 0);
            }
            else {
                insets.set(1, 0, 1, 0);
            }
            return insets;
        }
        
        static {
            MenuBarBorder.borderInsets = new Insets(1, 0, 1, 0);
        }
    }
    
    public static class MenuItemBorder extends AbstractBorder implements UIResource
    {
        protected static Insets borderInsets;
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            if (!(component instanceof JMenuItem)) {
                return;
            }
            final ButtonModel model = ((JMenuItem)component).getModel();
            graphics.translate(n, n2);
            if (component.getParent() instanceof JMenuBar) {
                if (model.isArmed() || model.isSelected()) {
                    graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
                    graphics.drawLine(0, 0, n3 - 2, 0);
                    graphics.drawLine(0, 0, 0, n4 - 1);
                    graphics.drawLine(n3 - 2, 2, n3 - 2, n4 - 1);
                    graphics.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
                    graphics.drawLine(n3 - 1, 1, n3 - 1, n4 - 1);
                    graphics.setColor(MetalLookAndFeel.getMenuBackground());
                    graphics.drawLine(n3 - 1, 0, n3 - 1, 0);
                }
            }
            else if (model.isArmed() || (component instanceof JMenu && model.isSelected())) {
                graphics.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
                graphics.drawLine(0, 0, n3 - 1, 0);
                graphics.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
                graphics.drawLine(0, n4 - 1, n3 - 1, n4 - 1);
            }
            else {
                graphics.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
                graphics.drawLine(0, 0, 0, n4 - 1);
            }
            graphics.translate(-n, -n2);
        }
        
        @Override
        public Insets getBorderInsets(final Component component, final Insets insets) {
            insets.set(2, 2, 2, 2);
            return insets;
        }
        
        static {
            MenuItemBorder.borderInsets = new Insets(2, 2, 2, 2);
        }
    }
    
    public static class PopupMenuBorder extends AbstractBorder implements UIResource
    {
        protected static Insets borderInsets;
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            graphics.translate(n, n2);
            graphics.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
            graphics.drawRect(0, 0, n3 - 1, n4 - 1);
            graphics.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
            graphics.drawLine(1, 1, n3 - 2, 1);
            graphics.drawLine(1, 2, 1, 2);
            graphics.drawLine(1, n4 - 2, 1, n4 - 2);
            graphics.translate(-n, -n2);
        }
        
        @Override
        public Insets getBorderInsets(final Component component, final Insets insets) {
            insets.set(3, 1, 2, 1);
            return insets;
        }
        
        static {
            PopupMenuBorder.borderInsets = new Insets(3, 1, 2, 1);
        }
    }
    
    public static class RolloverButtonBorder extends ButtonBorder
    {
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final ButtonModel model = ((AbstractButton)component).getModel();
            if (model.isRollover() && (!model.isPressed() || model.isArmed())) {
                super.paintBorder(component, graphics, n, n2, n3, n4);
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
    
    public static class ToolBarBorder extends AbstractBorder implements UIResource, SwingConstants
    {
        protected MetalBumps bumps;
        
        public ToolBarBorder() {
            this.bumps = new MetalBumps(10, 10, MetalLookAndFeel.getControlHighlight(), MetalLookAndFeel.getControlDarkShadow(), UIManager.getColor("ToolBar.background"));
        }
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            if (!(component instanceof JToolBar)) {
                return;
            }
            graphics.translate(n, n2);
            if (((JToolBar)component).isFloatable()) {
                if (((JToolBar)component).getOrientation() == 0) {
                    final int n5 = MetalLookAndFeel.usingOcean() ? -1 : 0;
                    this.bumps.setBumpArea(10, n4 - 4);
                    if (MetalUtils.isLeftToRight(component)) {
                        this.bumps.paintIcon(component, graphics, 2, 2 + n5);
                    }
                    else {
                        this.bumps.paintIcon(component, graphics, n3 - 12, 2 + n5);
                    }
                }
                else {
                    this.bumps.setBumpArea(n3 - 4, 10);
                    this.bumps.paintIcon(component, graphics, 2, 2);
                }
            }
            if (((JToolBar)component).getOrientation() == 0 && MetalLookAndFeel.usingOcean()) {
                graphics.setColor(MetalLookAndFeel.getControl());
                graphics.drawLine(0, n4 - 2, n3, n4 - 2);
                graphics.setColor(UIManager.getColor("ToolBar.borderColor"));
                graphics.drawLine(0, n4 - 1, n3, n4 - 1);
            }
            graphics.translate(-n, -n2);
        }
        
        @Override
        public Insets getBorderInsets(final Component component, final Insets insets) {
            if (MetalLookAndFeel.usingOcean()) {
                insets.set(1, 2, 3, 2);
            }
            else {
                final int n = 2;
                insets.right = n;
                insets.bottom = n;
                insets.left = n;
                insets.top = n;
            }
            if (!(component instanceof JToolBar)) {
                return insets;
            }
            if (((JToolBar)component).isFloatable()) {
                if (((JToolBar)component).getOrientation() == 0) {
                    if (component.getComponentOrientation().isLeftToRight()) {
                        insets.left = 16;
                    }
                    else {
                        insets.right = 16;
                    }
                }
                else {
                    insets.top = 16;
                }
            }
            final Insets margin = ((JToolBar)component).getMargin();
            if (margin != null) {
                insets.left += margin.left;
                insets.top += margin.top;
                insets.right += margin.right;
                insets.bottom += margin.bottom;
            }
            return insets;
        }
    }
    
    public static class TextFieldBorder extends Flush3DBorder
    {
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            if (!(component instanceof JTextComponent)) {
                if (component.isEnabled()) {
                    MetalUtils.drawFlush3DBorder(graphics, n, n2, n3, n4);
                }
                else {
                    MetalUtils.drawDisabledBorder(graphics, n, n2, n3, n4);
                }
                return;
            }
            if (component.isEnabled() && ((JTextComponent)component).isEditable()) {
                MetalUtils.drawFlush3DBorder(graphics, n, n2, n3, n4);
            }
            else {
                MetalUtils.drawDisabledBorder(graphics, n, n2, n3, n4);
            }
        }
    }
    
    public static class ScrollPaneBorder extends AbstractBorder implements UIResource
    {
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            if (!(component instanceof JScrollPane)) {
                return;
            }
            final JScrollPane scrollPane = (JScrollPane)component;
            final JViewport columnHeader = scrollPane.getColumnHeader();
            int height = 0;
            if (columnHeader != null) {
                height = columnHeader.getHeight();
            }
            final JViewport rowHeader = scrollPane.getRowHeader();
            int width = 0;
            if (rowHeader != null) {
                width = rowHeader.getWidth();
            }
            graphics.translate(n, n2);
            graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
            graphics.drawRect(0, 0, n3 - 2, n4 - 2);
            graphics.setColor(MetalLookAndFeel.getControlHighlight());
            graphics.drawLine(n3 - 1, 1, n3 - 1, n4 - 1);
            graphics.drawLine(1, n4 - 1, n3 - 1, n4 - 1);
            graphics.setColor(MetalLookAndFeel.getControl());
            graphics.drawLine(n3 - 2, 2 + height, n3 - 2, 2 + height);
            graphics.drawLine(1 + width, n4 - 2, 1 + width, n4 - 2);
            graphics.translate(-n, -n2);
        }
        
        @Override
        public Insets getBorderInsets(final Component component, final Insets insets) {
            insets.set(1, 1, 2, 2);
            return insets;
        }
    }
    
    public static class ToggleButtonBorder extends ButtonBorder
    {
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final AbstractButton abstractButton = (AbstractButton)component;
            final ButtonModel model = abstractButton.getModel();
            if (MetalLookAndFeel.usingOcean()) {
                if (model.isArmed() || !abstractButton.isEnabled()) {
                    super.paintBorder(component, graphics, n, n2, n3, n4);
                }
                else {
                    graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
                    graphics.drawRect(0, 0, n3 - 1, n4 - 1);
                }
                return;
            }
            if (!component.isEnabled()) {
                MetalUtils.drawDisabledBorder(graphics, n, n2, n3 - 1, n4 - 1);
            }
            else if (model.isPressed() && model.isArmed()) {
                MetalUtils.drawPressed3DBorder(graphics, n, n2, n3, n4);
            }
            else if (model.isSelected()) {
                MetalUtils.drawDark3DBorder(graphics, n, n2, n3, n4);
            }
            else {
                MetalUtils.drawFlush3DBorder(graphics, n, n2, n3, n4);
            }
        }
    }
    
    public static class TableHeaderBorder extends AbstractBorder
    {
        protected Insets editorBorderInsets;
        
        public TableHeaderBorder() {
            this.editorBorderInsets = new Insets(2, 2, 2, 0);
        }
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            graphics.translate(n, n2);
            graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
            graphics.drawLine(n3 - 1, 0, n3 - 1, n4 - 1);
            graphics.drawLine(1, n4 - 1, n3 - 1, n4 - 1);
            graphics.setColor(MetalLookAndFeel.getControlHighlight());
            graphics.drawLine(0, 0, n3 - 2, 0);
            graphics.drawLine(0, 0, 0, n4 - 2);
            graphics.translate(-n, -n2);
        }
        
        @Override
        public Insets getBorderInsets(final Component component, final Insets insets) {
            insets.set(2, 2, 2, 0);
            return insets;
        }
    }
}
