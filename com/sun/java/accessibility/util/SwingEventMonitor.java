package com.sun.java.accessibility.util;

import java.beans.PropertyVetoException;
import java.beans.PropertyChangeEvent;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.MenuEvent;
import javax.swing.event.ListDataEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.CaretEvent;
import javax.swing.event.AncestorEvent;
import java.awt.event.ContainerEvent;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.ListSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.table.TableModel;
import javax.swing.ListModel;
import javax.swing.text.JTextComponent;
import javax.swing.text.Document;
import javax.swing.table.TableColumnModel;
import javax.swing.CellEditor;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JComponent;
import java.awt.event.ContainerListener;
import java.awt.Container;
import java.awt.Component;
import java.lang.reflect.Method;
import java.beans.VetoableChangeListener;
import java.beans.PropertyChangeListener;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TableModelListener;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.MenuListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListDataListener;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.ChangeListener;
import javax.swing.event.CellEditorListener;
import javax.swing.event.CaretListener;
import javax.swing.event.AncestorListener;
import javax.swing.event.EventListenerList;
import jdk.Exported;

@Exported
public class SwingEventMonitor extends AWTEventMonitor
{
    protected static final EventListenerList listenerList;
    protected static final SwingEventListener swingListener;
    
    public static void addAncestorListener(final AncestorListener ancestorListener) {
        if (SwingEventMonitor.listenerList.getListenerCount(AncestorListener.class) == 0) {
            SwingEventMonitor.swingListener.installListeners(12);
        }
        SwingEventMonitor.listenerList.add(AncestorListener.class, ancestorListener);
    }
    
    public static void removeAncestorListener(final AncestorListener ancestorListener) {
        SwingEventMonitor.listenerList.remove(AncestorListener.class, ancestorListener);
        if (SwingEventMonitor.listenerList.getListenerCount(AncestorListener.class) == 0) {
            SwingEventMonitor.swingListener.removeListeners(12);
        }
    }
    
    public static void addCaretListener(final CaretListener caretListener) {
        if (SwingEventMonitor.listenerList.getListenerCount(CaretListener.class) == 0) {
            SwingEventMonitor.swingListener.installListeners(13);
        }
        SwingEventMonitor.listenerList.add(CaretListener.class, caretListener);
    }
    
    public static void removeCaretListener(final CaretListener caretListener) {
        SwingEventMonitor.listenerList.remove(CaretListener.class, caretListener);
        if (SwingEventMonitor.listenerList.getListenerCount(CaretListener.class) == 0) {
            SwingEventMonitor.swingListener.removeListeners(13);
        }
    }
    
    public static void addCellEditorListener(final CellEditorListener cellEditorListener) {
        if (SwingEventMonitor.listenerList.getListenerCount(CellEditorListener.class) == 0) {
            SwingEventMonitor.swingListener.installListeners(14);
        }
        SwingEventMonitor.listenerList.add(CellEditorListener.class, cellEditorListener);
    }
    
    public static void removeCellEditorListener(final CellEditorListener cellEditorListener) {
        SwingEventMonitor.listenerList.remove(CellEditorListener.class, cellEditorListener);
        if (SwingEventMonitor.listenerList.getListenerCount(CellEditorListener.class) == 0) {
            SwingEventMonitor.swingListener.removeListeners(14);
        }
    }
    
    public static void addChangeListener(final ChangeListener changeListener) {
        if (SwingEventMonitor.listenerList.getListenerCount(ChangeListener.class) == 0) {
            SwingEventMonitor.swingListener.installListeners(15);
        }
        SwingEventMonitor.listenerList.add(ChangeListener.class, changeListener);
    }
    
    public static void removeChangeListener(final ChangeListener changeListener) {
        SwingEventMonitor.listenerList.remove(ChangeListener.class, changeListener);
        if (SwingEventMonitor.listenerList.getListenerCount(ChangeListener.class) == 0) {
            SwingEventMonitor.swingListener.removeListeners(15);
        }
    }
    
    public static void addColumnModelListener(final TableColumnModelListener tableColumnModelListener) {
        if (SwingEventMonitor.listenerList.getListenerCount(TableColumnModelListener.class) == 0) {
            SwingEventMonitor.swingListener.installListeners(16);
        }
        SwingEventMonitor.listenerList.add(TableColumnModelListener.class, tableColumnModelListener);
    }
    
    public static void removeColumnModelListener(final TableColumnModelListener tableColumnModelListener) {
        SwingEventMonitor.listenerList.remove(TableColumnModelListener.class, tableColumnModelListener);
        if (SwingEventMonitor.listenerList.getListenerCount(TableColumnModelListener.class) == 0) {
            SwingEventMonitor.swingListener.removeListeners(16);
        }
    }
    
    public static void addDocumentListener(final DocumentListener documentListener) {
        if (SwingEventMonitor.listenerList.getListenerCount(DocumentListener.class) == 0) {
            SwingEventMonitor.swingListener.installListeners(17);
        }
        SwingEventMonitor.listenerList.add(DocumentListener.class, documentListener);
    }
    
    public static void removeDocumentListener(final DocumentListener documentListener) {
        SwingEventMonitor.listenerList.remove(DocumentListener.class, documentListener);
        if (SwingEventMonitor.listenerList.getListenerCount(DocumentListener.class) == 0) {
            SwingEventMonitor.swingListener.removeListeners(17);
        }
    }
    
    public static void addListDataListener(final ListDataListener listDataListener) {
        if (SwingEventMonitor.listenerList.getListenerCount(ListDataListener.class) == 0) {
            SwingEventMonitor.swingListener.installListeners(18);
        }
        SwingEventMonitor.listenerList.add(ListDataListener.class, listDataListener);
    }
    
    public static void removeListDataListener(final ListDataListener listDataListener) {
        SwingEventMonitor.listenerList.remove(ListDataListener.class, listDataListener);
        if (SwingEventMonitor.listenerList.getListenerCount(ListDataListener.class) == 0) {
            SwingEventMonitor.swingListener.removeListeners(18);
        }
    }
    
    public static void addListSelectionListener(final ListSelectionListener listSelectionListener) {
        if (SwingEventMonitor.listenerList.getListenerCount(ListSelectionListener.class) == 0) {
            SwingEventMonitor.swingListener.installListeners(19);
        }
        SwingEventMonitor.listenerList.add(ListSelectionListener.class, listSelectionListener);
    }
    
    public static void removeListSelectionListener(final ListSelectionListener listSelectionListener) {
        SwingEventMonitor.listenerList.remove(ListSelectionListener.class, listSelectionListener);
        if (SwingEventMonitor.listenerList.getListenerCount(ListSelectionListener.class) == 0) {
            SwingEventMonitor.swingListener.removeListeners(19);
        }
    }
    
    public static void addMenuListener(final MenuListener menuListener) {
        if (SwingEventMonitor.listenerList.getListenerCount(MenuListener.class) == 0) {
            SwingEventMonitor.swingListener.installListeners(20);
        }
        SwingEventMonitor.listenerList.add(MenuListener.class, menuListener);
    }
    
    public static void removeMenuListener(final MenuListener menuListener) {
        SwingEventMonitor.listenerList.remove(MenuListener.class, menuListener);
        if (SwingEventMonitor.listenerList.getListenerCount(MenuListener.class) == 0) {
            SwingEventMonitor.swingListener.removeListeners(20);
        }
    }
    
    public static void addPopupMenuListener(final PopupMenuListener popupMenuListener) {
        if (SwingEventMonitor.listenerList.getListenerCount(PopupMenuListener.class) == 0) {
            SwingEventMonitor.swingListener.installListeners(21);
        }
        SwingEventMonitor.listenerList.add(PopupMenuListener.class, popupMenuListener);
    }
    
    public static void removePopupMenuListener(final PopupMenuListener popupMenuListener) {
        SwingEventMonitor.listenerList.remove(PopupMenuListener.class, popupMenuListener);
        if (SwingEventMonitor.listenerList.getListenerCount(PopupMenuListener.class) == 0) {
            SwingEventMonitor.swingListener.removeListeners(21);
        }
    }
    
    public static void addTableModelListener(final TableModelListener tableModelListener) {
        if (SwingEventMonitor.listenerList.getListenerCount(TableModelListener.class) == 0) {
            SwingEventMonitor.swingListener.installListeners(22);
        }
        SwingEventMonitor.listenerList.add(TableModelListener.class, tableModelListener);
    }
    
    public static void removeTableModelListener(final TableModelListener tableModelListener) {
        SwingEventMonitor.listenerList.remove(TableModelListener.class, tableModelListener);
        if (SwingEventMonitor.listenerList.getListenerCount(TableModelListener.class) == 0) {
            SwingEventMonitor.swingListener.removeListeners(22);
        }
    }
    
