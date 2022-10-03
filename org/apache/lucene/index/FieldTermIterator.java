package org.apache.lucene.index;

import org.apache.lucene.util.BytesRefIterator;

abstract class FieldTermIterator implements BytesRefIterator
{
    abstract String field();
    
    abstract long delGen();
}
