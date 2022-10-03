package javax.swing;

import java.util.Enumeration;
import java.awt.event.KeyEvent;
import java.applet.Applet;
import java.awt.Window;
import java.awt.AWTKeyStroke;
import sun.awt.EmbeddedFrame;
import java.util.Vector;
import java.awt.Container;
import java.util.Hashtable;

class KeyboardManager
{
    static KeyboardManager currentManager;
    Hashtable<Container, Hashtable> containerMap;
    Hashtable<ComponentKeyStrokePair, Container> componentKeyStrokeMap;
    
    KeyboardManager() {
        this.containerMap = new Hashtable<Container, Hashtable>();
        this.componentKeyStrokeMap = new Hashtable<ComponentKeyStrokePair, Container>();
    }
    
    public static KeyboardManager getCurrentManager() {
        return KeyboardManager.currentManager;
    }
    
    public static void setCurrentManager(final KeyboardManager currentManager) {
        KeyboardManager.currentManager = currentManager;
    }
    
    public void registerKeyStroke(final KeyStroke keyStroke, final JComponent component) {
        final Container topAncestor = getTopAncestor(component);
        if (topAncestor == null) {
            return;
        }
        Hashtable registerNewTopContainer = this.containerMap.get(topAncestor);
        if (registerNewTopContainer == null) {
            registerNewTopContainer = this.registerNewTopContainer(topAncestor);
        }
        final Object value = registerNewTopContainer.get(keyStroke);
        if (value == null) {
            registerNewTopContainer.put(keyStroke, component);
        }
        else if (value instanceof Vector) {
            final Vector vector = (Vector)value;
            if (!vector.contains(component)) {
                vector.addElement(component);
            }
        }
        else if (value instanceof JComponent) {
            if (value != component) {
                final Vector<JComponent> vector2 = new Vector<JComponent>();
                vector2.addElement((JComponent)value);
                vector2.addElement(component);
                registerNewTopContainer.put(keyStroke, vector2);
            }
        }
        else {
            System.out.println("Unexpected condition in registerKeyStroke");
            Thread.dumpStack();
        }
        this.componentKeyStrokeMap.put(new ComponentKeyStrokePair(component, keyStroke), topAncestor);
        if (topAncestor instanceof EmbeddedFrame) {
            ((EmbeddedFrame)topAncestor).registerAccelerator(keyStroke);
        }
    }
    
    private static Container getTopAncestor(final JComponent component) {
        for (Container container = component.getParent(); container != null; container = container.getParent()) {
            if ((container instanceof Window && ((Window)container).isFocusableWindow()) || container instanceof Applet || container instanceof JInternalFrame) {
                return container;
            }
        }
        return null;
    }
    
    public void unregisterKeyStroke(final KeyStroke keyStroke, final JComponent component) {
        final ComponentKeyStrokePair componentKeyStrokePair = new ComponentKeyStrokePair(component, keyStroke);
        final Container container = this.componentKeyStrokeMap.get(componentKeyStrokePair);
        if (container == null) {
            return;
        }
        final Hashtable hashtable = this.containerMap.get(container);
        if (hashtable == null) {
            Thread.dumpStack();
            return;
        }
        final Object value = hashtable.get(keyStroke);
        if (value == null) {
            Thread.dumpStack();
            return;
        }
        if (value instanceof JComponent && value == component) {
            hashtable.remove(keyStroke);
        }
        else if (value instanceof Vector) {
            final Vector vector = (Vector)value;
            vector.removeElement(component);
            if (vector.isEmpty()) {
                hashtable.remove(keyStroke);
            }
        }
        if (hashtable.isEmpty()) {
            this.containerMap.remove(container);
        }
        this.componentKeyStrokeMap.remove(componentKeyStrokePair);
        if (container instanceof EmbeddedFrame) {
            ((EmbeddedFrame)container).unregisterAccelerator(keyStroke);
        }
    }
    
