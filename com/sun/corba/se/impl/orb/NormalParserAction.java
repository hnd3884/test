package com.sun.corba.se.impl.orb;

import java.util.Properties;
import com.sun.corba.se.spi.orb.Operation;

public class NormalParserAction extends ParserActionBase
{
    public NormalParserAction(final String s, final Operation operation, final String s2) {
        super(s, false, operation, s2);
    }
    
    @Override
    public Object apply(final Properties properties) {
        final String property = properties.getProperty(this.getPropertyName());
        if (property != null) {
            return this.getOperation().operate(property);
        }
        return null;
    }
}
