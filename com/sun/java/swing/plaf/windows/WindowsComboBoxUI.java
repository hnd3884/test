package com.sun.java.swing.plaf.windows;

import javax.swing.border.EmptyBorder;
import sun.swing.StringUIClientPropertyKey;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.KeyboardFocusManager;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import java.awt.event.KeyListener;
import javax.swing.plaf.basic.BasicComboPopup;
import java.awt.ComponentOrientation;
import java.beans.PropertyChangeEvent;
import javax.swing.ButtonModel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.ComboBoxEditor;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Dimension;
import javax.swing.ListCellRenderer;
import java.awt.Container;
import javax.swing.JPanel;
import java.awt.Color;
import sun.swing.DefaultLookup;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Component;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.beans.PropertyChangeListener;
import java.awt.event.MouseListener;
import javax.swing.plaf.basic.BasicComboBoxUI;

public class WindowsComboBoxUI extends BasicComboBoxUI
{
    private static final MouseListener rolloverListener;
    private boolean isRollover;
    private static final PropertyChangeListener componentOrientationListener;
    
    public WindowsComboBoxUI() {
        this.isRollover = false;
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new WindowsComboBoxUI();
    }
    
    @Override
    public void installUI(final JComponent component) {
        super.installUI(component);
        this.isRollover = false;
        this.comboBox.setRequestFocusEnabled(true);
        if (XPStyle.getXP() != null && this.arrowButton != null) {
            this.comboBox.addMouseListener(WindowsComboBoxUI.rolloverListener);
            this.arrowButton.addMouseListener(WindowsComboBoxUI.rolloverListener);
        }
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        this.comboBox.removeMouseListener(WindowsComboBoxUI.rolloverListener);
        if (this.arrowButton != null) {
            this.arrowButton.removeMouseListener(WindowsComboBoxUI.rolloverListener);
        }
        super.uninstallUI(component);
    }
    
    @Override
    protected void installListeners() {
        super.installListeners();
        final XPStyle xp = XPStyle.getXP();
        if (xp != null && xp.isSkinDefined(this.comboBox, TMSchema.Part.CP_DROPDOWNBUTTONRIGHT)) {
            this.comboBox.addPropertyChangeListener("componentOrientation", WindowsComboBoxUI.componentOrientationListener);
        }
    }
    
    @Override
    protected void uninstallListeners() {
        super.uninstallListeners();
        this.comboBox.removePropertyChangeListener("componentOrientation", WindowsComboBoxUI.componentOrientationListener);
    }
    
    @Override
    protected void configureEditor() {
        super.configureEditor();
        if (XPStyle.getXP() != null) {
            this.editor.addMouseListener(WindowsComboBoxUI.rolloverListener);
        }
    }
    
    @Override
    protected void unconfigureEditor() {
        super.unconfigureEditor();
        this.editor.removeMouseListener(WindowsComboBoxUI.rolloverListener);
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        if (XPStyle.getXP() != null) {
            this.paintXPComboBoxBackground(graphics, component);
        }
        super.paint(graphics, component);
    }
    
    TMSchema.State getXPComboBoxState(final JComponent component) {
        TMSchema.State state = TMSchema.State.NORMAL;
        if (!component.isEnabled()) {
            state = TMSchema.State.DISABLED;
        }
        else if (this.isPopupVisible(this.comboBox)) {
            state = TMSchema.State.PRESSED;
        }
        else if (this.isRollover) {
            state = TMSchema.State.HOT;
        }
        return state;
    }
    
    private void paintXPComboBoxBackground(final Graphics graphics, final JComponent component) {
        final XPStyle xp = XPStyle.getXP();
        if (xp == null) {
            return;
        }
        final TMSchema.State xpComboBoxState = this.getXPComboBoxState(component);
        XPStyle.Skin skin = null;
        if (!this.comboBox.isEditable() && xp.isSkinDefined(component, TMSchema.Part.CP_READONLY)) {
            skin = xp.getSkin(component, TMSchema.Part.CP_READONLY);
        }
        if (skin == null) {
            skin = xp.getSkin(component, TMSchema.Part.CP_COMBOBOX);
        }
        skin.paintSkin(graphics, 0, 0, component.getWidth(), component.getHeight(), xpComboBoxState);
    }
    
