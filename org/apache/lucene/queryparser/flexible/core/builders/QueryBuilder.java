package org.apache.lucene.queryparser.flexible.core.builders;

import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;

public interface QueryBuilder
{
    Object build(final QueryNode p0) throws QueryNodeException;
}
