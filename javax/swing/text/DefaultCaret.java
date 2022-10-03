package javax.swing.text;

import java.beans.PropertyChangeEvent;
import javax.swing.event.DocumentEvent;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import javax.swing.JPasswordField;
import java.awt.event.ActionListener;
import javax.swing.plaf.TextUI;
import java.util.EventListener;
import javax.swing.event.ChangeListener;
import java.beans.PropertyChangeListener;
import javax.swing.event.DocumentListener;
import java.awt.Graphics;
import java.awt.datatransfer.Transferable;
import javax.swing.TransferHandler;
import java.awt.datatransfer.Clipboard;
import java.awt.HeadlessException;
import javax.swing.JComponent;
import java.awt.Component;
import javax.swing.UIManager;
import java.awt.AWTEvent;
import sun.swing.SwingUtilities2;
import javax.swing.ActionMap;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import javax.swing.SwingUtilities;
import java.awt.event.MouseEvent;
import javax.swing.Action;
import java.awt.Point;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.event.FocusListener;
import java.awt.Rectangle;

public class DefaultCaret extends Rectangle implements Caret, FocusListener, MouseListener, MouseMotionListener
{
    public static final int UPDATE_WHEN_ON_EDT = 0;
    public static final int NEVER_UPDATE = 1;
    public static final int ALWAYS_UPDATE = 2;
    protected EventListenerList listenerList;
    protected transient ChangeEvent changeEvent;
    JTextComponent component;
    int updatePolicy;
    boolean visible;
    boolean active;
    int dot;
    int mark;
    Object selectionTag;
    boolean selectionVisible;
    Timer flasher;
    Point magicCaretPosition;
    transient Position.Bias dotBias;
    transient Position.Bias markBias;
    boolean dotLTR;
    boolean markLTR;
    transient Handler handler;
    private transient int[] flagXPoints;
    private transient int[] flagYPoints;
    private transient NavigationFilter.FilterBypass filterBypass;
    private static transient Action selectWord;
    private static transient Action selectLine;
    private boolean ownsSelection;
    private boolean forceCaretPositionChange;
    private transient boolean shouldHandleRelease;
    private transient MouseEvent selectedWordEvent;
    private int caretWidth;
    private float aspectRatio;
    
    public DefaultCaret() {
        this.listenerList = new EventListenerList();
        this.changeEvent = null;
        this.updatePolicy = 0;
        this.handler = new Handler();
        this.flagXPoints = new int[3];
        this.flagYPoints = new int[3];
        this.selectedWordEvent = null;
        this.caretWidth = -1;
        this.aspectRatio = -1.0f;
    }
    
    public void setUpdatePolicy(final int updatePolicy) {
        this.updatePolicy = updatePolicy;
    }
    
    public int getUpdatePolicy() {
        return this.updatePolicy;
    }
    
    protected final JTextComponent getComponent() {
        return this.component;
    }
    
    protected final synchronized void repaint() {
        if (this.component != null) {
            this.component.repaint(this.x, this.y, this.width, this.height);
        }
    }
    
    protected synchronized void damage(final Rectangle rectangle) {
        if (rectangle != null) {
            final int caretWidth = this.getCaretWidth(rectangle.height);
            this.x = rectangle.x - 4 - (caretWidth >> 1);
            this.y = rectangle.y;
            this.width = 9 + caretWidth;
            this.height = rectangle.height;
            this.repaint();
        }
    }
    
    protected void adjustVisibility(final Rectangle rectangle) {
        if (this.component == null) {
            return;
        }
        if (SwingUtilities.isEventDispatchThread()) {
            this.component.scrollRectToVisible(rectangle);
        }
        else {
            SwingUtilities.invokeLater(new SafeScroller(rectangle));
        }
    }
    
    protected Highlighter.HighlightPainter getSelectionPainter() {
        return DefaultHighlighter.DefaultPainter;
    }
    
    protected void positionCaret(final MouseEvent mouseEvent) {
        final Point point = new Point(mouseEvent.getX(), mouseEvent.getY());
        final Position.Bias[] array = { null };
        final int viewToModel = this.component.getUI().viewToModel(this.component, point, array);
        if (array[0] == null) {
            array[0] = Position.Bias.Forward;
        }
        if (viewToModel >= 0) {
            this.setDot(viewToModel, array[0]);
        }
    }
    
    protected void moveCaret(final MouseEvent mouseEvent) {
        final Point point = new Point(mouseEvent.getX(), mouseEvent.getY());
        final Position.Bias[] array = { null };
        final int viewToModel = this.component.getUI().viewToModel(this.component, point, array);
        if (array[0] == null) {
            array[0] = Position.Bias.Forward;
        }
        if (viewToModel >= 0) {
            this.moveDot(viewToModel, array[0]);
        }
    }
    
