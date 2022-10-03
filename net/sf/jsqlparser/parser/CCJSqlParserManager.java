package net.sf.jsqlparser.parser;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.statement.Statement;
import java.io.Reader;

public class CCJSqlParserManager implements JSqlParser
{
    @Override
    public Statement parse(final Reader statementReader) throws JSQLParserException {
        final CCJSqlParser parser = new CCJSqlParser(statementReader);
        try {
            return parser.Statement();
        }
        catch (final Exception ex) {
            throw new JSQLParserException(ex);
        }
    }
}
