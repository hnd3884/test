package sun.awt.im;

import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.awt.event.InputMethodEvent;
import java.awt.Graphics2D;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.im.InputMethodRequests;
import java.awt.Dimension;
import java.awt.event.WindowListener;
import java.awt.Component;
import java.awt.Color;
import javax.swing.border.LineBorder;
import java.awt.Toolkit;
import javax.swing.JFrame;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.event.InputMethodListener;
import javax.swing.JPanel;

public final class CompositionArea extends JPanel implements InputMethodListener
{
    private CompositionAreaHandler handler;
    private TextLayout composedTextLayout;
    private TextHitInfo caret;
    private JFrame compositionWindow;
    private static final int TEXT_ORIGIN_X = 5;
    private static final int TEXT_ORIGIN_Y = 15;
    private static final int PASSIVE_WIDTH = 480;
    private static final int WIDTH_MARGIN = 10;
    private static final int HEIGHT_MARGIN = 3;
    private static final long serialVersionUID = -1057247068746557444L;
    
    CompositionArea() {
        this.caret = null;
        this.compositionWindow = (JFrame)InputMethodContext.createInputMethodWindow(Toolkit.getProperty("AWT.CompositionWindowTitle", "Input Window"), null, true);
        this.setOpaque(true);
        this.setBorder(LineBorder.createGrayLineBorder());
        this.setForeground(Color.black);
        this.setBackground(Color.white);
        this.enableInputMethods(true);
        this.enableEvents(8L);
        this.compositionWindow.getContentPane().add(this);
        this.compositionWindow.addWindowListener(new FrameWindowAdapter());
        this.addInputMethodListener(this);
        this.compositionWindow.enableInputMethods(false);
        this.compositionWindow.pack();
        final Dimension size = this.compositionWindow.getSize();
        final Dimension screenSize = this.getToolkit().getScreenSize();
        this.compositionWindow.setLocation(screenSize.width - size.width - 20, screenSize.height - size.height - 100);
        this.compositionWindow.setVisible(false);
    }
    
    synchronized void setHandlerInfo(final CompositionAreaHandler handler, final InputContext inputContext) {
        this.handler = handler;
        ((InputMethodWindow)this.compositionWindow).setInputContext(inputContext);
    }
    
    @Override
    public InputMethodRequests getInputMethodRequests() {
        return this.handler;
    }
    
    private Rectangle getCaretRectangle(final TextHitInfo textHitInfo) {
        int round = 0;
        final TextLayout composedTextLayout = this.composedTextLayout;
        if (composedTextLayout != null) {
            round = Math.round(composedTextLayout.getCaretInfo(textHitInfo)[0]);
        }
        final Graphics graphics = this.getGraphics();
        FontMetrics fontMetrics = null;
        try {
            fontMetrics = graphics.getFontMetrics();
        }
        finally {
            graphics.dispose();
        }
        return new Rectangle(5 + round, 15 - fontMetrics.getAscent(), 0, fontMetrics.getAscent() + fontMetrics.getDescent());
    }
    
    @Override
    public void paint(final Graphics graphics) {
        super.paint(graphics);
        graphics.setColor(this.getForeground());
        final TextLayout composedTextLayout = this.composedTextLayout;
        if (composedTextLayout != null) {
            composedTextLayout.draw((Graphics2D)graphics, 5.0f, 15.0f);
        }
        if (this.caret != null) {
            final Rectangle caretRectangle = this.getCaretRectangle(this.caret);
            graphics.setXORMode(this.getBackground());
            graphics.fillRect(caretRectangle.x, caretRectangle.y, 1, caretRectangle.height);
            graphics.setPaintMode();
        }
    }
    
    void setCompositionAreaVisible(final boolean visible) {
        this.compositionWindow.setVisible(visible);
    }
    
    boolean isCompositionAreaVisible() {
        return this.compositionWindow.isVisible();
    }
    
    @Override
    public void inputMethodTextChanged(final InputMethodEvent inputMethodEvent) {
        this.handler.inputMethodTextChanged(inputMethodEvent);
    }
    
