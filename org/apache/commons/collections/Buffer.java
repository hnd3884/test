package org.apache.commons.collections;

import java.util.Collection;

public interface Buffer extends Collection
{
    Object remove();
    
    Object get();
}