    public static void addTreeExpansionListener(final TreeExpansionListener treeExpansionListener) {
        if (SwingEventMonitor.listenerList.getListenerCount(TreeExpansionListener.class) == 0) {
            SwingEventMonitor.swingListener.installListeners(23);
        }
        SwingEventMonitor.listenerList.add(TreeExpansionListener.class, treeExpansionListener);
    }
    
    public static void removeTreeExpansionListener(final TreeExpansionListener treeExpansionListener) {
        SwingEventMonitor.listenerList.remove(TreeExpansionListener.class, treeExpansionListener);
        if (SwingEventMonitor.listenerList.getListenerCount(TreeExpansionListener.class) == 0) {
            SwingEventMonitor.swingListener.removeListeners(23);
        }
    }
    
    public static void addTreeModelListener(final TreeModelListener treeModelListener) {
        if (SwingEventMonitor.listenerList.getListenerCount(TreeModelListener.class) == 0) {
            SwingEventMonitor.swingListener.installListeners(24);
        }
        SwingEventMonitor.listenerList.add(TreeModelListener.class, treeModelListener);
    }
    
    public static void removeTreeModelListener(final TreeModelListener treeModelListener) {
        SwingEventMonitor.listenerList.remove(TreeModelListener.class, treeModelListener);
        if (SwingEventMonitor.listenerList.getListenerCount(TreeModelListener.class) == 0) {
            SwingEventMonitor.swingListener.removeListeners(24);
        }
    }
    
    public static void addTreeSelectionListener(final TreeSelectionListener treeSelectionListener) {
        if (SwingEventMonitor.listenerList.getListenerCount(TreeSelectionListener.class) == 0) {
            SwingEventMonitor.swingListener.installListeners(25);
        }
        SwingEventMonitor.listenerList.add(TreeSelectionListener.class, treeSelectionListener);
    }
    
    public static void removeTreeSelectionListener(final TreeSelectionListener treeSelectionListener) {
        SwingEventMonitor.listenerList.remove(TreeSelectionListener.class, treeSelectionListener);
        if (SwingEventMonitor.listenerList.getListenerCount(TreeSelectionListener.class) == 0) {
            SwingEventMonitor.swingListener.removeListeners(25);
        }
    }
    
    public static void addUndoableEditListener(final UndoableEditListener undoableEditListener) {
        if (SwingEventMonitor.listenerList.getListenerCount(UndoableEditListener.class) == 0) {
            SwingEventMonitor.swingListener.installListeners(26);
        }
        SwingEventMonitor.listenerList.add(UndoableEditListener.class, undoableEditListener);
    }
    
    public static void removeUndoableEditListener(final UndoableEditListener undoableEditListener) {
        SwingEventMonitor.listenerList.remove(UndoableEditListener.class, undoableEditListener);
        if (SwingEventMonitor.listenerList.getListenerCount(UndoableEditListener.class) == 0) {
            SwingEventMonitor.swingListener.removeListeners(26);
        }
    }
    
    public static void addInternalFrameListener(final InternalFrameListener internalFrameListener) {
        if (SwingEventMonitor.listenerList.getListenerCount(InternalFrameListener.class) == 0) {
            SwingEventMonitor.swingListener.installListeners(29);
        }
        SwingEventMonitor.listenerList.add(InternalFrameListener.class, internalFrameListener);
    }
    
    public static void removeInternalFrameListener(final InternalFrameListener internalFrameListener) {
        SwingEventMonitor.listenerList.remove(InternalFrameListener.class, internalFrameListener);
        if (SwingEventMonitor.listenerList.getListenerCount(InternalFrameListener.class) == 0) {
            SwingEventMonitor.swingListener.removeListeners(29);
        }
    }
    