    @Override
    public void focusGained(final FocusEvent focusEvent) {
        if (this.component.isEnabled()) {
            if (this.component.isEditable()) {
                this.setVisible(true);
            }
            this.setSelectionVisible(true);
        }
    }
    
    @Override
    public void focusLost(final FocusEvent focusEvent) {
        this.setVisible(false);
        this.setSelectionVisible(this.ownsSelection || focusEvent.isTemporary());
    }
    
    private void selectWord(final MouseEvent selectedWordEvent) {
        if (this.selectedWordEvent != null && this.selectedWordEvent.getX() == selectedWordEvent.getX() && this.selectedWordEvent.getY() == selectedWordEvent.getY()) {
            return;
        }
        Action action = null;
        final ActionMap actionMap = this.getComponent().getActionMap();
        if (actionMap != null) {
            action = actionMap.get("select-word");
        }
        if (action == null) {
            if (DefaultCaret.selectWord == null) {
                DefaultCaret.selectWord = new DefaultEditorKit.SelectWordAction();
            }
            action = DefaultCaret.selectWord;
        }
        action.actionPerformed(new ActionEvent(this.getComponent(), 1001, null, selectedWordEvent.getWhen(), selectedWordEvent.getModifiers()));
        this.selectedWordEvent = selectedWordEvent;
    }
    
