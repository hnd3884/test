package javax.swing.plaf.basic;

import java.awt.event.KeyEvent;
import java.awt.KeyboardFocusManager;
import java.util.Enumeration;
import javax.swing.ButtonGroup;
import javax.swing.DefaultButtonModel;
import java.awt.FocusTraversalPolicy;
import java.awt.Container;
import java.util.HashSet;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.awt.FontMetrics;
import java.awt.Font;
import javax.swing.ButtonModel;
import java.awt.Shape;
import javax.swing.text.View;
import java.awt.Component;
import javax.swing.SwingUtilities;
import sun.swing.SwingUtilities2;
import java.awt.Graphics;
import javax.swing.KeyStroke;
import javax.swing.Action;
import javax.swing.JRadioButton;
import javax.swing.UIManager;
import javax.swing.AbstractButton;
import sun.awt.AppContext;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.event.KeyListener;
import javax.swing.Icon;

public class BasicRadioButtonUI extends BasicToggleButtonUI
{
    private static final Object BASIC_RADIO_BUTTON_UI_KEY;
    protected Icon icon;
    private boolean defaults_initialized;
    private static final String propertyPrefix = "RadioButton.";
    private KeyListener keyListener;
    private static Dimension size;
    private static Rectangle viewRect;
    private static Rectangle iconRect;
    private static Rectangle textRect;
    private static Rectangle prefViewRect;
    private static Rectangle prefIconRect;
    private static Rectangle prefTextRect;
    private static Insets prefInsets;
    
    public BasicRadioButtonUI() {
        this.defaults_initialized = false;
        this.keyListener = null;
    }
    
    public static ComponentUI createUI(final JComponent component) {
        final AppContext appContext = AppContext.getAppContext();
        BasicRadioButtonUI basicRadioButtonUI = (BasicRadioButtonUI)appContext.get(BasicRadioButtonUI.BASIC_RADIO_BUTTON_UI_KEY);
        if (basicRadioButtonUI == null) {
            basicRadioButtonUI = new BasicRadioButtonUI();
            appContext.put(BasicRadioButtonUI.BASIC_RADIO_BUTTON_UI_KEY, basicRadioButtonUI);
        }
        return basicRadioButtonUI;
    }
    
    @Override
    protected String getPropertyPrefix() {
        return "RadioButton.";
    }
    
    @Override
    protected void installDefaults(final AbstractButton abstractButton) {
        super.installDefaults(abstractButton);
        if (!this.defaults_initialized) {
            this.icon = UIManager.getIcon(this.getPropertyPrefix() + "icon");
            this.defaults_initialized = true;
        }
    }
    
    @Override
    protected void uninstallDefaults(final AbstractButton abstractButton) {
        super.uninstallDefaults(abstractButton);
        this.defaults_initialized = false;
    }
    
    public Icon getDefaultIcon() {
        return this.icon;
    }
    
    @Override
    protected void installListeners(final AbstractButton abstractButton) {
        super.installListeners(abstractButton);
        if (!(abstractButton instanceof JRadioButton)) {
            return;
        }
        abstractButton.addKeyListener(this.keyListener = this.createKeyListener());
        abstractButton.setFocusTraversalKeysEnabled(false);
        abstractButton.getActionMap().put("Previous", new SelectPreviousBtn());
        abstractButton.getActionMap().put("Next", new SelectNextBtn());
        abstractButton.getInputMap(1).put(KeyStroke.getKeyStroke("UP"), "Previous");
        abstractButton.getInputMap(1).put(KeyStroke.getKeyStroke("DOWN"), "Next");
        abstractButton.getInputMap(1).put(KeyStroke.getKeyStroke("LEFT"), "Previous");
        abstractButton.getInputMap(1).put(KeyStroke.getKeyStroke("RIGHT"), "Next");
    }
    
