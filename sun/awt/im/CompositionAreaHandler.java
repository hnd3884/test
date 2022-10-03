package sun.awt.im;

import java.awt.Rectangle;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.awt.event.InputMethodEvent;
import java.awt.Component;
import java.lang.ref.WeakReference;
import java.awt.font.TextHitInfo;
import java.text.AttributedCharacterIterator;
import java.awt.im.InputMethodRequests;
import java.awt.event.InputMethodListener;

class CompositionAreaHandler implements InputMethodListener, InputMethodRequests
{
    private static CompositionArea compositionArea;
    private static Object compositionAreaLock;
    private static CompositionAreaHandler compositionAreaOwner;
    private AttributedCharacterIterator composedText;
    private TextHitInfo caret;
    private WeakReference<Component> clientComponent;
    private InputMethodContext inputMethodContext;
    private static final AttributedCharacterIterator.Attribute[] IM_ATTRIBUTES;
    private static final AttributedCharacterIterator EMPTY_TEXT;
    
    CompositionAreaHandler(final InputMethodContext inputMethodContext) {
        this.caret = null;
        this.clientComponent = new WeakReference<Component>(null);
        this.inputMethodContext = inputMethodContext;
    }
    
    private void createCompositionArea() {
        synchronized (CompositionAreaHandler.compositionAreaLock) {
            CompositionAreaHandler.compositionArea = new CompositionArea();
            if (CompositionAreaHandler.compositionAreaOwner != null) {
                CompositionAreaHandler.compositionArea.setHandlerInfo(CompositionAreaHandler.compositionAreaOwner, this.inputMethodContext);
            }
            final Component component = this.clientComponent.get();
            if (component != null && component.getInputMethodRequests() != null && this.inputMethodContext.useBelowTheSpotInput()) {
                this.setCompositionAreaUndecorated(true);
            }
        }
    }
    
    void setClientComponent(final Component component) {
        this.clientComponent = new WeakReference<Component>(component);
    }
    
    void grabCompositionArea(final boolean b) {
        synchronized (CompositionAreaHandler.compositionAreaLock) {
            if (CompositionAreaHandler.compositionAreaOwner != this) {
                CompositionAreaHandler.compositionAreaOwner = this;
                if (CompositionAreaHandler.compositionArea != null) {
                    CompositionAreaHandler.compositionArea.setHandlerInfo(this, this.inputMethodContext);
                }
                if (b) {
                    if (this.composedText != null && CompositionAreaHandler.compositionArea == null) {
                        this.createCompositionArea();
                    }
                    if (CompositionAreaHandler.compositionArea != null) {
                        CompositionAreaHandler.compositionArea.setText(this.composedText, this.caret);
                    }
                }
            }
        }
    }
    
    void releaseCompositionArea() {
        synchronized (CompositionAreaHandler.compositionAreaLock) {
            if (CompositionAreaHandler.compositionAreaOwner == this) {
                CompositionAreaHandler.compositionAreaOwner = null;
                if (CompositionAreaHandler.compositionArea != null) {
                    CompositionAreaHandler.compositionArea.setHandlerInfo(null, null);
                    CompositionAreaHandler.compositionArea.setText(null, null);
                }
            }
        }
    }
    
    static void closeCompositionArea() {
        if (CompositionAreaHandler.compositionArea != null) {
            synchronized (CompositionAreaHandler.compositionAreaLock) {
                CompositionAreaHandler.compositionAreaOwner = null;
                CompositionAreaHandler.compositionArea.setHandlerInfo(null, null);
                CompositionAreaHandler.compositionArea.setText(null, null);
            }
        }
    }
    
    boolean isCompositionAreaVisible() {
        return CompositionAreaHandler.compositionArea != null && CompositionAreaHandler.compositionArea.isCompositionAreaVisible();
    }
    
    void setCompositionAreaVisible(final boolean compositionAreaVisible) {
        if (CompositionAreaHandler.compositionArea != null) {
            CompositionAreaHandler.compositionArea.setCompositionAreaVisible(compositionAreaVisible);
        }
    }
    
    void processInputMethodEvent(final InputMethodEvent inputMethodEvent) {
        if (inputMethodEvent.getID() == 1100) {
            this.inputMethodTextChanged(inputMethodEvent);
        }
        else {
            this.caretPositionChanged(inputMethodEvent);
        }
    }
    
    void setCompositionAreaUndecorated(final boolean compositionAreaUndecorated) {
        if (CompositionAreaHandler.compositionArea != null) {
            CompositionAreaHandler.compositionArea.setCompositionAreaUndecorated(compositionAreaUndecorated);
        }
    }
    
