package org.apache.lucene.queryparser.flexible.core.parser;

import org.apache.lucene.queryparser.flexible.core.QueryNodeParseException;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;

public interface SyntaxParser
{
    QueryNode parse(final CharSequence p0, final CharSequence p1) throws QueryNodeParseException;
}
