package java.awt;

import java.text.BreakIterator;
import javax.swing.text.AttributeSet;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleContext;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import sun.security.util.SecurityConstants;
import java.awt.event.TextEvent;
import java.util.EventListener;
import java.awt.peer.TextComponentPeer;
import java.awt.im.InputMethodRequests;
import sun.awt.InputMethodSupport;
import java.awt.event.TextListener;
import javax.accessibility.Accessible;

public class TextComponent extends Component implements Accessible
{
    String text;
    boolean editable;
    int selectionStart;
    int selectionEnd;
    boolean backgroundSetByClientCode;
    protected transient TextListener textListener;
    private static final long serialVersionUID = -2214773872412987419L;
    private int textComponentSerializedDataVersion;
    private boolean checkForEnableIM;
    
    TextComponent(final String s) throws HeadlessException {
        this.editable = true;
        this.backgroundSetByClientCode = false;
        this.textComponentSerializedDataVersion = 1;
        this.checkForEnableIM = true;
        GraphicsEnvironment.checkHeadless();
        this.text = ((s != null) ? s : "");
        this.setCursor(Cursor.getPredefinedCursor(2));
    }
    
    private void enableInputMethodsIfNecessary() {
        if (this.checkForEnableIM) {
            this.checkForEnableIM = false;
            try {
                final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
                boolean enableInputMethodsForTextComponent = false;
                if (defaultToolkit instanceof InputMethodSupport) {
                    enableInputMethodsForTextComponent = ((InputMethodSupport)defaultToolkit).enableInputMethodsForTextComponent();
                }
                this.enableInputMethods(enableInputMethodsForTextComponent);
            }
            catch (final Exception ex) {}
        }
    }
    
    @Override
    public void enableInputMethods(final boolean b) {
        this.checkForEnableIM = false;
        super.enableInputMethods(b);
    }
    
    @Override
    boolean areInputMethodsEnabled() {
        if (this.checkForEnableIM) {
            this.enableInputMethodsIfNecessary();
        }
        return (this.eventMask & 0x1000L) != 0x0L;
    }
    
    @Override
    public InputMethodRequests getInputMethodRequests() {
        final TextComponentPeer textComponentPeer = (TextComponentPeer)this.peer;
        if (textComponentPeer != null) {
            return textComponentPeer.getInputMethodRequests();
        }
        return null;
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        this.enableInputMethodsIfNecessary();
    }
    
    @Override
    public void removeNotify() {
        synchronized (this.getTreeLock()) {
            final TextComponentPeer textComponentPeer = (TextComponentPeer)this.peer;
            if (textComponentPeer != null) {
                this.text = textComponentPeer.getText();
                this.selectionStart = textComponentPeer.getSelectionStart();
                this.selectionEnd = textComponentPeer.getSelectionEnd();
            }
            super.removeNotify();
        }
    }
    
    public synchronized void setText(final String s) {
        final boolean b = (this.text == null || this.text.isEmpty()) && (s == null || s.isEmpty());
        this.text = ((s != null) ? s : "");
        final TextComponentPeer textComponentPeer = (TextComponentPeer)this.peer;
        if (textComponentPeer != null && !b) {
            textComponentPeer.setText(this.text);
        }
    }
    
    public synchronized String getText() {
        final TextComponentPeer textComponentPeer = (TextComponentPeer)this.peer;
        if (textComponentPeer != null) {
            this.text = textComponentPeer.getText();
        }
        return this.text;
    }
    
    public synchronized String getSelectedText() {
        return this.getText().substring(this.getSelectionStart(), this.getSelectionEnd());
    }
    
    public boolean isEditable() {
        return this.editable;
    }
    
    public synchronized void setEditable(final boolean b) {
        if (this.editable == b) {
            return;
        }
        this.editable = b;
        final TextComponentPeer textComponentPeer = (TextComponentPeer)this.peer;
        if (textComponentPeer != null) {
            textComponentPeer.setEditable(b);
        }
    }
    
