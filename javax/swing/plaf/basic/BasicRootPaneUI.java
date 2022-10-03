package javax.swing.plaf.basic;

import javax.swing.plaf.ComponentInputMapUIResource;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import java.awt.Rectangle;
import javax.swing.JPopupMenu;
import javax.swing.JButton;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import sun.swing.UIAction;
import java.beans.PropertyChangeEvent;
import javax.swing.Action;
import javax.swing.ComponentInputMap;
import sun.swing.DefaultLookup;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.SwingUtilities;
import javax.swing.LookAndFeel;
import javax.swing.JRootPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.beans.PropertyChangeListener;
import javax.swing.plaf.RootPaneUI;

public class BasicRootPaneUI extends RootPaneUI implements PropertyChangeListener
{
    private static RootPaneUI rootPaneUI;
    
    public static ComponentUI createUI(final JComponent component) {
        return BasicRootPaneUI.rootPaneUI;
    }
    
    @Override
    public void installUI(final JComponent component) {
        this.installDefaults((JRootPane)component);
        this.installComponents((JRootPane)component);
        this.installListeners((JRootPane)component);
        this.installKeyboardActions((JRootPane)component);
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        this.uninstallDefaults((JRootPane)component);
        this.uninstallComponents((JRootPane)component);
        this.uninstallListeners((JRootPane)component);
        this.uninstallKeyboardActions((JRootPane)component);
    }
    
    protected void installDefaults(final JRootPane rootPane) {
        LookAndFeel.installProperty(rootPane, "opaque", Boolean.FALSE);
    }
    
    protected void installComponents(final JRootPane rootPane) {
    }
    
    protected void installListeners(final JRootPane rootPane) {
        rootPane.addPropertyChangeListener(this);
    }
    
    protected void installKeyboardActions(final JRootPane rootPane) {
        SwingUtilities.replaceUIInputMap(rootPane, 2, this.getInputMap(2, rootPane));
        SwingUtilities.replaceUIInputMap(rootPane, 1, this.getInputMap(1, rootPane));
        LazyActionMap.installLazyActionMap(rootPane, BasicRootPaneUI.class, "RootPane.actionMap");
        this.updateDefaultButtonBindings(rootPane);
    }
    
    protected void uninstallDefaults(final JRootPane rootPane) {
    }
    
    protected void uninstallComponents(final JRootPane rootPane) {
    }
    
    protected void uninstallListeners(final JRootPane rootPane) {
        rootPane.removePropertyChangeListener(this);
    }
    
    protected void uninstallKeyboardActions(final JRootPane rootPane) {
        SwingUtilities.replaceUIInputMap(rootPane, 2, null);
        SwingUtilities.replaceUIActionMap(rootPane, null);
    }
    
    InputMap getInputMap(final int n, final JComponent component) {
        if (n == 1) {
            return (InputMap)DefaultLookup.get(component, this, "RootPane.ancestorInputMap");
        }
        if (n == 2) {
            return this.createInputMap(n, component);
        }
        return null;
    }
    
    ComponentInputMap createInputMap(final int n, final JComponent component) {
        return new RootPaneInputMap(component);
    }
    
    static void loadActionMap(final LazyActionMap lazyActionMap) {
        lazyActionMap.put(new Actions("press"));
        lazyActionMap.put(new Actions("release"));
        lazyActionMap.put(new Actions("postPopup"));
    }
    
    void updateDefaultButtonBindings(final JRootPane rootPane) {
        InputMap inputMap;
        for (inputMap = SwingUtilities.getUIInputMap(rootPane, 2); inputMap != null && !(inputMap instanceof RootPaneInputMap); inputMap = inputMap.getParent()) {}
        if (inputMap != null) {
            inputMap.clear();
            if (rootPane.getDefaultButton() != null) {
                final Object[] array = (Object[])DefaultLookup.get(rootPane, this, "RootPane.defaultButtonWindowKeyBindings");
                if (array != null) {
                    LookAndFeel.loadKeyBindings(inputMap, array);
                }
            }
        }
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getPropertyName().equals("defaultButton")) {
            final JRootPane rootPane = (JRootPane)propertyChangeEvent.getSource();
            this.updateDefaultButtonBindings(rootPane);
            if (rootPane.getClientProperty("temporaryDefaultButton") == null) {
                rootPane.putClientProperty("initialDefaultButton", propertyChangeEvent.getNewValue());
            }
        }
    }
    
    static {
        BasicRootPaneUI.rootPaneUI = new BasicRootPaneUI();
    }
    
    static class Actions extends UIAction
    {
        public static final String PRESS = "press";
        public static final String RELEASE = "release";
        public static final String POST_POPUP = "postPopup";
        
        Actions(final String s) {
            super(s);
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JRootPane rootPane = (JRootPane)actionEvent.getSource();
            final JButton defaultButton = rootPane.getDefaultButton();
            final String name = this.getName();
            if (name == "postPopup") {
                final Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                if (focusOwner instanceof JComponent) {
                    final JComponent component = (JComponent)focusOwner;
                    final JPopupMenu componentPopupMenu = component.getComponentPopupMenu();
                    if (componentPopupMenu != null) {
                        Point popupLocation = component.getPopupLocation(null);
                        if (popupLocation == null) {
                            final Rectangle visibleRect = component.getVisibleRect();
                            popupLocation = new Point(visibleRect.x + visibleRect.width / 2, visibleRect.y + visibleRect.height / 2);
                        }
                        componentPopupMenu.show(focusOwner, popupLocation.x, popupLocation.y);
                    }
                }
            }
            else if (defaultButton != null && SwingUtilities.getRootPane(defaultButton) == rootPane && name == "press") {
                defaultButton.doClick(20);
            }
        }
        
        @Override
        public boolean isEnabled(final Object o) {
            if (this.getName() == "postPopup") {
                final MenuElement[] selectedPath = MenuSelectionManager.defaultManager().getSelectedPath();
                if (selectedPath != null && selectedPath.length != 0) {
                    return false;
                }
                final Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                return focusOwner instanceof JComponent && ((JComponent)focusOwner).getComponentPopupMenu() != null;
            }
            else {
                if (o != null && o instanceof JRootPane) {
                    final JButton defaultButton = ((JRootPane)o).getDefaultButton();
                    return defaultButton != null && defaultButton.getModel().isEnabled();
                }
                return true;
            }
        }
    }
    
    private static class RootPaneInputMap extends ComponentInputMapUIResource
    {
        public RootPaneInputMap(final JComponent component) {
            super(component);
        }
    }
}
