package com.github.odiszapc.nginxparser.antlr;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

public interface NginxVisitor<T> extends ParseTreeVisitor<T>
{
    T visitConfig(final NginxParser.ConfigContext p0);
    
    T visitStatement(final NginxParser.StatementContext p0);
    
    T visitGenericStatement(final NginxParser.GenericStatementContext p0);
    
    T visitRegexHeaderStatement(final NginxParser.RegexHeaderStatementContext p0);
    
    T visitBlock(final NginxParser.BlockContext p0);
    
    T visitGenericBlockHeader(final NginxParser.GenericBlockHeaderContext p0);
    
    T visitIf_statement(final NginxParser.If_statementContext p0);
    
    T visitIf_body(final NginxParser.If_bodyContext p0);
    
    T visitRegexp(final NginxParser.RegexpContext p0);
    
    T visitLocationBlockHeader(final NginxParser.LocationBlockHeaderContext p0);
    
    T visitRewriteStatement(final NginxParser.RewriteStatementContext p0);
}
