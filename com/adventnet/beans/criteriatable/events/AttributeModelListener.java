package com.adventnet.beans.criteriatable.events;

import java.util.EventListener;

public interface AttributeModelListener extends EventListener
{
    void attributeModelChanged(final AttributeModelEvent p0);
}
