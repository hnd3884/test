package javax.swing.plaf.basic;

import java.io.StringBufferInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.Writer;
import java.io.StringWriter;
import java.awt.im.InputContext;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.datatransfer.Transferable;
import javax.swing.JPasswordField;
import java.io.IOException;
import java.io.Reader;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputAdapter;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import javax.swing.DropMode;
import java.awt.Component;
import java.util.Hashtable;
import java.awt.LayoutManager2;
import java.awt.Container;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.DefaultHighlighter;
import sun.awt.AppContext;
import javax.swing.text.Element;
import java.awt.Point;
import javax.swing.text.BadLocationException;
import java.awt.Dimension;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import java.awt.LayoutManager;
import javax.swing.event.DocumentListener;
import java.beans.PropertyChangeListener;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.text.View;
import java.awt.Graphics;
import javax.swing.text.TextAction;
import javax.swing.Action;
import javax.swing.plaf.ActionMapUIResource;
import java.awt.AWTKeyStroke;
import java.util.Set;
import javax.swing.LookAndFeel;
import java.util.Collection;
import java.util.HashSet;
import javax.swing.text.DefaultEditorKit;
import javax.swing.KeyStroke;
import javax.swing.plaf.ComponentInputMapUIResource;
import javax.swing.plaf.InputMapUIResource;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.SwingUtilities;
import java.awt.Cursor;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.Insets;
import javax.swing.border.Border;
import java.awt.Font;
import javax.swing.UIManager;
import javax.swing.JEditorPane;
import java.awt.Color;
import javax.swing.plaf.UIResource;
import javax.swing.JTextArea;
import javax.swing.plaf.synth.SynthUI;
import java.beans.PropertyChangeEvent;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import sun.swing.DefaultLookup;
import javax.swing.text.Keymap;
import javax.swing.text.Highlighter;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Position;
import javax.swing.TransferHandler;
import javax.swing.text.JTextComponent;
import javax.swing.text.EditorKit;
import javax.swing.text.ViewFactory;
import javax.swing.plaf.TextUI;

public abstract class BasicTextUI extends TextUI implements ViewFactory
{
    private static BasicCursor textCursor;
    private static final EditorKit defaultKit;
    transient JTextComponent editor;
    transient boolean painted;
    transient RootView rootView;
    transient UpdateHandler updateHandler;
    private static final TransferHandler defaultTransferHandler;
    private final DragListener dragListener;
    private static final Position.Bias[] discardBias;
    private DefaultCaret dropCaret;
    
    public BasicTextUI() {
        this.rootView = new RootView();
        this.updateHandler = new UpdateHandler();
        this.dragListener = getDragListener();
        this.painted = false;
    }
    
    protected Caret createCaret() {
        return new BasicCaret();
    }
    
    protected Highlighter createHighlighter() {
        return new BasicHighlighter();
    }
    
    protected String getKeymapName() {
        String s = this.getClass().getName();
        final int lastIndex = s.lastIndexOf(46);
        if (lastIndex >= 0) {
            s = s.substring(lastIndex + 1, s.length());
        }
        return s;
    }
    
    protected Keymap createKeymap() {
        final String keymapName = this.getKeymapName();
        Keymap keymap = JTextComponent.getKeymap(keymapName);
        if (keymap == null) {
            keymap = JTextComponent.addKeymap(keymapName, JTextComponent.getKeymap("default"));
            final Object value = DefaultLookup.get(this.editor, this, this.getPropertyPrefix() + ".keyBindings");
            if (value != null && value instanceof JTextComponent.KeyBinding[]) {
                JTextComponent.loadKeymap(keymap, (JTextComponent.KeyBinding[])value, this.getComponent().getActions());
            }
        }
        return keymap;
    }
    
