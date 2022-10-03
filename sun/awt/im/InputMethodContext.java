package sun.awt.im;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.awt.im.spi.InputMethod;
import sun.awt.InputMethodSupport;
import java.awt.Toolkit;
import java.awt.HeadlessException;
import java.awt.GraphicsEnvironment;
import javax.swing.JFrame;
import java.awt.Window;
import java.awt.im.InputMethodRequests;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.text.AttributedString;
import java.awt.AWTEvent;
import java.awt.event.InputMethodEvent;
import java.awt.font.TextHitInfo;
import java.text.AttributedCharacterIterator;
import java.awt.Component;

public class InputMethodContext extends InputContext implements java.awt.im.spi.InputMethodContext
{
    private boolean dispatchingCommittedText;
    private CompositionAreaHandler compositionAreaHandler;
    private Object compositionAreaHandlerLock;
    private static boolean belowTheSpotInputRequested;
    private boolean inputMethodSupportsBelowTheSpot;
    
    public InputMethodContext() {
        this.compositionAreaHandlerLock = new Object();
    }
    
    void setInputMethodSupportsBelowTheSpot(final boolean inputMethodSupportsBelowTheSpot) {
        this.inputMethodSupportsBelowTheSpot = inputMethodSupportsBelowTheSpot;
    }
    
    boolean useBelowTheSpotInput() {
        return InputMethodContext.belowTheSpotInputRequested && this.inputMethodSupportsBelowTheSpot;
    }
    
    private boolean haveActiveClient() {
        final Component clientComponent = this.getClientComponent();
        return clientComponent != null && clientComponent.getInputMethodRequests() != null;
    }
    
    @Override
    public void dispatchInputMethodEvent(final int n, final AttributedCharacterIterator attributedCharacterIterator, final int n2, final TextHitInfo textHitInfo, final TextHitInfo textHitInfo2) {
        final Component clientComponent = this.getClientComponent();
        if (clientComponent != null) {
            final InputMethodEvent inputMethodEvent = new InputMethodEvent(clientComponent, n, attributedCharacterIterator, n2, textHitInfo, textHitInfo2);
            if (this.haveActiveClient() && !this.useBelowTheSpotInput()) {
                clientComponent.dispatchEvent(inputMethodEvent);
            }
            else {
                this.getCompositionAreaHandler(true).processInputMethodEvent(inputMethodEvent);
            }
        }
    }
    
    synchronized void dispatchCommittedText(final Component component, final AttributedCharacterIterator attributedCharacterIterator, int n) {
        if (n == 0 || attributedCharacterIterator.getEndIndex() <= attributedCharacterIterator.getBeginIndex()) {
            return;
        }
        final long currentTimeMillis = System.currentTimeMillis();
        this.dispatchingCommittedText = true;
        try {
            if (component.getInputMethodRequests() != null) {
                final int beginIndex = attributedCharacterIterator.getBeginIndex();
                component.dispatchEvent(new InputMethodEvent(component, 1100, new AttributedString(attributedCharacterIterator, beginIndex, beginIndex + n).getIterator(), n, null, null));
            }
            else {
                for (char c = attributedCharacterIterator.first(); n-- > 0 && c != '\uffff'; c = attributedCharacterIterator.next()) {
                    component.dispatchEvent(new KeyEvent(component, 400, currentTimeMillis, 0, 0, c));
                }
            }
        }
        finally {
            this.dispatchingCommittedText = false;
        }
    }
    
    @Override
    public void dispatchEvent(final AWTEvent awtEvent) {
        if (awtEvent instanceof InputMethodEvent) {
            if (((Component)awtEvent.getSource()).getInputMethodRequests() == null || (this.useBelowTheSpotInput() && !this.dispatchingCommittedText)) {
                this.getCompositionAreaHandler(true).processInputMethodEvent((InputMethodEvent)awtEvent);
            }
        }
        else if (!this.dispatchingCommittedText) {
            super.dispatchEvent(awtEvent);
        }
    }
    
