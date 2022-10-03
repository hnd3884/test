package org.apache.xerces.xs;

import org.w3c.dom.ls.LSInput;
import java.util.List;

public interface LSInputList extends List
{
    int getLength();
    
    LSInput item(final int p0);
}
