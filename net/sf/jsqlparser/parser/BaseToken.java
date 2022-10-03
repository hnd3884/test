package net.sf.jsqlparser.parser;

public class BaseToken
{
    public int absoluteBegin;
    public int absoluteEnd;
    
    public BaseToken() {
        this.absoluteBegin = 0;
        this.absoluteEnd = 0;
    }
}
