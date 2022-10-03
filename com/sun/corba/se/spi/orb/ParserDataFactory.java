package com.sun.corba.se.spi.orb;

import com.sun.corba.se.impl.orb.PrefixParserData;
import com.sun.corba.se.impl.orb.NormalParserData;

public class ParserDataFactory
{
    public static ParserData make(final String s, final Operation operation, final String s2, final Object o, final Object o2, final String s3) {
        return new NormalParserData(s, operation, s2, o, o2, s3);
    }
    
    public static ParserData make(final String s, final Operation operation, final String s2, final Object o, final Object o2, final StringPair[] array, final Class clazz) {
        return new PrefixParserData(s, operation, s2, o, o2, array, clazz);
    }
}
