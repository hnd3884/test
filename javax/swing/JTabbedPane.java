package javax.swing;

import javax.accessibility.AccessibleIcon;
import java.awt.event.FocusListener;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.Cursor;
import java.util.Locale;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleComponent;
import java.awt.Point;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleContext;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.awt.event.MouseEvent;
import java.awt.Rectangle;
import java.awt.Color;
import javax.accessibility.AccessibleState;
import java.beans.Transient;
import sun.swing.SwingUtilities2;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TabbedPaneUI;
import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import java.awt.Component;
import java.util.List;
import javax.swing.event.ChangeListener;
import javax.accessibility.Accessible;
import java.io.Serializable;

public class JTabbedPane extends JComponent implements Serializable, Accessible, SwingConstants
{
    public static final int WRAP_TAB_LAYOUT = 0;
    public static final int SCROLL_TAB_LAYOUT = 1;
    private static final String uiClassID = "TabbedPaneUI";
    protected int tabPlacement;
    private int tabLayoutPolicy;
    protected SingleSelectionModel model;
    private boolean haveRegistered;
    protected ChangeListener changeListener;
    private final List<Page> pages;
    private Component visComp;
    protected transient ChangeEvent changeEvent;
    
    public JTabbedPane() {
        this(1, 0);
    }
    
    public JTabbedPane(final int n) {
        this(n, 0);
    }
    
    public JTabbedPane(final int tabPlacement, final int tabLayoutPolicy) {
        this.tabPlacement = 1;
        this.changeListener = null;
        this.visComp = null;
        this.changeEvent = null;
        this.setTabPlacement(tabPlacement);
        this.setTabLayoutPolicy(tabLayoutPolicy);
        this.pages = new ArrayList<Page>(1);
        this.setModel(new DefaultSingleSelectionModel());
        this.updateUI();
    }
    
    public TabbedPaneUI getUI() {
        return (TabbedPaneUI)this.ui;
    }
    
    public void setUI(final TabbedPaneUI ui) {
        super.setUI(ui);
        for (int i = 0; i < this.getTabCount(); ++i) {
            if (this.pages.get(i).disabledIcon instanceof UIResource) {
                this.setDisabledIconAt(i, null);
            }
        }
    }
    
    @Override
    public void updateUI() {
        this.setUI((TabbedPaneUI)UIManager.getUI(this));
    }
    
    @Override
    public String getUIClassID() {
        return "TabbedPaneUI";
    }
    
    protected ChangeListener createChangeListener() {
        return new ModelListener();
    }
    
    public void addChangeListener(final ChangeListener changeListener) {
        this.listenerList.add(ChangeListener.class, changeListener);
    }
    
    public void removeChangeListener(final ChangeListener changeListener) {
        this.listenerList.remove(ChangeListener.class, changeListener);
    }
    
    public ChangeListener[] getChangeListeners() {
        return this.listenerList.getListeners(ChangeListener.class);
    }
    
