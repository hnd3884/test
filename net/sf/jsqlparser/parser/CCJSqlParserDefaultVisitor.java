package net.sf.jsqlparser.parser;

public class CCJSqlParserDefaultVisitor implements CCJSqlParserVisitor
{
    public Object defaultVisit(final SimpleNode node, final Object data) {
        node.childrenAccept(this, data);
        return data;
    }
    
    @Override
    public Object visit(final SimpleNode node, final Object data) {
        return this.defaultVisit(node, data);
    }
}
