package org.apache.tomcat.util.http.parser;

import java.io.IOException;
import java.util.Locale;
import java.io.Reader;
import java.util.HashMap;
import java.io.StringReader;
import java.util.Map;

public class Authorization
{
    private static final Map<String, FieldType> fieldTypes;
    
    private Authorization() {
    }
    
    public static Map<String, String> parseAuthorizationDigest(final StringReader input) throws IllegalArgumentException, IOException {
        final Map<String, String> result = new HashMap<String, String>();
        if (HttpParser.skipConstant(input, "Digest") != SkipResult.FOUND) {
            return null;
        }
        String field = HttpParser.readToken(input);
        if (field == null) {
            return null;
        }
        while (!field.equals("")) {
            if (HttpParser.skipConstant(input, "=") != SkipResult.FOUND) {
                return null;
            }
            String value = null;
            FieldType type = Authorization.fieldTypes.get(field.toLowerCase(Locale.ENGLISH));
            if (type == null) {
                type = FieldType.TOKEN_OR_QUOTED_STRING;
            }
            switch (type) {
                case QUOTED_STRING: {
                    value = HttpParser.readQuotedString(input, false);
                    break;
                }
                case TOKEN_OR_QUOTED_STRING: {
                    value = HttpParser.readTokenOrQuotedString(input, false);
                    break;
                }
                case LHEX: {
                    value = HttpParser.readLhex(input);
                    break;
                }
                case QUOTED_TOKEN: {
                    value = HttpParser.readQuotedToken(input);
                    break;
                }
            }
            if (value == null) {
                return null;
            }
            result.put(field, value);
            if (HttpParser.skipConstant(input, ",") == SkipResult.NOT_FOUND) {
                return null;
            }
            field = HttpParser.readToken(input);
            if (field == null) {
                return null;
            }
        }
        return result;
    }
    
    static {
        (fieldTypes = new HashMap<String, FieldType>()).put("username", FieldType.QUOTED_STRING);
        Authorization.fieldTypes.put("realm", FieldType.QUOTED_STRING);
        Authorization.fieldTypes.put("nonce", FieldType.QUOTED_STRING);
        Authorization.fieldTypes.put("digest-uri", FieldType.QUOTED_STRING);
        Authorization.fieldTypes.put("response", FieldType.LHEX);
        Authorization.fieldTypes.put("algorithm", FieldType.QUOTED_TOKEN);
        Authorization.fieldTypes.put("cnonce", FieldType.QUOTED_STRING);
        Authorization.fieldTypes.put("opaque", FieldType.QUOTED_STRING);
        Authorization.fieldTypes.put("qop", FieldType.QUOTED_TOKEN);
        Authorization.fieldTypes.put("nc", FieldType.LHEX);
    }
    
    private enum FieldType
    {
        QUOTED_STRING, 
        TOKEN_OR_QUOTED_STRING, 
        LHEX, 
        QUOTED_TOKEN;
    }
}
