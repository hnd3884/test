package com.sun.java.accessibility.util;

import java.awt.event.WindowEvent;
import java.awt.event.TextEvent;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.ItemEvent;
import java.awt.event.FocusEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.ActionEvent;
import java.awt.Container;
import java.awt.Adjustable;
import java.lang.reflect.InvocationTargetException;
import javax.swing.MenuElement;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import java.awt.KeyboardFocusManager;
import javax.swing.event.ChangeEvent;
import java.awt.Window;
import javax.swing.MenuSelectionManager;
import java.lang.reflect.Method;
import javax.swing.event.ChangeListener;
import java.awt.AWTEventMulticaster;
import sun.security.util.SecurityConstants;
import java.awt.event.TextListener;
import java.awt.event.ItemListener;
import java.awt.event.AdjustmentListener;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.event.KeyListener;
import java.awt.event.FocusListener;
import java.awt.event.ContainerListener;
import java.awt.event.ComponentListener;
import java.awt.Component;
import jdk.Exported;

@Exported
public class AWTEventMonitor
{
    private static boolean runningOnJDK1_4;
    @Deprecated
    protected static Component componentWithFocus;
    private static Component componentWithFocus_private;
    @Deprecated
    protected static ComponentListener componentListener;
    private static ComponentListener componentListener_private;
    @Deprecated
    protected static ContainerListener containerListener;
    private static ContainerListener containerListener_private;
    @Deprecated
    protected static FocusListener focusListener;
    private static FocusListener focusListener_private;
    @Deprecated
    protected static KeyListener keyListener;
    private static KeyListener keyListener_private;
    @Deprecated
    protected static MouseListener mouseListener;
    private static MouseListener mouseListener_private;
    @Deprecated
    protected static MouseMotionListener mouseMotionListener;
    private static MouseMotionListener mouseMotionListener_private;
    @Deprecated
    protected static WindowListener windowListener;
    private static WindowListener windowListener_private;
    @Deprecated
    protected static ActionListener actionListener;
    private static ActionListener actionListener_private;
    @Deprecated
    protected static AdjustmentListener adjustmentListener;
    private static AdjustmentListener adjustmentListener_private;
    @Deprecated
    protected static ItemListener itemListener;
    private static ItemListener itemListener_private;
    @Deprecated
    protected static TextListener textListener;
    private static TextListener textListener_private;
    @Deprecated
    protected static AWTEventsListener awtListener;
    private static final AWTEventsListener awtListener_private;
    
    public static Component getComponentWithFocus() {
        return AWTEventMonitor.componentWithFocus_private;
    }
    
