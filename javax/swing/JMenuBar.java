package javax.swing;

import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleSelection;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import javax.accessibility.AccessibleContext;
import java.util.Vector;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.Graphics;
import java.beans.Transient;
import java.awt.Component;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.MenuBarUI;
import java.awt.Insets;
import javax.accessibility.Accessible;

public class JMenuBar extends JComponent implements Accessible, MenuElement
{
    private static final String uiClassID = "MenuBarUI";
    private transient SingleSelectionModel selectionModel;
    private boolean paintBorder;
    private Insets margin;
    private static final boolean TRACE = false;
    private static final boolean VERBOSE = false;
    private static final boolean DEBUG = false;
    
    public JMenuBar() {
        this.paintBorder = true;
        this.margin = null;
        this.setFocusTraversalKeysEnabled(false);
        this.setSelectionModel(new DefaultSingleSelectionModel());
        this.updateUI();
    }
    
    public MenuBarUI getUI() {
        return (MenuBarUI)this.ui;
    }
    
    public void setUI(final MenuBarUI ui) {
        super.setUI(ui);
    }
    
    @Override
    public void updateUI() {
        this.setUI((MenuBarUI)UIManager.getUI(this));
    }
    
    @Override
    public String getUIClassID() {
        return "MenuBarUI";
    }
    
    public SingleSelectionModel getSelectionModel() {
        return this.selectionModel;
    }
    
    public void setSelectionModel(final SingleSelectionModel selectionModel) {
        this.firePropertyChange("selectionModel", this.selectionModel, this.selectionModel = selectionModel);
    }
    
    public JMenu add(final JMenu menu) {
        super.add(menu);
        return menu;
    }
    
    public JMenu getMenu(final int n) {
        final Component componentAtIndex = this.getComponentAtIndex(n);
        if (componentAtIndex instanceof JMenu) {
            return (JMenu)componentAtIndex;
        }
        return null;
    }
    
    public int getMenuCount() {
        return this.getComponentCount();
    }
    
    public void setHelpMenu(final JMenu menu) {
        throw new Error("setHelpMenu() not yet implemented.");
    }
    
    @Transient
    public JMenu getHelpMenu() {
        throw new Error("getHelpMenu() not yet implemented.");
    }
    
    @Deprecated
    public Component getComponentAtIndex(final int n) {
        if (n < 0 || n >= this.getComponentCount()) {
            return null;
        }
        return this.getComponent(n);
    }
    
    public int getComponentIndex(final Component component) {
        final int componentCount = this.getComponentCount();
        final Component[] components = this.getComponents();
        for (int i = 0; i < componentCount; ++i) {
            if (components[i] == component) {
                return i;
            }
        }
        return -1;
    }
    
    public void setSelected(final Component component) {
        this.getSelectionModel().setSelectedIndex(this.getComponentIndex(component));
    }
    
    public boolean isSelected() {
        return this.selectionModel.isSelected();
    }
    
    public boolean isBorderPainted() {
        return this.paintBorder;
    }
    
    public void setBorderPainted(final boolean paintBorder) {
        final boolean paintBorder2 = this.paintBorder;
        this.firePropertyChange("borderPainted", paintBorder2, this.paintBorder = paintBorder);
        if (paintBorder != paintBorder2) {
            this.revalidate();
            this.repaint();
        }
    }
    
    @Override
    protected void paintBorder(final Graphics graphics) {
        if (this.isBorderPainted()) {
            super.paintBorder(graphics);
        }
    }
    
    public void setMargin(final Insets margin) {
        final Insets margin2 = this.margin;
        this.firePropertyChange("margin", margin2, this.margin = margin);
        if (margin2 == null || !margin2.equals(margin)) {
            this.revalidate();
            this.repaint();
        }
    }
    
    public Insets getMargin() {
        if (this.margin == null) {
            return new Insets(0, 0, 0, 0);
        }
        return this.margin;
    }
    
    @Override
    public void processMouseEvent(final MouseEvent mouseEvent, final MenuElement[] array, final MenuSelectionManager menuSelectionManager) {
    }
    
    @Override
    public void processKeyEvent(final KeyEvent keyEvent, final MenuElement[] array, final MenuSelectionManager menuSelectionManager) {
    }
    
    @Override
    public void menuSelectionChanged(final boolean b) {
    }
    
    @Override
    public MenuElement[] getSubElements() {
        final Vector vector = new Vector();
        for (int componentCount = this.getComponentCount(), i = 0; i < componentCount; ++i) {
            final Component component = this.getComponent(i);
            if (component instanceof MenuElement) {
                vector.addElement(component);
            }
        }
        final MenuElement[] array = new MenuElement[vector.size()];
        for (int j = 0; j < vector.size(); ++j) {
            array[j] = (MenuElement)vector.elementAt(j);
        }
        return array;
    }
    
    @Override
    public Component getComponent() {
        return this;
    }
    
