package javax.swing;

import java.awt.Dimension;
import javax.swing.plaf.DesktopIconUI;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleValue;
import java.beans.PropertyChangeEvent;
import javax.accessibility.AccessibleContext;
import java.awt.Graphics;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import sun.swing.SwingUtilities2;
import java.awt.FocusTraversalPolicy;
import java.beans.PropertyVetoException;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.InternalFrameUI;
import java.awt.Container;
import sun.awt.SunToolkit;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.Window;
import java.beans.PropertyChangeListener;
import java.awt.KeyboardFocusManager;
import sun.awt.AppContext;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Cursor;
import javax.accessibility.Accessible;

public class JInternalFrame extends JComponent implements Accessible, WindowConstants, RootPaneContainer
{
    private static final String uiClassID = "InternalFrameUI";
    protected JRootPane rootPane;
    protected boolean rootPaneCheckingEnabled;
    protected boolean closable;
    protected boolean isClosed;
    protected boolean maximizable;
    protected boolean isMaximum;
    protected boolean iconable;
    protected boolean isIcon;
    protected boolean resizable;
    protected boolean isSelected;
    protected Icon frameIcon;
    protected String title;
    protected JDesktopIcon desktopIcon;
    private Cursor lastCursor;
    private boolean opened;
    private Rectangle normalBounds;
    private int defaultCloseOperation;
    private Component lastFocusOwner;
    public static final String CONTENT_PANE_PROPERTY = "contentPane";
    public static final String MENU_BAR_PROPERTY = "JMenuBar";
    public static final String TITLE_PROPERTY = "title";
    public static final String LAYERED_PANE_PROPERTY = "layeredPane";
    public static final String ROOT_PANE_PROPERTY = "rootPane";
    public static final String GLASS_PANE_PROPERTY = "glassPane";
    public static final String FRAME_ICON_PROPERTY = "frameIcon";
    public static final String IS_SELECTED_PROPERTY = "selected";
    public static final String IS_CLOSED_PROPERTY = "closed";
    public static final String IS_MAXIMUM_PROPERTY = "maximum";
    public static final String IS_ICON_PROPERTY = "icon";
    private static final Object PROPERTY_CHANGE_LISTENER_KEY;
    boolean isDragging;
    boolean danger;
    
    private static void addPropertyChangeListenerIfNecessary() {
        if (AppContext.getAppContext().get(JInternalFrame.PROPERTY_CHANGE_LISTENER_KEY) == null) {
            final FocusPropertyChangeListener focusPropertyChangeListener = new FocusPropertyChangeListener();
            AppContext.getAppContext().put(JInternalFrame.PROPERTY_CHANGE_LISTENER_KEY, focusPropertyChangeListener);
            KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(focusPropertyChangeListener);
        }
    }
    
    private static void updateLastFocusOwner(final Component lastFocusOwner) {
        if (lastFocusOwner != null) {
            for (Component parent = lastFocusOwner; parent != null && !(parent instanceof Window); parent = parent.getParent()) {
                if (parent instanceof JInternalFrame) {
                    ((JInternalFrame)parent).setLastFocusOwner(lastFocusOwner);
                }
            }
        }
    }
    
    public JInternalFrame() {
        this("", false, false, false, false);
    }
    
    public JInternalFrame(final String s) {
        this(s, false, false, false, false);
    }
    
    public JInternalFrame(final String s, final boolean b) {
        this(s, b, false, false, false);
    }
    
    public JInternalFrame(final String s, final boolean b, final boolean b2) {
        this(s, b, b2, false, false);
    }
    
    public JInternalFrame(final String s, final boolean b, final boolean b2, final boolean b3) {
        this(s, b, b2, b3, false);
    }
    
