package javax.swing;

import java.awt.event.WindowEvent;
import java.awt.Insets;
import java.awt.Image;
import sun.reflect.misc.ReflectUtil;
import sun.awt.AppContext;
import java.awt.event.WindowListener;
import java.awt.HeadlessException;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import sun.swing.UIAction;
import java.awt.event.KeyEvent;
import java.awt.KeyboardFocusManager;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.Accessible;
import java.lang.reflect.InvocationTargetException;
import java.awt.EventQueue;
import java.awt.Graphics;
import javax.swing.text.View;
import sun.swing.SwingUtilities2;
import java.awt.FontMetrics;
import java.awt.IllegalComponentStateException;
import java.applet.Applet;
import sun.awt.AWTAccessor;
import javax.swing.event.MenuDragMouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.awt.Container;
import java.awt.Window;
import java.awt.Rectangle;
import java.awt.GraphicsEnvironment;
import java.awt.dnd.DropTarget;
import javax.swing.plaf.UIResource;
import java.awt.Component;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;

public class SwingUtilities implements SwingConstants
{
    private static boolean canAccessEventQueue;
    private static boolean eventQueueTested;
    private static boolean suppressDropSupport;
    private static boolean checkedSuppressDropSupport;
    private static final Object sharedOwnerFrameKey;
    