    @Override
    public Color getBackground() {
        if (!this.editable && !this.backgroundSetByClientCode) {
            return SystemColor.control;
        }
        return super.getBackground();
    }
    
    @Override
    public void setBackground(final Color background) {
        this.backgroundSetByClientCode = true;
        super.setBackground(background);
    }
    
    public synchronized int getSelectionStart() {
        final TextComponentPeer textComponentPeer = (TextComponentPeer)this.peer;
        if (textComponentPeer != null) {
            this.selectionStart = textComponentPeer.getSelectionStart();
        }
        return this.selectionStart;
    }
    
    public synchronized void setSelectionStart(final int n) {
        this.select(n, this.getSelectionEnd());
    }
    
    public synchronized int getSelectionEnd() {
        final TextComponentPeer textComponentPeer = (TextComponentPeer)this.peer;
        if (textComponentPeer != null) {
            this.selectionEnd = textComponentPeer.getSelectionEnd();
        }
        return this.selectionEnd;
    }
    
    public synchronized void setSelectionEnd(final int n) {
        this.select(this.getSelectionStart(), n);
    }
    
    public synchronized void select(int length, int length2) {
        final String text = this.getText();
        if (length < 0) {
            length = 0;
        }
        if (length > text.length()) {
            length = text.length();
        }
        if (length2 > text.length()) {
            length2 = text.length();
        }
        if (length2 < length) {
            length2 = length;
        }
        this.selectionStart = length;
        this.selectionEnd = length2;
        final TextComponentPeer textComponentPeer = (TextComponentPeer)this.peer;
        if (textComponentPeer != null) {
            textComponentPeer.select(length, length2);
        }
    }
    
    public synchronized void selectAll() {
        this.selectionStart = 0;
        this.selectionEnd = this.getText().length();
        final TextComponentPeer textComponentPeer = (TextComponentPeer)this.peer;
        if (textComponentPeer != null) {
            textComponentPeer.select(this.selectionStart, this.selectionEnd);
        }
    }
    
    public synchronized void setCaretPosition(int caretPosition) {
        if (caretPosition < 0) {
            throw new IllegalArgumentException("position less than zero.");
        }
        final int length = this.getText().length();
        if (caretPosition > length) {
            caretPosition = length;
        }
        final TextComponentPeer textComponentPeer = (TextComponentPeer)this.peer;
        if (textComponentPeer != null) {
            textComponentPeer.setCaretPosition(caretPosition);
        }
        else {
            this.select(caretPosition, caretPosition);
        }
    }
    
    public synchronized int getCaretPosition() {
        final TextComponentPeer textComponentPeer = (TextComponentPeer)this.peer;
        int n;
        if (textComponentPeer != null) {
            n = textComponentPeer.getCaretPosition();
        }
        else {
            n = this.selectionStart;
        }
        final int length = this.getText().length();
        if (n > length) {
            n = length;
        }
        return n;
    }
    
    public synchronized void addTextListener(final TextListener textListener) {
        if (textListener == null) {
            return;
        }
        this.textListener = AWTEventMulticaster.add(this.textListener, textListener);
        this.newEventsOnly = true;
    }
    
    public synchronized void removeTextListener(final TextListener textListener) {
        if (textListener == null) {
            return;
        }
        this.textListener = AWTEventMulticaster.remove(this.textListener, textListener);
    }
    
    public synchronized TextListener[] getTextListeners() {
        return this.getListeners(TextListener.class);
    }
    
    @Override
    public <T extends EventListener> T[] getListeners(final Class<T> clazz) {
        if (clazz == TextListener.class) {
            return AWTEventMulticaster.getListeners(this.textListener, clazz);
        }
        return super.getListeners(clazz);
    }
    
    @Override
    boolean eventEnabled(final AWTEvent awtEvent) {
        if (awtEvent.id == 900) {
            return (this.eventMask & 0x400L) != 0x0L || this.textListener != null;
        }
        return super.eventEnabled(awtEvent);
    }
    
    @Override
    protected void processEvent(final AWTEvent awtEvent) {
        if (awtEvent instanceof TextEvent) {
            this.processTextEvent((TextEvent)awtEvent);
            return;
        }
        super.processEvent(awtEvent);
    }
    
