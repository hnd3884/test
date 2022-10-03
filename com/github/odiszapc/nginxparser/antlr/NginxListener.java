package com.github.odiszapc.nginxparser.antlr;

import org.antlr.v4.runtime.tree.ParseTreeListener;

public interface NginxListener extends ParseTreeListener
{
    void enterConfig(final NginxParser.ConfigContext p0);
    
    void exitConfig(final NginxParser.ConfigContext p0);
    
    void enterStatement(final NginxParser.StatementContext p0);
    
    void exitStatement(final NginxParser.StatementContext p0);
    
    void enterGenericStatement(final NginxParser.GenericStatementContext p0);
    
    void exitGenericStatement(final NginxParser.GenericStatementContext p0);
    
    void enterRegexHeaderStatement(final NginxParser.RegexHeaderStatementContext p0);
    
    void exitRegexHeaderStatement(final NginxParser.RegexHeaderStatementContext p0);
    
    void enterBlock(final NginxParser.BlockContext p0);
    
    void exitBlock(final NginxParser.BlockContext p0);
    
    void enterGenericBlockHeader(final NginxParser.GenericBlockHeaderContext p0);
    
    void exitGenericBlockHeader(final NginxParser.GenericBlockHeaderContext p0);
    
    void enterIf_statement(final NginxParser.If_statementContext p0);
    
    void exitIf_statement(final NginxParser.If_statementContext p0);
    
    void enterIf_body(final NginxParser.If_bodyContext p0);
    
    void exitIf_body(final NginxParser.If_bodyContext p0);
    
    void enterRegexp(final NginxParser.RegexpContext p0);
    
    void exitRegexp(final NginxParser.RegexpContext p0);
    
    void enterLocationBlockHeader(final NginxParser.LocationBlockHeaderContext p0);
    
    void exitLocationBlockHeader(final NginxParser.LocationBlockHeaderContext p0);
    
    void enterRewriteStatement(final NginxParser.RewriteStatementContext p0);
    
    void exitRewriteStatement(final NginxParser.RewriteStatementContext p0);
}
