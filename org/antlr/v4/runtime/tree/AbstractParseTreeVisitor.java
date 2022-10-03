package org.antlr.v4.runtime.tree;

public abstract class AbstractParseTreeVisitor<T> implements ParseTreeVisitor<T>
{
    @Override
    public T visit(final ParseTree tree) {
        return tree.accept((ParseTreeVisitor<? extends T>)this);
    }
    
    @Override
    public T visitChildren(final RuleNode node) {
        T result = this.defaultResult();
        T childResult;
        for (int n = node.getChildCount(), i = 0; i < n && this.shouldVisitNextChild(node, result); result = this.aggregateResult(result, childResult), ++i) {
            final ParseTree c = node.getChild(i);
            childResult = c.accept((ParseTreeVisitor<? extends T>)this);
        }
        return result;
    }
    
    @Override
    public T visitTerminal(final TerminalNode node) {
        return this.defaultResult();
    }
    
    @Override
    public T visitErrorNode(final ErrorNode node) {
        return this.defaultResult();
    }
    
    protected T defaultResult() {
        return null;
    }
    
    protected T aggregateResult(final T aggregate, final T nextResult) {
        return nextResult;
    }
    
    protected boolean shouldVisitNextChild(final RuleNode node, final T currentResult) {
        return true;
    }
}
