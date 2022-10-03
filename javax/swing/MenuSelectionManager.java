package javax.swing;

import java.awt.event.KeyEvent;
import java.awt.Rectangle;
import java.awt.Component;
import java.awt.Point;
import sun.awt.AWTAccessor;
import java.awt.event.MouseEvent;
import javax.swing.event.ChangeListener;
import sun.swing.SwingUtilities2;
import sun.awt.AppContext;
import javax.swing.event.EventListenerList;
import javax.swing.event.ChangeEvent;
import java.util.Vector;

public class MenuSelectionManager
{
    private Vector<MenuElement> selection;
    private static final boolean TRACE = false;
    private static final boolean VERBOSE = false;
    private static final boolean DEBUG = false;
    private static final StringBuilder MENU_SELECTION_MANAGER_KEY;
    protected transient ChangeEvent changeEvent;
    protected EventListenerList listenerList;
    
    public MenuSelectionManager() {
        this.selection = new Vector<MenuElement>();
        this.changeEvent = null;
        this.listenerList = new EventListenerList();
    }
    
    public static MenuSelectionManager defaultManager() {
        synchronized (MenuSelectionManager.MENU_SELECTION_MANAGER_KEY) {
            final AppContext appContext = AppContext.getAppContext();
            MenuSelectionManager menuSelectionManager = (MenuSelectionManager)appContext.get(MenuSelectionManager.MENU_SELECTION_MANAGER_KEY);
            if (menuSelectionManager == null) {
                menuSelectionManager = new MenuSelectionManager();
                appContext.put(MenuSelectionManager.MENU_SELECTION_MANAGER_KEY, menuSelectionManager);
                final Object value = appContext.get(SwingUtilities2.MENU_SELECTION_MANAGER_LISTENER_KEY);
                if (value != null && value instanceof ChangeListener) {
                    menuSelectionManager.addChangeListener((ChangeListener)value);
                }
            }
            return menuSelectionManager;
        }
    }
    
    public void setSelectedPath(MenuElement[] array) {
        final int size = this.selection.size();
        int n = 0;
        if (array == null) {
            array = new MenuElement[0];
        }
        for (int n2 = 0; n2 < array.length && n2 < size && this.selection.elementAt(n2) == array[n2]; ++n2) {
            ++n;
        }
        for (int i = size - 1; i >= n; --i) {
            final MenuElement menuElement = this.selection.elementAt(i);
            this.selection.removeElementAt(i);
            menuElement.menuSelectionChanged(false);
        }
        for (int j = n; j < array.length; ++j) {
            if (array[j] != null) {
                this.selection.addElement(array[j]);
                array[j].menuSelectionChanged(true);
            }
        }
        this.fireStateChanged();
    }
    
    public MenuElement[] getSelectedPath() {
        final MenuElement[] array = new MenuElement[this.selection.size()];
        for (int i = 0; i < this.selection.size(); ++i) {
            array[i] = this.selection.elementAt(i);
        }
        return array;
    }
    
