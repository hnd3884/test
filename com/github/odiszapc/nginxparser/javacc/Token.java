package com.github.odiszapc.nginxparser.javacc;

import java.io.Serializable;

public class Token implements Serializable
{
    private static final long serialVersionUID = 1L;
    public int kind;
    public int beginLine;
    public int beginColumn;
    public int endLine;
    public int endColumn;
    public String image;
    public Token next;
    public Token specialToken;
    
    public Object getValue() {
        return null;
    }
    
    public Token() {
    }
    
    public Token(final int n) {
        this(n, null);
    }
    
    public Token(final int kind, final String image) {
        this.kind = kind;
        this.image = image;
    }
    
    @Override
    public String toString() {
        return this.image;
    }
    
    public static Token newToken(final int n, final String s) {
        return new Token(n, s);
    }
    
    public static Token newToken(final int n) {
        return newToken(n, null);
    }
}
