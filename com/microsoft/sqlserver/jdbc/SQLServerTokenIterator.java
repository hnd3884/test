package com.microsoft.sqlserver.jdbc;

import java.util.ArrayList;
import org.antlr.v4.runtime.Token;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicInteger;

final class SQLServerTokenIterator
{
    private final AtomicInteger index;
    private final int listSize;
    private final ListIterator<? extends Token> iter;
    
    SQLServerTokenIterator(final ArrayList<? extends Token> tokenList) {
        this.iter = tokenList.listIterator();
        this.index = new AtomicInteger(0);
        this.listSize = tokenList.size();
    }
    
    Token next() {
        this.index.incrementAndGet();
        return (Token)this.iter.next();
    }
    
    Token previous() {
        this.index.decrementAndGet();
        return (Token)this.iter.previous();
    }
    
    boolean hasNext() {
        return this.index.intValue() < this.listSize;
    }
    
    boolean hasPrevious() {
        return this.index.intValue() > 0;
    }
    
    int nextIndex() {
        return this.index.intValue() + 1;
    }
}
