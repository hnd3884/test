package com.adventnet.beans.criteriatable;

import java.awt.Component;
import javax.swing.JTable;
import java.awt.event.ActionListener;

public interface AttributeCellEditor
{
    void addActionListener(final ActionListener p0);
    
    void removeActionListener(final ActionListener p0);
    
    Component getAttributeCellEditorComponent(final JTable p0, final Object p1, final boolean p2, final int p3, final int p4, final Attribute[] p5);
    
    Attribute getCellEditorValue();
}
