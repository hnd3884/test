package net.sf.jsqlparser.parser;

public class ASTNodeAccessImpl implements ASTNodeAccess
{
    private SimpleNode node;
    
    @Override
    public SimpleNode getASTNode() {
        return this.node;
    }
    
    @Override
    public void setASTNode(final SimpleNode node) {
        this.node = node;
    }
}
