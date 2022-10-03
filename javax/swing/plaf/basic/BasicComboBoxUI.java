package javax.swing.plaf.basic;

import javax.swing.text.Position;
import java.awt.EventQueue;
import java.awt.event.InputEvent;
import javax.swing.JRootPane;
import java.awt.event.ActionEvent;
import sun.swing.UIAction;
import java.beans.PropertyChangeEvent;
import java.awt.event.ItemEvent;
import javax.swing.event.ListDataEvent;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import javax.swing.ActionMap;
import javax.swing.SwingUtilities;
import javax.swing.ComboBoxModel;
import java.awt.Container;
import javax.swing.JPanel;
import java.awt.Color;
import sun.swing.DefaultLookup;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.accessibility.Accessible;
import javax.swing.JLabel;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import javax.swing.UIManager;
import javax.swing.LookAndFeel;
import javax.swing.ComboBoxEditor;
import java.awt.LayoutManager;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import sun.awt.AppContext;
import javax.swing.ListCellRenderer;
import java.awt.Insets;
import java.awt.Dimension;
import javax.swing.event.ListDataListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import java.awt.Component;
import javax.swing.CellRendererPane;
import javax.swing.JList;
import javax.swing.JComboBox;
import javax.swing.plaf.ComboBoxUI;

public class BasicComboBoxUI extends ComboBoxUI
{
    protected JComboBox comboBox;
    protected boolean hasFocus;
    private boolean isTableCellEditor;
    private static final String IS_TABLE_CELL_EDITOR = "JComboBox.isTableCellEditor";
    protected JList listBox;
    protected CellRendererPane currentValuePane;
    protected ComboPopup popup;
    protected Component editor;
    protected JButton arrowButton;
    protected KeyListener keyListener;
    protected FocusListener focusListener;
    protected PropertyChangeListener propertyChangeListener;
    protected ItemListener itemListener;
    protected MouseListener popupMouseListener;
    protected MouseMotionListener popupMouseMotionListener;
    protected KeyListener popupKeyListener;
    protected ListDataListener listDataListener;
    private Handler handler;
    private long timeFactor;
    private long lastTime;
    private long time;
    JComboBox.KeySelectionManager keySelectionManager;
    protected boolean isMinimumSizeDirty;
    protected Dimension cachedMinimumSize;
    private boolean isDisplaySizeDirty;
    private Dimension cachedDisplaySize;
    private static final Object COMBO_UI_LIST_CELL_RENDERER_KEY;
    static final StringBuffer HIDE_POPUP_KEY;
    private boolean sameBaseline;
    protected boolean squareButton;
    protected Insets padding;
    
    public BasicComboBoxUI() {
        this.hasFocus = false;
        this.isTableCellEditor = false;
        this.currentValuePane = new CellRendererPane();
        this.timeFactor = 1000L;
        this.lastTime = 0L;
        this.time = 0L;
        this.isMinimumSizeDirty = true;
        this.cachedMinimumSize = new Dimension(0, 0);
        this.isDisplaySizeDirty = true;
        this.cachedDisplaySize = new Dimension(0, 0);
        this.squareButton = true;
    }
    
    private static ListCellRenderer getDefaultListCellRenderer() {
        ListCellRenderer listCellRenderer = (ListCellRenderer)AppContext.getAppContext().get(BasicComboBoxUI.COMBO_UI_LIST_CELL_RENDERER_KEY);
        if (listCellRenderer == null) {
            listCellRenderer = new DefaultListCellRenderer();
            AppContext.getAppContext().put(BasicComboBoxUI.COMBO_UI_LIST_CELL_RENDERER_KEY, new DefaultListCellRenderer());
        }
        return listCellRenderer;
    }
    
    static void loadActionMap(final LazyActionMap lazyActionMap) {
        lazyActionMap.put(new Actions("hidePopup"));
        lazyActionMap.put(new Actions("pageDownPassThrough"));
        lazyActionMap.put(new Actions("pageUpPassThrough"));
        lazyActionMap.put(new Actions("homePassThrough"));
        lazyActionMap.put(new Actions("endPassThrough"));
        lazyActionMap.put(new Actions("selectNext"));
        lazyActionMap.put(new Actions("selectNext2"));
        lazyActionMap.put(new Actions("togglePopup"));
        lazyActionMap.put(new Actions("spacePopup"));
        lazyActionMap.put(new Actions("selectPrevious"));
        lazyActionMap.put(new Actions("selectPrevious2"));
        lazyActionMap.put(new Actions("enterPressed"));
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new BasicComboBoxUI();
    }
    
    @Override
    public void installUI(final JComponent component) {
        this.isMinimumSizeDirty = true;
        this.comboBox = (JComboBox)component;
        this.installDefaults();
        this.popup = this.createPopup();
        this.listBox = this.popup.getList();
        final Boolean b = (Boolean)component.getClientProperty("JComboBox.isTableCellEditor");
        if (b != null) {
            this.isTableCellEditor = b.equals(Boolean.TRUE);
        }
        if (this.comboBox.getRenderer() == null || this.comboBox.getRenderer() instanceof UIResource) {
            this.comboBox.setRenderer(this.createRenderer());
        }
        if (this.comboBox.getEditor() == null || this.comboBox.getEditor() instanceof UIResource) {
            this.comboBox.setEditor(this.createEditor());
        }
        this.installListeners();
        this.installComponents();
        this.comboBox.setLayout(this.createLayoutManager());
        this.comboBox.setRequestFocusEnabled(true);
        this.installKeyboardActions();
        this.comboBox.putClientProperty("doNotCancelPopup", BasicComboBoxUI.HIDE_POPUP_KEY);
        if (this.keySelectionManager == null || this.keySelectionManager instanceof UIResource) {
            this.keySelectionManager = new DefaultKeySelectionManager();
        }
        this.comboBox.setKeySelectionManager(this.keySelectionManager);
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        this.setPopupVisible(this.comboBox, false);
        this.popup.uninstallingUI();
        this.uninstallKeyboardActions();
        this.comboBox.setLayout(null);
        this.uninstallComponents();
        this.uninstallListeners();
        this.uninstallDefaults();
        if (this.comboBox.getRenderer() == null || this.comboBox.getRenderer() instanceof UIResource) {
            this.comboBox.setRenderer(null);
        }
        final ComboBoxEditor editor = this.comboBox.getEditor();
        if (editor instanceof UIResource) {
            if (editor.getEditorComponent().hasFocus()) {
                this.comboBox.requestFocusInWindow();
            }
            this.comboBox.setEditor(null);
        }
        if (this.keySelectionManager instanceof UIResource) {
            this.comboBox.setKeySelectionManager(null);
        }
        this.handler = null;
        this.keyListener = null;
        this.focusListener = null;
        this.listDataListener = null;
        this.propertyChangeListener = null;
        this.popup = null;
        this.listBox = null;
        this.comboBox = null;
    }
    
