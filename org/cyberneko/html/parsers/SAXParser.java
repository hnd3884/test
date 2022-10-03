package org.cyberneko.html.parsers;

import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.cyberneko.html.HTMLConfiguration;
import org.apache.xerces.parsers.AbstractSAXParser;

public class SAXParser extends AbstractSAXParser
{
    public SAXParser() {
        super((XMLParserConfiguration)new HTMLConfiguration());
    }
}
