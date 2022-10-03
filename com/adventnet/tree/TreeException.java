package com.adventnet.tree;

public class TreeException extends Exception
{
    public TreeException(final String str, final Exception cause) {
        super(str, cause);
    }
    
    public TreeException(final String str) {
        super(str);
    }
    
    public TreeException(final Exception e) {
        super(e);
    }
}
