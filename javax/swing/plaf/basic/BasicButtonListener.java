package javax.swing.plaf.basic;

import java.awt.event.ActionEvent;
import sun.swing.UIAction;
import java.awt.event.MouseEvent;
import javax.swing.ButtonModel;
import javax.swing.JRootPane;
import javax.swing.JButton;
import java.awt.event.FocusEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.KeyStroke;
import javax.swing.plaf.ComponentInputMapUIResource;
import sun.swing.DefaultLookup;
import javax.swing.plaf.ComponentUI;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.SwingUtilities;
import javax.swing.JComponent;
import java.beans.PropertyChangeEvent;
import javax.swing.AbstractButton;
import javax.swing.Action;
import java.beans.PropertyChangeListener;
import javax.swing.event.ChangeListener;
import java.awt.event.FocusListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;

public class BasicButtonListener implements MouseListener, MouseMotionListener, FocusListener, ChangeListener, PropertyChangeListener
{
    private long lastPressedTimestamp;
    private boolean shouldDiscardRelease;
    
    static void loadActionMap(final LazyActionMap lazyActionMap) {
        lazyActionMap.put(new Actions("pressed"));
        lazyActionMap.put(new Actions("released"));
    }
    
    public BasicButtonListener(final AbstractButton abstractButton) {
        this.lastPressedTimestamp = -1L;
        this.shouldDiscardRelease = false;
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        final String propertyName = propertyChangeEvent.getPropertyName();
        if (propertyName == "mnemonic") {
            this.updateMnemonicBinding((AbstractButton)propertyChangeEvent.getSource());
        }
        else if (propertyName == "contentAreaFilled") {
            this.checkOpacity((AbstractButton)propertyChangeEvent.getSource());
        }
        else if (propertyName == "text" || "font" == propertyName || "foreground" == propertyName) {
            final AbstractButton abstractButton = (AbstractButton)propertyChangeEvent.getSource();
            BasicHTML.updateRenderer(abstractButton, abstractButton.getText());
        }
    }
    
    protected void checkOpacity(final AbstractButton abstractButton) {
        abstractButton.setOpaque(abstractButton.isContentAreaFilled());
    }
    
    public void installKeyboardActions(final JComponent component) {
        this.updateMnemonicBinding((AbstractButton)component);
        LazyActionMap.installLazyActionMap(component, BasicButtonListener.class, "Button.actionMap");
        SwingUtilities.replaceUIInputMap(component, 0, this.getInputMap(0, component));
    }
    
    public void uninstallKeyboardActions(final JComponent component) {
        SwingUtilities.replaceUIInputMap(component, 2, null);
        SwingUtilities.replaceUIInputMap(component, 0, null);
        SwingUtilities.replaceUIActionMap(component, null);
    }
    
    InputMap getInputMap(final int n, final JComponent component) {
        if (n == 0) {
            final BasicButtonUI basicButtonUI = (BasicButtonUI)BasicLookAndFeel.getUIOfType(((AbstractButton)component).getUI(), BasicButtonUI.class);
            if (basicButtonUI != null) {
                return (InputMap)DefaultLookup.get(component, basicButtonUI, basicButtonUI.getPropertyPrefix() + "focusInputMap");
            }
        }
        return null;
    }
    
    void updateMnemonicBinding(final AbstractButton abstractButton) {
        final int mnemonic = abstractButton.getMnemonic();
        if (mnemonic != 0) {
            InputMap uiInputMap = SwingUtilities.getUIInputMap(abstractButton, 2);
            if (uiInputMap == null) {
                uiInputMap = new ComponentInputMapUIResource(abstractButton);
                SwingUtilities.replaceUIInputMap(abstractButton, 2, uiInputMap);
            }
            uiInputMap.clear();
            uiInputMap.put(KeyStroke.getKeyStroke(mnemonic, BasicLookAndFeel.getFocusAcceleratorKeyMask(), false), "pressed");
            uiInputMap.put(KeyStroke.getKeyStroke(mnemonic, BasicLookAndFeel.getFocusAcceleratorKeyMask(), true), "released");
            uiInputMap.put(KeyStroke.getKeyStroke(mnemonic, 0, true), "released");
        }
        else {
            final InputMap uiInputMap2 = SwingUtilities.getUIInputMap(abstractButton, 2);
            if (uiInputMap2 != null) {
                uiInputMap2.clear();
            }
        }
    }
    
    @Override
    public void stateChanged(final ChangeEvent changeEvent) {
        ((AbstractButton)changeEvent.getSource()).repaint();
    }
    
