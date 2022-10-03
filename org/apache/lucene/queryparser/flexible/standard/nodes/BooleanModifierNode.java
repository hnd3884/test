package org.apache.lucene.queryparser.flexible.standard.nodes;

import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.ModifierQueryNode;

public class BooleanModifierNode extends ModifierQueryNode
{
    public BooleanModifierNode(final QueryNode node, final Modifier mod) {
        super(node, mod);
    }
}