    @Override
    protected void uninstallListeners(final AbstractButton abstractButton) {
        super.uninstallListeners(abstractButton);
        if (!(abstractButton instanceof JRadioButton)) {
            return;
        }
        abstractButton.getActionMap().remove("Previous");
        abstractButton.getActionMap().remove("Next");
        abstractButton.getInputMap(1).remove(KeyStroke.getKeyStroke("UP"));
        abstractButton.getInputMap(1).remove(KeyStroke.getKeyStroke("DOWN"));
        abstractButton.getInputMap(1).remove(KeyStroke.getKeyStroke("LEFT"));
        abstractButton.getInputMap(1).remove(KeyStroke.getKeyStroke("RIGHT"));
        if (this.keyListener != null) {
            abstractButton.removeKeyListener(this.keyListener);
            this.keyListener = null;
        }
    }
    
    @Override
    public synchronized void paint(final Graphics graphics, final JComponent component) {
        final AbstractButton abstractButton = (AbstractButton)component;
        final ButtonModel model = abstractButton.getModel();
        final Font font = component.getFont();
        graphics.setFont(font);
        final FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(component, graphics, font);
        final Insets insets = component.getInsets();
        BasicRadioButtonUI.size = abstractButton.getSize(BasicRadioButtonUI.size);
        BasicRadioButtonUI.viewRect.x = insets.left;
        BasicRadioButtonUI.viewRect.y = insets.top;
        BasicRadioButtonUI.viewRect.width = BasicRadioButtonUI.size.width - (insets.right + BasicRadioButtonUI.viewRect.x);
        BasicRadioButtonUI.viewRect.height = BasicRadioButtonUI.size.height - (insets.bottom + BasicRadioButtonUI.viewRect.y);
        final Rectangle iconRect = BasicRadioButtonUI.iconRect;
        final Rectangle iconRect2 = BasicRadioButtonUI.iconRect;
        final Rectangle iconRect3 = BasicRadioButtonUI.iconRect;
        final Rectangle iconRect4 = BasicRadioButtonUI.iconRect;
        final int n = 0;
        iconRect4.height = n;
        iconRect3.width = n;
        iconRect2.y = n;
        iconRect.x = n;
        final Rectangle textRect = BasicRadioButtonUI.textRect;
        final Rectangle textRect2 = BasicRadioButtonUI.textRect;
        final Rectangle textRect3 = BasicRadioButtonUI.textRect;
        final Rectangle textRect4 = BasicRadioButtonUI.textRect;
        final int n2 = 0;
        textRect4.height = n2;
        textRect3.width = n2;
        textRect2.y = n2;
        textRect.x = n2;
        Icon icon = abstractButton.getIcon();
        final String layoutCompoundLabel = SwingUtilities.layoutCompoundLabel(component, fontMetrics, abstractButton.getText(), (icon != null) ? icon : this.getDefaultIcon(), abstractButton.getVerticalAlignment(), abstractButton.getHorizontalAlignment(), abstractButton.getVerticalTextPosition(), abstractButton.getHorizontalTextPosition(), BasicRadioButtonUI.viewRect, BasicRadioButtonUI.iconRect, BasicRadioButtonUI.textRect, (abstractButton.getText() == null) ? 0 : abstractButton.getIconTextGap());
        if (component.isOpaque()) {
            graphics.setColor(abstractButton.getBackground());
            graphics.fillRect(0, 0, BasicRadioButtonUI.size.width, BasicRadioButtonUI.size.height);
        }
        if (icon != null) {
            if (!model.isEnabled()) {
                if (model.isSelected()) {
                    icon = abstractButton.getDisabledSelectedIcon();
                }
                else {
                    icon = abstractButton.getDisabledIcon();
                }
            }
            else if (model.isPressed() && model.isArmed()) {
                icon = abstractButton.getPressedIcon();
                if (icon == null) {
                    icon = abstractButton.getSelectedIcon();
                }
            }
            else if (model.isSelected()) {
                if (abstractButton.isRolloverEnabled() && model.isRollover()) {
                    icon = abstractButton.getRolloverSelectedIcon();
                    if (icon == null) {
                        icon = abstractButton.getSelectedIcon();
                    }
                }
                else {
                    icon = abstractButton.getSelectedIcon();
                }
            }
            else if (abstractButton.isRolloverEnabled() && model.isRollover()) {
                icon = abstractButton.getRolloverIcon();
            }
            if (icon == null) {
                icon = abstractButton.getIcon();
            }
            icon.paintIcon(component, graphics, BasicRadioButtonUI.iconRect.x, BasicRadioButtonUI.iconRect.y);
        }
        else {
            this.getDefaultIcon().paintIcon(component, graphics, BasicRadioButtonUI.iconRect.x, BasicRadioButtonUI.iconRect.y);
        }
        if (layoutCompoundLabel != null) {
            final View view = (View)component.getClientProperty("html");
            if (view != null) {
                view.paint(graphics, BasicRadioButtonUI.textRect);
            }
            else {
                this.paintText(graphics, abstractButton, BasicRadioButtonUI.textRect, layoutCompoundLabel);
            }
            if (abstractButton.hasFocus() && abstractButton.isFocusPainted() && BasicRadioButtonUI.textRect.width > 0 && BasicRadioButtonUI.textRect.height > 0) {
                this.paintFocus(graphics, BasicRadioButtonUI.textRect, BasicRadioButtonUI.size);
            }
        }
    }
    
