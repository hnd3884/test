package javax.swing.plaf.basic;

import java.awt.event.MouseWheelEvent;
import java.awt.ComponentOrientation;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.awt.event.ItemEvent;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseAdapter;
import java.io.Serializable;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.Insets;
import javax.swing.ListCellRenderer;
import javax.swing.JComponent;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;
import java.awt.LayoutManager;
import java.awt.Container;
import javax.swing.BoxLayout;
import javax.swing.JScrollBar;
import javax.swing.UIManager;
import sun.awt.AWTAccessor;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import javax.swing.ComboBoxModel;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import java.awt.Point;
import java.awt.Component;
import javax.swing.Timer;
import java.awt.event.MouseWheelListener;
import java.awt.event.ItemListener;
import javax.swing.event.ListDataListener;
import java.beans.PropertyChangeListener;
import javax.swing.event.ListSelectionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JComboBox;
import javax.swing.border.Border;
import javax.swing.ListModel;
import javax.swing.JPopupMenu;

public class BasicComboPopup extends JPopupMenu implements ComboPopup
{
    static final ListModel EmptyListModel;
    private static Border LIST_BORDER;
    protected JComboBox comboBox;
    protected JList list;
    protected JScrollPane scroller;
    protected boolean valueIsAdjusting;
    private Handler handler;
    protected MouseMotionListener mouseMotionListener;
    protected MouseListener mouseListener;
    protected KeyListener keyListener;
    protected ListSelectionListener listSelectionListener;
    protected MouseListener listMouseListener;
    protected MouseMotionListener listMouseMotionListener;
    protected PropertyChangeListener propertyChangeListener;
    protected ListDataListener listDataListener;
    protected ItemListener itemListener;
    private MouseWheelListener scrollerMouseWheelListener;
    protected Timer autoscrollTimer;
    protected boolean hasEntered;
    protected boolean isAutoScrolling;
    protected int scrollDirection;
    protected static final int SCROLL_UP = 0;
    protected static final int SCROLL_DOWN = 1;
    
    @Override
    public void show() {
        this.comboBox.firePopupMenuWillBecomeVisible();
        this.setListSelection(this.comboBox.getSelectedIndex());
        final Point popupLocation = this.getPopupLocation();
        this.show(this.comboBox, popupLocation.x, popupLocation.y);
    }
    
    @Override
    public void hide() {
        final MenuSelectionManager defaultManager = MenuSelectionManager.defaultManager();
        final MenuElement[] selectedPath = defaultManager.getSelectedPath();
        for (int i = 0; i < selectedPath.length; ++i) {
            if (selectedPath[i] == this) {
                defaultManager.clearSelectedPath();
                break;
            }
        }
        if (selectedPath.length > 0) {
            this.comboBox.repaint();
        }
    }
    
    @Override
    public JList getList() {
        return this.list;
    }
    
    @Override
    public MouseListener getMouseListener() {
        if (this.mouseListener == null) {
            this.mouseListener = this.createMouseListener();
        }
        return this.mouseListener;
    }
    
    @Override
    public MouseMotionListener getMouseMotionListener() {
        if (this.mouseMotionListener == null) {
            this.mouseMotionListener = this.createMouseMotionListener();
        }
        return this.mouseMotionListener;
    }
    
    @Override
    public KeyListener getKeyListener() {
        if (this.keyListener == null) {
            this.keyListener = this.createKeyListener();
        }
        return this.keyListener;
    }
    
    @Override
    public void uninstallingUI() {
        if (this.propertyChangeListener != null) {
            this.comboBox.removePropertyChangeListener(this.propertyChangeListener);
        }
        if (this.itemListener != null) {
            this.comboBox.removeItemListener(this.itemListener);
        }
        this.uninstallComboBoxModelListeners(this.comboBox.getModel());
        this.uninstallKeyboardActions();
        this.uninstallListListeners();
        this.uninstallScrollerListeners();
        this.list.setModel(BasicComboPopup.EmptyListModel);
    }
    
    protected void uninstallComboBoxModelListeners(final ComboBoxModel comboBoxModel) {
        if (comboBoxModel != null && this.listDataListener != null) {
            comboBoxModel.removeListDataListener(this.listDataListener);
        }
    }
    
    protected void uninstallKeyboardActions() {
    }
    