    private static boolean getSuppressDropTarget() {
        if (!SwingUtilities.checkedSuppressDropSupport) {
            SwingUtilities.suppressDropSupport = Boolean.valueOf(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("suppressSwingDropSupport")));
            SwingUtilities.checkedSuppressDropSupport = true;
        }
        return SwingUtilities.suppressDropSupport;
    }
    
    static void installSwingDropTargetAsNecessary(final Component component, final TransferHandler transferHandler) {
        if (!getSuppressDropTarget()) {
            final DropTarget dropTarget = component.getDropTarget();
            if (dropTarget == null || dropTarget instanceof UIResource) {
                if (transferHandler == null) {
                    component.setDropTarget(null);
                }
                else if (!GraphicsEnvironment.isHeadless()) {
                    component.setDropTarget(new TransferHandler.SwingDropTarget(component));
                }
            }
        }
    }
    
    public static final boolean isRectangleContainingRectangle(final Rectangle rectangle, final Rectangle rectangle2) {
        return rectangle2.x >= rectangle.x && rectangle2.x + rectangle2.width <= rectangle.x + rectangle.width && rectangle2.y >= rectangle.y && rectangle2.y + rectangle2.height <= rectangle.y + rectangle.height;
    }
    
    public static Rectangle getLocalBounds(final Component component) {
        final Rectangle rectangle2;
        final Rectangle rectangle = rectangle2 = new Rectangle(component.getBounds());
        final int n = 0;
        rectangle.y = n;
        rectangle2.x = n;
        return rectangle;
    }
    
    public static Window getWindowAncestor(final Component component) {
        for (Container container = component.getParent(); container != null; container = container.getParent()) {
            if (container instanceof Window) {
                return (Window)container;
            }
        }
        return null;
    }
    
    static Point convertScreenLocationToParent(final Container container, final int n, final int n2) {
        for (Container parent = container; parent != null; parent = parent.getParent()) {
            if (parent instanceof Window) {
                final Point point = new Point(n, n2);
                convertPointFromScreen(point, container);
                return point;
            }
        }
        throw new Error("convertScreenLocationToParent: no window ancestor");
    }
    
    public static Point convertPoint(Component windowAncestor, final Point point, Component windowAncestor2) {
        if (windowAncestor == null && windowAncestor2 == null) {
            return point;
        }
        if (windowAncestor == null) {
            windowAncestor = getWindowAncestor(windowAncestor2);
            if (windowAncestor == null) {
                throw new Error("Source component not connected to component tree hierarchy");
            }
        }
        final Point point2 = new Point(point);
        convertPointToScreen(point2, windowAncestor);
        if (windowAncestor2 == null) {
            windowAncestor2 = getWindowAncestor(windowAncestor);
            if (windowAncestor2 == null) {
                throw new Error("Destination component not connected to component tree hierarchy");
            }
        }
        convertPointFromScreen(point2, windowAncestor2);
        return point2;
    }
    
    public static Point convertPoint(final Component component, final int n, final int n2, final Component component2) {
        return convertPoint(component, new Point(n, n2), component2);
    }
    
    public static Rectangle convertRectangle(final Component component, final Rectangle rectangle, final Component component2) {
        final Point convertPoint = convertPoint(component, new Point(rectangle.x, rectangle.y), component2);
        return new Rectangle(convertPoint.x, convertPoint.y, rectangle.width, rectangle.height);
    }
    
    public static Container getAncestorOfClass(final Class<?> clazz, final Component component) {
        if (component == null || clazz == null) {
            return null;
        }
        Container container;
        for (container = component.getParent(); container != null && !clazz.isInstance(container); container = container.getParent()) {}
        return container;
    }
    
    public static Container getAncestorNamed(final String s, final Component component) {
        if (component == null || s == null) {
            return null;
        }
        Container container;
        for (container = component.getParent(); container != null && !s.equals(container.getName()); container = container.getParent()) {}
        return container;
    }
    
    public static Component getDeepestComponentAt(final Component component, final int n, final int n2) {
        if (!component.contains(n, n2)) {
            return null;
        }
        if (component instanceof Container) {
            for (final Component component2 : ((Container)component).getComponents()) {
                if (component2 != null && component2.isVisible()) {
                    final Point location = component2.getLocation();
                    Component component3;
                    if (component2 instanceof Container) {
                        component3 = getDeepestComponentAt(component2, n - location.x, n2 - location.y);
                    }
                    else {
                        component3 = component2.getComponentAt(n - location.x, n2 - location.y);
                    }
                    if (component3 != null && component3.isVisible()) {
                        return component3;
                    }
                }
            }
        }
        return component;
    }
    
    public static MouseEvent convertMouseEvent(final Component component, final MouseEvent mouseEvent, final Component component2) {
        final Point convertPoint = convertPoint(component, new Point(mouseEvent.getX(), mouseEvent.getY()), component2);
        Component component3;
        if (component2 != null) {
            component3 = component2;
        }
        else {
            component3 = component;
        }
        MouseEvent mouseEvent2;
        if (mouseEvent instanceof MouseWheelEvent) {
            final MouseWheelEvent mouseWheelEvent = (MouseWheelEvent)mouseEvent;
            mouseEvent2 = new MouseWheelEvent(component3, mouseWheelEvent.getID(), mouseWheelEvent.getWhen(), mouseWheelEvent.getModifiers() | mouseWheelEvent.getModifiersEx(), convertPoint.x, convertPoint.y, mouseWheelEvent.getXOnScreen(), mouseWheelEvent.getYOnScreen(), mouseWheelEvent.getClickCount(), mouseWheelEvent.isPopupTrigger(), mouseWheelEvent.getScrollType(), mouseWheelEvent.getScrollAmount(), mouseWheelEvent.getWheelRotation());
        }
        else if (mouseEvent instanceof MenuDragMouseEvent) {
            final MenuDragMouseEvent menuDragMouseEvent = (MenuDragMouseEvent)mouseEvent;
            mouseEvent2 = new MenuDragMouseEvent(component3, menuDragMouseEvent.getID(), menuDragMouseEvent.getWhen(), menuDragMouseEvent.getModifiers() | menuDragMouseEvent.getModifiersEx(), convertPoint.x, convertPoint.y, menuDragMouseEvent.getXOnScreen(), menuDragMouseEvent.getYOnScreen(), menuDragMouseEvent.getClickCount(), menuDragMouseEvent.isPopupTrigger(), menuDragMouseEvent.getPath(), menuDragMouseEvent.getMenuSelectionManager());
        }
        else {
            mouseEvent2 = new MouseEvent(component3, mouseEvent.getID(), mouseEvent.getWhen(), mouseEvent.getModifiers() | mouseEvent.getModifiersEx(), convertPoint.x, convertPoint.y, mouseEvent.getXOnScreen(), mouseEvent.getYOnScreen(), mouseEvent.getClickCount(), mouseEvent.isPopupTrigger(), mouseEvent.getButton());
            final AWTAccessor.MouseEventAccessor mouseEventAccessor = AWTAccessor.getMouseEventAccessor();
            mouseEventAccessor.setCausedByTouchEvent(mouseEvent2, mouseEventAccessor.isCausedByTouchEvent(mouseEvent));
        }
        return mouseEvent2;
    }
    
    public static void convertPointToScreen(final Point point, Component parent) {
        do {
            int n = 0;
            int n2 = 0;
            Label_0084: {
                if (parent instanceof JComponent) {
                    n = parent.getX();
                    n2 = parent.getY();
                }
                else {
                    if (!(parent instanceof Applet)) {
                        if (!(parent instanceof Window)) {
                            n = parent.getX();
                            n2 = parent.getY();
                            break Label_0084;
                        }
                    }
                    try {
                        final Point locationOnScreen = parent.getLocationOnScreen();
                        n = locationOnScreen.x;
                        n2 = locationOnScreen.y;
                    }
                    catch (final IllegalComponentStateException ex) {
                        n = parent.getX();
                        n2 = parent.getY();
                    }
                }
            }
            point.x += n;
            point.y += n2;
            if (parent instanceof Window) {
                break;
            }
            if (parent instanceof Applet) {
                break;
            }
            parent = parent.getParent();
        } while (parent != null);
    }
    
    public static void convertPointFromScreen(final Point point, Component parent) {
        do {
            int n = 0;
            int n2 = 0;
            Label_0084: {
                if (parent instanceof JComponent) {
                    n = parent.getX();
                    n2 = parent.getY();
                }
                else {
                    if (!(parent instanceof Applet)) {
                        if (!(parent instanceof Window)) {
                            n = parent.getX();
                            n2 = parent.getY();
                            break Label_0084;
                        }
                    }
                    try {
                        final Point locationOnScreen = parent.getLocationOnScreen();
                        n = locationOnScreen.x;
                        n2 = locationOnScreen.y;
                    }
                    catch (final IllegalComponentStateException ex) {
                        n = parent.getX();
                        n2 = parent.getY();
                    }
                }
            }
            point.x -= n;
            point.y -= n2;
            if (parent instanceof Window) {
                break;
            }
            if (parent instanceof Applet) {
                break;
            }
            parent = parent.getParent();
        } while (parent != null);
    }
    
    public static Window windowForComponent(final Component component) {
        return getWindowAncestor(component);
    }
    
    public static boolean isDescendingFrom(final Component component, final Component component2) {
        if (component == component2) {
            return true;
        }
        for (Container container = component.getParent(); container != null; container = container.getParent()) {
            if (container == component2) {
                return true;
            }
        }
        return false;
    }
    
    public static Rectangle computeIntersection(final int n, final int n2, final int n3, final int n4, final Rectangle rectangle) {
        final int x = (n > rectangle.x) ? n : rectangle.x;
        final int n5 = (n + n3 < rectangle.x + rectangle.width) ? (n + n3) : (rectangle.x + rectangle.width);
        final int y = (n2 > rectangle.y) ? n2 : rectangle.y;
        final int n6 = (n2 + n4 < rectangle.y + rectangle.height) ? (n2 + n4) : (rectangle.y + rectangle.height);
        rectangle.x = x;
        rectangle.y = y;
        rectangle.width = n5 - x;
        rectangle.height = n6 - y;
        if (rectangle.width < 0 || rectangle.height < 0) {
            final int n7 = 0;
            rectangle.height = n7;
            rectangle.width = n7;
            rectangle.y = n7;
            rectangle.x = n7;
        }
        return rectangle;
    }
    
    public static Rectangle computeUnion(final int n, final int n2, final int n3, final int n4, final Rectangle rectangle) {
        final int x = (n < rectangle.x) ? n : rectangle.x;
        final int n5 = (n + n3 > rectangle.x + rectangle.width) ? (n + n3) : (rectangle.x + rectangle.width);
        final int y = (n2 < rectangle.y) ? n2 : rectangle.y;
        final int n6 = (n2 + n4 > rectangle.y + rectangle.height) ? (n2 + n4) : (rectangle.y + rectangle.height);
        rectangle.x = x;
        rectangle.y = y;
        rectangle.width = n5 - x;
        rectangle.height = n6 - y;
        return rectangle;
    }
    
    public static Rectangle[] computeDifference(final Rectangle rectangle, final Rectangle rectangle2) {
        if (rectangle2 == null || !rectangle.intersects(rectangle2) || isRectangleContainingRectangle(rectangle2, rectangle)) {
            return new Rectangle[0];
        }
        final Rectangle rectangle3 = new Rectangle();
        Rectangle rectangle4 = null;
        Rectangle rectangle5 = null;
        Rectangle rectangle6 = null;
        Rectangle rectangle7 = null;
        int n = 0;
        if (isRectangleContainingRectangle(rectangle, rectangle2)) {
            rectangle3.x = rectangle.x;
            rectangle3.y = rectangle.y;
            rectangle3.width = rectangle2.x - rectangle.x;
            rectangle3.height = rectangle.height;
            if (rectangle3.width > 0 && rectangle3.height > 0) {
                rectangle4 = new Rectangle(rectangle3);
                ++n;
            }
            rectangle3.x = rectangle2.x;
            rectangle3.y = rectangle.y;
            rectangle3.width = rectangle2.width;
            rectangle3.height = rectangle2.y - rectangle.y;
            if (rectangle3.width > 0 && rectangle3.height > 0) {
                rectangle5 = new Rectangle(rectangle3);
                ++n;
            }
            rectangle3.x = rectangle2.x;
            rectangle3.y = rectangle2.y + rectangle2.height;
            rectangle3.width = rectangle2.width;
            rectangle3.height = rectangle.y + rectangle.height - (rectangle2.y + rectangle2.height);
            if (rectangle3.width > 0 && rectangle3.height > 0) {
                rectangle6 = new Rectangle(rectangle3);
                ++n;
            }
            rectangle3.x = rectangle2.x + rectangle2.width;
            rectangle3.y = rectangle.y;
            rectangle3.width = rectangle.x + rectangle.width - (rectangle2.x + rectangle2.width);
            rectangle3.height = rectangle.height;
            if (rectangle3.width > 0 && rectangle3.height > 0) {
                rectangle7 = new Rectangle(rectangle3);
                ++n;
            }
        }
        else if (rectangle2.x <= rectangle.x && rectangle2.y <= rectangle.y) {
            if (rectangle2.x + rectangle2.width > rectangle.x + rectangle.width) {
                rectangle3.x = rectangle.x;
                rectangle3.y = rectangle2.y + rectangle2.height;
                rectangle3.width = rectangle.width;
                rectangle3.height = rectangle.y + rectangle.height - (rectangle2.y + rectangle2.height);
                if (rectangle3.width > 0 && rectangle3.height > 0) {
                    rectangle4 = rectangle3;
                    ++n;
                }
            }
            else if (rectangle2.y + rectangle2.height > rectangle.y + rectangle.height) {
                rectangle3.setBounds(rectangle2.x + rectangle2.width, rectangle.y, rectangle.x + rectangle.width - (rectangle2.x + rectangle2.width), rectangle.height);
                if (rectangle3.width > 0 && rectangle3.height > 0) {
                    rectangle4 = rectangle3;
                    ++n;
                }
            }
            else {
                rectangle3.setBounds(rectangle2.x + rectangle2.width, rectangle.y, rectangle.x + rectangle.width - (rectangle2.x + rectangle2.width), rectangle2.y + rectangle2.height - rectangle.y);
                if (rectangle3.width > 0 && rectangle3.height > 0) {
                    rectangle4 = new Rectangle(rectangle3);
                    ++n;
                }
                rectangle3.setBounds(rectangle.x, rectangle2.y + rectangle2.height, rectangle.width, rectangle.y + rectangle.height - (rectangle2.y + rectangle2.height));
                if (rectangle3.width > 0 && rectangle3.height > 0) {
                    rectangle5 = new Rectangle(rectangle3);
                    ++n;
                }
            }
        }
        else if (rectangle2.x <= rectangle.x && rectangle2.y + rectangle2.height >= rectangle.y + rectangle.height) {
            if (rectangle2.x + rectangle2.width > rectangle.x + rectangle.width) {
                rectangle3.setBounds(rectangle.x, rectangle.y, rectangle.width, rectangle2.y - rectangle.y);
                if (rectangle3.width > 0 && rectangle3.height > 0) {
                    rectangle4 = rectangle3;
                    ++n;
                }
            }
            else {
                rectangle3.setBounds(rectangle.x, rectangle.y, rectangle.width, rectangle2.y - rectangle.y);
                if (rectangle3.width > 0 && rectangle3.height > 0) {
                    rectangle4 = new Rectangle(rectangle3);
                    ++n;
                }
                rectangle3.setBounds(rectangle2.x + rectangle2.width, rectangle2.y, rectangle.x + rectangle.width - (rectangle2.x + rectangle2.width), rectangle.y + rectangle.height - rectangle2.y);
                if (rectangle3.width > 0 && rectangle3.height > 0) {
                    rectangle5 = new Rectangle(rectangle3);
                    ++n;
                }
            }
        }
        else if (rectangle2.x <= rectangle.x) {
            if (rectangle2.x + rectangle2.width >= rectangle.x + rectangle.width) {
                rectangle3.setBounds(rectangle.x, rectangle.y, rectangle.width, rectangle2.y - rectangle.y);
                if (rectangle3.width > 0 && rectangle3.height > 0) {
                    rectangle4 = new Rectangle(rectangle3);
                    ++n;
                }
                rectangle3.setBounds(rectangle.x, rectangle2.y + rectangle2.height, rectangle.width, rectangle.y + rectangle.height - (rectangle2.y + rectangle2.height));
                if (rectangle3.width > 0 && rectangle3.height > 0) {
                    rectangle5 = new Rectangle(rectangle3);
                    ++n;
                }
            }
            else {
                rectangle3.setBounds(rectangle.x, rectangle.y, rectangle.width, rectangle2.y - rectangle.y);
                if (rectangle3.width > 0 && rectangle3.height > 0) {
                    rectangle4 = new Rectangle(rectangle3);
                    ++n;
                }
                rectangle3.setBounds(rectangle2.x + rectangle2.width, rectangle2.y, rectangle.x + rectangle.width - (rectangle2.x + rectangle2.width), rectangle2.height);
                if (rectangle3.width > 0 && rectangle3.height > 0) {
                    rectangle5 = new Rectangle(rectangle3);
                    ++n;
                }
                rectangle3.setBounds(rectangle.x, rectangle2.y + rectangle2.height, rectangle.width, rectangle.y + rectangle.height - (rectangle2.y + rectangle2.height));
                if (rectangle3.width > 0 && rectangle3.height > 0) {
                    rectangle6 = new Rectangle(rectangle3);
                    ++n;
                }
            }
        }
        else if (rectangle2.x <= rectangle.x + rectangle.width && rectangle2.x + rectangle2.width > rectangle.x + rectangle.width) {
            if (rectangle2.y <= rectangle.y && rectangle2.y + rectangle2.height > rectangle.y + rectangle.height) {
                rectangle3.setBounds(rectangle.x, rectangle.y, rectangle2.x - rectangle.x, rectangle.height);
                if (rectangle3.width > 0 && rectangle3.height > 0) {
                    rectangle4 = rectangle3;
                    ++n;
                }
            }
            else if (rectangle2.y <= rectangle.y) {
                rectangle3.setBounds(rectangle.x, rectangle.y, rectangle2.x - rectangle.x, rectangle2.y + rectangle2.height - rectangle.y);
                if (rectangle3.width > 0 && rectangle3.height > 0) {
                    rectangle4 = new Rectangle(rectangle3);
                    ++n;
                }
                rectangle3.setBounds(rectangle.x, rectangle2.y + rectangle2.height, rectangle.width, rectangle.y + rectangle.height - (rectangle2.y + rectangle2.height));
                if (rectangle3.width > 0 && rectangle3.height > 0) {
                    rectangle5 = new Rectangle(rectangle3);
                    ++n;
                }
            }
            else if (rectangle2.y + rectangle2.height > rectangle.y + rectangle.height) {
                rectangle3.setBounds(rectangle.x, rectangle.y, rectangle.width, rectangle2.y - rectangle.y);
                if (rectangle3.width > 0 && rectangle3.height > 0) {
                    rectangle4 = new Rectangle(rectangle3);
                    ++n;
                }
                rectangle3.setBounds(rectangle.x, rectangle2.y, rectangle2.x - rectangle.x, rectangle.y + rectangle.height - rectangle2.y);
                if (rectangle3.width > 0 && rectangle3.height > 0) {
                    rectangle5 = new Rectangle(rectangle3);
                    ++n;
                }
            }
            else {
                rectangle3.setBounds(rectangle.x, rectangle.y, rectangle.width, rectangle2.y - rectangle.y);
                if (rectangle3.width > 0 && rectangle3.height > 0) {
                    rectangle4 = new Rectangle(rectangle3);
                    ++n;
                }
                rectangle3.setBounds(rectangle.x, rectangle2.y, rectangle2.x - rectangle.x, rectangle2.height);
                if (rectangle3.width > 0 && rectangle3.height > 0) {
                    rectangle5 = new Rectangle(rectangle3);
                    ++n;
                }
                rectangle3.setBounds(rectangle.x, rectangle2.y + rectangle2.height, rectangle.width, rectangle.y + rectangle.height - (rectangle2.y + rectangle2.height));
                if (rectangle3.width > 0 && rectangle3.height > 0) {
                    rectangle6 = new Rectangle(rectangle3);
                    ++n;
                }
            }
        }
        else if (rectangle2.x >= rectangle.x && rectangle2.x + rectangle2.width <= rectangle.x + rectangle.width) {
            if (rectangle2.y <= rectangle.y && rectangle2.y + rectangle2.height > rectangle.y + rectangle.height) {
                rectangle3.setBounds(rectangle.x, rectangle.y, rectangle2.x - rectangle.x, rectangle.height);
                if (rectangle3.width > 0 && rectangle3.height > 0) {
                    rectangle4 = new Rectangle(rectangle3);
                    ++n;
                }
                rectangle3.setBounds(rectangle2.x + rectangle2.width, rectangle.y, rectangle.x + rectangle.width - (rectangle2.x + rectangle2.width), rectangle.height);
                if (rectangle3.width > 0 && rectangle3.height > 0) {
                    rectangle5 = new Rectangle(rectangle3);
                    ++n;
                }
            }
            else if (rectangle2.y <= rectangle.y) {
                rectangle3.setBounds(rectangle.x, rectangle.y, rectangle2.x - rectangle.x, rectangle.height);
                if (rectangle3.width > 0 && rectangle3.height > 0) {
                    rectangle4 = new Rectangle(rectangle3);
                    ++n;
                }
                rectangle3.setBounds(rectangle2.x, rectangle2.y + rectangle2.height, rectangle2.width, rectangle.y + rectangle.height - (rectangle2.y + rectangle2.height));
                if (rectangle3.width > 0 && rectangle3.height > 0) {
                    rectangle5 = new Rectangle(rectangle3);
                    ++n;
                }
                rectangle3.setBounds(rectangle2.x + rectangle2.width, rectangle.y, rectangle.x + rectangle.width - (rectangle2.x + rectangle2.width), rectangle.height);
                if (rectangle3.width > 0 && rectangle3.height > 0) {
                    rectangle6 = new Rectangle(rectangle3);
                    ++n;
                }
            }
            else {
                rectangle3.setBounds(rectangle.x, rectangle.y, rectangle2.x - rectangle.x, rectangle.height);
                if (rectangle3.width > 0 && rectangle3.height > 0) {
                    rectangle4 = new Rectangle(rectangle3);
                    ++n;
                }
                rectangle3.setBounds(rectangle2.x, rectangle.y, rectangle2.width, rectangle2.y - rectangle.y);
                if (rectangle3.width > 0 && rectangle3.height > 0) {
                    rectangle5 = new Rectangle(rectangle3);
                    ++n;
                }
                rectangle3.setBounds(rectangle2.x + rectangle2.width, rectangle.y, rectangle.x + rectangle.width - (rectangle2.x + rectangle2.width), rectangle.height);
                if (rectangle3.width > 0 && rectangle3.height > 0) {
                    rectangle6 = new Rectangle(rectangle3);
                    ++n;
                }
            }
        }
        final Rectangle[] array = new Rectangle[n];
        int n2 = 0;
        if (rectangle4 != null) {
            array[n2++] = rectangle4;
        }
        if (rectangle5 != null) {
            array[n2++] = rectangle5;
        }
        if (rectangle6 != null) {
            array[n2++] = rectangle6;
        }
        if (rectangle7 != null) {
            array[n2++] = rectangle7;
        }
        return array;
    }
    
    public static boolean isLeftMouseButton(final MouseEvent mouseEvent) {
        return (mouseEvent.getModifiersEx() & 0x400) != 0x0 || mouseEvent.getButton() == 1;
    }
    
    public static boolean isMiddleMouseButton(final MouseEvent mouseEvent) {
        return (mouseEvent.getModifiersEx() & 0x800) != 0x0 || mouseEvent.getButton() == 2;
    }
    
    public static boolean isRightMouseButton(final MouseEvent mouseEvent) {
        return (mouseEvent.getModifiersEx() & 0x1000) != 0x0 || mouseEvent.getButton() == 3;
    }
    
    public static int computeStringWidth(final FontMetrics fontMetrics, final String s) {
        return SwingUtilities2.stringWidth(null, fontMetrics, s);
    }
    
    public static String layoutCompoundLabel(final JComponent component, final FontMetrics fontMetrics, final String s, final Icon icon, final int n, final int n2, final int n3, final int n4, final Rectangle rectangle, final Rectangle rectangle2, final Rectangle rectangle3, final int n5) {
        boolean b = true;
        int n6 = n2;
        int n7 = n4;
        if (component != null && !component.getComponentOrientation().isLeftToRight()) {
            b = false;
        }
        switch (n2) {
            case 10: {
                n6 = (b ? 2 : 4);
                break;
            }
            case 11: {
                n6 = (b ? 4 : 2);
                break;
            }
        }
        switch (n4) {
            case 10: {
                n7 = (b ? 2 : 4);
                break;
            }
            case 11: {
                n7 = (b ? 4 : 2);
                break;
            }
        }
        return layoutCompoundLabelImpl(component, fontMetrics, s, icon, n, n6, n3, n7, rectangle, rectangle2, rectangle3, n5);
    }
    
    public static String layoutCompoundLabel(final FontMetrics fontMetrics, final String s, final Icon icon, final int n, final int n2, final int n3, final int n4, final Rectangle rectangle, final Rectangle rectangle2, final Rectangle rectangle3, final int n5) {
        return layoutCompoundLabelImpl(null, fontMetrics, s, icon, n, n2, n3, n4, rectangle, rectangle2, rectangle3, n5);
    }
    
    private static String layoutCompoundLabelImpl(final JComponent component, final FontMetrics fontMetrics, String clipString, final Icon icon, final int n, final int n2, final int n3, final int n4, final Rectangle rectangle, final Rectangle rectangle2, final Rectangle rectangle3, final int n5) {
        if (icon != null) {
            rectangle2.width = icon.getIconWidth();
            rectangle2.height = icon.getIconHeight();
        }
        else {
            final int n6 = 0;
            rectangle2.height = n6;
            rectangle2.width = n6;
        }
        final boolean b = clipString == null || clipString.equals("");
        int leftSideBearing = 0;
        final int n7 = 0;
        int n9;
        if (b) {
            final int n8 = 0;
            rectangle3.height = n8;
            rectangle3.width = n8;
            clipString = "";
            n9 = 0;
        }
        else {
            n9 = ((icon == null) ? 0 : n5);
            int width;
            if (n4 == 0) {
                width = rectangle.width;
            }
            else {
                width = rectangle.width - (rectangle2.width + n9);
            }
            final View view = (component != null) ? ((View)component.getClientProperty("html")) : null;
            if (view != null) {
                rectangle3.width = Math.min(width, (int)view.getPreferredSpan(0));
                rectangle3.height = (int)view.getPreferredSpan(1);
            }
            else {
                rectangle3.width = SwingUtilities2.stringWidth(component, fontMetrics, clipString);
                leftSideBearing = SwingUtilities2.getLeftSideBearing(component, fontMetrics, clipString);
                if (leftSideBearing < 0) {
                    rectangle3.width -= leftSideBearing;
                }
                if (rectangle3.width > width) {
                    clipString = SwingUtilities2.clipString(component, fontMetrics, clipString, width);
                    rectangle3.width = SwingUtilities2.stringWidth(component, fontMetrics, clipString);
                }
                rectangle3.height = fontMetrics.getHeight();
            }
        }
        if (n3 == 1) {
            if (n4 != 0) {
                rectangle3.y = 0;
            }
            else {
                rectangle3.y = -(rectangle3.height + n9);
            }
        }
        else if (n3 == 0) {
            rectangle3.y = rectangle2.height / 2 - rectangle3.height / 2;
        }
        else if (n4 != 0) {
            rectangle3.y = rectangle2.height - rectangle3.height;
        }
        else {
            rectangle3.y = rectangle2.height + n9;
        }
        if (n4 == 2) {
            rectangle3.x = -(rectangle3.width + n9);
        }
        else if (n4 == 0) {
            rectangle3.x = rectangle2.width / 2 - rectangle3.width / 2;
        }
        else {
            rectangle3.x = rectangle2.width + n9;
        }
        final int min = Math.min(rectangle2.x, rectangle3.x);
        final int n10 = Math.max(rectangle2.x + rectangle2.width, rectangle3.x + rectangle3.width) - min;
        final int min2 = Math.min(rectangle2.y, rectangle3.y);
        final int n11 = Math.max(rectangle2.y + rectangle2.height, rectangle3.y + rectangle3.height) - min2;
        int n12;
        if (n == 1) {
            n12 = rectangle.y - min2;
        }
        else if (n == 0) {
            n12 = rectangle.y + rectangle.height / 2 - (min2 + n11 / 2);
        }
        else {
            n12 = rectangle.y + rectangle.height - (min2 + n11);
        }
        int n13;
        if (n2 == 2) {
            n13 = rectangle.x - min;
        }
        else if (n2 == 4) {
            n13 = rectangle.x + rectangle.width - (min + n10);
        }
        else {
            n13 = rectangle.x + rectangle.width / 2 - (min + n10 / 2);
        }
        rectangle3.x += n13;
        rectangle3.y += n12;
        rectangle2.x += n13;
        rectangle2.y += n12;
        if (leftSideBearing < 0) {
            rectangle3.x -= leftSideBearing;
            rectangle3.width += leftSideBearing;
        }
        if (n7 > 0) {
            rectangle3.width -= n7;
        }
        return clipString;
    }
    
    public static void paintComponent(final Graphics graphics, final Component component, final Container container, final int n, final int n2, final int n3, final int n4) {
        getCellRendererPane(component, container).paintComponent(graphics, component, container, n, n2, n3, n4, false);
    }
    
    public static void paintComponent(final Graphics graphics, final Component component, final Container container, final Rectangle rectangle) {
        paintComponent(graphics, component, container, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }
    
    private static CellRendererPane getCellRendererPane(final Component component, final Container container) {
        Container parent = component.getParent();
        if (parent instanceof CellRendererPane) {
            if (parent.getParent() != container) {
                container.add(parent);
            }
        }
        else {
            parent = new CellRendererPane();
            parent.add(component);
            container.add(parent);
        }
        return (CellRendererPane)parent;
    }
    
    public static void updateComponentTreeUI(final Component component) {
        updateComponentTreeUI0(component);
        component.invalidate();
        component.validate();
        component.repaint();
    }
    
    private static void updateComponentTreeUI0(final Component component) {
        if (component instanceof JComponent) {
            final JComponent component2 = (JComponent)component;
            component2.updateUI();
            final JPopupMenu componentPopupMenu = component2.getComponentPopupMenu();
            if (componentPopupMenu != null) {
                updateComponentTreeUI(componentPopupMenu);
            }
        }
        Component[] array = null;
        if (component instanceof JMenu) {
            array = ((JMenu)component).getMenuComponents();
        }
        else if (component instanceof Container) {
            array = ((Container)component).getComponents();
        }
        if (array != null) {
            final Component[] array2 = array;
            for (int length = array2.length, i = 0; i < length; ++i) {
                updateComponentTreeUI0(array2[i]);
            }
        }
    }
    
    public static void invokeLater(final Runnable runnable) {
        EventQueue.invokeLater(runnable);
    }
    
    public static void invokeAndWait(final Runnable runnable) throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(runnable);
    }
    
    public static boolean isEventDispatchThread() {
        return EventQueue.isDispatchThread();
    }
    
    public static int getAccessibleIndexInParent(final Component component) {
        return component.getAccessibleContext().getAccessibleIndexInParent();
    }
    
    public static Accessible getAccessibleAt(final Component component, final Point point) {
        if (component instanceof Container) {
            return component.getAccessibleContext().getAccessibleComponent().getAccessibleAt(point);
        }
        if (component instanceof Accessible) {
            final Accessible accessible = (Accessible)component;
            if (accessible != null) {
                AccessibleContext accessibleContext = accessible.getAccessibleContext();
                if (accessibleContext != null) {
                    for (int accessibleChildrenCount = accessibleContext.getAccessibleChildrenCount(), i = 0; i < accessibleChildrenCount; ++i) {
                        final Accessible accessibleChild = accessibleContext.getAccessibleChild(i);
                        if (accessibleChild != null) {
                            accessibleContext = accessibleChild.getAccessibleContext();
                            if (accessibleContext != null) {
                                final AccessibleComponent accessibleComponent = accessibleContext.getAccessibleComponent();
                                if (accessibleComponent != null && accessibleComponent.isShowing()) {
                                    final Point location = accessibleComponent.getLocation();
                                    if (accessibleComponent.contains(new Point(point.x - location.x, point.y - location.y))) {
                                        return accessibleChild;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return (Accessible)component;
        }
        return null;
    }
    
    public static AccessibleStateSet getAccessibleStateSet(final Component component) {
        return component.getAccessibleContext().getAccessibleStateSet();
    }
    
    public static int getAccessibleChildrenCount(final Component component) {
        return component.getAccessibleContext().getAccessibleChildrenCount();
    }
    
    public static Accessible getAccessibleChild(final Component component, final int n) {
        return component.getAccessibleContext().getAccessibleChild(n);
    }
    
    @Deprecated
    public static Component findFocusOwner(final Component component) {
        Component focusOwner;
        for (Component component2 = focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner(); focusOwner != null; focusOwner = ((focusOwner instanceof Window) ? null : focusOwner.getParent())) {
            if (focusOwner == component) {
                return component2;
            }
        }
        return null;
    }
    
    public static JRootPane getRootPane(Component parent) {
        if (parent instanceof RootPaneContainer) {
            return ((RootPaneContainer)parent).getRootPane();
        }
        while (parent != null) {
            if (parent instanceof JRootPane) {
                return (JRootPane)parent;
            }
            parent = parent.getParent();
        }
        return null;
    }
    
    public static Component getRoot(final Component component) {
        Component component2 = null;
        for (Component parent = component; parent != null; parent = parent.getParent()) {
            if (parent instanceof Window) {
                return parent;
            }
            if (parent instanceof Applet) {
                component2 = parent;
            }
        }
        return component2;
    }
    
    static JComponent getPaintingOrigin(final JComponent component) {
        Container parent = component;
        while ((parent = parent.getParent()) instanceof JComponent) {
            final JComponent component2 = (JComponent)parent;
            if (component2.isPaintingOrigin()) {
                return component2;
            }
        }
        return null;
    }
    
    public static boolean processKeyBindings(final KeyEvent keyEvent) {
        if (keyEvent != null) {
            if (keyEvent.isConsumed()) {
                return false;
            }
            Component component = keyEvent.getComponent();
            final boolean b = keyEvent.getID() == 401;
            if (!isValidKeyEventForKeyBindings(keyEvent)) {
                return false;
            }
            while (component != null) {
                if (component instanceof JComponent) {
                    return ((JComponent)component).processKeyBindings(keyEvent, b);
                }
                if (component instanceof Applet || component instanceof Window) {
                    return JComponent.processKeyBindingsForAllComponents(keyEvent, (Container)component, b);
                }
                component = component.getParent();
            }
        }
        return false;
    }
    
    static boolean isValidKeyEventForKeyBindings(final KeyEvent keyEvent) {
        return true;
    }
    
    public static boolean notifyAction(final Action action, final KeyStroke keyStroke, final KeyEvent keyEvent, final Object o, final int n) {
        if (action == null) {
            return false;
        }
        if (action instanceof UIAction) {
            if (!((UIAction)action).isEnabled(o)) {
                return false;
            }
        }
        else if (!action.isEnabled()) {
            return false;
        }
        final Object value = action.getValue("ActionCommandKey");
        final boolean b = value == null && action instanceof JComponent.ActionStandin;
        String s;
        if (value != null) {
            s = value.toString();
        }
        else if (!b && keyEvent.getKeyChar() != '\uffff') {
            s = String.valueOf(keyEvent.getKeyChar());
        }
        else {
            s = null;
        }
        action.actionPerformed(new ActionEvent(o, 1001, s, keyEvent.getWhen(), n));
        return true;
    }
    
    public static void replaceUIInputMap(final JComponent component, final int n, final InputMap parent) {
        InputMap parent2;
        for (InputMap inputMap = component.getInputMap(n, parent != null); inputMap != null; inputMap = parent2) {
            parent2 = inputMap.getParent();
            if (parent2 == null || parent2 instanceof UIResource) {
                inputMap.setParent(parent);
                return;
            }
        }
    }
    
    public static void replaceUIActionMap(final JComponent component, final ActionMap parent) {
        ActionMap parent2;
        for (ActionMap actionMap = component.getActionMap(parent != null); actionMap != null; actionMap = parent2) {
            parent2 = actionMap.getParent();
            if (parent2 == null || parent2 instanceof UIResource) {
                actionMap.setParent(parent);
                return;
            }
        }
    }
    
    public static InputMap getUIInputMap(final JComponent component, final int n) {
        InputMap parent;
        for (InputMap inputMap = component.getInputMap(n, false); inputMap != null; inputMap = parent) {
            parent = inputMap.getParent();
            if (parent instanceof UIResource) {
                return parent;
            }
        }
        return null;
    }
    
    public static ActionMap getUIActionMap(final JComponent component) {
        ActionMap parent;
        for (ActionMap actionMap = component.getActionMap(false); actionMap != null; actionMap = parent) {
            parent = actionMap.getParent();
            if (parent instanceof UIResource) {
                return parent;
            }
        }
        return null;
    }
    
    static Frame getSharedOwnerFrame() throws HeadlessException {
        Frame frame = (Frame)appContextGet(SwingUtilities.sharedOwnerFrameKey);
        if (frame == null) {
            frame = new SharedOwnerFrame();
            appContextPut(SwingUtilities.sharedOwnerFrameKey, frame);
        }
        return frame;
    }
    
    static WindowListener getSharedOwnerFrameShutdownListener() throws HeadlessException {
        return (WindowListener)getSharedOwnerFrame();
    }
    
    static Object appContextGet(final Object o) {
        return AppContext.getAppContext().get(o);
    }
    
    static void appContextPut(final Object o, final Object o2) {
        AppContext.getAppContext().put(o, o2);
    }
    
    static void appContextRemove(final Object o) {
        AppContext.getAppContext().remove(o);
    }
    
    static Class<?> loadSystemClass(final String s) throws ClassNotFoundException {
        ReflectUtil.checkPackageAccess(s);
        return Class.forName(s, true, Thread.currentThread().getContextClassLoader());
    }
    
    static boolean isLeftToRight(final Component component) {
        return component.getComponentOrientation().isLeftToRight();
    }
    
    private SwingUtilities() {
        throw new Error("SwingUtilities is just a container for static methods");
    }
    
    static boolean doesIconReferenceImage(final Icon icon, final Image image) {
        return ((icon != null && icon instanceof ImageIcon) ? ((ImageIcon)icon).getImage() : null) == image;
    }
    
    static int findDisplayedMnemonicIndex(final String s, final int n) {
        if (s == null || n == 0) {
            return -1;
        }
        final char upperCase = Character.toUpperCase((char)n);
        final char lowerCase = Character.toLowerCase((char)n);
        final int index = s.indexOf(upperCase);
        final int index2 = s.indexOf(lowerCase);
        if (index == -1) {
            return index2;
        }
        if (index2 == -1) {
            return index;
        }
        return (index2 < index) ? index2 : index;
    }
    
    public static Rectangle calculateInnerArea(final JComponent component, final Rectangle rectangle) {
        if (component == null) {
            return null;
        }
        Rectangle rectangle2 = rectangle;
        final Insets insets = component.getInsets();
        if (rectangle2 == null) {
            rectangle2 = new Rectangle();
        }
        rectangle2.x = insets.left;
        rectangle2.y = insets.top;
        rectangle2.width = component.getWidth() - insets.left - insets.right;
        rectangle2.height = component.getHeight() - insets.top - insets.bottom;
        return rectangle2;
    }
    
    static void updateRendererOrEditorUI(final Object o) {
        if (o == null) {
            return;
        }
        Component component = null;
        if (o instanceof Component) {
            component = (Component)o;
        }
        if (o instanceof DefaultCellEditor) {
            component = ((DefaultCellEditor)o).getComponent();
        }
        if (component != null) {
            updateComponentTreeUI(component);
        }
    }
    
    public static Container getUnwrappedParent(final Component component) {
        Container container;
        for (container = component.getParent(); container instanceof JLayer; container = container.getParent()) {}
        return container;
    }
    
    public static Component getUnwrappedView(final JViewport viewport) {
        Component component;
        for (component = viewport.getView(); component instanceof JLayer; component = ((JLayer<Component>)component).getView()) {}
        return component;
    }
    
    static Container getValidateRoot(Container container, final boolean b) {
        Container container2 = null;
        while (container != null) {
            if (!container.isDisplayable() || container instanceof CellRendererPane) {
                return null;
            }
            if (container.isValidateRoot()) {
                container2 = container;
                break;
            }
            container = container.getParent();
        }
        if (container2 == null) {
            return null;
        }
        while (container != null) {
            if (!container.isDisplayable() || (b && !container.isVisible())) {
                return null;
            }
            if (container instanceof Window || container instanceof Applet) {
                return container2;
            }
            container = container.getParent();
        }
        return null;
    }
    
    static {
        SwingUtilities.canAccessEventQueue = false;
        SwingUtilities.eventQueueTested = false;
        sharedOwnerFrameKey = new StringBuffer("SwingUtilities.sharedOwnerFrame");
    }
    
    static class SharedOwnerFrame extends Frame implements WindowListener
    {
        @Override
        public void addNotify() {
            super.addNotify();
            this.installListeners();
        }
        
        void installListeners() {
            for (final Window window : this.getOwnedWindows()) {
                if (window != null) {
                    window.removeWindowListener(this);
                    window.addWindowListener(this);
                }
            }
        }
        
        @Override
        public void windowClosed(final WindowEvent windowEvent) {
            synchronized (this.getTreeLock()) {
                for (final Window window : this.getOwnedWindows()) {
                    if (window != null) {
                        if (window.isDisplayable()) {
                            return;
                        }
                        window.removeWindowListener(this);
                    }
                }
                this.dispose();
            }
        }
        
        @Override
        public void windowOpened(final WindowEvent windowEvent) {
        }
        
        @Override
        public void windowClosing(final WindowEvent windowEvent) {
        }
        
        @Override
        public void windowIconified(final WindowEvent windowEvent) {
        }
        
        @Override
        public void windowDeiconified(final WindowEvent windowEvent) {
        }
        
        @Override
        public void windowActivated(final WindowEvent windowEvent) {
        }
        
        @Override
        public void windowDeactivated(final WindowEvent windowEvent) {
        }
        
        @Override
        public void show() {
        }
        
        @Override
        public void dispose() {
            try {
                this.getToolkit().getSystemEventQueue();
                super.dispose();
            }
            catch (final Exception ex) {}
        }
    }
}
