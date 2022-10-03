package java.awt;

import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleContext;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.awt.event.ActionEvent;
import java.util.EventListener;
import java.awt.peer.TextFieldPeer;
import java.awt.event.ActionListener;

public class TextField extends TextComponent
{
    int columns;
    char echoChar;
    transient ActionListener actionListener;
    private static final String base = "textfield";
    private static int nameCounter;
    private static final long serialVersionUID = -2966288784432217853L;
    private int textFieldSerializedDataVersion;
    
    private static native void initIDs();
    
    public TextField() throws HeadlessException {
        this("", 0);
    }
    
    public TextField(final String s) throws HeadlessException {
        this(s, (s != null) ? s.length() : 0);
    }
    
    public TextField(final int n) throws HeadlessException {
        this("", n);
    }
    
    public TextField(final String s, final int n) throws HeadlessException {
        super(s);
        this.textFieldSerializedDataVersion = 1;
        this.columns = ((n >= 0) ? n : 0);
    }
    
    @Override
    String constructComponentName() {
        synchronized (TextField.class) {
            return "textfield" + TextField.nameCounter++;
        }
    }
    
    @Override
    public void addNotify() {
        synchronized (this.getTreeLock()) {
            if (this.peer == null) {
                this.peer = this.getToolkit().createTextField(this);
            }
            super.addNotify();
        }
    }
    
    public char getEchoChar() {
        return this.echoChar;
    }
    
    public void setEchoChar(final char echoCharacter) {
        this.setEchoCharacter(echoCharacter);
    }
    
    @Deprecated
    public synchronized void setEchoCharacter(final char c) {
        if (this.echoChar != c) {
            this.echoChar = c;
            final TextFieldPeer textFieldPeer = (TextFieldPeer)this.peer;
            if (textFieldPeer != null) {
                textFieldPeer.setEchoChar(c);
            }
        }
    }
    
    @Override
    public void setText(final String text) {
        super.setText(text);
        this.invalidateIfValid();
    }
    
    public boolean echoCharIsSet() {
        return this.echoChar != '\0';
    }
    
    public int getColumns() {
        return this.columns;
    }
    
    public void setColumns(final int columns) {
        final int columns2;
        synchronized (this) {
            columns2 = this.columns;
            if (columns < 0) {
                throw new IllegalArgumentException("columns less than zero.");
            }
            if (columns != columns2) {
                this.columns = columns;
            }
        }
        if (columns != columns2) {
            this.invalidate();
        }
    }
    
    public Dimension getPreferredSize(final int n) {
        return this.preferredSize(n);
    }
    
    @Deprecated
    public Dimension preferredSize(final int n) {
        synchronized (this.getTreeLock()) {
            final TextFieldPeer textFieldPeer = (TextFieldPeer)this.peer;
            return (textFieldPeer != null) ? textFieldPeer.getPreferredSize(n) : super.preferredSize();
        }
    }
    
    @Override
    public Dimension getPreferredSize() {
        return this.preferredSize();
    }
    
    @Deprecated
    @Override
    public Dimension preferredSize() {
        synchronized (this.getTreeLock()) {
            return (this.columns > 0) ? this.preferredSize(this.columns) : super.preferredSize();
        }
    }
    
    public Dimension getMinimumSize(final int n) {
        return this.minimumSize(n);
    }
    
    @Deprecated
    public Dimension minimumSize(final int n) {
        synchronized (this.getTreeLock()) {
            final TextFieldPeer textFieldPeer = (TextFieldPeer)this.peer;
            return (textFieldPeer != null) ? textFieldPeer.getMinimumSize(n) : super.minimumSize();
        }
    }
    
    @Override
    public Dimension getMinimumSize() {
        return this.minimumSize();
    }
    
    @Deprecated
    @Override
    public Dimension minimumSize() {
        synchronized (this.getTreeLock()) {
            return (this.columns > 0) ? this.minimumSize(this.columns) : super.minimumSize();
        }
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
        String s = super.paramString();
        if (this.echoChar != '\0') {
            s = s + ",echo=" + this.echoChar;
        }
        return s;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        AWTEventMulticaster.save(objectOutputStream, "actionL", this.actionListener);
        objectOutputStream.writeObject(null);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException, HeadlessException {
        objectInputStream.defaultReadObject();
        if (this.columns < 0) {
            this.columns = 0;
        }
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
            this.accessibleContext = new AccessibleAWTTextField();
        }
        return this.accessibleContext;
    }
    
    static {
        TextField.nameCounter = 0;
        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
    }
    
    protected class AccessibleAWTTextField extends AccessibleAWTTextComponent
    {
        private static final long serialVersionUID = 6219164359235943158L;
        
        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            final AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
            accessibleStateSet.add(AccessibleState.SINGLE_LINE);
            return accessibleStateSet;
        }
    }
}
