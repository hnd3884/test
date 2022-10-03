package java.awt;

import java.lang.reflect.Array;
import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.awt.event.MouseWheelEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.TextEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.FocusEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ComponentEvent;
import java.util.EventListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyListener;
import java.awt.event.InputMethodListener;
import java.awt.event.TextListener;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemListener;
import java.awt.event.ActionListener;
import java.awt.event.WindowStateListener;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.event.KeyListener;
import java.awt.event.FocusListener;
import java.awt.event.ContainerListener;
import java.awt.event.ComponentListener;

public class AWTEventMulticaster implements ComponentListener, ContainerListener, FocusListener, KeyListener, MouseListener, MouseMotionListener, WindowListener, WindowFocusListener, WindowStateListener, ActionListener, ItemListener, AdjustmentListener, TextListener, InputMethodListener, HierarchyListener, HierarchyBoundsListener, MouseWheelListener
{
    protected final EventListener a;
    protected final EventListener b;
    
    protected AWTEventMulticaster(final EventListener a, final EventListener b) {
        this.a = a;
        this.b = b;
    }
    
    protected EventListener remove(final EventListener eventListener) {
        if (eventListener == this.a) {
            return this.b;
        }
        if (eventListener == this.b) {
            return this.a;
        }
        final EventListener removeInternal = removeInternal(this.a, eventListener);
        final EventListener removeInternal2 = removeInternal(this.b, eventListener);
        if (removeInternal == this.a && removeInternal2 == this.b) {
            return this;
        }
        return addInternal(removeInternal, removeInternal2);
    }
    
    @Override
    public void componentResized(final ComponentEvent componentEvent) {
        ((ComponentListener)this.a).componentResized(componentEvent);
        ((ComponentListener)this.b).componentResized(componentEvent);
    }
    
    @Override
    public void componentMoved(final ComponentEvent componentEvent) {
        ((ComponentListener)this.a).componentMoved(componentEvent);
        ((ComponentListener)this.b).componentMoved(componentEvent);
    }
    
    @Override
    public void componentShown(final ComponentEvent componentEvent) {
        ((ComponentListener)this.a).componentShown(componentEvent);
        ((ComponentListener)this.b).componentShown(componentEvent);
    }
    
    @Override
    public void componentHidden(final ComponentEvent componentEvent) {
        ((ComponentListener)this.a).componentHidden(componentEvent);
        ((ComponentListener)this.b).componentHidden(componentEvent);
    }
    
    @Override
    public void componentAdded(final ContainerEvent containerEvent) {
        ((ContainerListener)this.a).componentAdded(containerEvent);
        ((ContainerListener)this.b).componentAdded(containerEvent);
    }
    
    @Override
    public void componentRemoved(final ContainerEvent containerEvent) {
        ((ContainerListener)this.a).componentRemoved(containerEvent);
        ((ContainerListener)this.b).componentRemoved(containerEvent);
    }
    
    @Override
    public void focusGained(final FocusEvent focusEvent) {
        ((FocusListener)this.a).focusGained(focusEvent);
        ((FocusListener)this.b).focusGained(focusEvent);
    }
    
    @Override
    public void focusLost(final FocusEvent focusEvent) {
        ((FocusListener)this.a).focusLost(focusEvent);
        ((FocusListener)this.b).focusLost(focusEvent);
    }
    
    @Override
    public void keyTyped(final KeyEvent keyEvent) {
        ((KeyListener)this.a).keyTyped(keyEvent);
        ((KeyListener)this.b).keyTyped(keyEvent);
    }
    
    @Override
    public void keyPressed(final KeyEvent keyEvent) {
        ((KeyListener)this.a).keyPressed(keyEvent);
        ((KeyListener)this.b).keyPressed(keyEvent);
    }
    
    @Override
    public void keyReleased(final KeyEvent keyEvent) {
        ((KeyListener)this.a).keyReleased(keyEvent);
        ((KeyListener)this.b).keyReleased(keyEvent);
    }
    
    @Override
    public void mouseClicked(final MouseEvent mouseEvent) {
        ((MouseListener)this.a).mouseClicked(mouseEvent);
        ((MouseListener)this.b).mouseClicked(mouseEvent);
    }
    
