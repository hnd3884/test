package com.sun.corba.se.impl.orb;

import java.util.Properties;
import com.sun.corba.se.spi.orb.PropertyParser;
import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.orb.StringPair;

public class PrefixParserData extends ParserDataBase
{
    private StringPair[] testData;
    private Class componentType;
    
    public PrefixParserData(final String s, final Operation operation, final String s2, final Object o, final Object o2, final StringPair[] testData, final Class componentType) {
        super(s, operation, s2, o, o2);
        this.testData = testData;
        this.componentType = componentType;
    }
    
    @Override
    public void addToParser(final PropertyParser propertyParser) {
        propertyParser.addPrefix(this.getPropertyName(), this.getOperation(), this.getFieldName(), this.componentType);
    }
    
    @Override
    public void addToProperties(final Properties properties) {
        for (int i = 0; i < this.testData.length; ++i) {
            final StringPair stringPair = this.testData[i];
            String s = this.getPropertyName();
            if (s.charAt(s.length() - 1) != '.') {
                s += ".";
            }
            properties.setProperty(s + stringPair.getFirst(), stringPair.getSecond());
        }
    }
}
