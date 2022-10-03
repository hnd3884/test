package org.apache.tika.parser;

import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.util.Map;

public abstract class ParserFactory
{
    final Map<String, String> args;
    
    public ParserFactory(final Map<String, String> args) {
        this.args = args;
    }
    
    public abstract Parser build() throws IOException, SAXException, TikaException;
}
