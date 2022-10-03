package javax.swing;

import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleContext;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Objects;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ToolTipUI;
import javax.accessibility.Accessible;

public class JToolTip extends JComponent implements Accessible
{
    private static final String uiClassID = "ToolTipUI";
    String tipText;
    JComponent component;
    
    public JToolTip() {
        this.setOpaque(true);
        this.updateUI();
    }
    
    public ToolTipUI getUI() {
        return (ToolTipUI)this.ui;
    }
    
    @Override
    public void updateUI() {
        this.setUI(UIManager.getUI(this));
    }
    
    @Override
    public String getUIClassID() {
        return "ToolTipUI";
    }
    
    public void setTipText(final String tipText) {
        final String tipText2 = this.tipText;
        this.firePropertyChange("tiptext", tipText2, this.tipText = tipText);
        if (!Objects.equals(tipText2, tipText)) {
            this.revalidate();
            this.repaint();
        }
    }
    
    public String getTipText() {
        return this.tipText;
    }
    
    public void setComponent(final JComponent component) {
        this.firePropertyChange("component", this.component, this.component = component);
    }
    
    public JComponent getComponent() {
        return this.component;
    }
    
    @Override
    boolean alwaysOnTop() {
        return true;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.getUIClassID().equals("ToolTipUI")) {
            final byte b = (byte)(JComponent.getWriteObjCounter(this) - 1);
            JComponent.setWriteObjCounter(this, b);
            if (b == 0 && this.ui != null) {
                this.ui.installUI(this);
            }
        }
    }
    
    @Override
    protected String paramString() {
        return super.paramString() + ",tipText=" + ((this.tipText != null) ? this.tipText : "");
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJToolTip();
        }
        return this.accessibleContext;
    }
    
    protected class AccessibleJToolTip extends AccessibleJComponent
    {
        @Override
        public String getAccessibleDescription() {
            String s = this.accessibleDescription;
            if (s == null) {
                s = (String)JToolTip.this.getClientProperty("AccessibleDescription");
            }
            if (s == null) {
                s = JToolTip.this.getTipText();
            }
            return s;
        }
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.TOOL_TIP;
        }
    }
}
