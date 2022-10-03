package javax.swing;

import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleContext;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;

public class JCheckBoxMenuItem extends JMenuItem implements SwingConstants, Accessible
{
    private static final String uiClassID = "CheckBoxMenuItemUI";
    
    public JCheckBoxMenuItem() {
        this(null, null, false);
    }
    
    public JCheckBoxMenuItem(final Icon icon) {
        this(null, icon, false);
    }
    
    public JCheckBoxMenuItem(final String s) {
        this(s, null, false);
    }
    
    public JCheckBoxMenuItem(final Action action) {
        this();
        this.setAction(action);
    }
    
    public JCheckBoxMenuItem(final String s, final Icon icon) {
        this(s, icon, false);
    }
    
    public JCheckBoxMenuItem(final String s, final boolean b) {
        this(s, null, b);
    }
    
    public JCheckBoxMenuItem(final String s, final Icon icon, final boolean selected) {
        super(s, icon);
        this.setModel(new JToggleButton.ToggleButtonModel());
        this.setSelected(selected);
        this.setFocusable(false);
    }
    
    @Override
    public String getUIClassID() {
        return "CheckBoxMenuItemUI";
    }
    
    public boolean getState() {
        return this.isSelected();
    }
    
    public synchronized void setState(final boolean selected) {
        this.setSelected(selected);
    }
    
    @Override
    public Object[] getSelectedObjects() {
        if (!this.isSelected()) {
            return null;
        }
        return new Object[] { this.getText() };
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.getUIClassID().equals("CheckBoxMenuItemUI")) {
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
    boolean shouldUpdateSelectedStateFromAction() {
        return true;
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJCheckBoxMenuItem();
        }
        return this.accessibleContext;
    }
    
    protected class AccessibleJCheckBoxMenuItem extends AccessibleJMenuItem
    {
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.CHECK_BOX;
        }
    }
}