    protected void paintFocus(final Graphics graphics, final Rectangle rectangle, final Dimension dimension) {
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        if (component.getComponentCount() > 0) {
            return null;
        }
        final AbstractButton abstractButton = (AbstractButton)component;
        final String text = abstractButton.getText();
        Icon icon = abstractButton.getIcon();
        if (icon == null) {
            icon = this.getDefaultIcon();
        }
        final FontMetrics fontMetrics = abstractButton.getFontMetrics(abstractButton.getFont());
        final Rectangle prefViewRect = BasicRadioButtonUI.prefViewRect;
        final Rectangle prefViewRect2 = BasicRadioButtonUI.prefViewRect;
        final int n = 0;
        prefViewRect2.y = n;
        prefViewRect.x = n;
        BasicRadioButtonUI.prefViewRect.width = 32767;
        BasicRadioButtonUI.prefViewRect.height = 32767;
        final Rectangle prefIconRect = BasicRadioButtonUI.prefIconRect;
        final Rectangle prefIconRect2 = BasicRadioButtonUI.prefIconRect;
        final Rectangle prefIconRect3 = BasicRadioButtonUI.prefIconRect;
        final Rectangle prefIconRect4 = BasicRadioButtonUI.prefIconRect;
        final int n2 = 0;
        prefIconRect4.height = n2;
        prefIconRect3.width = n2;
        prefIconRect2.y = n2;
        prefIconRect.x = n2;
        final Rectangle prefTextRect = BasicRadioButtonUI.prefTextRect;
        final Rectangle prefTextRect2 = BasicRadioButtonUI.prefTextRect;
        final Rectangle prefTextRect3 = BasicRadioButtonUI.prefTextRect;
        final Rectangle prefTextRect4 = BasicRadioButtonUI.prefTextRect;
        final int n3 = 0;
        prefTextRect4.height = n3;
        prefTextRect3.width = n3;
        prefTextRect2.y = n3;
        prefTextRect.x = n3;
        SwingUtilities.layoutCompoundLabel(component, fontMetrics, text, icon, abstractButton.getVerticalAlignment(), abstractButton.getHorizontalAlignment(), abstractButton.getVerticalTextPosition(), abstractButton.getHorizontalTextPosition(), BasicRadioButtonUI.prefViewRect, BasicRadioButtonUI.prefIconRect, BasicRadioButtonUI.prefTextRect, (text == null) ? 0 : abstractButton.getIconTextGap());
        final int min = Math.min(BasicRadioButtonUI.prefIconRect.x, BasicRadioButtonUI.prefTextRect.x);
        final int max = Math.max(BasicRadioButtonUI.prefIconRect.x + BasicRadioButtonUI.prefIconRect.width, BasicRadioButtonUI.prefTextRect.x + BasicRadioButtonUI.prefTextRect.width);
        final int min2 = Math.min(BasicRadioButtonUI.prefIconRect.y, BasicRadioButtonUI.prefTextRect.y);
        final int max2 = Math.max(BasicRadioButtonUI.prefIconRect.y + BasicRadioButtonUI.prefIconRect.height, BasicRadioButtonUI.prefTextRect.y + BasicRadioButtonUI.prefTextRect.height);
        final int n4 = max - min;
        final int n5 = max2 - min2;
        BasicRadioButtonUI.prefInsets = abstractButton.getInsets(BasicRadioButtonUI.prefInsets);
        return new Dimension(n4 + (BasicRadioButtonUI.prefInsets.left + BasicRadioButtonUI.prefInsets.right), n5 + (BasicRadioButtonUI.prefInsets.top + BasicRadioButtonUI.prefInsets.bottom));
    }
    
