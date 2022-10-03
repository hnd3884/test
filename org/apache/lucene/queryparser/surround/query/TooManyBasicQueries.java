package org.apache.lucene.queryparser.surround.query;

import java.io.IOException;

public class TooManyBasicQueries extends IOException
{
    public TooManyBasicQueries(final int maxBasicQueries) {
        super("Exceeded maximum of " + maxBasicQueries + " basic queries.");
    }
}