    protected void installDefaults() {
        LookAndFeel.installColorsAndFont(this.comboBox, "ComboBox.background", "ComboBox.foreground", "ComboBox.font");
        LookAndFeel.installBorder(this.comboBox, "ComboBox.border");
        LookAndFeel.installProperty(this.comboBox, "opaque", Boolean.TRUE);
        final Long n = (Long)UIManager.get("ComboBox.timeFactor");
        this.timeFactor = ((n == null) ? 1000L : n);
        final Boolean b = (Boolean)UIManager.get("ComboBox.squareButton");
        this.squareButton = (b == null || b);
        this.padding = UIManager.getInsets("ComboBox.padding");
    }
    
    protected void installListeners() {
        final ItemListener itemListener = this.createItemListener();
        this.itemListener = itemListener;
        if (itemListener != null) {
            this.comboBox.addItemListener(this.itemListener);
        }
        if ((this.propertyChangeListener = this.createPropertyChangeListener()) != null) {
            this.comboBox.addPropertyChangeListener(this.propertyChangeListener);
        }
        if ((this.keyListener = this.createKeyListener()) != null) {
            this.comboBox.addKeyListener(this.keyListener);
        }
        if ((this.focusListener = this.createFocusListener()) != null) {
            this.comboBox.addFocusListener(this.focusListener);
        }
        if ((this.popupMouseListener = this.popup.getMouseListener()) != null) {
            this.comboBox.addMouseListener(this.popupMouseListener);
        }
        if ((this.popupMouseMotionListener = this.popup.getMouseMotionListener()) != null) {
            this.comboBox.addMouseMotionListener(this.popupMouseMotionListener);
        }
        if ((this.popupKeyListener = this.popup.getKeyListener()) != null) {
            this.comboBox.addKeyListener(this.popupKeyListener);
        }
        if (this.comboBox.getModel() != null && (this.listDataListener = this.createListDataListener()) != null) {
            this.comboBox.getModel().addListDataListener(this.listDataListener);
        }
    }
    
    protected void uninstallDefaults() {
        LookAndFeel.installColorsAndFont(this.comboBox, "ComboBox.background", "ComboBox.foreground", "ComboBox.font");
        LookAndFeel.uninstallBorder(this.comboBox);
    }
    
    protected void uninstallListeners() {
        if (this.keyListener != null) {
            this.comboBox.removeKeyListener(this.keyListener);
        }
        if (this.itemListener != null) {
            this.comboBox.removeItemListener(this.itemListener);
        }
        if (this.propertyChangeListener != null) {
            this.comboBox.removePropertyChangeListener(this.propertyChangeListener);
        }
        if (this.focusListener != null) {
            this.comboBox.removeFocusListener(this.focusListener);
        }
        if (this.popupMouseListener != null) {
            this.comboBox.removeMouseListener(this.popupMouseListener);
        }
        if (this.popupMouseMotionListener != null) {
            this.comboBox.removeMouseMotionListener(this.popupMouseMotionListener);
        }
        if (this.popupKeyListener != null) {
            this.comboBox.removeKeyListener(this.popupKeyListener);
        }
        if (this.comboBox.getModel() != null && this.listDataListener != null) {
            this.comboBox.getModel().removeListDataListener(this.listDataListener);
        }
    }
    
    protected ComboPopup createPopup() {
        return new BasicComboPopup(this.comboBox);
    }
    
    protected KeyListener createKeyListener() {
        return this.getHandler();
    }
    
    protected FocusListener createFocusListener() {
        return this.getHandler();
    }
    
    protected ListDataListener createListDataListener() {
        return this.getHandler();
    }
    
    protected ItemListener createItemListener() {
        return null;
    }
    
    protected PropertyChangeListener createPropertyChangeListener() {
        return this.getHandler();
    }
    
    protected LayoutManager createLayoutManager() {
        return this.getHandler();
    }
    
    protected ListCellRenderer createRenderer() {
        return new BasicComboBoxRenderer.UIResource();
    }
    
    protected ComboBoxEditor createEditor() {
        return new BasicComboBoxEditor.UIResource();
    }
    
    private Handler getHandler() {
        if (this.handler == null) {
            this.handler = new Handler();
        }
        return this.handler;
    }
    
    private void updateToolTipTextForChildren() {
        final Component[] components = this.comboBox.getComponents();
        for (int i = 0; i < components.length; ++i) {
            if (components[i] instanceof JComponent) {
                ((JComponent)components[i]).setToolTipText(this.comboBox.getToolTipText());
            }
        }
    }
    
    protected void installComponents() {
        this.arrowButton = this.createArrowButton();
        if (this.arrowButton != null) {
            this.comboBox.add(this.arrowButton);
            this.configureArrowButton();
        }
        if (this.comboBox.isEditable()) {
            this.addEditor();
        }
        this.comboBox.add(this.currentValuePane);
    }
    
    protected void uninstallComponents() {
        if (this.arrowButton != null) {
            this.unconfigureArrowButton();
        }
        if (this.editor != null) {
            this.unconfigureEditor();
        }
        this.comboBox.removeAll();
        this.arrowButton = null;
    }
    
    public void addEditor() {
        this.removeEditor();
        this.editor = this.comboBox.getEditor().getEditorComponent();
        if (this.editor != null) {
            this.configureEditor();
            this.comboBox.add(this.editor);
            if (this.comboBox.isFocusOwner()) {
                this.editor.requestFocusInWindow();
            }
        }
    }
    
    public void removeEditor() {
        if (this.editor != null) {
            this.unconfigureEditor();
            this.comboBox.remove(this.editor);
            this.editor = null;
        }
    }
    