    public JInternalFrame(final String title, final boolean resizable, final boolean closable, final boolean maximizable, final boolean iconable) {
        this.rootPaneCheckingEnabled = false;
        this.normalBounds = null;
        this.defaultCloseOperation = 2;
        this.isDragging = false;
        this.danger = false;
        this.setRootPane(this.createRootPane());
        this.setLayout(new BorderLayout());
        this.title = title;
        this.resizable = resizable;
        this.closable = closable;
        this.maximizable = maximizable;
        this.isMaximum = false;
        this.iconable = iconable;
        this.setVisible(this.isIcon = false);
        this.setRootPaneCheckingEnabled(true);
        this.desktopIcon = new JDesktopIcon(this);
        this.updateUI();
        SunToolkit.checkAndSetPolicy(this);
        addPropertyChangeListenerIfNecessary();
    }
    
    protected JRootPane createRootPane() {
        return new JRootPane();
    }
    
    public InternalFrameUI getUI() {
        return (InternalFrameUI)this.ui;
    }
    
    public void setUI(final InternalFrameUI ui) {
        final boolean rootPaneCheckingEnabled = this.isRootPaneCheckingEnabled();
        try {
            this.setRootPaneCheckingEnabled(false);
            super.setUI(ui);
        }
        finally {
            this.setRootPaneCheckingEnabled(rootPaneCheckingEnabled);
        }
    }
    
    @Override
    public void updateUI() {
        this.setUI((InternalFrameUI)UIManager.getUI(this));
        this.invalidate();
        if (this.desktopIcon != null) {
            this.desktopIcon.updateUIWhenHidden();
        }
    }
    
    void updateUIWhenHidden() {
        this.setUI((InternalFrameUI)UIManager.getUI(this));
        this.invalidate();
        final Component[] components = this.getComponents();
        if (components != null) {
            final Component[] array = components;
            for (int length = array.length, i = 0; i < length; ++i) {
                SwingUtilities.updateComponentTreeUI(array[i]);
            }
        }
    }
    
    @Override
    public String getUIClassID() {
        return "InternalFrameUI";
    }
    
    protected boolean isRootPaneCheckingEnabled() {
        return this.rootPaneCheckingEnabled;
    }
    
    protected void setRootPaneCheckingEnabled(final boolean rootPaneCheckingEnabled) {
        this.rootPaneCheckingEnabled = rootPaneCheckingEnabled;
    }
    
    @Override
    protected void addImpl(final Component component, final Object o, final int n) {
        if (this.isRootPaneCheckingEnabled()) {
            this.getContentPane().add(component, o, n);
        }
        else {
            super.addImpl(component, o, n);
        }
    }
    
    @Override
    public void remove(final Component component) {
        final int componentCount = this.getComponentCount();
        super.remove(component);
        if (componentCount == this.getComponentCount()) {
            this.getContentPane().remove(component);
        }
    }
    
    @Override
    public void setLayout(final LayoutManager layoutManager) {
        if (this.isRootPaneCheckingEnabled()) {
            this.getContentPane().setLayout(layoutManager);
        }
        else {
            super.setLayout(layoutManager);
        }
    }
    
    @Deprecated
    public JMenuBar getMenuBar() {
        return this.getRootPane().getMenuBar();
    }
    
    public JMenuBar getJMenuBar() {
        return this.getRootPane().getJMenuBar();
    }
    
    @Deprecated
    public void setMenuBar(final JMenuBar jMenuBar) {
        final JMenuBar menuBar = this.getMenuBar();
        this.getRootPane().setJMenuBar(jMenuBar);
        this.firePropertyChange("JMenuBar", menuBar, jMenuBar);
    }
    
    public void setJMenuBar(final JMenuBar jMenuBar) {
        final JMenuBar menuBar = this.getMenuBar();
        this.getRootPane().setJMenuBar(jMenuBar);
        this.firePropertyChange("JMenuBar", menuBar, jMenuBar);
    }
    
    @Override
    public Container getContentPane() {
        return this.getRootPane().getContentPane();
    }
    
    @Override
    public void setContentPane(final Container contentPane) {
        final Container contentPane2 = this.getContentPane();
        this.getRootPane().setContentPane(contentPane);
        this.firePropertyChange("contentPane", contentPane2, contentPane);
    }
    
    @Override
    public JLayeredPane getLayeredPane() {
        return this.getRootPane().getLayeredPane();
    }
    
