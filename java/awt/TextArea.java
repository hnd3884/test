package java.awt;

import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import java.util.HashSet;
import javax.accessibility.AccessibleContext;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.awt.peer.TextAreaPeer;
import java.util.Set;

public class TextArea extends TextComponent
{
    int rows;
    int columns;
    private static final String base = "text";
    private static int nameCounter;
    public static final int SCROLLBARS_BOTH = 0;
    public static final int SCROLLBARS_VERTICAL_ONLY = 1;
    public static final int SCROLLBARS_HORIZONTAL_ONLY = 2;
    public static final int SCROLLBARS_NONE = 3;
    private int scrollbarVisibility;
    private static Set<AWTKeyStroke> forwardTraversalKeys;
    private static Set<AWTKeyStroke> backwardTraversalKeys;
    private static final long serialVersionUID = 3692302836626095722L;
    private int textAreaSerializedDataVersion;
    
    private static native void initIDs();
    
    public TextArea() throws HeadlessException {
        this("", 0, 0, 0);
    }
    
    public TextArea(final String s) throws HeadlessException {
        this(s, 0, 0, 0);
    }
    
    public TextArea(final int n, final int n2) throws HeadlessException {
        this("", n, n2, 0);
    }
    
    public TextArea(final String s, final int n, final int n2) throws HeadlessException {
        this(s, n, n2, 0);
    }
    
    public TextArea(final String s, final int n, final int n2, final int scrollbarVisibility) throws HeadlessException {
        super(s);
        this.textAreaSerializedDataVersion = 2;
        this.rows = ((n >= 0) ? n : 0);
        this.columns = ((n2 >= 0) ? n2 : 0);
        if (scrollbarVisibility >= 0 && scrollbarVisibility <= 3) {
            this.scrollbarVisibility = scrollbarVisibility;
        }
        else {
            this.scrollbarVisibility = 0;
        }
        this.setFocusTraversalKeys(0, TextArea.forwardTraversalKeys);
        this.setFocusTraversalKeys(1, TextArea.backwardTraversalKeys);
    }
    
    @Override
    String constructComponentName() {
        synchronized (TextArea.class) {
            return "text" + TextArea.nameCounter++;
        }
    }
    
    @Override
    public void addNotify() {
        synchronized (this.getTreeLock()) {
            if (this.peer == null) {
                this.peer = this.getToolkit().createTextArea(this);
            }
            super.addNotify();
        }
    }
    
    public void insert(final String s, final int n) {
        this.insertText(s, n);
    }
    
    @Deprecated
    public synchronized void insertText(final String s, final int n) {
        final TextAreaPeer textAreaPeer = (TextAreaPeer)this.peer;
        if (textAreaPeer != null) {
            textAreaPeer.insert(s, n);
        }
        this.text = this.text.substring(0, n) + s + this.text.substring(n);
    }
    
    public void append(final String s) {
        this.appendText(s);
    }
    
    @Deprecated
    public synchronized void appendText(final String s) {
        this.insertText(s, this.getText().length());
    }
    
    public void replaceRange(final String s, final int n, final int n2) {
        this.replaceText(s, n, n2);
    }
    
    @Deprecated
    public synchronized void replaceText(final String s, final int n, final int n2) {
        final TextAreaPeer textAreaPeer = (TextAreaPeer)this.peer;
        if (textAreaPeer != null) {
            textAreaPeer.replaceRange(s, n, n2);
        }
        this.text = this.text.substring(0, n) + s + this.text.substring(n2);
    }
    
    public int getRows() {
        return this.rows;
    }
    
    public void setRows(final int rows) {
        final int rows2 = this.rows;
        if (rows < 0) {
            throw new IllegalArgumentException("rows less than zero.");
        }
        if (rows != rows2) {
            this.rows = rows;
            this.invalidate();
        }
    }
    
    public int getColumns() {
        return this.columns;
    }
    
    public void setColumns(final int columns) {
        final int columns2 = this.columns;
        if (columns < 0) {
            throw new IllegalArgumentException("columns less than zero.");
        }
        if (columns != columns2) {
            this.columns = columns;
            this.invalidate();
        }
    }
    
    public int getScrollbarVisibility() {
        return this.scrollbarVisibility;
    }
    
    public Dimension getPreferredSize(final int n, final int n2) {
        return this.preferredSize(n, n2);
    }
    
    @Deprecated
    public Dimension preferredSize(final int n, final int n2) {
        synchronized (this.getTreeLock()) {
            final TextAreaPeer textAreaPeer = (TextAreaPeer)this.peer;
            return (textAreaPeer != null) ? textAreaPeer.getPreferredSize(n, n2) : super.preferredSize();
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
            return (this.rows > 0 && this.columns > 0) ? this.preferredSize(this.rows, this.columns) : super.preferredSize();
        }
    }
    
    public Dimension getMinimumSize(final int n, final int n2) {
        return this.minimumSize(n, n2);
    }
    
    @Deprecated
    public Dimension minimumSize(final int n, final int n2) {
        synchronized (this.getTreeLock()) {
            final TextAreaPeer textAreaPeer = (TextAreaPeer)this.peer;
            return (textAreaPeer != null) ? textAreaPeer.getMinimumSize(n, n2) : super.minimumSize();
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
            return (this.rows > 0 && this.columns > 0) ? this.minimumSize(this.rows, this.columns) : super.minimumSize();
        }
    }
    
    @Override
    protected String paramString() {
        String s = null;
        switch (this.scrollbarVisibility) {
            case 0: {
                s = "both";
                break;
            }
            case 1: {
                s = "vertical-only";
                break;
            }
            case 2: {
                s = "horizontal-only";
                break;
            }
            case 3: {
                s = "none";
                break;
            }
            default: {
                s = "invalid display policy";
                break;
            }
        }
        return super.paramString() + ",rows=" + this.rows + ",columns=" + this.columns + ",scrollbarVisibility=" + s;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException, HeadlessException {
        objectInputStream.defaultReadObject();
        if (this.columns < 0) {
            this.columns = 0;
        }
        if (this.rows < 0) {
            this.rows = 0;
        }
        if (this.scrollbarVisibility < 0 || this.scrollbarVisibility > 3) {
            this.scrollbarVisibility = 0;
        }
        if (this.textAreaSerializedDataVersion < 2) {
            this.setFocusTraversalKeys(0, TextArea.forwardTraversalKeys);
            this.setFocusTraversalKeys(1, TextArea.backwardTraversalKeys);
        }
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleAWTTextArea();
        }
        return this.accessibleContext;
    }
    
    static {
        TextArea.nameCounter = 0;
        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
        TextArea.forwardTraversalKeys = KeyboardFocusManager.initFocusTraversalKeysSet("ctrl TAB", new HashSet<AWTKeyStroke>());
        TextArea.backwardTraversalKeys = KeyboardFocusManager.initFocusTraversalKeysSet("ctrl shift TAB", new HashSet<AWTKeyStroke>());
    }
    
    protected class AccessibleAWTTextArea extends AccessibleAWTTextComponent
    {
        private static final long serialVersionUID = 3472827823632144419L;
        
        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            final AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
            accessibleStateSet.add(AccessibleState.MULTI_LINE);
            return accessibleStateSet;
        }
    }
}
