package com.adventnet.beans.criteriatable.events;

import com.adventnet.beans.criteriatable.AttributeModel;
import com.adventnet.beans.criteriatable.Attribute;
import java.util.EventObject;

public class AttributeModelEvent extends EventObject
{
    public static final int ADD = 0;
    public static final int DELETE = 1;
    private int type;
    private Attribute attr;
    
    public AttributeModelEvent(final AttributeModel attributeModel, final Attribute attr, final int type) {
        super(attributeModel);
        this.type = type;
        this.attr = attr;
    }
    
    public int getType() {
        return this.type;
    }
    
    public Attribute getAttribute() {
        return this.attr;
    }
}