    @Override
    public void caretPositionChanged(final InputMethodEvent inputMethodEvent) {
        this.handler.caretPositionChanged(inputMethodEvent);
    }
    
    void setText(final AttributedCharacterIterator attributedCharacterIterator, final TextHitInfo caret) {
        this.composedTextLayout = null;
        if (attributedCharacterIterator == null) {
            this.compositionWindow.setVisible(false);
            this.caret = null;
        }
        else {
            if (!this.compositionWindow.isVisible()) {
                this.compositionWindow.setVisible(true);
            }
            final Graphics graphics = this.getGraphics();
            if (graphics == null) {
                return;
            }
            try {
                this.updateWindowLocation();
                this.composedTextLayout = new TextLayout(attributedCharacterIterator, ((Graphics2D)graphics).getFontRenderContext());
                final Rectangle2D bounds = this.composedTextLayout.getBounds();
                this.caret = caret;
                final int n = (int)graphics.getFontMetrics().getMaxCharBounds(graphics).getHeight() + 3;
                final int n2 = n + this.compositionWindow.getInsets().top + this.compositionWindow.getInsets().bottom;
                final int n3 = (this.handler.getClientInputMethodRequests() == null) ? 480 : ((int)bounds.getWidth() + 10);
                final int n4 = n3 + this.compositionWindow.getInsets().left + this.compositionWindow.getInsets().right;
                this.setPreferredSize(new Dimension(n3, n));
                this.compositionWindow.setSize(new Dimension(n4, n2));
                this.paint(graphics);
            }
            finally {
                graphics.dispose();
            }
        }
    }
    
    void setCaret(final TextHitInfo caret) {
        this.caret = caret;
        if (this.compositionWindow.isVisible()) {
            final Graphics graphics = this.getGraphics();
            try {
                this.paint(graphics);
            }
            finally {
                graphics.dispose();
            }
        }
    }
    
    void updateWindowLocation() {
        final InputMethodRequests clientInputMethodRequests = this.handler.getClientInputMethodRequests();
        if (clientInputMethodRequests == null) {
            return;
        }
        final Point location = new Point();
        final Rectangle textLocation = clientInputMethodRequests.getTextLocation(null);
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final Dimension size = this.compositionWindow.getSize();
        if (textLocation.x + size.width > screenSize.width) {
            location.x = screenSize.width - size.width;
        }
        else {
            location.x = textLocation.x;
        }
        if (textLocation.y + textLocation.height + 2 + size.height > screenSize.height) {
            location.y = textLocation.y - 2 - size.height;
        }
        else {
            location.y = textLocation.y + textLocation.height + 2;
        }
        this.compositionWindow.setLocation(location);
    }
    
    Rectangle getTextLocation(final TextHitInfo textHitInfo) {
        final Rectangle caretRectangle = this.getCaretRectangle(textHitInfo);
        final Point locationOnScreen = this.getLocationOnScreen();
        caretRectangle.translate(locationOnScreen.x, locationOnScreen.y);
        return caretRectangle;
    }
    
    TextHitInfo getLocationOffset(int n, int n2) {
        final TextLayout composedTextLayout = this.composedTextLayout;
        if (composedTextLayout == null) {
            return null;
        }
        final Point locationOnScreen = this.getLocationOnScreen();
        n -= locationOnScreen.x + 5;
        n2 -= locationOnScreen.y + 15;
        if (composedTextLayout.getBounds().contains(n, n2)) {
            return composedTextLayout.hitTestChar((float)n, (float)n2);
        }
        return null;
    }
    
    void setCompositionAreaUndecorated(final boolean undecorated) {
        if (this.compositionWindow.isDisplayable()) {
            this.compositionWindow.removeNotify();
        }
        this.compositionWindow.setUndecorated(undecorated);
        this.compositionWindow.pack();
    }
    
    class FrameWindowAdapter extends WindowAdapter
    {
        @Override
        public void windowActivated(final WindowEvent windowEvent) {
            CompositionArea.this.requestFocus();
        }
    }
}
