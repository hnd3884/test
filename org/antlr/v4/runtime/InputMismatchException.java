package org.antlr.v4.runtime;

public class InputMismatchException extends RecognitionException
{
    public InputMismatchException(final Parser recognizer) {
        super(recognizer, recognizer.getInputStream(), recognizer._ctx);
        this.setOffendingToken(recognizer.getCurrentToken());
    }
}
