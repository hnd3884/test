package net.sf.jsqlparser.parser;

import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.expression.Expression;
import java.io.InputStream;
import java.io.StringReader;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.statement.Statement;
import java.io.Reader;

public final class CCJSqlParserUtil
{
    private CCJSqlParserUtil() {
    }
    
    public static Statement parse(final Reader statementReader) throws JSQLParserException {
        final CCJSqlParser parser = new CCJSqlParser(statementReader);
        try {
            return parser.Statement();
        }
        catch (final Exception ex) {
            throw new JSQLParserException(ex);
        }
    }
    
    public static Statement parse(final String sql) throws JSQLParserException {
        final CCJSqlParser parser = new CCJSqlParser(new StringReader(sql));
        try {
            return parser.Statement();
        }
        catch (final Exception ex) {
            throw new JSQLParserException(ex);
        }
    }
    
    public static Node parseAST(final String sql) throws JSQLParserException {
        final CCJSqlParser parser = new CCJSqlParser(new StringReader(sql));
        try {
            parser.Statement();
            return parser.jjtree.rootNode();
        }
        catch (final Exception ex) {
            throw new JSQLParserException(ex);
        }
    }
    
    public static Statement parse(final InputStream is) throws JSQLParserException {
        final CCJSqlParser parser = new CCJSqlParser(is);
        try {
            return parser.Statement();
        }
        catch (final Exception ex) {
            throw new JSQLParserException(ex);
        }
    }
    
    public static Statement parse(final InputStream is, final String encoding) throws JSQLParserException {
        final CCJSqlParser parser = new CCJSqlParser(is, encoding);
        try {
            return parser.Statement();
        }
        catch (final Exception ex) {
            throw new JSQLParserException(ex);
        }
    }
    
    public static Expression parseExpression(final String expression) throws JSQLParserException {
        final CCJSqlParser parser = new CCJSqlParser(new StringReader(expression));
        try {
            return parser.SimpleExpression();
        }
        catch (final Exception ex) {
            throw new JSQLParserException(ex);
        }
    }
    
    public static Expression parseCondExpression(final String condExpr) throws JSQLParserException {
        final CCJSqlParser parser = new CCJSqlParser(new StringReader(condExpr));
        try {
            return parser.Expression();
        }
        catch (final Exception ex) {
            throw new JSQLParserException(ex);
        }
    }
    
    public static Statements parseStatements(final String sqls) throws JSQLParserException {
        final CCJSqlParser parser = new CCJSqlParser(new StringReader(sqls));
        try {
            return parser.Statements();
        }
        catch (final Exception ex) {
            throw new JSQLParserException(ex);
        }
    }
}
