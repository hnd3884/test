package org.apache.lucene.queryparser.flexible.standard.nodes;

import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import java.util.List;
import org.apache.lucene.queryparser.flexible.core.nodes.BooleanQueryNode;

public class StandardBooleanQueryNode extends BooleanQueryNode
{
    private boolean disableCoord;
    
    public StandardBooleanQueryNode(final List<QueryNode> clauses, final boolean disableCoord) {
        super(clauses);
        this.disableCoord = disableCoord;
    }
    
    public boolean isDisableCoord() {
        return this.disableCoord;
    }
}
