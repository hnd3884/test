package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.ParseCancellationException;

public class BailErrorStrategy extends DefaultErrorStrategy
{
    @Override
    public void recover(final Parser recognizer, final RecognitionException e) {
        for (ParserRuleContext context = recognizer.getContext(); context != null; context = context.getParent()) {
            context.exception = e;
        }
        throw new ParseCancellationException(e);
    }
    
    @Override
    public Token recoverInline(final Parser recognizer) throws RecognitionException {
        final InputMismatchException e = new InputMismatchException(recognizer);
        for (ParserRuleContext context = recognizer.getContext(); context != null; context = context.getParent()) {
            context.exception = e;
        }
        throw new ParseCancellationException(e);
    }
    
    @Override
    public void sync(final Parser recognizer) {
    }
}
