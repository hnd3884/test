package net.sf.jsqlparser.parser;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.statement.Statement;
import java.io.Reader;

public interface JSqlParser
{
    Statement parse(final Reader p0) throws JSQLParserException;
}