    protected void fireStateChanged() {
        final int selectedIndex = this.getSelectedIndex();
        if (selectedIndex < 0) {
            if (this.visComp != null && this.visComp.isVisible()) {
                this.visComp.setVisible(false);
            }
            this.visComp = null;
        }
        else {
            final Component component = this.getComponentAt(selectedIndex);
            if (component != null && component != this.visComp) {
                int n = 0;
                if (this.visComp != null) {
                    n = ((SwingUtilities.findFocusOwner(this.visComp) != null) ? 1 : 0);
                    if (this.visComp.isVisible()) {
                        this.visComp.setVisible(false);
                    }
                }
                if (!component.isVisible()) {
                    component.setVisible(true);
                }
                if (n != 0) {
                    SwingUtilities2.tabbedPaneChangeFocusTo(component);
                }
                this.visComp = component;
            }
        }
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
    
    public SingleSelectionModel getModel() {
        return this.model;
    }
    
    public void setModel(final SingleSelectionModel model) {
        final SingleSelectionModel model2 = this.getModel();
        if (model2 != null) {
            model2.removeChangeListener(this.changeListener);
            this.changeListener = null;
        }
        if ((this.model = model) != null) {
            model.addChangeListener(this.changeListener = this.createChangeListener());
        }
        this.firePropertyChange("model", model2, model);
        this.repaint();
    }
    
    public int getTabPlacement() {
        return this.tabPlacement;
    }
    
    public void setTabPlacement(final int tabPlacement) {
        if (tabPlacement != 1 && tabPlacement != 2 && tabPlacement != 3 && tabPlacement != 4) {
            throw new IllegalArgumentException("illegal tab placement: must be TOP, BOTTOM, LEFT, or RIGHT");
        }
        if (this.tabPlacement != tabPlacement) {
            this.firePropertyChange("tabPlacement", this.tabPlacement, this.tabPlacement = tabPlacement);
            this.revalidate();
            this.repaint();
        }
    }
    
    public int getTabLayoutPolicy() {
        return this.tabLayoutPolicy;
    }
    
    public void setTabLayoutPolicy(final int tabLayoutPolicy) {
        if (tabLayoutPolicy != 0 && tabLayoutPolicy != 1) {
            throw new IllegalArgumentException("illegal tab layout policy: must be WRAP_TAB_LAYOUT or SCROLL_TAB_LAYOUT");
        }
        if (this.tabLayoutPolicy != tabLayoutPolicy) {
            this.firePropertyChange("tabLayoutPolicy", this.tabLayoutPolicy, this.tabLayoutPolicy = tabLayoutPolicy);
            this.revalidate();
            this.repaint();
        }
    }
    
    @Transient
    public int getSelectedIndex() {
        return this.model.getSelectedIndex();
    }
    
    public void setSelectedIndex(final int n) {
        if (n != -1) {
            this.checkIndex(n);
        }
        this.setSelectedIndexImpl(n, true);
    }
    
    private void setSelectedIndexImpl(final int selectedIndex, final boolean b) {
        final int selectedIndex2 = this.model.getSelectedIndex();
        Page page = null;
        Page page2 = null;
        String accessibleName = null;
        final boolean b2 = b && selectedIndex2 != selectedIndex;
        if (b2) {
            if (this.accessibleContext != null) {
                accessibleName = this.accessibleContext.getAccessibleName();
            }
            if (selectedIndex2 >= 0) {
                page = this.pages.get(selectedIndex2);
            }
            if (selectedIndex >= 0) {
                page2 = this.pages.get(selectedIndex);
            }
        }
        this.model.setSelectedIndex(selectedIndex);
        if (b2) {
            this.changeAccessibleSelection(page, accessibleName, page2);
        }
    }
    
    private void changeAccessibleSelection(final Page page, final String s, final Page page2) {
        if (this.accessibleContext == null) {
            return;
        }
        if (page != null) {
            page.firePropertyChange("AccessibleState", AccessibleState.SELECTED, null);
        }
        if (page2 != null) {
            page2.firePropertyChange("AccessibleState", null, AccessibleState.SELECTED);
        }
        this.accessibleContext.firePropertyChange("AccessibleName", s, this.accessibleContext.getAccessibleName());
    }
    
    @Transient
    public Component getSelectedComponent() {
        final int selectedIndex = this.getSelectedIndex();
        if (selectedIndex == -1) {
            return null;
        }
        return this.getComponentAt(selectedIndex);
    }
    
    public void setSelectedComponent(final Component component) {
        final int indexOfComponent = this.indexOfComponent(component);
        if (indexOfComponent != -1) {
            this.setSelectedIndex(indexOfComponent);
            return;
        }
        throw new IllegalArgumentException("component not found in tabbed pane");
    }
    
    public void insertTab(final String s, final Icon icon, final Component component, final String s2, final int n) {
        int n2 = n;
        final int indexOfComponent = this.indexOfComponent(component);
        if (component != null && indexOfComponent != -1) {
            this.removeTabAt(indexOfComponent);
            if (n2 > indexOfComponent) {
                --n2;
            }
        }
        final int selectedIndex = this.getSelectedIndex();
        this.pages.add(n2, new Page(this, (s != null) ? s : "", icon, null, component, s2));
        if (component != null) {
            this.addImpl(component, null, -1);
            component.setVisible(false);
        }
        else {
            this.firePropertyChange("indexForNullComponent", -1, n);
        }
        if (this.pages.size() == 1) {
            this.setSelectedIndex(0);
        }
        if (selectedIndex >= n2) {
            this.setSelectedIndexImpl(selectedIndex + 1, false);
        }
        if (!this.haveRegistered && s2 != null) {
            ToolTipManager.sharedInstance().registerComponent(this);
            this.haveRegistered = true;
        }
        if (this.accessibleContext != null) {
            this.accessibleContext.firePropertyChange("AccessibleVisibleData", null, component);
        }
        this.revalidate();
        this.repaint();
    }
    
    public void addTab(final String s, final Icon icon, final Component component, final String s2) {
        this.insertTab(s, icon, component, s2, this.pages.size());
    }
    
    public void addTab(final String s, final Icon icon, final Component component) {
        this.insertTab(s, icon, component, null, this.pages.size());
    }
    
    public void addTab(final String s, final Component component) {
        this.insertTab(s, null, component, null, this.pages.size());
    }
    
    @Override
    public Component add(final Component component) {
        if (!(component instanceof UIResource)) {
            this.addTab(component.getName(), component);
        }
        else {
            super.add(component);
        }
        return component;
    }
    
    @Override
    public Component add(final String s, final Component component) {
        if (!(component instanceof UIResource)) {
            this.addTab(s, component);
        }
        else {
            super.add(s, component);
        }
        return component;
    }
    
    @Override
    public Component add(final Component component, final int n) {
        if (!(component instanceof UIResource)) {
            this.insertTab(component.getName(), null, component, null, (n == -1) ? this.getTabCount() : n);
        }
        else {
            super.add(component, n);
        }
        return component;
    }
    
    @Override
    public void add(final Component component, final Object o) {
        if (!(component instanceof UIResource)) {
            if (o instanceof String) {
                this.addTab((String)o, component);
            }
            else if (o instanceof Icon) {
                this.addTab(null, (Icon)o, component);
            }
            else {
                this.add(component);
            }
        }
        else {
            super.add(component, o);
        }
    }
    
    @Override
    public void add(final Component component, final Object o, final int n) {
        if (!(component instanceof UIResource)) {
            this.insertTab((o instanceof String) ? ((String)o) : null, (o instanceof Icon) ? ((Icon)o) : null, component, null, (n == -1) ? this.getTabCount() : n);
        }
        else {
            super.add(component, o, n);
        }
    }
    
    public void removeTabAt(final int n) {
        this.checkIndex(n);
        final Component component = this.getComponentAt(n);
        int n2 = 0;
        final int selectedIndex = this.getSelectedIndex();
        String accessibleName = null;
        if (component == this.visComp) {
            n2 = ((SwingUtilities.findFocusOwner(this.visComp) != null) ? 1 : 0);
            this.visComp = null;
        }
        if (this.accessibleContext != null) {
            if (n == selectedIndex) {
                this.pages.get(n).firePropertyChange("AccessibleState", AccessibleState.SELECTED, null);
                accessibleName = this.accessibleContext.getAccessibleName();
            }
            this.accessibleContext.firePropertyChange("AccessibleVisibleData", component, null);
        }
        this.setTabComponentAt(n, null);
        this.pages.remove(n);
        this.putClientProperty("__index_to_remove__", n);
        if (selectedIndex > n) {
            this.setSelectedIndexImpl(selectedIndex - 1, false);
        }
        else if (selectedIndex >= this.getTabCount()) {
            this.setSelectedIndexImpl(selectedIndex - 1, false);
            this.changeAccessibleSelection(null, accessibleName, (selectedIndex != 0) ? this.pages.get(selectedIndex - 1) : null);
        }
        else if (n == selectedIndex) {
            this.fireStateChanged();
            this.changeAccessibleSelection(null, accessibleName, this.pages.get(n));
        }
        if (component != null) {
            final Component[] components = this.getComponents();
            int length = components.length;
            while (--length >= 0) {
                if (components[length] == component) {
                    super.remove(length);
                    component.setVisible(true);
                    break;
                }
            }
        }
        if (n2 != 0) {
            SwingUtilities2.tabbedPaneChangeFocusTo(this.getSelectedComponent());
        }
        this.revalidate();
        this.repaint();
    }
    
    @Override
    public void remove(final Component component) {
        final int indexOfComponent = this.indexOfComponent(component);
        if (indexOfComponent != -1) {
            this.removeTabAt(indexOfComponent);
        }
        else {
            final Component[] components = this.getComponents();
            for (int i = 0; i < components.length; ++i) {
                if (component == components[i]) {
                    super.remove(i);
                    break;
                }
            }
        }
    }
    
    @Override
    public void remove(final int n) {
        this.removeTabAt(n);
    }
    
    @Override
    public void removeAll() {
        this.setSelectedIndexImpl(-1, true);
        int tabCount = this.getTabCount();
        while (tabCount-- > 0) {
            this.removeTabAt(tabCount);
        }
    }
    
    public int getTabCount() {
        return this.pages.size();
    }
    
    public int getTabRunCount() {
        if (this.ui != null) {
            return ((TabbedPaneUI)this.ui).getTabRunCount(this);
        }
        return 0;
    }
    
    public String getTitleAt(final int n) {
        return this.pages.get(n).title;
    }
    
    public Icon getIconAt(final int n) {
        return this.pages.get(n).icon;
    }
    
    public Icon getDisabledIconAt(final int n) {
        final Page page = this.pages.get(n);
        if (page.disabledIcon == null) {
            page.disabledIcon = UIManager.getLookAndFeel().getDisabledIcon(this, page.icon);
        }
        return page.disabledIcon;
    }
    
    public String getToolTipTextAt(final int n) {
        return this.pages.get(n).tip;
    }
    
    public Color getBackgroundAt(final int n) {
        return this.pages.get(n).getBackground();
    }
    
    public Color getForegroundAt(final int n) {
        return this.pages.get(n).getForeground();
    }
    
    public boolean isEnabledAt(final int n) {
        return this.pages.get(n).isEnabled();
    }
    
    public Component getComponentAt(final int n) {
        return this.pages.get(n).component;
    }
    
    public int getMnemonicAt(final int n) {
        this.checkIndex(n);
        return this.pages.get(n).getMnemonic();
    }
    
    public int getDisplayedMnemonicIndexAt(final int n) {
        this.checkIndex(n);
        return this.pages.get(n).getDisplayedMnemonicIndex();
    }
    
    public Rectangle getBoundsAt(final int n) {
        this.checkIndex(n);
        if (this.ui != null) {
            return ((TabbedPaneUI)this.ui).getTabBounds(this, n);
        }
        return null;
    }
    
    public void setTitleAt(final int n, final String title) {
        final Page page = this.pages.get(n);
        final String title2 = page.title;
        page.title = title;
        if (title2 != title) {
            this.firePropertyChange("indexForTitle", -1, n);
        }
        page.updateDisplayedMnemonicIndex();
        if (title2 != title && this.accessibleContext != null) {
            this.accessibleContext.firePropertyChange("AccessibleVisibleData", title2, title);
        }
        if (title == null || title2 == null || !title.equals(title2)) {
            this.revalidate();
            this.repaint();
        }
    }
    
    public void setIconAt(final int n, final Icon icon) {
        final Page page = this.pages.get(n);
        final Icon icon2 = page.icon;
        if (icon != icon2) {
            page.icon = icon;
            if (page.disabledIcon instanceof UIResource) {
                page.disabledIcon = null;
            }
            if (this.accessibleContext != null) {
                this.accessibleContext.firePropertyChange("AccessibleVisibleData", icon2, icon);
            }
            this.revalidate();
            this.repaint();
        }
    }
    
    public void setDisabledIconAt(final int n, final Icon disabledIcon) {
        final Icon disabledIcon2 = this.pages.get(n).disabledIcon;
        this.pages.get(n).disabledIcon = disabledIcon;
        if (disabledIcon != disabledIcon2 && !this.isEnabledAt(n)) {
            this.revalidate();
            this.repaint();
        }
    }
    
    public void setToolTipTextAt(final int n, final String tip) {
        final String tip2 = this.pages.get(n).tip;
        this.pages.get(n).tip = tip;
        if (tip2 != tip && this.accessibleContext != null) {
            this.accessibleContext.firePropertyChange("AccessibleVisibleData", tip2, tip);
        }
        if (!this.haveRegistered && tip != null) {
            ToolTipManager.sharedInstance().registerComponent(this);
            this.haveRegistered = true;
        }
    }
    
    public void setBackgroundAt(final int n, final Color background) {
        final Color background2 = this.pages.get(n).background;
        this.pages.get(n).setBackground(background);
        if (background == null || background2 == null || !background.equals(background2)) {
            final Rectangle bounds = this.getBoundsAt(n);
            if (bounds != null) {
                this.repaint(bounds);
            }
        }
    }
    
    public void setForegroundAt(final int n, final Color foreground) {
        final Color foreground2 = this.pages.get(n).foreground;
        this.pages.get(n).setForeground(foreground);
        if (foreground == null || foreground2 == null || !foreground.equals(foreground2)) {
            final Rectangle bounds = this.getBoundsAt(n);
            if (bounds != null) {
                this.repaint(bounds);
            }
        }
    }
    
    public void setEnabledAt(final int n, final boolean enabled) {
        final boolean enabled2 = this.pages.get(n).isEnabled();
        this.pages.get(n).setEnabled(enabled);
        if (enabled != enabled2) {
            this.revalidate();
            this.repaint();
        }
    }
    
    public void setComponentAt(final int n, final Component component) {
        final Page page = this.pages.get(n);
        if (component != page.component) {
            int n2 = 0;
            if (page.component != null) {
                n2 = ((SwingUtilities.findFocusOwner(page.component) != null) ? 1 : 0);
                synchronized (this.getTreeLock()) {
                    final int componentCount = this.getComponentCount();
                    final Component[] components = this.getComponents();
                    for (int i = 0; i < componentCount; ++i) {
                        if (components[i] == page.component) {
                            super.remove(i);
                        }
                    }
                }
            }
            page.component = component;
            final boolean visible = this.getSelectedIndex() == n;
            if (visible) {
                this.visComp = component;
            }
            if (component != null) {
                component.setVisible(visible);
                this.addImpl(component, null, -1);
                if (n2 != 0) {
                    SwingUtilities2.tabbedPaneChangeFocusTo(component);
                }
            }
            else {
                this.repaint();
            }
            this.revalidate();
        }
    }
    
    public void setDisplayedMnemonicIndexAt(final int n, final int displayedMnemonicIndex) {
        this.checkIndex(n);
        this.pages.get(n).setDisplayedMnemonicIndex(displayedMnemonicIndex);
    }
    
    public void setMnemonicAt(final int n, final int mnemonic) {
        this.checkIndex(n);
        this.pages.get(n).setMnemonic(mnemonic);
        this.firePropertyChange("mnemonicAt", null, null);
    }
    
    public int indexOfTab(final String s) {
        for (int i = 0; i < this.getTabCount(); ++i) {
            if (this.getTitleAt(i).equals((s == null) ? "" : s)) {
                return i;
            }
        }
        return -1;
    }
    
    public int indexOfTab(final Icon icon) {
        for (int i = 0; i < this.getTabCount(); ++i) {
            final Icon icon2 = this.getIconAt(i);
            if ((icon2 != null && icon2.equals(icon)) || (icon2 == null && icon2 == icon)) {
                return i;
            }
        }
        return -1;
    }
    
    public int indexOfComponent(final Component component) {
        for (int i = 0; i < this.getTabCount(); ++i) {
            final Component component2 = this.getComponentAt(i);
            if ((component2 != null && component2.equals(component)) || (component2 == null && component2 == component)) {
                return i;
            }
        }
        return -1;
    }
    
    public int indexAtLocation(final int n, final int n2) {
        if (this.ui != null) {
            return ((TabbedPaneUI)this.ui).tabForCoordinate(this, n, n2);
        }
        return -1;
    }
    
    @Override
    public String getToolTipText(final MouseEvent mouseEvent) {
        if (this.ui != null) {
            final int tabForCoordinate = ((TabbedPaneUI)this.ui).tabForCoordinate(this, mouseEvent.getX(), mouseEvent.getY());
            if (tabForCoordinate != -1) {
                return this.pages.get(tabForCoordinate).tip;
            }
        }
        return super.getToolTipText(mouseEvent);
    }
    
    private void checkIndex(final int n) {
        if (n < 0 || n >= this.pages.size()) {
            throw new IndexOutOfBoundsException("Index: " + n + ", Tab count: " + this.pages.size());
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.getUIClassID().equals("TabbedPaneUI")) {
            final byte b = (byte)(JComponent.getWriteObjCounter(this) - 1);
            JComponent.setWriteObjCounter(this, b);
            if (b == 0 && this.ui != null) {
                this.ui.installUI(this);
            }
        }
    }
    
    @Override
    void compWriteObjectNotify() {
        super.compWriteObjectNotify();
        if (this.getToolTipText() == null && this.haveRegistered) {
            ToolTipManager.sharedInstance().unregisterComponent(this);
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        if (this.ui != null && this.getUIClassID().equals("TabbedPaneUI")) {
            this.ui.installUI(this);
        }
        if (this.getToolTipText() == null && this.haveRegistered) {
            ToolTipManager.sharedInstance().registerComponent(this);
        }
    }
    
    @Override
    protected String paramString() {
        String s;
        if (this.tabPlacement == 1) {
            s = "TOP";
        }
        else if (this.tabPlacement == 3) {
            s = "BOTTOM";
        }
        else if (this.tabPlacement == 2) {
            s = "LEFT";
        }
        else if (this.tabPlacement == 4) {
            s = "RIGHT";
        }
        else {
            s = "";
        }
        return super.paramString() + ",haveRegistered=" + (this.haveRegistered ? "true" : "false") + ",tabPlacement=" + s;
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJTabbedPane();
            for (int tabCount = this.getTabCount(), i = 0; i < tabCount; ++i) {
                this.pages.get(i).initAccessibleContext();
            }
        }
        return this.accessibleContext;
    }
    
    public void setTabComponentAt(final int n, final Component tabComponent) {
        if (tabComponent != null && this.indexOfComponent(tabComponent) != -1) {
            throw new IllegalArgumentException("Component is already added to this JTabbedPane");
        }
        if (tabComponent != this.getTabComponentAt(n)) {
            final int indexOfTabComponent = this.indexOfTabComponent(tabComponent);
            if (indexOfTabComponent != -1) {
                this.setTabComponentAt(indexOfTabComponent, null);
            }
            this.pages.get(n).tabComponent = tabComponent;
            this.firePropertyChange("indexForTabComponent", -1, n);
        }
    }
    
    public Component getTabComponentAt(final int n) {
        return this.pages.get(n).tabComponent;
    }
    
    public int indexOfTabComponent(final Component component) {
        for (int i = 0; i < this.getTabCount(); ++i) {
            if (this.getTabComponentAt(i) == component) {
                return i;
            }
        }
        return -1;
    }
    
    protected class ModelListener implements ChangeListener, Serializable
    {
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            JTabbedPane.this.fireStateChanged();
        }
    }
    
