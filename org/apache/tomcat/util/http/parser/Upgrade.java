package org.apache.tomcat.util.http.parser;

import java.io.Reader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Enumeration;

public class Upgrade
{
    private final String protocolName;
    private final String protocolVersion;
    
    private Upgrade(final String protocolName, final String protocolVersion) {
        this.protocolName = protocolName;
        this.protocolVersion = protocolVersion;
    }
    
    public String getProtocolName() {
        return this.protocolName;
    }
    
    public String getProtocolVersion() {
        return this.protocolVersion;
    }
    
    @Override
    public String toString() {
        if (this.protocolVersion == null) {
            return this.protocolName;
        }
        return this.protocolName + "/" + this.protocolVersion;
    }
    
    public static List<Upgrade> parse(final Enumeration<String> headerValues) {
        try {
            final List<Upgrade> result = new ArrayList<Upgrade>();
            while (headerValues.hasMoreElements()) {
                final String headerValue = headerValues.nextElement();
                if (headerValue == null) {
                    return null;
                }
                final Reader r = new StringReader(headerValue);
                SkipResult skipComma;
                do {
                    HttpParser.skipLws(r);
                    final String protocolName = HttpParser.readToken(r);
                    if (protocolName == null || protocolName.isEmpty()) {
                        return null;
                    }
                    String protocolVersion = null;
                    if (HttpParser.skipConstant(r, "/") == SkipResult.FOUND) {
                        protocolVersion = HttpParser.readToken(r);
                        if (protocolVersion == null || protocolVersion.isEmpty()) {
                            return null;
                        }
                    }
                    HttpParser.skipLws(r);
                    skipComma = HttpParser.skipConstant(r, ",");
                    if (skipComma == SkipResult.NOT_FOUND) {
                        return null;
                    }
                    result.add(new Upgrade(protocolName, protocolVersion));
                } while (skipComma == SkipResult.FOUND);
            }
            return result;
        }
        catch (final IOException ioe) {
            return null;
        }
    }
}