    @Override
    public void setLayeredPane(final JLayeredPane layeredPane) {
        final JLayeredPane layeredPane2 = this.getLayeredPane();
        this.getRootPane().setLayeredPane(layeredPane);
        this.firePropertyChange("layeredPane", layeredPane2, layeredPane);
    }
    
    @Override
    public Component getGlassPane() {
        return this.getRootPane().getGlassPane();
    }
    
    @Override
    public void setGlassPane(final Component glassPane) {
        final Component glassPane2 = this.getGlassPane();
        this.getRootPane().setGlassPane(glassPane);
        this.firePropertyChange("glassPane", glassPane2, glassPane);
    }
    
    @Override
    public JRootPane getRootPane() {
        return this.rootPane;
    }
    
    protected void setRootPane(final JRootPane rootPane) {
        if (this.rootPane != null) {
            this.remove(this.rootPane);
        }
        final JRootPane rootPane2 = this.getRootPane();
        this.rootPane = rootPane;
        if (this.rootPane != null) {
            final boolean rootPaneCheckingEnabled = this.isRootPaneCheckingEnabled();
            try {
                this.setRootPaneCheckingEnabled(false);
                this.add(this.rootPane, "Center");
            }
            finally {
                this.setRootPaneCheckingEnabled(rootPaneCheckingEnabled);
            }
        }
        this.firePropertyChange("rootPane", rootPane2, rootPane);
    }
    
    public void setClosable(final boolean closable) {
        final Boolean b = this.closable ? Boolean.TRUE : Boolean.FALSE;
        final Boolean b2 = closable ? Boolean.TRUE : Boolean.FALSE;
        this.closable = closable;
        this.firePropertyChange("closable", b, b2);
    }
    
    public boolean isClosable() {
        return this.closable;
    }
    
    public boolean isClosed() {
        return this.isClosed;
    }
    
    public void setClosed(final boolean isClosed) throws PropertyVetoException {
        if (this.isClosed == isClosed) {
            return;
        }
        final Boolean b = this.isClosed ? Boolean.TRUE : Boolean.FALSE;
        final Boolean b2 = isClosed ? Boolean.TRUE : Boolean.FALSE;
        if (isClosed) {
            this.fireInternalFrameEvent(25550);
        }
        this.fireVetoableChange("closed", b, b2);
        this.isClosed = isClosed;
        if (this.isClosed) {
            this.setVisible(false);
        }
        this.firePropertyChange("closed", b, b2);
        if (this.isClosed) {
            this.dispose();
        }
        else if (!this.opened) {}
    }
    
    public void setResizable(final boolean resizable) {
        final Boolean b = this.resizable ? Boolean.TRUE : Boolean.FALSE;
        final Boolean b2 = resizable ? Boolean.TRUE : Boolean.FALSE;
        this.resizable = resizable;
        this.firePropertyChange("resizable", b, b2);
    }
    
    public boolean isResizable() {
        return !this.isMaximum && this.resizable;
    }
    
    public void setIconifiable(final boolean iconable) {
        final Boolean b = this.iconable ? Boolean.TRUE : Boolean.FALSE;
        final Boolean b2 = iconable ? Boolean.TRUE : Boolean.FALSE;
        this.iconable = iconable;
        this.firePropertyChange("iconable", b, b2);
    }
    
    public boolean isIconifiable() {
        return this.iconable;
    }
    
    public boolean isIcon() {
        return this.isIcon;
    }
    
    public void setIcon(final boolean isIcon) throws PropertyVetoException {
        if (this.isIcon == isIcon) {
            return;
        }
        this.firePropertyChange("ancestor", null, this.getParent());
        final Boolean b = this.isIcon ? Boolean.TRUE : Boolean.FALSE;
        final Boolean b2 = isIcon ? Boolean.TRUE : Boolean.FALSE;
        this.fireVetoableChange("icon", b, b2);
        this.isIcon = isIcon;
        this.firePropertyChange("icon", b, b2);
        if (isIcon) {
            this.fireInternalFrameEvent(25552);
        }
        else {
            this.fireInternalFrameEvent(25553);
        }
    }
    
