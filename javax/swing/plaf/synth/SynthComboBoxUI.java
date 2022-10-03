package javax.swing.plaf.synth;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.event.PopupMenuEvent;
import java.awt.event.MouseEvent;
import javax.swing.DefaultButtonModel;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import java.awt.Dimension;
import java.awt.Container;
import javax.swing.plaf.UIResource;
import javax.swing.JPanel;
import java.awt.Rectangle;
import java.awt.Graphics;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import java.beans.PropertyChangeEvent;
import javax.swing.ComboBoxEditor;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.event.PopupMenuListener;
import java.awt.event.MouseListener;
import java.awt.Component;
import javax.swing.JComboBox;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.Insets;
import java.beans.PropertyChangeListener;
import javax.swing.plaf.basic.BasicComboBoxUI;

public class SynthComboBoxUI extends BasicComboBoxUI implements PropertyChangeListener, SynthUI
{
    private SynthStyle style;
    private boolean useListColors;
    Insets popupInsets;
    private boolean buttonWhenNotEditable;
    private boolean pressedWhenPopupVisible;
    private ButtonHandler buttonHandler;
    private EditorFocusHandler editorFocusHandler;
    private boolean forceOpaque;
    
    public SynthComboBoxUI() {
        this.forceOpaque = false;
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new SynthComboBoxUI();
    }
    
    @Override
    public void installUI(final JComponent component) {
        this.buttonHandler = new ButtonHandler();
        super.installUI(component);
    }
    
    @Override
    protected void installDefaults() {
        this.updateStyle(this.comboBox);
    }
    
    private void updateStyle(final JComboBox comboBox) {
        final SynthStyle style = this.style;
        final SynthContext context = this.getContext(comboBox, 1);
        this.style = SynthLookAndFeel.updateStyle(context, this);
        if (this.style != style) {
            this.padding = (Insets)this.style.get(context, "ComboBox.padding");
            this.popupInsets = (Insets)this.style.get(context, "ComboBox.popupInsets");
            this.useListColors = this.style.getBoolean(context, "ComboBox.rendererUseListColors", true);
            this.buttonWhenNotEditable = this.style.getBoolean(context, "ComboBox.buttonWhenNotEditable", false);
            this.pressedWhenPopupVisible = this.style.getBoolean(context, "ComboBox.pressedWhenPopupVisible", false);
            this.squareButton = this.style.getBoolean(context, "ComboBox.squareButton", true);
            if (style != null) {
                this.uninstallKeyboardActions();
                this.installKeyboardActions();
            }
            this.forceOpaque = this.style.getBoolean(context, "ComboBox.forceOpaque", false);
        }
        context.dispose();
        if (this.listBox != null) {
            SynthLookAndFeel.updateStyles(this.listBox);
        }
    }
    
    @Override
    protected void installListeners() {
        this.comboBox.addPropertyChangeListener(this);
        this.comboBox.addMouseListener(this.buttonHandler);
        this.editorFocusHandler = new EditorFocusHandler(this.comboBox);
        super.installListeners();
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        if (this.popup instanceof SynthComboPopup) {
            ((SynthComboPopup)this.popup).removePopupMenuListener(this.buttonHandler);
        }
        super.uninstallUI(component);
        this.buttonHandler = null;
    }
    
    @Override
    protected void uninstallDefaults() {
        final SynthContext context = this.getContext(this.comboBox, 1);
        this.style.uninstallDefaults(context);
        context.dispose();
        this.style = null;
    }
    
    @Override
    protected void uninstallListeners() {
        this.editorFocusHandler.unregister();
        this.comboBox.removePropertyChangeListener(this);
        this.comboBox.removeMouseListener(this.buttonHandler);
        this.buttonHandler.pressed = false;
        this.buttonHandler.over = false;
        super.uninstallListeners();
    }
    
    @Override
    public SynthContext getContext(final JComponent component) {
        return this.getContext(component, this.getComponentState(component));
    }
    
    private SynthContext getContext(final JComponent component, final int n) {
        return SynthContext.getContext(component, this.style, n);
    }
    
    private int getComponentState(final JComponent component) {
        if (!(component instanceof JComboBox)) {
            return SynthLookAndFeel.getComponentState(component);
        }
        final JComboBox comboBox = (JComboBox)component;
        if (this.shouldActLikeButton()) {
            int n = 1;
            if (!component.isEnabled()) {
                n = 8;
            }
            if (this.buttonHandler.isPressed()) {
                n |= 0x4;
            }
            if (this.buttonHandler.isRollover()) {
                n |= 0x2;
            }
            if (comboBox.isFocusOwner()) {
                n |= 0x100;
            }
            return n;
        }
        int componentState = SynthLookAndFeel.getComponentState(component);
        if (comboBox.isEditable() && comboBox.getEditor().getEditorComponent().isFocusOwner()) {
            componentState |= 0x100;
        }
        return componentState;
    }
    