    public void clearSelectedPath() {
        if (this.selection.size() > 0) {
            this.setSelectedPath(null);
        }
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
    
    public void processMouseEvent(final MouseEvent mouseEvent) {
        final Point point = mouseEvent.getPoint();
        final Component component = mouseEvent.getComponent();
        if (component != null && !component.isShowing()) {
            return;
        }
        final int id = mouseEvent.getID();
        final int modifiers = mouseEvent.getModifiers();
        if ((id == 504 || id == 505) && (modifiers & 0x1C) != 0x0) {
            return;
        }
        if (component != null) {
            SwingUtilities.convertPointToScreen(point, component);
        }
        final int x = point.x;
        final int y = point.y;
        final Vector vector = (Vector)this.selection.clone();
        final int size = vector.size();
        for (int n = 0, n2 = size - 1; n2 >= 0 && n == 0; --n2) {
            final MenuElement[] subElements = vector.elementAt(n2).getSubElements();
            MenuElement[] array = null;
            for (int n3 = 0; n3 < subElements.length && n == 0; ++n3) {
                if (subElements[n3] != null) {
                    final Component component2 = subElements[n3].getComponent();
                    if (component2.isShowing()) {
                        int n4;
                        int n5;
                        if (component2 instanceof JComponent) {
                            n4 = component2.getWidth();
                            n5 = component2.getHeight();
                        }
                        else {
                            final Rectangle bounds = component2.getBounds();
                            n4 = bounds.width;
                            n5 = bounds.height;
                        }
                        point.x = x;
                        point.y = y;
                        SwingUtilities.convertPointFromScreen(point, component2);
                        if (point.x >= 0 && point.x < n4 && point.y >= 0 && point.y < n5) {
                            if (array == null) {
                                array = new MenuElement[n2 + 2];
                                for (int i = 0; i <= n2; ++i) {
                                    array[i] = (MenuElement)vector.elementAt(i);
                                }
                            }
                            array[n2 + 1] = subElements[n3];
                            final MenuElement[] selectedPath = this.getSelectedPath();
                            if (selectedPath[selectedPath.length - 1] != array[n2 + 1] && (selectedPath.length < 2 || selectedPath[selectedPath.length - 2] != array[n2 + 1])) {
                                final MouseEvent mouseEvent2 = new MouseEvent(selectedPath[selectedPath.length - 1].getComponent(), 505, mouseEvent.getWhen(), mouseEvent.getModifiers(), point.x, point.y, mouseEvent.getXOnScreen(), mouseEvent.getYOnScreen(), mouseEvent.getClickCount(), mouseEvent.isPopupTrigger(), 0);
                                final AWTAccessor.MouseEventAccessor mouseEventAccessor = AWTAccessor.getMouseEventAccessor();
                                mouseEventAccessor.setCausedByTouchEvent(mouseEvent2, mouseEventAccessor.isCausedByTouchEvent(mouseEvent));
                                selectedPath[selectedPath.length - 1].processMouseEvent(mouseEvent2, array, this);
                                final MouseEvent mouseEvent3 = new MouseEvent(component2, 504, mouseEvent.getWhen(), mouseEvent.getModifiers(), point.x, point.y, mouseEvent.getXOnScreen(), mouseEvent.getYOnScreen(), mouseEvent.getClickCount(), mouseEvent.isPopupTrigger(), 0);
                                mouseEventAccessor.setCausedByTouchEvent(mouseEvent3, mouseEventAccessor.isCausedByTouchEvent(mouseEvent));
                                subElements[n3].processMouseEvent(mouseEvent3, array, this);
                            }
                            final MouseEvent mouseEvent4 = new MouseEvent(component2, mouseEvent.getID(), mouseEvent.getWhen(), mouseEvent.getModifiers(), point.x, point.y, mouseEvent.getXOnScreen(), mouseEvent.getYOnScreen(), mouseEvent.getClickCount(), mouseEvent.isPopupTrigger(), 0);
                            final AWTAccessor.MouseEventAccessor mouseEventAccessor2 = AWTAccessor.getMouseEventAccessor();
                            mouseEventAccessor2.setCausedByTouchEvent(mouseEvent4, mouseEventAccessor2.isCausedByTouchEvent(mouseEvent));
                            subElements[n3].processMouseEvent(mouseEvent4, array, this);
                            n = 1;
                            mouseEvent.consume();
                        }
                    }
                }
            }
        }
    }
    
    private void printMenuElementArray(final MenuElement[] array) {
        this.printMenuElementArray(array, false);
    }
    
    private void printMenuElementArray(final MenuElement[] array, final boolean b) {
        System.out.println("Path is(");
        for (int i = 0; i < array.length; ++i) {
            for (int j = 0; j <= i; ++j) {
                System.out.print("  ");
            }
            final MenuElement menuElement = array[i];
            if (menuElement instanceof JMenuItem) {
                System.out.println(((JMenuItem)menuElement).getText() + ", ");
            }
            else if (menuElement instanceof JMenuBar) {
                System.out.println("JMenuBar, ");
            }
            else if (menuElement instanceof JPopupMenu) {
                System.out.println("JPopupMenu, ");
            }
            else if (menuElement == null) {
                System.out.println("NULL , ");
            }
            else {
                System.out.println("" + menuElement + ", ");
            }
        }
        System.out.println(")");
        if (b) {
            Thread.dumpStack();
        }
    }
    
    public Component componentForPoint(final Component component, final Point point) {
        SwingUtilities.convertPointToScreen(point, component);
        final int x = point.x;
        final int y = point.y;
        final Vector vector = (Vector)this.selection.clone();
        for (int i = vector.size() - 1; i >= 0; --i) {
            final MenuElement[] subElements = vector.elementAt(i).getSubElements();
            for (int j = 0; j < subElements.length; ++j) {
                if (subElements[j] != null) {
                    final Component component2 = subElements[j].getComponent();
                    if (component2.isShowing()) {
                        int n;
                        int n2;
                        if (component2 instanceof JComponent) {
                            n = component2.getWidth();
                            n2 = component2.getHeight();
                        }
                        else {
                            final Rectangle bounds = component2.getBounds();
                            n = bounds.width;
                            n2 = bounds.height;
                        }
                        point.x = x;
                        point.y = y;
                        SwingUtilities.convertPointFromScreen(point, component2);
                        if (point.x >= 0 && point.x < n && point.y >= 0 && point.y < n2) {
                            return component2;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public void processKeyEvent(final KeyEvent keyEvent) {
        final MenuElement[] array = this.selection.toArray(new MenuElement[0]);
        final int length = array.length;
        if (length < 1) {
            return;
        }
        for (int i = length - 1; i >= 0; --i) {
            final MenuElement[] subElements = array[i].getSubElements();
            MenuElement[] array2 = null;
            for (int j = 0; j < subElements.length; ++j) {
                if (subElements[j] != null && subElements[j].getComponent().isShowing()) {
                    if (subElements[j].getComponent().isEnabled()) {
                        if (array2 == null) {
                            array2 = new MenuElement[i + 2];
                            System.arraycopy(array, 0, array2, 0, i + 1);
                        }
                        array2[i + 1] = subElements[j];
                        subElements[j].processKeyEvent(keyEvent, array2, this);
                        if (keyEvent.isConsumed()) {
                            return;
                        }
                    }
                }
            }
        }
        final MenuElement[] array3 = { array[0] };
        array3[0].processKeyEvent(keyEvent, array3, this);
        if (keyEvent.isConsumed()) {
            return;
        }
    }
    
    public boolean isComponentPartOfCurrentMenu(final Component component) {
        return this.selection.size() > 0 && this.isComponentPartOfCurrentMenu(this.selection.elementAt(0), component);
    }
    
    private boolean isComponentPartOfCurrentMenu(final MenuElement menuElement, final Component component) {
        if (menuElement == null) {
            return false;
        }
        if (menuElement.getComponent() == component) {
            return true;
        }
        final MenuElement[] subElements = menuElement.getSubElements();
        for (int i = 0; i < subElements.length; ++i) {
            if (this.isComponentPartOfCurrentMenu(subElements[i], component)) {
                return true;
            }
        }
        return false;
    }
    
    static {
        MENU_SELECTION_MANAGER_KEY = new StringBuilder("javax.swing.MenuSelectionManager");
    }
}