    public BasicComboPopup(final JComboBox comboBox) {
        this.valueIsAdjusting = false;
        this.hasEntered = false;
        this.isAutoScrolling = false;
        this.scrollDirection = 0;
        this.setName("ComboPopup.popup");
        this.comboBox = comboBox;
        this.setLightWeightPopupEnabled(this.comboBox.isLightWeightPopupEnabled());
        (this.list = this.createList()).setName("ComboBox.list");
        this.configureList();
        (this.scroller = this.createScroller()).setName("ComboBox.scrollPane");
        this.configureScroller();
        this.configurePopup();
        this.installComboBoxListeners();
        this.installKeyboardActions();
    }
    
    @Override
    protected void firePopupMenuWillBecomeVisible() {
        if (this.scrollerMouseWheelListener != null) {
            this.comboBox.addMouseWheelListener(this.scrollerMouseWheelListener);
        }
        super.firePopupMenuWillBecomeVisible();
    }
    
    @Override
    protected void firePopupMenuWillBecomeInvisible() {
        if (this.scrollerMouseWheelListener != null) {
            this.comboBox.removeMouseWheelListener(this.scrollerMouseWheelListener);
        }
        super.firePopupMenuWillBecomeInvisible();
        this.comboBox.firePopupMenuWillBecomeInvisible();
    }
    
    @Override
    protected void firePopupMenuCanceled() {
        if (this.scrollerMouseWheelListener != null) {
            this.comboBox.removeMouseWheelListener(this.scrollerMouseWheelListener);
        }
        super.firePopupMenuCanceled();
        this.comboBox.firePopupMenuCanceled();
    }
    
    protected MouseListener createMouseListener() {
        return this.getHandler();
    }
    
    protected MouseMotionListener createMouseMotionListener() {
        return this.getHandler();
    }
    
    protected KeyListener createKeyListener() {
        return null;
    }
    
    protected ListSelectionListener createListSelectionListener() {
        return null;
    }
    
    protected ListDataListener createListDataListener() {
        return null;
    }
    
    protected MouseListener createListMouseListener() {
        return this.getHandler();
    }
    
    protected MouseMotionListener createListMouseMotionListener() {
        return this.getHandler();
    }
    
    protected PropertyChangeListener createPropertyChangeListener() {
        return this.getHandler();
    }
    
    protected ItemListener createItemListener() {
        return this.getHandler();
    }
    
    private Handler getHandler() {
        if (this.handler == null) {
            this.handler = new Handler();
        }
        return this.handler;
    }
    