    public static void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        if (SwingEventMonitor.listenerList.getListenerCount(PropertyChangeListener.class) == 0) {
            SwingEventMonitor.swingListener.installListeners(27);
        }
        SwingEventMonitor.listenerList.add(PropertyChangeListener.class, propertyChangeListener);
    }
    
    public static void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        SwingEventMonitor.listenerList.remove(PropertyChangeListener.class, propertyChangeListener);
        if (SwingEventMonitor.listenerList.getListenerCount(PropertyChangeListener.class) == 0) {
            SwingEventMonitor.swingListener.removeListeners(27);
        }
    }
    
    public static void addVetoableChangeListener(final VetoableChangeListener vetoableChangeListener) {
        if (SwingEventMonitor.listenerList.getListenerCount(VetoableChangeListener.class) == 0) {
            SwingEventMonitor.swingListener.installListeners(28);
        }
        SwingEventMonitor.listenerList.add(VetoableChangeListener.class, vetoableChangeListener);
    }
    
    public static void removeVetoableChangeListener(final VetoableChangeListener vetoableChangeListener) {
        SwingEventMonitor.listenerList.remove(VetoableChangeListener.class, vetoableChangeListener);
        if (SwingEventMonitor.listenerList.getListenerCount(VetoableChangeListener.class) == 0) {
            SwingEventMonitor.swingListener.removeListeners(28);
        }
    }
    
    static {
        listenerList = new EventListenerList();
        swingListener = new SwingEventListener();
    }
    
    static class SwingEventListener extends AWTEventsListener implements AncestorListener, CaretListener, CellEditorListener, ChangeListener, DocumentListener, ListDataListener, ListSelectionListener, MenuListener, PopupMenuListener, TableColumnModelListener, TableModelListener, TreeExpansionListener, TreeModelListener, TreeSelectionListener, UndoableEditListener, InternalFrameListener, PropertyChangeListener, VetoableChangeListener
    {
        private Class[] caretListeners;
        private Method removeCaretMethod;
        private Method addCaretMethod;
        private Object[] caretArgs;
        private Class[] cellEditorListeners;
        private Method removeCellEditorMethod;
        private Method addCellEditorMethod;
        private Object[] cellEditorArgs;
        private Method getCellEditorMethod;
        private Class[] changeListeners;
        private Method removeChangeMethod;
        private Method addChangeMethod;
        private Object[] changeArgs;
        private Method getColumnModelMethod;
        private Class[] documentListeners;
        private Method removeDocumentMethod;
        private Method addDocumentMethod;
        private Object[] documentArgs;
        private Method getDocumentMethod;
        private Method getModelMethod;
        private Class[] listSelectionListeners;
        private Method removeListSelectionMethod;
        private Method addListSelectionMethod;
        private Object[] listSelectionArgs;
        private Method getSelectionModelMethod;
        private Class[] menuListeners;
        private Method removeMenuMethod;
        private Method addMenuMethod;
        private Object[] menuArgs;
        private Class[] popupMenuListeners;
        private Method removePopupMenuMethod;
        private Method addPopupMenuMethod;
        private Object[] popupMenuArgs;
        private Method getPopupMenuMethod;
        private Class[] treeExpansionListeners;
        private Method removeTreeExpansionMethod;
        private Method addTreeExpansionMethod;
        private Object[] treeExpansionArgs;
        private Class[] treeSelectionListeners;
        private Method removeTreeSelectionMethod;
        private Method addTreeSelectionMethod;
        private Object[] treeSelectionArgs;
        private Class[] undoableEditListeners;
        private Method removeUndoableEditMethod;
        private Method addUndoableEditMethod;
        private Object[] undoableEditArgs;
        private Class[] internalFrameListeners;
        private Method removeInternalFrameMethod;
        private Method addInternalFrameMethod;
        private Object[] internalFrameArgs;
        private Class[] propertyChangeListeners;
        private Method removePropertyChangeMethod;
        private Method addPropertyChangeMethod;
        private Object[] propertyChangeArgs;
        private Class[] nullClass;
        private Object[] nullArgs;
        
        public SwingEventListener() {
            this.initializeIntrospection();
            this.installListeners();
            EventQueueMonitor.addTopLevelWindowListener(this);
        }
        
        private boolean initializeIntrospection() {
            try {
                this.caretListeners = new Class[1];
                this.caretArgs = new Object[1];
                this.caretListeners[0] = Class.forName("javax.swing.event.CaretListener");
                this.caretArgs[0] = this;
                this.cellEditorListeners = new Class[1];
                this.cellEditorArgs = new Object[1];
                this.cellEditorListeners[0] = Class.forName("javax.swing.event.CellEditorListener");
                this.cellEditorArgs[0] = this;
                this.changeListeners = new Class[1];
                this.changeArgs = new Object[1];
                this.changeListeners[0] = Class.forName("javax.swing.event.ChangeListener");
                this.changeArgs[0] = this;
                this.documentListeners = new Class[1];
                this.documentArgs = new Object[1];
                this.documentListeners[0] = Class.forName("javax.swing.event.DocumentListener");
                this.documentArgs[0] = this;
                this.listSelectionListeners = new Class[1];
                this.listSelectionArgs = new Object[1];
                this.listSelectionListeners[0] = Class.forName("javax.swing.event.ListSelectionListener");
                this.listSelectionArgs[0] = this;
                this.menuListeners = new Class[1];
                this.menuArgs = new Object[1];
                this.menuListeners[0] = Class.forName("javax.swing.event.MenuListener");
                this.menuArgs[0] = this;
                this.popupMenuListeners = new Class[1];
                this.popupMenuArgs = new Object[1];
                this.popupMenuListeners[0] = Class.forName("javax.swing.event.PopupMenuListener");
                this.popupMenuArgs[0] = this;
                this.treeExpansionListeners = new Class[1];
                this.treeExpansionArgs = new Object[1];
                this.treeExpansionListeners[0] = Class.forName("javax.swing.event.TreeExpansionListener");
                this.treeExpansionArgs[0] = this;
                this.treeSelectionListeners = new Class[1];
                this.treeSelectionArgs = new Object[1];
                this.treeSelectionListeners[0] = Class.forName("javax.swing.event.TreeSelectionListener");
                this.treeSelectionArgs[0] = this;
                this.undoableEditListeners = new Class[1];
                this.undoableEditArgs = new Object[1];
                this.undoableEditListeners[0] = Class.forName("javax.swing.event.UndoableEditListener");
                this.undoableEditArgs[0] = this;
                this.internalFrameListeners = new Class[1];
                this.internalFrameArgs = new Object[1];
                this.internalFrameListeners[0] = Class.forName("javax.swing.event.InternalFrameListener");
                this.internalFrameArgs[0] = this;
                this.nullClass = new Class[0];
                this.nullArgs = new Object[0];
            }
            catch (final ClassNotFoundException ex) {
                System.out.println("EXCEPTION - Class 'javax.swing.event.*' not in CLASSPATH");
                return false;
            }
            try {
                this.propertyChangeListeners = new Class[1];
                this.propertyChangeArgs = new Object[1];
                this.propertyChangeListeners[0] = Class.forName("java.beans.PropertyChangeListener");
                this.propertyChangeArgs[0] = this;
            }
            catch (final ClassNotFoundException ex2) {
                System.out.println("EXCEPTION - Class 'java.beans.*' not in CLASSPATH");
                return false;
            }
            return true;
        }
        
        @Override
        protected void installListeners(final Component component) {
            this.installListeners(component, 3);
            if (SwingEventMonitor.listenerList.getListenerCount(AncestorListener.class) > 0) {
                this.installListeners(component, 12);
            }
            if (SwingEventMonitor.listenerList.getListenerCount(CaretListener.class) > 0) {
                this.installListeners(component, 13);
            }
            if (SwingEventMonitor.listenerList.getListenerCount(CellEditorListener.class) > 0) {
                this.installListeners(component, 14);
            }
            if (SwingEventMonitor.listenerList.getListenerCount(ChangeListener.class) > 0) {
                this.installListeners(component, 15);
            }
            if (SwingEventMonitor.listenerList.getListenerCount(TableColumnModelListener.class) > 0) {
                this.installListeners(component, 16);
            }
            if (SwingEventMonitor.listenerList.getListenerCount(DocumentListener.class) > 0) {
                this.installListeners(component, 17);
            }
            if (SwingEventMonitor.listenerList.getListenerCount(ListDataListener.class) > 0) {
                this.installListeners(component, 18);
            }
            if (SwingEventMonitor.listenerList.getListenerCount(ListSelectionListener.class) > 0) {
                this.installListeners(component, 19);
            }
            if (SwingEventMonitor.listenerList.getListenerCount(MenuListener.class) > 0) {
                this.installListeners(component, 20);
            }
            if (SwingEventMonitor.listenerList.getListenerCount(PopupMenuListener.class) > 0) {
                this.installListeners(component, 21);
            }
            if (SwingEventMonitor.listenerList.getListenerCount(TableModelListener.class) > 0) {
                this.installListeners(component, 22);
            }
            if (SwingEventMonitor.listenerList.getListenerCount(TreeExpansionListener.class) > 0) {
                this.installListeners(component, 23);
            }
            if (SwingEventMonitor.listenerList.getListenerCount(TreeModelListener.class) > 0) {
                this.installListeners(component, 24);
            }
            if (SwingEventMonitor.listenerList.getListenerCount(TreeSelectionListener.class) > 0) {
                this.installListeners(component, 25);
            }
            if (SwingEventMonitor.listenerList.getListenerCount(UndoableEditListener.class) > 0) {
                this.installListeners(component, 26);
            }
            if (SwingEventMonitor.listenerList.getListenerCount(InternalFrameListener.class) > 0) {
                this.installListeners(component, 29);
            }
            if (SwingEventMonitor.listenerList.getListenerCount(PropertyChangeListener.class) > 0) {
                this.installListeners(component, 27);
            }
            if (SwingEventMonitor.listenerList.getListenerCount(VetoableChangeListener.class) > 0) {
                this.installListeners(component, 28);
            }
            super.installListeners(component);
        }
        
        @Override
        protected void installListeners(final Component component, final int n) {
            switch (n) {
                case 3: {
                    if (component instanceof Container) {
                        ((Container)component).removeContainerListener(this);
                        ((Container)component).addContainerListener(this);
                        break;
                    }
                    break;
                }
                case 12: {
                    if (component instanceof JComponent) {
                        ((JComponent)component).removeAncestorListener(this);
                        ((JComponent)component).addAncestorListener(this);
                        break;
                    }
                    break;
                }
                case 13: {
                    try {
                        this.removeCaretMethod = component.getClass().getMethod("removeCaretListener", (Class<?>[])this.caretListeners);
                        this.addCaretMethod = component.getClass().getMethod("addCaretListener", (Class<?>[])this.caretListeners);
                        try {
                            this.removeCaretMethod.invoke(component, this.caretArgs);
                            this.addCaretMethod.invoke(component, this.caretArgs);
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
                case 14: {
                    try {
                        this.getCellEditorMethod = component.getClass().getMethod("getCellEditorMethod", (Class<?>[])this.nullClass);
                        try {
                            final Object invoke = this.getCellEditorMethod.invoke(component, this.nullArgs);
                            if (invoke != null && invoke instanceof CellEditor) {
                                ((CellEditor)invoke).removeCellEditorListener(this);
                                ((CellEditor)invoke).addCellEditorListener(this);
                            }
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
                    try {
                        this.removeCellEditorMethod = component.getClass().getMethod("removeCellEditorListener", (Class<?>[])this.cellEditorListeners);
                        this.addCellEditorMethod = component.getClass().getMethod("addCellEditorListener", (Class<?>[])this.cellEditorListeners);
                        try {
                            this.removeCellEditorMethod.invoke(component, this.cellEditorArgs);
                            this.addCellEditorMethod.invoke(component, this.cellEditorArgs);
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
                case 15: {
                    try {
                        this.removeChangeMethod = component.getClass().getMethod("removeChangeListener", (Class<?>[])this.changeListeners);
                        this.addChangeMethod = component.getClass().getMethod("addChangeListener", (Class<?>[])this.changeListeners);
                        try {
                            this.removeChangeMethod.invoke(component, this.changeArgs);
                            this.addChangeMethod.invoke(component, this.changeArgs);
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
                    try {
                        this.getModelMethod = component.getClass().getMethod("getModel", (Class<?>[])this.nullClass);
                        try {
                            final Object invoke2 = this.getModelMethod.invoke(component, this.nullArgs);
                            if (invoke2 != null) {
                                this.removeChangeMethod = invoke2.getClass().getMethod("removeChangeListener", (Class<?>[])this.changeListeners);
                                this.addChangeMethod = invoke2.getClass().getMethod("addChangeListener", (Class<?>[])this.changeListeners);
                                this.removeChangeMethod.invoke(invoke2, this.changeArgs);
                                this.addChangeMethod.invoke(invoke2, this.changeArgs);
                            }
                        }
                        catch (final InvocationTargetException ex17) {
                            System.out.println("Exception: " + ex17.toString());
                        }
                        catch (final IllegalAccessException ex18) {
                            System.out.println("Exception: " + ex18.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex19) {}
                    catch (final SecurityException ex20) {
                        System.out.println("Exception: " + ex20.toString());
                    }
                    break;
                }
                case 16: {
                    try {
                        this.getColumnModelMethod = component.getClass().getMethod("getTableColumnModel", (Class<?>[])this.nullClass);
                        try {
                            final Object invoke3 = this.getColumnModelMethod.invoke(component, this.nullArgs);
                            if (invoke3 != null && invoke3 instanceof TableColumnModel) {
                                ((TableColumnModel)invoke3).removeColumnModelListener(this);
                                ((TableColumnModel)invoke3).addColumnModelListener(this);
                            }
                        }
                        catch (final InvocationTargetException ex21) {
                            System.out.println("Exception: " + ex21.toString());
                        }
                        catch (final IllegalAccessException ex22) {
                            System.out.println("Exception: " + ex22.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex23) {}
                    catch (final SecurityException ex24) {
                        System.out.println("Exception: " + ex24.toString());
                    }
                    break;
                }
                case 17: {
                    try {
                        this.getDocumentMethod = component.getClass().getMethod("getDocument", (Class<?>[])this.nullClass);
                        try {
                            final Object invoke4 = this.getDocumentMethod.invoke(component, this.nullArgs);
                            if (invoke4 != null && invoke4 instanceof Document) {
                                ((Document)invoke4).removeDocumentListener(this);
                                ((Document)invoke4).addDocumentListener(this);
                            }
                        }
                        catch (final InvocationTargetException ex25) {
                            System.out.println("Exception: " + ex25.toString());
                        }
                        catch (final IllegalAccessException ex26) {
                            System.out.println("Exception: " + ex26.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex27) {}
                    catch (final SecurityException ex28) {
                        System.out.println("Exception: " + ex28.toString());
                    }
                    try {
                        this.removeDocumentMethod = component.getClass().getMethod("removeDocumentListener", (Class<?>[])this.documentListeners);
                        this.addDocumentMethod = component.getClass().getMethod("addDocumentListener", (Class<?>[])this.documentListeners);
                        try {
                            this.removeDocumentMethod.invoke(component, this.documentArgs);
                            this.addDocumentMethod.invoke(component, this.documentArgs);
                        }
                        catch (final InvocationTargetException ex29) {
                            System.out.println("Exception: " + ex29.toString());
                        }
                        catch (final IllegalAccessException ex30) {
                            System.out.println("Exception: " + ex30.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex31) {}
                    catch (final SecurityException ex32) {
                        System.out.println("Exception: " + ex32.toString());
                    }
                    if (component instanceof JTextComponent) {
                        try {
                            this.removePropertyChangeMethod = component.getClass().getMethod("removePropertyChangeListener", (Class<?>[])this.propertyChangeListeners);
                            this.addPropertyChangeMethod = component.getClass().getMethod("addPropertyChangeListener", (Class<?>[])this.propertyChangeListeners);
                            try {
                                this.removePropertyChangeMethod.invoke(component, this.propertyChangeArgs);
                                this.addPropertyChangeMethod.invoke(component, this.propertyChangeArgs);
                            }
                            catch (final InvocationTargetException ex33) {
                                System.out.println("Exception: " + ex33.toString());
                            }
                            catch (final IllegalAccessException ex34) {
                                System.out.println("Exception: " + ex34.toString());
                            }
                        }
                        catch (final NoSuchMethodException ex35) {}
                        catch (final SecurityException ex36) {
                            System.out.println("Exception: " + ex36.toString());
                        }
                        break;
                    }
                    break;
                }
                case 18:
                case 22:
                case 24: {
                    try {
                        this.getModelMethod = component.getClass().getMethod("getModel", (Class<?>[])this.nullClass);
                        try {
                            final Object invoke5 = this.getModelMethod.invoke(component, this.nullArgs);
                            if (invoke5 != null) {
                                if (n == 18 && invoke5 instanceof ListModel) {
                                    ((ListModel)invoke5).removeListDataListener(this);
                                    ((ListModel)invoke5).addListDataListener(this);
                                }
                                else if (n == 22 && invoke5 instanceof TableModel) {
                                    ((TableModel)invoke5).removeTableModelListener(this);
                                    ((TableModel)invoke5).addTableModelListener(this);
                                }
                                else if (invoke5 instanceof TreeModel) {
                                    ((TreeModel)invoke5).removeTreeModelListener(this);
                                    ((TreeModel)invoke5).addTreeModelListener(this);
                                }
                            }
                        }
                        catch (final InvocationTargetException ex37) {
                            System.out.println("Exception: " + ex37.toString());
                        }
                        catch (final IllegalAccessException ex38) {
                            System.out.println("Exception: " + ex38.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex39) {}
                    catch (final SecurityException ex40) {
                        System.out.println("Exception: " + ex40.toString());
                    }
                    break;
                }
                case 19: {
                    try {
                        this.removeListSelectionMethod = component.getClass().getMethod("removeListSelectionListener", (Class<?>[])this.listSelectionListeners);
                        this.addListSelectionMethod = component.getClass().getMethod("addListSelectionListener", (Class<?>[])this.listSelectionListeners);
                        try {
                            this.removeListSelectionMethod.invoke(component, this.listSelectionArgs);
                            this.addListSelectionMethod.invoke(component, this.listSelectionArgs);
                        }
                        catch (final InvocationTargetException ex41) {
                            System.out.println("Exception: " + ex41.toString());
                        }
                        catch (final IllegalAccessException ex42) {
                            System.out.println("Exception: " + ex42.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex43) {}
                    catch (final SecurityException ex44) {
                        System.out.println("Exception: " + ex44.toString());
                    }
                    try {
                        this.getSelectionModelMethod = component.getClass().getMethod("getSelectionModel", (Class<?>[])this.nullClass);
                        try {
                            final Object invoke6 = this.getSelectionModelMethod.invoke(component, this.nullArgs);
                            if (invoke6 != null && invoke6 instanceof ListSelectionModel) {
                                ((ListSelectionModel)invoke6).removeListSelectionListener(this);
                                ((ListSelectionModel)invoke6).addListSelectionListener(this);
                            }
                        }
                        catch (final InvocationTargetException ex45) {
                            System.out.println("Exception: " + ex45.toString());
                        }
                        catch (final IllegalAccessException ex46) {
                            System.out.println("Exception: " + ex46.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex47) {}
                    catch (final SecurityException ex48) {
                        System.out.println("Exception: " + ex48.toString());
                    }
                    break;
                }
                case 20: {
                    try {
                        this.removeMenuMethod = component.getClass().getMethod("removeMenuListener", (Class<?>[])this.menuListeners);
                        this.addMenuMethod = component.getClass().getMethod("addMenuListener", (Class<?>[])this.menuListeners);
                        try {
                            this.removeMenuMethod.invoke(component, this.menuArgs);
                            this.addMenuMethod.invoke(component, this.menuArgs);
                        }
                        catch (final InvocationTargetException ex49) {
                            System.out.println("Exception: " + ex49.toString());
                        }
                        catch (final IllegalAccessException ex50) {
                            System.out.println("Exception: " + ex50.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex51) {}
                    catch (final SecurityException ex52) {
                        System.out.println("Exception: " + ex52.toString());
                    }
                    break;
                }
                case 21: {
                    try {
                        this.removePopupMenuMethod = component.getClass().getMethod("removePopupMenuListener", (Class<?>[])this.popupMenuListeners);
                        this.addPopupMenuMethod = component.getClass().getMethod("addPopupMenuListener", (Class<?>[])this.popupMenuListeners);
                        try {
                            this.removePopupMenuMethod.invoke(component, this.popupMenuArgs);
                            this.addPopupMenuMethod.invoke(component, this.popupMenuArgs);
                        }
                        catch (final InvocationTargetException ex53) {
                            System.out.println("Exception: " + ex53.toString());
                        }
                        catch (final IllegalAccessException ex54) {
                            System.out.println("Exception: " + ex54.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex55) {}
                    catch (final SecurityException ex56) {
                        System.out.println("Exception: " + ex56.toString());
                    }
                    try {
                        this.getPopupMenuMethod = component.getClass().getMethod("getPopupMenu", (Class<?>[])this.nullClass);
                        try {
                            final Object invoke7 = this.getPopupMenuMethod.invoke(component, this.nullArgs);
                            if (invoke7 != null) {
                                this.removePopupMenuMethod = invoke7.getClass().getMethod("removePopupMenuListener", (Class<?>[])this.popupMenuListeners);
                                this.addPopupMenuMethod = invoke7.getClass().getMethod("addPopupMenuListener", (Class<?>[])this.popupMenuListeners);
                                this.removePopupMenuMethod.invoke(invoke7, this.popupMenuArgs);
                                this.addPopupMenuMethod.invoke(invoke7, this.popupMenuArgs);
                            }
                        }
                        catch (final InvocationTargetException ex57) {
                            System.out.println("Exception: " + ex57.toString());
                        }
                        catch (final IllegalAccessException ex58) {
                            System.out.println("Exception: " + ex58.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex59) {}
                    catch (final SecurityException ex60) {
                        System.out.println("Exception: " + ex60.toString());
                    }
                    break;
                }
                case 23: {
                    try {
                        this.removeTreeExpansionMethod = component.getClass().getMethod("removeTreeExpansionListener", (Class<?>[])this.treeExpansionListeners);
                        this.addTreeExpansionMethod = component.getClass().getMethod("addTreeExpansionListener", (Class<?>[])this.treeExpansionListeners);
                        try {
                            this.removeTreeExpansionMethod.invoke(component, this.treeExpansionArgs);
                            this.addTreeExpansionMethod.invoke(component, this.treeExpansionArgs);
                        }
                        catch (final InvocationTargetException ex61) {
                            System.out.println("Exception: " + ex61.toString());
                        }
                        catch (final IllegalAccessException ex62) {
                            System.out.println("Exception: " + ex62.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex63) {}
                    catch (final SecurityException ex64) {
                        System.out.println("Exception: " + ex64.toString());
                    }
                    break;
                }
                case 25: {
                    try {
                        this.removeTreeSelectionMethod = component.getClass().getMethod("removeTreeSelectionListener", (Class<?>[])this.treeSelectionListeners);
                        this.addTreeSelectionMethod = component.getClass().getMethod("addTreeSelectionListener", (Class<?>[])this.treeSelectionListeners);
                        try {
                            this.removeTreeSelectionMethod.invoke(component, this.treeSelectionArgs);
                            this.addTreeSelectionMethod.invoke(component, this.treeSelectionArgs);
                        }
                        catch (final InvocationTargetException ex65) {
                            System.out.println("Exception: " + ex65.toString());
                        }
                        catch (final IllegalAccessException ex66) {
                            System.out.println("Exception: " + ex66.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex67) {}
                    catch (final SecurityException ex68) {
                        System.out.println("Exception: " + ex68.toString());
                    }
                    break;
                }
                case 26: {
                    try {
                        this.getDocumentMethod = component.getClass().getMethod("getDocument", (Class<?>[])this.nullClass);
                        try {
                            final Object invoke8 = this.getDocumentMethod.invoke(component, this.nullArgs);
                            if (invoke8 != null && invoke8 instanceof Document) {
                                ((Document)invoke8).removeUndoableEditListener(this);
                                ((Document)invoke8).addUndoableEditListener(this);
                            }
                        }
                        catch (final InvocationTargetException ex69) {
                            System.out.println("Exception: " + ex69.toString());
                        }
                        catch (final IllegalAccessException ex70) {
                            System.out.println("Exception: " + ex70.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex71) {}
                    catch (final SecurityException ex72) {
                        System.out.println("Exception: " + ex72.toString());
                    }
                    try {
                        this.removeUndoableEditMethod = component.getClass().getMethod("removeUndoableEditListener", (Class<?>[])this.undoableEditListeners);
                        this.addUndoableEditMethod = component.getClass().getMethod("addUndoableEditListener", (Class<?>[])this.undoableEditListeners);
                        try {
                            this.removeUndoableEditMethod.invoke(component, this.undoableEditArgs);
                            this.addUndoableEditMethod.invoke(component, this.undoableEditArgs);
                        }
                        catch (final InvocationTargetException ex73) {
                            System.out.println("Exception: " + ex73.toString());
                        }
                        catch (final IllegalAccessException ex74) {
                            System.out.println("Exception: " + ex74.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex75) {}
                    catch (final SecurityException ex76) {
                        System.out.println("Exception: " + ex76.toString());
                    }
                    break;
                }
                case 29: {
                    try {
                        this.removeInternalFrameMethod = component.getClass().getMethod("removeInternalFrameListener", (Class<?>[])this.internalFrameListeners);
                        this.addInternalFrameMethod = component.getClass().getMethod("addInternalFrameListener", (Class<?>[])this.internalFrameListeners);
                        try {
                            this.removeInternalFrameMethod.invoke(component, this.internalFrameArgs);
                            this.addInternalFrameMethod.invoke(component, this.internalFrameArgs);
                        }
                        catch (final InvocationTargetException ex77) {
                            System.out.println("Exception: " + ex77.toString());
                        }
                        catch (final IllegalAccessException ex78) {
                            System.out.println("Exception: " + ex78.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex79) {}
                    catch (final SecurityException ex80) {
                        System.out.println("Exception: " + ex80.toString());
                    }
                    break;
                }
                case 27: {
                    try {
                        this.removePropertyChangeMethod = component.getClass().getMethod("removePropertyChangeListener", (Class<?>[])this.propertyChangeListeners);
                        this.addPropertyChangeMethod = component.getClass().getMethod("addPropertyChangeListener", (Class<?>[])this.propertyChangeListeners);
                        try {
                            this.removePropertyChangeMethod.invoke(component, this.propertyChangeArgs);
                            this.addPropertyChangeMethod.invoke(component, this.propertyChangeArgs);
                        }
                        catch (final InvocationTargetException ex81) {
                            System.out.println("Exception: " + ex81.toString());
                        }
                        catch (final IllegalAccessException ex82) {
                            System.out.println("Exception: " + ex82.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex83) {}
                    catch (final SecurityException ex84) {
                        System.out.println("Exception: " + ex84.toString());
                    }
                    try {
                        this.getSelectionModelMethod = component.getClass().getMethod("getSelectionModel", (Class<?>[])this.nullClass);
                        try {
                            final Object invoke9 = this.getSelectionModelMethod.invoke(component, this.nullArgs);
                            if (invoke9 != null && invoke9 instanceof TreeSelectionModel) {
                                ((TreeSelectionModel)invoke9).removePropertyChangeListener(this);
                                ((TreeSelectionModel)invoke9).addPropertyChangeListener(this);
                            }
                        }
                        catch (final InvocationTargetException ex85) {
                            System.out.println("Exception: " + ex85.toString());
                        }
                        catch (final IllegalAccessException ex86) {
                            System.out.println("Exception: " + ex86.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex87) {}
                    catch (final SecurityException ex88) {
                        System.out.println("Exception: " + ex88.toString());
                    }
                    break;
                }
                case 28: {
                    if (component instanceof JComponent) {
                        ((JComponent)component).removeVetoableChangeListener(this);
                        ((JComponent)component).addVetoableChangeListener(this);
                        break;
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
        
        @Override
        protected void removeListeners(final Component component) {
            if (SwingEventMonitor.listenerList.getListenerCount(AncestorListener.class) > 0) {
                this.removeListeners(component, 12);
            }
            if (SwingEventMonitor.listenerList.getListenerCount(CaretListener.class) > 0) {
                this.removeListeners(component, 13);
            }
            if (SwingEventMonitor.listenerList.getListenerCount(CellEditorListener.class) > 0) {
                this.removeListeners(component, 14);
            }
            if (SwingEventMonitor.listenerList.getListenerCount(ChangeListener.class) > 0) {
                this.removeListeners(component, 15);
            }
            if (SwingEventMonitor.listenerList.getListenerCount(TableColumnModelListener.class) > 0) {
                this.removeListeners(component, 16);
            }
            if (SwingEventMonitor.listenerList.getListenerCount(DocumentListener.class) > 0) {
                this.removeListeners(component, 17);
            }
            if (SwingEventMonitor.listenerList.getListenerCount(ListDataListener.class) > 0) {
                this.removeListeners(component, 18);
            }
            if (SwingEventMonitor.listenerList.getListenerCount(ListSelectionListener.class) > 0) {
                this.removeListeners(component, 19);
            }
            if (SwingEventMonitor.listenerList.getListenerCount(MenuListener.class) > 0) {
                this.removeListeners(component, 20);
            }
            if (SwingEventMonitor.listenerList.getListenerCount(PopupMenuListener.class) > 0) {
                this.removeListeners(component, 21);
            }
            if (SwingEventMonitor.listenerList.getListenerCount(TableModelListener.class) > 0) {
                this.removeListeners(component, 22);
            }
            if (SwingEventMonitor.listenerList.getListenerCount(TreeExpansionListener.class) > 0) {
                this.removeListeners(component, 23);
            }
            if (SwingEventMonitor.listenerList.getListenerCount(TreeModelListener.class) > 0) {
                this.removeListeners(component, 24);
            }
            if (SwingEventMonitor.listenerList.getListenerCount(TreeSelectionListener.class) > 0) {
                this.removeListeners(component, 25);
            }
            if (SwingEventMonitor.listenerList.getListenerCount(UndoableEditListener.class) > 0) {
                this.removeListeners(component, 26);
            }
            if (SwingEventMonitor.listenerList.getListenerCount(InternalFrameListener.class) > 0) {
                this.removeListeners(component, 29);
            }
            if (SwingEventMonitor.listenerList.getListenerCount(PropertyChangeListener.class) > 0) {
                this.removeListeners(component, 27);
            }
            if (SwingEventMonitor.listenerList.getListenerCount(VetoableChangeListener.class) > 0) {
                this.removeListeners(component, 28);
            }
            super.removeListeners(component);
        }
        
        @Override
        protected void removeListeners(final Component component, final int n) {
            switch (n) {
                case 3: {
                    break;
                }
                case 12: {
                    if (component instanceof JComponent) {
                        ((JComponent)component).removeAncestorListener(this);
                        break;
                    }
                    break;
                }
                case 13: {
                    try {
                        this.removeCaretMethod = component.getClass().getMethod("removeCaretListener", (Class<?>[])this.caretListeners);
                        try {
                            this.removeCaretMethod.invoke(component, this.caretArgs);
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
                case 14: {
                    try {
                        this.getCellEditorMethod = component.getClass().getMethod("getCellEditorMethod", (Class<?>[])this.nullClass);
                        try {
                            final Object invoke = this.getCellEditorMethod.invoke(component, this.nullArgs);
                            if (invoke != null && invoke instanceof CellEditor) {
                                ((CellEditor)invoke).removeCellEditorListener(this);
                            }
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
                    try {
                        this.removeCellEditorMethod = component.getClass().getMethod("removeCellEditorListener", (Class<?>[])this.cellEditorListeners);
                        try {
                            this.removeCellEditorMethod.invoke(component, this.cellEditorArgs);
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
                case 15: {
                    try {
                        this.removeChangeMethod = component.getClass().getMethod("removeChangeListener", (Class<?>[])this.changeListeners);
                        try {
                            this.removeChangeMethod.invoke(component, this.changeArgs);
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
                    try {
                        this.getModelMethod = component.getClass().getMethod("getModel", (Class<?>[])this.nullClass);
                        try {
                            final Object invoke2 = this.getModelMethod.invoke(component, this.nullArgs);
                            if (invoke2 != null) {
                                (this.removeChangeMethod = invoke2.getClass().getMethod("removeChangeListener", (Class<?>[])this.changeListeners)).invoke(invoke2, this.changeArgs);
                            }
                        }
                        catch (final InvocationTargetException ex17) {
                            System.out.println("Exception: " + ex17.toString());
                        }
                        catch (final IllegalAccessException ex18) {
                            System.out.println("Exception: " + ex18.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex19) {}
                    catch (final SecurityException ex20) {
                        System.out.println("Exception: " + ex20.toString());
                    }
                    break;
                }
                case 16: {
                    try {
                        this.getColumnModelMethod = component.getClass().getMethod("getTableColumnModel", (Class<?>[])this.nullClass);
                        try {
                            final Object invoke3 = this.getColumnModelMethod.invoke(component, this.nullArgs);
                            if (invoke3 != null && invoke3 instanceof TableColumnModel) {
                                ((TableColumnModel)invoke3).removeColumnModelListener(this);
                            }
                        }
                        catch (final InvocationTargetException ex21) {
                            System.out.println("Exception: " + ex21.toString());
                        }
                        catch (final IllegalAccessException ex22) {
                            System.out.println("Exception: " + ex22.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex23) {}
                    catch (final SecurityException ex24) {
                        System.out.println("Exception: " + ex24.toString());
                    }
                    break;
                }
                case 17: {
                    try {
                        this.getDocumentMethod = component.getClass().getMethod("getDocument", (Class<?>[])this.nullClass);
                        try {
                            final Object invoke4 = this.getDocumentMethod.invoke(component, this.nullArgs);
                            if (invoke4 != null && invoke4 instanceof Document) {
                                ((Document)invoke4).removeDocumentListener(this);
                            }
                        }
                        catch (final InvocationTargetException ex25) {
                            System.out.println("Exception: " + ex25.toString());
                        }
                        catch (final IllegalAccessException ex26) {
                            System.out.println("Exception: " + ex26.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex27) {}
                    catch (final SecurityException ex28) {
                        System.out.println("Exception: " + ex28.toString());
                    }
                    try {
                        this.removeDocumentMethod = component.getClass().getMethod("removeDocumentListener", (Class<?>[])this.documentListeners);
                        try {
                            this.removeDocumentMethod.invoke(component, this.documentArgs);
                        }
                        catch (final InvocationTargetException ex29) {
                            System.out.println("Exception: " + ex29.toString());
                        }
                        catch (final IllegalAccessException ex30) {
                            System.out.println("Exception: " + ex30.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex31) {}
                    catch (final SecurityException ex32) {
                        System.out.println("Exception: " + ex32.toString());
                    }
                    break;
                }
                case 18:
                case 22:
                case 24: {
                    try {
                        this.getModelMethod = component.getClass().getMethod("getModel", (Class<?>[])this.nullClass);
                        try {
                            final Object invoke5 = this.getModelMethod.invoke(component, this.nullArgs);
                            if (invoke5 != null) {
                                if (n == 18 && invoke5 instanceof ListModel) {
                                    ((ListModel)invoke5).removeListDataListener(this);
                                }
                                else if (n == 22 && invoke5 instanceof TableModel) {
                                    ((TableModel)invoke5).removeTableModelListener(this);
                                }
                                else if (invoke5 instanceof TreeModel) {
                                    ((TreeModel)invoke5).removeTreeModelListener(this);
                                }
                            }
                        }
                        catch (final InvocationTargetException ex33) {
                            System.out.println("Exception: " + ex33.toString());
                        }
                        catch (final IllegalAccessException ex34) {
                            System.out.println("Exception: " + ex34.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex35) {}
                    catch (final SecurityException ex36) {
                        System.out.println("Exception: " + ex36.toString());
                    }
                    break;
                }
                case 19: {
                    try {
                        this.removeListSelectionMethod = component.getClass().getMethod("removeListSelectionListener", (Class<?>[])this.listSelectionListeners);
                        try {
                            this.removeListSelectionMethod.invoke(component, this.listSelectionArgs);
                        }
                        catch (final InvocationTargetException ex37) {
                            System.out.println("Exception: " + ex37.toString());
                        }
                        catch (final IllegalAccessException ex38) {
                            System.out.println("Exception: " + ex38.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex39) {}
                    catch (final SecurityException ex40) {
                        System.out.println("Exception: " + ex40.toString());
                    }
                    try {
                        this.getSelectionModelMethod = component.getClass().getMethod("getSelectionModel", (Class<?>[])this.nullClass);
                        try {
                            final Object invoke6 = this.getSelectionModelMethod.invoke(component, this.nullArgs);
                            if (invoke6 != null && invoke6 instanceof ListSelectionModel) {
                                ((ListSelectionModel)invoke6).removeListSelectionListener(this);
                            }
                        }
                        catch (final InvocationTargetException ex41) {
                            System.out.println("Exception: " + ex41.toString());
                        }
                        catch (final IllegalAccessException ex42) {
                            System.out.println("Exception: " + ex42.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex43) {}
                    catch (final SecurityException ex44) {
                        System.out.println("Exception: " + ex44.toString());
                    }
                    break;
                }
                case 20: {
                    try {
                        this.removeMenuMethod = component.getClass().getMethod("removeMenuListener", (Class<?>[])this.menuListeners);
                        try {
                            this.removeMenuMethod.invoke(component, this.menuArgs);
                        }
                        catch (final InvocationTargetException ex45) {
                            System.out.println("Exception: " + ex45.toString());
                        }
                        catch (final IllegalAccessException ex46) {
                            System.out.println("Exception: " + ex46.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex47) {}
                    catch (final SecurityException ex48) {
                        System.out.println("Exception: " + ex48.toString());
                    }
                    break;
                }
                case 21: {
                    try {
                        this.removePopupMenuMethod = component.getClass().getMethod("removePopupMenuListener", (Class<?>[])this.popupMenuListeners);
                        try {
                            this.removePopupMenuMethod.invoke(component, this.popupMenuArgs);
                        }
                        catch (final InvocationTargetException ex49) {
                            System.out.println("Exception: " + ex49.toString());
                        }
                        catch (final IllegalAccessException ex50) {
                            System.out.println("Exception: " + ex50.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex51) {}
                    catch (final SecurityException ex52) {
                        System.out.println("Exception: " + ex52.toString());
                    }
                    try {
                        this.getPopupMenuMethod = component.getClass().getMethod("getPopupMenu", (Class<?>[])this.nullClass);
                        try {
                            final Object invoke7 = this.getPopupMenuMethod.invoke(component, this.nullArgs);
                            if (invoke7 != null) {
                                (this.removePopupMenuMethod = invoke7.getClass().getMethod("removePopupMenuListener", (Class<?>[])this.popupMenuListeners)).invoke(invoke7, this.popupMenuArgs);
                            }
                        }
                        catch (final InvocationTargetException ex53) {
                            System.out.println("Exception: " + ex53.toString());
                        }
                        catch (final IllegalAccessException ex54) {
                            System.out.println("Exception: " + ex54.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex55) {}
                    catch (final SecurityException ex56) {
                        System.out.println("Exception: " + ex56.toString());
                    }
                    break;
                }
                case 23: {
                    try {
                        this.removeTreeExpansionMethod = component.getClass().getMethod("removeTreeExpansionListener", (Class<?>[])this.treeExpansionListeners);
                        try {
                            this.removeTreeExpansionMethod.invoke(component, this.treeExpansionArgs);
                        }
                        catch (final InvocationTargetException ex57) {
                            System.out.println("Exception: " + ex57.toString());
                        }
                        catch (final IllegalAccessException ex58) {
                            System.out.println("Exception: " + ex58.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex59) {}
                    catch (final SecurityException ex60) {
                        System.out.println("Exception: " + ex60.toString());
                    }
                    break;
                }
                case 25: {
                    try {
                        this.removeTreeSelectionMethod = component.getClass().getMethod("removeTreeSelectionListener", (Class<?>[])this.treeSelectionListeners);
                        try {
                            this.removeTreeSelectionMethod.invoke(component, this.treeSelectionArgs);
                        }
                        catch (final InvocationTargetException ex61) {
                            System.out.println("Exception: " + ex61.toString());
                        }
                        catch (final IllegalAccessException ex62) {
                            System.out.println("Exception: " + ex62.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex63) {}
                    catch (final SecurityException ex64) {
                        System.out.println("Exception: " + ex64.toString());
                    }
                    break;
                }
                case 26: {
                    try {
                        this.getDocumentMethod = component.getClass().getMethod("getDocument", (Class<?>[])this.nullClass);
                        try {
                            final Object invoke8 = this.getDocumentMethod.invoke(component, this.nullArgs);
                            if (invoke8 != null && invoke8 instanceof Document) {
                                ((Document)invoke8).removeUndoableEditListener(this);
                            }
                        }
                        catch (final InvocationTargetException ex65) {
                            System.out.println("Exception: " + ex65.toString());
                        }
                        catch (final IllegalAccessException ex66) {
                            System.out.println("Exception: " + ex66.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex67) {}
                    catch (final SecurityException ex68) {
                        System.out.println("Exception: " + ex68.toString());
                    }
                    try {
                        this.removeUndoableEditMethod = component.getClass().getMethod("removeUndoableEditListener", (Class<?>[])this.undoableEditListeners);
                        try {
                            this.removeUndoableEditMethod.invoke(component, this.undoableEditArgs);
                        }
                        catch (final InvocationTargetException ex69) {
                            System.out.println("Exception: " + ex69.toString());
                        }
                        catch (final IllegalAccessException ex70) {
                            System.out.println("Exception: " + ex70.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex71) {}
                    catch (final SecurityException ex72) {
                        System.out.println("Exception: " + ex72.toString());
                    }
                    break;
                }
                case 29: {
                    try {
                        this.removeInternalFrameMethod = component.getClass().getMethod("removeInternalFrameListener", (Class<?>[])this.internalFrameListeners);
                        try {
                            this.removeInternalFrameMethod.invoke(component, this.internalFrameArgs);
                        }
                        catch (final InvocationTargetException ex73) {
                            System.out.println("Exception: " + ex73.toString());
                        }
                        catch (final IllegalAccessException ex74) {
                            System.out.println("Exception: " + ex74.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex75) {}
                    catch (final SecurityException ex76) {
                        System.out.println("Exception: " + ex76.toString());
                    }
                    break;
                }
                case 27: {
                    try {
                        this.removePropertyChangeMethod = component.getClass().getMethod("removePropertyChangeListener", (Class<?>[])this.propertyChangeListeners);
                        try {
                            this.removePropertyChangeMethod.invoke(component, this.propertyChangeArgs);
                        }
                        catch (final InvocationTargetException ex77) {
                            System.out.println("Exception: " + ex77.toString());
                        }
                        catch (final IllegalAccessException ex78) {
                            System.out.println("Exception: " + ex78.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex79) {}
                    catch (final SecurityException ex80) {
                        System.out.println("Exception: " + ex80.toString());
                    }
                    try {
                        this.getSelectionModelMethod = component.getClass().getMethod("getSelectionModel", (Class<?>[])this.nullClass);
                        try {
                            final Object invoke9 = this.getSelectionModelMethod.invoke(component, this.nullArgs);
                            if (invoke9 != null && invoke9 instanceof TreeSelectionModel) {
                                ((TreeSelectionModel)invoke9).removePropertyChangeListener(this);
                            }
                        }
                        catch (final InvocationTargetException ex81) {
                            System.out.println("Exception: " + ex81.toString());
                        }
                        catch (final IllegalAccessException ex82) {
                            System.out.println("Exception: " + ex82.toString());
                        }
                    }
                    catch (final NoSuchMethodException ex83) {}
                    catch (final SecurityException ex84) {
                        System.out.println("Exception: " + ex84.toString());
                    }
                    break;
                }
                case 28: {
                    if (component instanceof JComponent) {
                        ((JComponent)component).removeVetoableChangeListener(this);
                        break;
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
        public void componentAdded(final ContainerEvent containerEvent) {
            this.installListeners(containerEvent.getChild());
        }
        
        @Override
        public void componentRemoved(final ContainerEvent containerEvent) {
            this.removeListeners(containerEvent.getChild());
        }
        
        @Override
        public void ancestorAdded(final AncestorEvent ancestorEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == AncestorListener.class) {
                    ((AncestorListener)listenerList[i + 1]).ancestorAdded(ancestorEvent);
                }
            }
        }
        
        @Override
        public void ancestorRemoved(final AncestorEvent ancestorEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == AncestorListener.class) {
                    ((AncestorListener)listenerList[i + 1]).ancestorRemoved(ancestorEvent);
                }
            }
        }
        
        @Override
        public void ancestorMoved(final AncestorEvent ancestorEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == AncestorListener.class) {
                    ((AncestorListener)listenerList[i + 1]).ancestorMoved(ancestorEvent);
                }
            }
        }
        
        @Override
        public void caretUpdate(final CaretEvent caretEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == CaretListener.class) {
                    ((CaretListener)listenerList[i + 1]).caretUpdate(caretEvent);
                }
            }
        }
        
        @Override
        public void editingStopped(final ChangeEvent changeEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == CellEditorListener.class) {
                    ((CellEditorListener)listenerList[i + 1]).editingStopped(changeEvent);
                }
            }
        }
        
        @Override
        public void editingCanceled(final ChangeEvent changeEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == CellEditorListener.class) {
                    ((CellEditorListener)listenerList[i + 1]).editingCanceled(changeEvent);
                }
            }
        }
        
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == ChangeListener.class) {
                    ((ChangeListener)listenerList[i + 1]).stateChanged(changeEvent);
                }
            }
        }
        
        @Override
        public void columnAdded(final TableColumnModelEvent tableColumnModelEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == TableColumnModelListener.class) {
                    ((TableColumnModelListener)listenerList[i + 1]).columnAdded(tableColumnModelEvent);
                }
            }
        }
        
        @Override
        public void columnMarginChanged(final ChangeEvent changeEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == TableColumnModelListener.class) {
                    ((TableColumnModelListener)listenerList[i + 1]).columnMarginChanged(changeEvent);
                }
            }
        }
        
        @Override
        public void columnMoved(final TableColumnModelEvent tableColumnModelEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == TableColumnModelListener.class) {
                    ((TableColumnModelListener)listenerList[i + 1]).columnMoved(tableColumnModelEvent);
                }
            }
        }
        
        @Override
        public void columnRemoved(final TableColumnModelEvent tableColumnModelEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == TableColumnModelListener.class) {
                    ((TableColumnModelListener)listenerList[i + 1]).columnRemoved(tableColumnModelEvent);
                }
            }
        }
        
        @Override
        public void columnSelectionChanged(final ListSelectionEvent listSelectionEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == TableColumnModelListener.class) {
                    ((TableColumnModelListener)listenerList[i + 1]).columnSelectionChanged(listSelectionEvent);
                }
            }
        }
        
        @Override
        public void changedUpdate(final DocumentEvent documentEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == DocumentListener.class) {
                    ((DocumentListener)listenerList[i + 1]).changedUpdate(documentEvent);
                }
            }
        }
        
        @Override
        public void insertUpdate(final DocumentEvent documentEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == DocumentListener.class) {
                    ((DocumentListener)listenerList[i + 1]).insertUpdate(documentEvent);
                }
            }
        }
        
        @Override
        public void removeUpdate(final DocumentEvent documentEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == DocumentListener.class) {
                    ((DocumentListener)listenerList[i + 1]).removeUpdate(documentEvent);
                }
            }
        }
        
        @Override
        public void contentsChanged(final ListDataEvent listDataEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == ListDataListener.class) {
                    ((ListDataListener)listenerList[i + 1]).contentsChanged(listDataEvent);
                }
            }
        }
        
        @Override
        public void intervalAdded(final ListDataEvent listDataEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == ListDataListener.class) {
                    ((ListDataListener)listenerList[i + 1]).intervalAdded(listDataEvent);
                }
            }
        }
        
        @Override
        public void intervalRemoved(final ListDataEvent listDataEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == ListDataListener.class) {
                    ((ListDataListener)listenerList[i + 1]).intervalRemoved(listDataEvent);
                }
            }
        }
        
        @Override
        public void valueChanged(final ListSelectionEvent listSelectionEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == ListSelectionListener.class) {
                    ((ListSelectionListener)listenerList[i + 1]).valueChanged(listSelectionEvent);
                }
            }
        }
        
        @Override
        public void menuCanceled(final MenuEvent menuEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == MenuListener.class) {
                    ((MenuListener)listenerList[i + 1]).menuCanceled(menuEvent);
                }
            }
        }
        
        @Override
        public void menuDeselected(final MenuEvent menuEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == MenuListener.class) {
                    ((MenuListener)listenerList[i + 1]).menuDeselected(menuEvent);
                }
            }
        }
        
        @Override
        public void menuSelected(final MenuEvent menuEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == MenuListener.class) {
                    ((MenuListener)listenerList[i + 1]).menuSelected(menuEvent);
                }
            }
        }
        
        @Override
        public void popupMenuWillBecomeVisible(final PopupMenuEvent popupMenuEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == PopupMenuListener.class) {
                    ((PopupMenuListener)listenerList[i + 1]).popupMenuWillBecomeVisible(popupMenuEvent);
                }
            }
        }
        
        @Override
        public void popupMenuWillBecomeInvisible(final PopupMenuEvent popupMenuEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == PopupMenuListener.class) {
                    ((PopupMenuListener)listenerList[i + 1]).popupMenuWillBecomeInvisible(popupMenuEvent);
                }
            }
        }
        
        @Override
        public void popupMenuCanceled(final PopupMenuEvent popupMenuEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == PopupMenuListener.class) {
                    ((PopupMenuListener)listenerList[i + 1]).popupMenuCanceled(popupMenuEvent);
                }
            }
        }
        
        @Override
        public void tableChanged(final TableModelEvent tableModelEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == TableModelListener.class) {
                    ((TableModelListener)listenerList[i + 1]).tableChanged(tableModelEvent);
                }
            }
        }
        
        @Override
        public void treeCollapsed(final TreeExpansionEvent treeExpansionEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == TreeExpansionListener.class) {
                    ((TreeExpansionListener)listenerList[i + 1]).treeCollapsed(treeExpansionEvent);
                }
            }
        }
        
        @Override
        public void treeExpanded(final TreeExpansionEvent treeExpansionEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == TreeExpansionListener.class) {
                    ((TreeExpansionListener)listenerList[i + 1]).treeExpanded(treeExpansionEvent);
                }
            }
        }
        
        @Override
        public void treeNodesChanged(final TreeModelEvent treeModelEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == TreeModelListener.class) {
                    ((TreeModelListener)listenerList[i + 1]).treeNodesChanged(treeModelEvent);
                }
            }
        }
        
        @Override
        public void treeNodesInserted(final TreeModelEvent treeModelEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == TreeModelListener.class) {
                    ((TreeModelListener)listenerList[i + 1]).treeNodesInserted(treeModelEvent);
                }
            }
        }
        
        @Override
        public void treeNodesRemoved(final TreeModelEvent treeModelEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == TreeModelListener.class) {
                    ((TreeModelListener)listenerList[i + 1]).treeNodesRemoved(treeModelEvent);
                }
            }
        }
        
        @Override
        public void treeStructureChanged(final TreeModelEvent treeModelEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == TreeModelListener.class) {
                    ((TreeModelListener)listenerList[i + 1]).treeStructureChanged(treeModelEvent);
                }
            }
        }
        
        @Override
        public void valueChanged(final TreeSelectionEvent treeSelectionEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == TreeSelectionListener.class) {
                    ((TreeSelectionListener)listenerList[i + 1]).valueChanged(treeSelectionEvent);
                }
            }
        }
        
        @Override
        public void undoableEditHappened(final UndoableEditEvent undoableEditEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == UndoableEditListener.class) {
                    ((UndoableEditListener)listenerList[i + 1]).undoableEditHappened(undoableEditEvent);
                }
            }
        }
        
        @Override
        public void internalFrameOpened(final InternalFrameEvent internalFrameEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == InternalFrameListener.class) {
                    ((InternalFrameListener)listenerList[i + 1]).internalFrameOpened(internalFrameEvent);
                }
            }
        }
        
        @Override
        public void internalFrameActivated(final InternalFrameEvent internalFrameEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == InternalFrameListener.class) {
                    ((InternalFrameListener)listenerList[i + 1]).internalFrameActivated(internalFrameEvent);
                }
            }
        }
        
        @Override
        public void internalFrameDeactivated(final InternalFrameEvent internalFrameEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == InternalFrameListener.class) {
                    ((InternalFrameListener)listenerList[i + 1]).internalFrameDeactivated(internalFrameEvent);
                }
            }
        }
        
        @Override
        public void internalFrameIconified(final InternalFrameEvent internalFrameEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == InternalFrameListener.class) {
                    ((InternalFrameListener)listenerList[i + 1]).internalFrameIconified(internalFrameEvent);
                }
            }
        }
        
        @Override
        public void internalFrameDeiconified(final InternalFrameEvent internalFrameEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == InternalFrameListener.class) {
                    ((InternalFrameListener)listenerList[i + 1]).internalFrameDeiconified(internalFrameEvent);
                }
            }
        }
        
        @Override
        public void internalFrameClosing(final InternalFrameEvent internalFrameEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == InternalFrameListener.class) {
                    ((InternalFrameListener)listenerList[i + 1]).internalFrameClosing(internalFrameEvent);
                }
            }
        }
        
        @Override
        public void internalFrameClosed(final InternalFrameEvent internalFrameEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == InternalFrameListener.class) {
                    ((InternalFrameListener)listenerList[i + 1]).internalFrameClosed(internalFrameEvent);
                }
            }
        }
        
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == PropertyChangeListener.class) {
                    ((PropertyChangeListener)listenerList[i + 1]).propertyChange(propertyChangeEvent);
                }
            }
            if (propertyChangeEvent.getSource() instanceof JTextComponent) {
                final Document document = ((JTextComponent)propertyChangeEvent.getSource()).getDocument();
                if (document == null) {
                    return;
                }
                try {
                    this.removeDocumentMethod = document.getClass().getMethod("removeDocumentListener", (Class<?>[])this.documentListeners);
                    this.addDocumentMethod = document.getClass().getMethod("addDocumentListener", (Class<?>[])this.documentListeners);
                    try {
                        this.removeDocumentMethod.invoke(document, this.documentArgs);
                        this.addDocumentMethod.invoke(document, this.documentArgs);
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
            }
        }
        
        @Override
        public void vetoableChange(final PropertyChangeEvent propertyChangeEvent) throws PropertyVetoException {
            final Object[] listenerList = SwingEventMonitor.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == VetoableChangeListener.class) {
                    ((VetoableChangeListener)listenerList[i + 1]).vetoableChange(propertyChangeEvent);
                }
            }
        }
    }
}
