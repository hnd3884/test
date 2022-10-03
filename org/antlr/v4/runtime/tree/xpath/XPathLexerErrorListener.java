package org.antlr.v4.runtime.tree.xpath;

import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.BaseErrorListener;

public class XPathLexerErrorListener extends BaseErrorListener
{
    @Override
    public void syntaxError(final Recognizer<?, ?> recognizer, final Object offendingSymbol, final int line, final int charPositionInLine, final String msg, final RecognitionException e) {
    }
}