    protected void configureEditor() {
        this.editor.setEnabled(this.comboBox.isEnabled());
        this.editor.setFocusable(this.comboBox.isFocusable());
        this.editor.setFont(this.comboBox.getFont());
        if (this.focusListener != null) {
            this.editor.addFocusListener(this.focusListener);
        }
        this.editor.addFocusListener(this.getHandler());
        this.comboBox.getEditor().addActionListener(this.getHandler());
        if (this.editor instanceof JComponent) {
            ((JComponent)this.editor).putClientProperty("doNotCancelPopup", BasicComboBoxUI.HIDE_POPUP_KEY);
            ((JComponent)this.editor).setInheritsPopupMenu(true);
        }
        this.comboBox.configureEditor(this.comboBox.getEditor(), this.comboBox.getSelectedItem());
        this.editor.addPropertyChangeListener(this.propertyChangeListener);
    }
    
    protected void unconfigureEditor() {
        if (this.focusListener != null) {
            this.editor.removeFocusListener(this.focusListener);
        }
        this.editor.removePropertyChangeListener(this.propertyChangeListener);
        this.editor.removeFocusListener(this.getHandler());
        this.comboBox.getEditor().removeActionListener(this.getHandler());
    }
    
    public void configureArrowButton() {
        if (this.arrowButton != null) {
            this.arrowButton.setEnabled(this.comboBox.isEnabled());
            this.arrowButton.setFocusable(this.comboBox.isFocusable());
            this.arrowButton.setRequestFocusEnabled(false);
            this.arrowButton.addMouseListener(this.popup.getMouseListener());
            this.arrowButton.addMouseMotionListener(this.popup.getMouseMotionListener());
            this.arrowButton.resetKeyboardActions();
            this.arrowButton.putClientProperty("doNotCancelPopup", BasicComboBoxUI.HIDE_POPUP_KEY);
            this.arrowButton.setInheritsPopupMenu(true);
        }
    }
    
    public void unconfigureArrowButton() {
        if (this.arrowButton != null) {
            this.arrowButton.removeMouseListener(this.popup.getMouseListener());
            this.arrowButton.removeMouseMotionListener(this.popup.getMouseMotionListener());
        }
    }
    
    protected JButton createArrowButton() {
        final BasicArrowButton basicArrowButton = new BasicArrowButton(5, UIManager.getColor("ComboBox.buttonBackground"), UIManager.getColor("ComboBox.buttonShadow"), UIManager.getColor("ComboBox.buttonDarkShadow"), UIManager.getColor("ComboBox.buttonHighlight"));
        basicArrowButton.setName("ComboBox.arrowButton");
        return basicArrowButton;
    }
    
    @Override
    public boolean isPopupVisible(final JComboBox comboBox) {
        return this.popup.isVisible();
    }
    
    @Override
    public void setPopupVisible(final JComboBox comboBox, final boolean b) {
        if (b) {
            this.popup.show();
        }
        else {
            this.popup.hide();
        }
    }
    
    @Override
    public boolean isFocusTraversable(final JComboBox comboBox) {
        return !this.comboBox.isEditable();
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        this.hasFocus = this.comboBox.hasFocus();
        if (!this.comboBox.isEditable()) {
            final Rectangle rectangleForCurrentValue = this.rectangleForCurrentValue();
            this.paintCurrentValueBackground(graphics, rectangleForCurrentValue, this.hasFocus);
            this.paintCurrentValue(graphics, rectangleForCurrentValue, this.hasFocus);
        }
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        return this.getMinimumSize(component);
    }
    
    @Override
    public Dimension getMinimumSize(final JComponent component) {
        if (!this.isMinimumSizeDirty) {
            return new Dimension(this.cachedMinimumSize);
        }
        final Dimension displaySize = this.getDisplaySize();
        final Insets insets = this.getInsets();
        final int height = displaySize.height;
        final int n = this.squareButton ? height : this.arrowButton.getPreferredSize().width;
        final Dimension dimension = displaySize;
        dimension.height += insets.top + insets.bottom;
        final Dimension dimension2 = displaySize;
        dimension2.width += insets.left + insets.right + n;
        this.cachedMinimumSize.setSize(displaySize.width, displaySize.height);
        this.isMinimumSizeDirty = false;
        return new Dimension(displaySize);
    }
    
    @Override
    public Dimension getMaximumSize(final JComponent component) {
        return new Dimension(32767, 32767);
    }
    
    @Override
    public int getBaseline(final JComponent component, final int n, int n2) {
        super.getBaseline(component, n, n2);
        int n3 = -1;
        this.getDisplaySize();
        if (this.sameBaseline) {
            final Insets insets = component.getInsets();
            n2 = n2 - insets.top - insets.bottom;
            if (!this.comboBox.isEditable()) {
                ListCellRenderer renderer = this.comboBox.getRenderer();
                if (renderer == null) {
                    renderer = new DefaultListCellRenderer();
                }
                Object element = null;
                final Object prototypeDisplayValue = this.comboBox.getPrototypeDisplayValue();
                if (prototypeDisplayValue != null) {
                    element = prototypeDisplayValue;
                }
                else if (this.comboBox.getModel().getSize() > 0) {
                    element = this.comboBox.getModel().getElementAt(0);
                }
                final Component listCellRendererComponent = renderer.getListCellRendererComponent(this.listBox, element, -1, false, false);
                if (listCellRendererComponent instanceof JLabel) {
                    final JLabel label = (JLabel)listCellRendererComponent;
                    final String text = label.getText();
                    if (text == null || text.isEmpty()) {
                        label.setText(" ");
                    }
                }
                if (listCellRendererComponent instanceof JComponent) {
                    listCellRendererComponent.setFont(this.comboBox.getFont());
                }
                n3 = listCellRendererComponent.getBaseline(n, n2);
            }
            else {
                n3 = this.editor.getBaseline(n, n2);
            }
            if (n3 > 0) {
                n3 += insets.top;
            }
        }
        return n3;
    }
    
    @Override
    public Component.BaselineResizeBehavior getBaselineResizeBehavior(final JComponent component) {
        super.getBaselineResizeBehavior(component);
        this.getDisplaySize();
        if (this.comboBox.isEditable()) {
            return this.editor.getBaselineResizeBehavior();
        }
        if (this.sameBaseline) {
            ListCellRenderer renderer = this.comboBox.getRenderer();
            if (renderer == null) {
                renderer = new DefaultListCellRenderer();
            }
            Object element = null;
            final Object prototypeDisplayValue = this.comboBox.getPrototypeDisplayValue();
            if (prototypeDisplayValue != null) {
                element = prototypeDisplayValue;
            }
            else if (this.comboBox.getModel().getSize() > 0) {
                element = this.comboBox.getModel().getElementAt(0);
            }
            if (element != null) {
                return renderer.getListCellRendererComponent(this.listBox, element, -1, false, false).getBaselineResizeBehavior();
            }
        }
        return Component.BaselineResizeBehavior.OTHER;
    }
    