    private static void checkInstallPermission() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(SecurityConstants.AWT.ALL_AWT_EVENTS_PERMISSION);
        }
    }
    
    public static void addComponentListener(final ComponentListener componentListener) {
        if (AWTEventMonitor.componentListener_private == null) {
            checkInstallPermission();
            AWTEventMonitor.awtListener_private.installListeners(2);
        }
        AWTEventMonitor.componentListener_private = AWTEventMulticaster.add(AWTEventMonitor.componentListener_private, componentListener);
    }
    
    public static void removeComponentListener(final ComponentListener componentListener) {
        AWTEventMonitor.componentListener_private = AWTEventMulticaster.remove(AWTEventMonitor.componentListener_private, componentListener);
        if (AWTEventMonitor.componentListener_private == null) {
            AWTEventMonitor.awtListener_private.removeListeners(2);
        }
    }
    
    public static void addContainerListener(final ContainerListener containerListener) {
        AWTEventMonitor.containerListener_private = AWTEventMulticaster.add(AWTEventMonitor.containerListener_private, containerListener);
    }
    
    public static void removeContainerListener(final ContainerListener containerListener) {
        AWTEventMonitor.containerListener_private = AWTEventMulticaster.remove(AWTEventMonitor.containerListener_private, containerListener);
    }
    
    public static void addFocusListener(final FocusListener focusListener) {
        AWTEventMonitor.focusListener_private = AWTEventMulticaster.add(AWTEventMonitor.focusListener_private, focusListener);
    }
    
    public static void removeFocusListener(final FocusListener focusListener) {
        AWTEventMonitor.focusListener_private = AWTEventMulticaster.remove(AWTEventMonitor.focusListener_private, focusListener);
    }
    
    public static void addKeyListener(final KeyListener keyListener) {
        if (AWTEventMonitor.keyListener_private == null) {
            checkInstallPermission();
            AWTEventMonitor.awtListener_private.installListeners(6);
        }
        AWTEventMonitor.keyListener_private = AWTEventMulticaster.add(AWTEventMonitor.keyListener_private, keyListener);
    }
    
    public static void removeKeyListener(final KeyListener keyListener) {
        AWTEventMonitor.keyListener_private = AWTEventMulticaster.remove(AWTEventMonitor.keyListener_private, keyListener);
        if (AWTEventMonitor.keyListener_private == null) {
            AWTEventMonitor.awtListener_private.removeListeners(6);
        }
    }
    
    public static void addMouseListener(final MouseListener mouseListener) {
        if (AWTEventMonitor.mouseListener_private == null) {
            checkInstallPermission();
            AWTEventMonitor.awtListener_private.installListeners(7);
        }
        AWTEventMonitor.mouseListener_private = AWTEventMulticaster.add(AWTEventMonitor.mouseListener_private, mouseListener);
    }
    
    public static void removeMouseListener(final MouseListener mouseListener) {
        AWTEventMonitor.mouseListener_private = AWTEventMulticaster.remove(AWTEventMonitor.mouseListener_private, mouseListener);
        if (AWTEventMonitor.mouseListener_private == null) {
            AWTEventMonitor.awtListener_private.removeListeners(7);
        }
    }
    
    public static void addMouseMotionListener(final MouseMotionListener mouseMotionListener) {
        if (AWTEventMonitor.mouseMotionListener_private == null) {
            checkInstallPermission();
            AWTEventMonitor.awtListener_private.installListeners(8);
        }
        AWTEventMonitor.mouseMotionListener_private = AWTEventMulticaster.add(AWTEventMonitor.mouseMotionListener_private, mouseMotionListener);
    }
    
    public static void removeMouseMotionListener(final MouseMotionListener mouseMotionListener) {
        AWTEventMonitor.mouseMotionListener_private = AWTEventMulticaster.remove(AWTEventMonitor.mouseMotionListener_private, mouseMotionListener);
        if (AWTEventMonitor.mouseMotionListener_private == null) {
            AWTEventMonitor.awtListener_private.removeListeners(8);
        }
    }
    
    public static void addWindowListener(final WindowListener windowListener) {
        if (AWTEventMonitor.windowListener_private == null) {
            checkInstallPermission();
            AWTEventMonitor.awtListener_private.installListeners(11);
        }
        AWTEventMonitor.windowListener_private = AWTEventMulticaster.add(AWTEventMonitor.windowListener_private, windowListener);
    }
    
    public static void removeWindowListener(final WindowListener windowListener) {
        AWTEventMonitor.windowListener_private = AWTEventMulticaster.remove(AWTEventMonitor.windowListener_private, windowListener);
        if (AWTEventMonitor.windowListener_private == null) {
            AWTEventMonitor.awtListener_private.removeListeners(11);
        }
    }
    
    public static void addActionListener(final ActionListener actionListener) {
        if (AWTEventMonitor.actionListener_private == null) {
            checkInstallPermission();
            AWTEventMonitor.awtListener_private.installListeners(0);
        }
        AWTEventMonitor.actionListener_private = AWTEventMulticaster.add(AWTEventMonitor.actionListener_private, actionListener);
    }
    
    public static void removeActionListener(final ActionListener actionListener) {
        AWTEventMonitor.actionListener_private = AWTEventMulticaster.remove(AWTEventMonitor.actionListener_private, actionListener);
        if (AWTEventMonitor.actionListener_private == null) {
            AWTEventMonitor.awtListener_private.removeListeners(0);
        }
    }
    
    public static void addAdjustmentListener(final AdjustmentListener adjustmentListener) {
        if (AWTEventMonitor.adjustmentListener_private == null) {
            checkInstallPermission();
            AWTEventMonitor.awtListener_private.installListeners(1);
        }
        AWTEventMonitor.adjustmentListener_private = AWTEventMulticaster.add(AWTEventMonitor.adjustmentListener_private, adjustmentListener);
    }
    
    public static void removeAdjustmentListener(final AdjustmentListener adjustmentListener) {
        AWTEventMonitor.adjustmentListener_private = AWTEventMulticaster.remove(AWTEventMonitor.adjustmentListener_private, adjustmentListener);
        if (AWTEventMonitor.adjustmentListener_private == null) {
            AWTEventMonitor.awtListener_private.removeListeners(1);
        }
    }
    
    public static void addItemListener(final ItemListener itemListener) {
        if (AWTEventMonitor.itemListener_private == null) {
            checkInstallPermission();
            AWTEventMonitor.awtListener_private.installListeners(5);
        }
        AWTEventMonitor.itemListener_private = AWTEventMulticaster.add(AWTEventMonitor.itemListener_private, itemListener);
    }
    
    public static void removeItemListener(final ItemListener itemListener) {
        AWTEventMonitor.itemListener_private = AWTEventMulticaster.remove(AWTEventMonitor.itemListener_private, itemListener);
        if (AWTEventMonitor.itemListener_private == null) {
            AWTEventMonitor.awtListener_private.removeListeners(5);
        }
    }
    
    public static void addTextListener(final TextListener textListener) {
        if (AWTEventMonitor.textListener_private == null) {
            checkInstallPermission();
            AWTEventMonitor.awtListener_private.installListeners(10);
        }
        AWTEventMonitor.textListener_private = AWTEventMulticaster.add(AWTEventMonitor.textListener_private, textListener);
    }
    
    public static void removeTextListener(final TextListener textListener) {
        AWTEventMonitor.textListener_private = AWTEventMulticaster.remove(AWTEventMonitor.textListener_private, textListener);
        if (AWTEventMonitor.textListener_private == null) {
            AWTEventMonitor.awtListener_private.removeListeners(10);
        }
    }
    
    static {
        AWTEventMonitor.runningOnJDK1_4 = false;
        AWTEventMonitor.componentWithFocus = null;
        AWTEventMonitor.componentWithFocus_private = null;
        AWTEventMonitor.componentListener = null;
        AWTEventMonitor.componentListener_private = null;
        AWTEventMonitor.containerListener = null;
        AWTEventMonitor.containerListener_private = null;
        AWTEventMonitor.focusListener = null;
        AWTEventMonitor.focusListener_private = null;
        AWTEventMonitor.keyListener = null;
        AWTEventMonitor.keyListener_private = null;
        AWTEventMonitor.mouseListener = null;
        AWTEventMonitor.mouseListener_private = null;
        AWTEventMonitor.mouseMotionListener = null;
        AWTEventMonitor.mouseMotionListener_private = null;
        AWTEventMonitor.windowListener = null;
        AWTEventMonitor.windowListener_private = null;
        AWTEventMonitor.actionListener = null;
        AWTEventMonitor.actionListener_private = null;
        AWTEventMonitor.adjustmentListener = null;
        AWTEventMonitor.adjustmentListener_private = null;
        AWTEventMonitor.itemListener = null;
        AWTEventMonitor.itemListener_private = null;
        AWTEventMonitor.textListener = null;
        AWTEventMonitor.textListener_private = null;
        AWTEventMonitor.awtListener = new AWTEventsListener();
        awtListener_private = new AWTEventsListener();
    }
    
    static class AWTEventsListener implements TopLevelWindowListener, ActionListener, AdjustmentListener, ComponentListener, ContainerListener, FocusListener, ItemListener, KeyListener, MouseListener, MouseMotionListener, TextListener, WindowListener, ChangeListener
    {
        private Class[] actionListeners;
        private Method removeActionMethod;
        private Method addActionMethod;
        private Object[] actionArgs;
        private Class[] itemListeners;
        private Method removeItemMethod;
        private Method addItemMethod;
        private Object[] itemArgs;
        private Class[] textListeners;
        private Method removeTextMethod;
        private Method addTextMethod;
        private Object[] textArgs;
        private Class[] windowListeners;
        private Method removeWindowMethod;
        private Method addWindowMethod;
        private Object[] windowArgs;
        
        public AWTEventsListener() {
            final String property = System.getProperty("java.version");
            if (property != null) {
                AWTEventMonitor.runningOnJDK1_4 = (property.compareTo("1.4") >= 0);
            }
            this.initializeIntrospection();
            this.installListeners();
            if (AWTEventMonitor.runningOnJDK1_4) {
                MenuSelectionManager.defaultManager().addChangeListener(this);
            }
            EventQueueMonitor.addTopLevelWindowListener(this);
        }
        
        private boolean initializeIntrospection() {
            try {
                this.actionListeners = new Class[1];
                this.actionArgs = new Object[1];
                this.actionListeners[0] = Class.forName("java.awt.event.ActionListener");
                this.actionArgs[0] = this;
                this.itemListeners = new Class[1];
                this.itemArgs = new Object[1];
                this.itemListeners[0] = Class.forName("java.awt.event.ItemListener");
                this.itemArgs[0] = this;
                this.textListeners = new Class[1];
                this.textArgs = new Object[1];
                this.textListeners[0] = Class.forName("java.awt.event.TextListener");
                this.textArgs[0] = this;
                this.windowListeners = new Class[1];
                this.windowArgs = new Object[1];
                this.windowListeners[0] = Class.forName("java.awt.event.WindowListener");
                this.windowArgs[0] = this;
                return true;
            }
            catch (final ClassNotFoundException ex) {
                System.out.println("EXCEPTION - Class 'java.awt.event.*' not in CLASSPATH");
                return false;
            }
        }
        
        protected void installListeners() {
            final Window[] topLevelWindows = EventQueueMonitor.getTopLevelWindows();
            if (topLevelWindows != null) {
                for (int i = 0; i < topLevelWindows.length; ++i) {
                    this.installListeners(topLevelWindows[i]);
                }
            }
        }
        
        protected void installListeners(final int n) {
            final Window[] topLevelWindows = EventQueueMonitor.getTopLevelWindows();
            if (topLevelWindows != null) {
                for (int i = 0; i < topLevelWindows.length; ++i) {
                    this.installListeners(topLevelWindows[i], n);
                }
            }
        }
        
        protected void installListeners(final Component component) {
            this.installListeners(component, 3);
            this.installListeners(component, 4);
            if (AWTEventMonitor.componentListener_private != null) {
                this.installListeners(component, 2);
            }
            if (AWTEventMonitor.keyListener_private != null) {
                this.installListeners(component, 6);
            }
            if (AWTEventMonitor.mouseListener_private != null) {
                this.installListeners(component, 7);
            }
            if (AWTEventMonitor.mouseMotionListener_private != null) {
                this.installListeners(component, 8);
            }
            if (AWTEventMonitor.windowListener_private != null) {
                this.installListeners(component, 11);
            }
            if (AWTEventMonitor.actionListener_private != null) {
                this.installListeners(component, 0);
            }
            if (AWTEventMonitor.adjustmentListener_private != null) {
                this.installListeners(component, 1);
            }
            if (AWTEventMonitor.itemListener_private != null) {
                this.installListeners(component, 5);
            }
            if (AWTEventMonitor.textListener_private != null) {
                this.installListeners(component, 10);
            }
        }
        
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            this.processFocusGained();
        }
        
        private void processFocusGained() {
            final Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            if (focusOwner == null) {
                return;
            }
            MenuSelectionManager.defaultManager().removeChangeListener(this);
            MenuSelectionManager.defaultManager().addChangeListener(this);
            if (focusOwner instanceof JRootPane) {
                final MenuElement[] selectedPath = MenuSelectionManager.defaultManager().getSelectedPath();
                if (selectedPath.length > 1) {
                    final Component component = selectedPath[selectedPath.length - 2].getComponent();
                    final Component component2 = selectedPath[selectedPath.length - 1].getComponent();
                    if (component2 instanceof JPopupMenu || component2 instanceof JMenu) {
                        AWTEventMonitor.componentWithFocus_private = component2;
                    }
                    else if (component instanceof JPopupMenu) {
                        AWTEventMonitor.componentWithFocus_private = component;
                    }
                }
            }
            else {
                AWTEventMonitor.componentWithFocus_private = focusOwner;
            }
        }
        
        protected void installListeners(final Component component, final int n) {
            switch (n) {
                case 0: {
                    try {
                        this.removeActionMethod = component.getClass().getMethod("removeActionListener", (Class<?>[])this.actionListeners);
                        this.addActionMethod = component.getClass().getMethod("addActionListener", (Class<?>[])this.actionListeners);
                        try {
                            this.removeActionMethod.invoke(component, this.actionArgs);
                            this.addActionMethod.invoke(component, this.actionArgs);
                        }
                        catch (final InvocationTargetException ex) {
                            System.out.println("Exception: " + ex.toString());
                        }
                        catch (final IllegalAccessException ex2) {
                            System.out.println("Exception: " + ex2.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex3) {}
                    catch (final SecurityException ex4) {
                        System.out.println("Exception: " + ex4.toString());
                    }
                    break;
                }
                case 1: {
                    if (component instanceof Adjustable) {
                        ((Adjustable)component).removeAdjustmentListener(this);
                        ((Adjustable)component).addAdjustmentListener(this);
                        break;
                    }
                    break;
                }
                case 2: {
                    component.removeComponentListener(this);
                    component.addComponentListener(this);
                    break;
                }
                case 3: {
                    if (component instanceof Container) {
                        ((Container)component).removeContainerListener(this);
                        ((Container)component).addContainerListener(this);
                        break;
                    }
                    break;
                }
                case 4: {
                    component.removeFocusListener(this);
                    component.addFocusListener(this);
                    if (AWTEventMonitor.runningOnJDK1_4) {
                        this.processFocusGained();
                        break;
                    }
                    if (component != AWTEventMonitor.componentWithFocus_private && component.hasFocus()) {
                        AWTEventMonitor.componentWithFocus_private = component;
                        break;
                    }
                    break;
                }
                case 5: {
                    try {
                        this.removeItemMethod = component.getClass().getMethod("removeItemListener", (Class<?>[])this.itemListeners);
                        this.addItemMethod = component.getClass().getMethod("addItemListener", (Class<?>[])this.itemListeners);
                        try {
                            this.removeItemMethod.invoke(component, this.itemArgs);
                            this.addItemMethod.invoke(component, this.itemArgs);
                        }
                        catch (final InvocationTargetException ex5) {
                            System.out.println("Exception: " + ex5.toString());
                        }
                        catch (final IllegalAccessException ex6) {
                            System.out.println("Exception: " + ex6.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex7) {}
                    catch (final SecurityException ex8) {
                        System.out.println("Exception: " + ex8.toString());
                    }
                    break;
                }
                case 6: {
                    component.removeKeyListener(this);
                    component.addKeyListener(this);
                    break;
                }
                case 7: {
                    component.removeMouseListener(this);
                    component.addMouseListener(this);
                    break;
                }
                case 8: {
                    component.removeMouseMotionListener(this);
                    component.addMouseMotionListener(this);
                    break;
                }
                case 10: {
                    try {
                        this.removeTextMethod = component.getClass().getMethod("removeTextListener", (Class<?>[])this.textListeners);
                        this.addTextMethod = component.getClass().getMethod("addTextListener", (Class<?>[])this.textListeners);
                        try {
                            this.removeTextMethod.invoke(component, this.textArgs);
                            this.addTextMethod.invoke(component, this.textArgs);
                        }
                        catch (final InvocationTargetException ex9) {
                            System.out.println("Exception: " + ex9.toString());
                        }
                        catch (final IllegalAccessException ex10) {
                            System.out.println("Exception: " + ex10.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex11) {}
                    catch (final SecurityException ex12) {
                        System.out.println("Exception: " + ex12.toString());
                    }
                    break;
                }
                case 11: {
                    try {
                        this.removeWindowMethod = component.getClass().getMethod("removeWindowListener", (Class<?>[])this.windowListeners);
                        this.addWindowMethod = component.getClass().getMethod("addWindowListener", (Class<?>[])this.windowListeners);
                        try {
                            this.removeWindowMethod.invoke(component, this.windowArgs);
                            this.addWindowMethod.invoke(component, this.windowArgs);
                        }
                        catch (final InvocationTargetException ex13) {
                            System.out.println("Exception: " + ex13.toString());
                        }
                        catch (final IllegalAccessException ex14) {
                            System.out.println("Exception: " + ex14.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex15) {}
                    catch (final SecurityException ex16) {
                        System.out.println("Exception: " + ex16.toString());
                    }
                    break;
                }
                default: {
                    return;
                }
            }
            if (component instanceof Container) {
                for (int componentCount = ((Container)component).getComponentCount(), i = 0; i < componentCount; ++i) {
                    this.installListeners(((Container)component).getComponent(i), n);
                }
            }
        }
        
        protected void removeListeners(final int n) {
            final Window[] topLevelWindows = EventQueueMonitor.getTopLevelWindows();
            if (topLevelWindows != null) {
                for (int i = 0; i < topLevelWindows.length; ++i) {
                    this.removeListeners(topLevelWindows[i], n);
                }
            }
        }
        
        protected void removeListeners(final Component component) {
            if (AWTEventMonitor.componentListener_private != null) {
                this.removeListeners(component, 2);
            }
            if (AWTEventMonitor.keyListener_private != null) {
                this.removeListeners(component, 6);
            }
            if (AWTEventMonitor.mouseListener_private != null) {
                this.removeListeners(component, 7);
            }
            if (AWTEventMonitor.mouseMotionListener_private != null) {
                this.removeListeners(component, 8);
            }
            if (AWTEventMonitor.windowListener_private != null) {
                this.removeListeners(component, 11);
            }
            if (AWTEventMonitor.actionListener_private != null) {
                this.removeListeners(component, 0);
            }
            if (AWTEventMonitor.adjustmentListener_private != null) {
                this.removeListeners(component, 1);
            }
            if (AWTEventMonitor.itemListener_private != null) {
                this.removeListeners(component, 5);
            }
            if (AWTEventMonitor.textListener_private != null) {
                this.removeListeners(component, 10);
            }
        }
        
        protected void removeListeners(final Component component, final int n) {
            switch (n) {
                case 0: {
                    try {
                        this.removeActionMethod = component.getClass().getMethod("removeActionListener", (Class<?>[])this.actionListeners);
                        try {
                            this.removeActionMethod.invoke(component, this.actionArgs);
                        }
                        catch (final InvocationTargetException ex) {
                            System.out.println("Exception: " + ex.toString());
                        }
                        catch (final IllegalAccessException ex2) {
                            System.out.println("Exception: " + ex2.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex3) {}
                    catch (final SecurityException ex4) {
                        System.out.println("Exception: " + ex4.toString());
                    }
                    break;
                }
                case 1: {
                    if (component instanceof Adjustable) {
                        ((Adjustable)component).removeAdjustmentListener(this);
                        break;
                    }
                    break;
                }
                case 2: {
                    component.removeComponentListener(this);
                    break;
                }
                case 5: {
                    try {
                        this.removeItemMethod = component.getClass().getMethod("removeItemListener", (Class<?>[])this.itemListeners);
                        try {
                            this.removeItemMethod.invoke(component, this.itemArgs);
                        }
                        catch (final InvocationTargetException ex5) {
                            System.out.println("Exception: " + ex5.toString());
                        }
                        catch (final IllegalAccessException ex6) {
                            System.out.println("Exception: " + ex6.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex7) {}
                    catch (final SecurityException ex8) {
                        System.out.println("Exception: " + ex8.toString());
                    }
                    break;
                }
                case 6: {
                    component.removeKeyListener(this);
                    break;
                }
                case 7: {
                    component.removeMouseListener(this);
                    break;
                }
                case 8: {
                    component.removeMouseMotionListener(this);
                    break;
                }
                case 10: {
                    try {
                        this.removeTextMethod = component.getClass().getMethod("removeTextListener", (Class<?>[])this.textListeners);
                        try {
                            this.removeTextMethod.invoke(component, this.textArgs);
                        }
                        catch (final InvocationTargetException ex9) {
                            System.out.println("Exception: " + ex9.toString());
                        }
                        catch (final IllegalAccessException ex10) {
                            System.out.println("Exception: " + ex10.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex11) {}
                    catch (final SecurityException ex12) {
                        System.out.println("Exception: " + ex12.toString());
                    }
                    break;
                }
                case 11: {
                    try {
                        this.removeWindowMethod = component.getClass().getMethod("removeWindowListener", (Class<?>[])this.windowListeners);
                        try {
                            this.removeWindowMethod.invoke(component, this.windowArgs);
                        }
                        catch (final InvocationTargetException ex13) {
                            System.out.println("Exception: " + ex13.toString());
                        }
                        catch (final IllegalAccessException ex14) {
                            System.out.println("Exception: " + ex14.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex15) {}
                    catch (final SecurityException ex16) {
                        System.out.println("Exception: " + ex16.toString());
                    }
                    break;
                }
                default: {
                    return;
                }
            }
            if (component instanceof Container) {
                for (int componentCount = ((Container)component).getComponentCount(), i = 0; i < componentCount; ++i) {
                    this.removeListeners(((Container)component).getComponent(i), n);
                }
            }
        }
        
        @Override
        public void topLevelWindowCreated(final Window window) {
            this.installListeners(window);
        }
        
        @Override
        public void topLevelWindowDestroyed(final Window window) {
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (AWTEventMonitor.actionListener_private != null) {
                AWTEventMonitor.actionListener_private.actionPerformed(actionEvent);
            }
        }
        
        @Override
        public void adjustmentValueChanged(final AdjustmentEvent adjustmentEvent) {
            if (AWTEventMonitor.adjustmentListener_private != null) {
                AWTEventMonitor.adjustmentListener_private.adjustmentValueChanged(adjustmentEvent);
            }
        }
        
        @Override
        public void componentHidden(final ComponentEvent componentEvent) {
            if (AWTEventMonitor.componentListener_private != null) {
                AWTEventMonitor.componentListener_private.componentHidden(componentEvent);
            }
        }
        
        @Override
        public void componentMoved(final ComponentEvent componentEvent) {
            if (AWTEventMonitor.componentListener_private != null) {
                AWTEventMonitor.componentListener_private.componentMoved(componentEvent);
            }
        }
        
        @Override
        public void componentResized(final ComponentEvent componentEvent) {
            if (AWTEventMonitor.componentListener_private != null) {
                AWTEventMonitor.componentListener_private.componentResized(componentEvent);
            }
        }
        
        @Override
        public void componentShown(final ComponentEvent componentEvent) {
            if (AWTEventMonitor.componentListener_private != null) {
                AWTEventMonitor.componentListener_private.componentShown(componentEvent);
            }
        }
        
        @Override
        public void componentAdded(final ContainerEvent containerEvent) {
            this.installListeners(containerEvent.getChild());
            if (AWTEventMonitor.containerListener_private != null) {
                AWTEventMonitor.containerListener_private.componentAdded(containerEvent);
            }
        }
        
        @Override
        public void componentRemoved(final ContainerEvent containerEvent) {
            this.removeListeners(containerEvent.getChild());
            if (AWTEventMonitor.containerListener_private != null) {
                AWTEventMonitor.containerListener_private.componentRemoved(containerEvent);
            }
        }
        
        @Override
        public void focusGained(final FocusEvent focusEvent) {
            AWTEventMonitor.componentWithFocus_private = (Component)focusEvent.getSource();
            if (AWTEventMonitor.focusListener_private != null) {
                AWTEventMonitor.focusListener_private.focusGained(focusEvent);
            }
        }
        
        @Override
        public void focusLost(final FocusEvent focusEvent) {
            AWTEventMonitor.componentWithFocus_private = null;
            if (AWTEventMonitor.focusListener_private != null) {
                AWTEventMonitor.focusListener_private.focusLost(focusEvent);
            }
        }
        
        @Override
        public void itemStateChanged(final ItemEvent itemEvent) {
            if (AWTEventMonitor.itemListener_private != null) {
                AWTEventMonitor.itemListener_private.itemStateChanged(itemEvent);
            }
        }
        
        @Override
        public void keyPressed(final KeyEvent keyEvent) {
            if (AWTEventMonitor.keyListener_private != null) {
                AWTEventMonitor.keyListener_private.keyPressed(keyEvent);
            }
        }
        
        @Override
        public void keyReleased(final KeyEvent keyEvent) {
            if (AWTEventMonitor.keyListener_private != null) {
                AWTEventMonitor.keyListener_private.keyReleased(keyEvent);
            }
        }
        
        @Override
        public void keyTyped(final KeyEvent keyEvent) {
            if (AWTEventMonitor.keyListener_private != null) {
                AWTEventMonitor.keyListener_private.keyTyped(keyEvent);
            }
        }
        
        @Override
        public void mouseClicked(final MouseEvent mouseEvent) {
            if (AWTEventMonitor.mouseListener_private != null) {
                AWTEventMonitor.mouseListener_private.mouseClicked(mouseEvent);
            }
        }
        
        @Override
        public void mouseEntered(final MouseEvent mouseEvent) {
            if (AWTEventMonitor.mouseListener_private != null) {
                AWTEventMonitor.mouseListener_private.mouseEntered(mouseEvent);
            }
        }
        
        @Override
        public void mouseExited(final MouseEvent mouseEvent) {
            if (AWTEventMonitor.mouseListener_private != null) {
                AWTEventMonitor.mouseListener_private.mouseExited(mouseEvent);
            }
        }
        
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            if (AWTEventMonitor.mouseListener_private != null) {
                AWTEventMonitor.mouseListener_private.mousePressed(mouseEvent);
            }
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
            if (AWTEventMonitor.mouseListener_private != null) {
                AWTEventMonitor.mouseListener_private.mouseReleased(mouseEvent);
            }
        }
        
        @Override
        public void mouseDragged(final MouseEvent mouseEvent) {
            if (AWTEventMonitor.mouseMotionListener_private != null) {
                AWTEventMonitor.mouseMotionListener_private.mouseDragged(mouseEvent);
            }
        }
        
        @Override
        public void mouseMoved(final MouseEvent mouseEvent) {
            if (AWTEventMonitor.mouseMotionListener_private != null) {
                AWTEventMonitor.mouseMotionListener_private.mouseMoved(mouseEvent);
            }
        }
        
        @Override
        public void textValueChanged(final TextEvent textEvent) {
            if (AWTEventMonitor.textListener_private != null) {
                AWTEventMonitor.textListener_private.textValueChanged(textEvent);
            }
        }
        
        @Override
        public void windowOpened(final WindowEvent windowEvent) {
            if (AWTEventMonitor.windowListener_private != null) {
                AWTEventMonitor.windowListener_private.windowOpened(windowEvent);
            }
        }
        
        @Override
        public void windowClosing(final WindowEvent windowEvent) {
            if (AWTEventMonitor.windowListener_private != null) {
                AWTEventMonitor.windowListener_private.windowClosing(windowEvent);
            }
        }
        
        @Override
        public void windowClosed(final WindowEvent windowEvent) {
            if (AWTEventMonitor.windowListener_private != null) {
                AWTEventMonitor.windowListener_private.windowClosed(windowEvent);
            }
        }
        
        @Override
        public void windowIconified(final WindowEvent windowEvent) {
            if (AWTEventMonitor.windowListener_private != null) {
                AWTEventMonitor.windowListener_private.windowIconified(windowEvent);
            }
        }
        
        @Override
        public void windowDeiconified(final WindowEvent windowEvent) {
            if (AWTEventMonitor.windowListener_private != null) {
                AWTEventMonitor.windowListener_private.windowDeiconified(windowEvent);
            }
        }
        
        @Override
        public void windowActivated(final WindowEvent windowEvent) {
            if (AWTEventMonitor.windowListener_private != null) {
                AWTEventMonitor.windowListener_private.windowActivated(windowEvent);
            }
        }
        
        @Override
        public void windowDeactivated(final WindowEvent windowEvent) {
            if (AWTEventMonitor.windowListener_private != null) {
                AWTEventMonitor.windowListener_private.windowDeactivated(windowEvent);
            }
        }
    }
}
