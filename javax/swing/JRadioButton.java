package javax.swing;

import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleContext;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.swing.plaf.ButtonUI;
import javax.accessibility.Accessible;

public class JRadioButton extends JToggleButton implements Accessible
{
    private static final String uiClassID = "RadioButtonUI";
    
    public JRadioButton() {
        this(null, null, false);
    }
    
    public JRadioButton(final Icon icon) {
        this(null, icon, false);
    }
    
    public JRadioButton(final Action action) {
        this();
        this.setAction(action);
    }
    
    public JRadioButton(final Icon icon, final boolean b) {
        this(null, icon, b);
    }
    
    public JRadioButton(final String s) {
        this(s, null, false);
    }
    
    public JRadioButton(final String s, final boolean b) {
        this(s, null, b);
    }
    
    public JRadioButton(final String s, final Icon icon) {
        this(s, icon, false);
    }
    
    public JRadioButton(final String s, final Icon icon, final boolean b) {
        super(s, icon, b);
        this.setBorderPainted(false);
        this.setHorizontalAlignment(10);
    }
    
    @Override
    public void updateUI() {
        this.setUI((ButtonUI)UIManager.getUI(this));
    }
    
    @Override
    public String getUIClassID() {
        return "RadioButtonUI";
    }
    
    @Override
    void setIconFromAction(final Action action) {
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.getUIClassID().equals("RadioButtonUI")) {
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
            this.accessibleContext = new AccessibleJRadioButton();
        }
        return this.accessibleContext;
    }
    
    protected class AccessibleJRadioButton extends AccessibleJToggleButton
    {
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.RADIO_BUTTON;
        }
    }
}
