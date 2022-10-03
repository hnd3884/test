package org.apache.lucene.index;

import java.io.FileNotFoundException;

public final class IndexNotFoundException extends FileNotFoundException
{
    public IndexNotFoundException(final String msg) {
        super(msg);
    }
}
