package com.azul.crs.com.fasterxml.jackson.core.async;

public interface NonBlockingInputFeeder
{
    boolean needMoreInput();
    
    void endOfInput();
}
