package javax.swing;

import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleContext;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.awt.Component;
import javax.swing.plaf.ButtonUI;
import java.beans.ConstructorProperties;
import javax.accessibility.Accessible;

public class JButton extends AbstractButton implements Accessible
{
    private static final String uiClassID = "ButtonUI";
    
    public JButton() {
        this(null, null);
    }
    
    public JButton(final Icon icon) {
        this(null, icon);
    }
    
    @ConstructorProperties({ "text" })
    public JButton(final String s) {
        this(s, null);
    }
    
    public JButton(final Action action) {
        this();
        this.setAction(action);
    }
    
    public JButton(final String s, final Icon icon) {
        this.setModel(new DefaultButtonModel());
        this.init(s, icon);
    }
    
    @Override
    public void updateUI() {
        this.setUI((ButtonUI)UIManager.getUI(this));
    }
    
    @Override
    public String getUIClassID() {
        return "ButtonUI";
    }
    
    public boolean isDefaultButton() {
        final JRootPane rootPane = SwingUtilities.getRootPane(this);
        return rootPane != null && rootPane.getDefaultButton() == this;
    }
    
    public boolean isDefaultCapable() {
        return this.defaultCapable;
    }
    
    public void setDefaultCapable(final boolean defaultCapable) {
        this.firePropertyChange("defaultCapable", this.defaultCapable, this.defaultCapable = defaultCapable);
    }
    
    @Override
    public void removeNotify() {
        final JRootPane rootPane = SwingUtilities.getRootPane(this);
        if (rootPane != null && rootPane.getDefaultButton() == this) {
            rootPane.setDefaultButton(null);
        }
        super.removeNotify();
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.getUIClassID().equals("ButtonUI")) {
            final byte b = (byte)(JComponent.getWriteObjCounter(this) - 1);
            JComponent.setWriteObjCounter(this, b);
            if (b == 0 && this.ui != null) {
                this.ui.installUI(this);
            }
        }
    }
    
    @Override
    protected String paramString() {
        return super.paramString() + ",defaultCapable=" + (this.defaultCapable ? "true" : "false");
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJButton();
        }
        return this.accessibleContext;
    }
    
    protected class AccessibleJButton extends AccessibleAbstractButton
    {
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.PUSH_BUTTON;
        }
    }
}
