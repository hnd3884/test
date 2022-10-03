package javax.swing.tree;

import java.awt.Component;
import javax.swing.JTree;
import javax.swing.CellEditor;

public interface TreeCellEditor extends CellEditor
{
    Component getTreeCellEditorComponent(final JTree p0, final Object p1, final boolean p2, final boolean p3, final boolean p4, final int p5);
}
