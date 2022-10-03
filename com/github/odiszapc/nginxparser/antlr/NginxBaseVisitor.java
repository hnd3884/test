package com.github.odiszapc.nginxparser.antlr;

import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

public class NginxBaseVisitor<T> extends AbstractParseTreeVisitor<T> implements NginxVisitor<T>
{
    public T visitConfig(final NginxParser.ConfigContext configContext) {
        return (T)this.visitChildren((RuleNode)configContext);
    }
    
    public T visitStatement(final NginxParser.StatementContext statementContext) {
        return (T)this.visitChildren((RuleNode)statementContext);
    }
    
    public T visitGenericStatement(final NginxParser.GenericStatementContext genericStatementContext) {
        return (T)this.visitChildren((RuleNode)genericStatementContext);
    }
    
    public T visitRegexHeaderStatement(final NginxParser.RegexHeaderStatementContext regexHeaderStatementContext) {
        return (T)this.visitChildren((RuleNode)regexHeaderStatementContext);
    }
    
    public T visitBlock(final NginxParser.BlockContext blockContext) {
        return (T)this.visitChildren((RuleNode)blockContext);
    }
    
    public T visitGenericBlockHeader(final NginxParser.GenericBlockHeaderContext genericBlockHeaderContext) {
        return (T)this.visitChildren((RuleNode)genericBlockHeaderContext);
    }
    
    public T visitIf_statement(final NginxParser.If_statementContext if_statementContext) {
        return (T)this.visitChildren((RuleNode)if_statementContext);
    }
    
    public T visitIf_body(final NginxParser.If_bodyContext if_bodyContext) {
        return (T)this.visitChildren((RuleNode)if_bodyContext);
    }
    
    public T visitRegexp(final NginxParser.RegexpContext regexpContext) {
        return (T)this.visitChildren((RuleNode)regexpContext);
    }
    
    public T visitLocationBlockHeader(final NginxParser.LocationBlockHeaderContext locationBlockHeaderContext) {
        return (T)this.visitChildren((RuleNode)locationBlockHeaderContext);
    }
    
    public T visitRewriteStatement(final NginxParser.RewriteStatementContext rewriteStatementContext) {
        return (T)this.visitChildren((RuleNode)rewriteStatementContext);
    }
}
