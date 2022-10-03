package com.adventnet.beans.criteriatable;

import com.adventnet.beans.criteriatable.events.AttributeModelListener;

public interface AttributeModel
{
    int getAttributeCount();
    
    Attribute getAttribute(final int p0);
    
    Attribute getAttributeByName(final String p0);
    
    Attribute getAttributeByValue(final Object p0);
    
    int getOperatorsCount();
    
    String getOperator(final int p0);
    
    int getGroupingElementsCount();
    
    String getGroupingElement(final int p0);
    
    void addAttributeModelListener(final AttributeModelListener p0);
    
    void removeAttributeModelListener(final AttributeModelListener p0);
}