    private KeyListener createKeyListener() {
        if (this.keyListener == null) {
            this.keyListener = new KeyHandler();
        }
        return this.keyListener;
    }
    
    private boolean isValidRadioButtonObj(final Object o) {
        return o instanceof JRadioButton && ((JRadioButton)o).isVisible() && ((JRadioButton)o).isEnabled();
    }
    
    private void selectRadioButton(final ActionEvent actionEvent, final boolean b) {
        final Object source = actionEvent.getSource();
        if (!this.isValidRadioButtonObj(source)) {
            return;
        }
        new ButtonGroupInfo((JRadioButton)source).selectNewButton(b);
    }
    
    static {
        BASIC_RADIO_BUTTON_UI_KEY = new Object();
        BasicRadioButtonUI.size = new Dimension();
        BasicRadioButtonUI.viewRect = new Rectangle();
        BasicRadioButtonUI.iconRect = new Rectangle();
        BasicRadioButtonUI.textRect = new Rectangle();
        BasicRadioButtonUI.prefViewRect = new Rectangle();
        BasicRadioButtonUI.prefIconRect = new Rectangle();
        BasicRadioButtonUI.prefTextRect = new Rectangle();
        BasicRadioButtonUI.prefInsets = new Insets(0, 0, 0, 0);
    }
    
    private class SelectPreviousBtn extends AbstractAction
    {
        public SelectPreviousBtn() {
            super("Previous");
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            BasicRadioButtonUI.this.selectRadioButton(actionEvent, false);
        }
    }
    
    private class SelectNextBtn extends AbstractAction
    {
        public SelectNextBtn() {
            super("Next");
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            BasicRadioButtonUI.this.selectRadioButton(actionEvent, true);
        }
    }
    
    private class ButtonGroupInfo
    {
        JRadioButton activeBtn;
        JRadioButton firstBtn;
        JRadioButton lastBtn;
        JRadioButton previousBtn;
        JRadioButton nextBtn;
        HashSet<JRadioButton> btnsInGroup;
        boolean srcFound;
        
        public ButtonGroupInfo(final JRadioButton activeBtn) {
            this.activeBtn = null;
            this.firstBtn = null;
            this.lastBtn = null;
            this.previousBtn = null;
            this.nextBtn = null;
            this.btnsInGroup = null;
            this.srcFound = false;
            this.activeBtn = activeBtn;
            this.btnsInGroup = new HashSet<JRadioButton>();
        }
        
        boolean containsInGroup(final Object o) {
            return this.btnsInGroup.contains(o);
        }
        