    @Override
    protected String paramString() {
        return super.paramString() + ",margin=" + ((this.margin != null) ? this.margin.toString() : "") + ",paintBorder=" + (this.paintBorder ? "true" : "false");
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJMenuBar();
        }
        return this.accessibleContext;
    }
    
    @Override
    protected boolean processKeyBinding(final KeyStroke keyStroke, final KeyEvent keyEvent, final int n, final boolean b) {
        final boolean processKeyBinding = super.processKeyBinding(keyStroke, keyEvent, n, b);
        if (!processKeyBinding) {
            final MenuElement[] subElements = this.getSubElements();
            for (int length = subElements.length, i = 0; i < length; ++i) {
                if (processBindingForKeyStrokeRecursive(subElements[i], keyStroke, keyEvent, n, b)) {
                    return true;
                }
            }
        }
        return processKeyBinding;
    }
    
    static boolean processBindingForKeyStrokeRecursive(final MenuElement menuElement, final KeyStroke keyStroke, final KeyEvent keyEvent, final int n, final boolean b) {
        if (menuElement == null) {
            return false;
        }
        final Component component = menuElement.getComponent();
        if ((!component.isVisible() && !(component instanceof JPopupMenu)) || !component.isEnabled()) {
            return false;
        }
        if (component != null && component instanceof JComponent && ((JComponent)component).processKeyBinding(keyStroke, keyEvent, n, b)) {
            return true;
        }
        final MenuElement[] subElements = menuElement.getSubElements();
        for (int length = subElements.length, i = 0; i < length; ++i) {
            if (processBindingForKeyStrokeRecursive(subElements[i], keyStroke, keyEvent, n, b)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        KeyboardManager.getCurrentManager().registerMenuBar(this);
    }
    
    @Override
    public void removeNotify() {
        super.removeNotify();
        KeyboardManager.getCurrentManager().unregisterMenuBar(this);
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.getUIClassID().equals("MenuBarUI")) {
            final byte b = (byte)(JComponent.getWriteObjCounter(this) - 1);
            JComponent.setWriteObjCounter(this, b);
            if (b == 0 && this.ui != null) {
                this.ui.installUI(this);
            }
        }
        final Object[] array = new Object[4];
        int n = 0;
        if (this.selectionModel instanceof Serializable) {
            array[n++] = "selectionModel";
            array[n++] = this.selectionModel;
        }
        objectOutputStream.writeObject(array);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        final Object[] array = (Object[])objectInputStream.readObject();
        for (int n = 0; n < array.length && array[n] != null; n += 2) {
            if (array[n].equals("selectionModel")) {
                this.selectionModel = (SingleSelectionModel)array[n + 1];
            }
        }
    }
    
    protected class AccessibleJMenuBar extends AccessibleJComponent implements AccessibleSelection
    {
        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            return super.getAccessibleStateSet();
        }
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.MENU_BAR;
        }
        
        @Override
        public AccessibleSelection getAccessibleSelection() {
            return this;
        }
        
        @Override
        public int getAccessibleSelectionCount() {
            if (JMenuBar.this.isSelected()) {
                return 1;
            }
            return 0;
        }
        
        @Override
        public Accessible getAccessibleSelection(final int n) {
            if (JMenuBar.this.isSelected()) {
                if (n != 0) {
                    return null;
                }
                final int selectedIndex = JMenuBar.this.getSelectionModel().getSelectedIndex();
                if (JMenuBar.this.getComponentAtIndex(selectedIndex) instanceof Accessible) {
                    return (Accessible)JMenuBar.this.getComponentAtIndex(selectedIndex);
                }
            }
            return null;
        }
        
        @Override
        public boolean isAccessibleChildSelected(final int n) {
            return n == JMenuBar.this.getSelectionModel().getSelectedIndex();
        }
        
        @Override
        public void addAccessibleSelection(final int selectedIndex) {
            final int selectedIndex2 = JMenuBar.this.getSelectionModel().getSelectedIndex();
            if (selectedIndex == selectedIndex2) {
                return;
            }
            if (selectedIndex2 >= 0 && selectedIndex2 < JMenuBar.this.getMenuCount() && JMenuBar.this.getMenu(selectedIndex2) != null) {
                MenuSelectionManager.defaultManager().setSelectedPath(null);
            }
            JMenuBar.this.getSelectionModel().setSelectedIndex(selectedIndex);
            final JMenu menu = JMenuBar.this.getMenu(selectedIndex);
            if (menu != null) {
                MenuSelectionManager.defaultManager().setSelectedPath(new MenuElement[] { JMenuBar.this, menu, menu.getPopupMenu() });
            }
        }
        
        @Override
        public void removeAccessibleSelection(final int n) {
            if (n >= 0 && n < JMenuBar.this.getMenuCount()) {
                if (JMenuBar.this.getMenu(n) != null) {
                    MenuSelectionManager.defaultManager().setSelectedPath(null);
                }
                JMenuBar.this.getSelectionModel().setSelectedIndex(-1);
            }
        }
        
        @Override
        public void clearAccessibleSelection() {
            final int selectedIndex = JMenuBar.this.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < JMenuBar.this.getMenuCount() && JMenuBar.this.getMenu(selectedIndex) != null) {
                MenuSelectionManager.defaultManager().setSelectedPath(null);
            }
            JMenuBar.this.getSelectionModel().setSelectedIndex(-1);
        }
        
        @Override
        public void selectAllAccessibleSelection() {
        }
    }
}