    @Override
    public void mousePressed(final MouseEvent mouseEvent) {
        ((MouseListener)this.a).mousePressed(mouseEvent);
        ((MouseListener)this.b).mousePressed(mouseEvent);
    }
    
    @Override
    public void mouseReleased(final MouseEvent mouseEvent) {
        ((MouseListener)this.a).mouseReleased(mouseEvent);
        ((MouseListener)this.b).mouseReleased(mouseEvent);
    }
    
    @Override
    public void mouseEntered(final MouseEvent mouseEvent) {
        ((MouseListener)this.a).mouseEntered(mouseEvent);
        ((MouseListener)this.b).mouseEntered(mouseEvent);
    }
    
    @Override
    public void mouseExited(final MouseEvent mouseEvent) {
        ((MouseListener)this.a).mouseExited(mouseEvent);
        ((MouseListener)this.b).mouseExited(mouseEvent);
    }
    
    @Override
    public void mouseDragged(final MouseEvent mouseEvent) {
        ((MouseMotionListener)this.a).mouseDragged(mouseEvent);
        ((MouseMotionListener)this.b).mouseDragged(mouseEvent);
    }
    
    @Override
    public void mouseMoved(final MouseEvent mouseEvent) {
        ((MouseMotionListener)this.a).mouseMoved(mouseEvent);
        ((MouseMotionListener)this.b).mouseMoved(mouseEvent);
    }
    
    @Override
    public void windowOpened(final WindowEvent windowEvent) {
        ((WindowListener)this.a).windowOpened(windowEvent);
        ((WindowListener)this.b).windowOpened(windowEvent);
    }
    
    @Override
    public void windowClosing(final WindowEvent windowEvent) {
        ((WindowListener)this.a).windowClosing(windowEvent);
        ((WindowListener)this.b).windowClosing(windowEvent);
    }
    
    @Override
    public void windowClosed(final WindowEvent windowEvent) {
        ((WindowListener)this.a).windowClosed(windowEvent);
        ((WindowListener)this.b).windowClosed(windowEvent);
    }
    
    @Override
    public void windowIconified(final WindowEvent windowEvent) {
        ((WindowListener)this.a).windowIconified(windowEvent);
        ((WindowListener)this.b).windowIconified(windowEvent);
    }
    
    @Override
    public void windowDeiconified(final WindowEvent windowEvent) {
        ((WindowListener)this.a).windowDeiconified(windowEvent);
        ((WindowListener)this.b).windowDeiconified(windowEvent);
    }
    
    @Override
    public void windowActivated(final WindowEvent windowEvent) {
        ((WindowListener)this.a).windowActivated(windowEvent);
        ((WindowListener)this.b).windowActivated(windowEvent);
    }
    
    @Override
    public void windowDeactivated(final WindowEvent windowEvent) {
        ((WindowListener)this.a).windowDeactivated(windowEvent);
        ((WindowListener)this.b).windowDeactivated(windowEvent);
    }
    
    @Override
    public void windowStateChanged(final WindowEvent windowEvent) {
        ((WindowStateListener)this.a).windowStateChanged(windowEvent);
        ((WindowStateListener)this.b).windowStateChanged(windowEvent);
    }
    
    @Override
    public void windowGainedFocus(final WindowEvent windowEvent) {
        ((WindowFocusListener)this.a).windowGainedFocus(windowEvent);
        ((WindowFocusListener)this.b).windowGainedFocus(windowEvent);
    }
    
