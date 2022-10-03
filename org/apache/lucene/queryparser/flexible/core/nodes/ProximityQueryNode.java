package org.apache.lucene.queryparser.flexible.core.nodes;

import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;
import java.util.Iterator;
import org.apache.lucene.queryparser.flexible.messages.Message;
import org.apache.lucene.queryparser.flexible.core.QueryNodeError;
import org.apache.lucene.queryparser.flexible.messages.MessageImpl;
import org.apache.lucene.queryparser.flexible.core.messages.QueryParserMessages;
import java.util.List;

public class ProximityQueryNode extends BooleanQueryNode
{
    private Type proximityType;
    private int distance;
    private boolean inorder;
    private CharSequence field;
    
    public ProximityQueryNode(final List<QueryNode> clauses, final CharSequence field, final Type type, final int distance, final boolean inorder) {
        super(clauses);
        this.proximityType = Type.SENTENCE;
        this.distance = -1;
        this.inorder = false;
        this.field = null;
        this.setLeaf(false);
        this.proximityType = type;
        this.inorder = inorder;
        this.field = field;
        if (type == Type.NUMBER) {
            if (distance <= 0) {
                throw new QueryNodeError(new MessageImpl(QueryParserMessages.PARAMETER_VALUE_NOT_SUPPORTED, new Object[] { "distance", distance }));
            }
            this.distance = distance;
        }
        clearFields(clauses, field);
    }
    
    public ProximityQueryNode(final List<QueryNode> clauses, final CharSequence field, final Type type, final boolean inorder) {
        this(clauses, field, type, -1, inorder);
    }
    
    private static void clearFields(final List<QueryNode> nodes, final CharSequence field) {
        if (nodes == null || nodes.size() == 0) {
            return;
        }
        for (final QueryNode clause : nodes) {
            if (clause instanceof FieldQueryNode) {
                ((FieldQueryNode)clause).toQueryStringIgnoreFields = true;
                ((FieldQueryNode)clause).setField(field);
            }
        }
    }
    
    public Type getProximityType() {
        return this.proximityType;
    }
    
    @Override
    public String toString() {
        final String distanceSTR = (this.distance == -1) ? "" : (" distance='" + this.distance + "'");
        if (this.getChildren() == null || this.getChildren().size() == 0) {
            return "<proximity field='" + (Object)this.field + "' inorder='" + this.inorder + "' type='" + this.proximityType.toString() + "'" + distanceSTR + "/>";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("<proximity field='" + (Object)this.field + "' inorder='" + this.inorder + "' type='" + this.proximityType.toString() + "'" + distanceSTR + ">");
        for (final QueryNode child : this.getChildren()) {
            sb.append("\n");
            sb.append(child.toString());
        }
        sb.append("\n</proximity>");
        return sb.toString();
    }
    
    @Override
    public CharSequence toQueryString(final EscapeQuerySyntax escapeSyntaxParser) {
        final String withinSTR = (Object)this.proximityType.toQueryString() + ((this.distance == -1) ? "" : (" " + this.distance)) + (this.inorder ? " INORDER" : "");
        final StringBuilder sb = new StringBuilder();
        if (this.getChildren() != null) {
            if (this.getChildren().size() != 0) {
                String filler = "";
                for (final QueryNode child : this.getChildren()) {
                    sb.append(filler).append(child.toQueryString(escapeSyntaxParser));
                    filler = " ";
                }
            }
        }
        if (this.isDefaultField(this.field)) {
            return "( " + sb.toString() + " ) " + withinSTR;
        }
        return (Object)this.field + ":(( " + sb.toString() + " ) " + withinSTR + ")";
    }
    
    @Override
    public QueryNode cloneTree() throws CloneNotSupportedException {
        final ProximityQueryNode clone = (ProximityQueryNode)super.cloneTree();
        clone.proximityType = this.proximityType;
        clone.distance = this.distance;
        clone.field = this.field;
        return clone;
    }
    
    public int getDistance() {
        return this.distance;
    }
    
    public CharSequence getField() {
        return this.field;
    }
    
    public String getFieldAsString() {
        if (this.field == null) {
            return null;
        }
        return this.field.toString();
    }
    
    public void setField(final CharSequence field) {
        this.field = field;
    }
    
    public boolean isInOrder() {
        return this.inorder;
    }
    
    public enum Type
    {
        PARAGRAPH {
            @Override
            CharSequence toQueryString() {
                return "WITHIN PARAGRAPH";
            }
        }, 
        SENTENCE {
            @Override
            CharSequence toQueryString() {
                return "WITHIN SENTENCE";
            }
        }, 
        NUMBER {
            @Override
            CharSequence toQueryString() {
                return "WITHIN";
            }
        };
        
        abstract CharSequence toQueryString();
    }
    
    public static class ProximityType
    {
        int pDistance;
        Type pType;
        
        public ProximityType(final Type type) {
            this(type, 0);
        }
        
        public ProximityType(final Type type, final int distance) {
            this.pDistance = 0;
            this.pType = null;
            this.pType = type;
            this.pDistance = distance;
        }
    }
}
