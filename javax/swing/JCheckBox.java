package javax.swing;

import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleContext;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.swing.plaf.ButtonUI;
import javax.accessibility.Accessible;

public class JCheckBox extends JToggleButton implements Accessible
{
    public static final String BORDER_PAINTED_FLAT_CHANGED_PROPERTY = "borderPaintedFlat";
    private boolean flat;
    private static final String uiClassID = "CheckBoxUI";
    
    public JCheckBox() {
        this(null, null, false);
    }
    
    public JCheckBox(final Icon icon) {
        this(null, icon, false);
    }
    
    public JCheckBox(final Icon icon, final boolean b) {
        this(null, icon, b);
    }
    
    public JCheckBox(final String s) {
        this(s, null, false);
    }
    
    public JCheckBox(final Action action) {
        this();
        this.setAction(action);
    }
    
    public JCheckBox(final String s, final boolean b) {
        this(s, null, b);
    }
    
    public JCheckBox(final String s, final Icon icon) {
        this(s, icon, false);
    }
    
    public JCheckBox(final String s, final Icon icon, final boolean b) {
        super(s, icon, b);
        this.flat = false;
        this.setUIProperty("borderPainted", Boolean.FALSE);
        this.setHorizontalAlignment(10);
    }
    
    public void setBorderPaintedFlat(final boolean flat) {
        final boolean flat2 = this.flat;
        this.firePropertyChange("borderPaintedFlat", flat2, this.flat = flat);
        if (flat != flat2) {
            this.revalidate();
            this.repaint();
        }
    }
    
    public boolean isBorderPaintedFlat() {
        return this.flat;
    }
    
    @Override
    public void updateUI() {
        this.setUI((ButtonUI)UIManager.getUI(this));
    }
    
    @Override
    public String getUIClassID() {
        return "CheckBoxUI";
    }
    
    @Override
    void setIconFromAction(final Action action) {
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.getUIClassID().equals("CheckBoxUI")) {
            final byte b = (byte)(JComponent.getWriteObjCounter(this) - 1);
            JComponent.setWriteObjCounter(this, b);
            if (b == 0 && this.ui != null) {
                this.ui.installUI(this);
            }
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        if (this.getUIClassID().equals("CheckBoxUI")) {
            this.updateUI();
        }
    }
    
    @Override
    protected String paramString() {
        return super.paramString();
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJCheckBox();
        }
        return this.accessibleContext;
    }
    
    protected class AccessibleJCheckBox extends AccessibleJToggleButton
    {
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.CHECK_BOX;
        }
    }
}
