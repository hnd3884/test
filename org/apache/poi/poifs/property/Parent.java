package org.apache.poi.poifs.property;

import java.io.IOException;
import java.util.Iterator;

public interface Parent extends Child, Iterable<Property>
{
    Iterator<Property> getChildren();
    
    void addChild(final Property p0) throws IOException;
    
    void setPreviousChild(final Child p0);
    
    void setNextChild(final Child p0);
}