    @Override
    public void paintCurrentValue(final Graphics graphics, final Rectangle rectangle, final boolean b) {
        final XPStyle xp = XPStyle.getXP();
        if (xp != null) {
            rectangle.x += 2;
            rectangle.y += 2;
            rectangle.width -= 4;
            rectangle.height -= 4;
        }
        else {
            ++rectangle.x;
            ++rectangle.y;
            rectangle.width -= 2;
            rectangle.height -= 2;
        }
        if (!this.comboBox.isEditable() && xp != null && xp.isSkinDefined(this.comboBox, TMSchema.Part.CP_READONLY)) {
            final ListCellRenderer renderer = this.comboBox.getRenderer();
            Component component;
            if (b && !this.isPopupVisible(this.comboBox)) {
                component = renderer.getListCellRendererComponent(this.listBox, this.comboBox.getSelectedItem(), -1, true, false);
            }
            else {
                component = renderer.getListCellRendererComponent(this.listBox, this.comboBox.getSelectedItem(), -1, false, false);
            }
            component.setFont(this.comboBox.getFont());
            if (this.comboBox.isEnabled()) {
                component.setForeground(this.comboBox.getForeground());
                component.setBackground(this.comboBox.getBackground());
            }
            else {
                component.setForeground(DefaultLookup.getColor(this.comboBox, this, "ComboBox.disabledForeground", null));
                component.setBackground(DefaultLookup.getColor(this.comboBox, this, "ComboBox.disabledBackground", null));
            }
            boolean b2 = false;
            if (component instanceof JPanel) {
                b2 = true;
            }
            this.currentValuePane.paintComponent(graphics, component, this.comboBox, rectangle.x, rectangle.y, rectangle.width, rectangle.height, b2);
        }
        else {
            super.paintCurrentValue(graphics, rectangle, b);
        }
    }
    
    @Override
    public void paintCurrentValueBackground(final Graphics graphics, final Rectangle rectangle, final boolean b) {
        if (XPStyle.getXP() == null) {
            super.paintCurrentValueBackground(graphics, rectangle, b);
        }
    }
    
    @Override
    public Dimension getMinimumSize(final JComponent component) {
        final Dimension minimumSize = super.getMinimumSize(component);
        if (XPStyle.getXP() != null) {
            final Dimension dimension = minimumSize;
            dimension.width += 5;
        }
        else {
            final Dimension dimension2 = minimumSize;
            dimension2.width += 4;
        }
        final Dimension dimension3 = minimumSize;
        dimension3.height += 2;
        return minimumSize;
    }
    
    @Override
    protected LayoutManager createLayoutManager() {
        return new ComboBoxLayoutManager() {
            @Override
            public void layoutContainer(final Container container) {
                super.layoutContainer(container);
                if (XPStyle.getXP() != null && WindowsComboBoxUI.this.arrowButton != null) {
                    final Dimension size = container.getSize();
                    final Insets access$700 = BasicComboBoxUI.this.getInsets();
                    final int width = WindowsComboBoxUI.this.arrowButton.getPreferredSize().width;
                    WindowsComboBoxUI.this.arrowButton.setBounds(WindowsGraphicsUtils.isLeftToRight(container) ? (size.width - access$700.right - width) : access$700.left, access$700.top, width, size.height - access$700.top - access$700.bottom);
                }
            }
        };
    }
    
    @Override
    protected void installKeyboardActions() {
        super.installKeyboardActions();
    }
    
    @Override
    protected ComboPopup createPopup() {
        return super.createPopup();
    }
    
    @Override
    protected ComboBoxEditor createEditor() {
        return new WindowsComboBoxEditor();
    }
    
