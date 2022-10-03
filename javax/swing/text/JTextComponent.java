package javax.swing.text;

import java.awt.Graphics;
import java.io.Serializable;
import java.awt.event.FocusEvent;
import javax.swing.event.ChangeEvent;
import java.util.Vector;
import java.util.Enumeration;
import java.awt.im.InputContext;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import javax.swing.plaf.UIResource;
import javax.accessibility.AccessibleTextSequence;
import java.text.CharacterIterator;
import java.text.BreakIterator;
import java.awt.Shape;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.swing.event.DocumentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;
import java.awt.IllegalComponentStateException;
import javax.accessibility.AccessibleExtendedText;
import javax.accessibility.AccessibleEditableText;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleText;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.swing.SwingAccessor;
import java.awt.font.TextHitInfo;
import javax.swing.KeyStroke;
import java.text.AttributedString;
import java.text.AttributedCharacterIterator;
import java.awt.event.KeyEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.InputMethodEvent;
import java.awt.AWTEvent;
import java.awt.event.InputEvent;
import java.io.ObjectInputStream;
import javax.accessibility.AccessibleContext;
import sun.swing.text.TextComponentPrintable;
import java.awt.print.Printable;
import java.awt.print.PrinterAbortException;
import java.util.concurrent.ExecutionException;
import sun.swing.SwingUtilities2;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Callable;
import javax.print.attribute.HashPrintRequestAttributeSet;
import sun.swing.PrintingStatus;
import java.awt.print.PrinterJob;
import java.awt.print.PrinterException;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.PrintService;
import java.text.MessageFormat;
import java.awt.Container;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.io.Writer;
import java.io.IOException;
import java.io.Reader;
import java.awt.event.ActionEvent;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.Component;
import java.util.Hashtable;
import sun.awt.AppContext;
import java.util.HashMap;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.TransferHandler;
import java.awt.Point;
import java.awt.HeadlessException;
import java.awt.GraphicsEnvironment;
import javax.swing.event.ChangeListener;
import java.beans.Transient;
import javax.swing.Action;
import java.awt.ComponentOrientation;
import java.awt.font.TextAttribute;
import javax.swing.event.DocumentListener;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TextUI;
import java.awt.LayoutManager;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;
import java.awt.im.InputMethodRequests;
import com.sun.beans.util.Cache;
import javax.swing.DropMode;
import java.awt.Insets;
import java.awt.Color;
import javax.accessibility.Accessible;
import javax.swing.Scrollable;
import javax.swing.JComponent;

public abstract class JTextComponent extends JComponent implements Scrollable, Accessible
{
    public static final String FOCUS_ACCELERATOR_KEY = "focusAcceleratorKey";
    private Document model;
    private transient Caret caret;
    private NavigationFilter navigationFilter;
    private transient Highlighter highlighter;
    private transient Keymap keymap;
    private transient MutableCaretEvent caretEvent;
    private Color caretColor;
    private Color selectionColor;
    private Color selectedTextColor;
    private Color disabledTextColor;
    private boolean editable;
    private Insets margin;
    private char focusAccelerator;
    private boolean dragEnabled;
    private DropMode dropMode;
    private transient DropLocation dropLocation;
    private static DefaultTransferHandler defaultTransferHandler;
    private static Cache<Class<?>, Boolean> METHOD_OVERRIDDEN;
    private static final Object KEYMAP_TABLE;
    private transient InputMethodRequests inputMethodRequestsHandler;
    private SimpleAttributeSet composedTextAttribute;
    private String composedTextContent;
    private Position composedTextStart;
    private Position composedTextEnd;
    private Position latestCommittedTextStart;
    private Position latestCommittedTextEnd;
    private ComposedTextCaret composedTextCaret;
    private transient Caret originalCaret;
    private boolean checkedInputOverride;
    private boolean needToSendKeyTypedEvent;
    private static final Object FOCUSED_COMPONENT;
    public static final String DEFAULT_KEYMAP = "default";
    
    public JTextComponent() {
        this.dropMode = DropMode.USE_SELECTION;
        this.enableEvents(2056L);
        this.addMouseListener(this.caretEvent = new MutableCaretEvent(this));
        this.addFocusListener(this.caretEvent);
        this.setEditable(true);
        this.setDragEnabled(false);
        this.setLayout(null);
        this.updateUI();
    }
    
    public TextUI getUI() {
        return (TextUI)this.ui;
    }
    
    public void setUI(final TextUI ui) {
        super.setUI(ui);
    }
    
    @Override
    public void updateUI() {
        this.setUI((TextUI)UIManager.getUI(this));
        this.invalidate();
    }
    
    public void addCaretListener(final CaretListener caretListener) {
        this.listenerList.add(CaretListener.class, caretListener);
    }
    
    public void removeCaretListener(final CaretListener caretListener) {
        this.listenerList.remove(CaretListener.class, caretListener);
    }
    
    public CaretListener[] getCaretListeners() {
        return this.listenerList.getListeners(CaretListener.class);
    }
    