    protected void processTextEvent(final TextEvent textEvent) {
        final TextListener textListener = this.textListener;
        if (textListener != null) {
            switch (textEvent.getID()) {
                case 900: {
                    textListener.textValueChanged(textEvent);
                    break;
                }
            }
        }
    }
    
    @Override
    protected String paramString() {
        String s = super.paramString() + ",text=" + this.getText();
        if (this.editable) {
            s += ",editable";
        }
        return s + ",selection=" + this.getSelectionStart() + "-" + this.getSelectionEnd();
    }
    
    private boolean canAccessClipboard() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager == null) {
            return true;
        }
        try {
            securityManager.checkPermission(SecurityConstants.AWT.ACCESS_CLIPBOARD_PERMISSION);
            return true;
        }
        catch (final SecurityException ex) {
            return false;
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        final TextComponentPeer textComponentPeer = (TextComponentPeer)this.peer;
        if (textComponentPeer != null) {
            this.text = textComponentPeer.getText();
            this.selectionStart = textComponentPeer.getSelectionStart();
            this.selectionEnd = textComponentPeer.getSelectionEnd();
        }
        objectOutputStream.defaultWriteObject();
        AWTEventMulticaster.save(objectOutputStream, "textL", this.textListener);
        objectOutputStream.writeObject(null);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException, HeadlessException {
        GraphicsEnvironment.checkHeadless();
        objectInputStream.defaultReadObject();
        this.text = ((this.text != null) ? this.text : "");
        this.select(this.selectionStart, this.selectionEnd);
        Object object;
        while (null != (object = objectInputStream.readObject())) {
            if ("textL" == ((String)object).intern()) {
                this.addTextListener((TextListener)objectInputStream.readObject());
            }
            else {
                objectInputStream.readObject();
            }
        }
        this.enableInputMethodsIfNecessary();
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleAWTTextComponent();
        }
        return this.accessibleContext;
    }
    
    protected class AccessibleAWTTextComponent extends AccessibleAWTComponent implements AccessibleText, TextListener
    {
        private static final long serialVersionUID = 3631432373506317811L;
        private static final boolean NEXT = true;
        private static final boolean PREVIOUS = false;
        
        public AccessibleAWTTextComponent() {
            TextComponent.this.addTextListener(this);
        }
        
        @Override
        public void textValueChanged(final TextEvent textEvent) {
            this.firePropertyChange("AccessibleText", null, TextComponent.this.getCaretPosition());
        }
        
        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            final AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
            if (TextComponent.this.isEditable()) {
                accessibleStateSet.add(AccessibleState.EDITABLE);
            }
            return accessibleStateSet;
        }
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.TEXT;
        }
        
        @Override
        public AccessibleText getAccessibleText() {
            return this;
        }
        
        @Override
        public int getIndexAtPoint(final Point point) {
            return -1;
        }
        
        @Override
        public Rectangle getCharacterBounds(final int n) {
            return null;
        }
        
        @Override
        public int getCharCount() {
            return TextComponent.this.getText().length();
        }
        
        @Override
        public int getCaretPosition() {
            return TextComponent.this.getCaretPosition();
        }
        
        @Override
        public AttributeSet getCharacterAttribute(final int n) {
            return null;
        }
        
        @Override
        public int getSelectionStart() {
            return TextComponent.this.getSelectionStart();
        }
        
        @Override
        public int getSelectionEnd() {
            return TextComponent.this.getSelectionEnd();
        }
        
        @Override
        public String getSelectedText() {
            final String selectedText = TextComponent.this.getSelectedText();
            if (selectedText == null || selectedText.equals("")) {
                return null;
            }
            return selectedText;
        }
        
        @Override
        public String getAtIndex(final int n, final int n2) {
            if (n2 < 0 || n2 >= TextComponent.this.getText().length()) {
                return null;
            }
            switch (n) {
                case 1: {
                    return TextComponent.this.getText().substring(n2, n2 + 1);
                }
                case 2: {
                    final String text = TextComponent.this.getText();
                    final BreakIterator wordInstance = BreakIterator.getWordInstance();
                    wordInstance.setText(text);
                    return text.substring(wordInstance.previous(), wordInstance.following(n2));
                }
                case 3: {
                    final String text2 = TextComponent.this.getText();
                    final BreakIterator sentenceInstance = BreakIterator.getSentenceInstance();
                    sentenceInstance.setText(text2);
                    return text2.substring(sentenceInstance.previous(), sentenceInstance.following(n2));
                }
                default: {
                    return null;
                }
            }
        }
        
        private int findWordLimit(final int n, final BreakIterator breakIterator, final boolean b, final String s) {
            int n2 = b ? breakIterator.following(n) : breakIterator.preceding(n);
            for (int i = b ? breakIterator.next() : breakIterator.previous(); i != -1; i = (b ? breakIterator.next() : breakIterator.previous())) {
                for (int j = Math.min(n2, i); j < Math.max(n2, i); ++j) {
                    if (Character.isLetter(s.charAt(j))) {
                        return n2;
                    }
                }
                n2 = i;
            }
            return -1;
        }
        
        @Override
        public String getAfterIndex(final int n, final int n2) {
            if (n2 < 0 || n2 >= TextComponent.this.getText().length()) {
                return null;
            }
            switch (n) {
                case 1: {
                    if (n2 + 1 >= TextComponent.this.getText().length()) {
                        return null;
                    }
                    return TextComponent.this.getText().substring(n2 + 1, n2 + 2);
                }
                case 2: {
                    final String text = TextComponent.this.getText();
                    final BreakIterator wordInstance = BreakIterator.getWordInstance();
                    wordInstance.setText(text);
                    final int wordLimit = this.findWordLimit(n2, wordInstance, true, text);
                    if (wordLimit == -1 || wordLimit >= text.length()) {
                        return null;
                    }
                    final int following = wordInstance.following(wordLimit);
                    if (following == -1 || following >= text.length()) {
                        return null;
                    }
                    return text.substring(wordLimit, following);
                }
                case 3: {
                    final String text2 = TextComponent.this.getText();
                    final BreakIterator sentenceInstance = BreakIterator.getSentenceInstance();
                    sentenceInstance.setText(text2);
                    final int following2 = sentenceInstance.following(n2);
                    if (following2 == -1 || following2 >= text2.length()) {
                        return null;
                    }
                    final int following3 = sentenceInstance.following(following2);
                    if (following3 == -1 || following3 >= text2.length()) {
                        return null;
                    }
                    return text2.substring(following2, following3);
                }
                default: {
                    return null;
                }
            }
        }
        
        @Override
        public String getBeforeIndex(final int n, final int n2) {
            if (n2 < 0 || n2 > TextComponent.this.getText().length() - 1) {
                return null;
            }
            switch (n) {
                case 1: {
                    if (n2 == 0) {
                        return null;
                    }
                    return TextComponent.this.getText().substring(n2 - 1, n2);
                }
                case 2: {
                    final String text = TextComponent.this.getText();
                    final BreakIterator wordInstance = BreakIterator.getWordInstance();
                    wordInstance.setText(text);
                    final int wordLimit = this.findWordLimit(n2, wordInstance, false, text);
                    if (wordLimit == -1) {
                        return null;
                    }
                    final int preceding = wordInstance.preceding(wordLimit);
                    if (preceding == -1) {
                        return null;
                    }
                    return text.substring(preceding, wordLimit);
                }
                case 3: {
                    final String text2 = TextComponent.this.getText();
                    final BreakIterator sentenceInstance = BreakIterator.getSentenceInstance();
                    sentenceInstance.setText(text2);
                    sentenceInstance.following(n2);
                    final int previous = sentenceInstance.previous();
                    final int previous2 = sentenceInstance.previous();
                    if (previous2 == -1) {
                        return null;
                    }
                    return text2.substring(previous2, previous);
                }
                default: {
                    return null;
                }
            }
        }
    }
}
