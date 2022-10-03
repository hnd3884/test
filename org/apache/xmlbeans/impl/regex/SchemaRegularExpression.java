package org.apache.xmlbeans.impl.regex;

import org.apache.xmlbeans.impl.common.XMLChar;
import java.util.HashMap;
import java.util.Map;

public class SchemaRegularExpression extends RegularExpression
{
    static final Map knownPatterns;
    
    private SchemaRegularExpression(final String pattern) {
        super(pattern, "X");
    }
    
    public static RegularExpression forPattern(final String s) {
        final SchemaRegularExpression tre = SchemaRegularExpression.knownPatterns.get(s);
        if (tre != null) {
            return tre;
        }
        return new RegularExpression(s, "X");
    }
    
    private static Map buildKnownPatternMap() {
        final Map result = new HashMap();
        result.put("\\c+", new SchemaRegularExpression("\\c+") {
            @Override
            public boolean matches(final String s) {
                return XMLChar.isValidNmtoken(s);
            }
        });
        result.put("\\i\\c*", new SchemaRegularExpression("\\i\\c*") {
            @Override
            public boolean matches(final String s) {
                return XMLChar.isValidName(s);
            }
        });
        result.put("[\\i-[:]][\\c-[:]]*", new SchemaRegularExpression("[\\i-[:]][\\c-[:]]*") {
            @Override
            public boolean matches(final String s) {
                return XMLChar.isValidNCName(s);
            }
        });
        return result;
    }
    
    static {
        knownPatterns = buildKnownPatternMap();
    }
}
