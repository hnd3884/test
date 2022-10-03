package javax.swing;

import java.awt.event.ItemEvent;
import java.io.Serializable;
import java.awt.event.ItemListener;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.beans.ConstructorProperties;
import java.awt.event.ActionListener;
import javax.swing.tree.TreeCellEditor;
import javax.swing.table.TableCellEditor;

public class DefaultCellEditor extends AbstractCellEditor implements TableCellEditor, TreeCellEditor
{
    protected JComponent editorComponent;
    protected EditorDelegate delegate;
    protected int clickCountToStart;
    
    @ConstructorProperties({ "component" })
    public DefaultCellEditor(final JTextField editorComponent) {
        this.clickCountToStart = 1;
        this.editorComponent = editorComponent;
        this.clickCountToStart = 2;
        editorComponent.addActionListener(this.delegate = new EditorDelegate() {
            @Override
            public void setValue(final Object o) {
                editorComponent.setText((o != null) ? o.toString() : "");
            }
            
            @Override
            public Object getCellEditorValue() {
                return editorComponent.getText();
            }
        });
    }
    
    public DefaultCellEditor(final JCheckBox editorComponent) {
        this.clickCountToStart = 1;
        ((AbstractButton)(this.editorComponent = editorComponent)).addActionListener(this.delegate = new EditorDelegate() {
            @Override
            public void setValue(final Object o) {
                boolean selected = false;
                if (o instanceof Boolean) {
                    selected = (boolean)o;
                }
                else if (o instanceof String) {
                    selected = o.equals("true");
                }
                editorComponent.setSelected(selected);
            }
            
            @Override
            public Object getCellEditorValue() {
                return editorComponent.isSelected();
            }
        });
        editorComponent.setRequestFocusEnabled(false);
    }
    
    public DefaultCellEditor(final JComboBox editorComponent) {
        this.clickCountToStart = 1;
        (this.editorComponent = editorComponent).putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
        editorComponent.addActionListener(this.delegate = new EditorDelegate() {
            @Override
            public void setValue(final Object selectedItem) {
                editorComponent.setSelectedItem(selectedItem);
            }
            
            @Override
            public Object getCellEditorValue() {
                return editorComponent.getSelectedItem();
            }
            
            @Override
            public boolean shouldSelectCell(final EventObject eventObject) {
                return !(eventObject instanceof MouseEvent) || ((MouseEvent)eventObject).getID() != 506;
            }
            
            @Override
            public boolean stopCellEditing() {
                if (editorComponent.isEditable()) {
                    editorComponent.actionPerformed(new ActionEvent(DefaultCellEditor.this, 0, ""));
                }
                return super.stopCellEditing();
            }
        });
    }
    
    public Component getComponent() {
        return this.editorComponent;
    }
    
    public void setClickCountToStart(final int clickCountToStart) {
        this.clickCountToStart = clickCountToStart;
    }
    
    public int getClickCountToStart() {
        return this.clickCountToStart;
    }
    
    @Override
    public Object getCellEditorValue() {
        return this.delegate.getCellEditorValue();
    }
    
    @Override
    public boolean isCellEditable(final EventObject eventObject) {
        return this.delegate.isCellEditable(eventObject);
    }
    
    @Override
    public boolean shouldSelectCell(final EventObject eventObject) {
        return this.delegate.shouldSelectCell(eventObject);
    }
    
    @Override
    public boolean stopCellEditing() {
        return this.delegate.stopCellEditing();
    }
    
    @Override
    public void cancelCellEditing() {
        this.delegate.cancelCellEditing();
    }
    
    @Override
    public Component getTreeCellEditorComponent(final JTree tree, final Object o, final boolean b, final boolean b2, final boolean b3, final int n) {
        this.delegate.setValue(tree.convertValueToText(o, b, b2, b3, n, false));
        return this.editorComponent;
    }
    
    @Override
    public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean b, final int n, final int n2) {
        this.delegate.setValue(value);
        if (this.editorComponent instanceof JCheckBox) {
            final Component tableCellRendererComponent = table.getCellRenderer(n, n2).getTableCellRendererComponent(table, value, b, true, n, n2);
            if (tableCellRendererComponent != null) {
                this.editorComponent.setOpaque(true);
                this.editorComponent.setBackground(tableCellRendererComponent.getBackground());
                if (tableCellRendererComponent instanceof JComponent) {
                    this.editorComponent.setBorder(((JComponent)tableCellRendererComponent).getBorder());
                }
            }
            else {
                this.editorComponent.setOpaque(false);
            }
        }
        return this.editorComponent;
    }
    
    protected class EditorDelegate implements ActionListener, ItemListener, Serializable
    {
        protected Object value;
        
        public Object getCellEditorValue() {
            return this.value;
        }
        
        public void setValue(final Object value) {
            this.value = value;
        }
        
        public boolean isCellEditable(final EventObject eventObject) {
            return !(eventObject instanceof MouseEvent) || ((MouseEvent)eventObject).getClickCount() >= DefaultCellEditor.this.clickCountToStart;
        }
        
        public boolean shouldSelectCell(final EventObject eventObject) {
            return true;
        }
        
        public boolean startCellEditing(final EventObject eventObject) {
            return true;
        }
        
        public boolean stopCellEditing() {
            DefaultCellEditor.this.fireEditingStopped();
            return true;
        }
        
        public void cancelCellEditing() {
            DefaultCellEditor.this.fireEditingCanceled();
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            DefaultCellEditor.this.stopCellEditing();
        }
        
        @Override
        public void itemStateChanged(final ItemEvent itemEvent) {
            DefaultCellEditor.this.stopCellEditing();
        }
    }
}