    @Override
    public void mouseClicked(final MouseEvent mouseEvent) {
        if (this.getComponent() == null) {
            return;
        }
        final int adjustedClickCount = SwingUtilities2.getAdjustedClickCount(this.getComponent(), mouseEvent);
        if (!mouseEvent.isConsumed()) {
            if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
                if (adjustedClickCount == 1) {
                    this.selectedWordEvent = null;
                }
                else if (adjustedClickCount == 2 && SwingUtilities2.canEventAccessSystemClipboard(mouseEvent)) {
                    this.selectWord(mouseEvent);
                    this.selectedWordEvent = null;
                }
                else if (adjustedClickCount == 3 && SwingUtilities2.canEventAccessSystemClipboard(mouseEvent)) {
                    Action action = null;
                    final ActionMap actionMap = this.getComponent().getActionMap();
                    if (actionMap != null) {
                        action = actionMap.get("select-line");
                    }
                    if (action == null) {
                        if (DefaultCaret.selectLine == null) {
                            DefaultCaret.selectLine = new DefaultEditorKit.SelectLineAction();
                        }
                        action = DefaultCaret.selectLine;
                    }
                    action.actionPerformed(new ActionEvent(this.getComponent(), 1001, null, mouseEvent.getWhen(), mouseEvent.getModifiers()));
                }
            }
            else if (SwingUtilities.isMiddleMouseButton(mouseEvent) && adjustedClickCount == 1 && this.component.isEditable() && this.component.isEnabled() && SwingUtilities2.canEventAccessSystemClipboard(mouseEvent)) {
                final JTextComponent textComponent = (JTextComponent)mouseEvent.getSource();
                if (textComponent != null) {
                    try {
                        final Clipboard systemSelection = textComponent.getToolkit().getSystemSelection();
                        if (systemSelection != null) {
                            this.adjustCaret(mouseEvent);
                            final TransferHandler transferHandler = textComponent.getTransferHandler();
                            if (transferHandler != null) {
                                Transferable contents = null;
                                try {
                                    contents = systemSelection.getContents(null);
                                }
                                catch (final IllegalStateException ex) {
                                    UIManager.getLookAndFeel().provideErrorFeedback(textComponent);
                                }
                                if (contents != null) {
                                    transferHandler.importData(textComponent, contents);
                                }
                            }
                            this.adjustFocus(true);
                        }
                    }
                    catch (final HeadlessException ex2) {}
                }
            }
        }
    }
    
    @Override
    public void mousePressed(final MouseEvent mouseEvent) {
        final int adjustedClickCount = SwingUtilities2.getAdjustedClickCount(this.getComponent(), mouseEvent);
        if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
            if (mouseEvent.isConsumed()) {
                this.shouldHandleRelease = true;
            }
            else {
                this.shouldHandleRelease = false;
                this.adjustCaretAndFocus(mouseEvent);
                if (adjustedClickCount == 2 && SwingUtilities2.canEventAccessSystemClipboard(mouseEvent)) {
                    this.selectWord(mouseEvent);
                }
            }
        }
    }
    
    void adjustCaretAndFocus(final MouseEvent mouseEvent) {
        this.adjustCaret(mouseEvent);
        this.adjustFocus(false);
    }
    
    private void adjustCaret(final MouseEvent mouseEvent) {
        if ((mouseEvent.getModifiers() & 0x1) != 0x0 && this.getDot() != -1) {
            this.moveCaret(mouseEvent);
        }
        else if (!mouseEvent.isPopupTrigger()) {
            this.positionCaret(mouseEvent);
        }
    }
    
    private void adjustFocus(final boolean b) {
        if (this.component != null && this.component.isEnabled() && this.component.isRequestFocusEnabled()) {
            if (b) {
                this.component.requestFocusInWindow();
            }
            else {
                this.component.requestFocus();
            }
        }
    }
    
    @Override
    public void mouseReleased(final MouseEvent mouseEvent) {
        if (!mouseEvent.isConsumed() && this.shouldHandleRelease && SwingUtilities.isLeftMouseButton(mouseEvent)) {
            this.adjustCaretAndFocus(mouseEvent);
        }
    }
    
    @Override
    public void mouseEntered(final MouseEvent mouseEvent) {
    }
    
    @Override
    public void mouseExited(final MouseEvent mouseEvent) {
    }
    
    @Override
    public void mouseDragged(final MouseEvent mouseEvent) {
        if (!mouseEvent.isConsumed() && SwingUtilities.isLeftMouseButton(mouseEvent)) {
            this.moveCaret(mouseEvent);
        }
    }
    
    @Override
    public void mouseMoved(final MouseEvent mouseEvent) {
    }
    
    @Override
    public void paint(final Graphics graphics) {
        if (this.isVisible()) {
            try {
                final Rectangle modelToView = this.component.getUI().modelToView(this.component, this.dot, this.dotBias);
                if (modelToView == null || (modelToView.width == 0 && modelToView.height == 0)) {
                    return;
                }
                if (this.width > 0 && this.height > 0 && !this._contains(modelToView.x, modelToView.y, modelToView.width, modelToView.height)) {
                    final Rectangle clipBounds = graphics.getClipBounds();
                    if (clipBounds != null && !clipBounds.contains(this)) {
                        this.repaint();
                    }
                    this.damage(modelToView);
                }
                graphics.setColor(this.component.getCaretColor());
                final int caretWidth = this.getCaretWidth(modelToView.height);
                final Rectangle rectangle = modelToView;
                rectangle.x -= caretWidth >> 1;
                graphics.fillRect(modelToView.x, modelToView.y, caretWidth, modelToView.height);
                final Document document = this.component.getDocument();
                if (document instanceof AbstractDocument) {
                    final Element bidiRootElement = ((AbstractDocument)document).getBidiRootElement();
                    if (bidiRootElement != null && bidiRootElement.getElementCount() > 1) {
                        this.flagXPoints[0] = modelToView.x + (this.dotLTR ? caretWidth : false);
                        this.flagYPoints[0] = modelToView.y;
                        this.flagXPoints[1] = this.flagXPoints[0];
                        this.flagYPoints[1] = this.flagYPoints[0] + 4;
                        this.flagXPoints[2] = this.flagXPoints[0] + (this.dotLTR ? 4 : -4);
                        this.flagYPoints[2] = this.flagYPoints[0];
                        graphics.fillPolygon(this.flagXPoints, this.flagYPoints, 3);
                    }
                }
            }
            catch (final BadLocationException ex) {}
        }
    }
    
    @Override
    public void install(final JTextComponent component) {
        this.component = component;
        final Document document = component.getDocument();
        final int n = 0;
        this.mark = n;
        this.dot = n;
        final boolean b = true;
        this.markLTR = b;
        this.dotLTR = b;
        final Position.Bias forward = Position.Bias.Forward;
        this.markBias = forward;
        this.dotBias = forward;
        if (document != null) {
            document.addDocumentListener(this.handler);
        }
        component.addPropertyChangeListener(this.handler);
        component.addFocusListener(this);
        component.addMouseListener(this);
        component.addMouseMotionListener(this);
        if (this.component.hasFocus()) {
            this.focusGained(null);
        }
        final Number n2 = (Number)component.getClientProperty("caretAspectRatio");
        if (n2 != null) {
            this.aspectRatio = n2.floatValue();
        }
        else {
            this.aspectRatio = -1.0f;
        }
        final Integer n3 = (Integer)component.getClientProperty("caretWidth");
        if (n3 != null) {
            this.caretWidth = n3;
        }
        else {
            this.caretWidth = -1;
        }
    }
    
    @Override
    public void deinstall(final JTextComponent textComponent) {
        textComponent.removeMouseListener(this);
        textComponent.removeMouseMotionListener(this);
        textComponent.removeFocusListener(this);
        textComponent.removePropertyChangeListener(this.handler);
        final Document document = textComponent.getDocument();
        if (document != null) {
            document.removeDocumentListener(this.handler);
        }
        synchronized (this) {
            this.component = null;
        }
        if (this.flasher != null) {
            this.flasher.stop();
        }
    }
    
    @Override
    public void addChangeListener(final ChangeListener changeListener) {
        this.listenerList.add(ChangeListener.class, changeListener);
    }
    
    @Override
    public void removeChangeListener(final ChangeListener changeListener) {
        this.listenerList.remove(ChangeListener.class, changeListener);
    }
    
    public ChangeListener[] getChangeListeners() {
        return this.listenerList.getListeners(ChangeListener.class);
    }
    
    protected void fireStateChanged() {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == ChangeListener.class) {
                if (this.changeEvent == null) {
                    this.changeEvent = new ChangeEvent(this);
                }
                ((ChangeListener)listenerList[i + 1]).stateChanged(this.changeEvent);
            }
        }
    }
    
    public <T extends EventListener> T[] getListeners(final Class<T> clazz) {
        return this.listenerList.getListeners(clazz);
    }
    
    @Override
    public void setSelectionVisible(final boolean selectionVisible) {
        if (selectionVisible != this.selectionVisible) {
            this.selectionVisible = selectionVisible;
            if (this.selectionVisible) {
                final Highlighter highlighter = this.component.getHighlighter();
                if (this.dot != this.mark && highlighter != null && this.selectionTag == null) {
                    final int min = Math.min(this.dot, this.mark);
                    final int max = Math.max(this.dot, this.mark);
                    final Highlighter.HighlightPainter selectionPainter = this.getSelectionPainter();
                    try {
                        this.selectionTag = highlighter.addHighlight(min, max, selectionPainter);
                    }
                    catch (final BadLocationException ex) {
                        this.selectionTag = null;
                    }
                }
            }
            else if (this.selectionTag != null) {
                this.component.getHighlighter().removeHighlight(this.selectionTag);
                this.selectionTag = null;
            }
        }
    }
    
    @Override
    public boolean isSelectionVisible() {
        return this.selectionVisible;
    }
    
    public boolean isActive() {
        return this.active;
    }
    
    @Override
    public boolean isVisible() {
        return this.visible;
    }
    
    @Override
    public void setVisible(final boolean b) {
        this.active = b;
        if (this.component != null) {
            final TextUI ui = this.component.getUI();
            if (this.visible != b) {
                this.visible = b;
                try {
                    this.damage(ui.modelToView(this.component, this.dot, this.dotBias));
                }
                catch (final BadLocationException ex) {}
            }
        }
        if (this.flasher != null) {
            if (this.visible) {
                this.flasher.start();
            }
            else {
                this.flasher.stop();
            }
        }
    }
    
    @Override
    public void setBlinkRate(final int delay) {
        if (delay != 0) {
            if (this.flasher == null) {
                this.flasher = new Timer(delay, this.handler);
            }
            this.flasher.setDelay(delay);
        }
        else if (this.flasher != null) {
            this.flasher.stop();
            this.flasher.removeActionListener(this.handler);
            this.flasher = null;
        }
    }
    
    @Override
    public int getBlinkRate() {
        return (this.flasher == null) ? 0 : this.flasher.getDelay();
    }
    
    @Override
    public int getDot() {
        return this.dot;
    }
    
    @Override
    public int getMark() {
        return this.mark;
    }
    
    @Override
    public void setDot(final int n) {
        this.setDot(n, Position.Bias.Forward);
    }
    
    @Override
    public void moveDot(final int n) {
        this.moveDot(n, Position.Bias.Forward);
    }
    
    public void moveDot(final int n, final Position.Bias bias) {
        if (bias == null) {
            throw new IllegalArgumentException("null bias");
        }
        if (!this.component.isEnabled()) {
            this.setDot(n, bias);
            return;
        }
        if (n != this.dot) {
            final NavigationFilter navigationFilter = this.component.getNavigationFilter();
            if (navigationFilter != null) {
                navigationFilter.moveDot(this.getFilterBypass(), n, bias);
            }
            else {
                this.handleMoveDot(n, bias);
            }
        }
    }
    
    void handleMoveDot(final int n, final Position.Bias bias) {
        this.changeCaretPosition(n, bias);
        if (this.selectionVisible) {
            final Highlighter highlighter = this.component.getHighlighter();
            if (highlighter != null) {
                final int min = Math.min(n, this.mark);
                final int max = Math.max(n, this.mark);
                if (min == max) {
                    if (this.selectionTag != null) {
                        highlighter.removeHighlight(this.selectionTag);
                        this.selectionTag = null;
                    }
                }
                else {
                    try {
                        if (this.selectionTag != null) {
                            highlighter.changeHighlight(this.selectionTag, min, max);
                        }
                        else {
                            this.selectionTag = highlighter.addHighlight(min, max, this.getSelectionPainter());
                        }
                    }
                    catch (final BadLocationException ex) {
                        throw new StateInvariantError("Bad caret position");
                    }
                }
            }
        }
    }
    
    public void setDot(final int n, final Position.Bias bias) {
        if (bias == null) {
            throw new IllegalArgumentException("null bias");
        }
        final NavigationFilter navigationFilter = this.component.getNavigationFilter();
        if (navigationFilter != null) {
            navigationFilter.setDot(this.getFilterBypass(), n, bias);
        }
        else {
            this.handleSetDot(n, bias);
        }
    }
    
    void handleSetDot(int mark, Position.Bias forward) {
        final Document document = this.component.getDocument();
        if (document != null) {
            mark = Math.min(mark, document.getLength());
        }
        mark = Math.max(mark, 0);
        if (mark == 0) {
            forward = Position.Bias.Forward;
        }
        this.mark = mark;
        if (this.dot != mark || this.dotBias != forward || this.selectionTag != null || this.forceCaretPositionChange) {
            this.changeCaretPosition(mark, forward);
        }
        this.markBias = this.dotBias;
        this.markLTR = this.dotLTR;
        final Highlighter highlighter = this.component.getHighlighter();
        if (highlighter != null && this.selectionTag != null) {
            highlighter.removeHighlight(this.selectionTag);
            this.selectionTag = null;
        }
    }
    
    public Position.Bias getDotBias() {
        return this.dotBias;
    }
    
    public Position.Bias getMarkBias() {
        return this.markBias;
    }
    
    boolean isDotLeftToRight() {
        return this.dotLTR;
    }
    
    boolean isMarkLeftToRight() {
        return this.markLTR;
    }
    
    boolean isPositionLTR(int n, final Position.Bias bias) {
        final Document document = this.component.getDocument();
        if (bias == Position.Bias.Backward && --n < 0) {
            n = 0;
        }
        return AbstractDocument.isLeftToRight(document, n, n);
    }
    
    Position.Bias guessBiasForOffset(final int n, Position.Bias bias, final boolean b) {
        if (b != this.isPositionLTR(n, bias)) {
            bias = Position.Bias.Backward;
        }
        else if (bias != Position.Bias.Backward && b != this.isPositionLTR(n, Position.Bias.Backward)) {
            bias = Position.Bias.Backward;
        }
        if (bias == Position.Bias.Backward && n > 0) {
            try {
                final Segment segment = new Segment();
                this.component.getDocument().getText(n - 1, 1, segment);
                if (segment.count > 0 && segment.array[segment.offset] == '\n') {
                    bias = Position.Bias.Forward;
                }
            }
            catch (final BadLocationException ex) {}
        }
        return bias;
    }
    
    void changeCaretPosition(final int dot, final Position.Bias dotBias) {
        this.repaint();
        if (this.flasher != null && this.flasher.isRunning()) {
            this.visible = true;
            this.flasher.restart();
        }
        this.dot = dot;
        this.dotBias = dotBias;
        this.dotLTR = this.isPositionLTR(dot, dotBias);
        this.fireStateChanged();
        this.updateSystemSelection();
        this.setMagicCaretPosition(null);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                DefaultCaret.this.repaintNewCaret();
            }
        });
    }
    
    void repaintNewCaret() {
        if (this.component != null) {
            final TextUI ui = this.component.getUI();
            final Document document = this.component.getDocument();
            if (ui != null && document != null) {
                Rectangle modelToView;
                try {
                    modelToView = ui.modelToView(this.component, this.dot, this.dotBias);
                }
                catch (final BadLocationException ex) {
                    modelToView = null;
                }
                if (modelToView != null) {
                    this.adjustVisibility(modelToView);
                    if (this.getMagicCaretPosition() == null) {
                        this.setMagicCaretPosition(new Point(modelToView.x, modelToView.y));
                    }
                }
                this.damage(modelToView);
            }
        }
    }
    
    private void updateSystemSelection() {
        if (!SwingUtilities2.canCurrentEventAccessSystemClipboard()) {
            return;
        }
        if (this.dot != this.mark && this.component != null && this.component.hasFocus()) {
            final Clipboard systemSelection = this.getSystemSelection();
            if (systemSelection != null) {
                String selectedText;
                if (this.component instanceof JPasswordField && this.component.getClientProperty("JPasswordField.cutCopyAllowed") != Boolean.TRUE) {
                    StringBuilder sb = null;
                    final char echoChar = ((JPasswordField)this.component).getEchoChar();
                    final int min = Math.min(this.getDot(), this.getMark());
                    for (int max = Math.max(this.getDot(), this.getMark()), i = min; i < max; ++i) {
                        if (sb == null) {
                            sb = new StringBuilder();
                        }
                        sb.append(echoChar);
                    }
                    selectedText = ((sb != null) ? sb.toString() : null);
                }
                else {
                    selectedText = this.component.getSelectedText();
                }
                try {
                    systemSelection.setContents(new StringSelection(selectedText), this.getClipboardOwner());
                    this.ownsSelection = true;
                }
                catch (final IllegalStateException ex) {}
            }
        }
    }
    
    private Clipboard getSystemSelection() {
        try {
            return this.component.getToolkit().getSystemSelection();
        }
        catch (final HeadlessException ex) {}
        catch (final SecurityException ex2) {}
        return null;
    }
    
    private ClipboardOwner getClipboardOwner() {
        return this.handler;
    }
    
    private void ensureValidPosition() {
        final int length = this.component.getDocument().getLength();
        if (this.dot > length || this.mark > length) {
            this.handleSetDot(length, Position.Bias.Forward);
        }
    }
    
    @Override
    public void setMagicCaretPosition(final Point magicCaretPosition) {
        this.magicCaretPosition = magicCaretPosition;
    }
    
    @Override
    public Point getMagicCaretPosition() {
        return this.magicCaretPosition;
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o;
    }
    
    @Override
    public String toString() {
        return "Dot=(" + this.dot + ", " + this.dotBias + ")" + " Mark=(" + this.mark + ", " + this.markBias + ")";
    }
    
    private NavigationFilter.FilterBypass getFilterBypass() {
        if (this.filterBypass == null) {
            this.filterBypass = new DefaultFilterBypass();
        }
        return this.filterBypass;
    }
    
    private boolean _contains(final int n, final int n2, int n3, int n4) {
        final int width = this.width;
        final int height = this.height;
        if ((width | height | n3 | n4) < 0) {
            return false;
        }
        final int x = this.x;
        final int y = this.y;
        if (n < x || n2 < y) {
            return false;
        }
        if (n3 > 0) {
            final int n5 = width + x;
            n3 += n;
            if (n3 <= n) {
                if (n5 >= x || n3 > n5) {
                    return false;
                }
            }
            else if (n5 >= x && n3 > n5) {
                return false;
            }
        }
        else if (x + width < n) {
            return false;
        }
        if (n4 > 0) {
            final int n6 = height + y;
            n4 += n2;
            if (n4 <= n2) {
                if (n6 >= y || n4 > n6) {
                    return false;
                }
            }
            else if (n6 >= y && n4 > n6) {
                return false;
            }
        }
        else if (y + height < n2) {
            return false;
        }
        return true;
    }
    
    int getCaretWidth(final int n) {
        if (this.aspectRatio > -1.0f) {
            return (int)(this.aspectRatio * n) + 1;
        }
        if (this.caretWidth > -1) {
            return this.caretWidth;
        }
        final Object value = UIManager.get("Caret.width");
        if (value instanceof Integer) {
            return (int)value;
        }
        return 1;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        objectInputStream.defaultReadObject();
        this.handler = new Handler();
        if (!objectInputStream.readBoolean()) {
            this.dotBias = Position.Bias.Forward;
        }
        else {
            this.dotBias = Position.Bias.Backward;
        }
        if (!objectInputStream.readBoolean()) {
            this.markBias = Position.Bias.Forward;
        }
        else {
            this.markBias = Position.Bias.Backward;
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeBoolean(this.dotBias == Position.Bias.Backward);
        objectOutputStream.writeBoolean(this.markBias == Position.Bias.Backward);
    }
    
    static {
        DefaultCaret.selectWord = null;
        DefaultCaret.selectLine = null;
    }
    
    class SafeScroller implements Runnable
    {
        Rectangle r;
        
        SafeScroller(final Rectangle r) {
            this.r = r;
        }
        
        @Override
        public void run() {
            if (DefaultCaret.this.component != null) {
                DefaultCaret.this.component.scrollRectToVisible(this.r);
            }
        }
    }
    
    class Handler implements PropertyChangeListener, DocumentListener, ActionListener, ClipboardOwner
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if ((DefaultCaret.this.width == 0 || DefaultCaret.this.height == 0) && DefaultCaret.this.component != null) {
                final TextUI ui = DefaultCaret.this.component.getUI();
                try {
                    final Rectangle modelToView = ui.modelToView(DefaultCaret.this.component, DefaultCaret.this.dot, DefaultCaret.this.dotBias);
                    if (modelToView != null && modelToView.width != 0 && modelToView.height != 0) {
                        DefaultCaret.this.damage(modelToView);
                    }
                }
                catch (final BadLocationException ex) {}
            }
            DefaultCaret.this.visible = !DefaultCaret.this.visible;
            DefaultCaret.this.repaint();
        }
        
        @Override
        public void insertUpdate(final DocumentEvent documentEvent) {
            if (DefaultCaret.this.getUpdatePolicy() == 1 || (DefaultCaret.this.getUpdatePolicy() == 0 && !SwingUtilities.isEventDispatchThread())) {
                if ((documentEvent.getOffset() <= DefaultCaret.this.dot || documentEvent.getOffset() <= DefaultCaret.this.mark) && DefaultCaret.this.selectionTag != null) {
                    try {
                        DefaultCaret.this.component.getHighlighter().changeHighlight(DefaultCaret.this.selectionTag, Math.min(DefaultCaret.this.dot, DefaultCaret.this.mark), Math.max(DefaultCaret.this.dot, DefaultCaret.this.mark));
                    }
                    catch (final BadLocationException ex) {
                        ex.printStackTrace();
                    }
                }
                return;
            }
            final int offset = documentEvent.getOffset();
            final int length = documentEvent.getLength();
            int dot = DefaultCaret.this.dot;
            short n = 0;
            if (documentEvent instanceof AbstractDocument.UndoRedoDocumentEvent) {
                DefaultCaret.this.setDot(offset + length);
                return;
            }
            if (dot >= offset) {
                dot += length;
                n |= 0x1;
            }
            int mark = DefaultCaret.this.mark;
            if (mark >= offset) {
                mark += length;
                n |= 0x2;
            }
            if (n != 0) {
                Position.Bias bias = DefaultCaret.this.dotBias;
                if (DefaultCaret.this.dot == offset) {
                    final Document document = DefaultCaret.this.component.getDocument();
                    boolean b;
                    try {
                        final Segment segment = new Segment();
                        document.getText(dot - 1, 1, segment);
                        b = (segment.count > 0 && segment.array[segment.offset] == '\n');
                    }
                    catch (final BadLocationException ex2) {
                        b = false;
                    }
                    if (b) {
                        bias = Position.Bias.Forward;
                    }
                    else {
                        bias = Position.Bias.Backward;
                    }
                }
                if (mark == dot) {
                    DefaultCaret.this.setDot(dot, bias);
                    DefaultCaret.this.ensureValidPosition();
                }
                else {
                    DefaultCaret.this.setDot(mark, DefaultCaret.this.markBias);
                    if (DefaultCaret.this.getDot() == mark) {
                        DefaultCaret.this.moveDot(dot, bias);
                    }
                    DefaultCaret.this.ensureValidPosition();
                }
            }
        }
        
        @Override
        public void removeUpdate(final DocumentEvent documentEvent) {
            if (DefaultCaret.this.getUpdatePolicy() == 1 || (DefaultCaret.this.getUpdatePolicy() == 0 && !SwingUtilities.isEventDispatchThread())) {
                final int length = DefaultCaret.this.component.getDocument().getLength();
                DefaultCaret.this.dot = Math.min(DefaultCaret.this.dot, length);
                DefaultCaret.this.mark = Math.min(DefaultCaret.this.mark, length);
                if ((documentEvent.getOffset() < DefaultCaret.this.dot || documentEvent.getOffset() < DefaultCaret.this.mark) && DefaultCaret.this.selectionTag != null) {
                    try {
                        DefaultCaret.this.component.getHighlighter().changeHighlight(DefaultCaret.this.selectionTag, Math.min(DefaultCaret.this.dot, DefaultCaret.this.mark), Math.max(DefaultCaret.this.dot, DefaultCaret.this.mark));
                    }
                    catch (final BadLocationException ex) {
                        ex.printStackTrace();
                    }
                }
                return;
            }
            final int offset = documentEvent.getOffset();
            final int n = offset + documentEvent.getLength();
            int dot = DefaultCaret.this.dot;
            boolean b = false;
            int mark = DefaultCaret.this.mark;
            boolean b2 = false;
            if (documentEvent instanceof AbstractDocument.UndoRedoDocumentEvent) {
                DefaultCaret.this.setDot(offset);
                return;
            }
            if (dot >= n) {
                dot -= n - offset;
                if (dot == n) {
                    b = true;
                }
            }
            else if (dot >= offset) {
                dot = offset;
                b = true;
            }
            if (mark >= n) {
                mark -= n - offset;
                if (mark == n) {
                    b2 = true;
                }
            }
            else if (mark >= offset) {
                mark = offset;
                b2 = true;
            }
            if (mark == dot) {
                DefaultCaret.this.forceCaretPositionChange = true;
                try {
                    DefaultCaret.this.setDot(dot, DefaultCaret.this.guessBiasForOffset(dot, DefaultCaret.this.dotBias, DefaultCaret.this.dotLTR));
                }
                finally {
                    DefaultCaret.this.forceCaretPositionChange = false;
                }
                DefaultCaret.this.ensureValidPosition();
            }
            else {
                Position.Bias bias = DefaultCaret.this.dotBias;
                Position.Bias bias2 = DefaultCaret.this.markBias;
                if (b) {
                    bias = DefaultCaret.this.guessBiasForOffset(dot, bias, DefaultCaret.this.dotLTR);
                }
                if (b2) {
                    bias2 = DefaultCaret.this.guessBiasForOffset(DefaultCaret.this.mark, bias2, DefaultCaret.this.markLTR);
                }
                DefaultCaret.this.setDot(mark, bias2);
                if (DefaultCaret.this.getDot() == mark) {
                    DefaultCaret.this.moveDot(dot, bias);
                }
                DefaultCaret.this.ensureValidPosition();
            }
        }
        
        @Override
        public void changedUpdate(final DocumentEvent documentEvent) {
            if (DefaultCaret.this.getUpdatePolicy() == 1 || (DefaultCaret.this.getUpdatePolicy() == 0 && !SwingUtilities.isEventDispatchThread())) {
                return;
            }
            if (documentEvent instanceof AbstractDocument.UndoRedoDocumentEvent) {
                DefaultCaret.this.setDot(documentEvent.getOffset() + documentEvent.getLength());
            }
        }
        
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final Object oldValue = propertyChangeEvent.getOldValue();
            final Object newValue = propertyChangeEvent.getNewValue();
            if (oldValue instanceof Document || newValue instanceof Document) {
                DefaultCaret.this.setDot(0);
                if (oldValue != null) {
                    ((Document)oldValue).removeDocumentListener(this);
                }
                if (newValue != null) {
                    ((Document)newValue).addDocumentListener(this);
                }
            }
            else if ("enabled".equals(propertyChangeEvent.getPropertyName())) {
                final Boolean b = (Boolean)propertyChangeEvent.getNewValue();
                if (DefaultCaret.this.component.isFocusOwner()) {
                    if (b == Boolean.TRUE) {
                        if (DefaultCaret.this.component.isEditable()) {
                            DefaultCaret.this.setVisible(true);
                        }
                        DefaultCaret.this.setSelectionVisible(true);
                    }
                    else {
                        DefaultCaret.this.setVisible(false);
                        DefaultCaret.this.setSelectionVisible(false);
                    }
                }
            }
            else if ("caretWidth".equals(propertyChangeEvent.getPropertyName())) {
                final Integer n = (Integer)propertyChangeEvent.getNewValue();
                if (n != null) {
                    DefaultCaret.this.caretWidth = n;
                }
                else {
                    DefaultCaret.this.caretWidth = -1;
                }
                DefaultCaret.this.repaint();
            }
            else if ("caretAspectRatio".equals(propertyChangeEvent.getPropertyName())) {
                final Number n2 = (Number)propertyChangeEvent.getNewValue();
                if (n2 != null) {
                    DefaultCaret.this.aspectRatio = n2.floatValue();
                }
                else {
                    DefaultCaret.this.aspectRatio = -1.0f;
                }
                DefaultCaret.this.repaint();
            }
        }
        
        @Override
        public void lostOwnership(final Clipboard clipboard, final Transferable transferable) {
            if (DefaultCaret.this.ownsSelection) {
                DefaultCaret.this.ownsSelection = false;
                if (DefaultCaret.this.component != null && !DefaultCaret.this.component.hasFocus()) {
                    DefaultCaret.this.setSelectionVisible(false);
                }
            }
        }
    }
    
    private class DefaultFilterBypass extends NavigationFilter.FilterBypass
    {
        @Override
        public Caret getCaret() {
            return DefaultCaret.this;
        }
        
        @Override
        public void setDot(final int n, final Position.Bias bias) {
            DefaultCaret.this.handleSetDot(n, bias);
        }
        
        @Override
        public void moveDot(final int n, final Position.Bias bias) {
            DefaultCaret.this.handleMoveDot(n, bias);
        }
    }
}