    @Override
    public int getAccessibleChildrenCount(final JComponent component) {
        if (this.comboBox.isEditable()) {
            return 2;
        }
        return 1;
    }
    
    @Override
    public Accessible getAccessibleChild(final JComponent component, final int n) {
        switch (n) {
            case 0: {
                if (this.popup instanceof Accessible) {
                    ((Accessible)this.popup).getAccessibleContext().setAccessibleParent(this.comboBox);
                    return (Accessible)this.popup;
                }
                break;
            }
            case 1: {
                if (this.comboBox.isEditable() && this.editor instanceof Accessible) {
                    ((Accessible)this.editor).getAccessibleContext().setAccessibleParent(this.comboBox);
                    return (Accessible)this.editor;
                }
                break;
            }
        }
        return null;
    }
    
    protected boolean isNavigationKey(final int n) {
        return n == 38 || n == 40 || n == 224 || n == 225;
    }
    
    private boolean isNavigationKey(final int n, final int n2) {
        final InputMap inputMap = this.comboBox.getInputMap(1);
        final KeyStroke keyStroke = KeyStroke.getKeyStroke(n, n2);
        return inputMap != null && inputMap.get(keyStroke) != null;
    }
    
    protected void selectNextPossibleValue() {
        int n;
        if (this.comboBox.isPopupVisible()) {
            n = this.listBox.getSelectedIndex();
        }
        else {
            n = this.comboBox.getSelectedIndex();
        }
        if (n < this.comboBox.getModel().getSize() - 1) {
            this.listBox.setSelectedIndex(n + 1);
            this.listBox.ensureIndexIsVisible(n + 1);
            if (!this.isTableCellEditor && (!UIManager.getBoolean("ComboBox.noActionOnKeyNavigation") || !this.comboBox.isPopupVisible())) {
                this.comboBox.setSelectedIndex(n + 1);
            }
            this.comboBox.repaint();
        }
    }
    
    protected void selectPreviousPossibleValue() {
        int n;
        if (this.comboBox.isPopupVisible()) {
            n = this.listBox.getSelectedIndex();
        }
        else {
            n = this.comboBox.getSelectedIndex();
        }
        if (n > 0) {
            this.listBox.setSelectedIndex(n - 1);
            this.listBox.ensureIndexIsVisible(n - 1);
            if (!this.isTableCellEditor && (!UIManager.getBoolean("ComboBox.noActionOnKeyNavigation") || !this.comboBox.isPopupVisible())) {
                this.comboBox.setSelectedIndex(n - 1);
            }
            this.comboBox.repaint();
        }
    }
    
    protected void toggleOpenClose() {
        this.setPopupVisible(this.comboBox, !this.isPopupVisible(this.comboBox));
    }
    
    protected Rectangle rectangleForCurrentValue() {
        final int width = this.comboBox.getWidth();
        final int height = this.comboBox.getHeight();
        final Insets insets = this.getInsets();
        int width2 = height - (insets.top + insets.bottom);
        if (this.arrowButton != null) {
            width2 = this.arrowButton.getWidth();
        }
        if (BasicGraphicsUtils.isLeftToRight(this.comboBox)) {
            return new Rectangle(insets.left, insets.top, width - (insets.left + insets.right + width2), height - (insets.top + insets.bottom));
        }
        return new Rectangle(insets.left + width2, insets.top, width - (insets.left + insets.right + width2), height - (insets.top + insets.bottom));
    }
    
    protected Insets getInsets() {
        return this.comboBox.getInsets();
    }
    
    public void paintCurrentValue(final Graphics graphics, final Rectangle rectangle, final boolean b) {
        final ListCellRenderer renderer = this.comboBox.getRenderer();
        Component component;
        if (b && !this.isPopupVisible(this.comboBox)) {
            component = renderer.getListCellRendererComponent(this.listBox, this.comboBox.getSelectedItem(), -1, true, false);
        }
        else {
            component = renderer.getListCellRendererComponent(this.listBox, this.comboBox.getSelectedItem(), -1, false, false);
            component.setBackground(UIManager.getColor("ComboBox.background"));
        }
        component.setFont(this.comboBox.getFont());
        if (b && !this.isPopupVisible(this.comboBox)) {
            component.setForeground(this.listBox.getSelectionForeground());
            component.setBackground(this.listBox.getSelectionBackground());
        }
        else if (this.comboBox.isEnabled()) {
            component.setForeground(this.comboBox.getForeground());
            component.setBackground(this.comboBox.getBackground());
        }
        else {
            component.setForeground(DefaultLookup.getColor(this.comboBox, this, "ComboBox.disabledForeground", null));
            component.setBackground(DefaultLookup.getColor(this.comboBox, this, "ComboBox.disabledBackground", null));
        }
        boolean b2 = false;
        if (component instanceof JPanel) {
            b2 = true;
        }
        int x = rectangle.x;
        int y = rectangle.y;
        int width = rectangle.width;
        int height = rectangle.height;
        if (this.padding != null) {
            x = rectangle.x + this.padding.left;
            y = rectangle.y + this.padding.top;
            width = rectangle.width - (this.padding.left + this.padding.right);
            height = rectangle.height - (this.padding.top + this.padding.bottom);
        }
        this.currentValuePane.paintComponent(graphics, component, this.comboBox, x, y, width, height, b2);
    }
    
    public void paintCurrentValueBackground(final Graphics graphics, final Rectangle rectangle, final boolean b) {
        final Color color = graphics.getColor();
        if (this.comboBox.isEnabled()) {
            graphics.setColor(DefaultLookup.getColor(this.comboBox, this, "ComboBox.background", null));
        }
        else {
            graphics.setColor(DefaultLookup.getColor(this.comboBox, this, "ComboBox.disabledBackground", null));
        }
        graphics.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        graphics.setColor(color);
    }
    
    void repaintCurrentValue() {
        final Rectangle rectangleForCurrentValue = this.rectangleForCurrentValue();
        this.comboBox.repaint(rectangleForCurrentValue.x, rectangleForCurrentValue.y, rectangleForCurrentValue.width, rectangleForCurrentValue.height);
    }
    
    protected Dimension getDefaultSize() {
        final Dimension sizeForComponent = this.getSizeForComponent(getDefaultListCellRenderer().getListCellRendererComponent(this.listBox, " ", -1, false, false));
        return new Dimension(sizeForComponent.width, sizeForComponent.height);
    }
    
