package java.awt;

import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleValue;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.awt.event.ActionEvent;
import java.util.EventListener;
import java.awt.peer.ButtonPeer;
import java.awt.event.ActionListener;
import javax.accessibility.Accessible;

public class Button extends Component implements Accessible
{
    String label;
    String actionCommand;
    transient ActionListener actionListener;
    private static final String base = "button";
    private static int nameCounter;
    private static final long serialVersionUID = -8774683716313001058L;
    private int buttonSerializedDataVersion;
    
    private static native void initIDs();
    
    public Button() throws HeadlessException {
        this("");
    }
    
    public Button(final String label) throws HeadlessException {
        this.buttonSerializedDataVersion = 1;
        GraphicsEnvironment.checkHeadless();
        this.label = label;
    }
    
    @Override
    String constructComponentName() {
        synchronized (Button.class) {
            return "button" + Button.nameCounter++;
        }
    }
    
    @Override
    public void addNotify() {
        synchronized (this.getTreeLock()) {
            if (this.peer == null) {
                this.peer = this.getToolkit().createButton(this);
            }
            super.addNotify();
        }
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public void setLabel(final String s) {
        boolean b = false;
        synchronized (this) {
            if (s != this.label && (this.label == null || !this.label.equals(s))) {
                this.label = s;
                final ButtonPeer buttonPeer = (ButtonPeer)this.peer;
                if (buttonPeer != null) {
                    buttonPeer.setLabel(s);
                }
                b = true;
            }
        }
        if (b) {
            this.invalidateIfValid();
        }
    }
    
    public void setActionCommand(final String actionCommand) {
        this.actionCommand = actionCommand;
    }
    
    public String getActionCommand() {
        return (this.actionCommand == null) ? this.label : this.actionCommand;
    }
    
    public synchronized void addActionListener(final ActionListener actionListener) {
        if (actionListener == null) {
            return;
        }
        this.actionListener = AWTEventMulticaster.add(this.actionListener, actionListener);
        this.newEventsOnly = true;
    }
    
    public synchronized void removeActionListener(final ActionListener actionListener) {
        if (actionListener == null) {
            return;
        }
        this.actionListener = AWTEventMulticaster.remove(this.actionListener, actionListener);
    }
    
    public synchronized ActionListener[] getActionListeners() {
        return this.getListeners(ActionListener.class);
    }
    
    @Override
    public <T extends EventListener> T[] getListeners(final Class<T> clazz) {
        if (clazz == ActionListener.class) {
            return AWTEventMulticaster.getListeners(this.actionListener, clazz);
        }
        return super.getListeners(clazz);
    }
    
    @Override
    boolean eventEnabled(final AWTEvent awtEvent) {
        if (awtEvent.id == 1001) {
            return (this.eventMask & 0x80L) != 0x0L || this.actionListener != null;
        }
        return super.eventEnabled(awtEvent);
    }
    
    @Override
    protected void processEvent(final AWTEvent awtEvent) {
        if (awtEvent instanceof ActionEvent) {
            this.processActionEvent((ActionEvent)awtEvent);
            return;
        }
        super.processEvent(awtEvent);
    }
    
    protected void processActionEvent(final ActionEvent actionEvent) {
        final ActionListener actionListener = this.actionListener;
        if (actionListener != null) {
            actionListener.actionPerformed(actionEvent);
        }
    }
    
    @Override
    protected String paramString() {
        return super.paramString() + ",label=" + this.label;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        AWTEventMulticaster.save(objectOutputStream, "actionL", this.actionListener);
        objectOutputStream.writeObject(null);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException, HeadlessException {
        GraphicsEnvironment.checkHeadless();
        objectInputStream.defaultReadObject();
        Object object;
        while (null != (object = objectInputStream.readObject())) {
            if ("actionL" == ((String)object).intern()) {
                this.addActionListener((ActionListener)objectInputStream.readObject());
            }
            else {
                objectInputStream.readObject();
            }
        }
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleAWTButton();
        }
        return this.accessibleContext;
    }
    
    static {
        Button.nameCounter = 0;
        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
    }
    
    protected class AccessibleAWTButton extends AccessibleAWTComponent implements AccessibleAction, AccessibleValue
    {
        private static final long serialVersionUID = -5932203980244017102L;
        
        @Override
        public String getAccessibleName() {
            if (this.accessibleName != null) {
                return this.accessibleName;
            }
            if (Button.this.getLabel() == null) {
                return super.getAccessibleName();
            }
            return Button.this.getLabel();
        }
        
        @Override
        public AccessibleAction getAccessibleAction() {
            return this;
        }
        
        @Override
        public AccessibleValue getAccessibleValue() {
            return this;
        }
        
        @Override
        public int getAccessibleActionCount() {
            return 1;
        }
        
        @Override
        public String getAccessibleActionDescription(final int n) {
            if (n == 0) {
                return "click";
            }
            return null;
        }
        
        @Override
        public boolean doAccessibleAction(final int n) {
            if (n == 0) {
                Toolkit.getEventQueue().postEvent(new ActionEvent(Button.this, 1001, Button.this.getActionCommand()));
                return true;
            }
            return false;
        }
        
        @Override
        public Number getCurrentAccessibleValue() {
            return 0;
        }
        
        @Override
        public boolean setCurrentAccessibleValue(final Number n) {
            return false;
        }
        
        @Override
        public Number getMinimumAccessibleValue() {
            return 0;
        }
        
        @Override
        public Number getMaximumAccessibleValue() {
            return 0;
        }
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.PUSH_BUTTON;
        }
    }
}
