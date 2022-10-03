package org.apache.commons.text.lookup;

import org.xml.sax.InputSource;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import javax.xml.xpath.XPathFactory;

final class XmlStringLookup extends AbstractStringLookup
{
    static final XmlStringLookup INSTANCE;
    
    private XmlStringLookup() {
    }
    
    @Override
    public String lookup(final String key) {
        if (key == null) {
            return null;
        }
        final String[] keys = key.split(XmlStringLookup.SPLIT_STR);
        final int keyLen = keys.length;
        if (keyLen != 2) {
            throw IllegalArgumentExceptions.format("Bad XML key format [%s]; expected format is DocumentPath:XPath.", key);
        }
        final String documentPath = keys[0];
        final String xpath = this.substringAfter(key, ':');
        try {
            return XPathFactory.newInstance().newXPath().evaluate(xpath, new InputSource(Files.newInputStream(Paths.get(documentPath, new String[0]), new OpenOption[0])));
        }
        catch (final Exception e) {
            throw IllegalArgumentExceptions.format(e, "Error looking up XML document [%s] and XPath [%s].", documentPath, xpath);
        }
    }
    
    static {
        INSTANCE = new XmlStringLookup();
    }
}