    protected Dimension getDisplaySize() {
        if (!this.isDisplaySizeDirty) {
            return new Dimension(this.cachedDisplaySize);
        }
        Dimension dimension = new Dimension();
        ListCellRenderer renderer = this.comboBox.getRenderer();
        if (renderer == null) {
            renderer = new DefaultListCellRenderer();
        }
        this.sameBaseline = true;
        final Object prototypeDisplayValue = this.comboBox.getPrototypeDisplayValue();
        if (prototypeDisplayValue != null) {
            dimension = this.getSizeForComponent(renderer.getListCellRendererComponent(this.listBox, prototypeDisplayValue, -1, false, false));
        }
        else {
            final ComboBoxModel model = this.comboBox.getModel();
            final int size = model.getSize();
            int n = -1;
            if (size > 0) {
                for (int i = 0; i < size; ++i) {
                    final Object element = model.getElementAt(i);
                    final Component listCellRendererComponent = renderer.getListCellRendererComponent(this.listBox, element, -1, false, false);
                    final Dimension sizeForComponent = this.getSizeForComponent(listCellRendererComponent);
                    if (this.sameBaseline && element != null && (!(element instanceof String) || !"".equals(element))) {
                        final int baseline = listCellRendererComponent.getBaseline(sizeForComponent.width, sizeForComponent.height);
                        if (baseline == -1) {
                            this.sameBaseline = false;
                        }
                        else if (n == -1) {
                            n = baseline;
                        }
                        else if (n != baseline) {
                            this.sameBaseline = false;
                        }
                    }
                    dimension.width = Math.max(dimension.width, sizeForComponent.width);
                    dimension.height = Math.max(dimension.height, sizeForComponent.height);
                }
            }
            else {
                dimension = this.getDefaultSize();
                if (this.comboBox.isEditable()) {
                    dimension.width = 100;
                }
            }
        }
        if (this.comboBox.isEditable()) {
            final Dimension preferredSize = this.editor.getPreferredSize();
            dimension.width = Math.max(dimension.width, preferredSize.width);
            dimension.height = Math.max(dimension.height, preferredSize.height);
        }
        if (this.padding != null) {
            final Dimension dimension2 = dimension;
            dimension2.width += this.padding.left + this.padding.right;
            final Dimension dimension3 = dimension;
            dimension3.height += this.padding.top + this.padding.bottom;
        }
        this.cachedDisplaySize.setSize(dimension.width, dimension.height);
        this.isDisplaySizeDirty = false;
        return dimension;
    }
    
    protected Dimension getSizeForComponent(final Component component) {
        this.currentValuePane.add(component);
        component.setFont(this.comboBox.getFont());
        final Dimension preferredSize = component.getPreferredSize();
        this.currentValuePane.remove(component);
        return preferredSize;
    }
    
    protected void installKeyboardActions() {
        SwingUtilities.replaceUIInputMap(this.comboBox, 1, this.getInputMap(1));
        LazyActionMap.installLazyActionMap(this.comboBox, BasicComboBoxUI.class, "ComboBox.actionMap");
    }
    
    InputMap getInputMap(final int n) {
        if (n == 1) {
            return (InputMap)DefaultLookup.get(this.comboBox, this, "ComboBox.ancestorInputMap");
        }
        return null;
    }
    
    boolean isTableCellEditor() {
        return this.isTableCellEditor;
    }
    
    protected void uninstallKeyboardActions() {
        SwingUtilities.replaceUIInputMap(this.comboBox, 1, null);
        SwingUtilities.replaceUIActionMap(this.comboBox, null);
    }
    
    static {
        COMBO_UI_LIST_CELL_RENDERER_KEY = new StringBuffer("DefaultListCellRendererKey");
        HIDE_POPUP_KEY = new StringBuffer("HidePopupKey");
    }
    
    public class KeyHandler extends KeyAdapter
    {
        @Override
        public void keyPressed(final KeyEvent keyEvent) {
            BasicComboBoxUI.this.getHandler().keyPressed(keyEvent);
        }
    }
    
    public class FocusHandler implements FocusListener
    {
        @Override
        public void focusGained(final FocusEvent focusEvent) {
            BasicComboBoxUI.this.getHandler().focusGained(focusEvent);
        }
        
        @Override
        public void focusLost(final FocusEvent focusEvent) {
            BasicComboBoxUI.this.getHandler().focusLost(focusEvent);
        }
    }
    
    public class ListDataHandler implements ListDataListener
    {
        @Override
        public void contentsChanged(final ListDataEvent listDataEvent) {
            BasicComboBoxUI.this.getHandler().contentsChanged(listDataEvent);
        }
        
        @Override
        public void intervalAdded(final ListDataEvent listDataEvent) {
            BasicComboBoxUI.this.getHandler().intervalAdded(listDataEvent);
        }
        
        @Override
        public void intervalRemoved(final ListDataEvent listDataEvent) {
            BasicComboBoxUI.this.getHandler().intervalRemoved(listDataEvent);
        }
    }
    
    public class ItemHandler implements ItemListener
    {
        @Override
        public void itemStateChanged(final ItemEvent itemEvent) {
        }
    }
    