    @Override
    protected ComboPopup createPopup() {
        final SynthComboPopup synthComboPopup = new SynthComboPopup(this.comboBox);
        synthComboPopup.addPopupMenuListener(this.buttonHandler);
        return synthComboPopup;
    }
    
    @Override
    protected ListCellRenderer createRenderer() {
        return new SynthComboBoxRenderer();
    }
    
    @Override
    protected ComboBoxEditor createEditor() {
        return new SynthComboBoxEditor();
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if (SynthLookAndFeel.shouldUpdateStyle(propertyChangeEvent)) {
            this.updateStyle(this.comboBox);
        }
    }
    
    @Override
    protected JButton createArrowButton() {
        final SynthArrowButton synthArrowButton = new SynthArrowButton(5);
        synthArrowButton.setName("ComboBox.arrowButton");
        synthArrowButton.setModel(this.buttonHandler);
        return synthArrowButton;
    }
    
    @Override
    public void update(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        SynthLookAndFeel.update(context, graphics);
        context.getPainter().paintComboBoxBackground(context, graphics, 0, 0, component.getWidth(), component.getHeight());
        this.paint(context, graphics);
        context.dispose();
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        this.paint(context, graphics);
        context.dispose();
    }
    
    protected void paint(final SynthContext synthContext, final Graphics graphics) {
        this.hasFocus = this.comboBox.hasFocus();
        if (!this.comboBox.isEditable()) {
            this.paintCurrentValue(graphics, this.rectangleForCurrentValue(), this.hasFocus);
        }
    }
    
