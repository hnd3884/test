package javax.swing;

import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleContext;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.SeparatorUI;
import javax.accessibility.Accessible;

public class JSeparator extends JComponent implements SwingConstants, Accessible
{
    private static final String uiClassID = "SeparatorUI";
    private int orientation;
    
    public JSeparator() {
        this(0);
    }
    
    public JSeparator(final int orientation) {
        this.orientation = 0;
        this.checkOrientation(orientation);
        this.orientation = orientation;
        this.setFocusable(false);
        this.updateUI();
    }
    
    public SeparatorUI getUI() {
        return (SeparatorUI)this.ui;
    }
    
    public void setUI(final SeparatorUI ui) {
        super.setUI(ui);
    }
    
    @Override
    public void updateUI() {
        this.setUI((SeparatorUI)UIManager.getUI(this));
    }
    
    @Override
    public String getUIClassID() {
        return "SeparatorUI";
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.getUIClassID().equals("SeparatorUI")) {
            final byte b = (byte)(JComponent.getWriteObjCounter(this) - 1);
            JComponent.setWriteObjCounter(this, b);
            if (b == 0 && this.ui != null) {
                this.ui.installUI(this);
            }
        }
    }
    
    public int getOrientation() {
        return this.orientation;
    }
    
    public void setOrientation(final int orientation) {
        if (this.orientation == orientation) {
            return;
        }
        final int orientation2 = this.orientation;
        this.checkOrientation(orientation);
        this.firePropertyChange("orientation", orientation2, this.orientation = orientation);
        this.revalidate();
        this.repaint();
    }
    
    private void checkOrientation(final int n) {
        switch (n) {
            case 0:
            case 1: {
                return;
            }
            default: {
                throw new IllegalArgumentException("orientation must be one of: VERTICAL, HORIZONTAL");
            }
        }
    }
    
    @Override
    protected String paramString() {
        return super.paramString() + ",orientation=" + ((this.orientation == 0) ? "HORIZONTAL" : "VERTICAL");
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJSeparator();
        }
        return this.accessibleContext;
    }
    
    protected class AccessibleJSeparator extends AccessibleJComponent
    {
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.SEPARATOR;
        }
    }
}
