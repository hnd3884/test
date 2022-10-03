package java.awt;

import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleContext;
import java.awt.peer.LabelPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import javax.accessibility.Accessible;

public class Label extends Component implements Accessible
{
    public static final int LEFT = 0;
    public static final int CENTER = 1;
    public static final int RIGHT = 2;
    String text;
    int alignment;
    private static final String base = "label";
    private static int nameCounter;
    private static final long serialVersionUID = 3094126758329070636L;
    
    public Label() throws HeadlessException {
        this("", 0);
    }
    
    public Label(final String s) throws HeadlessException {
        this(s, 0);
    }
    
    public Label(final String text, final int alignment) throws HeadlessException {
        this.alignment = 0;
        GraphicsEnvironment.checkHeadless();
        this.text = text;
        this.setAlignment(alignment);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException, HeadlessException {
        GraphicsEnvironment.checkHeadless();
        objectInputStream.defaultReadObject();
    }
    
    @Override
    String constructComponentName() {
        synchronized (Label.class) {
            return "label" + Label.nameCounter++;
        }
    }
    
    @Override
    public void addNotify() {
        synchronized (this.getTreeLock()) {
            if (this.peer == null) {
                this.peer = this.getToolkit().createLabel(this);
            }
            super.addNotify();
        }
    }
    
    public int getAlignment() {
        return this.alignment;
    }
    
    public synchronized void setAlignment(final int n) {
        switch (n) {
            case 0:
            case 1:
            case 2: {
                this.alignment = n;
                final LabelPeer labelPeer = (LabelPeer)this.peer;
                if (labelPeer != null) {
                    labelPeer.setAlignment(n);
                }
                return;
            }
            default: {
                throw new IllegalArgumentException("improper alignment: " + n);
            }
        }
    }
    
    public String getText() {
        return this.text;
    }
    
    public void setText(final String s) {
        boolean b = false;
        synchronized (this) {
            if (s != this.text && (this.text == null || !this.text.equals(s))) {
                this.text = s;
                final LabelPeer labelPeer = (LabelPeer)this.peer;
                if (labelPeer != null) {
                    labelPeer.setText(s);
                }
                b = true;
            }
        }
        if (b) {
            this.invalidateIfValid();
        }
    }
    
    @Override
    protected String paramString() {
        String s = "";
        switch (this.alignment) {
            case 0: {
                s = "left";
                break;
            }
            case 1: {
                s = "center";
                break;
            }
            case 2: {
                s = "right";
                break;
            }
        }
        return super.paramString() + ",align=" + s + ",text=" + this.text;
    }
    
    private static native void initIDs();
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleAWTLabel();
        }
        return this.accessibleContext;
    }
    
    static {
        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
        Label.nameCounter = 0;
    }
    
    protected class AccessibleAWTLabel extends AccessibleAWTComponent
    {
        private static final long serialVersionUID = -3568967560160480438L;
        
        public AccessibleAWTLabel() {
        }
        
        @Override
        public String getAccessibleName() {
            if (this.accessibleName != null) {
                return this.accessibleName;
            }
            if (Label.this.getText() == null) {
                return super.getAccessibleName();
            }
            return Label.this.getText();
        }
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.LABEL;
        }
    }
}
