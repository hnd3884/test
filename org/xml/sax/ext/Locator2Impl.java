package org.xml.sax.ext;

import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

public class Locator2Impl extends LocatorImpl implements Locator2
{
    private String encoding;
    private String version;
    
    public Locator2Impl() {
    }
    
    public Locator2Impl(final Locator locator) {
        super(locator);
        if (locator instanceof Locator2) {
            final Locator2 locator2 = (Locator2)locator;
            this.version = locator2.getXMLVersion();
            this.encoding = locator2.getEncoding();
        }
    }
    
    public String getXMLVersion() {
        return this.version;
    }
    
    public String getEncoding() {
        return this.encoding;
    }
    
    public void setXMLVersion(final String version) {
        this.version = version;
    }
    
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }
}