        Component getFocusTransferBaseComponent(final boolean b) {
            JRadioButton activeBtn = this.activeBtn;
            final Container focusCycleRootAncestor = activeBtn.getFocusCycleRootAncestor();
            if (focusCycleRootAncestor != null) {
                final FocusTraversalPolicy focusTraversalPolicy = focusCycleRootAncestor.getFocusTraversalPolicy();
                if (this.containsInGroup(b ? focusTraversalPolicy.getComponentAfter(focusCycleRootAncestor, this.activeBtn) : focusTraversalPolicy.getComponentBefore(focusCycleRootAncestor, this.activeBtn))) {
                    activeBtn = (b ? this.lastBtn : this.firstBtn);
                }
            }
            return activeBtn;
        }
        
        boolean getButtonGroupInfo() {
            if (this.activeBtn == null) {
                return false;
            }
            this.btnsInGroup.clear();
            final ButtonModel model = this.activeBtn.getModel();
            if (!(model instanceof DefaultButtonModel)) {
                return false;
            }
            final ButtonGroup group = ((DefaultButtonModel)model).getGroup();
            if (group == null) {
                return false;
            }
            final Enumeration<AbstractButton> elements = group.getElements();
            if (elements == null) {
                return false;
            }
            while (elements.hasMoreElements()) {
                final AbstractButton abstractButton = elements.nextElement();
                if (!BasicRadioButtonUI.this.isValidRadioButtonObj(abstractButton)) {
                    continue;
                }
                this.btnsInGroup.add((JRadioButton)abstractButton);
                if (null == this.firstBtn) {
                    this.firstBtn = (JRadioButton)abstractButton;
                }
                if (this.activeBtn == abstractButton) {
                    this.srcFound = true;
                }
                else if (!this.srcFound) {
                    this.previousBtn = (JRadioButton)abstractButton;
                }
                else if (this.nextBtn == null) {
                    this.nextBtn = (JRadioButton)abstractButton;
                }
                this.lastBtn = (JRadioButton)abstractButton;
            }
            return true;
        }
        
        void selectNewButton(final boolean b) {
            if (!this.getButtonGroupInfo()) {
                return;
            }
            if (this.srcFound) {
                JRadioButton radioButton;
                if (b) {
                    radioButton = ((null == this.nextBtn) ? this.firstBtn : this.nextBtn);
                }
                else {
                    radioButton = ((null == this.previousBtn) ? this.lastBtn : this.previousBtn);
                }
                if (radioButton != null && radioButton != this.activeBtn) {
                    final ButtonModel model = radioButton.getModel();
                    model.setPressed(true);
                    model.setArmed(true);
                    radioButton.requestFocusInWindow();
                    radioButton.setSelected(true);
                    model.setPressed(false);
                    model.setArmed(false);
                }
            }
        }
        
        void jumpToNextComponent(final boolean b) {
            if (!this.getButtonGroupInfo()) {
                if (this.activeBtn == null) {
                    return;
                }
                this.lastBtn = this.activeBtn;
                this.firstBtn = this.activeBtn;
            }
            final JRadioButton activeBtn = this.activeBtn;
            final Component focusTransferBaseComponent = this.getFocusTransferBaseComponent(b);
            if (focusTransferBaseComponent != null) {
                if (b) {
                    KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent(focusTransferBaseComponent);
                }
                else {
                    KeyboardFocusManager.getCurrentKeyboardFocusManager().focusPreviousComponent(focusTransferBaseComponent);
                }
            }
        }
    }
    
    private class KeyHandler implements KeyListener
    {
        @Override
        public void keyPressed(final KeyEvent keyEvent) {
            if (keyEvent.getKeyCode() == 9) {
                final Object source = keyEvent.getSource();
                if (BasicRadioButtonUI.this.isValidRadioButtonObj(source)) {
                    keyEvent.consume();
                    new ButtonGroupInfo((JRadioButton)source).jumpToNextComponent(!keyEvent.isShiftDown());
                }
            }
        }
        
        @Override
        public void keyReleased(final KeyEvent keyEvent) {
        }
        
        @Override
        public void keyTyped(final KeyEvent keyEvent) {
        }
    }
}
