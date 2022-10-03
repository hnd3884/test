package javax.swing.plaf.basic;

import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import java.awt.event.ActionEvent;
import sun.swing.UIAction;
import java.awt.Component;
import java.awt.event.ContainerEvent;
import javax.swing.event.ChangeEvent;
import java.awt.Dimension;
import javax.swing.ActionMap;
import sun.swing.DefaultLookup;
import javax.swing.InputMap;
import javax.swing.SwingUtilities;
import javax.swing.JMenu;
import javax.swing.LookAndFeel;
import java.awt.LayoutManager;
import java.awt.Container;
import javax.swing.plaf.UIResource;
import javax.swing.Action;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import java.awt.event.ContainerListener;
import javax.swing.JMenuBar;
import javax.swing.plaf.MenuBarUI;

public class BasicMenuBarUI extends MenuBarUI
{
    protected JMenuBar menuBar;
    protected ContainerListener containerListener;
    protected ChangeListener changeListener;
    private Handler handler;
    
    public BasicMenuBarUI() {
        this.menuBar = null;
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new BasicMenuBarUI();
    }
    
    static void loadActionMap(final LazyActionMap lazyActionMap) {
        lazyActionMap.put(new Actions("takeFocus"));
    }
    
    @Override
    public void installUI(final JComponent component) {
        this.menuBar = (JMenuBar)component;
        this.installDefaults();
        this.installListeners();
        this.installKeyboardActions();
    }
    
    protected void installDefaults() {
        if (this.menuBar.getLayout() == null || this.menuBar.getLayout() instanceof UIResource) {
            this.menuBar.setLayout(new DefaultMenuLayout(this.menuBar, 2));
        }
        LookAndFeel.installProperty(this.menuBar, "opaque", Boolean.TRUE);
        LookAndFeel.installBorder(this.menuBar, "MenuBar.border");
        LookAndFeel.installColorsAndFont(this.menuBar, "MenuBar.background", "MenuBar.foreground", "MenuBar.font");
    }
    
    protected void installListeners() {
        this.containerListener = this.createContainerListener();
        this.changeListener = this.createChangeListener();
        for (int i = 0; i < this.menuBar.getMenuCount(); ++i) {
            final JMenu menu = this.menuBar.getMenu(i);
            if (menu != null) {
                menu.getModel().addChangeListener(this.changeListener);
            }
        }
        this.menuBar.addContainerListener(this.containerListener);
    }
    
    protected void installKeyboardActions() {
        SwingUtilities.replaceUIInputMap(this.menuBar, 2, this.getInputMap(2));
        LazyActionMap.installLazyActionMap(this.menuBar, BasicMenuBarUI.class, "MenuBar.actionMap");
    }
    
    InputMap getInputMap(final int n) {
        if (n == 2) {
            final Object[] array = (Object[])DefaultLookup.get(this.menuBar, this, "MenuBar.windowBindings");
            if (array != null) {
                return LookAndFeel.makeComponentInputMap(this.menuBar, array);
            }
        }
        return null;
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        this.uninstallDefaults();
        this.uninstallListeners();
        this.uninstallKeyboardActions();
        this.menuBar = null;
    }
    
    protected void uninstallDefaults() {
        if (this.menuBar != null) {
            LookAndFeel.uninstallBorder(this.menuBar);
        }
    }
    
    protected void uninstallListeners() {
        this.menuBar.removeContainerListener(this.containerListener);
        for (int i = 0; i < this.menuBar.getMenuCount(); ++i) {
            final JMenu menu = this.menuBar.getMenu(i);
            if (menu != null) {
                menu.getModel().removeChangeListener(this.changeListener);
            }
        }
        this.containerListener = null;
        this.changeListener = null;
        this.handler = null;
    }
    
    protected void uninstallKeyboardActions() {
        SwingUtilities.replaceUIInputMap(this.menuBar, 2, null);
        SwingUtilities.replaceUIActionMap(this.menuBar, null);
    }
    
    protected ContainerListener createContainerListener() {
        return this.getHandler();
    }
    
    protected ChangeListener createChangeListener() {
        return this.getHandler();
    }
    
    private Handler getHandler() {
        if (this.handler == null) {
            this.handler = new Handler();
        }
        return this.handler;
    }
    
    @Override
    public Dimension getMinimumSize(final JComponent component) {
        return null;
    }
    
    @Override
    public Dimension getMaximumSize(final JComponent component) {
        return null;
    }
    
    private class Handler implements ChangeListener, ContainerListener
    {
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            for (int i = 0; i < BasicMenuBarUI.this.menuBar.getMenuCount(); ++i) {
                final JMenu menu = BasicMenuBarUI.this.menuBar.getMenu(i);
                if (menu != null && menu.isSelected()) {
                    BasicMenuBarUI.this.menuBar.getSelectionModel().setSelectedIndex(i);
                    break;
                }
            }
        }
        
        @Override
        public void componentAdded(final ContainerEvent containerEvent) {
            final Component child = containerEvent.getChild();
            if (child instanceof JMenu) {
                ((JMenu)child).getModel().addChangeListener(BasicMenuBarUI.this.changeListener);
            }
        }
        
        @Override
        public void componentRemoved(final ContainerEvent containerEvent) {
            final Component child = containerEvent.getChild();
            if (child instanceof JMenu) {
                ((JMenu)child).getModel().removeChangeListener(BasicMenuBarUI.this.changeListener);
            }
        }
    }
    
    private static class Actions extends UIAction
    {
        private static final String TAKE_FOCUS = "takeFocus";
        
        Actions(final String s) {
            super(s);
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JMenuBar menuBar = (JMenuBar)actionEvent.getSource();
            final MenuSelectionManager defaultManager = MenuSelectionManager.defaultManager();
            final JMenu menu = menuBar.getMenu(0);
            if (menu != null) {
                defaultManager.setSelectedPath(new MenuElement[] { menuBar, menu, menu.getPopupMenu() });
            }
        }
    }
}
