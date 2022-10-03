package javax.swing;

import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleContext;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;

public class JRadioButtonMenuItem extends JMenuItem implements Accessible
{
    private static final String uiClassID = "RadioButtonMenuItemUI";
    
    public JRadioButtonMenuItem() {
        this(null, null, false);
    }
    
    public JRadioButtonMenuItem(final Icon icon) {
        this(null, icon, false);
    }
    
    public JRadioButtonMenuItem(final String s) {
        this(s, null, false);
    }
    
    public JRadioButtonMenuItem(final Action action) {
        this();
        this.setAction(action);
    }
    
    public JRadioButtonMenuItem(final String s, final Icon icon) {
        this(s, icon, false);
    }
    
    public JRadioButtonMenuItem(final String s, final boolean selected) {
        this(s);
        this.setSelected(selected);
    }
    
    public JRadioButtonMenuItem(final Icon icon, final boolean b) {
        this(null, icon, b);
    }
    
    public JRadioButtonMenuItem(final String s, final Icon icon, final boolean selected) {
        super(s, icon);
        this.setModel(new JToggleButton.ToggleButtonModel());
        this.setSelected(selected);
        this.setFocusable(false);
    }
    
    @Override
    public String getUIClassID() {
        return "RadioButtonMenuItemUI";
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.getUIClassID().equals("RadioButtonMenuItemUI")) {
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
            this.accessibleContext = new AccessibleJRadioButtonMenuItem();
        }
        return this.accessibleContext;
    }
    
    protected class AccessibleJRadioButtonMenuItem extends AccessibleJMenuItem
    {
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.RADIO_BUTTON;
        }
    }
}
