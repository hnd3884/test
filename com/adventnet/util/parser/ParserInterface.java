package com.adventnet.util.parser;

import java.util.ArrayList;

public interface ParserInterface
{
    void init() throws ParseException;
    
    Object parseMessage(final String p0, final String p1) throws ParseException;
    
    void parseRules(final String[] p0, final Object[] p1) throws ParseException;
    
    void parseErrorRules(final ArrayList p0) throws ParseException;
}