    private CompositionAreaHandler getCompositionAreaHandler(final boolean b) {
        synchronized (this.compositionAreaHandlerLock) {
            if (this.compositionAreaHandler == null) {
                this.compositionAreaHandler = new CompositionAreaHandler(this);
            }
            this.compositionAreaHandler.setClientComponent(this.getClientComponent());
            if (b) {
                this.compositionAreaHandler.grabCompositionArea(false);
            }
            return this.compositionAreaHandler;
        }
    }
    
    void grabCompositionArea(final boolean b) {
        synchronized (this.compositionAreaHandlerLock) {
            if (this.compositionAreaHandler != null) {
                this.compositionAreaHandler.grabCompositionArea(b);
            }
            else {
                CompositionAreaHandler.closeCompositionArea();
            }
        }
    }
    
    void releaseCompositionArea() {
        synchronized (this.compositionAreaHandlerLock) {
            if (this.compositionAreaHandler != null) {
                this.compositionAreaHandler.releaseCompositionArea();
            }
        }
    }
    
    boolean isCompositionAreaVisible() {
        return this.compositionAreaHandler != null && this.compositionAreaHandler.isCompositionAreaVisible();
    }
    
    void setCompositionAreaVisible(final boolean compositionAreaVisible) {
        if (this.compositionAreaHandler != null) {
            this.compositionAreaHandler.setCompositionAreaVisible(compositionAreaVisible);
        }
    }
    
    @Override
    public Rectangle getTextLocation(final TextHitInfo textHitInfo) {
        return this.getReq().getTextLocation(textHitInfo);
    }
    
    @Override
    public TextHitInfo getLocationOffset(final int n, final int n2) {
        return this.getReq().getLocationOffset(n, n2);
    }
    
    @Override
    public int getInsertPositionOffset() {
        return this.getReq().getInsertPositionOffset();
    }
    
    @Override
    public AttributedCharacterIterator getCommittedText(final int n, final int n2, final AttributedCharacterIterator.Attribute[] array) {
        return this.getReq().getCommittedText(n, n2, array);
    }
    
    @Override
    public int getCommittedTextLength() {
        return this.getReq().getCommittedTextLength();
    }
    
    @Override
    public AttributedCharacterIterator cancelLatestCommittedText(final AttributedCharacterIterator.Attribute[] array) {
        return this.getReq().cancelLatestCommittedText(array);
    }
    
    @Override
    public AttributedCharacterIterator getSelectedText(final AttributedCharacterIterator.Attribute[] array) {
        return this.getReq().getSelectedText(array);
    }
    
    private InputMethodRequests getReq() {
        if (this.haveActiveClient() && !this.useBelowTheSpotInput()) {
            return this.getClientComponent().getInputMethodRequests();
        }
        return this.getCompositionAreaHandler(false);
    }
    
    @Override
    public Window createInputMethodWindow(final String s, final boolean b) {
        return createInputMethodWindow(s, b ? this : null, false);
    }
    
    @Override
    public JFrame createInputMethodJFrame(final String s, final boolean b) {
        return (JFrame)createInputMethodWindow(s, b ? this : null, true);
    }
    
    static Window createInputMethodWindow(final String s, final InputContext inputContext, final boolean b) {
        if (GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException();
        }
        if (b) {
            return new InputMethodJFrame(s, inputContext);
        }
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        if (defaultToolkit instanceof InputMethodSupport) {
            return ((InputMethodSupport)defaultToolkit).createInputMethodWindow(s, inputContext);
        }
        throw new InternalError("Input methods must be supported");
    }
    
    @Override
    public void enableClientWindowNotification(final InputMethod inputMethod, final boolean b) {
        super.enableClientWindowNotification(inputMethod, b);
    }
    
    void setCompositionAreaUndecorated(final boolean compositionAreaUndecorated) {
        if (this.compositionAreaHandler != null) {
            this.compositionAreaHandler.setCompositionAreaUndecorated(compositionAreaUndecorated);
        }
    }
    
    static {
        String property = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("java.awt.im.style", null));
        if (property == null) {
            property = Toolkit.getProperty("java.awt.im.style", null);
        }
        InputMethodContext.belowTheSpotInputRequested = "below-the-spot".equals(property);
    }
}