    @Override
    public void inputMethodTextChanged(final InputMethodEvent inputMethodEvent) {
        final AttributedCharacterIterator text = inputMethodEvent.getText();
        final int committedCharacterCount = inputMethodEvent.getCommittedCharacterCount();
        this.composedText = null;
        this.caret = null;
        if (text != null && committedCharacterCount < text.getEndIndex() - text.getBeginIndex()) {
            if (CompositionAreaHandler.compositionArea == null) {
                this.createCompositionArea();
            }
            final AttributedString attributedString = new AttributedString(text, text.getBeginIndex() + committedCharacterCount, text.getEndIndex(), CompositionAreaHandler.IM_ATTRIBUTES);
            attributedString.addAttribute(TextAttribute.FONT, CompositionAreaHandler.compositionArea.getFont());
            this.composedText = attributedString.getIterator();
            this.caret = inputMethodEvent.getCaret();
        }
        if (CompositionAreaHandler.compositionArea != null) {
            CompositionAreaHandler.compositionArea.setText(this.composedText, this.caret);
        }
        if (committedCharacterCount > 0) {
            this.inputMethodContext.dispatchCommittedText((Component)inputMethodEvent.getSource(), text, committedCharacterCount);
            if (this.isCompositionAreaVisible()) {
                CompositionAreaHandler.compositionArea.updateWindowLocation();
            }
        }
        inputMethodEvent.consume();
    }
    
    @Override
    public void caretPositionChanged(final InputMethodEvent inputMethodEvent) {
        if (CompositionAreaHandler.compositionArea != null) {
            CompositionAreaHandler.compositionArea.setCaret(inputMethodEvent.getCaret());
        }
        inputMethodEvent.consume();
    }
    
    InputMethodRequests getClientInputMethodRequests() {
        final Component component = this.clientComponent.get();
        if (component != null) {
            return component.getInputMethodRequests();
        }
        return null;
    }
    
    @Override
    public Rectangle getTextLocation(final TextHitInfo textHitInfo) {
        synchronized (CompositionAreaHandler.compositionAreaLock) {
            if (CompositionAreaHandler.compositionAreaOwner == this && this.isCompositionAreaVisible()) {
                return CompositionAreaHandler.compositionArea.getTextLocation(textHitInfo);
            }
            if (this.composedText != null) {
                return new Rectangle(0, 0, 0, 10);
            }
            final InputMethodRequests clientInputMethodRequests = this.getClientInputMethodRequests();
            if (clientInputMethodRequests != null) {
                return clientInputMethodRequests.getTextLocation(textHitInfo);
            }
            return new Rectangle(0, 0, 0, 10);
        }
    }
    
    @Override
    public TextHitInfo getLocationOffset(final int n, final int n2) {
        synchronized (CompositionAreaHandler.compositionAreaLock) {
            if (CompositionAreaHandler.compositionAreaOwner == this && this.isCompositionAreaVisible()) {
                return CompositionAreaHandler.compositionArea.getLocationOffset(n, n2);
            }
            return null;
        }
    }
    
    @Override
    public int getInsertPositionOffset() {
        final InputMethodRequests clientInputMethodRequests = this.getClientInputMethodRequests();
        if (clientInputMethodRequests != null) {
            return clientInputMethodRequests.getInsertPositionOffset();
        }
        return 0;
    }
    
    @Override
    public AttributedCharacterIterator getCommittedText(final int n, final int n2, final AttributedCharacterIterator.Attribute[] array) {
        final InputMethodRequests clientInputMethodRequests = this.getClientInputMethodRequests();
        if (clientInputMethodRequests != null) {
            return clientInputMethodRequests.getCommittedText(n, n2, array);
        }
        return CompositionAreaHandler.EMPTY_TEXT;
    }
    
    @Override
    public int getCommittedTextLength() {
        final InputMethodRequests clientInputMethodRequests = this.getClientInputMethodRequests();
        if (clientInputMethodRequests != null) {
            return clientInputMethodRequests.getCommittedTextLength();
        }
        return 0;
    }
    
    @Override
    public AttributedCharacterIterator cancelLatestCommittedText(final AttributedCharacterIterator.Attribute[] array) {
        final InputMethodRequests clientInputMethodRequests = this.getClientInputMethodRequests();
        if (clientInputMethodRequests != null) {
            return clientInputMethodRequests.cancelLatestCommittedText(array);
        }
        return null;
    }
    
    @Override
    public AttributedCharacterIterator getSelectedText(final AttributedCharacterIterator.Attribute[] array) {
        final InputMethodRequests clientInputMethodRequests = this.getClientInputMethodRequests();
        if (clientInputMethodRequests != null) {
            return clientInputMethodRequests.getSelectedText(array);
        }
        return CompositionAreaHandler.EMPTY_TEXT;
    }
    
    static {
        CompositionAreaHandler.compositionAreaLock = new Object();
        IM_ATTRIBUTES = new AttributedCharacterIterator.Attribute[] { TextAttribute.INPUT_METHOD_HIGHLIGHT };
        EMPTY_TEXT = new AttributedString("").getIterator();
    }
}