    @Override
    public void windowLostFocus(final WindowEvent windowEvent) {
        ((WindowFocusListener)this.a).windowLostFocus(windowEvent);
        ((WindowFocusListener)this.b).windowLostFocus(windowEvent);
    }
    
    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        ((ActionListener)this.a).actionPerformed(actionEvent);
        ((ActionListener)this.b).actionPerformed(actionEvent);
    }
    
    @Override
    public void itemStateChanged(final ItemEvent itemEvent) {
        ((ItemListener)this.a).itemStateChanged(itemEvent);
        ((ItemListener)this.b).itemStateChanged(itemEvent);
    }
    
    @Override
    public void adjustmentValueChanged(final AdjustmentEvent adjustmentEvent) {
        ((AdjustmentListener)this.a).adjustmentValueChanged(adjustmentEvent);
        ((AdjustmentListener)this.b).adjustmentValueChanged(adjustmentEvent);
    }
    
    @Override
    public void textValueChanged(final TextEvent textEvent) {
        ((TextListener)this.a).textValueChanged(textEvent);
        ((TextListener)this.b).textValueChanged(textEvent);
    }
    
    @Override
    public void inputMethodTextChanged(final InputMethodEvent inputMethodEvent) {
        ((InputMethodListener)this.a).inputMethodTextChanged(inputMethodEvent);
        ((InputMethodListener)this.b).inputMethodTextChanged(inputMethodEvent);
    }
    
    @Override
    public void caretPositionChanged(final InputMethodEvent inputMethodEvent) {
        ((InputMethodListener)this.a).caretPositionChanged(inputMethodEvent);
        ((InputMethodListener)this.b).caretPositionChanged(inputMethodEvent);
    }
    
    @Override
    public void hierarchyChanged(final HierarchyEvent hierarchyEvent) {
        ((HierarchyListener)this.a).hierarchyChanged(hierarchyEvent);
        ((HierarchyListener)this.b).hierarchyChanged(hierarchyEvent);
    }
    
    @Override
    public void ancestorMoved(final HierarchyEvent hierarchyEvent) {
        ((HierarchyBoundsListener)this.a).ancestorMoved(hierarchyEvent);
        ((HierarchyBoundsListener)this.b).ancestorMoved(hierarchyEvent);
    }
    
    @Override
    public void ancestorResized(final HierarchyEvent hierarchyEvent) {
        ((HierarchyBoundsListener)this.a).ancestorResized(hierarchyEvent);
        ((HierarchyBoundsListener)this.b).ancestorResized(hierarchyEvent);
    }
    
    @Override
    public void mouseWheelMoved(final MouseWheelEvent mouseWheelEvent) {
        ((MouseWheelListener)this.a).mouseWheelMoved(mouseWheelEvent);
        ((MouseWheelListener)this.b).mouseWheelMoved(mouseWheelEvent);
    }
    
    public static ComponentListener add(final ComponentListener componentListener, final ComponentListener componentListener2) {
        return (ComponentListener)addInternal(componentListener, componentListener2);
    }
    
    public static ContainerListener add(final ContainerListener containerListener, final ContainerListener containerListener2) {
        return (ContainerListener)addInternal(containerListener, containerListener2);
    }
    
    public static FocusListener add(final FocusListener focusListener, final FocusListener focusListener2) {
        return (FocusListener)addInternal(focusListener, focusListener2);
    }
    
    public static KeyListener add(final KeyListener keyListener, final KeyListener keyListener2) {
        return (KeyListener)addInternal(keyListener, keyListener2);
    }
    
    public static MouseListener add(final MouseListener mouseListener, final MouseListener mouseListener2) {
        return (MouseListener)addInternal(mouseListener, mouseListener2);
    }
    
    public static MouseMotionListener add(final MouseMotionListener mouseMotionListener, final MouseMotionListener mouseMotionListener2) {
        return (MouseMotionListener)addInternal(mouseMotionListener, mouseMotionListener2);
    }
    
    public static WindowListener add(final WindowListener windowListener, final WindowListener windowListener2) {
        return (WindowListener)addInternal(windowListener, windowListener2);
    }
    
    public static WindowStateListener add(final WindowStateListener windowStateListener, final WindowStateListener windowStateListener2) {
        return (WindowStateListener)addInternal(windowStateListener, windowStateListener2);
    }
    
    public static WindowFocusListener add(final WindowFocusListener windowFocusListener, final WindowFocusListener windowFocusListener2) {
        return (WindowFocusListener)addInternal(windowFocusListener, windowFocusListener2);
    }
    
    public static ActionListener add(final ActionListener actionListener, final ActionListener actionListener2) {
        return (ActionListener)addInternal(actionListener, actionListener2);
    }
    
    public static ItemListener add(final ItemListener itemListener, final ItemListener itemListener2) {
        return (ItemListener)addInternal(itemListener, itemListener2);
    }
    
    public static AdjustmentListener add(final AdjustmentListener adjustmentListener, final AdjustmentListener adjustmentListener2) {
        return (AdjustmentListener)addInternal(adjustmentListener, adjustmentListener2);
    }
    
    public static TextListener add(final TextListener textListener, final TextListener textListener2) {
        return (TextListener)addInternal(textListener, textListener2);
    }
    
    public static InputMethodListener add(final InputMethodListener inputMethodListener, final InputMethodListener inputMethodListener2) {
        return (InputMethodListener)addInternal(inputMethodListener, inputMethodListener2);
    }
    
    public static HierarchyListener add(final HierarchyListener hierarchyListener, final HierarchyListener hierarchyListener2) {
        return (HierarchyListener)addInternal(hierarchyListener, hierarchyListener2);
    }
    
    public static HierarchyBoundsListener add(final HierarchyBoundsListener hierarchyBoundsListener, final HierarchyBoundsListener hierarchyBoundsListener2) {
        return (HierarchyBoundsListener)addInternal(hierarchyBoundsListener, hierarchyBoundsListener2);
    }
    
    public static MouseWheelListener add(final MouseWheelListener mouseWheelListener, final MouseWheelListener mouseWheelListener2) {
        return (MouseWheelListener)addInternal(mouseWheelListener, mouseWheelListener2);
    }
    
    public static ComponentListener remove(final ComponentListener componentListener, final ComponentListener componentListener2) {
        return (ComponentListener)removeInternal(componentListener, componentListener2);
    }
    
    public static ContainerListener remove(final ContainerListener containerListener, final ContainerListener containerListener2) {
        return (ContainerListener)removeInternal(containerListener, containerListener2);
    }
    
    public static FocusListener remove(final FocusListener focusListener, final FocusListener focusListener2) {
        return (FocusListener)removeInternal(focusListener, focusListener2);
    }
    
    public static KeyListener remove(final KeyListener keyListener, final KeyListener keyListener2) {
        return (KeyListener)removeInternal(keyListener, keyListener2);
    }
    
    public static MouseListener remove(final MouseListener mouseListener, final MouseListener mouseListener2) {
        return (MouseListener)removeInternal(mouseListener, mouseListener2);
    }
    
    public static MouseMotionListener remove(final MouseMotionListener mouseMotionListener, final MouseMotionListener mouseMotionListener2) {
        return (MouseMotionListener)removeInternal(mouseMotionListener, mouseMotionListener2);
    }
    
    public static WindowListener remove(final WindowListener windowListener, final WindowListener windowListener2) {
        return (WindowListener)removeInternal(windowListener, windowListener2);
    }
    
    public static WindowStateListener remove(final WindowStateListener windowStateListener, final WindowStateListener windowStateListener2) {
        return (WindowStateListener)removeInternal(windowStateListener, windowStateListener2);
    }
    
    public static WindowFocusListener remove(final WindowFocusListener windowFocusListener, final WindowFocusListener windowFocusListener2) {
        return (WindowFocusListener)removeInternal(windowFocusListener, windowFocusListener2);
    }
    
    public static ActionListener remove(final ActionListener actionListener, final ActionListener actionListener2) {
        return (ActionListener)removeInternal(actionListener, actionListener2);
    }
    
    public static ItemListener remove(final ItemListener itemListener, final ItemListener itemListener2) {
        return (ItemListener)removeInternal(itemListener, itemListener2);
    }
    
    public static AdjustmentListener remove(final AdjustmentListener adjustmentListener, final AdjustmentListener adjustmentListener2) {
        return (AdjustmentListener)removeInternal(adjustmentListener, adjustmentListener2);
    }
    
    public static TextListener remove(final TextListener textListener, final TextListener textListener2) {
        return (TextListener)removeInternal(textListener, textListener2);
    }
    
    public static InputMethodListener remove(final InputMethodListener inputMethodListener, final InputMethodListener inputMethodListener2) {
        return (InputMethodListener)removeInternal(inputMethodListener, inputMethodListener2);
    }
    
    public static HierarchyListener remove(final HierarchyListener hierarchyListener, final HierarchyListener hierarchyListener2) {
        return (HierarchyListener)removeInternal(hierarchyListener, hierarchyListener2);
    }
    
    public static HierarchyBoundsListener remove(final HierarchyBoundsListener hierarchyBoundsListener, final HierarchyBoundsListener hierarchyBoundsListener2) {
        return (HierarchyBoundsListener)removeInternal(hierarchyBoundsListener, hierarchyBoundsListener2);
    }
    
    public static MouseWheelListener remove(final MouseWheelListener mouseWheelListener, final MouseWheelListener mouseWheelListener2) {
        return (MouseWheelListener)removeInternal(mouseWheelListener, mouseWheelListener2);
    }
    
    protected static EventListener addInternal(final EventListener eventListener, final EventListener eventListener2) {
        if (eventListener == null) {
            return eventListener2;
        }
        if (eventListener2 == null) {
            return eventListener;
        }
        return new AWTEventMulticaster(eventListener, eventListener2);
    }
    
    protected static EventListener removeInternal(final EventListener eventListener, final EventListener eventListener2) {
        if (eventListener == eventListener2 || eventListener == null) {
            return null;
        }
        if (eventListener instanceof AWTEventMulticaster) {
            return ((AWTEventMulticaster)eventListener).remove(eventListener2);
        }
        return eventListener;
    }
    
    protected void saveInternal(final ObjectOutputStream objectOutputStream, final String s) throws IOException {
        if (this.a instanceof AWTEventMulticaster) {
            ((AWTEventMulticaster)this.a).saveInternal(objectOutputStream, s);
        }
        else if (this.a instanceof Serializable) {
            objectOutputStream.writeObject(s);
            objectOutputStream.writeObject(this.a);
        }
        if (this.b instanceof AWTEventMulticaster) {
            ((AWTEventMulticaster)this.b).saveInternal(objectOutputStream, s);
        }
        else if (this.b instanceof Serializable) {
            objectOutputStream.writeObject(s);
            objectOutputStream.writeObject(this.b);
        }
    }
    
    protected static void save(final ObjectOutputStream objectOutputStream, final String s, final EventListener eventListener) throws IOException {
        if (eventListener == null) {
            return;
        }
        if (eventListener instanceof AWTEventMulticaster) {
            ((AWTEventMulticaster)eventListener).saveInternal(objectOutputStream, s);
        }
        else if (eventListener instanceof Serializable) {
            objectOutputStream.writeObject(s);
            objectOutputStream.writeObject(eventListener);
        }
    }
    
    private static int getListenerCount(final EventListener eventListener, final Class<?> clazz) {
        if (eventListener instanceof AWTEventMulticaster) {
            final AWTEventMulticaster awtEventMulticaster = (AWTEventMulticaster)eventListener;
            return getListenerCount(awtEventMulticaster.a, clazz) + getListenerCount(awtEventMulticaster.b, clazz);
        }
        return clazz.isInstance(eventListener) ? 1 : 0;
    }
    
    private static int populateListenerArray(final EventListener[] array, final EventListener eventListener, final int n) {
        if (eventListener instanceof AWTEventMulticaster) {
            final AWTEventMulticaster awtEventMulticaster = (AWTEventMulticaster)eventListener;
            return populateListenerArray(array, awtEventMulticaster.b, populateListenerArray(array, awtEventMulticaster.a, n));
        }
        if (array.getClass().getComponentType().isInstance(eventListener)) {
            array[n] = eventListener;
            return n + 1;
        }
        return n;
    }
    
    public static <T extends EventListener> T[] getListeners(final EventListener eventListener, final Class<T> clazz) {
        if (clazz == null) {
            throw new NullPointerException("Listener type should not be null");
        }
        final EventListener[] array = (EventListener[])Array.newInstance(clazz, getListenerCount(eventListener, clazz));
        populateListenerArray(array, eventListener, 0);
        return (T[])array;
    }
}