    @Override
    protected ListCellRenderer createRenderer() {
        final XPStyle xp = XPStyle.getXP();
        if (xp != null && xp.isSkinDefined(this.comboBox, TMSchema.Part.CP_READONLY)) {
            return new WindowsComboBoxRenderer();
        }
        return super.createRenderer();
    }
    
    @Override
    protected JButton createArrowButton() {
        final XPStyle xp = XPStyle.getXP();
        if (xp != null) {
            return new XPComboBoxButton(xp);
        }
        return super.createArrowButton();
    }
    
    static {
        rolloverListener = new MouseAdapter() {
            private void handleRollover(final MouseEvent mouseEvent, final boolean rollover) {
                final JComboBox comboBox = this.getComboBox(mouseEvent);
                final WindowsComboBoxUI windowsComboBoxUI = this.getWindowsComboBoxUI(mouseEvent);
                if (comboBox == null || windowsComboBoxUI == null) {
                    return;
                }
                if (!comboBox.isEditable()) {
                    ButtonModel model = null;
                    if (windowsComboBoxUI.arrowButton != null) {
                        model = windowsComboBoxUI.arrowButton.getModel();
                    }
                    if (model != null) {
                        model.setRollover(rollover);
                    }
                }
                windowsComboBoxUI.isRollover = rollover;
                comboBox.repaint();
            }
            
            @Override
            public void mouseEntered(final MouseEvent mouseEvent) {
                this.handleRollover(mouseEvent, true);
            }
            
            @Override
            public void mouseExited(final MouseEvent mouseEvent) {
                this.handleRollover(mouseEvent, false);
            }
            
            private JComboBox getComboBox(final MouseEvent mouseEvent) {
                final Object source = mouseEvent.getSource();
                JComboBox access$300 = null;
                if (source instanceof JComboBox) {
                    access$300 = (JComboBox)source;
                }
                else if (source instanceof XPComboBoxButton) {
                    access$300 = ((XPComboBoxButton)source).getWindowsComboBoxUI().comboBox;
                }
                return access$300;
            }
            
            private WindowsComboBoxUI getWindowsComboBoxUI(final MouseEvent mouseEvent) {
                final JComboBox comboBox = this.getComboBox(mouseEvent);
                WindowsComboBoxUI windowsComboBoxUI = null;
                if (comboBox != null && comboBox.getUI() instanceof WindowsComboBoxUI) {
                    windowsComboBoxUI = (WindowsComboBoxUI)comboBox.getUI();
                }
                return windowsComboBoxUI;
            }
        };
        componentOrientationListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
                final Object source;
                if ("componentOrientation" == propertyChangeEvent.getPropertyName() && (source = propertyChangeEvent.getSource()) instanceof JComboBox && ((JComboBox)source).getUI() instanceof WindowsComboBoxUI) {
                    final JComboBox comboBox = (JComboBox)source;
                    final WindowsComboBoxUI windowsComboBoxUI = (WindowsComboBoxUI)comboBox.getUI();
                    if (windowsComboBoxUI.arrowButton instanceof XPComboBoxButton) {
                        ((XPComboBoxButton)windowsComboBoxUI.arrowButton).setPart((comboBox.getComponentOrientation() == ComponentOrientation.RIGHT_TO_LEFT) ? TMSchema.Part.CP_DROPDOWNBUTTONLEFT : TMSchema.Part.CP_DROPDOWNBUTTONRIGHT);
                    }
                }
            }
        };
    }
    
    private class XPComboBoxButton extends XPStyle.GlyphButton
    {
        public XPComboBoxButton(final XPStyle xpStyle) {
            super(null, xpStyle.isSkinDefined(WindowsComboBoxUI.this.comboBox, TMSchema.Part.CP_DROPDOWNBUTTONRIGHT) ? ((WindowsComboBoxUI.this.comboBox.getComponentOrientation() == ComponentOrientation.RIGHT_TO_LEFT) ? TMSchema.Part.CP_DROPDOWNBUTTONLEFT : TMSchema.Part.CP_DROPDOWNBUTTONRIGHT) : TMSchema.Part.CP_DROPDOWNBUTTON);
            this.setRequestFocusEnabled(false);
        }
        
        @Override
        protected TMSchema.State getState() {
            TMSchema.State state = super.getState();
            final XPStyle xp = XPStyle.getXP();
            if (state != TMSchema.State.DISABLED && WindowsComboBoxUI.this.comboBox != null && !WindowsComboBoxUI.this.comboBox.isEditable() && xp != null && xp.isSkinDefined(WindowsComboBoxUI.this.comboBox, TMSchema.Part.CP_DROPDOWNBUTTONRIGHT)) {
                state = TMSchema.State.NORMAL;
            }
            return state;
        }
        
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(17, 21);
        }
        
        void setPart(final TMSchema.Part part) {
            this.setPart(WindowsComboBoxUI.this.comboBox, part);
        }
        
        WindowsComboBoxUI getWindowsComboBoxUI() {
            return WindowsComboBoxUI.this;
        }
    }
    
    @Deprecated
    protected class WindowsComboPopup extends BasicComboPopup
    {
        public WindowsComboPopup(final JComboBox comboBox) {
            super(comboBox);
        }
        
        @Override
        protected KeyListener createKeyListener() {
            return new InvocationKeyHandler();
        }
        
        protected class InvocationKeyHandler extends BasicComboPopup.InvocationKeyHandler
        {
        }
    }
    
    public static class WindowsComboBoxEditor extends UIResource
    {
        @Override
        protected JTextField createEditorComponent() {
            final JTextField editorComponent = super.createEditorComponent();
            final Border border = (Border)UIManager.get("ComboBox.editorBorder");
            if (border != null) {
                editorComponent.setBorder(border);
            }
            editorComponent.setOpaque(false);
            return editorComponent;
        }
        
        @Override
        public void setItem(final Object item) {
            super.setItem(item);
            final Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            if (focusOwner == this.editor || focusOwner == this.editor.getParent()) {
                this.editor.selectAll();
            }
        }
    }
    
    private static class WindowsComboBoxRenderer extends UIResource
    {
        private static final Object BORDER_KEY;
        private static final Border NULL_BORDER;
        
        @Override
        public Component getListCellRendererComponent(final JList list, final Object o, final int n, final boolean b, final boolean b2) {
            final Component listCellRendererComponent = super.getListCellRendererComponent(list, o, n, b, b2);
            if (listCellRendererComponent instanceof JComponent) {
                final JComponent component = (JComponent)listCellRendererComponent;
                if (n == -1 && b) {
                    final Border border = component.getBorder();
                    component.setBorder(new WindowsBorders.DashedBorder(list.getForeground()));
                    if (component.getClientProperty(WindowsComboBoxRenderer.BORDER_KEY) == null) {
                        component.putClientProperty(WindowsComboBoxRenderer.BORDER_KEY, (border == null) ? WindowsComboBoxRenderer.NULL_BORDER : border);
                    }
                }
                else if (component.getBorder() instanceof WindowsBorders.DashedBorder) {
                    final Object clientProperty = component.getClientProperty(WindowsComboBoxRenderer.BORDER_KEY);
                    if (clientProperty instanceof Border) {
                        component.setBorder((clientProperty == WindowsComboBoxRenderer.NULL_BORDER) ? null : ((Border)clientProperty));
                    }
                    component.putClientProperty(WindowsComboBoxRenderer.BORDER_KEY, null);
                }
                if (n == -1) {
                    component.setOpaque(false);
                    component.setForeground(list.getForeground());
                }
                else {
                    component.setOpaque(true);
                }
            }
            return listCellRendererComponent;
        }
        
        static {
            BORDER_KEY = new StringUIClientPropertyKey("BORDER_KEY");
            NULL_BORDER = new EmptyBorder(0, 0, 0, 0);
        }
    }
}
