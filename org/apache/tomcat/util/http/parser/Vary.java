package org.apache.tomcat.util.http.parser;

import java.io.IOException;
import java.util.Collection;
import java.io.Reader;
import java.util.Set;
import java.io.StringReader;

@Deprecated
public class Vary
{
    private Vary() {
    }
    
    public static void parseVary(final StringReader input, final Set<String> result) throws IOException {
        TokenList.parseTokenList(input, result);
    }
}