    public class PropertyChangeHandler implements PropertyChangeListener
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            BasicComboBoxUI.this.getHandler().propertyChange(propertyChangeEvent);
        }
    }
    
    public class ComboBoxLayoutManager implements LayoutManager
    {
        @Override
        public void addLayoutComponent(final String s, final Component component) {
        }
        
        @Override
        public void removeLayoutComponent(final Component component) {
        }
        
        @Override
        public Dimension preferredLayoutSize(final Container container) {
            return BasicComboBoxUI.this.getHandler().preferredLayoutSize(container);
        }
        
        @Override
        public Dimension minimumLayoutSize(final Container container) {
            return BasicComboBoxUI.this.getHandler().minimumLayoutSize(container);
        }
        
        @Override
        public void layoutContainer(final Container container) {
            BasicComboBoxUI.this.getHandler().layoutContainer(container);
        }
    }
    
    private static class Actions extends UIAction
    {
        private static final String HIDE = "hidePopup";
        private static final String DOWN = "selectNext";
        private static final String DOWN_2 = "selectNext2";
        private static final String TOGGLE = "togglePopup";
        private static final String TOGGLE_2 = "spacePopup";
        private static final String UP = "selectPrevious";
        private static final String UP_2 = "selectPrevious2";
        private static final String ENTER = "enterPressed";
        private static final String PAGE_DOWN = "pageDownPassThrough";
        private static final String PAGE_UP = "pageUpPassThrough";
        private static final String HOME = "homePassThrough";
        private static final String END = "endPassThrough";
        
        Actions(final String s) {
            super(s);
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final String name = this.getName();
            final JComboBox comboBox = (JComboBox)actionEvent.getSource();
            final BasicComboBoxUI basicComboBoxUI = (BasicComboBoxUI)BasicLookAndFeel.getUIOfType(comboBox.getUI(), BasicComboBoxUI.class);
            if (name == "hidePopup") {
                comboBox.firePopupMenuCanceled();
                comboBox.setPopupVisible(false);
            }
            else if (name == "pageDownPassThrough" || name == "pageUpPassThrough" || name == "homePassThrough" || name == "endPassThrough") {
                final int nextIndex = this.getNextIndex(comboBox, name);
                if (nextIndex >= 0 && nextIndex < comboBox.getItemCount()) {
                    if (UIManager.getBoolean("ComboBox.noActionOnKeyNavigation") && comboBox.isPopupVisible()) {
                        basicComboBoxUI.listBox.setSelectedIndex(nextIndex);
                        basicComboBoxUI.listBox.ensureIndexIsVisible(nextIndex);
                        comboBox.repaint();
                    }
                    else {
                        comboBox.setSelectedIndex(nextIndex);
                    }
                }
            }
            else if (name == "selectNext") {
                if (comboBox.isShowing()) {
                    if (comboBox.isPopupVisible()) {
                        if (basicComboBoxUI != null) {
                            basicComboBoxUI.selectNextPossibleValue();
                        }
                    }
                    else {
                        comboBox.setPopupVisible(true);
                    }
                }
            }
            else if (name == "selectNext2") {
                if (comboBox.isShowing()) {
                    if ((comboBox.isEditable() || (basicComboBoxUI != null && basicComboBoxUI.isTableCellEditor())) && !comboBox.isPopupVisible()) {
                        comboBox.setPopupVisible(true);
                    }
                    else if (basicComboBoxUI != null) {
                        basicComboBoxUI.selectNextPossibleValue();
                    }
                }
            }
            else if (name == "togglePopup" || name == "spacePopup") {
                if (basicComboBoxUI != null && (name == "togglePopup" || !comboBox.isEditable())) {
                    if (basicComboBoxUI.isTableCellEditor()) {
                        comboBox.setSelectedIndex(basicComboBoxUI.popup.getList().getSelectedIndex());
                    }
                    else {
                        comboBox.setPopupVisible(!comboBox.isPopupVisible());
                    }
                }
            }
            else if (name == "selectPrevious") {
                if (basicComboBoxUI != null) {
                    if (basicComboBoxUI.isPopupVisible(comboBox)) {
                        basicComboBoxUI.selectPreviousPossibleValue();
                    }
                    else if (DefaultLookup.getBoolean(comboBox, basicComboBoxUI, "ComboBox.showPopupOnNavigation", false)) {
                        basicComboBoxUI.setPopupVisible(comboBox, true);
                    }
                }
            }
            else if (name == "selectPrevious2") {
                if (comboBox.isShowing() && basicComboBoxUI != null) {
                    if (comboBox.isEditable() && !comboBox.isPopupVisible()) {
                        comboBox.setPopupVisible(true);
                    }
                    else {
                        basicComboBoxUI.selectPreviousPossibleValue();
                    }
                }
            }
            else if (name == "enterPressed") {
                if (comboBox.isPopupVisible()) {
                    if (UIManager.getBoolean("ComboBox.noActionOnKeyNavigation")) {
                        final Object selectedValue = basicComboBoxUI.popup.getList().getSelectedValue();
                        if (selectedValue != null) {
                            comboBox.getEditor().setItem(selectedValue);
                            comboBox.setSelectedItem(selectedValue);
                        }
                        comboBox.setPopupVisible(false);
                    }
                    else {
                        final boolean boolean1 = UIManager.getBoolean("ComboBox.isEnterSelectablePopup");
                        if (!comboBox.isEditable() || boolean1 || basicComboBoxUI.isTableCellEditor) {
                            final Object selectedValue2 = basicComboBoxUI.popup.getList().getSelectedValue();
                            if (selectedValue2 != null) {
                                comboBox.getEditor().setItem(selectedValue2);
                                comboBox.setSelectedItem(selectedValue2);
                            }
                        }
                        comboBox.setPopupVisible(false);
                    }
                }
                else {
                    if (basicComboBoxUI.isTableCellEditor && !comboBox.isEditable()) {
                        comboBox.setSelectedItem(comboBox.getSelectedItem());
                    }
                    final JRootPane rootPane = SwingUtilities.getRootPane(comboBox);
                    if (rootPane != null) {
                        final InputMap inputMap = rootPane.getInputMap(2);
                        final ActionMap actionMap = rootPane.getActionMap();
                        if (inputMap != null && actionMap != null) {
                            final Object value = inputMap.get(KeyStroke.getKeyStroke(10, 0));
                            if (value != null) {
                                final Action value2 = actionMap.get(value);
                                if (value2 != null) {
                                    value2.actionPerformed(new ActionEvent(rootPane, actionEvent.getID(), actionEvent.getActionCommand(), actionEvent.getWhen(), actionEvent.getModifiers()));
                                }
                            }
                        }
                    }
                }
            }
        }
        
        private int getNextIndex(final JComboBox comboBox, final String s) {
            final int maximumRowCount = comboBox.getMaximumRowCount();
            int n = comboBox.getSelectedIndex();
            if (UIManager.getBoolean("ComboBox.noActionOnKeyNavigation") && comboBox.getUI() instanceof BasicComboBoxUI) {
                n = ((BasicComboBoxUI)comboBox.getUI()).listBox.getSelectedIndex();
            }
            if (s == "pageUpPassThrough") {
                final int n2 = n - maximumRowCount;
                return (n2 < 0) ? 0 : n2;
            }
            if (s == "pageDownPassThrough") {
                final int n3 = n + maximumRowCount;
                final int itemCount = comboBox.getItemCount();
                return (n3 < itemCount) ? n3 : (itemCount - 1);
            }
            if (s == "homePassThrough") {
                return 0;
            }
            if (s == "endPassThrough") {
                return comboBox.getItemCount() - 1;
            }
            return comboBox.getSelectedIndex();
        }
        
        @Override
        public boolean isEnabled(final Object o) {
            return this.getName() != "hidePopup" || (o != null && ((JComboBox)o).isPopupVisible());
        }
    }
    
    private class Handler implements ActionListener, FocusListener, KeyListener, LayoutManager, ListDataListener, PropertyChangeListener
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final String propertyName = propertyChangeEvent.getPropertyName();
            if (propertyChangeEvent.getSource() == BasicComboBoxUI.this.editor) {
                if ("border".equals(propertyName)) {
                    BasicComboBoxUI.this.isMinimumSizeDirty = true;
                    BasicComboBoxUI.this.isDisplaySizeDirty = true;
                    BasicComboBoxUI.this.comboBox.revalidate();
                }
            }
            else {
                final JComboBox comboBox = (JComboBox)propertyChangeEvent.getSource();
                if (propertyName == "model") {
                    final ComboBoxModel comboBoxModel = (ComboBoxModel)propertyChangeEvent.getNewValue();
                    final ComboBoxModel comboBoxModel2 = (ComboBoxModel)propertyChangeEvent.getOldValue();
                    if (comboBoxModel2 != null && BasicComboBoxUI.this.listDataListener != null) {
                        comboBoxModel2.removeListDataListener(BasicComboBoxUI.this.listDataListener);
                    }
                    if (comboBoxModel != null && BasicComboBoxUI.this.listDataListener != null) {
                        comboBoxModel.addListDataListener(BasicComboBoxUI.this.listDataListener);
                    }
                    if (BasicComboBoxUI.this.editor != null) {
                        comboBox.configureEditor(comboBox.getEditor(), comboBox.getSelectedItem());
                    }
                    BasicComboBoxUI.this.isMinimumSizeDirty = true;
                    BasicComboBoxUI.this.isDisplaySizeDirty = true;
                    comboBox.revalidate();
                    comboBox.repaint();
                }
                else if (propertyName == "editor" && comboBox.isEditable()) {
                    BasicComboBoxUI.this.addEditor();
                    comboBox.revalidate();
                }
                else if (propertyName == "editable") {
                    if (comboBox.isEditable()) {
                        comboBox.setRequestFocusEnabled(false);
                        BasicComboBoxUI.this.addEditor();
                    }
                    else {
                        comboBox.setRequestFocusEnabled(true);
                        BasicComboBoxUI.this.removeEditor();
                    }
                    BasicComboBoxUI.this.updateToolTipTextForChildren();
                    comboBox.revalidate();
                }
                else if (propertyName == "enabled") {
                    final boolean enabled = comboBox.isEnabled();
                    if (BasicComboBoxUI.this.editor != null) {
                        BasicComboBoxUI.this.editor.setEnabled(enabled);
                    }
                    if (BasicComboBoxUI.this.arrowButton != null) {
                        BasicComboBoxUI.this.arrowButton.setEnabled(enabled);
                    }
                    comboBox.repaint();
                }
                else if (propertyName == "focusable") {
                    final boolean focusable = comboBox.isFocusable();
                    if (BasicComboBoxUI.this.editor != null) {
                        BasicComboBoxUI.this.editor.setFocusable(focusable);
                    }
                    if (BasicComboBoxUI.this.arrowButton != null) {
                        BasicComboBoxUI.this.arrowButton.setFocusable(focusable);
                    }
                    comboBox.repaint();
                }
                else if (propertyName == "maximumRowCount") {
                    if (BasicComboBoxUI.this.isPopupVisible(comboBox)) {
                        BasicComboBoxUI.this.setPopupVisible(comboBox, false);
                        BasicComboBoxUI.this.setPopupVisible(comboBox, true);
                    }
                }
                else if (propertyName == "font") {
                    BasicComboBoxUI.this.listBox.setFont(comboBox.getFont());
                    if (BasicComboBoxUI.this.editor != null) {
                        BasicComboBoxUI.this.editor.setFont(comboBox.getFont());
                    }
                    BasicComboBoxUI.this.isMinimumSizeDirty = true;
                    BasicComboBoxUI.this.isDisplaySizeDirty = true;
                    comboBox.validate();
                }
                else if (propertyName == "ToolTipText") {
                    BasicComboBoxUI.this.updateToolTipTextForChildren();
                }
                else if (propertyName == "JComboBox.isTableCellEditor") {
                    BasicComboBoxUI.this.isTableCellEditor = ((Boolean)propertyChangeEvent.getNewValue()).equals(Boolean.TRUE);
                }
                else if (propertyName == "prototypeDisplayValue") {
                    BasicComboBoxUI.this.isMinimumSizeDirty = true;
                    BasicComboBoxUI.this.isDisplaySizeDirty = true;
                    comboBox.revalidate();
                }
                else if (propertyName == "renderer") {
                    BasicComboBoxUI.this.isMinimumSizeDirty = true;
                    BasicComboBoxUI.this.isDisplaySizeDirty = true;
                    comboBox.revalidate();
                }
            }
        }
        
        @Override
        public void keyPressed(final KeyEvent keyEvent) {
            if (BasicComboBoxUI.this.isNavigationKey(keyEvent.getKeyCode(), keyEvent.getModifiers())) {
                BasicComboBoxUI.this.lastTime = 0L;
            }
            else if (BasicComboBoxUI.this.comboBox.isEnabled() && BasicComboBoxUI.this.comboBox.getModel().getSize() != 0 && this.isTypeAheadKey(keyEvent) && keyEvent.getKeyChar() != '\uffff') {
                BasicComboBoxUI.this.time = keyEvent.getWhen();
                if (BasicComboBoxUI.this.comboBox.selectWithKeyChar(keyEvent.getKeyChar())) {
                    keyEvent.consume();
                }
            }
        }
        
        @Override
        public void keyTyped(final KeyEvent keyEvent) {
        }
        
        @Override
        public void keyReleased(final KeyEvent keyEvent) {
        }
        
        private boolean isTypeAheadKey(final KeyEvent keyEvent) {
            return !keyEvent.isAltDown() && !BasicGraphicsUtils.isMenuShortcutKeyDown(keyEvent);
        }
        
        @Override
        public void focusGained(final FocusEvent focusEvent) {
            final ComboBoxEditor editor = BasicComboBoxUI.this.comboBox.getEditor();
            if (editor != null && focusEvent.getSource() == editor.getEditorComponent()) {
                return;
            }
            BasicComboBoxUI.this.hasFocus = true;
            BasicComboBoxUI.this.comboBox.repaint();
            if (BasicComboBoxUI.this.comboBox.isEditable() && BasicComboBoxUI.this.editor != null) {
                BasicComboBoxUI.this.editor.requestFocus();
            }
        }
        
        @Override
        public void focusLost(final FocusEvent focusEvent) {
            final ComboBoxEditor editor = BasicComboBoxUI.this.comboBox.getEditor();
            if (editor != null && focusEvent.getSource() == editor.getEditorComponent()) {
                final Object item = editor.getItem();
                final Object selectedItem = BasicComboBoxUI.this.comboBox.getSelectedItem();
                if (!focusEvent.isTemporary() && item != null && !item.equals((selectedItem == null) ? "" : selectedItem)) {
                    BasicComboBoxUI.this.comboBox.actionPerformed(new ActionEvent(editor, 0, "", EventQueue.getMostRecentEventTime(), 0));
                }
            }
            BasicComboBoxUI.this.hasFocus = false;
            if (!focusEvent.isTemporary()) {
                BasicComboBoxUI.this.setPopupVisible(BasicComboBoxUI.this.comboBox, false);
            }
            BasicComboBoxUI.this.comboBox.repaint();
        }
        
        @Override
        public void contentsChanged(final ListDataEvent listDataEvent) {
            if (listDataEvent.getIndex0() != -1 || listDataEvent.getIndex1() != -1) {
                BasicComboBoxUI.this.isMinimumSizeDirty = true;
                BasicComboBoxUI.this.comboBox.revalidate();
            }
            if (BasicComboBoxUI.this.comboBox.isEditable() && BasicComboBoxUI.this.editor != null) {
                BasicComboBoxUI.this.comboBox.configureEditor(BasicComboBoxUI.this.comboBox.getEditor(), BasicComboBoxUI.this.comboBox.getSelectedItem());
            }
            BasicComboBoxUI.this.isDisplaySizeDirty = true;
            BasicComboBoxUI.this.comboBox.repaint();
        }
        
        @Override
        public void intervalAdded(final ListDataEvent listDataEvent) {
            this.contentsChanged(listDataEvent);
        }
        
        @Override
        public void intervalRemoved(final ListDataEvent listDataEvent) {
            this.contentsChanged(listDataEvent);
        }
        
        @Override
        public void addLayoutComponent(final String s, final Component component) {
        }
        
        @Override
        public void removeLayoutComponent(final Component component) {
        }
        
        @Override
        public Dimension preferredLayoutSize(final Container container) {
            return container.getPreferredSize();
        }
        
        @Override
        public Dimension minimumLayoutSize(final Container container) {
            return container.getMinimumSize();
        }
        
        @Override
        public void layoutContainer(final Container container) {
            final JComboBox comboBox = (JComboBox)container;
            final int width = comboBox.getWidth();
            final int height = comboBox.getHeight();
            final Insets insets = BasicComboBoxUI.this.getInsets();
            int n2;
            final int n = n2 = height - (insets.top + insets.bottom);
            if (BasicComboBoxUI.this.arrowButton != null) {
                final Insets insets2 = BasicComboBoxUI.this.arrowButton.getInsets();
                n2 = (BasicComboBoxUI.this.squareButton ? n : (BasicComboBoxUI.this.arrowButton.getPreferredSize().width + insets2.left + insets2.right));
            }
            if (BasicComboBoxUI.this.arrowButton != null) {
                if (BasicGraphicsUtils.isLeftToRight(comboBox)) {
                    BasicComboBoxUI.this.arrowButton.setBounds(width - (insets.right + n2), insets.top, n2, n);
                }
                else {
                    BasicComboBoxUI.this.arrowButton.setBounds(insets.left, insets.top, n2, n);
                }
            }
            if (BasicComboBoxUI.this.editor != null) {
                BasicComboBoxUI.this.editor.setBounds(BasicComboBoxUI.this.rectangleForCurrentValue());
            }
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final Object item = BasicComboBoxUI.this.comboBox.getEditor().getItem();
            if (item != null) {
                if (!BasicComboBoxUI.this.comboBox.isPopupVisible() && !item.equals(BasicComboBoxUI.this.comboBox.getSelectedItem())) {
                    BasicComboBoxUI.this.comboBox.setSelectedItem(BasicComboBoxUI.this.comboBox.getEditor().getItem());
                }
                final ActionMap actionMap = BasicComboBoxUI.this.comboBox.getActionMap();
                if (actionMap != null) {
                    final Action value = actionMap.get("enterPressed");
                    if (value != null) {
                        value.actionPerformed(new ActionEvent(BasicComboBoxUI.this.comboBox, actionEvent.getID(), actionEvent.getActionCommand(), actionEvent.getModifiers()));
                    }
                }
            }
        }
    }
    
    class DefaultKeySelectionManager implements JComboBox.KeySelectionManager, UIResource
    {
        private String prefix;
        private String typedString;
        
        DefaultKeySelectionManager() {
            this.prefix = "";
            this.typedString = "";
        }
        
        @Override
        public int selectionForKey(final char c, final ComboBoxModel comboBoxModel) {
            if (BasicComboBoxUI.this.lastTime == 0L) {
                this.prefix = "";
                this.typedString = "";
            }
            boolean b = true;
            int selectedIndex = BasicComboBoxUI.this.comboBox.getSelectedIndex();
            if (BasicComboBoxUI.this.time - BasicComboBoxUI.this.lastTime < BasicComboBoxUI.this.timeFactor) {
                this.typedString += c;
                if (this.prefix.length() == 1 && c == this.prefix.charAt(0)) {
                    ++selectedIndex;
                }
                else {
                    this.prefix = this.typedString;
                }
            }
            else {
                ++selectedIndex;
                this.typedString = "" + c;
                this.prefix = this.typedString;
            }
            BasicComboBoxUI.this.lastTime = BasicComboBoxUI.this.time;
            if (selectedIndex < 0 || selectedIndex >= comboBoxModel.getSize()) {
                b = false;
                selectedIndex = 0;
            }
            int n = BasicComboBoxUI.this.listBox.getNextMatch(this.prefix, selectedIndex, Position.Bias.Forward);
            if (n < 0 && b) {
                n = BasicComboBoxUI.this.listBox.getNextMatch(this.prefix, 0, Position.Bias.Forward);
            }
            return n;
        }
    }
}