    @Override
    public void paintBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        synthContext.getPainter().paintComboBoxBorder(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paintCurrentValue(final Graphics graphics, final Rectangle rectangle, final boolean b) {
        final Component listCellRendererComponent = this.comboBox.getRenderer().getListCellRendererComponent(this.listBox, this.comboBox.getSelectedItem(), -1, false, false);
        boolean b2 = false;
        if (listCellRendererComponent instanceof JPanel) {
            b2 = true;
        }
        if (listCellRendererComponent instanceof UIResource) {
            listCellRendererComponent.setName("ComboBox.renderer");
        }
        final boolean b3 = this.forceOpaque && listCellRendererComponent instanceof JComponent;
        if (b3) {
            ((JComponent)listCellRendererComponent).setOpaque(false);
        }
        int x = rectangle.x;
        int y = rectangle.y;
        int width = rectangle.width;
        int height = rectangle.height;
        if (this.padding != null) {
            x = rectangle.x + this.padding.left;
            y = rectangle.y + this.padding.top;
            width = rectangle.width - (this.padding.left + this.padding.right);
            height = rectangle.height - (this.padding.top + this.padding.bottom);
        }
        this.currentValuePane.paintComponent(graphics, listCellRendererComponent, this.comboBox, x, y, width, height, b2);
        if (b3) {
            ((JComponent)listCellRendererComponent).setOpaque(true);
        }
    }
    
    private boolean shouldActLikeButton() {
        return this.buttonWhenNotEditable && !this.comboBox.isEditable();
    }
    
    @Override
    protected Dimension getDefaultSize() {
        final Dimension sizeForComponent = this.getSizeForComponent(new SynthComboBoxRenderer().getListCellRendererComponent(this.listBox, " ", -1, false, false));
        return new Dimension(sizeForComponent.width, sizeForComponent.height);
    }
    
    private class SynthComboBoxRenderer extends JLabel implements ListCellRenderer<Object>, UIResource
    {
        public SynthComboBoxRenderer() {
            this.setText(" ");
        }
        
        @Override
        public String getName() {
            final String name = super.getName();
            return (name == null) ? "ComboBox.renderer" : name;
        }
        
        @Override
        public Component getListCellRendererComponent(final JList<?> list, final Object o, final int n, final boolean b, final boolean b2) {
            this.setName("ComboBox.listRenderer");
            SynthLookAndFeel.resetSelectedUI();
            if (b) {
                this.setBackground(list.getSelectionBackground());
                this.setForeground(list.getSelectionForeground());
                if (!SynthComboBoxUI.this.useListColors) {
                    SynthLookAndFeel.setSelectedUI((ComponentUI)SynthLookAndFeel.getUIOfType(this.getUI(), SynthLabelUI.class), b, b2, list.isEnabled(), false);
                }
            }
            else {
                this.setBackground(list.getBackground());
                this.setForeground(list.getForeground());
            }
            this.setFont(list.getFont());
            if (o instanceof Icon) {
                this.setIcon((Icon)o);
                this.setText("");
            }
            else {
                String text = (o == null) ? " " : o.toString();
                if ("".equals(text)) {
                    text = " ";
                }
                this.setText(text);
            }
            if (SynthComboBoxUI.this.comboBox != null) {
                this.setEnabled(SynthComboBoxUI.this.comboBox.isEnabled());
                this.setComponentOrientation(SynthComboBoxUI.this.comboBox.getComponentOrientation());
            }
            return this;
        }
        
        @Override
        public void paint(final Graphics graphics) {
            super.paint(graphics);
            SynthLookAndFeel.resetSelectedUI();
        }
    }
    
    private static class SynthComboBoxEditor extends UIResource
    {
        public JTextField createEditorComponent() {
            final JTextField textField = new JTextField("", 9);
            textField.setName("ComboBox.textField");
            return textField;
        }
    }
    
    private final class ButtonHandler extends DefaultButtonModel implements MouseListener, PopupMenuListener
    {
        private boolean over;
        private boolean pressed;
        
        private void updatePressed(final boolean b) {
            this.pressed = (b && this.isEnabled());
            if (SynthComboBoxUI.this.shouldActLikeButton()) {
                SynthComboBoxUI.this.comboBox.repaint();
            }
        }
        
        private void updateOver(final boolean b) {
            final boolean rollover = this.isRollover();
            this.over = (b && this.isEnabled());
            final boolean rollover2 = this.isRollover();
            if (SynthComboBoxUI.this.shouldActLikeButton() && rollover != rollover2) {
                SynthComboBoxUI.this.comboBox.repaint();
            }
        }
        
        @Override
        public boolean isPressed() {
            return (SynthComboBoxUI.this.shouldActLikeButton() ? this.pressed : super.isPressed()) || (SynthComboBoxUI.this.pressedWhenPopupVisible && SynthComboBoxUI.this.comboBox.isPopupVisible());
        }
        
        @Override
        public boolean isArmed() {
            return (SynthComboBoxUI.this.shouldActLikeButton() || (SynthComboBoxUI.this.pressedWhenPopupVisible && SynthComboBoxUI.this.comboBox.isPopupVisible())) ? this.isPressed() : super.isArmed();
        }
        
        @Override
        public boolean isRollover() {
            return SynthComboBoxUI.this.shouldActLikeButton() ? this.over : super.isRollover();
        }
        
        @Override
        public void setPressed(final boolean pressed) {
            super.setPressed(pressed);
            this.updatePressed(pressed);
        }
        
        @Override
        public void setRollover(final boolean rollover) {
            super.setRollover(rollover);
            this.updateOver(rollover);
        }
        
        @Override
        public void mouseEntered(final MouseEvent mouseEvent) {
            this.updateOver(true);
        }
        
        @Override
        public void mouseExited(final MouseEvent mouseEvent) {
            this.updateOver(false);
        }
        
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            this.updatePressed(true);
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
            this.updatePressed(false);
        }
        
        @Override
        public void mouseClicked(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void popupMenuCanceled(final PopupMenuEvent popupMenuEvent) {
            if (SynthComboBoxUI.this.shouldActLikeButton() || SynthComboBoxUI.this.pressedWhenPopupVisible) {
                SynthComboBoxUI.this.comboBox.repaint();
            }
        }
        
        @Override
        public void popupMenuWillBecomeVisible(final PopupMenuEvent popupMenuEvent) {
        }
        
        @Override
        public void popupMenuWillBecomeInvisible(final PopupMenuEvent popupMenuEvent) {
        }
    }
    
    private static class EditorFocusHandler implements FocusListener, PropertyChangeListener
    {
        private JComboBox comboBox;
        private ComboBoxEditor editor;
        private Component editorComponent;
        
        private EditorFocusHandler(final JComboBox comboBox) {
            this.editor = null;
            this.editorComponent = null;
            this.comboBox = comboBox;
            this.editor = comboBox.getEditor();
            if (this.editor != null) {
                this.editorComponent = this.editor.getEditorComponent();
                if (this.editorComponent != null) {
                    this.editorComponent.addFocusListener(this);
                }
            }
            comboBox.addPropertyChangeListener("editor", this);
        }
        
        public void unregister() {
            this.comboBox.removePropertyChangeListener(this);
            if (this.editorComponent != null) {
                this.editorComponent.removeFocusListener(this);
            }
        }
        
        @Override
        public void focusGained(final FocusEvent focusEvent) {
            this.comboBox.repaint();
        }
        
        @Override
        public void focusLost(final FocusEvent focusEvent) {
            this.comboBox.repaint();
        }
        
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final ComboBoxEditor editor = this.comboBox.getEditor();
            if (this.editor != editor) {
                if (this.editorComponent != null) {
                    this.editorComponent.removeFocusListener(this);
                }
                this.editor = editor;
                if (this.editor != null) {
                    this.editorComponent = this.editor.getEditorComponent();
                    if (this.editorComponent != null) {
                        this.editorComponent.addFocusListener(this);
                    }
                }
            }
        }
    }
}
