package com.adventnet.beans.criteriatable;

import java.awt.event.ActionListener;

public interface AttributeValueEditorComponent
{
    void setAttributeValue(final Object p0);
    
    Object getAttributeValue();
    
    void initializeComponent();
    
    void addActionListener(final ActionListener p0);
    
    void removeActionListener(final ActionListener p0);
}