    public boolean fireKeyboardAction(final KeyEvent keyEvent, final boolean b, final Container container) {
        if (keyEvent.isConsumed()) {
            System.out.println("Acquired pre-used event!");
            Thread.dumpStack();
        }
        KeyStroke keyStroke = null;
        KeyStroke keyStroke2;
        if (keyEvent.getID() == 400) {
            keyStroke2 = KeyStroke.getKeyStroke(keyEvent.getKeyChar());
        }
        else {
            if (keyEvent.getKeyCode() != keyEvent.getExtendedKeyCode()) {
                keyStroke = KeyStroke.getKeyStroke(keyEvent.getExtendedKeyCode(), keyEvent.getModifiers(), !b);
            }
            keyStroke2 = KeyStroke.getKeyStroke(keyEvent.getKeyCode(), keyEvent.getModifiers(), !b);
        }
        final Hashtable hashtable = this.containerMap.get(container);
        if (hashtable != null) {
            Object o = null;
            if (keyStroke != null) {
                o = hashtable.get(keyStroke);
                if (o != null) {
                    keyStroke2 = keyStroke;
                }
            }
            if (o == null) {
                o = hashtable.get(keyStroke2);
            }
            if (o != null) {
                if (o instanceof JComponent) {
                    final JComponent component = (JComponent)o;
                    if (component.isShowing() && component.isEnabled()) {
                        this.fireBinding(component, keyStroke2, keyEvent, b);
                    }
                }
                else if (o instanceof Vector) {
                    final Vector vector = (Vector)o;
                    for (int i = vector.size() - 1; i >= 0; --i) {
                        final JComponent component2 = vector.elementAt(i);
                        if (component2.isShowing() && component2.isEnabled()) {
                            this.fireBinding(component2, keyStroke2, keyEvent, b);
                            if (keyEvent.isConsumed()) {
                                return true;
                            }
                        }
                    }
                }
                else {
                    System.out.println("Unexpected condition in fireKeyboardAction " + o);
                    Thread.dumpStack();
                }
            }
        }
        if (keyEvent.isConsumed()) {
            return true;
        }
        if (hashtable != null) {
            final Vector vector2 = (Vector)hashtable.get(JMenuBar.class);
            if (vector2 != null) {
                final Enumeration elements = vector2.elements();
                while (elements.hasMoreElements()) {
                    final JMenuBar menuBar = (JMenuBar)elements.nextElement();
                    if (menuBar.isShowing() && menuBar.isEnabled()) {
                        final boolean b2 = keyStroke != null && !keyStroke.equals(keyStroke2);
                        if (b2) {
                            this.fireBinding(menuBar, keyStroke, keyEvent, b);
                        }
                        if (!b2 || !keyEvent.isConsumed()) {
                            this.fireBinding(menuBar, keyStroke2, keyEvent, b);
                        }
                        if (keyEvent.isConsumed()) {
                            return true;
                        }
                        continue;
                    }
                }
            }
        }
        return keyEvent.isConsumed();
    }
    
    void fireBinding(final JComponent component, final KeyStroke keyStroke, final KeyEvent keyEvent, final boolean b) {
        if (component.processKeyBinding(keyStroke, keyEvent, 2, b)) {
            keyEvent.consume();
        }
    }
    
    public void registerMenuBar(final JMenuBar menuBar) {
        final Container topAncestor = getTopAncestor(menuBar);
        if (topAncestor == null) {
            return;
        }
        Hashtable registerNewTopContainer = this.containerMap.get(topAncestor);
        if (registerNewTopContainer == null) {
            registerNewTopContainer = this.registerNewTopContainer(topAncestor);
        }
        Vector<?> vector = (Vector<?>)registerNewTopContainer.get(JMenuBar.class);
        if (vector == null) {
            vector = new Vector<Object>();
            registerNewTopContainer.put(JMenuBar.class, vector);
        }
        if (!vector.contains(menuBar)) {
            vector.addElement(menuBar);
        }
    }
    
    public void unregisterMenuBar(final JMenuBar menuBar) {
        final Container topAncestor = getTopAncestor(menuBar);
        if (topAncestor == null) {
            return;
        }
        final Hashtable hashtable = this.containerMap.get(topAncestor);
        if (hashtable != null) {
            final Vector vector = (Vector)hashtable.get(JMenuBar.class);
            if (vector != null) {
                vector.removeElement(menuBar);
                if (vector.isEmpty()) {
                    hashtable.remove(JMenuBar.class);
                    if (hashtable.isEmpty()) {
                        this.containerMap.remove(topAncestor);
                    }
                }
            }
        }
    }
    
    protected Hashtable registerNewTopContainer(final Container container) {
        final Hashtable hashtable = new Hashtable();
        this.containerMap.put(container, hashtable);
        return hashtable;
    }
    
    static {
        KeyboardManager.currentManager = new KeyboardManager();
    }
    
    class ComponentKeyStrokePair
    {
        Object component;
        Object keyStroke;
        
        public ComponentKeyStrokePair(final Object component, final Object keyStroke) {
            this.component = component;
            this.keyStroke = keyStroke;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof ComponentKeyStrokePair)) {
                return false;
            }
            final ComponentKeyStrokePair componentKeyStrokePair = (ComponentKeyStrokePair)o;
            return this.component.equals(componentKeyStrokePair.component) && this.keyStroke.equals(componentKeyStrokePair.keyStroke);
        }
        
        @Override
        public int hashCode() {
            return this.component.hashCode() * this.keyStroke.hashCode();
        }
    }
}
