package javax.swing;

import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleContext;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.PanelUI;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import javax.accessibility.Accessible;

public class JPanel extends JComponent implements Accessible
{
    private static final String uiClassID = "PanelUI";
    
    public JPanel(final LayoutManager layout, final boolean doubleBuffered) {
        this.setLayout(layout);
        this.setDoubleBuffered(doubleBuffered);
        this.setUIProperty("opaque", Boolean.TRUE);
        this.updateUI();
    }
    
    public JPanel(final LayoutManager layoutManager) {
        this(layoutManager, true);
    }
    
    public JPanel(final boolean b) {
        this(new FlowLayout(), b);
    }
    
    public JPanel() {
        this(true);
    }
    
    @Override
    public void updateUI() {
        this.setUI((PanelUI)UIManager.getUI(this));
    }
    
    public PanelUI getUI() {
        return (PanelUI)this.ui;
    }
    
    public void setUI(final PanelUI ui) {
        super.setUI(ui);
    }
    
    @Override
    public String getUIClassID() {
        return "PanelUI";
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.getUIClassID().equals("PanelUI")) {
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
            this.accessibleContext = new AccessibleJPanel();
        }
        return this.accessibleContext;
    }
    
    protected class AccessibleJPanel extends AccessibleJComponent
    {
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.PANEL;
        }
    }
}