    protected void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getPropertyName().equals("editable") || propertyChangeEvent.getPropertyName().equals("enabled")) {
            this.updateBackground((JTextComponent)propertyChangeEvent.getSource());
        }
    }
    
    private void updateBackground(final JTextComponent textComponent) {
        if (this instanceof SynthUI || textComponent instanceof JTextArea) {
            return;
        }
        final Color background = textComponent.getBackground();
        if (background instanceof UIResource) {
            final String propertyPrefix = this.getPropertyPrefix();
            final Color color = DefaultLookup.getColor(textComponent, this, propertyPrefix + ".disabledBackground", null);
            final Color color2 = DefaultLookup.getColor(textComponent, this, propertyPrefix + ".inactiveBackground", null);
            final Color color3 = DefaultLookup.getColor(textComponent, this, propertyPrefix + ".background", null);
            if ((textComponent instanceof JTextArea || textComponent instanceof JEditorPane) && background != color && background != color2 && background != color3) {
                return;
            }
            Color background2 = null;
            if (!textComponent.isEnabled()) {
                background2 = color;
            }
            if (background2 == null && !textComponent.isEditable()) {
                background2 = color2;
            }
            if (background2 == null) {
                background2 = color3;
            }
            if (background2 != null && background2 != background) {
                textComponent.setBackground(background2);
            }
        }
    }
    
    protected abstract String getPropertyPrefix();
    
    protected void installDefaults() {
        final String propertyPrefix = this.getPropertyPrefix();
        final Font font = this.editor.getFont();
        if (font == null || font instanceof UIResource) {
            this.editor.setFont(UIManager.getFont(propertyPrefix + ".font"));
        }
        final Color background = this.editor.getBackground();
        if (background == null || background instanceof UIResource) {
            this.editor.setBackground(UIManager.getColor(propertyPrefix + ".background"));
        }
        final Color foreground = this.editor.getForeground();
        if (foreground == null || foreground instanceof UIResource) {
            this.editor.setForeground(UIManager.getColor(propertyPrefix + ".foreground"));
        }
        final Color caretColor = this.editor.getCaretColor();
        if (caretColor == null || caretColor instanceof UIResource) {
            this.editor.setCaretColor(UIManager.getColor(propertyPrefix + ".caretForeground"));
        }
        final Color selectionColor = this.editor.getSelectionColor();
        if (selectionColor == null || selectionColor instanceof UIResource) {
            this.editor.setSelectionColor(UIManager.getColor(propertyPrefix + ".selectionBackground"));
        }
        final Color selectedTextColor = this.editor.getSelectedTextColor();
        if (selectedTextColor == null || selectedTextColor instanceof UIResource) {
            this.editor.setSelectedTextColor(UIManager.getColor(propertyPrefix + ".selectionForeground"));
        }
        final Color disabledTextColor = this.editor.getDisabledTextColor();
        if (disabledTextColor == null || disabledTextColor instanceof UIResource) {
            this.editor.setDisabledTextColor(UIManager.getColor(propertyPrefix + ".inactiveForeground"));
        }
        final Border border = this.editor.getBorder();
        if (border == null || border instanceof UIResource) {
            this.editor.setBorder(UIManager.getBorder(propertyPrefix + ".border"));
        }
        final Insets margin = this.editor.getMargin();
        if (margin == null || margin instanceof UIResource) {
            this.editor.setMargin(UIManager.getInsets(propertyPrefix + ".margin"));
        }
        this.updateCursor();
    }
    
    private void installDefaults2() {
        this.editor.addMouseListener(this.dragListener);
        this.editor.addMouseMotionListener(this.dragListener);
        final String propertyPrefix = this.getPropertyPrefix();
        final Caret caret = this.editor.getCaret();
        if (caret == null || caret instanceof UIResource) {
            final Caret caret2 = this.createCaret();
            this.editor.setCaret(caret2);
            caret2.setBlinkRate(DefaultLookup.getInt(this.getComponent(), this, propertyPrefix + ".caretBlinkRate", 500));
        }
        final Highlighter highlighter = this.editor.getHighlighter();
        if (highlighter == null || highlighter instanceof UIResource) {
            this.editor.setHighlighter(this.createHighlighter());
        }
        final TransferHandler transferHandler = this.editor.getTransferHandler();
        if (transferHandler == null || transferHandler instanceof UIResource) {
            this.editor.setTransferHandler(this.getTransferHandler());
        }
    }
    
    protected void uninstallDefaults() {
        this.editor.removeMouseListener(this.dragListener);
        this.editor.removeMouseMotionListener(this.dragListener);
        if (this.editor.getCaretColor() instanceof UIResource) {
            this.editor.setCaretColor(null);
        }
        if (this.editor.getSelectionColor() instanceof UIResource) {
            this.editor.setSelectionColor(null);
        }
        if (this.editor.getDisabledTextColor() instanceof UIResource) {
            this.editor.setDisabledTextColor(null);
        }
        if (this.editor.getSelectedTextColor() instanceof UIResource) {
            this.editor.setSelectedTextColor(null);
        }
        if (this.editor.getBorder() instanceof UIResource) {
            this.editor.setBorder(null);
        }
        if (this.editor.getMargin() instanceof UIResource) {
            this.editor.setMargin(null);
        }
        if (this.editor.getCaret() instanceof UIResource) {
            this.editor.setCaret(null);
        }
        if (this.editor.getHighlighter() instanceof UIResource) {
            this.editor.setHighlighter(null);
        }
        if (this.editor.getTransferHandler() instanceof UIResource) {
            this.editor.setTransferHandler(null);
        }
        if (this.editor.getCursor() instanceof UIResource) {
            this.editor.setCursor(null);
        }
    }
    
    protected void installListeners() {
    }
    
    protected void uninstallListeners() {
    }
    
    protected void installKeyboardActions() {
        this.editor.setKeymap(this.createKeymap());
        final InputMap inputMap = this.getInputMap();
        if (inputMap != null) {
            SwingUtilities.replaceUIInputMap(this.editor, 0, inputMap);
        }
        final ActionMap actionMap = this.getActionMap();
        if (actionMap != null) {
            SwingUtilities.replaceUIActionMap(this.editor, actionMap);
        }
        this.updateFocusAcceleratorBinding(false);
    }
    
    InputMap getInputMap() {
        final InputMapUIResource inputMapUIResource = new InputMapUIResource();
        final InputMap parent = (InputMap)DefaultLookup.get(this.editor, this, this.getPropertyPrefix() + ".focusInputMap");
        if (parent != null) {
            inputMapUIResource.setParent(parent);
        }
        return inputMapUIResource;
    }
    
    void updateFocusAcceleratorBinding(final boolean b) {
        final char focusAccelerator = this.editor.getFocusAccelerator();
        if (b || focusAccelerator != '\0') {
            InputMap uiInputMap = SwingUtilities.getUIInputMap(this.editor, 2);
            if (uiInputMap == null && focusAccelerator != '\0') {
                uiInputMap = new ComponentInputMapUIResource(this.editor);
                SwingUtilities.replaceUIInputMap(this.editor, 2, uiInputMap);
                SwingUtilities.replaceUIActionMap(this.editor, this.getActionMap());
            }
            if (uiInputMap != null) {
                uiInputMap.clear();
                if (focusAccelerator != '\0') {
                    uiInputMap.put(KeyStroke.getKeyStroke(focusAccelerator, BasicLookAndFeel.getFocusAcceleratorKeyMask()), "requestFocus");
                }
            }
        }
    }
    
    void updateFocusTraversalKeys() {
        final EditorKit editorKit = this.getEditorKit(this.editor);
        if (editorKit != null && editorKit instanceof DefaultEditorKit) {
            final Set<AWTKeyStroke> focusTraversalKeys = this.editor.getFocusTraversalKeys(0);
            final Set<AWTKeyStroke> focusTraversalKeys2 = this.editor.getFocusTraversalKeys(1);
            final HashSet set = new HashSet(focusTraversalKeys);
            final HashSet set2 = new HashSet(focusTraversalKeys2);
            if (this.editor.isEditable()) {
                set.remove(KeyStroke.getKeyStroke(9, 0));
                set2.remove(KeyStroke.getKeyStroke(9, 1));
            }
            else {
                set.add((Object)KeyStroke.getKeyStroke(9, 0));
                set2.add((Object)KeyStroke.getKeyStroke(9, 1));
            }
            LookAndFeel.installProperty(this.editor, "focusTraversalKeysForward", set);
            LookAndFeel.installProperty(this.editor, "focusTraversalKeysBackward", set2);
        }
    }
    
    private void updateCursor() {
        if (!this.editor.isCursorSet() || this.editor.getCursor() instanceof UIResource) {
            this.editor.setCursor(this.editor.isEditable() ? BasicTextUI.textCursor : null);
        }
    }
    
    TransferHandler getTransferHandler() {
        return BasicTextUI.defaultTransferHandler;
    }
    
    ActionMap getActionMap() {
        final String string = this.getPropertyPrefix() + ".actionMap";
        ActionMap actionMap = (ActionMap)UIManager.get(string);
        if (actionMap == null) {
            actionMap = this.createActionMap();
            if (actionMap != null) {
                UIManager.getLookAndFeelDefaults().put(string, actionMap);
            }
        }
        final ActionMapUIResource actionMapUIResource = new ActionMapUIResource();
        actionMapUIResource.put("requestFocus", new FocusAction());
        if (this.getEditorKit(this.editor) instanceof DefaultEditorKit && actionMap != null) {
            final Action value = actionMap.get("insert-break");
            if (value != null && value instanceof DefaultEditorKit.InsertBreakAction) {
                final TextActionWrapper textActionWrapper = new TextActionWrapper((TextAction)value);
                actionMapUIResource.put(textActionWrapper.getValue("Name"), textActionWrapper);
            }
        }
        if (actionMap != null) {
            actionMapUIResource.setParent(actionMap);
        }
        return actionMapUIResource;
    }
    
    ActionMap createActionMap() {
        final ActionMapUIResource actionMapUIResource = new ActionMapUIResource();
        for (final Action action : this.editor.getActions()) {
            actionMapUIResource.put(action.getValue("Name"), action);
        }
        actionMapUIResource.put(TransferHandler.getCutAction().getValue("Name"), TransferHandler.getCutAction());
        actionMapUIResource.put(TransferHandler.getCopyAction().getValue("Name"), TransferHandler.getCopyAction());
        actionMapUIResource.put(TransferHandler.getPasteAction().getValue("Name"), TransferHandler.getPasteAction());
        return actionMapUIResource;
    }
    
    protected void uninstallKeyboardActions() {
        this.editor.setKeymap(null);
        SwingUtilities.replaceUIInputMap(this.editor, 2, null);
        SwingUtilities.replaceUIActionMap(this.editor, null);
    }
    
    protected void paintBackground(final Graphics graphics) {
        graphics.setColor(this.editor.getBackground());
        graphics.fillRect(0, 0, this.editor.getWidth(), this.editor.getHeight());
    }
    
    protected final JTextComponent getComponent() {
        return this.editor;
    }
    
    protected void modelChanged() {
        this.setView(this.rootView.getViewFactory().create(this.editor.getDocument().getDefaultRootElement()));
    }
    
    protected final void setView(final View view) {
        this.rootView.setView(view);
        this.painted = false;
        this.editor.revalidate();
        this.editor.repaint();
    }
    
    protected void paintSafely(final Graphics graphics) {
        this.painted = true;
        final Highlighter highlighter = this.editor.getHighlighter();
        final Caret caret = this.editor.getCaret();
        if (this.editor.isOpaque()) {
            this.paintBackground(graphics);
        }
        if (highlighter != null) {
            highlighter.paint(graphics);
        }
        final Rectangle visibleEditorRect = this.getVisibleEditorRect();
        if (visibleEditorRect != null) {
            this.rootView.paint(graphics, visibleEditorRect);
        }
        if (caret != null) {
            caret.paint(graphics);
        }
        if (this.dropCaret != null) {
            this.dropCaret.paint(graphics);
        }
    }
    
    @Override
    public void installUI(final JComponent component) {
        if (component instanceof JTextComponent) {
            LookAndFeel.installProperty(this.editor = (JTextComponent)component, "opaque", Boolean.TRUE);
            LookAndFeel.installProperty(this.editor, "autoscrolls", Boolean.TRUE);
            this.installDefaults();
            this.installDefaults2();
            this.editor.addPropertyChangeListener(this.updateHandler);
            final Document document = this.editor.getDocument();
            if (document == null) {
                this.editor.setDocument(this.getEditorKit(this.editor).createDefaultDocument());
            }
            else {
                document.addDocumentListener(this.updateHandler);
                this.modelChanged();
            }
            this.installListeners();
            this.installKeyboardActions();
            final LayoutManager layout = this.editor.getLayout();
            if (layout == null || layout instanceof UIResource) {
                this.editor.setLayout(this.updateHandler);
            }
            this.updateBackground(this.editor);
            return;
        }
        throw new Error("TextUI needs JTextComponent");
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        this.editor.removePropertyChangeListener(this.updateHandler);
        this.editor.getDocument().removeDocumentListener(this.updateHandler);
        this.painted = false;
        this.uninstallDefaults();
        this.rootView.setView(null);
        component.removeAll();
        if (component.getLayout() instanceof UIResource) {
            component.setLayout(null);
        }
        this.uninstallKeyboardActions();
        this.uninstallListeners();
        this.editor = null;
    }
    
    @Override
    public void update(final Graphics graphics, final JComponent component) {
        this.paint(graphics, component);
    }
    
    @Override
    public final void paint(final Graphics graphics, final JComponent component) {
        if (this.rootView.getViewCount() > 0 && this.rootView.getView(0) != null) {
            final Document document = this.editor.getDocument();
            if (document instanceof AbstractDocument) {
                ((AbstractDocument)document).readLock();
            }
            try {
                this.paintSafely(graphics);
            }
            finally {
                if (document instanceof AbstractDocument) {
                    ((AbstractDocument)document).readUnlock();
                }
            }
        }
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        final Document document = this.editor.getDocument();
        final Insets insets = component.getInsets();
        final Dimension size = component.getSize();
        if (document instanceof AbstractDocument) {
            ((AbstractDocument)document).readLock();
        }
        try {
            if (size.width > insets.left + insets.right && size.height > insets.top + insets.bottom) {
                this.rootView.setSize((float)(size.width - insets.left - insets.right), (float)(size.height - insets.top - insets.bottom));
            }
            else if (size.width == 0 && size.height == 0) {
                this.rootView.setSize(2.14748365E9f, 2.14748365E9f);
            }
            size.width = (int)Math.min((long)this.rootView.getPreferredSpan(0) + insets.left + insets.right, 2147483647L);
            size.height = (int)Math.min((long)this.rootView.getPreferredSpan(1) + insets.top + insets.bottom, 2147483647L);
        }
        finally {
            if (document instanceof AbstractDocument) {
                ((AbstractDocument)document).readUnlock();
            }
        }
        return size;
    }
    
    @Override
    public Dimension getMinimumSize(final JComponent component) {
        final Document document = this.editor.getDocument();
        final Insets insets = component.getInsets();
        final Dimension dimension = new Dimension();
        if (document instanceof AbstractDocument) {
            ((AbstractDocument)document).readLock();
        }
        try {
            dimension.width = (int)this.rootView.getMinimumSpan(0) + insets.left + insets.right;
            dimension.height = (int)this.rootView.getMinimumSpan(1) + insets.top + insets.bottom;
        }
        finally {
            if (document instanceof AbstractDocument) {
                ((AbstractDocument)document).readUnlock();
            }
        }
        return dimension;
    }
    
    @Override
    public Dimension getMaximumSize(final JComponent component) {
        final Document document = this.editor.getDocument();
        final Insets insets = component.getInsets();
        final Dimension dimension = new Dimension();
        if (document instanceof AbstractDocument) {
            ((AbstractDocument)document).readLock();
        }
        try {
            dimension.width = (int)Math.min((long)this.rootView.getMaximumSpan(0) + insets.left + insets.right, 2147483647L);
            dimension.height = (int)Math.min((long)this.rootView.getMaximumSpan(1) + insets.top + insets.bottom, 2147483647L);
        }
        finally {
            if (document instanceof AbstractDocument) {
                ((AbstractDocument)document).readUnlock();
            }
        }
        return dimension;
    }
    
    protected Rectangle getVisibleEditorRect() {
        final Rectangle bounds = this.editor.getBounds();
        if (bounds.width > 0 && bounds.height > 0) {
            final Rectangle rectangle = bounds;
            final Rectangle rectangle2 = bounds;
            final int n = 0;
            rectangle2.y = n;
            rectangle.x = n;
            final Insets insets = this.editor.getInsets();
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
    public Rectangle modelToView(final JTextComponent textComponent, final int n) throws BadLocationException {
        return this.modelToView(textComponent, n, Position.Bias.Forward);
    }
    
    @Override
    public Rectangle modelToView(final JTextComponent textComponent, final int n, final Position.Bias bias) throws BadLocationException {
        final Document document = this.editor.getDocument();
        if (document instanceof AbstractDocument) {
            ((AbstractDocument)document).readLock();
        }
        try {
            final Rectangle visibleEditorRect = this.getVisibleEditorRect();
            if (visibleEditorRect != null) {
                this.rootView.setSize((float)visibleEditorRect.width, (float)visibleEditorRect.height);
                final Shape modelToView = this.rootView.modelToView(n, visibleEditorRect, bias);
                if (modelToView != null) {
                    return modelToView.getBounds();
                }
            }
        }
        finally {
            if (document instanceof AbstractDocument) {
                ((AbstractDocument)document).readUnlock();
            }
        }
        return null;
    }
    
    @Override
    public int viewToModel(final JTextComponent textComponent, final Point point) {
        return this.viewToModel(textComponent, point, BasicTextUI.discardBias);
    }
    
    @Override
    public int viewToModel(final JTextComponent textComponent, final Point point, final Position.Bias[] array) {
        int viewToModel = -1;
        final Document document = this.editor.getDocument();
        if (document instanceof AbstractDocument) {
            ((AbstractDocument)document).readLock();
        }
        try {
            final Rectangle visibleEditorRect = this.getVisibleEditorRect();
            if (visibleEditorRect != null) {
                this.rootView.setSize((float)visibleEditorRect.width, (float)visibleEditorRect.height);
                viewToModel = this.rootView.viewToModel((float)point.x, (float)point.y, visibleEditorRect, array);
            }
        }
        finally {
            if (document instanceof AbstractDocument) {
                ((AbstractDocument)document).readUnlock();
            }
        }
        return viewToModel;
    }
    
    @Override
    public int getNextVisualPositionFrom(final JTextComponent textComponent, final int n, final Position.Bias bias, final int n2, final Position.Bias[] array) throws BadLocationException {
        final Document document = this.editor.getDocument();
        if (document instanceof AbstractDocument) {
            ((AbstractDocument)document).readLock();
        }
        try {
            if (this.painted) {
                final Rectangle visibleEditorRect = this.getVisibleEditorRect();
                if (visibleEditorRect != null) {
                    this.rootView.setSize((float)visibleEditorRect.width, (float)visibleEditorRect.height);
                }
                return this.rootView.getNextVisualPositionFrom(n, bias, visibleEditorRect, n2, array);
            }
        }
        finally {
            if (document instanceof AbstractDocument) {
                ((AbstractDocument)document).readUnlock();
            }
        }
        return -1;
    }
    
    @Override
    public void damageRange(final JTextComponent textComponent, final int n, final int n2) {
        this.damageRange(textComponent, n, n2, Position.Bias.Forward, Position.Bias.Backward);
    }
    
    @Override
    public void damageRange(final JTextComponent textComponent, final int n, final int n2, final Position.Bias bias, final Position.Bias bias2) {
        if (this.painted) {
            final Rectangle visibleEditorRect = this.getVisibleEditorRect();
            if (visibleEditorRect != null) {
                final Document document = textComponent.getDocument();
                if (document instanceof AbstractDocument) {
                    ((AbstractDocument)document).readLock();
                }
                try {
                    this.rootView.setSize((float)visibleEditorRect.width, (float)visibleEditorRect.height);
                    final Shape modelToView = this.rootView.modelToView(n, bias, n2, bias2, visibleEditorRect);
                    final Rectangle rectangle = (Rectangle)((modelToView instanceof Rectangle) ? modelToView : modelToView.getBounds());
                    this.editor.repaint(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
                }
                catch (final BadLocationException ex) {}
                finally {
                    if (document instanceof AbstractDocument) {
                        ((AbstractDocument)document).readUnlock();
                    }
                }
            }
        }
    }
    
    @Override
    public EditorKit getEditorKit(final JTextComponent textComponent) {
        return BasicTextUI.defaultKit;
    }
    
    @Override
    public View getRootView(final JTextComponent textComponent) {
        return this.rootView;
    }
    
    @Override
    public String getToolTipText(final JTextComponent textComponent, final Point point) {
        if (!this.painted) {
            return null;
        }
        final Document document = this.editor.getDocument();
        String toolTipText = null;
        final Rectangle visibleEditorRect = this.getVisibleEditorRect();
        if (visibleEditorRect != null) {
            if (document instanceof AbstractDocument) {
                ((AbstractDocument)document).readLock();
            }
            try {
                toolTipText = this.rootView.getToolTipText((float)point.x, (float)point.y, visibleEditorRect);
            }
            finally {
                if (document instanceof AbstractDocument) {
                    ((AbstractDocument)document).readUnlock();
                }
            }
        }
        return toolTipText;
    }
    
    @Override
    public View create(final Element element) {
        return null;
    }
    
    public View create(final Element element, final int n, final int n2) {
        return null;
    }
    
    private static DragListener getDragListener() {
        synchronized (DragListener.class) {
            DragListener dragListener = (DragListener)AppContext.getAppContext().get(DragListener.class);
            if (dragListener == null) {
                dragListener = new DragListener();
                AppContext.getAppContext().put(DragListener.class, dragListener);
            }
            return dragListener;
        }
    }
    
    static {
        BasicTextUI.textCursor = new BasicCursor(2);
        defaultKit = new DefaultEditorKit();
        defaultTransferHandler = new TextTransferHandler();
        discardBias = new Position.Bias[1];
    }
    
    public static class BasicCaret extends DefaultCaret implements UIResource
    {
    }
    
    public static class BasicHighlighter extends DefaultHighlighter implements UIResource
    {
    }
    
    static class BasicCursor extends Cursor implements UIResource
    {
        BasicCursor(final int n) {
            super(n);
        }
        
        BasicCursor(final String s) {
            super(s);
        }
    }
    
    class RootView extends View
    {
        private View view;
        
        RootView() {
            super(null);
        }
        
        void setView(final View view) {
            final View view2 = this.view;
            this.view = null;
            if (view2 != null) {
                view2.setParent(null);
            }
            if (view != null) {
                view.setParent(this);
            }
            this.view = view;
        }
        
        @Override
        public AttributeSet getAttributes() {
            return null;
        }
        
        @Override
        public float getPreferredSpan(final int n) {
            if (this.view != null) {
                return this.view.getPreferredSpan(n);
            }
            return 10.0f;
        }
        
        @Override
        public float getMinimumSpan(final int n) {
            if (this.view != null) {
                return this.view.getMinimumSpan(n);
            }
            return 10.0f;
        }
        
        @Override
        public float getMaximumSpan(final int n) {
            return 2.14748365E9f;
        }
        
        @Override
        public void preferenceChanged(final View view, final boolean b, final boolean b2) {
            BasicTextUI.this.editor.revalidate();
        }
        
        @Override
        public float getAlignment(final int n) {
            if (this.view != null) {
                return this.view.getAlignment(n);
            }
            return 0.0f;
        }
        
        @Override
        public void paint(final Graphics graphics, final Shape shape) {
            if (this.view != null) {
                final Rectangle rectangle = (Rectangle)((shape instanceof Rectangle) ? shape : shape.getBounds());
                this.setSize((float)rectangle.width, (float)rectangle.height);
                this.view.paint(graphics, shape);
            }
        }
        
        @Override
        public void setParent(final View view) {
            throw new Error("Can't set parent on root view");
        }
        
        @Override
        public int getViewCount() {
            return 1;
        }
        
        @Override
        public View getView(final int n) {
            return this.view;
        }
        
        @Override
        public int getViewIndex(final int n, final Position.Bias bias) {
            return 0;
        }
        
        @Override
        public Shape getChildAllocation(final int n, final Shape shape) {
            return shape;
        }
        
        @Override
        public Shape modelToView(final int n, final Shape shape, final Position.Bias bias) throws BadLocationException {
            if (this.view != null) {
                return this.view.modelToView(n, shape, bias);
            }
            return null;
        }
        
        @Override
        public Shape modelToView(final int n, final Position.Bias bias, final int n2, final Position.Bias bias2, final Shape shape) throws BadLocationException {
            if (this.view != null) {
                return this.view.modelToView(n, bias, n2, bias2, shape);
            }
            return null;
        }
        
        @Override
        public int viewToModel(final float n, final float n2, final Shape shape, final Position.Bias[] array) {
            if (this.view != null) {
                return this.view.viewToModel(n, n2, shape, array);
            }
            return -1;
        }
        
        @Override
        public int getNextVisualPositionFrom(int n, final Position.Bias bias, final Shape shape, final int n2, final Position.Bias[] array) throws BadLocationException {
            if (n < -1) {
                throw new BadLocationException("invalid position", n);
            }
            if (this.view != null) {
                final int nextVisualPosition = this.view.getNextVisualPositionFrom(n, bias, shape, n2, array);
                if (nextVisualPosition != -1) {
                    n = nextVisualPosition;
                }
                else {
                    array[0] = bias;
                }
            }
            return n;
        }
        
        @Override
        public void insertUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
            if (this.view != null) {
                this.view.insertUpdate(documentEvent, shape, viewFactory);
            }
        }
        
        @Override
        public void removeUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
            if (this.view != null) {
                this.view.removeUpdate(documentEvent, shape, viewFactory);
            }
        }
        
        @Override
        public void changedUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
            if (this.view != null) {
                this.view.changedUpdate(documentEvent, shape, viewFactory);
            }
        }
        
        @Override
        public Document getDocument() {
            return BasicTextUI.this.editor.getDocument();
        }
        
        @Override
        public int getStartOffset() {
            if (this.view != null) {
                return this.view.getStartOffset();
            }
            return this.getElement().getStartOffset();
        }
        
        @Override
        public int getEndOffset() {
            if (this.view != null) {
                return this.view.getEndOffset();
            }
            return this.getElement().getEndOffset();
        }
        
        @Override
        public Element getElement() {
            if (this.view != null) {
                return this.view.getElement();
            }
            return BasicTextUI.this.editor.getDocument().getDefaultRootElement();
        }
        
        public View breakView(final int n, final float n2, final Shape shape) {
            throw new Error("Can't break root view");
        }
        
        @Override
        public int getResizeWeight(final int n) {
            if (this.view != null) {
                return this.view.getResizeWeight(n);
            }
            return 0;
        }
        
        @Override
        public void setSize(final float n, final float n2) {
            if (this.view != null) {
                this.view.setSize(n, n2);
            }
        }
        
        @Override
        public Container getContainer() {
            return BasicTextUI.this.editor;
        }
        
        @Override
        public ViewFactory getViewFactory() {
            final ViewFactory viewFactory = BasicTextUI.this.getEditorKit(BasicTextUI.this.editor).getViewFactory();
            if (viewFactory != null) {
                return viewFactory;
            }
            return BasicTextUI.this;
        }
    }
    
    class UpdateHandler implements PropertyChangeListener, DocumentListener, LayoutManager2, UIResource
    {
        private Hashtable<Component, Object> constraints;
        private boolean i18nView;
        
        UpdateHandler() {
            this.i18nView = false;
        }
        
        @Override
        public final void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final Object oldValue = propertyChangeEvent.getOldValue();
            final Object newValue = propertyChangeEvent.getNewValue();
            final String propertyName = propertyChangeEvent.getPropertyName();
            if (oldValue instanceof Document || newValue instanceof Document) {
                if (oldValue != null) {
                    ((Document)oldValue).removeDocumentListener(this);
                    this.i18nView = false;
                }
                if (newValue != null) {
                    ((Document)newValue).addDocumentListener(this);
                    if ("document" == propertyName) {
                        BasicTextUI.this.setView(null);
                        BasicTextUI.this.propertyChange(propertyChangeEvent);
                        BasicTextUI.this.modelChanged();
                        return;
                    }
                }
                BasicTextUI.this.modelChanged();
            }
            if ("focusAccelerator" == propertyName) {
                BasicTextUI.this.updateFocusAcceleratorBinding(true);
            }
            else if ("componentOrientation" == propertyName) {
                BasicTextUI.this.modelChanged();
            }
            else if ("font" == propertyName) {
                BasicTextUI.this.modelChanged();
            }
            else if ("dropLocation" == propertyName) {
                this.dropIndexChanged();
            }
            else if ("editable" == propertyName) {
                BasicTextUI.this.updateCursor();
                BasicTextUI.this.modelChanged();
            }
            BasicTextUI.this.propertyChange(propertyChangeEvent);
        }
        
        private void dropIndexChanged() {
            if (BasicTextUI.this.editor.getDropMode() == DropMode.USE_SELECTION) {
                return;
            }
            final JTextComponent.DropLocation dropLocation = BasicTextUI.this.editor.getDropLocation();
            if (dropLocation == null) {
                if (BasicTextUI.this.dropCaret != null) {
                    BasicTextUI.this.dropCaret.deinstall(BasicTextUI.this.editor);
                    BasicTextUI.this.editor.repaint(BasicTextUI.this.dropCaret);
                    BasicTextUI.this.dropCaret = null;
                }
            }
            else {
                if (BasicTextUI.this.dropCaret == null) {
                    BasicTextUI.this.dropCaret = new BasicCaret();
                    BasicTextUI.this.dropCaret.install(BasicTextUI.this.editor);
                    BasicTextUI.this.dropCaret.setVisible(true);
                }
                BasicTextUI.this.dropCaret.setDot(dropLocation.getIndex(), dropLocation.getBias());
            }
        }
        
        @Override
        public final void insertUpdate(final DocumentEvent documentEvent) {
            final Object property = documentEvent.getDocument().getProperty("i18n");
            if (property instanceof Boolean) {
                final Boolean b = (Boolean)property;
                if (b != this.i18nView) {
                    this.i18nView = b;
                    BasicTextUI.this.modelChanged();
                    return;
                }
            }
            BasicTextUI.this.rootView.insertUpdate(documentEvent, BasicTextUI.this.painted ? BasicTextUI.this.getVisibleEditorRect() : null, BasicTextUI.this.rootView.getViewFactory());
        }
        
        @Override
        public final void removeUpdate(final DocumentEvent documentEvent) {
            BasicTextUI.this.rootView.removeUpdate(documentEvent, BasicTextUI.this.painted ? BasicTextUI.this.getVisibleEditorRect() : null, BasicTextUI.this.rootView.getViewFactory());
        }
        
        @Override
        public final void changedUpdate(final DocumentEvent documentEvent) {
            BasicTextUI.this.rootView.changedUpdate(documentEvent, BasicTextUI.this.painted ? BasicTextUI.this.getVisibleEditorRect() : null, BasicTextUI.this.rootView.getViewFactory());
        }
        
        @Override
        public void addLayoutComponent(final String s, final Component component) {
        }
        
        @Override
        public void removeLayoutComponent(final Component component) {
            if (this.constraints != null) {
                this.constraints.remove(component);
            }
        }
        
        @Override
        public Dimension preferredLayoutSize(final Container container) {
            return null;
        }
        
        @Override
        public Dimension minimumLayoutSize(final Container container) {
            return null;
        }
        
        @Override
        public void layoutContainer(final Container container) {
            if (this.constraints != null && !this.constraints.isEmpty()) {
                final Rectangle visibleEditorRect = BasicTextUI.this.getVisibleEditorRect();
                if (visibleEditorRect != null) {
                    final Document document = BasicTextUI.this.editor.getDocument();
                    if (document instanceof AbstractDocument) {
                        ((AbstractDocument)document).readLock();
                    }
                    try {
                        BasicTextUI.this.rootView.setSize((float)visibleEditorRect.width, (float)visibleEditorRect.height);
                        final Enumeration<Component> keys = this.constraints.keys();
                        while (keys.hasMoreElements()) {
                            final Component component = keys.nextElement();
                            final Shape calculateViewPosition = this.calculateViewPosition(visibleEditorRect, this.constraints.get(component));
                            if (calculateViewPosition != null) {
                                component.setBounds((calculateViewPosition instanceof Rectangle) ? ((Rectangle)calculateViewPosition) : calculateViewPosition.getBounds());
                            }
                        }
                    }
                    finally {
                        if (document instanceof AbstractDocument) {
                            ((AbstractDocument)document).readUnlock();
                        }
                    }
                }
            }
        }
        
        Shape calculateViewPosition(Shape childAllocation, final View view) {
            final int startOffset = view.getStartOffset();
            View view2 = null;
            int viewIndex;
            for (RootView rootView = BasicTextUI.this.rootView; rootView != null && rootView != view; view2 = (rootView = (RootView)rootView.getView(viewIndex))) {
                viewIndex = rootView.getViewIndex(startOffset, Position.Bias.Forward);
                childAllocation = rootView.getChildAllocation(viewIndex, childAllocation);
            }
            return (view2 != null) ? childAllocation : null;
        }
        
        @Override
        public void addLayoutComponent(final Component component, final Object o) {
            if (o instanceof View) {
                if (this.constraints == null) {
                    this.constraints = new Hashtable<Component, Object>(7);
                }
                this.constraints.put(component, o);
            }
        }
        
        @Override
        public Dimension maximumLayoutSize(final Container container) {
            return null;
        }
        
        @Override
        public float getLayoutAlignmentX(final Container container) {
            return 0.5f;
        }
        
        @Override
        public float getLayoutAlignmentY(final Container container) {
            return 0.5f;
        }
        
        @Override
        public void invalidateLayout(final Container container) {
        }
    }
    
    class TextActionWrapper extends TextAction
    {
        TextAction action;
        
        public TextActionWrapper(final TextAction action) {
            super((String)action.getValue("Name"));
            this.action = null;
            this.action = action;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            this.action.actionPerformed(actionEvent);
        }
        
        @Override
        public boolean isEnabled() {
            return (BasicTextUI.this.editor == null || BasicTextUI.this.editor.isEditable()) && this.action.isEnabled();
        }
    }
    
    class FocusAction extends AbstractAction
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            BasicTextUI.this.editor.requestFocus();
        }
        
        @Override
        public boolean isEnabled() {
            return BasicTextUI.this.editor.isEditable();
        }
    }
    
    static class DragListener extends MouseInputAdapter implements DragRecognitionSupport.BeforeDrag
    {
        private boolean dragStarted;
        
        @Override
        public void dragStarting(final MouseEvent mouseEvent) {
            this.dragStarted = true;
        }
        
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            if (((JTextComponent)mouseEvent.getSource()).getDragEnabled()) {
                this.dragStarted = false;
                if (this.isDragPossible(mouseEvent) && DragRecognitionSupport.mousePressed(mouseEvent)) {
                    mouseEvent.consume();
                }
            }
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
            if (((JTextComponent)mouseEvent.getSource()).getDragEnabled()) {
                if (this.dragStarted) {
                    mouseEvent.consume();
                }
                DragRecognitionSupport.mouseReleased(mouseEvent);
            }
        }
        
        @Override
        public void mouseDragged(final MouseEvent mouseEvent) {
            if (((JTextComponent)mouseEvent.getSource()).getDragEnabled() && (this.dragStarted || DragRecognitionSupport.mouseDragged(mouseEvent, this))) {
                mouseEvent.consume();
            }
        }
        
        protected boolean isDragPossible(final MouseEvent mouseEvent) {
            final JTextComponent textComponent = (JTextComponent)mouseEvent.getSource();
            if (textComponent.isEnabled()) {
                final Caret caret = textComponent.getCaret();
                final int dot = caret.getDot();
                final int mark = caret.getMark();
                if (dot != mark) {
                    final int viewToModel = textComponent.viewToModel(new Point(mouseEvent.getX(), mouseEvent.getY()));
                    final int min = Math.min(dot, mark);
                    final int max = Math.max(dot, mark);
                    if (viewToModel >= min && viewToModel < max) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
    
    static class TextTransferHandler extends TransferHandler implements UIResource
    {
        private JTextComponent exportComp;
        private boolean shouldRemove;
        private int p0;
        private int p1;
        private boolean modeBetween;
        private boolean isDrop;
        private int dropAction;
        private Position.Bias dropBias;
        
        TextTransferHandler() {
            this.modeBetween = false;
            this.isDrop = false;
            this.dropAction = 2;
        }
        
        protected DataFlavor getImportFlavor(final DataFlavor[] array, final JTextComponent textComponent) {
            DataFlavor dataFlavor = null;
            DataFlavor dataFlavor2 = null;
            DataFlavor dataFlavor3 = null;
            if (textComponent instanceof JEditorPane) {
                for (int i = 0; i < array.length; ++i) {
                    final String mimeType = array[i].getMimeType();
                    if (mimeType.startsWith(((JEditorPane)textComponent).getEditorKit().getContentType())) {
                        return array[i];
                    }
                    if (dataFlavor == null && mimeType.startsWith("text/plain")) {
                        dataFlavor = array[i];
                    }
                    else if (dataFlavor2 == null && mimeType.startsWith("application/x-java-jvm-local-objectref") && array[i].getRepresentationClass() == String.class) {
                        dataFlavor2 = array[i];
                    }
                    else if (dataFlavor3 == null && array[i].equals(DataFlavor.stringFlavor)) {
                        dataFlavor3 = array[i];
                    }
                }
                if (dataFlavor != null) {
                    return dataFlavor;
                }
                if (dataFlavor2 != null) {
                    return dataFlavor2;
                }
                if (dataFlavor3 != null) {
                    return dataFlavor3;
                }
                return null;
            }
            else {
                for (int j = 0; j < array.length; ++j) {
                    final String mimeType2 = array[j].getMimeType();
                    if (mimeType2.startsWith("text/plain")) {
                        return array[j];
                    }
                    if (dataFlavor2 == null && mimeType2.startsWith("application/x-java-jvm-local-objectref") && array[j].getRepresentationClass() == String.class) {
                        dataFlavor2 = array[j];
                    }
                    else if (dataFlavor3 == null && array[j].equals(DataFlavor.stringFlavor)) {
                        dataFlavor3 = array[j];
                    }
                }
                if (dataFlavor2 != null) {
                    return dataFlavor2;
                }
                if (dataFlavor3 != null) {
                    return dataFlavor3;
                }
                return null;
            }
        }
        
        protected void handleReaderImport(final Reader reader, final JTextComponent textComponent, final boolean b) throws BadLocationException, IOException {
            if (b) {
                final int selectionStart = textComponent.getSelectionStart();
                final int n = textComponent.getSelectionEnd() - selectionStart;
                final EditorKit editorKit = textComponent.getUI().getEditorKit(textComponent);
                final Document document = textComponent.getDocument();
                if (n > 0) {
                    document.remove(selectionStart, n);
                }
                editorKit.read(reader, document, selectionStart);
            }
            else {
                final char[] array = new char[1024];
                int n2 = 0;
                StringBuffer sb = null;
                int read;
                while ((read = reader.read(array, 0, array.length)) != -1) {
                    if (sb == null) {
                        sb = new StringBuffer(read);
                    }
                    int n3 = 0;
                    for (int i = 0; i < read; ++i) {
                        switch (array[i]) {
                            case '\r': {
                                if (n2 == 0) {
                                    n2 = 1;
                                    break;
                                }
                                if (i == 0) {
                                    sb.append('\n');
                                    break;
                                }
                                array[i - 1] = '\n';
                                break;
                            }
                            case '\n': {
                                if (n2 != 0) {
                                    if (i > n3 + 1) {
                                        sb.append(array, n3, i - n3 - 1);
                                    }
                                    n2 = 0;
                                    n3 = i;
                                    break;
                                }
                                break;
                            }
                            default: {
                                if (n2 != 0) {
                                    if (i == 0) {
                                        sb.append('\n');
                                    }
                                    else {
                                        array[i - 1] = '\n';
                                    }
                                    n2 = 0;
                                    break;
                                }
                                break;
                            }
                        }
                    }
                    if (n3 < read) {
                        if (n2 != 0) {
                            if (n3 >= read - 1) {
                                continue;
                            }
                            sb.append(array, n3, read - n3 - 1);
                        }
                        else {
                            sb.append(array, n3, read - n3);
                        }
                    }
                }
                if (n2 != 0) {
                    sb.append('\n');
                }
                textComponent.replaceSelection((sb != null) ? sb.toString() : "");
            }
        }
        
        @Override
        public int getSourceActions(final JComponent component) {
            if (component instanceof JPasswordField && component.getClientProperty("JPasswordField.cutCopyAllowed") != Boolean.TRUE) {
                return 0;
            }
            return ((JTextComponent)component).isEditable() ? 3 : 1;
        }
        
        @Override
        protected Transferable createTransferable(final JComponent component) {
            this.exportComp = (JTextComponent)component;
            this.shouldRemove = true;
            this.p0 = this.exportComp.getSelectionStart();
            this.p1 = this.exportComp.getSelectionEnd();
            return (this.p0 != this.p1) ? new TextTransferable(this.exportComp, this.p0, this.p1) : null;
        }
        
        @Override
        protected void exportDone(final JComponent component, final Transferable transferable, final int n) {
            if (this.shouldRemove && n == 2) {
                ((TextTransferable)transferable).removeText();
            }
            this.exportComp = null;
        }
        
        @Override
        public boolean importData(final TransferSupport transferSupport) {
            this.isDrop = transferSupport.isDrop();
            if (this.isDrop) {
                this.modeBetween = (((JTextComponent)transferSupport.getComponent()).getDropMode() == DropMode.INSERT);
                this.dropBias = ((JTextComponent.DropLocation)transferSupport.getDropLocation()).getBias();
                this.dropAction = transferSupport.getDropAction();
            }
            try {
                return super.importData(transferSupport);
            }
            finally {
                this.isDrop = false;
                this.modeBetween = false;
                this.dropBias = null;
                this.dropAction = 2;
            }
        }
        
        @Override
        public boolean importData(final JComponent component, final Transferable transferable) {
            final JTextComponent textComponent = (JTextComponent)component;
            final int caretPosition = this.modeBetween ? textComponent.getDropLocation().getIndex() : textComponent.getCaretPosition();
            if (this.dropAction == 2 && textComponent == this.exportComp && caretPosition >= this.p0 && caretPosition <= this.p1) {
                this.shouldRemove = false;
                return true;
            }
            boolean b = false;
            final DataFlavor importFlavor = this.getImportFlavor(transferable.getTransferDataFlavors(), textComponent);
            if (importFlavor != null) {
                try {
                    boolean b2 = false;
                    if (component instanceof JEditorPane) {
                        final JEditorPane editorPane = (JEditorPane)component;
                        if (!editorPane.getContentType().startsWith("text/plain") && importFlavor.getMimeType().startsWith(editorPane.getContentType())) {
                            b2 = true;
                        }
                    }
                    final InputContext inputContext = textComponent.getInputContext();
                    if (inputContext != null) {
                        inputContext.endComposition();
                    }
                    final Reader readerForText = importFlavor.getReaderForText(transferable);
                    if (this.modeBetween) {
                        final Caret caret = textComponent.getCaret();
                        if (caret instanceof DefaultCaret) {
                            ((DefaultCaret)caret).setDot(caretPosition, this.dropBias);
                        }
                        else {
                            textComponent.setCaretPosition(caretPosition);
                        }
                    }
                    this.handleReaderImport(readerForText, textComponent, b2);
                    if (this.isDrop) {
                        textComponent.requestFocus();
                        final Caret caret2 = textComponent.getCaret();
                        if (caret2 instanceof DefaultCaret) {
                            final int dot = caret2.getDot();
                            final Position.Bias dotBias = ((DefaultCaret)caret2).getDotBias();
                            ((DefaultCaret)caret2).setDot(caretPosition, this.dropBias);
                            ((DefaultCaret)caret2).moveDot(dot, dotBias);
                        }
                        else {
                            textComponent.select(caretPosition, textComponent.getCaretPosition());
                        }
                    }
                    b = true;
                }
                catch (final UnsupportedFlavorException ex) {}
                catch (final BadLocationException ex2) {}
                catch (final IOException ex3) {}
            }
            return b;
        }
        
        @Override
        public boolean canImport(final JComponent component, final DataFlavor[] array) {
            final JTextComponent textComponent = (JTextComponent)component;
            return textComponent.isEditable() && textComponent.isEnabled() && this.getImportFlavor(array, textComponent) != null;
        }
        
        static class TextTransferable extends BasicTransferable
        {
            Position p0;
            Position p1;
            String mimeType;
            String richText;
            JTextComponent c;
            
            TextTransferable(final JTextComponent c, final int n, final int n2) {
                super(null, null);
                this.c = c;
                final Document document = c.getDocument();
                try {
                    this.p0 = document.createPosition(n);
                    this.p1 = document.createPosition(n2);
                    this.plainData = c.getSelectedText();
                    if (c instanceof JEditorPane) {
                        final JEditorPane editorPane = (JEditorPane)c;
                        this.mimeType = editorPane.getContentType();
                        if (this.mimeType.startsWith("text/plain")) {
                            return;
                        }
                        final StringWriter stringWriter = new StringWriter(this.p1.getOffset() - this.p0.getOffset());
                        editorPane.getEditorKit().write(stringWriter, document, this.p0.getOffset(), this.p1.getOffset() - this.p0.getOffset());
                        if (this.mimeType.startsWith("text/html")) {
                            this.htmlData = stringWriter.toString();
                        }
                        else {
                            this.richText = stringWriter.toString();
                        }
                    }
                }
                catch (final BadLocationException ex) {}
                catch (final IOException ex2) {}
            }
            
            void removeText() {
                if (this.p0 != null && this.p1 != null && this.p0.getOffset() != this.p1.getOffset()) {
                    try {
                        this.c.getDocument().remove(this.p0.getOffset(), this.p1.getOffset() - this.p0.getOffset());
                    }
                    catch (final BadLocationException ex) {}
                }
            }
            
            @Override
            protected DataFlavor[] getRicherFlavors() {
                if (this.richText == null) {
                    return null;
                }
                try {
                    return new DataFlavor[] { new DataFlavor(this.mimeType + ";class=java.lang.String"), new DataFlavor(this.mimeType + ";class=java.io.Reader"), new DataFlavor(this.mimeType + ";class=java.io.InputStream;charset=unicode") };
                }
                catch (final ClassNotFoundException ex) {
                    return null;
                }
            }
            
            @Override
            protected Object getRicherData(final DataFlavor dataFlavor) throws UnsupportedFlavorException {
                if (this.richText == null) {
                    return null;
                }
                if (String.class.equals(dataFlavor.getRepresentationClass())) {
                    return this.richText;
                }
                if (Reader.class.equals(dataFlavor.getRepresentationClass())) {
                    return new StringReader(this.richText);
                }
                if (InputStream.class.equals(dataFlavor.getRepresentationClass())) {
                    return new StringBufferInputStream(this.richText);
                }
                throw new UnsupportedFlavorException(dataFlavor);
            }
        }
    }
}
