package org.apache.tomcat.util.http.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class EntityTag
{
    public static Boolean compareEntityTag(final StringReader input, final boolean compareWeak, final String resourceETag) throws IOException {
        String comparisonETag;
        if (compareWeak && resourceETag.startsWith("W/")) {
            comparisonETag = resourceETag.substring(2);
        }
        else {
            comparisonETag = resourceETag;
        }
        Boolean result = Boolean.FALSE;
        while (true) {
            boolean strong = false;
            HttpParser.skipLws(input);
            switch (HttpParser.skipConstant(input, "W/")) {
                case EOF: {
                    return null;
                }
                case NOT_FOUND: {
                    strong = true;
                    break;
                }
                case FOUND: {
                    strong = false;
                    break;
                }
            }
            final String value = HttpParser.readQuotedString(input, true);
            if (value == null) {
                return null;
            }
            if ((strong || compareWeak) && comparisonETag.equals(value)) {
                result = Boolean.TRUE;
            }
            HttpParser.skipLws(input);
            switch (HttpParser.skipConstant(input, ",")) {
                case EOF: {
                    return result;
                }
                case NOT_FOUND: {
                    return null;
                }
                default: {
                    continue;
                }
            }
        }
    }
}
