package com.adventnet.management.transaction;

public class TransactionException extends RuntimeException
{
    public TransactionException() {
        super("TransactionException");
    }
    
    public TransactionException(final String s) {
        super(s);
    }
}
