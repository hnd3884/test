package org.apache.lucene.queryparser.ext;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;

public abstract class ParserExtension
{
    public abstract Query parse(final ExtensionQuery p0) throws ParseException;
}
