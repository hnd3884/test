package javax.swing;

import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import java.awt.event.ItemListener;
import java.awt.AWTEvent;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.EventQueue;
import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;
import javax.accessibility.AccessibleContext;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.swing.plaf.ButtonUI;
import javax.accessibility.Accessible;

public class JToggleButton extends AbstractButton implements Accessible
{
    private static final String uiClassID = "ToggleButtonUI";
    
    public JToggleButton() {
        this(null, null, false);
    }
    
    public JToggleButton(final Icon icon) {
        this(null, icon, false);
    }
    
    public JToggleButton(final Icon icon, final boolean b) {
        this(null, icon, b);
    }
    
    public JToggleButton(final String s) {
        this(s, null, false);
    }
    
    public JToggleButton(final String s, final boolean b) {
        this(s, null, b);
    }
    
    public JToggleButton(final Action action) {
        this();
        this.setAction(action);
    }
    
    public JToggleButton(final String s, final Icon icon) {
        this(s, icon, false);
    }
    
    public JToggleButton(final String s, final Icon icon, final boolean selected) {
        this.setModel(new ToggleButtonModel());
        this.model.setSelected(selected);
        this.init(s, icon);
    }
    
    @Override
    public void updateUI() {
        this.setUI((ButtonUI)UIManager.getUI(this));
    }
    
    @Override
    public String getUIClassID() {
        return "ToggleButtonUI";
    }
    
    @Override
    boolean shouldUpdateSelectedStateFromAction() {
        return true;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.getUIClassID().equals("ToggleButtonUI")) {
            final byte b = (byte)(JComponent.getWriteObjCounter(this) - 1);
            JComponent.setWriteObjCounter(this, b);
            if (b == 0 && this.ui != null) {
                this.ui.installUI(this);
            }
        }
    }
    
    @Override
    protected String paramString() {
        return super.paramString();
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJToggleButton();
        }
        return this.accessibleContext;
    }
    
    public static class ToggleButtonModel extends DefaultButtonModel
    {
        @Override
        public boolean isSelected() {
            return (this.stateMask & 0x2) != 0x0;
        }
        
        @Override
        public void setSelected(boolean selected) {
            final ButtonGroup group = this.getGroup();
            if (group != null) {
                group.setSelected(this, selected);
                selected = group.isSelected(this);
            }
            if (this.isSelected() == selected) {
                return;
            }
            if (selected) {
                this.stateMask |= 0x2;
            }
            else {
                this.stateMask &= 0xFFFFFFFD;
            }
            this.fireStateChanged();
            this.fireItemStateChanged(new ItemEvent(this, 701, this, this.isSelected() ? 1 : 2));
        }
        
        @Override
        public void setPressed(final boolean b) {
            if (this.isPressed() == b || !this.isEnabled()) {
                return;
            }
            if (!b && this.isArmed()) {
                this.setSelected(!this.isSelected());
            }
            if (b) {
                this.stateMask |= 0x4;
            }
            else {
                this.stateMask &= 0xFFFFFFFB;
            }
            this.fireStateChanged();
            if (!this.isPressed() && this.isArmed()) {
                int n = 0;
                final AWTEvent currentEvent = EventQueue.getCurrentEvent();
                if (currentEvent instanceof InputEvent) {
                    n = ((InputEvent)currentEvent).getModifiers();
                }
                else if (currentEvent instanceof ActionEvent) {
                    n = ((ActionEvent)currentEvent).getModifiers();
                }
                this.fireActionPerformed(new ActionEvent(this, 1001, this.getActionCommand(), EventQueue.getMostRecentEventTime(), n));
            }
        }
    }
    
    protected class AccessibleJToggleButton extends AccessibleAbstractButton implements ItemListener
    {
        public AccessibleJToggleButton() {
            JToggleButton.this.addItemListener(this);
        }
        
        @Override
        public void itemStateChanged(final ItemEvent itemEvent) {
            final JToggleButton toggleButton = (JToggleButton)itemEvent.getSource();
            if (JToggleButton.this.accessibleContext != null) {
                if (toggleButton.isSelected()) {
                    JToggleButton.this.accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.CHECKED);
                }
                else {
                    JToggleButton.this.accessibleContext.firePropertyChange("AccessibleState", AccessibleState.CHECKED, null);
                }
            }
        }
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.TOGGLE_BUTTON;
        }
    }
}