    protected class AccessibleJTabbedPane extends AccessibleJComponent implements AccessibleSelection, ChangeListener
    {
        @Override
        public String getAccessibleName() {
            if (this.accessibleName != null) {
                return this.accessibleName;
            }
            final String s = (String)JTabbedPane.this.getClientProperty("AccessibleName");
            if (s != null) {
                return s;
            }
            final int selectedIndex = JTabbedPane.this.getSelectedIndex();
            if (selectedIndex >= 0) {
                return ((Page)JTabbedPane.this.pages.get(selectedIndex)).getAccessibleName();
            }
            return super.getAccessibleName();
        }
        
        public AccessibleJTabbedPane() {
            JTabbedPane.this.model.addChangeListener(this);
        }
        
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            this.firePropertyChange("AccessibleSelection", null, changeEvent.getSource());
        }
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.PAGE_TAB_LIST;
        }
        
        @Override
        public int getAccessibleChildrenCount() {
            return JTabbedPane.this.getTabCount();
        }
        
        @Override
        public Accessible getAccessibleChild(final int n) {
            if (n < 0 || n >= JTabbedPane.this.getTabCount()) {
                return null;
            }
            return JTabbedPane.this.pages.get(n);
        }
        
        @Override
        public AccessibleSelection getAccessibleSelection() {
            return this;
        }
        
        @Override
        public Accessible getAccessibleAt(final Point point) {
            int n = ((TabbedPaneUI)JTabbedPane.this.ui).tabForCoordinate(JTabbedPane.this, point.x, point.y);
            if (n == -1) {
                n = JTabbedPane.this.getSelectedIndex();
            }
            return this.getAccessibleChild(n);
        }
        
        @Override
        public int getAccessibleSelectionCount() {
            return 1;
        }
        
        @Override
        public Accessible getAccessibleSelection(final int n) {
            final int selectedIndex = JTabbedPane.this.getSelectedIndex();
            if (selectedIndex == -1) {
                return null;
            }
            return (Accessible)JTabbedPane.this.pages.get(selectedIndex);
        }
        
        @Override
        public boolean isAccessibleChildSelected(final int n) {
            return n == JTabbedPane.this.getSelectedIndex();
        }
        
        @Override
        public void addAccessibleSelection(final int selectedIndex) {
            JTabbedPane.this.setSelectedIndex(selectedIndex);
        }
        
        @Override
        public void removeAccessibleSelection(final int n) {
        }
        
        @Override
        public void clearAccessibleSelection() {
        }
        
        @Override
        public void selectAllAccessibleSelection() {
        }
    }
    
    private class Page extends AccessibleContext implements Serializable, Accessible, AccessibleComponent
    {
        String title;
        Color background;
        Color foreground;
        Icon icon;
        Icon disabledIcon;
        JTabbedPane parent;
        Component component;
        String tip;
        boolean enabled;
        boolean needsUIUpdate;
        int mnemonic;
        int mnemonicIndex;
        Component tabComponent;
        
        Page(final JTabbedPane parent, final String title, final Icon icon, final Icon disabledIcon, final Component component, final String tip) {
            this.enabled = true;
            this.mnemonic = -1;
            this.mnemonicIndex = -1;
            this.title = title;
            this.icon = icon;
            this.disabledIcon = disabledIcon;
            this.setAccessibleParent(this.parent = parent);
            this.component = component;
            this.tip = tip;
            this.initAccessibleContext();
        }
        
        void initAccessibleContext() {
            if (JTabbedPane.this.accessibleContext != null && this.component instanceof Accessible) {
                final AccessibleContext accessibleContext = this.component.getAccessibleContext();
                if (accessibleContext != null) {
                    accessibleContext.setAccessibleParent(this);
                }
            }
        }
        
        void setMnemonic(final int mnemonic) {
            this.mnemonic = mnemonic;
            this.updateDisplayedMnemonicIndex();
        }
        
        int getMnemonic() {
            return this.mnemonic;
        }
        
        void setDisplayedMnemonicIndex(final int mnemonicIndex) {
            if (this.mnemonicIndex != mnemonicIndex) {
                if (mnemonicIndex != -1 && (this.title == null || mnemonicIndex < 0 || mnemonicIndex >= this.title.length())) {
                    throw new IllegalArgumentException("Invalid mnemonic index: " + mnemonicIndex);
                }
                this.mnemonicIndex = mnemonicIndex;
                Component.this.firePropertyChange("displayedMnemonicIndexAt", null, null);
            }
        }
        
        int getDisplayedMnemonicIndex() {
            return this.mnemonicIndex;
        }
        
        void updateDisplayedMnemonicIndex() {
            this.setDisplayedMnemonicIndex(SwingUtilities.findDisplayedMnemonicIndex(this.title, this.mnemonic));
        }
        
        @Override
        public AccessibleContext getAccessibleContext() {
            return this;
        }
        
        @Override
        public String getAccessibleName() {
            if (this.accessibleName != null) {
                return this.accessibleName;
            }
            if (this.title != null) {
                return this.title;
            }
            return null;
        }
        
        @Override
        public String getAccessibleDescription() {
            if (this.accessibleDescription != null) {
                return this.accessibleDescription;
            }
            if (this.tip != null) {
                return this.tip;
            }
            return null;
        }
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.PAGE_TAB;
        }
        
        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            final AccessibleStateSet accessibleStateSet = this.parent.getAccessibleContext().getAccessibleStateSet();
            accessibleStateSet.add(AccessibleState.SELECTABLE);
            if (this.parent.indexOfTab(this.title) == this.parent.getSelectedIndex()) {
                accessibleStateSet.add(AccessibleState.SELECTED);
            }
            return accessibleStateSet;
        }
        
        @Override
        public int getAccessibleIndexInParent() {
            return this.parent.indexOfTab(this.title);
        }
        
        @Override
        public int getAccessibleChildrenCount() {
            if (this.component instanceof Accessible) {
                return 1;
            }
            return 0;
        }
        
        @Override
        public Accessible getAccessibleChild(final int n) {
            if (this.component instanceof Accessible) {
                return (Accessible)this.component;
            }
            return null;
        }
        
        @Override
        public Locale getLocale() {
            return this.parent.getLocale();
        }
        
        @Override
        public AccessibleComponent getAccessibleComponent() {
            return this;
        }
        
        @Override
        public Color getBackground() {
            return (this.background != null) ? this.background : this.parent.getBackground();
        }
        
        @Override
        public void setBackground(final Color background) {
            this.background = background;
        }
        
        @Override
        public Color getForeground() {
            return (this.foreground != null) ? this.foreground : this.parent.getForeground();
        }
        
        @Override
        public void setForeground(final Color foreground) {
            this.foreground = foreground;
        }
        
        @Override
        public Cursor getCursor() {
            return this.parent.getCursor();
        }
        
        @Override
        public void setCursor(final Cursor cursor) {
            this.parent.setCursor(cursor);
        }
        
        @Override
        public Font getFont() {
            return this.parent.getFont();
        }
        
        @Override
        public void setFont(final Font font) {
            this.parent.setFont(font);
        }
        
        @Override
        public FontMetrics getFontMetrics(final Font font) {
            return this.parent.getFontMetrics(font);
        }
        
        @Override
        public boolean isEnabled() {
            return this.enabled;
        }
        
        @Override
        public void setEnabled(final boolean enabled) {
            this.enabled = enabled;
        }
        
        @Override
        public boolean isVisible() {
            return this.parent.isVisible();
        }
        
        @Override
        public void setVisible(final boolean visible) {
            this.parent.setVisible(visible);
        }
        
        @Override
        public boolean isShowing() {
            return this.parent.isShowing();
        }
        
        @Override
        public boolean contains(final Point point) {
            return this.getBounds().contains(point);
        }
        
        @Override
        public Point getLocationOnScreen() {
            final Point locationOnScreen = this.parent.getLocationOnScreen();
            final Point location = this.getLocation();
            location.translate(locationOnScreen.x, locationOnScreen.y);
            return location;
        }
        
        @Override
        public Point getLocation() {
            final Rectangle bounds = this.getBounds();
            return new Point(bounds.x, bounds.y);
        }
        
        @Override
        public void setLocation(final Point point) {
        }
        
        @Override
        public Rectangle getBounds() {
            return this.parent.getUI().getTabBounds(this.parent, this.parent.indexOfTab(this.title));
        }
        
        @Override
        public void setBounds(final Rectangle rectangle) {
        }
        
        @Override
        public Dimension getSize() {
            final Rectangle bounds = this.getBounds();
            return new Dimension(bounds.width, bounds.height);
        }
        
        @Override
        public void setSize(final Dimension dimension) {
        }
        
        @Override
        public Accessible getAccessibleAt(final Point point) {
            if (this.component instanceof Accessible) {
                return (Accessible)this.component;
            }
            return null;
        }
        
        @Override
        public boolean isFocusTraversable() {
            return false;
        }
        
        @Override
        public void requestFocus() {
        }
        
        @Override
        public void addFocusListener(final FocusListener focusListener) {
        }
        
        @Override
        public void removeFocusListener(final FocusListener focusListener) {
        }
        
        @Override
        public AccessibleIcon[] getAccessibleIcon() {
            AccessibleIcon accessibleIcon = null;
            if (this.enabled && this.icon instanceof ImageIcon) {
                accessibleIcon = (AccessibleIcon)((ImageIcon)this.icon).getAccessibleContext();
            }
            else if (!this.enabled && this.disabledIcon instanceof ImageIcon) {
                accessibleIcon = (AccessibleIcon)((ImageIcon)this.disabledIcon).getAccessibleContext();
            }
            if (accessibleIcon != null) {
                return new AccessibleIcon[] { accessibleIcon };
            }
            return null;
        }
    }
}