    public void setMaximizable(final boolean maximizable) {
        final Boolean b = this.maximizable ? Boolean.TRUE : Boolean.FALSE;
        final Boolean b2 = maximizable ? Boolean.TRUE : Boolean.FALSE;
        this.maximizable = maximizable;
        this.firePropertyChange("maximizable", b, b2);
    }
    
    public boolean isMaximizable() {
        return this.maximizable;
    }
    
    public boolean isMaximum() {
        return this.isMaximum;
    }
    
    public void setMaximum(final boolean isMaximum) throws PropertyVetoException {
        if (this.isMaximum == isMaximum) {
            return;
        }
        final Boolean b = this.isMaximum ? Boolean.TRUE : Boolean.FALSE;
        final Boolean b2 = isMaximum ? Boolean.TRUE : Boolean.FALSE;
        this.fireVetoableChange("maximum", b, b2);
        this.isMaximum = isMaximum;
        this.firePropertyChange("maximum", b, b2);
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(final String title) {
        this.firePropertyChange("title", this.title, this.title = title);
    }
    
    public void setSelected(final boolean isSelected) throws PropertyVetoException {
        if (isSelected && this.isSelected) {
            this.restoreSubcomponentFocus();
            return;
        }
        if (this.isSelected != isSelected) {
            if (isSelected) {
                if (this.isIcon) {
                    if (!this.desktopIcon.isShowing()) {
                        return;
                    }
                }
                else if (!this.isShowing()) {
                    return;
                }
            }
            final Boolean b = this.isSelected ? Boolean.TRUE : Boolean.FALSE;
            final Boolean b2 = isSelected ? Boolean.TRUE : Boolean.FALSE;
            this.fireVetoableChange("selected", b, b2);
            if (isSelected) {
                this.restoreSubcomponentFocus();
            }
            this.isSelected = isSelected;
            this.firePropertyChange("selected", b, b2);
            if (this.isSelected) {
                this.fireInternalFrameEvent(25554);
            }
            else {
                this.fireInternalFrameEvent(25555);
            }
            this.repaint();
        }
    }
    
    public boolean isSelected() {
        return this.isSelected;
    }
    
    public void setFrameIcon(final Icon frameIcon) {
        this.firePropertyChange("frameIcon", this.frameIcon, this.frameIcon = frameIcon);
    }
    
    public Icon getFrameIcon() {
        return this.frameIcon;
    }
    
    public void moveToFront() {
        if (this.isIcon()) {
            if (this.getDesktopIcon().getParent() instanceof JLayeredPane) {
                ((JLayeredPane)this.getDesktopIcon().getParent()).moveToFront(this.getDesktopIcon());
            }
        }
        else if (this.getParent() instanceof JLayeredPane) {
            ((JLayeredPane)this.getParent()).moveToFront(this);
        }
    }
    
    public void moveToBack() {
        if (this.isIcon()) {
            if (this.getDesktopIcon().getParent() instanceof JLayeredPane) {
                ((JLayeredPane)this.getDesktopIcon().getParent()).moveToBack(this.getDesktopIcon());
            }
        }
        else if (this.getParent() instanceof JLayeredPane) {
            ((JLayeredPane)this.getParent()).moveToBack(this);
        }
    }
    
    public Cursor getLastCursor() {
        return this.lastCursor;
    }
    
    @Override
    public void setCursor(final Cursor cursor) {
        if (cursor == null) {
            this.lastCursor = null;
            super.setCursor(cursor);
            return;
        }
        final int type = cursor.getType();
        if (type != 4 && type != 5 && type != 6 && type != 7 && type != 8 && type != 9 && type != 10 && type != 11) {
            this.lastCursor = cursor;
        }
        super.setCursor(cursor);
    }
    
    public void setLayer(final Integer n) {
        if (this.getParent() != null && this.getParent() instanceof JLayeredPane) {
            final JLayeredPane layeredPane = (JLayeredPane)this.getParent();
            layeredPane.setLayer(this, n, layeredPane.getPosition(this));
        }
        else {
            JLayeredPane.putLayer(this, n);
            if (this.getParent() != null) {
                this.getParent().repaint(this.getX(), this.getY(), this.getWidth(), this.getHeight());
            }
        }
    }
    
    public void setLayer(final int n) {
        this.setLayer(Integer.valueOf(n));
    }
    
    public int getLayer() {
        return JLayeredPane.getLayer(this);
    }
    
    public JDesktopPane getDesktopPane() {
        Container container;
        for (container = this.getParent(); container != null && !(container instanceof JDesktopPane); container = container.getParent()) {}
        if (container == null) {
            for (container = this.getDesktopIcon().getParent(); container != null && !(container instanceof JDesktopPane); container = container.getParent()) {}
        }
        return (JDesktopPane)container;
    }
    
    public void setDesktopIcon(final JDesktopIcon desktopIcon) {
        this.firePropertyChange("desktopIcon", this.getDesktopIcon(), this.desktopIcon = desktopIcon);
    }
    
    public JDesktopIcon getDesktopIcon() {
        return this.desktopIcon;
    }
    
    public Rectangle getNormalBounds() {
        if (this.normalBounds != null) {
            return this.normalBounds;
        }
        return this.getBounds();
    }
    
    public void setNormalBounds(final Rectangle normalBounds) {
        this.normalBounds = normalBounds;
    }
    
    public Component getFocusOwner() {
        if (this.isSelected()) {
            return this.lastFocusOwner;
        }
        return null;
    }
    
    public Component getMostRecentFocusOwner() {
        if (this.isSelected()) {
            return this.getFocusOwner();
        }
        if (this.lastFocusOwner != null) {
            return this.lastFocusOwner;
        }
        final FocusTraversalPolicy focusTraversalPolicy = this.getFocusTraversalPolicy();
        if (focusTraversalPolicy instanceof InternalFrameFocusTraversalPolicy) {
            return ((InternalFrameFocusTraversalPolicy)focusTraversalPolicy).getInitialComponent(this);
        }
        final Component defaultComponent = focusTraversalPolicy.getDefaultComponent(this);
        if (defaultComponent != null) {
            return defaultComponent;
        }
        return this.getContentPane();
    }
    
    public void restoreSubcomponentFocus() {
        if (this.isIcon()) {
            SwingUtilities2.compositeRequestFocus(this.getDesktopIcon());
        }
        else {
            final Component permanentFocusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            if (permanentFocusOwner == null || !SwingUtilities.isDescendingFrom(permanentFocusOwner, this)) {
                this.setLastFocusOwner(this.getMostRecentFocusOwner());
                if (this.lastFocusOwner == null) {
                    this.setLastFocusOwner(this.getContentPane());
                }
                this.lastFocusOwner.requestFocus();
            }
        }
    }
    
    private void setLastFocusOwner(final Component lastFocusOwner) {
        this.lastFocusOwner = lastFocusOwner;
    }
    
    @Override
    public void reshape(final int n, final int n2, final int n3, final int n4) {
        super.reshape(n, n2, n3, n4);
        this.validate();
        this.repaint();
    }
    
    public void addInternalFrameListener(final InternalFrameListener internalFrameListener) {
        this.listenerList.add(InternalFrameListener.class, internalFrameListener);
        this.enableEvents(0L);
    }
    
    public void removeInternalFrameListener(final InternalFrameListener internalFrameListener) {
        this.listenerList.remove(InternalFrameListener.class, internalFrameListener);
    }
    
    public InternalFrameListener[] getInternalFrameListeners() {
        return this.listenerList.getListeners(InternalFrameListener.class);
    }
    
    protected void fireInternalFrameEvent(final int n) {
        final Object[] listenerList = this.listenerList.getListenerList();
        InternalFrameEvent internalFrameEvent = null;
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == InternalFrameListener.class) {
                if (internalFrameEvent == null) {
                    internalFrameEvent = new InternalFrameEvent(this, n);
                }
                switch (internalFrameEvent.getID()) {
                    case 25549: {
                        ((InternalFrameListener)listenerList[i + 1]).internalFrameOpened(internalFrameEvent);
                        break;
                    }
                    case 25550: {
                        ((InternalFrameListener)listenerList[i + 1]).internalFrameClosing(internalFrameEvent);
                        break;
                    }
                    case 25551: {
                        ((InternalFrameListener)listenerList[i + 1]).internalFrameClosed(internalFrameEvent);
                        break;
                    }
                    case 25552: {
                        ((InternalFrameListener)listenerList[i + 1]).internalFrameIconified(internalFrameEvent);
                        break;
                    }
                    case 25553: {
                        ((InternalFrameListener)listenerList[i + 1]).internalFrameDeiconified(internalFrameEvent);
                        break;
                    }
                    case 25554: {
                        ((InternalFrameListener)listenerList[i + 1]).internalFrameActivated(internalFrameEvent);
                        break;
                    }
                    case 25555: {
                        ((InternalFrameListener)listenerList[i + 1]).internalFrameDeactivated(internalFrameEvent);
                        break;
                    }
                }
            }
        }
    }
    
    public void doDefaultCloseAction() {
        this.fireInternalFrameEvent(25550);
        switch (this.defaultCloseOperation) {
            case 1: {
                this.setVisible(false);
                if (this.isSelected()) {
                    try {
                        this.setSelected(false);
                    }
                    catch (final PropertyVetoException ex) {}
                    break;
                }
                break;
            }
            case 2: {
                try {
                    this.fireVetoableChange("closed", Boolean.FALSE, Boolean.TRUE);
                    this.isClosed = true;
                    this.setVisible(false);
                    this.firePropertyChange("closed", Boolean.FALSE, Boolean.TRUE);
                    this.dispose();
                }
                catch (final PropertyVetoException ex2) {}
                break;
            }
        }
    }
    
    public void setDefaultCloseOperation(final int defaultCloseOperation) {
        this.defaultCloseOperation = defaultCloseOperation;
    }
    
    public int getDefaultCloseOperation() {
        return this.defaultCloseOperation;
    }
    
    public void pack() {
        try {
            if (this.isIcon()) {
                this.setIcon(false);
            }
            else if (this.isMaximum()) {
                this.setMaximum(false);
            }
        }
        catch (final PropertyVetoException ex) {
            return;
        }
        this.setSize(this.getPreferredSize());
        this.validate();
    }
    
    @Override
    public void show() {
        if (this.isVisible()) {
            return;
        }
        if (!this.opened) {
            this.fireInternalFrameEvent(25549);
            this.opened = true;
        }
        this.getDesktopIcon().setVisible(true);
        this.toFront();
        super.show();
        if (this.isIcon) {
            return;
        }
        if (!this.isSelected()) {
            try {
                this.setSelected(true);
            }
            catch (final PropertyVetoException ex) {}
        }
    }
    
    @Override
    public void hide() {
        if (this.isIcon()) {
            this.getDesktopIcon().setVisible(false);
        }
        super.hide();
    }
    
    public void dispose() {
        if (this.isVisible()) {
            this.setVisible(false);
        }
        if (this.isSelected()) {
            try {
                this.setSelected(false);
            }
            catch (final PropertyVetoException ex) {}
        }
        if (!this.isClosed) {
            this.firePropertyChange("closed", Boolean.FALSE, Boolean.TRUE);
            this.isClosed = true;
        }
        this.fireInternalFrameEvent(25551);
    }
    
    public void toFront() {
        this.moveToFront();
    }
    
    public void toBack() {
        this.moveToBack();
    }
    
    @Override
    public final void setFocusCycleRoot(final boolean b) {
    }
    
    @Override
    public final boolean isFocusCycleRoot() {
        return true;
    }
    
    @Override
    public final Container getFocusCycleRootAncestor() {
        return null;
    }
    
    public final String getWarningString() {
        return null;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.getUIClassID().equals("InternalFrameUI")) {
            final byte b = (byte)(JComponent.getWriteObjCounter(this) - 1);
            JComponent.setWriteObjCounter(this, b);
            if (b == 0 && this.ui != null) {
                final boolean rootPaneCheckingEnabled = this.isRootPaneCheckingEnabled();
                try {
                    this.setRootPaneCheckingEnabled(false);
                    this.ui.installUI(this);
                }
                finally {
                    this.setRootPaneCheckingEnabled(rootPaneCheckingEnabled);
                }
            }
        }
    }
    
    @Override
    void compWriteObjectNotify() {
        final boolean rootPaneCheckingEnabled = this.isRootPaneCheckingEnabled();
        try {
            this.setRootPaneCheckingEnabled(false);
            super.compWriteObjectNotify();
        }
        finally {
            this.setRootPaneCheckingEnabled(rootPaneCheckingEnabled);
        }
    }
    
    @Override
    protected String paramString() {
        final String s = (this.rootPane != null) ? this.rootPane.toString() : "";
        final String s2 = this.rootPaneCheckingEnabled ? "true" : "false";
        final String s3 = this.closable ? "true" : "false";
        final String s4 = this.isClosed ? "true" : "false";
        final String s5 = this.maximizable ? "true" : "false";
        final String s6 = this.isMaximum ? "true" : "false";
        final String s7 = this.iconable ? "true" : "false";
        final String s8 = this.isIcon ? "true" : "false";
        final String s9 = this.resizable ? "true" : "false";
        final String s10 = this.isSelected ? "true" : "false";
        final String s11 = (this.frameIcon != null) ? this.frameIcon.toString() : "";
        final String s12 = (this.title != null) ? this.title : "";
        final String s13 = (this.desktopIcon != null) ? this.desktopIcon.toString() : "";
        final String s14 = this.opened ? "true" : "false";
        String s15;
        if (this.defaultCloseOperation == 1) {
            s15 = "HIDE_ON_CLOSE";
        }
        else if (this.defaultCloseOperation == 2) {
            s15 = "DISPOSE_ON_CLOSE";
        }
        else if (this.defaultCloseOperation == 0) {
            s15 = "DO_NOTHING_ON_CLOSE";
        }
        else {
            s15 = "";
        }
        return super.paramString() + ",closable=" + s3 + ",defaultCloseOperation=" + s15 + ",desktopIcon=" + s13 + ",frameIcon=" + s11 + ",iconable=" + s7 + ",isClosed=" + s4 + ",isIcon=" + s8 + ",isMaximum=" + s6 + ",isSelected=" + s10 + ",maximizable=" + s5 + ",opened=" + s14 + ",resizable=" + s9 + ",rootPane=" + s + ",rootPaneCheckingEnabled=" + s2 + ",title=" + s12;
    }
    
    @Override
    protected void paintComponent(final Graphics graphics) {
        if (this.isDragging) {
            this.danger = true;
        }
        super.paintComponent(graphics);
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJInternalFrame();
        }
        return this.accessibleContext;
    }
    
    static {
        PROPERTY_CHANGE_LISTENER_KEY = new StringBuilder("InternalFramePropertyChangeListener");
    }
    
    private static class FocusPropertyChangeListener implements PropertyChangeListener
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            if (propertyChangeEvent.getPropertyName() == "permanentFocusOwner") {
                updateLastFocusOwner((Component)propertyChangeEvent.getNewValue());
            }
        }
    }
    
    protected class AccessibleJInternalFrame extends AccessibleJComponent implements AccessibleValue
    {
        @Override
        public String getAccessibleName() {
            String s = this.accessibleName;
            if (s == null) {
                s = (String)JInternalFrame.this.getClientProperty("AccessibleName");
            }
            if (s == null) {
                s = JInternalFrame.this.getTitle();
            }
            return s;
        }
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.INTERNAL_FRAME;
        }
        
        @Override
        public AccessibleValue getAccessibleValue() {
            return this;
        }
        
        @Override
        public Number getCurrentAccessibleValue() {
            return JInternalFrame.this.getLayer();
        }
        
        @Override
        public boolean setCurrentAccessibleValue(final Number n) {
            if (n == null) {
                return false;
            }
            JInternalFrame.this.setLayer(new Integer(n.intValue()));
            return true;
        }
        
        @Override
        public Number getMinimumAccessibleValue() {
            return Integer.MIN_VALUE;
        }
        
        @Override
        public Number getMaximumAccessibleValue() {
            return Integer.MAX_VALUE;
        }
    }
    
    public static class JDesktopIcon extends JComponent implements Accessible
    {
        JInternalFrame internalFrame;
        
        public JDesktopIcon(final JInternalFrame internalFrame) {
            this.setVisible(false);
            this.setInternalFrame(internalFrame);
            this.updateUI();
        }
        
        public DesktopIconUI getUI() {
            return (DesktopIconUI)this.ui;
        }
        
        public void setUI(final DesktopIconUI ui) {
            super.setUI(ui);
        }
        
        public JInternalFrame getInternalFrame() {
            return this.internalFrame;
        }
        
        public void setInternalFrame(final JInternalFrame internalFrame) {
            this.internalFrame = internalFrame;
        }
        
        public JDesktopPane getDesktopPane() {
            if (this.getInternalFrame() != null) {
                return this.getInternalFrame().getDesktopPane();
            }
            return null;
        }
        
        @Override
        public void updateUI() {
            final boolean b = this.ui != null;
            this.setUI((DesktopIconUI)UIManager.getUI(this));
            this.invalidate();
            final Dimension preferredSize = this.getPreferredSize();
            this.setSize(preferredSize.width, preferredSize.height);
            if (this.internalFrame != null && this.internalFrame.getUI() != null) {
                SwingUtilities.updateComponentTreeUI(this.internalFrame);
            }
        }
        
        void updateUIWhenHidden() {
            this.setUI((DesktopIconUI)UIManager.getUI(this));
            final Dimension preferredSize = this.getPreferredSize();
            this.setSize(preferredSize.width, preferredSize.height);
            this.invalidate();
            final Component[] components = this.getComponents();
            if (components != null) {
                final Component[] array = components;
                for (int length = array.length, i = 0; i < length; ++i) {
                    SwingUtilities.updateComponentTreeUI(array[i]);
                }
            }
        }
        
        @Override
        public String getUIClassID() {
            return "DesktopIconUI";
        }
        
        private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.defaultWriteObject();
            if (this.getUIClassID().equals("DesktopIconUI")) {
                final byte b = (byte)(JComponent.getWriteObjCounter(this) - 1);
                JComponent.setWriteObjCounter(this, b);
                if (b == 0 && this.ui != null) {
                    this.ui.installUI(this);
                }
            }
        }
        
        @Override
        public AccessibleContext getAccessibleContext() {
            if (this.accessibleContext == null) {
                this.accessibleContext = new AccessibleJDesktopIcon();
            }
            return this.accessibleContext;
        }
        
        protected class AccessibleJDesktopIcon extends AccessibleJComponent implements AccessibleValue
        {
            @Override
            public AccessibleRole getAccessibleRole() {
                return AccessibleRole.DESKTOP_ICON;
            }
            
            @Override
            public AccessibleValue getAccessibleValue() {
                return this;
            }
            
            @Override
            public Number getCurrentAccessibleValue() {
                final AccessibleValue accessibleValue = JDesktopIcon.this.getInternalFrame().getAccessibleContext().getAccessibleValue();
                if (accessibleValue != null) {
                    return accessibleValue.getCurrentAccessibleValue();
                }
                return null;
            }
            
            @Override
            public boolean setCurrentAccessibleValue(final Number currentAccessibleValue) {
                if (currentAccessibleValue == null) {
                    return false;
                }
                final AccessibleValue accessibleValue = JDesktopIcon.this.getInternalFrame().getAccessibleContext().getAccessibleValue();
                return accessibleValue != null && accessibleValue.setCurrentAccessibleValue(currentAccessibleValue);
            }
            
            @Override
            public Number getMinimumAccessibleValue() {
                final AccessibleContext accessibleContext = JDesktopIcon.this.getInternalFrame().getAccessibleContext();
                if (accessibleContext instanceof AccessibleValue) {
                    return ((AccessibleValue)accessibleContext).getMinimumAccessibleValue();
                }
                return null;
            }
            
            @Override
            public Number getMaximumAccessibleValue() {
                final AccessibleContext accessibleContext = JDesktopIcon.this.getInternalFrame().getAccessibleContext();
                if (accessibleContext instanceof AccessibleValue) {
                    return ((AccessibleValue)accessibleContext).getMaximumAccessibleValue();
                }
                return null;
            }
        }
    }
}
