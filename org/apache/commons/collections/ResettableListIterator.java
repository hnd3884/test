package org.apache.commons.collections;

import java.util.ListIterator;

public interface ResettableListIterator extends ListIterator, ResettableIterator
{
    void reset();
}
