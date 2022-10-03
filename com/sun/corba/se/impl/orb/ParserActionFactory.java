package com.sun.corba.se.impl.orb;

import com.sun.corba.se.spi.orb.Operation;

public class ParserActionFactory
{
    private ParserActionFactory() {
    }
    
    public static ParserAction makeNormalAction(final String s, final Operation operation, final String s2) {
        return new NormalParserAction(s, operation, s2);
    }
    
    public static ParserAction makePrefixAction(final String s, final Operation operation, final String s2, final Class clazz) {
        return new PrefixParserAction(s, operation, s2, clazz);
    }
}
