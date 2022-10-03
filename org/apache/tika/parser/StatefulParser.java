package org.apache.tika.parser;

public class StatefulParser extends ParserDecorator
{
    public StatefulParser(final Parser parser) {
        super(parser);
    }
}