    protected JList createList() {
        return new JList(this.comboBox.getModel()) {
            public void processMouseEvent(MouseEvent mouseEvent) {
                if (BasicGraphicsUtils.isMenuShortcutKeyDown(mouseEvent)) {
                    final MouseEvent mouseEvent2 = new MouseEvent((Component)mouseEvent.getSource(), mouseEvent.getID(), mouseEvent.getWhen(), mouseEvent.getModifiers() ^ Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), mouseEvent.getX(), mouseEvent.getY(), mouseEvent.getXOnScreen(), mouseEvent.getYOnScreen(), mouseEvent.getClickCount(), mouseEvent.isPopupTrigger(), 0);
                    final AWTAccessor.MouseEventAccessor mouseEventAccessor = AWTAccessor.getMouseEventAccessor();
                    mouseEventAccessor.setCausedByTouchEvent(mouseEvent2, mouseEventAccessor.isCausedByTouchEvent(mouseEvent));
                    mouseEvent = mouseEvent2;
                }
                super.processMouseEvent(mouseEvent);
            }
        };
    }
    
    protected void configureList() {
        this.list.setFont(this.comboBox.getFont());
        this.list.setForeground(this.comboBox.getForeground());
        this.list.setBackground(this.comboBox.getBackground());
        this.list.setSelectionForeground(UIManager.getColor("ComboBox.selectionForeground"));
        this.list.setSelectionBackground(UIManager.getColor("ComboBox.selectionBackground"));
        this.list.setBorder(null);
        this.list.setCellRenderer(this.comboBox.getRenderer());
        this.list.setFocusable(false);
        this.list.setSelectionMode(0);
        this.setListSelection(this.comboBox.getSelectedIndex());
        this.installListListeners();
    }
    
    protected void installListListeners() {
        final MouseListener listMouseListener = this.createListMouseListener();
        this.listMouseListener = listMouseListener;
        if (listMouseListener != null) {
            this.list.addMouseListener(this.listMouseListener);
        }
        if ((this.listMouseMotionListener = this.createListMouseMotionListener()) != null) {
            this.list.addMouseMotionListener(this.listMouseMotionListener);
        }
        if ((this.listSelectionListener = this.createListSelectionListener()) != null) {
            this.list.addListSelectionListener(this.listSelectionListener);
        }
    }
    
    void uninstallListListeners() {
        if (this.listMouseListener != null) {
            this.list.removeMouseListener(this.listMouseListener);
            this.listMouseListener = null;
        }
        if (this.listMouseMotionListener != null) {
            this.list.removeMouseMotionListener(this.listMouseMotionListener);
            this.listMouseMotionListener = null;
        }
        if (this.listSelectionListener != null) {
            this.list.removeListSelectionListener(this.listSelectionListener);
            this.listSelectionListener = null;
        }
        this.handler = null;
    }
    
    protected JScrollPane createScroller() {
        final JScrollPane scrollPane = new JScrollPane(this.list, 20, 31);
        scrollPane.setHorizontalScrollBar(null);
        return scrollPane;
    }
    
    protected void configureScroller() {
        this.scroller.setFocusable(false);
        this.scroller.getVerticalScrollBar().setFocusable(false);
        this.scroller.setBorder(null);
        this.installScrollerListeners();
    }
    
    protected void configurePopup() {
        this.setLayout(new BoxLayout(this, 1));
        this.setBorderPainted(true);
        this.setBorder(BasicComboPopup.LIST_BORDER);
        this.setOpaque(false);
        this.add(this.scroller);
        this.setDoubleBuffered(true);
        this.setFocusable(false);
    }
    
    private void installScrollerListeners() {
        this.scrollerMouseWheelListener = this.getHandler();
        if (this.scrollerMouseWheelListener != null) {
            this.scroller.addMouseWheelListener(this.scrollerMouseWheelListener);
        }
    }
    
    private void uninstallScrollerListeners() {
        if (this.scrollerMouseWheelListener != null) {
            this.scroller.removeMouseWheelListener(this.scrollerMouseWheelListener);
            this.scrollerMouseWheelListener = null;
        }
    }
    
    protected void installComboBoxListeners() {
        final PropertyChangeListener propertyChangeListener = this.createPropertyChangeListener();
        this.propertyChangeListener = propertyChangeListener;
        if (propertyChangeListener != null) {
            this.comboBox.addPropertyChangeListener(this.propertyChangeListener);
        }
        if ((this.itemListener = this.createItemListener()) != null) {
            this.comboBox.addItemListener(this.itemListener);
        }
        this.installComboBoxModelListeners(this.comboBox.getModel());
    }
    
    protected void installComboBoxModelListeners(final ComboBoxModel comboBoxModel) {
        if (comboBoxModel != null && (this.listDataListener = this.createListDataListener()) != null) {
            comboBoxModel.addListDataListener(this.listDataListener);
        }
    }
    
    protected void installKeyboardActions() {
    }
    
    @Override
    public boolean isFocusTraversable() {
        return false;
    }
    
    protected void startAutoScrolling(final int n) {
        if (this.isAutoScrolling) {
            this.autoscrollTimer.stop();
        }
        this.isAutoScrolling = true;
        if (n == 0) {
            this.scrollDirection = 0;
            this.list.setSelectedIndex(this.list.locationToIndex(SwingUtilities.convertPoint(this.scroller, new Point(1, 1), this.list)));
            this.autoscrollTimer = new Timer(100, new AutoScrollActionHandler(0));
        }
        else if (n == 1) {
            this.scrollDirection = 1;
            this.list.setSelectedIndex(this.list.locationToIndex(SwingUtilities.convertPoint(this.scroller, new Point(1, this.scroller.getSize().height - 1 - 2), this.list)));
            this.autoscrollTimer = new Timer(100, new AutoScrollActionHandler(1));
        }
        this.autoscrollTimer.start();
    }
    
    protected void stopAutoScrolling() {
        this.isAutoScrolling = false;
        if (this.autoscrollTimer != null) {
            this.autoscrollTimer.stop();
            this.autoscrollTimer = null;
        }
    }
    
    protected void autoScrollUp() {
        final int selectedIndex = this.list.getSelectedIndex();
        if (selectedIndex > 0) {
            this.list.setSelectedIndex(selectedIndex - 1);
            this.list.ensureIndexIsVisible(selectedIndex - 1);
        }
    }
    
    protected void autoScrollDown() {
        final int selectedIndex = this.list.getSelectedIndex();
        if (selectedIndex < this.list.getModel().getSize() - 1) {
            this.list.setSelectedIndex(selectedIndex + 1);
            this.list.ensureIndexIsVisible(selectedIndex + 1);
        }
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        final AccessibleContext accessibleContext = super.getAccessibleContext();
        accessibleContext.setAccessibleParent(this.comboBox);
        return accessibleContext;
    }
    
    protected void delegateFocus(final MouseEvent mouseEvent) {
        if (this.comboBox.isEditable()) {
            final Component editorComponent = this.comboBox.getEditor().getEditorComponent();
            if (!(editorComponent instanceof JComponent) || ((JComponent)editorComponent).isRequestFocusEnabled()) {
                editorComponent.requestFocus();
            }
        }
        else if (this.comboBox.isRequestFocusEnabled()) {
            this.comboBox.requestFocus();
        }
    }
    
    protected void togglePopup() {
        if (this.isVisible()) {
            this.hide();
        }
        else {
            this.show();
        }
    }
    
    private void setListSelection(final int selectedIndex) {
        if (selectedIndex == -1) {
            this.list.clearSelection();
        }
        else {
            this.list.setSelectedIndex(selectedIndex);
            this.list.ensureIndexIsVisible(selectedIndex);
        }
    }
    
    protected MouseEvent convertMouseEvent(final MouseEvent mouseEvent) {
        final Point convertPoint = SwingUtilities.convertPoint((Component)mouseEvent.getSource(), mouseEvent.getPoint(), this.list);
        final MouseEvent mouseEvent2 = new MouseEvent((Component)mouseEvent.getSource(), mouseEvent.getID(), mouseEvent.getWhen(), mouseEvent.getModifiers(), convertPoint.x, convertPoint.y, mouseEvent.getXOnScreen(), mouseEvent.getYOnScreen(), mouseEvent.getClickCount(), mouseEvent.isPopupTrigger(), 0);
        final AWTAccessor.MouseEventAccessor mouseEventAccessor = AWTAccessor.getMouseEventAccessor();
        mouseEventAccessor.setCausedByTouchEvent(mouseEvent2, mouseEventAccessor.isCausedByTouchEvent(mouseEvent));
        return mouseEvent2;
    }
    
    protected int getPopupHeightForRowCount(final int n) {
        final int min = Math.min(n, this.comboBox.getItemCount());
        int height = 0;
        final ListCellRenderer cellRenderer = this.list.getCellRenderer();
        for (int i = 0; i < min; ++i) {
            height += cellRenderer.getListCellRendererComponent(this.list, this.list.getModel().getElementAt(i), i, false, false).getPreferredSize().height;
        }
        if (height == 0) {
            height = this.comboBox.getHeight();
        }
        final Border viewportBorder = this.scroller.getViewportBorder();
        if (viewportBorder != null) {
            final Insets borderInsets = viewportBorder.getBorderInsets(null);
            height += borderInsets.top + borderInsets.bottom;
        }
        final Border border = this.scroller.getBorder();
        if (border != null) {
            final Insets borderInsets2 = border.getBorderInsets(null);
            height += borderInsets2.top + borderInsets2.bottom;
        }
        return height;
    }
    
    protected Rectangle computePopupBounds(final int n, final int n2, final int n3, final int n4) {
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        final GraphicsConfiguration graphicsConfiguration = this.comboBox.getGraphicsConfiguration();
        final Point point = new Point();
        SwingUtilities.convertPointFromScreen(point, this.comboBox);
        Rectangle rectangle;
        if (graphicsConfiguration != null) {
            final Insets screenInsets = defaultToolkit.getScreenInsets(graphicsConfiguration);
            final Rectangle bounds;
            rectangle = (bounds = graphicsConfiguration.getBounds());
            bounds.width -= screenInsets.left + screenInsets.right;
            final Rectangle rectangle2 = rectangle;
            rectangle2.height -= screenInsets.top + screenInsets.bottom;
            final Rectangle rectangle3 = rectangle;
            rectangle3.x += point.x + screenInsets.left;
            final Rectangle rectangle4 = rectangle;
            rectangle4.y += point.y + screenInsets.top;
        }
        else {
            rectangle = new Rectangle(point, defaultToolkit.getScreenSize());
        }
        final Rectangle rectangle5 = new Rectangle(n, n2, n3, n4);
        if (n2 + n4 > rectangle.y + rectangle.height && n4 < rectangle.height) {
            rectangle5.y = -rectangle5.height;
        }
        return rectangle5;
    }
    
    private Point getPopupLocation() {
        final Dimension size = this.comboBox.getSize();
        final Insets insets = this.getInsets();
        size.setSize(size.width - (insets.right + insets.left), this.getPopupHeightForRowCount(this.comboBox.getMaximumRowCount()));
        final Rectangle computePopupBounds = this.computePopupBounds(0, this.comboBox.getBounds().height, size.width, size.height);
        final Dimension size2 = computePopupBounds.getSize();
        final Point location = computePopupBounds.getLocation();
        this.scroller.setMaximumSize(size2);
        this.scroller.setPreferredSize(size2);
        this.scroller.setMinimumSize(size2);
        this.list.revalidate();
        return location;
    }
    
    protected void updateListBoxSelectionForEvent(final MouseEvent mouseEvent, final boolean b) {
        final Point point = mouseEvent.getPoint();
        if (this.list == null) {
            return;
        }
        int locationToIndex = this.list.locationToIndex(point);
        if (locationToIndex == -1) {
            if (point.y < 0) {
                locationToIndex = 0;
            }
            else {
                locationToIndex = this.comboBox.getModel().getSize() - 1;
            }
        }
        if (this.list.getSelectedIndex() != locationToIndex) {
            this.list.setSelectedIndex(locationToIndex);
            if (b) {
                this.list.ensureIndexIsVisible(locationToIndex);
            }
        }
    }
    
    static {
        EmptyListModel = new EmptyListModelClass();
        BasicComboPopup.LIST_BORDER = new LineBorder(Color.BLACK, 1);
    }
    
    private static class EmptyListModelClass implements ListModel<Object>, Serializable
    {
        @Override
        public int getSize() {
            return 0;
        }
        
        @Override
        public Object getElementAt(final int n) {
            return null;
        }
        
        @Override
        public void addListDataListener(final ListDataListener listDataListener) {
        }
        
        @Override
        public void removeListDataListener(final ListDataListener listDataListener) {
        }
    }
    
    protected class InvocationMouseHandler extends MouseAdapter
    {
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            BasicComboPopup.this.getHandler().mousePressed(mouseEvent);
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
            BasicComboPopup.this.getHandler().mouseReleased(mouseEvent);
        }
    }
    
    protected class InvocationMouseMotionHandler extends MouseMotionAdapter
    {
        @Override
        public void mouseDragged(final MouseEvent mouseEvent) {
            BasicComboPopup.this.getHandler().mouseDragged(mouseEvent);
        }
    }
    
    public class InvocationKeyHandler extends KeyAdapter
    {
        @Override
        public void keyReleased(final KeyEvent keyEvent) {
        }
    }
    
    protected class ListSelectionHandler implements ListSelectionListener
    {
        @Override
        public void valueChanged(final ListSelectionEvent listSelectionEvent) {
        }
    }
    
    public class ListDataHandler implements ListDataListener
    {
        @Override
        public void contentsChanged(final ListDataEvent listDataEvent) {
        }
        
        @Override
        public void intervalAdded(final ListDataEvent listDataEvent) {
        }
        
        @Override
        public void intervalRemoved(final ListDataEvent listDataEvent) {
        }
    }
    
    protected class ListMouseHandler extends MouseAdapter
    {
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
            BasicComboPopup.this.getHandler().mouseReleased(mouseEvent);
        }
    }
    
    protected class ListMouseMotionHandler extends MouseMotionAdapter
    {
        @Override
        public void mouseMoved(final MouseEvent mouseEvent) {
            BasicComboPopup.this.getHandler().mouseMoved(mouseEvent);
        }
    }
    
    protected class ItemHandler implements ItemListener
    {
        @Override
        public void itemStateChanged(final ItemEvent itemEvent) {
            BasicComboPopup.this.getHandler().itemStateChanged(itemEvent);
        }
    }
    
    protected class PropertyChangeHandler implements PropertyChangeListener
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            BasicComboPopup.this.getHandler().propertyChange(propertyChangeEvent);
        }
    }
    
    private class AutoScrollActionHandler implements ActionListener
    {
        private int direction;
        
        AutoScrollActionHandler(final int direction) {
            this.direction = direction;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (this.direction == 0) {
                BasicComboPopup.this.autoScrollUp();
            }
            else {
                BasicComboPopup.this.autoScrollDown();
            }
        }
    }
    
    private class Handler implements ItemListener, MouseListener, MouseMotionListener, MouseWheelListener, PropertyChangeListener, Serializable
    {
        @Override
        public void mouseClicked(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            if (mouseEvent.getSource() == BasicComboPopup.this.list) {
                return;
            }
            if (!SwingUtilities.isLeftMouseButton(mouseEvent) || !BasicComboPopup.this.comboBox.isEnabled()) {
                return;
            }
            if (BasicComboPopup.this.comboBox.isEditable()) {
                final Component editorComponent = BasicComboPopup.this.comboBox.getEditor().getEditorComponent();
                if (!(editorComponent instanceof JComponent) || ((JComponent)editorComponent).isRequestFocusEnabled()) {
                    editorComponent.requestFocus();
                }
            }
            else if (BasicComboPopup.this.comboBox.isRequestFocusEnabled()) {
                BasicComboPopup.this.comboBox.requestFocus();
            }
            BasicComboPopup.this.togglePopup();
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
            if (mouseEvent.getSource() == BasicComboPopup.this.list) {
                if (BasicComboPopup.this.list.getModel().getSize() > 0) {
                    if (BasicComboPopup.this.comboBox.getSelectedIndex() == BasicComboPopup.this.list.getSelectedIndex()) {
                        BasicComboPopup.this.comboBox.getEditor().setItem(BasicComboPopup.this.list.getSelectedValue());
                    }
                    BasicComboPopup.this.comboBox.setSelectedIndex(BasicComboPopup.this.list.getSelectedIndex());
                }
                BasicComboPopup.this.comboBox.setPopupVisible(false);
                if (BasicComboPopup.this.comboBox.isEditable() && BasicComboPopup.this.comboBox.getEditor() != null) {
                    BasicComboPopup.this.comboBox.configureEditor(BasicComboPopup.this.comboBox.getEditor(), BasicComboPopup.this.comboBox.getSelectedItem());
                }
                return;
            }
            final Dimension size = ((Component)mouseEvent.getSource()).getSize();
            if (!new Rectangle(0, 0, size.width - 1, size.height - 1).contains(mouseEvent.getPoint())) {
                final Point point = BasicComboPopup.this.convertMouseEvent(mouseEvent).getPoint();
                final Rectangle rectangle = new Rectangle();
                BasicComboPopup.this.list.computeVisibleRect(rectangle);
                if (rectangle.contains(point)) {
                    if (BasicComboPopup.this.comboBox.getSelectedIndex() == BasicComboPopup.this.list.getSelectedIndex()) {
                        BasicComboPopup.this.comboBox.getEditor().setItem(BasicComboPopup.this.list.getSelectedValue());
                    }
                    BasicComboPopup.this.comboBox.setSelectedIndex(BasicComboPopup.this.list.getSelectedIndex());
                }
                BasicComboPopup.this.comboBox.setPopupVisible(false);
            }
            BasicComboPopup.this.hasEntered = false;
            BasicComboPopup.this.stopAutoScrolling();
        }
        
        @Override
        public void mouseEntered(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseExited(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseMoved(final MouseEvent mouseEvent) {
            if (mouseEvent.getSource() == BasicComboPopup.this.list) {
                final Point point = mouseEvent.getPoint();
                final Rectangle rectangle = new Rectangle();
                BasicComboPopup.this.list.computeVisibleRect(rectangle);
                if (rectangle.contains(point)) {
                    BasicComboPopup.this.updateListBoxSelectionForEvent(mouseEvent, false);
                }
            }
        }
        
        @Override
        public void mouseDragged(final MouseEvent mouseEvent) {
            if (mouseEvent.getSource() == BasicComboPopup.this.list) {
                return;
            }
            if (BasicComboPopup.this.isVisible()) {
                final MouseEvent convertMouseEvent = BasicComboPopup.this.convertMouseEvent(mouseEvent);
                final Rectangle rectangle = new Rectangle();
                BasicComboPopup.this.list.computeVisibleRect(rectangle);
                if (convertMouseEvent.getPoint().y >= rectangle.y && convertMouseEvent.getPoint().y <= rectangle.y + rectangle.height - 1) {
                    BasicComboPopup.this.hasEntered = true;
                    if (BasicComboPopup.this.isAutoScrolling) {
                        BasicComboPopup.this.stopAutoScrolling();
                    }
                    if (rectangle.contains(convertMouseEvent.getPoint())) {
                        BasicComboPopup.this.updateListBoxSelectionForEvent(convertMouseEvent, false);
                    }
                }
                else if (BasicComboPopup.this.hasEntered) {
                    final int n = (convertMouseEvent.getPoint().y >= rectangle.y) ? 1 : 0;
                    if (BasicComboPopup.this.isAutoScrolling && BasicComboPopup.this.scrollDirection != n) {
                        BasicComboPopup.this.stopAutoScrolling();
                        BasicComboPopup.this.startAutoScrolling(n);
                    }
                    else if (!BasicComboPopup.this.isAutoScrolling) {
                        BasicComboPopup.this.startAutoScrolling(n);
                    }
                }
                else if (mouseEvent.getPoint().y < 0) {
                    BasicComboPopup.this.hasEntered = true;
                    BasicComboPopup.this.startAutoScrolling(0);
                }
            }
        }
        
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final JComboBox comboBox = (JComboBox)propertyChangeEvent.getSource();
            final String propertyName = propertyChangeEvent.getPropertyName();
            if (propertyName == "model") {
                final ComboBoxModel comboBoxModel = (ComboBoxModel)propertyChangeEvent.getOldValue();
                final ComboBoxModel model = (ComboBoxModel)propertyChangeEvent.getNewValue();
                BasicComboPopup.this.uninstallComboBoxModelListeners(comboBoxModel);
                BasicComboPopup.this.installComboBoxModelListeners(model);
                BasicComboPopup.this.list.setModel(model);
                if (BasicComboPopup.this.isVisible()) {
                    BasicComboPopup.this.hide();
                }
            }
            else if (propertyName == "renderer") {
                BasicComboPopup.this.list.setCellRenderer(comboBox.getRenderer());
                if (BasicComboPopup.this.isVisible()) {
                    BasicComboPopup.this.hide();
                }
            }
            else if (propertyName == "componentOrientation") {
                final ComponentOrientation componentOrientation = (ComponentOrientation)propertyChangeEvent.getNewValue();
                final JList list = BasicComboPopup.this.getList();
                if (list != null && list.getComponentOrientation() != componentOrientation) {
                    list.setComponentOrientation(componentOrientation);
                }
                if (BasicComboPopup.this.scroller != null && BasicComboPopup.this.scroller.getComponentOrientation() != componentOrientation) {
                    BasicComboPopup.this.scroller.setComponentOrientation(componentOrientation);
                }
                if (componentOrientation != BasicComboPopup.this.getComponentOrientation()) {
                    BasicComboPopup.this.setComponentOrientation(componentOrientation);
                }
            }
            else if (propertyName == "lightWeightPopupEnabled") {
                BasicComboPopup.this.setLightWeightPopupEnabled(comboBox.isLightWeightPopupEnabled());
            }
        }
        
        @Override
        public void itemStateChanged(final ItemEvent itemEvent) {
            if (itemEvent.getStateChange() == 1) {
                BasicComboPopup.this.setListSelection(((JComboBox)itemEvent.getSource()).getSelectedIndex());
            }
            else {
                BasicComboPopup.this.setListSelection(-1);
            }
        }
        
        @Override
        public void mouseWheelMoved(final MouseWheelEvent mouseWheelEvent) {
            mouseWheelEvent.consume();
        }
    }
}
