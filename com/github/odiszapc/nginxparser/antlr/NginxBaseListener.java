package com.github.odiszapc.nginxparser.antlr;

import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.ParserRuleContext;

public class NginxBaseListener implements NginxListener
{
    @Override
    public void enterConfig(final NginxParser.ConfigContext configContext) {
    }
    
    @Override
    public void exitConfig(final NginxParser.ConfigContext configContext) {
    }
    
    @Override
    public void enterStatement(final NginxParser.StatementContext statementContext) {
    }
    
    @Override
    public void exitStatement(final NginxParser.StatementContext statementContext) {
    }
    
    @Override
    public void enterGenericStatement(final NginxParser.GenericStatementContext genericStatementContext) {
    }
    
    @Override
    public void exitGenericStatement(final NginxParser.GenericStatementContext genericStatementContext) {
    }
    
    @Override
    public void enterRegexHeaderStatement(final NginxParser.RegexHeaderStatementContext regexHeaderStatementContext) {
    }
    
    @Override
    public void exitRegexHeaderStatement(final NginxParser.RegexHeaderStatementContext regexHeaderStatementContext) {
    }
    
    @Override
    public void enterBlock(final NginxParser.BlockContext blockContext) {
    }
    
    @Override
    public void exitBlock(final NginxParser.BlockContext blockContext) {
    }
    
    @Override
    public void enterGenericBlockHeader(final NginxParser.GenericBlockHeaderContext genericBlockHeaderContext) {
    }
    
    @Override
    public void exitGenericBlockHeader(final NginxParser.GenericBlockHeaderContext genericBlockHeaderContext) {
    }
    
    @Override
    public void enterIf_statement(final NginxParser.If_statementContext if_statementContext) {
    }
    
    @Override
    public void exitIf_statement(final NginxParser.If_statementContext if_statementContext) {
    }
    
    @Override
    public void enterIf_body(final NginxParser.If_bodyContext if_bodyContext) {
    }
    
    @Override
    public void exitIf_body(final NginxParser.If_bodyContext if_bodyContext) {
    }
    
    @Override
    public void enterRegexp(final NginxParser.RegexpContext regexpContext) {
    }
    
    @Override
    public void exitRegexp(final NginxParser.RegexpContext regexpContext) {
    }
    
    @Override
    public void enterLocationBlockHeader(final NginxParser.LocationBlockHeaderContext locationBlockHeaderContext) {
    }
    
    @Override
    public void exitLocationBlockHeader(final NginxParser.LocationBlockHeaderContext locationBlockHeaderContext) {
    }
    
    @Override
    public void enterRewriteStatement(final NginxParser.RewriteStatementContext rewriteStatementContext) {
    }
    
    @Override
    public void exitRewriteStatement(final NginxParser.RewriteStatementContext rewriteStatementContext) {
    }
    
    public void enterEveryRule(final ParserRuleContext parserRuleContext) {
    }
    
    public void exitEveryRule(final ParserRuleContext parserRuleContext) {
    }
    
    public void visitTerminal(final TerminalNode terminalNode) {
    }
    
    public void visitErrorNode(final ErrorNode errorNode) {
    }
}