    protected void fireCaretUpdate(final CaretEvent caretEvent) {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == CaretListener.class) {
                ((CaretListener)listenerList[i + 1]).caretUpdate(caretEvent);
            }
        }
    }
    
    public void setDocument(final Document model) {
        final Document model2 = this.model;
        try {
            if (model2 instanceof AbstractDocument) {
                ((AbstractDocument)model2).readLock();
            }
            if (this.accessibleContext != null) {
                this.model.removeDocumentListener((DocumentListener)this.accessibleContext);
            }
            if (this.inputMethodRequestsHandler != null) {
                this.model.removeDocumentListener((DocumentListener)this.inputMethodRequestsHandler);
            }
            this.model = model;
            final Boolean b = this.getComponentOrientation().isLeftToRight() ? TextAttribute.RUN_DIRECTION_LTR : TextAttribute.RUN_DIRECTION_RTL;
            if (b != model.getProperty(TextAttribute.RUN_DIRECTION)) {
                model.putProperty(TextAttribute.RUN_DIRECTION, b);
            }
            this.firePropertyChange("document", model2, model);
        }
        finally {
            if (model2 instanceof AbstractDocument) {
                ((AbstractDocument)model2).readUnlock();
            }
        }
        this.revalidate();
        this.repaint();
        if (this.accessibleContext != null) {
            this.model.addDocumentListener((DocumentListener)this.accessibleContext);
        }
        if (this.inputMethodRequestsHandler != null) {
            this.model.addDocumentListener((DocumentListener)this.inputMethodRequestsHandler);
        }
    }
    
    public Document getDocument() {
        return this.model;
    }
    
    @Override
    public void setComponentOrientation(final ComponentOrientation componentOrientation) {
        final Document document = this.getDocument();
        if (document != null) {
            document.putProperty(TextAttribute.RUN_DIRECTION, componentOrientation.isLeftToRight() ? TextAttribute.RUN_DIRECTION_LTR : TextAttribute.RUN_DIRECTION_RTL);
        }
        super.setComponentOrientation(componentOrientation);
    }
    
    public Action[] getActions() {
        return this.getUI().getEditorKit(this).getActions();
    }
    
    public void setMargin(final Insets margin) {
        this.firePropertyChange("margin", this.margin, this.margin = margin);
        this.invalidate();
    }
    
    public Insets getMargin() {
        return this.margin;
    }
    
    public void setNavigationFilter(final NavigationFilter navigationFilter) {
        this.navigationFilter = navigationFilter;
    }
    
    public NavigationFilter getNavigationFilter() {
        return this.navigationFilter;
    }
    
    @Transient
    public Caret getCaret() {
        return this.caret;
    }
    
    public void setCaret(final Caret caret) {
        if (this.caret != null) {
            this.caret.removeChangeListener(this.caretEvent);
            this.caret.deinstall(this);
        }
        final Caret caret2 = this.caret;
        this.caret = caret;
        if (this.caret != null) {
            this.caret.install(this);
            this.caret.addChangeListener(this.caretEvent);
        }
        this.firePropertyChange("caret", caret2, this.caret);
    }
    
    public Highlighter getHighlighter() {
        return this.highlighter;
    }
    
    public void setHighlighter(final Highlighter highlighter) {
        if (this.highlighter != null) {
            this.highlighter.deinstall(this);
        }
        final Highlighter highlighter2 = this.highlighter;
        this.highlighter = highlighter;
        if (this.highlighter != null) {
            this.highlighter.install(this);
        }
        this.firePropertyChange("highlighter", highlighter2, highlighter);
    }
    
    public void setKeymap(final Keymap keymap) {
        final Keymap keymap2 = this.keymap;
        this.firePropertyChange("keymap", keymap2, this.keymap = keymap);
        this.updateInputMap(keymap2, keymap);
    }
    
    public void setDragEnabled(final boolean dragEnabled) {
        if (dragEnabled && GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException();
        }
        this.dragEnabled = dragEnabled;
    }
    
    public boolean getDragEnabled() {
        return this.dragEnabled;
    }
    
    public final void setDropMode(final DropMode dropMode) {
        if (dropMode != null) {
            switch (dropMode) {
                case USE_SELECTION:
                case INSERT: {
                    this.dropMode = dropMode;
                    return;
                }
            }
        }
        throw new IllegalArgumentException(dropMode + ": Unsupported drop mode for text");
    }
    
    public final DropMode getDropMode() {
        return this.dropMode;
    }
    
    @Override
    DropLocation dropLocationForPoint(final Point point) {
        final Position.Bias[] array = { null };
        final int viewToModel = this.getUI().viewToModel(this, point, array);
        if (array[0] == null) {
            array[0] = Position.Bias.Forward;
        }
        return new DropLocation(point, viewToModel, array[0]);
    }
    
    @Override
    Object setDropLocation(final TransferHandler.DropLocation dropLocation, final Object o, final boolean b) {
        Object value = null;
        final DropLocation dropLocation2 = (DropLocation)dropLocation;
        if (this.dropMode == DropMode.USE_SELECTION) {
            if (dropLocation2 == null) {
                if (o != null) {
                    final Object[] array = (Object[])o;
                    if (!b) {
                        if (this.caret instanceof DefaultCaret) {
                            ((DefaultCaret)this.caret).setDot((int)array[0], (Position.Bias)array[3]);
                            ((DefaultCaret)this.caret).moveDot((int)array[1], (Position.Bias)array[4]);
                        }
                        else {
                            this.caret.setDot((int)array[0]);
                            this.caret.moveDot((int)array[1]);
                        }
                    }
                    this.caret.setVisible((boolean)array[2]);
                }
            }
            else {
                if (this.dropLocation == null) {
                    if (this.caret instanceof DefaultCaret) {
                        final DefaultCaret defaultCaret = (DefaultCaret)this.caret;
                        value = new Object[] { defaultCaret.getMark(), defaultCaret.getDot(), defaultCaret.isActive(), defaultCaret.getMarkBias(), defaultCaret.getDotBias() };
                    }
                    else {
                        value = new Object[] { this.caret.getMark(), this.caret.getDot(), this.caret.isVisible() };
                    }
                    this.caret.setVisible(true);
                }
                else {
                    value = o;
                }
                if (this.caret instanceof DefaultCaret) {
                    ((DefaultCaret)this.caret).setDot(dropLocation2.getIndex(), dropLocation2.getBias());
                }
                else {
                    this.caret.setDot(dropLocation2.getIndex());
                }
            }
        }
        else if (dropLocation2 == null) {
            if (o != null) {
                this.caret.setVisible((boolean)o);
            }
        }
        else if (this.dropLocation == null) {
            value = ((this.caret instanceof DefaultCaret) ? ((DefaultCaret)this.caret).isActive() : this.caret.isVisible());
            this.caret.setVisible(false);
        }
        else {
            value = o;
        }
        this.firePropertyChange("dropLocation", this.dropLocation, this.dropLocation = dropLocation2);
        return value;
    }
    
    public final DropLocation getDropLocation() {
        return this.dropLocation;
    }
    
    void updateInputMap(final Keymap keymap, final Keymap keymap2) {
        InputMap inputMap;
        InputMap parent;
        for (parent = (inputMap = this.getInputMap(0)); parent != null && !(parent instanceof KeymapWrapper); parent = parent.getParent()) {
            inputMap = parent;
        }
        if (parent != null) {
            if (keymap2 == null) {
                if (inputMap != parent) {
                    inputMap.setParent(parent.getParent());
                }
                else {
                    inputMap.setParent(null);
                }
            }
            else {
                final KeymapWrapper parent2 = new KeymapWrapper(keymap2);
                inputMap.setParent(parent2);
                if (inputMap != parent) {
                    parent2.setParent(parent.getParent());
                }
            }
        }
        else if (keymap2 != null) {
            final InputMap inputMap2 = this.getInputMap(0);
            if (inputMap2 != null) {
                final KeymapWrapper parent3 = new KeymapWrapper(keymap2);
                parent3.setParent(inputMap2.getParent());
                inputMap2.setParent(parent3);
            }
        }
        ActionMap actionMap;
        ActionMap parent4;
        for (parent4 = (actionMap = this.getActionMap()); parent4 != null && !(parent4 instanceof KeymapActionMap); parent4 = parent4.getParent()) {
            actionMap = parent4;
        }
        if (parent4 != null) {
            if (keymap2 == null) {
                if (actionMap != parent4) {
                    actionMap.setParent(parent4.getParent());
                }
                else {
                    actionMap.setParent(null);
                }
            }
            else {
                final KeymapActionMap parent5 = new KeymapActionMap(keymap2);
                actionMap.setParent(parent5);
                if (actionMap != parent4) {
                    parent5.setParent(parent4.getParent());
                }
            }
        }
        else if (keymap2 != null) {
            final ActionMap actionMap2 = this.getActionMap();
            if (actionMap2 != null) {
                final KeymapActionMap parent6 = new KeymapActionMap(keymap2);
                parent6.setParent(actionMap2.getParent());
                actionMap2.setParent(parent6);
            }
        }
    }
    
    public Keymap getKeymap() {
        return this.keymap;
    }
    
    public static Keymap addKeymap(final String s, final Keymap keymap) {
        final DefaultKeymap defaultKeymap = new DefaultKeymap(s, keymap);
        if (s != null) {
            getKeymapTable().put(s, defaultKeymap);
        }
        return defaultKeymap;
    }
    
    public static Keymap removeKeymap(final String s) {
        return getKeymapTable().remove(s);
    }
    
    public static Keymap getKeymap(final String s) {
        return getKeymapTable().get(s);
    }
    
    private static HashMap<String, Keymap> getKeymapTable() {
        synchronized (JTextComponent.KEYMAP_TABLE) {
            final AppContext appContext = AppContext.getAppContext();
            HashMap hashMap = (HashMap)appContext.get(JTextComponent.KEYMAP_TABLE);
            if (hashMap == null) {
                hashMap = new HashMap(17);
                appContext.put(JTextComponent.KEYMAP_TABLE, hashMap);
                addKeymap("default", null).setDefaultAction(new DefaultEditorKit.DefaultKeyTypedAction());
            }
            return hashMap;
        }
    }
    
    public static void loadKeymap(final Keymap keymap, final KeyBinding[] array, final Action[] array2) {
        final Hashtable hashtable = new Hashtable();
        for (final Action action : array2) {
            final String s = (String)action.getValue("Name");
            hashtable.put((s != null) ? s : "", action);
        }
        for (final KeyBinding keyBinding : array) {
            final Action action2 = hashtable.get(keyBinding.actionName);
            if (action2 != null) {
                keymap.addActionForKeyStroke(keyBinding.key, action2);
            }
        }
    }
    
    public Color getCaretColor() {
        return this.caretColor;
    }
    
    public void setCaretColor(final Color caretColor) {
        this.firePropertyChange("caretColor", this.caretColor, this.caretColor = caretColor);
    }
    
    public Color getSelectionColor() {
        return this.selectionColor;
    }
    
    public void setSelectionColor(final Color selectionColor) {
        this.firePropertyChange("selectionColor", this.selectionColor, this.selectionColor = selectionColor);
    }
    
    public Color getSelectedTextColor() {
        return this.selectedTextColor;
    }
    
    public void setSelectedTextColor(final Color selectedTextColor) {
        this.firePropertyChange("selectedTextColor", this.selectedTextColor, this.selectedTextColor = selectedTextColor);
    }
    
    public Color getDisabledTextColor() {
        return this.disabledTextColor;
    }
    
    public void setDisabledTextColor(final Color disabledTextColor) {
        this.firePropertyChange("disabledTextColor", this.disabledTextColor, this.disabledTextColor = disabledTextColor);
    }
    
    public void replaceSelection(final String s) {
        final Document document = this.getDocument();
        if (document != null) {
            try {
                final boolean saveComposedText = this.saveComposedText(this.caret.getDot());
                final int min = Math.min(this.caret.getDot(), this.caret.getMark());
                final int max = Math.max(this.caret.getDot(), this.caret.getMark());
                if (document instanceof AbstractDocument) {
                    ((AbstractDocument)document).replace(min, max - min, s, null);
                }
                else {
                    if (min != max) {
                        document.remove(min, max - min);
                    }
                    if (s != null && s.length() > 0) {
                        document.insertString(min, s, null);
                    }
                }
                if (saveComposedText) {
                    this.restoreComposedText();
                }
            }
            catch (final BadLocationException ex) {
                UIManager.getLookAndFeel().provideErrorFeedback(this);
            }
        }
    }
    
    public String getText(final int n, final int n2) throws BadLocationException {
        return this.getDocument().getText(n, n2);
    }
    
    public Rectangle modelToView(final int n) throws BadLocationException {
        return this.getUI().modelToView(this, n);
    }
    
    public int viewToModel(final Point point) {
        return this.getUI().viewToModel(this, point);
    }
    
    public void cut() {
        if (this.isEditable() && this.isEnabled()) {
            this.invokeAction("cut", TransferHandler.getCutAction());
        }
    }
    
    public void copy() {
        this.invokeAction("copy", TransferHandler.getCopyAction());
    }
    
    public void paste() {
        if (this.isEditable() && this.isEnabled()) {
            this.invokeAction("paste", TransferHandler.getPasteAction());
        }
    }
    
    private void invokeAction(final String s, final Action action) {
        final ActionMap actionMap = this.getActionMap();
        Action value = null;
        if (actionMap != null) {
            value = actionMap.get(s);
        }
        if (value == null) {
            this.installDefaultTransferHandlerIfNecessary();
            value = action;
        }
        value.actionPerformed(new ActionEvent(this, 1001, (String)value.getValue("Name"), EventQueue.getMostRecentEventTime(), this.getCurrentEventModifiers()));
    }
    
    private void installDefaultTransferHandlerIfNecessary() {
        if (this.getTransferHandler() == null) {
            if (JTextComponent.defaultTransferHandler == null) {
                JTextComponent.defaultTransferHandler = new DefaultTransferHandler();
            }
            this.setTransferHandler(JTextComponent.defaultTransferHandler);
        }
    }
    
    public void moveCaretPosition(final int n) {
        final Document document = this.getDocument();
        if (document != null) {
            if (n > document.getLength() || n < 0) {
                throw new IllegalArgumentException("bad position: " + n);
            }
            this.caret.moveDot(n);
        }
    }
    
    public void setFocusAccelerator(final char c) {
        final char upperCase = Character.toUpperCase(c);
        final char focusAccelerator = this.focusAccelerator;
        this.firePropertyChange("focusAcceleratorKey", focusAccelerator, this.focusAccelerator = upperCase);
        this.firePropertyChange("focusAccelerator", focusAccelerator, this.focusAccelerator);
    }
    
    public char getFocusAccelerator() {
        return this.focusAccelerator;
    }
    
    public void read(final Reader reader, final Object o) throws IOException {
        final EditorKit editorKit = this.getUI().getEditorKit(this);
        final Document defaultDocument = editorKit.createDefaultDocument();
        if (o != null) {
            defaultDocument.putProperty("stream", o);
        }
        try {
            editorKit.read(reader, defaultDocument, 0);
            this.setDocument(defaultDocument);
        }
        catch (final BadLocationException ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    public void write(final Writer writer) throws IOException {
        final Document document = this.getDocument();
        try {
            this.getUI().getEditorKit(this).write(writer, document, 0, document.getLength());
        }
        catch (final BadLocationException ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    @Override
    public void removeNotify() {
        super.removeNotify();
        if (getFocusedComponent() == this) {
            AppContext.getAppContext().remove(JTextComponent.FOCUSED_COMPONENT);
        }
    }
    
    public void setCaretPosition(final int dot) {
        final Document document = this.getDocument();
        if (document != null) {
            if (dot > document.getLength() || dot < 0) {
                throw new IllegalArgumentException("bad position: " + dot);
            }
            this.caret.setDot(dot);
        }
    }
    
    @Transient
    public int getCaretPosition() {
        return this.caret.getDot();
    }
    
    public void setText(final String s) {
        try {
            final Document document = this.getDocument();
            if (document instanceof AbstractDocument) {
                ((AbstractDocument)document).replace(0, document.getLength(), s, null);
            }
            else {
                document.remove(0, document.getLength());
                document.insertString(0, s, null);
            }
        }
        catch (final BadLocationException ex) {
            UIManager.getLookAndFeel().provideErrorFeedback(this);
        }
    }
    
    public String getText() {
        final Document document = this.getDocument();
        String text;
        try {
            text = document.getText(0, document.getLength());
        }
        catch (final BadLocationException ex) {
            text = null;
        }
        return text;
    }
    
    public String getSelectedText() {
        String text = null;
        final int min = Math.min(this.caret.getDot(), this.caret.getMark());
        final int max = Math.max(this.caret.getDot(), this.caret.getMark());
        if (min != max) {
            try {
                text = this.getDocument().getText(min, max - min);
            }
            catch (final BadLocationException ex) {
                throw new IllegalArgumentException(ex.getMessage());
            }
        }
        return text;
    }
    
    public boolean isEditable() {
        return this.editable;
    }
    
    public void setEditable(final boolean editable) {
        if (editable != this.editable) {
            final boolean editable2 = this.editable;
            this.enableInputMethods(this.editable = editable);
            this.firePropertyChange("editable", editable2, (Object)this.editable);
            this.repaint();
        }
    }
    
    @Transient
    public int getSelectionStart() {
        return Math.min(this.caret.getDot(), this.caret.getMark());
    }
    
    public void setSelectionStart(final int n) {
        this.select(n, this.getSelectionEnd());
    }
    
    @Transient
    public int getSelectionEnd() {
        return Math.max(this.caret.getDot(), this.caret.getMark());
    }
    
    public void setSelectionEnd(final int n) {
        this.select(this.getSelectionStart(), n);
    }
    
    public void select(int caretPosition, int n) {
        final int length = this.getDocument().getLength();
        if (caretPosition < 0) {
            caretPosition = 0;
        }
        if (caretPosition > length) {
            caretPosition = length;
        }
        if (n > length) {
            n = length;
        }
        if (n < caretPosition) {
            n = caretPosition;
        }
        this.setCaretPosition(caretPosition);
        this.moveCaretPosition(n);
    }
    
    public void selectAll() {
        final Document document = this.getDocument();
        if (document != null) {
            this.setCaretPosition(0);
            this.moveCaretPosition(document.getLength());
        }
    }
    
    @Override
    public String getToolTipText(final MouseEvent mouseEvent) {
        String s = super.getToolTipText(mouseEvent);
        if (s == null) {
            final TextUI ui = this.getUI();
            if (ui != null) {
                s = ui.getToolTipText(this, new Point(mouseEvent.getX(), mouseEvent.getY()));
            }
        }
        return s;
    }
    
    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return this.getPreferredSize();
    }
    
    @Override
    public int getScrollableUnitIncrement(final Rectangle rectangle, final int n, final int n2) {
        switch (n) {
            case 1: {
                return rectangle.height / 10;
            }
            case 0: {
                return rectangle.width / 10;
            }
            default: {
                throw new IllegalArgumentException("Invalid orientation: " + n);
            }
        }
    }
    
    @Override
    public int getScrollableBlockIncrement(final Rectangle rectangle, final int n, final int n2) {
        switch (n) {
            case 1: {
                return rectangle.height;
            }
            case 0: {
                return rectangle.width;
            }
            default: {
                throw new IllegalArgumentException("Invalid orientation: " + n);
            }
        }
    }
    
    @Override
    public boolean getScrollableTracksViewportWidth() {
        final Container unwrappedParent = SwingUtilities.getUnwrappedParent(this);
        return unwrappedParent instanceof JViewport && unwrappedParent.getWidth() > this.getPreferredSize().width;
    }
    
    @Override
    public boolean getScrollableTracksViewportHeight() {
        final Container unwrappedParent = SwingUtilities.getUnwrappedParent(this);
        return unwrappedParent instanceof JViewport && unwrappedParent.getHeight() > this.getPreferredSize().height;
    }
    
    public boolean print() throws PrinterException {
        return this.print(null, null, true, null, null, true);
    }
    
    public boolean print(final MessageFormat messageFormat, final MessageFormat messageFormat2) throws PrinterException {
        return this.print(messageFormat, messageFormat2, true, null, null, true);
    }
    
    public boolean print(final MessageFormat messageFormat, final MessageFormat messageFormat2, final boolean b, final PrintService printService, final PrintRequestAttributeSet set, final boolean b2) throws PrinterException {
        final PrinterJob printerJob = PrinterJob.getPrinterJob();
        final boolean headless = GraphicsEnvironment.isHeadless();
        final boolean eventDispatchThread = SwingUtilities.isEventDispatchThread();
        final Printable printable = this.getPrintable(messageFormat, messageFormat2);
        PrintingStatus printingStatus;
        Printable notificationPrintable;
        if (b2 && !headless) {
            printingStatus = PrintingStatus.createPrintingStatus(this, printerJob);
            notificationPrintable = printingStatus.createNotificationPrintable(printable);
        }
        else {
            printingStatus = null;
            notificationPrintable = printable;
        }
        if (printService != null) {
            printerJob.setPrintService(printService);
        }
        printerJob.setPrintable(notificationPrintable);
        final PrintRequestAttributeSet set2 = (set == null) ? new HashPrintRequestAttributeSet() : set;
        if (b && !headless && !printerJob.printDialog(set2)) {
            return false;
        }
        final FutureTask futureTask = new FutureTask(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                try {
                    printerJob.print(set2);
                }
                finally {
                    if (printingStatus != null) {
                        printingStatus.dispose();
                    }
                }
                return null;
            }
        });
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                boolean booleanValue = false;
                if (eventDispatchThread) {
                    if (JTextComponent.this.isEnabled()) {
                        booleanValue = true;
                        JTextComponent.this.setEnabled(false);
                    }
                }
                else {
                    try {
                        booleanValue = SwingUtilities2.submit((Callable<Boolean>)new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                final boolean enabled = JTextComponent.this.isEnabled();
                                if (enabled) {
                                    JTextComponent.this.setEnabled(false);
                                }
                                return enabled;
                            }
                        }).get();
                    }
                    catch (final InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    catch (final ExecutionException ex2) {
                        final Throwable cause = ex2.getCause();
                        if (cause instanceof Error) {
                            throw (Error)cause;
                        }
                        if (cause instanceof RuntimeException) {
                            throw (RuntimeException)cause;
                        }
                        throw new AssertionError((Object)cause);
                    }
                }
                JTextComponent.this.getDocument().render(futureTask);
                if (booleanValue) {
                    if (eventDispatchThread) {
                        JTextComponent.this.setEnabled(true);
                    }
                    else {
                        try {
                            SwingUtilities2.submit(new Runnable() {
                                @Override
                                public void run() {
                                    JTextComponent.this.setEnabled(true);
                                }
                            }, (Object)null).get();
                        }
                        catch (final InterruptedException ex3) {
                            throw new RuntimeException(ex3);
                        }
                        catch (final ExecutionException ex4) {
                            final Throwable cause2 = ex4.getCause();
                            if (cause2 instanceof Error) {
                                throw (Error)cause2;
                            }
                            if (cause2 instanceof RuntimeException) {
                                throw (RuntimeException)cause2;
                            }
                            throw new AssertionError((Object)cause2);
                        }
                    }
                }
            }
        };
        if (!b2 || headless) {
            runnable.run();
        }
        else if (eventDispatchThread) {
            new Thread(runnable).start();
            printingStatus.showModal(true);
        }
        else {
            printingStatus.showModal(false);
            runnable.run();
        }
        try {
            futureTask.get();
        }
        catch (final InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        catch (final ExecutionException ex2) {
            final Throwable cause = ex2.getCause();
            if (cause instanceof PrinterAbortException) {
                if (printingStatus != null && printingStatus.isAborted()) {
                    return false;
                }
                throw (PrinterAbortException)cause;
            }
            else {
                if (cause instanceof PrinterException) {
                    throw (PrinterException)cause;
                }
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException)cause;
                }
                if (cause instanceof Error) {
                    throw (Error)cause;
                }
                throw new AssertionError((Object)cause);
            }
        }
        return true;
    }
    
    public Printable getPrintable(final MessageFormat messageFormat, final MessageFormat messageFormat2) {
        return TextComponentPrintable.getPrintable(this, messageFormat, messageFormat2);
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJTextComponent();
        }
        return this.accessibleContext;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.addMouseListener(this.caretEvent = new MutableCaretEvent(this));
        this.addFocusListener(this.caretEvent);
    }
    
    @Override
    protected String paramString() {
        return super.paramString() + ",caretColor=" + ((this.caretColor != null) ? this.caretColor.toString() : "") + ",disabledTextColor=" + ((this.disabledTextColor != null) ? this.disabledTextColor.toString() : "") + ",editable=" + (this.editable ? "true" : "false") + ",margin=" + ((this.margin != null) ? this.margin.toString() : "") + ",selectedTextColor=" + ((this.selectedTextColor != null) ? this.selectedTextColor.toString() : "") + ",selectionColor=" + ((this.selectionColor != null) ? this.selectionColor.toString() : "");
    }
    
    static final JTextComponent getFocusedComponent() {
        return (JTextComponent)AppContext.getAppContext().get(JTextComponent.FOCUSED_COMPONENT);
    }
    
    private int getCurrentEventModifiers() {
        int n = 0;
        final AWTEvent currentEvent = EventQueue.getCurrentEvent();
        if (currentEvent instanceof InputEvent) {
            n = ((InputEvent)currentEvent).getModifiers();
        }
        else if (currentEvent instanceof ActionEvent) {
            n = ((ActionEvent)currentEvent).getModifiers();
        }
        return n;
    }
    
    @Override
    protected void processInputMethodEvent(final InputMethodEvent inputMethodCaretPosition) {
        super.processInputMethodEvent(inputMethodCaretPosition);
        if (!inputMethodCaretPosition.isConsumed()) {
            if (!this.isEditable()) {
                return;
            }
            switch (inputMethodCaretPosition.getID()) {
                case 1100: {
                    this.replaceInputMethodText(inputMethodCaretPosition);
                }
                case 1101: {
                    this.setInputMethodCaretPosition(inputMethodCaretPosition);
                    break;
                }
            }
            inputMethodCaretPosition.consume();
        }
    }
    
    @Override
    public InputMethodRequests getInputMethodRequests() {
        if (this.inputMethodRequestsHandler == null) {
            this.inputMethodRequestsHandler = new InputMethodRequestsHandler();
            final Document document = this.getDocument();
            if (document != null) {
                document.addDocumentListener((DocumentListener)this.inputMethodRequestsHandler);
            }
        }
        return this.inputMethodRequestsHandler;
    }
    
    @Override
    public void addInputMethodListener(final InputMethodListener inputMethodListener) {
        super.addInputMethodListener(inputMethodListener);
        if (inputMethodListener != null) {
            this.needToSendKeyTypedEvent = false;
            this.checkedInputOverride = true;
        }
    }
    
    private void replaceInputMethodText(final InputMethodEvent inputMethodEvent) {
        int i = inputMethodEvent.getCommittedCharacterCount();
        final AttributedCharacterIterator text = inputMethodEvent.getText();
        final Document document = this.getDocument();
        if (this.composedTextExists()) {
            try {
                document.remove(this.composedTextStart.getOffset(), this.composedTextEnd.getOffset() - this.composedTextStart.getOffset());
            }
            catch (final BadLocationException ex) {}
            final Position position = null;
            this.composedTextEnd = position;
            this.composedTextStart = position;
            this.composedTextAttribute = null;
            this.composedTextContent = null;
        }
        if (text != null) {
            text.first();
            int dot = 0;
            int dot2 = 0;
            if (i > 0) {
                dot = this.caret.getDot();
                if (this.shouldSynthensizeKeyEvents()) {
                    char c = text.current();
                    while (i > 0) {
                        this.processKeyEvent(new KeyEvent(this, 400, EventQueue.getMostRecentEventTime(), 0, 0, c));
                        c = text.next();
                        --i;
                    }
                }
                else {
                    final StringBuilder sb = new StringBuilder();
                    char c2 = text.current();
                    while (i > 0) {
                        sb.append(c2);
                        c2 = text.next();
                        --i;
                    }
                    this.mapCommittedTextToAction(sb.toString());
                }
                dot2 = this.caret.getDot();
            }
            final int index = text.getIndex();
            if (index < text.getEndIndex()) {
                this.createComposedTextAttribute(index, text);
                try {
                    this.replaceSelection(null);
                    document.insertString(this.caret.getDot(), this.composedTextContent, this.composedTextAttribute);
                    this.composedTextStart = document.createPosition(this.caret.getDot() - this.composedTextContent.length());
                    this.composedTextEnd = document.createPosition(this.caret.getDot());
                }
                catch (final BadLocationException ex2) {
                    final Position position2 = null;
                    this.composedTextEnd = position2;
                    this.composedTextStart = position2;
                    this.composedTextAttribute = null;
                    this.composedTextContent = null;
                }
            }
            if (dot != dot2) {
                try {
                    this.latestCommittedTextStart = document.createPosition(dot);
                    this.latestCommittedTextEnd = document.createPosition(dot2);
                }
                catch (final BadLocationException ex3) {
                    final Position position3 = null;
                    this.latestCommittedTextEnd = position3;
                    this.latestCommittedTextStart = position3;
                }
            }
            else {
                final Position position4 = null;
                this.latestCommittedTextEnd = position4;
                this.latestCommittedTextStart = position4;
            }
        }
    }
    
    private void createComposedTextAttribute(final int index, final AttributedCharacterIterator attributedCharacterIterator) {
        this.getDocument();
        final StringBuilder sb = new StringBuilder();
        for (char c = attributedCharacterIterator.setIndex(index); c != '\uffff'; c = attributedCharacterIterator.next()) {
            sb.append(c);
        }
        this.composedTextContent = sb.toString();
        (this.composedTextAttribute = new SimpleAttributeSet()).addAttribute(StyleConstants.ComposedTextAttribute, new AttributedString(attributedCharacterIterator, index, attributedCharacterIterator.getEndIndex()));
    }
    
    protected boolean saveComposedText(final int n) {
        if (this.composedTextExists()) {
            final int offset = this.composedTextStart.getOffset();
            final int n2 = this.composedTextEnd.getOffset() - this.composedTextStart.getOffset();
            if (n >= offset && n <= offset + n2) {
                try {
                    this.getDocument().remove(offset, n2);
                    return true;
                }
                catch (final BadLocationException ex) {}
            }
        }
        return false;
    }
    
    protected void restoreComposedText() {
        final Document document = this.getDocument();
        try {
            document.insertString(this.caret.getDot(), this.composedTextContent, this.composedTextAttribute);
            this.composedTextStart = document.createPosition(this.caret.getDot() - this.composedTextContent.length());
            this.composedTextEnd = document.createPosition(this.caret.getDot());
        }
        catch (final BadLocationException ex) {}
    }
    
    private void mapCommittedTextToAction(final String s) {
        final Keymap keymap = this.getKeymap();
        if (keymap != null) {
            Action action = null;
            if (s.length() == 1) {
                action = keymap.getAction(KeyStroke.getKeyStroke(s.charAt(0)));
            }
            if (action == null) {
                action = keymap.getDefaultAction();
            }
            if (action != null) {
                action.actionPerformed(new ActionEvent(this, 1001, s, EventQueue.getMostRecentEventTime(), this.getCurrentEventModifiers()));
            }
        }
    }
    
    private void setInputMethodCaretPosition(final InputMethodEvent inputMethodEvent) {
        if (this.composedTextExists()) {
            int offset = this.composedTextStart.getOffset();
            if (!(this.caret instanceof ComposedTextCaret)) {
                if (this.composedTextCaret == null) {
                    this.composedTextCaret = new ComposedTextCaret();
                }
                this.exchangeCaret(this.originalCaret = this.caret, this.composedTextCaret);
            }
            final TextHitInfo caret = inputMethodEvent.getCaret();
            if (caret != null) {
                final int insertionIndex = caret.getInsertionIndex();
                offset += insertionIndex;
                if (insertionIndex == 0) {
                    try {
                        final Rectangle modelToView = this.modelToView(offset);
                        final Rectangle modelToView2 = this.modelToView(this.composedTextEnd.getOffset());
                        final Rectangle bounds = this.getBounds();
                        final Rectangle rectangle = modelToView;
                        rectangle.x += Math.min(modelToView2.x - modelToView.x, bounds.width);
                        this.scrollRectToVisible(modelToView);
                    }
                    catch (final BadLocationException ex) {}
                }
            }
            this.caret.setDot(offset);
        }
        else if (this.caret instanceof ComposedTextCaret) {
            final int dot = this.caret.getDot();
            this.exchangeCaret(this.caret, this.originalCaret);
            this.caret.setDot(dot);
        }
    }
    
    private void exchangeCaret(final Caret caret, final Caret caret2) {
        final int blinkRate = caret.getBlinkRate();
        this.setCaret(caret2);
        this.caret.setBlinkRate(blinkRate);
        this.caret.setVisible(this.hasFocus());
    }
    
    private boolean shouldSynthensizeKeyEvents() {
        if (!this.checkedInputOverride) {
            this.needToSendKeyTypedEvent = !JTextComponent.METHOD_OVERRIDDEN.get(this.getClass());
            this.checkedInputOverride = true;
        }
        return this.needToSendKeyTypedEvent;
    }
    
    boolean composedTextExists() {
        return this.composedTextStart != null;
    }
    
    static {
        SwingAccessor.setJTextComponentAccessor(new SwingAccessor.JTextComponentAccessor() {
            @Override
            public TransferHandler.DropLocation dropLocationForPoint(final JTextComponent textComponent, final Point point) {
                return textComponent.dropLocationForPoint(point);
            }
            
            @Override
            public Object setDropLocation(final JTextComponent textComponent, final TransferHandler.DropLocation dropLocation, final Object o, final boolean b) {
                return textComponent.setDropLocation(dropLocation, o, b);
            }
        });
        JTextComponent.METHOD_OVERRIDDEN = new Cache<Class<?>, Boolean>(Cache.Kind.WEAK, Cache.Kind.STRONG) {
            @Override
            public Boolean create(final Class<?> clazz) {
                if (JTextComponent.class == clazz) {
                    return Boolean.FALSE;
                }
                if (((Cache<Class<? super JTextComponent>, Boolean>)this).get(clazz.getSuperclass())) {
                    return Boolean.TRUE;
                }
                return AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
                    @Override
                    public Boolean run() {
                        try {
                            clazz.getDeclaredMethod("processInputMethodEvent", InputMethodEvent.class);
                            return Boolean.TRUE;
                        }
                        catch (final NoSuchMethodException ex) {
                            return Boolean.FALSE;
                        }
                    }
                });
            }
        };
        KEYMAP_TABLE = new StringBuilder("JTextComponent_KeymapTable");
        FOCUSED_COMPONENT = new StringBuilder("JTextComponent_FocusedComponent");
    }
    
    public static class KeyBinding
    {
        public KeyStroke key;
        public String actionName;
        
        public KeyBinding(final KeyStroke key, final String actionName) {
            this.key = key;
            this.actionName = actionName;
        }
    }
    
    public class AccessibleJTextComponent extends AccessibleJComponent implements AccessibleText, CaretListener, DocumentListener, AccessibleAction, AccessibleEditableText, AccessibleExtendedText
    {
        int caretPos;
        Point oldLocationOnScreen;
        
        public AccessibleJTextComponent() {
            final Document document = JTextComponent.this.getDocument();
            if (document != null) {
                document.addDocumentListener(this);
            }
            JTextComponent.this.addCaretListener(this);
            this.caretPos = this.getCaretPosition();
            try {
                this.oldLocationOnScreen = this.getLocationOnScreen();
            }
            catch (final IllegalComponentStateException ex) {}
            JTextComponent.this.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentMoved(final ComponentEvent componentEvent) {
                    try {
                        final Point locationOnScreen = AccessibleJTextComponent.this.getLocationOnScreen();
                        AccessibleJTextComponent.this.firePropertyChange("AccessibleVisibleData", AccessibleJTextComponent.this.oldLocationOnScreen, locationOnScreen);
                        AccessibleJTextComponent.this.oldLocationOnScreen = locationOnScreen;
                    }
                    catch (final IllegalComponentStateException ex) {}
                }
            });
        }
        
        @Override
        public void caretUpdate(final CaretEvent caretEvent) {
            final int dot = caretEvent.getDot();
            final int mark = caretEvent.getMark();
            if (this.caretPos != dot) {
                this.firePropertyChange("AccessibleCaret", new Integer(this.caretPos), new Integer(dot));
                this.caretPos = dot;
                try {
                    this.oldLocationOnScreen = this.getLocationOnScreen();
                }
                catch (final IllegalComponentStateException ex) {}
            }
            if (mark != dot) {
                this.firePropertyChange("AccessibleSelection", null, this.getSelectedText());
            }
        }
        
        @Override
        public void insertUpdate(final DocumentEvent documentEvent) {
            final Integer n = new Integer(documentEvent.getOffset());
            if (SwingUtilities.isEventDispatchThread()) {
                this.firePropertyChange("AccessibleText", null, n);
            }
            else {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        AccessibleJTextComponent.this.firePropertyChange("AccessibleText", null, n);
                    }
                });
            }
        }
        
        @Override
        public void removeUpdate(final DocumentEvent documentEvent) {
            final Integer n = new Integer(documentEvent.getOffset());
            if (SwingUtilities.isEventDispatchThread()) {
                this.firePropertyChange("AccessibleText", null, n);
            }
            else {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        AccessibleJTextComponent.this.firePropertyChange("AccessibleText", null, n);
                    }
                });
            }
        }
        
        @Override
        public void changedUpdate(final DocumentEvent documentEvent) {
            final Integer n = new Integer(documentEvent.getOffset());
            if (SwingUtilities.isEventDispatchThread()) {
                this.firePropertyChange("AccessibleText", null, n);
            }
            else {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        AccessibleJTextComponent.this.firePropertyChange("AccessibleText", null, n);
                    }
                });
            }
        }
        
        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            final AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
            if (JTextComponent.this.isEditable()) {
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
            if (point == null) {
                return -1;
            }
            return JTextComponent.this.viewToModel(point);
        }
        
        Rectangle getRootEditorRect() {
            final Rectangle bounds = JTextComponent.this.getBounds();
            if (bounds.width > 0 && bounds.height > 0) {
                final Rectangle rectangle = bounds;
                final Rectangle rectangle2 = bounds;
                final int n = 0;
                rectangle2.y = n;
                rectangle.x = n;
                final Insets insets = JTextComponent.this.getInsets();
                final Rectangle rectangle3 = bounds;
                rectangle3.x += insets.left;
                final Rectangle rectangle4 = bounds;
                rectangle4.y += insets.top;
                final Rectangle rectangle5 = bounds;
                rectangle5.width -= insets.left + insets.right;
                final Rectangle rectangle6 = bounds;
                rectangle6.height -= insets.top + insets.bottom;
                return bounds;
            }
            return null;
        }
        
        @Override
        public Rectangle getCharacterBounds(final int n) {
            if (n < 0 || n > JTextComponent.this.model.getLength() - 1) {
                return null;
            }
            final TextUI ui = JTextComponent.this.getUI();
            if (ui == null) {
                return null;
            }
            Rectangle rectangle = null;
            final Rectangle rootEditorRect = this.getRootEditorRect();
            if (rootEditorRect == null) {
                return null;
            }
            if (JTextComponent.this.model instanceof AbstractDocument) {
                ((AbstractDocument)JTextComponent.this.model).readLock();
            }
            try {
                final View rootView = ui.getRootView(JTextComponent.this);
                if (rootView != null) {
                    rootView.setSize((float)rootEditorRect.width, (float)rootEditorRect.height);
                    final Shape modelToView = rootView.modelToView(n, Position.Bias.Forward, n + 1, Position.Bias.Backward, rootEditorRect);
                    rectangle = (Rectangle)((modelToView instanceof Rectangle) ? modelToView : modelToView.getBounds());
                }
            }
            catch (final BadLocationException ex) {}
            finally {
                if (JTextComponent.this.model instanceof AbstractDocument) {
                    ((AbstractDocument)JTextComponent.this.model).readUnlock();
                }
            }
            return rectangle;
        }
        
        @Override
        public int getCharCount() {
            return JTextComponent.this.model.getLength();
        }
        
        @Override
        public int getCaretPosition() {
            return JTextComponent.this.getCaretPosition();
        }
        
        @Override
        public AttributeSet getCharacterAttribute(final int n) {
            Element element = null;
            if (JTextComponent.this.model instanceof AbstractDocument) {
                ((AbstractDocument)JTextComponent.this.model).readLock();
            }
            try {
                for (element = JTextComponent.this.model.getDefaultRootElement(); !element.isLeaf(); element = element.getElement(element.getElementIndex(n))) {}
            }
            finally {
                if (JTextComponent.this.model instanceof AbstractDocument) {
                    ((AbstractDocument)JTextComponent.this.model).readUnlock();
                }
            }
            return element.getAttributes();
        }
        
        @Override
        public int getSelectionStart() {
            return JTextComponent.this.getSelectionStart();
        }
        
        @Override
        public int getSelectionEnd() {
            return JTextComponent.this.getSelectionEnd();
        }
        
        @Override
        public String getSelectedText() {
            return JTextComponent.this.getSelectedText();
        }
        
        @Override
        public String getAtIndex(final int n, final int n2) {
            return this.getAtIndex(n, n2, 0);
        }
        
        @Override
        public String getAfterIndex(final int n, final int n2) {
            return this.getAtIndex(n, n2, 1);
        }
        
        @Override
        public String getBeforeIndex(final int n, final int n2) {
            return this.getAtIndex(n, n2, -1);
        }
        
        private String getAtIndex(final int n, final int n2, final int n3) {
            if (JTextComponent.this.model instanceof AbstractDocument) {
                ((AbstractDocument)JTextComponent.this.model).readLock();
            }
            try {
                if (n2 < 0 || n2 >= JTextComponent.this.model.getLength()) {
                    return null;
                }
                switch (n) {
                    case 1: {
                        if (n2 + n3 < JTextComponent.this.model.getLength() && n2 + n3 >= 0) {
                            return JTextComponent.this.model.getText(n2 + n3, 1);
                        }
                        break;
                    }
                    case 2:
                    case 3: {
                        IndexedSegment indexedSegment = this.getSegmentAt(n, n2);
                        if (indexedSegment == null) {
                            break;
                        }
                        if (n3 != 0) {
                            int n4;
                            if (n3 < 0) {
                                n4 = indexedSegment.modelOffset - 1;
                            }
                            else {
                                n4 = indexedSegment.modelOffset + n3 * indexedSegment.count;
                            }
                            if (n4 >= 0 && n4 <= JTextComponent.this.model.getLength()) {
                                indexedSegment = this.getSegmentAt(n, n4);
                            }
                            else {
                                indexedSegment = null;
                            }
                        }
                        if (indexedSegment != null) {
                            return new String(indexedSegment.array, indexedSegment.offset, indexedSegment.count);
                        }
                        break;
                    }
                }
            }
            catch (final BadLocationException ex) {}
            finally {
                if (JTextComponent.this.model instanceof AbstractDocument) {
                    ((AbstractDocument)JTextComponent.this.model).readUnlock();
                }
            }
            return null;
        }
        
        private Element getParagraphElement(final int n) {
            if (JTextComponent.this.model instanceof PlainDocument) {
                return ((PlainDocument)JTextComponent.this.model).getParagraphElement(n);
            }
            if (JTextComponent.this.model instanceof StyledDocument) {
                return ((StyledDocument)JTextComponent.this.model).getParagraphElement(n);
            }
            Element element;
            for (element = JTextComponent.this.model.getDefaultRootElement(); !element.isLeaf(); element = element.getElement(element.getElementIndex(n))) {}
            if (element == null) {
                return null;
            }
            return element.getParentElement();
        }
        
        private IndexedSegment getParagraphElementText(final int n) throws BadLocationException {
            final Element paragraphElement = this.getParagraphElement(n);
            if (paragraphElement != null) {
                final IndexedSegment indexedSegment = new IndexedSegment();
                try {
                    JTextComponent.this.model.getText(paragraphElement.getStartOffset(), paragraphElement.getEndOffset() - paragraphElement.getStartOffset(), indexedSegment);
                }
                catch (final BadLocationException ex) {
                    return null;
                }
                indexedSegment.modelOffset = paragraphElement.getStartOffset();
                return indexedSegment;
            }
            return null;
        }
        
        private IndexedSegment getSegmentAt(final int n, final int n2) throws BadLocationException {
            final IndexedSegment paragraphElementText = this.getParagraphElementText(n2);
            if (paragraphElementText == null) {
                return null;
            }
            BreakIterator breakIterator = null;
            switch (n) {
                case 2: {
                    breakIterator = BreakIterator.getWordInstance(this.getLocale());
                    break;
                }
                case 3: {
                    breakIterator = BreakIterator.getSentenceInstance(this.getLocale());
                    break;
                }
                default: {
                    return null;
                }
            }
            paragraphElementText.first();
            breakIterator.setText(paragraphElementText);
            final int following = breakIterator.following(n2 - paragraphElementText.modelOffset + paragraphElementText.offset);
            if (following == -1) {
                return null;
            }
            if (following > paragraphElementText.offset + paragraphElementText.count) {
                return null;
            }
            final int previous = breakIterator.previous();
            if (previous == -1 || previous >= paragraphElementText.offset + paragraphElementText.count) {
                return null;
            }
            paragraphElementText.modelOffset = paragraphElementText.modelOffset + previous - paragraphElementText.offset;
            paragraphElementText.offset = previous;
            paragraphElementText.count = following - previous;
            return paragraphElementText;
        }
        
        @Override
        public AccessibleEditableText getAccessibleEditableText() {
            return this;
        }
        
        @Override
        public void setTextContents(final String text) {
            JTextComponent.this.setText(text);
        }
        
        @Override
        public void insertTextAtIndex(final int n, final String s) {
            final Document document = JTextComponent.this.getDocument();
            if (document != null) {
                try {
                    if (s != null && s.length() > 0) {
                        final boolean saveComposedText = JTextComponent.this.saveComposedText(n);
                        document.insertString(n, s, null);
                        if (saveComposedText) {
                            JTextComponent.this.restoreComposedText();
                        }
                    }
                }
                catch (final BadLocationException ex) {
                    UIManager.getLookAndFeel().provideErrorFeedback(JTextComponent.this);
                }
            }
        }
        
        @Override
        public String getTextRange(final int n, final int n2) {
            String text = null;
            final int min = Math.min(n, n2);
            final int max = Math.max(n, n2);
            if (min != max) {
                try {
                    text = JTextComponent.this.getDocument().getText(min, max - min);
                }
                catch (final BadLocationException ex) {
                    throw new IllegalArgumentException(ex.getMessage());
                }
            }
            return text;
        }
        
        @Override
        public void delete(final int n, final int n2) {
            if (JTextComponent.this.isEditable() && this.isEnabled()) {
                try {
                    final int min = Math.min(n, n2);
                    final int max = Math.max(n, n2);
                    if (min != max) {
                        JTextComponent.this.getDocument().remove(min, max - min);
                    }
                }
                catch (final BadLocationException ex) {}
            }
            else {
                UIManager.getLookAndFeel().provideErrorFeedback(JTextComponent.this);
            }
        }
        
        @Override
        public void cut(final int n, final int n2) {
            this.selectText(n, n2);
            JTextComponent.this.cut();
        }
        
        @Override
        public void paste(final int caretPosition) {
            JTextComponent.this.setCaretPosition(caretPosition);
            JTextComponent.this.paste();
        }
        
        @Override
        public void replaceText(final int n, final int n2, final String s) {
            this.selectText(n, n2);
            JTextComponent.this.replaceSelection(s);
        }
        
        @Override
        public void selectText(final int n, final int n2) {
            JTextComponent.this.select(n, n2);
        }
        
        @Override
        public void setAttributes(final int n, final int n2, final AttributeSet set) {
            final Document document = JTextComponent.this.getDocument();
            if (document != null && document instanceof StyledDocument) {
                ((StyledDocument)document).setCharacterAttributes(n, n2 - n, set, true);
            }
        }
        
        private AccessibleTextSequence getSequenceAtIndex(final int n, final int n2, final int n3) {
            if (n2 < 0 || n2 >= JTextComponent.this.model.getLength()) {
                return null;
            }
            if (n3 < -1 || n3 > 1) {
                return null;
            }
            switch (n) {
                case 1: {
                    if (JTextComponent.this.model instanceof AbstractDocument) {
                        ((AbstractDocument)JTextComponent.this.model).readLock();
                    }
                    AccessibleTextSequence accessibleTextSequence = null;
                    try {
                        if (n2 + n3 < JTextComponent.this.model.getLength() && n2 + n3 >= 0) {
                            accessibleTextSequence = new AccessibleTextSequence(n2 + n3, n2 + n3 + 1, JTextComponent.this.model.getText(n2 + n3, 1));
                        }
                    }
                    catch (final BadLocationException ex) {}
                    finally {
                        if (JTextComponent.this.model instanceof AbstractDocument) {
                            ((AbstractDocument)JTextComponent.this.model).readUnlock();
                        }
                    }
                    return accessibleTextSequence;
                }
                case 2:
                case 3: {
                    if (JTextComponent.this.model instanceof AbstractDocument) {
                        ((AbstractDocument)JTextComponent.this.model).readLock();
                    }
                    AccessibleTextSequence accessibleTextSequence2 = null;
                    try {
                        IndexedSegment indexedSegment = this.getSegmentAt(n, n2);
                        if (indexedSegment != null) {
                            if (n3 != 0) {
                                int n4;
                                if (n3 < 0) {
                                    n4 = indexedSegment.modelOffset - 1;
                                }
                                else {
                                    n4 = indexedSegment.modelOffset + indexedSegment.count;
                                }
                                if (n4 >= 0 && n4 <= JTextComponent.this.model.getLength()) {
                                    indexedSegment = this.getSegmentAt(n, n4);
                                }
                                else {
                                    indexedSegment = null;
                                }
                            }
                            if (indexedSegment != null && indexedSegment.offset + indexedSegment.count <= JTextComponent.this.model.getLength()) {
                                accessibleTextSequence2 = new AccessibleTextSequence(indexedSegment.offset, indexedSegment.offset + indexedSegment.count, new String(indexedSegment.array, indexedSegment.offset, indexedSegment.count));
                            }
                        }
                    }
                    catch (final BadLocationException ex2) {}
                    finally {
                        if (JTextComponent.this.model instanceof AbstractDocument) {
                            ((AbstractDocument)JTextComponent.this.model).readUnlock();
                        }
                    }
                    return accessibleTextSequence2;
                }
                case 4: {
                    AccessibleTextSequence accessibleTextSequence3 = null;
                    if (JTextComponent.this.model instanceof AbstractDocument) {
                        ((AbstractDocument)JTextComponent.this.model).readLock();
                    }
                    try {
                        final int rowStart = Utilities.getRowStart(JTextComponent.this, n2);
                        final int rowEnd = Utilities.getRowEnd(JTextComponent.this, n2);
                        if (rowStart >= 0 && rowEnd >= rowStart) {
                            if (n3 == 0) {
                                accessibleTextSequence3 = new AccessibleTextSequence(rowStart, rowEnd, JTextComponent.this.model.getText(rowStart, rowEnd - rowStart + 1));
                            }
                            else if (n3 == -1 && rowStart > 0) {
                                final int rowEnd2 = Utilities.getRowEnd(JTextComponent.this, rowStart - 1);
                                final int rowStart2 = Utilities.getRowStart(JTextComponent.this, rowStart - 1);
                                if (rowStart2 >= 0 && rowEnd2 >= rowStart2) {
                                    accessibleTextSequence3 = new AccessibleTextSequence(rowStart2, rowEnd2, JTextComponent.this.model.getText(rowStart2, rowEnd2 - rowStart2 + 1));
                                }
                            }
                            else if (n3 == 1 && rowEnd < JTextComponent.this.model.getLength()) {
                                final int rowStart3 = Utilities.getRowStart(JTextComponent.this, rowEnd + 1);
                                final int rowEnd3 = Utilities.getRowEnd(JTextComponent.this, rowEnd + 1);
                                if (rowStart3 >= 0 && rowEnd3 >= rowStart3) {
                                    accessibleTextSequence3 = new AccessibleTextSequence(rowStart3, rowEnd3, JTextComponent.this.model.getText(rowStart3, rowEnd3 - rowStart3 + 1));
                                }
                            }
                        }
                    }
                    catch (final BadLocationException ex3) {}
                    finally {
                        if (JTextComponent.this.model instanceof AbstractDocument) {
                            ((AbstractDocument)JTextComponent.this.model).readUnlock();
                        }
                    }
                    return accessibleTextSequence3;
                }
                case 5: {
                    String text = null;
                    if (JTextComponent.this.model instanceof AbstractDocument) {
                        ((AbstractDocument)JTextComponent.this.model).readLock();
                    }
                    int n5;
                    int runEdge;
                    try {
                        runEdge = (n5 = Integer.MIN_VALUE);
                        int runEdge2 = n2;
                        switch (n3) {
                            case -1: {
                                runEdge = this.getRunEdge(n2, n3);
                                runEdge2 = runEdge - 1;
                                break;
                            }
                            case 1: {
                                n5 = (runEdge2 = this.getRunEdge(n2, n3));
                                break;
                            }
                            case 0: {
                                break;
                            }
                            default: {
                                throw new AssertionError(n3);
                            }
                        }
                        n5 = ((n5 != Integer.MIN_VALUE) ? n5 : this.getRunEdge(runEdge2, -1));
                        runEdge = ((runEdge != Integer.MIN_VALUE) ? runEdge : this.getRunEdge(runEdge2, 1));
                        text = JTextComponent.this.model.getText(n5, runEdge - n5);
                    }
                    catch (final BadLocationException ex4) {
                        return null;
                    }
                    finally {
                        if (JTextComponent.this.model instanceof AbstractDocument) {
                            ((AbstractDocument)JTextComponent.this.model).readUnlock();
                        }
                    }
                    return new AccessibleTextSequence(n5, runEdge, text);
                }
                default: {
                    return null;
                }
            }
        }
        
        private int getRunEdge(final int n, final int n2) throws BadLocationException {
            if (n < 0 || n >= JTextComponent.this.model.getLength()) {
                throw new BadLocationException("Location out of bounds", n);
            }
            int elementIndex;
            Element element;
            for (elementIndex = -1, element = JTextComponent.this.model.getDefaultRootElement(); !element.isLeaf(); element = element.getElement(elementIndex)) {
                elementIndex = element.getElementIndex(n);
            }
            if (elementIndex == -1) {
                throw new AssertionError(n);
            }
            final AttributeSet attributes = element.getAttributes();
            final Element parentElement = element.getParentElement();
            switch (n2) {
                case -1:
                case 1: {
                    int n3 = elementIndex;
                    for (int elementCount = parentElement.getElementCount(); n3 + n2 > 0 && n3 + n2 < elementCount && parentElement.getElement(n3 + n2).getAttributes().isEqual(attributes); n3 += n2) {}
                    final Element element2 = parentElement.getElement(n3);
                    switch (n2) {
                        case -1: {
                            return element2.getStartOffset();
                        }
                        case 1: {
                            return element2.getEndOffset();
                        }
                        default: {
                            return Integer.MIN_VALUE;
                        }
                    }
                    break;
                }
                default: {
                    throw new AssertionError(n2);
                }
            }
        }
        
        @Override
        public AccessibleTextSequence getTextSequenceAt(final int n, final int n2) {
            return this.getSequenceAtIndex(n, n2, 0);
        }
        
        @Override
        public AccessibleTextSequence getTextSequenceAfter(final int n, final int n2) {
            return this.getSequenceAtIndex(n, n2, 1);
        }
        
        @Override
        public AccessibleTextSequence getTextSequenceBefore(final int n, final int n2) {
            return this.getSequenceAtIndex(n, n2, -1);
        }
        
        @Override
        public Rectangle getTextBounds(final int n, final int n2) {
            if (n < 0 || n > JTextComponent.this.model.getLength() - 1 || n2 < 0 || n2 > JTextComponent.this.model.getLength() - 1 || n > n2) {
                return null;
            }
            final TextUI ui = JTextComponent.this.getUI();
            if (ui == null) {
                return null;
            }
            Rectangle rectangle = null;
            final Rectangle rootEditorRect = this.getRootEditorRect();
            if (rootEditorRect == null) {
                return null;
            }
            if (JTextComponent.this.model instanceof AbstractDocument) {
                ((AbstractDocument)JTextComponent.this.model).readLock();
            }
            try {
                final View rootView = ui.getRootView(JTextComponent.this);
                if (rootView != null) {
                    final Shape modelToView = rootView.modelToView(n, Position.Bias.Forward, n2, Position.Bias.Backward, rootEditorRect);
                    rectangle = (Rectangle)((modelToView instanceof Rectangle) ? modelToView : modelToView.getBounds());
                }
            }
            catch (final BadLocationException ex) {}
            finally {
                if (JTextComponent.this.model instanceof AbstractDocument) {
                    ((AbstractDocument)JTextComponent.this.model).readUnlock();
                }
            }
            return rectangle;
        }
        
        @Override
        public AccessibleAction getAccessibleAction() {
            return this;
        }
        
        @Override
        public int getAccessibleActionCount() {
            return JTextComponent.this.getActions().length;
        }
        
        @Override
        public String getAccessibleActionDescription(final int n) {
            final Action[] actions = JTextComponent.this.getActions();
            if (n < 0 || n >= actions.length) {
                return null;
            }
            return (String)actions[n].getValue("Name");
        }
        
        @Override
        public boolean doAccessibleAction(final int n) {
            final Action[] actions = JTextComponent.this.getActions();
            if (n < 0 || n >= actions.length) {
                return false;
            }
            actions[n].actionPerformed(new ActionEvent(JTextComponent.this, 1001, null, EventQueue.getMostRecentEventTime(), JTextComponent.this.getCurrentEventModifiers()));
            return true;
        }
        
        private class IndexedSegment extends Segment
        {
            public int modelOffset;
        }
    }
    
    public static final class DropLocation extends TransferHandler.DropLocation
    {
        private final int index;
        private final Position.Bias bias;
        
        private DropLocation(final Point point, final int index, final Position.Bias bias) {
            super(point);
            this.index = index;
            this.bias = bias;
        }
        
        public int getIndex() {
            return this.index;
        }
        
        public Position.Bias getBias() {
            return this.bias;
        }
        
        @Override
        public String toString() {
            return this.getClass().getName() + "[dropPoint=" + this.getDropPoint() + ",index=" + this.index + ",bias=" + this.bias + "]";
        }
    }
    
    static class DefaultTransferHandler extends TransferHandler implements UIResource
    {
        @Override
        public void exportToClipboard(final JComponent component, final Clipboard clipboard, final int n) throws IllegalStateException {
            if (component instanceof JTextComponent) {
                final JTextComponent textComponent = (JTextComponent)component;
                final int selectionStart = textComponent.getSelectionStart();
                final int selectionEnd = textComponent.getSelectionEnd();
                if (selectionStart != selectionEnd) {
                    try {
                        final Document document = textComponent.getDocument();
                        clipboard.setContents(new StringSelection(document.getText(selectionStart, selectionEnd - selectionStart)), null);
                        if (n == 2) {
                            document.remove(selectionStart, selectionEnd - selectionStart);
                        }
                    }
                    catch (final BadLocationException ex) {}
                }
            }
        }
        
        @Override
        public boolean importData(final JComponent component, final Transferable transferable) {
            if (component instanceof JTextComponent) {
                final DataFlavor flavor = this.getFlavor(transferable.getTransferDataFlavors());
                if (flavor != null) {
                    final InputContext inputContext = component.getInputContext();
                    if (inputContext != null) {
                        inputContext.endComposition();
                    }
                    try {
                        ((JTextComponent)component).replaceSelection((String)transferable.getTransferData(flavor));
                        return true;
                    }
                    catch (final UnsupportedFlavorException ex) {}
                    catch (final IOException ex2) {}
                }
            }
            return false;
        }
        
        @Override
        public boolean canImport(final JComponent component, final DataFlavor[] array) {
            final JTextComponent textComponent = (JTextComponent)component;
            return textComponent.isEditable() && textComponent.isEnabled() && this.getFlavor(array) != null;
        }
        
        @Override
        public int getSourceActions(final JComponent component) {
            return 0;
        }
        
        private DataFlavor getFlavor(final DataFlavor[] array) {
            if (array != null) {
                for (final DataFlavor dataFlavor : array) {
                    if (dataFlavor.equals(DataFlavor.stringFlavor)) {
                        return dataFlavor;
                    }
                }
            }
            return null;
        }
    }
    
    static class DefaultKeymap implements Keymap
    {
        String nm;
        Keymap parent;
        Hashtable<KeyStroke, Action> bindings;
        Action defaultAction;
        
        DefaultKeymap(final String nm, final Keymap parent) {
            this.nm = nm;
            this.parent = parent;
            this.bindings = new Hashtable<KeyStroke, Action>();
        }
        
        @Override
        public Action getDefaultAction() {
            if (this.defaultAction != null) {
                return this.defaultAction;
            }
            return (this.parent != null) ? this.parent.getDefaultAction() : null;
        }
        
        @Override
        public void setDefaultAction(final Action defaultAction) {
            this.defaultAction = defaultAction;
        }
        
        @Override
        public String getName() {
            return this.nm;
        }
        
        @Override
        public Action getAction(final KeyStroke keyStroke) {
            Action action = this.bindings.get(keyStroke);
            if (action == null && this.parent != null) {
                action = this.parent.getAction(keyStroke);
            }
            return action;
        }
        
        @Override
        public KeyStroke[] getBoundKeyStrokes() {
            final KeyStroke[] array = new KeyStroke[this.bindings.size()];
            int n = 0;
            final Enumeration<KeyStroke> keys = this.bindings.keys();
            while (keys.hasMoreElements()) {
                array[n++] = keys.nextElement();
            }
            return array;
        }
        
        @Override
        public Action[] getBoundActions() {
            final Action[] array = new Action[this.bindings.size()];
            int n = 0;
            final Enumeration<Action> elements = this.bindings.elements();
            while (elements.hasMoreElements()) {
                array[n++] = elements.nextElement();
            }
            return array;
        }
        
        @Override
        public KeyStroke[] getKeyStrokesForAction(final Action action) {
            if (action == null) {
                return null;
            }
            Object[] array = null;
            Vector<KeyStroke> vector = null;
            final Enumeration<KeyStroke> keys = this.bindings.keys();
            while (keys.hasMoreElements()) {
                final KeyStroke keyStroke = keys.nextElement();
                if (this.bindings.get(keyStroke) == action) {
                    if (vector == null) {
                        vector = new Vector<KeyStroke>();
                    }
                    vector.addElement(keyStroke);
                }
            }
            if (this.parent != null) {
                final KeyStroke[] keyStrokesForAction = this.parent.getKeyStrokesForAction(action);
                if (keyStrokesForAction != null) {
                    int n = 0;
                    for (int i = keyStrokesForAction.length - 1; i >= 0; --i) {
                        if (this.isLocallyDefined(keyStrokesForAction[i])) {
                            keyStrokesForAction[i] = null;
                            ++n;
                        }
                    }
                    if (n > 0 && n < keyStrokesForAction.length) {
                        if (vector == null) {
                            vector = new Vector<KeyStroke>();
                        }
                        for (int j = keyStrokesForAction.length - 1; j >= 0; --j) {
                            if (keyStrokesForAction[j] != null) {
                                vector.addElement(keyStrokesForAction[j]);
                            }
                        }
                    }
                    else if (n == 0) {
                        if (vector == null) {
                            array = keyStrokesForAction;
                        }
                        else {
                            array = new KeyStroke[vector.size() + keyStrokesForAction.length];
                            vector.copyInto(array);
                            System.arraycopy(keyStrokesForAction, 0, array, vector.size(), keyStrokesForAction.length);
                            vector = null;
                        }
                    }
                }
            }
            if (vector != null) {
                array = new KeyStroke[vector.size()];
                vector.copyInto(array);
            }
            return (KeyStroke[])array;
        }
        
        @Override
        public boolean isLocallyDefined(final KeyStroke keyStroke) {
            return this.bindings.containsKey(keyStroke);
        }
        
        @Override
        public void addActionForKeyStroke(final KeyStroke keyStroke, final Action action) {
            this.bindings.put(keyStroke, action);
        }
        
        @Override
        public void removeKeyStrokeBinding(final KeyStroke keyStroke) {
            this.bindings.remove(keyStroke);
        }
        
        @Override
        public void removeBindings() {
            this.bindings.clear();
        }
        
        @Override
        public Keymap getResolveParent() {
            return this.parent;
        }
        
        @Override
        public void setResolveParent(final Keymap parent) {
            this.parent = parent;
        }
        
        @Override
        public String toString() {
            return "Keymap[" + this.nm + "]" + this.bindings;
        }
    }
    
    static class KeymapWrapper extends InputMap
    {
        static final Object DefaultActionKey;
        private Keymap keymap;
        
        KeymapWrapper(final Keymap keymap) {
            this.keymap = keymap;
        }
        
        @Override
        public KeyStroke[] keys() {
            final KeyStroke[] keys = super.keys();
            final KeyStroke[] boundKeyStrokes = this.keymap.getBoundKeyStrokes();
            final int n = (keys == null) ? 0 : keys.length;
            final int n2 = (boundKeyStrokes == null) ? 0 : boundKeyStrokes.length;
            if (n == 0) {
                return boundKeyStrokes;
            }
            if (n2 == 0) {
                return keys;
            }
            final KeyStroke[] array = new KeyStroke[n + n2];
            System.arraycopy(keys, 0, array, 0, n);
            System.arraycopy(boundKeyStrokes, 0, array, n, n2);
            return array;
        }
        
        @Override
        public int size() {
            final KeyStroke[] boundKeyStrokes = this.keymap.getBoundKeyStrokes();
            return super.size() + ((boundKeyStrokes == null) ? 0 : boundKeyStrokes.length);
        }
        
        @Override
        public Object get(final KeyStroke keyStroke) {
            Object o = this.keymap.getAction(keyStroke);
            if (o == null) {
                o = super.get(keyStroke);
                if (o == null && keyStroke.getKeyChar() != '\uffff' && this.keymap.getDefaultAction() != null) {
                    o = KeymapWrapper.DefaultActionKey;
                }
            }
            return o;
        }
        
        static {
            DefaultActionKey = new Object();
        }
    }
    
    static class KeymapActionMap extends ActionMap
    {
        private Keymap keymap;
        
        KeymapActionMap(final Keymap keymap) {
            this.keymap = keymap;
        }
        
        @Override
        public Object[] keys() {
            final Object[] keys = super.keys();
            final Action[] boundActions = this.keymap.getBoundActions();
            final int n = (keys == null) ? 0 : keys.length;
            int n2 = (boundActions == null) ? 0 : boundActions.length;
            final boolean b = this.keymap.getDefaultAction() != null;
            if (b) {
                ++n2;
            }
            if (n == 0) {
                if (b) {
                    final Object[] array = new Object[n2];
                    if (n2 > 1) {
                        System.arraycopy(boundActions, 0, array, 0, n2 - 1);
                    }
                    array[n2 - 1] = KeymapWrapper.DefaultActionKey;
                    return array;
                }
                return boundActions;
            }
            else {
                if (n2 == 0) {
                    return keys;
                }
                final Object[] array2 = new Object[n + n2];
                System.arraycopy(keys, 0, array2, 0, n);
                if (b) {
                    if (n2 > 1) {
                        System.arraycopy(boundActions, 0, array2, n, n2 - 1);
                    }
                    array2[n + n2 - 1] = KeymapWrapper.DefaultActionKey;
                }
                else {
                    System.arraycopy(boundActions, 0, array2, n, n2);
                }
                return array2;
            }
        }
        
        @Override
        public int size() {
            final Action[] boundActions = this.keymap.getBoundActions();
            int n = (boundActions == null) ? 0 : boundActions.length;
            if (this.keymap.getDefaultAction() != null) {
                ++n;
            }
            return super.size() + n;
        }
        
        @Override
        public Action get(final Object o) {
            Action action = super.get(o);
            if (action == null) {
                if (o == KeymapWrapper.DefaultActionKey) {
                    action = this.keymap.getDefaultAction();
                }
                else if (o instanceof Action) {
                    action = (Action)o;
                }
            }
            return action;
        }
    }
    
    static class MutableCaretEvent extends CaretEvent implements ChangeListener, FocusListener, MouseListener
    {
        private boolean dragActive;
        private int dot;
        private int mark;
        
        MutableCaretEvent(final JTextComponent textComponent) {
            super(textComponent);
        }
        
        final void fire() {
            final JTextComponent textComponent = (JTextComponent)this.getSource();
            if (textComponent != null) {
                final Caret caret = textComponent.getCaret();
                this.dot = caret.getDot();
                this.mark = caret.getMark();
                textComponent.fireCaretUpdate(this);
            }
        }
        
        @Override
        public final String toString() {
            return "dot=" + this.dot + ",mark=" + this.mark;
        }
        
        @Override
        public final int getDot() {
            return this.dot;
        }
        
        @Override
        public final int getMark() {
            return this.mark;
        }
        
        @Override
        public final void stateChanged(final ChangeEvent changeEvent) {
            if (!this.dragActive) {
                this.fire();
            }
        }
        
        @Override
        public void focusGained(final FocusEvent focusEvent) {
            AppContext.getAppContext().put(JTextComponent.FOCUSED_COMPONENT, focusEvent.getSource());
        }
        
        @Override
        public void focusLost(final FocusEvent focusEvent) {
        }
        
        @Override
        public final void mousePressed(final MouseEvent mouseEvent) {
            this.dragActive = true;
        }
        
        @Override
        public final void mouseReleased(final MouseEvent mouseEvent) {
            this.dragActive = false;
            this.fire();
        }
        
        @Override
        public final void mouseClicked(final MouseEvent mouseEvent) {
        }
        
        @Override
        public final void mouseEntered(final MouseEvent mouseEvent) {
        }
        
        @Override
        public final void mouseExited(final MouseEvent mouseEvent) {
        }
    }
    
    class InputMethodRequestsHandler implements InputMethodRequests, DocumentListener
    {
        @Override
        public AttributedCharacterIterator cancelLatestCommittedText(final AttributedCharacterIterator.Attribute[] array) {
            final Document document = JTextComponent.this.getDocument();
            if (document != null && JTextComponent.this.latestCommittedTextStart != null && !JTextComponent.this.latestCommittedTextStart.equals(JTextComponent.this.latestCommittedTextEnd)) {
                try {
                    final int offset = JTextComponent.this.latestCommittedTextStart.getOffset();
                    final int offset2 = JTextComponent.this.latestCommittedTextEnd.getOffset();
                    final String text = document.getText(offset, offset2 - offset);
                    document.remove(offset, offset2 - offset);
                    return new AttributedString(text).getIterator();
                }
                catch (final BadLocationException ex) {}
            }
            return null;
        }
        
        @Override
        public AttributedCharacterIterator getCommittedText(final int n, final int n2, final AttributedCharacterIterator.Attribute[] array) {
            int offset = 0;
            int offset2 = 0;
            if (JTextComponent.this.composedTextExists()) {
                offset = JTextComponent.this.composedTextStart.getOffset();
                offset2 = JTextComponent.this.composedTextEnd.getOffset();
            }
            String s;
            try {
                if (n < offset) {
                    if (n2 <= offset) {
                        s = JTextComponent.this.getText(n, n2 - n);
                    }
                    else {
                        final int n3 = offset - n;
                        s = JTextComponent.this.getText(n, n3) + JTextComponent.this.getText(offset2, n2 - n - n3);
                    }
                }
                else {
                    s = JTextComponent.this.getText(n + (offset2 - offset), n2 - n);
                }
            }
            catch (final BadLocationException ex) {
                throw new IllegalArgumentException("Invalid range");
            }
            return new AttributedString(s).getIterator();
        }
        
        @Override
        public int getCommittedTextLength() {
            final Document document = JTextComponent.this.getDocument();
            int length = 0;
            if (document != null) {
                length = document.getLength();
                if (JTextComponent.this.composedTextContent != null) {
                    if (JTextComponent.this.composedTextEnd == null || JTextComponent.this.composedTextStart == null) {
                        length -= JTextComponent.this.composedTextContent.length();
                    }
                    else {
                        length -= JTextComponent.this.composedTextEnd.getOffset() - JTextComponent.this.composedTextStart.getOffset();
                    }
                }
            }
            return length;
        }
        
        @Override
        public int getInsertPositionOffset() {
            int offset = 0;
            int offset2 = 0;
            if (JTextComponent.this.composedTextExists()) {
                offset = JTextComponent.this.composedTextStart.getOffset();
                offset2 = JTextComponent.this.composedTextEnd.getOffset();
            }
            final int caretPosition = JTextComponent.this.getCaretPosition();
            if (caretPosition < offset) {
                return caretPosition;
            }
            if (caretPosition < offset2) {
                return offset;
            }
            return caretPosition - (offset2 - offset);
        }
        
        @Override
        public TextHitInfo getLocationOffset(final int n, final int n2) {
            if (JTextComponent.this.composedTextAttribute == null) {
                return null;
            }
            final Point locationOnScreen = JTextComponent.this.getLocationOnScreen();
            locationOnScreen.x = n - locationOnScreen.x;
            locationOnScreen.y = n2 - locationOnScreen.y;
            final int viewToModel = JTextComponent.this.viewToModel(locationOnScreen);
            if (viewToModel >= JTextComponent.this.composedTextStart.getOffset() && viewToModel <= JTextComponent.this.composedTextEnd.getOffset()) {
                return TextHitInfo.leading(viewToModel - JTextComponent.this.composedTextStart.getOffset());
            }
            return null;
        }
        
        @Override
        public Rectangle getTextLocation(final TextHitInfo textHitInfo) {
            Rectangle modelToView;
            try {
                modelToView = JTextComponent.this.modelToView(JTextComponent.this.getCaretPosition());
                if (modelToView != null) {
                    final Point locationOnScreen = JTextComponent.this.getLocationOnScreen();
                    modelToView.translate(locationOnScreen.x, locationOnScreen.y);
                }
            }
            catch (final BadLocationException ex) {
                modelToView = null;
            }
            if (modelToView == null) {
                modelToView = new Rectangle();
            }
            return modelToView;
        }
        
        @Override
        public AttributedCharacterIterator getSelectedText(final AttributedCharacterIterator.Attribute[] array) {
            final String selectedText = JTextComponent.this.getSelectedText();
            if (selectedText != null) {
                return new AttributedString(selectedText).getIterator();
            }
            return null;
        }
        
        @Override
        public void changedUpdate(final DocumentEvent documentEvent) {
            JTextComponent.this.latestCommittedTextStart = (JTextComponent.this.latestCommittedTextEnd = null);
        }
        
        @Override
        public void insertUpdate(final DocumentEvent documentEvent) {
            JTextComponent.this.latestCommittedTextStart = (JTextComponent.this.latestCommittedTextEnd = null);
        }
        
        @Override
        public void removeUpdate(final DocumentEvent documentEvent) {
            JTextComponent.this.latestCommittedTextStart = (JTextComponent.this.latestCommittedTextEnd = null);
        }
    }
    
    class ComposedTextCaret extends DefaultCaret implements Serializable
    {
        Color bg;
        
        @Override
        public void install(final JTextComponent textComponent) {
            super.install(textComponent);
            final Document document = textComponent.getDocument();
            if (document instanceof StyledDocument) {
                final StyledDocument styledDocument = (StyledDocument)document;
                this.bg = styledDocument.getBackground(styledDocument.getCharacterElement(textComponent.composedTextStart.getOffset()).getAttributes());
            }
            if (this.bg == null) {
                this.bg = textComponent.getBackground();
            }
        }
        
        @Override
        public void paint(final Graphics graphics) {
            if (this.isVisible()) {
                try {
                    final Rectangle modelToView = this.component.modelToView(this.getDot());
                    graphics.setXORMode(this.bg);
                    graphics.drawLine(modelToView.x, modelToView.y, modelToView.x, modelToView.y + modelToView.height - 1);
                    graphics.setPaintMode();
                }
                catch (final BadLocationException ex) {}
            }
        }
        
        @Override
        protected void positionCaret(final MouseEvent mouseEvent) {
            final JTextComponent component = this.component;
            final int viewToModel = component.viewToModel(new Point(mouseEvent.getX(), mouseEvent.getY()));
            if (viewToModel >= component.composedTextStart.getOffset()) {
                if (viewToModel <= JTextComponent.this.composedTextEnd.getOffset()) {
                    super.positionCaret(mouseEvent);
                    return;
                }
            }
            try {
                final Position position = component.getDocument().createPosition(viewToModel);
                component.getInputContext().endComposition();
                EventQueue.invokeLater(new DoSetCaretPosition(component, position));
            }
            catch (final BadLocationException ex) {
                System.err.println(ex);
            }
        }
    }
    
    private class DoSetCaretPosition implements Runnable
    {
        JTextComponent host;
        Position newPos;
        
        DoSetCaretPosition(final JTextComponent host, final Position newPos) {
            this.host = host;
            this.newPos = newPos;
        }
        
        @Override
        public void run() {
            this.host.setCaretPosition(this.newPos.getOffset());
        }
    }
}