    @Override
    public void focusGained(final FocusEvent focusEvent) {
        final AbstractButton abstractButton = (AbstractButton)focusEvent.getSource();
        if (abstractButton instanceof JButton && ((JButton)abstractButton).isDefaultCapable()) {
            final JRootPane rootPane = abstractButton.getRootPane();
            if (rootPane != null) {
                final BasicButtonUI basicButtonUI = (BasicButtonUI)BasicLookAndFeel.getUIOfType(abstractButton.getUI(), BasicButtonUI.class);
                if (basicButtonUI != null && DefaultLookup.getBoolean(abstractButton, basicButtonUI, basicButtonUI.getPropertyPrefix() + "defaultButtonFollowsFocus", true)) {
                    rootPane.putClientProperty("temporaryDefaultButton", abstractButton);
                    rootPane.setDefaultButton((JButton)abstractButton);
                    rootPane.putClientProperty("temporaryDefaultButton", null);
                }
            }
        }
        abstractButton.repaint();
    }
    
    @Override
    public void focusLost(final FocusEvent focusEvent) {
        final AbstractButton abstractButton = (AbstractButton)focusEvent.getSource();
        final JRootPane rootPane = abstractButton.getRootPane();
        if (rootPane != null) {
            final JButton defaultButton = (JButton)rootPane.getClientProperty("initialDefaultButton");
            if (abstractButton != defaultButton) {
                final BasicButtonUI basicButtonUI = (BasicButtonUI)BasicLookAndFeel.getUIOfType(abstractButton.getUI(), BasicButtonUI.class);
                if (basicButtonUI != null && DefaultLookup.getBoolean(abstractButton, basicButtonUI, basicButtonUI.getPropertyPrefix() + "defaultButtonFollowsFocus", true)) {
                    rootPane.setDefaultButton(defaultButton);
                }
            }
        }
        final ButtonModel model = abstractButton.getModel();
        model.setPressed(false);
        model.setArmed(false);
        abstractButton.repaint();
    }
    
    @Override
    public void mouseMoved(final MouseEvent mouseEvent) {
    }
    
    @Override
    public void mouseDragged(final MouseEvent mouseEvent) {
    }
    
    @Override
    public void mouseClicked(final MouseEvent mouseEvent) {
    }
    
    @Override
    public void mousePressed(final MouseEvent mouseEvent) {
        if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
            final AbstractButton abstractButton = (AbstractButton)mouseEvent.getSource();
            if (abstractButton.contains(mouseEvent.getX(), mouseEvent.getY())) {
                final long multiClickThreshhold = abstractButton.getMultiClickThreshhold();
                final long lastPressedTimestamp = this.lastPressedTimestamp;
                final long when = mouseEvent.getWhen();
                this.lastPressedTimestamp = when;
                final long n = when;
                if (lastPressedTimestamp != -1L && n - lastPressedTimestamp < multiClickThreshhold) {
                    this.shouldDiscardRelease = true;
                    return;
                }
                final ButtonModel model = abstractButton.getModel();
                if (!model.isEnabled()) {
                    return;
                }
                if (!model.isArmed()) {
                    model.setArmed(true);
                }
                model.setPressed(true);
                if (!abstractButton.hasFocus() && abstractButton.isRequestFocusEnabled()) {
                    abstractButton.requestFocus();
                }
            }
        }
    }
    
    @Override
    public void mouseReleased(final MouseEvent mouseEvent) {
        if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
            if (this.shouldDiscardRelease) {
                this.shouldDiscardRelease = false;
                return;
            }
            final ButtonModel model = ((AbstractButton)mouseEvent.getSource()).getModel();
            model.setPressed(false);
            model.setArmed(false);
        }
    }
    
    @Override
    public void mouseEntered(final MouseEvent mouseEvent) {
        final AbstractButton abstractButton = (AbstractButton)mouseEvent.getSource();
        final ButtonModel model = abstractButton.getModel();
        if (abstractButton.isRolloverEnabled() && !SwingUtilities.isLeftMouseButton(mouseEvent)) {
            model.setRollover(true);
        }
        if (model.isPressed()) {
            model.setArmed(true);
        }
    }
    
    @Override
    public void mouseExited(final MouseEvent mouseEvent) {
        final AbstractButton abstractButton = (AbstractButton)mouseEvent.getSource();
        final ButtonModel model = abstractButton.getModel();
        if (abstractButton.isRolloverEnabled()) {
            model.setRollover(false);
        }
        model.setArmed(false);
    }
    
    private static class Actions extends UIAction
    {
        private static final String PRESS = "pressed";
        private static final String RELEASE = "released";
        
        Actions(final String s) {
            super(s);
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final AbstractButton abstractButton = (AbstractButton)actionEvent.getSource();
            final String name = this.getName();
            if (name == "pressed") {
                final ButtonModel model = abstractButton.getModel();
                model.setArmed(true);
                model.setPressed(true);
                if (!abstractButton.hasFocus()) {
                    abstractButton.requestFocus();
                }
            }
            else if (name == "released") {
                final ButtonModel model2 = abstractButton.getModel();
                model2.setPressed(false);
                model2.setArmed(false);
            }
        }
        
        @Override
        public boolean isEnabled(final Object o) {
            return o == null || !(o instanceof AbstractButton) || ((AbstractButton)o).getModel().isEnabled();
        }
    }
}
