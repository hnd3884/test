package com.sun.corba.se.impl.orb;

import java.util.Properties;
import com.sun.corba.se.spi.orb.PropertyParser;
import com.sun.corba.se.spi.orb.Operation;

public class NormalParserData extends ParserDataBase
{
    private String testData;
    
    public NormalParserData(final String s, final Operation operation, final String s2, final Object o, final Object o2, final String testData) {
        super(s, operation, s2, o, o2);
        this.testData = testData;
    }
    
    @Override
    public void addToParser(final PropertyParser propertyParser) {
        propertyParser.add(this.getPropertyName(), this.getOperation(), this.getFieldName());
    }
    
    @Override
    public void addToProperties(final Properties properties) {
        properties.setProperty(this.getPropertyName(), this.testData);
    }
}
